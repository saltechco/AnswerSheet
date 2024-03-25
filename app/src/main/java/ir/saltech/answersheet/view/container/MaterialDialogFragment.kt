package ir.saltech.answersheet.view.container

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import ir.saltech.answersheet.R
import ir.saltech.answersheet.intf.listener.DialogDismissListener
import ir.saltech.answersheet.`object`.container.Saver
import ir.saltech.answersheet.view.activity.MainActivity

open class MaterialDialogFragment : Fragment() {
    private var dialogParent: ConstraintLayout? = null
    private var iconImage: ImageView? = null
    private var titleText: TextView? = null
    private var messageText: TextView? = null
    private var progressBar: ProgressBar? = null
    private var buttonsPanel: LinearLayout? = null
    private var contentFrame: FragmentContainerView? = null
    private var primaryButtonView: Button? = null
    private var secondaryButtonView: Button? = null
    private var naturalButtonView: Button? = null
    private var dialogCard: CardView? = null
    private var backgroundFocus: View? = null
    private var contentView: Fragment? = null
    private var dismissListener: DialogDismissListener? = null
    private var dismissReceiver: BroadcastReceiver? = null
    private var lastColorState = false

    protected fun getDismissReceiver(): BroadcastReceiver {
        return dismissReceiver!!
    }

    protected fun setDismissReceiver(dismissReceiver: BroadcastReceiver) {
        this.dismissReceiver = dismissReceiver
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_material_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init(view)
        prepareView()
        onClicks()
    }

    private fun prepareView() {
        lastColorState = Saver.Companion.getInstance(requireContext()).lastStatusBarColorState
        MainActivity.Companion.setStatusBarTheme(requireActivity(), false)
    }

    private fun onClicks() {
        backgroundFocus!!.setOnClickListener { view: View? -> dismiss(this@MaterialDialogFragment) }
    }

    private fun init(v: View) {
        buttonsPanel = v.findViewById<LinearLayout>(R.id.dialog_buttons)
        iconImage = v.findViewById<ImageView>(R.id.dialog_icon_image)
        titleText = v.findViewById<TextView>(R.id.dialog_title_text)
        messageText = v.findViewById<TextView>(R.id.dialog_message_text)
        contentFrame = v.findViewById<FragmentContainerView>(R.id.dialog_content_frame)
        primaryButtonView = v.findViewById<Button>(R.id.dialog_primary_button)
        secondaryButtonView = v.findViewById<Button>(R.id.dialog_secondary_button)
        naturalButtonView = v.findViewById<Button>(R.id.dialog_natural_button)
        dialogCard = v.findViewById<CardView>(R.id.dialog_card)
        backgroundFocus = v.findViewById<View>(R.id.dialog_focus)
        dialogParent = v.findViewById<ConstraintLayout>(R.id.dialog_parent)
        progressBar = v.findViewById<ProgressBar>(R.id.dialog_progress_bar)
    }

    protected open fun setIcon(icon: Drawable?) {
        if (icon != null) {
            iconImage!!.visibility = View.VISIBLE
            iconImage!!.setImageDrawable(icon)
        } else {
            iconImage!!.visibility = View.GONE
            iconImage!!.setImageResource(R.drawable.text)
        }
    }

    protected open fun setTitle(title: String?) {
        titleText!!.text = title
        if (title != null) {
            titleText!!.visibility = View.VISIBLE
        } else {
            titleText!!.visibility = View.GONE
        }
    }

    protected fun showProgressBar(enable: Boolean) {
        if (enable) {
            progressBar!!.visibility = View.VISIBLE
        } else {
            progressBar!!.visibility = View.GONE
        }
    }

    protected open fun setMessage(message: String?) {
        messageText!!.text = message
        if (message != null) {
            messageText!!.visibility = View.VISIBLE
            messageText!!.movementMethod = ScrollingMovementMethod()
        } else {
            messageText!!.visibility = View.GONE
        }
    }

    protected open fun setCancelable(cancelable: Boolean) {
        backgroundFocus!!.isClickable = cancelable
    }

    protected open fun setPositiveButton(
        primaryButton: String?,
        clickListener: View.OnClickListener?
    ) {
        if (primaryButton != null) {
            buttonsPanel!!.visibility = View.VISIBLE
            primaryButtonView!!.text = primaryButton
            primaryButtonView!!.setOnClickListener(clickListener)
            primaryButtonView!!.visibility = View.VISIBLE
        } else {
            primaryButtonView!!.visibility = View.GONE
            if (secondaryButtonView!!.visibility == View.GONE && primaryButtonView!!.visibility == View.GONE) {
                buttonsPanel!!.visibility = View.GONE
            }
        }
    }

    protected open fun setNegativeButton(
        secondaryButton: String?,
        clickListener: View.OnClickListener?
    ) {
        if (secondaryButton != null) {
            buttonsPanel!!.visibility = View.VISIBLE
            secondaryButtonView!!.text = secondaryButton
            secondaryButtonView!!.setOnClickListener(clickListener)
            secondaryButtonView!!.visibility = View.VISIBLE
        } else {
            secondaryButtonView!!.visibility = View.GONE
            if (secondaryButtonView!!.visibility == View.GONE && primaryButtonView!!.visibility == View.GONE) {
                buttonsPanel!!.visibility = View.GONE
            }
        }
    }

    protected open fun setNaturalButton(
        naturalButton: String?,
        clickListener: View.OnClickListener?
    ) {
        if (naturalButton != null) {
            naturalButtonView!!.text = naturalButton
            naturalButtonView!!.setOnClickListener(clickListener)
            naturalButtonView!!.visibility = View.VISIBLE
        } else {
            naturalButtonView!!.visibility = View.GONE
        }
    }

