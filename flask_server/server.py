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
	latitude = request.args.get('latitude')
	longitude = request.args.get('longitude')
	radius = request.args.get('radius')

	# google maps api call to get place id
	place_ids = get_place_ids(latitude, longitude, radius)

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

@app.route('/')
def index():
	return "Successfully connected "

# Run the Flask app
if __name__ == '__main__':
	app.run(host='0.0.0.0', port=5000, debug=True)
