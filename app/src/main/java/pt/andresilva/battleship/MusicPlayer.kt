package pt.andresilva.battleship

import android.content.Context
import android.media.MediaPlayer

object MusicPlayer {
    private var musicMenu: MediaPlayer? = null
    private var musicGame: MediaPlayer? = null
    private var musicPlayingMenu: Boolean = false
    private var musicPlayingGame: Boolean = false


    //MENU MUSIC
    fun PlaySoundMenu(ctx: Context?) {
        musicMenu = MediaPlayer.create(ctx, R.raw.menu)
        musicMenu?.setOnCompletionListener {
            musicMenu?.start()
        }
    }

    fun ResumeSoundMenu() {
        musicMenu!!.start()
    }

    fun PauseSoundMenu() {
        musicMenu!!.pause()
    }

    fun StopSoundMenu() {
        musicMenu!!.stop()
    }

    fun resetSoundMenu() {
        musicMenu!!.reset()
        musicMenu!!.release()
    }


    //GAME MUSIC
    fun PlaySoundGame(ctx: Context?) {
        musicGame = MediaPlayer.create(ctx, R.raw.game)
        musicGame?.setOnCompletionListener {
            musicGame?.start()
        }
    }

    fun ResumeSoundGame() {
        musicGame!!.start()
    }

    fun PauseSoundGame() {
        musicGame?.pause()
    }

    fun StopSoundGame() {
        musicGame?.stop()
    }

    fun resetSoundGame() {
        musicGame!!.reset()
        musicGame!!.release()
    }
}