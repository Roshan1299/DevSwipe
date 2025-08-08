package com.first.projectswipe.presentation.ui.collaboration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class SeekingCollaboratorsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Temporary layout
        return inflater.inflate(android.R.layout.simple_list_item_1, container, false)
    }
}
