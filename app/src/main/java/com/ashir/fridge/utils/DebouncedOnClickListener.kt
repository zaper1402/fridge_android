package com.ashir.fridge.utils

import android.os.SystemClock
import android.view.View
import com.threemusketeers.dliverCustomer.main.network.NetworkStateManager.isConnectedToInternet

/**
 * A Debounced OnClickListener
 * Rejects clicks that are too close together in time.
 */
abstract class DebouncedOnClickListener : View.OnClickListener {
    private var threshold: Long
    private var lastClickMillis: Long = 0

    /**
     * Implement this in your subclass instead of onClick
     * @param v The view that was clicked
     */
    abstract fun onDebouncedClick(v: View?)

    constructor() {
        threshold = DEFAULT_CLICK_THRESHOLD
    }

    /**
     * @param threshold The minimum allowed time between clicks - any click sooner than this after a previous click will be rejected
     */
    constructor(threshold: Long) {
        this.threshold = threshold
    }

    override fun onClick(clickedView: View) {
        val currentTimestamp = SystemClock.uptimeMillis()

        if ((currentTimestamp - lastClickMillis > threshold)) {
            onDebouncedClick(clickedView)
            lastClickMillis = currentTimestamp
        }
    }


    companion object {
        const val DEFAULT_CLICK_THRESHOLD: Long = 400L

        fun wrap(
            threshold: Long,
            isHandleInternet: Boolean,
            onClickListener: View.OnClickListener,
            tryAgainCTA: Boolean
        ): View.OnClickListener {
            return object : DebouncedOnClickListener(threshold) {
                override fun onDebouncedClick(v: View?) {
                    if (isHandleInternet && !isConnectedToInternet) {
//                    ViewUtils.INSTANCE.showCustomSnackBar(v.getContext(), "No Internet connection", tryAgainCTA?"Try Again".toUpperCase():null , R.drawable.ic_no_wifi, onClickListener, null, false, null, null);
                    } else {
                        onClickListener.onClick(v)
                    }
                }
            }
        }
    }
}