package com.example.workin.presentation.Profile.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.workin.R
import com.example.workin.databinding.FragmentEditHeadBinding
import com.example.workin.databinding.FragmentEmailRecicvedBinding


class EditHeadFragment : Fragment() {
    private lateinit var _binding: FragmentEditHeadBinding
    private val binding get() =_binding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditHeadBinding.inflate(layoutInflater)
        return binding.root
    }

}