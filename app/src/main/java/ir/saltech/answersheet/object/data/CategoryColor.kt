package ir.saltech.answersheet.object.data;

import android.graphics.Color;

import androidx.annotation.NonNull;

public final class CategoryColor {
	private static final int BACKGROUND_COLOR_TRANSPARENCY = 38;
	private static final int EDIT_BUTTON_CONTRAST = 0;
	private final int accentColor;
	private final int editButtonColor;
	private final int backgroundColor;

	public CategoryColor(@NonNull int[] rgbColorElements) {
		this.accentColor = Color.rgb(rgbColorElements[0], rgbColorElements[1], rgbColorElements[2]);
		this.editButtonColor = Color.rgb(rgbColorElements[0] + EDIT_BUTTON_CONTRAST, rgbColorElements[1] + EDIT_BUTTON_CONTRAST, rgbColorElements[2] + EDIT_BUTTON_CONTRAST);
		this.backgroundColor = Color.argb(BACKGROUND_COLOR_TRANSPARENCY, rgbColorElements[0], rgbColorElements[1], rgbColorElements[2]);
	}

	public int getBackgroundColor() {
		return backgroundColor;
	}

	public int getAccentColor() {
		return accentColor;
	}

	public int getEditButtonColor() {
		return editButtonColor;
	}

	@NonNull
	@Override
	public String toString() {
		return "CategoryColor{" +
				"color=" + accentColor +
				", backgroundColor=" + backgroundColor +
				'}';
	}
}
