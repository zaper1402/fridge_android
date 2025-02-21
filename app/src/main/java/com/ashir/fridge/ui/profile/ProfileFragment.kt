package com.ashir.fridge.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.ashir.fridge.MainActivity
import com.ashir.fridge.account.AccountManager
import com.ashir.fridge.databinding.FragmentProfileBinding
import com.threemusketeers.dliverCustomer.main.utils.extensions.debouncedClickListener

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val profileViewModel by viewModels<ProfileViewModel>()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val profileViewModel =
            ViewModelProvider(this)[ProfileViewModel::class.java]

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUi()
        setupObservers()
        setupClickListeners()
    }

    private fun setupUi() {
        // Setup UI
        binding.editName.text = AccountManager.user?.name
        binding.editGender.text = AccountManager.user?.gender
        binding.editDob.text = AccountManager.user?.dateOfBirth
        binding.editMobileNumber.text = AccountManager.user?.phoneNumber
    }

    private fun setupObservers() {
        // Setup Observers
        profileViewModel.logoutLiveData.observe(viewLifecycleOwner) {
            when (it) {
                else -> handleLogout()
            }
        }
    }

    private fun handleLogout() {
        AccountManager.resetAccountInfo()
        // Navigate to Login Screen
        (activity as? MainActivity)?.setupPreLogin()
    }

    private fun setupClickListeners() {
        // Setup Click Listeners
        binding.btnContinue.debouncedClickListener {
            profileViewModel.logout()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}