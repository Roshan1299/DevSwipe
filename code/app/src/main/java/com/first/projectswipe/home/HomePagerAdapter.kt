package com.first.projectswipe.home

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.first.projectswipe.projects.ProjectIdeasFragment
import com.first.projectswipe.SeekingCollaboratorsFragment

class HomePagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ProjectIdeasFragment()
            1 -> SeekingCollaboratorsFragment()
            else -> throw IllegalStateException("Invalid position: $position")
        } as Fragment
    }
}