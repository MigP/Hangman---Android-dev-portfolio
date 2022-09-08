package bf.be.android.hangman.model.apis

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Definition {
    @SerializedName("definition")
    @Expose
    private val definition: String? = null

    fun getDefinition(): String? {
        return definition
    }
}