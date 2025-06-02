package com.example.myauto.activity

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.myauto.R
import com.example.myauto.data.PreferencesManager
import com.example.myauto.databinding.ActivityAuthBinding
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicatorSpec
import com.google.android.material.progressindicator.IndeterminateDrawable
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException

class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var progressDrawable: IndeterminateDrawable<CircularProgressIndicatorSpec>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        enableEdgeToEdge()
        setContentView(binding.root)

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    setResult(Activity.RESULT_CANCELED)
                    finish()
                }
            }
        )

        val spec = CircularProgressIndicatorSpec(this, null)
        progressDrawable = IndeterminateDrawable.createCircularDrawable(this, spec)

        initUI()
    }


    private fun loginUser(email: String, password: String) {
        val emailContainer = binding.emailEditTextContainer
        val passwordContainer = binding.passwordEditTextContainer
        emailContainer.error = null
        passwordContainer.error = null

        if (email.isEmpty()) {
            emailContainer.error = getString(R.string.error_enter_email)
            return
        }
        if (password.isEmpty()) {
            passwordContainer.error = getString(R.string.error_enter_password)
            return
        }

        showLoading(true, 0)
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                showLoading(false, 0)
                if (task.isSuccessful) {
                    clearGuestMode()
                    setResult(Activity.RESULT_OK)
                    finish()
                } else {
                    handleAuthError(task.exception, emailContainer, passwordContainer)
                }
            }
    }

    private fun registerUser(email: String, password: String) {
        val emailContainer = binding.emailEditTextContainer
        val passwordContainer = binding.passwordEditTextContainer
        emailContainer.error = null
        passwordContainer.error = null

        if (email.isEmpty()) {
            emailContainer.error = getString(R.string.error_enter_email)
            return
        }

        if (password.length < 6) {
            passwordContainer.error = getString(R.string.error_short_password)
            return
        }

        showLoading(true, 1)
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                showLoading(false, 1)
                if (task.isSuccessful) {
                    Toast.makeText(this, getString(R.string.registration_success), Toast.LENGTH_SHORT).show()
                    clearGuestMode()
                    setResult(Activity.RESULT_OK)
                    finish()
                } else {
                    handleAuthError(task.exception, emailContainer, passwordContainer)
                }
            }
    }

    private fun handleAuthError(exception: Exception?,
                                emailContainer: TextInputLayout,
                                passwordContainer: TextInputLayout) {
        when (exception) {
            is FirebaseAuthInvalidUserException ->
                emailContainer.error = getString(R.string.error_user_not_found)

            is FirebaseAuthInvalidCredentialsException -> {
                if (exception.message?.contains("email", ignoreCase = true) == true) {
                    emailContainer.error = getString(R.string.error_invalid_email)
                } else {
                    passwordContainer.error = getString(R.string.error_wrong_password)
                }
            }

            is FirebaseAuthUserCollisionException -> {
                emailContainer.error = getString(R.string.error_email_in_use)
            }

            is FirebaseNetworkException ->
                Toast.makeText(this, getString(R.string.error_network), Toast.LENGTH_LONG).show()

            is FirebaseTooManyRequestsException ->
                Toast.makeText(this, getString(R.string.error_too_many_requests), Toast.LENGTH_LONG).show()


            else -> {
                emailContainer.error = getString(R.string.error_auth)
                passwordContainer.error = exception?.message
            }
        }
    }

    private fun initUI() {
        val emailEditText = binding.emailEditText
        val passwordEditText = binding.passwordEditText
        val loginButton = binding.loginButton as MaterialButton
        val registerButton = binding.registerButton as MaterialButton
        val continueWithoutAuthButton = binding.continueWithoutAuthButton

        emailEditText.addTextChangedListener {
            binding.emailEditTextContainer.error = null
        }

        passwordEditText.addTextChangedListener {
            binding.passwordEditTextContainer.error = null
        }

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            loginUser(email, password)
        }

        registerButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            registerUser(email, password)
        }

        continueWithoutAuthButton.setOnClickListener {
            saveGuestMode()
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    private fun showLoading(show: Boolean, buttonN: Int = 0) {
        with(binding) {
            loginButton.isEnabled = !show
            registerButton.isEnabled = !show
            continueWithoutAuthButton.isEnabled = !show
            val button = (if (buttonN == 0) loginButton else registerButton) as MaterialButton
            button.icon = if (show) progressDrawable else null
        }
    }


    private fun saveGuestMode() {
        PreferencesManager.setGuestMode(true)
    }

    private fun clearGuestMode() {
        PreferencesManager.setGuestMode(false)
    }
}
