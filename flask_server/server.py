from flask import Flask, jsonify, request
from flask_cors import CORS
import os
from dotenv import load_dotenv
from supabase import create_client, Client
from datetime import datetime
import google_maps
import math
import logging
import pytz
import hashlib
import requests
import bcrypt

# Configure logging
logger = logging.getLogger('werkzeug')
logger.propagate = False

load_dotenv() # remove when using railway server
url = os.getenv("SUPABASE_URL")
key = os.getenv("SUPABASE_KEY")

if not url or not key:
	raise ValueError("SUPABASE_URL or SUPABASE_KEY is not set.")

supabase: Client = create_client(url, key)

app = Flask(__name__)

CORS(app)

######### HELPER FUNCTIONS ##############
def haversine(lat1, lon1, lat2, lon2):
	R = 6371000  # Radius of the Earth in meters

	# Convert latitude and longitude from degrees to radians
	lat1, lon1, lat2, lon2 = map(math.radians, [lat1, lon1, lat2, lon2])

	# Differences in coordinates
	dlat = lat2 - lat1
	dlon = lon2 - lon1

	# Haversine formula
	a = math.sin(dlat / 2) ** 2 + math.cos(lat1) * math.cos(lat2) * math.sin(dlon / 2) ** 2
	c = 2 * math.atan2(math.sqrt(a), math.sqrt(1 - a))

	# Distance in meters
	distance = R * c
	return distance

def iso_to_unix(iso_string):
	"""Converts an ISO 8601 string to a Unix timestamp."""
	try:
		dt = datetime.fromisoformat(iso_string.replace('Z', '+00:00'))
		utc_dt = dt.astimezone(pytz.UTC)
		return int(utc_dt.timestamp() * 1000)
	except ValueError as e:
		logger.error(f"Error parsing ISO string: {str(e)}", exc_info=True)
		return None

def remove_vote_in_db(user_id, deal_id):
	"""Remove vote for given deal and user in Supabase."""
	try:
		existing_entry = supabase.from_('Vote').select('user_id', 'deal_id') \
			.eq('user_id', user_id).eq('deal_id', deal_id).execute()

		if not existing_entry.data:
			return jsonify({"success": False, "message": "Vote is not saved in Supabase"})

		response = supabase.from_('Vote').delete().eq('user_id', user_id).eq('deal_id', deal_id).execute()

		if response.data:
			return jsonify({"success": True, "message": "Vote deleted successfully"})
		else:
			logger.error(f"Failed to delete vote", exc_info=True)
			return jsonify({"success": False, "message": "Failed to delete vote"})

	except Exception as e:
		logger.error(f"Error updating vote deal: {str(e)}", exc_info=True)
		return jsonify({"success": False, "message": f"Error deleting deal vote: {str(e)}"})

def update_vote_in_db(user_id, deal_id, vote_type):
	"""Update vote for the given deal and user in Supabase."""
	try:
		response = supabase.from_('Vote').upsert({
			'user_id': user_id,
			'deal_id': deal_id,
			'user_vote': vote_type
		}).execute()

		if response.data:
			return jsonify({"success": True, "message": f"Vote updated successfully with {vote_type}"})
		else:
			logger.error(f"Failed to update vote", exc_info=True)
			return jsonify({"success": False, "message": f"Failed to update vote with {vote_type}"})

	except Exception as e:
		logger.error(f"Error updating vote deal: {str(e)}", exc_info=True)
		return jsonify({"success": False, "message": f"Error updating deal vote: {str(e)}"})

def mark_deal_removed_in_db(deal_id):
	"""Marks deal as expired given the deal id."""
	try:
		response = supabase.table("Deal").update({"is_removed": True}).eq("id", deal_id).execute()

		if not response.data:
			raise response.error

	except Exception as e:
		logger.error(f"Error marking deal as removed: {str(e)}", exc_info=True)

