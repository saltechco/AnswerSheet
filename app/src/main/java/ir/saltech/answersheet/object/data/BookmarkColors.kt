package ir.saltech.answersheet.`object`.data

class BookmarkColors {
    private var bookmarkColors: MutableList<BookmarkColor>

    init {
        bookmarkColors = ArrayList()
    }

    fun getBookmarkColors(): List<BookmarkColor> {
        return bookmarkColors
    }

    fun setBookmarkColors(bookmarkColors: MutableList<BookmarkColor>) {
        this.bookmarkColors = bookmarkColors
    }

    fun addBookmarkColor(q: BookmarkColor) {
        bookmarkColors.add(q)
    }

    override fun toString(): String {
        return "BookmarkColors{" +
                "bookmarkColors=" + bookmarkColors +
                '}'
    }
}
