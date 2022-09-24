package bf.be.android.hangman.model

import android.content.Context
import android.content.res.Resources
import bf.be.android.hangman.R


class GameRound (private val context: Context) {
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
        initialiseConstants()
    }

    override fun toString(): String {
        return "Word{" +
                ", letterMisses='" + letterMisses + '\'' +
                ", guessedLetters='" + guessedLetters + '\'' +
                ", lettersGuessedConsecutively='" + lettersGuessedConsecutively + '\'' +
                ", wordsGuessedConsecutively='" + wordsGuessedConsecutively + '\'' +
                ", potentialPrize='" + potentialPrize + '\'' +
                ", exchangeValues_Banknotes_price='" + exchangeValues_Banknotes_price + '\'' +
                ", exchangeValues_Diamonds_price='" + exchangeValues_Diamonds_price + '\'' +
                ", exchangeValues_Lives_price='" + exchangeValues_Lives_price + '\'' +
                ", helpValues_Letter_price='" + helpValues_Letter_price + '\'' +
                ", helpValues_Definition_price='" + helpValues_Definition_price + '\'' +
                ", helpValues_BodyPart_price='" + helpValues_BodyPart_price + '\'' +
                '}'
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