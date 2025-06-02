package com.example.myauto.car_selection.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myauto.R
import com.example.myauto.car_selection.rcview.SelectorAdapter
import com.example.myauto.room.entity.CarInfoEntity
import com.google.android.material.transition.MaterialSharedAxis
import dev.androidbroadcast.vbpd.viewBinding

abstract class BaseSelectorFragment : Fragment() {
    protected val binding: BaseSelectorFragmentBinding by viewBinding(BaseSelectorFragmentBinding.Companion::bind)
    protected abstract val titleResId: Int
    protected lateinit var selectorAdapter: SelectorAdapter
    private var scrollPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupTransitions()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_base_selector, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.titleTextView.setText(titleResId)
        initRecyclerView()
        initBackButton()
        initUI()
    }

    override fun onPause() {
        super.onPause()
        val layoutManager = binding.recyclerView.layoutManager as LinearLayoutManager
        scrollPosition = layoutManager.findFirstVisibleItemPosition()
    }

    protected open fun setupTransitions() {
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true).apply {
            excludeTarget(R.id.backButton, true)
        }
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false).apply {
            excludeTarget(R.id.backButton, true)
        }
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false).apply {
            excludeTarget(R.id.backButton, true)
        }
    }

    protected open fun initRecyclerView() {
        selectorAdapter = SelectorAdapter { selectedItem ->
            onItemClick(selectedItem)
        }
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = selectorAdapter
            scrollToPosition(scrollPosition)
        }
    }

    protected open fun initBackButton() {
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    protected abstract fun onItemClick(item: CarInfoEntity)
    protected abstract fun initUI()
}