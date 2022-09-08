package bf.be.android.hangman.model.apis
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiDefinitionsEnglish {
    @GET("api/v2/entries/en/{word}")
    fun getDefinitions(@Path("word") word: String?): Call<List<English>?>
}