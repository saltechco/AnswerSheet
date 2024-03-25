package ir.saltech.answersheet.`object`.data

class Question : Thing() {
    var questionNumber: Int = 0
    var selectedChoice: Int = 0

    @Deprecated("")
    private var bookmark = 0 // It was the old bookmark object. It has deprecated from version 2.37
    private var bookmarkObject = Bookmark()
    var isWhite: Boolean = true
    var isNowSelected: Boolean = false
    var isCorrect: Boolean = false
    var isExamEnded: Boolean = false
    var isExamCorrectionEnded: Boolean = false
    var isSelected: Boolean = false
    var timeOfThinking: Long = 0
    var correctAnswerChoice: Int = 0
    var category: Category? = null

    fun getBookmark(): Bookmark {
        if (bookmark != 0) {
            val b: Bookmark
            if (bookmark == 1) {
                b = Bookmark("پر اهمیّت")
                b.pinColor = BookmarkColor(1, true)
            } else {
                b = Bookmark("دشوار")
                b.pinColor = BookmarkColor(0, true)
            }
            return b
        } else {
            return bookmarkObject
        }
    }

    fun setBookmark(bookmark: Int) {
        this.bookmark = bookmark
    }

    fun setBookmark(bookmarkObject: Bookmark) {
        this.bookmarkObject = bookmarkObject
    }

    override fun toString(): String {
        return "Question{" +
                "questionNumber=" + questionNumber +
                ", selectedChoice=" + selectedChoice +
                ", bookmark=" + bookmarkObject +
                ", isWhite=" + isWhite +
                ", isCorrect=" + isCorrect +
                ", category=" + category +
                ", isExamEnded=" + isExamEnded +
                ", isExamCorrectionEnded=" + isExamCorrectionEnded +
                '}'
    }
}
