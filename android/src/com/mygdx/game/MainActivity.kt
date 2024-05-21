package com.mygdx.game

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box

import androidx.compose.material3.MaterialTheme

import androidx.compose.ui.Modifier
import com.mygdx.game.data.dao.GameItem

import com.mygdx.game.dto.CharacterType

import com.mygdx.game.ui.theme.ArAppTheme
import com.mygdx.game.presentation.MainViewModel
import com.mygdx.game.presentation.navgraph.NavGraph

import com.mygdx.game.util.Constants.PLAYER_LIST
import com.mygdx.game.util.Constants.USER
import com.mygdx.game.util.Constants.USERNAME

import com.mygdx.game.util.Constants.USER_SETTINGS2
import com.mygdx.game.util.fromIntegerToSkin
import com.mygdx.game.util.removeCharactersAfterAt
import dagger.hilt.android.AndroidEntryPoint

import org.json.JSONObject
import java.io.Serializable

interface Multiplayer{
    fun onSetMultiplayer(gameItems: List<GameItem>)
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
                        object: Multiplayer{ override fun onSetMultiplayer(gameItems: List<GameItem>) { setMultiplayer(gameItems) } },
                        startDestination = startDestination
                    )
                }
            }
        }
    }

    fun setMultiplayer(gameItems: List<GameItem>){

        val sharedPreferences = applicationContext.getSharedPreferences(USER_SETTINGS2, Context.MODE_PRIVATE)

        Log.d("DEBUG", "$gameItems <---")

        // read username from shared preferences and pass it to the libgdx game
        val userName = sharedPreferences.getString(USERNAME,"")?.replace("\\\"", "")?.replace("\"","") ?: ""
        val userEmail = sharedPreferences.getString(USER,"")?.replace("\\\"", "")?.replace("\"","") ?: ""

        // convert game item list into character type list
        val characters: MutableList<CharacterType> = mutableListOf()
        gameItems.forEach { gameItem ->
            val character = CharacterType(fromIntegerToSkin(gameItem.rarity), gameItem.hp, gameItem.damage)
            characters.add(character)
        }

        // Specify the intent
        val intent = Intent(this, AndroidLauncher::class.java)

        intent.putExtra(PLAYER_LIST, characters as Serializable)
        intent.putExtra(USERNAME, userName)
        intent.putExtra(USER, userEmail.removeCharactersAfterAt())

        startActivity(intent)
    }
}

