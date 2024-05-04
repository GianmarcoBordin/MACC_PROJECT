class Message:
    def __init__(self, type):
        self.type = type

class MovementMessage(Message):
    def __init__(self, senderId, receiverId, newX, newY):
        super().__init__("MOVEMENT")
        self.senderId = senderId
        self.receiverId = receiverId
        self.newX = newX
        self.newY = newY

class InitMessage(Message):
    def __init__(self, first_id, second_id):
        super().__init__("INIT")
        self.firstId = first_id
        self.secondId = second_id

class StartMessage(Message):
    def __init__(self, otherId):
        super().__init__("START")
        self.otherPlayerId = otherId
        
class GenericMessage():
    def __init__(self, type, senderId, receiverId, newX, newY, playerType):
        self.type = type
        self.senderId = senderId
        self.receiverId = receiverId
        self.newX = newX
        self.newY = newY
        self.playerType = playerType
