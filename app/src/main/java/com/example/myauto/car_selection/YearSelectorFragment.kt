package com.example.myauto.car_selection

import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.myauto.R
import com.example.myauto.car_selection.base.BaseSelectorFragment
import com.example.myauto.data.YearEntity
import com.example.myauto.room.entity.CarInfoEntity

class YearSelectorFragment : BaseSelectorFragment() {
    private val args: YearSelectorFragmentArgs by navArgs()
    override val titleResId: Int = R.string.year

    override fun onItemClick(item: CarInfoEntity) {
        val yearEntity = item as YearEntity
        val action = YearSelectorFragmentDirections.actionYearToBodyType(
            brandName = args.brandName,
            modelName = args.modelName,
            year = yearEntity.year
        )
        findNavController().navigate(action)
    }

    override fun initUI() {
        val yearsList = (args.yearFrom..args.yearTo).map { YearEntity(it) }
        selectorAdapter.submitList(yearsList)
    }
}
