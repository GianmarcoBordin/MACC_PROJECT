package com.mygdx.game.presentation.scan

import android.Manifest
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.opengl.Matrix
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.android.filament.Engine
import com.google.ar.core.Anchor
import com.google.ar.core.Config
import com.google.ar.core.Frame
import com.google.ar.core.Plane
import com.google.ar.core.TrackingFailureReason
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.arcore.createAnchorOrNull
import io.github.sceneview.ar.arcore.getUpdatedPlanes
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.ar.rememberARCameraNode
import io.github.sceneview.loaders.MaterialLoader
import io.github.sceneview.loaders.ModelLoader
import io.github.sceneview.model.ModelInstance
import io.github.sceneview.node.CubeNode
import io.github.sceneview.node.ModelNode
import io.github.sceneview.rememberCollisionSystem
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberMaterialLoader
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNodes
import io.github.sceneview.rememberView
import com.mygdx.game.presentation.navgraph.Route
import com.mygdx.game.presentation.scan.events.DimensionsEvent
import com.mygdx.game.presentation.scan.events.FocusEvent
import com.mygdx.game.presentation.scan.events.VisibilityEvent
import java.io.ByteArrayOutputStream
import com.mygdx.game.presentation.components.BackButton
import com.mygdx.game.presentation.scan.events.BitmapEvent

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ARScreen(focusHandler: (FocusEvent.Focus) -> Unit, visibilityHandler: (VisibilityEvent.Visible) -> Unit,
             dimensionsHandler: (DimensionsEvent.Dimensions) -> Unit, bitmapHandler: (BitmapEvent.BitmapCreated) -> Unit,
             viewModel: ARViewModel, navController: NavController) {
    // Camera permission state
    var permissionRequested by remember { mutableStateOf(false) }
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    LaunchedEffect(Unit) {
        if (!permissionRequested) {
            cameraPermissionState.launchPermissionRequest()
            permissionRequested = true
        }
    }

    if (cameraPermissionState.status.isGranted) {
        // If camera permission is granted, show the screen
        Screen(focusHandler, visibilityHandler, dimensionsHandler, bitmapHandler, viewModel, navController)

    } else {
        Column {
            val textToShow = if (cameraPermissionState.status.shouldShowRationale) {
                // If the user has denied the permission but the rationale can be shown,
                // then gently explain why the app requires this permission
                "The camera is important for this app. Please grant the permission."
            } else {
                // If it's the first time the user lands on this feature, or the user
                // doesn't want to be asked again for this permission, explain that the
                // permission is required
                "Camera permission required for this feature to be available. " +
                        "Please grant the permission"
            }
            Text(textToShow)
            BackButton(onClick = {  navController.popBackStack()})
            Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                Text("Request permission")
            }
        }
    }

    Lifecycle(viewModel = viewModel) // TODO

}

