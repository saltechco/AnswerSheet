package ir.saltech.answersheet.view.container;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import ir.saltech.answersheet.R;
import ir.saltech.answersheet.intf.listener.DialogDismissListener;
import ir.saltech.answersheet.object.container.Saver;
import ir.saltech.answersheet.view.activity.MainActivity;

public class MaterialDialogFragment extends Fragment {
    public static final String SIDE_ALERT_DIALOG = "side_alert_dialog";
    public static final String SIDE_FRAGMENT_SHOWER = "side_fragment_shower";
    public static final String DISMISS_DIALOG_EVENT = "dismiss_dialog_event";
    public static final String DIALOG_BACKSTACK = "dialog_backstack";
    public static final String DIALOG_CONTENT_BACKSTACK = "dialog_content_backstack";
    private static final int DEFAULT_COLOR = Color.argb(185, 0, 0, 0);
    private ConstraintLayout dialogParent;
    private ImageView iconImage;
    private TextView titleText;
    private TextView messageText;
    private ProgressBar progressBar;
    private LinearLayout buttonsPanel;
    private FragmentContainerView contentFrame;
    private Button primaryButtonView;
    private Button secondaryButtonView;
    private Button naturalButtonView;
    private CardView dialogCard;
    private View backgroundFocus;
    private Fragment contentView;
    private DialogDismissListener dismissListener;
    private BroadcastReceiver dismissReceiver;
    private boolean lastColorState;

    public MaterialDialogFragment() {
        super();
    }

    @NonNull
    protected BroadcastReceiver getDismissReceiver() {
        return dismissReceiver;
    }

    protected void setDismissReceiver(@NonNull BroadcastReceiver dismissReceiver) {
        this.dismissReceiver = dismissReceiver;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_material_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        init(view);
        prepareView();
        onClicks();
    }

    private void prepareView() {
        lastColorState = Saver.getInstance(requireContext()).getLastStatusBarColorState();
        MainActivity.setStatusBarTheme(requireActivity(), false);
    }

    private void onClicks() {
        backgroundFocus.setOnClickListener(view -> dismiss(MaterialDialogFragment.this));
    }

    private void init(View v) {
        buttonsPanel = v.findViewById(R.id.dialog_buttons);
        iconImage = v.findViewById(R.id.dialog_icon_image);
        titleText = v.findViewById(R.id.dialog_title_text);
        messageText = v.findViewById(R.id.dialog_message_text);
        contentFrame = v.findViewById(R.id.dialog_content_frame);
        primaryButtonView = v.findViewById(R.id.dialog_primary_button);
        secondaryButtonView = v.findViewById(R.id.dialog_secondary_button);
        naturalButtonView = v.findViewById(R.id.dialog_natural_button);
        dialogCard = v.findViewById(R.id.dialog_card);
        backgroundFocus = v.findViewById(R.id.dialog_focus);
        dialogParent = v.findViewById(R.id.dialog_parent);
        progressBar = v.findViewById(R.id.dialog_progress_bar);
    }

    protected void setIcon(@Nullable Drawable icon) {
        if (icon != null) {
            iconImage.setVisibility(View.VISIBLE);
            iconImage.setImageDrawable(icon);
        } else {
            iconImage.setVisibility(GONE);
            iconImage.setImageResource(R.drawable.text);
        }
    }

    protected void setTitle(@Nullable String title) {
        titleText.setText(title);
        if (title != null) {
            titleText.setVisibility(View.VISIBLE);
        } else {
            titleText.setVisibility(GONE);
        }
    }

    protected void showProgressBar(boolean enable) {
        if (enable) {
            progressBar.setVisibility(VISIBLE);
        } else {
            progressBar.setVisibility(GONE);
        }
    }

    protected void setMessage(@Nullable String message) {
        messageText.setText(message);
        if (message != null) {
            messageText.setVisibility(View.VISIBLE);
            messageText.setMovementMethod(new ScrollingMovementMethod());
        } else {
            messageText.setVisibility(GONE);
        }
    }

    protected void setCancelable(boolean cancelable) {
        backgroundFocus.setClickable(cancelable);
    }

    protected void setPositiveButton(@Nullable String primaryButton, @Nullable View.OnClickListener clickListener) {
        if (primaryButton != null) {
            buttonsPanel.setVisibility(VISIBLE);
            primaryButtonView.setText(primaryButton);
            primaryButtonView.setOnClickListener(clickListener);
            primaryButtonView.setVisibility(View.VISIBLE);
        } else {
            primaryButtonView.setVisibility(GONE);
            if (secondaryButtonView.getVisibility() == GONE && primaryButtonView.getVisibility() == GONE) {
                buttonsPanel.setVisibility(GONE);
            }
        }
    }

