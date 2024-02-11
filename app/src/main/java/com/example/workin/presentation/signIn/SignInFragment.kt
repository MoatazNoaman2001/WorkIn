package com.example.workin.presentation.signIn

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.workin.MainAppActivity
import com.example.workin.R
import com.example.workin.commons.SigningLogistic
import com.example.workin.databinding.FragmentSignInBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


private const val TAG = "SignInFragment"

@AndroidEntryPoint
class SignInFragment : Fragment() {
    lateinit var binding: FragmentSignInBinding
    lateinit var controller: NavController
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignInBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        controller = Navigation.findNavController(requireView())
        binding.registering.setOnClickListener {
            controller.navigate(R.id.action_signInFragment_to_signUpFragment)
        }
        binding.forgottenPasswordBtn.setOnClickListener {
            controller.navigate(R.id.action_signInFragment_to_forgottenPasswordFragment)
        }

        binding.loginBtn.setOnClickListener {
            val email = binding.EmailTextInput.editText?.text.toString()
            val password = binding.PassWordTextInput.editText?.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty())
                CoroutineScope(Dispatchers.IO).launch {
                    val res = SigningLogistic.signInUser(email, password)
                    MainScope().launch {
                        when (res.isSuccess) {
                            true -> {res.onSuccess {
                                Toast.makeText(requireContext() , it , Toast.LENGTH_SHORT).show() }
                                requireActivity().startActivity(Intent(requireContext() , MainAppActivity::class.java))
                            }
                            else -> {SigningLogistic.FB_ExcpetionsHandler(res.exceptionOrNull() , context = requireContext())}
                        }
                    }
                }
        }

        binding.EmailTextInput.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                controller.currentBackStackEntry?.savedStateHandle?.set("email", s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

    }
}

