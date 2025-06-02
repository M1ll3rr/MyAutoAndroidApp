package com.example.myauto.unified

import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.myauto.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class BaseFragmentBinding(
    val root: ConstraintLayout,
    val backButton: ImageButton,
    val sortTypeButton: ImageButton,
    val sortOrderButton: ImageButton,
    val titleTextView: TextView,
    val recyclerView: RecyclerView,
    val addButton: FloatingActionButton,
    val graphButton: FloatingActionButton
) : ViewBinding {
    companion object {
        fun bind(root: View): BaseFragmentBinding {
            return BaseFragmentBinding(
                root = root as ConstraintLayout,
                backButton = root.findViewById(R.id.backButton),
                sortTypeButton = root.findViewById(R.id.sortTypeButton),
                sortOrderButton = root.findViewById(R.id.sortOrderButton),
                titleTextView = root.findViewById(R.id.titleTextView),
                recyclerView = root.findViewById(R.id.recyclerView),
                addButton = root.findViewById(R.id.addButton),
                graphButton = root.findViewById(R.id.graphButton)
            )
        }
    }

    override fun getRoot(): View {
        return root
    }
}