    protected void setNegativeButton(@Nullable String secondaryButton, @Nullable View.OnClickListener clickListener) {
        if (secondaryButton != null) {
            buttonsPanel.setVisibility(VISIBLE);
            secondaryButtonView.setText(secondaryButton);
            secondaryButtonView.setOnClickListener(clickListener);
            secondaryButtonView.setVisibility(View.VISIBLE);
        } else {
            secondaryButtonView.setVisibility(GONE);
            if (secondaryButtonView.getVisibility() == GONE && primaryButtonView.getVisibility() == GONE) {
                buttonsPanel.setVisibility(GONE);
            }
        }
    }

    protected void setNaturalButton(@Nullable String naturalButton, @Nullable View.OnClickListener clickListener) {
        if (naturalButton != null) {
            naturalButtonView.setText(naturalButton);
            naturalButtonView.setOnClickListener(clickListener);
            naturalButtonView.setVisibility(View.VISIBLE);
        } else {
            naturalButtonView.setVisibility(GONE);
        }
    }

    protected void setContentView(@Nullable Fragment contentView, boolean matchParent) {
        if (contentView != null) {
            this.contentView = contentView;
            contentFrame.setVisibility(View.VISIBLE);
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) dialogCard.getLayoutParams();
            if (matchParent) {
                params.height = ConstraintLayout.LayoutParams.MATCH_PARENT;
            } else {
                params.height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
            }
            dialogCard.setLayoutParams(params);
            requireActivity().getSupportFragmentManager().beginTransaction().setReorderingAllowed(true).add(contentFrame.getId(), contentView).addToBackStack(DIALOG_CONTENT_BACKSTACK).commit();
            Log.d("TAG", "Fragments: " + requireActivity().getSupportFragmentManager().getFragments());
        } else {
            contentFrame.setVisibility(GONE);
        }
    }

    public void dismiss(@NonNull MaterialDialogFragment dialogFragment) {
        ValueAnimator hideBackgroundFocus = ValueAnimator.ofObject(new ArgbEvaluator(), DEFAULT_COLOR, Color.argb(0, 33, 37, 41));
        hideBackgroundFocus.addUpdateListener(valueAnimator -> backgroundFocus.setBackgroundColor((int) valueAnimator.getAnimatedValue()));
        ObjectAnimator hideCardViewAlpha = ObjectAnimator.ofFloat(dialogCard, "alpha", 1f, 0f);
        ObjectAnimator hideCardViewScaleX = ObjectAnimator.ofFloat(dialogCard, "scaleX", 1f, 0f);
        ObjectAnimator hideCardViewScaleY = ObjectAnimator.ofFloat(dialogCard, "scaleY", 1f, 0f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(hideBackgroundFocus, hideCardViewAlpha, hideCardViewScaleX, hideCardViewScaleY);
        animatorSet.setDuration(250);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @SuppressLint("SyntheticAccessor")
            @Override
            public void onAnimationEnd(Animator animator) {
                if (contentFrame.getVisibility() == VISIBLE) {
                    try {
                        requireActivity().getSupportFragmentManager().beginTransaction().remove(dialogFragment).remove(contentView).commit();
                        if (((MaterialFragmentShower) dialogFragment).isHasContent()) {
                            MaterialDialogFragment.this.show();
                        }
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    }
                } else {
                    requireActivity().getSupportFragmentManager().beginTransaction().remove(dialogFragment).commit();
                }
                Log.d("TAG", "Fragments POPED: " + requireActivity().getSupportFragmentManager().getFragments());
                dialogParent.setVisibility(GONE);
                if (dismissListener != null) {
                    dismissListener.onDismissed();
                }
                MainActivity.setStatusBarTheme(requireActivity(), lastColorState);
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

    protected void show() {
        ValueAnimator showBackgroundFocus = ValueAnimator.ofObject(new ArgbEvaluator(), Color.argb(0, 33, 37, 41), DEFAULT_COLOR);
        showBackgroundFocus.addUpdateListener(valueAnimator -> backgroundFocus.setBackgroundColor((int) valueAnimator.getAnimatedValue()));
        ObjectAnimator showCardViewAlpha = ObjectAnimator.ofFloat(dialogCard, "alpha", 0f, 1f);
        ObjectAnimator showCardViewScaleX = ObjectAnimator.ofFloat(dialogCard, "scaleX", 0f, 1.02f, 1f);
        ObjectAnimator showCardViewScaleY = ObjectAnimator.ofFloat(dialogCard, "scaleY", 0f, 1.02f, 1f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(showBackgroundFocus, showCardViewAlpha, showCardViewScaleX, showCardViewScaleY);
        animatorSet.setStartDelay(100);
        animatorSet.setDuration(250);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                dialogParent.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
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

    public void setOnDismissListener(DialogDismissListener dismissListener) {
        this.dismissListener = dismissListener;
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(dismissReceiver, new IntentFilter(DISMISS_DIALOG_EVENT));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(dismissReceiver);
    }
}
