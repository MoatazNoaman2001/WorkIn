package com.example.workin.presentation.JobsManger

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.workin.R
import com.example.workin.databinding.FragmentAvailableJobsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AvailableJobsFragment : Fragment() {
    lateinit var _binding : FragmentAvailableJobsBinding
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAvailableJobsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}