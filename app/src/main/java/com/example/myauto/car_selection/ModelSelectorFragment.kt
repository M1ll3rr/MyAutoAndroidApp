package com.example.myauto.car_selection

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.myauto.R
import com.example.myauto.activity.MainActivity
import com.example.myauto.car_selection.base.BaseSelectorFragment
import com.example.myauto.car_selection.vm.CarSelectorViewModel
import com.example.myauto.car_selection.vm.CarSelectorViewModelFactory
import com.example.myauto.room.entity.CarInfoEntity
import com.example.myauto.room.entity.CarModelInfoEntity
import kotlinx.coroutines.launch

class ModelSelectorFragment : BaseSelectorFragment() {
    private val viewModel: CarSelectorViewModel by viewModels() {
        CarSelectorViewModelFactory(
            (requireActivity() as MainActivity).getRepository(),
            (requireActivity() as MainActivity).application
        )
    }
    private val args: ModelSelectorFragmentArgs by navArgs()
    override val titleResId: Int = R.string.model

    override fun onItemClick(item: CarInfoEntity) {
        viewLifecycleOwner.lifecycleScope.launch {
            val brandName = viewModel.getBrandNameById(args.brandId)
            val model = item as CarModelInfoEntity
            val action = ModelSelectorFragmentDirections.actionModelToYear(
                brandName = brandName,
                modelName = model.name,
                yearFrom = model.yearFrom,
                yearTo = model.yearTo
            )
            findNavController().navigate(action)
        }
    }

    override fun initUI() {
        viewLifecycleOwner.lifecycleScope.launch {
            selectorAdapter.submitList(viewModel.getModels(args.brandId))
        }
    }
}
