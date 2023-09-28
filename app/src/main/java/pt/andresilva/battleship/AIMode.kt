package pt.andresilva.battleship


import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import pt.andresilva.battleship.databinding.ActivityAimodeBinding
import kotlin.concurrent.thread
import kotlin.random.Random


class AIMode : AppCompatActivity() {

    private lateinit var b: ActivityAimodeBinding

    //Boards
    private var shipsPositions1 = Array(10) { Array(10) { "" } }
    private var shipsPositions2 = Array(10) { Array(10) { "" } }
    private var boardPlayer = Array(10) { Array(10) { "" } }
    private var boardAI = Array(10) { Array(10) { "" } }

    //Round Management
    private lateinit var actualP: String
    private val PLAYER: String = "P"
    private val AI: String = "AI"

    //Boats Management
    private var num5boatP: Int = 1
    private var num4boatP: Int = 2
    private var num3boatP: Int = 2
    private var num2boatP: Int = 3
    private var num1boatP: Int = 3
    private var num5boatAI: Int = 1
    private var num4boatAI: Int = 2
    private var num3boatAI: Int = 2
    private var num2boatAI: Int = 3
    private var num1boatAI: Int = 3
    private var remainingSpotsP = 28
    private var remainingSpotsAI = 28

    //AI Play Vars
    private var firstHit: Boolean = false
    private var secondHit: Boolean = false
    private var lastHitCollumn: Int = 0
    private var lastHitRow: Int = 0
    private var firstHitRow: Int = 0
    private var firstHitCollumn: Int = 0
    private var presentDirection = ""
    private val UP = "UP"
    private val DOWN = "DOWN"
    private val RIGHT = "RIGHT"
    private val LEFT = "LEFT"
    private var up = true
    private var down = true
    private var right = true
    private var letf = true
    private var random: Random = Random(System.currentTimeMillis())

