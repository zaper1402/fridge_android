package com.ashir.fridge.utils

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Process
import android.provider.Settings
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.common.util.CollectionUtils
import com.threemusketeers.dliverCustomer.main.utils.extensions.floorTo
import com.threemusketeers.dliverCustomer.main.utils.extensions.getAppContext
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.io.InputStream
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale


object Utils {

    var scaledDensityMultiplier = 1.0f
    var densityMultiplier = 1.0f
    var densityDpi = 0
    var displayWidthPixels = 0
    var displayHeightPixels = 0
    private var appVersionName: String? = null
    private var appVersionCode: Int? = null
    private var uiHandler: Handler = Handler(Looper.getMainLooper())
    const val TAG = "Utils"
    private var areOverallNotifsEnabled = true


    fun displayWidthDp() = pxToDp(displayWidthPixels)

    fun displayHeightDp() = pxToDp(displayHeightPixels)

    /**
     * Used for setting the density multiplier, which is to be multiplied with any pixel value that is programmatically given
     *
     * @param displayMetrics
     */
    fun setDensityMultiplier(displayMetrics: DisplayMetrics) {
        scaledDensityMultiplier = displayMetrics.scaledDensity
        densityDpi = displayMetrics.densityDpi
        densityMultiplier = displayMetrics.density
        displayWidthPixels = displayMetrics.widthPixels
        displayHeightPixels = displayMetrics.heightPixels
    }

    fun addressWrapper(
        address: String?,
        city: String?,
        state: String?,
        country: String?
    ): String {
        return listOfNotNull(address, city, state, country)
            .filter { it.isNotBlank() }
            .joinToString(separator = ", ")
    }


    fun dpToPx(dp: Float): Int {
        return (dp * densityMultiplier).toInt()
    }

    fun dpToPx(dp: Int): Int {
        return (dp * densityMultiplier).toInt()
    }

    fun pxToDp(px: Int): Float {
        return px / densityMultiplier
    }


