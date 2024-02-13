package com.example.workin.presentation.forgottenPassword

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.workin.R
import com.example.workin.databinding.FragmentEmailRecicvedBinding

private const val TAG = "EmailReceivedFragment"

private const val ID = "id"
class EmailReceivedFragment : Fragment() {
   lateinit var binding: FragmentEmailRecicvedBinding
   lateinit var controller: NavController
   private var id = 0


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        controller = Navigation.findNavController(requireView())
        id = requireArguments().getInt(ID)
        if (id == 5){
            binding.headLine.text = getString(R.string.email_verification)
            binding.subtitle.text = getString(R.string.check_mail_box_to_verify_email_and_sign_in_with_you_email)
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner , object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                controller.popBackStack()
                controller.popBackStack()
            }
        })

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEmailRecicvedBinding.inflate(layoutInflater)
        return binding.root
    }

    companion object{
        fun getInstance(id:Int) = EmailReceivedFragment().apply {
            arguments = Bundle().apply {
                putInt(ID , id)
            }
        }
    }
}