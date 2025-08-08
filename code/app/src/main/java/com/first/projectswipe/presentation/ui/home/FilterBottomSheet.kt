// File: com/first/projectswipe/home/FilterBottomSheet.kt

package com.first.projectswipe.presentation.ui.home

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import com.first.projectswipe.R
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FilterBottomSheet : BottomSheetDialogFragment() {

    private var selectedDifficulty: String? = null
    private val selectedTags = mutableSetOf<String>()

    private var filterListener: FilterListener? = null

    interface FilterListener {
        fun onFiltersSelected(filterMap: Map<String, Any?>)
    }

    fun setFilterListener(listener: FilterListener) {
        filterListener = listener
    }

    companion object {
        const val TAG = "FilterBottomSheet"

        private const val ARG_SELECTED_DIFFICULTY = "arg_selected_difficulty"
        private const val ARG_SELECTED_TAGS = "arg_selected_tags"

        /**
         * Create a new instance of FilterBottomSheet with the currently selected filters,
         * so the UI state can prepopulate chips correctly.
         */
        fun newInstance(
            selectedDifficulty: String?,
            selectedTags: Set<String>
        ): FilterBottomSheet {
            val fragment = FilterBottomSheet()
            val bundle = Bundle()
            bundle.putString(ARG_SELECTED_DIFFICULTY, selectedDifficulty)
            bundle.putStringArrayList(ARG_SELECTED_TAGS, ArrayList(selectedTags))
            fragment.arguments = bundle
            return fragment
        }

        /**
         * Helper to show the BottomSheet with saved filter parameters.
         */
        fun show(
            fragmentManager: FragmentManager,
            selectedDifficulty: String?,
            selectedTags: Set<String>,
            listener: FilterListener
        ) {
            val sheet = newInstance(selectedDifficulty, selectedTags)
            sheet.setFilterListener(listener)
            sheet.show(fragmentManager, TAG)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            selectedDifficulty = it.getString(ARG_SELECTED_DIFFICULTY)
            val tags = it.getStringArrayList(ARG_SELECTED_TAGS)
            selectedTags.clear()
            if (tags != null) selectedTags.addAll(tags)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.bottom_sheet_filter, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDifficultyChips(view)
        setupTagChips(view)

        view.findViewById<Button>(R.id.resetButton).setOnClickListener {
            selectedDifficulty = null
            selectedTags.clear()
            setupDifficultyChips(view)
            setupTagChips(view)
            filterListener?.onFiltersSelected(emptyMap())
        }

        view.findViewById<Button>(R.id.applyButton).setOnClickListener {
            val filterMap = mutableMapOf<String, Any?>()
            selectedDifficulty?.let { filterMap["difficulty"] = it }
            if (selectedTags.isNotEmpty()) filterMap["tags"] = selectedTags.toList()
            Log.d(TAG, "Apply filters: $filterMap")
            filterListener?.onFiltersSelected(filterMap)
            dismiss()
        }
    }

    /**
     * Setup single-select difficulty chips with current selection highlighted.
     */
    private fun setupDifficultyChips(root: View) {
        val container = root.findViewById<ViewGroup>(R.id.difficultyGroup)
        container.removeAllViews()
        listOf("Beginner", "Intermediate", "Advanced").forEach { label ->
            val chip = LayoutInflater.from(context).inflate(
                if (selectedDifficulty == label) R.layout.chip_interest_selected else R.layout.chip_interest_unselected,
                container,
                false
            )
            chip.findViewById<TextView>(R.id.chipText).text = label
            chip.setOnClickListener {
                selectedDifficulty = if (selectedDifficulty == label) null else label
                setupDifficultyChips(root)  // refresh to update UI
            }
            container.addView(chip)
        }
    }

    /**
     * Setup multi-select tag chips with current selections highlighted.
     */
    private fun setupTagChips(root: View) {
        val container = root.findViewById<FlexboxLayout>(R.id.tagsGroup)
        container.removeAllViews()
        val tagList = listOf("AI/ML", "Web Dev", "Mobile Apps", "UI/UX", "Game Dev")

        tagList.forEach { tag ->
            val isSelected = selectedTags.contains(tag)
            val chip = LayoutInflater.from(context).inflate(
                if (isSelected) R.layout.chip_interest_selected else R.layout.chip_interest_unselected,
                container,
                false
            )
            chip.findViewById<TextView>(R.id.chipText).text = tag
            chip.setOnClickListener {
                if (selectedTags.contains(tag)) selectedTags.remove(tag) else selectedTags.add(tag)
                setupTagChips(root)  // refresh to update UI
            }
            container.addView(chip)
        }
    }
}
