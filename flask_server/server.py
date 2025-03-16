from flask import Flask, jsonify, request
from flask_cors import CORS
import os
from datetime import datetime
from dotenv import load_dotenv
from supabase import create_client, Client
import google_maps
import math
import logging
import pytz

# Configure logging
logger = logging.getLogger('werkzeug')
logger.propagate = False

load_dotenv()
url = os.getenv("SUPABASE_URL")
key = os.getenv("SUPABASE_KEY")

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
		return int(utc_dt.timestamp())
	except ValueError as e:
		logger.error(f"Error parsing ISO string: {str(e)}", exc_info=True)
		return None

def remove_vote_in_db(user_id, deal_id):
	"""Remove vote for given deal and user in Supabase."""
	try:
		existing_entry = supabase.from_('Vote').select('user_id', 'deal_id') \
			.eq('user_id', user_id).eq('deal_id', deal_id).execute()

		if not existing_entry.data:
			return jsonify({"success": False, "message": "Vote is not saved in Supabase"}), 404

		response = supabase.from_('Vote').delete().eq('user_id', user_id).eq('deal_id', deal_id).execute()

		if response.data:
			return jsonify({"success": True, "message": "Vote deleted successfully"}), 201
		else:
			logger.error(f"Failed to delete vote", exc_info=True)
			return jsonify({"success": False, "message": "Failed to delete vote"}), 400

	except Exception as e:
		logger.error(f"Error updating vote deal: {str(e)}", exc_info=True)
		return jsonify({"success": False, "message": f"Error deleting deal vote: {str(e)}"}), 500

def update_vote_in_db(user_id, deal_id, vote_type):
	"""Update vote for the given deal and user in Supabase."""
	try:
		response = supabase.from_('Vote').upsert({
			'user_id': user_id,
			'deal_id': deal_id,
			'user_vote': vote_type
		}).execute()

		if response.data:
			return jsonify({"success": True, "message": f"Vote updated successfully with {vote_type}"}), 201
		else:
			logger.error(f"Failed to update vote", exc_info=True)
			return jsonify({"success": False, "message": f"Failed to update vote with {vote_type}"}), 400

	except Exception as e:
		logger.error(f"Error updating vote deal: {str(e)}", exc_info=True)
		return jsonify({"success": False, "message": f"Error updating deal vote: {str(e)}"}), 500

def mark_deal_expired_in_db(deal_id):
	"""Marks deal as expired given the deal id."""
	try:
		response = supabase.table("Deal").update({"is_expired": True}).eq("id", deal_id).execute()

		if not response.data:
			logger.error("Error marking deal as expired")

	except Exception as e:
		logger.error(f"Error marking deal as expired: {str(e)}", exc_info=True)

def mark_deal_saved_in_db(deal_id, user_id):
	"""Marks deal as saved given the deal id and user_id."""
	try:
		# Check if the deal is already saved
		existing_entry = supabase.from_('Saved').select('user_id', 'deal_id') \
			.eq('user_id', user_id).eq('deal_id', deal_id).execute()

		if existing_entry.data:  # If the entry exists
			return jsonify({"success": False, "message": "Deal is already saved"}), 409

		# Insert into 'Saved' table
		response = supabase.from_('Saved').insert({
			"user_id": user_id,
			"deal_id": deal_id
		}).execute()

		if response.data:
			return jsonify({"success": True, "message": "Deal saved successfully"}), 201
		else:
			logger.error(f"Error saving deal in Supabase")
			return jsonify({"success": False, "message": "Failed to save deal"}), 400

	except Exception as e:
		logger.error(f"Error marking deal as saved: {str(e)}", exc_info=True)
		return jsonify({"success": False, "message": f"Error saving deal: {str(e)}"}), 500

def unmark_deal_saved_in_db(deal_id, user_id):
	"""Removes a saved deal given the deal_id and user_id."""
	try:
		# Check if the deal is saved
		existing_entry = supabase.from_('Saved').select('user_id', 'deal_id') \
			.eq('user_id', user_id).eq('deal_id', deal_id).execute()

		if not existing_entry.data:  # If the entry does NOT exist
			return jsonify({"success": False, "message": "Deal is not saved in Supabase"}), 404

		# Delete the saved deal
		response = supabase.from_('Saved').delete().eq('user_id', user_id).eq('deal_id', deal_id).execute()

		if response.data:
			return jsonify({"success": True, "message": "Deal unsaved successfully"}), 200
		else:
			logger.error(f"Error unsaving deal: {str(response)}", exc_info=True)
			return jsonify({"success": False, "message": "Failed to unsave deal"}), 400

	except Exception as e:
		logger.error(f"Error unsaving deal: {str(e)}", exc_info=True)
		return jsonify({"success": False, "message": f"Error unsaving deal: {str(e)}"}), 500

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

