package ir.saltech.answersheet.view.fragment;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import ir.saltech.answersheet.R;

public class CollapsablePanelFragment extends Fragment {
    private static final double MOVE_SPEED = 1.0;
    private static final int BACK_TO_NEUTRAL_STATUS_ANIM_DURATION = 250;
    private ConstraintLayout parentLayout;
    private ConstraintLayout collapsablePanel;
    private View focusBackground;
    private int currentY;
    private int lastY;
    private int deltaY;
    private Fragment fragment;
    private AnimatorSet animatorSet;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_collapsable_panel, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        showPanelAnimation();
        showFragment();
        onClicks();
    }

    public void setContentFragment(@NonNull Fragment fragment) {
        this.fragment = fragment;
    }

    private void showFragment() {
        if (fragment != null) {
            requireActivity().getSupportFragmentManager().beginTransaction().add(R.id.collapsable_panel_fragment_container, fragment).commit();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void onClicks() {
        focusBackground.setOnClickListener(v -> hidePanelAnimation());
        collapsablePanel.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                currentY = Math.round(event.getRawY());
                deltaY = -currentY + lastY;
                lastY = currentY;
                if (deltaY >= -50 && deltaY <= 100) {
                    if ((collapsablePanel.getTranslationY() >= 0f && deltaY < 0) || (collapsablePanel.getTranslationY() >= 5f && deltaY > 0)) {
                        collapsablePanel.setTranslationY(Math.round((float) (collapsablePanel.getTranslationY() - (deltaY * MOVE_SPEED))));
                        //Log.i("TOUCH_ACTION", "Current TRANS Y: " + collapsablePanel.getTranslationY() + ", DELTA Y: " + deltaY + ", HEIGHT: " + collapsablePanel.getMeasuredHeight());
                    } else if (collapsablePanel.getTranslationY() < 0f) {
                        collapsablePanel.setTranslationY(0f);
                    }
                }
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                int panelHeight = collapsablePanel.getMeasuredHeight();
                float currentTranslationY = collapsablePanel.getTranslationY();
                if (currentTranslationY <= (float) (panelHeight / 2)) {
                    ObjectAnimator raiseUpView = ObjectAnimator.ofFloat(collapsablePanel, "translationY", currentTranslationY, 0f);
                    raiseUpView.setDuration(BACK_TO_NEUTRAL_STATUS_ANIM_DURATION);
                    raiseUpView.start();
                } else {
                    hidePanelAnimation(currentTranslationY);
                }
                return false;
            } else {
                return true;
            }
        });
    }

    private void hidePanelAnimation(float currentTranslationY) {
        @SuppressLint("Recycle") ValueAnimator hideBackgroundFocus = ValueAnimator.ofObject(new ArgbEvaluator(), Color.argb(180, 0, 0, 0), Color.argb(0, 0, 0, 0));
        hideBackgroundFocus.addUpdateListener(valueAnimator -> focusBackground.setBackgroundColor((int) valueAnimator.getAnimatedValue()));
        ObjectAnimator takeDownPanel = ObjectAnimator.ofFloat(collapsablePanel, "translationY", currentTranslationY, parentLayout.getMeasuredHeight() + 250);
        animatorSet = new AnimatorSet();
        animatorSet.playTogether(hideBackgroundFocus, takeDownPanel);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animation) {
            }

            @Override
            public void onAnimationEnd(@NonNull Animator animation) {
                closePanel();
            }

            @Override
            public void onAnimationCancel(@NonNull Animator animation) {

            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animation) {

            }
        });
        animatorSet.setDuration(BACK_TO_NEUTRAL_STATUS_ANIM_DURATION);
        animatorSet.start();
    }

    private void hidePanelAnimation() {
        @SuppressLint("Recycle") ValueAnimator hideBackgroundFocus = ValueAnimator.ofObject(new ArgbEvaluator(), Color.argb(180, 0, 0, 0), Color.argb(0, 0, 0, 0));
        hideBackgroundFocus.addUpdateListener(valueAnimator -> focusBackground.setBackgroundColor((int) valueAnimator.getAnimatedValue()));
        ObjectAnimator takeDownPanel = ObjectAnimator.ofFloat(collapsablePanel, "translationY", 0f, parentLayout.getMeasuredHeight() + 250);
        animatorSet = new AnimatorSet();
        animatorSet.playTogether(hideBackgroundFocus, takeDownPanel);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animation) {
            }

            @Override
            public void onAnimationEnd(@NonNull Animator animation) {
                closePanel();
            }

            @Override
            public void onAnimationCancel(@NonNull Animator animation) {

            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animation) {

            }
        });
        animatorSet.setDuration(BACK_TO_NEUTRAL_STATUS_ANIM_DURATION);
        animatorSet.start();
    }

    private void closePanel() {
        collapsablePanel.setVisibility(View.GONE);
        focusBackground.setClickable(false);
        requireActivity().getSupportFragmentManager().beginTransaction().remove(CollapsablePanelFragment.this).commit();
    }

    private void showPanelAnimation() {
        @SuppressLint("Recycle") ValueAnimator showBackgroundFocus = ValueAnimator.ofObject(new ArgbEvaluator(), Color.argb(0, 0, 0, 0), Color.argb(180, 0, 0, 0));
        showBackgroundFocus.addUpdateListener(valueAnimator -> focusBackground.setBackgroundColor((int) valueAnimator.getAnimatedValue()));
        ObjectAnimator startUpPanelAnim = ObjectAnimator.ofFloat(collapsablePanel, "translationY", 2000f, 0f);
        animatorSet = new AnimatorSet();
        animatorSet.playTogether(showBackgroundFocus, startUpPanelAnim);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animation) {
                collapsablePanel.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(@NonNull Animator animation) {
            }

            @Override
            public void onAnimationCancel(@NonNull Animator animation) {

            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animation) {

            }
        });
        animatorSet.setStartDelay(500);
        animatorSet.setDuration(BACK_TO_NEUTRAL_STATUS_ANIM_DURATION);
        animatorSet.start();
    }

    private void init(View v) {
        collapsablePanel = v.findViewById(R.id.collapsable_panel);
        parentLayout = v.findViewById(R.id.parent_layout);
        focusBackground = v.findViewById(R.id.focus_background);
    }
}
