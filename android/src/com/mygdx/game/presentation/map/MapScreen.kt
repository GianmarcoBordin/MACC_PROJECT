package com.mygdx.game.presentation.map

import android.Manifest
import android.content.ContentValues.TAG
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Location
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import androidx.activity.ComponentActivity
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
import androidx.compose.foundation.shape.RoundedCornerShape
//noinspection UsingMaterialAndMaterial3Libraries

import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
//noinspection UsingMaterialAndMaterial3Libraries

import androidx.compose.material.CircularProgressIndicator
//noinspection UsingMaterialAndMaterial3Libraries

import androidx.compose.material.Surface
//noinspection UsingMaterialAndMaterial3Libraries

import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.graphics.scale
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.google.gson.Gson
import com.mygdx.game.Constants
import com.mygdx.game.R
import com.mygdx.game.data.dao.GameItem
import com.mygdx.game.presentation.components.BackButton
import com.mygdx.game.presentation.map.events.LocationDeniedEvent
import macc.ar.presentation.map.events.LocationGrantedEvent
import com.mygdx.game.presentation.map.events.RetryMapEvent
import com.mygdx.game.presentation.map.events.RouteEvent
import com.mygdx.game.presentation.map.events.UpdateMapEvent
import com.mygdx.game.presentation.map.utility.findMarker
import com.mygdx.game.presentation.navgraph.Route
import com.mygdx.game.ui.theme.ArAppTheme
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline


@Composable
fun MapScreen(
    mapRetryHandler: (RetryMapEvent.MapRetry) -> Unit,
    mapUpdateHandler:(UpdateMapEvent.MapUpdate) -> Unit,
    routeHandler: (RouteEvent.Route) -> Unit,
    locationGrantedHandler: (LocationGrantedEvent.LocationGranted) -> Unit,
    locationDeniedHandler:(LocationDeniedEvent.LocationDenied) -> Unit,
    viewModel: MapViewModel,
    navController: NavController
) {
    // observable state
    val isLocGranted by viewModel.isLocGranted.observeAsState()
    //lifecycle
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.resume()
            }
            if (event == Lifecycle.Event.ON_PAUSE) {
                viewModel.release()

            }
            if (event == Lifecycle.Event.ON_DESTROY) {
                viewModel.release()

            }
            if (event == Lifecycle.Event.ON_STOP) {
                viewModel.release()

            }
            if (event == Lifecycle.Event.ON_START) {
                viewModel.resume()

            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
        // if startProcessIndicatorAnimation() then stop

    }
    ArAppTheme {
        Permission(locationGrantedHandler, locationDeniedHandler)
        Surface(color = androidx.compose.material3.MaterialTheme.colorScheme.surface){
            Column(modifier = Modifier.fillMaxSize()) {
                    if (isLocGranted== true) {
                        DefaultMapContent(
                            mapUpdateHandler = mapUpdateHandler,
                            mapRetryHandler =mapRetryHandler,
                            routeHandler= routeHandler,
                            viewModel = viewModel,
                            navController = navController
                        )
                    }else{
                        BackButton(onClick = {  navController.popBackStack()})
                        Text(
                            text = "The app has no permissions to open the map, allow position tracking in settings",
                            color =Color.Red
                        )
                    }
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
    mapRetryHandler: (RetryMapEvent.MapRetry) -> Unit,
    routeHandler: (RouteEvent.Route) -> Unit,
    navController:NavController,
    viewModel: MapViewModel
) {

    // mutable state
    val isLoading by viewModel.isLoading.observeAsState()
    val isError by viewModel.isError.observeAsState()


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

            BackButton(onClick = {  navController.popBackStack()})
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(300.dp))
                CircularProgressIndicator(progress = progress.value, color = Color.Blue)
                Button(
                    shape = RoundedCornerShape(size = 16.dp),
                    onClick = { mapRetryHandler(RetryMapEvent.MapRetry)
                    }) {
                    Text(text = "Retry")

                }

                if (isError==true) {
                    Text(
                        text = "Check your internet connection and retry later",
                        color = MaterialTheme.colorScheme.onError,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else if (isLoading==false){
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    OsmMap(
                        routeHandler = routeHandler,
                        navController,
                        viewModel = viewModel, modifier =  Modifier

                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        BackButton(onClick = { navController.popBackStack() })

                        Button(
                            shape = RoundedCornerShape(size = 16.dp),
                            onClick = {
                                mapUpdateHandler(UpdateMapEvent.MapUpdate)
                            }) {
                            Text(
                                text = "Refresh"
                            )
                        }
                    }
                }
        }
    }
}




@Composable
fun OsmMap(
    routeHandler: (RouteEvent.Route)->Unit,
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
    val thresholdButton = 50
    val thresholdButtonFlag by viewModel.thresholdButtonFlag.observeAsState()
    var pathOverlay : Polyline = Polyline()
    var userMarker : Marker = Marker(MapView(LocalContext.current))




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
                            if (clickedMarker != null && clickedMarker.position.latitude != userLocation?.latitude && clickedMarker.position.longitude != userLocation?.longitude) {
                                // If a marker is clicked, show marker details
                                Toast.makeText(
                                    context,
                                    "Clicked marker: ${clickedMarker.title}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                performClick() // Simulate a click event on the map view
                                // Convert GeoPoints to Locations
                                val to = Location("provider").apply {
                                    latitude = clickedMarker.position.latitude
                                    longitude = clickedMarker.position.longitude
                                }
                                routeHandler(RouteEvent.Route(userLocation, to))
                                true
                            } else {
                                false
                            }
                        }

                        else -> false // Continue processing other touch events
                    }
                }


            }
        }
    ) { mapView ->
        val thresholdKm = 1000 // Set the threshold to 1 kilometer
        // Add user's location marker
        userLocation?.let { location ->
            mapView.overlays.remove(userMarker)
            val userGeoPoint = GeoPoint(location.latitude, location.longitude)
            Log.d("MAP SCREEN", "$userGeoPoint")
            mapView.controller.setCenter(userGeoPoint)
            userMarker = Marker(mapView)
            userMarker.position = userGeoPoint
            userMarker.title = "Your Location"
            mapView.overlays.add(userMarker)
        }

        players?.forEach { player ->
            //println(player)
            val playerGeoPoint = GeoPoint(player.location.latitude, player.location.longitude)
            val playerMarker = Marker(mapView)
            playerMarker.position = playerGeoPoint


            val distanceString = if (player.distance > thresholdKm) {
                "%.0f km".format(player.distance)
            } else {
                "%.2f meters".format(player.distance)
            }
            playerMarker.title =
                "Username: ${player.username} Distance From Me: $distanceString"
            //playerMarker.icon.colorFilter= PorterDuffColorFilter(Color.Blue.toArgb(), PorterDuff.Mode.SRC_ATOP)
            mapView.overlays.add(playerMarker)
        }

        objects?.forEach { obj ->
            val objectGeoPoint = GeoPoint(obj.location.latitude, obj.location.longitude)
            val objectMarker = Marker(mapView)
            objectMarker.position = objectGeoPoint

            Log.d("DEBUG", "${obj.distance} $thresholdKm")
            val distanceString = if (obj.distance > thresholdKm) {
                "%.0f km".format(obj.distance)
            } else {
                "%.2f meters".format(obj.distance)
            }
            if (obj.distance < thresholdButton) {
                viewModel.update(obj, true)
            }
            objectMarker.title =
                "Item id: ${obj.itemId} Item Name: ${obj.itemId} Item Rarity: ${obj.itemRarity} Distance From Me: $distanceString"
            //objectMarker.icon.colorFilter= PorterDuffColorFilter(Color.Green.toArgb(), PorterDuff.Mode.OVERLAY)
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

        if (thresholdButtonFlag?.isNotEmpty() == true) {
            Column(
                modifier = Modifier
                    .fillMaxWidth() // Fill the width of the parent
                    .padding(top = 16.dp), // Optional padding // Center horizontally
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val itemBitMap: ImageBitmap
                val hp: Int
                val damage: Int
                val firstTrueItemKey = thresholdButtonFlag!!.entries.find { it.value }?.key
                when (firstTrueItemKey?.itemRarity) {
                    "1" -> {
                        itemBitMap = ImageBitmap.imageResource(id = R.drawable.gunner_green)
                        // Parse JSON string to Item object
                        hp = com.mygdx.game.util.Constants.RARITY_1_HP
                        damage = com.mygdx.game.util.Constants.RARITY_1_DAMAGE
                    }

                    "2" -> {
                        itemBitMap = ImageBitmap.imageResource(id = R.drawable.gunner_red)
                        hp = com.mygdx.game.util.Constants.RARITY_2_HP
                        damage = com.mygdx.game.util.Constants.RARITY_2_DAMAGE
                    }

                    "3" -> {
                        itemBitMap = ImageBitmap.imageResource(id = R.drawable.gunner_yellow)
                        hp = com.mygdx.game.util.Constants.RARITY_3_HP
                        damage = com.mygdx.game.util.Constants.RARITY_3_DAMAGE
                    }

                    "4" -> {
                        itemBitMap = ImageBitmap.imageResource(id = R.drawable.gunner_blue)
                        hp = com.mygdx.game.util.Constants.RARITY_4_HP
                        damage = com.mygdx.game.util.Constants.RARITY_4_DAMAGE
                    }

                    "5" -> {
                        itemBitMap = ImageBitmap.imageResource(id = R.drawable.gunner_black)
                        hp = com.mygdx.game.util.Constants.RARITY_5_HP
                        damage = com.mygdx.game.util.Constants.RARITY_5_DAMAGE
                    }

                    else -> {
                        itemBitMap = ImageBitmap.imageResource(id = R.drawable.gunner_green)
                        hp = com.mygdx.game.util.Constants.RARITY_1_HP
                        damage = com.mygdx.game.util.Constants.RARITY_1_DAMAGE
                    }

                }

                val configuration = LocalConfiguration.current
                val screenWidth =
                    with(LocalDensity.current) { configuration.screenWidthDp.dp.toPx().toInt() }


                Button(
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.surface), // Set the background color of the button
                    shape = RoundedCornerShape(size = 20.dp),
                    onClick = {

                        // -> bitmap of the item
                        // convert to bitmap
                        val itemBitmap = itemBitMap.asAndroidBitmap()
                        // scale the item so that its width is 1/4 of the screen width,
                        // but the ratio between its dimensions is maintained
                        val itemWidth = screenWidth / 4
                        val itemOriginalWidth = itemBitmap.width
                        val itemRatio = itemWidth.toDouble() / itemOriginalWidth
                        val itemHeight = (itemBitmap.height * itemRatio).toInt()
                        val finalItemBitmap = itemBitmap.scale(itemWidth, itemHeight)
                        val finalGameItem = GameItem(
                            firstTrueItemKey!!.itemId,
                            firstTrueItemKey.itemRarity.toInt(),
                            hp,
                            damage,
                            finalItemBitmap
                        )
                        viewModel.saveGameItem(finalGameItem)
                        navController.navigate(Route.ARScreen.route)
                    },
                    enabled = thresholdButtonFlag!!.any { it.value },

                    ) {
                    Text(
                        text = "Catch ${firstTrueItemKey?.itemId}",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
    }