def get_all_restaurant_deals_with_user_details_in_db(user_id=None):
	"""Fetches all restaurants and their deals from Supabase, along with user saved status."""
	try:
		# the query, get_all_restaurant_deals, can be viewed in supabase terminal using `SELECT pg_get_functiondef('get_all_restaurant_deals'::regproc);`
		# NOTE: if you want to change what it returns, you need to modify `get_all_restaurant_deals`, ask joyce if you need help
		response = supabase.rpc('get_all_restaurant_deals', params={"target_user_id": user_id}).execute()
		data = response.data

		# Group deals by restaurant
		restaurant_map = {}

		for deal in data:
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
				"image_id": deal["image_id"],
				"user_saved": deal["user_saved"],
				"user_vote": deal["user_vote"],
				"applicable_group": deal["applicable_group"],
			})

		return list(restaurant_map.values())

	except Exception as e:
		logger.error(f"Failed to fetch data: {str(e)}", exc_info=True)
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

def process_and_filter_restaurant_deals(restaurants):
	"""Clean up and format restaurant data before sending to the Android app."""
	try:
		for restaurant in restaurants:
			valid_deals = []

			for deal in restaurant["Deal"]:
				deal['date_posted'] = iso_to_unix(deal['date_posted'])
				if deal.get('expiry_date'):
					deal['expiry_date'] = iso_to_unix(deal['expiry_date'])

					# Check if the deal is expired
					expiry_date = datetime.fromtimestamp(deal['expiry_date'], tz=pytz.UTC)
					today = datetime.now(tz=pytz.UTC)

					# Only keep non-expired deals
					if expiry_date >= today:
						valid_deals.append(deal)
					else:
						deal_id = deal['id']
						logger.info(f"Deal {deal_id} expired and was removed from the valid deals list.")
						mark_deal_expired_in_db(deal_id)

				else:
					valid_deals.append(deal)

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
		favicon_url = f"https://www.google.com/s2/favicons?sz=128&domain={domain}"
		return favicon_url

	except Exception as e:
		logger.error(f"Failed to get image URL for place_id {place_id}: {str(e)}", exc_info=True)
		return None


######### ROUTES ##############

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
		return jsonify({"error": "An error occurred while fetching restaurant deals"}), 500


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
			user_id = restaurant.get("user_id", "9f7ab2ec-15d8-4f31-8a33-8e4218a03e90")

			deal_item = {
				"restaurant_id": restaurant_id,
				"item": deal["item"],
				"description": deal.get("description"),
				"type": deal.get("type"),
				"expiry_date": datetime(deal["expiry_date"] / 1000).isoformat() if deal.get("expiry_date") else None,
				"date_posted": datetime(deal["date_posted"] / 1000).isoformat(),
				"user_id": user_id,
				"image_id": deal.get("imageId")
			}

			response = supabase.from_('Deal').insert([deal_item]).execute()
			deal_uuid = response.data[0]['id'] if response.data else None
			logger.info(f"Added new deal: {deal['item']} for restaurant {restaurant.get('restaurant_name')}")

			return jsonify({"dealId": str(deal_uuid)}), 200

	except Exception as e:
		error_message = str(e)
		logger.error(f"Error occurred at add_restaurant_deal: {error_message}", exc_info=True)
		return jsonify({"error": "An error occurred while adding the deal"}), 500


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
		return jsonify({"error": "An error occurred while searching for nearby restaurants"}), 500


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


@app.route('/delete_deal', methods=["GET"])
def delete_deal():
	deal_id = request.args.get('deal_id')
	user_id = request.args.get('user_id')

	deal = get_deal_by_id(deal_id)

	if not deal:
		return jsonify({"error": "Deal not found. Incorrect deal_id"}), 404

	if deal["user_id"] != user_id:
		return jsonify({"error": "Unauthorized: You are not the creator of this deal"}), 403

	try:
		mark_deal_expired_in_db(deal_id)
		return jsonify({"success": True, "message": "Deal successfully marked as expired"}), 200

	except Exception as e:
		return jsonify({"success": False, "message": f"Error deleting deal: {str(e)}"}), 500

@app.route('/')
def index():
	return "Successfully connected "

# Run the Flask app
if __name__ == '__main__':
	app.run(host='0.0.0.0', port=5001, debug=True)
