package bf.be.android.hangman.view

import android.media.MediaPlayer
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
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
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        val view = binding.root

        _binding!!.registerLoginText.setOnClickListener {
            setFragmentResult(
                "requestKey",
                bundleOf("targetFragment" to "LogIn")
            )
        }

        _binding!!.registerButton.setOnClickListener(this::register)
        val pwVisibility = binding.registerVisibleIcon
        val pwVisibility2 = binding.registerConfirmVisibleIcon

        pwVisibility.setOnClickListener {
            if(pwVisibilityState) {
                binding.registerPasswordInput.transformationMethod = PasswordTransformationMethod.getInstance()
                pwVisibility.setImageResource(R.drawable.invisible)
            } else{
                binding.registerPasswordInput.transformationMethod = HideReturnsTransformationMethod.getInstance()
                pwVisibility.setImageResource(R.drawable.visible)
            }
            pwVisibilityState = !pwVisibilityState
        }

        pwVisibility2.setOnClickListener {
            if(pw2VisibilityState) {
                binding.registerConfirmPasswordInput.transformationMethod = PasswordTransformationMethod.getInstance()
                pwVisibility2.setImageResource(R.drawable.invisible)
            } else{
                binding.registerConfirmPasswordInput.transformationMethod = HideReturnsTransformationMethod.getInstance()
                pwVisibility2.setImageResource(R.drawable.visible)
            }
            pw2VisibilityState = !pw2VisibilityState
        }
        return view
    }

    companion object {
        var pwVisibilityState = false
        var pw2VisibilityState = false
        @JvmStatic
        fun newInstance() =
            RegisterFragment().apply {
            }
    }

    private fun register (view: View) {
        // Button click sound
        val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        if (prefs.getString("sound", "on").equals("on")) {
            val soundFile = R.raw.click_button
            playSound(soundFile)
        }

        val enteredUsername = binding.registerUsernameInput.text.toString()
        val enteredPassword = binding.registerPasswordInput.text.toString()
        val enteredConfirmation = binding.registerConfirmPasswordInput.text.toString()

        if (enteredUsername == "" || enteredPassword == "" || enteredConfirmation == "") { // At least one field is empty
            Toast.makeText(requireContext(), R.string.fill_all_fields, Toast.LENGTH_LONG).show()
        } else {
            if (enteredPassword != enteredConfirmation) { // Password confirmation entered is different from password entered
                Toast.makeText(requireContext(), R.string.passwords_different, Toast.LENGTH_LONG).show()
            } else {
                viewLifecycleOwner.lifecycleScope.launch {
                    if (viewModel.usernameExists(requireContext(), enteredUsername)) { // Chosen user name already exists
                        Toast.makeText(requireContext(), R.string.username_exists, Toast.LENGTH_LONG).show()
                    } else { // Registration successful. Go to Log in fragment
                        viewModel.insertUser(requireContext(), User(requireContext(), enteredUsername, enteredPassword))
                        setFragmentResult("requestKey", bundleOf("targetFragment" to "LogIn"))
                    }
                }
            }
        }
    }

    // Sound effects
    private fun playSound(soundFile: Int) {
        val soundToPlay = MediaPlayer.create(requireContext(), soundFile)
        soundToPlay.start()
        soundToPlay.setOnCompletionListener { soundToPlay ->
            soundToPlay.stop()
            soundToPlay?.release()
        }
    }
}