package com.example.workin.domain.repo

import android.content.SharedPreferences
import com.example.workin.commons.Constant
import com.example.workin.commons.di.PicStorageViewModeScope
import com.example.workin.commons.di.PreferenceSummaryViewModelScope
import com.example.workin.commons.di.PreferenceViewModelScope
import com.example.workin.commons.di.SummaryFireStoreViewModeScope
import com.example.workin.domain.model.Summary
import com.example.workin.domain.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.StorageReference
import com.google.firestore.v1.StructuredAggregationQuery.Aggregation.Sum
import com.google.gson.Gson
import javax.inject.Inject

class SummaryRepoImpl
@Inject constructor(
    @PreferenceViewModelScope private val mainUserSharedPreferences: SharedPreferences,
    @PreferenceSummaryViewModelScope private val summarySharedPreferences: SharedPreferences,
    @SummaryFireStoreViewModeScope private val summaryDocRef: DocumentReference,
    private val firestore: CollectionReference
) : SummaryRepo {

    private var summary: ((Resources<Summary>) -> Unit)? = null
    override fun updateRepo(summary: Summary) {
        summaryDocRef.set(summary)

    }

    override fun inspectSum() {
        val sum = getSummaryFromPreference()
        if (sum == null)
            summary?.invoke(Resources.loading())
        else
            summary?.invoke(Resources.success(getSummaryFromPreference()!!))
        summaryDocRef.addSnapshotListener { value, error ->
            if (error == null) {
                if (value?.exists()!!) {
                    val sum = value.toObject(Summary::class.java)
                    summary?.invoke(Resources.success(sum!!))
                } else {
                    summary?.invoke(Resources.success(Summary()))
                }
            } else {
                summary?.invoke(Resources.failed(error.message!!))

            }
        }
    }

    override fun getSum(summaryRes: ((Resources<Summary>) -> Unit)) {
        this.summary = summaryRes
    }

    fun getUserFromPreference(): User? {
        return Gson().fromJson(
            mainUserSharedPreferences.getString(Constant.MainUser, null),
            User::class.java
        )
    }

    fun getSummaryFromPreference(): Summary? {
        return Gson().fromJson(
            summarySharedPreferences.getString(Constant.Sumamry, null),
            Summary::class.java
        )
    }

    fun storeSummaryFromPreference(summary: Summary) {
        summarySharedPreferences.edit().putString(
            Constant.Sumamry,
            Gson().toJson(summary, Summary::class.java)
        ).apply()
    }


}