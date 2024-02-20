package com.example.workin.presentation.initialStart

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.workin.R
import com.example.workin.databinding.FragmentHelloBinding
import com.example.workin.databinding.FragmentSignInBinding
import com.example.workin.domain.repo.Resources
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.util.regex.Pattern

private const val TAG = "HelloFragment"
@AndroidEntryPoint
class HelloFragment : Fragment() {
   lateinit var _binding:FragmentHelloBinding
   private val viewmodel : HelloViewModel by viewModels<HelloViewModel>()
   private val binding
       get() = _binding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            viewmodel.user.collectLatest {
                when(it){
                    is Resources.failed -> {
                        Log.d(TAG, "onViewCreated: Error: ${it.message}")

                    }
                    is Resources.loading -> {

                    }
                    is Resources.success -> {
                        Log.d(TAG, "onViewCreated: ${it.data}")
                        MainScope().launch {
                            if (!binding.subHead.text.contains(it.data.name))
                            binding.subHead.text = binding.subHead.text.replace("Mr".toRegex()  , "Mr ${it.data.name}")
                            binding.next.setOnClickListener {
                                findNavController().navigate(R.id.action_helloFragment_to_usersCategoryFragment)
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
        _binding = FragmentHelloBinding.inflate(layoutInflater)
        return binding.root
    }
}