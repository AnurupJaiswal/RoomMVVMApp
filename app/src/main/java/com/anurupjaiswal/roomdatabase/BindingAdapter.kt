package com.anurupjaiswal.roomdatabase

import android.widget.EditText
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter


@BindingAdapter("android:text")
fun setText(view: EditText, value: String?) {
    if (view.text.toString() != value) {
        view.setText(value)
    }
}

@InverseBindingAdapter(attribute = "android:text", event = "android:textAttrChanged")
fun getText(view: EditText): String {
    return view.text.toString()
}
