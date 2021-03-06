package com.thomaskioko.paybillmanager.mobile.extension

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.google.android.material.textfield.TextInputLayout

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.GONE
}

fun TextView.showErrorMessage(errorMessage: String) {
    this.text = errorMessage
}

fun TextInputLayout.showErrorMessage(errorMessage: String) {
    this.isErrorEnabled = true
    this.error = errorMessage
}

fun ViewGroup.inflate(
        @LayoutRes layoutRes: Int,
        attachToRoot: Boolean = false
): View = LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
