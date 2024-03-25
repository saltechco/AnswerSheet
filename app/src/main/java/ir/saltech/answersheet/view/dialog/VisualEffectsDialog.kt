package ir.saltech.answersheet.view.dialog

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PorterDuff
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.Switch
import androidx.fragment.app.Fragment
import ir.saltech.answersheet.R
import ir.saltech.answersheet.`object`.container.Saver

class VisualEffectsDialog : Fragment() {
    private var saver: Saver? = null
    private var vibrator: Vibrator? = null

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private var keepScreenOn: Switch? = null
    private var keepScreenOnImg: ImageView? = null

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private var vibrationEffects: Switch? = null
    private var vibrationEffectsImg: ImageView? = null

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private var musicEffects: Switch? = null
    private var musicEffectsImg: ImageView? = null
    private var animatorSet: AnimatorSet? = null
    private var player: MediaPlayer? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_visual_effects, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        saver = Saver.getInstance(requireContext())
        vibrator = requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        loadRecentSettings()
        onClicks()
    }

    private fun onClicks() {
        keepScreenOn!!.setOnCheckedChangeListener { _: CompoundButton?, b: Boolean ->
            saver = Saver.Companion.getInstance(
                requireContext()
            )
            val animatorScaleX: ValueAnimator = ValueAnimator.ofFloat(1f, 0.6f, 1.05f, 1f)
            animatorScaleX.addUpdateListener { valueAnimator: ValueAnimator ->
                val animatedValue = valueAnimator.getAnimatedValue() as Float
                keepScreenOnImg!!.scaleX = animatedValue
                if (animatedValue > 1f) {
                    if (b) {
                        keepScreenOnImg!!.setImageResource(R.drawable.keep_screen_on)
                    } else {
                        keepScreenOnImg!!.setImageResource(R.drawable.keep_screen_on_disable)
                    }
                    showStatusOfSetting(keepScreenOn, keepScreenOnImg, b)
                }
            }
            val animatorScaleY: ObjectAnimator =
                ObjectAnimator.ofFloat(keepScreenOnImg, "scaleY", 1f, 0.6f, 1.05f, 1f)
            animatorSet = AnimatorSet()
            animatorSet!!.playTogether(animatorScaleX, animatorScaleY)
            animatorSet!!.setStartDelay(75)
            animatorSet!!.setDuration(275)
            animatorSet!!.start()
            saver!!.keepScreenOn = b
        }
        if (vibrator!!.hasVibrator()) {
            vibrationEffects!!.visibility = View.VISIBLE
            vibrationEffectsImg!!.visibility = View.VISIBLE
        } else {
            vibrationEffects!!.visibility = View.GONE
            vibrationEffectsImg!!.visibility = View.GONE
            vibrationEffects!!.setChecked(false)
            saver!!.vibrationEffects = (false)
        }
        vibrationEffects!!.setOnCheckedChangeListener { compoundButton: CompoundButton?, b: Boolean ->
            saver = Saver.getInstance(
                requireContext()
            )
            if (b) {
                vibrationEffectsImg!!.scaleX = 1f
                vibrationEffectsImg!!.scaleY = 1f
                vibrator!!.vibrate(longArrayOf(0, 225, 150, 225), -1)
                val animatorTranslationX: ValueAnimator = ValueAnimator.ofFloat(1f, 2.5f, -2.5f, 1f)
                animatorTranslationX.addUpdateListener { valueAnimator: ValueAnimator ->
                    val animatedValue = valueAnimator.getAnimatedValue() as Float
                    vibrationEffectsImg!!.translationX = animatedValue
                    if (animatedValue < 1f) {
                        vibrationEffectsImg!!.setImageResource(R.drawable.vibration_effects)
                        showStatusOfSetting(vibrationEffects, vibrationEffectsImg, true)
                    }
                }
                animatorTranslationX.setStartDelay(75)
                animatorTranslationX.setDuration(100)
                animatorTranslationX.repeatCount = 5
                animatorTranslationX.start()
            } else {
                vibrationEffectsImg!!.translationX = 1f
                val animatorScaleX: ValueAnimator = ValueAnimator.ofFloat(1f, 0.6f, 1.05f, 1f)
                animatorScaleX.addUpdateListener { valueAnimator: ValueAnimator ->
                    val animatedValue = valueAnimator.getAnimatedValue() as Float
                    vibrationEffectsImg!!.scaleX = animatedValue
                    if (animatedValue > 1f) {
                        vibrationEffectsImg!!.setImageResource(R.drawable.keep_screen_on_disable)
                        showStatusOfSetting(vibrationEffects, vibrationEffectsImg, false)
                    }
                }
                val animatorScaleY: ObjectAnimator =
                    ObjectAnimator.ofFloat(vibrationEffectsImg, "scaleY", 1f, 0.6f, 1.05f, 1f)
                animatorSet = AnimatorSet()
                animatorSet!!.playTogether(animatorScaleX, animatorScaleY)
                animatorSet!!.setStartDelay(75)
                animatorSet!!.setDuration(275)
                animatorSet!!.start()
            }
            saver!!.vibrationEffects = (b)
        }
        musicEffects!!.setOnCheckedChangeListener { compoundButton: CompoundButton?, b: Boolean ->
            saver = Saver.Companion.getInstance(
                requireContext()
            )
            val animatorScaleX: ValueAnimator = ValueAnimator.ofFloat(1f, 0.6f, 1.05f, 1f)
            animatorScaleX.addUpdateListener(ValueAnimator.AnimatorUpdateListener { valueAnimator: ValueAnimator ->
                val animatedValue = valueAnimator.getAnimatedValue() as Float
                musicEffectsImg!!.scaleX = animatedValue
                if (animatedValue > 1f) {
                    if (b) {
                        playSoundEffect()
                        musicEffectsImg!!.setImageResource(R.drawable.music_effects)
                    } else {
                        musicEffectsImg!!.setImageResource(R.drawable.music_effects_disable)
                    }
                    showStatusOfSetting(musicEffects, musicEffectsImg, b)
                }
            })
            val animatorScaleY: ObjectAnimator =
                ObjectAnimator.ofFloat(musicEffectsImg, "scaleY", 1f, 0.6f, 1.05f, 1f)
            animatorSet = AnimatorSet()
            animatorSet!!.playTogether(animatorScaleX, animatorScaleY)
            animatorSet!!.setStartDelay(75)
            animatorSet!!.setDuration(275)
            animatorSet!!.start()
            saver!!.musicEffects = (b)
        }
    }

    private fun playSoundEffect() {
        if (!player!!.isPlaying) player!!.start()
    }

    private fun loadRecentSettings() {
        if (saver!!.keepScreenOn) {
            keepScreenOnImg!!.setImageResource(R.drawable.keep_screen_on)
        } else {
            keepScreenOnImg!!.setImageResource(R.drawable.keep_screen_on_disable)
        }
        if (saver!!.vibrationEffects) {
            vibrationEffectsImg!!.setImageResource(R.drawable.vibration_effects)
        } else {
            vibrationEffectsImg!!.setImageResource(R.drawable.keep_screen_on_disable)
        }
        if (saver!!.musicEffects) {
            musicEffectsImg!!.setImageResource(R.drawable.music_effects)
        } else {
            musicEffectsImg!!.setImageResource(R.drawable.music_effects_disable)
        }
        showStatusOfSetting(keepScreenOn, keepScreenOnImg, saver!!.keepScreenOn)
        showStatusOfSetting(vibrationEffects, vibrationEffectsImg, saver!!.keepScreenOn)
        showStatusOfSetting(musicEffects, musicEffectsImg, saver!!.musicEffects)
    }

    private fun showStatusOfSetting(
        @SuppressLint("UseSwitchCompatOrMaterialCode") setting: Switch?,
        image: ImageView?,
        enable: Boolean
    ) {
        setting!!.setChecked(enable)
        if (enable) {
            setting.setTextColor(resources.getColor(R.color.colorAccent))
            image!!.drawable.setColorFilter(
                resources.getColor(R.color.colorAccent),
                PorterDuff.Mode.SRC_IN
            )
        } else {
            setting.setTextColor(resources.getColor(R.color.elements_color_tint))
            image!!.drawable.setColorFilter(
                resources.getColor(R.color.elements_color_tint),
                PorterDuff.Mode.SRC_IN
            )
        }
    }

    private fun init(v: View) {
        keepScreenOn = v.findViewById<Switch>(R.id.use_keep_screen_on)
        keepScreenOnImg = v.findViewById<ImageView>(R.id.use_keep_screen_on_img)
        vibrationEffects = v.findViewById<Switch>(R.id.use_vibration_effects)
        vibrationEffectsImg = v.findViewById<ImageView>(R.id.use_vibration_effects_img)
        musicEffects = v.findViewById<Switch>(R.id.use_music_effects)
        musicEffectsImg = v.findViewById<ImageView>(R.id.use_music_effects_img)
        player = MediaPlayer.create(context, R.raw.sound_effects_enabled)
        player!!.isLooping = false
        player!!.setVolume(0.3f, 0.3f)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (player != null) {
            player!!.stop()
        }
    }
}
