package com.example.workin.presentation.userCategories.ui

import android.content.Intent
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.workin.MainAppActivity
import com.example.workin.R
import com.example.workin.commons.SigningLogistic
import com.example.workin.databinding.UserTypeCategorySelectRecycleItemBinding
import com.example.workin.presentation.userCategories.data.models.Category
import com.google.firebase.firestore.DocumentReference

class CategoryRecycleAdapter(val preferences: SharedPreferences , val reference: DocumentReference) : ListAdapter<Category , CategoryRecycleAdapter.ViewHolder>(CategoryDiffUtils()) {

    class CategoryDiffUtils : DiffUtil.ItemCallback<Category> (){
        override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem.title == newItem.title
        }

    }
    class ViewHolder(val binding:UserTypeCategorySelectRecycleItemBinding , val preferences: SharedPreferences, val reference: DocumentReference) :RecyclerView.ViewHolder(binding.root){
        fun bindItem(category: Category){
            binding.tile.text = binding.root.context.getString(category.title)
            binding.subtitle.text = binding.root.context.getString(category.desc)
            binding.lottieAnimator.setAnimation(category.reId)
            binding.root.setOnClickListener {view->
                val user = SigningLogistic.getUserFromPreference(preferences)
                user?.category = binding.root.context.getString(category.title)
                reference.set(user!!).addOnSuccessListener {
                    SigningLogistic.storeInPreference(preferences , user)
                    Navigation.findNavController(view).navigate(R.id.action_usersCategoryFragment_to_subtitleAndSummaryFragment)
//                    binding.root.context.startActivity(Intent( binding.root.context , MainAppActivity::class.java))
                }.addOnFailureListener {
                    SigningLogistic.FB_ExcpetionsHandler(it, binding.root.context)
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(UserTypeCategorySelectRecycleItemBinding.inflate(LayoutInflater.from(parent.context) , parent , false), preferences , reference)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null)
            holder.bindItem(item)
    }
}