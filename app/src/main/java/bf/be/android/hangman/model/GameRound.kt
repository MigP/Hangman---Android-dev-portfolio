package bf.be.android.hangman.model

import android.os.CountDownTimer

class GameRound {

    var letterboard = HashMap<String, Int>()
    var displayedAvatar = ArrayList<String>()
    var letterMisses = 0
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
                ", letterMisses='" + letterMisses + '\'' +
                ", guessedLetters='" + guessedLetters + '\'' +
                '}'
    }

    fun getLetterboardState(letter: String): Int? {
        return this.letterboard[letter]
    }

    fun setLetterboardState(letter: String, state: Int): GameRound {
        this.letterboard[letter] = state
        return this
    }

    private fun initialiseLetterboard() {
        this.letterboard["A"] = 0
        this.letterboard["B"] = 0
        this.letterboard["C"] = 0
        this.letterboard["D"] = 0
        this.letterboard["E"] = 0
        this.letterboard["F"] = 0
        this.letterboard["G"] = 0
        this.letterboard["H"] = 0
        this.letterboard["I"] = 0
        this.letterboard["J"] = 0
        this.letterboard["K"] = 0
        this.letterboard["L"] = 0
        this.letterboard["M"] = 0
        this.letterboard["N"] = 0
        this.letterboard["O"] = 0
        this.letterboard["P"] = 0
        this.letterboard["Q"] = 0
        this.letterboard["R"] = 0
        this.letterboard["S"] = 0
        this.letterboard["T"] = 0
        this.letterboard["U"] = 0
        this.letterboard["V"] = 0
        this.letterboard["W"] = 0
        this.letterboard["X"] = 0
        this.letterboard["Y"] = 0
        this.letterboard["Z"] = 0
    }

    private fun initialiseDisplayedAvatar() {
        this.displayedAvatar.add("head")
        this.displayedAvatar.add("torso")
        this.displayedAvatar.add("left arm")
        this.displayedAvatar.add("right arm")
        this.displayedAvatar.add("left leg")
        this.displayedAvatar.add("right leg")
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