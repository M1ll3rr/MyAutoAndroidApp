package com.example.myauto.car_selection

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.myauto.R
import com.example.myauto.activity.MainActivity
import com.example.myauto.car_selection.base.BaseSelectorFragment
import com.example.myauto.car_selection.vm.AddCarViewModel
import com.example.myauto.data.BodyTypeData
import com.example.myauto.data.BodyTypeEntity
import com.example.myauto.room.entity.CarInfoEntity
import com.example.myauto.unified.UnifiedViewModelFactory
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.launch

class BtSelectorFragment : BaseSelectorFragment() {
    private val args: BtSelectorFragmentArgs by navArgs()
    private val viewModel: AddCarViewModel by viewModels {
        val repository = (requireActivity() as MainActivity).getRepository()
        UnifiedViewModelFactory(repository)
    }
    override val titleResId: Int = R.string.body_type

    override fun setupTransitions() {
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true).apply {
            excludeTarget(R.id.backButton, true)
        }
        returnTransition = null
    }

    override fun onItemClick(item: CarInfoEntity) {
        val bodyTypeEntity = item as BodyTypeEntity
        val bodyType = BodyTypeData.fromEntity(bodyTypeEntity)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.addCar(args.brandName, args.modelName, args.year, bodyType)
            returnTransition = MaterialSharedAxis(MaterialSharedAxis.Y, true)
            findNavController().popBackStack(R.id.mainFragment, false)
        }
    }

    override fun initUI() {
        val bodyTypes = BodyTypeData.entries.map { it.toEntity(requireContext()) }
        selectorAdapter.submitList(bodyTypes)
    }
}