package com.mygdx.game.presentation.map

import android.Manifest
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
//noinspection UsingMaterialAndMaterial3Libraries

//noinspection UsingMaterialAndMaterial3Libraries

import androidx.compose.material3.CircularProgressIndicator
//noinspection UsingMaterialAndMaterial3Libraries

import androidx.compose.material3.Surface
//noinspection UsingMaterialAndMaterial3Libraries

import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.graphics.scale
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import com.mygdx.game.R
import com.mygdx.game.data.dao.GameItem
import com.mygdx.game.presentation.components.BackButton
import com.mygdx.game.presentation.components.CustomBackHandler
import com.mygdx.game.presentation.components.ExitPopup
import com.mygdx.game.presentation.map.components.InfoButton
import com.mygdx.game.presentation.map.components.InfoDialog
import com.mygdx.game.presentation.map.components.MyBackButton
import com.mygdx.game.presentation.map.components.ObjectDialog
import com.mygdx.game.presentation.map.components.RefreshButton
import com.mygdx.game.presentation.map.events.LocationDeniedEvent
import macc.ar.presentation.map.events.LocationGrantedEvent
import com.mygdx.game.presentation.map.events.UpdateMapEvent
import com.mygdx.game.presentation.map.utility.MapItemType
import com.mygdx.game.presentation.map.utility.findMarker
import com.mygdx.game.presentation.navgraph.Route
import com.mygdx.game.presentation.scan.events.GameItemEvent
import com.mygdx.game.ui.theme.ArAppTheme
import com.mygdx.game.util.Constants.MARKER_HEIGHT
import com.mygdx.game.util.Constants.MARKER_WIDTH
import com.mygdx.game.util.Constants.OBJECT_MARKER_HEIGHT
import com.mygdx.game.util.Constants.OBJECT_MARKER_WIDTH
import com.mygdx.game.util.deserializeObject
import com.mygdx.game.util.getItemDetails
import com.mygdx.game.util.getItemDrawable
import com.mygdx.game.util.serializeObject


import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

@Composable
fun LockScreenOrientation(orientation: Int) {
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val activity = context.findActivity() ?: return@DisposableEffect onDispose {}
        val originalOrientation = activity.requestedOrientation
        activity.requestedOrientation = orientation
        onDispose {
            // restore original orientation when view disappears
            activity.requestedOrientation = originalOrientation
        }
    }
}

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}


@Composable
fun MapScreen(
    mapUpdateHandler:(UpdateMapEvent.MapUpdate) -> Unit,
    locationGrantedHandler: (LocationGrantedEvent.LocationGranted) -> Unit,
    locationDeniedHandler:(LocationDeniedEvent.LocationDenied) -> Unit,
    saveGameItemHandler: (GameItemEvent.SaveGameItem) -> Unit,
    viewModel: MapViewModel,
    navController: NavController
) {
    LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

    // observable state
    val isLocGranted by viewModel.isLocGranted.observeAsState()
    // lifecycle
    val lifecycleOwner = LocalLifecycleOwner.current

    val openPopup = rememberSaveable { mutableStateOf(false) }

    ManageLifecycle(lifecycleOwner = lifecycleOwner, viewModel = viewModel)
    CustomBackHandler(
        onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher ?: return,
        enabled = true // Set to false to disable back press handling
    ) {
        openPopup.value = true
    }

    ArAppTheme {
        Permission(locationGrantedHandler, locationDeniedHandler)
        Surface(color = MaterialTheme.colorScheme.surface) {
            Column(modifier = Modifier.fillMaxSize()) {
                if (isLocGranted == true) {
                    DefaultMapContent(
                        mapUpdateHandler = mapUpdateHandler,
                        saveGameItemHandler = saveGameItemHandler,
                        viewModel = viewModel,
                        navController = navController
                    )
                } else {
                    BackButton(onClick = { navController.navigate(Route.HomeScreen.route) })
                    Text(
                        text = "The app has no permissions to open the map, allow position tracking in settings",
                        color =Color.Red
                    )
                }
                ExitPopup(openPopup)
            }
        }
    }
}

