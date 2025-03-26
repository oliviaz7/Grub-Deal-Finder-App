import requests
import os
import logging
from server import get_restaurant_image_url

logger = logging.getLogger('werkzeug')

MAPS_API_KEY = os.getenv("MAPS_API_KEY")
NEARBY_SEARCH_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json"
WEBSITE_URL = "https://maps.googleapis.com/maps/api/place/details/json"

def _check_api_key():
    if not MAPS_API_KEY:
        logger.error("Missing API Key: MAPS_API_KEY is not set.")
        return False
    return True
def get_restaurant_website(place_id):
    if not _check_api_key():
        return None

    params = {
        "place_id": place_id,
        "fields": "name,website",
        "key": MAPS_API_KEY
    }
    response = requests.get(WEBSITE_URL, params=params)

    if response.status_code != 200:
        logger.error(f"Failed API Request: {WEBSITE_URL} responded with status code {response.status_code}.")
        return None

    data = response.json()

    return data.get("result", {}).get("website", None)

def search_nearby_restaurants(keyword, latitude, longitude, radius):
    if not _check_api_key():
        return []

    params = {
        "keyword": keyword,
        "location": f"{latitude},{longitude}",
        "radius": radius,
        "key": MAPS_API_KEY,
        "type": "restaurant",
    }

    # Make the request to the Google Places API
    response = requests.get(NEARBY_SEARCH_URL, params=params)

    if response.status_code != 200:
        logger.error(f"API Request Error for search_nearby_restaurants")
        return []

    places = response.json()

    nearby_restaurants = []
    for place in places.get("results", []):
        place_id = place["place_id"]
        place_info = {
            "id": "", # empty id
            "place_id": place_id,
            "restaurant_name": place["name"],
            "coordinates": {
                "latitude": place["geometry"]["location"]["lat"],
                "longitude": place["geometry"]["location"]["lng"]
            },
            "display_address": place["vicinity"],
            "Deal": [], # empty deals
            "image_url": get_restaurant_image_url(place_id)
        }

        nearby_restaurants.append(place_info)

    return nearby_restaurants


