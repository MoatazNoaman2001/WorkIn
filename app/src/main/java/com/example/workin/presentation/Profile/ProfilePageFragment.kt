package com.example.workin.presentation.Profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.workin.R
import com.example.workin.databinding.FragmentProfilePageBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ProfilePageFragment : Fragment() {
    lateinit var _binding :FragmentProfilePageBinding
    private val binding get() = _binding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfilePageBinding.inflate(layoutInflater)
        return binding.root
    }
}