@Composable
fun Permission(
    locationGrantedHandler: (LocationGrantedEvent.LocationGranted) -> Unit,
    locationDeniedHandler: (LocationDeniedEvent.LocationDenied) -> Unit
) {
    val context = LocalContext.current
    LocalContext.current as ComponentActivity

    // Initialize the permission launcher
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission granted, handle it here
            Log.d(TAG,"Permission Granted")
            locationGrantedHandler(LocationGrantedEvent.LocationGranted)
        } else {
            // Permission denied, handle it here
            Log.d(TAG,"Permission Denied")
            locationDeniedHandler(LocationDeniedEvent.LocationDenied)
        }
    }

    // Check if permission is granted
    val permissionGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PERMISSION_GRANTED

    // Request permission if not granted (OnCreate,OnStart)
    LaunchedEffect(Unit) {

        if (!permissionGranted) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }else{
            locationGrantedHandler(LocationGrantedEvent.LocationGranted)
        }
    }
}

@Composable
fun DefaultMapContent(
    mapUpdateHandler: (UpdateMapEvent.MapUpdate) -> Unit,
    saveGameItemHandler: (GameItemEvent.SaveGameItem) -> Unit,
    navController:NavController,
    viewModel: MapViewModel
) {
    // mutable state
    val isLoading by viewModel.isLoading.observeAsState()
    val isError by viewModel.isError.observeAsState()

    // state variable used to trigger info dialog display
    val openInfoDialog = rememberSaveable {
        mutableStateOf(false)
    }

    Box(
        contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()
    ) {
        if (isLoading == true) {
            val progress = remember { Animatable(0f) }
            LaunchedEffect(Unit) {

                progress.animateTo(
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = 1000),
                        repeatMode = RepeatMode.Reverse
                    )
                )
            }

            BackButton(onClick = { navController.navigate(Route.HomeScreen.route) })
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(300.dp))
                CircularProgressIndicator(
                    progress = { progress.value },
                    color = MaterialTheme.colorScheme.primary,
                )

                if (isError == true) {
                    Text(
                        text = "Check your internet connection and retry later",
                        color = MaterialTheme.colorScheme.onError,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else if (isLoading == false){
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                OsmMap(
                    saveGameItemHandler = saveGameItemHandler,
                    navController =  navController,
                    viewModel = viewModel,
                    modifier =  Modifier
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MyBackButton(onClick = {
                        navController.navigate(Route.HomeScreen.route)
                    })
                    RefreshButton(onClick = {
                        mapUpdateHandler(UpdateMapEvent.MapUpdate)
                    })
                    InfoButton(onClick = {
                        openInfoDialog.value = true
                    })
                    if (openInfoDialog.value) {
                        InfoDialog(onDismissRequest = {openInfoDialog.value = false})
                    }
                }
            }
        }
    }
}

