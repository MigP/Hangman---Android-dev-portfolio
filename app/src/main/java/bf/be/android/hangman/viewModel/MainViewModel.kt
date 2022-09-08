package bf.be.android.hangman.viewModel

import android.app.Application
import android.content.Context
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.TextView
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import bf.be.android.hangman.R
import bf.be.android.hangman.model.Word
import bf.be.android.hangman.model.apis.*
import bf.be.android.hangman.model.dal.dao.UserDao
import bf.be.android.hangman.model.dal.entities.User
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Integer.parseInt
import java.util.*
import kotlin.collections.ArrayList

class MainViewModel(application: Application): AndroidViewModel(application) {

    // User object
    private var _activeUser: MutableLiveData<User>? = null
    val activeUser: LiveData<User>?
        get() = _activeUser

    // Fetched random word (in whichever language is active)
    private var _randomWord: MutableLiveData<String> = MutableLiveData("")

    // Fetched definitions for the random word (in whichever language is active)
    private var _definitions = MutableLiveData<ArrayList<String>>()

    // Word object
    private var _word = MutableLiveData<Word>()
    val word: LiveData<Word>
        get() = _word

    //Word object related methods
    private fun generateNewWord(language: String) {
        val newWord = Word(_randomWord.value.toString(), _definitions.value, language)
        _word.value = newWord
    }

    fun updateDisplayedWord(guessedLetter: String) {
        if (_word.value != null) {
            val word = StringBuilder()
            for (i in 0 until _word.value?.displayedWord!!.length) {
                if (guessedLetter.equals(_word.value?.hiddenWord?.get(i).toString())) {
                    word.append(guessedLetter)
                } else {
                    word.append(_word.value?.displayedWord!!.get(i))
                }
            }
            val newWord = _word.value!!
            newWord!!.displayedWord = word.toString()
            _word.value = newWord
        }
    }

    //Database related methods
    suspend fun usernameExists(context: Context, username: String): Boolean = coroutineScope {
        var usernameFound = false

        val waitFor = CoroutineScope(Dispatchers.IO).async {
            val userDao = UserDao(context)
            userDao.openReadable()
            if (userDao.findByUsername(username).size > 0) {
                usernameFound = true
            }

            return@async usernameFound
        }
        waitFor.await()
        return@coroutineScope usernameFound
    }

    suspend fun userExists(context: Context, username: String, password: String): Boolean = coroutineScope {
        var userFound = false

        val waitFor = CoroutineScope(Dispatchers.IO).async {
            val userDao = UserDao(context)
            userDao.openReadable()
            if (userDao.findByUsernameAndPassword(username, password).size > 0) {
                userFound = true
            }

            return@async userFound
        }
        waitFor.await()
        return@coroutineScope userFound
    }

    suspend fun findAllUsers(context: Context): List<User> = coroutineScope {
        var allUsers: List<User>? = null

        val waitFor = CoroutineScope(Dispatchers.IO).async {
            val userDao = UserDao(context)
            userDao.openReadable()
            allUsers = userDao.findAll()
            return@async allUsers
        }
        waitFor.await()
        return@coroutineScope allUsers!!
    }

    suspend fun findUserById(context: Context, id: Long): List<User>? = coroutineScope {
        var userFound: List<User>? = null

        val waitFor = CoroutineScope(Dispatchers.IO).async {
            val userDao = UserDao(context)
            userDao.openReadable()
            userFound = userDao.findById(id)
            return@async userFound
        }
        waitFor.await()
        return@coroutineScope userFound
    }

    suspend fun insertUser(context: Context, user: User) = coroutineScope {
        launch {
            val userDao = UserDao(context)
            userDao.openWritable()
            userDao.insert(user)
        }
    }

    suspend fun updateUser(context: Context, id: Long, user: User) = coroutineScope {
        launch {
            val userDao = UserDao(context)
            userDao.openWritable()
            userDao.update(id, user)
        }
    }

    suspend fun deleteUser(context: Context, id: Long) = coroutineScope {
        launch {
            val userDao = UserDao(context)
            userDao.openWritable()
            userDao.delete(id)
        }
    }

    //Api related methods
    fun getRandomWordFr(view: View) {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://frenchwordsapi.herokuapp.com/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(ApiRandomWordFrench::class.java)
        val call = api.getRandomWord()
        call.enqueue(object : Callback<List<RandomFrenchWord>> {
            override fun onResponse(call: Call<List<RandomFrenchWord>>, response: Response<List<RandomFrenchWord>>) {
                var fetchedWord = response.body()?.get(0)?.getResult()!! // Random French word here
                _randomWord.value = fetchedWord

                getFrenchDefinition(fetchedWord)
            }

            override fun onFailure(call: Call<List<RandomFrenchWord>>, t: Throwable) {
                println("Error") //TODO Handle error
            }
        })
    }

    fun getRandomWordEn(view: View) {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://random-word-api.herokuapp.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(ApiRandomWordEnglish::class.java)
        val call = api.getRandomWord()
        call.enqueue(object : Callback<List<String>> {
            override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                var fetchedWord = response.body()?.get(0)!! // Random English word here
                _randomWord.value = fetchedWord

                getEnglishDefinition(fetchedWord)
            }

            override fun onFailure(call: Call<List<String>>, t: Throwable) {
                println("Error") //TODO Handle error
            }
        })
    }

    fun getFrenchDefinition(word: String) {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://frenchwordsapi.herokuapp.com/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(ApiDefinitionsFrench::class.java)
        val call = api.getDefinitions(word)
        call.enqueue(object : Callback<French?> {
            override fun onResponse(call: Call<French?>, response: Response<French?>) {
                val frenchDefinitions = response.body()?.getDefinitionsList() // French definitions here
                if (frenchDefinitions != null) {
                    _definitions.value = frenchDefinitions!!
                } else {
                    _definitions.value = ArrayList()
                }
                generateNewWord("French")
            }

            override fun onFailure(call: Call<French?>, t: Throwable) {
                println("Error") //TODO Handle error
            }
        })
    }

    fun getEnglishDefinition(word: String) {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://api.dictionaryapi.dev/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(ApiDefinitionsEnglish::class.java)
        val call = api.getDefinitions(word)
        call.enqueue(object : Callback<List<English>?> {
            override fun onResponse(call: Call<List<English>?>, response: Response<List<English>?>) {
                var error = false
                var englishDefinitions = ArrayList<String>()
                val dictionaryResponse = response.body()

                if (dictionaryResponse != null) {
                    val meanings = dictionaryResponse[0].getMeanings()
                    if (meanings != null) {
                        for (i in 0 until meanings.size) {
                            val definitions = meanings[i]?.getDefinitions()
                            if (definitions != null) {
                                for (j in 0 until definitions.size) {
                                    englishDefinitions.add(definitions[j]?.getDefinition().toString()) // English definitions here
                                }
                            } else {
                                error = true //TODO Alert the user: no definitions found
                            }
                        }
                    } else {
                        error = true //TODO Alert the user: no definitions found
                    }
                } else {
                    error = true //TODO Alert the user: no definitions found
                }
                if (englishDefinitions != null) {
                    _definitions.value = englishDefinitions!!
                } else {
                    _definitions.value = ArrayList()
                }
                generateNewWord("English")
            }

            override fun onFailure(call: Call<List<English>?>, t: Throwable) {
                println("Error") //TODO Handle error
            }
        })
    }
}