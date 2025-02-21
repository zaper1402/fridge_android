package com.threemusketeers.dliverCustomer.main.utils.extensions

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Point
import android.graphics.Shader
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.style.ForegroundColorSpan
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.ashir.fridge.FridgeApplication
import com.ashir.fridge.utils.DebouncedOnClickListener
import com.ashir.fridge.utils.Utils
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.threemusketeers.dliverCustomer.main.utils.ViewUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import org.json.JSONObject
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.Locale
import kotlin.math.pow
import kotlin.math.roundToInt

fun Any.getAppContext():Context = FridgeApplication.instance

fun <T: View> T.setGone(doAnimation: Boolean = false):T {
    if(this.visibility == View.GONE) return this
    if(!doAnimation) {
        this.visibility = View.GONE
        return this
    }
    return this
}

fun <T: View> T.setVisible(doAnimation: Boolean = false):T {
    if(this.visibility == View.VISIBLE) return this
    if(!doAnimation) {
        this.visibility = View.VISIBLE
        return this
    }
    return this
}

fun <T: View> T.setVisibilityBoolean(isVisible: Boolean) {
    if(isVisible) this.setVisible() else this.setGone()
}

fun <T: View> T.setVisibleMultiple(vararg views: View?) {
    this.setVisible()
    views.forEach { it?.setVisible() }
}

fun <T: View> T.setGoneMultiple(vararg views: View?) {
    this.setGone()
    views.forEach { it?.setGone() }
}

fun <T : View> T.setInVisible(): T {
    this.visibility = View.INVISIBLE
    return this
}

fun <T: View> T.setInvisibleMultiple(vararg views: View?) {
    this.setInVisible()
    views.forEach { it?.setInVisible() }
}

fun <T: View> T.isVisible(): Boolean {
    return this.visibility == View.VISIBLE
}


fun <T: View> T.setAlphaMultiple(alphaValue: Float, vararg views: View) {
    this.alpha = alphaValue
    views.forEach { it.alpha = alphaValue }
}

fun <T: View> T.setElevationMultiple(elevationValue: Float, vararg views: View) {
    this.elevation = elevationValue
    views.forEach { it.elevation = elevationValue }
}

fun <T: TextView> T.setTextSizeMultiple(size: Float, vararg views: TextView?) {
    this.textSize = size
    views.forEach { it?.textSize = size }
}

fun Int?.toPx():Int = this?.let { Utils.dpToPx(it) } ?: 0

fun Float?.toPx():Int = this?.let { Utils.dpToPx(it.toInt()) } ?: 0

fun Int?.toDp():Float = this?.let { Utils.pxToDp(it) } ?: 0f

fun Float?.toDp():Float = this?.let { Utils.pxToDp(it.toInt()) } ?: 0f

fun Float?.defaultValueIfNull(defaultValue:Float? = 0f): Float {
    this ?: return (defaultValue ?: 0f)
    return this
}

fun String.capitalizeFirstChar(): String {
    return this.replaceFirstChar {
        if (it.isLowerCase())
            it.titlecase(Locale.getDefault())
        else it.toString()
    }
}

fun View.debouncedClickListener(threshold: Long = DebouncedOnClickListener.DEFAULT_CLICK_THRESHOLD, tryAgainCTA: Boolean = true, isHandleInternet:Boolean = false, clickListener: View.OnClickListener) {
    ViewUtils.debounceClick(this, threshold, tryAgainCTA = tryAgainCTA, clickListener = clickListener, isHandleInternet = isHandleInternet)
}

//fun Any?.showToast(msg: String?) {
//    ToastUtils.showToastShort(msg)
//}
//
//fun Any?.showToastWithGenericErrorMessage() {
//    ToastUtils.showToastShort("Something went wrong. Please try again.")
//}
//
//fun Any?.showToastWithNoNetworkMessage() {
//    ToastUtils.showToastShort("Please check your internet connection and try again")
//}


//
//fun Response?.getJSONObject(): JSONObject? {
//    if(this == null) { return null }
//    val response = body?.content as? JSONObject
//    return response
//}

fun <T> MutableLiveData<T>.changeValue(value: T) {
    if (Looper.myLooper() == Looper.getMainLooper()) {
        setValue(value)
    } else {
        postValue(value)
    }
}

