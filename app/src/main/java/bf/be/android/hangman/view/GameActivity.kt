package bf.be.android.hangman.view

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.DialogInterface.OnShowListener
import android.content.Intent
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.preference.PreferenceManager
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import bf.be.android.hangman.R
import bf.be.android.hangman.databinding.ActivityGameBinding
import bf.be.android.hangman.model.GameRound
import bf.be.android.hangman.model.Word
import bf.be.android.hangman.model.adapters.DefinitionsAdapter
import bf.be.android.hangman.model.adapters.HighscoresAdapter
import bf.be.android.hangman.model.dal.entities.Avatar
import bf.be.android.hangman.model.dal.entities.Highscore
import bf.be.android.hangman.model.dal.entities.Language
import bf.be.android.hangman.viewModel.MainViewModel
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


class GameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGameBinding
    private lateinit var toggle: ActionBarDrawerToggle

    companion object {
        var appBarMenu: Menu? = null
        var activeRound = false
        var timer10: CountDownTimer? = null
    }

    //Create a ViewModel
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // View binding
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialise all bindings
        initialiseBindings()

        initViewModel()

        // Shared preferences
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)

        // Initialises UI
        initialiseUi()

        // Creates viewModel game round object
        viewModel.createNewGameRound()

        lifecycleScope.launch {
            // Creates viewModel avatar list
            val allAvatars = viewModel.findAllAvatars(applicationContext)
            viewModel.avatarList = MutableLiveData(allAvatars)

            // Creates viewModel language list
            val allLanguages = viewModel.findAllLanguages(applicationContext)
            viewModel.languageList = MutableLiveData(allLanguages)

            // Creates user object
            viewModel.createUser(applicationContext, prefs.getString("userId", "")!!.toLong(), appBarMenu!!)
            actionBar?.title = viewModel.activeUser?.value!!.username
            supportActionBar?.title = viewModel.activeUser?.value!!.username

            // Check if user has chosen an avatar yet and if not, prompt for it (pass initial check parameter as true so that language check is also performed)
            if (viewModel.activeUser?.value!!.avatarId == 0) { // Open window to choose avatar
                chooseAvatars(true)
            } else { // Starts with the avatar the user has in the database
                // Update viewModel active avatar
                val tempAvatar: Avatar = viewModel.avatarList.value!![viewModel.activeUser?.value?.avatarId!! - 1]
                viewModel._activeAvatar = MutableLiveData(tempAvatar)
            }
        }

        // Options side menu
        binding.apply {
            toggle = ActionBarDrawerToggle(this@GameActivity, drawerLayout, R.string.open, R.string.close)
            drawerLayout.addDrawerListener(MyDrawerListener())
            drawerLayout.addDrawerListener(toggle)
            toggle.syncState()

            supportActionBar?.setDisplayHomeAsUpEnabled(true)

            optionsSideMenu.setNavigationItemSelectedListener {
                drawerLayout.closeDrawers()
                when (it.itemId) {
                    R.id.languageItem -> {
                        lifecycleScope.launch {
                            chooseLanguage()
                        }
                    }
                    R.id.avatarItem -> {
                        lifecycleScope.launch {
                            chooseAvatars(false)
                        }
                    }
                    R.id.usernameItem -> {
                        lifecycleScope.launch {
                            chooseUsername()
                        }
                    }
                    R.id.passwordItem -> {
                        lifecycleScope.launch {
                            choosePassword()
                        }
                    }
                    R.id.helpItem -> {
                        lifecycleScope.launch {
                            displayHelp()
                        }
                    }
                    R.id.deleteItem -> {
                        deleteAccount()
                    }
                    R.id.logOutItem -> {
                        logout()
                    }
                }
                true
            }
        }
    }

    // --- Bindings ---
    // Initialises all bindings
    private fun initialiseBindings() {
        initialiseKeyboardBinding()
        initialiseRoundBtnBinding()
        initialiseHintBtnBinding()
        initialiseExchangeBtnBinding()
        initialiseAbandonBtnBinding()
    }

    // Keyboard binding
    private fun initialiseKeyboardBinding() {
        binding.keyboardA.setOnClickListener { keyboardPressed("A", binding.keyboardA) }
        binding.keyboardB.setOnClickListener { keyboardPressed("B", binding.keyboardB) }
        binding.keyboardC.setOnClickListener { keyboardPressed("C", binding.keyboardC) }
        binding.keyboardD.setOnClickListener { keyboardPressed("D", binding.keyboardD) }
        binding.keyboardE.setOnClickListener { keyboardPressed("E", binding.keyboardE) }
        binding.keyboardF.setOnClickListener { keyboardPressed("F", binding.keyboardF) }
        binding.keyboardG.setOnClickListener { keyboardPressed("G", binding.keyboardG) }
        binding.keyboardH.setOnClickListener { keyboardPressed("H", binding.keyboardH) }
        binding.keyboardI.setOnClickListener { keyboardPressed("I", binding.keyboardI) }
        binding.keyboardJ.setOnClickListener { keyboardPressed("J", binding.keyboardJ) }
        binding.keyboardK.setOnClickListener { keyboardPressed("K", binding.keyboardK) }
        binding.keyboardL.setOnClickListener { keyboardPressed("L", binding.keyboardL) }
        binding.keyboardM.setOnClickListener { keyboardPressed("M", binding.keyboardM) }
        binding.keyboardN.setOnClickListener { keyboardPressed("N", binding.keyboardN) }
        binding.keyboardO.setOnClickListener { keyboardPressed("O", binding.keyboardO) }
        binding.keyboardP.setOnClickListener { keyboardPressed("P", binding.keyboardP) }
        binding.keyboardQ.setOnClickListener { keyboardPressed("Q", binding.keyboardQ) }
        binding.keyboardR.setOnClickListener { keyboardPressed("R", binding.keyboardR) }
        binding.keyboardS.setOnClickListener { keyboardPressed("S", binding.keyboardS) }
        binding.keyboardT.setOnClickListener { keyboardPressed("T", binding.keyboardT) }
        binding.keyboardU.setOnClickListener { keyboardPressed("U", binding.keyboardU) }
        binding.keyboardV.setOnClickListener { keyboardPressed("V", binding.keyboardV) }
        binding.keyboardW.setOnClickListener { keyboardPressed("W", binding.keyboardW) }
        binding.keyboardX.setOnClickListener { keyboardPressed("X", binding.keyboardX) }
        binding.keyboardY.setOnClickListener { keyboardPressed("Y", binding.keyboardY) }
        binding.keyboardZ.setOnClickListener { keyboardPressed("Z", binding.keyboardZ) }
    }

    // New round button binding
    private fun initialiseRoundBtnBinding() {
        binding.newRoundBtn.setOnClickListener {
            // Button click sound
            val soundFile = R.raw.click_button
            playSound(soundFile)

            initiateNewRound(it)
        }
    }

    // Help button binding
    private fun initialiseHintBtnBinding() {
        binding.hintBtn.setOnClickListener {
            // Button click sound
            val soundFile = R.raw.click_button
            playSound(soundFile)

            showHelpMenu(it)
        }
    }

    // Exchange button binding
    private fun initialiseExchangeBtnBinding() {
        binding.exchangeBtn.setOnClickListener {
            // Button click sound
            val soundFile = R.raw.click_button
            playSound(soundFile)

            showExchangeMenu(it)
        }
    }

    // Abandon button binding
    private fun initialiseAbandonBtnBinding() {
        binding.abandonBtn.setOnClickListener {
            // Button click sound
            val soundFile = R.raw.click_button
            playSound(soundFile)

            showAbandonGameRound()
        }
    }

    // --- Initialisations ---
    // Initialises the UI
    private fun initialiseUi() {
        hideAvatarGraphics()
        hideAllAnimations()
        hideHintBtn()
        hideExchangeBtn()
        hideAbandonBtn()
        Glide.with(this).load(R.drawable.waiting).into(binding.waitingPlaceholder)
    }

    // --- ViewModel ---
    //Initialises view model observables
    private fun initViewModel() {
        // When view model word changes, startNewRoundUi is called
        viewModel.word.observe(this, this::validateRandomWord)
    }

    // --- Right side menus ---
    // Show help menu
    private fun showHelpMenu(view: View) {
        val layoutInflater = LayoutInflater.from(this)
        val helpMenuView: View = layoutInflater.inflate(R.layout.help_menu_layout, null)
        val builder = AlertDialog.Builder(this)

        val textView = TextView(this)
        textView.setText(R.string.buy_help)
        textView.setPadding(20, 30, 20, 30)
        textView.textSize = 25f
        textView.setTextColor(ContextCompat.getColor(this, R.color.menu_text_colour))

        builder.setCustomTitle(textView)

        val buyLetterBtn = helpMenuView.findViewById(R.id.letterBuyBtn) as Button
        val buyDefinitionBtn = helpMenuView.findViewById(R.id.definitionBuyBtn) as Button
        val buyBodyPartBtn = helpMenuView.findViewById(R.id.bodyPartBuyBtn) as Button

        builder.setView(helpMenuView)
        val dialog = builder.create()

        dialog.window?.decorView?.setBackgroundResource(R.drawable.menu_shape)
        dialog.show()

        val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
        val height = ViewGroup.LayoutParams.WRAP_CONTENT

        if (viewModel.activeUser?.value!!.coins < 150 || !activeRound) {
            buyLetterBtn.isEnabled = false
            buyLetterBtn.backgroundTintList = this.resources.getColorStateList(R.color.inactive_state)
        } else {
            buyLetterBtn.isActivated = true
        }

        if (viewModel.activeUser?.value!!.banknotes < 10 || !activeRound || viewModel.word.value?.revealedDefinitions!!.isEmpty()) {
            buyDefinitionBtn.isEnabled = false
            buyDefinitionBtn.backgroundTintList = this.resources.getColorStateList(R.color.inactive_state)
        } else {
            buyDefinitionBtn.isActivated = true
        }

        if (viewModel.activeUser?.value!!.diamonds < 2 || !activeRound || viewModel.activeGameRound?.value!!.letterMisses == 0) {
            buyBodyPartBtn.isEnabled = false
            buyBodyPartBtn.backgroundTintList = this.resources.getColorStateList(R.color.inactive_state)
        } else {
            buyBodyPartBtn.isActivated = true
        }

        dialog.window?.setLayout(width, height)

        buyLetterBtn.setOnClickListener {
            // Button click sound
            val soundFile1 = R.raw.click_button
            playSound(soundFile1)

            // Chooses a random letter missing from the displayed word
            val missingLetters = ArrayList<String>()
            for (i in 0..viewModel.word.value!!.displayedWord.length - 1) {
                if (viewModel.word.value!!.displayedWord[i].toString().equals("*")) {
                    missingLetters.add(viewModel.word.value!!.hiddenWord[i].toString())
                }
            }

            // Formats the letter correctly
            val randomMissingLetter = viewModel.prepareCharacter(missingLetters[(0..(missingLetters.size - 1)).random()].uppercase())

            // Get correct keyboard key and simulate a key press
            val keyboardKey: Button = findViewById(resources.getIdentifier("keyboard" + randomMissingLetter, "id", packageName))
            keyboardPressed(randomMissingLetter, keyboardKey)

            // Reveal hint sound
            val soundFile2 = R.raw.reveal_hint
            playSound(soundFile2)

            viewModel.activeUser?.value!!.coins -= 150
            updateAssetBar()

            dialog.cancel()
        }
        buyDefinitionBtn.setOnClickListener {
            // Button click sound
            val soundFile1 = R.raw.click_button
            playSound(soundFile1)

            // Gets a random index position in the revealed definitions array
            val revealedDefinitionsNr = viewModel.word.value!!.revealedDefinitions?.size!!
            val i = (0..(revealedDefinitionsNr - 1)).random()
            val revealedDefinition = viewModel.word.value!!.revealedDefinitions?.get(i)
            viewModel.word.value!!.revealedDefinitions?.removeAt(i)

            revealDefinition(revealedDefinition!!)

            // Reveal hint sound
            val soundFile2 = R.raw.reveal_hint
            playSound(soundFile2)

            viewModel.activeUser?.value!!.banknotes -= 10
            updateAssetBar()

            dialog.cancel()
        }
        buyBodyPartBtn.setOnClickListener {
            // Button click sound
            val soundFile1 = R.raw.click_button
            playSound(soundFile1)

            viewModel.activeGameRound?.value!!.letterMisses--
            lifecycleScope.launch {
                updateAvatar()
            }

            // Reveal hint sound
            val soundFile2 = R.raw.reveal_hint
            playSound(soundFile2)

            viewModel.activeUser?.value!!.diamonds -= 2
            updateAssetBar()
            updateAssetBar()

            dialog.cancel()
        }
    }

    // Reveals one definition
    private fun revealDefinition(definition: String) {
        val layoutInflater = LayoutInflater.from(this)
        val singleDefinitionView: View = layoutInflater.inflate(R.layout.single_definition_layout, null)
        val builder = AlertDialog.Builder(this)

        val singleDefinitionDefinition = singleDefinitionView.findViewById(R.id.singleDefinition_definition) as TextView
        val singleDefinitionBtn = singleDefinitionView.findViewById(R.id.singleDefinitionBtn) as Button

        singleDefinitionDefinition.setText(definition)

        builder.setView(singleDefinitionView)
        val dialog = builder.create()

        dialog.window?.decorView?.setBackgroundResource(R.drawable.menu_shape)
        dialog.show()

        singleDefinitionBtn.setOnClickListener {
            // Button click sound
            val soundFile = R.raw.click_button
            playSound(soundFile)

            dialog.cancel()
        }
    }

    // Show exchange menu
    private fun showExchangeMenu(view: View) {
        val layoutInflater = LayoutInflater.from(this)
        val exchangeMenuView: View = layoutInflater.inflate(R.layout.exchange_menu_layout, null)
        val builder = AlertDialog.Builder(this)

        val textView = TextView(this)
        textView.setText(R.string.exchange_resources)
        textView.setPadding(20, 30, 20, 30)
        textView.textSize = 25f
        textView.setTextColor(ContextCompat.getColor(this, R.color.menu_text_colour))

        builder.setCustomTitle(textView)

        val buyBanknote = exchangeMenuView.findViewById(R.id.exchangeBuyBanknoteBtn) as Button
        val buyDiamond = exchangeMenuView.findViewById(R.id.exchangeBuyDiamondBtn) as Button
        val buyLife = exchangeMenuView.findViewById(R.id.exchangeBuyLifeBtn) as Button

        builder.setView(exchangeMenuView)
        val dialog = builder.create()

        dialog.window?.decorView?.setBackgroundResource(R.drawable.menu_shape)
        dialog.show()

        val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
        val height = ViewGroup.LayoutParams.WRAP_CONTENT

        if (viewModel.activeUser?.value!!.coins < 25) {
            buyBanknote.isEnabled = false
            buyBanknote.backgroundTintList = this.resources.getColorStateList(R.color.inactive_state)
        } else {
            buyBanknote.isActivated = true
        }

        if (viewModel.activeUser?.value!!.banknotes < 10) {
            buyDiamond.isEnabled = false
            buyDiamond.backgroundTintList = this.resources.getColorStateList(R.color.inactive_state)
        } else {
            buyDiamond.isActivated = true
        }

        if (viewModel.activeUser?.value!!.diamonds < 3) {
            buyLife.isEnabled = false
            buyLife.backgroundTintList = this.resources.getColorStateList(R.color.inactive_state)
        } else {
            buyLife.isActivated = true
        }

        dialog.window?.setLayout(width, height)

        buyBanknote.setOnClickListener {
            // Button click sound
            val soundFile1 = R.raw.click_button
            playSound(soundFile1)

            viewModel.activeUser?.value!!.coins -= 25
            viewModel.activeUser?.value!!.banknotes += 1

            // Banknotes sound
            val soundFile2 = R.raw.banknotes
            playSound(soundFile2)

            updateAssetBar()
            dialog.cancel()
        }
        buyDiamond.setOnClickListener {
            // Button click sound
            val soundFile1 = R.raw.click_button
            playSound(soundFile1)
            viewModel.activeUser?.value!!.banknotes -= 10
            viewModel.activeUser?.value!!.diamonds += 1

            // Diamonds sound
            val soundFile2 = R.raw.diamonds
            playSound(soundFile2)

            updateAssetBar()
            dialog.cancel()
        }
        buyLife.setOnClickListener {
            // Button click sound
            val soundFile1 = R.raw.click_button
            playSound(soundFile1)
            viewModel.activeUser?.value!!.diamonds -= 3
            viewModel.activeUser?.value!!.lives += 1

            // Lives sound
            val soundFile2 = R.raw.buy_lives
            playSound(soundFile2)

            updateAssetBar()
            dialog.cancel()
        }
    }

    // Displays window where the user can abandon the current game round
    private fun showAbandonGameRound() {
        val dialogbuider = AlertDialog.Builder(this)

        val textView = TextView(this)
        textView.setText(R.string.abandon_round)
        textView.setPadding(20, 30, 20, 30)
        textView.textSize = 25f
        textView.setTextColor(ContextCompat.getColor(this, R.color.menu_text_colour))

        dialogbuider.setCustomTitle(textView)
        dialogbuider.setView(R.layout.abandon_round_layout)

        dialogbuider.setPositiveButton(R.string.yes) { dialog, _ ->
            // Button click sound
            val soundFile = R.raw.click_button
            playSound(soundFile)

            abandonGameRound()
            dialog.cancel()
        }
        dialogbuider.setNegativeButton(R.string.no) { dialog, _ ->
            // Button click sound
            val soundFile = R.raw.click_button
            playSound(soundFile)
            dialog.cancel()
        }

        val dialog = dialogbuider.create()
        dialog.show()
    }

    // Abandon round
    private fun abandonGameRound() {
        viewModel.activeGameRound?.value!!.letterMisses = 0
        viewModel.activeGameRound?.value!!.guessedLetters = 0
        viewModel.activeGameRound?.value!!.lettersGuessedConsecutively = 0
        viewModel.activeGameRound?.value!!.wordsGuessedConsecutively = 0
        activeRound = false
        resetKeyboard()
        viewModel.activeUser?.value!!.diamonds = 0
        viewModel.activeUser?.value!!.banknotes = 0
        viewModel.activeUser?.value!!.coins = 0
        viewModel.activeUser?.value!!.lives = 5
        viewModel.activeUser?.value!!.score = 0

        (timer10 as CountDownTimer).cancel()
        binding.potentialPrize.alpha = 0F

        updateAssetBar()
        hideKeyboard()
        hideAssetBar()
        hideHintBtn()
        hideExchangeBtn()
        hideAbandonBtn()
        hideDisplayedWord()
        hideAllAnimations()
        hideAvatarGraphics()
        binding.newRoundBtn.setText(R.string.new_round)
        showNewRoundBtn()

        // Abandon game round sound
        val soundFile = R.raw.abandon_round
        playSound(soundFile)
    }

    // --- Left side menu ---
    // Options menu listener
    private inner class MyDrawerListener : DrawerLayout.DrawerListener {
        override fun onDrawerOpened(drawerView: View) {}
        override fun onDrawerClosed(drawerView: View) {}
        override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}
        override fun onDrawerStateChanged(newState: Int) {
            if(newState == DrawerLayout.STATE_SETTLING) {
                val optionsMenu: DrawerLayout = findViewById(R.id.drawer_layout)
                if(optionsMenu.isDrawerOpen(GravityCompat.START)) {
                    // Close menu sound
                    val soundFile = R.raw.close_window
                    playSound(soundFile)
                } else {
                    // Open menu sound
                    val soundFile = R.raw.open_window
                    playSound(soundFile)
                }
            }
        }
    }

    // Handles sandwich icon state of options menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        toggle.onOptionsItemSelected(item)
        return super.onOptionsItemSelected(item)
    }

    // Displays window where the user can choose a language
    private fun chooseLanguage() {
        if (!activeRound) {
            val dialogbuider = AlertDialog.Builder(this)
            dialogbuider.setCancelable(false)

            val textView = TextView(this)
            textView.setText(R.string.choose_language)
            textView.setPadding(20, 30, 20, 30)
            textView.textSize = 25f
            textView.setTextColor(ContextCompat.getColor(this, R.color.menu_text_colour))

            dialogbuider.setCustomTitle(textView)
            val adapter: ArrayAdapter<Language> =
                object : ArrayAdapter<Language>(this, R.layout.language_list_item_layout, viewModel.languageList.value!!) {
                    var selectedPosition = 0
                    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                        var v = convertView
                        if (v == null) {
                            val vi =
                                getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                            v = vi.inflate(R.layout.language_list_item_layout, null)
                        }

                        val languageListImg = v!!.findViewById<ImageView>(R.id.languageListImg)
                        languageListImg.setImageResource(resources.getIdentifier(viewModel.languageList.value!![position].src, "drawable", packageName))

                        val tvLanguage: TextView = v.findViewById(R.id.tv_languageName)
                        tvLanguage.text = viewModel.languageList.value!![position].name

                        val r = v.findViewById<View>(R.id.radiobox_languageList) as RadioButton
                        r.isChecked = position == selectedPosition
                        r.tag = position
                        r.setOnClickListener { view ->
                            selectedPosition = view.tag as Int
                            notifyDataSetChanged()
                        }
                        viewModel.languageLastSelectedCheckbox.value = selectedPosition
                        return v
                    }
                }

            dialogbuider.setAdapter(adapter) { _: DialogInterface?, _: Int -> }

            dialogbuider.setPositiveButton("OK") { _: DialogInterface?, _: Int ->
                // Button click sound
                val soundFile = R.raw.click_button
                playSound(soundFile)

                // Update user object and db with the selected language
                val tempUser = viewModel.activeUser!!.value
                tempUser!!.languageId = viewModel.languageLastSelectedCheckbox.value!! + 1
                viewModel.updateUser(this, tempUser.id, tempUser)

                // Update viewModel active language
                val tempLanguage: Language = viewModel.languageList.value!![viewModel.languageLastSelectedCheckbox.value!!]
                viewModel._activeLanguage = MutableLiveData(tempLanguage)

                // Changes the language icon on the app bar
                if (viewModel.languageLastSelectedCheckbox.value == 0) {
                    appBarMenu?.getItem(0)?.icon = ContextCompat.getDrawable(this, R.drawable.france)
                } else if (viewModel.languageLastSelectedCheckbox.value == 1) {
                    appBarMenu?.getItem(0)?.icon = ContextCompat.getDrawable(this, R.drawable.uk)
                }
                appBarMenu?.getItem(0)?.isVisible = true
            }
            val dialog = dialogbuider.create()

            dialog.show()
        } else {
            Toast.makeText(this, R.string.not_available_during_round, Toast.LENGTH_LONG).show()
        }
    }

    // Displays window where the user can choose an avatar (on the first log in, it also redirects for the window where the user can choose a language)
    private fun chooseAvatars(initialCheck: Boolean) {
        if (!activeRound) {
            val dialogbuider = AlertDialog.Builder(this)
            dialogbuider.setCancelable(false)

            val textView = TextView(this)
            textView.setText(R.string.choose_avatar)
            textView.setPadding(20, 30, 20, 30)
            textView.textSize = 25f
            textView.setTextColor(ContextCompat.getColor(this, R.color.menu_text_colour))

            dialogbuider.setCustomTitle(textView)

            val adapter: ArrayAdapter<Avatar> =
                object : ArrayAdapter<Avatar>(
                    this,
                    R.layout.avatar_list_item_layout,
                    viewModel.avatarList.value!!
                ) {
                    var selectedPosition = 0
                    override fun getView(
                        position: Int,
                        convertView: View?,
                        parent: ViewGroup
                    ): View {
                        var v = convertView
                        if (v == null) {
                            val vi =
                                getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                            v = vi.inflate(R.layout.avatar_list_item_layout, null)
                        }

                        val avatarsListImg = v!!.findViewById<ImageView>(R.id.avatarsListImg)
                        avatarsListImg.setImageResource(
                            resources.getIdentifier(
                                viewModel.avatarList.value!![position].headShot,
                                "drawable",
                                packageName
                            )
                        )

                        val tvAvatarsName: TextView = v.findViewById(R.id.tv_avatarsName)
                        if (viewModel.avatarList.value!![position].headShot.startsWith("woman")) {
                            tvAvatarsName.text = resources.getText(R.string.woman).toString() + " " + (position + -11)
                        } else if (viewModel.avatarList.value!![position].headShot.startsWith("man")) {
                            tvAvatarsName.text = resources.getText(R.string.man).toString() + " " + (position + 1)
                        }

                        val r = v.findViewById<View>(R.id.radiobox_avatarsList) as RadioButton
                        r.isChecked = position == selectedPosition
                        r.tag = position
                        r.setOnClickListener { view ->
                            selectedPosition = view.tag as Int
                            notifyDataSetChanged()
                        }
                        viewModel.avatarLastSelectedCheckbox.value = selectedPosition
                        return v
                    }
                }

            dialogbuider.setAdapter(adapter) { _: DialogInterface?, _: Int -> }
            dialogbuider.setPositiveButton("OK") { _: DialogInterface?, _: Int ->
                // Button click sound
                val soundFile = R.raw.click_button
                playSound(soundFile)

                // Update user object and db with the selected avatar
                val tempUser = viewModel.activeUser!!.value
                tempUser!!.avatarId = viewModel.avatarLastSelectedCheckbox.value!! + 1
                viewModel.updateUser(this, tempUser.id, tempUser)

                // Update viewModel active avatar
                val tempAvatar: Avatar =
                    viewModel.avatarList.value!![viewModel.avatarLastSelectedCheckbox.value!!]
                viewModel._activeAvatar = MutableLiveData(tempAvatar)

                // Check if user has chosen a language yet and if not, prompt for it
                if (viewModel.activeUser?.value!!.languageId == 0 && initialCheck) { // Open window to choose language
                    chooseLanguage()
                }
            }

            val dialog = dialogbuider.create()
            dialog.show()
        } else {
            Toast.makeText(this, R.string.not_available_during_round, Toast.LENGTH_LONG).show()
        }
    }

    // Displays window where the user can change their user name
    private fun chooseUsername() {
        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)

        val textView = TextView(this)
        textView.setText(R.string.choose_username)
        textView.setPadding(20, 30, 20, 30)
        textView.textSize = 25f
        textView.setTextColor(ContextCompat.getColor(this, R.color.menu_text_colour))

        dialogBuilder.setCustomTitle(textView)
        dialogBuilder.setView(R.layout.edit_username_layout)
        dialogBuilder.setPositiveButton("OK", null)
        dialogBuilder.setNegativeButton(R.string.cancel) { dialog, _ ->
            // Button click sound
            val soundFile = R.raw.click_button
            playSound(soundFile)
            dialog.cancel()
        }

        val mAlertDialog: AlertDialog = dialogBuilder.create();
        mAlertDialog.setOnShowListener(OnShowListener {
            val b: Button = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            b.setOnClickListener(object : View.OnClickListener {
                override fun onClick(view: View?) {
                    // Button click sound
                    val soundFile = R.raw.click_button
                    playSound(soundFile)

                    val input = mAlertDialog.findViewById<View>(R.id.et_editUsername) as EditText

                    if (input.text.toString() == "") {
                        Toast.makeText(this@GameActivity, R.string.username_is_empty, Toast.LENGTH_LONG).show()
                    } else {
                        lifecycleScope.launch {
                            if (viewModel.usernameExists(this@GameActivity, input.text.toString())) { // Chosen user name already exists
                                Toast.makeText(this@GameActivity, R.string.username_exists, Toast.LENGTH_LONG).show()
                            } else {
                                // Update user object and db with the selected user name
                                val tempUser = viewModel.activeUser!!.value
                                tempUser!!.username = input.text.toString()
                                viewModel.updateUser(this@GameActivity, tempUser.id, tempUser)

                                actionBar?.title = viewModel.activeUser?.value!!.username
                                supportActionBar?.title = viewModel.activeUser?.value!!.username

                                mAlertDialog.cancel()
                            }
                        }
                    }
                }
            })
        })
        mAlertDialog.show()
    }

    // Displays window where the user can change their password
    private fun choosePassword() {
        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)

        val textView = TextView(this)
        textView.setText(R.string.choose_password)
        textView.setPadding(20, 30, 20, 30)
        textView.textSize = 25f
        textView.setTextColor(ContextCompat.getColor(this, R.color.menu_text_colour))

        dialogBuilder.setCustomTitle(textView)
        dialogBuilder.setView(R.layout.edit_password_layout)
        dialogBuilder.setPositiveButton("OK", null)
        dialogBuilder.setNegativeButton(R.string.cancel) { dialog, _ ->
            // Button click sound
            val soundFile = R.raw.click_button
            playSound(soundFile)
            dialog.cancel()
        }

        val mAlertDialog: AlertDialog = dialogBuilder.create();
        mAlertDialog.setOnShowListener(OnShowListener {
            val b: Button = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            b.setOnClickListener(object : View.OnClickListener {
                override fun onClick(view: View?) {
                    // Button click sound
                    val soundFile = R.raw.click_button
                    playSound(soundFile)

                    val inputPassword = mAlertDialog.findViewById<View>(R.id.et_editPassword) as EditText
                    val inputConfirmation = mAlertDialog.findViewById<View>(R.id.et_confirmPassword) as EditText

                    if (inputPassword.text.toString() == "" || inputConfirmation.text.toString() == "") {
                        Toast.makeText(this@GameActivity, R.string.fill_all_fields, Toast.LENGTH_LONG).show()
                    } else {
                        if (inputPassword != inputConfirmation) { // Password confirmation entered is different from password entered
                            Toast.makeText(this@GameActivity, R.string.passwords_different, Toast.LENGTH_LONG).show()
                        } else {
                            lifecycleScope.launch {
                                // Button click sound
                                val soundFile = R.raw.click_button
                                playSound(soundFile)

                                // Update user object and db with the selected password
                                val tempUser = viewModel.activeUser!!.value
                                tempUser!!.password = inputPassword.text.toString()
                                viewModel.updateUser(this@GameActivity, tempUser.id, tempUser)

                                mAlertDialog.cancel()
                            }
                        }
                    }
                }
            })
        })
        mAlertDialog.show()
    }

    // Displays window containing the game rules
    private fun displayHelp() {
        val layoutInflater = LayoutInflater.from(this)
        val gameHelpView: View = layoutInflater.inflate(R.layout.game_help_layout, null)
        val builder = AlertDialog.Builder(this)

        val gameHelpBtn = gameHelpView.findViewById(R.id.gameHelpBtn) as Button

        builder.setView(gameHelpView)
        val dialog = builder.create()

        dialog.window?.decorView?.setBackgroundResource(R.drawable.menu_shape)
        dialog.show()

        val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.90).toInt()

        dialog.window?.setLayout(width, height)

        gameHelpBtn.setOnClickListener {
            // Button click sound
            val soundFile = R.raw.click_button
            playSound(soundFile)

            dialog.cancel()
        }
    }

    // Displays window where the user can delete their account
    private fun deleteAccount() {
        val dialogbuider = AlertDialog.Builder(this)

        val textView = TextView(this)
        textView.setText(R.string.delete_account)
        textView.setPadding(20, 30, 20, 30)
        textView.textSize = 25f
        textView.setTextColor(ContextCompat.getColor(this, R.color.menu_text_colour))

        dialogbuider.setCustomTitle(textView)
        dialogbuider.setView(R.layout.confirm_action_layout)

        dialogbuider.setPositiveButton(R.string.yes) { _, _ ->
            // Button click sound
            val soundFile = R.raw.click_button
            playSound(soundFile)

            // Delete the user's account from the database
            viewModel.deleteUser(this, viewModel.activeUser?.value!!.id)

            // Clear shared preferences
            val prefs = PreferenceManager.getDefaultSharedPreferences(this)
            prefs.edit().clear().apply()

            // Redirect to log in page
            val loginIntent = Intent(applicationContext, MainActivity::class.java)
            startActivity(loginIntent)
        }
        dialogbuider.setNegativeButton(R.string.no) { dialog, _ ->
            // Button click sound
            val soundFile = R.raw.click_button
            playSound(soundFile)
            dialog.cancel()
        }

        val dialog = dialogbuider.create()
        dialog.show()
    }

    // Displays window where the user can log out
    private fun logout() {
        val dialogbuider = AlertDialog.Builder(this)

        val textView = TextView(this)
        textView.setText(R.string.logout)
        textView.setPadding(20, 30, 20, 30)
        textView.textSize = 25f
        textView.setTextColor(ContextCompat.getColor(this, R.color.menu_text_colour))

        dialogbuider.setCustomTitle(textView)
        dialogbuider.setView(R.layout.confirm_action_layout)

        dialogbuider.setPositiveButton(R.string.yes) { _, _ ->
            // Button click sound
            val soundFile = R.raw.click_button
            playSound(soundFile)

            val prefs = PreferenceManager.getDefaultSharedPreferences(this)
            val editor = prefs.edit()
            val loginIntent = Intent(applicationContext, MainActivity::class.java)
            editor.putString("rememberMe", "false")
            editor.apply()
            startActivity(loginIntent)
        }
        dialogbuider.setNegativeButton(R.string.no) { dialog, _ ->
            // Button click sound
            val soundFile = R.raw.click_button
            playSound(soundFile)
            dialog.cancel()
        }

        val dialog = dialogbuider.create()
        dialog.show()
    }

    // --- App bar ---
    // App bar menu and icons
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Create status bar menu with sound, highscores, and flag icons corresponding on the selected language
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.appbar_options, menu)

        appBarMenu = menu
        appBarMenu?.getItem(0)?.isVisible = true
        appBarMenu?.getItem(0)?.isEnabled = true
        appBarMenu?.getItem(1)?.isVisible = true
        appBarMenu?.getItem(1)?.isEnabled = true

        // Change sound menu icon according to the settings in preferences
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = prefs.edit()
        if (prefs.getString("sound", "on").equals("off")) {
            menu?.findItem(R.id.soundOptions)?.setIcon(R.drawable.sound_off)
            menu?.findItem(R.id.soundOptions)?.title = "Off"
        }
        editor.apply()
        return true
    }

    // Handles language icon click listener
    fun onLanguageItemClick(item: MenuItem) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)

        // Button click sound
        val soundFile = R.raw.click_button
        playSound(soundFile)

        chooseLanguage()
    }

    // Handles highscores icon click listener
    fun onHighscoresItemClick(item: MenuItem) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)

        // Button click sound
        val soundFile = R.raw.click_button
        playSound(soundFile)

        showHighscores()
    }

    // Handles sound icon click listener
    fun onSoundItemClick(item: MenuItem) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)

        // Button click sound
        val soundFile = R.raw.click_button
        playSound(soundFile)

        // Change sound preferences
        val editor = prefs.edit()
        if (item.title.equals("On")) {
            item.setIcon(R.drawable.sound_off)
            item.title = "Off"
            editor.putString("sound", "off")
        } else if (item.title.equals("Off")) {
            item.setIcon(R.drawable.sound_on)
            item.title = "On"
            editor.putString("sound", "on")
        }
        editor.apply()
    }

    // Shows the highscores window
    private fun showHighscores() {
        val layoutInflater = LayoutInflater.from(this)
        val highscoresView: View = layoutInflater.inflate(R.layout.highscores_layout, null)
        val builder = AlertDialog.Builder(this)

        val highscoresBtn = highscoresView.findViewById<Button>(R.id.highscoresBtn)
        var listOfHighscores = ArrayList<Highscore>()
        lifecycleScope.launch {
            // Gets list of top scores
            listOfHighscores = viewModel.findAllHighscores(this@GameActivity)

            val sortedList = listOfHighscores.sortedWith(compareByDescending({ it.score }))

            var listOfAvatars = ArrayList<String>()
            var listOfLanguages = ArrayList<String>()
            var listOfUsers = ArrayList<String>()
            for (item in sortedList) {
                // Gets list of the avatars of the highscores
                listOfAvatars.add(viewModel.findAvatarById(this@GameActivity, item.avatarId.toLong())!![0].headShot)

                // Gets list of the languages of the highscores
                listOfLanguages.add(viewModel.findLanguageById(this@GameActivity, item.languageId.toLong())!![0].src!!.toString())

                // Gets list of the users of the highscores
                listOfUsers.add(viewModel.findUserById(this@GameActivity, item.userId.toLong())!![0].username)
            }

            // Passes the highscores onto the recyclerview
            var highscoresAdapterLayoutManager: RecyclerView.LayoutManager? = null
            var highscoresAdapter: RecyclerView.Adapter<HighscoresAdapter.ViewHolder>? = null
            highscoresAdapterLayoutManager = LinearLayoutManager(this@GameActivity)
            val recyclerView = highscoresView.findViewById<RecyclerView>(R.id.listOfHighscores)
            recyclerView.layoutManager = highscoresAdapterLayoutManager
            highscoresAdapter = HighscoresAdapter(listOfAvatars, listOfLanguages, listOfUsers, sortedList, this@GameActivity)
            recyclerView.adapter = highscoresAdapter
        }


        builder.setView(highscoresView)
        val dialog = builder.create()

        dialog.window?.decorView?.setBackgroundResource(R.drawable.menu_shape)
        dialog.show()

        val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.90).toInt()//ViewGroup.LayoutParams.WRAP_CONTENT
        dialog.window?.setLayout(width, height)


        highscoresBtn.setOnClickListener {
            // Button click sound
            val soundFile = R.raw.click_letter_guess
            playSound(soundFile)

            dialog.cancel()
        }
    }

    // --- Sound effects ---
    // Sound effects
    private fun playSound(soundFile: Int) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        if (prefs.getString("sound", "on").equals("on")) {
            val soundToPlay = MediaPlayer.create(applicationContext, soundFile)
            soundToPlay.start()
            soundToPlay.setOnCompletionListener { soundToPlay ->
                soundToPlay.stop()
                soundToPlay?.release()
            }
        }
    }

    // --- Keyboard (game letterboard) ---
    // Handles the letters pressed on the keyboard
    private fun keyboardPressed(pressed: String, buttonPressed: Button) {
        var prizeCoins = 1

        if (viewModel.updateDisplayedWord(pressed)) { // Guessed letter
            viewModel._activeGameRound?.value!!.setLetterboardState(pressed, 1)
            buttonPressed.backgroundTintList = this.resources.getColorStateList(R.color.guessed_letter)

            // Updates number of consecutive guessed letters
            viewModel.activeGameRound?.value!!.lettersGuessedConsecutively++

            if (viewModel.activeGameRound?.value!!.lettersGuessedConsecutively == 5) { // Adds bonus coins for guessing 5 letters consecutively
                prizeCoins = 5
            } else if (viewModel.activeGameRound?.value!!.lettersGuessedConsecutively == 10) { // Adds bonus coins for guessing 10 letters consecutively and resets counter
                prizeCoins = 10

                // Resets number of consecutive guessed letters
                viewModel.activeGameRound?.value!!.lettersGuessedConsecutively = 0
            }

            // Adds prize to the assets bar
            if (viewModel.activeGameRound?.value!!.potentialPrize > 0) {
                viewModel.activeUser?.value!!.coins += viewModel.activeGameRound?.value!!.potentialPrize + 1 + prizeCoins

                // Coins sound
                val soundFile = R.raw.coins
                playSound(soundFile)
            }

            // Adds score to the assets bar
            viewModel.activeUser?.value!!.score += prizeCoins

            updateAssetBar()

            //TODO Implement avatar's reactions methods (timer, etc.)

            // Button click sound
            val soundFile = R.raw.click_letter_guess
            playSound(soundFile)
        } else { // Missed letter
            viewModel._activeGameRound?.value!!.setLetterboardState(pressed, -1)
            viewModel.activeGameRound?.value!!.letterMisses++
            viewModel.activeGameRound?.value!!.lettersGuessedConsecutively = 0
            viewModel.activeGameRound?.value!!.wordsGuessedConsecutivelyNoFaults = 0

            buttonPressed.backgroundTintList = this.resources.getColorStateList(R.color.missed_letter)

            // Button click sound
            val soundFile = R.raw.click_letter_miss
            playSound(soundFile)

            lifecycleScope.launch {
                if (viewModel.activeGameRound!!.value!!.letterMisses == 6) { // Word failed
                    displayDeadAvatar()
                } else {
                    updateAvatar()
                }
            }
        }
        buttonPressed.isEnabled = false

        prizeFadeOutCountdown()

        if (viewModel.activeGameRound?.value!!.guessedLetters == viewModel.word.value?.hiddenWord.toString().length) {
            wordGuessed()
        }

        if (viewModel.activeGameRound!!.value!!.letterMisses == 6) { // Word failed
            wordFailed()
        }
    }

    // Reset the keyboard
    private fun resetKeyboard() {
        var c: Char = 'A'
        while (c <= 'Z') {
            val letterBtn: Button = findViewById(resources.getIdentifier("keyboard$c", "id", packageName))
            letterBtn.isEnabled = true
            letterBtn.backgroundTintList = this.resources.getColorStateList(R.color.reset_letter)
            viewModel._activeGameRound?.value!!.setLetterboardState(c.toString(), 0)

            ++c
        }
    }

    // --- Displayed avatar (on the gallows) ---
    // Hides all the graphics related to the avatar
    private fun hideAvatarGraphics() {
        // Gets the layer drawable
        val layerDrawable = resources.getDrawable(R.drawable.layers_gallows) as LayerDrawable

        //Hides them all
        for (i in 1 until layerDrawable.numberOfLayers) { // starts with 1 because 0 is the gallows
            layerDrawable.getDrawable(i).alpha = 0
        }

        // Gets the ImageView where the layer drawable is by its id
        val layoutlist1 = findViewById<View>(R.id.game_gallows_top) as ImageView
        // Sets its src to the new updated layer drawable
        layoutlist1.setImageDrawable(layerDrawable)
    }

    // Update displayed avatar (displays the relevant body parts only)
    private suspend fun updateAvatar() {
        val nrOfMisses = viewModel.activeGameRound?.value!!.letterMisses

        // Hides all displayed avatar graphics
        hideAvatarGraphics()

        // Gets relevant Ids from avatar object
        val avatarExtra = viewModel.activeAvatar!!.value!!.extrasId
        val avatarEyes = viewModel.activeAvatar!!.value!!.eyesId
        val avatarEyebrows = viewModel.activeAvatar!!.value!!.eyebrowsId
        val avatarMouth = viewModel.activeAvatar!!.value!!.mouthId

        // Gets the layer drawable
        val layerDrawable = resources.getDrawable(R.drawable.layers_gallows) as LayerDrawable

        // Extra: Gets the resource id, then gets the item drawable by its id
        var extra: Drawable? = null
        if (avatarExtra > 0) {
            val extraResourceName = viewModel.findExtraById(this, avatarExtra.toLong())?.get(0)?.src
            extra = layerDrawable.findDrawableByLayerId(getStringIdentifier(extraResourceName))
        }

        // Eyes: Gets the resource id, then gets the item drawable by its id
        val eyesResourceName = viewModel.findEyesById(this, avatarEyes.toLong())?.get(0)?.src
        val eyes = layerDrawable.findDrawableByLayerId(getStringIdentifier(eyesResourceName))

        // Eyebrows: Gets the resource id, then gets the item drawable by its id
        val eyebrowsResourceName = viewModel.findEyebrowsById(this, avatarEyebrows.toLong())?.get(0)?.src
        val eyebrows = layerDrawable.findDrawableByLayerId(getStringIdentifier(eyebrowsResourceName))

        // Mouth: Gets the resource id, then gets the item drawable by its id
        val mouthResourceName = viewModel.findMouthById(this, avatarMouth.toLong())?.get(0)?.src
        val mouth = layerDrawable.findDrawableByLayerId(getStringIdentifier(mouthResourceName))

        // Body parts: Gets the item drawable by its id
        val head = layerDrawable.findDrawableByLayerId(getStringIdentifier(viewModel.activeAvatar!!.value!!.headSrc))
        val leftArm = layerDrawable.findDrawableByLayerId(getStringIdentifier(viewModel.activeAvatar!!.value!!.leftArmSrc))
        val rightArm = layerDrawable.findDrawableByLayerId(getStringIdentifier(viewModel.activeAvatar!!.value!!.rightArmSrc))
        val leftLeg = layerDrawable.findDrawableByLayerId(getStringIdentifier(viewModel.activeAvatar!!.value!!.leftLegSrc))
        val rightLeg = layerDrawable.findDrawableByLayerId(getStringIdentifier(viewModel.activeAvatar!!.value!!.rightLegSrc))
        val torso = layerDrawable.findDrawableByLayerId(getStringIdentifier(viewModel.activeAvatar!!.value!!.torsoSrc))

        when(nrOfMisses) {
            1 -> { // Show head
                // Sets the opacity of the avatar's parts
                head.alpha = 255
                leftArm.alpha = 0
                rightArm.alpha = 0
                leftLeg.alpha = 0
                rightLeg.alpha = 0
                torso.alpha = 0

                if (avatarExtra > 0) {
                    extra!!.alpha = 255
                }
                eyes.alpha = 255
                eyebrows.alpha = 255
                mouth.alpha = 255
            }
            2 -> { // Show head and torso
                // Sets the opacity of the avatar's parts
                head.alpha = 255
                leftArm.alpha = 0
                rightArm.alpha = 0
                leftLeg.alpha = 0
                rightLeg.alpha = 0
                torso.alpha = 255

                if (avatarExtra > 0) {
                    extra!!.alpha = 255
                }
                eyes.alpha = 255
                eyebrows.alpha = 255
                mouth.alpha = 255
            }
            3 -> { // Show head, torso, and left arm
                // Sets the opacity of the avatar's parts
                head.alpha = 255
                leftArm.alpha = 255
                rightArm.alpha = 0
                leftLeg.alpha = 0
                rightLeg.alpha = 0
                torso.alpha = 255

                if (avatarExtra > 0) {
                    extra!!.alpha = 255
                }
                eyes.alpha = 255
                eyebrows.alpha = 255
                mouth.alpha = 255
            }
            4 -> { // Show head, torso, and both arms
                // Sets the opacity of the avatar's parts
                head.alpha = 255
                leftArm.alpha = 255
                rightArm.alpha = 255
                leftLeg.alpha = 0
                rightLeg.alpha = 0
                torso.alpha = 255

                if (avatarExtra > 0) {
                    extra!!.alpha = 255
                }
                eyes.alpha = 255
                eyebrows.alpha = 255
                mouth.alpha = 255
            }
            5 -> { // Show head, torso, both arms, and left leg
                // Sets the opacity of the avatar's parts
                head.alpha = 255
                leftArm.alpha = 255
                rightArm.alpha = 255
                leftLeg.alpha = 255
                rightLeg.alpha = 0
                torso.alpha = 255

                if (avatarExtra > 0) {
                    extra!!.alpha = 255
                }
                eyes.alpha = 255
                eyebrows.alpha = 255
                mouth.alpha = 255
            }
            6 -> { // Show full avatar
                // Sets the opacity of the avatar's parts
                head.alpha = 255
                leftArm.alpha = 255
                rightArm.alpha = 255
                leftLeg.alpha = 255
                rightLeg.alpha = 255
                torso.alpha = 255

                if (avatarExtra > 0) {
                    extra!!.alpha = 255
                }
                eyes.alpha = 255
                eyebrows.alpha = 255
                mouth.alpha = 255
            }
        }

        // Gets the ImageView where the layer drawable is by its id
        val layoutlist1 = findViewById<View>(R.id.game_gallows_top) as ImageView
        // Sets its src to the new updated layer drawable
        layoutlist1.setImageDrawable(layerDrawable)
    }

    // Returns the drawable id of a string (image src name)
    private fun getStringIdentifier(name: String?): Int {
        return resources.getIdentifier(name, "id", packageName)
    }

    // Display dead avatar
    private fun displayDeadAvatar() {
        val tempAvatar = viewModel.activeAvatar!!.value
        viewModel.activeAvatar!!.value!!.eyesId = 6 // Closed eyes
        if (viewModel.activeAvatar!!.value?.complexion  == "light") {
            viewModel.activeAvatar!!.value!!.eyebrowsId = 6 // Sad light eyebrows
            viewModel.activeAvatar!!.value!!.mouthId = 6 // Side light mouth
        } else if (viewModel.activeAvatar!!.value?.complexion == "dark") {
            viewModel.activeAvatar!!.value!!.eyebrowsId = 5 // Sad dark eyebrows
            viewModel.activeAvatar!!.value!!.mouthId = 2 // Side dark mouth
        }

        viewModel._activeAvatar?.value = tempAvatar
        lifecycleScope.launch {
            updateAvatar()
        }
    }

    // Display happy avatar
    private fun displayHappyAvatar() {
        val tempAvatar = viewModel.activeAvatar!!.value
        viewModel.activeAvatar!!.value!!.eyesId = 9 // Eyes happy forward
        if (viewModel.activeAvatar!!.value?.complexion  == "light") {
            viewModel.activeAvatar!!.value!!.eyebrowsId = 4 // Neutral light eyebrows
            viewModel.activeAvatar!!.value!!.mouthId = 7 // Smiling light mouth
        } else if (viewModel.activeAvatar!!.value?.complexion == "dark") {
            viewModel.activeAvatar!!.value!!.eyebrowsId = 3 // Neutral dark eyebrows
            viewModel.activeAvatar!!.value!!.mouthId = 3 // Smiling dark mouth
        }

        viewModel._activeAvatar?.value = tempAvatar
        lifecycleScope.launch {
            updateAvatar()
        }
    }

    // --- Game round ---
    // Resets the view model game round object
    private fun resetGameRound() {
        val tempGameRound = GameRound()
        viewModel._activeGameRound = MutableLiveData(tempGameRound)
    }

    // Initiates a new game round (prepares UI and fetches word and definitions)
    private fun initiateNewRound(view: View) {
        resetGameRound()
        hideNewRoundBtn()
        hideAllAnimations()
        hideDisplayedWord()
        displayHappyAvatar()
        hideAvatarGraphics()

        binding.waitingPlaceholder.isVisible = true

        if (viewModel.activeLanguage?.value?.name.equals("Français")) {
            viewModel.getRandomWordFr(view)
        } else if (viewModel.activeLanguage?.value?.name.equals("English")) {
            viewModel.getRandomWordEn(view)
        }
    }

    // Check if the word found isn't too long. If it is, find another one. Otherwise start the round
    private fun validateRandomWord(it: Word) {
        if (viewModel.word.value!!.hiddenWord.length <= 15) {
            startNewRound()
        } else {
            val gameView: View = this@GameActivity.window.decorView.findViewById(android.R.id.content)
            initiateNewRound(gameView)
        }
    }

    // Starts the UI for a new round after getting a new word object
    private fun startNewRound() {
        if (!activeRound) {
            activeRound = true

            viewModel.activeGameRound?.value!!.letterMisses = 0
            viewModel.activeGameRound?.value!!.guessedLetters = 0
            viewModel.activeGameRound?.value!!.lettersGuessedConsecutively = 0
            viewModel.activeGameRound?.value!!.wordsGuessedConsecutively = 0
            viewModel.activeGameRound?.value!!.wordsGuessedConsecutivelyNoFaults = 0

            resetKeyboard()
            prizeFadeOutCountdown()

            val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            val currentDate = LocalDateTime.now().format(formatter).toString()

            // New word sound
            val soundFile = R.raw.new_word
            playSound(soundFile)
        }
        binding.waitingPlaceholder.isVisible = false

        showKeyboard()
        updateAssetBar()
        showAssetBar()
        showHintBtn()
        showExchangeBtn()
        showAbandonBtn()
        showDisplayedWord()
    }

    // Guessed the word
    private fun wordGuessed() {
        (timer10 as CountDownTimer).cancel()
        binding.potentialPrize.alpha = 0F

        // Resets number of consecutive guessed letters
        viewModel.activeGameRound?.value!!.lettersGuessedConsecutively = 0

        // Adds bonus coins if no letters missed on this word
        if (viewModel.activeGameRound?.value!!.letterMisses == 0) {
            viewModel.activeUser?.value!!.coins += 50

            // Coins sound
            val soundFile = R.raw.coins
            playSound(soundFile)
        }

        // Adds bonus banknotes if consecutive words with no faults
        if (viewModel.activeGameRound?.value!!.wordsGuessedConsecutivelyNoFaults == 10) {
            viewModel.activeUser?.value!!.banknotes += 10

            // Banknotes sound
            val soundFile = R.raw.banknotes
            playSound(soundFile)
        } else if (viewModel.activeGameRound?.value!!.wordsGuessedConsecutivelyNoFaults == 5) {
            viewModel.activeUser?.value!!.banknotes += 5

            // Banknotes sound
            val soundFile = R.raw.banknotes
            playSound(soundFile)
        } else if (viewModel.activeGameRound?.value!!.wordsGuessedConsecutivelyNoFaults > 0) {
            viewModel.activeUser?.value!!.banknotes += 1

            // Banknotes sound
            val soundFile = R.raw.banknotes
            playSound(soundFile)
        }

        // Adds bonus diamonds if consecutive words with no faults
        if (viewModel.activeGameRound?.value!!.wordsGuessedConsecutivelyNoFaults == 10) {
            viewModel.activeUser?.value!!.diamonds += 1

            // Diamonds sound
            val soundFile = R.raw.diamonds
            playSound(soundFile)
        }

        // Adds bonus score if consecutive words with no faults
        if (viewModel.activeGameRound?.value!!.wordsGuessedConsecutivelyNoFaults == 10) {
            viewModel.activeGameRound?.value!!.wordsGuessedConsecutivelyNoFaults = 0
            viewModel.activeUser?.value!!.score += 25
        } else if (viewModel.activeGameRound?.value!!.wordsGuessedConsecutivelyNoFaults == 5) {
            viewModel.activeUser?.value!!.score += 50
        } else if (viewModel.activeGameRound?.value!!.wordsGuessedConsecutivelyNoFaults > 0) {
            viewModel.activeUser?.value!!.score += 100
        }
        updateAssetBar()

        viewModel.activeGameRound?.value!!.letterMisses = 0
        activeRound = false
        hideKeyboard()
        resetKeyboard()
        binding.newRoundBtn.setText(R.string.continue_)
        showNewRoundBtn()
        pickEndAnimation("win")

        showEndDefinitions()

        // Win word sound
        val soundFile = R.raw.win_word
        playSound(soundFile)
    }

    // Failed the word
    private fun wordFailed() {
        viewModel.activeUser?.value!!.lives--
        viewModel.activeGameRound?.value!!.wordsGuessedConsecutivelyNoFaults = 0

        updateAssetBar()

        timer10?.cancel()
        binding.potentialPrize.alpha = 0F

        val tempWord = viewModel.word.value
        tempWord!!.displayedWord = tempWord.hiddenWord
        viewModel._word.value = tempWord

        binding.newRoundBtn.setText(R.string.continue_)
        activeRound = false
        showNewRoundBtn()
        resetKeyboard()
        hideKeyboard()
        pickEndAnimation("lose")
        showEndDefinitions()
        viewModel.activeGameRound?.value!!.letterMisses = 0

        if (viewModel.activeUser?.value!!.lives == 0) {
            // Lose game round sound
            val soundFile = R.raw.fail_round
            playSound(soundFile)
        } else {
            // Fail word sound
            val soundFile = R.raw.fail_word
            playSound(soundFile)
        }
    }

    // Lose game round
    private fun loseGameRound() {
        viewModel.activeGameRound?.value!!.letterMisses = 0
        viewModel.activeGameRound?.value!!.guessedLetters = 0
        viewModel.activeGameRound?.value!!.lettersGuessedConsecutively = 0
        viewModel.activeGameRound?.value!!.wordsGuessedConsecutively = 0
        activeRound = false
        resetKeyboard()
        viewModel.activeUser?.value!!.diamonds = 0
        viewModel.activeUser?.value!!.banknotes = 0
        viewModel.activeUser?.value!!.coins = 0
        viewModel.activeUser?.value!!.lives = 5

        (timer10 as CountDownTimer).cancel()
        binding.potentialPrize.alpha = 0F

        updateAssetBar()
        hideKeyboard()
        hideAssetBar()
        hideHintBtn()
        hideExchangeBtn()
        hideAbandonBtn()
        hideDisplayedWord()
        hideAllAnimations()
        hideAvatarGraphics()
        binding.newRoundBtn.setText(R.string.new_round)
        showNewRoundBtn()

        val layoutInflater = LayoutInflater.from(this)
        val finalScoreView: View = layoutInflater.inflate(R.layout.end_game_layout, null)
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(false)

        val finalScore = finalScoreView.findViewById(R.id.endGameScore) as TextView
        finalScore.text = viewModel.activeUser?.value!!.score.toString()
        val finalScoreBtn = finalScoreView.findViewById(R.id.endGameHighscoresBtn) as Button

        builder.setView(finalScoreView)
        val dialog = builder.create()

        dialog.window?.decorView?.setBackgroundResource(R.drawable.menu_shape)
        dialog.show()

        val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
        val height = ViewGroup.LayoutParams.WRAP_CONTENT

        // Get personal highscore
        val personalHighscore = viewModel.activeUser!!.value?.highscore!!

        // Compare game score to personal highscore
        if (viewModel.activeUser?.value!!.score > personalHighscore) {
            // Updates active user object in viewModel and active user db
            val tempUser = viewModel.activeUser?.value!!
            tempUser.highscore = viewModel.activeUser?.value!!.score
            viewModel.updateUser(this, viewModel.activeUser!!.value!!.id, tempUser)

            // Sets the relevant layout elements to visible
            finalScoreView.findViewById<TextView>(R.id.endGamePersonalHighscore).isVisible = true
        }

        // Compare game score to top scores and replaces the lowest top score if it's lower than the game score
        var listOfHighscores = ArrayList<Highscore>()
        lifecycleScope.launch {
            // Gets list of top scores
            listOfHighscores = viewModel.findAllHighscores(this@GameActivity)
             if (listOfHighscores.size == 5) {
                 var lowestTopscore = listOfHighscores[0].score
                 var lowestTopScoreId = 0L

                 // Gets the lowest top score
                 for (item in listOfHighscores) {
                     if (item.score < lowestTopscore) {
                         lowestTopScoreId = item.id
                         lowestTopscore = item.score
                     }
                 }

                 // Compare it to game score and replace it if necessary
                 if (viewModel.activeUser?.value!!.score > lowestTopscore) {
                     val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
                     val currentDate = LocalDateTime.now().format(formatter).toString()

                     viewModel.updateHighscore(this@GameActivity, lowestTopScoreId, Highscore(
                         viewModel.activeUser?.value!!.score,
                         currentDate,
                         viewModel.activeUser?.value!!.languageId,
                         viewModel.activeUser?.value!!.id.toInt(),
                         viewModel.activeUser?.value!!.avatarId))

                     // Sets the relevant layout elements to visible
                     finalScoreView.findViewById<TextView>(R.id.endGameHighscoreMessage).isVisible = true
                     finalScoreView.findViewById<TextView>(R.id.endGameHighscoreIcon).isVisible = true
                 }
             } else {
                 viewModel.insertHighscore(viewModel.activeUser?.value!!.score, this@GameActivity)

                 // Sets the relevant layout elements to visible
                 finalScoreView.findViewById<TextView>(R.id.endGameHighscoreMessage).isVisible = true
                 finalScoreView.findViewById<TextView>(R.id.endGameHighscoreIcon).isVisible = true
             }
        }

        dialog.window?.setLayout(width, height)

        finalScoreBtn.setOnClickListener {
            // Button click sound
            val soundFile1 = R.raw.click_button
            playSound(soundFile1)

            viewModel.activeUser?.value!!.score = 0
            dialog.cancel()
        }
    }

    // Show definitions at the end
    private fun showEndDefinitions() {
        val layoutInflater = LayoutInflater.from(this)
        val definitionsView: View = layoutInflater.inflate(R.layout.reveal_all_definitions_layout, null)
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(false)

        val endDefinitionsTitle = definitionsView.findViewById<TextView>(R.id.allDefinitions_title)
        endDefinitionsTitle.setText(viewModel.word.value!!.hiddenWord)

        val endDefinitionsBtn = definitionsView.findViewById<Button>(R.id.allDefinitionsBtn)
        val definitionsList = viewModel.word.value!!.definitions!!

        // Passes the definitions onto the recyclerview
        var definitionsAdapterLayoutManager: RecyclerView.LayoutManager? = null
        var definitionsAdapter: RecyclerView.Adapter<DefinitionsAdapter.ViewHolder>? = null
        definitionsAdapterLayoutManager = LinearLayoutManager(this)
        val recyclerView = definitionsView.findViewById<RecyclerView>(R.id.listOfDefinitions)
        recyclerView.layoutManager = definitionsAdapterLayoutManager
        definitionsAdapter = DefinitionsAdapter(definitionsList, this)
        recyclerView.adapter = definitionsAdapter

        if (definitionsList.isEmpty()) {
            definitionsView.findViewById<TextView>(R.id.allDefinitions_noDefinitions).isVisible = true
        }

        builder.setView(definitionsView)
        val dialog = builder.create()

        dialog.window?.decorView?.setBackgroundResource(R.drawable.menu_shape)
        dialog.show()

        val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.6).toInt()

        dialog.window?.setLayout(width, height)

        var window: Window? = dialog.getWindow()
        var wlp: WindowManager.LayoutParams? = window?.getAttributes()
        wlp?.gravity = Gravity.TOP
        wlp?.verticalMargin = 0.05F
        window?.setAttributes(wlp)

        endDefinitionsBtn.setOnClickListener {
            // Button click sound
            val soundFile = R.raw.click_button
            playSound(soundFile)

            if (viewModel.activeUser?.value!!.lives == 0) {
                loseGameRound()
            }

            dialog.cancel()
        }
    }

    // Randomly picks an end animation and displays it
    private fun pickEndAnimation(type: String) {

        when ((0..1).random()) {
            0 -> {
                if (type == "win") {
                    Glide.with(this).load(R.drawable.win1).into(binding.endAnimLeft)
                    binding.endAnimLeft.isVisible = true
                } else if (type == "lose") {
                    Glide.with(this).load(R.drawable.lose1).into(binding.endAnimLeft)
                    binding.endAnimLeft.isVisible = true
                }
            }
            1 -> {
                if (type == "win") {
                    Glide.with(this).load(R.drawable.win2).into(binding.endAnimRight)
                    binding.endAnimRight.isVisible = true
                } else if (type == "lose") {
                    Glide.with(this).load(R.drawable.lose2).into(binding.endAnimRight)
                    binding.endAnimRight.isVisible = true
                }
            }
        }
    }

    // --- Graphic related methods ---
    // Show the asset bar
    private fun showAssetBar() {
        binding.gameAssetsBar.visibility = View.VISIBLE
    }

    // Hides the asset bar
    private fun hideAssetBar() {
        binding.gameAssetsBar.visibility = View.INVISIBLE
    }

    // Update the values on the asset bar
    private fun updateAssetBar() {
        binding.tvScore.text = viewModel.activeUser?.value!!.score.toString()
        binding.tvCoins.text = viewModel.activeUser?.value!!.coins.toString()
        binding.tvBanknotes.text = viewModel.activeUser?.value!!.banknotes.toString()
        binding.tvDiamonds.text = viewModel.activeUser?.value!!.diamonds.toString()
        binding.tvLives.text = viewModel.activeUser?.value!!.lives.toString()
    }

    // Show keyboard
    private fun showKeyboard() {
        binding.keyboard.visibility = View.VISIBLE
    }

    // Hides the keyboard
    private fun hideKeyboard() {
        binding.keyboard.visibility = View.INVISIBLE
    }

    // Show the displayed word
    private fun showDisplayedWord() {
        binding.displayedWord.text = viewModel.word.value?.displayedWord
        binding.displayedWord.visibility = View.VISIBLE
    }

    // Hides the displayed word
    private fun hideDisplayedWord() {
        binding.displayedWord.visibility = View.INVISIBLE
    }

    // Show the hint button
    private fun showHintBtn() {
        binding.hintBtn.visibility = View.VISIBLE
    }

    // Hides the hint button
    private fun hideHintBtn() {
        binding.hintBtn.visibility = View.INVISIBLE
    }

    // Show the exchange button
    private fun showExchangeBtn() {
        binding.exchangeBtn.visibility = View.VISIBLE
    }

    // Hides the exchange button
    private fun hideExchangeBtn() {
        binding.exchangeBtn.visibility = View.INVISIBLE
    }

    // Show the abandon button
    private fun showAbandonBtn() {
        binding.abandonBtn.visibility = View.VISIBLE
    }

    // Hides the abandon button
    private fun hideAbandonBtn() {
        binding.abandonBtn.visibility = View.INVISIBLE
    }

    // Show new round button (Play / Continue)
    private fun showNewRoundBtn() {
        binding.newRoundBtn.visibility = View.VISIBLE
    }

    // Hides new round button (Play / Continue)
    private fun hideNewRoundBtn() {
        binding.newRoundBtn.visibility = View.INVISIBLE
    }

    // Hides end animations
    private fun hideAllAnimations() {
        binding.endAnimLeft.isVisible = false
        binding.endAnimRight.isVisible = false
    }

    // --- Animations ---
    // Potential prize countdown animation
    private fun prizeFadeOutCountdown() {
        // Cancels the timer if it already exists and is running
        if (timer10 != null) {
            (timer10 as CountDownTimer).cancel()
        }

        // Sets the initial values
        binding.potentialPrizeAmount.setText("11")
        viewModel.activeGameRound?.value!!.potentialPrize = 10

        // Defines the timer
        timer10 = object: CountDownTimer(11000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.potentialPrize.alpha = 1F
                val prizeFadeOut: Animation = AnimationUtils.loadAnimation(this@GameActivity, R.anim.fadeout1s)
                prizeFadeOut.startOffset = 0
                prizeFadeOut.fillAfter = true
                binding.potentialPrize.startAnimation(prizeFadeOut)

                binding.potentialPrizeAmount.setText((binding.potentialPrizeAmount.text.toString().toInt() - 1).toString())
                viewModel.activeGameRound?.value!!.potentialPrize --
            }

            override fun onFinish() {
                binding.potentialPrize.alpha = 1F
            }
        }

        // Starts the timer
        (timer10 as CountDownTimer).start()
    }
}
