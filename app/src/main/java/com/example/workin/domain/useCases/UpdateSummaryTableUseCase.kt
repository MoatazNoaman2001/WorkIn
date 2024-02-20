package com.example.workin.domain.useCases

import com.example.workin.domain.model.Summary
import com.example.workin.domain.model.User
import com.example.workin.domain.repo.MainUserRepoImpl
import com.example.workin.domain.repo.Resources
import com.example.workin.domain.repo.SummaryRepoImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class UpdateSummaryTableUseCase @Inject constructor(val summaryRepoImpl: SummaryRepoImpl)  {

    private val _sum = MutableStateFlow<Resources<Summary>>(Resources.loading())

    fun launch(){
        summaryRepoImpl.getSum {
            CoroutineScope(Dispatchers.IO).launch{
                _sum.emit(it)
            }
        }

    }

    fun updateSum(summary: Summary){
        summaryRepoImpl.updateRepo(summary)
    }


    operator fun invoke() = _sum
}