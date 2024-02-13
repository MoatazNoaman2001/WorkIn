package com.example.workin.presentation.userCategories.data.models

import com.example.workin.R

enum class Category(val reId: Int , val title:Int , val desc: Int){
    Student(R.raw.student_anim , R.string.students , R.string.this_category_is_for_student_who_want_to_know_about_each_feid_and_seak_to_collect_scientific_knowledge_and_researches_this_focuses_on_gain_experience_not_much_in_work_and_earn_money),
    Talented(R.raw.talanted,  R.string.talented , R.string.talented_phrase),
    Developer(R.raw.developer , R.string.developer , R.string.developer_phrase),
    CEO(R.raw.ceo , R.string.manager , R.string.manager_phrase),
    Explorer(R.raw.explorer_cat , R.string.explorer , R.string.explorer_phrase)
}