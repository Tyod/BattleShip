package pt.andresilva.battleship

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import pt.andresilva.battleship.databinding.ActivityPickBoardBinding

class PickBoard : AppCompatActivity() {
    private lateinit var b: ActivityPickBoardBinding
    private var chosenMap: Int = 1
    private val map1 = Array(10) { Array(10) { "" } }
    private val map2 = Array(10) { Array(10) { "" } }
    private var keepPlayingMusic: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val actionBar: ActionBar? = supportActionBar
        actionBar!!.hide()
        b = ActivityPickBoardBinding.inflate(layoutInflater)
        setContentView(b.root)
        disableButtons()
        activateListners()

        b.btn00.setBackgroundColor(Color.GRAY)
        b.btn12.setBackgroundColor(Color.GRAY)
        b.btn13.setBackgroundColor(Color.GRAY)
        b.btn14.setBackgroundColor(Color.GRAY)
        b.btn15.setBackgroundColor(Color.GRAY)
        b.btn21.setBackgroundColor(Color.GRAY)
        b.btn31.setBackgroundColor(Color.GRAY)
        b.btn41.setBackgroundColor(Color.GRAY)
        b.btn61.setBackgroundColor(Color.GRAY)
        b.btn71.setBackgroundColor(Color.GRAY)
        b.btn81.setBackgroundColor(Color.GRAY)
        b.btn91.setBackgroundColor(Color.GRAY)
        b.btn34.setBackgroundColor(Color.GRAY)
        b.btn44.setBackgroundColor(Color.GRAY)
        b.btn54.setBackgroundColor(Color.GRAY)
        b.btn64.setBackgroundColor(Color.GRAY)
        b.btn74.setBackgroundColor(Color.GRAY)
        b.btn36.setBackgroundColor(Color.GRAY)
        b.btn94.setBackgroundColor(Color.GRAY)
        b.btn95.setBackgroundColor(Color.GRAY)
        b.btn56.setBackgroundColor(Color.GRAY)
        b.btn57.setBackgroundColor(Color.GRAY)
        b.btn18.setBackgroundColor(Color.GRAY)
        b.btn28.setBackgroundColor(Color.GRAY)
        b.btn77.setBackgroundColor(Color.GRAY)
        b.btn78.setBackgroundColor(Color.GRAY)
        b.btn79.setBackgroundColor(Color.GRAY)
        b.btn97.setBackgroundColor(Color.GRAY)
    }

    override fun onResume() {
        keepPlayingMusic = false
        super.onResume()
        MusicPlayer.ResumeSoundMenu()
    }

    override fun onStop() {
        if (!keepPlayingMusic)
            MusicPlayer.PauseSoundMenu()
        super.onStop()
    }

    @Override
    override fun onBackPressed() {
        keepPlayingMusic = true
        startActivity(Intent(this, ChooseGameMode::class.java))
    }

    private fun activateListners() {
        b.btnPrevious.setOnClickListener {
            if (chosenMap == 2) {
                chosenMap--
                clearBoard()

                b.btn00.setBackgroundColor(Color.GRAY)
                b.btn12.setBackgroundColor(Color.GRAY)
                b.btn13.setBackgroundColor(Color.GRAY)
                b.btn14.setBackgroundColor(Color.GRAY)
                b.btn15.setBackgroundColor(Color.GRAY)
                b.btn21.setBackgroundColor(Color.GRAY)
                b.btn31.setBackgroundColor(Color.GRAY)
                b.btn41.setBackgroundColor(Color.GRAY)
                b.btn61.setBackgroundColor(Color.GRAY)
                b.btn71.setBackgroundColor(Color.GRAY)
                b.btn81.setBackgroundColor(Color.GRAY)
                b.btn91.setBackgroundColor(Color.GRAY)
                b.btn34.setBackgroundColor(Color.GRAY)
                b.btn44.setBackgroundColor(Color.GRAY)
                b.btn54.setBackgroundColor(Color.GRAY)
                b.btn64.setBackgroundColor(Color.GRAY)
                b.btn74.setBackgroundColor(Color.GRAY)
                b.btn36.setBackgroundColor(Color.GRAY)
                b.btn94.setBackgroundColor(Color.GRAY)
                b.btn95.setBackgroundColor(Color.GRAY)
                b.btn56.setBackgroundColor(Color.GRAY)
                b.btn57.setBackgroundColor(Color.GRAY)
                b.btn18.setBackgroundColor(Color.GRAY)
                b.btn28.setBackgroundColor(Color.GRAY)
                b.btn77.setBackgroundColor(Color.GRAY)
                b.btn78.setBackgroundColor(Color.GRAY)
                b.btn79.setBackgroundColor(Color.GRAY)
            }
        }

        b.btnNext.setOnClickListener {
            if (chosenMap == 1) {
                chosenMap++
                clearBoard()

                b.btn00.setBackgroundColor(Color.GRAY)
                b.btn01.setBackgroundColor(Color.GRAY)
                b.btn02.setBackgroundColor(Color.GRAY)
                b.btn03.setBackgroundColor(Color.GRAY)
                b.btn04.setBackgroundColor(Color.GRAY)
                b.btn20.setBackgroundColor(Color.GRAY)
                b.btn31.setBackgroundColor(Color.GRAY)
                b.btn41.setBackgroundColor(Color.GRAY)
                b.btn51.setBackgroundColor(Color.GRAY)
                b.btn24.setBackgroundColor(Color.GRAY)
                b.btn25.setBackgroundColor(Color.GRAY)
                b.btn26.setBackgroundColor(Color.GRAY)
                b.btn09.setBackgroundColor(Color.GRAY)
                b.btn19.setBackgroundColor(Color.GRAY)
                b.btn54.setBackgroundColor(Color.GRAY)
                b.btn55.setBackgroundColor(Color.GRAY)
                b.btn38.setBackgroundColor(Color.GRAY)
                b.btn48.setBackgroundColor(Color.GRAY)
                b.btn58.setBackgroundColor(Color.GRAY)
                b.btn68.setBackgroundColor(Color.GRAY)
                b.btn81.setBackgroundColor(Color.GRAY)
                b.btn82.setBackgroundColor(Color.GRAY)
                b.btn83.setBackgroundColor(Color.GRAY)
                b.btn84.setBackgroundColor(Color.GRAY)
                b.btn76.setBackgroundColor(Color.GRAY)
                b.btn86.setBackgroundColor(Color.GRAY)
                b.btn88.setBackgroundColor(Color.GRAY)
                b.btn99.setBackgroundColor(Color.GRAY)
            }
        }

        b.btnSave.setOnClickListener {
            keepPlayingMusic = true
            MusicPlayer.StopSoundMenu()
            MusicPlayer.resetSoundMenu()
            MusicPlayer.PlaySoundMenu(this.applicationContext)

            val extras = Bundle()
            extras.putInt("Map", chosenMap)
            extras.putInt("numWinsP", intent.extras!!.getInt("numWinsP"))
            extras.putInt("numWinsAI", intent.extras!!.getInt("numWinsAI"))

            val intent = Intent(this, AIMode::class.java)
            intent.putExtras(extras)
            startActivity(intent)
        }
    }

    private fun disableButtons() {
        //Disabling Buttons
        b.btn00.isClickable = false
        b.btn01.isClickable = false
        b.btn02.isClickable = false
        b.btn03.isClickable = false
        b.btn04.isClickable = false
        b.btn05.isClickable = false
        b.btn06.isClickable = false
        b.btn07.isClickable = false
        b.btn08.isClickable = false
        b.btn09.isClickable = false

        b.btn10.isClickable = false
        b.btn11.isClickable = false
        b.btn12.isClickable = false
        b.btn13.isClickable = false
        b.btn14.isClickable = false
        b.btn15.isClickable = false
        b.btn16.isClickable = false
        b.btn17.isClickable = false
        b.btn18.isClickable = false
        b.btn19.isClickable = false

        b.btn20.isClickable = false
        b.btn21.isClickable = false
        b.btn22.isClickable = false
        b.btn23.isClickable = false
        b.btn24.isClickable = false
        b.btn25.isClickable = false
        b.btn26.isClickable = false
        b.btn27.isClickable = false
        b.btn28.isClickable = false
        b.btn29.isClickable = false

        b.btn30.isClickable = false
        b.btn31.isClickable = false
        b.btn32.isClickable = false
        b.btn33.isClickable = false
        b.btn34.isClickable = false
        b.btn35.isClickable = false
        b.btn36.isClickable = false
        b.btn37.isClickable = false
        b.btn38.isClickable = false
        b.btn39.isClickable = false

        b.btn40.isClickable = false
        b.btn41.isClickable = false
        b.btn42.isClickable = false
        b.btn43.isClickable = false
        b.btn44.isClickable = false
        b.btn45.isClickable = false
        b.btn46.isClickable = false
        b.btn47.isClickable = false
        b.btn48.isClickable = false
        b.btn49.isClickable = false

        b.btn50.isClickable = false
        b.btn51.isClickable = false
        b.btn52.isClickable = false
        b.btn53.isClickable = false
        b.btn54.isClickable = false
        b.btn55.isClickable = false
        b.btn56.isClickable = false
        b.btn57.isClickable = false
        b.btn58.isClickable = false
        b.btn59.isClickable = false

        b.btn60.isClickable = false
        b.btn61.isClickable = false
        b.btn62.isClickable = false
        b.btn63.isClickable = false
        b.btn64.isClickable = false
        b.btn65.isClickable = false
        b.btn66.isClickable = false
        b.btn67.isClickable = false
        b.btn68.isClickable = false
        b.btn69.isClickable = false

        b.btn70.isClickable = false
        b.btn71.isClickable = false
        b.btn72.isClickable = false
        b.btn73.isClickable = false
        b.btn74.isClickable = false
        b.btn75.isClickable = false
        b.btn76.isClickable = false
        b.btn77.isClickable = false
        b.btn78.isClickable = false
        b.btn79.isClickable = false

        b.btn80.isClickable = false
        b.btn81.isClickable = false
        b.btn82.isClickable = false
        b.btn83.isClickable = false
        b.btn84.isClickable = false
        b.btn85.isClickable = false
        b.btn86.isClickable = false
        b.btn87.isClickable = false
        b.btn88.isClickable = false
        b.btn89.isClickable = false

        b.btn90.isClickable = false
        b.btn91.isClickable = false
        b.btn92.isClickable = false
        b.btn93.isClickable = false
        b.btn94.isClickable = false
        b.btn95.isClickable = false
        b.btn96.isClickable = false
        b.btn97.isClickable = false
        b.btn98.isClickable = false
        b.btn99.isClickable = false
    }

    private fun clearBoard() {

        //Text
        b.btn00.text = ""
        b.btn01.text = ""
        b.btn02.text = ""
        b.btn03.text = ""
        b.btn04.text = ""
        b.btn05.text = ""
        b.btn06.text = ""
        b.btn07.text = ""
        b.btn08.text = ""
        b.btn09.text = ""

        b.btn10.text = ""
        b.btn11.text = ""
        b.btn12.text = ""
        b.btn13.text = ""
        b.btn14.text = ""
        b.btn15.text = ""
        b.btn16.text = ""
        b.btn17.text = ""
        b.btn18.text = ""
        b.btn19.text = ""

        b.btn20.text = ""
        b.btn21.text = ""
        b.btn22.text = ""
        b.btn23.text = ""
        b.btn24.text = ""
        b.btn25.text = ""
        b.btn26.text = ""
        b.btn27.text = ""
        b.btn28.text = ""
        b.btn29.text = ""

        b.btn30.text = ""
        b.btn31.text = ""
        b.btn32.text = ""
        b.btn33.text = ""
        b.btn34.text = ""
        b.btn35.text = ""
        b.btn36.text = ""
        b.btn37.text = ""
        b.btn38.text = ""
        b.btn39.text = ""

        b.btn40.text = ""
        b.btn41.text = ""
        b.btn42.text = ""
        b.btn43.text = ""
        b.btn44.text = ""
        b.btn45.text = ""
        b.btn46.text = ""
        b.btn47.text = ""
        b.btn48.text = ""
        b.btn49.text = ""

        b.btn50.text = ""
        b.btn51.text = ""
        b.btn52.text = ""
        b.btn53.text = ""
        b.btn54.text = ""
        b.btn55.text = ""
        b.btn56.text = ""
        b.btn57.text = ""
        b.btn58.text = ""
        b.btn59.text = ""

        b.btn60.text = ""
        b.btn61.text = ""
        b.btn62.text = ""
        b.btn63.text = ""
        b.btn64.text = ""
        b.btn65.text = ""
        b.btn66.text = ""
        b.btn67.text = ""
        b.btn68.text = ""
        b.btn69.text = ""

        b.btn70.text = ""
        b.btn71.text = ""
        b.btn72.text = ""
        b.btn73.text = ""
        b.btn74.text = ""
        b.btn75.text = ""
        b.btn76.text = ""
        b.btn77.text = ""
        b.btn78.text = ""
        b.btn79.text = ""

        b.btn80.text = ""
        b.btn81.text = ""
        b.btn82.text = ""
        b.btn83.text = ""
        b.btn84.text = ""
        b.btn85.text = ""
        b.btn86.text = ""
        b.btn87.text = ""
        b.btn88.text = ""
        b.btn89.text = ""

        b.btn90.text = ""
        b.btn91.text = ""
        b.btn92.text = ""
        b.btn93.text = ""
        b.btn94.text = ""
        b.btn95.text = ""
        b.btn96.text = ""
        b.btn97.text = ""
        b.btn98.text = ""
        b.btn99.text = ""


        //COLOR
        b.btn00.setBackgroundColor(0x2196F3)
        b.btn01.setBackgroundColor(0x2196F3)
        b.btn02.setBackgroundColor(0x2196F3)
        b.btn03.setBackgroundColor(0x2196F3)
        b.btn04.setBackgroundColor(0x2196F3)
        b.btn05.setBackgroundColor(0x2196F3)
        b.btn06.setBackgroundColor(0x2196F3)
        b.btn07.setBackgroundColor(0x2196F3)
        b.btn08.setBackgroundColor(0x2196F3)
        b.btn09.setBackgroundColor(0x2196F3)

        b.btn10.setBackgroundColor(0x2196F3)
        b.btn11.setBackgroundColor(0x2196F3)
        b.btn12.setBackgroundColor(0x2196F3)
        b.btn13.setBackgroundColor(0x2196F3)
        b.btn14.setBackgroundColor(0x2196F3)
        b.btn15.setBackgroundColor(0x2196F3)
        b.btn16.setBackgroundColor(0x2196F3)
        b.btn17.setBackgroundColor(0x2196F3)
        b.btn18.setBackgroundColor(0x2196F3)
        b.btn19.setBackgroundColor(0x2196F3)

        b.btn20.setBackgroundColor(0x2196F3)
        b.btn21.setBackgroundColor(0x2196F3)
        b.btn22.setBackgroundColor(0x2196F3)
        b.btn23.setBackgroundColor(0x2196F3)
        b.btn24.setBackgroundColor(0x2196F3)
        b.btn25.setBackgroundColor(0x2196F3)
        b.btn26.setBackgroundColor(0x2196F3)
        b.btn27.setBackgroundColor(0x2196F3)
        b.btn28.setBackgroundColor(0x2196F3)
        b.btn29.setBackgroundColor(0x2196F3)

        b.btn30.setBackgroundColor(0x2196F3)
        b.btn31.setBackgroundColor(0x2196F3)
        b.btn32.setBackgroundColor(0x2196F3)
        b.btn33.setBackgroundColor(0x2196F3)
        b.btn34.setBackgroundColor(0x2196F3)
        b.btn35.setBackgroundColor(0x2196F3)
        b.btn36.setBackgroundColor(0x2196F3)
        b.btn37.setBackgroundColor(0x2196F3)
        b.btn38.setBackgroundColor(0x2196F3)
        b.btn39.setBackgroundColor(0x2196F3)

        b.btn40.setBackgroundColor(0x2196F3)
        b.btn41.setBackgroundColor(0x2196F3)
        b.btn42.setBackgroundColor(0x2196F3)
        b.btn43.setBackgroundColor(0x2196F3)
        b.btn44.setBackgroundColor(0x2196F3)
        b.btn45.setBackgroundColor(0x2196F3)
        b.btn46.setBackgroundColor(0x2196F3)
        b.btn47.setBackgroundColor(0x2196F3)
        b.btn48.setBackgroundColor(0x2196F3)
        b.btn49.setBackgroundColor(0x2196F3)

        b.btn50.setBackgroundColor(0x2196F3)
        b.btn51.setBackgroundColor(0x2196F3)
        b.btn52.setBackgroundColor(0x2196F3)
        b.btn53.setBackgroundColor(0x2196F3)
        b.btn54.setBackgroundColor(0x2196F3)
        b.btn55.setBackgroundColor(0x2196F3)
        b.btn56.setBackgroundColor(0x2196F3)
        b.btn57.setBackgroundColor(0x2196F3)
        b.btn58.setBackgroundColor(0x2196F3)
        b.btn59.setBackgroundColor(0x2196F3)

        b.btn60.setBackgroundColor(0x2196F3)
        b.btn61.setBackgroundColor(0x2196F3)
        b.btn62.setBackgroundColor(0x2196F3)
        b.btn63.setBackgroundColor(0x2196F3)
        b.btn64.setBackgroundColor(0x2196F3)
        b.btn65.setBackgroundColor(0x2196F3)
        b.btn66.setBackgroundColor(0x2196F3)
        b.btn67.setBackgroundColor(0x2196F3)
        b.btn68.setBackgroundColor(0x2196F3)
        b.btn69.setBackgroundColor(0x2196F3)

        b.btn70.setBackgroundColor(0x2196F3)
        b.btn71.setBackgroundColor(0x2196F3)
        b.btn72.setBackgroundColor(0x2196F3)
        b.btn73.setBackgroundColor(0x2196F3)
        b.btn74.setBackgroundColor(0x2196F3)
        b.btn75.setBackgroundColor(0x2196F3)
        b.btn76.setBackgroundColor(0x2196F3)
        b.btn77.setBackgroundColor(0x2196F3)
        b.btn78.setBackgroundColor(0x2196F3)
        b.btn79.setBackgroundColor(0x2196F3)

        b.btn80.setBackgroundColor(0x2196F3)
        b.btn81.setBackgroundColor(0x2196F3)
        b.btn82.setBackgroundColor(0x2196F3)
        b.btn83.setBackgroundColor(0x2196F3)
        b.btn84.setBackgroundColor(0x2196F3)
        b.btn85.setBackgroundColor(0x2196F3)
        b.btn86.setBackgroundColor(0x2196F3)
        b.btn87.setBackgroundColor(0x2196F3)
        b.btn88.setBackgroundColor(0x2196F3)
        b.btn89.setBackgroundColor(0x2196F3)

        b.btn90.setBackgroundColor(0x2196F3)
        b.btn91.setBackgroundColor(0x2196F3)
        b.btn92.setBackgroundColor(0x2196F3)
        b.btn93.setBackgroundColor(0x2196F3)
        b.btn94.setBackgroundColor(0x2196F3)
        b.btn95.setBackgroundColor(0x2196F3)
        b.btn96.setBackgroundColor(0x2196F3)
        b.btn97.setBackgroundColor(0x2196F3)
        b.btn98.setBackgroundColor(0x2196F3)
        b.btn99.setBackgroundColor(0x2196F3)
    }
}