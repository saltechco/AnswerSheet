package ir.saltech.answersheet.object.data;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class Categories extends Things {
	private List<Category> categories;

	public Categories() {
		categories = new ArrayList<>();
	}

	public Categories(List<Category> categories) {
		this.categories = categories;
	}

	public List<Thing> getThings() {
		return super.convertToThings(categories);
	}

	public List<Category> getCategories() {
		return categories;
	}

	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}

	public void addCategory(Category c, int position) {
		categories.add(position, c);
	}

	public void addCategory(Category c) {
		categories.add(c);
	}

	public void removeCategory(int index) {
		categories.remove(index);
	}

	public boolean isCategoryAvailable(Category c) {
		boolean isExists = false;
		for (Category cse : categories) {
			Log.v("TAG", "ir.saltech eee" + cse);
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
		return "Categories{" +
				"categories=" + categories +
				'}';
	}
}

