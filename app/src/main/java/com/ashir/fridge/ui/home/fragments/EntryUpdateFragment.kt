package com.ashir.fridge.ui.home.fragments

import android.R
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.ashir.fridge.account.pojo.QuantityType
import com.ashir.fridge.databinding.EntryUpdateFormLayoutBinding
import com.ashir.fridge.ui.home.pojo.UserProduct

class EntryUpdateFragment : Fragment() {
    private var _binding: EntryUpdateFormLayoutBinding? = null
    private val binding get() = _binding!!
    private var userProduct: UserProduct? = null
    private val items = QuantityType.entries.map { it.value }
    private val spinnerAdapter: ArrayAdapter<String> by lazy {
        ArrayAdapter(requireContext(), R.layout.simple_spinner_item, items)
    }

    companion object {
        const val TAG = "EntryUpdateFragment"

        fun newInstance(userProduct: UserProduct) = EntryUpdateFragment().apply {
            arguments = Bundle().apply {
                // put arguments here
                putSerializable("userProduct", userProduct)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // get arguments here
        userProduct = arguments?.getSerializable("userProduct") as UserProduct
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = EntryUpdateFormLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        spinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding.spinnerUnit.adapter = spinnerAdapter
        setupUi()
    }

    private fun setupUi() {
        // setup ui here
        binding.editQunatity.setText(userProduct?.quantity.toString())
        binding.spinnerUnit.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                QuantityType.entries.firstOrNull { it.value == items[position]}?.let{
                    userProduct?.quantityType = it.name
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        binding.editQunatity.addTextChangedListener(object : TextWatcher {
            private var isUpdating = false
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                // store as int
                userProduct?.quantity = s.toString().toFloatOrNull() ?: 0f
                userProduct?.currentQuantity = userProduct?.quantity ?: 0f
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}