fun <T> MutableLiveData<T>.changeValueOnlyIfMismatch(newValue: T) {
    Handler(Looper.getMainLooper()).post {
        if (value != newValue) {
            changeValue(newValue)
        }
    }
}
/***  Double extensions Section Start */

infix fun Double?.isGreaterThan(other: Double?): Boolean =
    if (this != null && other != null) this > other else false

infix fun Double?.isEqualsTo(other: Double?): Boolean =
    if (this != null && other != null) this == other else false

infix fun Double?.isGreaterThan(other: Int?): Boolean =
    if (this != null && other != null) this > other else false

infix fun BigDecimal?.isGreaterThan(other: BigDecimal?): Boolean =
    if (this != null && other != null) this > other else false

infix fun BigDecimal?.isGreaterThanOrEqual(other: BigDecimal?): Boolean =
    if (this != null && other != null) this >= other else false

infix fun Double?.isGreaterThanOrEqual(other: Double?): Boolean =
    if (this != null && other != null) this >= other else false

infix fun Double?.isGreaterThanOrEqual(other: Int?): Boolean =
    if (this != null && other != null) this >= other else false

infix fun Double?.isLessThan(other: Double?): Boolean =
    if (this != null && other != null) this < other else false

infix fun Double?.isLessThanOrEqual(other: Double?): Boolean =
    if (this != null && other != null) this <= other else false

infix fun BigDecimal?.isLessThanOrEqual(other: BigDecimal?): Boolean =
    if (this != null && other != null) this <= other else false

infix fun Int?.isLessThanOrEqual(other: Int?): Boolean =
    if (this != null && other != null) this <= other else false

infix fun Int?.isLessThan(other: Int?): Boolean =
    if (this != null && other != null) this < other else false

fun Int?.isGreaterThan(other: Int?): Boolean =
    if (this != null && other != null) this > other else false

fun Int?.isGreaterThanOrEqual(other: Int?): Boolean =
    if (this != null && other != null) this >= other else false

fun Int?.isEqual(other: Int?): Boolean =
        if (this != null && other != null) this == other else false

infix fun Long?.isLessThanOrEqual(other: Long?): Boolean =
    if (this != null && other != null) this <= other else false

infix fun Long?.isLessThan(other: Long?): Boolean =
    if (this != null && other != null) this < other else false

fun Long?.isGreaterThan(other: Long?): Boolean =
    if (this != null && other != null) this > other else false

fun Long?.isGreaterThanOrEqual(other: Long?): Boolean =
    if (this != null && other != null) this >= other else false

fun Long?.isEqual(other: Long?): Boolean =
    if (this != null && other != null) this == other else false


fun Float?.toPrintableString(
    isAppendRupeeSymbol: Boolean = false,
    truncateDecimalDigits: Boolean = false,
    numFractionDigits: Int = 2
): String {
    val result = if (isAppendRupeeSymbol) "₹" else ""
    this ?: return if (isAppendRupeeSymbol) result + "0" else ""

    if (this % 1.0 == 0.0) { // if double is a whole number
        return result + this.toInt().toString()
    }

    if (isAppendRupeeSymbol || truncateDecimalDigits) {
        return result + BigDecimal(this.toString()).truncateBigDecimal(numFractionDigits).toPlainString()
//         cut decimals to numFractionDigits or default = 2 if its more than 2 like 3.567 will be 3.56
    }
    return result + this.toString()
}

fun Double?.toPrintableString(
    isAppendRupeeSymbol: Boolean = false,
    truncateDecimalDigits: Boolean = false,
    numFractionDigits: Int = 2
): String {
    val result = if (isAppendRupeeSymbol) "₹" else ""
    this ?: return if (isAppendRupeeSymbol) result + "0" else ""

    if (this % 1.0 == 0.0) { // if double is a whole number
        return result + this.toInt().toString()
    }

    if (isAppendRupeeSymbol || truncateDecimalDigits) {
        return result + BigDecimal(this.toString()).truncateBigDecimal(numFractionDigits).toPlainString()
//         cut decimals to numFractionDigits or default = 2 if its more than 2 like 3.567 will be 3.56
    }
    return result + this.toString()
}

