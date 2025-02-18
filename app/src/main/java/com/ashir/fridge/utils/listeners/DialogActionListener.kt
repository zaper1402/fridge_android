package com.threemusketeers.myapplication.main.utils.listeners

interface DialogActionListener {
    fun onDialogActionTaken(action: String, data: Any? = null)
}

enum class StagProdDialogActions{
    DISMISS,
    LOCALHOST_CLICKED,
    STAGING_CLICKED,
    PRODUCTION_CLICKED
}