@Composable
fun Screen(focusHandler: (FocusEvent.Focus) -> Unit, visibilityHandler: (VisibilityEvent.Visible) -> Unit,
           dimensionsHandler: (DimensionsEvent.Dimensions) -> Unit, bitmapHandler: (BitmapEvent.BitmapCreated) -> Unit,
           viewModel: ARViewModel, navController: NavController) {
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        // The destroy calls are automatically made when their disposable effect leaves
        // the composition or its key changes.
        val engine = rememberEngine()
        val modelLoader = rememberModelLoader(engine)
        val materialLoader = rememberMaterialLoader(engine)
        val cameraNode = rememberARCameraNode(engine)
        val childNodes = rememberNodes()
        val view = rememberView(engine)
        val collisionSystem = rememberCollisionSystem(view)

        val planeRenderer by remember { mutableStateOf(true) }
        var isVisible by remember { mutableStateOf(false) }
        // variable used to update the circular progress bar
        var counter by remember { mutableStateOf(0f) }

        val modelInstances = remember { mutableListOf<ModelInstance>() }
        var trackingFailureReason by remember {
            mutableStateOf<TrackingFailureReason?>(null)
        }
        var frame by remember { mutableStateOf<Frame?>(null) }

        val configuration = LocalConfiguration.current
        val screenHeight = with(LocalDensity.current) { configuration.screenHeightDp.dp.toPx().toInt() }
        val screenWidth = with(LocalDensity.current) { configuration.screenWidthDp.dp.toPx().toInt() }
        dimensionsHandler(DimensionsEvent.Dimensions(screenWidth, screenHeight))
        BackButton(onClick = {  navController.popBackStack()})
        ARScene(
            modifier = Modifier.fillMaxSize(),
            childNodes = childNodes,
            engine = engine,
            view = view,
            modelLoader = modelLoader,
            collisionSystem = collisionSystem,
            sessionConfiguration = { session, config ->
                config.depthMode =
                    when (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
                        true -> Config.DepthMode.AUTOMATIC
                        else -> Config.DepthMode.DISABLED
                    }
                config.instantPlacementMode = Config.InstantPlacementMode.LOCAL_Y_UP
                config.lightEstimationMode =
                    Config.LightEstimationMode.ENVIRONMENTAL_HDR
                // configure cloud anchor mode
                config.cloudAnchorMode = Config.CloudAnchorMode.ENABLED


            },
            cameraNode = cameraNode,
            planeRenderer = planeRenderer,
            onTrackingFailureChanged = {
                trackingFailureReason = it
            },
            // session is updated each time a new frame is received (if the camera records at 60fps, then session is updated 60 times per second)
            onSessionUpdated = { session, updatedFrame ->
                frame = updatedFrame

                // used to populate the scene with exactly one new item if there is no item on the scene
                // and no item has already been captured
                if (childNodes.isEmpty() && !viewModel.scanned) {
                    updatedFrame.getUpdatedPlanes()
                        // if the first element is a Plane of type "Plane.Type.HORIZONTAL_UPWARD_FACING"
                        // (if the detected plane is a floor or a table)
                        .firstOrNull { it.type == Plane.Type.HORIZONTAL_UPWARD_FACING }
                        // then create an Anchor that has centerPose as coordinates and is attached to this Plane
                        // and pass it to create an Anchor Node
                        ?.let { it.createAnchorOrNull(it.centerPose) }?.let { anchor ->
                            childNodes += createAnchorNode(
                                engine = engine,
                                modelLoader = modelLoader,
                                materialLoader = materialLoader,
                                modelInstances = modelInstances,
                                anchor = anchor,
                            )
                        }
                }
                // used to track if the user captures the object
                else if (!viewModel.scanned){
                    val node = childNodes[0]
                    // get pose (both translation and rotation of the object)
                    val pos = floatArrayOf(node.worldPosition.x, node.worldPosition.y, node.worldPosition.z, 1.0f)
                    val projectionMatrix = FloatArray(16)
                    val viewMatrix = FloatArray(16)
                    // near clip plane is the nearest point of the Camera's view frustum
                    // far clip plane is the furthest point of the Camera's view frustum
                    // used because the floating point numbers are very dense between 0.0 and 1.0, but not everywhere else
                    // by setting them, you can specify where you want the density of floating point to be concentrated
                    frame!!.camera.getProjectionMatrix(projectionMatrix, 0, 0.1f, 100f)
                    frame!!.camera.getViewMatrix(viewMatrix, 0)

                    // compute 2d coordinates of the point
                    val screenPoint = projectPointToScreen(pos, viewModel.screenWidth, viewModel.screenHeight, projectionMatrix, viewMatrix)
                    // check if the item is behind the camera
                    // if it is, then reset the counter
                    if (screenPoint != null) {
                        // if it is not behind the camera, then check if it is inside the screen
                        isVisible = screenPoint.first > 0 && screenPoint.second > 0 && screenPoint.first < screenWidth && screenPoint.second < screenHeight
                        if (isVisible) {
                            visibilityHandler(VisibilityEvent.Visible(true))
                        } else {
                            visibilityHandler(VisibilityEvent.Visible(false))
                        }
                    } else {
                        visibilityHandler(VisibilityEvent.Visible(false))
                    }
                    counter = viewModel.counter.toFloat() / 300f

                    // if the user keeps the object visible for more than x seconds, then it is captured
                    if (viewModel.counter > 300 && !viewModel.scanned) {
                        childNodes.clear()
                        val bitmap = captureFrame(frame!!)
                        bitmapHandler(BitmapEvent.BitmapCreated(bitmap))
                        focusHandler(FocusEvent.Focus(true))
                        visibilityHandler(VisibilityEvent.Visible(false))
                        navController.navigate(Route.CaptureScreen.route)
                    }
                }

            }
        )
        Text(
            modifier = Modifier
                .systemBarsPadding()
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(top = 16.dp, start = 32.dp, end = 32.dp),
            textAlign = TextAlign.Center,
            fontSize = 28.sp,
            color = MaterialTheme.colorScheme.onSurface,
            text = if (isVisible) "Scanning the object..."
            else if (childNodes.isEmpty()) "Point your phone down at an empty space, and move it around slowly"
            else "Look for the object and stay still during scanning"
        )
        CircularProgressIndicator(
            progress = { counter },
            modifier = Modifier
                .size(
                    width = (viewModel.screenWidth / 10).dp,
                    height = (viewModel.screenWidth / 10).dp
                )
                .align(Alignment.TopCenter)
                .padding(top = 50.dp, start = 32.dp, end = 32.dp),
            )
    }
}

