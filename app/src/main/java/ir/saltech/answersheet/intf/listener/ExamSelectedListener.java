package ir.saltech.answersheet.intf.listener;

import ir.saltech.answersheet.object.data.Exam;

public interface ExamSelectedListener {
    void onExamDeleted(Exam exam, int position, String side);

    void onExamResumed(Exam exam);

    void onExamSuspended(Exam exam);

    void onExamClicked(Exam exam, String side);

    void onAddExamWanted();

    void onExamEdited(Exam exam);
}
