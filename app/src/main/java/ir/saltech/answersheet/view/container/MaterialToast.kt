package ir.saltech.answersheet.view.container

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.yy.mobile.rollingtextview.RollingTextView
import ir.saltech.answersheet.R

open class MaterialToast(context: Context) : PopupWindow(
    LayoutInflater.from(context).inflate(R.layout.popup_material_toast, null),
    WindowManager.LayoutParams.WRAP_CONTENT,
    WindowManager.LayoutParams.WRAP_CONTENT,
    false
) {
    private var card: CardView? = null
    private var textContainer: TextView? = null
    private var imageContainer: ImageView? = null
    private var animatorSet: AnimatorSet? = null

    fun init(v: View) {
        card = v.findViewById<CardView>(R.id.toast_card)
        imageContainer = v.findViewById<ImageView>(R.id.toast_image)
        textContainer = v.findViewById<TextView>(R.id.toast_text)
    }

    fun setIcon(icon: Drawable?) {
        if (icon != null) {
            imageContainer!!.visibility = View.VISIBLE
            imageContainer!!.setImageDrawable(icon)
        } else {
            imageContainer!!.visibility = View.GONE
        }
    }

    fun setText(text: String?) {
        if (text != null) {
            textContainer!!.visibility = View.VISIBLE
            textContainer!!.text = text
        } else {
            textContainer!!.visibility = View.GONE
        }
    }

    fun show(duration: Long) {
        val alpha: ObjectAnimator = ObjectAnimator.ofFloat(card, "alpha", 0f, ALPHA)
        alpha.setDuration(400)
        val scaleX: ObjectAnimator = ObjectAnimator.ofFloat(card, "scaleX", 0f, SCALE, 1f)
        scaleX.setDuration(400)
        val scaleY: ObjectAnimator = ObjectAnimator.ofFloat(card, "scaleY", 0f, SCALE, 1f)
        scaleY.setDuration(400)
        val translationY: ObjectAnimator =
            ObjectAnimator.ofFloat(contentView, "translationY", 1f, -8f)
        translationY.setDuration(duration)
        translationY.setStartDelay(150)
        animatorSet = AnimatorSet()
        animatorSet!!.playTogether(alpha, scaleX, scaleY, translationY)
        //animatorSet.setDuration(400);
        animatorSet!!.setStartDelay(50)
        animatorSet!!.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animator: Animator) {
            }

            override fun onAnimationEnd(animator: Animator) {
                dismiss()
                //				Handler handler = new Handler();
//				handler.postDelayed(MaterialToast.this::hide, duration);
            }

            override fun onAnimationCancel(animator: Animator) {
            }

            override fun onAnimationRepeat(animator: Animator) {
            }
        })
        this@MaterialToast.showAtLocation(card, Gravity.CENTER, 0, 750)
        animatorSet!!.start()
    }

    override fun dismiss() {
        val alpha: ObjectAnimator = ObjectAnimator.ofFloat(card, "alpha", ALPHA, 0f)
        animatorSet = AnimatorSet()
        animatorSet!!.play(alpha)
        animatorSet!!.setDuration(300)
        animatorSet!!.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animator: Animator) {
            }

            override fun onAnimationEnd(animator: Animator) {
                try {
                    super@MaterialToast.dismiss()
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
        private const val SCALE = 1.03f
        private const val ALPHA = 0.95f
    }
}
