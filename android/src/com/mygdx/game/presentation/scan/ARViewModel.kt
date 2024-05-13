package com.mygdx.game.presentation.scan

import android.graphics.Bitmap
import android.location.Location
import android.util.Log
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.core.graphics.scale
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mygdx.game.data.dao.GameItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.mygdx.game.data.dao.Line
import com.mygdx.game.data.dao.Message
import com.mygdx.game.data.dao.Ownership
import com.mygdx.game.data.manager.UpdateListener
import com.mygdx.game.domain.usecase.ar.ARUseCases
import com.mygdx.game.presentation.map.MapViewModel
import com.mygdx.game.presentation.scan.events.BitmapEvent
import com.mygdx.game.presentation.scan.events.DataStoreEvent
import com.mygdx.game.presentation.scan.events.DimensionsEvent
import com.mygdx.game.presentation.scan.events.FocusEvent
import com.mygdx.game.presentation.scan.events.GameEvent
import com.mygdx.game.presentation.scan.events.LineEvent
import com.mygdx.game.presentation.scan.events.UpdateDatabaseEvent
import com.mygdx.game.presentation.scan.events.VisibilityEvent
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.sqrt
import kotlin.random.Random

@HiltViewModel
class ARViewModel @Inject constructor(
    private val arUseCases: ARUseCases
) : ViewModel(), UpdateListener {
    // -> ARScreen
    var scanned = false
    var counter = 0
    // -> CaptureScreen
    var screenWidth = 0
    var screenHeight = 0
    // components of background image
    var bitmap : Bitmap? = null
    var imageRatio = 0.0f
    // counter for the general movement
    private var directionCounter = 120
    // bool to check if the direction towards a line has been already set
    private var directionTowardsLine = false
    // counter for movement towards closest point of the line
    private var directionTowardsLineCounter = 0
    private var direction = Vector2(0f, 0f)
    // counter for the presence of the bullets on the screen
    private var justShoot = 0

    // GameItem retrieved by the DataStore (the one generated from the server)
    private var gameItem: GameItem = GameItem()
    // GameItem retrieved by the database (the one the player has already captured)
    var ownedGameItem: GameItem = GameItem()

    private val _state = MutableStateFlow(GameState())
    val state = _state.asStateFlow()

    fun onDataStoreEvent(event: DataStoreEvent) {
        when (event) {
            DataStoreEvent.readDataStore -> {
                viewModelScope.launch {
                    gameItem = arUseCases.readGameItem()
                }
                // set the gameitem and its health
                _state.value = state.value.copy(gameItem = gameItem, hp = gameItem.hp)
            }
        }
    }

    fun onFocusEvent(event: FocusEvent) {
        when (event) {
            is FocusEvent.Focus -> {
                scanned = event.value
            }
        }
    }

    fun onVisibilityEvent(event: VisibilityEvent) {
        when (event) {
            is VisibilityEvent.Visible -> {
                if (event.value) {
                    counter++
                } else {
                    counter = 0
                }
            }
        }
    }

    fun onDimensionsEvent(event: DimensionsEvent) {
        when (event) {
            is DimensionsEvent.Dimensions -> {
                screenWidth = event.width
                screenHeight = event.height
            }
        }
    }

    fun onBitmapEvent(event: BitmapEvent) {
        when (event) {
            is BitmapEvent.BitmapCreated -> {
                bitmap = event.bitmap
            }
        }
    }

    fun onLineEvent(event: LineEvent) {
        when (event) {
            is LineEvent.AddNewLine -> {
                _state.value.lines.add(event.line)
            }

            LineEvent.DeleteAllLines -> {
                _state.value.lines.clear()
            }
        }
    }

    // bitmap is not used because it is pointless to save it on the db
    // (it requires more bandwidth, but provide no benefit for the user)
     fun onUpdateDatabaseEvent(event: UpdateDatabaseEvent) {
        when (event) {
            is UpdateDatabaseEvent.IncrementItemStats -> {
                val updatedGameItem = GameItem(ownedGameItem.id,
                    ownedGameItem.rarity,
                    ownedGameItem.hp + event.hpIncrement,
                    ownedGameItem.damage + event.damageIncrement)
                viewModelScope.launch {
                    arUseCases.addGameItem(updatedGameItem)
                }
            }

            UpdateDatabaseEvent.AddItem -> {
                val newGameItem = GameItem(state.value.gameItem.id,
                    state.value.gameItem.rarity,
                    state.value.gameItem.hp,
                    state.value.gameItem.damage)
                viewModelScope.launch {
                    arUseCases.addGameItem(newGameItem)
                }
            }

            is UpdateDatabaseEvent.AddOwnership -> {
                viewModelScope.launch {
                    val username = arUseCases.fetchUserProfile()?.displayName
                    val ownership = username?.let { Ownership(event.itemId, it) }
                    if (ownership != null) {
                        arUseCases.addOwnership(ownership)
                    }
                }
            }
        }
    }

     fun onGameEvent(event: GameEvent) {
        when (event) {
            is GameEvent.StartGame -> {
                // compute the ratio of the background image
                val imageWidthRatio = screenWidth / (bitmap?.width ?: 1).toFloat()
                val imageHeightRatio = screenHeight / (bitmap?.height ?: 1).toFloat()
                imageRatio = max(imageWidthRatio, imageHeightRatio)

                // -> bitmap of the bullets
                // convert to bitmap
                val bulletsBitmap = event.bullets.asAndroidBitmap()
                // scale the bullets so that its height is equal to the height of the item,
                // but the ratio between its dimensions is maintained
                val bulletsHeight = gameItem.bitmap.height
                val bulletsOriginalHeight = bulletsBitmap.height
                val bulletsRatio = bulletsHeight.toDouble() / bulletsOriginalHeight
                val bulletsWidth = (bulletsBitmap.width * bulletsRatio).toInt()
                val finalBullets = bulletsBitmap.scale(bulletsWidth, bulletsHeight)

                // set the bitmap and make the game start
                _state.value = state.value.copy(shootBitmap = finalBullets, isStarted = true)


                val itemId = state.value.gameItem.id

                viewModelScope.launch {
                    val username = arUseCases.fetchUserProfile()?.displayName
                    // set if the player already owns the item
                    if (username?.let { arUseCases.getOwnership(it, itemId).isNotEmpty() } == true) {
                        _state.value.owned = true
                        // because the player already owns an item, then retrieve it immediately and store it
                        val gameItemString = arUseCases.getGameItem(username, gameItem.rarity.toString())
                        gameItemString.value?.let {
                            Log.e("DEBUG", gameItemString.toString())
                            val id = it[0]
                            val rarity = it[1]
                            val hp = it[2]
                            val damage = it[3]
                            ownedGameItem = GameItem(id, rarity.toInt(), hp.toInt(), damage.toInt())
                        }
                    } else {
                        _state.value.owned = false
                    }

                    Log.e("DEBUG", "OWNED = ${state.value.owned}")
                    Log.e("DEBUG", "ITEM = ${ownedGameItem.hp}, ${ownedGameItem.damage}")

                    while (!state.value.isGameOver) {
                        // update each 16 milliseconds (60Hz)
                        delay(16L)
                        _state.value = updateGame(state.value)
                    }
                    // TODO CHECK now owns the item
                    _state.value.owned = true
                    // delete all remaining lines
                    _state.value.lines.clear()
                }
            }

            GameEvent.ResetGame -> {
                // reset the scanned value because the player must be able to scan it again
                scanned = false
                // reset the direction values
                directionCounter = 120
                directionTowardsLine = false
                directionTowardsLineCounter = 0
                direction = Vector2(0f, 0f)
                // reset the gamestate
                _state.value = GameState()
            }
        }
    }

    // Method to update the game state
    private fun updateGame(currentGame: GameState): GameState {
        if (currentGame.isGameOver) {
            return currentGame
        }

        // Move the object
        val newPosition = moveObject(currentGame)

        // make sure the item does not go outside the bound of the screen
        if (newPosition.x < 0f) {
            newPosition.x = 0f
        }
        if (newPosition.x > screenWidth - 10) {
            newPosition.x = (screenWidth - 10).toFloat()
        }
        if (newPosition.y < 0f) {
            newPosition.y = 0f
        }
        if (newPosition.y > screenHeight - 10) {
            newPosition.y = (screenHeight - 10).toFloat()
        }

        var shoot = false
        // 0.333% probability of shooting
        // on average, it shoots every 5 seconds
        if (Random.nextFloat() > 0.99667f || justShoot > 0) {
            shoot = true

            // increase the counter each time
            justShoot++
            // if the counter has reached 60 frames, then reset it
            if (justShoot == 60) {
                justShoot = 0
            }
        }

        // update the shoot position according to the item position
        val addition = Vector2(
            state.value.gameItem.bitmap.width.toFloat(),
            (state.value.gameItem.bitmap.height / 8).toFloat()
        )
        val newShootPosition = newPosition.add(addition)

        // update item hitbox
        val hitbox = updateHitbox(newPosition, currentGame.gameItem.bitmap)
        // update shoot hitbox (if shoot is false, then return an empty list)
        val shootHitbox = if (shoot) updateHitbox(newShootPosition, currentGame.shootBitmap) else mutableListOf()

        // detect collisions
        if (detectCollision(hitbox, state.value.lines) || detectCollision(shootHitbox, state.value.lines)) {
            _state.value.lines.clear()
        } else {
            val circle = findFirstCircle(currentGame.lines)
            if (isGameObjectInsideCircle(currentGame.position.toOffset(), circle)) {
                _state.value.hp--
                _state.value.lines.clear()
            }
            // if the health is 0, then the item is captured
            if (state.value.hp == 0)
                return currentGame.copy(isGameOver = true)
        }

        return currentGame.copy(position = newPosition, hitbox = hitbox, shoot = shoot, shootPosition = newShootPosition, shootHitbox = shootHitbox)
    }

    // Method to move the object
    private fun moveObject(currentGame: GameState): Vector2 {
        // direction of the item is refreshed every 2 seconds, if there is no lines drawn by the user or
        // direction of the item is refreshed once, if the user has drawn some lines
        if (currentGame.lines.isEmpty()) {
            directionTowardsLine = false
            // reset the counter of the closest line
            directionTowardsLineCounter = 0
            if (directionCounter == 120) {
                direction = computeDirection(currentGame)
                directionCounter = 0
            }
            // if 3 seconds have not passed yet and the user has not drawn any line, then keep the same direction as before
            directionCounter++
        } else {
            // reset the counter of the empty lines (directionCounter is reset to 120)
            directionCounter = 120
            // change direction if a new line has been added
            // or if the current direction has been followed for 2 seconds
            if (!directionTowardsLine || directionTowardsLineCounter == 120) {
                direction = computeDirection(currentGame)
                directionTowardsLine = true
                directionTowardsLineCounter = 0
            }
            directionTowardsLineCounter++
        }

        // Assuming the object has a speed property
        val speed = 5f // Adjust as needed

        // Calculate the new position based on direction and speed
        return currentGame.position.add(direction.multiply(speed))
    }

    private fun computeDirection(currentGame: GameState): Vector2 {
        var min = max(screenWidth, screenHeight) + 10.0f
        var point = Vector2(0.0f, 0.0f)
        // if the user has not drawn any line, the select a random point on the screen
        if (currentGame.lines.isEmpty()) {
            // nextFloat returns a Float between 0 and 1
            point = Vector2(Random.nextFloat() * screenWidth, Random.nextFloat() * screenHeight)
        }
        // compute the closest point of the line (drawn by the user) to the current position of the object
        currentGame.lines.forEach {
            val point1 = Vector2(it.start.x, it.start.y)
            val point2 = Vector2(it.end.x, it.end.y)
            val closestPoint = closestPointOfLineToPoint(currentGame.position, point1, point2)
            val distance = distance(currentGame.position, closestPoint)
            if (distance < min) {
                min = distance
                point = closestPoint
            }
        }
        // compute the direction towards which the object should move
        // start point - end point -> direction
        // normalization is used to only have the direction, not affected by the distance between the two points
        val normalization = currentGame.position.subtract(point).normalize()
        // negative x (or y) means that the subtraction happened with a number that was greater
        // so the direction should be positive (so that the subsequent addition will increase the position value)
        // on the other hand, positive x (or y) means that the subtraction happened with a number that was smaller
        // so the direction should be negative (so that the subsequent addition will decrease the position value)
        return normalization.multiply(-1f)
    }

    private fun closestPointOfLineToPoint(point: Vector2, linePoint1: Vector2, linePoint2: Vector2) : Vector2 {
        val dx = linePoint2.x - linePoint1.x
        val dy = linePoint2.y - linePoint1.y

        // If both points are the same, return distance between point and one of the points on the line
        if (dx == 0.0f && dy == 0.0f) {
            return linePoint1
        }

        val t = ((point.x - linePoint1.x) * dx + (point.y - linePoint1.y) * dy) / (dx * dx + dy * dy)

        // Check if point falls outside the segment, then calculate distance to the nearest endpoint
        if (t < 0) {
            return linePoint1
        } else if (t > 1) {
            return linePoint2
        }

        // Calculate closest point on the line to the given point
        val closestPointX = linePoint1.x + t * dx
        val closestPointY = linePoint1.y + t * dy

        return Vector2(closestPointX, closestPointY)
    }

    private fun distance(point1: Vector2, point2: Vector2): Float {
        val dx = point1.x - point2.x
        val dy = point1.y - point2.y
        return sqrt((dx * dx + dy * dy).toDouble()).toFloat()
    }

    private fun linesIntersection2D(
        pos1: Offset,
        pos2: Offset,
        nex1: Offset,
        nex2: Offset
    ): Boolean {
        val s1_x = pos2.x - pos1.x
        val s1_y = pos2.y - pos1.y
        val s2_x = nex2.x - nex1.x
        val s2_y = nex2.y - nex1.y
        val s =
            (-s1_y * (pos1.x - nex1.x) + s1_x * (pos1.y - nex1.y)) / (-s2_x * s1_y + s1_x * s2_y)
        val t = (s2_x * (pos1.y - nex1.y) - s2_y * (pos1.x - nex1.x)) / (-s2_x * s1_y + s1_x * s2_y)
        // if one of the 3 conditions is true, then a collision happened
        return s in 0.0..1.0 && t >= 0 && t <= 1
    }

    private fun findFirstCircle(lines: List<Line>): List<Line> {
        var circle = ArrayList<Line>()
        for (i in 0 until lines.size) {
            // extreme points of each line
            val pos1: Offset = lines[i].start
            val pos2: Offset = lines[i].end
            // start from the point subsequent to the next one
            // by doing so, subsequent segments are not considered
            for (j in i + 2 until lines.size) {
                val nex1: Offset = lines[j].start
                val nex2: Offset = lines[j].end
                if (linesIntersection2D(pos1, pos2, nex1, nex2)) {
                    circle = lines.slice(i..j) as ArrayList<Line>
                    return circle
                }
            }
        }
        return circle
    }


    private fun isGameObjectInsideCircle(objectPosition: Offset, circle: List<Line>): Boolean {
        // the north point that is used to intersect lines
        // same x of objectPosition, but y is higher than the screen
        // top-left is (0,0), so a negative y-value means higher
        val northPosition: Offset = objectPosition.copy(y = - 10.0f)
        // check whether the gameobject is inside or outside the circle
        // check how many times a ray casted from the center of the gameobject intersects with the circle in one direction (in this case, only the North is checked)
        // odd number means it is inside
        // even number means it is outside
        // the y coordinate is changed so that it intersects with all the lines of the circle in the up direction
        // it is changed to the max height of all points + 100 (to be sure)
        var counter = 0
        for (j in 0 until circle.size) {
            if (linesIntersection2D(
                    objectPosition,
                    northPosition,
                    circle[j].start,
                    circle[j].end
                )
            ) {
                counter++
            }
        }
        return counter % 2 == 1
    }

    private fun detectCollision(hitbox: MutableList<Vector2>, lines: List<Line>) : Boolean {
        for (i in hitbox.indices) {
            // the point 0 paired with the 1, the 1 with the 2, the 2 with the 3, the 3 with the 0
            val startHit = hitbox[i % hitbox.size].toOffset()
            val endHit = hitbox[(i + 1) % hitbox.size].toOffset()
            for (j in lines.indices) {
                val startLine = Offset(lines[j].start.x, lines[j].start.y)
                val endLine = Offset(lines[j].end.x, lines[j].end.y)

                if (linesIntersection2D(startHit, endHit, startLine, endLine))
                    return true
            }
        }
        return false
    }

    private fun updateHitbox(position: Vector2, item : Bitmap?): MutableList<Vector2> {
        item?.let {
            return mutableListOf(
                // bottom-right
                Vector2(position.x + item.width, position.y + item.height),
                // bottom-left
                Vector2(position.x, position.y + item.height),
                // top-right
                Vector2(position.x + item.width, position.y),
                // top-left
                Vector2(position.x, position.y)
            )
        }
        return mutableListOf()
    }

    override fun onUpdate(data: Location) {
    }

    override fun onUpdate(data: String) {
    }

    override fun onUpdate(data: Message) {
        //
    }

    override fun onCleared() {
        super.onCleared()
        release()
    }

    fun release(){

    }

    fun resume(){

    }


}
