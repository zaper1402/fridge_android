package com.ashir.fridge

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.ashir.fridge.account.AccountManager
import com.ashir.fridge.databinding.ActivityMainBinding
import com.ashir.fridge.http.Result
import com.ashir.fridge.ui.MainActivityViewModel
import com.ashir.fridge.ui.onboarding.enums.OnboardingStates
import com.ashir.fridge.ui.onboarding.enums.OnboardingStatesData
import com.ashir.fridge.ui.onboarding.fragments.OnboardingFragment
import com.ashir.fridge.ui.onboarding.interfaces.OnboardingStateListener
import com.ashir.fridge.utils.sharedprefs.SharedPrefConstants
import com.ashir.fridge.utils.sharedprefs.SharedPrefUtil
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import okhttp3.Response

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
        user = auth.currentUser
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupObservers()
        handleBasedOnAuth()

    }

    private fun setupObservers() {
        mainActivityViewModel?.getSelfUserLiveData?.observe(this ){
            when(it){
                is Result.Success -> {
                    AccountManager.saveAccountInfo(it.data)
                    setupPostLogin()
                }
                is Result.Error<*> -> {
                    val resp = it.responseBody as? Response
                    if(resp?.code == 404){
                        setupPreLogin()
                    }else {
                        // todo
                    }
                }
                is Result.InProgress -> {

                }
            }
        }
    }

    private fun handleBasedOnAuth() {
        if ((SharedPrefUtil.getDefaultInstance().getDataObject(SharedPrefConstants.ONBOARDING_STATE, OnboardingStatesData::class.java)?.state ?: OnboardingStates.UNKNOWN) != OnboardingStates.COMPLETED || AccountManager.uid == null) {
            setupPreLogin()
        } else if(user == null || auth.currentUser == null){
            mainActivityViewModel?.getSelfUser()
        }else {
            setupPostLogin()
        }
    }

    private fun setupPostLogin() {
        binding.postLoginParent.visibility = View.VISIBLE
        binding.preLoginParent.visibility = View.GONE
        binding.preLoginParent.removeAllViews()
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

    fun setupPreLogin() {
        binding.postLoginParent.visibility = View.GONE
        binding.preLoginParent.visibility = View.VISIBLE
        supportFragmentManager.beginTransaction().add(binding.preLoginParent.id, OnboardingFragment.getInstance(onboardingStateListener)).commit()
    }


}