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
import androidx.fragment.app.viewModels
import com.ashir.fridge.MainActivity
import com.ashir.fridge.account.AccountManager
import com.ashir.fridge.account.pojo.Product
import com.ashir.fridge.account.pojo.QuantityType
import com.ashir.fridge.databinding.ProductFormLayoutBinding
import com.ashir.fridge.http.Result
import com.ashir.fridge.ui.home.HomeViewModel
import com.ashir.fridge.utils.Utils
import com.threemusketeers.dliverCustomer.main.utils.extensions.debouncedClickListener
import okhttp3.Response
import okhttp3.internal.toNonNegativeInt
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class ProductFormFragment : Fragment() {

    private var _binding: ProductFormLayoutBinding? = null
    private val binding get() = _binding!!
    private var product: Product? = null
    private val userProduct = JSONObject()
    private val items = QuantityType.entries.map { it.value }
    private val spinnerAdapter: ArrayAdapter<String> by lazy {
        ArrayAdapter(requireContext(), R.layout.simple_spinner_item, items)
    }
    private val homeViewModel: HomeViewModel by viewModels<HomeViewModel>()

    companion object {
        const val TAG = "ProductFormFragment"

        fun newInstance(product: Product) = ProductFormFragment().apply {
            arguments = Bundle().apply {
                putSerializable("product", product)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ProductFormLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        product = arguments?.getSerializable("product") as? Product
        userProduct.put("product_id", product?.id)
        userProduct.put("user_id", AccountManager.uid)
        setupUi()
        setupObserver()
        setupClickListeners()
    }

    private fun setupUi() {
        disableEnableLoginButton(false)
        binding.editName.hint = product?.name
        spinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding.spinnerUnit.adapter = spinnerAdapter
        addTextWatchers()
    }

    private fun addTextWatchers() {
        binding.editName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                userProduct.put("name", s.toString())
                validateAndEnableButton()
            }
        })
        binding.editQunatity.addTextChangedListener(object : TextWatcher {
            private var isUpdating = false
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                // store as int
                userProduct.put("quantity", s.toString().toNonNegativeInt(0))
                validateAndEnableButton()
            }
        })
        binding.spinnerUnit.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                userProduct.put("quantity_type", QuantityType.entries.firstOrNull { it.value == items[position]})
                validateAndEnableButton()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                userProduct.put("quantity_type", "")
                validateAndEnableButton()
            }
        }
        binding.editBrand.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                userProduct.put("brand", s.toString())
            }
        })

        binding.expiryEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                userProduct.put("expiry", s.toString())
                validateAndEnableButton()
            }
        })
    }

    private fun validateAndEnableButton() {
        val isValid = userProduct.optString("name").isNotEmpty() &&
                userProduct.optInt("quantity") > 0 &&
                QuantityType.entries.firstOrNull{it.name == userProduct.optString("quantity_type")} != null &&
                Utils.isValidDate(userProduct.optString("expiry"))
        disableEnableLoginButton(isValid)
    }


    private fun disableEnableLoginButton(isEnabled: Boolean) {
        binding.layoutSaveButton.isEnabled = isEnabled
        binding.layoutSaveButton.alpha = if (isEnabled) 1f else 0.5f
    }

    private fun setupObserver() {
        // Setup Observer
        homeViewModel.addProductLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Result.Success -> {
                    parentFragmentManager.popBackStack()
                }
                is Result.Error<*> -> {
                    val resp = it.responseBody as? Response
                    if (resp?.code == 404) {
                        (activity as? MainActivity)?.setupPreLogin()
                    } else {
                        // todo
                    }
                }
                is Result.InProgress -> {
                    // todo
                }
            }
        }
    }

    private fun setupClickListeners() {
        // Setup Click Listeners
        binding.layoutSaveButton.debouncedClickListener(1000) {
            homeViewModel.addProduct(userProduct)
        }

        binding.expiryEt.debouncedClickListener {
            binding.checkboxStandardExpiry.isChecked = false
            Utils.showDatePickerDialog(requireContext(), binding.expiryEt)
        }

        binding.checkboxStandardExpiry.debouncedClickListener(1000) {
            if (binding.checkboxStandardExpiry.isChecked) {
                // add 7 days to current date
                val currentExpiry = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(product?.standardExpiryDays?.toLong()?:0L)
                userProduct
                binding.expiryEt.text = Utils.showDateFromTime(currentExpiry)
            } else {
                userProduct.put("expiry", "")
                binding.expiryEt.text = ""
            }
            validateAndEnableButton()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}