package ir.saltech.answersheet.intf.listener

import ir.saltech.answersheet.`object`.data.Exam

interface ExamEditedListener {
    fun onExamEdited(edited: Exam?)
}
