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
import android.preference.PreferenceManager
import android.view.*
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import bf.be.android.hangman.R
import bf.be.android.hangman.databinding.ActivityGameBinding
import bf.be.android.hangman.model.GameRound
import bf.be.android.hangman.model.dal.entities.Avatar
import bf.be.android.hangman.viewModel.MainViewModel
import kotlinx.coroutines.launch
import java.util.*


class GameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGameBinding
    private lateinit var toggle: ActionBarDrawerToggle

    companion object {
        lateinit var gameContext: Context
    }

    //Create a ViewModel
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // View binding
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Keyboard bindings
        initialiseKeyboardBinding()

        // Shared preferences
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)

        gameContext = this

        // Initialises avatar images on the screen
        hideAvatarGraphics()

        // Creates viewModel game round object
        viewModel.createNewGameRound()

        lifecycleScope.launch {
            // Creates viewModel avatar list
            val allAvatars = viewModel.findAllAvatars(applicationContext)
            viewModel.avatarList = MutableLiveData(allAvatars)

            // Creates user object
            viewModel.createUser(applicationContext, prefs.getString("userId", "")!!.toLong())

            //TODO this is for testing
            binding.gameGreeting.setText("Welcome "+viewModel.activeUser?.value!!.username)
            // ---

            // Check if user has chosen an avatar and a language yet and if not, prompt for them
            if (viewModel.activeUser?.value!!.languageId == 0) {
                //TODO Implement
                println("============= User needs to choose a language")
            }

            if (viewModel.activeUser?.value!!.avatarId == 0) { // Open window to choose avatar
                chooseAvatars(viewModel.getAvatarsHeadshots(applicationContext))
            } else { // Starts with the avatar the user has in the database
                // Update viewModel active avatar
                val tempAvatar: Avatar = viewModel.avatarList.value!![viewModel.activeUser?.value?.avatarId!! - 1]
                viewModel._activeAvatar = MutableLiveData(tempAvatar)

                //TODO for testing only
                // Display full avatar in gallows
                lifecycleScope.launch {
                    displayFullAvatar(viewModel.activeUser!!.value!!.avatarId)
                }
                // ---
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
                        //TODO implement
                        Toast.makeText(this@GameActivity, "Change language clicked", Toast.LENGTH_SHORT).show()
                    }
                    R.id.avatarItem -> {
                        //TODO implement
                        Toast.makeText(this@GameActivity, "Change avatar clicked", Toast.LENGTH_SHORT).show()
                        lifecycleScope.launch {
                            chooseAvatars(viewModel.getAvatarsHeadshots(applicationContext))
                        }
                    }
                    R.id.usernameItem -> {
                        //TODO implement
                        Toast.makeText(this@GameActivity, "Change user name clicked", Toast.LENGTH_SHORT).show()
                    }
                    R.id.passwordItem -> {
                        //TODO implement
                        Toast.makeText(this@GameActivity, "Change password clicked", Toast.LENGTH_SHORT).show()
                    }
                    R.id.deleteItem -> {
                        //TODO Add confirmation popup and implement the rest
                        Toast.makeText(this@GameActivity, "Delete account clicked", Toast.LENGTH_SHORT).show()
                    }
                    R.id.logOutItem -> {
                        //TODO Add confirmation popup
                        // Edit remember me option in preferences and then go to log in page
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

    // Keyboard biding initialisation
    private fun initialiseKeyboardBinding() {
        binding.keyboardA.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                keyboardPressed("A")
            }
        })
        binding.keyboardB.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                keyboardPressed("B")
            }
        })
        binding.keyboardC.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                keyboardPressed("C")
            }
        })
        binding.keyboardD.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                keyboardPressed("D")
            }
        })
        binding.keyboardE.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                keyboardPressed("E")
            }
        })
        binding.keyboardF.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                keyboardPressed("F")
            }
        })
        binding.keyboardG.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                keyboardPressed("G")
            }
        })
        binding.keyboardH.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                keyboardPressed("H")
            }
        })
        binding.keyboardI.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                keyboardPressed("I")
            }
        })
        binding.keyboardJ.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                keyboardPressed("J")
            }
        })
        binding.keyboardK.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                keyboardPressed("K")
            }
        })
        binding.keyboardL.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                keyboardPressed("L")
            }
        })
        binding.keyboardM.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                keyboardPressed("M")
            }
        })
        binding.keyboardN.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                keyboardPressed("N")
            }
        })
        binding.keyboardO.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                keyboardPressed("O")
            }
        })
        binding.keyboardP.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                keyboardPressed("P")
            }
        })
        binding.keyboardQ.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                keyboardPressed("Q")
            }
        })
        binding.keyboardR.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                keyboardPressed("R")
            }
        })
        binding.keyboardS.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                keyboardPressed("S")
            }
        })
        binding.keyboardT.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                keyboardPressed("T")
            }
        })
        binding.keyboardU.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                keyboardPressed("U")
            }
        })
        binding.keyboardV.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                keyboardPressed("V")
            }
        })
        binding.keyboardW.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                keyboardPressed("W")
            }
        })
        binding.keyboardX.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                keyboardPressed("X")
            }
        })
        binding.keyboardY.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                keyboardPressed("Y")
            }
        })
        binding.keyboardZ.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                keyboardPressed("Z")
            }
        })
    }

    //TODO pass this to game round object
    fun keyboardPressed(pressed: String) {
        // Button click sound
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        if (prefs.getString("sound", "").equals("on")) {
            var soundFile = R.raw.click_letter_miss
            playSound(soundFile)
        }
    }

    // Displays window where the user can choose an avatar
    private fun chooseAvatars(avatarsHeadshots: ArrayList<String>) {
        val dialogbuider = AlertDialog.Builder(this)
        dialogbuider.setCancelable(false)
        dialogbuider.setTitle(R.string.choose_avatar)

        val adapter: ArrayAdapter<Avatar> =
            object : ArrayAdapter<Avatar>(this, R.layout.avatar_list_item_layout, viewModel.avatarList.value!!) {
                var selectedPosition = 0
                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    var v = convertView
                    if (v == null) {
                        val vi =
                            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                        v = vi.inflate(R.layout.avatar_list_item_layout, null)
                        val r = v!!.findViewById<View>(R.id.radiobox_avatarsList) as RadioButton
                    }

                    val avatarsListImg = v!!.findViewById<ImageView>(R.id.avatarsListImg)
                    avatarsListImg.setImageResource(resources.getIdentifier(viewModel.avatarList.value!![position].headShot, "drawable", packageName))

                    val tvAvatarsName: TextView = v!!.findViewById(R.id.tv_avatarsName)
                    if (viewModel.avatarList.value!![position].headShot.startsWith("woman")) {
                        tvAvatarsName.text = "Woman " + (position + - 11)
                    } else if (viewModel.avatarList.value!![position].headShot.startsWith("man")) {
                        tvAvatarsName.text = "Man " + (position + 1)
                    }

                    val r = v!!.findViewById<View>(R.id.radiobox_avatarsList) as RadioButton
                    r.isChecked = position == selectedPosition
                    r.tag = position
                    r.setOnClickListener { view ->
                        selectedPosition = view.tag as Int
                        notifyDataSetChanged()
                        viewModel.avatarLastSelectedCheckbox.value = selectedPosition
                    }
                    return v!!
                }
            }

        dialogbuider.setAdapter(adapter) { dialog: DialogInterface?, which: Int -> }

        dialogbuider.setPositiveButton("OK") { dialogInterface: DialogInterface?, which: Int ->
            // Button click sound
            val prefs = PreferenceManager.getDefaultSharedPreferences(this)
            if (prefs.getString("sound", "").equals("on")) {
                var soundFile = R.raw.click_button
                playSound(soundFile)
            }

            // Update user object and db with the selected avatar
            val tempUser = viewModel.activeUser!!.value
            tempUser!!.avatarId = viewModel.avatarLastSelectedCheckbox.value!! + 1
            viewModel.updateUser(gameContext, tempUser.id, tempUser)

            // Update viewModel active avatar
            val tempAvatar: Avatar = viewModel.avatarList.value!![viewModel.avatarLastSelectedCheckbox.value!!]
            viewModel._activeAvatar = MutableLiveData(tempAvatar)

            //TODO for testing only
            // Display full avatar in gallows
            lifecycleScope.launch {
                displayFullAvatar(viewModel.avatarLastSelectedCheckbox.value!! + 1)
            }
            // ---
        }

        val dialog = dialogbuider.create()
        val listView = dialog.listView
        dialog.show()
    }

    // Options menu functions
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
                    if (prefs.getString("sound", "").equals("on")) {
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
                    if (prefs.getString("sound", "").equals("on")) {
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)){
            true
        }
        return super.onOptionsItemSelected(item)
    }

    // Sound effects
    private fun playSound(soundFile: Int) {
        var soundToPlay = MediaPlayer.create(applicationContext, soundFile)
        soundToPlay.start()
        soundToPlay.setOnCompletionListener(MediaPlayer.OnCompletionListener { soundToPlay ->
            soundToPlay.stop()
            soundToPlay?.release()
        })
    }

    // Sound menu functions
    override fun onCreateOptionsMenu(menu: Menu?): Boolean { // Create status bar menu with sound icon
        val inflater: MenuInflater = getMenuInflater()
        inflater.inflate(R.menu.sound_options, menu)

        // Change sound menu icon according to the settings in preferences
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = prefs.edit()
        if (prefs.getString("sound", "").equals("off")) {
            menu?.findItem(R.id.soundOptions)?.setIcon(R.drawable.sound_off)
            menu?.findItem(R.id.soundOptions)?.setTitle("Off")
        }
        editor.apply()
        return true
    }

    fun onItemClick(item: MenuItem) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)

        // Button click sound
        if (prefs.getString("sound", "").equals("on")) {
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

    fun hideAvatarGraphics() {
        // Gets the layer drawable
        val layerDrawable = resources.getDrawable(R.drawable.layers_gallows) as LayerDrawable
        // Gets the item by its id
        val item1 = layerDrawable.findDrawableByLayerId(R.id.woman12_head)
        val item2 = layerDrawable.findDrawableByLayerId(R.id.woman11_head)

        //Hides them all
        for (i in 1..layerDrawable.numberOfLayers - 1) { // starts with 1 because 0 is the gallows
            layerDrawable.getDrawable(i).alpha = 0
        }
    }

    fun getStringIdentifier(context: Context, name: String?): Int {
        return resources.getIdentifier(name, "id", packageName)
    }

    @SuppressLint("ResourceType")
    private suspend fun displayFullAvatar(id: Int) {

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

        // Gets the ImageView where the layer drawable is by its id
        val layoutlist1 = findViewById<View>(R.id.game_gallows_top) as ImageView
        // Sets its src to the new updated layer drawable
        layoutlist1.setImageDrawable(layerDrawable)
    }

    fun displayKeyboard() {

    }
}