package pt.andresilva.battleship
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import pt.andresilva.battleship.databinding.ActivityPvpModeBinding

class PvpMode : AppCompatActivity() {
    private lateinit var b : ActivityPvpModeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val actionBar: ActionBar? = supportActionBar
        actionBar!!.hide()
        b = ActivityPvpModeBinding.inflate(layoutInflater)
        setContentView(b.root)
    }
}