    //Utility Vars
    private var spamClickerBlocker: Boolean = false
    private var showAIShips: Boolean = false
    private var scouting: Boolean = false
    private var keepPlayingMusic: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val actionBar: ActionBar? = supportActionBar
        actionBar!!.hide()
        b = ActivityAimodeBinding.inflate(layoutInflater)
        setContentView(b.root)
        createPlayerBoard(intent.extras!!.getInt("Map"))
        b.numWinsAI.text = intent.extras!!.getInt("numWinsAI").toString()
        b.numWinsP.text = intent.extras!!.getInt("numWinsP").toString()
        activateListeners()
        actualP = PLAYER
        b.profileImage1.borderColor = Color.GREEN
        b.profileImage2.borderColor = Color.BLACK
        createAndChooseShipsPositions()
        showBoardAI()
    }

    override fun onResume() {
        super.onResume()
        keepPlayingMusic = false
        MusicPlayer.ResumeSoundGame()
    }

    override fun onStop() {
        if (!keepPlayingMusic)
            MusicPlayer.PauseSoundGame()
        super.onStop()
    }

    @SuppressLint("SetTextI18n")
    private fun switchPlayer() {
        if (actualP == PLAYER) {
            if(!checkWinner()){
                clearBoard()
                showBoardPlayer()
                actualP = AI
                b.tf5boat.text = num5boatP.toString() + "x"
                b.tf4boat.text = num4boatP.toString() + "x"
                b.tf3boat.text = num3boatP.toString() + "x"
                b.tf2boat.text = num2boatP.toString() + "x"
                b.tf1boat.text = num1boatP.toString() + "x"
                b.btnScout.isVisible = false
                b.btnInfo.isVisible = false
                b.tfBoats.text = "Boats Left:"
                b.tfBoats.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    setMargins(0, 0, 640, 0) //parameters are in pixel
                }
                b.TurnBoard.text = getString(R.string.enemy_turn)
                b.TurnBoard.setTextColor(Color.RED)
                b.profileImage2.borderColor = Color.RED
                b.profileImage1.borderColor = Color.BLACK
                aiPlay()
                return
            }
        }

        if (actualP == AI) {
            if(!checkWinner()){
                clearBoard()
                showBoardAI()
                actualP = PLAYER
                b.tf5boat.text = num5boatAI.toString() + "x"
                b.tf4boat.text = num4boatAI.toString() + "x"
                b.tf3boat.text = num3boatAI.toString() + "x"
                b.tf2boat.text = num2boatAI.toString() + "x"
                b.tf1boat.text = num1boatAI.toString() + "x"
                b.btnScout.isVisible = true
                b.btnInfo.isVisible = true
                b.tfBoats.text = "Enemy Boats Left:"
                b.tfBoats.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    setMargins(0, 0, 0, 0) //parameters are in pixel
                }
                b.TurnBoard.text = getString(R.string.my_turn)
                b.TurnBoard.setTextColor(Color.GREEN)
                b.profileImage1.borderColor = Color.GREEN
                b.profileImage2.borderColor = Color.BLACK
                spamClickerBlocker = false
            }
        }
    }

    private fun aiPlay() {
        var row: Int = random.nextInt(0, 10)
        var collumn: Int = random.nextInt(0, 10)

        if (!firstHit) {
            while (boardPlayer[row][collumn] == "X" || boardPlayer[row][collumn] == "SX") {
                row = random.nextInt(0, 10)
                collumn = random.nextInt(0, 10)
            }

            thread(start = true) {
                Thread.sleep(1000L)
                runOnUiThread { verifiyHitPlayer(row, collumn) }
            }
        }
        if (firstHit && !secondHit) {
            var validDirection = false

            //Pick a direction
            while (!validDirection) {
                val direction = random.nextInt(0, 4)

                //UP DIRECTION
                if (direction == 0 && up) {
                    if (lastHitRow - 1 != -1 && boardPlayer[lastHitRow - 1][lastHitCollumn] != "X" && boardPlayer[lastHitRow - 1][lastHitCollumn] != "SX") {
                        presentDirection = UP
                        validDirection = true
                        thread(start = true) {
                            Thread.sleep(1000L)
                            runOnUiThread { verifiyHitPlayer(lastHitRow - 1, lastHitCollumn) }
                        }
                    } else {
                        up = false
                    }
                }

                //RIGHT DIRECTION
                if (direction == 1 && right) {
                    if (lastHitCollumn + 1 != 10 && boardPlayer[lastHitRow][lastHitCollumn + 1] != "X" && boardPlayer[lastHitRow][lastHitCollumn + 1] != "SX") {
                        presentDirection = RIGHT
                        validDirection = true
                        thread(start = true) {
                            Thread.sleep(1000L)
                            runOnUiThread { verifiyHitPlayer(lastHitRow, lastHitCollumn + 1) }
                        }
                    } else {
                        right = false
                    }
                }

                //DOWN DIRECTION
                if (direction == 2 && down) {
                    if (lastHitRow + 1 != 10 && boardPlayer[lastHitRow + 1][lastHitCollumn] != "X" && boardPlayer[lastHitRow + 1][lastHitCollumn] != "SX") {
                        presentDirection = DOWN
                        validDirection = true
                        thread(start = true) {
                            Thread.sleep(1000L)
                            runOnUiThread { verifiyHitPlayer(lastHitRow + 1, lastHitCollumn) }
                        }
                    } else {
                        down = false
                    }
                }

                //LEFT DIRECTION
                if (direction == 3 && letf) {
                    if (lastHitCollumn - 1 != -1 && boardPlayer[lastHitRow][lastHitCollumn - 1] != "X" && boardPlayer[lastHitRow][lastHitCollumn-1] != "SX") {
                        presentDirection = LEFT
                        validDirection = true
                        thread(start = true) {
                            Thread.sleep(1000L)
                            runOnUiThread { verifiyHitPlayer(lastHitRow, lastHitCollumn - 1) }
                        }
                    } else {
                        letf = false
                    }
                }
            }
        }

        if (firstHit && secondHit) {

            if (presentDirection == UP) {
                if (lastHitRow - 1 != -1 && boardPlayer[lastHitRow - 1][lastHitCollumn] != "X" && boardPlayer[lastHitRow - 1][lastHitCollumn] != "SX") {
                    thread(start = true) {
                        Thread.sleep(1000L)
                        runOnUiThread { verifiyHitPlayer(lastHitRow - 1, lastHitCollumn) }
                    }
                } else {
                    lastHitRow = firstHitRow
                    lastHitCollumn = firstHitCollumn
                    presentDirection = DOWN
                    thread(start = true) {
                        Thread.sleep(1000L)
                        runOnUiThread { verifiyHitPlayer(lastHitRow + 1, lastHitCollumn) }
                    }
                    return
                }
            }
            if (presentDirection == RIGHT) {
                if (lastHitCollumn + 1 != 10 && boardPlayer[lastHitRow][lastHitCollumn + 1] != "X" && boardPlayer[lastHitRow][lastHitCollumn + 1] != "SX") {
                    thread(start = true) {
                        Thread.sleep(1000L)
                        runOnUiThread { verifiyHitPlayer(lastHitRow, lastHitCollumn + 1) }
                    }
                } else {
                    lastHitRow = firstHitRow
                    lastHitCollumn = firstHitCollumn
                    presentDirection = LEFT
                    thread(start = true) {
                        Thread.sleep(1000L)
                        runOnUiThread { verifiyHitPlayer(lastHitRow, lastHitCollumn - 1) }
                    }
                    return
                }
            }
            if (presentDirection == DOWN) {
                if (lastHitRow + 1 != 10 && boardPlayer[lastHitRow + 1][lastHitCollumn] != "X" && boardPlayer[lastHitRow + 1][lastHitCollumn] != "SX") {
                    thread(start = true) {
                        Thread.sleep(1000L)
                        runOnUiThread { verifiyHitPlayer(lastHitRow + 1, lastHitCollumn) }
                    }
                } else {
                    lastHitRow = firstHitRow
                    lastHitCollumn = firstHitCollumn
                    presentDirection = UP
                    thread(start = true) {
                        Thread.sleep(1000L)
                        runOnUiThread { verifiyHitPlayer(lastHitRow - 1, lastHitCollumn) }
                    }
                    return
                }
            }
            if (presentDirection == LEFT) {
                if (lastHitCollumn - 1 != -1 && boardPlayer[lastHitRow][lastHitCollumn-1] != "X" && boardPlayer[lastHitRow][lastHitCollumn-1] != "SX") {
                    thread(start = true) {
                        Thread.sleep(1000L)
                        runOnUiThread { verifiyHitPlayer(lastHitRow, lastHitCollumn - 1) }
                    }
                } else {
                    lastHitRow = firstHitRow
                    lastHitCollumn = firstHitCollumn
                    presentDirection = RIGHT
                    thread(start = true) {
                        Thread.sleep(1000L)
                        runOnUiThread { verifiyHitPlayer(lastHitRow, lastHitCollumn + 1) }
                    }
                    return
                }
            }
        }
    }

    private fun verifiyHitPlayer(row: Int, collumn: Int) {

        if (boardPlayer[row][collumn] == "S") {
            boardPlayer[row][collumn] = "SX"
            if (firstHit)
                secondHit = true
            else {
                firstHit = true
                firstHitRow = row
                firstHitCollumn = collumn
            }
            lastHitRow = row
            lastHitCollumn = collumn
            checkDestroyedBoatPlayer(lastHitRow, lastHitCollumn)
            remainingSpotsP--
        } else if (boardPlayer[row][collumn] == "")
            boardPlayer[row][collumn] = "X"

        showBoardPlayer()
        thread(start = true) {
            Thread.sleep(700L)
            runOnUiThread { switchPlayer() }
        }
    }

    private fun verifiyHitAI(row: Int, collumn: Int) {

        if (boardAI[row][collumn] == "B") {
            boardAI[row][collumn] = "BX"
            checkDestroyedBoatAI(row, collumn)
            remainingSpotsAI--
        } else if (boardAI[row][collumn] == "")
            boardAI[row][collumn] = "X"

        showBoardAI()
        thread(start = true) {
            Thread.sleep(1000L)
            runOnUiThread { switchPlayer() }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun checkDestroyedBoatAI(row: Int, collumn: Int) {
        lateinit var toast: Toast
        var numCells = 0
        var u = true
        var r = true
        var d = true
        var l = true

        //UP Direction
        if (row-1 != -1) {
            if(boardAI[row-1][collumn] == "B" || boardAI[row-1][collumn] == "BX")
                u = false
        }

        //DOWN Direction
        if (row+1 != 10) {
            if(boardAI[row+1][collumn] == "B" || boardAI[row+1][collumn] == "BX")
                d = false
        }

        //LEFT Direction
        if (collumn-1 != -1){
            if(boardAI[row][collumn-1] == "B" || boardAI[row][collumn-1] == "BX")
                l = false
        }

        //Right Direction
        if (collumn+1 != 10){
            if(boardAI[row][collumn+1] == "B" || boardAI[row][collumn+1] == "BX")
                r = false
        }

        if(u && r && d && l){
            num1boatAI--
            b.tf1boat.text = num1boatAI.toString() + "x"
            toast = Toast.makeText(applicationContext, "You Destroyed a boat! (Size '1')", Toast.LENGTH_LONG)
            toast.show()
        }else{
            u = true
            r = true
            d = true
            l = true
        }


        //Verify 2+Square Boat Vertical
        if(row-1 != -1){
            for(i in row-1 downTo 0){
                if(boardAI[i][collumn] == "B"){
                    u = false
                    break
                }
                if(boardAI[i][collumn]== "" || boardAI[i][collumn] == "X")
                    break
            }
        }

        if(row+1 != 10){
            for(i in row+1..9){
                if(boardAI[i][collumn] == "B"){
                    d = false
                    break
                }
                if(boardAI[i][collumn]== "" || boardAI[i][collumn] == "X")
                    break
            }
        }

        if(d && u){
            for(i in row..9){
                if(boardAI[i][collumn] == "BX")
                    numCells++
                if(boardAI[i][collumn] == "" || boardAI[i][collumn] == "X")
                    break
            }

            for(i in row downTo 0){
                if(boardAI[i][collumn] == "BX")
                    numCells++
                if(boardAI[i][collumn] == "" || boardAI[i][collumn] == "X")
                    break
            }

            numCells--

            if(numCells != 1){
                toast = Toast.makeText(applicationContext, "You Destroyed a boat! (Size '$numCells')", Toast.LENGTH_LONG)
                toast.show()

                if(numCells==2){
                    num2boatAI--
                    b.tf2boat.text = num2boatAI.toString() + "x"
                }
                if(numCells==3){
                    num3boatAI--
                    b.tf3boat.text = num3boatAI.toString() + "x"
                }
                if(numCells==4){
                    num4boatAI--
                    b.tf4boat.text = num4boatAI.toString() + "x"
                }
                if(numCells==5){
                    num5boatAI--
                    b.tf5boat.text = num5boatAI.toString() + "x"
                }
            }
        }else{
            r = true
            l = true
        }

        numCells = 0

        //Verify 2+Square Boat Horizontal
        if(collumn+1 != 10){
            for(i in collumn+1..9){
                if(boardAI[row][i] == "B"){
                    r = false
                    break
                }
                if(boardAI[row][i] == "" || boardAI[row][i] == "X")
                    break
            }
        }

        if(collumn-1 != -1){
            for(i in collumn-1 downTo 0){
                if(boardAI[row][i] == "B"){
                    l = false
                    break
                }
                if(boardAI[row][i] == "" || boardAI[row][i] == "X")
                    break
            }
        }

        if(r && l){
            for(i in collumn..9){
                if(boardAI[row][i] == "BX")
                    numCells++
                if(boardAI[row][i] == "" || boardAI[row][i] == "X")
                    break
            }

            for(i in collumn downTo 0){
                if(boardAI[row][i] == "BX")
                    numCells++
                if(boardAI[row][i] == "" || boardAI[row][i] == "X")
                    break
            }

            numCells--

            if(numCells != 1){
                toast = Toast.makeText(applicationContext, "You Destroyed a boat! (Size '$numCells')", Toast.LENGTH_LONG)
                toast.show()

                if(numCells==2){
                    num2boatAI--
                    b.tf2boat.text = num2boatAI.toString() + "x"
                }
                if(numCells==3){
                    num3boatAI--
                    b.tf3boat.text = num3boatAI.toString() + "x"
                }
                if(numCells==4){
                    num4boatAI--
                    b.tf4boat.text = num4boatAI.toString() + "x"
                }
                if(numCells==5){
                    num5boatAI--
                    b.tf5boat.text = num5boatAI.toString() + "x"
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun checkDestroyedBoatPlayer(lastHitRow: Int, lastHitCollumn: Int) {
        lateinit var toast: Toast
        var numCells = 0
        var u = true
        var r = true
        var d = true
        var l = true

        //Verify 1Square Boat
        if (presentDirection == "") {

            //UP Direction
            if (lastHitRow-1 != -1) {
                if(boardPlayer[lastHitRow-1][lastHitCollumn] == "S")
                    u = false
            }

            //DOWN Direction
            if (lastHitRow+1 != 10) {
                if(boardPlayer[lastHitRow+1][lastHitCollumn] == "S")
                    d = false
            }

            //LEFT Direction
            if (lastHitCollumn-1 != -1){
                if(boardPlayer[lastHitRow][lastHitCollumn-1] == "S")
                    l = false
            }

            //Right Direction
            if (lastHitCollumn+1 != 10){
                if(boardPlayer[lastHitRow][lastHitCollumn+1] == "S")
                    r = false
            }

            if(u && r && d && l){
                num1boatP--
                b.tf1boat.text = num1boatP.toString() + "x"
                toast = Toast.makeText(applicationContext, "Enemy Destroyed a Boat! (Size '1')", Toast.LENGTH_LONG)
                toast.show()
                resetVariablesAI()
            }
        }

        //Verify 2+Square Boat Vertical
        if (presentDirection == UP || presentDirection == DOWN) {

            if (lastHitRow-1 != -1) {
                if(boardPlayer[lastHitRow-1][lastHitCollumn] == "S")
                    u = false
            }

            if(firstHitRow+1 != 10){
                if( boardPlayer[firstHitRow+1][firstHitCollumn] == "S")
                    d = false
            }

            if (lastHitRow+1 != 10) {
                if(boardPlayer[lastHitRow+1][lastHitCollumn] == "S")
                    d = false
            }

            if(firstHitRow-1!= -1){
                if(boardPlayer[firstHitRow-1][firstHitCollumn] == "S")
                    u = false
            }

            if (u && d) {
                for (i in lastHitRow downTo 0) {
                    if (boardPlayer[i][lastHitCollumn] == "SX")
                        numCells++
                    if (boardPlayer[i][lastHitCollumn] == "X" || boardPlayer[i][lastHitCollumn] == "")
                        break
                }

                for (i in lastHitRow..9) {
                    if (boardPlayer[i][lastHitCollumn] == "SX")
                        numCells++
                    if (boardPlayer[i][lastHitCollumn] == "X" || boardPlayer[i][lastHitCollumn] == "")
                        break
                }

                numCells--
                toast = Toast.makeText(applicationContext, "Enemy Destroyed a Boat! (Size '$numCells')", Toast.LENGTH_LONG)
                toast.show()

                if (numCells == 2) {
                    num2boatP--
                    b.tf2boat.text = num2boatP.toString() + "x"
                }
                if (numCells == 3){
                    num3boatP--
                    b.tf3boat.text = num3boatP.toString() + "x"
                }
                if (numCells == 4) {
                    num4boatP--
                    b.tf4boat.text = num4boatP.toString() + "x"
                }
                if (numCells == 5) {
                    num5boatP--
                    b.tf5boat.text = num5boatP.toString() + "x"
                }

                resetVariablesAI()
            }
        }

        //Verify 2+Square Boat Horizontal
        if (presentDirection == RIGHT || presentDirection == LEFT) {

            if (lastHitCollumn-1 != -1){
                if(boardPlayer[lastHitRow][lastHitCollumn-1] == "S")
                    l = false
            }

            if(firstHitCollumn+1 != 10){
                if(boardPlayer[firstHitRow][firstHitCollumn+1] == "S")
                    r = false
            }

            if (lastHitCollumn+1 != 10){
                if(boardPlayer[lastHitRow][lastHitCollumn+1] == "S")
                    r = false
            }

            if(firstHitCollumn-1 != -1){
                if(boardPlayer[firstHitRow][firstHitCollumn-1] == "S")
                    l = false
            }


            if (l && r) {
                for (i in lastHitCollumn downTo 0) {
                    if (boardPlayer[lastHitRow][i] == "SX")
                        numCells++
                    if (boardPlayer[lastHitRow][i] == "X" || boardPlayer[lastHitRow][i] == "")
                        break
                }

                for (i in lastHitCollumn..9) {
                    if (boardPlayer[lastHitRow][i] == "SX")
                        numCells++
                    if (boardPlayer[lastHitRow][i] == "X" || boardPlayer[lastHitRow][i] == "")
                        break
                }

                numCells--
                toast = Toast.makeText(applicationContext, "Enemy Destroyed a Boat! (Size '$numCells')", Toast.LENGTH_LONG)
                toast.show()

                if (numCells == 2) {
                    num2boatP--
                    b.tf2boat.text = num2boatP.toString() + "x"
                }
                if (numCells == 3){
                    num3boatP--
                    b.tf3boat.text = num3boatP.toString() + "x"
                }
                if (numCells == 4) {
                    num4boatP--
                    b.tf4boat.text = num4boatP.toString() + "x"
                }
                if (numCells == 5) {
                    num5boatP--
                    b.tf5boat.text = num5boatP.toString() + "x"
                }

                resetVariablesAI()
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun checkWinner(): Boolean {
         lateinit var msgDisplayed : String

         //Check if there's a winner, build msg to be displayed and increment the points of the winner
         if(remainingSpotsP == 0 || remainingSpotsAI == 0){
             val extras = Bundle()
             if(remainingSpotsAI == 0){
                 extras.putInt("numWinsP", intent.extras!!.getInt("numWinsP")+1)
                 extras.putInt("numWinsAI", intent.extras!!.getInt("numWinsAI"))
                 msgDisplayed = "Congratulations Sailor you won!\nMay the newly conquered seas serve you well!"
             }
             if(remainingSpotsP == 0){
                 extras.putInt("numWinsP", intent.extras!!.getInt("numWinsP"))
                 extras.putInt("numWinsAI", intent.extras!!.getInt("numWinsAI")+1)
                 msgDisplayed = "Unfortunately all your naval fleet is down!\nThe enemy occupied the seas that once you fought for!"
             }

             //Build the Alert Dialogue
             val dialogBuilder = AlertDialog.Builder(this)
             // set message of alert dialog
             dialogBuilder.setMessage(msgDisplayed).setCancelable(false)
                 .setNegativeButton("Back Home!") { _, _ ->
                     keepPlayingMusic = true
                     MusicPlayer.StopSoundGame()
                     MusicPlayer.resetSoundGame()
                     MusicPlayer.PlaySoundGame(this.applicationContext)

                     startActivity(Intent(this, HomeScreen::class.java))
                 }
                 .setPositiveButton("Play Again!") { _, _ ->
                     keepPlayingMusic = true
                     MusicPlayer.StopSoundGame()
                     MusicPlayer.resetSoundGame()
                     MusicPlayer.PlaySoundGame(this.applicationContext)

                     val intent = Intent(this, PickBoard::class.java)
                     intent.putExtras(extras)
                     startActivity(intent)
                 }

             // create dialog box
             val alert = dialogBuilder.create()
             // set title for alert dialog box
             alert.setTitle("War is Over")
             alert.setIcon(getDrawable(R.drawable.battleship))
             // show alert dialog
             alert.show()
             return true
         }else
             return false
    }

    private fun resetVariablesAI() {
        firstHit = false
        secondHit = false
        lastHitCollumn = 0
        lastHitRow = 0
        firstHitRow = 0
        firstHitCollumn = 0
        presentDirection = ""
        up = true
        down = true
        right = true
        letf = true
    }

    @SuppressLint("ClickableViewAccessibility", "UseCompatLoadingForDrawables", "SetTextI18n")
    private fun activateListeners() {

        //1ª ROW
        b.btn00.setOnClickListener {

            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn00.isClickable = false
                verifiyHitAI(0, 0)
            }
        }

        b.btn01.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn01.isClickable = false
                verifiyHitAI(0, 1)
            }
        }

        b.btn02.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn02.isClickable = false
                verifiyHitAI(0, 2)
            }
        }

        b.btn03.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn03.isClickable = false
                verifiyHitAI(0, 3)
            }
        }

        b.btn04.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn04.isClickable = false
                verifiyHitAI(0, 4)
            }
        }

        b.btn05.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn05.isClickable = false
                verifiyHitAI(0, 5)
            }
        }

        b.btn06.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn06.isClickable = false
                verifiyHitAI(0, 6)
            }
        }

        b.btn07.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn07.isClickable = false
                verifiyHitAI(0, 7)
            }
        }

        b.btn08.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn08.isClickable = false
                verifiyHitAI(0, 8)
            }
        }

        b.btn09.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn09.isClickable = false
                verifiyHitAI(0, 9)
            }
        }


        //2ª ROW
        b.btn10.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn10.isClickable = false
                verifiyHitAI(1, 0)
            }
        }

        b.btn11.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn11.isClickable = false
                verifiyHitAI(1, 1)
            }
        }

        b.btn12.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn12.isClickable = false
                verifiyHitAI(1, 2)
            }
        }

        b.btn13.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn13.isClickable = false
                verifiyHitAI(1, 3)
            }
        }

        b.btn14.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn14.isClickable = false
                verifiyHitAI(1, 4)
            }
        }

        b.btn15.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn15.isClickable = false
                verifiyHitAI(1, 5)
            }
        }

        b.btn16.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn16.isClickable = false
                verifiyHitAI(1, 6)
            }
        }

        b.btn17.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn17.isClickable = false
                verifiyHitAI(1, 7)
            }
        }

        b.btn18.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn18.isClickable = false
                verifiyHitAI(1, 8)
            }
        }

        b.btn19.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn19.isClickable = false
                verifiyHitAI(1, 9)
            }
        }


        //3ª ROW
        b.btn20.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn20.isClickable = false
                verifiyHitAI(2, 0)
            }
        }

        b.btn21.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn21.isClickable = false
                verifiyHitAI(2, 1)
            }
        }

        b.btn22.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn22.isClickable = false
                verifiyHitAI(2, 2)
            }
        }

        b.btn23.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn23.isClickable = false
                verifiyHitAI(2, 3)
            }
        }

        b.btn24.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn24.isClickable = false
                verifiyHitAI(2, 4)
            }
        }

        b.btn25.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn25.isClickable = false
                verifiyHitAI(2, 5)
            }
        }

        b.btn26.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn26.isClickable = false
                verifiyHitAI(2, 6)
            }
        }

        b.btn27.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn27.isClickable = false
                verifiyHitAI(2, 7)
            }
        }

        b.btn28.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn28.isClickable = false
                verifiyHitAI(2, 8)
            }
        }

        b.btn29.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn29.isClickable = false
                verifiyHitAI(2, 9)
            }
        }


        //4ª ROW
        b.btn30.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn30.isClickable = false
                verifiyHitAI(3, 0)
            }
        }

        b.btn31.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn31.isClickable = false
                verifiyHitAI(3, 1)
            }
        }

        b.btn32.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn32.isClickable = false
                verifiyHitAI(3, 2)
            }
        }

        b.btn33.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn33.isClickable = false
                verifiyHitAI(3, 3)
            }
        }

        b.btn34.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn34.isClickable = false
                verifiyHitAI(3, 4)
            }
        }

        b.btn35.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn35.isClickable = false
                verifiyHitAI(3, 5)
            }
        }

        b.btn36.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn36.isClickable = false
                verifiyHitAI(3, 6)
            }
        }

        b.btn37.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn37.isClickable = false
                verifiyHitAI(3, 7)
            }
        }

        b.btn38.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn38.isClickable = false
                verifiyHitAI(3, 8)
            }
        }

        b.btn39.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn39.isClickable = false
                verifiyHitAI(3, 9)
            }
        }


        //5ª ROW
        b.btn40.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn40.isClickable = false
                verifiyHitAI(4, 0)
            }
        }

        b.btn41.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn41.isClickable = false
                verifiyHitAI(4, 1)
            }
        }

        b.btn42.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn42.isClickable = false
                verifiyHitAI(4, 2)
            }
        }

        b.btn43.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn43.isClickable = false
                verifiyHitAI(4, 3)
            }
        }

        b.btn44.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn44.isClickable = false
                verifiyHitAI(4, 4)
            }
        }

        b.btn45.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn45.isClickable = false
                verifiyHitAI(4, 5)
            }
        }

        b.btn46.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn46.isClickable = false
                verifiyHitAI(4, 6)
            }
        }

        b.btn47.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn47.isClickable = false
                verifiyHitAI(4, 7)
            }
        }

        b.btn48.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn48.isClickable = false
                verifiyHitAI(4, 8)
            }
        }

        b.btn49.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn49.isClickable = false
                verifiyHitAI(4, 9)
            }
        }


        //6ª ROW
        b.btn50.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn50.isClickable = false
                verifiyHitAI(5, 0)
            }
        }

        b.btn51.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn51.isClickable = false
                verifiyHitAI(5, 1)
            }
        }

        b.btn52.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn52.isClickable = false
                verifiyHitAI(5, 2)
            }
        }

        b.btn53.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn53.isClickable = false
                verifiyHitAI(5, 3)
            }
        }

        b.btn54.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn54.isClickable = false
                verifiyHitAI(5, 4)
            }
        }

        b.btn55.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn55.isClickable = false
                verifiyHitAI(5, 5)
            }
        }

        b.btn56.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn56.isClickable = false
                verifiyHitAI(5, 6)
            }
        }

        b.btn57.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn57.isClickable = false
                verifiyHitAI(5, 7)
            }
        }

        b.btn58.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn58.isClickable = false
                verifiyHitAI(5, 8)
            }
        }

        b.btn59.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn59.isClickable = false
                verifiyHitAI(5, 9)
            }
        }


        //7ª ROW
        b.btn60.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn60.isClickable = false
                verifiyHitAI(6, 0)
            }
        }

        b.btn61.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn61.isClickable = false
                verifiyHitAI(6, 1)
            }
        }

        b.btn62.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn62.isClickable = false
                verifiyHitAI(6, 2)
            }
        }

        b.btn63.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn63.isClickable = false
                verifiyHitAI(6, 3)
            }
        }

        b.btn64.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn64.isClickable = false
                verifiyHitAI(6, 4)
            }
        }

        b.btn65.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn65.isClickable = false
                verifiyHitAI(6, 5)
            }
        }

        b.btn66.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn66.isClickable = false
                verifiyHitAI(6, 6)
            }
        }

        b.btn67.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn67.isClickable = false
                verifiyHitAI(6, 7)
            }
        }

        b.btn68.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn68.isClickable = false
                verifiyHitAI(6, 8)
            }
        }

        b.btn69.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn69.isClickable = false
                verifiyHitAI(6, 9)
            }
        }


        //8ª ROW
        b.btn70.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn70.isClickable = false
                verifiyHitAI(7, 0)
            }
        }

        b.btn71.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn71.isClickable = false
                verifiyHitAI(7, 1)
            }
        }

        b.btn72.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn72.isClickable = false
                verifiyHitAI(7, 2)
            }
        }

        b.btn73.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn73.isClickable = false
                verifiyHitAI(7, 3)
            }
        }

        b.btn74.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn74.isClickable = false
                verifiyHitAI(7, 4)
            }
        }

        b.btn75.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn75.isClickable = false
                verifiyHitAI(7, 5)
            }
        }

        b.btn76.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn76.isClickable = false
                verifiyHitAI(7, 6)
            }
        }

        b.btn77.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn77.isClickable = false
                verifiyHitAI(7, 7)
            }
        }

        b.btn78.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn78.isClickable = false
                verifiyHitAI(7, 8)
            }
        }

        b.btn79.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn79.isClickable = false
                verifiyHitAI(7, 9)
            }
        }


        //9ª ROW
        b.btn80.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn80.isClickable = false
                verifiyHitAI(8, 0)
            }
        }

        b.btn81.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn81.isClickable = false
                verifiyHitAI(8, 1)
            }
        }

        b.btn82.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn82.isClickable = false
                verifiyHitAI(8, 2)
            }
        }

        b.btn83.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn83.isClickable = false
                verifiyHitAI(8, 3)
            }
        }

        b.btn84.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn84.isClickable = false
                verifiyHitAI(8, 4)
            }
        }

        b.btn85.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn85.isClickable = false
                verifiyHitAI(8, 5)
            }
        }

        b.btn86.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn86.isClickable = false
                verifiyHitAI(8, 6)
            }
        }

        b.btn87.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn87.isClickable = false
                verifiyHitAI(8, 7)
            }
        }

        b.btn88.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn88.isClickable = false
                verifiyHitAI(8, 8)
            }
        }

        b.btn89.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn89.isClickable = false
                verifiyHitAI(8, 9)
            }
        }


        //10º ROW
        b.btn90.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn90.isClickable = false
                verifiyHitAI(9, 0)
            }
        }

        b.btn91.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn91.isClickable = false
                verifiyHitAI(9, 1)
            }
        }

        b.btn92.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn92.isClickable = false
                verifiyHitAI(9, 2)
            }
        }

        b.btn93.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn93.isClickable = false
                verifiyHitAI(9, 3)
            }
        }

        b.btn94.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn94.isClickable = false
                verifiyHitAI(9, 4)
            }
        }

        b.btn95.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn95.isClickable = false
                verifiyHitAI(9, 5)
            }
        }

        b.btn96.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn96.isClickable = false
                verifiyHitAI(9, 6)
            }
        }

        b.btn97.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn97.isClickable = false
                verifiyHitAI(9, 7)
            }
        }

        b.btn98.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn98.isClickable = false
                verifiyHitAI(9, 8)
            }
        }

        b.btn99.setOnClickListener {
            if (actualP == PLAYER && !spamClickerBlocker && !scouting) {
                spamClickerBlocker = true
                b.btn99.isClickable = false
                verifiyHitAI(9, 9)
            }
        }

        b.btnScout.setOnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_UP -> {
                    clearBoard()
                    showBoardAI()
                    b.tf5boat.text = num5boatAI.toString() + "x"
                    b.tf4boat.text = num4boatAI.toString() + "x"
                    b.tf3boat.text = num3boatAI.toString() + "x"
                    b.tf2boat.text = num2boatAI.toString() + "x"
                    b.tf1boat.text = num1boatAI.toString() + "x"
                    b.tfBoats.text = "Enemy Boats Left:"
                    b.tfBoats.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                        setMargins(0, 0, 0, 0) //parameters are in pixel
                    }
                    scouting = false
                }
                MotionEvent.ACTION_DOWN -> {
                    scouting = true
                    clearBoard()
                    showBoardPlayer()
                    b.tf5boat.text = num5boatP.toString() + "x"
                    b.tf4boat.text = num4boatP.toString() + "x"
                    b.tf3boat.text = num3boatP.toString() + "x"
                    b.tf2boat.text = num2boatP.toString() + "x"
                    b.tf1boat.text = num1boatP.toString() + "x"
                    b.tfBoats.text = "Boats Left:"
                    b.tfBoats.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                        setMargins(0, 0, 150, 0) //parameters are in pixel
                    }
                }
            }
            v?.onTouchEvent(event) ?: true
        }

        b.btnInfo.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(this)
            // set message of alert dialog
            dialogBuilder.setMessage(
                "Welcome aboard sailor!" +
                        "\n\nBattleShip is a turn based game, where you must destroy all your enemy's ships, that are placed(fixed) along the board!" +
                        "\n\n\n\nHere goes some information, so you don't get lost, there's plenty of sea out there!" +
                        "\n\n1 - You're the one to shoot first, use it wisely!" +
                        "\n\n2 - The displayed board will always be, the opposing player's board, so you can hit him, this means, if it's your turn then the displayed board will be from the AI, and when it's AI's turn your board will be displayed showing the ships placement that you chose before starting the game!" +
                        "\n\n3 - By clicking on one of the squares of the board, you will shoot your opponent (only once per turn), if its a hit, that same square you clicked will turn grey, and a 'X' shall appear in it, if isn't a hit, only an 'X' shall emerge and the square will remain blue!" +
                        "\n\n4 - The first player to destroy all his enemy's boats wins! And takes all the glory home too!" +
                        "\n\n\n\nWhat about the the button with the '⚓' you ask? Curious sailor we got here!" +
                        "\n\nEnchanted by sirens, the magical button lets you take a look at your own board and remaining ships you got, as long as you keep pressing it!" +
                        "\n\n\n\nGood luck out there sailor, may mighty Poseidon's bravery be at your side! \uD83D\uDD31 \n\n\n"
            )
                .setCancelable(false)
                .setNegativeButton("Got it, let's sail! ⚔️") { dialog, _ ->
                    dialog.cancel()
                }

            // create dialog box
            val alert = dialogBuilder.create()
            // set title for alert dialog box
            alert.setTitle("How to play")
            alert.setIcon(getDrawable(R.drawable.battleship))
            // show alert dialog
            alert.show()
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBackPressed() {
        val dialogBuilder = AlertDialog.Builder(this)
        // set message of alert dialog
        dialogBuilder.setMessage("All your game progress will be lost!").setCancelable(false)
            .setNegativeButton("No!") { dialog, _ ->
                dialog.cancel()
            }
            .setPositiveButton("Yes!") { _, _ ->
                keepPlayingMusic = true
                MusicPlayer.StopSoundGame()
                MusicPlayer.resetSoundGame()
                MusicPlayer.PlaySoundGame(this.applicationContext)
                startActivity(Intent(this, HomeScreen::class.java))
            }

        // create dialog box
        val alert = dialogBuilder.create()
        // set title for alert dialog box
        alert.setTitle("Confirm Retreat?")
        alert.setIcon(getDrawable(R.drawable.battleship))
        // show alert dialog
        alert.show()
    }

    @SuppressLint("ResourceAsColor")
    private fun showBoardPlayer() {

        //1º ROW
        if (boardPlayer[0][0] == "") {
            b.btn00.text = ""
            b.btn00.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[0][0] == "X") {
            b.btn00.text = "X"
            b.btn00.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[0][0] == "S") {
            b.btn00.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[0][0] == "SX") {
            b.btn00.text = "X"
            b.btn00.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[0][1] == "") {
            b.btn01.text = ""
            b.btn01.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[0][1] == "X") {
            b.btn01.text = "X"
            b.btn01.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[0][1] == "S") {
            b.btn01.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[0][1] == "SX") {
            b.btn01.text = "X"
            b.btn01.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[0][2] == "") {
            b.btn02.text = ""
            b.btn02.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[0][2] == "X") {
            b.btn02.text = "X"
            b.btn02.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[0][2] == "S") {
            b.btn02.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[0][2] == "SX") {
            b.btn02.text = "X"
            b.btn02.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[0][3] == "") {
            b.btn03.text = ""
            b.btn03.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[0][3] == "X") {
            b.btn03.text = "X"
            b.btn03.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[0][3] == "S") {
            b.btn03.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[0][3] == "SX") {
            b.btn03.text = "X"
            b.btn03.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[0][4] == "") {
            b.btn04.text = ""
            b.btn04.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[0][4] == "X") {
            b.btn04.text = "X"
            b.btn04.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[0][4] == "S") {
            b.btn04.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[0][4] == "SX") {
            b.btn04.text = "X"
            b.btn04.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[0][5] == "") {
            b.btn05.text = ""
            b.btn05.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[0][5] == "X") {
            b.btn05.text = "X"
            b.btn05.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[0][5] == "S") {
            b.btn05.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[0][5] == "SX") {
            b.btn05.text = "X"
            b.btn05.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[0][6] == "") {
            b.btn06.text = ""
            b.btn06.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[0][6] == "X") {
            b.btn06.text = "X"
            b.btn06.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[0][6] == "S") {
            b.btn06.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[0][6] == "SX") {
            b.btn06.text = "X"
            b.btn06.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[0][7] == "") {
            b.btn07.text = ""
            b.btn07.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[0][7] == "X") {
            b.btn07.text = "X"
            b.btn07.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[0][7] == "S") {
            b.btn07.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[0][7] == "SX") {
            b.btn07.text = "X"
            b.btn07.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[0][8] == "") {
            b.btn08.text = ""
            b.btn08.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[0][8] == "X") {
            b.btn08.text = "X"
            b.btn08.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[0][8] == "S") {
            b.btn08.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[0][8] == "SX") {
            b.btn08.text = "X"
            b.btn08.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[0][9] == "") {
            b.btn09.text = ""
            b.btn09.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[0][9] == "X") {
            b.btn09.text = "X"
            b.btn09.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[0][9] == "S") {
            b.btn09.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[0][9] == "SX") {
            b.btn09.text = "X"
            b.btn09.setBackgroundColor(Color.GRAY)
        }


        //2º ROW
        if (boardPlayer[1][0] == "") {
            b.btn10.text = ""
            b.btn10.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[1][0] == "X") {
            b.btn10.text = "X"
            b.btn10.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[1][0] == "S") {
            b.btn10.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[1][0] == "SX") {
            b.btn10.text = "X"
            b.btn10.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[1][1] == "") {
            b.btn11.text = ""
            b.btn11.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[1][1] == "X") {
            b.btn11.text = "X"
            b.btn11.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[1][1] == "S") {
            b.btn11.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[1][1] == "SX") {
            b.btn11.text = "X"
            b.btn11.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[1][2] == "") {
            b.btn12.text = ""
            b.btn12.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[1][2] == "X") {
            b.btn12.text = "X"
            b.btn12.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[1][2] == "S") {
            b.btn12.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[1][2] == "SX") {
            b.btn12.text = "X"
            b.btn12.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[1][3] == "") {
            b.btn13.text = ""
            b.btn13.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[1][3] == "X") {
            b.btn13.text = "X"
            b.btn13.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[1][3] == "S") {
            b.btn13.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[1][3] == "SX") {
            b.btn13.text = "X"
            b.btn13.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[1][4] == "") {
            b.btn14.text = ""
            b.btn14.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[1][4] == "X") {
            b.btn14.text = "X"
            b.btn14.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[1][4] == "S") {
            b.btn14.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[1][4] == "SX") {
            b.btn14.text = "X"
            b.btn14.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[1][5] == "") {
            b.btn15.text = ""
            b.btn15.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[1][5] == "X") {
            b.btn15.text = "X"
            b.btn15.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[1][5] == "S") {
            b.btn15.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[1][5] == "SX") {
            b.btn15.text = "X"
            b.btn15.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[1][6] == "") {
            b.btn16.text = ""
            b.btn16.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[1][6] == "X") {
            b.btn16.text = "X"
            b.btn16.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[1][6] == "S") {
            b.btn16.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[1][6] == "SX") {
            b.btn16.text = "X"
            b.btn16.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[1][7] == "") {
            b.btn17.text = ""
            b.btn17.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[1][7] == "X") {
            b.btn17.text = "X"
            b.btn17.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[1][7] == "S") {
            b.btn17.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[1][7] == "SX") {
            b.btn17.text = "X"
            b.btn17.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[1][8] == "") {
            b.btn18.text = ""
            b.btn18.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[1][8] == "X") {
            b.btn18.text = "X"
            b.btn18.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[1][8] == "S") {
            b.btn18.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[1][8] == "SX") {
            b.btn18.text = "X"
            b.btn18.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[1][9] == "") {
            b.btn19.text = ""
            b.btn19.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[1][9] == "X") {
            b.btn19.text = "X"
            b.btn19.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[1][9] == "S") {
            b.btn19.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[1][9] == "SX") {
            b.btn19.text = "X"
            b.btn19.setBackgroundColor(Color.GRAY)
        }


        //3º ROW
        if (boardPlayer[2][0] == "") {
            b.btn20.text = ""
            b.btn20.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[2][0] == "X") {
            b.btn20.text = "X"
            b.btn20.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[2][0] == "S") {
            b.btn20.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[2][0] == "SX") {
            b.btn20.text = "X"
            b.btn20.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[2][1] == "") {
            b.btn21.text = ""
            b.btn21.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[2][1] == "X") {
            b.btn21.text = "X"
            b.btn21.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[2][1] == "S") {
            b.btn21.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[2][1] == "SX") {
            b.btn21.text = "X"
            b.btn21.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[2][2] == "") {
            b.btn22.text = ""
            b.btn22.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[2][2] == "X") {
            b.btn22.text = "X"
            b.btn22.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[2][2] == "S") {
            b.btn22.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[2][2] == "SX") {
            b.btn22.text = "X"
            b.btn22.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[2][3] == "") {
            b.btn23.text = ""
            b.btn23.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[2][3] == "X") {
            b.btn23.text = "X"
            b.btn23.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[2][3] == "S") {
            b.btn23.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[2][3] == "SX") {
            b.btn23.text = "X"
            b.btn23.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[2][4] == "") {
            b.btn24.text = ""
            b.btn24.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[2][4] == "X") {
            b.btn24.text = "X"
            b.btn24.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[2][4] == "S") {
            b.btn24.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[2][4] == "SX") {
            b.btn24.text = "X"
            b.btn24.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[2][5] == "") {
            b.btn25.text = ""
            b.btn25.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[2][5] == "X") {
            b.btn25.text = "X"
            b.btn25.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[2][5] == "S") {
            b.btn25.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[2][5] == "SX") {
            b.btn25.text = "X"
            b.btn25.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[2][6] == "") {
            b.btn26.text = ""
            b.btn26.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[2][6] == "X") {
            b.btn26.text = "X"
            b.btn26.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[2][6] == "S") {
            b.btn26.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[2][6] == "SX") {
            b.btn26.text = "X"
            b.btn26.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[2][7] == "") {
            b.btn27.text = ""
            b.btn27.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[2][7] == "X") {
            b.btn27.text = "X"
            b.btn27.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[2][7] == "S") {
            b.btn27.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[2][7] == "SX") {
            b.btn27.text = "X"
            b.btn27.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[2][8] == "") {
            b.btn28.text = ""
            b.btn28.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[2][8] == "X") {
            b.btn28.text = "X"
            b.btn28.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[2][8] == "S") {
            b.btn28.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[2][8] == "SX") {
            b.btn28.text = "X"
            b.btn28.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[2][9] == "") {
            b.btn29.text = ""
            b.btn29.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[2][9] == "X") {
            b.btn29.text = "X"
            b.btn29.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[2][9] == "S") {
            b.btn29.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[2][9] == "SX") {
            b.btn29.text = "X"
            b.btn29.setBackgroundColor(Color.GRAY)
        }


        //4ºROW
        if (boardPlayer[3][0] == "") {
            b.btn30.text = ""
            b.btn30.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[3][0] == "X") {
            b.btn30.text = "X"
            b.btn30.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[3][0] == "S") {
            b.btn30.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[3][0] == "SX") {
            b.btn30.text = "X"
            b.btn30.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[3][1] == "") {
            b.btn31.text = ""
            b.btn31.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[3][1] == "X") {
            b.btn31.text = "X"
            b.btn31.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[3][1] == "S") {
            b.btn31.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[3][1] == "SX") {
            b.btn31.text = "X"
            b.btn31.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[3][2] == "") {
            b.btn32.text = ""
            b.btn32.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[3][2] == "X") {
            b.btn32.text = "X"
            b.btn32.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[3][2] == "S") {
            b.btn32.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[3][2] == "SX") {
            b.btn32.text = "X"
            b.btn32.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[3][3] == "") {
            b.btn33.text = ""
            b.btn33.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[3][3] == "X") {
            b.btn33.text = "X"
            b.btn33.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[3][3] == "S") {
            b.btn33.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[3][3] == "SX") {
            b.btn33.text = "X"
            b.btn33.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[3][4] == "") {
            b.btn34.text = ""
            b.btn34.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[3][4] == "X") {
            b.btn34.text = "X"
            b.btn34.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[3][4] == "S") {
            b.btn34.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[3][4] == "SX") {
            b.btn34.text = "X"
            b.btn34.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[3][5] == "") {
            b.btn35.text = ""
            b.btn35.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[3][5] == "X") {
            b.btn35.text = "X"
            b.btn35.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[3][5] == "S") {
            b.btn35.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[3][5] == "SX") {
            b.btn35.text = "X"
            b.btn35.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[3][6] == "") {
            b.btn36.text = ""
            b.btn36.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[3][6] == "X") {
            b.btn36.text = "X"
            b.btn36.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[3][6] == "S") {
            b.btn36.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[3][6] == "SX") {
            b.btn36.text = "X"
            b.btn36.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[3][7] == "") {
            b.btn37.text = ""
            b.btn37.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[3][7] == "X") {
            b.btn37.text = "X"
            b.btn37.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[3][7] == "S") {
            b.btn37.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[3][7] == "SX") {
            b.btn37.text = "X"
            b.btn37.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[3][8] == "") {
            b.btn38.text = ""
            b.btn38.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[3][8] == "X") {
            b.btn38.text = "X"
            b.btn38.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[3][8] == "S") {
            b.btn38.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[3][8] == "SX") {
            b.btn38.text = "X"
            b.btn38.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[3][9] == "") {
            b.btn39.text = ""
            b.btn39.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[3][9] == "X") {
            b.btn39.text = "X"
            b.btn39.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[3][9] == "S") {
            b.btn39.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[3][9] == "SX") {
            b.btn39.text = "X"
            b.btn39.setBackgroundColor(Color.GRAY)
        }


        //5º ROW
        if (boardPlayer[4][0] == "") {
            b.btn40.text = ""
            b.btn40.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[4][0] == "X") {
            b.btn40.text = "X"
            b.btn40.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[4][0] == "S") {
            b.btn40.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[4][0] == "SX") {
            b.btn40.text = "X"
            b.btn40.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[4][1] == "") {
            b.btn41.text = ""
            b.btn41.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[4][1] == "X") {
            b.btn41.text = "X"
            b.btn41.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[4][1] == "S") {
            b.btn41.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[4][1] == "SX") {
            b.btn41.text = "X"
            b.btn41.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[4][2] == "") {
            b.btn42.text = ""
            b.btn42.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[4][2] == "X") {
            b.btn42.text = "X"
            b.btn42.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[4][2] == "S") {
            b.btn42.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[4][2] == "SX") {
            b.btn42.text = "X"
            b.btn42.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[4][3] == "") {
            b.btn43.text = ""
            b.btn43.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[4][3] == "X") {
            b.btn43.text = "X"
            b.btn43.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[4][3] == "S") {
            b.btn43.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[4][3] == "SX") {
            b.btn43.text = "X"
            b.btn43.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[4][4] == "") {
            b.btn44.text = ""
            b.btn44.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[4][4] == "X") {
            b.btn44.text = "X"
            b.btn44.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[4][4] == "S") {
            b.btn44.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[4][4] == "SX") {
            b.btn44.text = "X"
            b.btn44.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[4][5] == "") {
            b.btn45.text = ""
            b.btn45.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[4][5] == "X") {
            b.btn45.text = "X"
            b.btn45.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[4][5] == "S") {
            b.btn45.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[4][5] == "SX") {
            b.btn45.text = "X"
            b.btn45.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[4][6] == "") {
            b.btn46.text = ""
            b.btn46.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[4][6] == "X") {
            b.btn46.text = "X"
            b.btn46.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[4][6] == "S") {
            b.btn46.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[4][6] == "SX") {
            b.btn46.text = "X"
            b.btn46.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[4][7] == "") {
            b.btn47.text = ""
            b.btn47.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[4][7] == "X") {
            b.btn47.text = "X"
            b.btn47.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[4][7] == "S") {
            b.btn47.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[4][7] == "SX") {
            b.btn47.text = "X"
            b.btn47.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[4][8] == "") {
            b.btn48.text = ""
            b.btn48.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[4][8] == "X") {
            b.btn48.text = "X"
            b.btn48.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[4][8] == "S") {
            b.btn48.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[4][8] == "SX") {
            b.btn48.text = "X"
            b.btn48.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[4][9] == "") {
            b.btn49.text = ""
            b.btn49.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[4][9] == "X") {
            b.btn49.text = "X"
            b.btn49.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[4][9] == "S") {
            b.btn49.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[4][9] == "SX") {
            b.btn49.text = "X"
            b.btn49.setBackgroundColor(Color.GRAY)
        }


        //6º ROW
        if (boardPlayer[5][0] == "") {
            b.btn50.text = ""
            b.btn50.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[5][0] == "X") {
            b.btn50.text = "X"
            b.btn50.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[5][0] == "S") {
            b.btn50.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[5][0] == "SX") {
            b.btn50.text = "X"
            b.btn50.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[5][1] == "") {
            b.btn51.text = ""
            b.btn51.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[5][1] == "X") {
            b.btn51.text = "X"
            b.btn51.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[5][1] == "S") {
            b.btn51.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[5][1] == "SX") {
            b.btn51.text = "X"
            b.btn51.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[5][2] == "") {
            b.btn52.text = ""
            b.btn52.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[5][2] == "X") {
            b.btn52.text = "X"
            b.btn52.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[5][2] == "S") {
            b.btn52.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[5][2] == "SX") {
            b.btn52.text = "X"
            b.btn52.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[5][3] == "") {
            b.btn53.text = ""
            b.btn53.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[5][3] == "X") {
            b.btn53.text = "X"
            b.btn53.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[5][3] == "S") {
            b.btn53.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[5][3] == "SX") {
            b.btn53.text = "X"
            b.btn53.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[5][4] == "") {
            b.btn54.text = ""
            b.btn54.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[5][4] == "X") {
            b.btn54.text = "X"
            b.btn54.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[5][4] == "S") {
            b.btn54.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[5][4] == "SX") {
            b.btn54.text = "X"
            b.btn54.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[5][5] == "") {
            b.btn55.text = ""
            b.btn55.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[5][5] == "X") {
            b.btn55.text = "X"
            b.btn55.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[5][5] == "S") {
            b.btn55.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[5][5] == "SX") {
            b.btn55.text = "X"
            b.btn55.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[5][6] == "") {
            b.btn56.text = ""
            b.btn56.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[5][6] == "X") {
            b.btn56.text = "X"
            b.btn56.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[5][6] == "S") {
            b.btn56.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[5][6] == "SX") {
            b.btn56.text = "X"
            b.btn56.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[5][7] == "") {
            b.btn57.text = ""
            b.btn57.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[5][7] == "X") {
            b.btn57.text = "X"
            b.btn57.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[5][7] == "S") {
            b.btn57.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[5][7] == "SX") {
            b.btn57.text = "X"
            b.btn57.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[5][8] == "") {
            b.btn58.text = ""
            b.btn58.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[5][8] == "X") {
            b.btn58.text = "X"
            b.btn58.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[5][8] == "S") {
            b.btn58.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[5][8] == "SX") {
            b.btn58.text = "X"
            b.btn58.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[5][9] == "") {
            b.btn59.text = ""
            b.btn59.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[5][9] == "X") {
            b.btn59.text = "X"
            b.btn59.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[5][9] == "S") {
            b.btn59.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[5][9] == "SX") {
            b.btn59.text = "X"
            b.btn59.setBackgroundColor(Color.GRAY)
        }


        //7ºROW
        if (boardPlayer[6][0] == "") {
            b.btn60.text = ""
            b.btn60.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[6][0] == "X") {
            b.btn60.text = "X"
            b.btn60.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[6][0] == "S") {
            b.btn60.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[6][0] == "SX") {
            b.btn60.text = "X"
            b.btn60.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[6][1] == "") {
            b.btn61.text = ""
            b.btn61.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[6][1] == "X") {
            b.btn61.text = "X"
            b.btn61.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[6][1] == "S") {
            b.btn61.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[6][1] == "SX") {
            b.btn61.text = "X"
            b.btn61.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[6][2] == "") {
            b.btn62.text = ""
            b.btn62.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[6][2] == "X") {
            b.btn62.text = "X"
            b.btn62.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[6][2] == "S") {
            b.btn62.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[6][2] == "SX") {
            b.btn62.text = "X"
            b.btn62.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[6][3] == "") {
            b.btn63.text = ""
            b.btn63.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[6][3] == "X") {
            b.btn63.text = "X"
            b.btn63.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[6][3] == "S") {
            b.btn63.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[6][3] == "SX") {
            b.btn63.text = "X"
            b.btn63.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[6][4] == "") {
            b.btn64.text = ""
            b.btn64.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[6][4] == "X") {
            b.btn64.text = "X"
            b.btn64.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[6][4] == "S") {
            b.btn64.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[6][4] == "SX") {
            b.btn64.text = "X"
            b.btn64.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[6][5] == "") {
            b.btn65.text = ""
            b.btn65.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[6][5] == "X") {
            b.btn65.text = "X"
            b.btn65.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[6][5] == "S") {
            b.btn65.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[6][5] == "SX") {
            b.btn65.text = "X"
            b.btn65.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[6][6] == "") {
            b.btn66.text = ""
            b.btn66.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[6][6] == "X") {
            b.btn66.text = "X"
            b.btn66.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[6][6] == "S") {
            b.btn66.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[6][6] == "SX") {
            b.btn66.text = "X"
            b.btn66.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[6][7] == "") {
            b.btn67.text = ""
            b.btn67.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[6][7] == "X") {
            b.btn67.text = "X"
            b.btn67.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[6][7] == "S") {
            b.btn67.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[6][7] == "SX") {
            b.btn67.text = "X"
            b.btn67.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[6][8] == "") {
            b.btn68.text = ""
            b.btn68.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[6][8] == "X") {
            b.btn68.text = "X"
            b.btn68.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[6][8] == "S") {
            b.btn68.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[6][8] == "SX") {
            b.btn68.text = "X"
            b.btn68.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[6][9] == "") {
            b.btn69.text = ""
            b.btn69.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[6][9] == "X") {
            b.btn69.text = "X"
            b.btn69.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[6][9] == "S") {
            b.btn69.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[6][9] == "SX") {
            b.btn69.text = "X"
            b.btn69.setBackgroundColor(Color.GRAY)
        }


        //8º ROW
        if (boardPlayer[7][0] == "") {
            b.btn70.text = ""
            b.btn70.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[7][0] == "X") {
            b.btn70.text = "X"
            b.btn70.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[7][0] == "S") {
            b.btn70.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[7][0] == "SX") {
            b.btn70.text = "X"
            b.btn70.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[7][1] == "") {
            b.btn71.text = ""
            b.btn71.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[7][1] == "X") {
            b.btn71.text = "X"
            b.btn71.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[7][1] == "S") {
            b.btn71.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[7][1] == "SX") {
            b.btn71.text = "X"
            b.btn71.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[7][2] == "") {
            b.btn72.text = ""
            b.btn72.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[7][2] == "X") {
            b.btn72.text = "X"
            b.btn72.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[7][2] == "S") {
            b.btn72.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[7][2] == "SX") {
            b.btn72.text = "X"
            b.btn72.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[7][3] == "") {
            b.btn73.text = ""
            b.btn73.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[7][3] == "X") {
            b.btn73.text = "X"
            b.btn73.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[7][3] == "S") {
            b.btn73.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[7][3] == "SX") {
            b.btn73.text = "X"
            b.btn73.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[7][4] == "") {
            b.btn74.text = ""
            b.btn74.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[7][4] == "X") {
            b.btn74.text = "X"
            b.btn74.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[7][4] == "S") {
            b.btn74.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[7][4] == "SX") {
            b.btn74.text = "X"
            b.btn74.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[7][5] == "") {
            b.btn75.text = ""
            b.btn75.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[7][5] == "X") {
            b.btn75.text = "X"
            b.btn75.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[7][5] == "S") {
            b.btn75.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[7][5] == "SX") {
            b.btn75.text = "X"
            b.btn75.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[7][6] == "") {
            b.btn76.text = ""
            b.btn76.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[7][6] == "X") {
            b.btn76.text = "X"
            b.btn76.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[7][6] == "S") {
            b.btn76.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[7][6] == "SX") {
            b.btn76.text = "X"
            b.btn76.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[7][7] == "") {
            b.btn77.text = ""
            b.btn77.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[7][7] == "X") {
            b.btn77.text = "X"
            b.btn77.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[7][7] == "S") {
            b.btn77.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[7][7] == "SX") {
            b.btn77.text = "X"
            b.btn77.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[7][8] == "") {
            b.btn78.text = ""
            b.btn78.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[7][8] == "X") {
            b.btn78.text = "X"
            b.btn78.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[7][8] == "S") {
            b.btn78.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[7][8] == "SX") {
            b.btn78.text = "X"
            b.btn78.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[7][9] == "") {
            b.btn79.text = ""
            b.btn79.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[7][9] == "X") {
            b.btn79.text = "X"
            b.btn79.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[7][9] == "S") {
            b.btn79.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[7][9] == "SX") {
            b.btn79.text = "X"
            b.btn79.setBackgroundColor(Color.GRAY)
        }


        //9º ROW
        if (boardPlayer[8][0] == "") {
            b.btn80.text = ""
            b.btn80.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[8][0] == "X") {
            b.btn80.text = "X"
            b.btn80.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[8][0] == "S") {
            b.btn80.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[8][0] == "SX") {
            b.btn80.text = "X"
            b.btn80.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[8][1] == "") {
            b.btn81.text = ""
            b.btn81.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[8][1] == "X") {
            b.btn81.text = "X"
            b.btn81.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[8][1] == "S") {
            b.btn81.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[8][1] == "SX") {
            b.btn81.text = "X"
            b.btn81.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[8][2] == "") {
            b.btn82.text = ""
            b.btn82.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[8][2] == "X") {
            b.btn82.text = "X"
            b.btn82.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[8][2] == "S") {
            b.btn82.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[8][2] == "SX") {
            b.btn82.text = "X"
            b.btn82.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[8][3] == "") {
            b.btn83.text = ""
            b.btn83.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[8][3] == "X") {
            b.btn83.text = "X"
            b.btn83.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[8][3] == "S") {
            b.btn83.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[8][3] == "SX") {
            b.btn83.text = "X"
            b.btn83.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[8][4] == "") {
            b.btn84.text = ""
            b.btn84.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[8][4] == "X") {
            b.btn84.text = "X"
            b.btn84.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[8][4] == "S") {
            b.btn84.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[8][4] == "SX") {
            b.btn84.text = "X"
            b.btn84.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[8][5] == "") {
            b.btn85.text = ""
            b.btn85.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[8][5] == "X") {
            b.btn85.text = "X"
            b.btn85.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[8][5] == "S") {
            b.btn85.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[8][5] == "SX") {
            b.btn85.text = "X"
            b.btn85.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[8][6] == "") {
            b.btn86.text = ""
            b.btn86.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[8][6] == "X") {
            b.btn86.text = "X"
            b.btn86.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[8][6] == "S") {
            b.btn86.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[8][6] == "SX") {
            b.btn86.text = "X"
            b.btn86.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[8][7] == "") {
            b.btn87.text = ""
            b.btn87.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[8][7] == "X") {
            b.btn87.text = "X"
            b.btn87.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[8][7] == "S") {
            b.btn87.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[8][7] == "SX") {
            b.btn87.text = "X"
            b.btn87.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[8][8] == "") {
            b.btn88.text = ""
            b.btn88.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[8][8] == "X") {
            b.btn88.text = "X"
            b.btn88.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[8][8] == "S") {
            b.btn88.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[8][8] == "SX") {
            b.btn88.text = "X"
            b.btn88.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[8][9] == "") {
            b.btn89.text = ""
            b.btn89.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[8][9] == "X") {
            b.btn89.text = "X"
            b.btn89.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[8][9] == "S") {
            b.btn89.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[8][9] == "SX") {
            b.btn89.text = "X"
            b.btn89.setBackgroundColor(Color.GRAY)
        }


        //10º ROW
        if (boardPlayer[9][0] == "") {
            b.btn90.text = ""
            b.btn90.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[9][0] == "X") {
            b.btn90.text = "X"
            b.btn90.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[9][0] == "S") {
            b.btn90.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[9][0] == "SX") {
            b.btn90.text = "X"
            b.btn90.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[9][1] == "") {
            b.btn91.text = ""
            b.btn91.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[9][1] == "X") {
            b.btn91.text = "X"
            b.btn91.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[9][1] == "S") {
            b.btn91.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[9][1] == "SX") {
            b.btn91.text = "X"
            b.btn91.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[9][2] == "") {
            b.btn92.text = ""
            b.btn92.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[9][2] == "X") {
            b.btn92.text = "X"
            b.btn92.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[9][2] == "S") {
            b.btn92.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[9][2] == "SX") {
            b.btn92.text = "X"
            b.btn92.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[9][3] == "") {
            b.btn93.text = ""
            b.btn93.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[9][3] == "X") {
            b.btn93.text = "X"
            b.btn93.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[9][3] == "S") {
            b.btn93.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[9][3] == "SX") {
            b.btn93.text = "X"
            b.btn93.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[9][4] == "") {
            b.btn94.text = ""
            b.btn94.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[9][4] == "X") {
            b.btn94.text = "X"
            b.btn94.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[9][4] == "S") {
            b.btn94.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[9][4] == "SX") {
            b.btn94.text = "X"
            b.btn94.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[9][5] == "") {
            b.btn95.text = ""
            b.btn95.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[9][5] == "X") {
            b.btn95.text = "X"
            b.btn95.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[9][5] == "S") {
            b.btn95.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[9][5] == "SX") {
            b.btn95.text = "X"
            b.btn95.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[9][6] == "") {
            b.btn96.text = ""
            b.btn96.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[9][6] == "X") {
            b.btn96.text = "X"
            b.btn96.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[9][6] == "S") {
            b.btn96.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[9][6] == "SX") {
            b.btn96.text = "X"
            b.btn96.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[9][7] == "") {
            b.btn97.text = ""
            b.btn97.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[9][7] == "X") {
            b.btn97.text = "X"
            b.btn97.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[9][7] == "S") {
            b.btn97.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[9][7] == "SX") {
            b.btn97.text = "X"
            b.btn97.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[9][8] == "") {
            b.btn98.text = ""
            b.btn98.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[9][8] == "X") {
            b.btn98.text = "X"
            b.btn98.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[9][8] == "S") {
            b.btn98.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[9][8] == "SX") {
            b.btn98.text = "X"
            b.btn98.setBackgroundColor(Color.GRAY)
        }

        if (boardPlayer[9][9] == "") {
            b.btn99.text = ""
            b.btn99.setBackgroundColor(0x2196F3)
        } else if (boardPlayer[9][9] == "X") {
            b.btn99.text = "X"
            b.btn99.setBackgroundColor(R.color.Selected_side)
        } else if (boardPlayer[9][9] == "S") {
            b.btn99.setBackgroundColor(Color.GRAY)
        } else if (boardPlayer[9][9] == "SX") {
            b.btn99.text = "X"
            b.btn99.setBackgroundColor(Color.GRAY)
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun showBoardAI() {
        //1º ROW
        if (boardAI[0][0] == "") {
            b.btn00.text = ""
            b.btn00.setBackgroundColor(0x2196F3)
        } else if (boardAI[0][0] == "X") {
            b.btn00.text = "X"
            b.btn00.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[0][0] == "B" && showAIShips) {
            b.btn00.setBackgroundColor(Color.GRAY)
        } else if (boardAI[0][0] == "BX") {
            b.btn00.text = "X"
            b.btn00.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[0][1] == "") {
            b.btn01.text = ""
            b.btn01.setBackgroundColor(0x2196F3)
        } else if (boardAI[0][1] == "X") {
            b.btn01.text = "X"
            b.btn01.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[0][1] == "B" && showAIShips) {
            b.btn01.setBackgroundColor(Color.GRAY)
        } else if (boardAI[0][1] == "BX") {
            b.btn01.text = "X"
            b.btn01.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[0][2] == "") {
            b.btn02.text = ""
            b.btn02.setBackgroundColor(0x2196F3)
        } else if (boardAI[0][2] == "X") {
            b.btn02.text = "X"
            b.btn02.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[0][2] == "B" && showAIShips) {
            b.btn02.setBackgroundColor(Color.GRAY)
        } else if (boardAI[0][2] == "BX") {
            b.btn02.text = "X"
            b.btn02.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[0][3] == "") {
            b.btn03.text = ""
            b.btn03.setBackgroundColor(0x2196F3)
        } else if (boardAI[0][3] == "X") {
            b.btn03.text = "X"
            b.btn03.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[0][3] == "B" && showAIShips) {
            b.btn03.setBackgroundColor(Color.GRAY)
        } else if (boardAI[0][3] == "BX") {
            b.btn03.text = "X"
            b.btn03.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[0][4] == "") {
            b.btn04.text = ""
            b.btn04.setBackgroundColor(0x2196F3)
        } else if (boardAI[0][4] == "X") {
            b.btn04.text = "X"
            b.btn04.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[0][4] == "B" && showAIShips) {
            b.btn04.setBackgroundColor(Color.GRAY)
        } else if (boardAI[0][4] == "BX") {
            b.btn04.text = "X"
            b.btn04.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[0][5] == "") {
            b.btn05.text = ""
            b.btn05.setBackgroundColor(0x2196F3)
        } else if (boardAI[0][5] == "X") {
            b.btn05.text = "X"
            b.btn05.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[0][5] == "B" && showAIShips) {
            b.btn05.setBackgroundColor(Color.GRAY)
        } else if (boardAI[0][5] == "BX") {
            b.btn05.text = "X"
            b.btn05.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[0][6] == "") {
            b.btn06.text = ""
            b.btn06.setBackgroundColor(0x2196F3)
        } else if (boardAI[0][6] == "X") {
            b.btn06.text = "X"
            b.btn06.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[0][6] == "B" && showAIShips) {
            b.btn06.setBackgroundColor(Color.GRAY)
        } else if (boardAI[0][6] == "BX") {
            b.btn06.text = "X"
            b.btn06.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[0][7] == "") {
            b.btn07.text = ""
            b.btn07.setBackgroundColor(0x2196F3)
        } else if (boardAI[0][7] == "X") {
            b.btn07.text = "X"
            b.btn07.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[0][7] == "B" && showAIShips) {
            b.btn07.setBackgroundColor(Color.GRAY)
        } else if (boardAI[0][7] == "BX") {
            b.btn07.text = "X"
            b.btn07.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[0][8] == "") {
            b.btn08.text = ""
            b.btn08.setBackgroundColor(0x2196F3)
        } else if (boardAI[0][8] == "X") {
            b.btn08.text = "X"
            b.btn08.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[0][8] == "B" && showAIShips) {
            b.btn08.setBackgroundColor(Color.GRAY)
        } else if (boardAI[0][8] == "BX") {
            b.btn08.text = "X"
            b.btn08.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[0][9] == "") {
            b.btn09.text = ""
            b.btn09.setBackgroundColor(0x2196F3)
        } else if (boardAI[0][9] == "X") {
            b.btn09.text = "X"
            b.btn09.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[0][9] == "B" && showAIShips) {
            b.btn09.setBackgroundColor(Color.GRAY)
        } else if (boardAI[0][9] == "BX") {
            b.btn09.text = "X"
            b.btn09.setBackgroundColor(Color.GRAY)
        }


        //2º ROW
        if (boardAI[1][0] == "") {
            b.btn10.text = ""
            b.btn10.setBackgroundColor(0x2196F3)
        } else if (boardAI[1][0] == "X") {
            b.btn10.text = "X"
            b.btn10.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[1][0] == "B" && showAIShips) {
            b.btn10.setBackgroundColor(Color.GRAY)
        } else if (boardAI[1][0] == "BX") {
            b.btn10.text = "X"
            b.btn10.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[1][1] == "") {
            b.btn11.text = ""
            b.btn11.setBackgroundColor(0x2196F3)
        } else if (boardAI[1][1] == "X") {
            b.btn11.text = "X"
            b.btn11.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[1][1] == "B" && showAIShips) {
            b.btn11.setBackgroundColor(Color.GRAY)
        } else if (boardAI[1][1] == "BX") {
            b.btn11.text = "X"
            b.btn11.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[1][2] == "") {
            b.btn12.text = ""
            b.btn12.setBackgroundColor(0x2196F3)
        } else if (boardAI[1][2] == "X") {
            b.btn12.text = "X"
            b.btn12.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[1][2] == "B" && showAIShips) {
            b.btn12.setBackgroundColor(Color.GRAY)
        } else if (boardAI[1][2] == "BX") {
            b.btn12.text = "X"
            b.btn12.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[1][3] == "") {
            b.btn13.text = ""
            b.btn13.setBackgroundColor(0x2196F3)
        } else if (boardAI[1][3] == "X") {
            b.btn13.text = "X"
            b.btn13.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[1][3] == "B" && showAIShips) {
            b.btn13.setBackgroundColor(Color.GRAY)
        } else if (boardAI[1][3] == "BX") {
            b.btn13.text = "X"
            b.btn13.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[1][4] == "") {
            b.btn14.text = ""
            b.btn14.setBackgroundColor(0x2196F3)
        } else if (boardAI[1][4] == "X") {
            b.btn14.text = "X"
            b.btn14.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[1][4] == "B" && showAIShips) {
            b.btn14.setBackgroundColor(Color.GRAY)
        } else if (boardAI[1][4] == "BX") {
            b.btn14.text = "X"
            b.btn14.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[1][5] == "") {
            b.btn15.text = ""
            b.btn15.setBackgroundColor(0x2196F3)
        } else if (boardAI[1][5] == "X") {
            b.btn15.text = "X"
            b.btn15.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[1][5] == "B" && showAIShips) {
            b.btn15.setBackgroundColor(Color.GRAY)
        } else if (boardAI[1][5] == "BX") {
            b.btn15.text = "X"
            b.btn15.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[1][6] == "") {
            b.btn16.text = ""
            b.btn16.setBackgroundColor(0x2196F3)
        } else if (boardAI[1][6] == "X") {
            b.btn16.text = "X"
            b.btn16.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[1][6] == "B" && showAIShips) {
            b.btn16.setBackgroundColor(Color.GRAY)
        } else if (boardAI[1][6] == "BX") {
            b.btn16.text = "X"
            b.btn16.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[1][7] == "") {
            b.btn17.text = ""
            b.btn17.setBackgroundColor(0x2196F3)
        } else if (boardAI[1][7] == "X") {
            b.btn17.text = "X"
            b.btn17.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[1][7] == "B" && showAIShips) {
            b.btn17.setBackgroundColor(Color.GRAY)
        } else if (boardAI[1][7] == "BX") {
            b.btn17.text = "X"
            b.btn17.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[1][8] == "") {
            b.btn18.text = ""
            b.btn18.setBackgroundColor(0x2196F3)
        } else if (boardAI[1][8] == "X") {
            b.btn18.text = "X"
            b.btn18.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[1][8] == "B" && showAIShips) {
            b.btn18.setBackgroundColor(Color.GRAY)
        } else if (boardAI[1][8] == "BX") {
            b.btn18.text = "X"
            b.btn18.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[1][9] == "") {
            b.btn19.text = ""
            b.btn19.setBackgroundColor(0x2196F3)
        } else if (boardAI[1][9] == "X") {
            b.btn19.text = "X"
            b.btn19.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[1][9] == "B" && showAIShips) {
            b.btn19.setBackgroundColor(Color.GRAY)
        } else if (boardAI[1][9] == "BX") {
            b.btn19.text = "X"
            b.btn19.setBackgroundColor(Color.GRAY)
        }


        //3º ROW
        if (boardAI[2][0] == "") {
            b.btn20.text = ""
            b.btn20.setBackgroundColor(0x2196F3)
        } else if (boardAI[2][0] == "X") {
            b.btn20.text = "X"
            b.btn20.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[2][0] == "B" && showAIShips) {
            b.btn20.setBackgroundColor(Color.GRAY)
        } else if (boardAI[2][0] == "BX") {
            b.btn20.text = "X"
            b.btn20.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[2][1] == "") {
            b.btn21.text = ""
            b.btn21.setBackgroundColor(0x2196F3)
        } else if (boardAI[2][1] == "X") {
            b.btn21.text = "X"
            b.btn21.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[2][1] == "B" && showAIShips) {
            b.btn21.setBackgroundColor(Color.GRAY)
        } else if (boardAI[2][1] == "BX") {
            b.btn21.text = "X"
            b.btn21.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[2][2] == "") {
            b.btn22.text = ""
            b.btn22.setBackgroundColor(0x2196F3)
        } else if (boardAI[2][2] == "X") {
            b.btn22.text = "X"
            b.btn22.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[2][2] == "B" && showAIShips) {
            b.btn22.setBackgroundColor(Color.GRAY)
        } else if (boardAI[2][2] == "BX") {
            b.btn22.text = "X"
            b.btn22.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[2][3] == "") {
            b.btn23.text = ""
            b.btn23.setBackgroundColor(0x2196F3)
        } else if (boardAI[2][3] == "X") {
            b.btn23.text = "X"
            b.btn23.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[2][3] == "B" && showAIShips) {
            b.btn23.setBackgroundColor(Color.GRAY)
        } else if (boardAI[2][3] == "BX") {
            b.btn23.text = "X"
            b.btn23.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[2][4] == "") {
            b.btn24.text = ""
            b.btn24.setBackgroundColor(0x2196F3)
        } else if (boardAI[2][4] == "X") {
            b.btn24.text = "X"
            b.btn24.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[2][4] == "B" && showAIShips) {
            b.btn24.setBackgroundColor(Color.GRAY)
        } else if (boardAI[2][4] == "BX") {
            b.btn24.text = "X"
            b.btn24.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[2][5] == "") {
            b.btn25.text = ""
            b.btn25.setBackgroundColor(0x2196F3)
        } else if (boardAI[2][5] == "X") {
            b.btn25.text = "X"
            b.btn25.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[2][5] == "B" && showAIShips) {
            b.btn25.setBackgroundColor(Color.GRAY)
        } else if (boardAI[2][5] == "BX") {
            b.btn25.text = "X"
            b.btn25.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[2][6] == "") {
            b.btn26.text = ""
            b.btn26.setBackgroundColor(0x2196F3)
        } else if (boardAI[2][6] == "X") {
            b.btn26.text = "X"
            b.btn26.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[2][6] == "B" && showAIShips) {
            b.btn26.setBackgroundColor(Color.GRAY)
        } else if (boardAI[2][6] == "BX") {
            b.btn26.text = "X"
            b.btn26.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[2][7] == "") {
            b.btn27.text = ""
            b.btn27.setBackgroundColor(0x2196F3)
        } else if (boardAI[2][7] == "X") {
            b.btn27.text = "X"
            b.btn27.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[2][7] == "B" && showAIShips) {
            b.btn27.setBackgroundColor(Color.GRAY)
        } else if (boardAI[2][7] == "BX") {
            b.btn27.text = "X"
            b.btn27.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[2][8] == "") {
            b.btn28.text = ""
            b.btn28.setBackgroundColor(0x2196F3)
        } else if (boardAI[2][8] == "X") {
            b.btn28.text = "X"
            b.btn28.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[2][8] == "B" && showAIShips) {
            b.btn28.setBackgroundColor(Color.GRAY)
        } else if (boardAI[2][8] == "BX") {
            b.btn28.text = "X"
            b.btn28.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[2][9] == "") {
            b.btn29.text = ""
            b.btn29.setBackgroundColor(0x2196F3)
        } else if (boardAI[2][9] == "X") {
            b.btn29.text = "X"
            b.btn29.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[2][9] == "B" && showAIShips) {
            b.btn29.setBackgroundColor(Color.GRAY)
        } else if (boardAI[2][9] == "BX") {
            b.btn29.text = "X"
            b.btn29.setBackgroundColor(Color.GRAY)
        }


        //4ºROW
        if (boardAI[3][0] == "") {
            b.btn30.text = ""
            b.btn30.setBackgroundColor(0x2196F3)
        } else if (boardAI[3][0] == "X") {
            b.btn30.text = "X"
            b.btn30.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[3][0] == "B" && showAIShips) {
            b.btn30.setBackgroundColor(Color.GRAY)
        } else if (boardAI[3][0] == "BX") {
            b.btn30.text = "X"
            b.btn30.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[3][1] == "") {
            b.btn31.text = ""
            b.btn31.setBackgroundColor(0x2196F3)
        } else if (boardAI[3][1] == "X") {
            b.btn31.text = "X"
            b.btn31.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[3][1] == "B" && showAIShips) {
            b.btn31.setBackgroundColor(Color.GRAY)
        } else if (boardAI[3][1] == "BX") {
            b.btn31.text = "X"
            b.btn31.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[3][2] == "") {
            b.btn32.text = ""
            b.btn32.setBackgroundColor(0x2196F3)
        } else if (boardAI[3][2] == "X") {
            b.btn32.text = "X"
            b.btn32.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[3][2] == "B" && showAIShips) {
            b.btn32.setBackgroundColor(Color.GRAY)
        } else if (boardAI[3][2] == "BX") {
            b.btn32.text = "X"
            b.btn32.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[3][3] == "") {
            b.btn33.text = ""
            b.btn33.setBackgroundColor(0x2196F3)
        } else if (boardAI[3][3] == "X") {
            b.btn33.text = "X"
            b.btn33.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[3][3] == "B" && showAIShips) {
            b.btn33.setBackgroundColor(Color.GRAY)
        } else if (boardAI[3][3] == "BX") {
            b.btn33.text = "X"
            b.btn33.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[3][4] == "") {
            b.btn34.text = ""
            b.btn34.setBackgroundColor(0x2196F3)
        } else if (boardAI[3][4] == "X") {
            b.btn34.text = "X"
            b.btn34.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[3][4] == "B" && showAIShips) {
            b.btn34.setBackgroundColor(Color.GRAY)
        } else if (boardAI[3][4] == "BX") {
            b.btn34.text = "X"
            b.btn34.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[3][5] == "") {
            b.btn35.text = ""
            b.btn35.setBackgroundColor(0x2196F3)
        } else if (boardAI[3][5] == "X") {
            b.btn35.text = "X"
            b.btn35.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[3][5] == "B" && showAIShips) {
            b.btn35.setBackgroundColor(Color.GRAY)
        } else if (boardAI[3][5] == "BX") {
            b.btn35.text = "X"
            b.btn35.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[3][6] == "") {
            b.btn36.text = ""
            b.btn36.setBackgroundColor(0x2196F3)
        } else if (boardAI[3][6] == "X") {
            b.btn36.text = "X"
            b.btn36.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[3][6] == "B" && showAIShips) {
            b.btn36.setBackgroundColor(Color.GRAY)
        } else if (boardAI[3][6] == "BX") {
            b.btn36.text = "X"
            b.btn36.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[3][7] == "") {
            b.btn37.text = ""
            b.btn37.setBackgroundColor(0x2196F3)
        } else if (boardAI[3][7] == "X") {
            b.btn37.text = "X"
            b.btn37.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[3][7] == "B" && showAIShips) {
            b.btn37.setBackgroundColor(Color.GRAY)
        } else if (boardAI[3][7] == "BX") {
            b.btn37.text = "X"
            b.btn37.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[3][8] == "") {
            b.btn38.text = ""
            b.btn38.setBackgroundColor(0x2196F3)
        } else if (boardAI[3][8] == "X") {
            b.btn38.text = "X"
            b.btn38.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[3][8] == "B" && showAIShips) {
            b.btn38.setBackgroundColor(Color.GRAY)
        } else if (boardAI[3][8] == "BX") {
            b.btn38.text = "X"
            b.btn38.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[3][9] == "") {
            b.btn39.text = ""
            b.btn39.setBackgroundColor(0x2196F3)
        } else if (boardAI[3][9] == "X") {
            b.btn39.text = "X"
            b.btn39.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[3][9] == "B" && showAIShips) {
            b.btn39.setBackgroundColor(Color.GRAY)
        } else if (boardAI[3][9] == "BX") {
            b.btn39.text = "X"
            b.btn39.setBackgroundColor(Color.GRAY)
        }


        //5º ROW
        if (boardAI[4][0] == "") {
            b.btn40.text = ""
            b.btn40.setBackgroundColor(0x2196F3)
        } else if (boardAI[4][0] == "X") {
            b.btn40.text = "X"
            b.btn40.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[4][0] == "B" && showAIShips) {
            b.btn40.setBackgroundColor(Color.GRAY)
        } else if (boardAI[4][0] == "BX") {
            b.btn40.text = "X"
            b.btn40.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[4][1] == "") {
            b.btn41.text = ""
            b.btn41.setBackgroundColor(0x2196F3)
        } else if (boardAI[4][1] == "X") {
            b.btn41.text = "X"
            b.btn41.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[4][1] == "B" && showAIShips) {
            b.btn41.setBackgroundColor(Color.GRAY)
        } else if (boardAI[4][1] == "BX") {
            b.btn41.text = "X"
            b.btn41.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[4][2] == "") {
            b.btn42.text = ""
            b.btn42.setBackgroundColor(0x2196F3)
        } else if (boardAI[4][2] == "X") {
            b.btn42.text = "X"
            b.btn42.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[4][2] == "B" && showAIShips) {
            b.btn42.setBackgroundColor(Color.GRAY)
        } else if (boardAI[4][2] == "BX") {
            b.btn42.text = "X"
            b.btn42.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[4][3] == "") {
            b.btn43.text = ""
            b.btn43.setBackgroundColor(0x2196F3)
        } else if (boardAI[4][3] == "X") {
            b.btn43.text = "X"
            b.btn43.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[4][3] == "B" && showAIShips) {
            b.btn43.setBackgroundColor(Color.GRAY)
        } else if (boardAI[4][3] == "BX") {
            b.btn43.text = "X"
            b.btn43.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[4][4] == "") {
            b.btn44.text = ""
            b.btn44.setBackgroundColor(0x2196F3)
        } else if (boardAI[4][4] == "X") {
            b.btn44.text = "X"
            b.btn44.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[4][4] == "B" && showAIShips) {
            b.btn44.setBackgroundColor(Color.GRAY)
        } else if (boardAI[4][4] == "BX") {
            b.btn44.text = "X"
            b.btn44.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[4][5] == "") {
            b.btn45.text = ""
            b.btn45.setBackgroundColor(0x2196F3)
        } else if (boardAI[4][5] == "X") {
            b.btn45.text = "X"
            b.btn45.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[4][5] == "B" && showAIShips) {
            b.btn45.setBackgroundColor(Color.GRAY)
        } else if (boardAI[4][5] == "BX") {
            b.btn45.text = "X"
            b.btn45.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[4][6] == "") {
            b.btn46.text = ""
            b.btn46.setBackgroundColor(0x2196F3)
        } else if (boardAI[4][6] == "X") {
            b.btn46.text = "X"
            b.btn46.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[4][6] == "B" && showAIShips) {
            b.btn46.setBackgroundColor(Color.GRAY)
        } else if (boardAI[4][6] == "BX") {
            b.btn46.text = "X"
            b.btn46.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[4][7] == "") {
            b.btn47.text = ""
            b.btn47.setBackgroundColor(0x2196F3)
        } else if (boardAI[4][7] == "X") {
            b.btn47.text = "X"
            b.btn47.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[4][7] == "B" && showAIShips) {
            b.btn47.setBackgroundColor(Color.GRAY)
        } else if (boardAI[4][7] == "BX") {
            b.btn47.text = "X"
            b.btn47.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[4][8] == "") {
            b.btn48.text = ""
            b.btn48.setBackgroundColor(0x2196F3)
        } else if (boardAI[4][8] == "X") {
            b.btn48.text = "X"
            b.btn48.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[4][8] == "B" && showAIShips) {
            b.btn48.setBackgroundColor(Color.GRAY)
        } else if (boardAI[4][8] == "BX") {
            b.btn48.text = "X"
            b.btn48.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[4][9] == "") {
            b.btn49.text = ""
            b.btn49.setBackgroundColor(0x2196F3)
        } else if (boardAI[4][9] == "X") {
            b.btn49.text = "X"
            b.btn49.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[4][9] == "B" && showAIShips) {
            b.btn49.setBackgroundColor(Color.GRAY)
        } else if (boardAI[4][9] == "BX") {
            b.btn49.text = "X"
            b.btn49.setBackgroundColor(Color.GRAY)
        }


        //6º ROW
        if (boardAI[5][0] == "") {
            b.btn50.text = ""
            b.btn50.setBackgroundColor(0x2196F3)
        } else if (boardAI[5][0] == "X") {
            b.btn50.text = "X"
            b.btn50.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[5][0] == "B" && showAIShips) {
            b.btn50.setBackgroundColor(Color.GRAY)
        } else if (boardAI[5][0] == "BX") {
            b.btn50.text = "X"
            b.btn50.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[5][1] == "") {
            b.btn51.text = ""
            b.btn51.setBackgroundColor(0x2196F3)
        } else if (boardAI[5][1] == "X") {
            b.btn51.text = "X"
            b.btn51.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[5][1] == "B" && showAIShips) {
            b.btn51.setBackgroundColor(Color.GRAY)
        } else if (boardAI[5][1] == "BX") {
            b.btn51.text = "X"
            b.btn51.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[5][2] == "") {
            b.btn52.text = ""
            b.btn52.setBackgroundColor(0x2196F3)
        } else if (boardAI[5][2] == "X") {
            b.btn52.text = "X"
            b.btn52.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[5][2] == "B" && showAIShips) {
            b.btn52.setBackgroundColor(Color.GRAY)
        } else if (boardAI[5][2] == "BX") {
            b.btn52.text = "X"
            b.btn52.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[5][3] == "") {
            b.btn53.text = ""
            b.btn53.setBackgroundColor(0x2196F3)
        } else if (boardAI[5][3] == "X") {
            b.btn53.text = "X"
            b.btn53.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[5][3] == "B" && showAIShips) {
            b.btn53.setBackgroundColor(Color.GRAY)
        } else if (boardAI[5][3] == "BX") {
            b.btn53.text = "X"
            b.btn53.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[5][4] == "") {
            b.btn54.text = ""
            b.btn54.setBackgroundColor(0x2196F3)
        } else if (boardAI[5][4] == "X") {
            b.btn54.text = "X"
            b.btn54.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[5][4] == "B" && showAIShips) {
            b.btn54.setBackgroundColor(Color.GRAY)
        } else if (boardAI[5][4] == "BX") {
            b.btn54.text = "X"
            b.btn54.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[5][5] == "") {
            b.btn55.text = ""
            b.btn55.setBackgroundColor(0x2196F3)
        } else if (boardAI[5][5] == "X") {
            b.btn55.text = "X"
            b.btn55.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[5][5] == "B" && showAIShips) {
            b.btn55.setBackgroundColor(Color.GRAY)
        } else if (boardAI[5][5] == "BX") {
            b.btn55.text = "X"
            b.btn55.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[5][6] == "") {
            b.btn56.text = ""
            b.btn56.setBackgroundColor(0x2196F3)
        } else if (boardAI[5][6] == "X") {
            b.btn56.text = "X"
            b.btn56.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[5][6] == "B" && showAIShips) {
            b.btn56.setBackgroundColor(Color.GRAY)
        } else if (boardAI[5][6] == "BX") {
            b.btn56.text = "X"
            b.btn56.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[5][7] == "") {
            b.btn57.text = ""
            b.btn57.setBackgroundColor(0x2196F3)
        } else if (boardAI[5][7] == "X") {
            b.btn57.text = "X"
            b.btn57.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[5][7] == "B" && showAIShips) {
            b.btn57.setBackgroundColor(Color.GRAY)
        } else if (boardAI[5][7] == "BX") {
            b.btn57.text = "X"
            b.btn57.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[5][8] == "") {
            b.btn58.text = ""
            b.btn58.setBackgroundColor(0x2196F3)
        } else if (boardAI[5][8] == "X") {
            b.btn58.text = "X"
            b.btn58.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[5][8] == "B" && showAIShips) {
            b.btn58.setBackgroundColor(Color.GRAY)
        } else if (boardAI[5][8] == "BX") {
            b.btn58.text = "X"
            b.btn58.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[5][9] == "") {
            b.btn59.text = ""
            b.btn59.setBackgroundColor(0x2196F3)
        } else if (boardAI[5][9] == "X") {
            b.btn59.text = "X"
            b.btn59.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[5][9] == "B" && showAIShips) {
            b.btn59.setBackgroundColor(Color.GRAY)
        } else if (boardAI[5][9] == "BX") {
            b.btn59.text = "X"
            b.btn59.setBackgroundColor(Color.GRAY)
        }


        //7ºROW
        if (boardAI[6][0] == "") {
            b.btn60.text = ""
            b.btn60.setBackgroundColor(0x2196F3)
        } else if (boardAI[6][0] == "X") {
            b.btn60.text = "X"
            b.btn60.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[6][0] == "B" && showAIShips) {
            b.btn60.setBackgroundColor(Color.GRAY)
        } else if (boardAI[6][0] == "BX") {
            b.btn60.text = "X"
            b.btn60.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[6][1] == "") {
            b.btn61.text = ""
            b.btn61.setBackgroundColor(0x2196F3)
        } else if (boardAI[6][1] == "X") {
            b.btn61.text = "X"
            b.btn61.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[6][1] == "B" && showAIShips) {
            b.btn61.setBackgroundColor(Color.GRAY)
        } else if (boardAI[6][1] == "BX") {
            b.btn61.text = "X"
            b.btn61.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[6][2] == "") {
            b.btn62.text = ""
            b.btn62.setBackgroundColor(0x2196F3)
        } else if (boardAI[6][2] == "X") {
            b.btn62.text = "X"
            b.btn62.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[6][2] == "B" && showAIShips) {
            b.btn62.setBackgroundColor(Color.GRAY)
        } else if (boardAI[6][2] == "BX") {
            b.btn62.text = "X"
            b.btn62.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[6][3] == "") {
            b.btn63.text = ""
            b.btn63.setBackgroundColor(0x2196F3)
        } else if (boardAI[6][3] == "X") {
            b.btn63.text = "X"
            b.btn63.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[6][3] == "B" && showAIShips) {
            b.btn63.setBackgroundColor(Color.GRAY)
        } else if (boardAI[6][3] == "BX") {
            b.btn63.text = "X"
            b.btn63.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[6][4] == "") {
            b.btn64.text = ""
            b.btn64.setBackgroundColor(0x2196F3)
        } else if (boardAI[6][4] == "X") {
            b.btn64.text = "X"
            b.btn64.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[6][4] == "B" && showAIShips) {
            b.btn64.setBackgroundColor(Color.GRAY)
        } else if (boardAI[6][4] == "BX") {
            b.btn64.text = "X"
            b.btn64.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[6][5] == "") {
            b.btn65.text = ""
            b.btn65.setBackgroundColor(0x2196F3)
        } else if (boardAI[6][5] == "X") {
            b.btn65.text = "X"
            b.btn65.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[6][5] == "B" && showAIShips) {
            b.btn65.setBackgroundColor(Color.GRAY)
        } else if (boardAI[6][5] == "BX") {
            b.btn65.text = "X"
            b.btn65.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[6][6] == "") {
            b.btn66.text = ""
            b.btn66.setBackgroundColor(0x2196F3)
        } else if (boardAI[6][6] == "X") {
            b.btn66.text = "X"
            b.btn66.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[6][6] == "B" && showAIShips) {
            b.btn66.setBackgroundColor(Color.GRAY)
        } else if (boardAI[6][6] == "BX") {
            b.btn66.text = "X"
            b.btn66.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[6][7] == "") {
            b.btn67.text = ""
            b.btn67.setBackgroundColor(0x2196F3)
        } else if (boardAI[6][7] == "X") {
            b.btn67.text = "X"
            b.btn67.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[6][7] == "B" && showAIShips) {
            b.btn67.setBackgroundColor(Color.GRAY)
        } else if (boardAI[6][7] == "BX") {
            b.btn67.text = "X"
            b.btn67.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[6][8] == "") {
            b.btn68.text = ""
            b.btn68.setBackgroundColor(0x2196F3)
        } else if (boardAI[6][8] == "X") {
            b.btn68.text = "X"
            b.btn68.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[6][8] == "B" && showAIShips) {
            b.btn68.setBackgroundColor(Color.GRAY)
        } else if (boardAI[6][8] == "BX") {
            b.btn68.text = "X"
            b.btn68.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[6][9] == "") {
            b.btn69.text = ""
            b.btn69.setBackgroundColor(0x2196F3)
        } else if (boardAI[6][9] == "X") {
            b.btn69.text = "X"
            b.btn69.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[6][9] == "B" && showAIShips) {
            b.btn69.setBackgroundColor(Color.GRAY)
        } else if (boardAI[6][9] == "BX") {
            b.btn69.text = "X"
            b.btn69.setBackgroundColor(Color.GRAY)
        }


        //8º ROW
        if (boardAI[7][0] == "") {
            b.btn70.text = ""
            b.btn70.setBackgroundColor(0x2196F3)
        } else if (boardAI[7][0] == "X") {
            b.btn70.text = "X"
            b.btn70.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[7][0] == "B" && showAIShips) {
            b.btn70.setBackgroundColor(Color.GRAY)
        } else if (boardAI[7][0] == "BX") {
            b.btn70.text = "X"
            b.btn70.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[7][1] == "") {
            b.btn71.text = ""
            b.btn71.setBackgroundColor(0x2196F3)
        } else if (boardAI[7][1] == "X") {
            b.btn71.text = "X"
            b.btn71.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[7][1] == "B" && showAIShips) {
            b.btn71.setBackgroundColor(Color.GRAY)
        } else if (boardAI[7][1] == "BX") {
            b.btn71.text = "X"
            b.btn71.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[7][2] == "") {
            b.btn72.text = ""
            b.btn72.setBackgroundColor(0x2196F3)
        } else if (boardAI[7][2] == "X") {
            b.btn72.text = "X"
            b.btn72.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[7][2] == "B" && showAIShips) {
            b.btn72.setBackgroundColor(Color.GRAY)
        } else if (boardAI[7][2] == "BX") {
            b.btn72.text = "X"
            b.btn72.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[7][3] == "") {
            b.btn73.text = ""
            b.btn73.setBackgroundColor(0x2196F3)
        } else if (boardAI[7][3] == "X") {
            b.btn73.text = "X"
            b.btn73.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[7][3] == "B" && showAIShips) {
            b.btn73.setBackgroundColor(Color.GRAY)
        } else if (boardAI[7][3] == "BX") {
            b.btn73.text = "X"
            b.btn73.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[7][4] == "") {
            b.btn74.text = ""
            b.btn74.setBackgroundColor(0x2196F3)
        } else if (boardAI[7][4] == "X") {
            b.btn74.text = "X"
            b.btn74.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[7][4] == "B" && showAIShips) {
            b.btn74.setBackgroundColor(Color.GRAY)
        } else if (boardAI[7][4] == "BX") {
            b.btn74.text = "X"
            b.btn74.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[7][5] == "") {
            b.btn75.text = ""
            b.btn75.setBackgroundColor(0x2196F3)
        } else if (boardAI[7][5] == "X") {
            b.btn75.text = "X"
            b.btn75.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[7][5] == "B" && showAIShips) {
            b.btn75.setBackgroundColor(Color.GRAY)
        } else if (boardAI[7][5] == "BX") {
            b.btn75.text = "X"
            b.btn75.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[7][6] == "") {
            b.btn76.text = ""
            b.btn76.setBackgroundColor(0x2196F3)
        } else if (boardAI[7][6] == "X") {
            b.btn76.text = "X"
            b.btn76.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[7][6] == "B" && showAIShips) {
            b.btn76.setBackgroundColor(Color.GRAY)
        } else if (boardAI[7][6] == "BX") {
            b.btn76.text = "X"
            b.btn76.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[7][7] == "") {
            b.btn77.text = ""
            b.btn77.setBackgroundColor(0x2196F3)
        } else if (boardAI[7][7] == "X") {
            b.btn77.text = "X"
            b.btn77.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[7][7] == "B" && showAIShips) {
            b.btn77.setBackgroundColor(Color.GRAY)
        } else if (boardAI[7][7] == "BX") {
            b.btn77.text = "X"
            b.btn77.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[7][8] == "") {
            b.btn78.text = ""
            b.btn78.setBackgroundColor(0x2196F3)
        } else if (boardAI[7][8] == "X") {
            b.btn78.text = "X"
            b.btn78.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[7][8] == "B" && showAIShips) {
            b.btn78.setBackgroundColor(Color.GRAY)
        } else if (boardAI[7][8] == "BX") {
            b.btn78.text = "X"
            b.btn78.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[7][9] == "") {
            b.btn79.text = ""
            b.btn79.setBackgroundColor(0x2196F3)
        } else if (boardAI[7][9] == "X") {
            b.btn79.text = "X"
            b.btn79.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[7][9] == "B" && showAIShips) {
            b.btn79.setBackgroundColor(Color.GRAY)
        } else if (boardAI[7][9] == "BX") {
            b.btn79.text = "X"
            b.btn79.setBackgroundColor(Color.GRAY)
        }


        //9º ROW
        if (boardAI[8][0] == "") {
            b.btn80.text = ""
            b.btn80.setBackgroundColor(0x2196F3)
        } else if (boardAI[8][0] == "X") {
            b.btn80.text = "X"
            b.btn80.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[8][0] == "B" && showAIShips) {
            b.btn80.setBackgroundColor(Color.GRAY)
        } else if (boardAI[8][0] == "BX") {
            b.btn80.text = "X"
            b.btn80.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[8][1] == "") {
            b.btn81.text = ""
            b.btn81.setBackgroundColor(0x2196F3)
        } else if (boardAI[8][1] == "X") {
            b.btn81.text = "X"
            b.btn81.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[8][1] == "B" && showAIShips) {
            b.btn81.setBackgroundColor(Color.GRAY)
        } else if (boardAI[8][1] == "BX") {
            b.btn81.text = "X"
            b.btn81.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[8][2] == "") {
            b.btn82.text = ""
            b.btn82.setBackgroundColor(0x2196F3)
        } else if (boardAI[8][2] == "X") {
            b.btn82.text = "X"
            b.btn82.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[8][2] == "B" && showAIShips) {
            b.btn82.setBackgroundColor(Color.GRAY)
        } else if (boardAI[8][2] == "BX") {
            b.btn82.text = "X"
            b.btn82.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[8][3] == "") {
            b.btn83.text = ""
            b.btn83.setBackgroundColor(0x2196F3)
        } else if (boardAI[8][3] == "X") {
            b.btn83.text = "X"
            b.btn83.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[8][3] == "B" && showAIShips) {
            b.btn83.setBackgroundColor(Color.GRAY)
        } else if (boardAI[8][3] == "BX") {
            b.btn83.text = "X"
            b.btn83.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[8][4] == "") {
            b.btn84.text = ""
            b.btn84.setBackgroundColor(0x2196F3)
        } else if (boardAI[8][4] == "X") {
            b.btn84.text = "X"
            b.btn84.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[8][4] == "B" && showAIShips) {
            b.btn84.setBackgroundColor(Color.GRAY)
        } else if (boardAI[8][4] == "BX") {
            b.btn84.text = "X"
            b.btn84.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[8][5] == "") {
            b.btn85.text = ""
            b.btn85.setBackgroundColor(0x2196F3)
        } else if (boardAI[8][5] == "X") {
            b.btn85.text = "X"
            b.btn85.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[8][5] == "B" && showAIShips) {
            b.btn85.setBackgroundColor(Color.GRAY)
        } else if (boardAI[8][5] == "BX") {
            b.btn85.text = "X"
            b.btn85.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[8][6] == "") {
            b.btn86.text = ""
            b.btn86.setBackgroundColor(0x2196F3)
        } else if (boardAI[8][6] == "X") {
            b.btn86.text = "X"
            b.btn86.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[8][6] == "B" && showAIShips) {
            b.btn86.setBackgroundColor(Color.GRAY)
        } else if (boardAI[8][6] == "BX") {
            b.btn86.text = "X"
            b.btn86.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[8][7] == "") {
            b.btn87.text = ""
            b.btn87.setBackgroundColor(0x2196F3)
        } else if (boardAI[8][7] == "X") {
            b.btn87.text = "X"
            b.btn87.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[8][7] == "B" && showAIShips) {
            b.btn87.setBackgroundColor(Color.GRAY)
        } else if (boardAI[8][7] == "BX") {
            b.btn87.text = "X"
            b.btn87.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[8][8] == "") {
            b.btn88.text = ""
            b.btn88.setBackgroundColor(0x2196F3)
        } else if (boardAI[8][8] == "X") {
            b.btn88.text = "X"
            b.btn88.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[8][8] == "B" && showAIShips) {
            b.btn88.setBackgroundColor(Color.GRAY)
        } else if (boardAI[8][8] == "BX") {
            b.btn88.text = "X"
            b.btn88.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[8][9] == "") {
            b.btn89.text = ""
            b.btn89.setBackgroundColor(0x2196F3)
        } else if (boardAI[8][9] == "X") {
            b.btn89.text = "X"
            b.btn89.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[8][9] == "B" && showAIShips) {
            b.btn89.setBackgroundColor(Color.GRAY)
        } else if (boardAI[8][9] == "BX") {
            b.btn89.text = "X"
            b.btn89.setBackgroundColor(Color.GRAY)
        }


        //10º ROW
        if (boardAI[9][0] == "") {
            b.btn90.text = ""
            b.btn90.setBackgroundColor(0x2196F3)
        } else if (boardAI[9][0] == "X") {
            b.btn90.text = "X"
            b.btn90.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[9][0] == "B" && showAIShips) {
            b.btn90.setBackgroundColor(Color.GRAY)
        } else if (boardAI[9][0] == "BX") {
            b.btn90.text = "X"
            b.btn90.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[9][1] == "") {
            b.btn91.text = ""
            b.btn91.setBackgroundColor(0x2196F3)
        } else if (boardAI[9][1] == "X") {
            b.btn91.text = "X"
            b.btn91.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[9][1] == "B" && showAIShips) {
            b.btn91.setBackgroundColor(Color.GRAY)
        } else if (boardAI[9][1] == "BX") {
            b.btn91.text = "X"
            b.btn91.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[9][2] == "") {
            b.btn92.text = ""
            b.btn92.setBackgroundColor(0x2196F3)
        } else if (boardAI[9][2] == "X") {
            b.btn92.text = "X"
            b.btn92.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[9][2] == "B" && showAIShips) {
            b.btn92.setBackgroundColor(Color.GRAY)
        } else if (boardAI[9][2] == "BX") {
            b.btn92.text = "X"
            b.btn92.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[9][3] == "") {
            b.btn93.text = ""
            b.btn93.setBackgroundColor(0x2196F3)
        } else if (boardAI[9][3] == "X") {
            b.btn93.text = "X"
            b.btn93.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[9][3] == "B" && showAIShips) {
            b.btn93.setBackgroundColor(Color.GRAY)
        } else if (boardAI[9][3] == "BX") {
            b.btn93.text = "X"
            b.btn93.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[9][4] == "") {
            b.btn94.text = ""
            b.btn94.setBackgroundColor(0x2196F3)
        } else if (boardAI[9][4] == "X") {
            b.btn94.text = "X"
            b.btn94.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[9][4] == "B" && showAIShips) {
            b.btn94.setBackgroundColor(Color.GRAY)
        } else if (boardAI[9][4] == "BX") {
            b.btn94.text = "X"
            b.btn94.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[9][5] == "") {
            b.btn95.text = ""
            b.btn95.setBackgroundColor(0x2196F3)
        } else if (boardAI[9][5] == "X") {
            b.btn95.text = "X"
            b.btn95.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[9][5] == "B" && showAIShips) {
            b.btn95.setBackgroundColor(Color.GRAY)
        } else if (boardAI[9][5] == "BX") {
            b.btn95.text = "X"
            b.btn95.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[9][6] == "") {
            b.btn96.text = ""
            b.btn96.setBackgroundColor(0x2196F3)
        } else if (boardAI[9][6] == "X") {
            b.btn96.text = "X"
            b.btn96.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[9][6] == "B" && showAIShips) {
            b.btn96.setBackgroundColor(Color.GRAY)
        } else if (boardAI[9][6] == "BX") {
            b.btn96.text = "X"
            b.btn96.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[9][7] == "") {
            b.btn97.text = ""
            b.btn97.setBackgroundColor(0x2196F3)
        } else if (boardAI[9][7] == "X") {
            b.btn97.text = "X"
            b.btn97.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[9][7] == "B" && showAIShips) {
            b.btn97.setBackgroundColor(Color.GRAY)
        } else if (boardAI[9][7] == "BX") {
            b.btn97.text = "X"
            b.btn97.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[9][8] == "") {
            b.btn98.text = ""
            b.btn98.setBackgroundColor(0x2196F3)
        } else if (boardAI[9][8] == "X") {
            b.btn98.text = "X"
            b.btn98.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[9][8] == "B" && showAIShips) {
            b.btn98.setBackgroundColor(Color.GRAY)
        } else if (boardAI[9][8] == "BX") {
            b.btn98.text = "X"
            b.btn98.setBackgroundColor(Color.GRAY)
        }

        if (boardAI[9][9] == "") {
            b.btn99.text = ""
            b.btn99.setBackgroundColor(0x2196F3)
        } else if (boardAI[9][9] == "X") {
            b.btn99.text = "X"
            b.btn99.setBackgroundColor(R.color.Selected_side)
        } else if (boardAI[9][9] == "B" && showAIShips) {
            b.btn99.setBackgroundColor(Color.GRAY)
        } else if (boardAI[9][9] == "BX") {
            b.btn99.text = "X"
            b.btn99.setBackgroundColor(Color.GRAY)
        }
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

    fun createAndChooseShipsPositions() {
        //Positions 1
        shipsPositions1[0][0] = "B"
        shipsPositions1[0][8] = "B"
        shipsPositions1[0][9] = "B"
        shipsPositions1[1][3] = "B"
        shipsPositions1[1][4] = "B"
        shipsPositions1[1][5] = "B"
        shipsPositions1[1][6] = "B"
        shipsPositions1[2][1] = "B"
        shipsPositions1[3][1] = "B"
        shipsPositions1[4][1] = "B"
        shipsPositions1[3][3] = "B"
        shipsPositions1[3][6] = "B"
        shipsPositions1[3][7] = "B"
        shipsPositions1[4][4] = "B"
        shipsPositions1[5][4] = "B"
        shipsPositions1[6][4] = "B"
        shipsPositions1[7][4] = "B"
        shipsPositions1[5][0] = "B"
        shipsPositions1[6][0] = "B"
        shipsPositions1[7][0] = "B"
        shipsPositions1[8][0] = "B"
        shipsPositions1[9][0] = "B"
        shipsPositions1[7][2] = "B"
        shipsPositions1[5][7] = "B"
        shipsPositions1[6][7] = "B"
        shipsPositions1[8][6] = "B"
        shipsPositions1[8][7] = "B"
        shipsPositions1[8][8] = "B"

        //Positions 2
        shipsPositions2[0][0] = "B"
        shipsPositions2[0][1] = "B"
        shipsPositions2[0][2] = "B"
        shipsPositions2[0][7] = "B"
        shipsPositions2[0][8] = "B"
        shipsPositions2[2][0] = "B"
        shipsPositions2[2][3] = "B"
        shipsPositions2[2][4] = "B"
        shipsPositions2[2][5] = "B"
        shipsPositions2[2][6] = "B"
        shipsPositions2[2][7] = "B"
        shipsPositions2[3][1] = "B"
        shipsPositions2[4][1] = "B"
        shipsPositions2[5][1] = "B"
        shipsPositions2[6][1] = "B"
        shipsPositions2[4][3] = "B"
        shipsPositions2[5][3] = "B"
        shipsPositions2[6][4] = "B"
        shipsPositions2[5][6] = "B"
        shipsPositions2[6][6] = "B"
        shipsPositions2[7][6] = "B"
        shipsPositions2[4][8] = "B"
        shipsPositions2[8][0] = "B"
        shipsPositions2[9][0] = "B"
        shipsPositions2[8][2] = "B"
        shipsPositions2[8][3] = "B"
        shipsPositions2[8][4] = "B"
        shipsPositions2[8][5] = "B"

        val positionPick: Int = random.nextInt(0, 2)

        if (positionPick == 1)
            boardAI = shipsPositions1
        else
            boardAI = shipsPositions2
    }

    private fun createPlayerBoard(chosenMap: Int) {

        //MAP 1
        if (chosenMap == 1) {
            boardPlayer[0][0] = "S"
            boardPlayer[1][2] = "S"
            boardPlayer[1][3] = "S"
            boardPlayer[1][4] = "S"
            boardPlayer[1][5] = "S"
            boardPlayer[2][1] = "S"
            boardPlayer[3][1] = "S"
            boardPlayer[4][1] = "S"
            boardPlayer[6][1] = "S"
            boardPlayer[7][1] = "S"
            boardPlayer[8][1] = "S"
            boardPlayer[9][1] = "S"
            boardPlayer[3][4] = "S"
            boardPlayer[4][4] = "S"
            boardPlayer[5][4] = "S"
            boardPlayer[6][4] = "S"
            boardPlayer[7][4] = "S"
            boardPlayer[3][6] = "S"
            boardPlayer[9][4] = "S"
            boardPlayer[9][5] = "S"
            boardPlayer[5][6] = "S"
            boardPlayer[5][7] = "S"
            boardPlayer[1][8] = "S"
            boardPlayer[2][8] = "S"
            boardPlayer[7][7] = "S"
            boardPlayer[7][8] = "S"
            boardPlayer[7][9] = "S"
            boardPlayer[9][7] = "S"
        }

        //map2
        if (chosenMap == 2) {
            boardPlayer[0][0] = "S"
            boardPlayer[0][1] = "S"
            boardPlayer[0][2] = "S"
            boardPlayer[0][3] = "S"
            boardPlayer[0][4] = "S"
            boardPlayer[2][0] = "S"
            boardPlayer[3][1] = "S"
            boardPlayer[4][1] = "S"
            boardPlayer[5][1] = "S"
            boardPlayer[2][4] = "S"
            boardPlayer[2][5] = "S"
            boardPlayer[2][6] = "S"
            boardPlayer[0][9] = "S"
            boardPlayer[1][9] = "S"
            boardPlayer[5][4] = "S"
            boardPlayer[5][5] = "S"
            boardPlayer[3][8] = "S"
            boardPlayer[4][8] = "S"
            boardPlayer[5][8] = "S"
            boardPlayer[6][8] = "S"
            boardPlayer[8][1] = "S"
            boardPlayer[8][2] = "S"
            boardPlayer[8][3] = "S"
            boardPlayer[8][4] = "S"
            boardPlayer[7][6] = "S"
            boardPlayer[8][6] = "S"
            boardPlayer[8][8] = "S"
            boardPlayer[9][9] = "S"
        }
    }
}
