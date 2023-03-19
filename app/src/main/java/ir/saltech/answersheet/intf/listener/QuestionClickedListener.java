package ir.saltech.answersheet.intf.listener;

import ir.saltech.answersheet.object.data.Question;

public interface QuestionClickedListener {

    /**
     * For Grid question lists
     * @param question The current question
     * @param x pointer (x,y) -> x
     * @param y pointer (x,y) -> y
     */
    void onQuestionClicked(Question question, float x, float y);

    void onQuestionAnswered(Question q);

    void onQuestionAnswerDeleted(Question q);

    void onQuestionBookmarkChanged(Question q);

    void onQuestionDeleted(int qPosition);

    void onQuestionCategoryClicked();
}
