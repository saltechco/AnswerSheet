package ir.saltech.answersheet.object.data;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import ir.saltech.answersheet.R;

public class ExamName extends Thing {
    private String name;
    private List<Double> scores = new ArrayList<>();
    private List<String> examsDate = new ArrayList<>();
    private boolean oldScoresRecovered;
    private boolean oldExamsDateRecovered;

    public ExamName() {
        thingName = "نام آزمون";
        super.setIconResource(R.drawable.exam_name);
    }

    public boolean isOldExamsDateRecovered() {
        return oldExamsDateRecovered;
    }

    public void setOldExamsDateRecovered(boolean oldExamsDateRecovered) {
        this.oldExamsDateRecovered = oldExamsDateRecovered;
    }

    public boolean isOldScoresRecovered() {
        return oldScoresRecovered;
    }

    public void setOldScoresRecovered(boolean oldScoresRecovered) {
        this.oldScoresRecovered = oldScoresRecovered;
    }

    public List<String> getExamsDate() {
        return examsDate;
    }

    public void setExamsDate(List<String> examsDate) {
        this.examsDate = examsDate;
    }

    public List<Double> getScores() {
        return scores;
    }

    public void setScores(List<Double> scores) {
        this.scores = scores;
    }

    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
        super.setTitle(name);
    }

    @NonNull
    @Override
    public String toString() {
        return "Course{" +
                "name='" + name + '\'' +
                "scores='" + scores.toString() + '\'' +
                '}';
    }
}
