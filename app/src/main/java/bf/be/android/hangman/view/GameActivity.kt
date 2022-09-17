package bf.be.android.hangman.view

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.view.*
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
import bf.be.android.hangman.R
import bf.be.android.hangman.databinding.ActivityGameBinding
import bf.be.android.hangman.model.GameRound
import bf.be.android.hangman.model.Word
import bf.be.android.hangman.model.dal.entities.Avatar
import bf.be.android.hangman.model.dal.entities.Language
import bf.be.android.hangman.viewModel.MainViewModel
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch


class GameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGameBinding
    private lateinit var toggle: ActionBarDrawerToggle

    companion object {
        lateinit var gameContext: Context
        var gameView: View? = null
        var appBarMenu: Menu? = null
        var activeRound = false
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

        gameContext = this

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
            getActionBar()?.setTitle(viewModel.activeUser?.value!!.username);
            supportActionBar?.setTitle(viewModel.activeUser?.value!!.username);

            // Check if user has chosen an avatar yet and if not, prompt for it (pass initial check parametre as true so that language check is also performed)
            if (viewModel.activeUser?.value!!.avatarId == 0) { // Open window to choose avatar
                chooseAvatars(viewModel.getAvatarsHeadshots(applicationContext), true)
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
                drawerLayout.closeDrawers();
                when (it.itemId) {
                    R.id.languageItem -> {
                        lifecycleScope.launch {
                            chooseLanguage()
                        }
                    }
                    R.id.avatarItem -> {
                        lifecycleScope.launch {
                            chooseAvatars(viewModel.getAvatarsHeadshots(applicationContext), false)
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
                        val prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)
                        val editor = prefs.edit()
                        val loginIntent = Intent(applicationContext, MainActivity::class.java)
                        editor.putString("rememberMe", "false")
                        editor.apply()
                        startActivity(loginIntent)
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
        binding.keyboardA.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                keyboardPressed("A", binding.keyboardA)
            }
        })
        binding.keyboardB.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                keyboardPressed("B", binding.keyboardB)
            }
        })
        binding.keyboardC.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                keyboardPressed("C", binding.keyboardC)
            }
        })
        binding.keyboardD.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                keyboardPressed("D", binding.keyboardD)
            }
        })
        binding.keyboardE.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                keyboardPressed("E", binding.keyboardE)
            }
        })
        binding.keyboardF.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                keyboardPressed("F", binding.keyboardF)
            }
        })
        binding.keyboardG.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                keyboardPressed("G", binding.keyboardG)
            }
        })
        binding.keyboardH.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                keyboardPressed("H", binding.keyboardH)
            }
        })
        binding.keyboardI.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                keyboardPressed("I", binding.keyboardI)
            }
        })
        binding.keyboardJ.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                keyboardPressed("J", binding.keyboardJ)
            }
        })
        binding.keyboardK.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                keyboardPressed("K", binding.keyboardK)
            }
        })
        binding.keyboardL.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                keyboardPressed("L", binding.keyboardL)
            }
        })
        binding.keyboardM.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                keyboardPressed("M", binding.keyboardM)
            }
        })
        binding.keyboardN.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                keyboardPressed("N", binding.keyboardN)
            }
        })
        binding.keyboardO.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                keyboardPressed("O", binding.keyboardO)
            }
        })
        binding.keyboardP.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                keyboardPressed("P", binding.keyboardP)
            }
        })
        binding.keyboardQ.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                keyboardPressed("Q", binding.keyboardQ)
            }
        })
        binding.keyboardR.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                keyboardPressed("R", binding.keyboardR)
            }
        })
        binding.keyboardS.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                keyboardPressed("S", binding.keyboardS)
            }
        })
        binding.keyboardT.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                keyboardPressed("T", binding.keyboardT)
            }
        })
        binding.keyboardU.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                keyboardPressed("U", binding.keyboardU)
            }
        })
        binding.keyboardV.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                keyboardPressed("V", binding.keyboardV)
            }
        })
        binding.keyboardW.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                keyboardPressed("W", binding.keyboardW)
            }
        })
        binding.keyboardX.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                keyboardPressed("X", binding.keyboardX)
            }
        })
        binding.keyboardY.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                keyboardPressed("Y", binding.keyboardY)
            }
        })
        binding.keyboardZ.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                keyboardPressed("Z", binding.keyboardZ)
            }
        })
    }

    // New round button binding
    private fun initialiseRoundBtnBinding() {
        binding.newRoundBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                gameView = view

                // Button click sound
                val prefs = PreferenceManager.getDefaultSharedPreferences(gameContext)
                if (prefs.getString("sound", "on").equals("on")) {
                    var soundFile = R.raw.click_button
                    playSound(soundFile)
                }

                initiateNewRound(gameView!!)
            }
        })
    }

    // Help button binding
    private fun initialiseHintBtnBinding() {
        binding.hintBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                // Button click sound
                val prefs = PreferenceManager.getDefaultSharedPreferences(gameContext)
                if (prefs.getString("sound", "on").equals("on")) {
                    var soundFile = R.raw.click_button
                    playSound(soundFile)
                }
                showHelpMenu(view!!)
            }
        })
    }

    // Exchange button binding
    private fun initialiseExchangeBtnBinding() {
        binding.exchangeBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                // Button click sound
                val prefs = PreferenceManager.getDefaultSharedPreferences(gameContext)
                if (prefs.getString("sound", "on").equals("on")) {
                    var soundFile = R.raw.click_button
                    playSound(soundFile)
                }
                showExchangeMenu(view!!)
            }
        })
    }

    // Abandon button binding
    private fun initialiseAbandonBtnBinding() {
        binding.abandonBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                // Button click sound
                val prefs = PreferenceManager.getDefaultSharedPreferences(gameContext)
                if (prefs.getString("sound", "on").equals("on")) {
                    var soundFile = R.raw.click_button
                    playSound(soundFile)
                }
                showAbandonGameRound()
            }
        })
    }

    // --- Initialisations ---
    // Initialises the UI
    private fun initialiseUi() {
        hideAvatarGraphics()
        hideAllAnimations()
        hideHintBtn()
        hideExchangeBtn()
        hideAbandonBtn()
        Glide.with(this).load(R.drawable.waiting).into(binding.waitingPlaceholder);
    }

    // --- Help menu ---
    // Show help menu
    private fun showHelpMenu(view: View) {
        val layoutInflater = LayoutInflater.from(this)
        val helpMenuView: View = layoutInflater.inflate(R.layout.help_menu_layout, null)
        val builder = AlertDialog.Builder(this)

        val textView = TextView(gameContext)
        textView.setText(R.string.buy_help)
        textView.setPadding(20, 30, 20, 30)
        textView.textSize = 25f
        textView.setTextColor(ContextCompat.getColor(gameContext, R.color.menu_text_colour))

        builder.setCustomTitle(textView);

        val buyLetterBtn = helpMenuView.findViewById(R.id.letterBuyBtn) as Button
        val buyDefinitionBtn = helpMenuView.findViewById(R.id.definitionBuyBtn) as Button
        val buyBodyPartBtn = helpMenuView.findViewById(R.id.bodyPartBuyBtn) as Button

        builder.setView(helpMenuView)
        val dialog = builder.create()

        dialog.window?.decorView?.setBackgroundResource(R.drawable.menu_shape)
        dialog.show()

        val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
        val height = ViewGroup.LayoutParams.WRAP_CONTENT

        if (viewModel.activeUser?.value!!.coins < 5) {
            buyLetterBtn.isEnabled = false
            buyLetterBtn.setBackgroundTintList(gameContext.getResources().getColorStateList(R.color.inactive_state))
        } else {
            buyLetterBtn.isActivated = true
        }

        if (viewModel.activeUser?.value!!.banknotes < 5) {
            buyDefinitionBtn.isEnabled = false
            buyDefinitionBtn.setBackgroundTintList(gameContext.getResources().getColorStateList(R.color.inactive_state))
        } else {
            buyDefinitionBtn.isActivated = true
        }

        if (viewModel.activeUser?.value!!.diamonds < 5) {
            buyBodyPartBtn.isEnabled = false
            buyBodyPartBtn.setBackgroundTintList(gameContext.getResources().getColorStateList(R.color.inactive_state))
        } else {
            buyBodyPartBtn.isActivated = true
        }

        dialog.getWindow()?.setLayout(width, height)

        buyLetterBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                // Button click sound
                val prefs = PreferenceManager.getDefaultSharedPreferences(gameContext)
                if (prefs.getString("sound", "on").equals("on")) {
                    var soundFile = R.raw.click_button
                    playSound(soundFile)
                }
                //TODO Implement
                println("--------- Buy letter")
                dialog.cancel()
            }
        })
        buyDefinitionBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                // Button click sound
                val prefs = PreferenceManager.getDefaultSharedPreferences(gameContext)
                if (prefs.getString("sound", "on").equals("on")) {
                    var soundFile = R.raw.click_button
                    playSound(soundFile)
                }
                //TODO Implement
                println("--------- Buy definition")
                dialog.cancel()
            }
        })
        buyBodyPartBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                // Button click sound
                val prefs = PreferenceManager.getDefaultSharedPreferences(gameContext)
                if (prefs.getString("sound", "on").equals("on")) {
                    var soundFile = R.raw.click_button
                    playSound(soundFile)
                }
                //TODO Implement
                println("--------- Buy body part")
                dialog.cancel()
            }
        })
    }

    // --- Exchange menu ---
    // Show exchange menu
    private fun showExchangeMenu(view: View) {
        val layoutInflater = LayoutInflater.from(this)
        val exchangeMenuView: View = layoutInflater.inflate(R.layout.exchange_menu_layout, null)
        val builder = AlertDialog.Builder(this)

        val textView = TextView(gameContext)
        textView.setText(R.string.exchange_resources)
        textView.setPadding(20, 30, 20, 30)
        textView.textSize = 25f
        textView.setTextColor(ContextCompat.getColor(gameContext, R.color.menu_text_colour))

        builder.setCustomTitle(textView);

        val buyBanknote = exchangeMenuView.findViewById(R.id.exchangeBuyBanknoteBtn) as Button
        val buyDiamond = exchangeMenuView.findViewById(R.id.exchangeBuyDiamondBtn) as Button

        builder.setView(exchangeMenuView)
        val dialog = builder.create()

        dialog.window?.decorView?.setBackgroundResource(R.drawable.menu_shape)
        dialog.show()

        val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
        val height = ViewGroup.LayoutParams.WRAP_CONTENT

        if (viewModel.activeUser?.value!!.coins < 50) {
            buyBanknote.isEnabled = false
            buyBanknote.setBackgroundTintList(gameContext.getResources().getColorStateList(R.color.inactive_state))
        } else {
            buyBanknote.isActivated = true
        }

        if (viewModel.activeUser?.value!!.banknotes < 50) {
            buyDiamond.isEnabled = false
            buyDiamond.setBackgroundTintList(gameContext.getResources().getColorStateList(R.color.inactive_state))
        } else {
            buyDiamond.isActivated = true
        }

        dialog.getWindow()?.setLayout(width, height)

        buyBanknote.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                // Button click sound
                val prefs = PreferenceManager.getDefaultSharedPreferences(gameContext)
                if (prefs.getString("sound", "on").equals("on")) {
                    var soundFile = R.raw.click_button
                    playSound(soundFile)
                }
                viewModel.activeUser?.value!!.coins -= 50
                viewModel.activeUser?.value!!.banknotes += 1
                updateAssetBar()
                dialog.cancel()
            }
        })
        buyDiamond.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                // Button click sound
                val prefs = PreferenceManager.getDefaultSharedPreferences(gameContext)
                if (prefs.getString("sound", "on").equals("on")) {
                    var soundFile = R.raw.click_button
                    playSound(soundFile)
                }
                viewModel.activeUser?.value!!.banknotes -= 50
                viewModel.activeUser?.value!!.diamonds += 1
                updateAssetBar()
                dialog.cancel()
            }
        })
    }

    // --- Options side menu ---
    // Options menu listener
    private inner class MyDrawerListener(): DrawerLayout.DrawerListener {
        override fun onDrawerOpened(drawerView: View) {}
        override fun onDrawerClosed(drawerView: View) {}
        override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}
        override fun onDrawerStateChanged(newState: Int) {
            if(newState == DrawerLayout.STATE_SETTLING) {
                var optionsMenu: DrawerLayout = findViewById(R.id.drawer_layout);
                if(optionsMenu.isDrawerOpen(GravityCompat.START)) {
                    // Close menu sound
                    val prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)
                    if (prefs.getString("sound", "on").equals("on")) {
                        var closeMenuSound = MediaPlayer.create(applicationContext, R.raw.close_window)
                        closeMenuSound.start()
                        closeMenuSound.setOnCompletionListener(MediaPlayer.OnCompletionListener { closeMenuSound ->
                            closeMenuSound.stop()
                            closeMenuSound?.release()
                        })
                    }
                } else {
                    // Open menu sound
                    val prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)
                    if (prefs.getString("sound", "on").equals("on")) {
                        var closeMenuSound = MediaPlayer.create(applicationContext, R.raw.open_window)
                        closeMenuSound.start()
                        closeMenuSound.setOnCompletionListener(MediaPlayer.OnCompletionListener { closeMenuSound ->
                            closeMenuSound.stop()
                            closeMenuSound?.release()
                        })
                    }
                }
            }
        }
    }

    // Handles selected items
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)){
            true
        }
        return super.onOptionsItemSelected(item)
    }

    // --- Side menu pop up windows ---
    // Displays window where the user can choose an avatar
    private fun chooseAvatars(avatarsHeadshots: ArrayList<String>, initialCheck: Boolean) {
        if (!activeRound) {
            val dialogbuider = AlertDialog.Builder(this)
            dialogbuider.setCancelable(false)
            dialogbuider.setTitle(R.string.choose_avatar)

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
                            val r = v!!.findViewById<View>(R.id.radiobox_avatarsList) as RadioButton
                        }

                        val avatarsListImg = v!!.findViewById<ImageView>(R.id.avatarsListImg)
                        avatarsListImg.setImageResource(
                            resources.getIdentifier(
                                viewModel.avatarList.value!![position].headShot,
                                "drawable",
                                packageName
                            )
                        )

                        val tvAvatarsName: TextView = v!!.findViewById(R.id.tv_avatarsName)
                        if (viewModel.avatarList.value!![position].headShot.startsWith("woman")) {
                            tvAvatarsName.text = "Woman " + (position + -11)
                        } else if (viewModel.avatarList.value!![position].headShot.startsWith("man")) {
                            tvAvatarsName.text = "Man " + (position + 1)
                        }

                        val r = v!!.findViewById<View>(R.id.radiobox_avatarsList) as RadioButton
                        r.isChecked = position == selectedPosition
                        r.tag = position
                        r.setOnClickListener { view ->
                            selectedPosition = view.tag as Int
                            notifyDataSetChanged()
                        }
                        viewModel.avatarLastSelectedCheckbox.value = selectedPosition
                        return v!!
                    }
                }

            dialogbuider.setAdapter(adapter) { dialog: DialogInterface?, which: Int -> }

            dialogbuider.setPositiveButton("OK") { dialogInterface: DialogInterface?, which: Int ->
                // Button click sound
                val prefs = PreferenceManager.getDefaultSharedPreferences(this)
                if (prefs.getString("sound", "on").equals("on")) {
                    var soundFile = R.raw.click_button
                    playSound(soundFile)
                }

                // Update user object and db with the selected avatar
                val tempUser = viewModel.activeUser!!.value
                tempUser!!.avatarId = viewModel.avatarLastSelectedCheckbox.value!! + 1
                viewModel.updateUser(gameContext, tempUser.id, tempUser)

                // Update viewModel active avatar
                val tempAvatar: Avatar =
                    viewModel.avatarList.value!![viewModel.avatarLastSelectedCheckbox.value!!]
                viewModel._activeAvatar = MutableLiveData(tempAvatar)

                // Check if user has chosen a language yet and if not, prompt for it
                if (viewModel.activeUser?.value!!.languageId == 0 && initialCheck) { // Open window to choose language
                    //TODO Implement
                    chooseLanguage()
                }
            }

            val dialog = dialogbuider.create()
            val listView = dialog.listView
            dialog.show()
        } else {
            Toast.makeText(gameContext, R.string.not_available_during_round, Toast.LENGTH_LONG).show()
        }
    }

    // Displays window where the user can choose a language
    private fun chooseLanguage() {
        if (!activeRound) {
            val dialogbuider = AlertDialog.Builder(this)
            dialogbuider.setCancelable(false)
            dialogbuider.setTitle(R.string.choose_language)

            val adapter: ArrayAdapter<Language> =
                object : ArrayAdapter<Language>(this, R.layout.language_list_item_layout, viewModel.languageList.value!!) {
                    var selectedPosition = 0
                    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                        var v = convertView
                        if (v == null) {
                            val vi =
                                getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                            v = vi.inflate(R.layout.language_list_item_layout, null)
                            val r = v!!.findViewById<View>(R.id.radiobox_languageList) as RadioButton
                        }

                        val languageListImg = v!!.findViewById<ImageView>(R.id.languageListImg)
                        languageListImg.setImageResource(resources.getIdentifier(viewModel.languageList.value!![position].src, "drawable", packageName))

                        val tvLanguage: TextView = v!!.findViewById(R.id.tv_languageName)
                        tvLanguage.text = viewModel.languageList.value!![position].name

                        val r = v!!.findViewById<View>(R.id.radiobox_languageList) as RadioButton
                        r.isChecked = position == selectedPosition
                        r.tag = position
                        r.setOnClickListener { view ->
                            selectedPosition = view.tag as Int
                            notifyDataSetChanged()
                        }
                        viewModel.languageLastSelectedCheckbox.value = selectedPosition
                        return v!!
                    }
                }

            dialogbuider.setAdapter(adapter) { dialog: DialogInterface?, which: Int -> }

            dialogbuider.setPositiveButton("OK") { dialogInterface: DialogInterface?, which: Int ->
                // Button click sound
                val prefs = PreferenceManager.getDefaultSharedPreferences(this)
                if (prefs.getString("sound", "on").equals("on")) {
                    var soundFile = R.raw.click_button
                    playSound(soundFile)
                }

                // Update user object and db with the selected language
                val tempUser = viewModel.activeUser!!.value
                tempUser!!.languageId = viewModel.languageLastSelectedCheckbox.value!! + 1
                viewModel.updateUser(gameContext, tempUser.id, tempUser)

                // Update viewModel active language
                val tempLanguage: Language = viewModel.languageList.value!![viewModel.languageLastSelectedCheckbox.value!!]
                viewModel._activeLanguage = MutableLiveData(tempLanguage)

                // Changes the language icon on the app bar
                if (viewModel.languageLastSelectedCheckbox.value == 0) {
                    appBarMenu?.getItem(0)?.setIcon(ContextCompat.getDrawable(gameContext, R.drawable.france));
                } else if (viewModel.languageLastSelectedCheckbox.value == 1) {
                    appBarMenu?.getItem(0)?.setIcon(ContextCompat.getDrawable(gameContext, R.drawable.uk));
                }
                appBarMenu?.getItem(0)?.setVisible(true)
            }

            val dialog = dialogbuider.create()
            val listView = dialog.listView
            dialog.show()
        } else {
            Toast.makeText(gameContext, R.string.not_available_during_round, Toast.LENGTH_LONG).show()
        }
    }

    // Displays window where the user can change their user name
    private fun chooseUsername() {
        val builder: AlertDialog.Builder = android.app.AlertDialog.Builder(this)
        builder.setTitle(R.string.choose_username)

        builder.setView(R.layout.edit_username_layout)
        val input = findViewById<EditText>(R.id.et_editPassword)

        builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
            // Button click sound
            val prefs = PreferenceManager.getDefaultSharedPreferences(this)
            if (prefs.getString("sound", "on").equals("on")) {
                var soundFile = R.raw.click_button
                playSound(soundFile)
            }

            // Update user object and db with the selected user name
            val tempUser = viewModel.activeUser!!.value
            tempUser!!.username = input.text.toString()
            viewModel.updateUser(gameContext, tempUser.id, tempUser)

            getActionBar()?.setTitle(viewModel.activeUser?.value!!.username);
            supportActionBar?.setTitle(viewModel.activeUser?.value!!.username);
        })
        builder.setNegativeButton(R.string.cancel, DialogInterface.OnClickListener {dialog, which ->
            // Button click sound
            val prefs = PreferenceManager.getDefaultSharedPreferences(this)
            if (prefs.getString("sound", "on").equals("on")) {
                var soundFile = R.raw.click_button
                playSound(soundFile)
            }
            dialog.cancel() })
        builder.show()
    }

    // Displays window where the user can change their password
    private fun choosePassword() {
        val builder: AlertDialog.Builder = android.app.AlertDialog.Builder(this)
        builder.setTitle(R.string.choose_password)

        builder.setView(R.layout.edit_password_layout)
        val input = findViewById<EditText>(R.id.et_editPassword)

        builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
            // Button click sound
            val prefs = PreferenceManager.getDefaultSharedPreferences(this)
            if (prefs.getString("sound", "on").equals("on")) {
                var soundFile = R.raw.click_button
                playSound(soundFile)
            }

            // Update user object and db with the selected password
            val tempUser = viewModel.activeUser!!.value
            tempUser!!.password = input.text.toString()
            viewModel.updateUser(gameContext, tempUser.id, tempUser)
        })
        builder.setNegativeButton(R.string.cancel, DialogInterface.OnClickListener {dialog, which ->
            // Button click sound
            val prefs = PreferenceManager.getDefaultSharedPreferences(this)
            if (prefs.getString("sound", "on").equals("on")) {
                var soundFile = R.raw.click_button
                playSound(soundFile)
            }
            dialog.cancel() })
        builder.show()
    }

    // Displays window where the user can delete their account
    private fun deleteAccount() {
        val dialogbuider = AlertDialog.Builder(this)
        dialogbuider.setCancelable(false)
        dialogbuider.setTitle(R.string.delete_account)
        dialogbuider.setView(R.layout.delete_account_layout)

        dialogbuider.setPositiveButton(R.string.yes, DialogInterface.OnClickListener { dialog, which ->
            // Button click sound
            val prefs = PreferenceManager.getDefaultSharedPreferences(this)
            if (prefs.getString("sound", "on").equals("on")) {
                var soundFile = R.raw.click_button
                playSound(soundFile)
            }

            // Delete the user's account from the database
            viewModel.deleteUser(gameContext, viewModel.activeUser?.value!!.id)

            // Clear shared preferences
            prefs.edit().clear().commit()


            // Redirect to log in page
            val loginIntent = Intent(applicationContext, MainActivity::class.java)
            startActivity(loginIntent)

        })
        dialogbuider.setNegativeButton(R.string.no, DialogInterface.OnClickListener {dialog, which ->
            // Button click sound
            val prefs = PreferenceManager.getDefaultSharedPreferences(this)
            if (prefs.getString("sound", "on").equals("on")) {
                var soundFile = R.raw.click_button
                playSound(soundFile)
            }
            dialog.cancel() })

        val dialog = dialogbuider.create()
        val listView = dialog.listView
        dialog.show()
    }

    // Displays window where the user can abandon the current game round
    private fun showAbandonGameRound() {
        val dialogbuider = AlertDialog.Builder(this)

        val textView = TextView(gameContext)
        textView.setText(R.string.abandon_round)
        textView.setPadding(20, 30, 20, 30)
        textView.textSize = 25f
        textView.setTextColor(ContextCompat.getColor(gameContext, R.color.menu_text_colour))

        dialogbuider.setCustomTitle(textView);
        dialogbuider.setView(R.layout.abandon_round_layout)

        dialogbuider.setPositiveButton(R.string.yes, DialogInterface.OnClickListener { dialog, which ->
            // Button click sound
            val prefs = PreferenceManager.getDefaultSharedPreferences(this)
            if (prefs.getString("sound", "on").equals("on")) {
                var soundFile = R.raw.click_button
                playSound(soundFile)
            }

            abandonGameRound()
            dialog.cancel()
        })
        dialogbuider.setNegativeButton(R.string.no, DialogInterface.OnClickListener {dialog, which ->
            // Button click sound
            val prefs = PreferenceManager.getDefaultSharedPreferences(this)
            if (prefs.getString("sound", "on").equals("on")) {
                var soundFile = R.raw.click_button
                playSound(soundFile)
            }
            dialog.cancel() })

        val dialog = dialogbuider.create()
        val listView = dialog.listView
        dialog.show()
    }

    // Displays window containing the game rules
    private fun displayHelp() {
        val dialogbuider = AlertDialog.Builder(this)
        dialogbuider.setCancelable(false)
        dialogbuider.setTitle(R.string.game_help)
        dialogbuider.setView(R.layout.game_help_layout)

        dialogbuider.setNegativeButton("OK", DialogInterface.OnClickListener {dialog, which ->
            // Button click sound
            val prefs = PreferenceManager.getDefaultSharedPreferences(this)
            if (prefs.getString("sound", "on").equals("on")) {
                var soundFile = R.raw.click_button
                playSound(soundFile)
            }
            dialog.cancel() })
//TODO create this layout
        val dialog = dialogbuider.create()
        val listView = dialog.listView
        dialog.show()
    }

    // --- Sound effects ---
    // Sound effects
    private fun playSound(soundFile: Int) {
        var soundToPlay = MediaPlayer.create(applicationContext, soundFile)
        soundToPlay.start()
        soundToPlay.setOnCompletionListener(MediaPlayer.OnCompletionListener { soundToPlay ->
            soundToPlay.stop()
            soundToPlay?.release()
        })
    }

    // App bar menu functions
    override fun onCreateOptionsMenu(menu: Menu?): Boolean { // Create status bar menu with sound icon
        val inflater: MenuInflater = getMenuInflater()
        inflater.inflate(R.menu.sound_options, menu)

        appBarMenu = menu

        // Change sound menu icon according to the settings in preferences
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = prefs.edit()
        if (prefs.getString("sound", "on").equals("off")) {
            menu?.findItem(R.id.soundOptions)?.setIcon(R.drawable.sound_off)
            menu?.findItem(R.id.soundOptions)?.setTitle("Off")
        }
        editor.apply()
        return true
    }

    // Handles sound icon click listener
    fun onItemClick(item: MenuItem) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)

        // Button click sound
        if (prefs.getString("sound", "on").equals("on")) {
            var buttonClickSound = MediaPlayer.create(this, R.raw.click_button)
            buttonClickSound.start()
            buttonClickSound.setOnCompletionListener(MediaPlayer.OnCompletionListener { buttonClickSound ->
                buttonClickSound.stop()
                buttonClickSound?.release()
            })
        }

        // Change sound preferences
        val editor = prefs.edit()
        if (item.title.equals("On")) {
            item.setIcon(R.drawable.sound_off)
            item.setTitle("Off")
            editor.putString("sound", "off")
        } else if (item.title.equals("Off")) {
            item.setIcon(R.drawable.sound_on)
            item.setTitle("On")
            editor.putString("sound", "on")
        }
        editor.apply()
    }

    // --- Graphics ---
    // Hides end animations
    fun hideAllAnimations() {
        binding.endAnimLeft.isVisible = false
        binding.endAnimRight.isVisible = false
    }

    // Hides the displayed word
    fun hideDisplayedWord() {
        binding.displayedWord.visibility = View.INVISIBLE
    }

    // Show displayed word
    fun showDisplayedWord() {
        binding.displayedWord.setText(viewModel.word.value?.displayedWord)
        binding.displayedWord.visibility = View.VISIBLE
    }

    // Hides the keyboard
    fun hideKeyboard() {
        binding.keyboard.visibility = View.INVISIBLE
    }

    // Show keyboard
    fun showKeyboard() {
        binding.keyboard.visibility = View.VISIBLE
    }

    // Hides the hint button
    fun hideHintBtn() {
        binding.hintBtn.visibility = View.INVISIBLE
    }

    // Show the hint button
    fun showHintBtn() {
        binding.hintBtn.visibility = View.VISIBLE
    }

    // Hides the exchange button
    fun hideExchangeBtn() {
        binding.exchangeBtn.visibility = View.INVISIBLE
    }

    // Show the exchange button
    fun showExchangeBtn() {
        binding.exchangeBtn.visibility = View.VISIBLE
    }

    // Hides the abandon button
    fun hideAbandonBtn() {
        binding.abandonBtn.visibility = View.INVISIBLE
    }

    // Show the abandon button
    fun showAbandonBtn() {
        binding.abandonBtn.visibility = View.VISIBLE
    }

    // Hides the asset bar
    fun hideAssetBar() {
        binding.gameAssetsBar.visibility = View.INVISIBLE
    }

    // Show the asset bar
    fun showAssetBar() {
        binding.gameAssetsBar.visibility = View.VISIBLE
    }

    // Update the values on the asset bar
    private fun updateAssetBar() {
        binding.tvScore.setText(viewModel.activeUser?.value!!.score.toString())
        binding.tvCoins.setText(viewModel.activeUser?.value!!.coins.toString())
        binding.tvBanknotes.setText(viewModel.activeUser?.value!!.banknotes.toString())
        binding.tvDiamonds.setText(viewModel.activeUser?.value!!.diamonds.toString())
        binding.tvLives.setText(viewModel.activeUser?.value!!.lives.toString())
    }

    // Hides new round button
    fun hideNewRoundBtn() {
        binding.newRoundBtn.visibility = View.INVISIBLE
    }

    // Show new round button
    fun showNewRoundBtn() {
        binding.newRoundBtn.visibility = View.VISIBLE
    }

    // --- ViewModel ---
    //Initialises view model observables
    private fun initViewModel() {
        // When view model word changes, startNewRoundUi is called
        viewModel.word.observe(this, this::validateRandomWord)
    }

    // --- Game round ---
    // Reset the view model game round object
    private fun resetGameRound() {
        val tempGameRound = GameRound()
        viewModel._activeGameRound = MutableLiveData(tempGameRound)
    }

    // Initiates a new round
    private fun initiateNewRound(view: View) {
        resetGameRound()
        hideNewRoundBtn()
        hideAllAnimations()
        hideDisplayedWord()
        hideAvatarGraphics()

        binding.waitingPlaceholder.isVisible = true

        if (viewModel.activeLanguage?.value?.name.equals("Français")) {
            viewModel.getRandomWordFr(view)
        } else if (viewModel.activeLanguage?.value?.name.equals("English")) {
            viewModel.getRandomWordEn(view)
        }
    }

    // Check if the word found isn't too long. If it is, find another one
    private fun validateRandomWord(it: Word) {
        if (viewModel.word.value!!.hiddenWord.length <= 15) {
            startNewRound()
        } else {
            initiateNewRound(gameView!!)
        }
    }

    // Starts the UI for a new round after getting a new word object
    private fun startNewRound() {
        if (!activeRound) {
            activeRound = true
            resetKeyboard()

            // New word sound
            val prefs = PreferenceManager.getDefaultSharedPreferences(this)
            if (prefs.getString("sound", "on").equals("on")) {
                var soundFile = R.raw.new_word
                playSound(soundFile)
            }
        }
        binding.waitingPlaceholder.isVisible = false

        showKeyboard()
        updateAssetBar()
        showAssetBar()
        showHintBtn()
        showExchangeBtn()
        showAbandonBtn()
        showDisplayedWord()
        //TODO Implement rest
    }

    // Abandon round
    private fun abandonGameRound() {
        viewModel.activeGameRound?.value!!.letterMisses = 0
        activeRound = false
        resetKeyboard()
        viewModel.activeUser?.value!!.diamonds = 0
        viewModel.activeUser?.value!!.banknotes = 0
        viewModel.activeUser?.value!!.coins = 0
        viewModel.activeUser?.value!!.lives = 3
        viewModel.activeUser?.value!!.score = 0

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
        //TODO Implement the rest
        // reset displayed avatar

        // Abandon game sound
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        if (prefs.getString("sound", "on").equals("on")) {
            var soundFile = R.raw.abandon_round
            playSound(soundFile)
        }
    }

    // Guessed the word
    fun wordGuessed() {
        viewModel.activeGameRound?.value!!.letterMisses = 0
        activeRound = false
        hideKeyboard()
        resetKeyboard()
        binding.newRoundBtn.setText(R.string.continue_)
        showNewRoundBtn()
        pickEndAnimation("win")
        //TODO Implement the rest

        // Win word sound
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        if (prefs.getString("sound", "on").equals("on")) {
            var soundFile = R.raw.win_word
            playSound(soundFile)
        }
    }

    // Failed the word
    fun wordFailed() {
        // Fail word sound
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        if (prefs.getString("sound", "on").equals("on")) {
            var soundFile = R.raw.fail_word
            playSound(soundFile)
        }

        var tempWord = viewModel.word.value
        tempWord!!.displayedWord = tempWord.hiddenWord
        viewModel._word.value = tempWord

        binding.newRoundBtn.setText(R.string.continue_)
        viewModel.activeGameRound?.value!!.letterMisses = 0
        activeRound = false
        showNewRoundBtn()
        pickEndAnimation("lose")
        resetKeyboard()
        hideKeyboard()
        //TODO Implement the rest
    }

    // Randomly picks an end animation and displays it
    private fun pickEndAnimation(type: String) {
        val randomAnimNr = (0..1).random()

        when (randomAnimNr) {
            0 -> {
                if (type.equals("win")) {
                    Glide.with(this).load(R.drawable.win1).into(binding.endAnimLeft)
                    binding.endAnimLeft.isVisible = true
                } else if (type.equals("lose")) {
                    Glide.with(this).load(R.drawable.lose1).into(binding.endAnimLeft)
                    binding.endAnimLeft.isVisible = true
                }
            }
            1 -> {
                if (type.equals("win")) {
                    Glide.with(this).load(R.drawable.win2).into(binding.endAnimRight)
                    binding.endAnimRight.isVisible = true
                } else if (type.equals("lose")) {
                    Glide.with(this).load(R.drawable.lose2).into(binding.endAnimRight)
                    binding.endAnimRight.isVisible = true
                }
            }
        }
    }

    // --- Keyboard ---
    // Handles the letters pressed on the keyboard
    fun keyboardPressed(pressed: String, buttonPressed: Button) {
        //TODO Implement score, coins, etc.
        if (viewModel.updateDisplayedWord(pressed)) { // Guessed letter
            viewModel._activeGameRound?.value!!.setLetterboardState(pressed, 1)

            buttonPressed.setBackgroundTintList(gameContext.getResources().getColorStateList(R.color.guessed_letter))

            // Button click sound
            val prefs = PreferenceManager.getDefaultSharedPreferences(this)
            if (prefs.getString("sound", "on").equals("on")) {
                var soundFile = R.raw.click_letter_guess
                playSound(soundFile)
            }
        } else { // Missed letter
            viewModel._activeGameRound?.value!!.setLetterboardState(pressed, -1)
            viewModel.activeGameRound?.value!!.letterMisses++

            buttonPressed.setBackgroundTintList(gameContext.getResources().getColorStateList(R.color.missed_letter))

            // Button click sound
            val prefs = PreferenceManager.getDefaultSharedPreferences(this)
            if (prefs.getString("sound", "on").equals("on")) {
                var soundFile = R.raw.click_letter_miss
                playSound(soundFile)
            }

            lifecycleScope.launch {
                updateAvatar(viewModel.activeUser!!.value!!.avatarId)
            }

            if (viewModel.activeGameRound!!.value!!.letterMisses == 6) { // Word failed
                wordFailed()
            }
        }
        buttonPressed.isEnabled = false

        viewModel.activeGameRound?.value!!.letterGuessed(pressed)

        if (viewModel.activeGameRound?.value!!.guessedLetters == viewModel.word.value?.hiddenWord.toString().length) {
            wordGuessed()
        }
    }

    // Reset the keyboard
    private fun resetKeyboard() {
        var c: Char

        c = 'A'
        while (c <= 'Z') {
            var letterBtn: Button = findViewById(resources.getIdentifier("keyboard" + c.toString(), "id", packageName))
            letterBtn.isEnabled = true
            letterBtn.setBackgroundTintList(gameContext.getResources().getColorStateList(R.color.reset_letter))
            viewModel._activeGameRound?.value!!.setLetterboardState(c.toString(), 0)

            ++c
        }
    }

    // --- Displayed avatar ---
    // Hides all the graphics related to the avatar
    fun hideAvatarGraphics() {
        // Gets the layer drawable
        val layerDrawable = resources.getDrawable(R.drawable.layers_gallows) as LayerDrawable

        //Hides them all
        for (i in 1..layerDrawable.numberOfLayers - 1) { // starts with 1 because 0 is the gallows
            layerDrawable.getDrawable(i).alpha = 0
        }

        // Gets the ImageView where the layer drawable is by its id
        val layoutlist1 = findViewById<View>(R.id.game_gallows_top) as ImageView
        // Sets its src to the new updated layer drawable
        layoutlist1.setImageDrawable(layerDrawable)
    }

    // Update displayed avatar
    private suspend fun updateAvatar(id: Int) {
        // Get displayedAvatar in active game round
        val displayedAvatar = viewModel.activeGameRound?.value!!.displayedAvatar
        val nrOfMisses = viewModel.activeGameRound?.value!!.letterMisses

        // Hides all displayed avatar graphics
        hideAvatarGraphics()

        // Gets relevant Ids from avatar object
        var avatarComplexion = viewModel.activeAvatar!!.value!!.complexion
        var avatarExtra = viewModel.activeAvatar!!.value!!.extrasId
        var avatarEyes = viewModel.activeAvatar!!.value!!.eyesId
        var avatarEyebrows = viewModel.activeAvatar!!.value!!.eyebrowsId
        var avatarMouth = viewModel.activeAvatar!!.value!!.mouthId

        // Gets the layer drawable
        val layerDrawable = resources.getDrawable(R.drawable.layers_gallows) as LayerDrawable

        // Extra: Gets the resource id, then gets the item drawable by its id
        var extra: Drawable? = null
        if (avatarExtra > 0) {
            var extraResourceName = viewModel.findExtraById(gameContext, avatarExtra.toLong())?.get(0)?.src
            extra = layerDrawable.findDrawableByLayerId(getStringIdentifier(gameContext, extraResourceName))
        }

        // Eyes: Gets the resource id, then gets the item drawable by its id
        var eyesResourceName = viewModel.findEyesById(gameContext, avatarEyes.toLong())?.get(0)?.src
        val eyes = layerDrawable.findDrawableByLayerId(getStringIdentifier(gameContext, eyesResourceName))

        // Eyebrows: Gets the resource id, then gets the item drawable by its id
        var eyebrowsResourceName = viewModel.findEyebrowsById(gameContext, avatarEyebrows.toLong())?.get(0)?.src
        val eyebrows = layerDrawable.findDrawableByLayerId(getStringIdentifier(gameContext, eyebrowsResourceName))

        // Mouth: Gets the resource id, then gets the item drawable by its id
        var mouthResourceName = viewModel.findMouthById(gameContext, avatarMouth.toLong())?.get(0)?.src
        val mouth = layerDrawable.findDrawableByLayerId(getStringIdentifier(gameContext, mouthResourceName))

        // Body parts: Gets the item drawable by its id
        val head = layerDrawable.findDrawableByLayerId(getStringIdentifier(gameContext, viewModel.activeAvatar!!.value!!.headSrc))
        val leftArm = layerDrawable.findDrawableByLayerId(getStringIdentifier(gameContext, viewModel.activeAvatar!!.value!!.leftArmSrc))
        val rightArm = layerDrawable.findDrawableByLayerId(getStringIdentifier(gameContext, viewModel.activeAvatar!!.value!!.rightArmSrc))
        val leftLeg = layerDrawable.findDrawableByLayerId(getStringIdentifier(gameContext, viewModel.activeAvatar!!.value!!.leftLegSrc))
        val rightLeg = layerDrawable.findDrawableByLayerId(getStringIdentifier(gameContext, viewModel.activeAvatar!!.value!!.rightLegSrc))
        val torso = layerDrawable.findDrawableByLayerId(getStringIdentifier(gameContext, viewModel.activeAvatar!!.value!!.torsoSrc))

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
            5 -> { // Shpw head, torso, both arms, and left leg
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
    fun getStringIdentifier(context: Context, name: String?): Int {
        return resources.getIdentifier(name, "id", packageName)
    }
}
