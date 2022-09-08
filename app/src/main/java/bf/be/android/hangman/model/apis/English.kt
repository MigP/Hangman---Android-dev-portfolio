package bf.be.android.hangman.model.apis

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class English {
    @SerializedName("meanings")
    @Expose
    private val meanings: List<Meaning?>? = null

    fun getMeanings() = this.meanings
}