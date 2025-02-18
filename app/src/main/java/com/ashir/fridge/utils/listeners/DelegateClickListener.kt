package com.ashir.fridge.utils.listeners

import com.ashir.fridge.utils.IModel

interface DelegateClickListener {
    fun onClick(iModel: IModel?, position: Int, otherData: Any?)
}
