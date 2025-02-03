from flask import Flask, jsonify
from flask_cors import CORS
import os
from dotenv import load_dotenv
from supabase import create_client, Client

load_dotenv()
url = os.getenv("SUPABASE_URL")
key = os.getenv("SUPABASE_KEY")

supabase: Client = create_client(url, key)

app = Flask(__name__)

CORS(app)

@app.route('/')
def index():
	response = supabase.table("Deal").select("*").execute()

	# Check if the query was successful
	return jsonify(response.data)

# Run the Flask app
if __name__ == '__main__':
	app.run(host='0.0.0.0', port=5000, debug=True)
