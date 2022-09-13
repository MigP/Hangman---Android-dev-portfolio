package bf.be.android.hangman.view

import android.media.MediaPlayer
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import bf.be.android.hangman.R
import bf.be.android.hangman.databinding.FragmentLoginBinding
import bf.be.android.hangman.databinding.FragmentRegisterBinding
import bf.be.android.hangman.model.dal.entities.User
import bf.be.android.hangman.viewModel.MainViewModel
import kotlinx.coroutines.launch

class RegisterFragment : Fragment() {

    // View binding
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    //Create a ViewModel
    private val viewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        val view = binding.root

        _binding!!.registerLoginText.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                setFragmentResult("requestKey", bundleOf("targetFragment" to "LogIn"))
            }

        })

        _binding!!.registerButton.setOnClickListener(this::register)

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            RegisterFragment().apply {
            }
    }

    fun register (view: View) {
        // Button click sound
        val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        if (prefs.getString("sound", "").equals("on")) {
            var soundFile = R.raw.click_button
            playSound(soundFile)
        }

        val enteredUsername = binding.registerUsernameInput.text.toString()
        val enteredPassword = binding.registerPasswordInput.text.toString()
        val enteredConfirmation = binding.registerConfirmPasswordInput.text.toString()

        if (enteredUsername.equals("") || enteredPassword.equals("") || enteredConfirmation.equals("")) { // At least one field is empty
            Toast.makeText(requireContext(), R.string.fill_all_fields, Toast.LENGTH_LONG).show()
        } else {
            if (!enteredPassword.equals(enteredConfirmation)) { // Password confirmation entered is different from password entered
                Toast.makeText(requireContext(), R.string.passwords_different, Toast.LENGTH_LONG).show()
            } else {
                viewLifecycleOwner.lifecycleScope.launch {
                    if (viewModel.usernameExists(requireContext(), enteredUsername)) { // Chosen user name already exists
                        Toast.makeText(requireContext(), R.string.username_exists, Toast.LENGTH_LONG).show()
                    } else { // Registration successful. Go to Log in fragment
                        viewModel.insertUser(requireContext(), User(enteredUsername, enteredPassword))
                        setFragmentResult("requestKey", bundleOf("targetFragment" to "LogIn"))
                    }
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