def mark_deal_saved_in_db(deal_id, user_id):
	"""Marks deal as saved given the deal id and user_id."""
	try:
		# Check if the deal is already saved
		existing_entry = supabase.from_('Saved').select('user_id', 'deal_id') \
			.eq('user_id', user_id).eq('deal_id', deal_id).execute()

		if existing_entry.data:  # If the entry exists
			return jsonify({"success": False, "message": "Deal is already saved"})

		# Insert into 'Saved' table
		response = supabase.from_('Saved').insert({
			"user_id": user_id,
			"deal_id": deal_id
		}).execute()

		if response.data:
			return jsonify({"success": True, "message": "Deal saved successfully"})
		else:
			logger.error(f"Error saving deal in Supabase")
			return jsonify({"success": False, "message": "Failed to save deal"})

	except Exception as e:
		logger.error(f"Error marking deal as saved: {str(e)}", exc_info=True)
		return jsonify({"success": False, "message": f"Error saving deal: {str(e)}"})

def unmark_deal_saved_in_db(deal_id, user_id):
	"""Removes a saved deal given the deal_id and user_id."""
	try:
		# Check if the deal is saved
		existing_entry = supabase.from_('Saved').select('user_id', 'deal_id') \
			.eq('user_id', user_id).eq('deal_id', deal_id).execute()

		if not existing_entry.data:  # If the entry does NOT exist
			return jsonify({"success": False, "message": "Deal is not saved in Supabase"})

		# Delete the saved deal
		response = supabase.from_('Saved').delete().eq('user_id', user_id).eq('deal_id', deal_id).execute()

		if response.data:
			return jsonify({"success": True, "message": "Deal unsaved successfully"})
		else:
			logger.error(f"Error unsaving deal: {str(response)}", exc_info=True)
			return jsonify({"success": False, "message": "Failed to unsave deal"})

	except Exception as e:
		logger.error(f"Error unsaving deal: {str(e)}", exc_info=True)
		return jsonify({"success": False, "message": f"Error unsaving deal: {str(e)}"})

def get_user_by_id(user_id):
	"""Fetch user details from Supabase by user_id."""
	try:
		result = supabase.from_('User').select('username, first_name, last_name, created_at, email').eq('id', user_id).execute()

		if result.data:
			return result.data[0]
		else:
			logger.error(f"No user with user_id: {user_id}")
			return None

	except Exception as e:
		logger.error(f"Database query error: {str(e)}", exc_info=True)
		return None

def get_user_upvote(user_id):
    response = supabase.table("Vote") \
            .select("user_vote", count="exact") \
            .eq("user_id", user_id) \
            .eq("user_vote", "UPVOTE") \
            .execute()

    return response.count if response.count is not None else 0

def get_user_downvote(user_id):
    response = supabase.table("Vote") \
                .select("user_vote", count="exact") \
                .eq("user_id", user_id) \
                .eq("user_vote", "DOWNVOTE") \
                .execute()

    return response.count if response.count is not None else 0

def get_deal_by_id(deal_id):
	"""Fetches the deal by the id from Supabase."""
	try:
		result = supabase.from_('Deal').select('*').eq('id', deal_id).execute()

		if result.data:
			return result.data[0]
		else:
			logger.error(f"No deal with id: {deal_id}")
			return None

	except Exception as e:
		logger.error(f"Failed to fetch restaurant deals: {str(e)}", exc_info=True)
		return []

def get_all_restaurant_deals_in_db():
	"""Fetches all restaurants and their deals from Supabase."""
	try:
		result = supabase.from_('Restaurant').select('*, Deal(*)').execute()
		return result.data
	except Exception as e:
		logger.error(f"Failed to fetch restaurant deals: {str(e)}", exc_info=True)
		return []

def group_deals_by_restaurant(response_data):
	restaurant_map = {}

	for deal in response_data:
		rest_id = deal["restaurant_id"]

		# initialize restaurant
		restaurant = restaurant_map.setdefault(rest_id, {
			"id": rest_id,
			"place_id": deal["place_id"],
			"coordinates": {
				"latitude": deal["latitude"],
				"longitude": deal["longitude"]
			},
			"restaurant_name": deal["restaurant_name"],
			"display_address": deal["display_address"],
			"image_url": deal["image_url"],
			"Deal": []
		})

		# Append deal details to restaurant
		restaurant["Deal"].append({
			"id": deal["id"],
			"item": deal["item"],
			"description": deal["description"],
			"type": deal["type"],
			"expiry_date": deal["expiry_date"],
			"date_posted": deal["date_posted"],
			"user_id": deal["user_id"],
			"username": deal["username"],
			"price": deal["price"],
			"image_id": deal["image_id"],
			"user_saved": deal["user_saved"],
			"user_vote": deal["user_vote"],
			"applicable_group": deal["applicable_group"],
			"daily_start_times": deal["start_times"],
			"daily_end_times": deal["end_times"],
			"num_upvote": deal["upvotes"],
			"num_downvote": deal["downvotes"]
		})

	return list(restaurant_map.values())

