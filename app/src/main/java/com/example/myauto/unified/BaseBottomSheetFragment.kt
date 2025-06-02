package com.example.myauto.unified

import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.myauto.R
import com.example.myauto.data.SortOrder
import com.example.myauto.room.entity.UnifiedItemEntity
import com.example.myauto.utils.DatePickerHelper
import com.example.myauto.utils.DialogHelper
import com.example.myauto.utils.FormatterHelper
import com.example.myauto.utils.ValidationHelper
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.transition.MaterialSharedAxis
import dev.androidbroadcast.vbpd.viewBinding
import kotlinx.coroutines.launch
import java.util.Date

abstract class BaseBottomSheetFragment<
        ItemType : UnifiedItemEntity,
        BSB : ViewBinding>()
    : Fragment() {
    protected val binding: BaseFragmentBinding by viewBinding(BaseFragmentBinding::bind)
    protected abstract val viewModel: BaseViewModel<ItemType>
    protected abstract val titleResId: Int
    protected lateinit var adapter: UnifiedItemAdapter
    protected lateinit var bottomSheetDialog: BottomSheetDialog
    protected lateinit var bottomSheetBinding: BSB
    protected val calendar = Calendar.getInstance()
    protected var scrollToNewItem = false
    protected var scrollToStart = false
    protected val carId by lazy { viewModel.currentCarId }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, false)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Y, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_base, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.titleTextView.setText(titleResId)
        bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(bottomSheetBinding.root)

        setupAdapter()
        setupRecyclerView()
        setupButtons()
        setupBottomSheetListeners()
    }

    protected open fun setupAdapter() {
        adapter = UnifiedItemAdapter(
            onClick = { item -> showBottomSheetForEdit(item as ItemType) },
            onLongClick = { itemId -> DialogHelper.showDeleteDialog(requireContext()) {
                viewModel.deleteItem(itemId) }
            }
        )
    }

    protected open fun setupRecyclerView() {
        binding.recyclerView.apply {
            adapter = this@BaseBottomSheetFragment.adapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.items.collect { items ->
                    adapter.submitList(items) {
                        when {
                            scrollToNewItem -> scrollToNewItem(items)
                            scrollToStart -> binding.recyclerView.scrollToPosition(0).also { scrollToStart = false }
                        }
                    }
                }
            }
        }
    }

    protected open fun scrollToNewItem(items: List<UnifiedItemEntity>) {
        items.maxByOrNull { it.id }?.let { item ->
            val position = items.indexOf(item)
            if (position != -1) binding.recyclerView.scrollToPosition(position)
        }
        scrollToNewItem = false
    }

    protected open fun setupButtons() {
        with(binding) {
            backButton.setOnClickListener { findNavController().popBackStack() }
            addButton.setOnClickListener { showBottomSheet() }
            graphButton.setOnClickListener { navigateToGraph() }
            sortOrderButton.setOnClickListener {
                scrollToStart = true
                viewModel.toggleSortOrder()
                updateSortOrderIcon()
            }
            sortTypeButton.setOnClickListener { showSortTypeMenu(it) }
        }
    }

    protected open fun setupBottomSheetListeners() {
        bottomSheetBinding.root.apply {
            findViewById<Button>(R.id.saveButton).setOnClickListener {
                saveItemFromForm()
            }
            findViewById<Button>(R.id.cancelButton).setOnClickListener {
                bottomSheetDialog.dismiss()
            }
            findViewById<TextView>(R.id.dateTimeTextView).setOnClickListener {
                showDatePickerDialog(calendar, false)
            }
        }
    }


    protected fun updateSortOrderIcon() {
        binding.sortOrderButton.setImageResource(
            if (viewModel.sortOrder.value == SortOrder.DESC)
                R.drawable.ic_arrow_down
            else
                R.drawable.ic_arrow_up
        )
    }

    protected fun showDatePickerDialog(targetCalendar: Calendar, isFutureMode: Boolean = false) {
        DatePickerHelper.showDateTimePicker(requireContext(), targetCalendar, isFutureMode) {
            val formattedDate = FormatterHelper.formatDateTime(it.time)
            onDateSelected(formattedDate, targetCalendar == calendar)
        }
    }

    protected fun validateCommonFields(
        isUpdateMileage: Boolean,
        newMileage: Int?,
        date: Date
    ): Boolean {
        if (!ValidationHelper.validateDateNotFuture(date, requireContext())) return false
        if (!ValidationHelper.validateMileageUpdate(
                isUpdateMileage,
                newMileage,
                viewModel.getCurrentMileage(),
                requireContext()
            )
        ) return false

        return true
    }

    protected fun saveItem(item: ItemType) {
        if (item.id == 0) {
            viewModel.addItem(item)
            scrollToNewItem = true
        } else {
            viewModel.updateItem(item)
        }

        if (bottomSheetBinding.root.findViewById<MaterialSwitch>(R.id.mileageSwitch).isChecked) {
            item.mileage?.let {
                viewModel.updateCarMileage(it)
            }
        }
    }

    protected abstract fun saveItemFromForm()
    protected abstract fun clearFocusAll(toggle: Boolean)
    protected abstract fun onDateSelected(formattedDate: String, isMainDate: Boolean)
    protected abstract fun navigateToGraph()
    protected abstract fun showSortTypeMenu(anchor: View)
    protected abstract fun showBottomSheet()
    protected abstract fun showBottomSheetForEdit(item: ItemType)
}