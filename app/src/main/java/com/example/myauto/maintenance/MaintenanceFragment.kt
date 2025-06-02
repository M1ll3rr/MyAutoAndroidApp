package com.example.myauto.maintenance

import android.Manifest
import android.content.Intent
import android.icu.util.Calendar
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.myauto.R
import com.example.myauto.activity.MainActivity
import com.example.myauto.data.SortType
import com.example.myauto.databinding.BottomSheetMaintenanceBinding
import com.example.myauto.room.entity.MaintenanceItemEntity
import com.example.myauto.unified.BaseBottomSheetFragment
import com.example.myauto.unified.UnifiedViewModelFactory
import com.example.myauto.utils.FormatterHelper
import com.example.myauto.utils.PermissionHelper
import com.example.myauto.utils.ValidationHelper
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.Date

class MaintenanceFragment : BaseBottomSheetFragment<MaintenanceItemEntity, BottomSheetMaintenanceBinding>() {
    override val viewModel: MaintenanceViewModel by viewModels {
        UnifiedViewModelFactory(
            (requireActivity() as MainActivity).getRepository()
        )
    }

    override val titleResId = R.string.title_maintenance

    private var notifyCalendar = Calendar.getInstance()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            bottomSheetBinding.notifyChip.isChecked = true
        } else {
            if (!PermissionHelper.requestNotificationPermission(this)) {
                showPermissionSettingsDialog()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        bottomSheetBinding = BottomSheetMaintenanceBinding.inflate(layoutInflater)
        super.onViewCreated(view, savedInstanceState)
        setupDropdowns()
    }

    override fun setupBottomSheetListeners() {
        super.setupBottomSheetListeners()
        bottomSheetBinding.apply {
            dateTimeNotifyTV.setOnClickListener {
                showDatePickerDialog(notifyCalendar, true)
            }

            notifyChip.setOnClickListener {
                if (bottomSheetBinding.notifyChip.isChecked) {
                    if (PermissionHelper.hasNotificationPermission(requireContext())) {
                        bottomSheetBinding.notifyChip.isChecked = true
                    } else {
                        bottomSheetBinding.notifyChip.isChecked = false
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }
                }
            }

            notifyChip.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    notifySwitch.visibility = View.VISIBLE
                    mileageNotifyETContainer.visibility = View.VISIBLE
                    notifySwitch.isChecked = false
                } else {
                    notifySwitch.visibility = View.GONE
                    mileageNotifyETContainer.visibility = View.GONE
                    dateTimeNotifyTV.visibility = View.GONE
                }
            }

            notifySwitch.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    notifySwitch.setText(R.string.date)
                    mileageNotifyET.clearFocus()
                    mileageNotifyETContainer.visibility = View.GONE
                    dateTimeNotifyTV.visibility = View.VISIBLE
                } else {
                    notifySwitch.setText(R.string.mileage)
                    mileageNotifyETContainer.visibility = View.VISIBLE
                    dateTimeNotifyTV.visibility = View.GONE
                }
            }
        }
    }


    override fun saveItemFromForm() {
        bottomSheetBinding.apply {
            val isUpdateMileage = mileageSwitch.isChecked
            val isAddNotify = notifyChip.isChecked
            val isNotifyDate = notifySwitch.isChecked

            val newMileage = mileageET.text.toString().toIntOrNull()
            val id = saveButton.tag as? Int
            val category = categorySelector.text.toString().trim()
            val subcategory = subCategorySelector.text.toString().trim()
            val title = titleEditText.text.toString().trim()
            val brand = brandEditText.text.toString().trim()
            val description = descriptionEditText.text.toString().trim()
            val itemCost = itemCostEditText.text.toString().toFloatOrNull() ?: 0f
            val workCost = workCostEditText.text.toString().toFloatOrNull() ?: 0f
            val totalCost = itemCost + workCost

            val notificationMileage = if (isAddNotify && !isNotifyDate)
                mileageNotifyET.text.toString().toIntOrNull()
            else null

            val notificationDate = if (isAddNotify && isNotifyDate)
                notifyCalendar.time
            else null

            if (!ValidationHelper.validateCategory(category, requireContext())) return
            if (!ValidationHelper.validateTitleBrand(title, brand, requireContext())) return
            if (!ValidationHelper.validateDateNotFuture(calendar.time, requireContext())) return
            if (!ValidationHelper.validateMileageUpdate(isUpdateMileage, newMileage,
                    viewModel.getCurrentMileage(), requireContext())) return

            if (isAddNotify) {
                if (isNotifyDate) {
                    if (!ValidationHelper.validateNotificationDate(
                            notifyCalendar.time,
                            requireContext()
                        )) return
                } else {
                    if (!ValidationHelper.validateNotificationMileage(
                            notificationMileage,
                            viewModel.getCurrentMileage(),
                            requireContext()
                        )) return
                }
            }

            val item = MaintenanceItemEntity(
                id = id ?: 0,
                carId = carId,
                date = calendar.time,
                mileage = newMileage ?: viewModel.getCurrentMileage(),
                category = category,
                subcategory = subcategory.ifEmpty { null },
                title = title.ifEmpty { null },
                brand = brand.ifEmpty { null },
                description = description.ifEmpty { null },
                itemCost = itemCost,
                workCost = workCost,
                totalCost = totalCost,
                notificationMileage = notificationMileage,
                notificationDate = notificationDate
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
            mileageNotifyET.text?.clear()
            dateTimeTextView.text = FormatterHelper.formatDateTime(Date())
            dateTimeNotifyTV.text = FormatterHelper.formatDateTime(Date())

            subCategorySelectorContainer.visibility = View.GONE
            notifyTV.visibility = View.GONE

            clearFocusAll(true)

            saveButton.tag = null
        }
        bottomSheetDialog.show()
    }



    override fun showBottomSheetForEdit(item: MaintenanceItemEntity) {
        val currentItems = viewModel.items.value
        val selectedItem = (currentItems.firstOrNull { it.id == item.id } ?: item) as MaintenanceItemEntity
        calendar.time = selectedItem.date
        updateSubcategoriesDropdown(resources.getStringArray(R.array.consumables_categories).indexOf(selectedItem.category))

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

            when {
                selectedItem.notificationDate != null -> {
                    notifyTV.text = buildString {
                        append(getString(R.string.notify_replacement))
                        append(": ")
                        append(FormatterHelper.formatDateTimeShort(selectedItem.notificationDate))
                    }
                    notifyTV.visibility = View.VISIBLE
                    dateTimeNotifyTV.text = FormatterHelper.formatDateTimeShort(selectedItem.notificationDate)
                }
                selectedItem.notificationMileage != null -> {
                    notifyTV.text = buildString {
                        append(getString(R.string.notify_replacement))
                        append(": ")
                        append(selectedItem.notificationMileage)
                        append(" ")
                        append(getString(R.string.km))
                    }
                    notifyTV.visibility = View.VISIBLE
                    mileageNotifyET.setText(selectedItem.notificationMileage.toString())
                }
                else -> notifyTV.visibility = View.GONE
            }

            clearFocusAll(false)

            saveButton.tag = selectedItem.id
        }
        bottomSheetDialog.show()
    }

    private fun setupDropdowns() {
        val categoryEntries = resources.getStringArray(R.array.consumables_categories)
        val categorySelector = bottomSheetBinding.categorySelector
        val categoryAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            categoryEntries
        )
        categorySelector.setAdapter(categoryAdapter)

        categorySelector.setOnItemClickListener { _, _, position, _ ->
            updateSubcategoriesDropdown(position)
        }
    }

    private fun updateSubcategoriesDropdown(categoryPosition: Int) {
        val subcategoriesIds = resources.obtainTypedArray(R.array.consumables_subcategories_ids)
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
            mileageNotifyET.clearFocus()

            mileageSwitch.isEnabled = toggle
            mileageET.isEnabled = toggle
            dateTimeTextView.isClickable = toggle
            notifyChip.isChecked = false
        }
    }


    override fun onDateSelected(formattedDate: String, isMainDate: Boolean) {
        if (isMainDate)
            bottomSheetBinding.dateTimeTextView.text = formattedDate
        else
            bottomSheetBinding.dateTimeNotifyTV.text = formattedDate
    }

    override fun navigateToGraph() {
        findNavController().navigate(MaintenanceFragmentDirections.actionMaintenanceToMaintenanceStat())
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

    private fun showPermissionSettingsDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.permission_required_title)
            .setMessage(R.string.permission_settings_message)
            .setPositiveButton(R.string.settings) { _, _ ->
                openAppSettings()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", requireContext().packageName, null)
        }
        startActivity(intent)
    }
}