def get_user_saved_restaurant_deals(user_id):
	"""Fetches all saved restaurants and their deals from Supabase, along with user saved status."""
	try:
		response = supabase.rpc('get_user_saved_restaurant_deals', params={"target_user_id": user_id}).execute()
		data = response.data

		return group_deals_by_restaurant(data)

	except Exception as e:
		logger.error(f"Failed to fetch saved restaurant data: {str(e)}", exc_info=True)
		return []

def get_all_restaurant_deals_with_user_details_in_db(user_id=None):
	"""Fetches all restaurants and their deals from Supabase, along with user saved status."""
	try:
		# the query, get_all_restaurant_deals, can be viewed in supabase terminal using `SELECT pg_get_functiondef('get_all_restaurant_deals'::regproc);`
		# NOTE: if you want to change what it returns, you need to modify `get_all_restaurant_deals`, ask joyce if you need help
		response = supabase.rpc('get_all_restaurant_deals', params={"target_user_id": user_id}).execute()
		data = response.data

		return group_deals_by_restaurant(data)

	except Exception as e:
		logger.error(f"Failed to fetch restaurant data: {str(e)}", exc_info=True)
		return []

def get_restaurants_given_filters(user_lat, user_long, radius, user_id):
	"""Filter restaurants based on user location and radius."""
	restaurant_deals = get_all_restaurant_deals_with_user_details_in_db(user_id)
	filtered_restaurants = []

	for restaurant in restaurant_deals:
		res_lat = restaurant["coordinates"]["latitude"]
		res_long = restaurant["coordinates"]["longitude"]
		distance = haversine(res_lat, res_long, user_lat, user_long)

		# check if restaurant distance is within user location
		if distance <= radius:
			filtered_restaurants.append(restaurant)

	return filtered_restaurants

def format_deal(deal):
	"""Format the deal object."""
	deal['date_posted'] = iso_to_unix(deal['date_posted'])
	if deal.get("expiry_date"):
		deal['expiry_date'] = iso_to_unix(deal['expiry_date'])

		# Check if the deal is expired
		expiry_date = datetime.fromtimestamp(deal['expiry_date'] / 1000, tz=pytz.UTC)
		today = datetime.now(tz=pytz.UTC)

		# Only keep non-expired deals
		if expiry_date < today:
			deal_id = deal['id']
			logger.info(f"Deal {deal_id} expired and was removed from the valid deals list.")
			mark_deal_removed_in_db(deal_id)
			return None

	if "num_downvote" in deal and "num_upvote" in deal:
		bad_karma_deals = deal["num_downvote"] - deal["num_upvote"] >= 10

		if bad_karma_deals:
			deal_id = deal['id']
			logger.info(f"Deal {deal_id} has bad karma and was removed from the valid deals list.")
			mark_deal_removed_in_db(deal_id)
			return None

	return deal

def process_and_filter_restaurant_deals(restaurants):
	"""Clean up and format restaurant data before sending to the Android app."""
	try:
		for restaurant in restaurants:
			raw_deals = restaurant["Deal"]
			formatted_deals = map(format_deal, raw_deals)
			valid_deals = list(filter(None, formatted_deals))
			restaurant["Deal"] = valid_deals

		return restaurants

	except Exception as e:
		logger.error(f"Error processing deal: {e}", exc_info=True)

def get_restaurant_image_url(place_id):
	"""Fetches restaurant image URL using Google Favicon API."""
	try:
		website = google_maps.get_restaurant_website(place_id)

		if not website:
			logger.warning(f"No website found for place_id: {place_id} which has website {website}")
			return None

		domain = website.replace("https://", "").replace("http://", "").split("/")[0]
		domain_url = f"https://logo.clearbit.com/{domain}"
		return domain_url

	except Exception as e:
		logger.error(f"Failed to get image URL for place_id {place_id}: {str(e)}", exc_info=True)
		return None


