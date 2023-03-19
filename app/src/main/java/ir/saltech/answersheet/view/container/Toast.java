package ir.saltech.answersheet.view.container;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;

import ir.saltech.answersheet.R;

public class Toast {
	public static final int WARNING_SIGN = R.drawable.warning;
	public static final long LENGTH_SHORT = 3000;
	public static final long LENGTH_LONG = 5000;
	private final Context context;
	private final String text;
	private final long duration;
	private int imageResId;

	@SuppressLint("CommitPrefEdits")
	private Toast(Context context, String text, int imageResId, long duration) {
		this.text = text;
		this.imageResId = imageResId;
		this.duration = duration;
		this.context = context;
	}

	@SuppressLint("CommitPrefEdits")
	private Toast(Context context, String text, long duration) {
		this.text = text;
		this.duration = duration;
		this.context = context;
	}

	@NonNull
	public static Toast makeText(@NonNull Context context, @NonNull String text, long duration) {
		return new Toast(context, text, duration);
	}

	@NonNull
	public static Toast makeText(@NonNull Context context, @NonNull String text, int imageResId, long duration) {
		return new Toast(context, text, imageResId, duration);
	}

	@SuppressLint("UseCompatLoadingForDrawables")
	public void show() {
		MaterialTextToast toast = new MaterialTextToast(context);
		toast.setText(text);
		if (imageResId != 0) {
			toast.setIcon(context.getResources().getDrawable(imageResId));
		}
		toast.setDuration(duration);
		toast.showToast();
	}
}
