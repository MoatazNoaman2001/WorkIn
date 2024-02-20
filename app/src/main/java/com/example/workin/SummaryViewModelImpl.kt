package com.example.workin

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workin.domain.model.User
import com.example.workin.domain.repo.Resources
import com.example.workin.domain.useCases.GetUserUseCase
import com.example.workin.domain.useCases.UpdateSummaryTableUseCase
import com.example.workin.domain.useCases.UpdateUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "SummaryViewModelImpl"
@HiltViewModel
class SummaryViewModelImpl @Inject constructor(
    getUserUseCase: GetUserUseCase,
    val updateUserUseCase: UpdateUserUseCase,
    val updateSummaryTableUseCase: UpdateSummaryTableUseCase
) : ViewModel(), SummaryViewModel {
    val user = getUserUseCase()

    init {
        getUserUseCase.launch()
    }

    override fun updateUser(subTitle: String) {
        when (user.value) {
            is Resources.failed -> {

            }

            is Resources.loading -> {

            }

            is Resources.success -> {
                updateUserUseCase((user.value as Resources.success<User>).data.copy(subHead = subTitle))
            }
        }
    }

    override fun updateSummary(subTitle: String) {
        updateSummaryTableUseCase.launch()
        viewModelScope.launch {
            updateSummaryTableUseCase().collectLatest {
                when(it){
                    is Resources.failed -> {
                        Log.d(TAG, "updateSummary: failed update summary")
                    }
                    is Resources.loading ->{

                    }
                    is Resources.success -> {
                        val sum = it.data
                        updateSummaryTableUseCase.updateSum(sum)
                    }
                }

            }
        }
    }

}
