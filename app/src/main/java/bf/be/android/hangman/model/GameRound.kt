package bf.be.android.hangman.model

import android.os.CountDownTimer
import android.preference.PreferenceManager
import bf.be.android.hangman.R
import bf.be.android.hangman.model.dal.entities.Mouth

class GameRound {

    var letterboard = HashMap<String, Int>()
    var displayedAvatar = HashMap<String, Boolean>()
    var guessedLetters = 0

    companion object {
        var timeLeft = 0L
        var timer5: CountDownTimer? = null
    }

    init {
        initialiseLetterboard()
        initialiseDisplayedAvatar()

        timer5 = object: CountDownTimer(5000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeft = millisUntilFinished / 1000
            }

            override fun onFinish() {
                // Do something
            }
        }
    }

    override fun toString(): String {
        return "Word{" +
                "letterboard='" + letterboard + '\'' +
                ", displayedAvatar='" + displayedAvatar + '\'' +
                '}'
    }

    fun getLetterboardState(letter: String): Int? {
        return this.letterboard.get(letter)
    }

    fun setLetterboardState(letter: String, state: Int): GameRound {
        this.letterboard.put(letter, state)
        return this
    }

    private fun initialiseLetterboard() {
        this.letterboard.put("A", 0)
        this.letterboard.put("B", 0)
        this.letterboard.put("C", 0)
        this.letterboard.put("D", 0)
        this.letterboard.put("E", 0)
        this.letterboard.put("F", 0)
        this.letterboard.put("G", 0)
        this.letterboard.put("H", 0)
        this.letterboard.put("I", 0)
        this.letterboard.put("J", 0)
        this.letterboard.put("K", 0)
        this.letterboard.put("L", 0)
        this.letterboard.put("M", 0)
        this.letterboard.put("N", 0)
        this.letterboard.put("O", 0)
        this.letterboard.put("P", 0)
        this.letterboard.put("Q", 0)
        this.letterboard.put("R", 0)
        this.letterboard.put("S", 0)
        this.letterboard.put("T", 0)
        this.letterboard.put("U", 0)
        this.letterboard.put("V", 0)
        this.letterboard.put("W", 0)
        this.letterboard.put("X", 0)
        this.letterboard.put("Y", 0)
        this.letterboard.put("Z", 0)
    }

    private fun initialiseDisplayedAvatar() {
        this.displayedAvatar.put("head", false)
        this.displayedAvatar.put("torso", false)
        this.displayedAvatar.put("left arm", false)
        this.displayedAvatar.put("right arm", false)
        this.displayedAvatar.put("left leg", false)
        this.displayedAvatar.put("right leg", false)
    }

    private fun startTimer5() {
        timer5?.cancel()
        timer5?.start()
    }

    fun letterGuessed(letter: String) {
        //TODO Implement
        //setLetterboardState(letter, )
    }
}