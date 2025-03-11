from flask import Flask, jsonify, request
from flask_cors import CORS
import os
from datetime import datetime
from dotenv import load_dotenv
from supabase import create_client, Client
import google_maps
import math
import logging

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

def iso_to_unix(iso_date):
	try:
		return int(datetime.fromisoformat(iso_date.replace("Z", "+00:00")).timestamp() * 1000)

	except Exception as e:
		logger.error(f"Error occurred at iso_to_unix: {str(e)}", exc_info=True)
		return None

def get_all_restaurant_deals():
	"""Fetches all restaurants and their deals from Supabase."""
	try:
		result = supabase.from_('Restaurant').select('*, Deal(*)').execute()
		return result.data
	except Exception as e:
		logger.error(f"Failed to fetch restaurant deals: {str(e)}", exc_info=True)
		return []

def get_restaurants_given_filters(user_lat, user_long, radius):
	"""Filter restaurants based on user location and radius."""
	restaurant_deals = get_all_restaurant_deals()
	filtered_restaurants = []

	for restaurant in restaurant_deals:
		res_lat = restaurant["latitude"]
		res_long = restaurant["longitude"]
		distance = haversine(res_lat, res_long, user_lat, user_long)

		# check if restaurant distance is within user location
		if distance <= radius:
			filtered_restaurants.append(restaurant)

	return filtered_restaurants

def format_restaurant_data(restaurants):
	"""Clean up and format restaurant data before sending to the Android app."""
	for restaurant in restaurants:
		restaurant['coordinates'] = {
			"latitude": restaurant["latitude"],
			"longitude": restaurant["longitude"]
		}

		for deal in restaurant["Deal"]:
			deal['date_posted'] = iso_to_unix(deal['date_posted'])
			if deal.get('expiry_date'):
				deal['expiry_date'] = iso_to_unix(deal['expiry_date'])

	return restaurants

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
		logger.info(f"Fetching deals for lat: {latitude}, long: {longitude}, radius: {radius}")

		# Filter restaurants based on coords and radius
		filtered_restaurants = get_restaurants_given_filters(latitude, longitude, radius)

		# Format the restaurant data
		formatted_restaurants = format_restaurant_data(filtered_restaurants)

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
			deal_item = {
				"restaurant_id": restaurant_id,
				"item": deal["item"],
				"description": deal.get("description"),
				"type": deal.get("type"),
				"expiry_date": datetime.utcfromtimestamp(deal["expiry_date"] / 1000).isoformat() if deal.get("expiry_date") else None,
				"date_posted": datetime.utcfromtimestamp(deal["date_posted"] / 1000).isoformat(),
				"user_id": "9f7ab2ec-15d8-4f31-8a33-8e4218a03e90", # guest account
				"restrictions": deal.get("restrictions"),
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


@app.route('/search_nearby_restaurants')
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


@app.route('/')
def index():
	return "Successfully connected "

# Run the Flask app
if __name__ == '__main__':
	app.run(host='0.0.0.0', port=5001, debug=True)
