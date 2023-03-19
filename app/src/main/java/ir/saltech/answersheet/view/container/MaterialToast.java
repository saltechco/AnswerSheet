package ir.saltech.answersheet.view.container;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import ir.saltech.answersheet.R;

public class MaterialToast extends PopupWindow {
	private static final float SCALE = 1.03f;
	private static final float ALPHA = 0.95f;
	private CardView card;
	private TextView textContainer;
	private ImageView imageContainer;
	private AnimatorSet animatorSet;

	public MaterialToast(@NonNull Context context) {
		super(LayoutInflater.from(context).inflate(R.layout.popup_material_toast, null), WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, false);
	}

	public void init(View v) {
		card = v.findViewById(R.id.toast_card);
		imageContainer = v.findViewById(R.id.toast_image);
		textContainer = v.findViewById(R.id.toast_text);
	}

	public void setIcon(Drawable icon) {
		if (icon != null) {
			imageContainer.setVisibility(View.VISIBLE);
			imageContainer.setImageDrawable(icon);
		} else {
			imageContainer.setVisibility(View.GONE);
		}
	}

	public void setText(String text) {
		if (text != null) {
			textContainer.setVisibility(View.VISIBLE);
			textContainer.setText(text);
		} else {
			textContainer.setVisibility(View.GONE);
		}
	}

	public void show(long duration) {
		ObjectAnimator alpha = ObjectAnimator.ofFloat(card, "alpha", 0f, ALPHA);
		alpha.setDuration(400);
		ObjectAnimator scaleX = ObjectAnimator.ofFloat(card, "scaleX", 0f, SCALE, 1f);
		scaleX.setDuration(400);
		ObjectAnimator scaleY = ObjectAnimator.ofFloat(card, "scaleY", 0f, SCALE, 1f);
		scaleY.setDuration(400);
		ObjectAnimator translationY = ObjectAnimator.ofFloat(getContentView(), "translationY", 1f, -8f);
		translationY.setDuration(duration);
		translationY.setStartDelay(150);
		animatorSet = new AnimatorSet();
		animatorSet.playTogether(alpha, scaleX, scaleY, translationY);
		//animatorSet.setDuration(400);
		animatorSet.setStartDelay(50);
		animatorSet.addListener(new Animator.AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animator) {

			}

			@Override
			public void onAnimationEnd(Animator animator) {
				dismiss();
//				Handler handler = new Handler();
//				handler.postDelayed(MaterialToast.this::hide, duration);
			}

			@Override
			public void onAnimationCancel(Animator animator) {

			}

			@Override
			public void onAnimationRepeat(Animator animator) {

			}
		});
		MaterialToast.this.showAtLocation(card, Gravity.CENTER, 0, 750);
		animatorSet.start();
	}

	@Override
	public void dismiss() {
		ObjectAnimator alpha = ObjectAnimator.ofFloat(card, "alpha", ALPHA, 0f);
		animatorSet = new AnimatorSet();
		animatorSet.play(alpha);
		animatorSet.setDuration(300);
		animatorSet.addListener(new Animator.AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animator) {

			}

			@Override
			public void onAnimationEnd(Animator animator) {
				try {
					MaterialToast.super.dismiss();
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
