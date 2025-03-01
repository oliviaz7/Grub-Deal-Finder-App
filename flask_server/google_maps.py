import requests
import os

MAPS_API_KEY = os.getenv("MAPS_API_KEY")
NEARBY_SEARCH_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json"
WEBSITE_URL = "https://maps.googleapis.com/maps/api/place/details/json"

def get_restaurant_website(place_id):
    params = {
        "place_id": place_id,
        "fields": "name,website",
        "key": MAPS_API_KEY
    }
    response = requests.get(WEBSITE_URL, params=params)
    data = response.json()

    if response.status_code != 200:
        return None

    return data.get("result", {}).get("website", None)

# TODO: NOT USED ANYMORE
def get_place_ids(latitude, longitude, radius):
    # Prepare the parameters for the API request
    params = {
        "location": f"{latitude},{longitude}",
        "radius": radius,
        "key": MAPS_API_KEY,
        "type": "restaurant",
    }

    # Make the request to the Google Places API
    response = requests.get(NEARBY_SEARCH_URL, params=params)

    if response.status_code != 200:
        return None

    places = response.json()
    place_ids = [place["place_id"] for place in places.get("results", [])]

    return place_ids
