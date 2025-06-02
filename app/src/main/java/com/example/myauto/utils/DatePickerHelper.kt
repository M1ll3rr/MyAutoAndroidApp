package com.example.myauto.utils

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.icu.util.Calendar
import android.widget.DatePicker
import android.widget.TimePicker

object DatePickerHelper {
    fun showDateTimePicker(
        context: Context,
        calendar: Calendar,
        isFutureMode: Boolean = false,
        onDateTimeSet: (Calendar) -> Unit
    ) {
        val minDate = if (isFutureMode) {
            Calendar.getInstance().apply {
                add(Calendar.DAY_OF_MONTH, 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
        } else {
            Calendar.getInstance().apply {
                add(Calendar.YEAR, -1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
        }
        val maxDate = if (isFutureMode) null else Calendar.getInstance().timeInMillis

        showDatePicker(context, calendar, minDate, maxDate) { updatedCalendar ->
            showTimePicker(context, updatedCalendar) { finalCalendar ->
                onDateTimeSet(finalCalendar)
            }
        }
    }

    private fun showDatePicker(
        context: Context,
        calendar: Calendar,
        minDate: Long?,
        maxDate: Long?,
        onDateSet: (Calendar) -> Unit
    ) {
        val datePickerDialog = DatePickerDialog(
            context,
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                calendar.set(year, month, dayOfMonth)
                onDateSet(calendar)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        minDate?.let { datePickerDialog.datePicker.minDate = it }
        maxDate?.let { datePickerDialog.datePicker.maxDate = it }
        datePickerDialog.show()
    }

    private fun showTimePicker(
        context: Context,
        calendar: Calendar,
        onTimeSet: (Calendar) -> Unit
    ) {
        TimePickerDialog(
            context,
            { _: TimePicker, hourOfDay: Int, minute: Int ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                onTimeSet(calendar)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }
}