@Composable
fun Lifecycle(viewModel: ARViewModel){
    //lifecycle
    val lifecycleOwner = LocalLifecycleOwner.current
    val isActive = remember { mutableStateOf(false) }


    LaunchedEffect(Unit) {
        isActive.value=true
    }
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                isActive.value=true

                if (isActive.value) {
                    viewModel.resume()
                    // Perform operations when the activity is in the foreground
                    // For example:
                    //startLocationUpdates()
                    //startAnimations()
                    // Any other operations you want to resume
                }
            }
            if (event == Lifecycle.Event.ON_PAUSE) {
                isActive.value=false

                if (!isActive.value) {
                    viewModel.release()
                    // Perform operations when the activity goes into the background
                    // For example:
                    // release resources bitmap.recycle()
                    // release vars mutableState.clear()
                    // release coroutines launched on this scope coroutineScope.cancel()
                    // release registered listeners sensorManager.unregisterListener(sensorEventListener)
                    // release some resources of view model or other that I have the ref viewmodel.clear
                    //stopLocationUpdates()
                    //stopAnimations()
                    // Any other operations you want to pause or stop
                }
            }
            if (event == Lifecycle.Event.ON_DESTROY) {
                isActive.value=false
                viewModel.release()
                // Perform cleanup operations when the activity is being destroyed
                // For example:
                //releaseResources()
                //releaseResources()
                //unregisterListeners()
                //cancelOngoingProcesses()
                //cleanupUIComponents()
                //persistDataIfNecessary()
                // Any other cleanup operations
            }
            if (event == Lifecycle.Event.ON_STOP) {
                isActive.value=false

                if (!isActive.value) {
                    viewModel.release()
                    // Perform operations when the activity stops and is no longer visible to the user
                    // release resources bitmap.recycle()
                    // release vars mutableState.clear()
                    // release coroutines launched on this scope coroutineScope.cancel()
                    // release registered listeners sensorManager.unregisterListener(sensorEventListener)
                    // release some resources of view model or other that I have the ref viewmodel.clear
                    // For example:
                    //pauseLocationUpdates()
                    //pauseAnimations()
                    // Any other operations you want to pause
                }
            }
            if (event == Lifecycle.Event.ON_START) {
                isActive.value=true
                if (isActive.value) {
                    viewModel.resume()

                    // Perform operations when the activity starts and is visible to the user
                    // For example:
                    //resumeLocationUpdates()
                    //resumeAnimations()
                    // Any other operations you want to resume
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
        // if startProcessIndicatorAnimation() then stop

    }
}

