package bf.be.android.hangman.model.apis

import retrofit2.Call
import retrofit2.http.GET

interface ApiRandomWordEnglish {
    @GET("word")
    fun getRandomWord(): Call<List<String>>
}