######### ROUTES ##############

@app.route('/get_user_by_id', methods=["GET"])
def get_user_info():
	"""Gets user info based on user id."""
	try:
		user_id = request.args.get('user_id')

		user = get_user_by_id(user_id)

		if not user:
			return jsonify({"error": f"No user found with id {user_id}"})

		user_response = {
			"id": user_id,
			"username": user['username'],
			"firstName": user['first_name'],
			"lastName": user['last_name'],
			"email": user['email'],
			"upvote": get_user_upvote(user_id),
			"downvote": get_user_downvote(user_id),
		}

		return jsonify({
			"success": True,
			"message": "User retrieval successful",
			"user": user_response
		})

	except Exception as e:
		error_message = str(e)
		logger.error(f"Error occurred: {error_message}", exc_info=True)
		return jsonify({"error": "An error occurred while fetching user info"})


@app.route('/restaurant_deals', methods=["GET"])
def get_deals():
	"""Gets restaurant deals based on user location and radius."""
	try:
		"""Gets restaurant deals from Supabase given a latitude, longitude and radius."""
		latitude = float(request.args.get('latitude'))
		longitude = float(request.args.get('longitude'))
		radius = float(request.args.get('radius'))
		user_id = request.args.get('user_id')
		logger.info(f"Fetching deals for lat: {latitude}, long: {longitude}, radius: {radius}, user: {user_id}")

		# Filter restaurants based on coords and radius
		filtered_restaurants = get_restaurants_given_filters(latitude, longitude, radius, user_id)

		# Format the restaurant data
		formatted_restaurants = process_and_filter_restaurant_deals(filtered_restaurants)

		return jsonify(formatted_restaurants)

	except Exception as e:
		error_message = str(e)
		logger.error(f"Error occurred: {error_message}", exc_info=True)
		return jsonify({"error": "An error occurred while fetching restaurant deals"})


@app.route('/add_restaurant_deal', methods=["POST"])
def add_restaurant_deal():
	"""Adds a new restaurant deal to Supabase."""
	try:
		restaurant = request.json
		restaurant_place_id = restaurant.get("place_id")
		logger.info(f"Received new add deal request for place_id: {restaurant_place_id}")

		# Check if restaurant already exists
		existing_restaurant_id = supabase.from_('Restaurant').select('id').eq('place_id', restaurant_place_id).execute()
		restaurant_id = existing_restaurant_id.data[0]['id'] if existing_restaurant_id.data else None

		# Insert restaurant if it doesnt exist
		if not restaurant_id:
			image_url = get_restaurant_image_url(restaurant_place_id)

			restaurant_data = {
				"place_id": restaurant.get("place_id"),
				"restaurant_name": restaurant.get("restaurant_name"),
				"display_address": restaurant.get("display_address"),
				"latitude": restaurant["coordinates"]["latitude"],
				"longitude": restaurant["coordinates"]["longitude"],
				"image_url": image_url,
			}
			response = supabase.from_('Restaurant').insert([restaurant_data]).execute()
			restaurant_id = response.data[0]['id'] if response.data else None
			logger.info(f"Added new restaurant: {restaurant.get('restaurant_name')}")

		# Insert deal
		deal = restaurant.get("Deal", [None])[0]
		if deal:
			user_id = deal.get("user_id", "9f7ab2ec-15d8-4f31-8a33-8e4218a03e90")

			deal_item = {
				"restaurant_id": restaurant_id,
				"item": deal["item"],
				"description": deal.get("description"),
				"type": deal.get("type"),
				"expiry_date": datetime.fromtimestamp(deal["expiry_date"] / 1000).isoformat() if deal.get("expiry_date") else None,
				"date_posted": datetime.fromtimestamp(deal["date_posted"] / 1000).isoformat(),
				"user_id": user_id,
				"image_id": deal.get("image_id"),
				"applicable_group": deal.get("applicable_group"),
				"start_times": deal.get("daily_start_times"),
				"end_times": deal.get("daily_end_times"),
				"price": deal.get("price"),
			}

			response = supabase.from_('Deal').insert([deal_item]).execute()
			deal_uuid = response.data[0]['id'] if response.data else None
			logger.info(f"Added new deal: {deal['item']} for restaurant {restaurant.get('restaurant_name')}")

			return jsonify({"dealId": str(deal_uuid)})

	except Exception as e:
		error_message = str(e)
		logger.error(f"Error occurred at add_restaurant_deal: {error_message}", exc_info=True)
		return jsonify({"error": "An error occurred while adding the deal"})


