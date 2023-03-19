package ir.saltech.answersheet.view.fragment;

import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.airbnb.lottie.LottieAnimationView;

import java.util.concurrent.Executor;

import ir.saltech.answersheet.R;
import ir.saltech.answersheet.intf.listener.AuthenticationChangedListener;
import ir.saltech.answersheet.object.container.Saver;
import ir.saltech.answersheet.view.activity.MainActivity;
import ir.saltech.answersheet.view.container.Toast;

public class AuthFragment extends Fragment {
    public static final float WRONG_PASSWORD_VIBRATION_EFFECT_LENGTH = 25f;
    public static final int CLEAR_PASSWORD_FIELD_DELAY_MILLIS = 75;
    public static final String LOAD_FROM_SETTINGS = "load_from_settings";
    private final AuthenticationChangedListener authenticationListener;
    private LinearLayout passwordLayout;
    private LinearLayout secureKeypad;
    private LinearLayout enterThroughFingerprint;
    private LinearLayout passkeyDigitsLayout;
    private TextView title;
    private TextView subtitle;
    private ImageView passkeyDigit1;
    private ImageView passkeyDigit2;
    private ImageView passkeyDigit3;
    private ImageView passkeyDigit4;
    private LottieAnimationView lockAnim;
    private Vibrator vibrator;
    private StringBuilder password;
    private AnimatorSet animatorSet;
    private boolean isBackspacePressed;
    private View spaceView;
    private boolean loadFromSettings;
    private String newPassword;
    private String loadedPassword;
    private boolean newPasswordWanted;

    public AuthFragment(AuthenticationChangedListener authenticationListener) {
        this.authenticationListener = authenticationListener;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getArgs();
        return inflater.inflate(R.layout.fragment_auth, container, false);
    }

