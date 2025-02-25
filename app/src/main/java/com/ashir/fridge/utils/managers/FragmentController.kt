package com.ashir.fridge.utils.managers

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.threemusketeers.dliverCustomer.main.utils.extensions.forEachSafe
import com.threemusketeers.dliverCustomer.main.utils.extensions.isGreaterThan
import com.threemusketeers.dliverCustomer.main.utils.extensions.popBackstackSafe

object FragmentController {

    fun removeAllOverlayFragments(activity: FragmentActivity?) {
//        val fragments = (activity as? AppCompatActivity)?.supportFragmentManager?.fragments
//        fragments?.forEachSafe { fragment ->
//            if (fragment is Fragment) {
//                removeFragment(activity, fragment)
//            }
//        }
    }

    fun removeCurrentOverlayFragment(activity: FragmentActivity?, fragment: Fragment?) {
//        try {
//            if (fragment != null) {
//                removeFragment(activity, fragment)
//            }
//        } catch (e:Exception) {
//
//        }
    }

    private fun removeFragment(activity:FragmentActivity?, fragment: Fragment?){
        fragment ?: return
        val backStackEntryCount = activity?.supportFragmentManager?.backStackEntryCount ?: 0
        try {
            if (backStackEntryCount.isGreaterThan(0) && activity?.supportFragmentManager?.getBackStackEntryAt(backStackEntryCount-1)?.name.equals(fragment::class.java.simpleName)) {
                activity?.supportFragmentManager?.popBackstackSafe()
                return
            } else {
                activity?.supportFragmentManager?.beginTransaction()?.remove(fragment)?.commitAllowingStateLoss()
            }
        } catch (e: Exception) {
            val fragmentList = mutableListOf<String?>()
            for(i in 0 until backStackEntryCount) {
                fragmentList.add(activity?.supportFragmentManager?.getBackStackEntryAt(i)?.name)
            }
            e.printStackTrace()
        }
    }
}