package com.first.projectswipe.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.first.projectswipe.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FilterBottomSheet : BottomSheetDialogFragment() {

    private var selectedDifficulty: String? = null
    private val selectedTags = mutableSetOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_filter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup difficulty chips
        setupChipGroup(
            view.findViewById(R.id.difficultyGroup),
            listOf("Beginner", "Intermediate", "Advanced"),
            selectedDifficulty
        ) { selected ->
            selectedDifficulty = selected
        }

        // Setup tags chips
        setupChipGroup(
            view.findViewById(R.id.tagsGroup),
            listOf("AI/ML", "Web Dev", "Mobile Apps", "UI/UX", "Game Dev"),
            null,
            allowMultiSelect = true
        ) { selected ->
            selected?.let {
                if (selectedTags.contains(it)) {
                    selectedTags.remove(it)
                } else {
                    selectedTags.add(it)
                }
            }
        }

        // Reset button
        view.findViewById<Button>(R.id.resetButton).setOnClickListener {
            resetFilters()
        }

        // Apply button
        view.findViewById<Button>(R.id.applyButton).setOnClickListener {
            applyFilters()
            dismiss()
        }
    }

    private fun setupChipGroup(
        container: ViewGroup,
        items: List<String>,
        selectedItem: String? = null,
        allowMultiSelect: Boolean = false,
        onSelectionChanged: (String?) -> Unit
    ) {
        container.removeAllViews()

        items.forEach { item ->
            val chip = LayoutInflater.from(context).inflate(
                if (selectedItem == item || (allowMultiSelect && selectedTags.contains(item))) {
                    R.layout.chip_interest_selected
                } else {
                    R.layout.chip_interest_unselected
                },
                container,
                false
            )

            chip.findViewById<TextView>(R.id.chipText).text = item
            chip.setOnClickListener {
                if (!allowMultiSelect) {
                    // For single selection, clear all chips first
                    for (i in 0 until container.childCount) {
                        val child = container.getChildAt(i)
                        if (child.findViewById<TextView>(R.id.chipText).text.toString() != item) {
                            child.setBackgroundResource(R.drawable.chip_unselected_bg)
                        }
                    }
                }

                // Toggle the clicked chip
                val isSelected = chip.background.constantState ==
                        resources.getDrawable(R.drawable.chip_selected_bg).constantState

                if (isSelected) {
                    chip.setBackgroundResource(R.drawable.chip_unselected_bg)
                    onSelectionChanged(null)
                } else {
                    chip.setBackgroundResource(R.drawable.chip_selected_bg)
                    onSelectionChanged(item)
                }
            }

            container.addView(chip)
        }
    }

    private fun resetFilters() {
        selectedDifficulty = null
        selectedTags.clear()

        // Reset all chip groups
        view?.findViewById<ViewGroup>(R.id.difficultyGroup)?.let {
            setupChipGroup(it, listOf("Beginner", "Intermediate", "Advanced"), null) {}
        }
        view?.findViewById<ViewGroup>(R.id.tagsGroup)?.let {
            setupChipGroup(it, listOf("AI/ML", "Web Dev", "Mobile Apps", "UI/UX", "Game Dev"), null, true) {}
        }
    }

    private fun applyFilters() {
        val filters = mutableMapOf<String, Any?>()

        if (selectedDifficulty != null) {
            filters["difficulty"] = selectedDifficulty
            Log.d("FilterDebug", "Selected difficulty: $selectedDifficulty")
        }

        if (selectedTags.isNotEmpty()) {
            filters["tags"] = selectedTags.toList()
            Log.d("FilterDebug", "Selected tags: $selectedTags")
        }

        (parentFragment as? HomeFragment)?.applyFilters(filters)
        dismiss()
    }

    companion object {
        const val TAG = "FilterBottomSheet"
    }
}