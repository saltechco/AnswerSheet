package ir.saltech.answersheet.view.container

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Handler
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.PopupWindow
import android.widget.TextView
import androidx.cardview.widget.CardView
import ir.saltech.answersheet.R

class MaterialAlert(context: Context) : PopupWindow(
    LayoutInflater.from(context).inflate(R.layout.popup_material_alert, null),
    WindowManager.LayoutParams.WRAP_CONTENT,
    WindowManager.LayoutParams.WRAP_CONTENT,
    false
) {
    private var alertCard: CardView? = null
    private var alertText: TextView? = null
    private var animatorSet: AnimatorSet? = null
    private var duration: Long = 0

    fun show(text: String?, duration: Long) {
        this.duration = duration
        init(contentView)
        showCardAnimations()
        if (text != null) {
            alertText!!.text = text
            alertText!!.visibility = View.VISIBLE
        } else {
            alertText!!.setVisibility(View.GONE)
        }
        elevation = 13f
        try {
            showAtLocation(alertCard, Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 135)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun init(v: View) {
        alertCard = v.findViewById<CardView>(R.id.alert_card)
        alertText = v.findViewById<TextView>(R.id.alert_text)
    }

    private fun showCardAnimations() {
        val showTranslationYAnim: ObjectAnimator =
            ObjectAnimator.ofFloat(alertCard, "translationY", ALERT_OUT_OF_SCREEN_Y, 1f)
        val showAlphaAnim: ObjectAnimator = ObjectAnimator.ofFloat(alertCard, "alpha", 0f, 1f)
        animatorSet = AnimatorSet()
        animatorSet!!.playTogether(showAlphaAnim, showTranslationYAnim)
        animatorSet!!.setStartDelay(200)
        animatorSet!!.setDuration(250)
        animatorSet!!.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animator: Animator) {
            }

            override fun onAnimationEnd(a: Animator) {
                if (duration == LENGTH_LONG.toLong() || duration == LENGTH_SHORT.toLong()) {
                    val handler = Handler()
                    handler.postDelayed({ this@MaterialAlert.dismiss() }, duration)
                } else {
                    throw IllegalArgumentException("Alert duration must be defined to either of LENGTH_SHORT or LENGTH_LONG!")
                }
            }

            override fun onAnimationCancel(animator: Animator) {
            }

            override fun onAnimationRepeat(animator: Animator) {
            }
        })
        animatorSet!!.start()
    }

    override fun dismiss() {
        val hideTranslationYAnim: ObjectAnimator =
            ObjectAnimator.ofFloat(alertCard, "translationY", 1f, ALERT_OUT_OF_SCREEN_Y)
        val hideAlphaAnim: ObjectAnimator = ObjectAnimator.ofFloat(alertCard, "alpha", 1f, 0f)
        animatorSet = AnimatorSet()
        animatorSet!!.playTogether(hideTranslationYAnim, hideAlphaAnim)
        animatorSet!!.setDuration(250)
        animatorSet!!.addListener(object : Animator.AnimatorListener {
            @SuppressLint("SyntheticAccessor")
            override fun onAnimationStart(animator: Animator) {
            }

            override fun onAnimationEnd(animator: Animator) {
                try {
                    super@MaterialAlert.dismiss()
                } catch (e: IllegalArgumentException) {
                    e.printStackTrace()
                }
            }

            override fun onAnimationCancel(animator: Animator) {
            }

            override fun onAnimationRepeat(animator: Animator) {
            }
        })
        animatorSet!!.start()
    }

    companion object {
        const val LENGTH_LONG: Int = 8000
        const val LENGTH_SHORT: Int = 3000
        private const val ALERT_OUT_OF_SCREEN_Y = -100f

        @SuppressLint("StaticFieldLeak")
        private var instance: MaterialAlert? = null
        fun getInstance(context: Context): MaterialAlert {
            if (instance == null) {
                instance = MaterialAlert(context)
            }
            return instance!!
        }
    }
}
