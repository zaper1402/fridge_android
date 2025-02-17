package com.threemusketeers.dliverCustomer.main.utils.extensions

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.threemusketeers.dliverCustomer.main.utils.ViewUtils

/**
 * Load model into ImageView as a circle image with borderSize (optional) using Glide
 *
 * @param model - Any object supported by Glide (Uri, File, Bitmap, String, resource id as Int, ByteArray, and Drawable)
 * @param borderSize - The border size in pixel
 * @param borderColor - The border color
 */

fun <T> ImageView.getCircularImage(
        model: T,
        borderSize: Float = 0F,
        borderColor: Int = Color.WHITE
) {
    Glide.with(context)
            .asBitmap()
            .load(model)
            .apply(RequestOptions.circleCropTransform())
            .into(object : BitmapImageViewTarget(this) {
                override fun setResource(resource: Bitmap?) {
                    setImageDrawable(
                            resource?.run {
                                RoundedBitmapDrawableFactory.create(
                                        resources,
                                        if (borderSize > 0) {
                                            createBitmapWithBorder(borderSize, borderColor)
                                        } else {
                                            this
                                        }
                                ).apply {
                                    isCircular = true
                                }
                            }
                    )
                }
            })
}

/**
 * Create a new bordered bitmap with the specified borderSize and borderColor
 *
 * @param borderSize - The border size in pixel
 * @param borderColor - The border color
 * @return A new bordered bitmap with the specified borderSize and borderColor
 */
private fun Bitmap.createBitmapWithBorder(borderSize: Float, borderColor: Int): Bitmap {
    val borderOffset = (borderSize * 2).toInt()
    val halfWidth = width / 2
    val halfHeight = height / 2
    val circleRadius = Math.min(halfWidth, halfHeight).toFloat()
    val newBitmap = Bitmap.createBitmap(
            width + borderOffset,
            height + borderOffset,
            Bitmap.Config.ARGB_8888
    )

    // Center coordinates of the image
    val centerX = halfWidth + borderSize
    val centerY = halfHeight + borderSize

    val paint = Paint()
    val canvas = Canvas(newBitmap).apply {
        // Set transparent initial area
        drawARGB(0, 0, 0, 0)
    }

    // Draw the transparent initial area
    paint.isAntiAlias = true
    paint.style = Paint.Style.FILL
    canvas.drawCircle(centerX, centerY, circleRadius, paint)

    // Draw the image
    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    canvas.drawBitmap(this, borderSize, borderSize, paint)

    // Draw the createBitmapWithBorder
    paint.xfermode = null
    paint.style = Paint.Style.STROKE
    paint.color = borderColor
    paint.strokeWidth = borderSize
    canvas.drawCircle(centerX, centerY, circleRadius, paint)
    return newBitmap
}

// For multiple views this method provide press feedbacks
fun View.addPressFeedback(vararg otherViews:View){
    ViewUtils.addFeedbackLight(this, *otherViews)
}

fun View.setAlphas(alpha:Float, vararg otherViews:View){
    this.alpha = alpha
    otherViews.forEach { it.alpha = alpha }
}

fun ImageView.setImageResourceAsync(@DrawableRes drawableResId: Int, cacheNeeded:Boolean = false){
    val requestBuilder = Glide.with(this).load(drawableResId)
    if(cacheNeeded) {
        requestBuilder.diskCacheStrategy(DiskCacheStrategy.ALL)
    }
    requestBuilder.into(this)
}

fun View.setBackgroundAsync(@DrawableRes drawableResId: Int, cacheNeeded:Boolean = false){
    val requestBuilder = Glide.with(this).asDrawable().load(drawableResId)
    if(cacheNeeded) {
        requestBuilder.diskCacheStrategy(DiskCacheStrategy.ALL)
    }
    requestBuilder.into(object : CustomTarget<Drawable>() {
        override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
            this@setBackgroundAsync.background = resource
        }

        override fun onLoadCleared(placeholder: Drawable?) {
        }

    })
}