    fun cloneJSONObject(jsonObject: JSONObject?): JSONObject? {
        if (jsonObject == null) {
            return null
        }
        //To prevent concurrent modifications during map iteration that happens during stringify. This is not a deep copy but it serves our purpose as sub objects are not being modified.
        val keys: Array<String?>? = getStringArray(jsonObject.names())

        // create a new copy
        var cloneJson: JSONObject? = null
        try {
            cloneJson = keys?.let { JSONObject(jsonObject, it) } ?: jsonObject
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return cloneJson
    }

    private fun getStringArray(jsonArray: JSONArray?): Array<String?>? {
        var stringArray: Array<String?>? = null
        if (jsonArray != null) {
            val length = jsonArray.length()
            stringArray = arrayOfNulls(length)
            for (i in 0 until length) {
                stringArray[i] = jsonArray.optString(i)
            }
        }
        return stringArray
    }

    fun stripSpecialCharacters(str:String?):String? {
        str ?: return null
        val regex = Regex("[^A-Za-z0-9]")
        val updatedStr = regex.replace(str,"");
        return updatedStr
    }


    fun getGooglePlayServiceVersion(): String? {
        return try {
            getAppContext().packageManager.getPackageInfo("com.google.android.gms", 0).versionCode.toString() + ""
        } catch (e: Exception) {
            ""
        }
    }





    fun getDeviceResolution(): String {
        val density: Int = getAppContext().getResources().getDisplayMetrics().densityDpi
        return when (density) {
            DisplayMetrics.DENSITY_LOW -> "LDPI"
            DisplayMetrics.DENSITY_MEDIUM -> "MDPI"
            DisplayMetrics.DENSITY_HIGH -> "HDPI"
            DisplayMetrics.DENSITY_XHIGH -> "XHDPI"
            DisplayMetrics.DENSITY_XXHIGH -> "XXHDPI"
            DisplayMetrics.DENSITY_XXXHIGH -> "XXXHDPI"
            else -> "XXHDPI"
        }
    }

    fun showHideKeyboard(view: View?, isShow: Boolean, delay: Long = 0) {
        view ?: return
        val imm = getAppContext().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (delay > 0) {
            uiHandler.postDelayed({
                if (isShow) {
                    imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
                } else {
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                }
            }, delay)
        } else {
            if (isShow) {
                imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
            } else {
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }
    }

    fun showHideKeyboardWithFocus(view: View?, isShow: Boolean, delay: Long = 0) {
        view ?: return
        if (!view.hasFocus() && isShow) {
            view.requestFocus()
        } else if (!isShow) {
            view.clearFocus()
        }
        showHideKeyboard(view, isShow, delay)
    }

    fun getDayNumberSuffix(day: Int): String {
        return if (day in 11..13) {
            "th"
        } else when (day % 10) {
            1 -> "st"
            2 -> "nd"
            3 -> "rd"
            else -> "th"
        }
    }

    fun getCommaSeparatedINR(num: Double?, numFractionDigits: Int=2) : String {
        num ?: return "0"
        return try{
            if (num % 1.0 == 0.0) {  // if double is a whole number
                val df = DecimalFormat("##,##,###")
                df.format(num.toInt())
            } else {
                val df = DecimalFormat("##,##,###.##")
                df.format(num.floorTo(numFractionDigits))
            }
        } catch (exception: Exception) {
            num.toInt().toString()
        }
    }



    fun killProcess(context: Context?, process: String) {
        if (context != null) {
            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val procInfos = activityManager.runningAppProcesses
            if (!CollectionUtils.isEmpty(procInfos)) {
                for (i in procInfos.indices) {
                    if (procInfos[i].processName == process) {
                        val pid = procInfos[i].pid
                        Process.killProcess(pid)
                    }
                }
            }
        }
    }

    /*
	 * This function is called to delete a particular file from the System
	 *
	 * @param filePath : The complete file path of the file that is about to be deleted returns whether the file is deleted or not Does not return a guaranteed call for a full
	 * delete
	 */
    fun deleteDirectory(filePath: String): Boolean {
        val deletedDir = File(filePath)
        return if (deletedDir.exists()) {
            val isDeleted: Boolean = deleteOp(deletedDir)
            Log.d("FileSystemAccess", "Directory exists!")
            Log.d("FileSystemAccess", if (isDeleted) "File is deleted" else " File not deleted")
            isDeleted
        } else {
            Log.d("FileSystemAccess", "Invalid file path!")
            false
        }
    }

    // This method performs the actual deletion of the file
    fun deleteOp(dir: File): Boolean {
        Log.d("FileSystemAccess", "In delete")
        if (dir.exists()) { // This checks if the file/folder exits or not
            if (dir.isDirectory) // This checks if the call is made to delete a particular file (eg. "index.html") or an entire sub-folder
            {
                val children = dir.list()
                if (children != null && children.size > 0) {
                    for (i in children.indices) {
                        val temp = File(dir, children[i])
                        if (temp.isDirectory) {
                            Log.d("DeleteRecursive", "Recursive Call" + temp.path)
                            deleteOp(temp)
                        } else {
                            Log.d("DeleteRecursive", "Delete File" + temp.path)
                            val b = temp.delete()
                            if (!b) {
                                Log.d("DeleteRecursive", "DELETE FAIL")
                                return false
                            }
                        }
                    }
                }
                dir.delete()
            } else {
                dir.delete()
            }
            Log.d("FileSystemAccess", "Delete done!")
            return true
        }
        return false
    }


    fun getNavigationBarHeight(context: Context?): Int {
        try {
            context?.let {
                val resourceId: Int = it.resources.getIdentifier("navigation_bar_height", "dimen", "android")
                if (resourceId > 0) {
                    return it.resources.getDimensionPixelSize(resourceId)
                }
            }
        } catch (e: IllegalArgumentException) {
        }
        return 0
    }

    fun getAppVersionName(): String {
        if (appVersionName != null) return appVersionName!!
        var versionName: String? = ""
        try {
            versionName = getAppContext().packageManager.getPackageInfo(getAppContext().packageName, 0).versionName
            appVersionName = versionName
        } catch (e: Exception) {
        }
        return versionName!!
    }

    fun getAppVersionCode(): Int {
        if (appVersionCode != null) return appVersionCode!!
        var versionCode = "1"
        try {
            versionCode = getAppContext().packageManager.getPackageInfo(getAppContext().packageName, 0).versionCode.toString()
            appVersionCode = versionCode.toInt()
        } catch (e: Exception) {
        }
        return versionCode.toInt()
    }

    fun getStatusBarHeight(): Int {
        val context = getAppContext()
        var statusBarHeight = 0
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            statusBarHeight = context.resources.getDimensionPixelSize(resourceId)
        }
        return statusBarHeight
    }

//    fun shareToOtherAppsWithReferralInfo(activity: Activity?){
//        var desc:String? = ""
//        var deeplinks: List<DeeplinkInfo>? = null
//        if (AppLanguageManager.isSelectedLanguageEnglish()) {
//            desc = ApiResponseCacheManager.rafMainApiResponse?.referralInfoEnglish?.referralInfo?.desc
//            deeplinks = ApiResponseCacheManager.rafMainApiResponse?.referralInfoEnglish?.referralInfo?.deeplinks
//        }
//        else {
//            desc = ApiResponseCacheManager.rafMainApiResponse?.referralInfoHindi?.referralInfo?.desc
//            deeplinks = ApiResponseCacheManager.rafMainApiResponse?.referralInfoHindi?.referralInfo?.deeplinks
//        }
//        val shareOthersDeeplink = deeplinks?.filter { it.type == DeeplinkType.OTHERS.value }?.getSafe(0)?.deepLink
//        Utils.shareToOtherApps("$desc $shareOthersDeeplink", AppConstants.REFERRAL_EMAIL_SUBJECT, activity)
//        val props = HashMap<String, Any>()
//        props["raf_channel"] = "others"
//        ClevertapManager.pushEvent(ClevertapConstants.REFERRED_A_FRIEND, props)
//    }

//    fun shareToOtherApps(text: String?, emailSubject: String?, context: Activity?) {
//        context ?: return
//        text ?: return
//        try {
//            val shareIntent = Intent(Intent.ACTION_SEND)
//            shareIntent.type = "text/plain"
//            shareIntent.putExtra(Intent.EXTRA_TEXT, text)
//            shareIntent.putExtra(Intent.EXTRA_SUBJECT, emailSubject)
//            context.startActivity(Intent.createChooser(shareIntent, "choose one"))
//        } catch (e: Throwable) { // In some devices we get remoteexceptions n other errors as well.
//            Log.d(TAG, "Generic sharing failed")
//        }
//    }

//    fun shareToWhatsApp(text: String?, context: Activity?): Int {
//        context ?: return 0
//        text ?: return 0
//        val whatsAppIntent = Intent(Intent.ACTION_SEND)
//        whatsAppIntent.type = "text/plain"
//        whatsAppIntent.setPackage("com.whatsapp")
//        whatsAppIntent.putExtra(Intent.EXTRA_TEXT, text)
//        return try {
//            context.startActivity(whatsAppIntent)
//            1
//        } catch (ex: Throwable) { // In some devices we get remoteexceptions n other errors as well.
//            Log.d(TAG, "WhatsApp sharing failed")
//            -1
//        }
//    }

//    fun composeEmail(address: String?, subject: String?, body: String?, context: Activity?) {
//        context ?: return
//        val selectorIntent = Intent(Intent.ACTION_SENDTO)
//        selectorIntent.data = Uri.parse("mailto:")
//        val emailIntent = Intent(Intent.ACTION_SEND)
//        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(address))
//        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
//        emailIntent.putExtra(Intent.EXTRA_TEXT, body)
//        emailIntent.selector = selectorIntent
//        try {
//            context.startActivity(Intent.createChooser(emailIntent, "Send email..."))
//        } catch (ex: Throwable) { // In some devices we get remoteexceptions n other errors as well.
//            Log.d(TAG, "Email failed")
//        }
//    }

//    fun getExternalFilesDirectoryPath(): String {
//        val pathFromPref = defaultPrefs().getData(AppConstants.Companion.SharedPrefConstants.EXTERNAL_FILES_DIRECTORY_PATH, "")
//        return pathFromPref.ifBlank {
//            val dirPath = validateAndGetPath()
//            defaultPrefs().saveData(AppConstants.Companion.SharedPrefConstants.EXTERNAL_FILES_DIRECTORY_PATH, dirPath)
//            dirPath
//        }
//    }

    private fun validateAndGetPath(): String {
        val externalPath = getAppContext().getExternalFilesDir(null).toString()
        return try {
            val file = File("$externalPath/sample.txt")
            if (!file.exists()) {
                file.createNewFile()
                file.delete()
            }
            externalPath
        } catch (e: java.lang.Exception) {
            getAppContext().filesDir.toString()
        }
    }

    fun readFromFileInAssetFolder(context: Context, fileName: String): String? {
        var data: String? = null
        var inputStream: InputStream? = null
        try {
            inputStream = context.assets.open(fileName)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            data = String(buffer, Charsets.UTF_8)
        } catch (ex: IOException) {
            ex.printStackTrace()
        } finally {
            inputStream?.close()
        }
        return data
    }

    fun writeToFile(context: Context, jsonString: String?, fileName: String) : Boolean {
        var writer: FileWriter? = null
        var success = false
        try {
            val rootFolder = context.getExternalFilesDir(null)
            val file = File(rootFolder, fileName)
            writer = FileWriter(file)
            writer?.write(jsonString)
            success = true
        } catch (ex: Exception) {
            ex.printStackTrace()
        } finally {
            try {
                writer?.close()
            } catch (e: Exception) {
                Log.d(TAG, "Exception in closing file writer")
            }
        }
        return success
    }

    fun readFromFile(context: Context, fileName: String): String? {
        var bufferedReader: BufferedReader? = null
        val stringBuilder = StringBuilder()
        try {
            val file = File(context.getExternalFilesDir(null), fileName)
            val fileReader = FileReader(file)
            bufferedReader = BufferedReader(fileReader)
            var line: String? = bufferedReader?.readLine()
            while (!TextUtils.isEmpty(line)) {
                stringBuilder.append(line).append("\n")
                line = bufferedReader?.readLine()
            }
        } catch (ex: java.lang.Exception) {
            ex.printStackTrace()
            stringBuilder.clear()
        } finally {
            bufferedReader?.close()
        }
        return stringBuilder.toString()
    }

    fun isRunningOnMainThread(): Boolean {
        return Thread.currentThread() == Looper.getMainLooper().thread
    }

    fun allPermissionsGranted(activity: FragmentActivity, permissions: Array<String>) = permissions.all { permission ->
        ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
    }

//    @SuppressLint("ClickableViewAccessibility")
//    fun showPopupTip(
//        anchorView: View,
//        bubbleText: String,
//        bubbleTextCharSequence: CharSequence? = null,
//        bubbleTextColor: Int? = ThemesManager.currentTheme?.getBgColorData()?.primary?.getColor(),
//        tipHeight: Int,
//        tipWidth: Int,
//        tipMarginEnd: Int,
//        popupWidth: Int,
//        dismissOverlay: FrameLayout,
//        textPaddingStart:Int =14,
//        textPaddingEnd:Int =18,
//        textPaddingBottom:Int =11,
//        textPaddingTop:Int =15,
//        tipImageUrl: String? = null,
//        tipImageColor:Int? = null,
//        tipColor:Int? = null,
//        containerMarginStart: Int? = 0,
//        tipBackgroundColour: GradientDrawable? = ThemesManager.currentTheme?.getBgColorData()?.accent?.getDrawable(6),
//        onDismissListener:PopupWindow.OnDismissListener?=null
//    ): PopupWindow {
//        val layout  : PopupTipBinding = DataBindingUtil.inflate(LayoutInflater.from(getAppContext()), R.layout.popup_tip,null,false)
//        layout.currentTheme = ThemesManager.currentTheme
//        if (tipImageUrl != null) {
//            layout.tooltipIv.loadImage(tipImageUrl)
//            layout.tooltipIv.setVisible()
//        } else {
//            layout.tooltipIv.setGone()
//        }
//        layout.tooltipText.layoutParams = (layout.tooltipText.layoutParams as? ConstraintLayout.LayoutParams)?.apply {
//            if (layout.tooltipIv.visibility == View.VISIBLE) {
//                width = popupWidth - layout.tooltipIv.layoutParams.width - 15.toPx()    // This is icon margin
//                marginStart = 17.toPx()     // This is the default margin bw icon and text
//                marginEnd = 17.toPx()       // This is the default margin bw text and end of popup
//            } else {
//                layout.tooltipText.width = popupWidth
//            }
//        }
//        if (bubbleTextCharSequence.isNotBlankOrEmpty()) {
//            layout.tooltipText.text = bubbleTextCharSequence
//            layout.tooltipText.movementMethod = LinkMovementMethod.getInstance()
//        } else {
//            layout.tooltipText.text = bubbleText.defaultValueIfNull()
//        }
//        layout.tooltipText.setPadding(dpToPx(textPaddingStart), dpToPx(textPaddingTop), dpToPx(textPaddingEnd), dpToPx(textPaddingBottom))
//        tipImageColor?.let {
//            layout.tooltipIv.setColorFilter(it)
//        }
//        tipColor?.let {
//            layout.tooltipTip.setColorFilter(it)
//        }
//        tipBackgroundColour?.let {
//            layout.tipContainer.background = tipBackgroundColour
//        }
//
//        layout.root.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
//        bubbleTextColor?.let{
//            layout.tooltipText.setTextColor(it)
//        }
//        layout.tooltipTip.layoutParams.height = tipHeight.toPx()
//        layout.tooltipTip.layoutParams.width = tipWidth.toPx()
//        (layout.tooltipTip.layoutParams as? ConstraintLayout.LayoutParams)?.let{
//            val lp = it
//            lp.height = tipHeight.toPx()
//            lp.width = tipWidth.toPx()
//            lp.marginEnd = tipMarginEnd.toPx()
//            layout.tooltipTip.layoutParams = lp
//        }
//        val params: ConstraintLayout.LayoutParams = layout.tipContainer.layoutParams as ConstraintLayout.LayoutParams
//        params.marginStart = containerMarginStart.toPx()
//        layout.tooltipTip.requestLayout()
//        val popup = PopupWindow(
//            layout.root,
//            popupWidth,
//            FrameLayout.LayoutParams.WRAP_CONTENT,
//            true
//        )
//        if(onDismissListener!=null) {
//            popup.setOnDismissListener(onDismissListener)
//        }
//        popup.showAsDropDown(
//            anchorView,
//            -(popupWidth - anchorView.width/2 - (tipWidth.toPx()/2) - tipMarginEnd.toPx()),
//            -(layout.root.measuredHeight + anchorView.height)
//        )
//        dismissOverlay.setVisible()
//        dismissOverlay.setOnTouchListener { v, event ->
//            when (event?.action) {
//                MotionEvent.ACTION_DOWN -> {
//                    popup.dismiss()
//                    dismissOverlay.setGone()
//                }
//                MotionEvent.ACTION_UP -> {
//                    popup.dismiss()
//                    dismissOverlay.setGone()
//                }
//                MotionEvent.ACTION_CANCEL -> {
//                    popup.dismiss()
//                    dismissOverlay.setGone()
//                }
//            }
//            false
//        }
//        layout.root.setOnDebouncedClickListener {
//            popup.dismiss()
//            dismissOverlay.setGone()
//        }
//        return popup
//    }




//    @SuppressLint("ClickableViewAccessibility")
//    fun showVideoPopupTip(
//        anchorView: View,
//        headerText: String?,
//        bannerImg: String?,
//        videoPopup: VideoPopup?,
//        dismissOverlay: FrameLayout?,
//        tipMarginEnd: Int,
//        onDismissListener: PopupWindow.OnDismissListener ?= null,
//        showasDropdown : Boolean = false,
//        source : String? = null,
//        activity: WeakReference<FragmentActivity>? = null
//    ): PopupWindow {
//        val layout  : VideoPopupTipBinding = DataBindingUtil.inflate(LayoutInflater.from(getAppContext()), R.layout.video_popup_tip,null,false)
//        val toolTip = if(showasDropdown) layout.tooltipTipUp else layout.tooltipTip
//        toolTip.setVisible()
//        layout.parent.layoutTransition = LayoutTransition()
//        layout.currentTheme = ThemesManager.currentTheme
//        layout.bubbleContainer.background = ThemesManager.currentTheme?.getBgColorData()?.accentG?.getColorList()?.let {
//            ViewUtils.getGradientDrawable(it, orientation = GradientDrawable.Orientation.LEFT_RIGHT, cornerRadius = 16, strokeColor = -1)
//        }
//        toolTip.setColorFilter(Color.parseColor("#5128B8"))
//        if(headerText.isNotBlankOrEmpty()){
//            layout.header.text = headerText
//            ThemesManager.currentTheme?.getButtonProfileData()?.buttonP5?.let {
//                layout.header.setGradientHorizontal(it.getColorList().toIntArray())
//            }
//        }else {
//            layout.header.setGone()
//        }
//        if (bannerImg != null) {
//            layout.banner.loadImage(bannerImg)
//        } else {
//            layout.banner.setGone()
//        }
//        (toolTip.layoutParams as? ConstraintLayout.LayoutParams)?.let{
//            val lp = it
//            lp.marginEnd = tipMarginEnd
//            toolTip.layoutParams = lp
//        }
//        toolTip.requestLayout()
//        layout.root.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
//        val popup = PopupWindow(
//            layout.root,
//            layout.root.measuredWidth,
//            FrameLayout.LayoutParams.WRAP_CONTENT,
//            true
//        )
//        if(onDismissListener!=null) {
//            popup.setOnDismissListener(onDismissListener)
//        }
//        popup.showAsDropDown(
//            anchorView,
//            -(layout.root.measuredWidth - anchorView.width/2 - toolTip.measuredWidth/2 - tipMarginEnd - 20.toPx()),
//            if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.M){
//                0
//            }else {
//                if(showasDropdown) -layout.tooltipTipUp.marginTop else -(layout.root.measuredHeight + anchorView.height -layout.tooltipTip.marginBottom)
//            },
//        )
//        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.M){
//            if(popup.isAboveAnchor) {
//                layout.tooltipTipUp.setGone()
//                layout.tooltipTip.setVisible()
//                layout.tooltipTip.setColorFilter(Color.parseColor("#5128B8"))
//                (layout.tooltipTip.layoutParams as? ConstraintLayout.LayoutParams)?.let{
//                    val lp = it
//                    lp.marginEnd = tipMarginEnd
//                    lp.bottomMargin = 0
//                    layout.tooltipTip.layoutParams = lp
//                }
//            }else {
//                layout.tooltipTip.setGone()
//                layout.tooltipTipUp.setVisible()
//                layout.tooltipTipUp.setColorFilter(Color.parseColor("#5128B8"))
//                (layout.tooltipTipUp.layoutParams as? ConstraintLayout.LayoutParams)?.let{
//                    val lp = it
//                    lp.marginEnd = tipMarginEnd
//                    lp.topMargin = 0
//                    layout.tooltipTipUp.layoutParams = lp
//                }
//            }
//        }
//        layout.banner.setOnDebouncedClickListener {
//            videoPopup?.let{
//                VideoPopupDialog.newInstance(it,false,source, object :GenericDialogListener{
//                    override fun onPositiveBtnClicked(data: JSONObject?) {
//                        if (it.deeplink.isNullOrBlank().not()) {
//                            DeepLinkManager.fireDeeplinkOrWeblinkIfValid(it.deeplink)
//                        }
//                        super.onPositiveBtnClicked(data)
//                    }
//                }).showSafe(activity?.get()?.supportFragmentManager)
//            }
//            popup.dismiss()
//            dismissOverlay?.setGone()
//        }
//        dismissOverlay?.setVisible()
//        dismissOverlay?.setOnTouchListener { v, event ->
//            when (event?.action) {
//                MotionEvent.ACTION_DOWN -> {
//                    popup.dismiss()
//                    dismissOverlay.setGone()
//                }
//                MotionEvent.ACTION_UP -> {
//                    popup.dismiss()
//                    dismissOverlay.setGone()
//                }
//                MotionEvent.ACTION_CANCEL -> {
//                    popup.dismiss()
//                    dismissOverlay.setGone()
//                }
//            }
//            false
//        }
//        layout.bubbleContainer.setOnDebouncedClickListener {
//            popup.dismiss()
//            dismissOverlay?.setGone()
//        }
//        return popup
//    }

    fun gotoAppSettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", getAppContext().packageName, null)
            intent.data = uri
            startActivity(context, intent, null)
        } catch (e: Exception) {
            Log.d(TAG, "Could not redirect to app settings")
        }
    }


    fun canRequestPackageInstalls():Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return getAppContext().packageManager.canRequestPackageInstalls()
        }
        return true
    }

    //Method copied from backend code
    fun compareVersion(version1: String, version2: String): Int {
        val comparableVersion1: String = getComparableVersion(version1)
        val comparableVersion2: String = getComparableVersion(version2)
        val version1_parts = comparableVersion1.split("\\.".toRegex()).toTypedArray()
        val version2_parts = comparableVersion2.split("\\.".toRegex()).toTypedArray()
        val count =
            if (version1_parts.size > version2_parts.size) version2_parts.size else version1_parts.size
        val return_value =
            if (version1_parts.size == version2_parts.size) 0 else if (version1_parts.size > version2_parts.size) 1 else -1
        var v1: Long
        var v2: Long
        for (i in 0 until count) {
            v1 = getVersion(version1_parts[i])
            v2 = getVersion(version2_parts[i])
            return if (v1 < v2) -1 else if (v1 > v2) 1 else continue
        }
        return return_value
    }

    private fun getComparableVersion(version: String): String {
        var comparableVersion = ""
        if (TextUtils.isEmpty(version)) return comparableVersion
        val version_parts = version.split("\\.".toRegex()).toTypedArray()
        for (v in version_parts) {
            if (v.matches("\\d+".toRegex())) comparableVersion = "$comparableVersion$v."
        }
        return if (comparableVersion.endsWith(".")) comparableVersion.substring(
            0,
            comparableVersion.length - 1
        ) else comparableVersion
    }

    private fun getVersion(version: String): Long {
        if (!TextUtils.isEmpty(version)) {
            try {
                return version.toLong()
            } catch (nfe: NumberFormatException) {
            }
        }
        return -1
    }

    fun isAppVersionGreater(version: String, versionToCompareWith: String): Boolean {
        return compareVersion(version, versionToCompareWith) == 1
    }

    fun isAppVersionLower(version: String, versionToCompareWith: String): Boolean {
        return compareVersion(version, versionToCompareWith) == -1
    }

    fun areOverallNotificationsEnabled(): Boolean {
        return areOverallNotifsEnabled
    }

    fun setOverallNotificationsEnabledState(notifEnabledState:Boolean? = null) {
        if(notifEnabledState != null) {
            areOverallNotifsEnabled = notifEnabledState
        } else {
            areOverallNotifsEnabled = NotificationManagerCompat.from(getAppContext()).areNotificationsEnabled()
        }
    }

