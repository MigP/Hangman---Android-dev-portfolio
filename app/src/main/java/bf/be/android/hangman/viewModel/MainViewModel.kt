package bf.be.android.hangman.viewModel

import android.app.Application
import android.content.Context
import android.view.Menu
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import bf.be.android.hangman.R
import bf.be.android.hangman.model.GameRound
import bf.be.android.hangman.model.Word
import bf.be.android.hangman.model.apis.*
import bf.be.android.hangman.model.dal.dao.*
import bf.be.android.hangman.model.dal.entities.*
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
import kotlin.collections.ArrayList

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

    // Active language
    var _activeLanguage: MutableLiveData<Language>? = null
    val activeLanguage: LiveData<Language>?
        get() = _activeLanguage

    // Active game round
    var _activeGameRound: MutableLiveData<GameRound>? = null
    val activeGameRound: LiveData<GameRound>?
        get() = _activeGameRound

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
        var user = User()
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

    // Game round related methods
    fun createNewGameRound() {
            val tempGameRound = GameRound()
            _activeGameRound = MutableLiveData(tempGameRound)
    }

    // Word object related methods
    private fun generateNewWord(language: String) {
        val newWord = Word(_randomWord.value.toString(), _definitions.value, language)
        _word.value = newWord
    }

    fun updateDisplayedWord(guessedLetter: String): Boolean {
        var correctLetter = false
        if (_word.value != null) {
            val word = StringBuilder()
            for (i in 0 until _word.value?.displayedWord!!.length) {
                if (guessedLetter == prepareCharacter(_word.value?.hiddenWord?.get(i).toString())) {
                    correctLetter = true
                    _activeGameRound?.value!!.guessedLetters++
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
    private fun prepareCharacter(character: String): String {
        val latinise = mapOf("Á" to "A", "Ă" to "A", "Ắ" to "A", "Ặ" to "A", "Ằ" to "A", "Ẳ" to "A", "Ẵ" to "A", "Ǎ" to "A", "Â" to "A", "Ấ" to "A", "Ậ" to "A", "Ầ" to "A", "Ẩ" to "A", "Ẫ" to "A", "Ä" to "A", "Ǟ" to "A", "Ȧ" to "A", "Ǡ" to "A", "Ạ" to "A", "Ȁ" to "A", "À" to "A", "Ả" to "A", "Ȃ" to "A", "Ā" to "A", "Ą" to "A", "Å" to "A", "Ǻ" to "A", "Ḁ" to "A", "Ⱥ" to "A", "Ã" to "A", "Ꜳ" to "AA", "Æ" to "AE", "Ǽ" to "AE", "Ǣ" to "AE", "Ꜵ" to "AO", "Ꜷ" to "AU", "Ꜹ" to "AV", "Ꜻ" to "AV", "Ꜽ" to "AY", "Ḃ" to "B", "Ḅ" to "B", "Ɓ" to "B", "Ḇ" to "B", "Ƀ" to "B", "Ƃ" to "B", "Ć" to "C", "Č" to "C", "Ç" to "C", "Ḉ" to "C", "Ĉ" to "C", "Ċ" to "C", "Ƈ" to "C", "Ȼ" to "C", "Ď" to "D", "Ḑ" to "D", "Ḓ" to "D", "Ḋ" to "D", "Ḍ" to "D", "Ɗ" to "D", "Ḏ" to "D", "ǲ" to "D", "ǅ" to "D", "Đ" to "D", "Ƌ" to "D", "Ǳ" to "DZ", "Ǆ" to "DZ", "É" to "E", "Ĕ" to "E", "Ě" to "E", "Ȩ" to "E", "Ḝ" to "E", "Ê" to "E", "Ế" to "E", "Ệ" to "E", "Ề" to "E", "Ể" to "E", "Ễ" to "E", "Ḙ" to "E", "Ë" to "E", "Ė" to "E", "Ẹ" to "E", "Ȅ" to "E", "È" to "E", "Ẻ" to "E", "Ȇ" to "E", "Ē" to "E", "Ḗ" to "E", "Ḕ" to "E", "Ę" to "E", "Ɇ" to "E", "Ẽ" to "E", "Ḛ" to "E", "Ꝫ" to "ET", "Ḟ" to "F", "Ƒ" to "F", "Ǵ" to "G", "Ğ" to "G", "Ǧ" to "G", "Ģ" to "G", "Ĝ" to "G", "Ġ" to "G", "Ɠ" to "G", "Ḡ" to "G", "Ǥ" to "G", "Ḫ" to "H", "Ȟ" to "H", "Ḩ" to "H", "Ĥ" to "H", "Ⱨ" to "H", "Ḧ" to "H", "Ḣ" to "H", "Ḥ" to "H", "Ħ" to "H", "Í" to "I", "Ĭ" to "I", "Ǐ" to "I", "Î" to "I", "Ï" to "I", "Ḯ" to "I", "İ" to "I", "Ị" to "I", "Ȉ" to "I", "Ì" to "I", "Ỉ" to "I", "Ȋ" to "I", "Ī" to "I", "Į" to "I", "Ɨ" to "I", "Ĩ" to "I", "Ḭ" to "I", "Ꝺ" to "D", "Ꝼ" to "F", "Ᵹ" to "G", "Ꞃ" to "R", "Ꞅ" to "S", "Ꞇ" to "T", "Ꝭ" to "IS", "Ĵ" to "J", "Ɉ" to "J", "Ḱ" to "K", "Ǩ" to "K", "Ķ" to "K", "Ⱪ" to "K", "Ꝃ" to "K", "Ḳ" to "K", "Ƙ" to "K", "Ḵ" to "K", "Ꝁ" to "K", "Ꝅ" to "K", "Ĺ" to "L", "Ƚ" to "L", "Ľ" to "L", "Ļ" to "L", "Ḽ" to "L", "Ḷ" to "L", "Ḹ" to "L", "Ⱡ" to "L", "Ꝉ" to "L", "Ḻ" to "L", "Ŀ" to "L", "Ɫ" to "L", "ǈ" to "L", "Ł" to "L", "Ǉ" to "LJ", "Ḿ" to "M", "Ṁ" to "M", "Ṃ" to "M", "Ɱ" to "M", "Ń" to "N", "Ň" to "N", "Ņ" to "N", "Ṋ" to "N", "Ṅ" to "N", "Ṇ" to "N", "Ǹ" to "N", "Ɲ" to "N", "Ṉ" to "N", "Ƞ" to "N", "ǋ" to "N", "Ñ" to "N", "Ǌ" to "NJ", "Ó" to "O", "Ŏ" to "O", "Ǒ" to "O", "Ô" to "O", "Ố" to "O", "Ộ" to "O", "Ồ" to "O", "Ổ" to "O", "Ỗ" to "O", "Ö" to "O", "Ȫ" to "O", "Ȯ" to "O", "Ȱ" to "O", "Ọ" to "O", "Ő" to "O", "Ȍ" to "O", "Ò" to "O", "Ỏ" to "O", "Ơ" to "O", "Ớ" to "O", "Ợ" to "O", "Ờ" to "O", "Ở" to "O", "Ỡ" to "O", "Ȏ" to "O", "Ꝋ" to "O", "Ꝍ" to "O", "Ō" to "O", "Ṓ" to "O", "Ṑ" to "O", "Ɵ" to "O", "Ǫ" to "O", "Ǭ" to "O", "Ø" to "O", "Ǿ" to "O", "Õ" to "O", "Ṍ" to "O", "Ṏ" to "O", "Ȭ" to "O", "Ƣ" to "OI", "Ꝏ" to "OO", "Ɛ" to "E", "Ɔ" to "O", "Ȣ" to "OU", "Ṕ" to "P", "Ṗ" to "P", "Ꝓ" to "P", "Ƥ" to "P", "Ꝕ" to "P", "Ᵽ" to "P", "Ꝑ" to "P", "Ꝙ" to "Q", "Ꝗ" to "Q", "Ŕ" to "R", "Ř" to "R", "Ŗ" to "R", "Ṙ" to "R", "Ṛ" to "R", "Ṝ" to "R", "Ȑ" to "R", "Ȓ" to "R", "Ṟ" to "R", "Ɍ" to "R", "Ɽ" to "R", "Ꜿ" to "C", "Ǝ" to "E", "Ś" to "S", "Ṥ" to "S", "Š" to "S", "Ṧ" to "S", "Ş" to "S", "Ŝ" to "S", "Ș" to "S", "Ṡ" to "S", "Ṣ" to "S", "Ṩ" to "S", "Ť" to "T", "Ţ" to "T", "Ṱ" to "T", "Ț" to "T", "Ⱦ" to "T", "Ṫ" to "T", "Ṭ" to "T", "Ƭ" to "T", "Ṯ" to "T", "Ʈ" to "T", "Ŧ" to "T", "Ɐ" to "A", "Ꞁ" to "L", "Ɯ" to "M", "Ʌ" to "V", "Ꜩ" to "TZ", "Ú" to "U", "Ŭ" to "U", "Ǔ" to "U", "Û" to "U", "Ṷ" to "U", "Ü" to "U", "Ǘ" to "U", "Ǚ" to "U", "Ǜ" to "U", "Ǖ" to "U", "Ṳ" to "U", "Ụ" to "U", "Ű" to "U", "Ȕ" to "U", "Ù" to "U", "Ủ" to "U", "Ư" to "U", "Ứ" to "U", "Ự" to "U", "Ừ" to "U", "Ử" to "U", "Ữ" to "U", "Ȗ" to "U", "Ū" to "U", "Ṻ" to "U", "Ų" to "U", "Ů" to "U", "Ũ" to "U", "Ṹ" to "U", "Ṵ" to "U", "Ꝟ" to "V", "Ṿ" to "V", "Ʋ" to "V", "Ṽ" to "V", "Ꝡ" to "VY", "Ẃ" to "W", "Ŵ" to "W", "Ẅ" to "W", "Ẇ" to "W", "Ẉ" to "W", "Ẁ" to "W", "Ⱳ" to "W", "Ẍ" to "X", "Ẋ" to "X", "Ý" to "Y", "Ŷ" to "Y", "Ÿ" to "Y", "Ẏ" to "Y", "Ỵ" to "Y", "Ỳ" to "Y", "Ƴ" to "Y", "Ỷ" to "Y", "Ỿ" to "Y", "Ȳ" to "Y", "Ɏ" to "Y", "Ỹ" to "Y", "Ź" to "Z", "Ž" to "Z", "Ẑ" to "Z", "Ⱬ" to "Z", "Ż" to "Z", "Ẓ" to "Z", "Ȥ" to "Z", "Ẕ" to "Z", "Ƶ" to "Z", "Ĳ" to "IJ", "Œ" to "OE", "ᴀ" to "A", "ᴁ" to "AE", "ʙ" to "B", "ᴃ" to "B", "ᴄ" to "C", "ᴅ" to "D", "ᴇ" to "E", "ꜰ" to "F", "ɢ" to "G", "ʛ" to "G", "ʜ" to "H", "ɪ" to "I", "ʁ" to "R", "ᴊ" to "J", "ᴋ" to "K", "ʟ" to "L", "ᴌ" to "L", "ᴍ" to "M", "ɴ" to "N", "ᴏ" to "O", "ɶ" to "OE", "ᴐ" to "O", "ᴕ" to "OU", "ᴘ" to "P", "ʀ" to "R", "ᴎ" to "N", "ᴙ" to "R", "ꜱ" to "S", "ᴛ" to "T", "ⱻ" to "E", "ᴚ" to "R", "ᴜ" to "U", "ᴠ" to "V", "ᴡ" to "W", "ʏ" to "Y", "ᴢ" to "Z", "á" to "a", "ă" to "a", "ắ" to "a", "ặ" to "a", "ằ" to "a", "ẳ" to "a", "ẵ" to "a", "ǎ" to "a", "â" to "a", "ấ" to "a", "ậ" to "a", "ầ" to "a", "ẩ" to "a", "ẫ" to "a", "ä" to "a", "ǟ" to "a", "ȧ" to "a", "ǡ" to "a", "ạ" to "a", "ȁ" to "a", "à" to "a", "ả" to "a", "ȃ" to "a", "ā" to "a", "ą" to "a", "ᶏ" to "a", "ẚ" to "a", "å" to "a", "ǻ" to "a", "ḁ" to "a", "ⱥ" to "a", "ã" to "a", "ꜳ" to "aa", "æ" to "ae", "ǽ" to "ae", "ǣ" to "ae", "ꜵ" to "ao", "ꜷ" to "au", "ꜹ" to "av", "ꜻ" to "av", "ꜽ" to "ay", "ḃ" to "b", "ḅ" to "b", "ɓ" to "b", "ḇ" to "b", "ᵬ" to "b", "ᶀ" to "b", "ƀ" to "b", "ƃ" to "b", "ɵ" to "o", "ć" to "c", "č" to "c", "ç" to "c", "ḉ" to "c", "ĉ" to "c", "ɕ" to "c", "ċ" to "c", "ƈ" to "c", "ȼ" to "c", "ď" to "d", "ḑ" to "d", "ḓ" to "d", "ȡ" to "d", "ḋ" to "d", "ḍ" to "d", "ɗ" to "d", "ᶑ" to "d", "ḏ" to "d", "ᵭ" to "d", "ᶁ" to "d", "đ" to "d", "ɖ" to "d", "ƌ" to "d", "ı" to "i", "ȷ" to "j", "ɟ" to "j", "ʄ" to "j", "ǳ" to "dz", "ǆ" to "dz", "é" to "e", "ĕ" to "e", "ě" to "e", "ȩ" to "e", "ḝ" to "e", "ê" to "e", "ế" to "e", "ệ" to "e", "ề" to "e", "ể" to "e", "ễ" to "e", "ḙ" to "e", "ë" to "e", "ė" to "e", "ẹ" to "e", "ȅ" to "e", "è" to "e", "ẻ" to "e", "ȇ" to "e", "ē" to "e", "ḗ" to "e", "ḕ" to "e", "ⱸ" to "e", "ę" to "e", "ᶒ" to "e", "ɇ" to "e", "ẽ" to "e", "ḛ" to "e", "ꝫ" to "et", "ḟ" to "f", "ƒ" to "f", "ᵮ" to "f", "ᶂ" to "f", "ǵ" to "g", "ğ" to "g", "ǧ" to "g", "ģ" to "g", "ĝ" to "g", "ġ" to "g", "ɠ" to "g", "ḡ" to "g", "ᶃ" to "g", "ǥ" to "g", "ḫ" to "h", "ȟ" to "h", "ḩ" to "h", "ĥ" to "h", "ⱨ" to "h", "ḧ" to "h", "ḣ" to "h", "ḥ" to "h", "ɦ" to "h", "ẖ" to "h", "ħ" to "h", "ƕ" to "hv", "í" to "i", "ĭ" to "i", "ǐ" to "i", "î" to "i", "ï" to "i", "ḯ" to "i", "ị" to "i", "ȉ" to "i", "ì" to "i", "ỉ" to "i", "ȋ" to "i", "ī" to "i", "į" to "i", "ᶖ" to "i", "ɨ" to "i", "ĩ" to "i", "ḭ" to "i", "ꝺ" to "d", "ꝼ" to "f", "ᵹ" to "g", "ꞃ" to "r", "ꞅ" to "s", "ꞇ" to "t", "ꝭ" to "is", "ǰ" to "j", "ĵ" to "j", "ʝ" to "j", "ɉ" to "j", "ḱ" to "k", "ǩ" to "k", "ķ" to "k", "ⱪ" to "k", "ꝃ" to "k", "ḳ" to "k", "ƙ" to "k", "ḵ" to "k", "ᶄ" to "k", "ꝁ" to "k", "ꝅ" to "k", "ĺ" to "l", "ƚ" to "l", "ɬ" to "l", "ľ" to "l", "ļ" to "l", "ḽ" to "l", "ȴ" to "l", "ḷ" to "l", "ḹ" to "l", "ⱡ" to "l", "ꝉ" to "l", "ḻ" to "l", "ŀ" to "l", "ɫ" to "l", "ᶅ" to "l", "ɭ" to "l", "ł" to "l", "ǉ" to "lj", "ſ" to "s", "ẜ" to "s", "ẛ" to "s", "ẝ" to "s", "ḿ" to "m", "ṁ" to "m", "ṃ" to "m", "ɱ" to "m", "ᵯ" to "m", "ᶆ" to "m", "ń" to "n", "ň" to "n", "ņ" to "n", "ṋ" to "n", "ȵ" to "n", "ṅ" to "n", "ṇ" to "n", "ǹ" to "n", "ɲ" to "n", "ṉ" to "n", "ƞ" to "n", "ᵰ" to "n", "ᶇ" to "n", "ɳ" to "n", "ñ" to "n", "ǌ" to "nj", "ó" to "o", "ŏ" to "o", "ǒ" to "o", "ô" to "o", "ố" to "o", "ộ" to "o", "ồ" to "o", "ổ" to "o", "ỗ" to "o", "ö" to "o", "ȫ" to "o", "ȯ" to "o", "ȱ" to "o", "ọ" to "o", "ő" to "o", "ȍ" to "o", "ò" to "o", "ỏ" to "o", "ơ" to "o", "ớ" to "o", "ợ" to "o", "ờ" to "o", "ở" to "o", "ỡ" to "o", "ȏ" to "o", "ꝋ" to "o", "ꝍ" to "o", "ⱺ" to "o", "ō" to "o", "ṓ" to "o", "ṑ" to "o", "ǫ" to "o", "ǭ" to "o", "ø" to "o", "ǿ" to "o", "õ" to "o", "ṍ" to "o", "ṏ" to "o", "ȭ" to "o", "ƣ" to "oi", "ꝏ" to "oo", "ɛ" to "e", "ᶓ" to "e", "ɔ" to "o", "ᶗ" to "o", "ȣ" to "ou", "ṕ" to "p", "ṗ" to "p", "ꝓ" to "p", "ƥ" to "p", "ᵱ" to "p", "ᶈ" to "p", "ꝕ" to "p", "ᵽ" to "p", "ꝑ" to "p", "ꝙ" to "q", "ʠ" to "q", "ɋ" to "q", "ꝗ" to "q", "ŕ" to "r", "ř" to "r", "ŗ" to "r", "ṙ" to "r", "ṛ" to "r", "ṝ" to "r", "ȑ" to "r", "ɾ" to "r", "ᵳ" to "r", "ȓ" to "r", "ṟ" to "r", "ɼ" to "r", "ᵲ" to "r", "ᶉ" to "r", "ɍ" to "r", "ɽ" to "r", "ↄ" to "c", "ꜿ" to "c", "ɘ" to "e", "ɿ" to "r", "ś" to "s", "ṥ" to "s", "š" to "s", "ṧ" to "s", "ş" to "s", "ŝ" to "s", "ș" to "s", "ṡ" to "s", "ṣ" to "s", "ṩ" to "s", "ʂ" to "s", "ᵴ" to "s", "ᶊ" to "s", "ȿ" to "s", "ɡ" to "g", "ᴑ" to "o", "ᴓ" to "o", "ᴝ" to "u", "ť" to "t", "ţ" to "t", "ṱ" to "t", "ț" to "t", "ȶ" to "t", "ẗ" to "t", "ⱦ" to "t", "ṫ" to "t", "ṭ" to "t", "ƭ" to "t", "ṯ" to "t", "ᵵ" to "t", "ƫ" to "t", "ʈ" to "t", "ŧ" to "t", "ᵺ" to "th", "ɐ" to "a", "ᴂ" to "ae", "ǝ" to "e", "ᵷ" to "g", "ɥ" to "h", "ʮ" to "h", "ʯ" to "h", "ᴉ" to "i", "ʞ" to "k", "ꞁ" to "l", "ɯ" to "m", "ɰ" to "m", "ᴔ" to "oe", "ɹ" to "r", "ɻ" to "r", "ɺ" to "r", "ⱹ" to "r", "ʇ" to "t", "ʌ" to "v", "ʍ" to "w", "ʎ" to "y", "ꜩ" to "tz", "ú" to "u", "ŭ" to "u", "ǔ" to "u", "û" to "u", "ṷ" to "u", "ü" to "u", "ǘ" to "u", "ǚ" to "u", "ǜ" to "u", "ǖ" to "u", "ṳ" to "u", "ụ" to "u", "ű" to "u", "ȕ" to "u", "ù" to "u", "ủ" to "u", "ư" to "u", "ứ" to "u", "ự" to "u", "ừ" to "u", "ử" to "u", "ữ" to "u", "ȗ" to "u", "ū" to "u", "ṻ" to "u", "ų" to "u", "ᶙ" to "u", "ů" to "u", "ũ" to "u", "ṹ" to "u", "ṵ" to "u", "ᵫ" to "ue", "ꝸ" to "um", "ⱴ" to "v", "ꝟ" to "v", "ṿ" to "v", "ʋ" to "v", "ᶌ" to "v", "ⱱ" to "v", "ṽ" to "v", "ꝡ" to "vy", "ẃ" to "w", "ŵ" to "w", "ẅ" to "w", "ẇ" to "w", "ẉ" to "w", "ẁ" to "w", "ⱳ" to "w", "ẘ" to "w", "ẍ" to "x", "ẋ" to "x", "ᶍ" to "x", "ý" to "y", "ŷ" to "y", "ÿ" to "y", "ẏ" to "y", "ỵ" to "y", "ỳ" to "y", "ƴ" to "y", "ỷ" to "y", "ỿ" to "y", "ȳ" to "y", "ẙ" to "y", "ɏ" to "y", "ỹ" to "y", "ź" to "z", "ž" to "z", "ẑ" to "z", "ʑ" to "z", "ⱬ" to "z", "ż" to "z", "ẓ" to "z", "ȥ" to "z", "ẕ" to "z", "ᵶ" to "z", "ᶎ" to "z", "ʐ" to "z", "ƶ" to "z", "ɀ" to "z", "ﬀ" to "ff", "ﬃ" to "ffi", "ﬄ" to "ffl", "ﬁ" to "fi", "ﬂ" to "fl", "ĳ" to "ij", "œ" to "oe", "ﬆ" to "st", "ₐ" to "a", "ₑ" to "e", "ᵢ" to "i", "ⱼ" to "j", "ₒ" to "o", "ᵣ" to "r", "ᵤ" to "u", "ᵥ" to "v", "ₓ" to "x")
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

    suspend fun insertHighscore(highscore: Int, context: Context) = coroutineScope {
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
                println("Error") //TODO Handle this
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
                val fetchedWord = response.body()?.get(0)!! // Random English word here
                _randomWord.value = fetchedWord

                getEnglishDefinition(fetchedWord)
            }

            override fun onFailure(call: Call<List<String>>, t: Throwable) {
                println("Error") //TODO Handle this
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
                println("Error") //TODO Handle this
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
                var englishDefinitions = ArrayList<String>()
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
                                println("Error") //TODO Handle this
                            }
                        }
                    } else {
                        println("Error") //TODO Handle this
                    }
                } else {
                    println("Error") //TODO Handle this
                }
                if (englishDefinitions != null) {
                    _definitions.value = englishDefinitions
                } else {
                    _definitions.value = ArrayList()
                }
                generateNewWord("English")
            }

            override fun onFailure(call: Call<List<English>?>, t: Throwable) {
                println("Error") //TODO Handle this
            }
        })
    }
}