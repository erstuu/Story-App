package com.erstuu.app.story.ui.customView

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.widget.addTextChangedListener
import com.erstuu.app.story.R

class CustomTextInputPassword @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {

        init {
            addTextChangedListener { s ->
                error =
                    if (s.toString().length < 8) resources.getString(R.string.password_error)
                    else null
            }
        }
}