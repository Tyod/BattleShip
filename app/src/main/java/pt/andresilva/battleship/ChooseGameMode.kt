package pt.andresilva.battleship

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import pt.andresilva.battleship.databinding.ActivityChooseGameModeBinding

class ChooseGameMode : AppCompatActivity() {

    private lateinit var b: ActivityChooseGameModeBinding
    private var keepPlayingMusic: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val actionBar: ActionBar? = supportActionBar
        actionBar!!.hide()
        b = ActivityChooseGameModeBinding.inflate(layoutInflater)
        setContentView(b.root)

        ActivateListeners()
    }

    override fun onResume() {
        super.onResume()
        keepPlayingMusic = false
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
        startActivity(Intent(this, HomeScreen::class.java))
    }

    private fun ActivateListeners() {
        b.CoOpBtn.setOnClickListener {
            keepPlayingMusic = true

            val extras = Bundle()
            extras.putInt("numWinsP", 0)
            extras.putInt("numWinsAI", 0)

            val intent = Intent(this, PickBoard::class.java)
            intent.putExtras(extras)
            startActivity(intent)
        }

        b.PvpBtn.setOnClickListener {
            if(checkForInternet(this)){
                keepPlayingMusic = true
                startActivity(Intent(this, PvpMode::class.java))
            }else{
                Toast.makeText(this, "Can't Play PvP Mode! No Internet Connection Detected!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkForInternet(context: Context): Boolean {

        // register activity with the connectivity manager service
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // if the android version is equal to M
        // or greater we need to use the
        // NetworkCapabilities to check what type of
        // network has the internet connection
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // Returns a Network object corresponding to
            // the currently active default data network.
            val network = connectivityManager.activeNetwork ?: return false

            // Representation of the capabilities of an active network.
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                // Indicates this network uses a Wi-Fi transport,
                // or WiFi has network connectivity
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true

                // Indicates this network uses a Cellular transport. or
                // Cellular has network connectivity
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true

                // else return false
                else -> false
            }
        } else {
            // if the android version is below M
            @Suppress("DEPRECATION") val networkInfo =
                connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }
}