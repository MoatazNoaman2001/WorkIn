package com.example.workin.presentation.signIn

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.workin.MainAppActivity
import com.example.workin.R
import com.example.workin.commons.Constant
import com.example.workin.commons.SigningLogistic
import com.example.workin.databinding.FragmentSignInBinding
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named


private const val TAG = "SignInFragment"

@AndroidEntryPoint
class SignInFragment : Fragment() {
    lateinit var binding: FragmentSignInBinding
    lateinit var controller: NavController

    private val signInViewModel: SignInViewModel by viewModels()
    @Inject
    @Named(Constant.sharedFragment)
    lateinit var preferences: SharedPreferences

    @Inject
    lateinit var auth :FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignInBinding.inflate(layoutInflater)
        binding.EmailTextInput.editText?.setText(signInViewModel.email)
        binding.PassWordTextInput.editText?.setText(signInViewModel.password)
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
        CheckExistAccount()

        binding.loginBtn.setOnClickListener {
            val email = binding.EmailTextInput.editText?.text.toString()
            val password = binding.PassWordTextInput.editText?.text.toString()
            disableLogin()
            if (email.isNotEmpty() && password.isNotEmpty())
                CoroutineScope(Dispatchers.IO).launch {
                    val res = SigningLogistic.signInUser(email, password , preferences)
                    MainScope().launch {
                        when (res.isSuccess) {
                            true -> {
                                res.onSuccess {user->
                                    enableLogin()
                                    if (!user.personalImage.hasPic)
                                        controller.navigate(R.id.action_signInFragment_to_uploadProfileImage)
                                    else if (user.category.isEmpty())
                                        controller.navigate(R.id.action_signInFragment_to_usersCategoryFragment)
                                    else {
                                        requireActivity().startActivity(Intent(requireContext(), MainAppActivity::class.java))
                                        requireActivity().finish()
                                    }
                                }
                            }
                            else -> {SigningLogistic.FB_ExcpetionsHandler(res.exceptionOrNull() , context = requireContext()); enableLogin()}
                        }
                    }
                }
        }

        binding.EmailTextInput.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                controller.currentBackStackEntry?.savedStateHandle?.set(Constant.email, s.toString())
                signInViewModel.email = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        binding.PassWordTextInput.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                signInViewModel.password = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

    }

    fun enableLogin(){
        binding.loginBtn.isVisible = true
        binding.loading.isVisible =false
        binding.PassWordTextInput.editText?.isEnabled = true
        binding.EmailTextInput.editText?.isEnabled = true
    }

    fun disableLogin(){
        binding.PassWordTextInput.editText?.isEnabled = false
        binding.EmailTextInput.editText?.isEnabled = false
        binding.loginBtn.isVisible = false
        binding.loading.isVisible =true
    }
    private fun CheckExistAccount() {
        if (auth.currentUser != null && auth.currentUser?.isEmailVerified!!){
            lifecycleScope.launch(Dispatchers.IO) {
                val storedUser = SigningLogistic.checkCurrentUser(preferences)
                Log.d(TAG, "CheckExistAccount: $storedUser")
                MainScope().launch {
                    if (storedUser != null) {
                        if (!storedUser.personalImage.hasPic) {
                            controller.navigate(R.id.action_signInFragment_to_uploadProfileImage)
                        } else if(storedUser.category.isEmpty()){
                            controller.navigate(R.id.action_signInFragment_to_usersCategoryFragment)
                        }
                    }
                }
            }
        }
    }
}

