from flask import Flask, jsonify, request
from flask_cors import CORS
import os
from datetime import datetime
from dotenv import load_dotenv
from supabase import create_client, Client
from google_maps import get_place_ids

load_dotenv()
url = os.getenv("SUPABASE_URL")
key = os.getenv("SUPABASE_KEY")

supabase: Client = create_client(url, key)

app = Flask(__name__)

CORS(app)

def iso_to_unix(iso_date):
	return int(datetime.fromisoformat(iso_date.replace("Z", "+00:00")).timestamp() * 1000)

@app.route('/all_restaurant_deals', methods=["GET"])
def get_all_deals():
	result = supabase.from_('Restaurant').select('*, Deal(*)').execute()
	result_data = result.data
	return jsonify(result_data)

# TODO: connect to Google Maps API to get a list of place ids given coordinates and radius. Currently gets all restaurant deals.
@app.route('/restaurant_deals', methods=["GET"])
def get_deals():
	try:
		"""Gets restaurant deals from Supabase given a latitude, longitude and radius."""
		latitude = request.args.get('latitude')
		longitude = request.args.get('longitude')
		radius = request.args.get('radius')

		# google maps api call to get place id
		place_ids = get_place_ids(latitude, longitude, radius)

		# TODO: FIGURE OUT BEHAVIOUR FOR FRONT END SIDE
		if not place_ids:
			return jsonify({"error": "Failed to retrieve places from Google Maps API"}), 500

		# Supabase query to get fatty restaurant deals
		result = supabase.from_('Restaurant').select('*, Deal(*)').in_('place_id', place_ids).execute()
		result_data = result.data

		# clean up data to send to android app
		for restaurant in result_data:
			restaurant['coordinates'] = {
				"latitude": restaurant['latitude'],
				"longitude": restaurant['longitude']
			}

			for deal in restaurant["Deal"]:
				deal['date_posted'] = iso_to_unix(deal['date_posted'])
				if deal['expiry_date']:
					deal['expiry_date'] = iso_to_unix(deal['expiry_date'])

		return jsonify(result_data)

	except Exception as e:
		print(f"Error occurred: {str(e)}")
		return jsonify({"error": str(e)}), 500

@app.route('/add_restaurant_deal', methods=["POST"])
def add_restaurant_deal():
	"""Adds a new restaurant deal to Supabase."""
	try:
		restaurant = request.json
		restaurant_place_id = restaurant.get("place_id")

		# Check if restaurant already exists
		existing_restaurant = supabase.from_('Restaurant').select('place_id').eq('place_id', restaurant_place_id).execute()

		# Insert restaurant if it doesnt exist
		if not existing_restaurant.data:
			restaurant_data = {
				"id": restaurant.get("id"),
				"place_id": restaurant.get("place_id"),
				"restaurant_name": restaurant.get("restaurant_name"),
				"latitude": restaurant["coordinates"]["latitude"],
				"longitude": restaurant["coordinates"]["longitude"]
			}
			supabase.from_('Restaurant').insert([restaurant_data]).execute()

		# Insert deal
		deal = restaurant.get("Deal", [None])[0] if restaurant.get("Deal") else None
		if deal:
			deal_item = {
				"id": deal["id"],
				"restaurant_id": restaurant.get("id"),
				"item": deal["item"],
				"description": deal.get("description"),
				"type": deal["type"],
				"expiry_date": datetime.utcfromtimestamp(deal["expiry_date"] / 1000).isoformat() if deal.get("expiry_date") else None,
				"date_posted": datetime.utcfromtimestamp(deal["date_posted"] / 1000).isoformat(),
				"user_id": deal["user_id"],
				"restrictions": deal["restrictions"],
				"image_id": deal.get("imageId")
			}

			supabase.from_('Deal').insert([deal_item]).execute()

			return jsonify({"message": "Deal added successfully"}), 201

	except Exception as e:
		error_message = str(e)
		print(f"Error occurred: {error_message}")
		return jsonify({"error": str(e)}), 500


@app.route('/')
def index():
	return "Successfully connected "

# Run the Flask app
if __name__ == '__main__':
	app.run(host='0.0.0.0', port=5000, debug=True)