fun createAnchorNode(
    engine: Engine,
    modelLoader: ModelLoader,
    materialLoader: MaterialLoader,
    modelInstances: MutableList<ModelInstance>,
    anchor: Anchor
): AnchorNode {
    val kModelFile = "models/gunner_green.glb"
    val kMaxModelInstances = 3

    val anchorNode = AnchorNode(engine = engine, anchor = anchor)
    val modelNode = ModelNode(
        modelInstance = modelInstances.apply {
            // if the modelInstances list is empty
            if (isEmpty()) {
                // then create a number of new instances of the model from kModelFile (a path to the object)
                // that is equal to kMaxModelInstances
                // all of them share the same resources of the first asset
                this += modelLoader.createInstancedModel(kModelFile, kMaxModelInstances)
            }
        }.removeLast(),
        // Scale to fit in a 0.5 meters cube
        scaleToUnits = 0.5f
    ).apply {
        // Model Node needs to be editable for independent rotation from the anchor rotation
        isEditable = true
    }
    // create a cube node (presumably the hitbox of the object)
    val boundingBoxNode = CubeNode(
        engine,
        // that has the size equal to the size of the model node
        size = modelNode.extents,
        // and the same center
        center = modelNode.center,
        materialInstance = materialLoader.createColorInstance(Color.White.copy(alpha = 0.5f))
    ).apply {
        isVisible = false
    }
    modelNode.addChildNode(boundingBoxNode)
    anchorNode.addChildNode(modelNode)


    listOf(modelNode, anchorNode).forEach {
        it.onEditingChanged = { editingTransforms ->
            // if editingTransforms is not empty, then the Cube Node is visible
            // otherwise, it is hidden
            boundingBoxNode.isVisible = editingTransforms.isNotEmpty()
        }
    }
    return anchorNode
}

fun projectPointToScreen(
    worldPoint: FloatArray,
    screenWidth: Int,
    screenHeight: Int,
    projectionMatrix: FloatArray,
    viewMatrix: FloatArray
): Pair<Float, Float>? {
    // Apply the view and projection matrices to the world point
    val viewProjectionMatrix = FloatArray(16)
    Matrix.multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
    // - view matrix is used to convert from World Space to Camera Space
    //       exact location of object -> location as it appears to the camera
    // - projection matrix is used to convert from Camera Space to Screen Space
    //       location as it appears to the camera -> location on image plane
    val point = FloatArray(4)
    Matrix.multiplyMV(point, 0, viewProjectionMatrix, 0, worldPoint, 0)

    // Check if the point is behind the camera (if w is negative)
    if (point[3] < 0) {
        // Point is behind the camera, return null
        return null
    }

    // Normalize the point's coordinates
    val normalizedX = point[0] / point[3]
    val normalizedY = point[1] / point[3]

    // Convert normalized coordinates to screen coordinates
    val screenX = (normalizedX + 1) * screenWidth / 2
    val screenY = (1 - normalizedY) * screenHeight / 2

    return Pair(screenX, screenY)
}

private fun captureFrame(frame: Frame): Bitmap {
    // Convert the frame's byte buffer to a bitmap
    val byteArrayOutputStream = ByteArrayOutputStream()
    // this image is taken as YUV_420_888
    frame.acquireCameraImage().use { image ->
        val width = image.width
        val height = image.height
        // bytearray 200% bigger than frame size because it will contain YUV image
        // each pixel requires 1 byte for the Y component
        // each pixel requires half the bytes required for a single component of RGB for U
        // each pixel requires half the bytes required for a single component of RGB for V
        // indeed, half the number of bytes for U (50%) + half the number of bytes for V (50%)
        val yuvBytes = ByteArray(image.width * image.height * 2)
        var offset = 0
        // 3 planes: Y,U,V
        image.planes.forEach { plane ->
            // get the buffer of the plane
            val buffer = plane.buffer
            // returns the number of elements between the current position and the limit
            val remaining = buffer.remaining()
            // transfer all bytes from buffer into yuvBytes at offset position
            buffer.get(yuvBytes, offset, remaining)
            // a +1 is used so that the 3 planes are correctly separated
            offset += remaining + 1
        }
        // create a yuvImage from the bytes retrieved before (with the same dimensions of the frame)
        // YuvImage used because contains a method to compress it efficiently in a JPEG
        // YUV similar to RGB, but more efficient
        // this image is YUV_NV21 (one of the few supported to be compressed in a JPEG)
        val yuvImage = YuvImage(yuvBytes, ImageFormat.NV21, width, height, null)
        // byteArrayOutputStream is used as output
        yuvImage.compressToJpeg(Rect(0, 0, width, height), 50, byteArrayOutputStream)
    }
    val byteArray = byteArrayOutputStream.toByteArray()
    return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
}

