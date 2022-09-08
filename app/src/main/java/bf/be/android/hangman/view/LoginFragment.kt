package bf.be.android.hangman.view

import android.os.Bundle
import android.preference.PreferenceManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import bf.be.android.hangman.R
import bf.be.android.hangman.databinding.FragmentLoginBinding
import bf.be.android.hangman.model.dal.dao.UserDao
import bf.be.android.hangman.model.dal.entities.User
import bf.be.android.hangman.viewModel.MainViewModel
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.concurrent.timerTask

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

        _binding!!.loginPasswordIcon.setOnClickListener(this::temp)

        //TODO This exists for testing only
        _binding!!.loginButton.setOnClickListener(this::login)
        // ---

        //TODO This is only here for testing
        viewModel.word.observe(viewLifecycleOwner) {
//            println("----------------word object: " + it.toString())
        }
        // ---

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            LoginFragment().apply {
            }
    }

    fun login (view: View) {
        val enteredUsername = binding.loginUsernameInput.text.toString()
        val enteredPassword = binding.loginPasswordInput.text.toString()
        val rememberMe = binding.registerRememberMe.isChecked

        viewLifecycleOwner.lifecycleScope.launch {

            if (viewModel.userExists(requireContext(), enteredUsername, enteredPassword)) {
                //TODO Login failed. Alert error
            } else {
                //TODO Login successful. Go to game. Add active user to VM
                //TODO This exists for testing only
                viewModel.getRandomWordEn(view)
                // ---
            }
        }
    }

    //TODO This exists for testing only
    fun temp(view: View) {
        viewModel.updateDisplayedWord("e")
        println("----------------word object: " + viewModel.word.value.toString())
        viewModel.updateDisplayedWord("a")
        println("----------------word object: " + viewModel.word.value.toString())
        viewModel.updateDisplayedWord("r")
        println("----------------word object: " + viewModel.word.value.toString())
    }
    // ---
}