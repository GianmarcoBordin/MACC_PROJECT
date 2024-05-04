package com.mygdx.game.player

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound

const val path = "audio/"
const val volume = 0.3f

class SoundManager{

    private lateinit var battleMusic: Music
    private lateinit var shootSound: Sound
    private lateinit var hitSound: Sound 


    init {
        loadSounds()
    }

    private fun loadSounds() {
        // Load music and sounds here
        battleMusic = Gdx.audio.newMusic(Gdx.files.internal(path + "battle_loop.mp3"))
        shootSound = Gdx.audio.newSound(Gdx.files.internal(path + "shoot.wav"))
        hitSound = Gdx.audio.newSound(Gdx.files.internal(path + "hit.wav"))

    }
    
    fun playBattle(){
        battleMusic.isLooping = true
        battleMusic.play()
    }

    fun stopBattle(){
        battleMusic.stop()
    }




    fun playShoot(){
        shootSound.play(volume)
    }
    
    fun playHit(){
        hitSound.play(volume)
    }

    
    fun dispose() {
        battleMusic.dispose()
        shootSound.dispose()
        hitSound.dispose()

    }

}
