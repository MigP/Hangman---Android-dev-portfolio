package bf.be.android.hangman.model.apis

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Meaning {
    @SerializedName("definitions")
    @Expose
    private val definitions: List<Definition?>? = null

    fun getDefinitions(): List<Definition?>? {
        return definitions
    }
}