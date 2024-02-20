package com.example.workin.domain.repo

import com.example.workin.domain.model.Summary

interface SummaryRepo {

    fun updateRepo(summary: Summary)
    fun inspectSum()

    fun getSum(summaryRes: ((Resources<Summary>)->Unit))
}