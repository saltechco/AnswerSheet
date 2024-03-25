package ir.saltech.answersheet.view.fragment

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.KeyguardManager
import android.content.Context
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Vibrator
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.airbnb.lottie.LottieAnimationView
import com.yy.mobile.rollingtextview.RollingTextView
import ir.saltech.answersheet.R
import ir.saltech.answersheet.intf.listener.AuthenticationChangedListener
import ir.saltech.answersheet.`object`.container.Saver
import ir.saltech.answersheet.view.activity.MainActivity
import ir.saltech.answersheet.view.container.Toast
import java.util.concurrent.Executor
import kotlin.math.pow
import kotlin.math.sqrt

class AuthFragment(private val authenticationListener: AuthenticationChangedListener?) : Fragment() {
    private var passwordLayout: LinearLayout? = null
    private var secureKeypad: LinearLayout? = null
    private var enterThroughFingerprint: LinearLayout? = null
    private var passkeyDigitsLayout: LinearLayout? = null
    private var title: TextView? = null
    private var subtitle: TextView? = null
    private var passkeyDigit1: ImageView? = null
    private var passkeyDigit2: ImageView? = null
    private var passkeyDigit3: ImageView? = null
    private var passkeyDigit4: ImageView? = null
    private var lockAnim: LottieAnimationView? = null
    private var vibrator: Vibrator? = null
    private var password: StringBuilder? = null
    private var animatorSet: AnimatorSet? = null
    private var isBackspacePressed = false
    private var spaceView: View? = null
    private var loadFromSettings = false
    private var newPassword: String? = null
    private var loadedPassword: String? = null
    private var newPasswordWanted = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        args
        return inflater.inflate(R.layout.fragment_auth, container, false)
    }

    private val args: Unit
        get() {
            val extras: Bundle? = arguments
            if (extras != null) {
                if (extras.containsKey(LOAD_FROM_SETTINGS)) {
                    loadFromSettings = extras.getBoolean(LOAD_FROM_SETTINGS, false)
                }
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        setLockAnim()
        showPasswordDots()
        loadedPassword = Saver.getInstance(requireContext()).appPassword.toString()
        if (loadFromSettings) {
            if (Saver.getInstance(requireContext())
                    .appPassword == Saver.DEF_PASSKEY
            ) {
                title!!.text = "گذرواژه جدید خود را وارد کنید"
            } else {
                title!!.text = "تعیین کنید که این شمایید"
            }
            enterThroughFingerprint!!.visibility = View.INVISIBLE
            subtitle!!.visibility = View.INVISIBLE
        }
        onClicks(view)
    }

    private fun setLockAnim() {
        lockAnim!!.addAnimatorUpdateListener { animation: ValueAnimator ->
            if ((animation.getAnimatedValue() as Float) >= 0.3f) {
                lockAnim!!.pauseAnimation()
                showPasswordLayoutAnim()
            }
        }
    }

    private val screenSize: Double
        get() {
            val point = Point()
            (requireActivity().getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
                .getRealSize(point)
            val displayMetrics: DisplayMetrics = resources.displayMetrics
            val width = point.x
            val height = point.y
            val wi: Double = width.toDouble() / displayMetrics.xdpi.toDouble()
            val hi: Double = height.toDouble() / displayMetrics.ydpi.toDouble()
            val x: Double = wi.pow(2.0)
            val y: Double = hi.pow(2.0)
            return Math.round((sqrt(x + y)) * 10.0) / 10.0
        }

    private fun showPasswordLayoutAnim() {
        if (screenSize >= MainActivity.MIN_OF_NORMAL_SCREEN_SIZE) {
            val params: ConstraintLayout.LayoutParams =
                lockAnim!!.layoutParams as ConstraintLayout.LayoutParams
            @SuppressLint("Recycle") val topMarginAnimator: ValueAnimator = ValueAnimator.ofInt(
                params.topMargin,
                (params.topMargin + resources.getDimension(R.dimen.status_bar_margin)).toInt()
            )
            topMarginAnimator.addUpdateListener { animation: ValueAnimator ->
                params.topMargin = animation.getAnimatedValue() as Int
                lockAnim!!.setLayoutParams(params)
            }
            topMarginAnimator.setStartDelay(25)
            topMarginAnimator.setDuration(250)
            topMarginAnimator.start()
            val spaceViewTranslationY: ObjectAnimator =
                ObjectAnimator.ofFloat(spaceView, "translationY", -100f, 0f)
            spaceViewTranslationY.setDuration(100)
            spaceViewTranslationY.start()
            //params.topMargin += getResources().getDimension(R.dimen.status_bar_margin);
            //lockAnim.setLayoutParams(params);
            //spaceView.setVisibility(View.VISIBLE);
        }
        val lockAnimScaleX: ObjectAnimator = ObjectAnimator.ofFloat(lockAnim, "scaleX", 4f, 2f)
        val lockAnimScaleY: ObjectAnimator = ObjectAnimator.ofFloat(lockAnim, "scaleY", 4f, 2f)
        val params: ConstraintLayout.LayoutParams =
            lockAnim!!.layoutParams as ConstraintLayout.LayoutParams
        val currentVBias: Float = params.verticalBias
        val lockAnimVerticalBias: ValueAnimator = ValueAnimator.ofFloat(currentVBias, 0f)
        lockAnimVerticalBias.addUpdateListener { animation: ValueAnimator ->
            params.verticalBias = animation.getAnimatedValue() as Float
            lockAnim!!.setLayoutParams(params)
        }
        animatorSet = AnimatorSet()
        animatorSet!!.playTogether(lockAnimScaleX, lockAnimScaleY, lockAnimVerticalBias)
        animatorSet!!.setDuration(600)
        animatorSet!!.start()
        val passwordLayoutAlpha: ObjectAnimator =
            ObjectAnimator.ofFloat(passwordLayout, "alpha", 0f, 1f)
        val passwordLayoutTranslationY: ObjectAnimator =
            ObjectAnimator.ofFloat(passwordLayout, "translationY", 1000f, 1f)
        animatorSet = AnimatorSet()
        animatorSet!!.playTogether(passwordLayoutAlpha, passwordLayoutTranslationY)
        animatorSet!!.setStartDelay(275)
        animatorSet!!.setDuration(350)
        animatorSet!!.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                passwordLayout!!.visibility = View.VISIBLE
            }

            override fun onAnimationEnd(animation: Animator) {
            }

            override fun onAnimationCancel(animation: Animator) {
            }

            override fun onAnimationRepeat(animation: Animator) {
            }
        })
        animatorSet!!.start()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun showPasswordDots() {
        when (password!!.length) {
            0 -> {
                passkeyDigit1!!.setImageDrawable(resources.getDrawable(R.drawable.password_block_focused))
                passkeyDigit2!!.setImageDrawable(resources.getDrawable(R.drawable.password_block_none))
                passkeyDigit3!!.setImageDrawable(resources.getDrawable(R.drawable.password_block_none))
                passkeyDigit4!!.setImageDrawable(resources.getDrawable(R.drawable.password_block_none))
                if (isBackspacePressed) {
                    setDigitEnterAnimation(passkeyDigit1)
                }
            }

            1 -> {
                passkeyDigit1!!.setImageDrawable(resources.getDrawable(R.drawable.password_block_filled))
                passkeyDigit2!!.setImageDrawable(resources.getDrawable(R.drawable.password_block_focused))
                passkeyDigit3!!.setImageDrawable(resources.getDrawable(R.drawable.password_block_none))
                passkeyDigit4!!.setImageDrawable(resources.getDrawable(R.drawable.password_block_none))
                if (isBackspacePressed) {
                    setDigitEnterAnimation(passkeyDigit2)
                } else {
                    setDigitEnterAnimation(passkeyDigit1)
                }
            }

            2 -> {
                passkeyDigit1!!.setImageDrawable(resources.getDrawable(R.drawable.password_block_filled))
                passkeyDigit2!!.setImageDrawable(resources.getDrawable(R.drawable.password_block_filled))
                passkeyDigit3!!.setImageDrawable(resources.getDrawable(R.drawable.password_block_focused))
                passkeyDigit4!!.setImageDrawable(resources.getDrawable(R.drawable.password_block_none))
                if (isBackspacePressed) {
                    setDigitEnterAnimation(passkeyDigit3)
                } else {
                    setDigitEnterAnimation(passkeyDigit2)
                }
            }

            3 -> {
                passkeyDigit1!!.setImageDrawable(resources.getDrawable(R.drawable.password_block_filled))
                passkeyDigit2!!.setImageDrawable(resources.getDrawable(R.drawable.password_block_filled))
                passkeyDigit3!!.setImageDrawable(resources.getDrawable(R.drawable.password_block_filled))
                passkeyDigit4!!.setImageDrawable(resources.getDrawable(R.drawable.password_block_focused))
                if (isBackspacePressed) {
                    setDigitEnterAnimation(passkeyDigit4)
                } else {
                    setDigitEnterAnimation(passkeyDigit3)
                }
            }

            4 -> {
                passkeyDigit1!!.setImageDrawable(resources.getDrawable(R.drawable.password_block_filled))
                passkeyDigit2!!.setImageDrawable(resources.getDrawable(R.drawable.password_block_filled))
                passkeyDigit3!!.setImageDrawable(resources.getDrawable(R.drawable.password_block_filled))
                passkeyDigit4!!.setImageDrawable(resources.getDrawable(R.drawable.password_block_filled))
                if (!isBackspacePressed) {
                    setDigitEnterAnimation(passkeyDigit4)
                }
                if (loadFromSettings) {
                    if (Saver.Companion.getInstance(requireContext())
                            .appPassword == Saver.Companion.DEF_PASSKEY || newPasswordWanted
                    ) {
                        if (newPassword == null) {
                            if (loadedPassword != null) {
                                if (password.toString() != loadedPassword) {
                                    setupForReEnterNewPassword()
                                } else {
                                    Toast.Companion.makeText(
                                        requireContext(),
                                        "گذرواژه جدید نمیتواند مثل گذرواژه قدیمی باشد!",
                                        Toast.Companion.WARNING_SIGN,
                                        Toast.Companion.LENGTH_LONG
                                    ).show()
                                    password = StringBuilder()
                                    clearPassword()
                                }
                            } else {
                                setupForReEnterNewPassword()
                            }
                        } else {
                            checkPassword(newPassword)
                        }
                    } else {
                        checkPassword(loadedPassword)
                    }
                } else {
                    checkPassword(loadedPassword)
                }
            }

            else -> {}
        }
    }

    private fun setupForReEnterNewPassword() {
        title!!.text = "گذرواژه جدید خود را دوباره وارد کنید"
        subtitle!!.text = "لطفاً با دقت این کار را انجام دهید!"
        subtitle!!.setTextColor(resources.getColor(R.color.edu_level_bad))
        subtitle!!.visibility = View.VISIBLE
        newPassword = password.toString()
        clearPassword()
    }

    private fun setDigitEnterAnimation(passkeyDigit: ImageView?) {
        val enterPassKeyScaleX: ObjectAnimator =
            ObjectAnimator.ofFloat(passkeyDigit, "scaleX", 1.25f, 1.4f, 1.25f)
        val enterPassKeyScaleY: ObjectAnimator =
            ObjectAnimator.ofFloat(passkeyDigit, "scaleY", 1.25f, 1.4f, 1.25f)
        animatorSet = AnimatorSet()
        animatorSet!!.playTogether(enterPassKeyScaleX, enterPassKeyScaleY)
        animatorSet!!.setDuration(250)
        animatorSet!!.start()
        vibrator!!.vibrate(50)
    }

    private fun setCorrectPasswordDigitAnimation(passkeyDigit: ImageView?) {
        val enterPassKeyScaleX: ObjectAnimator =
            ObjectAnimator.ofFloat(passkeyDigit, "scaleX", 1.25f, 1.4f, 1.25f)
        val enterPassKeyScaleY: ObjectAnimator =
            ObjectAnimator.ofFloat(passkeyDigit, "scaleY", 1.25f, 1.4f, 1.25f)
        animatorSet = AnimatorSet()
        animatorSet!!.playTogether(enterPassKeyScaleX, enterPassKeyScaleY)
        animatorSet!!.setDuration(250)
        animatorSet!!.start()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun checkPassword(currentPassword: String?) {
        if (password.toString() != currentPassword) {
            passkeyDigit1!!.setImageDrawable(resources.getDrawable(R.drawable.password_block_filled_wrong))
            passkeyDigit2!!.setImageDrawable(resources.getDrawable(R.drawable.password_block_filled_wrong))
            passkeyDigit3!!.setImageDrawable(resources.getDrawable(R.drawable.password_block_filled_wrong))
            passkeyDigit4!!.setImageDrawable(resources.getDrawable(R.drawable.password_block_filled_wrong))
            @SuppressLint("Recycle") val translationXWrongPasswordAnim: ObjectAnimator =
                ObjectAnimator.ofFloat(
                    passkeyDigitsLayout,
                    "translationX",
                    1f,
                    -WRONG_PASSWORD_VIBRATION_EFFECT_LENGTH,
                    WRONG_PASSWORD_VIBRATION_EFFECT_LENGTH,
                    1f
                )
            translationXWrongPasswordAnim.setStartDelay(50)
            translationXWrongPasswordAnim.setDuration(100)
            translationXWrongPasswordAnim.repeatCount = 5
            translationXWrongPasswordAnim.start()
            translationXWrongPasswordAnim.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                }

                override fun onAnimationEnd(animation: Animator) {
                    clearPassword()
                }

                override fun onAnimationCancel(animation: Animator) {
                }

                override fun onAnimationRepeat(animation: Animator) {
                }
            })
            vibrator!!.vibrate(500)
        } else {
            secureKeypad!!.isClickable = false
            Handler().postDelayed({
                setCorrectPasswordDigitAnimation(passkeyDigit1)
                setCorrectPasswordDigitAnimation(passkeyDigit2)
                setCorrectPasswordDigitAnimation(passkeyDigit3)
                setCorrectPasswordDigitAnimation(passkeyDigit4)
                onAuthenticationSucceed()
            }, 100)
        }
    }

    private fun clearPassword() {
        password = StringBuilder()
        Handler().postDelayed({
            passkeyDigit4!!.setImageDrawable(resources.getDrawable(R.drawable.password_block_none))
            Handler().postDelayed({
                passkeyDigit3!!.setImageDrawable(resources.getDrawable(R.drawable.password_block_none))
                Handler().postDelayed({
                    passkeyDigit2!!.setImageDrawable(resources.getDrawable(R.drawable.password_block_none))
                    Handler().postDelayed({
                        passkeyDigit1!!.setImageDrawable(resources.getDrawable(R.drawable.password_block_none))
                        Handler().postDelayed({
                            passkeyDigit1!!.setImageDrawable(
                                resources.getDrawable(
                                    R.drawable.password_block_focused
                                )
                            )
                        }, CLEAR_PASSWORD_FIELD_DELAY_MILLIS.toLong())
                    }, CLEAR_PASSWORD_FIELD_DELAY_MILLIS.toLong())
                }, CLEAR_PASSWORD_FIELD_DELAY_MILLIS.toLong())
            }, CLEAR_PASSWORD_FIELD_DELAY_MILLIS.toLong())
        }, CLEAR_PASSWORD_FIELD_DELAY_MILLIS.toLong())
    }

    private fun onClicks(view: View) {
        enterThroughFingerprint!!.setOnClickListener(View.OnClickListener { v: View? -> useFingerprintAuthentication() })
        setupKeypad(view)
    }

    private fun setupKeypad(view: View) {
        for (i in 0 until secureKeypad!!.childCount) {
            val keypadRow: LinearLayout = secureKeypad!!.getChildAt(i) as LinearLayout
            for (j in 0 until keypadRow.childCount) {
                keypadRow.getChildAt(j).setOnClickListener { v: View ->
                    if (v.id != R.id.keypad_backspace) {
                        if (password!!.length < 4) password!!.append((v as Button).text)
                    } else {
                        if (password!!.length != 0) password!!.deleteCharAt(password!!.length - 1)
                    }
                    isBackspacePressed = v.id == R.id.keypad_backspace
                    view.findViewById<View>(R.id.keypad_backspace).isClickable =
                        password!!.length in 1..3
                    showPasswordDots()
                }
            }
        }
    }

    private fun useFingerprintAuthentication() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val biometricManager = BiometricManager.from(requireActivity())
            if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL) == BiometricManager.BIOMETRIC_SUCCESS) {
                val keyguardManager: KeyguardManager =
                    requireContext().getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
                if (keyguardManager.isDeviceSecure) {
                    showFingerprintPrompt()
                }
            }
        }
    }

    private fun showFingerprintPrompt() {
        val executor: Executor = ContextCompat.getMainExecutor(requireActivity())
        val biometricPrompt = BiometricPrompt(
            requireActivity(),
            executor,
            object : BiometricPrompt.AuthenticationCallback() {

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onAuthenticationSucceed()
                }

            })

        val promptInfo: BiometricPrompt.PromptInfo = BiometricPrompt.PromptInfo.Builder().setTitle("ورود به پاسخنامه")
            .setSubtitle("تعیین کنید که این شمایید!").setNegativeButtonText("درعوض، استفاده از پین")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    private fun onAuthenticationSucceed() {
        if (loadFromSettings) {
            if (newPassword != null) {
                newPasswordWanted = false
                Saver.getInstance(requireContext()).appPassword = (newPassword!!.toInt())
                Toast.makeText(
                    requireContext(),
                    "گذرواژه جدید شما تنظیم شد.",
                    Toast.WARNING_SIGN,
                    Toast.LENGTH_SHORT
                ).show()
                requireActivity().supportFragmentManager.beginTransaction().remove(this)
                    .setTransition(
                        FragmentTransaction.TRANSIT_FRAGMENT_CLOSE
                    ).commit()
            } else {
                title!!.setText("گذرواژه جدید خود را وارد کنید.")
                subtitle!!.setVisibility(View.INVISIBLE)
                newPasswordWanted = true
                clearPassword()
            }
        } else {
            authenticationListener?.onAuthenticationSucceed()
            requireActivity().supportFragmentManager.beginTransaction().remove(this).setTransition(
                FragmentTransaction.TRANSIT_FRAGMENT_CLOSE
            ).commit()
        }
    }

    private fun init(v: View) {
        password = StringBuilder()
        vibrator = requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        secureKeypad = v.findViewById<LinearLayout>(R.id.secure_keypad)
        title = v.findViewById<TextView>(R.id.textView3)
        subtitle = v.findViewById<TextView>(R.id.textView4)
        lockAnim = v.findViewById<LottieAnimationView>(R.id.img_lock_anim)
        spaceView = v.findViewById<View>(R.id.space_view)
        passwordLayout = v.findViewById<LinearLayout>(R.id.password_layout)
        enterThroughFingerprint = v.findViewById<LinearLayout>(R.id.enter_through_fingerprint)
        passkeyDigitsLayout = v.findViewById<LinearLayout>(R.id.passkey_digits_layout)
        passkeyDigit1 = v.findViewById<ImageView>(R.id.passkey_digit1)
        passkeyDigit2 = v.findViewById<ImageView>(R.id.passkey_digit2)
        passkeyDigit3 = v.findViewById<ImageView>(R.id.passkey_digit3)
        passkeyDigit4 = v.findViewById<ImageView>(R.id.passkey_digit4)
    }

    companion object {
        const val WRONG_PASSWORD_VIBRATION_EFFECT_LENGTH: Float = 25f
        const val CLEAR_PASSWORD_FIELD_DELAY_MILLIS: Int = 75
        const val LOAD_FROM_SETTINGS: String = "load_from_settings"
    }
}