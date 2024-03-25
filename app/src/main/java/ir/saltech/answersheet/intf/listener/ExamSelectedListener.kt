package ir.saltech.answersheet.intf.listener

import ir.saltech.answersheet.`object`.data.Exam

interface ExamSelectedListener {
    fun onExamDeleted(exam: Exam?, position: Int, side: String)

    fun onExamResumed(exam: Exam)

    fun onExamSuspended(exam: Exam?)

    fun onExamClicked(exam: Exam, side: String)

    fun onAddExamWanted()

    fun onExamEdited(exam: Exam?)
}
