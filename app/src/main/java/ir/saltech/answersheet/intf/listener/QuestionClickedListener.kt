package ir.saltech.answersheet.intf.listener

import ir.saltech.answersheet.`object`.data.Question

interface QuestionClickedListener {
    /**
     * For Grid question lists
     * @param question The current question
     * @param x pointer (x,y) -> x
     * @param y pointer (x,y) -> y
     */
    fun onQuestionClicked(question: Question?, x: Float, y: Float)

    fun onQuestionAnswered(q: Question?)

    fun onQuestionAnswerDeleted(q: Question?)

    fun onQuestionBookmarkChanged(q: Question?)

    fun onQuestionDeleted(qPosition: Int)

    fun onQuestionCategoryClicked()
}
