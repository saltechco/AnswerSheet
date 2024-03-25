package ir.saltech.answersheet.view.container;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import ir.saltech.answersheet.R;

public class MaterialAlert extends PopupWindow {
	public static final int LENGTH_LONG = 8000;
	public static final int LENGTH_SHORT = 3000;
	private static final float ALERT_OUT_OF_SCREEN_Y = -100f;
	@SuppressLint("StaticFieldLeak")
	private static MaterialAlert instance;
	private CardView alertCard;
	private TextView alertText;
	private AnimatorSet animatorSet;
	private long duration;

	public MaterialAlert(@NonNull Context context) {
		super(LayoutInflater.from(context).inflate(R.layout.popup_material_alert, null), WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, false);
	}

	@NonNull
	public static MaterialAlert getInstance(@NonNull Context context) {
		if (instance == null) {
			instance = new MaterialAlert(context);
		}
		return instance;
	}

	public void show(@Nullable String text, long duration) {
		this.duration = duration;
		init(getContentView());
		showCardAnimations();
		if (text != null) {
			alertText.setText(text);
			alertText.setVisibility(VISIBLE);
		} else {
			alertText.setVisibility(GONE);
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			setElevation(13f);
		}
		try {
			showAtLocation(alertCard, Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 135);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void init(View v) {
		alertCard = v.findViewById(R.id.alert_card);
		alertText = v.findViewById(R.id.alert_text);
	}

	private void showCardAnimations() {
		ObjectAnimator showTranslationYAnim = ObjectAnimator.ofFloat(alertCard, "translationY", ALERT_OUT_OF_SCREEN_Y, 1f);
		ObjectAnimator showAlphaAnim = ObjectAnimator.ofFloat(alertCard, "alpha", 0f, 1f);
		animatorSet = new AnimatorSet();
		animatorSet.playTogether(showAlphaAnim, showTranslationYAnim);
		animatorSet.setStartDelay(200);
		animatorSet.setDuration(250);
		animatorSet.addListener(new Animator.AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animator) {
			}

			@Override
			public void onAnimationEnd(Animator a) {
				if (duration == LENGTH_LONG || duration == LENGTH_SHORT) {
					Handler handler = new Handler();
					handler.postDelayed(MaterialAlert.this::dismiss, duration);
				} else {
					throw new IllegalArgumentException("Alert duration must be defined to either of LENGTH_SHORT or LENGTH_LONG!");
				}
			}

			@Override
			public void onAnimationCancel(Animator animator) {
			}

			@Override
			public void onAnimationRepeat(Animator animator) {
			}
		});
		animatorSet.start();
	}

	@Override
	public void dismiss() {
		ObjectAnimator hideTranslationYAnim = ObjectAnimator.ofFloat(alertCard, "translationY", 1f, ALERT_OUT_OF_SCREEN_Y);
		ObjectAnimator hideAlphaAnim = ObjectAnimator.ofFloat(alertCard, "alpha", 1f, 0f);
		animatorSet = new AnimatorSet();
		animatorSet.playTogether(hideTranslationYAnim, hideAlphaAnim);
		animatorSet.setDuration(250);
		animatorSet.addListener(new Animator.AnimatorListener() {
			@SuppressLint("SyntheticAccessor")
			@Override
			public void onAnimationStart(Animator animator) {
			}

			@Override
			public void onAnimationEnd(Animator animator) {
				try {
					MaterialAlert.super.dismiss();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onAnimationCancel(Animator animator) {

			}

			@Override
			public void onAnimationRepeat(Animator animator) {

			}
		});
		animatorSet.start();
	}
}
