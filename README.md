# AR Battle Game

## Description

AR Battle Game is an Augmented Reality (AR) video game developed with Kotlin for Android platforms. Users can capture character skins using their camera and then use these skins in multiplayer battles against other users. Skin capturing is location-based, and users can check OpenStreetMap for nearby skins and players. Multiplayer sessions are managed by a WebSocket server, and user data and authentication are handled with Firebase. The skin generation server is hosted on PythonAnywhere. The 2D videogame graphics and dynamics were create via LibGDX framework

## Gameplay


![map_compressed](https://github.com/GianmarcoBordin/MACC_PROJECT/assets/92364167/e5488cbc-4023-4a08-9fd0-7e1038643a8d)
![capture_compressed](https://github.com/GianmarcoBordin/MACC_PROJECT/assets/92364167/86c1cdcc-94fc-496a-898b-0296b714f04f)
![battle](https://github.com/GianmarcoBordin/MACC_PROJECT/assets/92364167/1ab54546-0112-46bc-82d2-168d6c7b358e)



## Installation Instructions

1. Clone this repository:
    ```bash
    git clone https://github.com/GianmarcoBordin/MACC_PROJECT.git
    ```
2. Open the project in Android Studio.
3. Sync the project with Gradle.
4. Switch to the multiplayer server branch and download the server.py
5. Execute with ```python server.py```
6. Run the application on an Android device or emulator.

## Usage

1. Open the app and sign in with your Firebase account.
2. Use the map to search nearby skin and other players.
3. Capture a skin when you are close enough.
4. Engage in multiplayer battles with other users.
5. Enjoy the game and explore various skins and features.

## Features

- Augmented Reality skin capturing.
- Location-based gameplay using OpenStreetMap.
- Multiplayer battles with WebSocket server.
- Firebase authentication and data management.
- Skin generation hosted on PythonAnywhere.

## Technologies Used

- Kotlin
- AR sceneview
- LibGDX
- OpenStreetMap API
- Firebase
- PythonAnywhere
- WebSocket
