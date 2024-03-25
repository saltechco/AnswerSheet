package ir.saltech.answersheet.view.fragment

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import ir.saltech.answersheet.R

class CollapsablePanelFragment : Fragment() {
    private var parentLayout: ConstraintLayout? = null
    private var collapsablePanel: ConstraintLayout? = null
    private var focusBackground: View? = null
    private var currentY = 0
    private var lastY = 0
    private var deltaY = 0
    private var fragment: Fragment? = null
    private var animatorSet: AnimatorSet? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_collapsable_panel, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        showPanelAnimation()
        showFragment()
        onClicks()
    }

    fun setContentFragment(fragment: Fragment) {
        this.fragment = fragment
    }

    private fun showFragment() {
        if (fragment != null) {
            requireActivity().supportFragmentManager.beginTransaction()
                .add(R.id.collapsable_panel_fragment_container, fragment!!).commit()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun onClicks() {
        focusBackground!!.setOnClickListener { v: View? -> hidePanelAnimation() }
        collapsablePanel!!.setOnTouchListener(View.OnTouchListener { v: View?, event: MotionEvent ->
            when (event.action) {
                MotionEvent.ACTION_MOVE -> {
                    currentY = Math.round(event.rawY)
                    deltaY = -currentY + lastY
                    lastY = currentY
                    if (deltaY >= -50 && deltaY <= 100) {
                        if ((collapsablePanel!!.translationY >= 0f && deltaY < 0) || (collapsablePanel!!.translationY >= 5f && deltaY > 0)) {
                            collapsablePanel!!.translationY = Math.round((collapsablePanel!!.translationY - (deltaY * MOVE_SPEED)).toFloat())
                                .toFloat()
                            //Log.i("TOUCH_ACTION", "Current TRANS Y: " + collapsablePanel.getTranslationY() + ", DELTA Y: " + deltaY + ", HEIGHT: " + collapsablePanel.getMeasuredHeight());
                        } else if (collapsablePanel!!.translationY < 0f) {
                            collapsablePanel!!.translationY = 0f
                        }
                    }
                    return@OnTouchListener true
                }
                MotionEvent.ACTION_UP -> {
                    val panelHeight: Int = collapsablePanel!!.measuredHeight
                    val currentTranslationY: Float = collapsablePanel!!.translationY
                    if (currentTranslationY <= (panelHeight / 2).toFloat()) {
                        val raiseUpView: ObjectAnimator = ObjectAnimator.ofFloat(
                            collapsablePanel,
                            "translationY",
                            currentTranslationY,
                            0f
                        )
                        raiseUpView.setDuration(BACK_TO_NEUTRAL_STATUS_ANIM_DURATION.toLong())
                        raiseUpView.start()
                    } else {
                        hidePanelAnimation(currentTranslationY)
                    }
                    return@OnTouchListener false
                }
                else -> {
                    return@OnTouchListener true
                }
            }
        })
    }

    private fun hidePanelAnimation(currentTranslationY: Float) {
        @SuppressLint("Recycle") val hideBackgroundFocus: ValueAnimator = ValueAnimator.ofObject(
            ArgbEvaluator(), Color.argb(180, 0, 0, 0), Color.argb(0, 0, 0, 0)
        )
        hideBackgroundFocus.addUpdateListener { valueAnimator: ValueAnimator ->
            focusBackground!!.setBackgroundColor(
                valueAnimator.getAnimatedValue() as Int
            )
        }
        val takeDownPanel: ObjectAnimator = ObjectAnimator.ofFloat(
            collapsablePanel,
            "translationY",
            currentTranslationY,
            (parentLayout!!.measuredHeight + 250).toFloat()
        )
        animatorSet = AnimatorSet()
        animatorSet!!.playTogether(hideBackgroundFocus, takeDownPanel)
        animatorSet!!.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
            }

            override fun onAnimationEnd(animation: Animator) {
                closePanel()
            }

            override fun onAnimationCancel(animation: Animator) {
            }

            override fun onAnimationRepeat(animation: Animator) {
            }
        })
        animatorSet!!.setDuration(BACK_TO_NEUTRAL_STATUS_ANIM_DURATION.toLong())
        animatorSet!!.start()
    }

    private fun hidePanelAnimation() {
        @SuppressLint("Recycle") val hideBackgroundFocus: ValueAnimator = ValueAnimator.ofObject(
            ArgbEvaluator(), Color.argb(180, 0, 0, 0), Color.argb(0, 0, 0, 0)
        )
        hideBackgroundFocus.addUpdateListener { valueAnimator: ValueAnimator ->
            focusBackground!!.setBackgroundColor(
                valueAnimator.getAnimatedValue() as Int
            )
        }
        val takeDownPanel: ObjectAnimator = ObjectAnimator.ofFloat(
            collapsablePanel,
            "translationY",
            0f,
            (parentLayout!!.measuredHeight + 250).toFloat()
        )
        animatorSet = AnimatorSet()
        animatorSet!!.playTogether(hideBackgroundFocus, takeDownPanel)
        animatorSet!!.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
            }

            override fun onAnimationEnd(animation: Animator) {
                closePanel()
            }

            override fun onAnimationCancel(animation: Animator) {
            }

            override fun onAnimationRepeat(animation: Animator) {
            }
        })
        animatorSet!!.setDuration(BACK_TO_NEUTRAL_STATUS_ANIM_DURATION.toLong())
        animatorSet!!.start()
    }

    private fun closePanel() {
        collapsablePanel!!.visibility = View.GONE
        focusBackground!!.isClickable = false
        requireActivity().supportFragmentManager.beginTransaction()
            .remove(this@CollapsablePanelFragment).commit()
    }

    private fun showPanelAnimation() {
        @SuppressLint("Recycle") val showBackgroundFocus: ValueAnimator = ValueAnimator.ofObject(
            ArgbEvaluator(), Color.argb(0, 0, 0, 0), Color.argb(180, 0, 0, 0)
        )
        showBackgroundFocus.addUpdateListener { valueAnimator: ValueAnimator ->
            focusBackground!!.setBackgroundColor(
                valueAnimator.getAnimatedValue() as Int
            )
        }
        val startUpPanelAnim: ObjectAnimator =
            ObjectAnimator.ofFloat(collapsablePanel, "translationY", 2000f, 0f)
        animatorSet = AnimatorSet()
        animatorSet!!.playTogether(showBackgroundFocus, startUpPanelAnim)
        animatorSet!!.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                collapsablePanel!!.visibility = View.VISIBLE
            }

            override fun onAnimationEnd(animation: Animator) {
            }

            override fun onAnimationCancel(animation: Animator) {
            }

            override fun onAnimationRepeat(animation: Animator) {
            }
        })
        animatorSet!!.setStartDelay(500)
        animatorSet!!.setDuration(BACK_TO_NEUTRAL_STATUS_ANIM_DURATION.toLong())
        animatorSet!!.start()
    }

    private fun init(v: View) {
        collapsablePanel = v.findViewById<ConstraintLayout>(R.id.collapsable_panel)
        parentLayout = v.findViewById<ConstraintLayout>(R.id.parent_layout)
        focusBackground = v.findViewById<View>(R.id.focus_background)
    }

    companion object {
        private const val MOVE_SPEED = 1.0
        private const val BACK_TO_NEUTRAL_STATUS_ANIM_DURATION = 250
    }
}
