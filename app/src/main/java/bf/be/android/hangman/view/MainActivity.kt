package bf.be.android.hangman.view

import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.content.Intent
import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import bf.be.android.hangman.R
import bf.be.android.hangman.databinding.ActivityMainBinding
import java.util.*
import kotlin.concurrent.timerTask


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var introAnimationDelayTime: Long = 250

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // View binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Check if this user has opted to be remembered and if so, bypass log in
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = prefs.edit()
        if(prefs.getString("rememberMe", "").equals("true")) {
            introAnimationDelayTime = 0
            val gameIntent = Intent(this, GameActivity::class.java)
            startActivity(gameIntent)
        } else {
            editor.putString("rememberMe", "false")
            editor.apply()
        }

        introAnimations(introAnimationDelayTime)

        // Show log in fragment (default starting place)
        val fm: FragmentManager = supportFragmentManager
        val transaction: FragmentTransaction = fm.beginTransaction()
        val loginFragment = LoginFragment.newInstance()

        transaction
            .add(R.id.fragment_container_view, loginFragment)
            .commit()

        // Adds a listener to the fragments for when the user clicks to switch to another fragment
        fm.setFragmentResultListener("requestKey", this) { key, bundle ->
            val result = bundle.getString("targetFragment")
            if (result.equals("createAccount")) { // Replace the fragment
                val transaction: FragmentTransaction = fm.beginTransaction()
                transaction.replace(R.id.fragment_container_view, RegisterFragment.newInstance())
                transaction.addToBackStack(null)
                transaction.commit()
            } else if (result.equals("LogIn")) { // Replace the fragment
                val transaction: FragmentTransaction = fm.beginTransaction()
                transaction.replace(R.id.fragment_container_view, LoginFragment.newInstance())
                transaction.addToBackStack(null)
                transaction.commit()
            }
        }
    }

    fun introAnimations(introAnimationDelayTime: Long) {
        // Title blow up
        val titleBlowUp: ValueAnimator = ValueAnimator.ofFloat(0f, 110f)
        titleBlowUp.addUpdateListener(AnimatorUpdateListener { valueAnimator ->
            val animatedValue = valueAnimator.animatedValue as Float
            binding.introTitle.setTextSize(animatedValue)
        })
        titleBlowUp.setDuration(350);
        titleBlowUp.startDelay = 250
        titleBlowUp.start();

        // Intro swoosh sound
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        if (prefs.getString("sound", "on").equals("on")) {
            Timer().schedule(timerTask {
                var soundFile = R.raw.intro_swoosh
                playSound(soundFile)
            }, introAnimationDelayTime)
        }

        val titleShrink: ValueAnimator = ValueAnimator.ofFloat(110f, 80f)
        titleShrink.addUpdateListener(AnimatorUpdateListener { valueAnimator ->
            val animatedValue = valueAnimator.animatedValue as Float
            binding.introTitle.setTextSize(animatedValue)
        })
        titleShrink.setDuration(150);
        titleShrink.startDelay = 600
        titleShrink.start();

        // Intro image fade out
        val introFadeOut: Animation = AnimationUtils.loadAnimation(this, R.anim.fadeout1s)
        introFadeOut.startOffset = 800
        introFadeOut.setFillAfter(true);
        binding.introGallowsTop.startAnimation(introFadeOut)
        binding.introGallowsBottom.startAnimation(introFadeOut)

        // Fragment fade in
        val fragmentFadeIn: Animation = AnimationUtils.loadAnimation(this, R.anim.fadein1s)
        fragmentFadeIn.startOffset = 1500
        fragmentFadeIn.setFillAfter(true);
        binding.fragmentContainerView.startAnimation(fragmentFadeIn)
    }

    // Sound effects
    private fun playSound(soundFile: Int) {
        var soundToPlay = MediaPlayer.create(applicationContext, soundFile)
        soundToPlay.start()
        soundToPlay.setOnCompletionListener(OnCompletionListener { soundToPlay ->
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
}