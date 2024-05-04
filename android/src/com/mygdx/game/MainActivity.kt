package com.mygdx.game

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box

import androidx.compose.material3.MaterialTheme

import androidx.compose.ui.Modifier

import com.mygdx.game.ui.theme.ArAppTheme
import com.mygdx.game.presentation.MainViewModel
import com.mygdx.game.presentation.navgraph.NavGraph
import dagger.hilt.android.AndroidEntryPoint

interface Multiplayer{
    fun onSetMultiplayer()
}

@AndroidEntryPoint
class MainActivity : ComponentActivity(){


    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {


            ArAppTheme(
                dynamicColor = false
            ) {

                Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                    val startDestination = viewModel.startDestination
                    NavGraph(
                        object: Multiplayer{ override fun onSetMultiplayer() { setMultiplayer() } },startDestination = startDestination)

                }

            }


        }
    }

    fun setMultiplayer(){
        val intent = Intent(this, AndroidLauncher::class.java)
        startActivity(intent)
    }


}

