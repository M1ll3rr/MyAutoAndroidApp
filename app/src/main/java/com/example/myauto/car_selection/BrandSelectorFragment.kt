package com.example.myauto.car_selection

import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.myauto.R
import com.example.myauto.activity.MainActivity
import com.example.myauto.car_selection.base.BaseSelectorFragment
import com.example.myauto.car_selection.vm.CarSelectorViewModel
import com.example.myauto.car_selection.vm.CarSelectorViewModelFactory
import com.example.myauto.room.entity.CarInfoEntity
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.launch

class BrandSelectorFragment : BaseSelectorFragment() {
    private val viewModel: CarSelectorViewModel by viewModels() {
        CarSelectorViewModelFactory(
            (requireActivity() as MainActivity).getRepository(),
            (requireActivity() as MainActivity).application
        )
    }
    override val titleResId: Int = R.string.brand

    override fun setupTransitions() {
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false).apply {
            excludeTarget(R.id.backButton, true)
        }
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    }

    override fun onItemClick(item: CarInfoEntity) {
        val action = BrandSelectorFragmentDirections.actionBrandToModel(item.id)
        findNavController().navigate(action)
    }

    override fun initUI() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.brands.collect { brands ->
                        if (brands.isNotEmpty()) {
                            selectorAdapter.submitList(brands)
                        }
                    }
                }
                launch {
                    viewModel.isLoading.collect {
                        if (it) showLoading() else hideLoading()
                    }
                }
            }
        }
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        binding.progressBar.visibility = View.GONE
    }

}

