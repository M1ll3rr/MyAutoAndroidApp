package com.example.myauto.car_selection.base

import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.myauto.R

class BaseSelectorFragmentBinding(
    val root: ConstraintLayout,
    val backButton: ImageButton,
    val titleTextView: TextView,
    val recyclerView: RecyclerView,
    val progressBar: ProgressBar
) : ViewBinding {
    companion object {
        fun bind(root: View): BaseSelectorFragmentBinding {
            return BaseSelectorFragmentBinding(
                root = root as ConstraintLayout,
                backButton = root.findViewById(R.id.backButton),
                titleTextView = root.findViewById(R.id.titleTextView),
                recyclerView = root.findViewById(R.id.recyclerView),
                progressBar = root.findViewById(R.id.progressBar)
            )
        }
    }

    override fun getRoot(): View = root
}