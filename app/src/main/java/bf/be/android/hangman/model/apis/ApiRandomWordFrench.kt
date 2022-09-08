package bf.be.android.hangman.model.apis

import retrofit2.Call
import retrofit2.http.GET

interface ApiRandomWordFrench {
    @GET("Word/GetRandomWord?nbrWordsNeeded=1")
    fun getRandomWord(): Call<List<RandomFrenchWord>>
}