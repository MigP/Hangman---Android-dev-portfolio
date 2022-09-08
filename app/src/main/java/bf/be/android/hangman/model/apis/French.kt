package bf.be.android.hangman.model.apis

import com.google.gson.annotations.SerializedName

class French {
    @SerializedName("Definition")
    var definitions = ArrayList<String>()

    fun getDefinitionsList(): ArrayList<String>? {
        if (this.definitions.size > 1) { // Removes the leading numbered list text present when there are multiple definitions (1. , 2. , 3. , etc.)
            for (i in 0 until this.definitions.size) {
                this.definitions[i] = this.definitions[i].substring(this.definitions[i].indexOf(".") + 2)
            }
        }
        return this.definitions
    }
}