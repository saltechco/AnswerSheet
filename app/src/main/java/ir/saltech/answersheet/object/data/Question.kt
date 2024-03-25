package ir.saltech.answersheet.object.data;

import androidx.annotation.NonNull;

public class Question extends Thing {
    private int questionNumber;
    private int selectedChoice;
    @Deprecated
    private int bookmark; // It was the old bookmark object. It has deprecated from version 2.37
    private Bookmark bookmarkObject = new Bookmark();
    private boolean white = true;
    private boolean nowSelected;
    private boolean correct;
    private boolean examEnded;
    private boolean examCorrectionEnded;
    private boolean selected;
    private long timeOfThinking;
    private int correctAnswerChoice;
    private Category category;

    public int getCorrectAnswerChoice() {
        return correctAnswerChoice;
    }

    public void setCorrectAnswerChoice(int correctAnswerChoice) {
        this.correctAnswerChoice = correctAnswerChoice;
    }

    public boolean isNowSelected() {
        return nowSelected;
    }

    public void setNowSelected(boolean nowSelected) {
        this.nowSelected = nowSelected;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public long getTimeOfThinking() {
        return timeOfThinking;
    }

    public void setTimeOfThinking(long timeOfThinking) {
        this.timeOfThinking = timeOfThinking;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public int getQuestionNumber() {
        return questionNumber;
    }

    public void setQuestionNumber(int questionNumber) {
        this.questionNumber = questionNumber;
    }

    public int getSelectedChoice() {
        return selectedChoice;
    }

    public void setSelectedChoice(int selectedChoice) {
        this.selectedChoice = selectedChoice;
    }

    public Bookmark getBookmark() {
        if (bookmark != 0) {
            Bookmark b;
            if (bookmark == 1) {
                b = new Bookmark("پر اهمیّت");
                b.setPinColor(new BookmarkColor(1, true));
            } else {
                b = new Bookmark("دشوار");
                b.setPinColor(new BookmarkColor(0, true));
            }
            return b;
        } else {
            return bookmarkObject;
        }
    }

    public void setBookmark(int bookmark) {
        this.bookmark = bookmark;
    }

    public void setBookmark(Bookmark bookmarkObject) {
        this.bookmarkObject = bookmarkObject;
    }

    public boolean isWhite() {
        return white;
    }

    public void setWhite(boolean white) {
        this.white = white;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public boolean isExamEnded() {
        return examEnded;
    }

    public void setExamEnded(boolean examEnded) {
        this.examEnded = examEnded;
    }

    public boolean isExamCorrectionEnded() {
        return examCorrectionEnded;
    }

    public void setExamCorrectionEnded(boolean examCorrectionEnded) {
        this.examCorrectionEnded = examCorrectionEnded;
    }

    @NonNull
    @Override
    public String toString() {
        return "Question{" +
                "questionNumber=" + questionNumber +
                ", selectedChoice=" + selectedChoice +
                ", bookmark=" + bookmarkObject +
                ", isWhite=" + white +
                ", isCorrect=" + correct +
                ", category=" + category +
                ", isExamEnded=" + examEnded +
                ", isExamCorrectionEnded=" + examCorrectionEnded +
                '}';
    }
}
