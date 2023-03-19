package ir.saltech.answersheet.object.data;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class Questions extends Things {
    private List<Question> questions;
    private List<Category> categories;
    public Questions(List<Question> questions) {
        if (questions != null) {
            this.questions = questions;
        } else {
            this.questions = new ArrayList<>();
        }
        categories = new ArrayList<>();
    }

    public Questions() {
        questions = new ArrayList<>();
        categories = new ArrayList<>();
    }

    public List<Thing> getQuestionThings() {
        return super.convertToThings(questions);
    }

    public List<Thing> getCategoryThings() {
        return super.convertToThings(categories);
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    @NonNull
    @Override
    public String toString() {
        return "Questions{" +
                "answerSheet=" + questions +
                ", categories=" + categories +
                '}';
    }
}
