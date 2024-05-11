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
import androidx.compose.material3.Button

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text

import androidx.compose.ui.Modifier
import com.mygdx.game.data.api.DataRepositoryImpl
import com.mygdx.game.data.dao.GameItem
import com.mygdx.game.data.dao.Rank
import com.mygdx.game.data.manager.LocalUserManagerImpl
import com.mygdx.game.domain.api.DataRepository
import com.mygdx.game.domain.api.RankApi


import com.mygdx.game.dto.CharacterType
import com.mygdx.game.player.PlayerSkin

import com.mygdx.game.ui.theme.ArAppTheme
import com.mygdx.game.presentation.MainViewModel
import com.mygdx.game.presentation.navgraph.NavGraph
import com.mygdx.game.util.Constants
import com.mygdx.game.util.Constants.PLAYER_LIST
import com.mygdx.game.util.Constants.USER
import com.mygdx.game.util.Constants.USERNAME
import com.mygdx.game.util.Constants.USER_SETTINGS
import com.mygdx.game.util.Constants.USER_SETTINGS2
import com.mygdx.game.util.fromIntegerToSkin
import com.mygdx.game.util.removeCharactersAfterAt
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.Serializable

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
                        object: Multiplayer{ override fun onSetMultiplayer() { setMultiplayer() } },
                        startDestination = startDestination
                    )

/*
                    Button(onClick = {

                        setMultiplayer()

                    }) {
                        Text(text = "Multiplayer")
                    }

 */



                }

            }

        }
    }



    fun setMultiplayer(){

        val sharedPreferences = applicationContext.getSharedPreferences(USER_SETTINGS2, Context.MODE_PRIVATE)

        // read username from shared preferences and pass it to the libgdx game
        val userName = sharedPreferences.getString(USERNAME,"")?.replace("\\\"", "")?.replace("\"","") ?: ""
        val userEmail = sharedPreferences.getString(USER,"")?.replace("\\\"", "")?.replace("\"","") ?: ""


        // read characters from username
        val characters = getUserCharacters(sharedPreferences)

        // Specify the intent
        val intent = Intent(this, AndroidLauncher::class.java)

        intent.putExtra(PLAYER_LIST, characters as Serializable)
        intent.putExtra(USERNAME, userName)//?.removeCharactersAfterAt())
        intent.putExtra(USER, userEmail.removeCharactersAfterAt()) //?.removeCharactersAfterAt()

        startActivity(intent)

    }

    private fun getUserCharacters(sharedPreferences: SharedPreferences): List<CharacterType>{
        val characters = mutableListOf<CharacterType>()

        for (i in 1..5){
            val skin = sharedPreferences.getString("SKIN_$i","")
            if (skin != null) {
                if (skin.isNotEmpty()){

                    // convert to json object
                    val jsonObject = JSONObject(skin)
                    val hp = jsonObject.getString("hp")
                    val damage = jsonObject.getString("damage")
                    Log.d("DEBUG","HP: $hp, DAMAGE: $damage")

                    val characterType = CharacterType(fromIntegerToSkin(i), hp.toInt(), damage.toInt())
                    characters.add(characterType)
                }
            }
        }

        return characters
    }


}

