package ir.saltech.answersheet.`object`.data

@Deprecated("")
class Feedbacks {
    var feedbacks: Array<Feedback> = arrayOf()

    override fun toString(): String {
        return "Feedbacks{" +
                "feedbacks=" + feedbacks.contentToString() +
                '}'
    }
}
