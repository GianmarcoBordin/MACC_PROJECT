from flask import Flask, request, jsonify
from flask_restful import Resource, Api, request
import sqlite3

app = Flask(__name__)
api = Api(app)
db_file = '/home/Gianm/mysite/ar.db'


class UserRankings(Resource):
    def get(self):
        # Implement GET method for user_rankings table
        # Attempt to connect to the database
        try:
            conn = sqlite3.connect(db_file)
            cursor = conn.cursor()
            user = request.args.get('user')
            if user:
                cursor.execute("SELECT * FROM user_rankings WHERE username = ?", (user,))
            else:
                cursor.execute("SELECT * FROM user_rankings ORDER BY score DESC")
            rows = cursor.fetchall()
            ranks = []
            for row in rows:
                rank = {
                    "username": row[0],
                    "score": row[1]
                }
                ranks.append(rank)
            return jsonify(ranks)
        except sqlite3.Error as e:
            print("ERROR: Failed to connect to the database:", e)
            return jsonify([]), 500
        finally:
            cursor.close()
            conn.close()

    def post(self):
        # Implement POST method for user_rankings table
        # Assuming the client sends JSON data
        data = request.json
        # Attempt to connect to the database
        try:
            conn = sqlite3.connect(db_file)
            cursor = conn.cursor()

            # Fetch old score for the specified username
            cursor.execute("SELECT score FROM user_rankings WHERE username = ?", (data['username'],))
            old_score = cursor.fetchone()

            if old_score:
                # Calculate new score
                new_score = old_score[0] + int(data['score'])
            else:
                # If no old score exists, set new score to the score received in the request
                new_score = int(data['score'])

            # Update user_rankings table with new score
            cursor.execute("INSERT OR REPLACE INTO user_rankings (username, score) VALUES (?, ?)", (data['username'], new_score))
            conn.commit()

            return jsonify({'message': 'Data received', 'data': data})
        except sqlite3.Error as e:
            print("ERROR: Failed to connect to the database:", e)
            return jsonify([]), 500
        finally:
            cursor.close()
            conn.close()


class Players(Resource):
    def get(self):
        try:
            conn = sqlite3.connect(db_file)
            cursor = conn.cursor()
            user = request.args.get('user')
            if user:
                cursor.execute("SELECT * FROM players WHERE username = ?", (user,))
            else:
                cursor.execute("SELECT * FROM players")
            rows = cursor.fetchall()
            players = []
            for row in rows:
                player = {
                    "username": row[0]
                }
                players.append(player)
            return jsonify(players)
        except sqlite3.Error as e:
            print("ERROR: Failed to connect to the database:", e)
            return jsonify([]), 500
        finally:
            cursor.close()
            conn.close()

    def post(self):
        data = request.json
        try:
            conn = sqlite3.connect(db_file)
            cursor = conn.cursor()
            cursor.execute("INSERT OR REPLACE INTO players (username) VALUES (?)", (data['username'],))
            conn.commit()
            return jsonify({'message': 'Data received', 'data': data})
        except sqlite3.Error as e:
            print("ERROR: Failed to connect to the database:", e)
            return jsonify([]), 500
        finally:
            cursor.close()
            conn.close()

class GameItems(Resource):
    def get(self):
        try:
            conn = sqlite3.connect(db_file)
            cursor = conn.cursor()
            user = request.args.get('user')
            rarity = request.args.get('rarity')
            if user and rarity:
                cursor.execute("SELECT * FROM game_items WHERE gameItemOwner = ? AND gameItemRarity = ?",(user, rarity))
            elif user:
                cursor.execute("SELECT * FROM game_items WHERE gameItemOwner = ?", (user,))
            else:
                cursor.execute("SELECT * FROM game_items")
            rows = cursor.fetchall()
            gameItems = []
            for row in rows:
                gameItem = {
                    "itemId":row[0],
                    "owner": row[1],
                    "rarity": row[2],
                    "hp": row[3],
                    "damage": row[4]
                }
                gameItems.append(gameItem)
            return jsonify(gameItems)
        except sqlite3.Error as e:
            print("ERROR: Failed to connect to the database:", e)
            return jsonify([]), 500
        finally:
            cursor.close()
            conn.close()
    def post(self):
        try:
            data = request.json
            conn = sqlite3.connect(db_file)
            cursor = conn.cursor()
            if 'deleteItemId' in data:
                # Delete operation
                cursor.execute("DELETE FROM game_items WHERE gameItemId = ? AND gameItemOwner = ?", (data['deleteItemId'],data['owner']))
                message = f"Item with id {data['deleteItemId']} deleted."
            else:
                # Insert or replace operation
                if 'itemId1' in data:
                    # Additional logic for itemId1, such as delete another item and increment values
                    cursor.execute("DELETE FROM game_items WHERE gameItemId = ?  AND gameItemOwner = ?", (data['itemId2'],data['owner']))
                    cursor.execute(
                        "INSERT OR REPLACE INTO game_items (gameItemId, gameItemOwner, gameItemRarity, gameItemHp, gameItemDamage) VALUES (?, ?, ?, ?, ?)",
                        (data['itemId1'], data['owner'], data['rarity'], int(data['hp']) + 1, int(data['damage']) + 1)
                    )
                    message = f"Item with id {data['itemId1']} inserted/replaced and item with id {data['itemId2']} deleted."
                else:
                    cursor.execute(
                        "INSERT OR REPLACE INTO game_items (gameItemId, gameItemOwner, gameItemRarity, gameItemHp, gameItemDamage) VALUES (?, ?, ?, ?, ?)",
                        (data['itemId'], data['owner'], data['rarity'], data['hp'], data['damage'])
                    )
                    message = f"Item with id {data['itemId']} inserted/replaced."
            conn.commit()
            return jsonify({'message': message, 'data': data})
        except sqlite3.Error as e:
            print("ERROR: Failed to connect to the database:", e)
            return jsonify([]), 500
        finally:
            cursor.close()
            conn.close()

# Add resources to API endpoints
api.add_resource(UserRankings, '/user_rankings')
api.add_resource(Players, '/players')
api.add_resource(GameItems, '/game_items')

if __name__ == '__main__':
    # start server
    app.run(port=8089)
