package bf.be.android.hangman.viewModel

import android.app.Application
import android.content.Context
import android.view.Menu
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import bf.be.android.hangman.R
import bf.be.android.hangman.model.AvatarMoods
import bf.be.android.hangman.model.GameRound
import bf.be.android.hangman.model.Word
import bf.be.android.hangman.model.apis.*
import bf.be.android.hangman.model.dal.dao.*
import bf.be.android.hangman.model.dal.entities.*
import bf.be.android.hangman.view.GameActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel(application: Application): AndroidViewModel(application) {

    // Active user object
    private var _activeUser: MutableLiveData<User>? = null
    val activeUser: LiveData<User>?
        get() = _activeUser

    // Fetched random word (in whichever language is active)
    private var _randomWord: MutableLiveData<String> = MutableLiveData("")

    // Fetched definitions for the random word (in whichever language is active)
    private var _definitions = MutableLiveData<ArrayList<String>?>()

    // Word object
    var _word = MutableLiveData<Word>()
    val word: LiveData<Word>
        get() = _word

    // List of avatars
    var avatarList = MutableLiveData<ArrayList<Avatar>>()

    // List of languages
    var languageList = MutableLiveData<ArrayList<Language>>()

    // Active avatar
    var _activeAvatar: MutableLiveData<Avatar>? = null
    val activeAvatar: LiveData<Avatar>?
        get() = _activeAvatar

    // Active avatar mood
    var _activeAvatarMood = MutableLiveData(AvatarMoods.FACE_HAPPY_EYES_FORWARD)
    val activeAvatarMood: LiveData<AvatarMoods>
        get() = _activeAvatarMood

    // Active language
    var _activeLanguage: MutableLiveData<Language>? = null
    val activeLanguage: LiveData<Language>?
        get() = _activeLanguage

    // Selected avatar on the list of avatars
    var avatarLastSelectedCheckbox = MutableLiveData(0)

    // Selected language on the list of languages
    var languageLastSelectedCheckbox = MutableLiveData(0)

    // Get all avatars
    suspend fun findAllAvatars(context: Context): ArrayList<Avatar> = coroutineScope {
        var allAvatars: ArrayList<Avatar>? = null

        val waitFor = CoroutineScope(Dispatchers.IO).async {
            val avatarDao = AvatarDao(context)
            avatarDao.openReadable()
            allAvatars = avatarDao.findAll() as ArrayList<Avatar>?
            return@async allAvatars
        }
        waitFor.await()
        return@coroutineScope allAvatars!!
    }

    // Get all languages
    suspend fun findAllLanguages(context: Context): ArrayList<Language> = coroutineScope {
        var allLanguages: ArrayList<Language>? = null

        val waitFor = CoroutineScope(Dispatchers.IO).async {
            val languageDao = LanguageDao(context)
            languageDao.openReadable()
            allLanguages = languageDao.findAll() as ArrayList<Language>?
            return@async allLanguages
        }
        waitFor.await()
        return@coroutineScope allLanguages!!
    }

    // User related methods
    suspend fun createUser(context: Context, id: Long, appBarMenu: Menu) = coroutineScope {
        var user = User(context)
        val waitFor = CoroutineScope(Dispatchers.IO).async {
            val userDao = UserDao(context)
            userDao.openReadable()
            user = userDao.findById(id)[0]

            return@async user
        }
        waitFor.await()

        _activeUser = MutableLiveData(user)

        // Changes the language icon on the app bar
        if (user.languageId != 0) {
            _activeLanguage = MutableLiveData(this@MainViewModel.languageList.value!![user.languageId - 1])
            if (user.languageId == 1) {
                appBarMenu.getItem(0).icon = ContextCompat.getDrawable(context, R.drawable.france)
            } else if (user.languageId == 2) {
                appBarMenu.getItem(0).icon = ContextCompat.getDrawable(context, R.drawable.uk)
            }
            appBarMenu.getItem(0).isVisible = true
        }
    }

    fun updateUser(context: Context, id: Long, user: User) {
        val userDao = UserDao(context)
        userDao.openWritable()
        userDao.update(id, user)

        _activeUser = MutableLiveData(user)
    }

    // Word object related methods
    private fun generateNewWord(language: String) {
        val newWord = Word(_randomWord.value.toString(), _definitions.value, language)
        _word.value = newWord
    }

    fun updateDisplayedWord(guessedLetter: String, gameRound: GameRound): Boolean {
        var correctLetter = false
        if (_word.value != null) {
            val word = StringBuilder()
            for (i in 0 until _word.value?.displayedWord!!.length) {
                if (guessedLetter == prepareCharacter(_word.value?.hiddenWord?.get(i).toString())) {
                    correctLetter = true
                    gameRound.guessedLetters++
                    word.append(_word.value?.hiddenWord?.get(i).toString().uppercase())
                } else {
                    word.append(_word.value?.displayedWord!![i])
                }
            }
            val newWord = _word.value!!
            newWord.displayedWord = word.toString()
            _word.value = newWord
        }

        return correctLetter
    }

    // Handles special characters and diacritics
    fun prepareCharacter(character: String): String {
        val latinise = mapOf("??" to "A", "??" to "A", "???" to "A", "???" to "A", "???" to "A", "???" to "A", "???" to "A", "??" to "A", "??" to "A", "???" to "A", "???" to "A", "???" to "A", "???" to "A", "???" to "A", "??" to "A", "??" to "A", "??" to "A", "??" to "A", "???" to "A", "??" to "A", "??" to "A", "???" to "A", "??" to "A", "??" to "A", "??" to "A", "??" to "A", "??" to "A", "???" to "A", "??" to "A", "??" to "A", "???" to "AA", "??" to "AE", "??" to "AE", "??" to "AE", "???" to "AO", "???" to "AU", "???" to "AV", "???" to "AV", "???" to "AY", "???" to "B", "???" to "B", "??" to "B", "???" to "B", "??" to "B", "??" to "B", "??" to "C", "??" to "C", "??" to "C", "???" to "C", "??" to "C", "??" to "C", "??" to "C", "??" to "C", "??" to "D", "???" to "D", "???" to "D", "???" to "D", "???" to "D", "??" to "D", "???" to "D", "??" to "D", "??" to "D", "??" to "D", "??" to "D", "??" to "DZ", "??" to "DZ", "??" to "E", "??" to "E", "??" to "E", "??" to "E", "???" to "E", "??" to "E", "???" to "E", "???" to "E", "???" to "E", "???" to "E", "???" to "E", "???" to "E", "??" to "E", "??" to "E", "???" to "E", "??" to "E", "??" to "E", "???" to "E", "??" to "E", "??" to "E", "???" to "E", "???" to "E", "??" to "E", "??" to "E", "???" to "E", "???" to "E", "???" to "ET", "???" to "F", "??" to "F", "??" to "G", "??" to "G", "??" to "G", "??" to "G", "??" to "G", "??" to "G", "??" to "G", "???" to "G", "??" to "G", "???" to "H", "??" to "H", "???" to "H", "??" to "H", "???" to "H", "???" to "H", "???" to "H", "???" to "H", "??" to "H", "??" to "I", "??" to "I", "??" to "I", "??" to "I", "??" to "I", "???" to "I", "??" to "I", "???" to "I", "??" to "I", "??" to "I", "???" to "I", "??" to "I", "??" to "I", "??" to "I", "??" to "I", "??" to "I", "???" to "I", "???" to "D", "???" to "F", "???" to "G", "???" to "R", "???" to "S", "???" to "T", "???" to "IS", "??" to "J", "??" to "J", "???" to "K", "??" to "K", "??" to "K", "???" to "K", "???" to "K", "???" to "K", "??" to "K", "???" to "K", "???" to "K", "???" to "K", "??" to "L", "??" to "L", "??" to "L", "??" to "L", "???" to "L", "???" to "L", "???" to "L", "???" to "L", "???" to "L", "???" to "L", "??" to "L", "???" to "L", "??" to "L", "??" to "L", "??" to "LJ", "???" to "M", "???" to "M", "???" to "M", "???" to "M", "??" to "N", "??" to "N", "??" to "N", "???" to "N", "???" to "N", "???" to "N", "??" to "N", "??" to "N", "???" to "N", "??" to "N", "??" to "N", "??" to "N", "??" to "NJ", "??" to "O", "??" to "O", "??" to "O", "??" to "O", "???" to "O", "???" to "O", "???" to "O", "???" to "O", "???" to "O", "??" to "O", "??" to "O", "??" to "O", "??" to "O", "???" to "O", "??" to "O", "??" to "O", "??" to "O", "???" to "O", "??" to "O", "???" to "O", "???" to "O", "???" to "O", "???" to "O", "???" to "O", "??" to "O", "???" to "O", "???" to "O", "??" to "O", "???" to "O", "???" to "O", "??" to "O", "??" to "O", "??" to "O", "??" to "O", "??" to "O", "??" to "O", "???" to "O", "???" to "O", "??" to "O", "??" to "OI", "???" to "OO", "??" to "E", "??" to "O", "??" to "OU", "???" to "P", "???" to "P", "???" to "P", "??" to "P", "???" to "P", "???" to "P", "???" to "P", "???" to "Q", "???" to "Q", "??" to "R", "??" to "R", "??" to "R", "???" to "R", "???" to "R", "???" to "R", "??" to "R", "??" to "R", "???" to "R", "??" to "R", "???" to "R", "???" to "C", "??" to "E", "??" to "S", "???" to "S", "??" to "S", "???" to "S", "??" to "S", "??" to "S", "??" to "S", "???" to "S", "???" to "S", "???" to "S", "??" to "T", "??" to "T", "???" to "T", "??" to "T", "??" to "T", "???" to "T", "???" to "T", "??" to "T", "???" to "T", "??" to "T", "??" to "T", "???" to "A", "???" to "L", "??" to "M", "??" to "V", "???" to "TZ", "??" to "U", "??" to "U", "??" to "U", "??" to "U", "???" to "U", "??" to "U", "??" to "U", "??" to "U", "??" to "U", "??" to "U", "???" to "U", "???" to "U", "??" to "U", "??" to "U", "??" to "U", "???" to "U", "??" to "U", "???" to "U", "???" to "U", "???" to "U", "???" to "U", "???" to "U", "??" to "U", "??" to "U", "???" to "U", "??" to "U", "??" to "U", "??" to "U", "???" to "U", "???" to "U", "???" to "V", "???" to "V", "??" to "V", "???" to "V", "???" to "VY", "???" to "W", "??" to "W", "???" to "W", "???" to "W", "???" to "W", "???" to "W", "???" to "W", "???" to "X", "???" to "X", "??" to "Y", "??" to "Y", "??" to "Y", "???" to "Y", "???" to "Y", "???" to "Y", "??" to "Y", "???" to "Y", "???" to "Y", "??" to "Y", "??" to "Y", "???" to "Y", "??" to "Z", "??" to "Z", "???" to "Z", "???" to "Z", "??" to "Z", "???" to "Z", "??" to "Z", "???" to "Z", "??" to "Z", "??" to "IJ", "??" to "OE", "???" to "A", "???" to "AE", "??" to "B", "???" to "B", "???" to "C", "???" to "D", "???" to "E", "???" to "F", "??" to "G", "??" to "G", "??" to "H", "??" to "I", "??" to "R", "???" to "J", "???" to "K", "??" to "L", "???" to "L", "???" to "M", "??" to "N", "???" to "O", "??" to "OE", "???" to "O", "???" to "OU", "???" to "P", "??" to "R", "???" to "N", "???" to "R", "???" to "S", "???" to "T", "???" to "E", "???" to "R", "???" to "U", "???" to "V", "???" to "W", "??" to "Y", "???" to "Z", "??" to "a", "??" to "a", "???" to "a", "???" to "a", "???" to "a", "???" to "a", "???" to "a", "??" to "a", "??" to "a", "???" to "a", "???" to "a", "???" to "a", "???" to "a", "???" to "a", "??" to "a", "??" to "a", "??" to "a", "??" to "a", "???" to "a", "??" to "a", "??" to "a", "???" to "a", "??" to "a", "??" to "a", "??" to "a", "???" to "a", "???" to "a", "??" to "a", "??" to "a", "???" to "a", "???" to "a", "??" to "a", "???" to "aa", "??" to "ae", "??" to "ae", "??" to "ae", "???" to "ao", "???" to "au", "???" to "av", "???" to "av", "???" to "ay", "???" to "b", "???" to "b", "??" to "b", "???" to "b", "???" to "b", "???" to "b", "??" to "b", "??" to "b", "??" to "o", "??" to "c", "??" to "c", "??" to "c", "???" to "c", "??" to "c", "??" to "c", "??" to "c", "??" to "c", "??" to "c", "??" to "d", "???" to "d", "???" to "d", "??" to "d", "???" to "d", "???" to "d", "??" to "d", "???" to "d", "???" to "d", "???" to "d", "???" to "d", "??" to "d", "??" to "d", "??" to "d", "??" to "i", "??" to "j", "??" to "j", "??" to "j", "??" to "dz", "??" to "dz", "??" to "e", "??" to "e", "??" to "e", "??" to "e", "???" to "e", "??" to "e", "???" to "e", "???" to "e", "???" to "e", "???" to "e", "???" to "e", "???" to "e", "??" to "e", "??" to "e", "???" to "e", "??" to "e", "??" to "e", "???" to "e", "??" to "e", "??" to "e", "???" to "e", "???" to "e", "???" to "e", "??" to "e", "???" to "e", "??" to "e", "???" to "e", "???" to "e", "???" to "et", "???" to "f", "??" to "f", "???" to "f", "???" to "f", "??" to "g", "??" to "g", "??" to "g", "??" to "g", "??" to "g", "??" to "g", "??" to "g", "???" to "g", "???" to "g", "??" to "g", "???" to "h", "??" to "h", "???" to "h", "??" to "h", "???" to "h", "???" to "h", "???" to "h", "???" to "h", "??" to "h", "???" to "h", "??" to "h", "??" to "hv", "??" to "i", "??" to "i", "??" to "i", "??" to "i", "??" to "i", "???" to "i", "???" to "i", "??" to "i", "??" to "i", "???" to "i", "??" to "i", "??" to "i", "??" to "i", "???" to "i", "??" to "i", "??" to "i", "???" to "i", "???" to "d", "???" to "f", "???" to "g", "???" to "r", "???" to "s", "???" to "t", "???" to "is", "??" to "j", "??" to "j", "??" to "j", "??" to "j", "???" to "k", "??" to "k", "??" to "k", "???" to "k", "???" to "k", "???" to "k", "??" to "k", "???" to "k", "???" to "k", "???" to "k", "???" to "k", "??" to "l", "??" to "l", "??" to "l", "??" to "l", "??" to "l", "???" to "l", "??" to "l", "???" to "l", "???" to "l", "???" to "l", "???" to "l", "???" to "l", "??" to "l", "??" to "l", "???" to "l", "??" to "l", "??" to "l", "??" to "lj", "??" to "s", "???" to "s", "???" to "s", "???" to "s", "???" to "m", "???" to "m", "???" to "m", "??" to "m", "???" to "m", "???" to "m", "??" to "n", "??" to "n", "??" to "n", "???" to "n", "??" to "n", "???" to "n", "???" to "n", "??" to "n", "??" to "n", "???" to "n", "??" to "n", "???" to "n", "???" to "n", "??" to "n", "??" to "n", "??" to "nj", "??" to "o", "??" to "o", "??" to "o", "??" to "o", "???" to "o", "???" to "o", "???" to "o", "???" to "o", "???" to "o", "??" to "o", "??" to "o", "??" to "o", "??" to "o", "???" to "o", "??" to "o", "??" to "o", "??" to "o", "???" to "o", "??" to "o", "???" to "o", "???" to "o", "???" to "o", "???" to "o", "???" to "o", "??" to "o", "???" to "o", "???" to "o", "???" to "o", "??" to "o", "???" to "o", "???" to "o", "??" to "o", "??" to "o", "??" to "o", "??" to "o", "??" to "o", "???" to "o", "???" to "o", "??" to "o", "??" to "oi", "???" to "oo", "??" to "e", "???" to "e", "??" to "o", "???" to "o", "??" to "ou", "???" to "p", "???" to "p", "???" to "p", "??" to "p", "???" to "p", "???" to "p", "???" to "p", "???" to "p", "???" to "p", "???" to "q", "??" to "q", "??" to "q", "???" to "q", "??" to "r", "??" to "r", "??" to "r", "???" to "r", "???" to "r", "???" to "r", "??" to "r", "??" to "r", "???" to "r", "??" to "r", "???" to "r", "??" to "r", "???" to "r", "???" to "r", "??" to "r", "??" to "r", "???" to "c", "???" to "c", "??" to "e", "??" to "r", "??" to "s", "???" to "s", "??" to "s", "???" to "s", "??" to "s", "??" to "s", "??" to "s", "???" to "s", "???" to "s", "???" to "s", "??" to "s", "???" to "s", "???" to "s", "??" to "s", "??" to "g", "???" to "o", "???" to "o", "???" to "u", "??" to "t", "??" to "t", "???" to "t", "??" to "t", "??" to "t", "???" to "t", "???" to "t", "???" to "t", "???" to "t", "??" to "t", "???" to "t", "???" to "t", "??" to "t", "??" to "t", "??" to "t", "???" to "th", "??" to "a", "???" to "ae", "??" to "e", "???" to "g", "??" to "h", "??" to "h", "??" to "h", "???" to "i", "??" to "k", "???" to "l", "??" to "m", "??" to "m", "???" to "oe", "??" to "r", "??" to "r", "??" to "r", "???" to "r", "??" to "t", "??" to "v", "??" to "w", "??" to "y", "???" to "tz", "??" to "u", "??" to "u", "??" to "u", "??" to "u", "???" to "u", "??" to "u", "??" to "u", "??" to "u", "??" to "u", "??" to "u", "???" to "u", "???" to "u", "??" to "u", "??" to "u", "??" to "u", "???" to "u", "??" to "u", "???" to "u", "???" to "u", "???" to "u", "???" to "u", "???" to "u", "??" to "u", "??" to "u", "???" to "u", "??" to "u", "???" to "u", "??" to "u", "??" to "u", "???" to "u", "???" to "u", "???" to "ue", "???" to "um", "???" to "v", "???" to "v", "???" to "v", "??" to "v", "???" to "v", "???" to "v", "???" to "v", "???" to "vy", "???" to "w", "??" to "w", "???" to "w", "???" to "w", "???" to "w", "???" to "w", "???" to "w", "???" to "w", "???" to "x", "???" to "x", "???" to "x", "??" to "y", "??" to "y", "??" to "y", "???" to "y", "???" to "y", "???" to "y", "??" to "y", "???" to "y", "???" to "y", "??" to "y", "???" to "y", "??" to "y", "???" to "y", "??" to "z", "??" to "z", "???" to "z", "??" to "z", "???" to "z", "??" to "z", "???" to "z", "??" to "z", "???" to "z", "???" to "z", "???" to "z", "??" to "z", "??" to "z", "??" to "z", "???" to "ff", "???" to "ffi", "???" to "ffl", "???" to "fi", "???" to "fl", "??" to "ij", "??" to "oe", "???" to "st", "???" to "a", "???" to "e", "???" to "i", "???" to "j", "???" to "o", "???" to "r", "???" to "u", "???" to "v", "???" to "x")
        return if (latinise[character] != null) {
            latinise[character]!!.uppercase()
        } else {
            character.uppercase()
        }
    }

    // Database related methods
    suspend fun findAllHighscores(context: Context): ArrayList<Highscore> = coroutineScope {
        var allHighscores: ArrayList<Highscore>? = null

        val waitFor = CoroutineScope(Dispatchers.IO).async {
            val highscoreDao = HighscoreDao(context)
            highscoreDao.openReadable()
            allHighscores = highscoreDao.findAll()
            return@async allHighscores
        }
        waitFor.await()
        return@coroutineScope allHighscores!!
    }

    fun insertHighscore(highscore: Int, context: Context) {
        val newHighscore = Highscore(
            highscore,
            SimpleDateFormat("dd/MM/yyyy").format(Date()).toString(),
            activeUser?.value!!.languageId,
            activeUser?.value!!.id.toInt(),
            activeUser?.value!!.avatarId
        )
        val highscoreDao = HighscoreDao(context)
        highscoreDao.openWritable()
        highscoreDao.insert(newHighscore)
    }

    fun updateHighscore(context: Context, id: Long, highscore: Highscore) {
        val highscoreDao = HighscoreDao(context)
        highscoreDao.openWritable()
        highscoreDao.update(id, highscore)
    }

    suspend fun getAvatarsHeadshots(context: Context): ArrayList<String> = coroutineScope {
        lateinit var avatars: MutableList<Avatar>
        val avatarsHeadshots = ArrayList<String>()

        val waitFor = CoroutineScope(Dispatchers.IO).async {
            val avatarDao = AvatarDao(context)
            avatarDao.openReadable()

            avatars = avatarDao.findAll()

            return@async avatars
        }
        waitFor.await()

        for (item in avatars) {
            avatarsHeadshots.add(item.headShot)
        }

        return@coroutineScope avatarsHeadshots
    }

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

    suspend fun findAvatarById(context: Context, id: Long): List<Avatar>? = coroutineScope {
        var avatarFound: List<Avatar>? = null

        val waitFor = CoroutineScope(Dispatchers.IO).async {
            val avatarDao = AvatarDao(context)
            avatarDao.openReadable()
            avatarFound = avatarDao.findById(id)
            return@async avatarDao
        }
        waitFor.await()
        return@coroutineScope avatarFound
    }

    suspend fun findLanguageById(context: Context, id: Long): List<Language>? = coroutineScope {
        var languageFound: List<Language>? = null

        val waitFor = CoroutineScope(Dispatchers.IO).async {
            val languageDao = LanguageDao(context)
            languageDao.openReadable()
            languageFound = languageDao.findById(id)
            return@async languageFound
        }
        waitFor.await()
        return@coroutineScope languageFound
    }

    suspend fun findUserId(context: Context, username: String, password: String): Long = coroutineScope {
        var userFoundId: Long = 0

        val waitFor = CoroutineScope(Dispatchers.IO).async {
            val userDao = UserDao(context)
            userDao.openReadable()
            if (userDao.findByUsernameAndPassword(username, password).size > 0) {
                userFoundId = userDao.findByUsernameAndPassword(username, password)[0].id
            }

            return@async userFoundId
        }
        waitFor.await()
        return@coroutineScope userFoundId
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

    suspend fun findEyebrowsById(context: Context, id: Long): List<Eyebrows>? = coroutineScope {
        var eyebrowsFound: List<Eyebrows>? = null

        val waitFor = CoroutineScope(Dispatchers.IO).async {
            val eyebrowsDao = EyebrowsDao(context)
            eyebrowsDao.openReadable()
            eyebrowsFound = eyebrowsDao.findById(id)
            return@async eyebrowsFound
        }
        waitFor.await()
        return@coroutineScope eyebrowsFound
    }

    suspend fun findEyesById(context: Context, id: Long): List<Eyes>? = coroutineScope {
        var eyesFound: List<Eyes>? = null

        val waitFor = CoroutineScope(Dispatchers.IO).async {
            val eyesDao = EyesDao(context)
            eyesDao.openReadable()
            eyesFound = eyesDao.findById(id)
            return@async eyesFound
        }
        waitFor.await()
        return@coroutineScope eyesFound
    }

    suspend fun findExtraById(context: Context, id: Long): List<Extra>? = coroutineScope {
        var extrasFound: List<Extra>? = null

        val waitFor = CoroutineScope(Dispatchers.IO).async {
            val extraDao = ExtraDao(context)
            extraDao.openReadable()
            extrasFound = extraDao.findById(id)
            return@async extrasFound
        }
        waitFor.await()
        return@coroutineScope extrasFound
    }

    suspend fun findMouthById(context: Context, id: Long): List<Mouth>? = coroutineScope {
        var mouthsFound: List<Mouth>? = null

        val waitFor = CoroutineScope(Dispatchers.IO).async {
            val mouthDao = MouthDao(context)
            mouthDao.openReadable()
            mouthsFound = mouthDao.findById(id)
            return@async mouthsFound
        }
        waitFor.await()
        return@coroutineScope mouthsFound
    }

    fun insertUser(context: Context, user: User) {
        val userDao = UserDao(context)
        userDao.openWritable()
        userDao.insert(user)
    }

    fun deleteUser(context: Context, id: Long) {
        val userDao = UserDao(context)
        userDao.openWritable()
        userDao.delete(id)
    }

    //Api related methods
    fun getRandomWordFr() {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://frenchwordsapi.herokuapp.com/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(ApiRandomWordFrench::class.java)
        val call = api.getRandomWord()
        call.enqueue(object : Callback<List<RandomFrenchWord>> {
            override fun onResponse(call: Call<List<RandomFrenchWord>>, response: Response<List<RandomFrenchWord>>) {
                val fetchedWord = response.body()?.get(0)?.getResult()!! // Random French word here
                _randomWord.value = fetchedWord

                getFrenchDefinition(fetchedWord)
            }

            override fun onFailure(call: Call<List<RandomFrenchWord>>, t: Throwable) {
                _randomWord.value = ""
            }
        })
    }

    fun getRandomWordEn() {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://random-word-api.herokuapp.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(ApiRandomWordEnglish::class.java)
        val call = api.getRandomWord()
        call.enqueue(object : Callback<List<String>> {
            override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                val fetchedWord = response.body()?.get(0)!! // Random English word here
                _randomWord.value = fetchedWord

                getEnglishDefinition(fetchedWord)
            }

            override fun onFailure(call: Call<List<String>>, t: Throwable) {
                _randomWord.value = ""
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
                    _definitions.value = frenchDefinitions
                } else {
                    _definitions.value = ArrayList()
                }
                generateNewWord("French")
            }

            override fun onFailure(call: Call<French?>, t: Throwable) {
                _definitions.value = ArrayList()
                generateNewWord("French")
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
                val englishDefinitions = ArrayList<String>()
                val dictionaryResponse = response.body()

                if (dictionaryResponse != null) {
                    val meanings = dictionaryResponse[0].getMeanings()
                    if (meanings != null) {
                        for (element in meanings) {
                            val definitions = element?.getDefinitions()
                            if (definitions != null) {
                                for (element in definitions) {
                                    englishDefinitions.add(element?.getDefinition().toString()) // English definitions here
                                }
                            } else {
                                // No definitions
                            }
                        }
                    } else {
                        // No definitions
                    }
                } else {
                    // No definitions
                }
                _definitions.value = englishDefinitions
                generateNewWord("English")
            }

            override fun onFailure(call: Call<List<English>?>, t: Throwable) {
                _definitions.value = ArrayList()
                generateNewWord("English")
            }
        })
    }
}