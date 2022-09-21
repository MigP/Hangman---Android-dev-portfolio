package bf.be.android.hangman.model

class Word(
    var hiddenWord: String,
    var definitions: ArrayList<String>?,
    private var language: String
) {

    var displayedWord = generateDisplayedWord()
    var revealedDefinitions = ArrayList<String>()

    init {
        initialiseRevealedDefinitions()
    }

    override fun toString(): String {
        return "Word{" +
                "hiddenWord='" + hiddenWord + '\'' +
                ", displayedWord='" + displayedWord + '\'' +
                ", definitions='" + definitions + '\'' +
                ", revealedDefinitions='" + revealedDefinitions + '\'' +
                ", language='" + language + '\'' +
                '}'
    }

    private fun generateDisplayedWord(): String {
        val word = StringBuilder()
        for (i in 0 until this.hiddenWord.length) {
            word.append("*")
        }
        return word.toString()
    }

    private fun initialiseRevealedDefinitions() {
        for (item in definitions!!) {
            revealedDefinitions.add(item)
        }
    }
}