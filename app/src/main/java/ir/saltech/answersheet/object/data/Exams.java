package ir.saltech.answersheet.object.data;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class Exams extends Things {
	private List<Exam> examList;

	public Exams() {
		examList = new ArrayList<>();
	}

	public Exams(@NonNull List<Exam> examList) {
		this.examList = examList;
	}

	public List<Thing> getThings() {
		return super.convertToThings(examList);
	}

	@NonNull
	public List<Exam> getExamList() {
		return examList;
	}

	public void setExamList(@NonNull List<Exam> examList) {
		this.examList = examList;
	}

	public void updateCurrentExam(@NonNull Exam currentExam) {
		for (int i = 0; i < examList.size(); i++) {
			if (examList.get(i).getId() == currentExam.getId()) {
				removeExam(i);
				examList.add(i, currentExam);
				break;
			}
		}
	}

	public void addExam(@NonNull Exam newExam) {
		examList.add(newExam);
	}

	public void removeExam(int examIndex) {
		examList.remove(examIndex);
	}

	public void clearList() {
		examList.clear();
	}

	@Override
	public String toString() {
		return "Exams{" +
				"examList=" + examList +
				'}';
	}
}