@Composable
fun OsmMap(
    saveGameItemHandler: (GameItemEvent.SaveGameItem) -> Unit,
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: MapViewModel
) {
    // OnStart OnCreate
    LaunchedEffect(Unit) {
        val userAgent = "ArApp/1.0 (Android)"
        // Set a relevant user agent for osmdroid
        Configuration.getInstance().userAgentValue = userAgent

    }
    // user agent

    // observable state
    val userLocation by viewModel.userLocation.observeAsState()
    val players by viewModel.players.observeAsState()
    val objects by viewModel.objects.observeAsState()
    val navPath by viewModel.navPath.observeAsState()

    val username by viewModel.username.observeAsState()

    // observe state to open dialog of clicked object
    val openObjectDialog = rememberSaveable {
        mutableStateOf(false)
    }
    val objectContent = rememberSaveable {
        mutableStateOf("")
    }

    var pathOverlay = Polyline()
    var userMarker = Marker(MapView(LocalContext.current))
    val playersMarker: MutableList<Marker> = mutableListOf()

    val playerMarkerIcon = ImageBitmap.imageResource(R.drawable.main_player_location)
    val otherPlayerLocationIcon = ImageBitmap.imageResource(R.drawable.other_player_location)

    val greenGunner = ImageBitmap.imageResource(R.drawable.gunner_green)
    val redGunner = ImageBitmap.imageResource(R.drawable.gunner_red)
    val yellowGunner = ImageBitmap.imageResource(R.drawable.gunner_yellow)
    val blueGunner = ImageBitmap.imageResource(R.drawable.gunner_blue)
    val blackGunner = ImageBitmap.imageResource(R.drawable.gunner_black)

    // Initialize OSM MapView
    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { context ->
            MapView(context).apply {
                // Configure the map settings

                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)

                // Set the minimum and maximum zoom levels for the map view
                minZoomLevel = 10.0
                //maxZoomLevel = 18.0
                controller.setZoom(18.0)

                // Register a touch event listener
                setOnTouchListener { _, event ->
                    when (event.action) {

                        MotionEvent.ACTION_DOWN -> {
                            val clickedMarker = findMarker(event.x, event.y)

                            if (clickedMarker != null){
                                when (clickedMarker.title) {

                                    MapItemType.ME.toString(), MapItemType.OTHER.toString() -> {
                                        Toast.makeText(
                                            context,
                                            clickedMarker.subDescription,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        performClick() // Simulate a click event on the map view

                                    }

                                    MapItemType.OBJECT.toString() -> {
                                        openObjectDialog.value = true
                                        objectContent.value = clickedMarker.subDescription

                                        // Convert GeoPoints to Locations
                                        Location("provider").apply {
                                            latitude = clickedMarker.position.latitude
                                            longitude = clickedMarker.position.longitude
                                        }

                                    }

                                }


                            }
                            true
                        }
                        else -> false // Continue processing other touch events
                    }
                }
            }
        }
    ) { mapView ->
        val thresholdKm = 1000 // Set the threshold to 1 kilometer

        // add current user
        userLocation?.let { location ->
            mapView.overlays.remove(userMarker)
            mapView.invalidate()
            val userGeoPoint = GeoPoint(location.latitude, location.longitude)
            mapView.controller.setCenter(userGeoPoint)

            userMarker = Marker(mapView)

            userMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

            userMarker.position = userGeoPoint
            userMarker.title = MapItemType.ME.toString()
            userMarker.subDescription = "Your Location"
            userMarker.icon = scaleBitmap(
                mapView.context.resources,
                playerMarkerIcon,
                MARKER_WIDTH,
                MARKER_HEIGHT
            )

            mapView.overlays.add(userMarker)
        }

        // Remove other players marker
        playersMarker.forEach {
            mapView.overlays.remove(it)

        }
        mapView.invalidate()
        playersMarker.clear()

        // add other user location
        players?.forEach { player ->

            if (player.distance > 0.0 && player.username != username) {
                //Log.d("DEBUG","$player")
                val playerGeoPoint = GeoPoint(player.location.latitude, player.location.longitude)
                val playerMarker = Marker(mapView)
                playerMarker.position = playerGeoPoint

                val distanceString = if (player.distance > thresholdKm) {
                    "%.0f km".format(player.distance / 1000)
                } else {
                    "%.2f meters".format(player.distance)
                }

                playerMarker.title = MapItemType.OTHER.toString()

                playerMarker.subDescription = "${player.username} is $distanceString far from you!"
                playerMarker.icon = scaleBitmap(
                    mapView.context.resources,
                    otherPlayerLocationIcon,
                    MARKER_WIDTH,
                    MARKER_HEIGHT
                )

                mapView.overlays.add(playerMarker)
                playersMarker.add(playerMarker)
            }
        }

        // add objects
        objects?.forEach { obj ->
            val objectGeoPoint = GeoPoint(obj.location.latitude, obj.location.longitude)
            val objectMarker = Marker(mapView)
            objectMarker.position = objectGeoPoint

            val distanceString = if (obj.distance > thresholdKm) {
                "%.0f km".format(obj.distance / 1000)
            } else {
                "%.2f meters".format(obj.distance)
            }

            val properties = listOf(
                "itemId" to obj.itemId.toString(),
                "itemRarity" to obj.itemRarity.toString(),
                "distanceFromMe" to distanceString
            )

            objectMarker.title = MapItemType.OBJECT.toString()
            objectMarker.subDescription = serializeObject(properties)

            val objectMarkerIcon: ImageBitmap = when (obj.itemRarity) {
                1 -> greenGunner
                2 -> redGunner
                3 -> yellowGunner
                4 -> blueGunner
                5 -> blackGunner
                else -> greenGunner
            }

            objectMarker.icon = scaleBitmap(
                mapView.context.resources,
                objectMarkerIcon,
                OBJECT_MARKER_WIDTH,
                OBJECT_MARKER_HEIGHT
            )
            mapView.overlays.add(objectMarker)
        }

        // Show navigation path
        navPath?.let {
            val boundingBox = BoundingBox.fromGeoPoints(navPath)
            // Set the center and zoom level of the MapView based on the bounding box
            mapView.overlays.remove(pathOverlay)
            mapView.zoomToBoundingBox(boundingBox, true)
            pathOverlay = Polyline()
            pathOverlay.setPoints(navPath)
            mapView.overlays.add(pathOverlay)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth() // Fill the width of the parent
            .padding(top = 16.dp), // Optional padding // Center horizontally
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if (openObjectDialog.value){
            val jsonObject = deserializeObject(objectContent.value)

            val itemId = jsonObject.getAsJsonPrimitive("itemId").asInt
            val itemRarity = jsonObject.getAsJsonPrimitive("itemRarity").asInt
            val distanceFromMe = jsonObject.getAsJsonPrimitive("distanceFromMe").asString

            objects?.forEach { obj ->

                if (obj.itemId == itemId) {
                    val (color, hp, damage) = getItemDetails(obj.itemRarity)
                    val itemBitMap: ImageBitmap = ImageBitmap.imageResource(color)

                    val configuration = LocalConfiguration.current
                    val screenWidth =
                        with(LocalDensity.current) { configuration.screenWidthDp.dp.toPx().toInt() }

                    val imageId = getItemDrawable(itemRarity)

                    ObjectDialog(
                        imageId = imageId,
                        distanceFromMe = distanceFromMe,
                        enabled = true,
                        onCatchObject = {
                            val itemBitmap = itemBitMap.asAndroidBitmap()
                            // scale the item so that its width is 1/4 of the screen width,
                            // but the ratio between its dimensions is maintained
                            val itemWidth = screenWidth / 4
                            val itemOriginalWidth = itemBitmap.width
                            val itemRatio = itemWidth.toDouble() / itemOriginalWidth
                            val itemHeight = (itemBitmap.height * itemRatio).toInt()
                            val finalItemBitmap = itemBitmap.scale(itemWidth, itemHeight)
                            val finalGameItem = GameItem(
                                owner = "",
                                itemId = obj.itemId,
                                rarity = obj.itemRarity,
                                hp = hp,
                                damage = damage,
                                bitmap = finalItemBitmap
                            )
                            saveGameItemHandler(GameItemEvent.SaveGameItem(finalGameItem, obj.itemId))
                            navController.navigate(Route.ARScreen.route)
                        },
                        onDismissRequest = { openObjectDialog.value = false }
                    )

                    return
                }

            }

        }


    }
}

fun scaleBitmap(resources: Resources,bitmap: ImageBitmap, newWidth: Int, newHeight: Int): BitmapDrawable {
    val scaledBitmap = Bitmap.createScaledBitmap(
        bitmap.asAndroidBitmap(),
        newWidth,
        newHeight,
        true
    )
    return BitmapDrawable(resources, scaledBitmap)
}

@Composable
private fun ManageLifecycle(lifecycleOwner: LifecycleOwner, viewModel: MapViewModel) {
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME, Lifecycle.Event.ON_START, Lifecycle.Event.ON_CREATE -> viewModel.resume()
                Lifecycle.Event.ON_PAUSE, Lifecycle.Event.ON_STOP, Lifecycle.Event.ON_DESTROY -> viewModel.release()
                else -> { }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}






