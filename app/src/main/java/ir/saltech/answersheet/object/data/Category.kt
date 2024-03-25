package ir.saltech.answersheet.object.data;

import androidx.annotation.NonNull;

import java.util.Arrays;

public class Category extends Thing {
    private int id;
    private String name;
    private int questionsCount;
    private int[] questionsRange = new int[3];
    private CategoryColor color;
    private double score;
    private long secondsOfThinkingOnQuestion;
    private long time;
    private boolean collapsed;
    private boolean finished;
    private boolean focused;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuestionsCount() {
        return questionsCount;
    }

    public void setQuestionsCount(int questionsCount) {
        this.questionsCount = questionsCount;
    }

    public int[] getQuestionsRange() {
        return questionsRange;
    }

    public void setQuestionsRange(int[] questionsRange) {
        this.questionsRange = questionsRange;
    }

    public CategoryColor getColor() {
        return color;
    }

    public void setColor(CategoryColor color) {
        this.color = color;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public long getSecondsOfThinkingOnQuestion() {
        return secondsOfThinkingOnQuestion;
    }

    public void setSecondsOfThinkingOnQuestion(long secondsOfThinkingOnQuestion) {
        this.secondsOfThinkingOnQuestion = secondsOfThinkingOnQuestion;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isCollapsed() {
        return collapsed;
    }

    public void setCollapsed(boolean collapsed) {
        this.collapsed = collapsed;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public boolean isFocused() {
        return focused;
    }

    public void setFocused(boolean focused) {
        this.focused = focused;
    }

    @NonNull
    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", questionsCount=" + questionsCount +
                ", questionsRange=" + Arrays.toString(questionsRange) +
                ", color=" + color +
                ", score=" + score +
                ", secondsOfThinkingOnQuestion=" + secondsOfThinkingOnQuestion +
                ", time=" + time +
                ", collapsed=" + collapsed +
                ", finished=" + finished +
                ", focused=" + focused +
                '}';
    }

    enum QRangeMode {MinQ, MaxQ, Count} // Minified
}
