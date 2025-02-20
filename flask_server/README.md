# Flask Server for Android App

This repository contains the backend Flask server that interacts with the Android app. 
The server exposes endpoints for the Android app to access and retrieve data from Supabase.

## Setup Instructions

### Prerequisites

- Python 3.x
- `pip` (Python package installer)
- Supabase account (for accessing the Supabase URL and Key)

### Step 1: Navigate to the Flask Server Directory

cd `flask_server`

### Step 2: Set Up the Virtual Environment

python3 -m venv venv

# Activate the virtual environment:
    On macOS/Linux: `source venv/bin/activate`
    On Windows: `.\venv\Scripts\activate`

### Step 3: Install Dependencies
pip install -r requirements.txt

### Step 4: Set Up Environment Variables
Copy the `.env.example` file to a new file named `.env` with `cp .env.example .env` and replace the placeholder values (search SUPABASE_URL in discord)

### Step 5: Run the Flask Server
To start the Flask server, run: `python server.py`