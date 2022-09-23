package bf.be.android.hangman.model

import android.content.Context
import android.content.res.Resources
import bf.be.android.hangman.R


class GameRound (context: Context) {
    private val context: Context = context
    private var letterboard = HashMap<String, Int>()
    var letterMisses = 0
    var guessedLetters = 0
    var lettersGuessedConsecutively = 0
    var wordsGuessedConsecutively = 0
    var wordsGuessedConsecutivelyNoFaults = 0
    var potentialPrize = 10
    var exchangeValues_Banknotes_price: Int = 0
    var exchangeValues_Diamonds_price: Int = 0
    var exchangeValues_Lives_price: Int = 0
    var helpValues_Letter_price: Int = 0
    var helpValues_Definition_price: Int = 0
    var helpValues_BodyPart_price: Int = 0

    init {
        initialiseLetterboard()
        initialiseConstants()
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

    private fun initialiseConstants() {
        val res: Resources = this.context.resources

        val gameExchangeConstantsValues: Array<String> = res.getStringArray(R.array.resource_exchanges)
        this.exchangeValues_Banknotes_price = gameExchangeConstantsValues[0].toInt()
        this.exchangeValues_Diamonds_price = gameExchangeConstantsValues[1].toInt()
        this.exchangeValues_Lives_price = gameExchangeConstantsValues[2].toInt()

        val gameHelpConstantsValues: Array<String> = res.getStringArray(R.array.help_prices)
        this.helpValues_Letter_price = gameHelpConstantsValues[0].toInt()
        this.helpValues_Definition_price = gameHelpConstantsValues[1].toInt()
        this.helpValues_BodyPart_price = gameHelpConstantsValues[2].toInt()
    }
}