fun BigDecimal?.toPrintableString(
    isAppendRupeeSymbol: Boolean = false,
    truncateDecimalDigits: Boolean = false,
    numFractionDigits: Int = 2
): String {
    val result = if (isAppendRupeeSymbol) "₹" else ""
    this ?: return if (isAppendRupeeSymbol) result + "0" else ""

    if (this.remainder(BigDecimal(1.0))
            .compareTo(BigDecimal.ZERO) == 0
    ) {  // if BigDecimal is a whole number
        return result + this.toInt().toString()
    }

    if (isAppendRupeeSymbol || truncateDecimalDigits) {
        return result + this.truncateBigDecimal(numFractionDigits = numFractionDigits)
            .toPlainString()
        // cut decimals to numFractionDigits or default = 2 if its more than 2 like 3.567 will be 3.56
    }
    return result + this.toString()
}



fun Long?.toPrintableString(isAppendRupeeSymbol: Boolean = false, truncateDecimalDigits: Boolean = false, numFractionDigits: Int = 2, divisorExp : Int = 0) : String {
    val result = if (isAppendRupeeSymbol) "₹" else ""
    this ?: return if (isAppendRupeeSymbol) result + "0" else ""

    val divisor = (10.0.pow(divisorExp))
    var ans = this.div(divisor)
    if (ans % 1.0 == 0.0) {  // if double is a whole number
        return result + ans.toInt().toString()
    }

    if (isAppendRupeeSymbol || truncateDecimalDigits) {
        return result + BigDecimal(ans.toString()).truncateBigDecimal(numFractionDigits).toPlainString()
    }
    return ans.toString()
}

fun Int?.toPrintableString(isAppendRupeeSymbol: Boolean = false, truncateDecimalDigits: Boolean = false, numFractionDigits: Int = 2, divisorExp : Int = 0) : String {
    val result = if (isAppendRupeeSymbol) "₹" else ""
    this ?: return if (isAppendRupeeSymbol) result + "0" else ""

    val divisor = (10.0.pow(divisorExp))
    var ans = this.div(divisor)
    if (ans % 1.0 == 0.0) {  // if double is a whole number
        return result + ans.toInt().toString()
    }

    if (isAppendRupeeSymbol || truncateDecimalDigits) {
        return result + BigDecimal(ans.toString()).truncateBigDecimal(numFractionDigits).toPlainString()
    }
    return ans.toString()
}

/**
 * To be used to scale down value eg -> 1056 to 10.56 with scaleFactor = 2
 */
fun Long?.toScaledDouble(scaleFactor : Int = 0): Double? {
    this?: return null
    if(scaleFactor <= 0) {
        return this.toDouble()
    }
    val divisor = (10.0.pow(scaleFactor))
    return this.div(divisor)
}

fun Int?.toScaledDouble(scaleFactor : Int = 0): Double? {
    this?: return null
    if(scaleFactor <= 0) {
        return this.toDouble()
    }
    val divisor = (10.0.pow(scaleFactor))
    return this.div(divisor)
}

fun String?.addRupeeIcon(): String {
    return "₹" + this.toString()
}

fun Double?.add(other: Double?): Double {
    this ?: return other.defaultValueIfNull()
    other ?: return this.defaultValueIfNull()
    return this.plus(other)
}

fun BigDecimal?.substract(other: BigDecimal?): BigDecimal {
    this ?: return (other.defaultValueIfNull().times(BigDecimal.ONE.negate()))
    other ?: return this
    return this.minus(other)
}

fun Double?.substract(other: Double?): Double {
    this ?: return (other.defaultValueIfNull().times(-1))
    other ?: return this
    return this.minus(other)
}

fun Long?.substract(other: Long?): Long {
    this ?: return (other.defaultValueIfNull().times(-1))
    other ?: return this
    return this.minus(other)
}

fun Double?.defaultValueIfNull(defaultValue: Double? = 0.0): Double {
    this ?: return (defaultValue ?: 0.0)
    return this
}

fun BigDecimal?.defaultValueIfNull(defaultValue: BigDecimal? = 0.toBigDecimal()): BigDecimal {
    this ?: return (defaultValue ?: 0.toBigDecimal())
    return this
}

