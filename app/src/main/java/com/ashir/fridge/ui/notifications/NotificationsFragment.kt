package com.ashir.fridge.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.ashir.fridge.R
import com.ashir.fridge.databinding.FragmentNotificationsBinding
import com.ashir.fridge.http.Result
import com.ashir.fridge.ui.notifications.adapters.ExpiryNotifAdapter
import com.ashir.fridge.ui.notifications.pojo.NotifData
import com.ashir.fridge.utils.IModel
import com.ashir.fridge.utils.listeners.DelegateClickListener
import com.threemusketeers.dliverCustomer.main.utils.extensions.setGone
import com.threemusketeers.dliverCustomer.main.utils.extensions.setVisible

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val notificationsViewModel by viewModels<NotificationsViewModel>()
    private var notifData : NotifData? = null

    private val delegateClickListener = object : DelegateClickListener {
        override fun onClick(iModel: IModel?, position: Int, otherData: Any?) {
            // Handle Click
        }
    }
    private val adapter = ExpiryNotifAdapter(delegateClickListener)
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUi()
        setupObservers()
        notificationsViewModel.getNotifData()
        setupClickListeners()
    }

    private fun setupUi() {
        // Setup UI
        binding.header.text = "Expiry Notifications"
        setupRv()
    }

    private fun setupRv() {
        binding.notifRv.adapter = adapter
        notifData?.notifItems?.let { adapter.submitList(it) }
    }

    private fun setupObservers() {
        // Setup Observers
        notificationsViewModel.notifLiveData.observe(viewLifecycleOwner) { notifData ->
            when (notifData) {
                is Result.Success -> {
                    setSuccessState(notifData.data)
                }
                is Result.Error<*> -> {
                    // Handle Error
                    setErrorState()
                }
                is Result.InProgress -> {
                    // Handle In Progress
                    setInProgressState()
                }
            }
        }
    }

    private fun setupClickListeners() {
        // Setup Click Listeners
    }

    private fun setSuccessState(data: NotifData) {
        // Set Success State
        this.notifData = data
        data.let {
            adapter.submitList(it.notifItems)
        }
        binding.emptyString.setGone()
    }

    private fun setErrorState() {
        // Set Error State
        binding.emptyString.text = getString(R.string.uh_oh_something_went_wrong_please_try_again_later)
        binding.emptyString.setVisible()
        binding.notifRv.setGone()
    }


    private fun setInProgressState() {
        // Set In Progress State
        binding.emptyString.text = getString(R.string.please_wait_fetching_all_your_items)
        binding.emptyString.setVisible()
        binding.notifRv.setGone()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}