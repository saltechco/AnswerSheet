package ir.saltech.answersheet.`object`.data

class Bookmark : Thing {
    var name: String = NONE
    var pinColor: BookmarkColor = BookmarkColor()
    var isBookmarkedQuestionsWanted: Boolean = false

    constructor(name: String) {
        this.name = name
    }

    constructor()

    override fun toString(): String {
        return "Bookmark{" +
                "name='" + name + '\'' +
                ", pinColor=" + pinColor +
                ", bookmarkedQuestionsWanted=" + isBookmarkedQuestionsWanted +
                '}'
    }

    companion object {
        const val NONE: String = "none"
    }
}
