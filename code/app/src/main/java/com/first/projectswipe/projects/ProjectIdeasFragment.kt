package com.first.projectswipe.projects

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class ProjectIdeasFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Temporary layout — replace with your own
        return inflater.inflate(R.layout.simple_list_item_1, container, false)
    }
}