package com.example.myauto.utils

import android.content.Context
import android.widget.Toast
import com.example.myauto.R
import java.util.Date

object ValidationHelper {
    fun validateMileageUpdate(
        isUpdateMileage: Boolean,
        newMileage: Int?,
        currentMileage: Int,
        context: Context
    ): Boolean {
        return when {
            isUpdateMileage -> {
                if (newMileage == null) {
                    showToast(context, R.string.toast_choose_mileage)
                    false
                }
                else if (newMileage < currentMileage ) {
                    showToast(context, R.string.toast_error_mileage_lower)
                    false
                }
                else true
            }
            else -> true
        }
    }

    fun validateCategory(category: String, context: Context): Boolean {
        if (category.isEmpty()) {
            showToast(context, R.string.toast_choose_category)
            return false
        }
        return true
    }

    fun validateTitleBrand(title: String?, brand: String?, context: Context): Boolean {
        if (title.isNullOrEmpty() && brand.isNullOrEmpty()) {
            showToast(context, R.string.toast_enter_title_brand)
            return false
        }
        return true
    }

    fun validateDateNotFuture(date: Date, context: Context): Boolean {
        if (date > Date()) {
            showToast(context, R.string.toast_error_time_future)
            return false
        }
        return true
    }

    fun validateNotificationDate(date: Date, context: Context): Boolean {
        if (date < Date()) {
            showToast(context, R.string.toast_error_time_past)
            return false
        }
        return true
    }

    fun validateNotificationMileage(
        notificationMileage: Int?,
        currentMileage: Int,
        context: Context
    ): Boolean {
        if (notificationMileage != null && notificationMileage < currentMileage) {
            showToast(context, R.string.toast_error_mileage_lower)
            return false
        }
        return true
    }

    private fun showToast(context: Context, stringRes: Int) {
        Toast.makeText(context, context.getString(stringRes), Toast.LENGTH_SHORT).show()
    }
}