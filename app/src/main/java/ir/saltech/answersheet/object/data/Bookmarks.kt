package ir.saltech.answersheet.`object`.data

import android.util.Log

class Bookmarks : Things {
    private var bookmarks: MutableList<Bookmark>

    constructor() {
        bookmarks = ArrayList()
    }

    val things: List<Thing?>?
        get() = super.convertToThings(bookmarks)

    constructor(bookmarks: MutableList<Bookmark>) {
        this.bookmarks = bookmarks
    }

    fun getBookmarks(): MutableList<Bookmark> {
        return bookmarks
    }

    fun setBookmarks(bookmarks: MutableList<Bookmark>) {
        this.bookmarks = bookmarks
    }

    fun addBookmark(c: Bookmark, position: Int) {
        bookmarks.add(position, c)
    }

    fun addBookmark(c: Bookmark) {
        bookmarks.add(c)
    }

    fun removeBookmark(index: Int) {
        bookmarks.removeAt(index)
    }

    fun isBookmarkAvailable(c: Bookmark): Boolean {
        var isExists = false
        for (cse in bookmarks) {
            Log.v("TAG", "com.saltechgroup. eee$cse")
            if (cse.name == c.name) {
                isExists = true
                break
            }
        }
        return !isExists
    }

    override fun toString(): String {
        return "Bookmarks{" +
                "bookmarks=" + bookmarks +
                '}'
    }
}
