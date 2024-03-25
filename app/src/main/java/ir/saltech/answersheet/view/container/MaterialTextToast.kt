package ir.saltech.answersheet.view.container

import android.content.Context

class MaterialTextToast(context: Context) : MaterialToast(context) {
    var duration: Long = 0

    init {
        super.init(contentView)
    }

    fun showToast() {
        if (duration != 0L) {
            super.show(duration)
        }
    }
}
