package ir.saltech.answersheet.object.data;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class Activities extends Things {
	private List<Activity> activities;

	public Activities() {
		activities = new ArrayList<>();
	}

	public Activities(@NonNull List<Activity> Activities) {
		this.activities = Activities;
	}

	@NonNull
	public List<Activity> getActivities() {
		return activities;
	}

	public List<Thing> getThings() {
		return super.convertToThings(activities);
	}

	public void setActivities(@NonNull List<Activity> Activities) {
		this.activities = Activities;
	}

	public void addActivity(@NonNull Activity c, int position) {
		activities.add(position, c);
	}

	public void addActivity(@NonNull Activity c) {
		activities.add(c);
	}

	public void removeActivity(int index) {
		activities.remove(index);
	}

	@NonNull
	@Override
	public String toString() {
		return "Activities{" +
				"Activities=" + activities +
				'}';
	}
}

