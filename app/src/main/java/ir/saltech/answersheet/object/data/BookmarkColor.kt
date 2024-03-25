package ir.saltech.answersheet.`object`.data

import android.graphics.Color

class BookmarkColor {
    var color: Int = 0
        private set
    var backgroundColor: Int = 0
        private set
    private var emoji: String? = null

    constructor()

    constructor(color: Int) {
        var colorIndex = -1
        for (i in BOOKMARK_COLORS.indices) {
            if (color == BOOKMARK_COLORS[i]) {
                colorIndex = i
                break
            }
        }
        if (colorIndex >= 0) {
            this.color = BOOKMARK_COLORS[colorIndex]
            this.backgroundColor = BACKGROUND_BOOKMARK_COLORS[colorIndex]
            this.emoji = BOOKMARK_EMOJIS[colorIndex]
        }
    }

    constructor(colorIndex: Int, unused: Boolean) {
        this.color = BOOKMARK_COLORS[colorIndex]
        this.backgroundColor = BACKGROUND_BOOKMARK_COLORS[colorIndex]
        this.emoji = BOOKMARK_EMOJIS[colorIndex]
    }

    fun getEmoji(): String {
        return emoji!!
    }

    override fun toString(): String {
        return "BookmarkColor{" +
                "color=" + color +
                ", backgroundColor=" + backgroundColor +
                ", emoji='" + emoji + "'" +
                '}'
    }

    companion object {
        private const val BACKGROUND_COLOR_TRANSPARENCY = 100
        private val BOOKMARK_COLORS = intArrayOf(
            Color.rgb(220, 53, 69),  //  RED
            Color.rgb(253, 126, 20),  // ORANGE
            Color.rgb(255, 193, 7),  // YELLOW
            Color.rgb(32, 201, 151),  // CYAN
            Color.rgb(13, 110, 253),  //  BLUE
            Color.rgb(214, 51, 132),  // PINK
            Color.rgb(121, 85, 72),  //  BROWN
            Color.rgb(108, 117, 125),  // BLACK
            Color.rgb(224, 224, 224) //  WHITE
        )
        private val BACKGROUND_BOOKMARK_COLORS = intArrayOf(
            Color.argb(BACKGROUND_COLOR_TRANSPARENCY, 220, 53, 69),  //  RED
            Color.argb(BACKGROUND_COLOR_TRANSPARENCY, 253, 126, 20),  // ORANGE
            Color.argb(BACKGROUND_COLOR_TRANSPARENCY, 255, 193, 7),  // YELLOW
            Color.argb(BACKGROUND_COLOR_TRANSPARENCY, 32, 201, 151),  // CYAN
            Color.argb(BACKGROUND_COLOR_TRANSPARENCY, 13, 110, 253),  //  BLUE
            Color.argb(BACKGROUND_COLOR_TRANSPARENCY, 214, 51, 132),  // PINK
            Color.argb(BACKGROUND_COLOR_TRANSPARENCY, 121, 85, 72),  //  BROWN
            Color.argb(BACKGROUND_COLOR_TRANSPARENCY, 108, 117, 125),  // BLACK
            Color.argb(BACKGROUND_COLOR_TRANSPARENCY, 224, 224, 224) //  WHITE
        )
        private val BOOKMARK_EMOJIS = arrayOf(
            "🔴",  //  RED
            "🟠",  // ORANGE
            "🟡",  // YELLOW
            "🟢",  // CYAN
            "🔵",  //  BLUE
            "🟣",  // PINK
            "🟤",  //  BROWN
            "⚫",  // BLACK
            "⚪" //  WHITE
        )
    }
}
