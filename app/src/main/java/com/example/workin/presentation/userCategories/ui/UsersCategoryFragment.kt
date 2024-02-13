package com.example.workin.presentation.userCategories.ui

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.workin.R
import com.example.workin.commons.Constant
import com.example.workin.databinding.FragmentUsersCategoryBinding
import com.example.workin.domain.model.User
import com.example.workin.presentation.userCategories.data.models.Category
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named


@AndroidEntryPoint
class UsersCategoryFragment : Fragment() {
    lateinit var _binding: FragmentUsersCategoryBinding
    lateinit var adapter: CategoryRecycleAdapter
    private val binding get() = _binding

    @Inject
    @Named(Constant.sharedFragment)
    lateinit var preferences: SharedPreferences

    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var reference: CollectionReference

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = CategoryRecycleAdapter(preferences, reference.document(auth.currentUser?.uid!!))
        binding.categoryRecycle.adapter = adapter
        adapter.submitList(categoryList())
    }

    fun categoryList(): ArrayList<Category>{
        return ArrayList(Category.entries)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUsersCategoryBinding.inflate(layoutInflater)
        return binding.root
    }
}