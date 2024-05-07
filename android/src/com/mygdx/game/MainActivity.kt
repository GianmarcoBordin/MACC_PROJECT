package com.mygdx.game

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Button

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text

import androidx.compose.ui.Modifier
import com.mygdx.game.domain.manager.LocalUserManager

import com.mygdx.game.ui.theme.ArAppTheme
import com.mygdx.game.presentation.MainViewModel
import com.mygdx.game.presentation.navgraph.NavGraph
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

interface Multiplayer{
    fun onSetMultiplayer()
}

@AndroidEntryPoint
class MainActivity : ComponentActivity(){


    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            val intent = Intent(this, AndroidLauncher::class.java)

            ArAppTheme(
                dynamicColor = false
            ) {

                Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {

                    val startDestination = viewModel.startDestination
                    NavGraph(
                        object: Multiplayer{ override fun onSetMultiplayer() { setMultiplayer() } },
                        startDestination = startDestination
                    )
                    /*
                    Button(onClick = {
                        startActivity(intent)
                    }) {
                        Text(text = "Multiplayer")
                    }
                    */
                }

            }


        }
    }

    fun setMultiplayer(){
        val sharedPreferences = applicationContext.getSharedPreferences("userSettings2", Context.MODE_PRIVATE)

        val intent = Intent(this, AndroidLauncher::class.java)
        // TODO pass data between activity here

        intent.putExtra("key","username")
        startActivity(intent)



    }


}