//    fun copyTextToClipBoard(copiedText: String?, activity: Activity) {
//        val clipboard = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
//        val clip: ClipData = ClipData.newPlainText("simple text", copiedText)
//        clipboard.setPrimaryClip(clip)
//        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
//            ToastUtils.showToastShort(activity.getString(R.string.copied))
//        }
//    }

     fun isValidDate(dateStr: String?): Boolean {
        return try {
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(dateStr)
            true
        } catch (e: ParseException) {
            false
        }
    }

    fun showDatePickerDialog(
        context: Context,
        dateSetListener: TextView?,
        ) {
        val calendar = java.util.Calendar.getInstance()
        val currentYear = calendar.get(java.util.Calendar.YEAR)
        val currentMonth = calendar.get(java.util.Calendar.MONTH)
        val currentDay = calendar.get(java.util.Calendar.DAY_OF_MONTH)
        val datePickerDialog = android.app.DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val monthStr = if (month + 1 < 10) "0${month + 1}" else "${month + 1}"
                val dayStr = if (dayOfMonth < 10) "0$dayOfMonth" else "$dayOfMonth"
                dateSetListener?.text = "$dayStr/$monthStr/$year"
            },
            currentYear,
            currentMonth,
            currentDay
        )
        datePickerDialog.show()
    }

    fun showDateFromTime(time: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(time)
    }
}
