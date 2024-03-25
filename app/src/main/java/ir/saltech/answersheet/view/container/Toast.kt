package ir.saltech.answersheet.view.container

import android.annotation.SuppressLint
import android.content.Context
import ir.saltech.answersheet.R

class Toast {
    private val context: Context
    private val text: String
    private val duration: Long
    private var imageResId = 0

    @SuppressLint("CommitPrefEdits")
    private constructor(context: Context, text: String, imageResId: Int, duration: Long) {
        this.text = text
        this.imageResId = imageResId
        this.duration = duration
        this.context = context
    }

    @SuppressLint("CommitPrefEdits")
    private constructor(context: Context, text: String, duration: Long) {
        this.text = text
        this.duration = duration
        this.context = context
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun show() {
        val toast = MaterialTextToast(context)
        toast.setText(text)
        if (imageResId != 0) {
            toast.setIcon(context.resources.getDrawable(imageResId))
        }
        toast.duration = duration
        toast.showToast()
    }

    companion object {
        val WARNING_SIGN: Int = R.drawable.warning
        const val LENGTH_SHORT: Long = 3000
        const val LENGTH_LONG: Long = 5000
        fun makeText(context: Context, text: String, duration: Long): Toast {
            return Toast(context, text, duration)
        }

        fun makeText(context: Context, text: String, imageResId: Int, duration: Long): Toast {
            return Toast(context, text, imageResId, duration)
        }
    }
}
