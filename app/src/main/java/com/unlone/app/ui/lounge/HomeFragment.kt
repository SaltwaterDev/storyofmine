package com.unlone.app.ui.lounge

import com.google.android.material.tabs.TabLayout
import androidx.viewpager2.widget.ViewPager2
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.unlone.app.R
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {
    lateinit var tabLayout: TabLayout
    lateinit var pager2: ViewPager2
    lateinit var adapter: FragmentAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_home, container, false) as ViewGroup
        tabLayout = root.findViewById(R.id.tab_layout)
        pager2 = root.findViewById(R.id.view_pager2)
        val fm = childFragmentManager
        adapter = FragmentAdapter(fm, lifecycle)
        pager2.adapter = adapter
        tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                pager2.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {// TODO ("scroll to the top")
            }
        })
        pager2.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                tabLayout.selectTab(tabLayout.getTabAt(position))
            }
        })
        val navController = NavHostFragment.findNavController(this)
        return root
    }
}