    protected fun setContentView(contentView: Fragment?, matchParent: Boolean) {
        if (contentView != null) {
            this.contentView = contentView
            contentFrame!!.visibility = View.VISIBLE
            val params: ConstraintLayout.LayoutParams =
                dialogCard!!.layoutParams as ConstraintLayout.LayoutParams
            if (matchParent) {
                params.height = ConstraintLayout.LayoutParams.MATCH_PARENT
            } else {
                params.height = ConstraintLayout.LayoutParams.WRAP_CONTENT
            }
            dialogCard!!.setLayoutParams(params)
            requireActivity().supportFragmentManager.beginTransaction().setReorderingAllowed(true)
                .add(contentFrame!!.id, contentView).addToBackStack(
                DIALOG_CONTENT_BACKSTACK
            ).commit()
            Log.d("TAG", "Fragments: " + requireActivity().supportFragmentManager.fragments)
        } else {
            contentFrame!!.visibility = View.GONE
        }
    }

    fun dismiss(dialogFragment: MaterialDialogFragment) {
        val hideBackgroundFocus: ValueAnimator =
            ValueAnimator.ofObject(ArgbEvaluator(), DEFAULT_COLOR, Color.argb(0, 33, 37, 41))
        hideBackgroundFocus.addUpdateListener { valueAnimator: ValueAnimator ->
            backgroundFocus!!.setBackgroundColor(
                valueAnimator.getAnimatedValue() as Int
            )
        }
        val hideCardViewAlpha: ObjectAnimator = ObjectAnimator.ofFloat(dialogCard, "alpha", 1f, 0f)
        val hideCardViewScaleX: ObjectAnimator =
            ObjectAnimator.ofFloat(dialogCard, "scaleX", 1f, 0f)
        val hideCardViewScaleY: ObjectAnimator =
            ObjectAnimator.ofFloat(dialogCard, "scaleY", 1f, 0f)
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(
            hideBackgroundFocus,
            hideCardViewAlpha,
            hideCardViewScaleX,
            hideCardViewScaleY
        )
        animatorSet.setDuration(250)
        animatorSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animator: Animator) {
            }

            @SuppressLint("SyntheticAccessor")
            override fun onAnimationEnd(animator: Animator) {
                if (contentFrame!!.getVisibility() == View.VISIBLE) {
                    try {
                        requireActivity().supportFragmentManager.beginTransaction()
                            .remove(dialogFragment).remove(
                            contentView!!
                        ).commit()
                        if ((dialogFragment as MaterialFragmentShower).hasContent) {
                            this@MaterialDialogFragment.show()
                        }
                    } catch (e: IllegalStateException) {
                        e.printStackTrace()
                    }
                } else {
                    requireActivity().supportFragmentManager.beginTransaction()
                        .remove(dialogFragment).commit()
                }
                Log.d(
                    "TAG",
                    "Fragments POPED: " + requireActivity().supportFragmentManager.fragments
                )
                dialogParent!!.setVisibility(View.GONE)
                if (dismissListener != null) {
                    dismissListener!!.onDismissed()
                }
                MainActivity.Companion.setStatusBarTheme(requireActivity(), lastColorState)
            }

            override fun onAnimationCancel(animator: Animator) {
            }

            override fun onAnimationRepeat(animator: Animator) {
            }
        })
        animatorSet.start()
    }

    protected fun show() {
        val showBackgroundFocus: ValueAnimator =
            ValueAnimator.ofObject(ArgbEvaluator(), Color.argb(0, 33, 37, 41), DEFAULT_COLOR)
        showBackgroundFocus.addUpdateListener { valueAnimator: ValueAnimator ->
            backgroundFocus!!.setBackgroundColor(
                valueAnimator.getAnimatedValue() as Int
            )
        }
        val showCardViewAlpha: ObjectAnimator = ObjectAnimator.ofFloat(dialogCard, "alpha", 0f, 1f)
        val showCardViewScaleX: ObjectAnimator =
            ObjectAnimator.ofFloat(dialogCard, "scaleX", 0f, 1.02f, 1f)
        val showCardViewScaleY: ObjectAnimator =
            ObjectAnimator.ofFloat(dialogCard, "scaleY", 0f, 1.02f, 1f)
        val animatorSet: AnimatorSet = AnimatorSet()
        animatorSet.playTogether(
            showBackgroundFocus,
            showCardViewAlpha,
            showCardViewScaleX,
            showCardViewScaleY
        )
        animatorSet.setStartDelay(100)
        animatorSet.setDuration(250)
        animatorSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animator: Animator) {
                dialogParent!!.setVisibility(View.VISIBLE)
            }

            override fun onAnimationEnd(animator: Animator) {
            }

            override fun onAnimationCancel(animator: Animator) {
            }

            override fun onAnimationRepeat(animator: Animator) {
            }
        })
        animatorSet.start()
    }

    fun setOnDismissListener(dismissListener: DialogDismissListener?) {
        this.dismissListener = dismissListener
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            dismissReceiver!!, IntentFilter(
                DISMISS_DIALOG_EVENT
            )
        )
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(dismissReceiver!!)
    }

    companion object {
        const val SIDE_ALERT_DIALOG: String = "side_alert_dialog"
        const val SIDE_FRAGMENT_SHOWER: String = "side_fragment_shower"
        const val DISMISS_DIALOG_EVENT: String = "dismiss_dialog_event"
        const val DIALOG_BACKSTACK: String = "dialog_backstack"
        const val DIALOG_CONTENT_BACKSTACK: String = "dialog_content_backstack"
        private val DEFAULT_COLOR = Color.argb(185, 0, 0, 0)
    }
}
