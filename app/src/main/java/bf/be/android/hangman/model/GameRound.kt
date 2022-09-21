package bf.be.android.hangman.model

import android.os.CountDownTimer
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import bf.be.android.hangman.R

class GameRound {

    var letterboard = HashMap<String, Int>()
    var letterMisses = 0
    var guessedLetters = 0
    var lettersGuessedConsecutively = 0
    var wordsGuessedConsecutively = 0
    var wordsGuessedConsecutivelyNoFaults = 0
    var potentialPrize = 10

    init {
        initialiseLetterboard()
    }

    override fun toString(): String {
        return "Word{" +
                "letterboard='" + letterboard + '\'' +
                ", letterMisses='" + letterMisses + '\'' +
                ", guessedLetters='" + guessedLetters + '\'' +
                ", lettersGuessedConsecutively='" + lettersGuessedConsecutively + '\'' +
                ", wordsGuessedConsecutively='" + wordsGuessedConsecutively + '\'' +
                ", potentialPrize='" + potentialPrize + '\'' +
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
}