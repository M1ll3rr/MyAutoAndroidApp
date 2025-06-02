package com.example.myauto.fuel

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.myauto.R
import com.example.myauto.activity.MainActivity
import com.example.myauto.data.PreferencesManager
import com.example.myauto.data.SortType
import com.example.myauto.databinding.BottomSheetFuelBinding
import com.example.myauto.room.entity.FuelItemEntity
import com.example.myauto.unified.BaseBottomSheetFragment
import com.example.myauto.unified.UnifiedViewModelFactory
import com.example.myauto.utils.FormatterHelper
import com.google.android.material.textfield.TextInputEditText
import java.util.Date

class FuelFragment : BaseBottomSheetFragment<FuelItemEntity, BottomSheetFuelBinding>() {
    override val viewModel: FuelViewModel by viewModels {
        UnifiedViewModelFactory(
            (requireActivity() as MainActivity).getRepository()
        )
    }

    override val titleResId = R.string.title_fuel

    private var isCalculating = false
    private enum class Field { VOLUME, PRICE, COST }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        bottomSheetBinding = BottomSheetFuelBinding.inflate(layoutInflater)
        super.onViewCreated(view, savedInstanceState)
        setupSpinner()
    }

    override fun navigateToGraph() {
        findNavController().navigate(FuelFragmentDirections.actionFuelToFuelStat())
    }

    override fun showSortTypeMenu(anchor: View) {
        PopupMenu(requireContext(), anchor).apply {
            menuInflater.inflate(R.menu.menu_fuel_sort_type, menu)
            setOnMenuItemClickListener { item ->
                scrollToStart = true
                when (item.itemId) {
                    R.id.fuel_menu_sort_date -> viewModel.setSortType(SortType.DATE)
                    R.id.fuel_menu_sort_cost -> viewModel.setSortType(SortType.COST)
                    R.id.fuel_menu_sort_volume -> viewModel.setSortType(SortType.VOLUME)
                    else -> return@setOnMenuItemClickListener false
                }
                true
            }
            show()
        }
    }

    override fun showBottomSheet() {
        calendar.time = Date()
        bottomSheetBinding.apply {
            mileageET.setText(viewModel.getCurrentMileage().toString())
            volumeEditText.text?.clear()
            costEditText.text?.clear()
            fuelPriceEditText.text?.clear()
            discountEditText.text?.clear()
            dateTimeTextView.text = FormatterHelper.formatDateTime(Date())

            clearFocusAll(true)
            saveButton.tag = null
        }
        bottomSheetDialog.show()
    }

    override fun showBottomSheetForEdit(item: FuelItemEntity) {
        calendar.time = item.date

        bottomSheetBinding.apply {
            dateTimeTextView.text = FormatterHelper.formatDateTime(item.date)
            mileageET.setText(item.mileage?.toString() ?: "")
            volumeEditText.setText(FormatterHelper.formatCurrency(item.volume))
            costEditText.setText(FormatterHelper.formatCurrency(item.totalCost))
            fuelPriceEditText.setText(FormatterHelper.formatCurrency(item.fuelPrice))
            discountEditText.setText(FormatterHelper.formatCurrency(item.discount))
            val fuelTypes = resources.getStringArray(R.array.fuel_types)
            val position = fuelTypes.indexOfFirst { it == item.fuelType }
            fuelTypeSpinner.setSelection(if (position != -1) position else 0)

            clearFocusAll(false)

            saveButton.tag = item.id
        }
        bottomSheetDialog.show()
    }

    override fun setupBottomSheetListeners() {
        super.setupBottomSheetListeners()
        bottomSheetBinding.apply {
            volumeEditText.addTextChangedListener(
                createTextWatcher(volumeEditText) { calculateValues(Field.VOLUME) })
            fuelPriceEditText.addTextChangedListener(
                createTextWatcher(fuelPriceEditText) { calculateValues(Field.PRICE) })
            costEditText.addTextChangedListener(
                createTextWatcher(costEditText) { calculateValues(Field.COST) })
        }
    }

    override fun saveItemFromForm() {
        bottomSheetBinding.apply {
            val id = saveButton.tag as? Int
            val isUpdateMileage = mileageSwitch.isChecked
            val newMileage = mileageET.text.toString().toIntOrNull()
            val volume = volumeEditText.text.toString().toFloatOrNull() ?: 0f
            val cost = costEditText.text.toString().toFloatOrNull() ?: 0f
            val price = fuelPriceEditText.text.toString().toFloatOrNull() ?: 0f
            val discount = discountEditText.text.toString().toFloatOrNull() ?: 0f

            if (volume <= 0 || cost <= 0 || price <= 0) return
            if (!validateCommonFields(isUpdateMileage, newMileage, calendar.time)) return

            val item = FuelItemEntity(
                carId = carId,
                id = id ?: 0,
                date = calendar.time,
                mileage = newMileage,
                fuelType = fuelTypeSpinner.selectedItem.toString(),
                volume = volume,
                totalCost = cost,
                fuelPrice = price,
                discount = discount
            )

            saveItem(item)
            bottomSheetDialog.dismiss()
        }
    }

    override fun clearFocusAll(toggle: Boolean) {
        with(bottomSheetBinding) {
            mileageET.clearFocus()
            volumeEditText.clearFocus()
            costEditText.clearFocus()
            fuelPriceEditText.clearFocus()
            discountEditText.clearFocus()

            mileageSwitch.isEnabled = toggle
            mileageSwitch.isChecked = toggle
            mileageET.isEnabled = toggle
            dateTimeTextView.isClickable = toggle
        }
    }

    override fun onDateSelected(formattedDate: String, isMainDate: Boolean) {
        bottomSheetBinding.dateTimeTextView.text = formattedDate
    }


    private fun setupSpinner() {
        val fuelSpinner = bottomSheetBinding.fuelTypeSpinner
        val fuelTypes = resources.getStringArray(R.array.fuel_types)
        val fuelTypesAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, fuelTypes)
        fuelTypesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        fuelSpinner.adapter = fuelTypesAdapter

        val savedFuelTypePosition = PreferencesManager.getFuelType()
        if (savedFuelTypePosition != -1) {
            fuelSpinner.setSelection(savedFuelTypePosition)
        }

        fuelSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, view: View?, position: Int, id: Long) {
                PreferencesManager.setFuelType(position)
            }
            override fun onNothingSelected(parentView: AdapterView<*>?) {}
        }
    }

    private fun calculateValues(changedField: Field) {
        if (isCalculating) return
        isCalculating = true

        bottomSheetBinding.apply {
            val volume = volumeEditText.text.toString().toFloatOrNull() ?: 0f
            val price = fuelPriceEditText.text.toString().toFloatOrNull() ?: 0f
            val cost = costEditText.text.toString().toFloatOrNull() ?: 0f

            when (changedField) {
                Field.VOLUME -> {
                    if (price > 0) {
                        val calculatedCost = volume * price
                        setValueSilently(costEditText, FormatterHelper.formatCurrency(calculatedCost))
                    }
                }
                Field.PRICE -> {
                    if (volume > 0) {
                        val calculatedCost = volume * price
                        setValueSilently(costEditText, FormatterHelper.formatCurrency(calculatedCost))
                    }
                }
                Field.COST -> {
                    when {
                        volume > 0 -> {
                            val calculatedPrice = cost / volume
                            setValueSilently(fuelPriceEditText, FormatterHelper.formatCurrency(calculatedPrice))
                        }
                        price > 0 -> {
                            val calculatedVolume = cost / price
                            setValueSilently(volumeEditText, FormatterHelper.formatCurrency(calculatedVolume))
                        }
                    }
                }
            }
        }
        isCalculating = false
    }

    private fun setValueSilently(editText: TextInputEditText, value: String) {
        editText.removeTextChangedListener(editText.tag as? TextWatcher)
        editText.setText(value)
        editText.setSelection(value.length)
        editText.tag?.let { editText.addTextChangedListener(it as TextWatcher) }
    }

    private fun createTextWatcher(
        editText: TextInputEditText,
        action: () -> Unit
    ): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) {}
            override fun onTextChanged(s: CharSequence?, st: Int, b: Int, c: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (!isCalculating && editText.isFocused) action()
            }
        }.also { editText.tag = it }
    }
}