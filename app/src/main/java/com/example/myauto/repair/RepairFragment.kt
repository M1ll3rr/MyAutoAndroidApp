package com.example.myauto.repair

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.myauto.R
import com.example.myauto.activity.MainActivity
import com.example.myauto.data.SortType
import com.example.myauto.databinding.BottomSheetRepairBinding
import com.example.myauto.room.entity.RepairItemEntity
import com.example.myauto.unified.BaseBottomSheetFragment
import com.example.myauto.unified.UnifiedViewModelFactory
import com.example.myauto.utils.DatePickerHelper
import com.example.myauto.utils.FormatterHelper
import com.example.myauto.utils.ValidationHelper
import java.util.Date

class RepairFragment : BaseBottomSheetFragment<RepairItemEntity, BottomSheetRepairBinding>() {
    override val viewModel: RepairViewModel by viewModels {
        UnifiedViewModelFactory(
            (requireActivity() as MainActivity).getRepository()
        )
    }

    override val titleResId = R.string.title_repair

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        bottomSheetBinding = BottomSheetRepairBinding.inflate(layoutInflater)
        super.onViewCreated(view, savedInstanceState)
        setupDropdowns()
    }


    override fun saveItemFromForm() {
        bottomSheetBinding.apply {
            val isUpdateMileage = mileageSwitch.isChecked

            val newMileage = mileageET.text.toString().toIntOrNull()
            val id = saveButton.tag as? Int
            val isRepair = buttonGroup.checkedButtonId == repairButton.id
            val category = categorySelector.text.toString().trim()
            val subcategory = subCategorySelector.text.toString().trim()
            val title = titleEditText.text.toString().trim()
            val brand = brandEditText.text.toString().trim()
            val description = descriptionEditText.text.toString().trim()
            val itemCost = itemCostEditText.text.toString().toFloatOrNull() ?: 0f
            val workCost = workCostEditText.text.toString().toFloatOrNull() ?: 0f
            val totalCost = itemCost + workCost

            if (!ValidationHelper.validateCategory(category, requireContext())) return
            if (!ValidationHelper.validateTitleBrand(title, brand, requireContext())) return
            if (!ValidationHelper.validateDateNotFuture(calendar.time, requireContext())) return
            if (!ValidationHelper.validateMileageUpdate(isUpdateMileage, newMileage,
                    viewModel.getCurrentMileage(), requireContext())) return

            val item = RepairItemEntity(
                id = id ?: 0,
                carId = carId,
                date = calendar.time,
                mileage = newMileage!!,
                isRepair = isRepair,
                category = category,
                subcategory = subcategory.ifEmpty { null },
                title = title.ifEmpty { null },
                brand = brand.ifEmpty { null },
                description = description.ifEmpty { null },
                itemCost = itemCost,
                workCost = workCost,
                totalCost = totalCost
            )

            saveItem(item)
            bottomSheetDialog.dismiss()
        }
    }


    override fun showBottomSheet() {
        calendar.time = Date()

        bottomSheetBinding.apply {
            mileageET.setText(viewModel.getCurrentMileage().toString())
            categorySelector.text.clear()
            subCategorySelector.text.clear()
            titleEditText.text?.clear()
            brandEditText.text?.clear()
            descriptionEditText.text?.clear()
            itemCostEditText.text?.clear()
            workCostEditText.text?.clear()
            dateTimeTextView.text = FormatterHelper.formatDateTime(Date())

            subCategorySelectorContainer.visibility = View.GONE
            clearFocusAll(true)
            buttonGroup.check(repairButton.id)

            saveButton.tag = null
        }
        bottomSheetDialog.show()
    }

    override fun showBottomSheetForEdit(item: RepairItemEntity) {
        val currentItems = viewModel.items.value
        val selectedItem = (currentItems.firstOrNull { it.id == item.id } ?: item) as RepairItemEntity
        calendar.time = selectedItem.date
        updateSubcategoriesDropdown(resources.getStringArray(R.array.parts_categories).indexOf(selectedItem.category))

        bottomSheetBinding.apply {
            mileageET.setText(selectedItem.mileage.toString())
            categorySelector.setText(selectedItem.category)
            subCategorySelector.setText(selectedItem.subcategory)
            titleEditText.setText(selectedItem.title)
            brandEditText.setText(selectedItem.brand)
            descriptionEditText.setText(selectedItem.description)
            itemCostEditText.setText(FormatterHelper.formatCurrency(selectedItem.itemCost))
            workCostEditText.setText(FormatterHelper.formatCurrency(selectedItem.workCost))
            dateTimeTextView.text = FormatterHelper.formatDateTime(selectedItem.date)

            clearFocusAll(false)

            if (selectedItem.isRepair) {
                buttonGroup.check(repairButton.id)
            } else {
                buttonGroup.check(replacementButton.id)
            }

            saveButton.tag = selectedItem.id
        }
        bottomSheetDialog.show()
    }

    private fun setupDropdowns() {
        val categoryEntries = resources.getStringArray(R.array.parts_categories)
        val categorySelector = bottomSheetBinding.categorySelector
        val categoryAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            categoryEntries
        )
        categorySelector.setAdapter(categoryAdapter)

        categorySelector.setOnItemClickListener{ _, _, position, _ ->
            updateSubcategoriesDropdown(position)
        }
    }

    private fun updateSubcategoriesDropdown(categoryPosition: Int) {
        val subcategoriesIds = resources.obtainTypedArray(R.array.parts_subcategories_ids)
        try {
            val subcategoryArrayId = subcategoriesIds.getResourceId(categoryPosition, -1)
            val hasSubcategories = subcategoryArrayId != -1

            bottomSheetBinding.apply {
                if (hasSubcategories) {
                    subCategorySelectorContainer.visibility = View.VISIBLE
                    subCategorySelector.text.clear()
                    val subcategories = resources.getStringArray(subcategoryArrayId)
                    subCategorySelector.setAdapter(ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_spinner_dropdown_item,
                        subcategories
                    ))
                } else {
                    subCategorySelectorContainer.visibility = View.GONE
                }
            }
        } finally {
            subcategoriesIds.recycle()
        }
    }

    override fun clearFocusAll(toggle: Boolean) {
        with(bottomSheetBinding) {
            categorySelector.clearFocus()
            subCategorySelector.clearFocus()
            titleEditText.clearFocus()
            brandEditText.clearFocus()
            descriptionEditText.clearFocus()
            itemCostEditText.clearFocus()
            workCostEditText.clearFocus()

            mileageSwitch.isEnabled = toggle
            mileageET.isEnabled = toggle
            dateTimeTextView.isClickable = toggle
            repairButton.isClickable = toggle
            replacementButton.isClickable = toggle
        }
    }

    override fun onDateSelected(formattedDate: String, isMainDate: Boolean) {
        bottomSheetBinding.dateTimeTextView.text = formattedDate
    }

    override fun navigateToGraph() {
        findNavController().navigate(RepairFragmentDirections.actionRepairToRepairStat())
    }

    override fun showSortTypeMenu(anchor: View) {
        PopupMenu(requireContext(), anchor).apply {
            menuInflater.inflate(R.menu.menu_sort_type, menu)
            setOnMenuItemClickListener { item ->
                scrollToStart = true
                when (item.itemId) {
                    R.id.menu_sort_date -> viewModel.setSortType(SortType.DATE)
                    R.id.menu_sort_cost -> viewModel.setSortType(SortType.COST)
                    R.id.menu_sort_category -> viewModel.setSortType(SortType.CATEGORY)
                    else -> return@setOnMenuItemClickListener false
                }
                true
            }
            show()
        }
    }

    private fun showDatePickerDialog() {
        DatePickerHelper.showDateTimePicker(requireContext(), calendar
        ) { updatedCalendar ->
            val formattedDate = FormatterHelper.formatDateTime(updatedCalendar.time)
            bottomSheetBinding.dateTimeTextView.text = formattedDate
        }
    }
}