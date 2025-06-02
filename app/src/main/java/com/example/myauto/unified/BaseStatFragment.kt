package com.example.myauto.unified

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import com.example.myauto.R
import com.example.myauto.utils.FormatterHelper
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.transition.MaterialSharedAxis
import java.util.Calendar
import java.util.Date

abstract class BaseStatFragment<Daily, Summary> : Fragment() {
    protected abstract val viewModel: BaseStatViewModel<Daily, Summary>
    protected abstract val layoutResId: Int
    protected val ONE_DAY_MS = 86400000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Y, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(layoutResId, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        view.findViewById<View>(R.id.dateRangeButton).setOnClickListener {
            showDatePicker()
        }
        view.findViewById<View>(R.id.backButton).setOnClickListener {
            findNavController().popBackStack()
        }
        val lastMonthPeriod = getLastMonthPeriod()
        setPeriodTitle(lastMonthPeriod)
        viewModel.setPeriod(lastMonthPeriod)
    }

    protected fun showDatePicker() {
        val datePicker = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText(getString(R.string.choose_period))
            .setSelection(getLastMonthPeriod())
            .build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            val adjustedSelection = Pair(selection.first, selection.second + ONE_DAY_MS)
            viewModel.setPeriod(adjustedSelection)
            setPeriodTitle(adjustedSelection)
        }
        datePicker.show(parentFragmentManager, "DATE_PICKER")
    }

    protected fun getLastMonthPeriod(): Pair<Long, Long> {
        Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            val today = timeInMillis
            add(Calendar.MONTH, -1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            return Pair(timeInMillis, today)
        }
    }

    protected fun setPeriodTitle(selection: Pair<Long, Long>) {
        val startDate = FormatterHelper.formatDate(Date(selection.first))
        val endDate = FormatterHelper.formatDate(Date(selection.second))
        view?.findViewById<TextView>(R.id.periodTitle)?.text = buildString {
            append(getString(R.string.selected_period))
            append(": ")
            append(startDate)
            append(" - ")
            append(endDate)
        }
    }

    protected fun <T> observeData(
        liveData: LiveData<T>,
        onData: (T) -> Unit
    ) {
        liveData.observe(viewLifecycleOwner) { data ->
            data?.let(onData)
        }
    }

    protected abstract fun setupObservers()
}