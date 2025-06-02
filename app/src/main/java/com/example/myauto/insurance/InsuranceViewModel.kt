package com.example.myauto.insurance

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myauto.AppRepository
import kotlinx.coroutines.launch

class InsuranceViewModel(private val repository: AppRepository) : ViewModel() {

    fun saveFile(uri: Uri) {
        viewModelScope.launch {
            val existing = repository.getPolicyFile()
            if (existing != null) {
                repository.updatePolicyFile(uri.toString())
            } else {
                repository.addPolicyFile(uri.toString())
            }
        }
    }

    fun getFileUri(callback: (String?) -> Unit) {
        viewModelScope.launch {
            val uri = repository.getPolicyFile()
            callback(uri)
        }
    }

    fun deleteFile() {
        viewModelScope.launch {
            repository.deletePolicyFile()
        }
    }
}