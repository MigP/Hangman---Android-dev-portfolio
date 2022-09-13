package bf.be.android.hangman.view

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
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
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)

        gameContext = this
        initialiseAvatarGraphics()

        // Create avatars and user objects
        lifecycleScope.launch {
            val allAvatars = viewModel.findAllAvatars(applicationContext)
            viewModel.avatarList = MutableLiveData(allAvatars)

            viewModel.createUser(applicationContext, prefs.getString("userId", "")!!.toLong())

            //TODO this is for testing
            binding.gameGreeting.setText("Welcome "+viewModel.activeUser?.value!!.username)
            // ---

            // Check if user has chosen an avatar and a language yet and if not, prompt for them
            if (viewModel.activeUser?.value!!.languageId == 0) {
                println("============= User needs to choose a language")
            }

            if (viewModel.activeUser?.value!!.avatarId == 0) { // Open window to choose avatar
                chooseAvatars(viewModel.getAvatarsHeadshots(applicationContext))
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

    // Displays window where the user can choose an avatar
    private fun chooseAvatars(avatarsHeadshots: ArrayList<String>) {
        viewModel.avatarLastSelectedCheckbox.value = 0
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

            fetchAvatarImgs(viewModel.avatarLastSelectedCheckbox.value!! + 1)
        }

        val dialog = dialogbuider.create()
        val listView = dialog.listView
        dialog.show()
    }

    //TODO temporary for testing
    private fun fetchAvatarImgs(id: Int) {
        // Update user object and db with the selected avatar
        val tempUser = viewModel.activeUser!!.value
        tempUser!!.avatarId = id
        viewModel.updateUser(gameContext, tempUser.id, tempUser)

        // Update viewModel active avatar
        val tempAvatar: Avatar = viewModel.avatarList.value!![id - 1]
        viewModel._activeAvatar = MutableLiveData(tempAvatar)
        avatarUpdateObserver()
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

    fun initialiseAvatarGraphics() {
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
    private fun avatarUpdateObserver() {
//        viewModel.activeAvatar?.observe(vie, Observer { updatedText ->
//            binding.gameGreeting = updatedText.
//        })

        var avatarName = "man1"
        initialiseAvatarGraphics()
        // Gets the layer drawable
        val layerDrawable = resources.getDrawable(R.drawable.layers_gallows) as LayerDrawable
        // Gets the item by its id
        val extra = layerDrawable.findDrawableByLayerId(R.id.glasses1)
        val eyes = layerDrawable.findDrawableByLayerId(R.id.face_eyes_happy_forward)
        val eyebrows = layerDrawable.findDrawableByLayerId(R.id.eyebrows_neutral_dark)
        val mouth = layerDrawable.findDrawableByLayerId(R.id.mouth_light_smiling)

        val head = layerDrawable.findDrawableByLayerId(getStringIdentifier(gameContext, viewModel.activeAvatar!!.value!!.headSrc))
        val leftArm = layerDrawable.findDrawableByLayerId(getStringIdentifier(gameContext, viewModel.activeAvatar!!.value!!.leftArmSrc))
        val rightArm = layerDrawable.findDrawableByLayerId(getStringIdentifier(gameContext, viewModel.activeAvatar!!.value!!.rightArmSrc))
        val leftLeg = layerDrawable.findDrawableByLayerId(getStringIdentifier(gameContext, viewModel.activeAvatar!!.value!!.leftLegSrc))
        val rightLeg = layerDrawable.findDrawableByLayerId(getStringIdentifier(gameContext, viewModel.activeAvatar!!.value!!.rightLegSrc))
        val torso = layerDrawable.findDrawableByLayerId(getStringIdentifier(gameContext, viewModel.activeAvatar!!.value!!.torsoSrc))


        println("---------------- Active avatar: " + viewModel.activeAvatar!!.value.toString())
        head.alpha = 255
        leftArm.alpha = 255
        rightArm.alpha = 255
        leftLeg.alpha = 255
        rightLeg.alpha = 255
        torso.alpha = 255

        extra.alpha = 0
        eyes.alpha = 255
        eyebrows.alpha = 255
        mouth.alpha = 255

        // Gets the ImageView where the layer drawable is by its id
        val layoutlist1 = findViewById<View>(R.id.game_gallows_top) as ImageView
        // Sets ts src to the new updated layer drawable
        layoutlist1.setImageDrawable(layerDrawable)
    }
}