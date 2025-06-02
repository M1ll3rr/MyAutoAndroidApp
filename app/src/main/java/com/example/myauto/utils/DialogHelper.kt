package com.example.myauto.utils

import android.content.Context
import com.example.myauto.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText

object DialogHelper {
    fun showDeleteDialog(context: Context, onConfirm: () -> Unit) {
        MaterialAlertDialogBuilder(context)
            .setTitle(R.string.delete_title)
            .setMessage(R.string.delete_confirmation)
            .setPositiveButton(R.string.yes) { _, _ -> onConfirm() }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    fun showConfirmationDialog(
        context: Context,
        titleRes: Int,
        messageRes: Int,
        positiveButtonRes: Int,
        onConfirm: () -> Unit
    ) {
        MaterialAlertDialogBuilder(context)
            .setTitle(context.getString(titleRes))
            .setMessage(context.getString(messageRes))
            .setPositiveButton(context.getString(positiveButtonRes)) { _, _ -> onConfirm() }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    fun showAboutDialog(context: Context) {
        MaterialAlertDialogBuilder(context)
            .setTitle(context.getString(R.string.about_title))
            .setMessage(context.getString(R.string.about_message))
            .setPositiveButton(android.R.string.ok, null)
            .show()
    }

    fun showAuthRequiredDialog(
        context: Context,
        onLogin: () -> Unit
    ) {
        MaterialAlertDialogBuilder(context)
            .setTitle(context.getString(R.string.auth_required_title))
            .setMessage(context.getString(R.string.auth_required_message))
            .setPositiveButton(context.getString(R.string.login)) { _, _ -> onLogin() }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    fun showAccountInfoDialog(
        context: Context,
        email: String,
        onLogout: () -> Unit
    ) {
        MaterialAlertDialogBuilder(context)
            .setTitle(context.getString(R.string.account_title))
            .setMessage(context.getString(R.string.account_message, email))
            .setPositiveButton(context.getString(R.string.logout)) { _, _ -> onLogout() }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
}