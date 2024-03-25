package ir.saltech.answersheet.`object`.data

class Category : Thing() {
    var id: Int = 0
    var name: String? = null
    var questionsCount: Int = 0
    var questionsRange: IntArray = IntArray(3)
    var color: CategoryColor? = null
    var score: Double = 0.0
    var secondsOfThinkingOnQuestion: Long = 0
    var time: Long = 0
    var isCollapsed: Boolean = false
    var isFinished: Boolean = false
    var isFocused: Boolean = false

    override fun toString(): String {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", questionsCount=" + questionsCount +
                ", questionsRange=" + questionsRange.contentToString() +
                ", color=" + color +
                ", score=" + score +
                ", secondsOfThinkingOnQuestion=" + secondsOfThinkingOnQuestion +
                ", time=" + time +
                ", collapsed=" + isCollapsed +
                ", finished=" + isFinished +
                ", focused=" + isFocused +
                '}'
    }

    internal enum class QRangeMode {
        MinQ, MaxQ, Count
    } // Minified
}
