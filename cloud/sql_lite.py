import sqlite3
import firebase_admin
from firebase_admin import credentials,firestore,auth
import random
import schedule
import time

# Initialize Firebase Admin SDK
cred = credentials.Certificate('/home/Gianm/mysite/google_services.json')
firebase_admin.initialize_app(cred)
firebase_db = firestore.client()
firebase_data = firebase_db.collection('location').get()
sql_lite_db_file = '/home/Gianm/mysite/ar.db'
season = 0
def init():
    try:
        # Connect to SQLite database (or create it if it doesn't exist)
        conn = sqlite3.connect(sql_lite_db_file)

        # Create a cursor object to execute SQL commands
        cursor = conn.cursor()

        # Create table for players
        cursor.execute('''CREATE TABLE IF NOT EXISTS players (
                            username TEXT PRIMARY KEY
                        )''')

        # Insert sample data into the player table
        cursor.execute("INSERT INTO players (username) VALUES (?)", ('Gabriele',))
        cursor.execute("INSERT INTO players (username) VALUES (?)", ('carlo',))
        cursor.execute("INSERT INTO players (username) VALUES (?)", ('jean',))


        # Create a table for user rankings
        cursor.execute('''CREATE TABLE IF NOT EXISTS user_rankings (
                            username TEXT PRIMARY KEY,
                            score INTEGER NOT NULL,
                            FOREIGN KEY (username) REFERENCES players(username) ON UPDATE CASCADE ON DELETE CASCADE
                        )''')

        # Insert sample data into the table
        cursor.execute("INSERT INTO user_rankings (username, score) VALUES (?, ?)", ('Gabriele', 100))
        cursor.execute("INSERT INTO user_rankings (username, score) VALUES (?, ?)", ('carlo', 150))
        cursor.execute("INSERT INTO user_rankings (username, score) VALUES (?, ?)", ('jean', 200))


        # Create table for GameItems
        cursor.execute('''CREATE TABLE IF NOT EXISTS game_items (
                            gameItemId INTEGER,
                            gameItemOwner TEXT,
                            gameItemRarity INTEGER,
                            gameItemHp INTEGER,
                            gameItemDamage INTEGER,
			    FOREIGN KEY (gameItemOwner) REFERENCES players(username) ON UPDATE CASCADE ON DELETE CASCADE,
			                PRIMARY KEY (gameItemOwner, gameItemId),
                            UNIQUE (gameItemOwner, gameItemId)
                        )''')

        # Insert sample data into the item table
        #cursor.execute("INSERT INTO game_items (gameItemId, gameItemOwner, gameItemRarity, gameItemHp, gameItemDamage) VALUES (?, ?, ?, ?, ?)", (1,'Gabriele', 1, 1, 1))
        #cursor.execute("INSERT INTO game_items (gameItemId, gameItemOwner, gameItemRarity, gameItemHp, gameItemDamage) VALUES (?, ?, ?, ?, ?)", (2,'carlo', 2, 2, 2))
        #cursor.execute("INSERT INTO game_items (gameItemId, gameItemOwner, gameItemRarity, gameItemHp, gameItemDamage) VALUES (?, ?, ?, ?, ?)", (3,'jean', 3, 3, 3))


        # Commit the changes
        conn.commit()

        # Fetch all rows from the user_rankings table
        cursor.execute("SELECT * FROM user_rankings")
        print("User Rankings:")
        print(cursor.fetchall())

        # Fetch all rows from the players table
        cursor.execute("SELECT * FROM players")
        print("Players:")
        print(cursor.fetchall())

        # Fetch all rows from the game items table
        cursor.execute("SELECT * FROM game_items")
        print("Game Items:")
        print(cursor.fetchall())

    except sqlite3.Error as e:
        print("ERROR: Failed to connect to the database:", e)

    finally:
        # Close the cursor and connection
        cursor.close()
        conn.close()

def firestore_consistency_check():
    try:
        print("Consistency check started reached")
        # Check consistency for players table
        firestore_players_data = {}
        for doc in firebase_db.collection('players').stream():
            doc_data = doc.to_dict()
            firestore_players_data[doc_data['username']] = {'username': doc_data['username']}
        print(firestore_players_data)

        conn = sqlite3.connect(sql_lite_db_file)
        cursor = conn.cursor()
        cursor.execute("SELECT * FROM players")
        sqlite_players_data = {row[0]: {'username': row[0]} for row in cursor.fetchall()}
        cursor.execute("SELECT * FROM user_rankings")
        sqlite_rankings_data = {row[0]: {'username': row[0], 'score' : row[1]} for row in cursor.fetchall()}
        for firestore_id, firestore_entry in firestore_players_data.items():
            sqlite_entry = sqlite_players_data.get(firestore_id)
            sqlite_entry_rankings = sqlite_rankings_data.get(firestore_id)
            if (sqlite_entry is None or sqlite_entry != firestore_entry) and firestore_id is not None:
                cursor.execute("INSERT OR REPLACE INTO players (username) VALUES (?)",
                               (firestore_entry['username'],))
            if (sqlite_entry_rankings is None or sqlite_entry_rankings != firestore_entry) and firestore_id is not None:
               cursor.execute("INSERT OR REPLACE INTO user_rankings (username, score) VALUES (?, COALESCE((SELECT score FROM user_rankings WHERE username = ?), 0))",
               (firestore_entry['username'], firestore_entry['username']))



        # check if an entry is in sqlite players table but not in firestore and if it is true delete the row
        # Iterate over SQLite players data
        for sqlite_id, sqlite_entry in sqlite_players_data.items():
            # Check if the player exists in Firestore
            if sqlite_id not in firestore_players_data:
                # Player exists in SQLite but not in Firestore, delete it from SQLite
                cursor.execute("DELETE FROM players WHERE username = ?", (sqlite_entry['username'],))


    except Exception as e:
        print("Error:", e)

    finally:
        # Close connections in the finally block
        conn.commit()
        conn.close()
        print("Consistency check completed.")

