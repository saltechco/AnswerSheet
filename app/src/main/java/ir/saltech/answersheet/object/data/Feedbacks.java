package ir.saltech.answersheet.object.data;

import androidx.annotation.NonNull;

import java.util.Arrays;
import java.util.List;

@Deprecated
public class Feedbacks {
	private Feedback[] feedbacks;

	@NonNull
	public Feedback[] getFeedbacks() {
		return feedbacks;
	}

	public void setFeedbacks(Feedback[] feedbacks) {
		this.feedbacks = feedbacks;
	}

	@NonNull
	@Override
	public String toString() {
		return "Feedbacks{" +
				"feedbacks=" + Arrays.toString(feedbacks) +
				'}';
	}
}
