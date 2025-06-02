package com.example.myauto.main

import android.content.Intent
import android.os.Bundle
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.myauto.R
import com.example.myauto.activity.AuthActivity
import com.example.myauto.activity.MainActivity
import com.example.myauto.data.BodyTypeData
import com.example.myauto.databinding.FragmentMainBinding
import com.example.myauto.room.DatabaseInitializer
import com.example.myauto.room.entity.UserCarEntity
import com.example.myauto.unified.UnifiedViewModelFactory
import com.example.myauto.utils.DialogHelper
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.transition.MaterialSharedAxis
import com.google.firebase.auth.FirebaseUser
import dev.androidbroadcast.vbpd.viewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainFragment : Fragment() {
    private val binding: FragmentMainBinding by viewBinding(FragmentMainBinding::bind)
    private val viewModel: UserCarViewModel by viewModels {
        val repository = (requireActivity() as MainActivity).getRepository()
        UnifiedViewModelFactory(repository)
    }
    private val firebaseAuthInstance by lazy {
        (requireActivity() as MainActivity).getFirebaseAuth()
    }
    private val firebaseSyncManager by lazy {
        (requireActivity() as MainActivity).getFirebaseSyncManager()
    }
    private var currentCarId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigationDrawer()
        setupSwipeGestures()
        initUI()
        initObservers()
    }

    private fun setupSwipeGestures() {
        val gestureDetector = GestureDetector(requireContext(), object : GestureDetector.SimpleOnGestureListener() {
            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                e1?.let {
                    val deltaX = e2.x - it.x
                    when {
                        deltaX < -100 -> handleSwipeLeft()
                        deltaX > 100 -> handleSwipeRight()
                    }
                }
                return true
            }
        })

        binding.MainFragmentLayout.setOnTouchListener { v, event ->
            gestureDetector.onTouchEvent(event)
            v.performClick() 
            true
        }
    }

    private fun handleSwipeLeft() {
        when (viewModel.getCurrentCarIndex()) {
            -1 -> return
            viewModel.getCarsSize() - 1 -> switchCarWithAnimation(SWIPE_TO_EMPTY)
            else -> switchCarWithAnimation(SWIPE_LEFT)
        }
    }

    private fun handleSwipeRight() {
        if (viewModel.getCurrentCarIndex() == 0
            || (viewModel.getCurrentCarIndex() == -1 && viewModel.getCarsSize() == 0))
            return
        else switchCarWithAnimation(SWIPE_RIGHT)
    }

    private fun switchCarWithAnimation(direction: Int) {
        val slideOut = AnimationUtils.loadAnimation(requireContext(),
            if (direction == SWIPE_RIGHT) R.anim.slide_out_right else R.anim.slide_out_left
        )

        val slideIn = AnimationUtils.loadAnimation(requireContext(),
            if (direction == SWIPE_RIGHT) R.anim.slide_in_left else R.anim.slide_in_right
        )


        with(binding) {
            carImage.startAnimation(slideOut)
            if (newCarButton.isVisible) {
                newCarButton.startAnimation(slideOut)
            }
            else {
                carInfoTV.visibility = View.GONE
                carMileageTV.visibility = View.GONE
                carInfoTV.startAnimation(slideOut)
                carMileageTV.startAnimation(slideOut)
            }
        }

        slideOut.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
                with(binding) {
                    newCarButton.isClickable = false
                    carInfoTV.isClickable = false
                    carMileageTV.isClickable = false
                    fuelButton.isClickable = false
                    maintenanceButton.isClickable = false
                    repairButton.isClickable = false
                    insuranceButton.isClickable = false
                    menuButton.isClickable = false
                }
            }
            override fun onAnimationRepeat(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                when (direction) {
                    SWIPE_LEFT -> viewModel.switchToNextCar()
                    SWIPE_RIGHT -> viewModel.switchToPreviousCar()
                    SWIPE_TO_EMPTY -> viewModel.clearSelection()
                }
                with(binding) {
                    carImage.startAnimation(slideIn)
                    if (direction == SWIPE_TO_EMPTY) {
                        carImage.setImageResource(R.drawable.car_unknown)
                        newCarButton.startAnimation(slideIn)
                    }
                    else {
                        carInfoTV.startAnimation(slideIn)
                        carMileageTV.startAnimation(slideIn)
                    }
                }
            }
        })
        slideIn.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationRepeat(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                with(binding) {
                    newCarButton.isClickable = true
                    carInfoTV.isClickable = true
                    carMileageTV.isClickable = true
                    fuelButton.isClickable = true
                    maintenanceButton.isClickable = true
                    repairButton.isClickable = true
                    insuranceButton.isClickable = true
                    menuButton.isClickable = true
                }
            }
        })
    }


    private fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.currentCar.collect { car ->
                    if (car != null) {
                        updateCarViews(car)
                    } else {
                        showEmptyState()
                    }
                    currentCarId = car?.id
                }
            }
        }
    }

    private fun updateCarViews(car: UserCarEntity) {
        with(binding) {
            val carMainInfo = "${car.brand} ${car.model} (${car.year})"
            val carMileage = "${getString(R.string.mileage)}: ${car.mileage} ${getString(R.string.km)}"
            carInfoTV.text = carMainInfo
            carInfoTV.visibility = View.VISIBLE
            carMileageTV.text = carMileage
            carMileageTV.visibility = View.VISIBLE
            newCarButton.visibility = View.GONE

            BodyTypeData.entries.getOrNull(car.carBodyType)?.let {
                carImage.setImageResource(it.imageId)
            }
        }
        if (car.mileage == 0) {
            showMileageDialog()
        }
    }

    private fun showEmptyState() {
        with(binding) {
            carImage.setImageResource(R.drawable.car_unknown)
            carInfoTV.visibility = View.GONE
            carMileageTV.visibility = View.GONE
            newCarButton.visibility = View.VISIBLE
        }
    }


    private fun setupNavigationDrawer() {
        val drawerLayout = binding.mainFragmentLayout
        val navigationView = binding.navView

        binding.menuButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_account -> {
                    handleAccountAction(firebaseAuthInstance.currentUser)
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
                R.id.nav_settings -> {
                    CoroutineScope(Dispatchers.IO).launch {
                        DatabaseInitializer(requireContext().applicationContext).populateDatabase()
                        requireActivity().runOnUiThread {
                            Toast.makeText(context, R.string.download_success, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                R.id.nav_about -> {
                    showAboutDialog()
                }

                R.id.nav_upload -> {
                    if (firebaseAuthInstance.currentUser == null) {
                        showAuthRequiredDialog()
                    } else {
                        lifecycleScope.launch {
                            try {
                                firebaseSyncManager.uploadAllData()
                                Toast.makeText(context, getString(R.string.download_success), Toast.LENGTH_SHORT).show()
                            } catch (e: Exception) {
                                Toast.makeText(context, "${getString(R.string.error)}: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }

                R.id.nav_download -> {
                    if (firebaseAuthInstance.currentUser == null) {
                        showAuthRequiredDialog()
                    } else {
                        lifecycleScope.launch {
                            try {
                                firebaseSyncManager.downloadAllData()
                                Toast.makeText(context, getString(R.string.download_success), Toast.LENGTH_SHORT).show()
                            } catch (e: Exception) {
                                Toast.makeText(context, "${getString(R.string.error)}: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun showDeleteCarDialog(carId: Int) {
        DialogHelper.showConfirmationDialog(
            context = requireContext(),
            titleRes = R.string.auto_remove_title,
            messageRes = R.string.auto_remove_info,
            positiveButtonRes = R.string.delete,
            onConfirm = { viewModel.deleteCar(carId) }
        )
    }


    private fun initUI() {
        val drawerLayout = binding.mainFragmentLayout

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                requireActivity().finish()
            }
        }

        binding.newCarButton.setOnClickListener {
            exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
            reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
            val action = MainFragmentDirections.actionMainToBrand()
            findNavController().navigate(action)
        }
        binding.insuranceButton.setOnClickListener {
            currentCarId?.let {
                exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
                reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
                val action = MainFragmentDirections.actionMainToInsurance()
                findNavController().navigate(action)
            } ?: showCarSelectionToast()
        }
        binding.fuelButton.setOnClickListener {
            currentCarId?.let {
                exitTransition = MaterialSharedAxis(MaterialSharedAxis.Y, true)
                reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, false)
                val action = MainFragmentDirections.actionMainToFuel()
                findNavController().navigate(action)
            } ?: showCarSelectionToast()
        }
        binding.repairButton.setOnClickListener {
            currentCarId?.let {
                exitTransition = MaterialSharedAxis(MaterialSharedAxis.Y, true)
                reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, false)
                val action = MainFragmentDirections.actionMainToRepair()
                findNavController().navigate(action)
            } ?: showCarSelectionToast()
        }
        binding.maintenanceButton.setOnClickListener {
            currentCarId?.let {
                exitTransition = MaterialSharedAxis(MaterialSharedAxis.Y, true)
                reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, false)
                val action = MainFragmentDirections.actionMainToMaintenance()
                findNavController().navigate(action)
            } ?: showCarSelectionToast()
        }
        binding.carInfoTV.setOnLongClickListener {
            currentCarId?.let { showDeleteCarDialog(it) }
            true
        }
        binding.carMileageTV.setOnClickListener() {
            showMileageDialog()
        }
    }

    private fun showCarSelectionToast() {
        val message = if (viewModel.getCarsSize() == 0) {
            requireContext().getString(R.string.toast_add_auto)
        } else {
            requireContext().getString(R.string.toast_choose_auto)
        }
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }


    private fun showAboutDialog() {
        DialogHelper.showAboutDialog(requireContext())
    }

    private fun showMileageDialog() {
        val currentCarMileage = viewModel.currentCar.value?.mileage ?: return

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.mileage_change))
            .setView(R.layout.dialog_mileage_et)
            .setPositiveButton(getString(R.string.save)) { dialog, _ ->
                val input = (dialog as AlertDialog).findViewById<TextInputEditText>(R.id.dialogMileageET)
                val newMileage = input?.text?.toString()

                when {
                    newMileage.isNullOrEmpty() ->
                        Toast.makeText(context, R.string.toast_error_mileage_empty, Toast.LENGTH_SHORT).show()

                    newMileage.toInt() < currentCarMileage ->
                        Toast.makeText(context, R.string.toast_error_mileage_lower, Toast.LENGTH_LONG).show()

                    else ->
                        viewModel.updateCarMileage(newMileage.toInt())
                }
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show().also { dialog ->
                (dialog as AlertDialog).findViewById<TextInputEditText>(R.id.dialogMileageET)?.apply {
                    setText(currentCarMileage.toString())
                }
            }
    }

    private fun showAuthRequiredDialog() {
        DialogHelper.showAuthRequiredDialog(
            context = requireContext(),
            onLogin = {
                (requireActivity() as MainActivity).authActivityLauncher.launch(
                    Intent(requireContext(), AuthActivity::class.java))
            }
        )
    }

    private fun handleAccountAction(user: FirebaseUser?) {
        if (user == null) {
            (requireActivity() as MainActivity).authActivityLauncher.launch(
                Intent(requireContext(), AuthActivity::class.java)
            )
        } else {
            showAccountInfoDialog(user.email ?: "")
        }
    }

    private fun showAccountInfoDialog(email: String) {
        DialogHelper.showAccountInfoDialog(
            context = requireContext(),
            email = email,
            onLogout = {
                firebaseAuthInstance.signOut()
                Toast.makeText(context, R.string.logged_out, Toast.LENGTH_SHORT).show()
            }
        )
    }


    companion object {
        const val SWIPE_LEFT = 0
        const val SWIPE_RIGHT = 1
        const val SWIPE_TO_EMPTY = 2
    }
}