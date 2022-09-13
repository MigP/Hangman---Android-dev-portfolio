package bf.be.android.hangman.view

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import bf.be.android.hangman.R
import bf.be.android.hangman.databinding.FragmentLoginBinding
import bf.be.android.hangman.viewModel.MainViewModel
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    // View binding
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    //Create a ViewModel
    private val viewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val view = binding.root

        _binding!!.loginRegisterText.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                setFragmentResult("requestKey", bundleOf("targetFragment" to "createAccount"))
            }
        })

        _binding!!.loginButton.setOnClickListener(this::login)

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            LoginFragment().apply {
            }
    }

    fun login (view: View) {
        // Button click sound
        val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        if (prefs.getString("sound", "").equals("on")) {
            var soundFile = R.raw.click_button
            playSound(soundFile)
        }

        val enteredUsername = binding.loginUsernameInput.text.toString()
        val enteredPassword = binding.loginPasswordInput.text.toString()
        val rememberMe = binding.registerRememberMe

        if (enteredUsername.equals("") || enteredPassword.equals("")) { // At least one field is empty
            Toast.makeText(requireContext(), R.string.fill_all_fields, Toast.LENGTH_LONG).show()
        } else {
            viewLifecycleOwner.lifecycleScope.launch {
                val userId = viewModel.findUserId(requireContext(), enteredUsername, enteredPassword)
                if (userId > 0) { // Log in successful
                    // Adds userId and remember me option to preferences and start game
                    val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
                    val editor = prefs.edit()
                    editor.putString("userId", userId.toString())
                    val gameIntent = Intent(requireContext(), GameActivity::class.java)
                    if (rememberMe.isChecked) {
                        editor.putString("rememberMe", "true")
                    } else {
                        editor.putString("rememberMe", "false")
                    }
                    editor.apply()
                    startActivity(gameIntent)
                } else { // Failed log in
                    Toast.makeText(requireContext(), R.string.wrong_credentials, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // Sound effects
    private fun playSound(soundFile: Int) {
        var soundToPlay = MediaPlayer.create(requireContext(), soundFile)
        soundToPlay.start()
        soundToPlay.setOnCompletionListener(MediaPlayer.OnCompletionListener { soundToPlay ->
            soundToPlay.stop()
            soundToPlay?.release()
        })
    }
}