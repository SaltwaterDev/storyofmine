package com.example.unlone.ui.lounge

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class FragmentAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun createFragment(position: Int): Fragment {
        when (position) {
            1 -> return LoungeAllFragment()
            2 -> return LoungeCategoryFragment()
        }
        return LoungeFollowingFragment()
    }

    override fun getItemCount(): Int {
        return 3
    }
}