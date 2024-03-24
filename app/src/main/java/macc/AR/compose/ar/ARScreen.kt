package macc.AR.compose.ar

import android.Manifest
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.android.filament.Engine
import com.google.ar.core.Anchor
import com.google.ar.core.Config
import com.google.ar.core.Frame
import com.google.ar.core.Plane
import com.google.ar.core.Session
import com.google.ar.core.TrackingFailureReason
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.arcore.createAnchorOrNull
import io.github.sceneview.ar.arcore.getUpdatedPlanes
import io.github.sceneview.ar.arcore.isValid
import io.github.sceneview.ar.getDescription
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.ar.node.CloudAnchorNode
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
import io.github.sceneview.rememberOnGestureListener
import io.github.sceneview.rememberView
import macc.signinup.R

// TODO this whole file is a copy-paste, so it must be adjusted for the purpose

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ARScreen() {
    // Camera permission state
    val cameraPermissionState = rememberPermissionState(
        Manifest.permission.CAMERA
    )

    if (cameraPermissionState.status.isGranted) {

        Screen()

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
            Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                Text("Request permission")
            }
        }
    }
}

@Composable
fun Screen() {
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

        var planeRenderer by remember { mutableStateOf(true) }

        val modelInstances = remember { mutableListOf<ModelInstance>() }
        var trackingFailureReason by remember {
            mutableStateOf<TrackingFailureReason?>(null)
        }
        var frame by remember { mutableStateOf<Frame?>(null) }
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

                if (childNodes.isEmpty()) {
                    updatedFrame.getUpdatedPlanes()
                        // if the first element is a Plane of type "Plane.Type.HORIZONTAL_UPWARD_FACING"
                        // (if the detected plane is a floor or a table)
                        .firstOrNull { it.type == Plane.Type.HORIZONTAL_UPWARD_FACING }
                        // then create an Anchor that has centerPose as coordinates and is attached to this Plane
                        // and pass it to create an Anchor Node
                        ?.let { it.createAnchorOrNull(it.centerPose) }?.let { anchor ->
                            /*
                            childNodes += createAnchorNode(
                                engine = engine,
                                modelLoader = modelLoader,
                                materialLoader = materialLoader,
                                modelInstances = modelInstances,
                                anchor = anchor,
                            )*/

                            childNodes += createCloudAnchorNode(
                                engine = engine,
                                anchor = anchor,
                                anchorID = null,
                                modelLoader = modelLoader,
                                materialLoader = materialLoader,
                                modelInstances = modelInstances,
                                session = session
                            )
                        }
                }

            },
            onGestureListener = rememberOnGestureListener(

                onSingleTapConfirmed = { motionEvent, node ->
                    if (node == null) {
                        // performs a ray cast from the user's device in the direction of the given location in the camera view
                        // hitResults is List of HitResult, intersection between a way and estimated real world geometry
                        val hitResults = frame?.hitTest(motionEvent.x, motionEvent.y)
                        hitResults?.firstOrNull {
                            it.isValid(
                                depthPoint = false,
                                point = false
                            )
                        }?.createAnchorOrNull()
                            ?.let { anchor ->
                                planeRenderer = false

                                /*
                                childNodes += createCloudAnchorNode(
                                    engine = engine,
                                    anchor = anchor,
                                    anchorID = null,
                                    modelLoader = modelLoader,
                                    materialLoader = materialLoader,
                                    modelInstances = modelInstances,

                                )*/

                                    /*
                                childNodes += createAnchorNode(
                                    engine = engine,
                                    modelLoader = modelLoader,
                                    materialLoader = materialLoader,
                                    modelInstances = modelInstances,
                                    anchor = anchor
                                )*/
                            }
                    }
                },
                onDoubleTapEvent = { motionEvent, node ->
                    if (node == null) {
                        childNodes.clear()
                    }
                }
            )
        )
        Text(
            modifier = Modifier
                .systemBarsPadding()
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(top = 16.dp, start = 32.dp, end = 32.dp),
            textAlign = TextAlign.Center,
            fontSize = 28.sp,
            color = Color.White,
            text = trackingFailureReason?.let {
                it.getDescription(LocalContext.current)
            } ?: if (childNodes.isEmpty()) {
                stringResource(R.string.point_your_phone_down)
            } else {
                stringResource(R.string.tap_anywhere_to_add_model)
            }
        )
        // TODO here insert a button to resolve the cloud anchors
    }
}

// TODO create cloud anchor node
fun createCloudAnchorNode(
    engine: Engine,
    anchor: Anchor,
    anchorID : String? = null,
    modelLoader: ModelLoader,
    materialLoader: MaterialLoader,
    modelInstances: MutableList<ModelInstance>,
    session: Session
) : AnchorNode{


    val kModelFile = "models/dragon_coin.glb"
    val kMaxModelInstances = 3


    val cloudAnchorNode = CloudAnchorNode(
        engine = engine,
        anchor = anchor,
        cloudAnchorId = anchorID,
        onHosted = { cloudAnchorId, state ->
            if (cloudAnchorId != null) {
                Log.d("ANCHORS", "cloud anchor hosted with id: $cloudAnchorId")
            }
            else {
                Log.d("ANCHORS", "cloud anchor is null")
            }
        },
    )

    // host the cloud anchor
    cloudAnchorNode.host(
        session = session,
        ttlDays = 1,
        onCompleted = {
            cloudAnchorId, state -> Log.d("ANCHORS", state.isError.toString())
        }
    )


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
    cloudAnchorNode.addChildNode(modelNode)


    listOf(modelNode, cloudAnchorNode).forEach {
        it.onEditingChanged = { editingTransforms ->
            // if editingTransforms is not empty, then the Cube Node is visible
            // otherwise, it is hidden
            boundingBoxNode.isVisible = editingTransforms.isNotEmpty()
        }
    }


    return cloudAnchorNode

}

fun createAnchorNode(
    engine: Engine,
    modelLoader: ModelLoader,
    materialLoader: MaterialLoader,
    modelInstances: MutableList<ModelInstance>,
    anchor: Anchor
): AnchorNode {
    val kModelFile = "models/dragon_coin.glb"
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