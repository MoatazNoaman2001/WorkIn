package com.example.workin

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.workin.commons.Constant
import com.example.workin.databinding.FragmentAddtionalInfoBinding
import com.example.workin.domain.model.User
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class AdditionalInfoFragment : Fragment() {
    lateinit var binding:FragmentAddtionalInfoBinding
    @Inject
    lateinit var auth:FirebaseAuth

    lateinit var datePicker: MaterialDatePicker<Long>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        datePicker = MaterialDatePicker.Builder.datePicker()
            .build()
        binding.next.setOnClickListener {
            if (auth.currentUser?.phoneNumber.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "verify phone number first to continue", Toast.LENGTH_SHORT).show()
            }else if (User().BirthDay == null ){

            }else{
                findNavController().navigate(R.id.action_addtionalInfo_to_uploadProfileImageFragment)
            }
        }
        binding.verifyBtn.setOnClickListener {
            binding.verifyBtn.isVisible = false
            binding.OTPLayout.isVisible = true
            authNumber()
        }
        datePicker.addOnPositiveButtonClickListener {
            val birthDay = Date(it)
            binding.birthDayTextInput.editText?.setText(SimpleDateFormat(Constant.birthDateFormate).format(birthDay))
        }
        binding.birthDayTextInput.setEndIconOnClickListener {
            datePicker.show(childFragmentManager, "date packer")
        }
        binding.VerifyOtpBtn.setOnClickListener {
            binding.verifyBtn.isVisible = false
            binding.verifyOtpLoading.isVisible  =true
        }
        binding.LocationTextInputLayout.setEndIconOnClickListener {
            Toast.makeText(requireContext(), "will implemented later" , Toast.LENGTH_SHORT).show()
        }
    }

    private fun authNumber() {
        val option = PhoneAuthOptions.newBuilder()
            .setPhoneNumber(binding.phoneNumberTextInput.editText?.text.toString())
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(PhoneAuthProviderCallBack(requireContext(), auth.currentUser!!, binding))
            .build()
        PhoneAuthProvider.verifyPhoneNumber(option)
    }

    internal class PhoneAuthProviderCallBack(val context: Context, val user: FirebaseUser,val  binding: FragmentAddtionalInfoBinding): PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
        override fun onVerificationCompleted(p0: PhoneAuthCredential) {
            Toast.makeText(context , "Authenticated" , Toast.LENGTH_SHORT).show()
            user.updatePhoneNumber(p0)
            binding.OTPLayout.isVisible = false
        }

        override fun onVerificationFailed(p0: FirebaseException) {
            Toast.makeText(context , "Failed Verifying" , Toast.LENGTH_SHORT).show()
            binding.OTPLayout.isVisible = false
            binding.verifyBtn.isVisible = true
        }

    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddtionalInfoBinding.inflate(layoutInflater)
        return binding.root
    }
}