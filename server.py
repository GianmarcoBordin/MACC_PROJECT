import asyncio
import websockets
import json
from util import player_opposite
from message import GenericMessage



class GameServer:
    def __init__(self):
        self.connected_players = {}     # player_id -> websocket
        self.player_skin = {}           # player_id -> Skin
        self.connection_pair = list()   # (A,B) (B,A) if both user are connected
        
    
    async def handle_client(self, websocket, path):
        
        try:
            
            async for message in websocket:
                
                json_data = json.loads(message)
                message_type = json_data.get("type")
                
                if message_type == "LASER" or message_type == "MOVEMENT":
                    await self.handle_game_message(json_data)
            
                elif message_type == "INIT":
                    
                    await self.handle_initialization(json_data, websocket)
                else:
                    await websocket.send("Unknown command.")
        
        except (websockets.exceptions.ConnectionClosedOK, websockets.exceptions.ConnectionClosedError ) as e:
            print(f"disconnection: {id(websocket)}")
        finally:
            await self.disconnect(websocket)

    
    async def handle_game_message(self, message):
        try:
            
            receiver_id = message.get("receiverId")
            receiver_socket = self.connected_players.get(receiver_id)
            if receiver_socket:
                await receiver_socket.send(json.dumps(message))

        except json.decoder.JSONDecodeError as e:
            print(e)



    async def handle_initialization(self, message, websocket):
        first_id = message.get("senderId")
        second_id = message.get("receiverId")
        player_position = message.get("playerPosition")
        player_type = message.get("playerType")
        
        self.connected_players[first_id] = websocket
        print(f"{first_id} -> {id(websocket)}")
        print(self.connection_pair)
        if (first_id, second_id, player_position) not in self.connection_pair:
            self.player_skin[first_id] = player_type
            self.connection_pair.append((first_id, second_id, player_position))
            await self.start_connection(first_id, second_id, player_position, player_type)
     
        else:
            await self.start_connection(first_id, second_id, player_position, player_type)


    async def start_connection(self,first_id, second_id, player_position, player_type):
    
        if (first_id, second_id, player_position) and (second_id, first_id, player_position) in self.connection_pair and second_id in self.connected_players:
            print(self.connection_pair)
            first_socket = self.connected_players[first_id]
            second_socket = self.connected_players[second_id]
            
            player_type = self.player_skin[second_id]
            second_player_type = self.player_skin[first_id]
            # todo
            first_message = GenericMessage("START",second_id, playerPosition = player_position, playerType = player_type).__dict__
            second_message = GenericMessage("START",first_id, playerPosition = player_opposite(player_position), playerType = second_player_type).__dict__
            
            await first_socket.send(json.dumps(first_message))
            print(f"sending to {first_id} via {id(first_socket)}")
                    
            await second_socket.send(json.dumps(second_message))
            print(f"sending to {second_id} via {id(second_socket)}")
                    
            print(f"connection between {first_id} and {second_id} started")



    async def disconnect(self, websocket):
        
        for player_id, ws in self.connected_players.items():
            if ws == websocket:
                del self.connected_players[player_id]
                del self.player_skin[player_id]
                for i in range(len(self.connection_pair)):
                    if player_id == self.connection_pair[i][0]:
                        
                        await self.notify_other_player(player_id, self.connection_pair[i][1])
                        self.connection_pair.pop(i)
                        break

                print(f"Player {player_id} disconnected.")  
                print(self.connected_players)       
                break

    async def notify_other_player(self, disconnected_player_id, online_player_id):
        if online_player_id in self.connected_players.keys():
            ws = self.connected_players[online_player_id]
            if ws:
                message = GenericMessage("DISCONNECTED",senderId = disconnected_player_id).__dict__
                await ws.send(json.dumps(message))
            del self.connected_players[online_player_id]
            print(f"Notified {online_player_id} about opponent's disconnection.")
            
            

    async def wait_for_player(self, player_id):
        for _ in range(10):
            if player_id in self.connected_players:
                return
            print(f"waiting player {player_id}")
            await asyncio.sleep(10)


    def start(self):
        start_server = websockets.serve(self.handle_client, "", 9000)
        print("Server started")
        asyncio.get_event_loop().run_until_complete(start_server)
        asyncio.get_event_loop().run_forever()


if __name__ == "__main__":
    server = GameServer()
    server.start()
