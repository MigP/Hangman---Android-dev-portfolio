package bf.be.android.hangman.model

class Word(
    var hiddenWord: String,
    private var definitions: ArrayList<String>?,
    private var language: String
) {

    var displayedWord = generateDisplayedWord()

    override fun toString(): String {
        return "Word{" +
                "hiddenWord='" + hiddenWord + '\'' +
                ", displayedWord='" + displayedWord + '\'' +
                ", definitions='" + definitions + '\'' +
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
}