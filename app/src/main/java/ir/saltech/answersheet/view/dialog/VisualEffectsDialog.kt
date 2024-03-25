package ir.saltech.answersheet.view.dialog;

import static android.graphics.PorterDuff.Mode.SRC_IN;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import ir.saltech.answersheet.R;
import ir.saltech.answersheet.object.container.Saver;

public class VisualEffectsDialog extends Fragment {
    private Saver saver;
    private Vibrator vibrator;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch keepScreenOn;
    private ImageView keepScreenOnImg;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch vibrationEffects;
    private ImageView vibrationEffectsImg;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch musicEffects;
    private ImageView musicEffectsImg;
    private AnimatorSet animatorSet;
    private MediaPlayer player;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_visual_effects, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        saver = Saver.getInstance(getContext());
        vibrator = (Vibrator) requireContext().getSystemService(Context.VIBRATOR_SERVICE);
        loadRecentSettings();
        onClicks();
    }

    private void onClicks() {
        keepScreenOn.setOnCheckedChangeListener((compoundButton, b) -> {
            saver = Saver.getInstance(getContext());
            ValueAnimator animatorScaleX = ValueAnimator.ofFloat(1f, 0.6f, 1.05f, 1f);
            animatorScaleX.addUpdateListener(valueAnimator -> {
                float animatedValue = (float) valueAnimator.getAnimatedValue();
                keepScreenOnImg.setScaleX(animatedValue);
                if (animatedValue > 1f) {
                    if (b) {
                        keepScreenOnImg.setImageResource(R.drawable.keep_screen_on);
                    } else {
                        keepScreenOnImg.setImageResource(R.drawable.keep_screen_on_disable);
                    }
                    showStatusOfSetting(keepScreenOn, keepScreenOnImg, b);
                }
            });
            ObjectAnimator animatorScaleY = ObjectAnimator.ofFloat(keepScreenOnImg, "scaleY", 1f, 0.6f, 1.05f, 1f);
            animatorSet = new AnimatorSet();
            animatorSet.playTogether(animatorScaleX, animatorScaleY);
            animatorSet.setStartDelay(75);
            animatorSet.setDuration(275);
            animatorSet.start();
            saver.setKeepScreenOn(b);
        });
        if (vibrator.hasVibrator()) {
            vibrationEffects.setVisibility(View.VISIBLE);
            vibrationEffectsImg.setVisibility(View.VISIBLE);
        } else {
            vibrationEffects.setVisibility(View.GONE);
            vibrationEffectsImg.setVisibility(View.GONE);
            vibrationEffects.setChecked(false);
            saver.setVibrationEffects(false);
        }
        vibrationEffects.setOnCheckedChangeListener((compoundButton, b) -> {
            saver = Saver.getInstance(getContext());
            if (b) {
                vibrationEffectsImg.setScaleX(1f);
                vibrationEffectsImg.setScaleY(1f);
                vibrator.vibrate(new long[]{0, 225, 150, 225}, -1);
                ValueAnimator animatorTranslationX = ValueAnimator.ofFloat(1f, 2.5f, -2.5f, 1f);
                animatorTranslationX.addUpdateListener(valueAnimator -> {
                    float animatedValue = (float) valueAnimator.getAnimatedValue();
                    vibrationEffectsImg.setTranslationX(animatedValue);
                    if (animatedValue < 1f) {
                        vibrationEffectsImg.setImageResource(R.drawable.vibration_effects);
                        showStatusOfSetting(vibrationEffects, vibrationEffectsImg, true);
                    }
                });
                animatorTranslationX.setStartDelay(75);
                animatorTranslationX.setDuration(100);
                animatorTranslationX.setRepeatCount(5);
                animatorTranslationX.start();
            } else {
                vibrationEffectsImg.setTranslationX(1f);
                ValueAnimator animatorScaleX = ValueAnimator.ofFloat(1f, 0.6f, 1.05f, 1f);
                animatorScaleX.addUpdateListener(valueAnimator -> {
                    float animatedValue = (float) valueAnimator.getAnimatedValue();
                    vibrationEffectsImg.setScaleX(animatedValue);
                    if (animatedValue > 1f) {
                        vibrationEffectsImg.setImageResource(R.drawable.keep_screen_on_disable);
                        showStatusOfSetting(vibrationEffects, vibrationEffectsImg, false);
                    }
                });
                ObjectAnimator animatorScaleY = ObjectAnimator.ofFloat(vibrationEffectsImg, "scaleY", 1f, 0.6f, 1.05f, 1f);
                animatorSet = new AnimatorSet();
                animatorSet.playTogether(animatorScaleX, animatorScaleY);
                animatorSet.setStartDelay(75);
                animatorSet.setDuration(275);
                animatorSet.start();
            }
            saver.setVibrationEffects(b);
        });
        musicEffects.setOnCheckedChangeListener((compoundButton, b) -> {
            saver = Saver.getInstance(getContext());

            ValueAnimator animatorScaleX = ValueAnimator.ofFloat(1f, 0.6f, 1.05f, 1f);
            animatorScaleX.addUpdateListener(valueAnimator -> {
                float animatedValue = (float) valueAnimator.getAnimatedValue();
                musicEffectsImg.setScaleX(animatedValue);
                if (animatedValue > 1f) {
                    if (b) {
                        playSoundEffect();
                        musicEffectsImg.setImageResource(R.drawable.music_effects);
                    } else {
                        musicEffectsImg.setImageResource(R.drawable.music_effects_disable);
                    }
                    showStatusOfSetting(musicEffects, musicEffectsImg, b);
                }
            });
            ObjectAnimator animatorScaleY = ObjectAnimator.ofFloat(musicEffectsImg, "scaleY", 1f, 0.6f, 1.05f, 1f);
            animatorSet = new AnimatorSet();
            animatorSet.playTogether(animatorScaleX, animatorScaleY);
            animatorSet.setStartDelay(75);
            animatorSet.setDuration(275);
            animatorSet.start();
            saver.setMusicEffects(b);
        });
    }

    private void playSoundEffect() {
        if (!player.isPlaying())
            player.start();
    }

    private void loadRecentSettings() {
        if (saver.getKeepScreenOn()) {
            keepScreenOnImg.setImageResource(R.drawable.keep_screen_on);
        } else {
            keepScreenOnImg.setImageResource(R.drawable.keep_screen_on_disable);
        }
        if (saver.getVibrationEffects()) {
            vibrationEffectsImg.setImageResource(R.drawable.vibration_effects);
        } else {
            vibrationEffectsImg.setImageResource(R.drawable.keep_screen_on_disable);
        }
        if (saver.getMusicEffects()) {
            musicEffectsImg.setImageResource(R.drawable.music_effects);
        } else {
            musicEffectsImg.setImageResource(R.drawable.music_effects_disable);
        }
        showStatusOfSetting(keepScreenOn, keepScreenOnImg, saver.getKeepScreenOn());
        showStatusOfSetting(vibrationEffects, vibrationEffectsImg, saver.getVibrationEffects());
        showStatusOfSetting(musicEffects, musicEffectsImg, saver.getMusicEffects());
    }

    private void showStatusOfSetting(@SuppressLint("UseSwitchCompatOrMaterialCode") Switch setting, ImageView image, boolean enable) {
        setting.setChecked(enable);
        if (enable) {
            setting.setTextColor(getResources().getColor(R.color.colorAccent));
            image.getDrawable().setColorFilter(getResources().getColor(R.color.colorAccent), SRC_IN);
        } else {
            setting.setTextColor(getResources().getColor(R.color.elements_color_tint));
            image.getDrawable().setColorFilter(getResources().getColor(R.color.elements_color_tint), SRC_IN);
        }
    }

    private void init(View v) {
        keepScreenOn = v.findViewById(R.id.use_keep_screen_on);
        keepScreenOnImg = v.findViewById(R.id.use_keep_screen_on_img);
        vibrationEffects = v.findViewById(R.id.use_vibration_effects);
        vibrationEffectsImg = v.findViewById(R.id.use_vibration_effects_img);
        musicEffects = v.findViewById(R.id.use_music_effects);
        musicEffectsImg = v.findViewById(R.id.use_music_effects_img);
        player = MediaPlayer.create(getContext(), R.raw.sound_effects_enabled);
        player.setLooping(false);
        player.setVolume(0.3f, 0.3f);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.stop();
        }
    }
}