@app.route('/search_nearby_restaurants', methods=["GET"])
def nearby_search():
	try:
		keyword = request.args.get('keyword')
		latitude = float(request.args.get('latitude'))
		longitude = float(request.args.get('longitude'))
		radius = float(request.args.get('radius'))

		logger.info(f"Received request for nearby restaurants with keyword='{keyword}', "
					f"latitude={latitude}, longitude={longitude}, radius={radius}")

		nearby_restaurants = google_maps.search_nearby_restaurants(keyword, latitude, longitude, radius)

		logger.info(f"Found {len(nearby_restaurants)} restaurants near ({latitude}, {longitude}) within {radius}m")
		nearby_restaurants.sort(key=lambda x: haversine(latitude, longitude, x["coordinates"]["latitude"], x["coordinates"]["longitude"]))

		return jsonify(nearby_restaurants)

	except Exception as e:
		error_message = str(e)
		logger.error(f"Error occurred at nearby_search: {error_message}", exc_info=True)
		return jsonify({"error": "An error occurred while searching for nearby restaurants"})

@app.route('/update_vote', methods=["GET"])
def update_vote():
	user_id = request.args.get('user_id')
	deal_id = request.args.get('deal_id')
	vote_type = request.args.get('user_vote')

	if vote_type == "NEUTRAL":
		return remove_vote_in_db(user_id, deal_id)
	else:
		return update_vote_in_db(user_id, deal_id, vote_type)

@app.route('/save_deal', methods=["GET"])
def save_deal():
	deal_id = request.args.get('deal_id')
	user_id = request.args.get('user_id')

	return mark_deal_saved_in_db(deal_id, user_id)

@app.route('/unsave_deal', methods=["GET"])
def unsave_deal():
	deal_id = request.args.get('deal_id')
	user_id = request.args.get('user_id')

	return unmark_deal_saved_in_db(deal_id, user_id)

@app.route("/get_saved_deals", methods=["GET"])
def get_saved_deals():
	user_id = request.args.get("user_id")

	saved_deals = get_user_saved_restaurant_deals(user_id)
	restaurants = process_and_filter_restaurant_deals(saved_deals)

	return jsonify(restaurants)

@app.route('/delete_deal', methods=["GET"])
def delete_deal():
	deal_id = request.args.get('deal_id')
	user_id = request.args.get('user_id')

	deal = get_deal_by_id(deal_id)

	if not deal:
		return jsonify({"success": False, "message": "Deal not found. Incorrect deal_id"})

	if deal["user_id"] != user_id:
		return jsonify({"success": False, "message": "Unauthorized: You are not the creator of this deal"})

	try:
		mark_deal_removed_in_db(deal_id)
		return jsonify({"success": True, "message": "Deal successfully removed"})

	except Exception as e:
		return jsonify({"success": False, "message": f"Error deleting deal: {str(e)}"})