fun Long?.defaultValueIfNull(defaultValue: Long? = 0L): Long {
    this ?: return (defaultValue ?: 0L)
    return this
}

fun Double?.absolute(): Double = this?.let { Math.abs(it) }.defaultValueIfNull()

fun Double?.multiplybyValueElseDefaultIfNull(value: Int = 1000): Long {
    this?.let {
        return (it * value).toLong()
    }
    return 0L
}

fun Double?.roundTo(numFractionDigits: Int): Double {
    this ?: return 0.0
    val factor = 10.0.pow(numFractionDigits.toDouble())
    return (this * factor).roundToInt() / factor
}


fun Double?.floorTo(numFractionDigits: Int): Double {
    this?:return 0.0
    val str = this.toString()
    val decimalInd = str.indexOf('.')
    if(decimalInd == -1){
        return this
    }else{
        val digitsAfterDecimal:Int = (str.length - 1) - decimalInd
        return str.substring(startIndex = 0,endIndex = decimalInd+Math.min(numFractionDigits,digitsAfterDecimal)+1).toDouble()
    }
}

fun Double?.getDecimalFormatted(): String? {
    this?: return ""
    val format = DecimalFormat()
    format.isDecimalSeparatorAlwaysShown = false
    return format.format(this)
}
/***  Double extensions Section End */

/***  Boolean extensions Section Start */

fun Boolean?.isTrue(): Boolean {
    this ?: return false
    return this
}

fun Boolean?.isFalse(): Boolean {
    return isTrue().not()
}

fun Boolean?.toIntString() = if (this == true) "1" else "0"

fun Boolean?.toInt() = if (this == true) 1 else 0

fun Int?.defaultValueIfNull(defaultValue:Int? = 0): Int {
    this ?: return (defaultValue ?: 0)
    return this
}

fun String?.defaultValueIfNull(defaultValue:String? = ""): String {
    this ?: return (defaultValue ?: "")
    return this
}

fun String?.defaultValueIfNullOrEmpty(defaultValue:String? = ""): String {
    this ?: return (defaultValue ?: "")
    if(this.isBlank()){
        return (defaultValue ?: "")
    }
    return this
}

fun String?.isValidUrl():Boolean {
    this ?: return false
    if(this.isBlank()) return false
    try {
        return Uri.parse(this).run {  isHierarchical && isAbsolute }
    } catch (e: Exception) {
        return false
    }
}

fun Boolean?.defaultValueIfNull(defaultValue:Boolean? = false): Boolean {
    this ?: return (defaultValue ?: false)
    return this
}

fun Any?.isNonNull() = this != null

fun Any?.isNull() = this == null


fun Bundle?.toPrintableString(): String {
    val bundle: Bundle = this ?: return "{}"
    val stringBuffer = StringBuffer("{ ")
    bundle.keySet().forEachSafe { key ->
        val value = bundle.get(key).toString()
        stringBuffer.append("$key=$value, ")
    }
    stringBuffer.append("}")
    return stringBuffer.toString()
}

fun String?.toJSONObject(): JSONObject? {
    this ?: return null
    try {
        return JSONObject(this)
    } catch (e: Exception) {
        return null
    }
}

