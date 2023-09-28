package pt.andresilva.battleship

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import pt.andresilva.battleship.databinding.ActivityHomeScreenBinding
import kotlin.system.exitProcess


class HomeScreen : AppCompatActivity() {

    private lateinit var b: ActivityHomeScreenBinding
    private var keepPlayingMusic: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val actionBar: ActionBar? = supportActionBar
        actionBar!!.hide()
        b = ActivityHomeScreenBinding.inflate(layoutInflater)
        setContentView(b.root)
        ActivateListeners()
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


    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBackPressed() {
        val dialogBuilder = AlertDialog.Builder(this)
        // set message of alert dialog
        dialogBuilder.setMessage("Do you really wish to leave sailor?").setCancelable(false)
            .setNegativeButton("No!") { dialog, _ ->
                dialog.cancel()
            }
            .setPositiveButton("Yes!") { _, _ ->
                keepPlayingMusic = true
                MusicPlayer.StopSoundMenu()
                MusicPlayer.resetSoundMenu()
                MusicPlayer.PlaySoundMenu(this.applicationContext)
                moveTaskToBack(true)
                exitProcess(0)
            }

        // create dialog box
        val alert = dialogBuilder.create()
        // set title for alert dialog box
        alert.setTitle("Leaving the app")
        alert.setIcon(getDrawable(R.drawable.battleship))
        // show alert dialog
        alert.show()
    }

    private fun ActivateListeners() {
        b.PlayBtn.setOnClickListener {
            keepPlayingMusic = true
            startActivity(Intent(this, ChooseGameMode::class.java))
        }
        b.EditBtn.setOnClickListener {
            if(checkForInternet(this)){
                keepPlayingMusic = true
                startActivity(Intent(this, EditProfile::class.java))
            }else{
                Toast.makeText(this, "Can't Edit Profile! No Internet Connection Detected!", Toast.LENGTH_SHORT).show()
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