# TODO: OLIVIA FIX THIS TO USE THE ERROR CODES AND NOT USE THE GENERIC RESPONSE
@app.route('/create_new_user_account', methods=["POST"])
def create_new_user_account():
	"""Creates a new user account in the User table."""
	try:
		data = request.get_json()
		# Get query parameters from the request
		username = data.get("username")
		password = data.get("password")
		first_name = data.get("firstName")
		last_name = data.get("lastName")
		email = data.get("email")

		logger.info(f"Received new user signup request for username: {username}")

		# Basic validation
		if not all([username, password, first_name, last_name, email]):
			logger.warning("Missing required fields in create_new_user_account request")
			return jsonify({"success": False, "message": "Error: Missing required fields"})

		# TODO: check that this actually catches duplicates
		# Check if username or email already exists
		# Fetch user from Supabase by username
		existing_user = supabase.from_('User').select('id').or_(
			f"username.eq.{username},email.eq.{email}"
		).execute()
		if existing_user.data:
			logger.warning(f"User with username {username} or email {email} already exists")
			return jsonify({"success": False, "message": "Error: Username or email already taken"})

		# Prepare user data for insertion
		user_data = {
			"username": username,
			"first_name": first_name,
			"last_name": last_name,
			"created_at": datetime.utcnow().isoformat(),
			"email": email,
			"password_hash": hash_password(password) # Storing plain password for now (no hashing)
		}

		# Insert the user into the User table
		response = supabase.from_('User').insert([user_data]).execute()

		if not response.data:
			logger.error("Failed to insert user into User table")
			return jsonify({"error": "Failed to create user"})

		logger.info(f"Successfully created new user: {username}")

		user_id = response.data[0]['id'] if response.data else None

		# TODO: should have a better return object (for one user_id should not be in "message")
		return jsonify({"success": True, "message": user_id})

	except Exception as e:
		error_message = str(e)
		logger.error(f"Error occurred in create_new_user_account: {error_message}", exc_info=True)
		return jsonify({"success": False, "message": "Error: Invalid fields could not create user"})


def hash_password(password: str) -> str:
    salt = bcrypt.gensalt()
    return bcrypt.hashpw(password.encode('utf-8'), salt).decode('utf-8')

@app.route('/login', methods=["POST"])
def login():
	"""Logs in a user and returns their full user object."""
	try:
		# Get query parameters from the request
		data = request.get_json()

		username = data.get("username")
		password = data.get("password")



		logger.info(f"Received login request for username: {username}")

		# Basic validation
		if not all([username, password]):
			logger.warning("Missing required fields in login request")
			return jsonify({
				"success": False,
				"message": "ERROR: Username and password are required"
			})

		# Fetch user from Supabase by username
		response = supabase.from_('User').select('*').eq('username', username).execute()

		if not response.data or len(response.data) == 0:
			logger.warning(f"No user found with username: {username}")
			return jsonify({
				"success": False,
				"message": "ERROR: Invalid username or password"
			})

		# Get the user data
		user = response.data[0]

		# USE THIS TO UPDATE PASSWORDS OH MY LORD
# 		print("new hashed password: ", hash_password(password))
# 		print("password_hash: ", user['password_hash'])

		# Verify password using bcrypt
		if not verify_password(password, user['password_hash']):
# 			print("Password: ", password)
# 			print("password_hash: ", user['password_hash'])
			logger.warning(f"Password mismatch for username: {username}")
			return jsonify({
		        "success": False,
                "message": "ERROR: Invalid username or password"
            })

		# Prepare the full user object to return
		user_response = {
			"id": user['id'],
			"username": user['username'],
			"password": user['password_hash'],
			"firstName": user['first_name'],
			"lastName": user['last_name'],
			"email": user['email'],
			"upvote": get_user_upvote(user['id']),
			"downvote": get_user_downvote(user['id']),
		}

		logger.info(f"Successfully logged in user: {username}")

		# Return success response with user object
		return jsonify({
			"success": True,
			"message": "Login successful",
			"user": user_response
		})

	except Exception as e:
		error_message = str(e)
		logger.error(f"Error occurred in login: {error_message}", exc_info=True)
		return jsonify({
			"success": False,
			"message": "ERROR: An error occurred during login"
		})

def verify_password(plain_password: str, hashed_password: str) -> bool:
#     print("THIS IS USED TO CHANGE PREVIOUSLY HASHED PASSWORDS")
#     print("plain password: ", plain_password)
#     print("THIS IS THE HASHED SALT: ", hash_password(plain_password))
    return bcrypt.checkpw(plain_password.encode('utf-8'), hashed_password.encode('utf-8'))

