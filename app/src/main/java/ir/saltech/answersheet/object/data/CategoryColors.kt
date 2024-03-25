package ir.saltech.answersheet.object.data;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class CategoryColors {
	private List<CategoryColor> categoryColors;

	public CategoryColors() {
		categoryColors = new ArrayList<>();
	}

	public List<CategoryColor> getCategoryColors() {
		return categoryColors;
	}

	public void setCategoryColors(List<CategoryColor> categoryColors) {
		this.categoryColors = categoryColors;
	}

	public void addCategoryColor(CategoryColor q) {
		categoryColors.add(q);
	}

	@NonNull
	@Override
	public String toString() {
		return "CategoryColors{" +
				"categoryColors=" + categoryColors +
				'}';
	}
}
