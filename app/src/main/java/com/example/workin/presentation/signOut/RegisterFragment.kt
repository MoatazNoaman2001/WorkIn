package com.example.workin.presentation.signOut

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.workin.R
import com.example.workin.commons.SigningLogistic
import com.example.workin.commons.SigningLogistic.FB_ExcpetionsHandler
import com.example.workin.commons.SigningLogistic.createNewUser
import com.example.workin.databinding.FragmentSignUpBinding
import com.example.workin.presentation.forgottenPassword.EmailReceivedFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "RegisterFragment"

@AndroidEntryPoint
class RegisterFragment : Fragment() {
    lateinit var binding: FragmentSignUpBinding
    lateinit var controller: NavController
//    private val signUpViewModel : SignUpViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        controller = Navigation.findNavController(requireView())
        binding.CreateAccountBtn.setOnClickListener {
            val name = binding.NameTextInputLayout.editText?.editableText.toString()
            val email = binding.EmailTextInput.editText?.editableText.toString()
            val password = binding.PassWordTextInput.editText?.editableText.toString()
            if (
                name.isNotEmpty() ||
                email.isNotEmpty() ||
                password.isNotEmpty()
            ) {
                CoroutineScope(Dispatchers.Unconfined).launch {
                    val res = createNewUser(name, email, password)

                    MainScope().launch {
                        when (res.isSuccess) {
                            true -> {
                                controller.navigate(R.id.action_signUpFragment_to_emailRecicvedFragment , EmailReceivedFragment.getInstance(50).requireArguments())
                            }
                            else -> {
                                res.onFailure {
                                    FB_ExcpetionsHandler(it, requireContext())
                                }
                            }
                        }
                    }

                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(layoutInflater)
        return binding.root
    }

}