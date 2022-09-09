package bf.be.android.hangman.view

import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import bf.be.android.hangman.R
import bf.be.android.hangman.databinding.ActivityMainBinding
import bf.be.android.hangman.viewModel.MainViewModel


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    //Create a ViewModel
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = prefs.edit()

        // Check if this user has opted to be remembered and if so, bypass log in
        if(prefs.getString("rememberMe", "") == "true") {
            //TODO User remembered. Go to game
        } else {
            editor.putString("rememberMe", "false")
            editor.apply()
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Title blow up
        val titleBlowUp: ValueAnimator = ValueAnimator.ofFloat(0f, 110f)
        titleBlowUp.addUpdateListener(AnimatorUpdateListener { valueAnimator ->
            val animatedValue = valueAnimator.animatedValue as Float
            binding.introTitle.setTextSize(animatedValue)
        })
        titleBlowUp.setDuration(350);
        titleBlowUp.startDelay = 250
        titleBlowUp.start();
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
}