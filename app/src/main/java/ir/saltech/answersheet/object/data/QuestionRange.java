package ir.saltech.answersheet.object.data;

import androidx.annotation.NonNull;

public class QuestionRange {
    private int firstQuestion;
    private int lastQuestion;
    private int questionsCount;
    private int countPattern;

    private int[] range;

    public QuestionRange() {
        this.firstQuestion = 1;
        this.questionsCount = 10;
        this.countPattern = 1;
        range = new int[4];
    }

    public QuestionRange(int questionsCount, int lastQuestion, int firstQuestion, int countPattern) {
        this.firstQuestion = firstQuestion;
        this.lastQuestion = lastQuestion;
        this.questionsCount = questionsCount;
        this.countPattern = countPattern;
        range = new int[]{firstQuestion, lastQuestion, questionsCount, countPattern};
    }

    public int getFirstQuestion() {
        return firstQuestion;
    }

    public void setFirstQuestion(int firstQuestion) {
        this.firstQuestion = firstQuestion;
    }

    public int getLastQuestion() {
        return lastQuestion;
    }

    public void setLastQuestion(int lastQuestion) {
        this.lastQuestion = lastQuestion;
    }

    public int getQuestionsCount() {
        return questionsCount;
    }

    public void setQuestionsCount(int questionsCount) {
        this.questionsCount = questionsCount;
    }

    public int getCountPattern() {
        return countPattern;
    }

    public void setCountPattern(int countPattern) {
        this.countPattern = countPattern;
    }

    public int[] getRange() {
        return range;
    }

    public void setRange(int[] range) {
        this.range = range;
    }

    @NonNull
    @Override
    public String toString() {
        return "QuestionRange{" +
                "firstQuestion=" + firstQuestion +
                ", lastQuestion=" + lastQuestion +
                ", questionsCount=" + questionsCount +
                ", countPattern=" + countPattern +
                '}';
    }
}
