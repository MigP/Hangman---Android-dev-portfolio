package bf.be.android.hangman.view

import android.os.Bundle
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
        val enteredUsername = binding.registerUsernameInput.text.toString()
        val enteredPassword = binding.registerPasswordInput.text.toString()
        val enteredConfirmation = binding.registerConfirmPasswordInput.text.toString()

        if (enteredPassword.equals(enteredConfirmation)) {
            //TODO Alert error
        }

        viewLifecycleOwner.lifecycleScope.launch {

            if (viewModel.usernameExists(requireContext(), enteredUsername)) {
                //TODO Alert error. Username already exists
            } else {
                //TODO Registration successful. Go to Log in
                viewModel.insertUser(requireContext(), User(enteredUsername, enteredPassword))
            }



//            println("$$$$$$$$$$$$$$$ Number of users: " + viewModel.findAllUsers(requireContext()).size)
//            if (viewModel.findUserById(requireContext(), 1)!!.size > 0) {
//                println("$$$$$$$$$$$$$$$ First username: " + viewModel.findUserById(requireContext(), 1)!![0].username)
//            } else {
//                println("$$$$$$$$$$$$$$$ First username: NONE")
//            }
//            println("$$$$$$$$$$$$$$$ Created user: " + viewModel.activeUser?.value!!.username)
        }
    }
}