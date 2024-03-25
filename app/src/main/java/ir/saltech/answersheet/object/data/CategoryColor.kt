package ir.saltech.answersheet.`object`.data

import android.graphics.Color

class CategoryColor(rgbColorElements: IntArray) {
    val accentColor: Int =
        Color.rgb(rgbColorElements[0], rgbColorElements[1], rgbColorElements[2])
    val editButtonColor: Int
    val backgroundColor: Int

    init {
        this.editButtonColor = Color.rgb(
            rgbColorElements[0] + EDIT_BUTTON_CONTRAST,
            rgbColorElements[1] + EDIT_BUTTON_CONTRAST,
            rgbColorElements[2] + EDIT_BUTTON_CONTRAST
        )
        this.backgroundColor = Color.argb(
            BACKGROUND_COLOR_TRANSPARENCY,
            rgbColorElements[0],
            rgbColorElements[1],
            rgbColorElements[2]
        )
    }

    override fun toString(): String {
        return "CategoryColor{" +
                "color=" + accentColor +
                ", backgroundColor=" + backgroundColor +
                '}'
    }

    companion object {
        private const val BACKGROUND_COLOR_TRANSPARENCY = 38
        private const val EDIT_BUTTON_CONTRAST = 0
    }
}
