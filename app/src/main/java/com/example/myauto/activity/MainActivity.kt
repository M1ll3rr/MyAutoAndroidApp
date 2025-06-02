package com.example.myauto.activity

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import com.example.myauto.AppRepository
import com.example.myauto.FirebaseSyncManager
import com.example.myauto.R
import com.example.myauto.data.PreferencesManager
import com.example.myauto.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val navController by lazy {
        (supportFragmentManager.findFragmentById(R.id.mainFragmentContainer) as NavHostFragment).navController
    }
    private val appRepository by lazy { AppRepository(applicationContext) }
    fun getRepository() = appRepository

    private val firebaseAuthInstance by lazy {
        FirebaseAuth.getInstance()
    }
    fun getFirebaseAuth() = firebaseAuthInstance

    private val _firebaseSyncManager by lazy {
        FirebaseSyncManager(appRepository, firebaseAuthInstance)
    }
    fun getFirebaseSyncManager() = _firebaseSyncManager


    val authActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!isGuestMode() && firebaseAuthInstance.currentUser == null) {
            authActivityLauncher.launch(Intent(this, AuthActivity::class.java))
        }
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED
    }

    private fun isGuestMode(): Boolean {
        return PreferencesManager.isGuestMode()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }

}