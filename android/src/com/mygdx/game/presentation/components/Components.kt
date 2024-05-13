package com.mygdx.game.presentation.components

//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CustomBackHandler(
    onBackPressedDispatcher: OnBackPressedDispatcher,
    enabled: Boolean = true,
    onBackPressed: () -> Unit
) {
    val backCallback = remember {
        object : OnBackPressedCallback(enabled) {
            override fun handleOnBackPressed() {
                onBackPressed()
            }
        }
    }

    androidx.activity.compose.LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher?.addCallback(backCallback)

    DisposableEffect(onBackPressedDispatcher) {
        backCallback.isEnabled = enabled
        onDispose {
            backCallback.remove()
        }
    }
}

@Composable
fun BackButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .background(
                color = androidx.compose.material3.MaterialTheme.colorScheme.surface,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp)
            )
    ){
        IconButton(
            onClick = onClick,
            modifier = Modifier.padding(8.dp).background(color=androidx.compose.material3.MaterialTheme.colorScheme.surface)
        ) {
            BackIcon()
        }
    }
}

@Composable
fun BackIcon() {
    Icon(
        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
        contentDescription = "Back",
        tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurface, // Change the color as needed
        modifier = Modifier.size(24.dp)
    )
}





