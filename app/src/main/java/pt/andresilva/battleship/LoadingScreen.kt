package pt.andresilva.battleship
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import pt.andresilva.battleship.databinding.ActivityLoadingscreenBinding

class LoadingScreen : AppCompatActivity() {

    private lateinit var b: ActivityLoadingscreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MusicPlayer.PlaySoundMenu(this.applicationContext)
        MusicPlayer.PlaySoundGame(this.applicationContext)
        val actionBar: ActionBar? = supportActionBar
        actionBar!!.hide()
        b = ActivityLoadingscreenBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.image.animate().apply {
            duration = 1000
            rotationYBy(360f)
        }.withEndAction{
            val sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)

            if(sharedPreferences.getBoolean("firstTimeRun", true)){
                startActivity(Intent(this, EditProfile::class.java))
            }else{
                startActivity(Intent(this, HomeScreen::class.java))
            }
        }
    }

    @Override
    override fun onBackPressed() {
    }
}