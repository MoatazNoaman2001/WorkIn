package com.example.workin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.workin.databinding.FragmentSubtitleAndSummaryBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class subtitleAndSummaryFragment : Fragment() {
    lateinit var binding: FragmentSubtitleAndSummaryBinding
    private val sumViewModel: SummaryViewModelImpl by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.next.setOnClickListener {
            if (updateUser())
                findNavController().navigate(R.id.action_subtitleAndSummaryFragment_to_addtionalInfo)
            else {
                if (binding.SummaryTextInput.editText?.text?.isEmpty()!!)
                    binding.SummaryTextInput.error = "Must type summary"
                if (binding.subHeadTextInput.editText?.text?.isEmpty()!!)
                    binding.subHeadTextInput.error = "Must type sub title"
            }
        }


        binding.subHeadTextInput.editText?.addTextChangedListener(
            onTextChanged = { text: CharSequence?, start: Int, before: Int, count: Int ->
                if (text.isNullOrBlank()){
                    binding.subHeadTextInput.error= ""
                }
            }
        )

        binding.SummaryTextInput.editText?.addTextChangedListener(
            onTextChanged = { text: CharSequence?, start: Int, before: Int, count: Int ->
                if (text.isNullOrBlank()){
                    binding.SummaryTextInput.error= ""
                }
            }
        )

    }

    private fun updateUser(): Boolean {
        val suTitle= binding.subHeadTextInput.editText?.text?.toString()?.trim()
        val summary= binding.SummaryTextInput.editText?.text?.toString()?.trim()
        return if (summary?.isNotEmpty()!! && suTitle?.isNotEmpty()!!){
            sumViewModel.updateUser(suTitle!!)
            sumViewModel.updateSummary(summary)
            true
        }else{
            false
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSubtitleAndSummaryBinding.inflate(layoutInflater)
        return binding.root
    }
}