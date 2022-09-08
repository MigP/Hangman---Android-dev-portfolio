package bf.be.android.hangman.view

import android.os.Bundle
import android.preference.PreferenceManager
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