def km_to_degrees(km):
    """Convert kilometers to degrees."""
    circumference_km = 40075  # Earth's circumference in kilometers
    degrees = (km / circumference_km) * 360
    return degrees

def generate_random_coordinates_around_centers(num_points, centers, radius_km):
    """
    Generate random geographic coordinates around randomly selected center points.

    Args:
    - num_points (int): Number of coordinates to generate.
    - centers (list of tuples): List of center points, where each tuple contains (latitude, longitude).
    - radius_km (float): Radius around each center point in kilometers.

    Returns:
    - List of tuples: List of generated coordinates, where each tuple contains (latitude, longitude).
    """
    coordinates = []
    radius_deg = km_to_degrees(radius_km)
    for _ in range(num_points):
        center = random.choice(centers)
        lat, lon = center
        lat_offset = random.uniform(-radius_deg, radius_deg)
        lon_offset = random.uniform(-radius_deg, radius_deg)
        new_lat = lat + lat_offset
        new_lon = lon + lon_offset
        coordinates.append((new_lat, new_lon))
    return coordinates


def get_centers_from_firestore():
    """Retrieve the list of centers from a Firestore collection."""
    try:
        centers = []
        docs = firebase_db.collection('players').stream()
        for doc in docs:
            center_data = doc.to_dict()
            location = center_data.get('location')
            if location:
                lat = location.latitude
                lon = location.longitude
                centers.append((lat, lon))
        return centers
    except Exception as e:
        print(f"An error occurred: {e}")
        return []
    finally:
        print("finally reached")

def spawn(num_points):
    radius_km = 0.1  # 100 m radius
    centers = get_centers_from_firestore()
    random_coordinates_around_centers = generate_random_coordinates_around_centers(num_points, centers, radius_km)
    for i, coord in enumerate(random_coordinates_around_centers, start=1):
        print(f"Point {i}: Latitude={coord[0]}, Longitude={coord[1]}")
    return random_coordinates_around_centers


def generate_random_item(location):
    """Generate random item data."""
    id = random.randint(1, 10)
    rarity = random.choice(["1", "2", "3","4","5"])
    return {"itemId": id, "itemRarity": rarity, "location":location},str(id)

def post_random_items_to_firestore():
    """
    Generate random num items data and post it to a Firestore collection.

    """
    try:
        num_items = random.randint(1, 5)
        random_coordinates_around_centers = spawn(num_items)
        geo_points = []
        for lat, lon in random_coordinates_around_centers:
            geo_point = firestore.GeoPoint(lat, lon)
            geo_points.append(geo_point)
        for i in range(num_items):
            location = geo_points[i]
            item_data,itemId = generate_random_item(location)
            firebase_db.collection('items').document(f"item{itemId}").set(item_data)
            print(f"Posted item: {item_data}")
        # expired session reset skins

        # Connect to SQLite database (or create it if it doesn't exist)
        conn = sqlite3.connect(sql_lite_db_file)

        # Create a cursor object to execute SQL commands
        cursor = conn.cursor()

        # Delete all game_items from table
        cursor.execute("DELETE FROM game_items")

        # Fetch all rows from the game items table
        cursor.execute("SELECT * FROM game_items")
        print("Game Items:")
        print(cursor.fetchall())

        # communicate the expiring of the session to firestore db
        itemId = 0
        item_data['itemId'] = 0
        global season
        season += 1
        item_data['itemRarity'] = season

        firebase_db.collection('items').document(f"item{itemId}").set(item_data)
        print(f"Posted session expired: {item_data}")
    except Exception as e:
        print(f"An error occurred: {e}")
    finally:
        # Close connections in the finally block
        conn.commit()
        conn.close()
        print("finally reached, new session begins")


if __name__ == '__main__':
    # Initialize SQL Lite database
    init()
    # Schedule synchronization task to run every hour
    schedule.every().hours.do(firestore_consistency_check)
    schedule.every().hours.do(post_random_items_to_firestore)

    # Main loop to run scheduled tasks
    while True:
        schedule.run_pending()
        time.sleep(1)


