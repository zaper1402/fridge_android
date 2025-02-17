package com.ashir.fridge

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.ashir.fridge.databinding.ActivityMainBinding
import com.ashir.fridge.ui.MainActivityViewModel
import com.ashir.fridge.ui.onboarding.enums.OnboardingStates
import com.ashir.fridge.ui.onboarding.enums.OnboardingStatesData
import com.ashir.fridge.ui.onboarding.fragments.OnboardingFragment
import com.ashir.fridge.ui.onboarding.interfaces.OnboardingStateListener
import com.ashir.fridge.utils.sharedprefs.SharedPrefUtil
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.threemusketeers.dliverCustomer.main.utils.sharedprefs.SharedPrefConstants

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val auth = Firebase.auth
    private var user: FirebaseUser? = null
    private var mainActivityViewModel : MainActivityViewModel? = null

    companion object {
        const val TAG = "MainActivity"
    }
    private val onboardingStateListener = object : OnboardingStateListener {
        override fun onLoginSuccessful(state: OnboardingStates, user: FirebaseUser?) {
            when (state) {
                OnboardingStates.COMPLETED -> setupPostLogin()
                else -> {}
            }
        }

        override fun onRegistrationSuccessful(state: OnboardingStates, user: FirebaseUser?) {
            when (state) {
                OnboardingStates.COMPLETED -> setupPostLogin()
                else -> {}
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        installSplashScreen()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        user = auth.currentUser
        handleBasedOnAuth()

    }

    private fun handleBasedOnAuth() {
        if (user == null || (SharedPrefUtil.getDefaultInstance().getDataObject(SharedPrefConstants.ONBOARDING_STATE, OnboardingStatesData::class.java)?.state ?: OnboardingStates.UNKNOWN) != OnboardingStates.COMPLETED) {
            setupPreLogin()
        } else {
            setupPostLogin()
        }
    }

    private fun setupPostLogin() {
        binding.postLoginParent.visibility = View.VISIBLE
        binding.preLoginParent.visibility = View.GONE
//        mainActivityViewModel?.getCustomerAccountData(AccountManager.uid)
        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
//        setupActionBarWithNavController(navController)
        navView.setupWithNavController(navController)
    }

    private fun setupPreLogin() {
        binding.postLoginParent.visibility = View.GONE
        binding.preLoginParent.visibility = View.VISIBLE
        supportFragmentManager.beginTransaction().add(binding.preLoginParent.id, OnboardingFragment.getInstance(onboardingStateListener)).commit()
    }
}