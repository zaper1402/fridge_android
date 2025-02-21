package com.ashir.fridge.ui.home.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.ashir.fridge.account.pojo.CuisineType
import com.ashir.fridge.account.pojo.Cuisines
import com.ashir.fridge.databinding.FragmentCuisineBinding
import com.ashir.fridge.ui.home.HomeUtils
import com.ashir.fridge.ui.home.adapters.CuisinesAdapter
import com.ashir.fridge.utils.IModel
import com.ashir.fridge.utils.listeners.DelegateClickListener
import com.threemusketeers.dliverCustomer.main.utils.extensions.getSafe

class CuisinesFragment: Fragment() {

    private var mBinding: FragmentCuisineBinding? = null
    private val binding get() = mBinding!!
    private var cuisines: Cuisines? = null

    companion object{
        const val TAG = "CuisinesFragment"
        const val CUISINES = "cuisines"
        fun newInstance(cuisines: Cuisines?) = CuisinesFragment().apply {
            arguments = Bundle().apply {
                putSerializable(CUISINES, cuisines)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mBinding = FragmentCuisineBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cuisines = arguments?.getSerializable(CUISINES) as? Cuisines
        setUpUi()
    }

    private val cuisineClickListener = object : DelegateClickListener {
        override fun onClick(iModel: IModel?, position: Int, otherData: Any?) {
            when (iModel) {
                is CuisineType -> {
                    val cuisineName = iModel.cuisineName
                    Toast.makeText(context, cuisineName, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setUpUi(){
        val cuisineList = cuisines?.cuisinesList
        val firstCuisineName = cuisineList?.getSafe(0)?.cuisineName
        mBinding?.firstCuisineTv?.text = firstCuisineName
        val firstCuisineRes = HomeUtils.cuisineToImageMap[firstCuisineName]
        if (firstCuisineRes != null) {
            mBinding?.firstCuisineIv?.setImageResource(firstCuisineRes)
        }

        val cuisineRvList = cuisineList?.subList(1, cuisineList.size)
        val gridLayoutManager = GridLayoutManager(activity, 2)
        mBinding?.cuisinesRv?.layoutManager = gridLayoutManager
        val adapter = CuisinesAdapter(cuisineRvList, cuisineClickListener)
        mBinding?.cuisinesRv?.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }
}