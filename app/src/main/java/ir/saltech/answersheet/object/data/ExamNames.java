package ir.saltech.answersheet.object.data;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class ExamNames extends Things {
    private List<ExamName> examNames;

    public ExamNames() {
        examNames = new ArrayList<>();
    }

    public ExamNames(List<ExamName> examNames) {
        this.examNames = examNames;
    }

    public List<ExamName> getExamNames() {
        return examNames;
    }

    public void setExamNames(List<ExamName> examNames) {
        this.examNames = examNames;
    }

    public List<Thing> getThings() {
        return super.convertToThings(examNames);
    }

    public void addExamName(ExamName c, int position) {
        examNames.add(position, c);
    }

    public void addExamName(ExamName c) {
        examNames.add(c);
    }

    public void removeCourse(int index) {
        examNames.remove(index);
    }

    public boolean isCourseAvailable(ExamName c) {
        boolean isExists = false;
        for (ExamName cse : examNames) {
            Log.v("TAG", "com.saltechgroup. eee" + cse);
            if (cse.getName().equals(c.getName())) {
                isExists = true;
                break;
            }
        }
        return !isExists;
    }

    @NonNull
    @Override
    public String toString() {
        return "Courses{" +
                "examNames=" + examNames +
                '}';
    }
}

