package pt.andresilva.battleship

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import pt.andresilva.battleship.databinding.ActivityEditProfileBinding


class EditProfile : AppCompatActivity() {

    private lateinit var b: ActivityEditProfileBinding
    private var Side: Boolean? = null
    private var keepPlayingMusic: Boolean = false
    private var dataBase =
        FirebaseDatabase.getInstance("https://battleship-d4403-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference("game")
    private var numPlayers = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val actionBar: ActionBar? = supportActionBar
        actionBar!!.hide()
        b = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(b.root)
        ActivateListeners()
        dataBase.child("users").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                numPlayers = dataSnapshot.childrenCount.toInt()
                Log.i("LOG", "$numPlayers")
                loadData()
                activateDBRelatedBtns()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
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
        startActivity(Intent(this, HomeScreen::class.java))
    }

    private fun VerifySide() {
        if (Side!!) {
            b.profileImage1.borderColor = Color.parseColor("#2196F3")
            b.profileImage2.borderColor = Color.parseColor("#00000000")
        } else {
            b.profileImage1.borderColor = Color.parseColor("#00000000")
            b.profileImage2.borderColor = Color.parseColor("#2196F3")
        }
    }

    private fun saveData() {
        val sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        User.side = Side!!

        if (b.CrewNameTF.text.isNullOrBlank() || b.CrewNameTF.text.isNullOrEmpty()) {

            //Refresh User Object data, and displayed name
            if (b.CrewNameTF.hint.toString() != sharedPreferences.getString(
                    "KeyCrewName",
                    "Nameless Crew"
                ).toString()
            )
                User.username = b.CrewNameTF.hint.toString()
            else {
                User.username =
                    sharedPreferences.getString("KeyCrewName", "Nameless Crew").toString()
                b.CrewNameTF.hint =
                    sharedPreferences.getString("KeyCrewName", "Nameless Crew").toString()
            }

            //Save data to DataBase, and get
            dataBase.child("users")
                .child(sharedPreferences.getString("KeyCrewName", "Nameless Crew").toString())
                .setValue(User)
        } else {
            //Refesh User Object data, and Save data to DataBase
            User.username = b.CrewNameTF.text.toString()
            dataBase.child("users")
                .child(sharedPreferences.getString("KeyCrewName", "Nameless Crew").toString())
                .setValue(User)
        }

    }

    private fun loadData() {
        val sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()

        editor.apply {
            if (sharedPreferences.getString("KeyCrewName", "Nameless Crew")
                    .toString() == "Nameless Crew"
            ) {
                editor.putString("KeyCrewName", "user$numPlayers")
                editor.putBoolean("firstTimeRun", true)
            }
        }.apply()

        if (sharedPreferences.getBoolean("firstTimeRun", true)) {
            User.username = sharedPreferences.getString("KeyCrewName", "Nameless Crew").toString()
            dataBase.child("users")
                .child(sharedPreferences.getString("KeyCrewName", "Nameless Crew").toString())
                .setValue(User)
            b.CrewNameTF.hint =
                sharedPreferences.getString("KeyCrewName", "Nameless Crew").toString()
            Side = true
            editor.apply {
                editor.putBoolean("firstTimeRun", false)
            }.apply()
            VerifySide()
        } else {
            dataBase.child("users").get().addOnSuccessListener {
                b.CrewNameTF.hint =
                    it.child(sharedPreferences.getString("KeyCrewName", "Nameless Crew").toString())
                        .child("username").value as String
                Side =
                    it.child(sharedPreferences.getString("KeyCrewName", "Nameless Crew").toString())
                        .child("side").value as Boolean
                VerifySide()
            }
        }
    }

    private fun ActivateListeners() {
        b.CancelBtn.setOnClickListener {
            disableFirstTimeRun();
            keepPlayingMusic = true
            val intent = Intent(this, HomeScreen::class.java)
            startActivity(intent)
        }
    }

    private fun disableFirstTimeRun() {
        val sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()

        editor.apply {
            if (sharedPreferences.getBoolean("firstTimeRun", true))
                editor.putBoolean("firstTimeRun", false)
        }.apply()
    }

    private fun activateDBRelatedBtns() {
        b.PickSideBtn.setOnClickListener {
            Side = !Side!!
            VerifySide()
        }

        b.SaveBtn.setOnClickListener {
            saveData()
        }
    }
}