package com.threemusketeers.dliverCustomer.main.utils

import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.Shader
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.GradientDrawable.Orientation
import android.os.Build
import android.provider.MediaStore
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StrikethroughSpan
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.ashir.fridge.utils.DebouncedOnClickListener
import com.ashir.fridge.utils.Utils
import com.threemusketeers.dliverCustomer.main.utils.extensions.getAppContext
import com.threemusketeers.dliverCustomer.main.utils.extensions.isActivitySafe
import com.threemusketeers.dliverCustomer.main.utils.extensions.setVisible


/**
 * Created by Harminder Singh on 12/12/20.
 */
object ViewUtils {

    const val TAG = "ViewUtils"
    private var animScale: Float = -1f

    // This is for setting Light button touch feedback in the app
    fun addFeedbackLight(vararg view:View?){
        view.iterator().forEach {
            it?.setOnTouchListener { p0, p1 ->
                when (p1?.action) {
                    MotionEvent.ACTION_DOWN -> {
                        p0?.alpha = 0.4f
                    }

                    MotionEvent.ACTION_UP -> {
                        p0?.alpha = 1f
                    }

                    MotionEvent.ACTION_CANCEL -> {
                        p0?.alpha = 1f
                    }
                }
//                it.performClick()
                false
            }
        }
    }

    fun removeFeedbackLight(vararg view:View?) {
        for(eachView in view) {
            eachView?.setOnTouchListener(null)
        }
    }

    fun debounceClick(view: View, clickListener: View.OnClickListener) {
        debounceClick(view, DebouncedOnClickListener.DEFAULT_CLICK_THRESHOLD, isHandleInternet = false, tryAgainCTA = true, clickListener)
    }

    fun debounceClickWithInternet(view: View, clickListener: View.OnClickListener) {
        debounceClick(view, DebouncedOnClickListener.DEFAULT_CLICK_THRESHOLD, isHandleInternet = true, tryAgainCTA = true , clickListener)
    }

    fun debounceClick(view: View, threshold: Long = DebouncedOnClickListener.DEFAULT_CLICK_THRESHOLD, isHandleInternet: Boolean = false, tryAgainCTA: Boolean = true, clickListener: View.OnClickListener ) {
        if (isNonNull(view)) {
            view.setOnClickListener(DebouncedOnClickListener.wrap(threshold, isHandleInternet, clickListener, tryAgainCTA))
        }
    }

    fun debounceClick(views:List<View?>, threshold: Long = DebouncedOnClickListener.DEFAULT_CLICK_THRESHOLD, isHandleInternet: Boolean = false, clickListener: View.OnClickListener){
        for(view in views){
            if (view != null) {
                debounceClick(view,threshold,isHandleInternet,true, clickListener)
            }
        }
    }

    private fun getColorInternal(colorString: String): Int? {
        try {
            return Color.parseColor(colorString)  // when error string is passed returns null
        } catch (e: Exception) {
            return null
        }
    }

    fun isNonNull(obj: Any?): Boolean {
        return obj != null
    }

    fun getColorFromAlpha(colorCode: String, percentage: Int): Int? {
        if(percentage > 100 || percentage < 0) return null

        val decValue = percentage.toDouble() / 100 * 255
        val rawHexColor = colorCode.replace("#", "")
        val str = StringBuilder(rawHexColor)
        if (Integer.toHexString(decValue.toInt()).length == 1) str.insert(0, "#0" + Integer.toHexString(decValue.toInt())) else str.insert(0, "#" + Integer.toHexString(decValue.toInt()))
        return getColorInternal(str.toString())
    }

    /*
    * This method handles both without pattern (#FFFFFF) and pattern strings (#6143B5,80)
    * When format is like #6143B5,90 ; this splits based on comma n gives color as #6143B5 & alpha as 90% & applies alpha to color n returns correct color
    * */
    fun getColor(pattern: String?): Int? {
        pattern ?: return null
        val splitList = pattern.split(",")
        if (splitList.size == 1 || splitList.size == 2) {
            if (splitList.size == 1) {
                return getColorInternal(splitList[0].trim())
            } else {
                try {
                    val colorFromAlpha = getColorFromAlpha(splitList[0].trim(), splitList[1].trim().toInt())
                    return colorFromAlpha
                } catch (e: Exception) {
                    return null
                }
            }
        } else {
            return null
        }
    }

    private fun getBackgroundGradient(startColor: Int?, endColor: Int?, cornerRadius: Int?, orientation: Orientation = Orientation.TOP_BOTTOM): GradientDrawable? {
        startColor ?: return null
        endColor ?: return null
        val drawable = GradientDrawable(orientation,
                intArrayOf(startColor, endColor))
        cornerRadius?.let {
            drawable.cornerRadius = Utils.dpToPx(it.toFloat()).toFloat()
        }
        return drawable
    }