@app.route('/change_password', methods=["POST"])
def change_password():
    try:
        data = request.json

        # Validate basic stuff
        username = data.get("username")
        old_password = data.get("oldPassword")
        new_password = data.get("newPassword")
        new_password_confirm = data.get("confirmPassword")

        if not all([username, old_password, new_password, new_password_confirm]):
            logger.warning("Missing required fields in change_password request")
            return jsonify({"success": False, "message": "Error: Missing required fields"})

        # New password and confirmation match
        if new_password != new_password_confirm:
            return jsonify({"success": False, "message": "Error: New passwords do not match"})

        # Fetch user from the database
        user = supabase.from_('User').select('id', 'password_hash').eq('username', username).execute()
        print("User query result:", user)

        user_data = user.data[0]

        # Check if old password is correct
        if not verify_password(old_password, user_data['password_hash']):
            return jsonify({"success": False, "message": "Error: Incorrect current password"})

        new_password_hash = hash_password(new_password)

#         print("User Data Retrieved:", user_data)
#         print("New Hashed Password:", new_password_hash)
        # Update the password in supabase
        response = (
            supabase.from_('User')
            .update({'password_hash': new_password_hash})
            .eq('id', user_data['id'])
            .execute()
        )

        if not response.data:
            return jsonify({"success": False, "message": "Error: Failed to update password"})

        return jsonify({"success": True, "message": "Password updated successfully"})

    except Exception as e:
        error_message = str(e)
        logger.error(f"Error occurred in change_password: {error_message}", exc_info=True)
        return jsonify({"success": False, "message": "Error: Unable to change password"})

@app.route('/get_restaurant', methods=["GET"])
def get_restaurant():
    """Get restaurant details from Supabase given a place_id."""
    try:
        place_id = request.args.get("place_id")
        logger.info(f"Fetching deals for restaurant: {place_id}")

        # Step 1: Search the restaurant table by place_id
        response = supabase.from_("Restaurant").select("*").eq("place_id", place_id).execute()
        # Check if any rows were returned
        if not response.data:
            logger.warning(f"No restaurant found with place_id: {place_id}")
            return jsonify({"restaurant": None}) # No Content


        # Get the first restaurant from the returned list
        restaurant = response.data[0]
        restaurant_id = restaurant["id"]

        # Format restaurant obj
        restaurant["coordinates"] = {
            "latitude": restaurant["latitude"],
            "longitude": restaurant["longitude"]
        }

        # Step 2: Fetch all deals from the deals table that match restaurant_id
        deals_response = supabase.from_("Deal").select("*").eq("restaurant_id", restaurant_id).eq("is_removed", False).execute()
        raw_deals = deals_response.data if deals_response.data else []

        # format deals obj
        formatted_deals = map(format_deal, raw_deals)
        valid_deals = list(filter(None, formatted_deals))

        # Step 3: Attach deals to the restaurant object and return the result
        restaurant["Deal"] = valid_deals
        return jsonify({"restaurant": restaurant})

    except Exception as e:
        error_message = str(e)
        logger.error(f"Error occurred: {error_message}", exc_info=True)
        return jsonify({"error": "An error occurred while fetching restaurant deals"})

GPU_SERVER_URL = "http://ece-nebula10.eng.uwaterloo.ca:8000"

# this is just to test if the server "is not up"
# GPU_SERVER_URL = "http://ece-nebula10.eng.uwaterloo.ca:5000"

@app.route("/proxy/generate", methods=["POST"])
def proxy_generate():
    try:
        # forward request to the GPU server
        response = requests.post(GPU_SERVER_URL + "/generate", json=request.json)
        logger.warning(response)

        return jsonify(response.json()), response.status_code
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route("/proxy/handshake", methods=["GET"])
def proxy_handshake():
    try:
        # attempt to call the GPU service handshake endpoint
        response = requests.get(GPU_SERVER_URL, timeout=5) # timeout in 5 seconds
        if response.status_code == 200:
            return jsonify({"gpu_status": True}), 200
        else:
            return jsonify({"gpu_status": False, "detail": "GPU service responded with status code " + str(response.status_code)}), 503
    except requests.exceptions.RequestException as e:
        # any error, consider the gpu service as down
        logger.warning(f"error: {e}")
        return jsonify({"gpu_status": False, "error": str(e)}), 503

@app.route('/')
def index():
	return "Successfully connected "

# Run the Flask app
if __name__ == '__main__':
	app.run(host='0.0.0.0', port=5001, debug=True)
