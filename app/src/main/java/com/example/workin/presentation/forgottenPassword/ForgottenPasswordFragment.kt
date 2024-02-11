package com.example.workin.presentation.forgottenPassword

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.workin.R
import com.example.workin.databinding.FragmentForgottenPasswordBinding


class ForgottenPasswordFragment : Fragment() {
    lateinit var binding: FragmentForgottenPasswordBinding
    lateinit var controller: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        controller = Navigation.findNavController(requireView())
        val email = controller.previousBackStackEntry?.savedStateHandle?.get<String>("email")
        if (!email.isNullOrEmpty()){

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding  = FragmentForgottenPasswordBinding.inflate(layoutInflater)
        return binding.root
    }

}