fun AppCompatActivity?.startActivitySafe(intent: Intent?){
    this ?: return
    if(isFinishing) return
    if(intent.isNull()) return
    try {
        startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun AppCompatActivity?.startActivityForResultSafe(intent: Intent?, requestCode: Int?){
    this ?: return
    if(isFinishing) return
    if (intent == null || requestCode == null) return
    try {
        startActivityForResult(intent, requestCode)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

// use this for Fragments without checking for as AppCompatActivity ...
fun FragmentActivity?.startActivitySafe(intent: Intent?){
    this ?: return
    if(isFinishing) return
    if(intent.isNull()) return
    try {
        startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun FragmentActivity?.startActivityForResultSafe(intent: Intent?, requestCode: Int?){
    this ?: return
    if(isFinishing) return
    if (intent == null || requestCode == null) return
    try {
        startActivityForResult(intent, requestCode)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun FragmentActivity?.isActivitySafe(): Boolean = this != null && isFinishing.not()

fun FragmentManager?.popBackstackSafe() {
    try {
        this?.popBackStack()
    } catch (e: Exception) { // in some race condition this produces exception. So ignore in this case.
        e.printStackTrace()
    }
}

fun Dialog?.dismissSafe() {
    try {
        this?.dismiss()
    } catch (e: Exception) {
    }
}

fun BottomSheetDialogFragment?.showSafe(manager: FragmentManager?, tag:String? = null) {
    this ?: return
    manager ?: return
    try {
        show(manager, tag)
    } catch (e: Exception) {
    }
}

fun DialogFragment?.showSafe(manager: FragmentManager?, tag:String? = null) {
    this ?: return
    manager ?: return
    try {
        show(manager, tag)
    } catch (e: Exception) {
    }
}

fun JSONObject?.optDoubleDefault(key:String): Double {
    this ?: return 0.0
    return this.optDouble(key, 0.0)
}

fun JSONObject?.putIfKeyDidntExist(key: String, value:Any?): JSONObject?{
    this ?: return this
    if (this.has(key)) {
        return this
    }
    putIfValueNonNull(key, value)
    return this
}

fun JSONObject?.putIfValueNonNull(key: String, value:Any?): JSONObject?{
    this ?: return this
    if (value == null) {
        return this
    }
    this.put(key,value)
    return this
}

fun <T> List<T>?.getSafe(index: Int): T? {
    this ?: return null
    if(index >= 0 && index < this.size){
        return this[index]
    }
    return null
}

inline fun <T> Iterable<T>.forEachSafe(action: (T) -> Unit): Unit {
    this.iterator().forEach(action)
}

fun <T> List<T>?.isLastIndex(index: Int): Boolean {
    this ?: return false
    return index == size-1
}

fun <T> List<T>?.isSecondLastIndex(index: Int): Boolean {
    this ?: return false
    return index == size-2
}

fun ViewDataBinding.launchWhenStarted(lambda: suspend CoroutineScope.() -> Unit): Job? {
    return this.lifecycleOwner?.lifecycleScope?.launchWhenStarted(lambda)
}

fun View.getLocationOnScreen(): Point {
    val location = IntArray(2)
    this.getLocationOnScreen(location)
    return Point(location[0],location[1])
}

fun Handler.removeCallbacksAndMessagesMultiple(vararg handlers: Handler) {
    this.removeCallbacksAndMessages(null)
    handlers.forEach { it.removeCallbacksAndMessages(null) }
}

fun Menu?.getItemSafe(index: Int): MenuItem? {
    this ?: return null
    if(index >= 0 && index < this.size()){
        return this.getItem(index)
    }
    return null
}

fun Image.toBitmap(): Bitmap {
    val buffer = planes[0].buffer
    buffer.rewind()
    val bytes = ByteArray(buffer.capacity())
    buffer.get(bytes)
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}

fun Bitmap.rotate(degrees: Float): Bitmap =
    Bitmap.createBitmap(this, 0, 0, width, height, Matrix().apply { postRotate(degrees)}, true)

fun Bitmap.flip(): Bitmap? { //not used currently. Will be required if front camera used with CameraX
    val matrix = Matrix()
    matrix.preScale(-1.0f, 1.0f)
    return Bitmap.createBitmap(this, 0, 0, this.width, this.height, matrix, true)
}

fun <T: TextView> T.setTextViewColorMultiple(textColor: Int?, vararg views: TextView?) {
    textColor ?: return
    this.setTextColor(textColor)
    views.forEach { it?.setTextColor(textColor) }
}

fun <T: ImageView> T.setImageViewTintMultiple(tintColor: Int, vararg views: ImageView) {
    this.setColorFilter(tintColor)
    views.forEach { it.setColorFilter(tintColor) }
}

fun Any?.runOnUIThread(uiHandler: Handler?, runnable: Runnable) {
    if (Looper.myLooper() == Looper.getMainLooper()) {
        runnable.run()
    } else {
        uiHandler?.post(runnable)
    }
}

fun Any?.executeOnUIThread(uiHandler: Handler?, runnable: Runnable) {
    runOnUIThread(uiHandler, runnable)
}

fun PopupWindow.dismissAllowingStateLoss() {
    try{
        this.dismiss()
    }catch (e : java.lang.Exception){
    }
}

fun TextView.setGradient(intArray: IntArray) {
    val paint: TextPaint = this.paint
    val textWidth = paint.measureText(this.text.toString())
    this.paint.shader  = LinearGradient(0f, 0f, textWidth, this.textSize,
        intArray, null, Shader.TileMode.CLAMP)
}

fun TextView.setGradient(colors: Array<String>) {
    val intArray = IntArray(colors.size)
    for (i in colors.indices) {
        intArray[i] = Color.parseColor(colors[i])
    }
    setGradient(intArray)
}

fun TextView.setGradientHorizontal(intArray: IntArray) {
    val paint: TextPaint = this.paint
    this.setTextColor(intArray[0])
    val textWidth = paint.measureText(this.text.toString())
    this.paint.shader  = LinearGradient(0f, 0f, textWidth, 0f,
        intArray, null, Shader.TileMode.CLAMP)
}

fun TextView.setGradientHorizontal(colors: Array<String>) {
    val intArray = IntArray(colors.size)
    for (i in colors.indices) {
        intArray[i] = Color.parseColor(colors[i])
    }
    setGradientHorizontal(intArray)
}

fun TextView.setGradientHorizontalMultiple(colors: Array<String>, vararg views: TextView?) {
    val intArray = IntArray(colors.size)
    for (i in colors.indices) {
        intArray[i] = Color.parseColor(colors[i])
    }
    setGradientHorizontal(intArray)
    views.forEach { it?.setGradientHorizontal(intArray) }
}

fun TextView.setTextWithGradientSubString(primaryString: String, substring: String, intArray: IntArray) {
    //check on length of primaryString and substring
    if (primaryString.length < substring.length) {
        return
    }
    val spannable = SpannableString(primaryString)
    val start = primaryString.indexOf(substring)
    if (start == -1) {
        return
    }
    val paint: TextPaint = this.paint
    this.setTextColor(intArray[0])
    val textWidth = paint.measureText(substring)
    val shader = LinearGradient(0f, 0f, textWidth, 0f,
        intArray, null, Shader.TileMode.CLAMP)
    val end = start + substring.length
    val shaderSpan = object : ForegroundColorSpan(intArray[0]) {
        override fun updateDrawState(ds: TextPaint) {
            ds.shader = shader
        }
    }
    spannable.setSpan(shaderSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    this.text = spannable
}

fun TextView.setTextWithGradientSubString(primaryString: String ,substring: String, colors: Array<String>) {
    val intArray = IntArray(colors.size)
    for (i in colors.indices) {
        intArray[i] = Color.parseColor(colors[i])
    }
    setTextWithGradientSubString(primaryString , substring, intArray)
}

fun TextView.removeGradient(){
    val paint: TextPaint = this.paint
    this.paint.shader = null
}

fun <T> LiveData<T>.observeOnce(owner: LifecycleOwner, observer: (T) -> Unit) {
    observe(owner, object : Observer<T> {
        override fun onChanged(value: T) {
            if (value != null) {
                removeObserver(this)
            }
            observer(value)
        }
    })
}

fun SpannableString.setSpanSafe(span: Any, start: Int, end: Int, flags: Int) {
    if (this.isNotEmpty() && start >= 0 && end < this.length) {
        this.setSpan(span, start, end, flags)
    }
}

fun BigDecimal?.truncateBigDecimal(
    numFractionDigits: Int = 2,
    roundingMode: RoundingMode = RoundingMode.DOWN
): BigDecimal {
    this ?: return 0.toBigDecimal()
    return this.setScale(numFractionDigits, roundingMode).stripTrailingZeros()
}

fun String?.isNumeric(): Boolean {
    if (this.isNullOrEmpty()) {
        // If the TextView is null or its text is empty, we consider it not numeric
        return false
    }
    // Remove spaces and commas for the purpose of validation
    val sanitizedText = this.toString().trim().replace(",", "").replace(" ", "")
    // Check if the sanitized text in the TextView is a valid number
    return sanitizedText.matches("-?\\d+(\\.\\d+)?".toRegex())
    // The regex "-?\\d+(\\.\\d+)?" matches:
    // - an optional minus sign (for negative numbers),
    // - followed by one or more digits,
    // - optionally followed by a decimal point and one or more digits (for decimal numbers).
}