    private fun getBackgroundGradient(startColor: Int?, endColor: Int?, cornerRadius: Int?, orientation: Orientation = Orientation.TOP_BOTTOM, strokeWidth: Int, strokeColor: Int): GradientDrawable? {
        startColor ?: return null
        endColor ?: return null
        val drawable = GradientDrawable(orientation,
                intArrayOf(startColor, endColor))
        cornerRadius?.let {
            drawable.cornerRadius = Utils.dpToPx(it.toFloat()).toFloat()
            drawable.setStroke(strokeWidth, strokeColor)
        }
        return drawable
    }

    private fun setVisibility(view: View?, visibility: Int) {
        if (view != null && view.visibility != visibility) {
            view.visibility = visibility
        }
    }

    fun setGone(vararg views: View?) {
        for (view in views) {
            setVisibility(view, View.GONE)
        }
    }

    fun setVisibleView(vararg views: View?) {
        for (view in views) {
            setVisibility(view, View.VISIBLE)
        }
    }

    fun setInVisible(vararg views: View?) {
        for (view in views) {
            setVisibility(view, View.INVISIBLE)
        }
    }

    fun isViewVisible(view: View): Boolean = view.visibility == View.VISIBLE

    /*
    If exactText is passed, this will color the exactText. If startingWord is passed along with endingWord, then it will color
    from startingWord to endingWord. If endingWord is not passed then it will color either upto next space or entire line
    depending on value of endWithSpace.
    */
    fun setWordColorInString(str: String?, exactText: String?=null, startingWord: String?=null, endingWord: String? = null, endWithSpace: Boolean = false, color: Int?): SpannableString {
        if (str.isNullOrEmpty()) return SpannableString("")
        val spannableString = SpannableString(str)
        if (exactText != null) {
            if (str.contains(exactText)) {
                val si = str.indexOf(exactText)
                spannableString.setSpan(color?.let { ForegroundColorSpan(it) }, si, si + exactText.length, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
            }
        } else if (startingWord != null) {
            if (str.contains(startingWord)) {
                val si = str.indexOf(startingWord)
                if (endingWord != null) {
                    val ei = str.indexOf(endingWord)
                    spannableString.setSpan(color?.let { ForegroundColorSpan(it) }, si, ei + endingWord.length, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
                } else if (endWithSpace) {
                    val spaceIndex = str.substring(str.indexOf(startingWord)).indexOf(' ')
                    if (spaceIndex == -1 || spaceIndex >= str.length) {
                        spannableString.setSpan(color?.let { ForegroundColorSpan(it) }, si, str.length, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
                    } else {
                        spannableString.setSpan(color?.let { ForegroundColorSpan(it) }, si, si + spaceIndex, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
                    }
                } else {
                    spannableString.setSpan(color?.let { ForegroundColorSpan(it) }, si, str.length, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
                }
            }
        }
        return spannableString
    }

    /**
     * @param fullText: original text/string
     * @param clickableTextStartIdx: clickable text start index
     * @param clickableTextEndIdx: clickable text end index
     * @param clickableTextColor: clickable text color
     * @param OnClick: lambda which invokes when clicked
     */
    fun TextView.makeTextClickableWithUnderline(fullText: String?, clickableTextStartIdx: Int?, clickableTextEndIdx: Int?, clickableTextColor: Int, OnClick: (v: View) -> Unit) {
        if (fullText == null || clickableTextStartIdx == null || clickableTextEndIdx == null) return
        val str = SpannableString(fullText)
        str.setSpan(object : ClickableSpan() {
            override fun onClick(v: View) {
                OnClick.invoke(v)
            }
        }, clickableTextStartIdx, clickableTextEndIdx, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        str.setSpan(ForegroundColorSpan(ContextCompat.getColor(getAppContext(),clickableTextColor)), clickableTextStartIdx, clickableTextEndIdx, 0)

        text = str
        movementMethod = LinkMovementMethod.getInstance()
    }

    fun rotateView(view: View?, angle: Float, duration: Long) {
        view?.animate()
            ?.rotation(angle)
            ?.setDuration(duration)
            ?.start()
    }
    
    /**
     * NOTE : Always set the count and repeat before setting min/max frame/progress.
     */
//    fun playLottie(lottieView: LottieAnimationView?, startFrame:Int, endFrame:Int, restartFrame:Int=-1, restartEndFrame:Int=-1, repeatCount: Int = -1, respectOsCheck: Boolean = false, speed: Float = 1f){
//        lottieView ?: return
//        lottieView.removeAllAnimatorListeners()
//        lottieView.setMinAndMaxFrame(startFrame,endFrame)
//        if (speed != 1f) {
//            lottieView.speed = speed
//        }
//        if(restartFrame!=-1) {
//            lottieView.addAnimatorListener(object : Animator.AnimatorListener {
//                override fun onAnimationStart(animation: Animator) {
//                    Logger.d(TAG, lottieView.id.toString() + " startAnimation")
//                }
//                override fun onAnimationEnd(animation: Animator) {
//                    if (isStandardAnimationScale().not()) return
//                    Logger.d(TAG, lottieView.id.toString() + " EndAnimation")
//                    lottieView.repeatMode = LottieDrawable.RESTART
//                    if (repeatCount > -1) {
//                        lottieView.repeatCount = repeatCount
//                    } else {
//                        lottieView.repeatCount = LottieDrawable.INFINITE
//                    }
//                    if(restartEndFrame!= -1) {
//                        lottieView.setMinAndMaxFrame(restartFrame, restartEndFrame)
//                    } else {
//                        lottieView.setMinAndMaxFrame(restartFrame, endFrame)
//                    }
//                    val shouldLoopLottie = LottieLoopGuard.addLottieToMapIfValidTime(lottieView.id.toString(), HikeTimeManager.getClientTimeWithServerOffset())
//                    if (shouldLoopLottie) {
//                        playAnimationBasedOnOS(lottieView, respectOsCheck)
//                    } else {
//                        lottieView.removeAllAnimatorListeners()
//                        return
//                    }
//                    lottieView.removeAllAnimatorListeners()
//                }
//                override fun onAnimationCancel(animation: Animator) {}
//                override fun onAnimationRepeat(animation: Animator) {}
//            })
//        }
//        LottieLoopGuard.addLottieToMapIfValidTime(lottieView.id.toString(), HikeTimeManager.getClientTimeWithServerOffset())
//        playAnimationBasedOnOS(lottieView, respectOsCheck)
//    }

//    fun getAnimationScale(): Float {
//        if (isAnimationScaleSet())
//            return animScale
//        val value: Float
//        try {
//            value = Settings.Global.getFloat(getAppContext().contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 0.0f)
//            Logger.d(TAG, "Animation scale is $value")
//        } catch (e: Throwable) {
//            GenericAnalytics.create().setFields("lottie_debug", genus = "animator_value_error").sendBg("lottie")
//            Logger.d(TAG, "Exception in getting animation scale")
//            animScale = 0.0f
//            return animScale
//        }
//        animScale = value
//        return value
//    }

    private fun isAnimationScaleSet(): Boolean {
        return animScale != -1f
    }

    fun resetAnimationScale() {
        animScale = -1f
    }
//
//    fun isStandardAnimationScale(): Boolean {
//        return getAnimationScale() == 1.0f
//    }

    fun animateTextViewLikeCounter(textview: TextView?, duration:Long, initialValue: Int, finalValue: Int) {
        textview ?: return
        textview.clearAnimation()
        val valueAnimator = ValueAnimator.ofInt(initialValue, finalValue)
        valueAnimator.duration = duration
        valueAnimator.addUpdateListener { animator -> textview.text = valueAnimator.animatedValue.toString() }
        valueAnimator.start()
    }

    private fun getColor(colorStr: String): Int {
        var colorStr = colorStr
        if (TextUtils.isEmpty(colorStr)) return Color.BLACK
        if (!colorStr.startsWith("#")) colorStr = "#$colorStr"
        return Color.parseColor(colorStr)
    }

    fun getResourceIdFromStringResourceName(resourceIdName: String): Int {
        getAppContext().apply {
            return resources.getIdentifier(resourceIdName, "id", this.packageName)
        }
    }

    //avoid using this apart from dls as this can cause ANRs

//    fun preloadImageWithGlide(url: String?, listener: GlideRequestListener? = null) {
//        HikeHandlerUtil.getInstance().postRunnable {
//            if (url.isNullOrBlank()) return@postRunnable
//            Glide.with(getAppContext())
//                    .load(url)
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .listener(object : RequestListener<Drawable> {
//                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
//                            listener?.onRequestFailure(e)
//                            return false
//                        }
//
//                        override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
//                            listener?.onRequestSuccess(resource)
//                            return false
//                        }
//                    })
//                    .preload()
//        }
//    }

    fun changeViewMargins(view: View?, left: Int, right: Int, top: Int, bottom: Int) {
        view ?: return
        view.layoutParams = (view.layoutParams as ConstraintLayout.LayoutParams).apply {
            marginStart = left
            topMargin = top
            marginEnd = right
            bottomMargin = bottom
        }
    }

    fun changeViewMarginsRecyclerView(view: View?, left: Int, right: Int, top: Int, bottom: Int) {
        view ?: return
        view.layoutParams = (view.layoutParams as RecyclerView.LayoutParams).apply {
            marginStart = left
            topMargin = top
            marginEnd = right
            bottomMargin = bottom
        }
    }

//    fun playAnimationMultiple(respectOsCheck: Boolean = false, vararg views: LottieAnimationView) {
//        views.forEach {
//            playAnimationBasedOnOS(it, respectOsCheck)
//        }
//    }

    fun makeLottieVisibleMultiple(respectOsCheck: Boolean = false, vararg views: View) {
        views.forEach {
            makeLottieVisibleBasedOnOS(it, respectOsCheck)
        }
    }

//    fun playAnimationBasedOnOS(anim: LottieAnimationView?, respectOsCheck: Boolean = true) {
//        anim ?: return
//        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P && respectOsCheck) return
//        anim.playAnimation()
//    }

    fun makeLottieVisibleBasedOnOS(view: View?, respectOsCheck: Boolean = true) {
        view ?: return
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P && respectOsCheck) return
        view.setVisible()
    }

    fun addAnimatorListener(anim: Animation, onAnimStart: (() -> Unit)?, onAnimEnd: (() -> Unit)?, onAnimRepeat: (() -> Unit)?) {
        anim.setAnimationListener(object: Animation.AnimationListener {
            override fun onAnimationStart(p0: Animation?) {
                onAnimStart?.invoke()
            }

            override fun onAnimationEnd(p0: Animation?) {
                onAnimEnd?.invoke()
            }

            override fun onAnimationRepeat(p0: Animation?) {
                onAnimRepeat?.invoke()
            }

        })
    }

//    fun cancelAnimationMultiple(vararg views: LottieAnimationView) {
//        views.forEach {
//            if (it.isAnimating) {
//                it.cancelAnimation()
//                it.removeAllAnimatorListeners()
//            }
//        }
//    }

    fun disableView(view: View) {
        view.isEnabled = false
        view.isFocusable = false
        view.isFocusableInTouchMode = false
        view.isClickable = false
    }

    fun setViewWidthHeight(view: View?, widthPx: Int, heightPx: Int) {
        view ?: return
        val params = view.layoutParams
        params.width = widthPx
        params.height = heightPx
        view.layoutParams = params
    }

    fun setStatusBarColor(activity: FragmentActivity?, color: Int) {
        activity?.window?.apply {
            if (this.statusBarColor != color) {
                statusBarColor = color
            }
        }
    }

    fun chooseImageFromGallery(activity: FragmentActivity?, requestCode: Int) {
        val pickPhoto =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickPhoto.type = "image/*"
        if (activity.isActivitySafe()) {
            try {
                activity?.startActivityForResult(Intent.createChooser(pickPhoto, "Select Photo"), requestCode)
            } catch (e: Exception) {
                //No activity found exception happening in some low end devices.
                e.printStackTrace()
            }
        }
    }

//    fun useTextDelegate(textToReplace: String, newText: String, view: LottieAnimationView) {
//        val textDelegate = TextDelegate(view)
//        textDelegate.setText(textToReplace, newText)
//        view.setTextDelegate(textDelegate)
//    }

    fun setTintMultiple(color: Int, vararg views: ImageView?) {
        views.forEach { view ->
            view?.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
        }
    }

    fun removeTintMultiple(vararg views: ImageView) {
        views.forEach { view ->
            view.colorFilter = null
        }
    }

    fun applyShaderToText(view: TextView, txt: String?, colorArray: IntArray) {
        val pt = view.paint
        val width = pt.measureText(txt)
        val shader = LinearGradient(0f, 0f, width, view.textSize, colorArray, null, Shader.TileMode.CLAMP)
        view.paint.shader = shader
    }


    fun isValidHexColor(code: String?): Boolean {
        if (code == null || (code.startsWith("#").not())) return false

        val codeWithoutHash = code.substring(1)
        val validLength = listOf(6, 8).contains(codeWithoutHash.length)

        return validLength && codeWithoutHash.all { it in '0'..'9' || it in 'a'..'f' || it in 'A'..'F' }
    }


    fun getStrikeOffText(text:String, color: Int): SpannableString? {
        val resultString = SpannableString(text)
        resultString.setSpan(StrikethroughSpan(), 0, resultString.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        resultString.setSpan(ForegroundColorSpan(color),0, resultString.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        return resultString
    }

    fun showGenericErrorToast(context: Context?){
        context?: return
        Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
    }
}
