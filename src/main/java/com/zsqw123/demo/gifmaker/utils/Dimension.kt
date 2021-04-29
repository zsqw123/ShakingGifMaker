package com.zsqw123.demo.gifmaker.utils

import android.content.Context
import android.content.res.Resources
import android.util.AttributeSet
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.core.content.res.use

private fun dimentionHelper(unit: Int, value: Float): Float = TypedValue.applyDimension(unit, value, Resources.getSystem().displayMetrics)

val Int.sp
    get() = dimentionHelper(TypedValue.COMPLEX_UNIT_SP, toFloat())
val Int.dp
    get() = dimentionHelper(TypedValue.COMPLEX_UNIT_DIP, toFloat())

fun Context.getDimension(attributeSet: AttributeSet, @AttrRes resId: Int, def: Float = 0f): Float {
    return obtainStyledAttributes(attributeSet, intArrayOf(resId)).use { it.getDimension(0, def) }
}