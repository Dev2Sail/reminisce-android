package studio.hcmc.reminisce.util

import android.text.Editable
import com.google.android.material.textfield.TextInputLayout

var TextInputLayout.text: Editable
    get() = editText!!.text
    set(value) { editText!!.text = value }

var TextInputLayout.string: String
    get() = editText!!.text.toString()
    set(value) { editText!!.setText(value) }

var TextInputLayout.stringOrNull: String?
    get() = editText?.text?.toString()
    set(value) { editText?.setText(value)}