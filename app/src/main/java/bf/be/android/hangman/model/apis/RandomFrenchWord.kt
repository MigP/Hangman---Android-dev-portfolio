package bf.be.android.hangman.model.apis

import com.google.gson.annotations.SerializedName

class RandomFrenchWord {
    @SerializedName("WordName")
    private val wordName: String = ""

    fun getResult(): String {
        return this.wordName
    }
}