    private void getArgs() {
        Bundle extras = getArguments();
        if (extras != null) {
            if (extras.containsKey(LOAD_FROM_SETTINGS)) {
                loadFromSettings = extras.getBoolean(LOAD_FROM_SETTINGS, false);
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        setLockAnim();
        showPasswordDots();
        loadedPassword = String.valueOf(Saver.getInstance(requireContext()).getAppPassword());
        if (loadFromSettings) {
            if (Saver.getInstance(requireContext()).getAppPassword() == Saver.DEF_PASSKEY) {
                title.setText("گذرواژه جدید خود را وارد کنید");
            } else {
                title.setText("تعیین کنید که این شمایید");
            }
            enterThroughFingerprint.setVisibility(View.INVISIBLE);
            subtitle.setVisibility(View.INVISIBLE);
        }
        onClicks(view);
    }

    private void setLockAnim() {
        lockAnim.addAnimatorUpdateListener(animation -> {
            if (((float) animation.getAnimatedValue()) >= 0.3f) {
                lockAnim.pauseAnimation();
                showPasswordLayoutAnim();
            }
        });
    }

    private double getScreenSize() {
        Point point = new Point();
        ((WindowManager) requireActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRealSize(point);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int width = point.x;
        int height = point.y;
        double wi = (double) width / (double) displayMetrics.xdpi;
        double hi = (double) height / (double) displayMetrics.ydpi;
        double x = Math.pow(wi, 2);
        double y = Math.pow(hi, 2);
        return Math.round((Math.sqrt(x + y)) * 10.0) / 10.0;
    }

    private void showPasswordLayoutAnim() {
        if (getScreenSize() >= MainActivity.MIN_OF_NORMAL_SCREEN_SIZE) {
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) lockAnim.getLayoutParams();
            @SuppressLint("Recycle") ValueAnimator topMarginAnimator = ValueAnimator.ofInt(params.topMargin, (int) (params.topMargin + getResources().getDimension(R.dimen.status_bar_margin)));
            topMarginAnimator.addUpdateListener(animation -> {
                params.topMargin = (int) animation.getAnimatedValue();
                lockAnim.setLayoutParams(params);
            });
            topMarginAnimator.setStartDelay(25);
            topMarginAnimator.setDuration(250);
            topMarginAnimator.start();
            ObjectAnimator spaceViewTranslationY = ObjectAnimator.ofFloat(spaceView, "translationY", -100f, 0f);
            spaceViewTranslationY.setDuration(100);
            spaceViewTranslationY.start();
            //params.topMargin += getResources().getDimension(R.dimen.status_bar_margin);
            //lockAnim.setLayoutParams(params);
            //spaceView.setVisibility(View.VISIBLE);
        }
        ObjectAnimator lockAnimScaleX = ObjectAnimator.ofFloat(lockAnim, "scaleX", 4f, 2f);
        ObjectAnimator lockAnimScaleY = ObjectAnimator.ofFloat(lockAnim, "scaleY", 4f, 2f);
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) lockAnim.getLayoutParams();
        float currentVBias = params.verticalBias;
        ValueAnimator lockAnimVerticalBias = ValueAnimator.ofFloat(currentVBias, 0f);
        lockAnimVerticalBias.addUpdateListener(animation -> {
            params.verticalBias = (float) animation.getAnimatedValue();
            lockAnim.setLayoutParams(params);
        });
        animatorSet = new AnimatorSet();
        animatorSet.playTogether(lockAnimScaleX, lockAnimScaleY, lockAnimVerticalBias);
        animatorSet.setDuration(600);
        animatorSet.start();
        ObjectAnimator passwordLayoutAlpha = ObjectAnimator.ofFloat(passwordLayout, "alpha", 0f, 1f);
        ObjectAnimator passwordLayoutTranslationY = ObjectAnimator.ofFloat(passwordLayout, "translationY", 1000f, 1f);
        animatorSet = new AnimatorSet();
        animatorSet.playTogether(passwordLayoutAlpha, passwordLayoutTranslationY);
        animatorSet.setStartDelay(275);
        animatorSet.setDuration(350);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animation) {
                passwordLayout.setVisibility(View.VISIBLE);
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
        animatorSet.start();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void showPasswordDots() {
        switch (password.length()) {
            case 0:
                passkeyDigit1.setImageDrawable(getResources().getDrawable(R.drawable.password_block_focused));
                passkeyDigit2.setImageDrawable(getResources().getDrawable(R.drawable.password_block_none));
                passkeyDigit3.setImageDrawable(getResources().getDrawable(R.drawable.password_block_none));
                passkeyDigit4.setImageDrawable(getResources().getDrawable(R.drawable.password_block_none));
                if (isBackspacePressed) {
                    setDigitEnterAnimation(passkeyDigit1);
                }
                break;
            case 1:
                passkeyDigit1.setImageDrawable(getResources().getDrawable(R.drawable.password_block_filled));
                passkeyDigit2.setImageDrawable(getResources().getDrawable(R.drawable.password_block_focused));
                passkeyDigit3.setImageDrawable(getResources().getDrawable(R.drawable.password_block_none));
                passkeyDigit4.setImageDrawable(getResources().getDrawable(R.drawable.password_block_none));
                if (isBackspacePressed) {
                    setDigitEnterAnimation(passkeyDigit2);
                } else {
                    setDigitEnterAnimation(passkeyDigit1);
                }
                break;
            case 2:
                passkeyDigit1.setImageDrawable(getResources().getDrawable(R.drawable.password_block_filled));
                passkeyDigit2.setImageDrawable(getResources().getDrawable(R.drawable.password_block_filled));
                passkeyDigit3.setImageDrawable(getResources().getDrawable(R.drawable.password_block_focused));
                passkeyDigit4.setImageDrawable(getResources().getDrawable(R.drawable.password_block_none));
                if (isBackspacePressed) {
                    setDigitEnterAnimation(passkeyDigit3);
                } else {
                    setDigitEnterAnimation(passkeyDigit2);
                }
                break;
            case 3:
                passkeyDigit1.setImageDrawable(getResources().getDrawable(R.drawable.password_block_filled));
                passkeyDigit2.setImageDrawable(getResources().getDrawable(R.drawable.password_block_filled));
                passkeyDigit3.setImageDrawable(getResources().getDrawable(R.drawable.password_block_filled));
                passkeyDigit4.setImageDrawable(getResources().getDrawable(R.drawable.password_block_focused));
                if (isBackspacePressed) {
                    setDigitEnterAnimation(passkeyDigit4);
                } else {
                    setDigitEnterAnimation(passkeyDigit3);
                }
                break;
            case 4:
                passkeyDigit1.setImageDrawable(getResources().getDrawable(R.drawable.password_block_filled));
                passkeyDigit2.setImageDrawable(getResources().getDrawable(R.drawable.password_block_filled));
                passkeyDigit3.setImageDrawable(getResources().getDrawable(R.drawable.password_block_filled));
                passkeyDigit4.setImageDrawable(getResources().getDrawable(R.drawable.password_block_filled));
                if (!isBackspacePressed) {
                    setDigitEnterAnimation(passkeyDigit4);
                }
                if (loadFromSettings) {
                    if (Saver.getInstance(requireContext()).getAppPassword() == Saver.DEF_PASSKEY || newPasswordWanted) {
                        if (newPassword == null) {
                            if (loadedPassword != null) {
                                if (!password.toString().equals(loadedPassword)) {
                                    setupForReEnterNewPassword();
                                } else {
                                    Toast.makeText(requireContext(), "گذرواژه جدید نمیتواند مثل گذرواژه قدیمی باشد!", Toast.WARNING_SIGN, Toast.LENGTH_LONG).show();
                                    password = new StringBuilder();
                                    clearPassword();
                                }
                            } else {
                                setupForReEnterNewPassword();
                            }
                        } else {
                            checkPassword(newPassword);
                        }
                    } else {
                        checkPassword(loadedPassword);
                    }
                } else {
                    checkPassword(loadedPassword);
                }
                break;
            default:
                break;
        }
    }

    private void setupForReEnterNewPassword() {
        title.setText("گذرواژه جدید خود را دوباره وارد کنید");
        subtitle.setText("لطفاً با دقت این کار را انجام دهید!");
        subtitle.setTextColor(getResources().getColor(R.color.edu_level_bad));
        subtitle.setVisibility(View.VISIBLE);
        newPassword = password.toString();
        clearPassword();
    }

    private void setDigitEnterAnimation(ImageView passkeyDigit) {
        ObjectAnimator enterPassKeyScaleX = ObjectAnimator.ofFloat(passkeyDigit, "scaleX", 1.25f, 1.4f, 1.25f);
        ObjectAnimator enterPassKeyScaleY = ObjectAnimator.ofFloat(passkeyDigit, "scaleY", 1.25f, 1.4f, 1.25f);
        animatorSet = new AnimatorSet();
        animatorSet.playTogether(enterPassKeyScaleX, enterPassKeyScaleY);
        animatorSet.setDuration(250);
        animatorSet.start();
        vibrator.vibrate(50);
    }

    private void setCorrectPasswordDigitAnimation(ImageView passkeyDigit) {
        ObjectAnimator enterPassKeyScaleX = ObjectAnimator.ofFloat(passkeyDigit, "scaleX", 1.25f, 1.4f, 1.25f);
        ObjectAnimator enterPassKeyScaleY = ObjectAnimator.ofFloat(passkeyDigit, "scaleY", 1.25f, 1.4f, 1.25f);
        animatorSet = new AnimatorSet();
        animatorSet.playTogether(enterPassKeyScaleX, enterPassKeyScaleY);
        animatorSet.setDuration(250);
        animatorSet.start();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void checkPassword(String currentPassword) {
        if (!password.toString().equals(currentPassword)) {
            passkeyDigit1.setImageDrawable(getResources().getDrawable(R.drawable.password_block_filled_wrong));
            passkeyDigit2.setImageDrawable(getResources().getDrawable(R.drawable.password_block_filled_wrong));
            passkeyDigit3.setImageDrawable(getResources().getDrawable(R.drawable.password_block_filled_wrong));
            passkeyDigit4.setImageDrawable(getResources().getDrawable(R.drawable.password_block_filled_wrong));
            @SuppressLint("Recycle") ObjectAnimator translationXWrongPasswordAnim = ObjectAnimator.ofFloat(passkeyDigitsLayout, "translationX", 1f, -WRONG_PASSWORD_VIBRATION_EFFECT_LENGTH, WRONG_PASSWORD_VIBRATION_EFFECT_LENGTH, 1f);
            translationXWrongPasswordAnim.setStartDelay(50);
            translationXWrongPasswordAnim.setDuration(100);
            translationXWrongPasswordAnim.setRepeatCount(5);
            translationXWrongPasswordAnim.start();
            translationXWrongPasswordAnim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(@NonNull Animator animation) {

                }

                @Override
                public void onAnimationEnd(@NonNull Animator animation) {
                    clearPassword();
                }

                @Override
                public void onAnimationCancel(@NonNull Animator animation) {

                }

                @Override
                public void onAnimationRepeat(@NonNull Animator animation) {

                }
            });
            vibrator.vibrate(500);
        } else {
            secureKeypad.setClickable(false);
            new Handler().postDelayed(() -> {
                setCorrectPasswordDigitAnimation(passkeyDigit1);
                setCorrectPasswordDigitAnimation(passkeyDigit2);
                setCorrectPasswordDigitAnimation(passkeyDigit3);
                setCorrectPasswordDigitAnimation(passkeyDigit4);
                onAuthenticationSucceed();
            }, 100);
        }
    }

    private void clearPassword() {
        password = new StringBuilder();
        new Handler().postDelayed(() -> {
            passkeyDigit4.setImageDrawable(getResources().getDrawable(R.drawable.password_block_none));
            new Handler().postDelayed(() -> {
                passkeyDigit3.setImageDrawable(getResources().getDrawable(R.drawable.password_block_none));
                new Handler().postDelayed(() -> {
                    passkeyDigit2.setImageDrawable(getResources().getDrawable(R.drawable.password_block_none));
                    new Handler().postDelayed(() -> {
                        passkeyDigit1.setImageDrawable(getResources().getDrawable(R.drawable.password_block_none));
                        new Handler().postDelayed(() -> passkeyDigit1.setImageDrawable(getResources().getDrawable(R.drawable.password_block_focused)), CLEAR_PASSWORD_FIELD_DELAY_MILLIS);
                    }, CLEAR_PASSWORD_FIELD_DELAY_MILLIS);
                }, CLEAR_PASSWORD_FIELD_DELAY_MILLIS);
            }, CLEAR_PASSWORD_FIELD_DELAY_MILLIS);
        }, CLEAR_PASSWORD_FIELD_DELAY_MILLIS);
    }

    private void onClicks(View view) {
        enterThroughFingerprint.setOnClickListener(v -> useFingerprintAuthentication());
        setupKeypad(view);
    }

    private void setupKeypad(View view) {
        for (int i = 0; i < secureKeypad.getChildCount(); i++) {
            LinearLayout keypadRow = (LinearLayout) secureKeypad.getChildAt(i);
            for (int j = 0; j < keypadRow.getChildCount(); j++) {
                keypadRow.getChildAt(j).setOnClickListener(v -> {
                    if (v.getId() != R.id.keypad_backspace) {
                        if (password.length() < 4)
                            password.append(((Button) v).getText());
                    } else {
                        if (password.length() != 0)
                            password.deleteCharAt(password.length() - 1);
                    }
                    isBackspacePressed = v.getId() == R.id.keypad_backspace;
                    view.findViewById(R.id.keypad_backspace).setClickable(password.length() > 0 && password.length() < 4);
                    showPasswordDots();
                });
            }
        }
    }

    private void useFingerprintAuthentication() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            BiometricManager biometricManager = BiometricManager.from(requireActivity());
            if (biometricManager.canAuthenticate(BIOMETRIC_STRONG | DEVICE_CREDENTIAL) == BiometricManager.BIOMETRIC_SUCCESS) {
                KeyguardManager keyguardManager = (KeyguardManager) requireContext().getSystemService(Context.KEYGUARD_SERVICE);
                if (keyguardManager.isDeviceSecure()) {
                    showFingerprintPrompt();
                }
            }
        }
    }

    private void showFingerprintPrompt() {
        Executor executor = ContextCompat.getMainExecutor(requireActivity());
        BiometricPrompt biometricPrompt = new BiometricPrompt(requireActivity(), executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                onAuthenticationSucceed();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }
        });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder().setTitle("ورود به پاسخنامه").setSubtitle("تعیین کنید که این شمایید!").setNegativeButtonText("درعوض، استفاده از پین").build();

        biometricPrompt.authenticate(promptInfo);
    }

    private void onAuthenticationSucceed() {
        if (loadFromSettings) {
            if (newPassword != null) {
                newPasswordWanted = false;
                Saver.getInstance(requireContext()).setAppPassword(Integer.parseInt(newPassword));
                Toast.makeText(requireContext(), "گذرواژه جدید شما تنظیم شد.", Toast.WARNING_SIGN, Toast.LENGTH_SHORT).show();
                requireActivity().getSupportFragmentManager().beginTransaction().remove(this).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE).commit();
            } else {
                title.setText("گذرواژه جدید خود را وارد کنید.");
                subtitle.setVisibility(View.INVISIBLE);
                newPasswordWanted = true;
                clearPassword();
            }
        } else {
            if (authenticationListener != null) {
                authenticationListener.onAuthenticationSucceed();
            }
            requireActivity().getSupportFragmentManager().beginTransaction().remove(this).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE).commit();
        }
    }

    private void init(@NonNull View v) {
        password = new StringBuilder();
        vibrator = (Vibrator) requireContext().getSystemService(Context.VIBRATOR_SERVICE);
        secureKeypad = v.findViewById(R.id.secure_keypad);
        title = v.findViewById(R.id.textView3);
        subtitle = v.findViewById(R.id.textView4);
        lockAnim = v.findViewById(R.id.img_lock_anim);
        spaceView = v.findViewById(R.id.space_view);
        passwordLayout = v.findViewById(R.id.password_layout);
        enterThroughFingerprint = v.findViewById(R.id.enter_through_fingerprint);
        passkeyDigitsLayout = v.findViewById(R.id.passkey_digits_layout);
        passkeyDigit1 = v.findViewById(R.id.passkey_digit1);
        passkeyDigit2 = v.findViewById(R.id.passkey_digit2);
        passkeyDigit3 = v.findViewById(R.id.passkey_digit3);
        passkeyDigit4 = v.findViewById(R.id.passkey_digit4);
    }
}