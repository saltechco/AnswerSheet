package ir.saltech.answersheet.view.activity

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.graphics.PorterDuff
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Environment
import android.os.Handler
import android.os.PowerManager
import android.os.Vibrator
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.KeyCharacterMap
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adivery.sdk.Adivery
import com.adivery.sdk.AdiveryAdListener
import com.adivery.sdk.AdiveryBannerAdView
import com.adivery.sdk.AdiveryListener
import com.airbnb.lottie.LottieAnimationView
import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar
import com.android.graphics.CanvasView
import com.google.android.material.textfield.TextInputLayout
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import com.shawnlin.numberpicker.NumberPicker
import com.yy.mobile.rollingtextview.CharOrder
import com.yy.mobile.rollingtextview.RollingTextView
import com.yy.mobile.rollingtextview.strategy.Direction
import com.yy.mobile.rollingtextview.strategy.Strategy.SameDirectionAnimation
import eightbitlab.com.blurview.BlurView
import ir.saltech.answersheet.R
import ir.saltech.answersheet.intf.listener.AuthenticationChangedListener
import ir.saltech.answersheet.intf.listener.CollapseBarChangedListener
import ir.saltech.answersheet.intf.listener.ExamSelectedListener
import ir.saltech.answersheet.intf.listener.QuestionClickedListener
import ir.saltech.answersheet.intf.listener.ThingSelectedListener
import ir.saltech.answersheet.intf.listener.ToggleButtonPartClickedListener
import ir.saltech.answersheet.`object`.container.Saver
import ir.saltech.answersheet.`object`.data.Bookmark
import ir.saltech.answersheet.`object`.data.Category
import ir.saltech.answersheet.`object`.data.Document
import ir.saltech.answersheet.`object`.data.Exam
import ir.saltech.answersheet.`object`.data.ExamName
import ir.saltech.answersheet.`object`.data.ExamNames
import ir.saltech.answersheet.`object`.data.ExamWallpaper
import ir.saltech.answersheet.`object`.data.Exams
import ir.saltech.answersheet.`object`.data.Question
import ir.saltech.answersheet.`object`.data.Questions
import ir.saltech.answersheet.`object`.data.Thing
import ir.saltech.answersheet.`object`.enums.WallpaperType
import ir.saltech.answersheet.`object`.util.DateConverter
import ir.saltech.answersheet.`object`.util.TensorModelLoader
import ir.saltech.answersheet.view.adapter.ExamsViewAdapter
import ir.saltech.answersheet.view.adapter.QuestionsViewAdapter
import ir.saltech.answersheet.view.container.BlurViewHolder
import ir.saltech.answersheet.view.container.MaterialAlert
import ir.saltech.answersheet.view.container.MaterialAlertDialog
import ir.saltech.answersheet.view.container.MaterialFragmentShower
import ir.saltech.answersheet.view.container.Toast
import ir.saltech.answersheet.view.dialog.SelectThingsDialog
import ir.saltech.answersheet.view.fragment.AuthFragment
import ir.saltech.answersheet.view.fragment.CollapsablePanelFragment
import ir.saltech.answersheet.view.fragment.SettingsFragment
import ir.saltech.answersheet.view.holder.ExamViewHolder
import ir.tapsell.plus.AdRequestCallback
import ir.tapsell.plus.AdShowListener
import ir.tapsell.plus.TapsellPlus
import ir.tapsell.plus.TapsellPlusBannerType
import ir.tapsell.plus.TapsellPlusInitListener
import ir.tapsell.plus.model.AdNetworkError
import ir.tapsell.plus.model.AdNetworks
import ir.tapsell.plus.model.TapsellPlusAdModel
import ir.tapsell.plus.model.TapsellPlusErrorModel
import jp.wasabeef.blurry.Blurry
import org.jetbrains.annotations.Contract
import org.tensorflow.lite.Interpreter
import java.io.File
import java.io.FileOutputStream
import java.util.Calendar
import java.util.Collections
import java.util.Date
import java.util.Locale
import java.util.Objects
import java.util.Random
import kotlin.math.pow
import kotlin.math.sqrt

class MainActivity : AppCompatActivity(), ToggleButtonPartClickedListener {
    private val random = Random()
    var animShow: Boolean = false
    private lateinit var recentExams: Exams
    private lateinit var parentLayout: ConstraintLayout
    private lateinit var menuItemsLayout: LinearLayout
    private lateinit var recentExamsLayout: ConstraintLayout
    private lateinit var userDashboardLayout: ConstraintLayout
    private lateinit var examsViewLayout: ConstraintLayout
    private lateinit var appMoreOptions: ImageButton
    private lateinit var clickableArea: View
    private lateinit var addExamButton: CardView
    private lateinit var startedExamsButton: CardView
    private lateinit var suspendedExamsButton: CardView
    private lateinit var finishedExamsButton: CardView
    private lateinit var creatingExamsButton: CardView
    private lateinit var correctingExamButton: CardView
    private lateinit var examsViewCard: CardView
    private lateinit var lastQuestionNoText: TextInputLayout
    private lateinit var questionsCPatternText: TextInputLayout
    private lateinit var firstQuestionNoText: TextInputLayout
    private lateinit var questionsCountText: TextInputLayout
    private lateinit var selectQuestionsRandomly: LinearLayout
    private lateinit var examsViewEmpty: TextView
    private lateinit var selectQuestionsMode: LinearLayout
    private lateinit var selectCorrectionMode: LinearLayout
    private lateinit var examsListBack: ImageButton
    private lateinit var examsViewTitle: TextView
    private lateinit var examsView: RecyclerView
    private lateinit var splashSalTechImg: ImageView
    private lateinit var splashAnim: LottieAnimationView
    private lateinit var appIcon: ImageView
    private lateinit var appTitle: TextView
    private lateinit var animatorSet: AnimatorSet
    private lateinit var recentExamsList: MutableList<Exam>
    private lateinit var currentExams: MutableList<Exam>
    private lateinit var suspendedExamsList: MutableList<Exam>
    private lateinit var creatingExamsList: MutableList<Exam>
    private lateinit var correctingExamsList: MutableList<Exam>
    private lateinit var examsViewAdapter: ExamsViewAdapter
    private lateinit var welcomeLayout: ConstraintLayout
    private lateinit var selectExamImmediately: ConstraintLayout
    private lateinit var createCustomExam: LinearLayout
    private lateinit var startExamButtonsLayout: LinearLayout
    private lateinit var welcomeImage: LottieAnimationView
    private lateinit var welcomeCompanyLogo: ImageView
    private lateinit var welcomeTitle: TextView
    private lateinit var welcomeAppDesc: TextView
    private lateinit var welcomeClickContinue: TextView
    private lateinit var minuteNP: NumberPicker
    private lateinit var secondNP: NumberPicker
    private lateinit var chronoThresholdNP: NumberPicker
    private lateinit var selectCategoryEnabled: LinearLayout
    private lateinit var useCategoryTimingEnabled: LinearLayout
    private lateinit var useCategoryScoreEnabled: LinearLayout
    private lateinit var splashScreenLayout: ConstraintLayout
    private lateinit var userDashboardButton: ImageButton
    private var optionsMenuOpened = false
    private lateinit var examFeatures: String
    private lateinit var vibrator: Vibrator
    private var repeatCount = 0
    private var welcomePageShowed = false
    private lateinit var setupExamTimeColon: TextView
    private lateinit var recentExamsScrollContainer: ScrollView
    private lateinit var createExamLayoutContainer: ScrollView
    private lateinit var examRunningLayout: ConstraintLayout
    private lateinit var examSetupLayout: ConstraintLayout
    private var selectedQRandomly = false
    private var useExamCategorize = false
    private lateinit var selectChronometerEnabled: LinearLayout
    private var useChronometer = false
    private var defaultPaletteColor = 0
    private lateinit var examAnimView: LottieAnimationView
    private lateinit var examPictureView: ImageView
    private lateinit var examNameText: TextView
    private lateinit var examTimeBar: CircularProgressBar
    private lateinit var startCurrentExam: Button
    private lateinit var runningExamOptions: LinearLayout
    private lateinit var examControlPanel: BlurView
    private lateinit var examAnswerSheetEmptyError: BlurView
    private lateinit var examDraftPages: FrameLayout
    private lateinit var answerSheetView: RecyclerView
    private val questionLayoutClosed = false
    private var questions: MutableList<Question> = mutableListOf()
    private lateinit var selectExamName: LinearLayout
    private lateinit var selectExamDocument: LinearLayout
    private var currentExam: Exam = Exam()
    private lateinit var backToMainView: ImageButton
    private lateinit var enterToExamRoom: Button
    private lateinit var scheduleCurrentExam: ImageButton
    private var currentExamName: ExamName? = null
    private var firstQuestion = 1
    private var lastQuestion = 0
    private var questionsCPattern = 1
    private var questionsCount = 10
    private lateinit var shortcutExamPreparingLayout: ConstraintLayout
    private lateinit var debugVerWatermark: TextView
    private var minute: Long = 0
    private var second: Long = 0
    private var isStartedManualExam = false
    private var correctionMode: Exam.CorrectionMode = Exam.CorrectionMode.None
    private var useCategoryScore = false
    private var useCategoryTiming = false
    private var chronoThreshold = 0
    private var examTime: Long = 0
    private lateinit var examTimeBoard: RollingTextView
    private var isQuestionsManually = false
    private var startedExamTime: String? = null
    private var examFile: Document? = null
    private lateinit var questionsAdapter: QuestionsViewAdapter
    private lateinit var examAction: ImageButton
    private lateinit var jumpToQuestion: ImageButton
    private lateinit var openDraftBox: ImageButton
    private lateinit var openExamFileBox: ImageButton
    private lateinit var addedBookmarksButton: ImageButton
    private lateinit var addQuestionButton: ImageButton
    private lateinit var removeQuestionButton: ImageButton
    private lateinit var resetChronometer: ImageButton
    private lateinit var shareWorksheetButton: ImageButton
    private lateinit var enableNegativePoint: ImageButton
    private lateinit var timerForThinkingTime: CountDownTimer
    private var buttonDisableSeconds: Long = 0
    private lateinit var examTimeLeft: CountDownTimer
    private val examHeaderCollapsed = false
    private var isRecentExamLoaded = false
    private lateinit var collapseExamHeader: ImageButton
    private lateinit var collapseExamTimeBar: RoundCornerProgressBar
    private var resetEnable = false
    private var startedTimeExam = false
    private var examTimeLeftUntilFinished: Long = 0
    private var criticalTimeVibrationRang = false
    private var warningTimeVibrationRang = false
    private lateinit var stopWatchEffectPlayer: MediaPlayer
    private var runningCategory = 0
    private var isExamStoppedManually = false
    private var isExamNowEnded = false
    private var correctedAsNow = false
    private var isExamStarted = false
    private lateinit var draftDrawingHint: TextView
    private lateinit var collapseDraftView: ImageButton
    private lateinit var draftViewOptions: ImageButton
    private lateinit var examDraftLayout: BlurView
    private lateinit var answerSheetLayout: ConstraintLayout
    private lateinit var draftToolboxItems: LinearLayout
    private val examFileVisibility = false
    private var draftPathErasingEnabled = false
    private val examFileLoaded = false
    private var draftPenStrokeSize = 3f
    private var draftEraserStrokeSize = 10f
    private var selectedColor = 0
    private var isDraftCanvasCleared = true
    private var isDraftDrawingHintShown = false
    private var draftToolboxItemIndex = 0
    private var dismissDrawOptionsWindowManually = false
    private var draftOptionsItemIndex = 0
    private var penModelIndex = 0
    private var selectedMode: CanvasView.Drawer = CanvasView.Drawer.PEN
    private var selectedModeResId: Int = R.drawable.path_drawing
    private var selectedDrawingMode: CanvasView.Mode = CanvasView.Mode.DRAW
    private var selectedStyle = Paint.Style.STROKE
    private var isDraftEraserStrokeSizeEdited = false
    private var isDraftPenStrokeSizeEdited = false
    private var strokePenColorIndex = 0
    private var dismissSubmitTextWindowManually = false
    private var isPdfNightModeEnabled = false
    private var currentDraftViewIndex = 0
    private lateinit var examDraftPage: CanvasView
    private lateinit var addDraftPage: ImageButton
    private lateinit var draftPagesOptions: ImageButton
    private var currentExamStatus: Exam.ExamStatus? = Exam.ExamStatus.Creating
    private var canUsingAdditionalSubtraction = false
    private var standardBannerResponseId: String? = null
    private lateinit var standardTapsellBanner: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        stopWatchEffectPlayer = MediaPlayer.create(this, R.raw.stopwatch)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        setStatusBarTheme(
            this, !checkDarkModeEnabled(
                this
            )
        )
        init()
        setNavigationBarMargin()
        if (!Saver.Companion.getInstance(this).welcomePageShowed) {
            showWelcomePage()
        } else {
            if (Saver.Companion.getInstance(this).appPassword != 0) {
                supportFragmentManager.beginTransaction().add(
                    R.id.fragment_container, AuthFragment(
                        object : AuthenticationChangedListener {
                            override fun onAuthenticationSucceed() {
                                this@MainActivity.showSplashScreen()
                            }
                        }
                    )
                ).commit()
            } else {
                showSplashScreen()
            }
        }
    }

    private fun usingMLModel() {
        val modelLoader: TensorModelLoader = TensorModelLoader()
        val `val` = "10"
        val input = FloatArray(1)
        input[0] = `val`.toFloat()
        val output = Array(1) { FloatArray(1) }
        Objects.requireNonNull<Interpreter>(modelLoader.loadModel(this, "linear.tflite", null))
            .run(input, output)
        Toast.Companion.makeText(this, "Predict is: " + output[0][0], Toast.Companion.LENGTH_SHORT)
            .show()
    }

    /**
     * Launches application after millis.
     *
     * @param millis 0 is NO_WAIT, DEFAULT_MILLIS is 750.
     */
    private fun launchApp(millis: Long) {
        Handler().postDelayed({
            splashScreenLayout.visibility = View.GONE
            setAppParameters()
        }, if ((millis == DEFAULT_MILLIS.toLong())) 750 else millis)
    }

    private fun showMainViewLayout(which: MainView) {
        when (which) {
            MainView.UserDashboard -> {
                userDashboardLayout.visibility = View.VISIBLE
                examRunningLayout.visibility = View.GONE
                recentExamsLayout.visibility = View.GONE
                examSetupLayout.visibility = View.GONE
                welcomeLayout.visibility = View.GONE
                splashScreenLayout.visibility = View.GONE
            }

            MainView.SplashScreen -> {
                userDashboardLayout.visibility = View.GONE
                examRunningLayout.visibility = View.GONE
                recentExamsLayout.visibility = View.GONE
                examSetupLayout.visibility = View.GONE
                welcomeLayout.visibility = View.GONE
                splashScreenLayout.visibility = View.VISIBLE
            }

            MainView.ExamSetup -> {
                selectExamImmediately.visibility = View.VISIBLE
                userDashboardLayout.visibility = View.GONE
                examRunningLayout.visibility = View.GONE
                recentExamsLayout.visibility = View.GONE
                examSetupLayout.visibility = View.VISIBLE
                welcomeLayout.visibility = View.GONE
                splashScreenLayout.visibility = View.GONE
            }

            MainView.ExamRunning -> {
                userDashboardLayout.visibility = View.GONE
                examRunningLayout.visibility = View.VISIBLE
                recentExamsLayout.visibility = View.GONE
                examSetupLayout.visibility = View.GONE
                welcomeLayout.visibility = View.GONE
                splashScreenLayout.visibility = View.GONE
                playExamRunning()
            }

            MainView.RecentExams -> {
                userDashboardLayout.visibility = View.GONE
                examRunningLayout.visibility = View.GONE
                recentExamsLayout.visibility = View.VISIBLE
                examSetupLayout.visibility = View.GONE
                welcomeLayout.visibility = View.GONE
                splashScreenLayout.visibility = View.GONE
            }

            MainView.WelcomePage -> {
                userDashboardLayout.visibility = View.GONE
                examRunningLayout.visibility = View.GONE
                recentExamsLayout.visibility = View.GONE
                examSetupLayout.visibility = View.GONE
                welcomeLayout.visibility = View.VISIBLE
                splashScreenLayout.visibility = View.GONE
            }

            else -> {}
        }
    }

    private fun playExamRunning() {
        val eRScaleXAnim: ObjectAnimator =
            ObjectAnimator.ofFloat(examRunningLayout, "scaleX", 0.75f, 1f)
        val eRScaleYAnim: ObjectAnimator =
            ObjectAnimator.ofFloat(examRunningLayout, "scaleY", 0.75f, 1f)
        val eRAlphaAnim: ObjectAnimator = ObjectAnimator.ofFloat(examRunningLayout, "alpha", 0f, 1f)
        val eRTranslationYAnim: ObjectAnimator =
            ObjectAnimator.ofFloat(examRunningLayout, "translationY", 250f, 1f)
        animatorSet = AnimatorSet()
        animatorSet.playTogether(eRScaleXAnim, eRScaleYAnim, eRAlphaAnim, eRTranslationYAnim)
        animatorSet.setDuration(300)
        animatorSet.setStartDelay(150)
        animatorSet.start()
    }

    private fun showWelcomePage() {
        welcomePageShowed = true
        showMainViewLayout(MainView.WelcomePage)
        welcomeTitle.text = "ســـلام!!! 🙋🏻‍♂️😃😄"
        val showTitle1Alpha: ObjectAnimator = ObjectAnimator.ofFloat(welcomeTitle, "alpha", 0f, 1f)
        val showTitle1ScaleX: ObjectAnimator =
            ObjectAnimator.ofFloat(welcomeTitle, "scaleX", 0f, 1.3f)
        val showTitle1ScaleY: ObjectAnimator =
            ObjectAnimator.ofFloat(welcomeTitle, "scaleY", 0f, 1.3f)
        animatorSet = AnimatorSet()
        animatorSet.playTogether(showTitle1Alpha, showTitle1ScaleY, showTitle1ScaleX)
        animatorSet.setStartDelay(300)
        animatorSet.setDuration(300)
        animatorSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animator: Animator) {
            }

            override fun onAnimationEnd(animator: Animator) {
                val hideTitle1Alpha: ObjectAnimator =
                    ObjectAnimator.ofFloat(welcomeTitle, "alpha", 1f, 0f)
                val hideTitle1ScaleX: ObjectAnimator =
                    ObjectAnimator.ofFloat(welcomeTitle, "scaleX", 1.3f, 0f)
                val hideTitle1ScaleY: ObjectAnimator =
                    ObjectAnimator.ofFloat(welcomeTitle, "scaleY", 1.3f, 0f)
                animatorSet = AnimatorSet()
                animatorSet.playTogether(hideTitle1Alpha, hideTitle1ScaleY, hideTitle1ScaleX)
                animatorSet.setStartDelay(1050)
                animatorSet.setDuration(300)
                animatorSet.addListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animator: Animator) {
                    }

                    override fun onAnimationEnd(animator: Animator) {
                        welcomeTitle.text = getString(R.string.welcome_title)
                        val showTitle2Alpha: ObjectAnimator =
                            ObjectAnimator.ofFloat(welcomeTitle, "alpha", 0f, 1f)
                        val showTitle2ScaleX: ObjectAnimator =
                            ObjectAnimator.ofFloat(welcomeTitle, "scaleX", 0f, 1.05f)
                        val showTitle2ScaleY: ObjectAnimator =
                            ObjectAnimator.ofFloat(welcomeTitle, "scaleY", 0f, 1.05f)
                        animatorSet = AnimatorSet()
                        animatorSet.playTogether(
                            showTitle2Alpha,
                            showTitle2ScaleY,
                            showTitle2ScaleX
                        )
                        animatorSet.setStartDelay(450)
                        animatorSet.setDuration(300)
                        animatorSet.addListener(object : Animator.AnimatorListener {
                            override fun onAnimationStart(animator: Animator) {
                            }

                            override fun onAnimationEnd(animator: Animator) {
                                val params: ConstraintLayout.LayoutParams =
                                    welcomeTitle.layoutParams as ConstraintLayout.LayoutParams
                                val hideTitle2VBias: ValueAnimator =
                                    ValueAnimator.ofFloat(0.5f, 0.55f)
                                hideTitle2VBias.addUpdateListener(ValueAnimator.AnimatorUpdateListener { valueAnimator: ValueAnimator ->
                                    params.verticalBias = valueAnimator.getAnimatedValue() as Float
                                    welcomeTitle.setLayoutParams(params)
                                })
                                val hideTitle2ScaleX: ObjectAnimator =
                                    ObjectAnimator.ofFloat(welcomeTitle, "scaleX", 1.05f, 1f)
                                val hideTitle2ScaleY: ObjectAnimator =
                                    ObjectAnimator.ofFloat(welcomeTitle, "scaleY", 1.05f, 1f)
                                val params2: ConstraintLayout.LayoutParams =
                                    welcomeImage.layoutParams as ConstraintLayout.LayoutParams
                                val showImageVBias: ValueAnimator =
                                    ValueAnimator.ofFloat(0.5f, 0.2f)
                                showImageVBias.addUpdateListener(ValueAnimator.AnimatorUpdateListener { valueAnimator: ValueAnimator ->
                                    params2.verticalBias = valueAnimator.getAnimatedValue() as Float
                                    welcomeImage.setLayoutParams(params2)
                                })
                                val showImageAlpha: ObjectAnimator =
                                    ObjectAnimator.ofFloat(welcomeImage, "alpha", 0f, 1f)
                                val showImageScaleX: ObjectAnimator =
                                    ObjectAnimator.ofFloat(welcomeImage, "scaleX", 0f, 1f)
                                val showImageScaleY: ObjectAnimator =
                                    ObjectAnimator.ofFloat(welcomeImage, "scaleY", 0f, 1f)
                                animatorSet = AnimatorSet()
                                animatorSet.playTogether(
                                    hideTitle2VBias,
                                    hideTitle2ScaleY,
                                    hideTitle2ScaleX
                                )
                                animatorSet.playTogether(
                                    showImageVBias,
                                    showImageAlpha,
                                    showImageScaleY,
                                    showImageScaleX
                                )
                                animatorSet.setStartDelay(1050)
                                animatorSet.setDuration(1000)
                                animatorSet.addListener(object : Animator.AnimatorListener {
                                    override fun onAnimationStart(animator: Animator) {
                                    }

                                    override fun onAnimationEnd(animator: Animator) {
                                        val showText1Alpha: ObjectAnimator =
                                            ObjectAnimator.ofFloat(welcomeAppDesc, "alpha", 0f, 1f)
                                        val showText1TranslationY: ObjectAnimator =
                                            ObjectAnimator.ofFloat(
                                                welcomeAppDesc,
                                                "translationY",
                                                100f,
                                                1f
                                            )
                                        val showText2Alpha: ObjectAnimator = ObjectAnimator.ofFloat(
                                            welcomeClickContinue,
                                            "alpha",
                                            0f,
                                            1f
                                        )
                                        showText2Alpha.setStartDelay(300)
                                        val showText2TranslationY: ObjectAnimator =
                                            ObjectAnimator.ofFloat(
                                                welcomeClickContinue,
                                                "translationY",
                                                100f,
                                                1f
                                            )
                                        animatorSet = AnimatorSet()
                                        animatorSet.playTogether(
                                            showText1Alpha,
                                            showText1TranslationY
                                        )
                                        animatorSet.playTogether(
                                            showText2Alpha,
                                            showText2TranslationY
                                        )
                                        animatorSet.setStartDelay(500)
                                        animatorSet.setDuration(500)
                                        animatorSet.addListener(object : Animator.AnimatorListener {
                                            override fun onAnimationStart(animator: Animator) {
                                            }

                                            override fun onAnimationEnd(animator: Animator) {
                                                val showCompanyLogo: ObjectAnimator =
                                                    ObjectAnimator.ofFloat(
                                                        welcomeCompanyLogo,
                                                        "alpha",
                                                        0f,
                                                        1f
                                                    )
                                                showCompanyLogo.setStartDelay(300)
                                                showCompanyLogo.setDuration(500)
                                                showCompanyLogo.addListener(object :
                                                    Animator.AnimatorListener {
                                                    override fun onAnimationStart(animator: Animator) {
                                                    }

                                                    override fun onAnimationEnd(animator: Animator) {
                                                        welcomeLayout.setOnClickListener(View.OnClickListener { view: View? ->
                                                            launchApp(
                                                                100
                                                            )
                                                        })
                                                        welcomeClickContinue.setOnClickListener(
                                                            View.OnClickListener { view: View? ->
                                                                launchApp(
                                                                    100
                                                                )
                                                            })
                                                    }

                                                    override fun onAnimationCancel(animator: Animator) {
                                                    }

                                                    override fun onAnimationRepeat(animator: Animator) {
                                                    }
                                                })
                                                showCompanyLogo.start()
                                                val showText2ScaleX: ObjectAnimator =
                                                    ObjectAnimator.ofFloat(
                                                        welcomeClickContinue,
                                                        "scaleX",
                                                        1f,
                                                        1.04f,
                                                        1f
                                                    )
                                                val showText2ScaleY: ObjectAnimator =
                                                    ObjectAnimator.ofFloat(
                                                        welcomeClickContinue,
                                                        "scaleY",
                                                        1f,
                                                        1.04f,
                                                        1f
                                                    )
                                                showText2ScaleX.setStartDelay(500)
                                                showText2ScaleX.setDuration(1000)
                                                showText2ScaleX.repeatCount = -1
                                                showText2ScaleX.start()
                                                showText2ScaleY.setStartDelay(500)
                                                showText2ScaleY.setDuration(1000)
                                                showText2ScaleY.repeatCount = -1
                                                showText2ScaleY.start()
                                            }

                                            override fun onAnimationCancel(animator: Animator) {
                                            }

                                            override fun onAnimationRepeat(animator: Animator) {
                                            }
                                        })
                                        animatorSet.start()
                                    }

                                    override fun onAnimationCancel(animator: Animator) {
                                    }

                                    override fun onAnimationRepeat(animator: Animator) {
                                    }
                                })
                                animatorSet.start()
                            }

                            override fun onAnimationCancel(animator: Animator) {
                            }

                            override fun onAnimationRepeat(animator: Animator) {
                            }
                        })
                        animatorSet.start()
                    }

                    override fun onAnimationCancel(animator: Animator) {
                    }

                    override fun onAnimationRepeat(animator: Animator) {
                    }
                })
                animatorSet.start()
            }

            override fun onAnimationCancel(animator: Animator) {
            }

            override fun onAnimationRepeat(animator: Animator) {
            }
        })
        animatorSet.start()
    }

    /**
     * This method checks if phone has < 4GB RAM, visual effects will be disabled.
     */
    private fun checkDeviceSupport(): Boolean {
        val am: ActivityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val mi: ActivityManager.MemoryInfo = ActivityManager.MemoryInfo()
        am.getMemoryInfo(mi)
        Log.i("TAG", "Total Memory: " + mi.totalMem)
        return mi.totalMem >= MIN_OF_DEVICE_RAM_CAPACITY
    }

    private fun setAppParameters() {
        welcomePageShowed = false
        Saver.Companion.getInstance(this@MainActivity).setAppRestartWanted(false)
        Saver.Companion.getInstance(this@MainActivity).welcomePageShowed = (true)
        showRecentExams()
        setTexts()
        onClicks()
        showAds()
        usingMLModel()
    }

    private fun hideTapsellAds() {
        if (standardBannerResponseId != null) TapsellPlus.destroyStandardBanner(
            this,
            standardBannerResponseId,
            standardTapsellBanner
        )
    }

    private fun showAds() {
        Adivery.configure(application, getString(R.string.adivery_app_id))
        Adivery.prepareInterstitialAd(
            this@MainActivity,
            getString(R.string.adivery_interstitial_ad_id)
        )
        Adivery.addGlobalListener(object : AdiveryListener() {
            override fun onAppOpenAdLoaded(placementId: String) {
                // تبلیغ اجرای اپلیکیشن بارگذاری شده است.
            }

            override fun onInterstitialAdLoaded(placementId: String) {
                Log.d("TAG", "Interstitial Ad has been loaded")
            }

            override fun onRewardedAdLoaded(placementId: String) {
                // تبلیغ جایزه‌ای بارگذاری شده
                Log.d("TAG", "Rewarded Ad has been loaded")
                if (Adivery.isLoaded(placementId)) {
                    Adivery.showAd(placementId)
                }
            }

            override fun onRewardedAdClosed(placementId: String, isRewarded: Boolean) {
                // بررسی کنید که آیا کاربر جایزه دریافت می‌کند یا خیر
            }

            override fun log(placementId: String, log: String) {
                Log.i("TAG", "Adivery said $log\nPlacementId: $placementId")
                // پیغام را چاپ کنید
            }
        })
        TapsellPlus.initialize(
            this, getString(R.string.tapsell_plus_id),
            object : TapsellPlusInitListener {
                override fun onInitializeSuccess(adNetworks: AdNetworks) {
                    Log.d("onInitializeSuccess", adNetworks.name)
                    TapsellPlus.setGDPRConsent(this@MainActivity, true)
                    TapsellPlus.requestStandardBannerAd(
                        this@MainActivity, getString(R.string.tapsell_banner_zone_id),
                        TapsellPlusBannerType.BANNER_320x50,
                        object : AdRequestCallback() {
                            override fun response(tapsellPlusAdModel: TapsellPlusAdModel) {
                                super.response(tapsellPlusAdModel)

                                //Ad is ready to show
                                //Put the ad's responseId to your responseId variable
                                standardBannerResponseId = tapsellPlusAdModel.responseId
                                TapsellPlus.showStandardBannerAd(
                                    this@MainActivity, standardBannerResponseId,
                                    standardTapsellBanner,
                                    object : AdShowListener() {
                                        override fun onOpened(tapsellPlusAdModel: TapsellPlusAdModel) {
                                            super.onOpened(tapsellPlusAdModel)
                                        }

                                        override fun onError(tapsellPlusErrorModel: TapsellPlusErrorModel) {
                                            super.onError(tapsellPlusErrorModel)
                                        }
                                    })
                            }

                            override fun error(message: String) {
                            }
                        })
                }

                override fun onInitializeFailed(
                    adNetworks: AdNetworks,
                    adNetworkError: AdNetworkError
                ) {
                    Log.e(
                        "onInitializeFailed",
                        "ad network: " + adNetworks.name + ", error: " + adNetworkError.errorMessage
                    )
                }
            })
        val bannerAd: AdiveryBannerAdView =
            findViewById<AdiveryBannerAdView>(R.id.adivery_banner_ad)
        bannerAd.setBannerAdListener(object : AdiveryAdListener() {
            override fun onAdLoaded() {
                Log.d("TAG", "Adivery banner has been loaded.")
                // تبلیغ به‌طور خودکار نمایش داده می‌شود، هر کار دیگری لازم است اینجا انجام دهید.
            }

            override fun onError(reason: String) {
                Log.e("TAG", "Error at adivery banner loading -> $reason")
                // خطا را چاپ کنید تا از دلیل آن مطلع شوید
            }

            override fun onAdClicked() {
                // کاربر روی بنر کلیک کرده
            }
        })
        bannerAd.loadAd()
        TapsellPlus.setDebugMode(Log.DEBUG)
    }

    private fun prepareAnswerSheet() {
        if (questionsCount > 0 && firstQuestion > 0 && questionsCPattern > 0) {
            if (currentExam.answerSheet != null) {
                if (currentExam.answerSheet!!.questions!!.size != 0) questions =
                    currentExam.answerSheet!!.questions!!
            }
            if (questions.size == 0) {
                var questionsCounter = 0
                val qNumbers: MutableList<Int> = ArrayList()
                if (selectedQRandomly) {
                    var i = 0
                    while (i < questionsCount) {
                        val generatedRQN = generateRandomQNumber(firstQuestion)
                        var hasSame = false
                        for (j in qNumbers.indices) {
                            if (qNumbers[j] == generatedRQN) {
                                hasSame = true
                                break
                            }
                        }
                        if (!hasSame) {
                            qNumbers.add(generatedRQN)
                            i++
                        }
                    }
                    qNumbers.sortWith { obj: Int, anotherInteger: Int? ->
                        obj.compareTo(
                            anotherInteger!!
                        )
                    }
                }
                var qIndex = firstQuestion - 1
                while (questionsCounter < questionsCount) {
                    val q: Question = Question()
                    if (selectedQRandomly) {
                        q.questionNumber = (qNumbers[questionsCounter])
                    } else {
                        q.questionNumber = (qIndex + 1)
                    }
                    q.isWhite = (true)
                    questions.add(q)
                    questionsCounter++
                    qIndex += questionsCPattern
                }
                Saver.Companion.getInstance(this@MainActivity).saveQuestions(Questions(questions))
                if (currentExam.answerSheet == null || isQuestionsManually) {
                    currentExam.answerSheet = (Questions(questions))
                    recentExams.updateCurrentExam(currentExam)
                    Saver.Companion.getInstance(this@MainActivity).saveRecentExams(recentExams)
                }
            }
            showAnswerSheetLinear()
            // TODO: If you want to have a different screen layout for any screen sizes, enable this
//        if (getScreenSize() >= MIN_OF_NORMAL_SCREEN_SIZE) {
//            showAnswerSheetLinear();
//        } else {
//            showAnswerSheetGrid();
//        }
        }
    }

    private fun generateRandomQNumber(questionFrom: Int): Int {
        return questionFrom + random.nextInt(lastQuestion - questionFrom)
    }

    private val screenSize: Double
        get() {
            val point = Point()
            (getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
                .getRealSize(point)
            val displayMetrics: DisplayMetrics = getResources().displayMetrics
            val width = point.x
            val height = point.y
            val wi: Double = width.toDouble() / displayMetrics.xdpi.toDouble()
            val hi: Double = height.toDouble() / displayMetrics.ydpi.toDouble()
            val x: Double = wi.pow(2.0)
            val y: Double = hi.pow(2.0)
            return Math.round((sqrt(x + y)) * 10.0) / 10.0
        }

    /*@Deprecated
    private void showAnswerSheetGrid() {
        ConstraintLayout questionMainLayout = findViewById(R.id.question_main_layout_2);
        BlurView questionParentLayout = findViewById(R.id.question_parent_layout_2);
        Button closeQuestion = findViewById(R.id.close_question);
        TextView questionNumber = findViewById(R.id.question_number_2);
        float lastX = questionParentLayout.getX();
        float lastY = answerSheetView.getPivotY() + questionParentLayout.getY() - DISPLAY_PIXEL_DIFFERENCE - 25;
        setBlurView(MainActivity.this, questionParentLayout);
        questionsAdapter = new QuestionsViewAdapter(this, defaultPaletteColor, questions, (question, x, y) -> {
            questionLayoutClosed = false;
            questionMainLayout.setClickable(true);
            questionParentLayout.setVisibility(VISIBLE);
            questionNumber.setTextColor(defaultPaletteColor);
            questionNumber.setText(String.valueOf(question));
            ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(questionParentLayout, "alpha", 0f, 1f);
            ObjectAnimator asAlphaAnim = ObjectAnimator.ofFloat(answerSheetView, "alpha", 1f, 0f);
            ObjectAnimator scaleXAnim = ObjectAnimator.ofFloat(questionParentLayout, "scaleX", 0.2f, 1f);
            ObjectAnimator scaleYAnim = ObjectAnimator.ofFloat(questionParentLayout, "scaleY", 0.2f, 1f);
            ObjectAnimator asScaleXAnim = ObjectAnimator.ofFloat(answerSheetView, "scaleX", 1f, 0.7f);
            ObjectAnimator asScaleYAnim = ObjectAnimator.ofFloat(answerSheetView, "scaleY", 1f, 0.7f);
            ObjectAnimator xAnim = ObjectAnimator.ofFloat(questionParentLayout, "x", x - answerSheetView.getPivotX(), lastX);
            ObjectAnimator yAnim = ObjectAnimator.ofFloat(questionParentLayout, "y", y - answerSheetView.getPivotY() - DISPLAY_PIXEL_DIFFERENCE, lastY);
            animatorSet = new AnimatorSet();
            animatorSet.playTogether(asAlphaAnim, alphaAnim, scaleYAnim, scaleXAnim, asScaleYAnim, asScaleXAnim, xAnim, yAnim);
            animatorSet.setDuration(250);
            animatorSet.setStartDelay(50);
            animatorSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(@NonNull Animator animation) {
                    answerSheetView.setClickable(false);
                }

                @Override
                public void onAnimationEnd(@NonNull Animator animation) {
                    if (!questionLayoutClosed) closeQuestion.setVisibility(VISIBLE);
                    Button closeQuestion = findViewById(R.id.close_question);
                    closeQuestion.setOnClickListener(v -> {
                        questionLayoutClosed = true;
                        questionMainLayout.setClickable(false);
                        answerSheetView.setClickable(true);
                        questionParentLayout.setVisibility(GONE);
                        closeQuestion.setVisibility(GONE);
                        ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(questionParentLayout, "alpha", 1f, 0f);
                        ObjectAnimator asAlphaAnim = ObjectAnimator.ofFloat(answerSheetView, "alpha", 0f, 1f);
                        ObjectAnimator scaleXAnim = ObjectAnimator.ofFloat(questionParentLayout, "scaleX", 1f, 0.2f);
                        ObjectAnimator scaleYAnim = ObjectAnimator.ofFloat(questionParentLayout, "scaleY", 1f, 0.2f);
                        ObjectAnimator asScaleXAnim = ObjectAnimator.ofFloat(answerSheetView, "scaleX", 0.7f, 1f);
                        ObjectAnimator asScaleYAnim = ObjectAnimator.ofFloat(answerSheetView, "scaleY", 0.7f, 1f);
                        ObjectAnimator xAnim = ObjectAnimator.ofFloat(questionParentLayout, "x", lastX, x - answerSheetView.getPivotX());
                        ObjectAnimator yAnim = ObjectAnimator.ofFloat(questionParentLayout, "y", lastY, y - answerSheetView.getPivotY() - DISPLAY_PIXEL_DIFFERENCE);
                        animatorSet = new AnimatorSet();
                        animatorSet.playTogether(asAlphaAnim, alphaAnim, scaleYAnim, scaleXAnim, asScaleYAnim, asScaleXAnim, xAnim, yAnim);
                        animatorSet.setDuration(250);
                        animatorSet.setStartDelay(50);
                        animatorSet.start();
                    });
                }

                @Override
                public void onAnimationCancel(@NonNull Animator animation) {

                }

                @Override
                public void onAnimationRepeat(@NonNull Animator animation) {

                }
            });
            animatorSet.start();
        });
        answerSheetView.setLayoutManager(new GridLayoutManager(this, 4, RecyclerView.VERTICAL, false));
        answerSheetView.setAdapter(questionsAdapter);
        examAnswerSheetEmptyError.setVisibility((questions.size() == 0) ? VISIBLE : GONE);
    }*/
    private fun showAnswerSheetLinear() {
        questionsAdapter = QuestionsViewAdapter(
            questions,
            defaultPaletteColor,
            null,
            currentExam,
            object : QuestionClickedListener {
                override fun onQuestionClicked(question: Question?, x: Float, y: Float) {
                }

                override fun onQuestionAnswered(q: Question?) {
                }

                override fun onQuestionAnswerDeleted(q: Question?) {
                }

                override fun onQuestionBookmarkChanged(q: Question?) {
                }

                override fun onQuestionDeleted(qPosition: Int) {
                }

                override fun onQuestionCategoryClicked() {
                }
            },
            this
        )
        answerSheetView.setLayoutManager(LinearLayoutManager(this, RecyclerView.VERTICAL, false))
        answerSheetView.setAdapter(questionsAdapter)
        examAnswerSheetEmptyError.visibility =
            if ((questions.size == 0)) View.VISIBLE else View.GONE
    }

    private fun setupShortcutLayout() {
        if (shortcutExamPreparingLayout.visibility == View.VISIBLE) {
            val layoutAlphaOut: ObjectAnimator =
                ObjectAnimator.ofFloat(shortcutExamPreparingLayout, "alpha", 1f, 0f)
            layoutAlphaOut.setStartDelay(50)
            layoutAlphaOut.setDuration(250)
            layoutAlphaOut.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animator: Animator) {
                }

                override fun onAnimationEnd(animator: Animator) {
                    shortcutExamPreparingLayout.visibility = View.GONE
                    setupExam(0)
                }

                override fun onAnimationCancel(animator: Animator) {
                }

                override fun onAnimationRepeat(animator: Animator) {
                }
            })
            layoutAlphaOut.start()
        }
    }

    private fun setupExam(delay: Long) {
        Handler().postDelayed({
            if (currentExam == null) {
                resetStartExamButtonToDefault()
                if (currentExamName != null) {
                    if (!isQuestionsManually) {
                        // Set Default Parameters
                        if (questionsCPatternText.editText!!.text.toString().isEmpty()) {
                            questionsCPatternText.editText!!.setText("1")
                        }
                        if (firstQuestionNoText.editText!!.text.toString().isEmpty()) {
                            firstQuestionNoText.editText!!.setText("1")
                        }
                        if (questionsCountText.editText!!.text.toString().isEmpty()) {
                            questionsCountText.editText!!.setText("10")
                        }
                        firstQuestion =
                            firstQuestionNoText.editText!!.text.toString().toInt()
                        if (selectedQRandomly) {
                            questionsCPatternText.editText!!.setText(getString(R.string.num, 1))
                            if (lastQuestionNoText.visibility == View.VISIBLE) {
                                if (lastQuestionNoText.editText!!.text!!.toString().isEmpty()) {
                                    setTextInputError(
                                        lastQuestionNoText,
                                        "شماره آخرین سؤال را وارد نکرده اید!"
                                    )
                                } else {
                                    setTextInputError(lastQuestionNoText, null)
                                    lastQuestion =
                                        lastQuestionNoText.editText!!.text!!.toString()
                                            .toInt()
                                }
                            }
                            questionsCPattern = 1
                        } else {
                            questionsCPattern =
                                questionsCPatternText.editText!!.text!!.toString().toInt()
                        }
                        questionsCount =
                            questionsCountText.editText!!.text!!.toString().toInt()
                        if (questionsCount >= MIN_OF_QUESTIONS_COUNT) {
                            if (questionsCount <= MAX_OF_QUESTIONS_COUNT) {
                                if (firstQuestion >= 1) {
                                    if (questionsCPattern >= 1) {
                                        setTextInputError(questionsCountText, null)
                                        setTextInputError(firstQuestionNoText, null)
                                        setTextInputError(questionsCPatternText, null)
                                        if (selectedQRandomly) {
                                            if ((lastQuestion >= questionsCount + firstQuestion) && lastQuestion > 3) {
                                                setTextInputError(lastQuestionNoText, null)
                                                questionsCPattern = 1
                                            } else {
                                                setTextInputError(
                                                    lastQuestionNoText, String.format(
                                                        Locale.getDefault(),
                                                        "شماره آخرین سؤال، باید بزرگتر از %d باشد!",
                                                        firstQuestion + questionsCount
                                                    )
                                                )
                                            }
                                            if (lastQuestionNoText.error == null) {
                                                setupExamFeatures()
                                            }
                                        } else {
                                            setupExamFeatures()
                                        }
                                    } else {
                                        setTextInputError(
                                            questionsCPatternText,
                                            "الگوی شمارش سؤالات، باید بزرگتر از ۱ باشد!"
                                        )
                                    }
                                } else {
                                    setTextInputError(
                                        firstQuestionNoText,
                                        "شماره اولین سؤال آزمون، باید بزرگتر از ۱ باشد!"
                                    )
                                }
                            } else {
                                setTextInputError(
                                    questionsCountText,
                                    "تعداد سؤال، نمی تواند بیشتر از ۱۰۰۰۰ باشد!"
                                )
                            }
                        } else {
                            setTextInputError(
                                questionsCountText,
                                "تعداد سؤال، نباید کمتر از ۵ باشد!"
                            )
                        }
                    } else {
                        setupExamFeatures()
                    }
                } else {
                    Toast.Companion.makeText(
                        this,
                        "لطفاً نام آزمون را مشخص کنید.",
                        Toast.Companion.WARNING_SIGN,
                        Toast.Companion.LENGTH_SHORT
                    ).show()
                }
            } else {
                prepareExam()
            }
        }, delay)
    }

    private fun resetStartExamButtonToDefault() {
        enterToExamRoom.isEnabled = true
        //if (preparingExamDialog != null) preparingExamDialog.dismiss(preparingExamDialog);
    }

    private fun showHideKeyboardLayout(show: Boolean, anchor: View?) {
        val keyboardManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (show) {
            keyboardManager.toggleSoftInputFromWindow(
                anchor!!.applicationWindowToken,
                InputMethodManager.SHOW_FORCED,
                0
            )
        } else {
            keyboardManager.hideSoftInputFromWindow(
                anchor!!.applicationWindowToken,
                InputMethodManager.RESULT_UNCHANGED_SHOWN
            )
        }
    }

    private fun setupExamFeatures() {
        chronoThreshold = chronoThresholdNP.value
        if (checkExamHasTime()) {
            minute = minuteNP.value.toLong()
            second = secondNP.value.toLong()
            if (checkToggleButtonChecked(selectChronometerEnabled)) {
                if (chronoThreshold != 0) {
                    prepareExamWithTiming()
                } else {
                    Toast.Companion.makeText(
                        this,
                        "لطفاً مقدار حد مجاز کرنومتر را تعیین کنید!",
                        Toast.Companion.WARNING_SIGN,
                        Toast.Companion.LENGTH_SHORT
                    ).show()
                }
            } else {
                prepareExamWithTiming()
            }
        } else {
            if (checkToggleButtonChecked(selectChronometerEnabled)) {
                if (chronoThreshold != 0) {
                    prepareExam()
                } else {
                    Toast.Companion.makeText(
                        this,
                        "لطفاً مقدار حد مجاز کرنومتر را تعیین کنید!",
                        Toast.Companion.WARNING_SIGN,
                        Toast.Companion.LENGTH_SHORT
                    ).show()
                }
            } else {
                prepareExam()
            }
        }
    }

    private fun prepareExamWithTiming() {
        if (examTime == 0L) {
            examTime = (minute * 60000L) + (second * 1000L)
        }
        prepareExam()
    }

    private fun prepareExam() {
        setupShortcutLayout()
        if (Saver.Companion.getInstance(this@MainActivity).keepScreenOn) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            setDeviceWakeLock()
        }
        showHideKeyboardLayout(false, enterToExamRoom)
        showMainViewLayout(MainView.ExamRunning)
        currentExamStatus = Exam.ExamStatus.Creating
        examNameText.text = currentExamName!!.getName()
        examNameText.visibility = View.VISIBLE
        examNameText.setSelected(true)
        examAction.setVisibility(View.VISIBLE)
        if (isQuestionsManually && !isStartedManualExam) MaterialAlert.Companion.getInstance(this@MainActivity)
            .show("سؤالات آزمون را تعیین کنید.", MaterialAlert.Companion.LENGTH_SHORT.toLong())
        else MaterialAlert.Companion.getInstance(this@MainActivity).show(
            "سؤالات آزمون را ویرایش کنید\nیا اینکه آزمون را شروع کنید.",
            MaterialAlert.Companion.LENGTH_SHORT.toLong()
        )
        answerSheetView.keepScreenOn = Saver.Companion.getInstance(this@MainActivity).keepScreenOn
        addQuestionButton.setVisibility(View.VISIBLE)
        if (questions.size >= 1) {
            removeQuestionButton.setVisibility(View.VISIBLE)
        }
        if (checkExamHasTime() && examTime != 0L) {
            showExamTime()
        }
        startCurrentExam.visibility = View.VISIBLE
        if (currentExam == null) createNewExam()
        else {
            currentExam.isLoading = (false)
            recentExams.updateCurrentExam(currentExam)
        }
        setExamWallpaper()
        setExamDraftBackground(0)
        setDynamicColor(
            examAnimView,
            examPictureView,
            examNameText,
            examTimeBar,
            startCurrentExam,
            runningExamOptions
        )
        playExamVisualEffects()
    }

    private fun setExamWallpaper() {
        val wallpapers: List<ExamWallpaper> =
            Saver.Companion.getInstance(this).loadExamWallpapers().getWallpapers()
        if (wallpapers.size != 0) {
            var selectedWallpaper: ExamWallpaper? = null
            for (wallpaper in wallpapers) {
                if (wallpaper.isSelected) {
                    selectedWallpaper = wallpaper
                    break
                }
            }
            for (i in wallpapers.indices) {
                if (wallpapers[i].isSelected) {
                    when (wallpapers[i].getType()) {
                        WallpaperType.Picture -> {
                            examAnimView.setVisibility(View.GONE)
                            examPictureView.visibility = View.VISIBLE
                            when (i) {
                                0 -> {
                                    examPictureView.setImageDrawable(getResources().getDrawable(R.drawable.wallpaper))
                                }

                                1 -> {
                                    examPictureView.setImageDrawable(getResources().getDrawable(R.drawable.wallpaper2))
                                }

                                2 -> {
                                    examPictureView.setImageDrawable(getResources().getDrawable(R.drawable.wallpaper3))
                                }

                                3 -> {
                                    examPictureView.setImageDrawable(getResources().getDrawable(R.drawable.wallpaper4))
                                }

                                4 -> {
                                    examPictureView.setImageDrawable(getResources().getDrawable(R.drawable.wallpaper5))
                                }

                                5 -> {
                                    examPictureView.setImageDrawable(getResources().getDrawable(R.drawable.wallpaper6))
                                }
                            }
                            examAnimView.invalidate()
                        }

                        WallpaperType.Animation -> {
                            examAnimView.setVisibility(View.VISIBLE)
                            examPictureView.visibility = View.GONE
                            if (i - 6 == 0) {
                                examAnimView.setAnimation(R.raw.turning_wave)
                            } else if (i - 6 == 1) {
                                examAnimView.setAnimation(R.raw.turning_wave2)
                            } else if (i - 6 == 2) {
                                examAnimView.setAnimation(R.raw.turning_wave3)
                            } else if (i - 6 == 3) {
                                examAnimView.setAnimation(R.raw.turning_wave4)
                            } else if (i - 6 == 4) {
                                examAnimView.setAnimation(R.raw.turning_wave5)
                            } else if (i - 6 == 5) {
                                examAnimView.setAnimation(R.raw.turning_wave6)
                            } else if (i - 6 == 6) {
                                examAnimView.setAnimation(R.raw.turning_wave7)
                            } else if (i - 6 == 7) {
                                examAnimView.setAnimation(R.raw.turning_wave8)
                            } else if (i - 6 == 8) {
                                examAnimView.setAnimation(R.raw.turning_wave9)
                            }
                            examAnimView.playAnimation()
                        }

                        else -> setDefaultExamWallpaper()
                    }
                }
            }
            /*if (!animShow) {
                examAnimView.setVisibility(VISIBLE);
                examPictureView.setVisibility(GONE);
                setDynamicColor(examAnimView, examPictureView, examNameText, examTimeRemBar, startCurrentExam, runningExamOptions);
            } else {
                examAnimView.setVisibility(GONE);
                examPictureView.setVisibility(VISIBLE);
                setDynamicColor(examAnimView, examPictureView, examNameText, examTimeRemBar, startCurrentExam, runningExamOptions);
            }
            animShow = !animShow;*/
        } else {
            setDefaultExamWallpaper()
        }
    }

    private fun setDefaultExamWallpaper() {
        examAnimView.setVisibility(View.GONE)
        examAnimView.invalidate()
        examPictureView.visibility = View.VISIBLE
        examPictureView.setImageResource(R.drawable.wallpaper)
    }

    private fun setExamDraftBackground(draftIndex: Int) {
        examDraftPage = examDraftPages.getChildAt(draftIndex) as CanvasView
        examDraftPage.baseColor = getResources().getColor(R.color.element_background_color)
    }

    @SuppressLint("WakelockTimeout")
    private fun setDeviceWakeLock() {
        val powerManager: PowerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock: PowerManager.WakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            ANSWER_SHEET_EXAM_WAKE_LOCK_TAG
        )
        wakeLock.acquire()
    }

    private fun setExamStartedTime() {
        val date = Date()
        val jalaliDate =
            DateConverter.gregorianToJalali((date.year + 1900), (date.month + 1), date.date)
        startedExamTime =
            printTime(date.minutes.toLong(), date.hours.toLong()) + " " + printDate(jalaliDate)
        currentExam.startExamTime = (startedExamTime)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateDataSetStatus(examStatus: Int) {
        if (questionsAdapter != null) {
            for (question in questions) {
                if (examStatus == EXAM_ENDED) question.isExamEnded = (true)
                else if (examStatus == EXAM_CORRECTION_ENDED) question.isExamCorrectionEnded =
                    (true)
            }
            questionsAdapter.notifyDataSetChanged()
        }
    }

    private fun printDate(jalaliDate: IntArray?): String {
        return if (jalaliDate!![1] >= TWO_DIGIT_NUM && jalaliDate[2] >= TWO_DIGIT_NUM) {
            String.format(
                Locale.getDefault(),
                "%d/%d/%d",
                jalaliDate[0],
                jalaliDate[1],
                jalaliDate[2]
            )
        } else if (jalaliDate[1] < TWO_DIGIT_NUM && jalaliDate[2] >= TWO_DIGIT_NUM) {
            if (Locale.getDefault() === Locale.US) String.format(
                Locale.getDefault(),
                "%d/0%d/%d",
                jalaliDate[0],
                jalaliDate[1],
                jalaliDate[2]
            )
            else String.format(
                Locale.getDefault(),
                "%d/۰%d/%d",
                jalaliDate[0],
                jalaliDate[1],
                jalaliDate[2]
            )
        } else if (jalaliDate[1] >= TWO_DIGIT_NUM) {
            if (Locale.getDefault() === Locale.US) String.format(
                Locale.getDefault(),
                "%d/%d/0%d",
                jalaliDate[0],
                jalaliDate[1],
                jalaliDate[2]
            )
            else String.format(
                Locale.getDefault(),
                "%d/%d/۰%d",
                jalaliDate[0],
                jalaliDate[1],
                jalaliDate[2]
            )
        } else {
            if (Locale.getDefault() === Locale.US) String.format(
                Locale.getDefault(),
                "%d/0%d/0%d",
                jalaliDate[0],
                jalaliDate[1],
                jalaliDate[2]
            )
            else String.format(
                Locale.getDefault(),
                "%d/۰%d/۰%d",
                jalaliDate[0],
                jalaliDate[1],
                jalaliDate[2]
            )
        }
    }

    private fun printEnglishDate(jalaliDate: IntArray): String {
        return if (jalaliDate[1] >= TWO_DIGIT_NUM && jalaliDate[2] >= TWO_DIGIT_NUM) {
            String.format(
                Locale.ENGLISH,
                "%d%d%d",
                jalaliDate[0],
                jalaliDate[1],
                jalaliDate[2]
            )
        } else if (jalaliDate[1] < TWO_DIGIT_NUM && jalaliDate[2] >= TWO_DIGIT_NUM) {
            String.format(
                Locale.ENGLISH,
                "%d0%d%d",
                jalaliDate[0],
                jalaliDate[1],
                jalaliDate[2]
            )
        } else if (jalaliDate[1] >= TWO_DIGIT_NUM) {
            String.format(
                Locale.ENGLISH,
                "%d%d0%d",
                jalaliDate[0],
                jalaliDate[1],
                jalaliDate[2]
            )
        } else {
            String.format(
                Locale.ENGLISH,
                "%d0%d0%d",
                jalaliDate[0],
                jalaliDate[1],
                jalaliDate[2]
            )
        }
    }

    private fun printTimeElements(second: Long, minute: Long): Array<String> {
        val out = if (second >= TWO_DIGIT_NUM && minute >= TWO_DIGIT_NUM) {
            String.format(Locale.getDefault(), "%d:%d", minute, second)
        } else if (second < TWO_DIGIT_NUM && minute >= TWO_DIGIT_NUM) {
            if (Locale.getDefault() === Locale.US) String.format(
                Locale.getDefault(),
                "%d:0%d",
                minute,
                second
            )
            else String.format(Locale.getDefault(), "%d:۰%d", minute, second)
        } else if (second >= TWO_DIGIT_NUM) {
            if (Locale.getDefault() === Locale.US) String.format(
                Locale.getDefault(),
                "0%d:%d",
                minute,
                second
            )
            else String.format(Locale.getDefault(), "۰%d:%d", minute, second)
        } else {
            if (Locale.getDefault() === Locale.US) String.format(
                Locale.getDefault(),
                "0%d:0%d",
                minute,
                second
            )
            else String.format(Locale.getDefault(), "۰%d:۰%d", minute, second)
        }
        return out.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    }

    private fun printTime(second: Long, minute: Long): String {
        return if (second >= TWO_DIGIT_NUM && minute >= TWO_DIGIT_NUM) {
            String.format(Locale.getDefault(), "%d:%d", minute, second)
        } else if (second < TWO_DIGIT_NUM && minute >= TWO_DIGIT_NUM) {
            if (Locale.getDefault() === Locale.US) String.format(
                Locale.getDefault(),
                "%d:0%d",
                minute,
                second
            )
            else String.format(Locale.getDefault(), "%d:۰%d", minute, second)
        } else if (second >= TWO_DIGIT_NUM) {
            if (Locale.getDefault() === Locale.US) String.format(
                Locale.getDefault(),
                "0%d:%d",
                minute,
                second
            )
            else String.format(Locale.getDefault(), "۰%d:%d", minute, second)
        } else {
            if (Locale.getDefault() === Locale.US) String.format(
                Locale.getDefault(),
                "0%d:0%d",
                minute,
                second
            )
            else String.format(Locale.getDefault(), "۰%d:۰%d", minute, second)
        }
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private fun startExam() {
        isExamStarted = true
        if (Saver.Companion.getInstance(this@MainActivity).keepScreenOn) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            setDeviceWakeLock()
        }
        if (currentExam != null) {
            currentExamStatus = Exam.ExamStatus.Started
            currentExam.examStatus = (currentExamStatus)
            if (!isRecentExamLoaded && checkExamHasTime() && examTime != 0L) {
                currentExam.examTime = (examTime)
                currentExam.examTimeLeft = (examTime)
            }
            if (startedExamTime == null) {
                setExamStartedTime()
            }
            setupExamQuestions(questionsCount, lastQuestion, firstQuestion, questionsCPattern)
            val examQuestionsCounter: IntArray = currentExam.getExamQuestionsRange()!!
            examQuestionsCounter[3] = questions.size
            currentExam.setExamQuestionsRange(examQuestionsCounter)
            updateCurrentExam()
            questionsAdapter.notifyDataSetChanged()
        } else {
            Toast.Companion.makeText(
                this,
                "آزمون نامشخص!",
                Toast.Companion.WARNING_SIGN,
                Toast.Companion.LENGTH_SHORT
            ).show()
        }
        startCurrentExam.visibility = View.GONE
        changeExamAction(R.drawable.done_select_questions)
        setControlButtonStates(CButtonState.Clicked, examAction, View.VISIBLE)
        answerSheetView.keepScreenOn = Saver.Companion.getInstance(this@MainActivity).keepScreenOn
        if (checkExamHasTime() && examTime != 0L) {
            examTimeLeft = object : CountDownTimer(examTime, INTERVAL) {
                @SuppressLint("SyntheticAccessor")
                override fun onTick(tl: Long) {
                    // TODO: ...
                    //updateCategoryTime();
                    updateExamTime(examTime)
                }

                @SuppressLint("SyntheticAccessor")
                override fun onFinish() {
                    // TODO: Set exam header collapse bar
                    if (examHeaderCollapsed) collapseExamHeader.getDrawable().setColorFilter(
                        getResources().getColor(R.color.colorAccent),
                        PorterDuff.Mode.SRC_IN
                    )
                    else collapseExamHeader.getDrawable().setColorFilter(
                        getResources().getColor(R.color.exam_body_movement_bar_color2),
                        PorterDuff.Mode.SRC_IN
                    )
                    second = 0
                    minute = 0
                    MaterialAlert.Companion.getInstance(this@MainActivity).show(
                        "زمان شما به اتمام رسید!",
                        MaterialAlert.Companion.LENGTH_SHORT.toLong()
                    )
                    endTheExam()
                }
            }
            showExamTime()
            startExamTime()
        }
        if (useChronometer) {
            timerForThinkingTime = object : CountDownTimer(1000000000, INTERVAL) {
                @SuppressLint("SyntheticAccessor")
                override fun onTick(l: Long) {
                    if (buttonDisableSeconds >= RESET_BUTTON_ENABLE_DELAY) {
                        buttonDisableSeconds = 0
                        changeResetChronoButtonState(true)
                    }
                    buttonDisableSeconds++
                    currentExam.secondsOfThinkingOnQuestion =
                        (currentExam.secondsOfThinkingOnQuestion + 1)
                    currentExam.answerSheet = (
                            Saver.Companion.getInstance(this@MainActivity).loadQuestions()
                            )
                    updateRecentExams()
                }

                @SuppressLint("SyntheticAccessor")
                override fun onFinish() {
                    timerForThinkingTime.start()
                }
            }
            timerForThinkingTime.start()
            setControlButtonStates(CButtonState.Idle, resetChronometer, View.VISIBLE)
        } else {
            setControlButtonStates(CButtonState.Disable, resetChronometer, View.VISIBLE)
        }
        setControlButtonStates(CButtonState.Clicked, addedBookmarksButton, View.VISIBLE)
        setControlButtonStates(CButtonState.Disable, removeQuestionButton, View.GONE)
        setControlButtonStates(CButtonState.Disable, addQuestionButton, View.GONE)
        //prepareExamWorkSpace();
        endTheLastExamLoading()
        updateRecentExams()
        showAnswerSheetLinear()
    }

    private fun changeExamAction(actionId: Int) {
        examAction.setImageResource(actionId)
        setControlButtonStates(CButtonState.Clicked, examAction, View.VISIBLE)
    }

    private fun updateRecentExams() {
        recentExams = Saver.Companion.getInstance(this@MainActivity).loadRecentExams()
        recentExams.updateCurrentExam(currentExam)
        Saver.Companion.getInstance(this@MainActivity).saveRecentExams(recentExams)
    }

    private fun changeResetChronoButtonState(enable: Boolean) {
        resetEnable = enable
        if (enable) {
            setControlButtonStates(CButtonState.Clicked, resetChronometer, View.VISIBLE)
            //resetChronometer.getDrawable().setColorFilter(getResources().getColor(R.color.reset_chronometer_button), SRC_IN);
        } else {
            buttonDisableSeconds = 0
            setControlButtonStates(CButtonState.Idle, resetChronometer, View.VISIBLE)
            //resetChronometer.getDrawable().setColorFilter(getResources().getColor(R.color.disable_button), SRC_IN);
        }
    }

    private fun endTheExam() {
        if (useChronometer && timerForThinkingTime != null) {
            timerForThinkingTime.onFinish()
            timerForThinkingTime.cancel()
        }
        if (collapseExamHeader.tag != null) {
            setCollapseBarAnimation(
                collapseExamHeader,
                collapseExamHeader.tag == ITEM_COLLAPSED
            )
        }

        startCurrentExam.visibility = View.GONE
        collapseExamHeader.setVisibility(View.VISIBLE)
        collapseExamTimeBar.visibility = View.GONE
        examTimeBoard.setText("--:--")
        examTimeBoard.textColor = getResources().getColor(R.color.disable_button)
        setExamTimeLayoutColor(EXAM_TIME_LAYOUT_DEFAULT_COLOR)
        examTimeBar.setProgressWithAnimation(0f)
        changeExamAction(R.drawable.reset_exam)
        setControlButtonStates(CButtonState.Idle, resetChronometer, View.GONE)
        setControlButtonStates(CButtonState.Clicked, addedBookmarksButton, View.VISIBLE)
        setControlButtonStates(CButtonState.Disable, removeQuestionButton, View.GONE)
        setControlButtonStates(CButtonState.Disable, addQuestionButton, View.GONE)
        if (currentExam.isUsedCorrection && currentExam.isCorrecting && correctionMode != Exam.CorrectionMode.None) {
            if (currentExam.hasAdditionalScore) setControlButtonStates(
                CButtonState.Clicked,
                enableNegativePoint,
                View.VISIBLE
            )
            else setControlButtonStates(CButtonState.Idle, enableNegativePoint, View.VISIBLE)
            setControlButtonStates(CButtonState.Disable, shareWorksheetButton, View.GONE)
            changeExamAction(R.drawable.correcting_exam)
            setControlButtonStates(CButtonState.Clicked, examAction, View.VISIBLE)
            currentExamStatus = Exam.ExamStatus.Correcting
            currentExam.examStatus = (currentExamStatus)
            updateRecentExams()
        } else {
            setControlButtonStates(CButtonState.Disable, enableNegativePoint, View.GONE)
            setControlButtonStates(CButtonState.Clicked, shareWorksheetButton, View.VISIBLE)
            changeExamAction(R.drawable.reset_exam)
            currentExamStatus = Exam.ExamStatus.Finished
            currentExam.examStatus = (currentExamStatus)
            updateRecentExams()
        }
        isExamNowEnded = true
        if (stopWatchEffectPlayer != null) {
            if (stopWatchEffectPlayer.isPlaying) stopWatchEffectPlayer.stop()
        }
        answerSheetView.scrollToPosition(0)
        //        categoryTitle.setVisibility(GONE);
//        categoryTimeRemainingLayout.setVisibility(GONE);
//        categoryScore.setVisibility(GONE);
//        categoryTimeOfThinkingLayout.setVisibility(GONE);
        resetChronometer.setVisibility(View.GONE)
        currentExam.examTimeLeft = (0)
        currentExam.secondsOfThinkingOnQuestion = (0)
        currentExam.isStarted = (false)
        currentExam.isLoading = (false)
        if (currentExam.isUsedCorrection) {
            currentExam.isCorrecting = (true)
        }
        updateRecentExams()
        showWhiteAnsweredQuestionsCount()
        if (currentExam.isUsedCorrection) {
            changeExamAction(R.drawable.correcting_exam)
            if (!currentExam.isUsedCorrectionByCorrectAnswers) {
                MaterialAlert.Companion.getInstance(this@MainActivity).show(
                    getString(R.string.exam_end),
                    MaterialAlert.Companion.LENGTH_SHORT.toLong()
                )
            } else {
                MaterialAlert.Companion.getInstance(this@MainActivity).show(
                    getString(R.string.exam_end_c_a),
                    MaterialAlert.Companion.LENGTH_SHORT.toLong()
                )
            }
            //examAction.setImageResource(R.drawable.correcting_exam);
            updateDataSetStatus(EXAM_ENDED)
            examAction.setOnClickListener(View.OnClickListener { v: View? ->
                if (questions.size >= 5) {
                    val ad: MaterialAlertDialog = MaterialAlertDialog(this@MainActivity)
                    ad.setCancelable(false)
                    ad.setIcon(R.drawable.correcting_exam)
                    ad.setTitle("اتمام تصحیح آزمون")
                    ad.setMessage("آیا همه سؤالات را تصحیح و بررسی کردید؟!")
                    ad.setNegativeButton("بله", View.OnClickListener { v7: View? ->
                        correctedAsNow = true
                        correctTheExam()
                        ad.dismiss(ad)
                    })
                    ad.setPositiveButton(
                        "خیر",
                        View.OnClickListener { v7: View? -> ad.dismiss(ad) })
                    if (currentExam.isUsedCorrectionByCorrectAnswers) {
                        questions = currentExam.answerSheet!!.questions!!
                        var hasNoCorrectedQuestion = false
                        for (q2 in questions) {
                            if (q2.correctAnswerChoice == 0) {
                                hasNoCorrectedQuestion = true
                                break
                            }
                        }
                        if (!hasNoCorrectedQuestion) {
                            ad.show(this@MainActivity)
                        } else {
                            Toast.Companion.makeText(
                                this,
                                "نمی توان آزمون را تصحیح کرد!\nزیرا هنوز کلید تمام سؤالات را وارد نکرده اید!",
                                Toast.Companion.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        questions = currentExam.answerSheet!!.questions!!
                        var hasNoCorrectedQuestion = false
                        for (q2 in questions) {
                            if (!q2.isCorrect && !q2.isWhite && q2.correctAnswerChoice == 0) {
                                hasNoCorrectedQuestion = true
                                break
                            }
                        }
                        if (!hasNoCorrectedQuestion) {
                            ad.show(this@MainActivity)
                        } else {
                            Toast.Companion.makeText(
                                this,
                                "نمی توان آزمون را تصحیح کرد!\nزیرا هنوز گزینه درست، برای سؤالات نادرست را وارد نکرده اید!",
                                Toast.Companion.LENGTH_LONG
                            ).show()
                        }
                    }
                } else {
                    Toast.Companion.makeText(
                        this@MainActivity,
                        "کمینه تعداد سؤالات، ۵ سؤال است!",
                        Toast.Companion.LENGTH_LONG
                    ).show()
                }
            })
        } else {
            //examAction.setVisibility(INVISIBLE);
            updateDataSetStatus(EXAM_ENDED)
            updateDataSetStatus(EXAM_CORRECTION_ENDED)
            calculateAverageOfTimeThinking()
            //timeRemaining.setOnClickListener(v1 -> resetExam());
        }
    }

    private fun calculateAverageOfTimeThinking() {
        // TODO: Setup this..
    }

    private fun showWhiteAnsweredQuestionsCount() {
        /* if (checkWhiteAnsweredQuestionsShowNeeded() && !currentExam.isCreating()) {
            int wQuestionsSize = getWhiteAnsweredQuestions().size();
            if (wQuestionsSize >= 1) {
                whiteAnsweredQuestionsCount.setText(String.valueOf(wQuestionsSize));
                whiteAnsweredQuestionsCount.setVisibility(VISIBLE);
            } else {
                whiteAnsweredQuestionsCount.setVisibility(INVISIBLE);
            }
        } else {
            whiteAnsweredQuestionsCount.setVisibility(GONE);
        }*/
        // TODO: Setup this...
    }

    /*private void updateCategoryTime() {
        if (useCategorize.isChecked()) {
            if (currentExam.answerSheet.categories.size() != 0) {
                List<Category> categories = currentExam.answerSheet.categories;
                if (runningCategory < categories.size()) {
                    if (runningCategory == -1) {
                        runningCategory = 0;
                        currentExam.setRunningCategory(runningCategory);
                        updateRecentExams();
                    }
                    if (categories.get(runningCategory).getTime() != 0) {
                        long currentTimeValue = categories.get(runningCategory).getTime();
                        updateQuestionsCategory(categories.get(runningCategory));
                        categories.set(runningCategory, categories.get(runningCategory));
                        Questions qs = Saver.getInstance(MainActivity.this).loadQuestions();
                        qs.setQuestions(questions);
                        qs.setCategories(categories);
                        currentExam.setAnswerSheet(qs);
                        updateRecentExams();
                        Saver.getInstance(MainActivity.this).saveQuestions(qs);
                        long categoryTimeLi = categories.get(runningCategory).getTime();
                        if (categoryTimeLi >= 60_000) {
                            categoryTimeRemaining.setText(printTime((int) ((((double) categoryTimeLi) % 60_000) / 1000), (int) (((double) categoryTimeLi) / 60_000)));
                        } else {
                            categoryTimeRemaining.setText(printTime((int) (((double) categoryTimeLi) / 1000), 0));
                        }
                        if ((int) (((double) categoryTimeLi) / 1000) < 11) {
                            if (categoryTimeLi == 0) {
                                categoryTimeRemainingLayout.setVisibility(GONE);
                            }
                            categoryTimeRemaining.setTextColor(getResources().getColor(R.color.time_red));
                            categoryTimeRemainingImage.getDrawable().setColorFilter(getResources().getColor(R.color.time_red), SRC_IN);
                        } else {
                            categoryTimeRemaining.setTextColor(getResources().getColor(R.color.elements_color_tint));
                            categoryTimeRemainingImage.getDrawable().setColorFilter(getResources().getColor(R.color.elements_color_tint), SRC_IN);
                        }
                        if (runningCategory >= 1) {
                            if (!reducedSecond) {
                                categories.get(runningCategory).setTime(currentTimeValue - 2000);
                                currentExam.setHasReducedSecond(true);
                                updateRecentExams();
                                reducedSecond = true;
                            } else {
                                categories.get(runningCategory).setTime(currentTimeValue - 1000);
                            }
                        } else {
                            categories.get(runningCategory).setTime(currentTimeValue - 1000);
                        }
                        qViewAdapter.notifyDataSetChanged();
                    } else {
                        if (runningCategory < currentExam.answerSheet.categories.size()) {
                            runningCategory++;
                            reducedSecond = false;
                        }
                        currentExam.setRunningCategory(runningCategory);
                        updateRecentExams();
                    }
                } else {
                    Log.e("TAG", "running category index is " + runningCategory);
                }
            }
        } else {
            currentExam.setAnswerSheet(Saver.getInstance(MainActivity.this).loadQuestions());
            updateRecentExams();
        }
    }*/
    private fun getHighCriticalTimeLeft(time: Long): Long {
        return (time * 3) / 100
    }

    private fun getCriticalTimeLeft(time: Long): Long {
        return time / 10
    }

    private fun getWarningTimeLeft(time: Long): Long {
        return (time * 4) / 10
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun startExamTime() {
        if (currentExam.isUsedCategorize && currentExam.isCanCalculateTimeForCategory && currentExam.runningCategory == -1) {
            var hasCategoryQuestions = 0
            for (question in questions) {
                if (question.category != null) {
                    hasCategoryQuestions++
                }
            }
            if (questions.size == hasCategoryQuestions) {
                var categoriesTime: Long = 0
                for (category in currentExam.answerSheet!!.categories) {
                    if (category.time != 0L) {
                        categoriesTime += category.time
                    }
                }
                if (categoriesTime == currentExam.examTime) {
                    examAction.setVisibility(View.VISIBLE)
                    currentExam.isEditingCategoryTimes = (false)
                    currentExam.lastScrollPosition = (0)
                    updateRecentExams()
                    answerSheetView.scrollToPosition(0)
                    // TODO: Setup Category Docked Header for scrolled category!
                    //categoryHeader.setVisibility(GONE);
                    examTimeLeft.start()
                    startedTimeExam = true
                } else {
                    examAction.setVisibility(View.GONE)
                    currentExam.isEditingCategoryTimes = (true)
                    updateRecentExams()
                }
                if (questionsAdapter != null) questionsAdapter.notifyDataSetChanged()
            } else {
                examAction.setVisibility(View.GONE)
                currentExam.isEditingCategoryTimes = (true)
                updateRecentExams()
                startedTimeExam = false
                if (questionsAdapter != null) questionsAdapter.notifyDataSetChanged()
                MaterialAlert.Companion.getInstance(this@MainActivity).show(
                    "ابتدا سؤالات را دسته بندی کنید!",
                    MaterialAlert.Companion.LENGTH_SHORT.toLong()
                )
                Log.w("TAG", "All Categories aren't time adjusted yet!")
            }
        } else {
            startedTimeExam = true
            examTimeLeft.start()
        }
    }

    private fun updateExamTime(time: Long) {
        examTimeLeftUntilFinished = (minute * 60000L) + (second * 1000L)
        if (examTimeLeftUntilFinished <= getCriticalTimeLeft(if ((currentExam != null)) currentExam.examTime else time)) {
            if (!criticalTimeVibrationRang) {
                if (vibrator.hasVibrator()) {
                    vibrator.vibrate(longArrayOf(0, 100, 200, 100, 200, 100), -1)
                }
                criticalTimeVibrationRang = true
            }
            setExamTimeLayoutColor(getResources().getColor(R.color.error))
            if (examTimeLeftUntilFinished <= getHighCriticalTimeLeft(if ((currentExam != null)) currentExam.examTime else time)) {
                if (Saver.Companion.getInstance(this@MainActivity)
                        .vibrationEffects
                ) vibrator.vibrate(75)
                if (stopWatchEffectPlayer != null) {
                    if (!stopWatchEffectPlayer.isPlaying) stopWatchEffectPlayer.start()
                }
                if (second > 1) {
                    // TODO: Update header collapse bar with timing bar
                    /*if (examHeaderCollapsed) {
                        showEndOfTimeAlarmCollapseBarHigh();
                    } else {
                        showEndOfTimeAlarmAnimation();
                    }*/
                }
            } else {
                // TODO: Update header collapse bar with timing bar
                /*if (examHeaderCollapsed) {
                    showEndOfTimeAlarmCollapseBar();
                }*/
            }
        } else {
            if (examTimeLeftUntilFinished <= getWarningTimeLeft(if ((currentExam != null)) currentExam.examTime else time)) {
                setExamTimeLayoutColor(getResources().getColor(R.color.edu_level_middle))
                // TODO: Update header collapse bar with timing bar
                /*if (examHeaderCollapsed) {
                    collapseExamHeader.getDrawable().setColorFilter(getResources().getColor(R.color.edu_level_middle), SRC_IN);
                } else {
                    collapseExamHeader.getDrawable().setColorFilter(getResources().getColor(R.color.exam_body_movement_bar_color2), SRC_IN);
                }*/
                if (!warningTimeVibrationRang) {
                    if (vibrator.hasVibrator()) {
                        vibrator.vibrate(longArrayOf(0, 100, 200, 100), -1)
                    }
                    warningTimeVibrationRang = true
                }
            } else {
                setExamTimeLayoutColor(EXAM_TIME_LAYOUT_DEFAULT_COLOR)
            }
        }
        if (second <= 0) {
            showExamTime()
            if (minute >= 1) {
                minute--
                second = 60
            }
            //            else {
//                currentExam.examTimeLeft =(0);
//                updateRecentExams();
//                examTimeLeft.cancel();
//                examTimeLeft.onFinish();
//            }
        }
        second--
        showExamTime()
        Log.d("TAG", "Time millis $examTimeLeftUntilFinished")
        currentExam.examTimeLeft = (examTimeLeftUntilFinished)
        if (useChronometer) {
            if (buttonDisableSeconds >= RESET_BUTTON_ENABLE_DELAY) {
                buttonDisableSeconds = 0
                changeResetChronoButtonState(true)
            }
            buttonDisableSeconds++
            currentExam.secondsOfThinkingOnQuestion = (currentExam.secondsOfThinkingOnQuestion + 1)
        }
        updateRecentExams()
    }

    private fun showExamTime() {
        collapseExamTimeBar.setProgress(calculateRemOfTimePercent().toInt())
        examTimeBar.setProgressWithAnimation(calculateRemOfTimePercent())
        examTimeBoard.animationDuration = 150
        examTimeBoard.charStrategy = SameDirectionAnimation(Direction.SCROLL_UP)
        examTimeBoard.addCharOrder(CharOrder.Number)
        examTimeBoard.typeface = ResourcesCompat.getFont(this, R.font.estedad_light)
        examTimeBoard.animationInterpolator = FastOutSlowInInterpolator()
        examTimeBoard.setText(printTime(second, minute), true)
    }

    private fun setExamTimeLayoutColor(color: Int) {
        if (color != EXAM_TIME_LAYOUT_DEFAULT_COLOR) {
            examTimeBar.progressBarColor = color
            collapseExamTimeBar.setProgressColor(color)
            examTimeBoard.textColor = color
        } else {
            examTimeBar.progressBarColor = defaultPaletteColor
            collapseExamTimeBar.setProgressColor(defaultPaletteColor)
            examTimeBoard.textColor = getResources().getColor(R.color.elements_color_tint)
        }
    }

    private fun calculateRemOfTimePercent(): Float {
        return if (currentExam != null) {
            currentExam.examTimeLeft.toFloat() / currentExam.examTime.toFloat() * 100f
        } else 100f
    }

    private fun endTheLastExamLoading() {
        if (!currentExam.isStarted && !currentExam.isCreating) {
            if (currentExam.isUsedCorrection) {
                if (currentExam.isChecked) correctTheExam()
                else endTheExam()
            } else {
                endTheExam()
            }
        }
    }

    private fun correctTheExam() {
        currentExamStatus = Exam.ExamStatus.Checked
        currentExam.examStatus = (currentExamStatus)
        updateRecentExams()
        enableNegativePoint.setVisibility(View.GONE)
        shareWorksheetButton.setVisibility(View.VISIBLE)
        // TODO: Setup this...
    }

    private fun checkExamHasTime(): Boolean {
        return if (currentExam != null) !currentExam.isStarted || currentExam.isCanCalculateTimeForCategory || minuteNP.value >= 5 || (currentExam.examTime >= 300000 && currentExam.examTimeLeft > 0) || examTime > 0
        else minuteNP.value >= 5
    }

    private fun setTextInputError(textInput: TextInputLayout, error: String?) {
        if (error == null) textInput.boxStrokeColor =
            getResources().getColor(R.color.text_input_bg_color)
        else textInput.boxStrokeColor = getResources().getColor(R.color.error)
        textInput.error = error
    }

    private fun setNavigationBarMargin() {
        val params: FrameLayout.LayoutParams =
            parentLayout.layoutParams as FrameLayout.LayoutParams
        params.bottomMargin = getNavigationBarHeight(this)
        parentLayout.setLayoutParams(params)
        Log.i("TAG", "Device nav bar height: " + getNavigationBarHeight(this))
    }

    @SuppressLint("DiscouragedApi")
    fun getNavigationBarHeight(c: Context): Int {
        val result = 0
        val hasMenuKey: Boolean = ViewConfiguration.get(c).hasPermanentMenuKey()
        val hasBackKey: Boolean = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK)
        if (!hasMenuKey && !hasBackKey) {
            val resources = c.resources
            val orientation = resources.configuration.orientation
            val resourceId =
                if ((c.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE) {
                    resources.getIdentifier(
                        if (orientation == Configuration.ORIENTATION_PORTRAIT) "navigation_bar_height" else "navigation_bar_height_landscape",
                        "dimen",
                        "android"
                    )
                } else {
                    resources.getIdentifier(
                        if (orientation == Configuration.ORIENTATION_PORTRAIT) "navigation_bar_height" else "navigation_bar_width",
                        "dimen",
                        "android"
                    )
                }
            if (resourceId > 0) {
                return resources.getDimensionPixelSize(resourceId)
            }
        }
        return result
    }

    private fun playExamVisualEffects() {
        playExamBackgroundAnim()
        BlurViewHolder.setBlurView(this, examControlPanel)
        BlurViewHolder.setBlurView(this, examAnswerSheetEmptyError)
    }

    private fun playExamBackgroundAnim() {
        if (checkDeviceSupport()) {
            if (examAnimView.visibility == View.VISIBLE) examAnimView.playAnimation()
        }
    }

    private fun setDynamicColor(
        animView: LottieAnimationView,
        pictureView: ImageView,
        examNameText: TextView,
        examTimeRemBar: CircularProgressBar,
        startExam: Button,
        l: LinearLayout?
    ) {
        try {
            Palette.Builder(getBitmapFromView((if ((animView.getVisibility() == View.VISIBLE)) animView else pictureView)!!))
                .generate(
                    Palette.PaletteAsyncListener { palette: Palette? ->
                        val defLightColor: Int = getResources().getColor(R.color.colorLightAccent2)
                        val defColor: Int = getResources().getColor(R.color.colorAccent)
                        if (palette != null) {
                            Log.e(
                                "TAG",
                                "Dominant Color Palette: " + printColor(
                                    palette.getDominantColor(defColor)
                                )
                            )
                            Log.i(
                                "TAG",
                                "Muted Color Palette: " + printColor(palette.getMutedColor(defColor))
                            )
                            Log.i(
                                "TAG",
                                "Vibrant Color Palette: " + printColor(
                                    palette.getVibrantColor(defColor)
                                )
                            )
                            Log.d(
                                "TAG",
                                "Light Muted Color Palette: " + printColor(
                                    palette.getLightMutedColor(defColor)
                                )
                            )
                            Log.d(
                                "TAG",
                                "Light Vibrant Color Palette: " + printColor(
                                    palette.getLightVibrantColor(defColor)
                                )
                            )
                            Log.w(
                                "TAG",
                                "Dark Muted Color Palette: " + printColor(
                                    palette.getDarkMutedColor(defColor)
                                )
                            )
                            Log.w(
                                "TAG",
                                "Dark Vibrant Color Palette: " + printColor(
                                    palette.getDarkVibrantColor(defColor)
                                )
                            )
                            defaultPaletteColor = setColorPalette(palette, defColor)
                            val dominateColor: Int = palette.getDominantColor(defColor)
                            if (checkDarkModeEnabled(this)) {
                                setStatusBarTheme(
                                    this,
                                    checkColorBrightness(dominateColor, ColorBrightness.Darken)
                                )
                            } else {
                                setStatusBarTheme(
                                    this,
                                    checkColorBrightness(dominateColor, ColorBrightness.Lighten)
                                )
                            }
                            examNameText!!.setTextColor(defaultPaletteColor)
                            examTimeRemBar!!.progressBarColor = defaultPaletteColor
                            startExam!!.backgroundTintList =
                                ColorStateList.valueOf(defaultPaletteColor)
                            setMaterialButtonTheme(
                                startExam,
                                checkColorBrightness(defaultPaletteColor)
                            )
                            setButtonsPaletteColor()
                            prepareAnswerSheet()
                        }
                    })
        } catch (e: Exception) {
            e.printStackTrace()
            defaultPaletteColor = getResources().getColor(R.color.colorAccent)
            prepareAnswerSheet()
        }
    }

    private fun setMaterialButtonTheme(b: Button?, light: Boolean) {
        if (light) {
            b!!.setTextColor(Color.parseColor("#323232"))
        } else {
            b!!.setTextColor(Color.parseColor("#f5f5f5"))
        }
    }

    private fun checkColorBrightness(
        color: Int,
        brightness: ColorBrightness = ColorBrightness.Moderate
    ): Boolean {
        val colorDarkness =
            1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(
                color
            )) / 255
        val colorDarknessRidge = when (brightness) {
            ColorBrightness.Darken -> 0.25
            ColorBrightness.Moderate -> 0.5
            ColorBrightness.Lighten -> 0.75
            else -> 0.0
        }
        return colorDarkness < colorDarknessRidge
    }

    private fun setButtonsPaletteColor() {
        setControlButtonStates(CButtonState.Disable, examAction, View.VISIBLE)
        setControlButtonStates(CButtonState.Clicked, openDraftBox, View.VISIBLE)
        setControlButtonStates(CButtonState.Clicked, jumpToQuestion, View.VISIBLE)
        if (examFile != null) setControlButtonStates(
            CButtonState.Clicked,
            openExamFileBox,
            View.VISIBLE
        )
        else setControlButtonStates(CButtonState.Disable, openExamFileBox, View.VISIBLE)
        setControlButtonStates(CButtonState.Clicked, addQuestionButton, View.VISIBLE)
        setControlButtonStates(CButtonState.Clicked, removeQuestionButton, View.VISIBLE)
    }

    private fun setColorPalette(palette: Palette, defColor: Int): Int {
        val vibColor: Int = palette.getVibrantColor(defColor)
        val lightVibColor: Int = palette.getLightVibrantColor(defColor)
        val darkVibColor: Int = palette.getDarkVibrantColor(defColor)
        return if (checkDarkModeEnabled(this)) {
            if (lightVibColor != defColor && checkColorBrightness(
                    lightVibColor,
                    ColorBrightness.Lighten
                )
            ) {
                lightVibColor
            } else {
                if (checkColorBrightness(vibColor)) {
                    vibColor
                } else {
                    defColor
                }
            }
        } else {
            if (darkVibColor != defColor && checkColorBrightness(
                    darkVibColor,
                    ColorBrightness.Darken
                )
            ) {
                darkVibColor
            } else {
                if (checkColorBrightness(vibColor)) {
                    vibColor
                } else {
                    darkVibColor
                }
            }
        }
    }

    private fun printColor(color: Int): String {
        return String.format("#%06X", (0xFFFFFF and color))
    }

    private fun getBitmapFromView(v: View): Bitmap {
        val specWidth = View.MeasureSpec.makeMeasureSpec(0,  /* any */View.MeasureSpec.UNSPECIFIED)
        v.measure(specWidth, specWidth)
        val questionWidth = v.measuredWidth
        val b = Bitmap.createBitmap(questionWidth, questionWidth, Bitmap.Config.ARGB_8888)
        val c = Canvas(b)
        v.layout(0, 0, v.measuredWidth, v.measuredHeight)
        v.draw(c)
        return b
    }

    private fun setTexts() {
//        String[] minutes = new String[236];
//        minutes[0] = "0";
//        for (int i = 1; i < minutes.length; i++) {
//            minutes[i] = String.valueOf(i + 4);
//        }
//        minuteNP.setDisplayedValues(minutes);
        setTILs()
        setNPParameters(minuteNP)
        setNPParameters(secondNP)
        setNPParameters(chronoThresholdNP)
    }

    private fun setTILs() {
        questionsCountText.editText!!
            .setOnEditorActionListener()
            { v: TextView?, actionId: Int, event: KeyEvent? ->
                firstQuestionNoText.editText!!.requestFocus()
                true
            }
        firstQuestionNoText.editText!!
            .setOnEditorActionListener() { v: TextView?, actionId: Int, event: KeyEvent? ->
                if (selectedQRandomly) lastQuestionNoText.editText!!.requestFocus()
                else questionsCPatternText.editText!!.requestFocus()
                true
            }
        questionsCPatternText.editText!!
            .setOnEditorActionListener { v: TextView, actionId: Int, event: KeyEvent? ->
                showHideKeyboardLayout(false, v)
                v.clearFocus()
                true
            }
        lastQuestionNoText.editText!!
            .setOnEditorActionListener { v: TextView, actionId: Int, event: KeyEvent? ->
                showHideKeyboardLayout(false, v)
                v.clearFocus()
                true
            }
    }

    private fun setNPParameters(np: NumberPicker) {
        np.selectedTextColor = getResources().getColor(R.color.disable_button)
        np.setOnValueChangedListener { picker: NumberPicker, oldVal: Int, newVal: Int ->
            if (picker.id == R.id.second_number_picker && newVal != 0 && minuteNP.value < 5) {
                minuteNP.value = 5
            }
            setEnabledRowButton(useCategoryTimingEnabled, checkExamHasTime(), useCategoryTiming)
            setNPTextColor(np, newVal)
            vibrator.vibrate(NP_VALUE_CHANGE_VIBRATION.toLong())
        }
        np.setTypeface(ResourcesCompat.getFont(this, R.font.estedad_light))
        np.setSelectedTypeface(ResourcesCompat.getFont(this, R.font.estedad_light))
    }

    private fun setNPTextColor(np: NumberPicker, value: Int) {
        val color: Int = if (value != 0) {
            getResources().getColor(R.color.colorAccent)
        } else {
            getResources().getColor(R.color.disable_button)
        }
        np.selectedTextColor = color
        if (checkExamHasTime()) {
            minuteNP.selectedTextColor = getResources().getColor(R.color.colorAccent)
            secondNP.selectedTextColor = getResources().getColor(R.color.colorAccent)
            setupExamTimeColon.setTextColor(getResources().getColor(R.color.colorAccent))
            setupExamTimeColon.setAlpha(1f)
        } else {
            minuteNP.selectedTextColor = getResources().getColor(R.color.disable_button)
            secondNP.selectedTextColor = getResources().getColor(R.color.disable_button)
            setupExamTimeColon.setTextColor(getResources().getColor(R.color.disable_button))
            setupExamTimeColon.setAlpha(0.7f)
        }
        refreshNPs()
    }

    private fun refreshNPs() {
        minuteNP.scrollTo(0, 10)
        minuteNP.scrollTo(0, 0)
        secondNP.scrollTo(0, 10)
        secondNP.scrollTo(0, 0)
    }

    @SuppressLint("UseCompatLoadingForDrawables", "ClickableViewAccessibility")
    private fun onClicks() {
        setToggleButtonsClick(selectQuestionsMode)
        setToggleButtonsClick(selectCorrectionMode)
        setNormalButtonsClicks()
        setDividerButtonsClicks()
        setRowToggleButtonsClicks()
        setExamsListClicks()
    }

    private fun setDividerButtonsClicks() {
        collapseExamTimeBar.setOnClickListener(View.OnClickListener { v: View ->
            this.setControlPanelCollapseBar(
                v
            )
        })
        collapseExamHeader.setOnClickListener(View.OnClickListener { v: View ->
            this.setControlPanelCollapseBar(
                v
            )
        })
        collapseDraftView.setOnClickListener(View.OnClickListener { v: View ->
            setDefaultCollapseBar(
                v,
                examDraftLayout,
                CollapseBarMode.Fullscreen,
                null
            )
        })
    }

    private fun setDefaultCollapseBar(
        v: View,
        parent: ViewGroup,
        mode: CollapseBarMode,
        collapseBarChangedListener: CollapseBarChangedListener?
    ) {
        val root: View = LayoutInflater.from(this@MainActivity)
            .inflate(R.layout.popup_collapse_options, parent, false)
        val collapseViewCard: BlurView = root.findViewById<BlurView>(R.id.collapse_options_card)
        val closeWindow: ImageButton = root.findViewById<ImageButton>(R.id.close_window)
        val collapseWindow: ImageButton = root.findViewById<ImageButton>(R.id.collapse_window)
        val fullscreenWindow: ImageButton = root.findViewById<ImageButton>(R.id.fullscreen_window)
        val window: PopupWindow = PopupWindow(
            root,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            true
        )
        val tag = (if ((v.tag != null)) v.tag else "") as String
        closeWindow.setOnClickListener(View.OnClickListener { v2: View? ->
            window.dismiss()
            if (parent.getVisibility() == View.VISIBLE) parent.setVisibility(View.GONE)
            if (collapseBarChangedListener != null) collapseBarChangedListener.onClosed(v, parent)
        })
        setImageButtonEnableStatus(
            closeWindow,
            !(mode != CollapseBarMode.None && tag == ITEM_FULLSCREEN),
            true
        )
        if (mode != CollapseBarMode.None) {
            if (tag == ITEM_FULLSCREEN) {
                setImageButtonEnableStatus(fullscreenWindow, false, false)
                setImageButtonEnableStatus(collapseWindow, true, false)
                collapseWindow.setOnClickListener(View.OnClickListener { v2: View? ->
                    window.dismiss()
                    v.tag = null
                    restoreControlPanel()
                    answerSheetLayout.visibility = View.VISIBLE
                    if (collapseBarChangedListener != null) collapseBarChangedListener.onRestored(
                        v,
                        parent
                    )
                })
            } else if (tag == ITEM_COLLAPSED) {
                setImageButtonEnableStatus(fullscreenWindow, true, false)
                setImageButtonEnableStatus(collapseWindow, false, false)
                fullscreenWindow.setOnClickListener(View.OnClickListener { v2: View? ->
                    window.dismiss()
                    v.tag = null
                    if (collapseBarChangedListener != null) collapseBarChangedListener.onRestored(
                        v,
                        parent
                    )
                })
            } else {
                setImageButtonEnableStatus(
                    fullscreenWindow,
                    mode == CollapseBarMode.Both || mode == CollapseBarMode.Fullscreen,
                    false
                )
                setImageButtonEnableStatus(
                    collapseWindow,
                    mode == CollapseBarMode.Both || mode == CollapseBarMode.Collapse,
                    false
                )
                fullscreenWindow.setOnClickListener(View.OnClickListener { v2: View? ->
                    window.dismiss()
                    v.tag = ITEM_FULLSCREEN
                    collapseControlPanel(collapseExamHeader)
                    answerSheetLayout.visibility = View.GONE
                    if (collapseBarChangedListener != null) collapseBarChangedListener.onFullscreen(
                        v,
                        parent
                    )
                })
                collapseWindow.setOnClickListener(View.OnClickListener { v2: View? ->
                    window.dismiss()
                    v.tag = ITEM_COLLAPSED
                    setCollapseBarAnimation(v as ImageButton, true)
                    if (collapseBarChangedListener != null) collapseBarChangedListener.onCollapsed(
                        v,
                        parent
                    )
                })
            }
        } else {
            setImageButtonEnableStatus(fullscreenWindow, false, false)
            setImageButtonEnableStatus(collapseWindow, false, false)
        }
        window.setElevation(30f)
        BlurViewHolder.setBlurView(this, collapseViewCard)
        window.showAsDropDown(v, -125, -200, Gravity.CENTER)
    }

    private fun setControlPanelCollapseBar(v: View) {
        val root: View = LayoutInflater.from(this@MainActivity)
            .inflate(R.layout.popup_collapse_options, examControlPanel, false)
        val collapseViewCard: BlurView = root.findViewById<BlurView>(R.id.collapse_options_card)
        val closeWindow: ImageButton = root.findViewById<ImageButton>(R.id.close_window)
        val collapseWindow: ImageButton = root.findViewById<ImageButton>(R.id.collapse_window)
        val fullscreenWindow: ImageButton = root.findViewById<ImageButton>(R.id.fullscreen_window)
        val window: PopupWindow = PopupWindow(
            root,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            true
        )
        setImageButtonEnableStatus(closeWindow, false, true)
        closeWindow.setOnClickListener(View.OnClickListener { v2: View? ->
            Toast.Companion.makeText(
                this,
                "کنترل پنل را نمی توان بست!",
                Toast.Companion.WARNING_SIGN,
                Toast.Companion.LENGTH_SHORT
            ).show()
        })
        if ((if ((collapseExamHeader.tag != null)) collapseExamHeader.tag else "") != ITEM_COLLAPSED) {
            setImageButtonEnableStatus(fullscreenWindow, false, false)
            setImageButtonEnableStatus(collapseWindow, true, false)
            fullscreenWindow.setOnClickListener(View.OnClickListener { v2: View? ->
                Toast.Companion.makeText(
                    this,
                    "کنترل پنل را نمی توان بزرگ کرد!",
                    Toast.Companion.WARNING_SIGN,
                    Toast.Companion.LENGTH_SHORT
                ).show()
            })
            collapseWindow.setOnClickListener(View.OnClickListener { v2: View? ->
                window.dismiss()
                collapseControlPanel(v)
            })
        } else {
            setImageButtonEnableStatus(fullscreenWindow, true, false)
            setImageButtonEnableStatus(collapseWindow, false, false)
            collapseWindow.setOnClickListener(View.OnClickListener { v2: View? ->
                Toast.Companion.makeText(
                    this,
                    "کنترل پنل کوچک شده است.",
                    Toast.Companion.WARNING_SIGN,
                    Toast.Companion.LENGTH_SHORT
                ).show()
            })
            fullscreenWindow.setOnClickListener(View.OnClickListener { v2: View? ->
                window.dismiss()
                restoreControlPanel()
            })
        }
        window.setElevation(30f)
        BlurViewHolder.setBlurView(this, collapseViewCard)
        window.showAsDropDown(v, -50, 0, Gravity.CENTER)
    }

    private fun collapseControlPanel(v: View?) {
        collapseExamHeader.tag = ITEM_COLLAPSED
        examNameText.visibility = View.GONE
        examTimeBar.visibility = View.GONE
        examTimeBoard.visibility = View.GONE
        runningExamOptions.visibility = View.GONE
        startCurrentExam.visibility = View.GONE
        if (currentExam != null) {
            if (currentExam.isUsedTiming && checkExamHasTime() && examTime != 0L) {
                collapseExamHeader.setVisibility(View.GONE)
                collapseExamTimeBar.visibility = View.VISIBLE
                collapseExamTimeBar.setProgress(calculateRemOfTimePercent().toInt())
            } else {
                setCollapseBarAnimation(collapseExamHeader, true)
                collapseExamHeader.setVisibility(View.VISIBLE)
                collapseExamTimeBar.visibility = View.GONE
            }
        } else {
            setCollapseBarAnimation(collapseExamHeader, true)
            collapseExamHeader.setVisibility(View.VISIBLE)
            collapseExamTimeBar.visibility = View.GONE
        }
    }

    private fun restoreControlPanel() {
        if (collapseExamHeader.tag != null) {
            collapseExamHeader.tag = null
            examNameText.visibility = View.VISIBLE
            examTimeBar.visibility = View.VISIBLE
            examTimeBoard.visibility = View.VISIBLE
            runningExamOptions.visibility = View.VISIBLE
            if (isExamStarted) startCurrentExam.visibility = View.GONE
            else startCurrentExam.visibility = View.VISIBLE
            if (currentExam != null) {
                if (!currentExam.isUsedTiming || !checkExamHasTime() || examTime == 0L) {
                    setCollapseBarAnimation(collapseExamHeader, false)
                }
            } else {
                setCollapseBarAnimation(collapseExamHeader, false)
            }
            collapseExamTimeBar.visibility = View.GONE
            collapseExamHeader.setVisibility(View.VISIBLE)
        }
    }

    private fun setImageButtonEnableStatus(
        button: ImageButton,
        enabled: Boolean,
        reverseColor: Boolean
    ) {
        button.setEnabled(enabled)
        val colorId: Int =
            if ((reverseColor)) R.color.elements_color_tint_rev else R.color.elements_color_tint
        if (enabled) {
            button.getDrawable()
                .setColorFilter(getResources().getColor(colorId), PorterDuff.Mode.SRC_IN)
            button.setAlpha(1f)
        } else {
            button.getDrawable().setColorFilter(
                getResources().getColor(R.color.disable_button_fade),
                PorterDuff.Mode.SRC_IN
            )
            button.setAlpha(0.88f)
        }
    }

    private fun setCollapseBarAnimation(collapseBar: ImageButton?, collapsed: Boolean) {
        val lp: ConstraintLayout.LayoutParams =
            collapseBar!!.getLayoutParams() as ConstraintLayout.LayoutParams
        val v: ValueAnimator
        if (collapsed) {
            v = ValueAnimator.ofInt(lp.width, lp.width + 50)
            v.setDuration(250)
            v.setStartDelay(50)
            v.addUpdateListener { animation: ValueAnimator ->
                lp.width = animation.getAnimatedValue() as Int
                collapseBar.setLayoutParams(lp)
                collapseBar.setAlpha(1f)
                collapseBar.getDrawable()
                    .setColorFilter(defaultPaletteColor, PorterDuff.Mode.SRC_IN)
            }
        } else {
            v = ValueAnimator.ofInt(lp.width, lp.width - 50)
            v.setDuration(250)
            v.setStartDelay(50)
            v.addUpdateListener { animation: ValueAnimator ->
                lp.width = animation.getAnimatedValue() as Int
                collapseBar.setLayoutParams(lp)
                collapseBar.setAlpha(0.9f)
                collapseBar.getDrawable().setColorFilter(
                    getResources().getColor(R.color.disable_button_fade),
                    PorterDuff.Mode.SRC_IN
                )
            }
        }
        v.start()
    }

    private fun checkToggleButtonChecked(toggleButton: LinearLayout): Boolean {
        return toggleButton.backgroundTintList == ColorStateList.valueOf(
            getResources().getColor(
                R.color.colorAccent
            )
        )
    }

    private fun vibrateDevice(pattern: LongArray) {
        if (Saver.Companion.getInstance(this).vibrationEffects) {
            if (vibrator.hasVibrator()) {
                vibrator.vibrate(pattern, -1)
            }
        }
    }

    private fun vibrateDevice(millis: Long) {
        if (Saver.Companion.getInstance(this).vibrationEffects) {
            if (vibrator.hasVibrator()) {
                vibrator.vibrate(millis)
            }
        }
    }

    private fun generateExamId(): Int {
        val id = random.nextInt(EXAM_ID_RANGE_MAX)
        if (id >= EXAM_ID_RANGE_MIN) {
            var isConflictID = false
            for (e in recentExams.getExamList()) {
                if (e.id == id) {
                    isConflictID = true
                    break
                }
            }
            return if (!isConflictID) id
            else generateExamId()
        } else {
            return generateExamId()
        }
    }

    private fun setupExamQuestions(
        questionsCounter: Int,
        questionTo: Int,
        questionFrom: Int,
        questionCountPattern: Int
    ) {
        currentExam.setExamQuestionsRange(
            intArrayOf(
                questionFrom,
                if (checkToggleButtonChecked(selectQuestionsRandomly)) questionTo else 0,
                questionsCounter,
                questionCountPattern
            )
        )
        if (questions != null) {
            currentExam.answerSheet = (Questions(questions))
            recentExams.updateCurrentExam(currentExam)
            Saver.Companion.getInstance(this@MainActivity).saveRecentExams(recentExams)
        }
    }

    private fun setupExamTime(time: Long) {
        if (checkExamHasTime() && time != 0L) {
            currentExam.examTime = (time)
            currentExam.examTimeLeft = (time)
        }
    }

    private fun setupExamOptions() {
        currentExam.isUsedChronometer = (useChronometer)
        currentExam.chornoThreshold = (chronoThreshold)
        currentExam.isUsedRandomQuestions = (selectedQRandomly)
        currentExam.isUsedCategorize = (useExamCategorize)
        currentExam.isUsedCorrection = (correctionMode != Exam.CorrectionMode.None)
        currentExam.isCanCalculateTimeForCategory = (useCategoryTiming)
        currentExam.isUsedCorrectionByCorrectAnswers = (correctionMode == Exam.CorrectionMode.Keys)
        currentExam.isUsedTiming = (checkExamHasTime())
        currentExam.isCanCalculateScoreOfCategory = (useCategoryScore)
        currentExam.startExamTime = (startedExamTime)
        currentExam.isSelectQuestionsManually = (isQuestionsManually)
    }

    private fun setupExamFile() {
        if (examFile != null) {
            currentExam.examFile = (examFile)
        }
    }

    private fun setupExamStatus(status: Exam.ExamStatus?) {
        when (status) {
            Exam.ExamStatus.Creating -> {
                currentExam.isCreating = (true)
                currentExam.isStarted = (false)
                currentExam.isChecked = (false)
                currentExam.isCorrecting = (false)
                currentExam.isSuspended = (false)
            }

            Exam.ExamStatus.Started -> {
                currentExam.isCreating = (false)
                currentExam.isStarted = (true)
                currentExam.isChecked = (false)
                currentExam.isCorrecting = (false)
                currentExam.isSuspended = (false)
            }

            Exam.ExamStatus.Suspended -> {
                currentExam.isCreating = (false)
                currentExam.isStarted = (false)
                currentExam.isChecked = (false)
                currentExam.isCorrecting = (false)
                currentExam.isSuspended = (true)
            }

            Exam.ExamStatus.Finished -> {
                currentExam.isCreating = (false)
                currentExam.isStarted = (false)
                currentExam.isChecked = (false)
                currentExam.isCorrecting = (false)
                currentExam.isSuspended = (false)
            }

            Exam.ExamStatus.Correcting -> {
                currentExam.isCreating = (false)
                currentExam.isStarted = (false)
                currentExam.isChecked = (false)
                currentExam.isCorrecting = (true)
                currentExam.isSuspended = (false)
            }

            Exam.ExamStatus.Checked -> {
                currentExam.isCreating = (false)
                currentExam.isStarted = (false)
                currentExam.isChecked = (true)
                currentExam.isCorrecting = (false)
                currentExam.isSuspended = (false)
            }

            else -> {}
        }
        if (currentExamStatus != null) currentExam.examStatus = (
                currentExamStatus
                )
    }

    private fun createNewExam() {
        currentExam = Exam()
        currentExam.id = (generateExamId())
        currentExam.setExamName(currentExamName)
        setupExamTime(examTime)
        setupExamFile()
        setupExamOptions()
        setupExamStatus(currentExamStatus)
        //suspendAnotherLiveExams();
        recentExams.addExam(currentExam)
        Saver.Companion.getInstance(this@MainActivity).saveRecentExams(recentExams)
    }

    /**
     * This update operation has not been consistent of questions!
     */
    private fun updateCurrentExam() {
        setupExamTime(examTime)
        setupExamFile()
        setupExamOptions()
        setupExamStatus(currentExamStatus)
    }

    @SuppressLint("NotifyDataSetChanged", "ClickableViewAccessibility")
    private fun setNormalButtonsClicks() {
        setExamDraftTouchEvents()
        addDraftPage.setOnClickListener(View.OnClickListener { v: View? ->
            currentDraftViewIndex++
            examDraftPages.addView(
                CanvasView(this),
                currentDraftViewIndex,
                FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    Gravity.CENTER
                )
            )
            (examDraftPages.getChildAt(currentDraftViewIndex) as CanvasView).baseColor =
                getResources().getColor(R.color.element_background_color)
            setupDraftPenStrokeColor()
            clearDraftCanvas()
            setExamDraftTouchEvents()
            updateDraftPagesCount()
        })
        draftPagesOptions.setOnClickListener(View.OnClickListener { v2: View? ->
            if (examDraftPages.childCount >= 2) {
                val v: View = LayoutInflater.from(this)
                    .inflate(R.layout.popup_draft_page_options, null, false)
                val window: PopupWindow = PopupWindow(
                    v,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    true
                )
                val backwardPage: ImageButton = v.findViewById<ImageButton>(R.id.goto_previous_page)
                val forwardPage: ImageButton = v.findViewById<ImageButton>(R.id.goto_next_page)
                val currentPageNumber: TextView = v.findViewById<TextView>(R.id.current_page_number)
                val deletePage: ImageButton = v.findViewById<ImageButton>(R.id.delete_current_page)
                currentPageNumber.text = getString(
                    R.string.page_counter_text,
                    currentDraftViewIndex + 1,
                    examDraftPages.childCount
                )
                setImageButtonEnableStatus(backwardPage, currentDraftViewIndex > 0, false)
                setImageButtonEnableStatus(
                    forwardPage,
                    currentDraftViewIndex < examDraftPages.childCount - 1,
                    false
                )
                setImageButtonEnableStatus(deletePage, currentDraftViewIndex != 0, false)
                if (deletePage.isEnabled) deletePage.getDrawable().setColorFilter(
                    getResources().getColor(R.color.edu_level_bad),
                    PorterDuff.Mode.SRC_IN
                )
                backwardPage.setOnClickListener(View.OnClickListener { v3: View? ->
                    window.dismiss()
                    if (currentDraftViewIndex > 0) {
                        currentDraftViewIndex--
                        updateDraftPage(currentDraftViewIndex)
                        currentPageNumber.text = getString(
                            R.string.page_counter_text,
                            currentDraftViewIndex + 1,
                            examDraftPages.childCount
                        )
                    }
                })
                forwardPage.setOnClickListener(View.OnClickListener { v3: View? ->
                    window.dismiss()
                    if (currentDraftViewIndex < examDraftPages.childCount - 1) {
                        currentDraftViewIndex++
                        updateDraftPage(currentDraftViewIndex)
                        currentPageNumber.text = getString(
                            R.string.page_counter_text,
                            currentDraftViewIndex + 1,
                            examDraftPages.childCount
                        )
                    }
                })
                deletePage.setOnClickListener(View.OnClickListener { v3: View? ->
                    window.dismiss()
                    if (currentDraftViewIndex > 0) {
                        val dialog: MaterialAlertDialog = MaterialAlertDialog(this)
                        dialog.setIcon(R.drawable.delete)
                        dialog.setTitle("حذف چرک نویس")
                        dialog.setMessage(
                            """آیا از حذف صفحه ${currentDraftViewIndex + 1} چرک نویس اطمینان دارید؟
همه داده های داخل آن حذف خواهد شد!"""
                        )
                        dialog.setPositiveButton("بله", View.OnClickListener { v4: View? ->
                            dialog.dismiss(dialog)
                            examDraftPages.removeViewAt(currentDraftViewIndex)
                            currentDraftViewIndex--
                            updateDraftPage(currentDraftViewIndex)
                            currentPageNumber.text = getString(
                                R.string.page_counter_text,
                                currentDraftViewIndex + 1,
                                examDraftPages.childCount
                            )
                            Toast.Companion.makeText(
                                this,
                                "صفحه حذف شد.",
                                Toast.Companion.WARNING_SIGN,
                                Toast.Companion.LENGTH_SHORT
                            ).show()
                            updateDraftPagesCount()
                        })
                        dialog.setNegativeButton(
                            "خیر",
                            View.OnClickListener { v4: View? -> dialog.dismiss(dialog) })
                        dialog.show(this)
                    } else {
                        Toast.Companion.makeText(
                            this,
                            "صفحه اصلی چرک نویس، قابل حذف نیست!",
                            Toast.Companion.WARNING_SIGN,
                            Toast.Companion.LENGTH_SHORT
                        ).show()
                    }
                })
                window.setElevation(30f)
                window.showAsDropDown(v2, -250, -250)
            }
        })
        draftViewOptions.setOnClickListener(View.OnClickListener { v9: View? ->
            if (examDraftPages.childCount >= 1 && examDraftPages.visibility == View.VISIBLE) {
                examDraftPage = examDraftPages.getChildAt(currentDraftViewIndex) as CanvasView
                if (examDraftPage != null) {
                    val v: View = LayoutInflater.from(this@MainActivity)
                        .inflate(R.layout.popup_draft_toolbox, null)
                    draftToolboxItems = v.findViewById<LinearLayout>(R.id.toolbox_items_layout)
                    if (!examFileVisibility) {
                        if (draftPathErasingEnabled) {
                            (draftToolboxItems.getChildAt(3) as ImageButton).setImageResource(R.drawable.search_by_exam_correcting)
                            (draftToolboxItems.getChildAt(3) as ImageButton).getDrawable()
                                .setColorFilter(
                                    getResources().getColor(R.color.colorAccent),
                                    PorterDuff.Mode.SRC_IN
                                )
                        } else {
                            (draftToolboxItems.getChildAt(3) as ImageButton).setImageResource(R.drawable.draft_pen_eraser)
                            (draftToolboxItems.getChildAt(3) as ImageButton).getDrawable()
                                .setColorFilter(
                                    getResources().getColor(R.color.elements_color_tint),
                                    PorterDuff.Mode.SRC_IN
                                )
                        }
                    } else {
                        (draftToolboxItems.getChildAt(3) as ImageButton).setImageResource(R.drawable.delete)
                        (draftToolboxItems.getChildAt(3) as ImageButton).getDrawable()
                            .setColorFilter(
                                getResources().getColor(R.color.elements_color_tint),
                                PorterDuff.Mode.SRC_IN
                            )
                    }
                    if (examFile != null && examFileLoaded) {
                        //draftToolboxItems.getChildAt(4).setVisibility(VISIBLE);
                        if (examFileVisibility) {
                            (draftToolboxItems.getChildAt(4) as ImageButton).setImageResource(R.drawable.exam_file_visibility_off)
                            (draftToolboxItems.getChildAt(4) as ImageButton).getDrawable()
                                .setColorFilter(
                                    getResources().getColor(R.color.colorAccent),
                                    PorterDuff.Mode.SRC_IN
                                )
                        } else {
                            (draftToolboxItems.getChildAt(4) as ImageButton).setImageResource(R.drawable.exam_file_visibility)
                            (draftToolboxItems.getChildAt(4) as ImageButton).getDrawable()
                                .setColorFilter(
                                    getResources().getColor(R.color.elements_color_tint),
                                    PorterDuff.Mode.SRC_IN
                                )
                        }
                    } else {
                        //draftToolboxItems.getChildAt(4).setVisibility(GONE);
                    }
                    (draftToolboxItems.getChildAt(0) as ImageButton).setImageResource(
                        selectedModeResId
                    )
                    (draftToolboxItems.getChildAt(1) as ImageButton).getDrawable()
                        .setColorFilter(selectedColor, PorterDuff.Mode.SRC_IN)
                    if (!isDraftCanvasCleared) {
                        draftToolboxItems.getChildAt(5).visibility = View.VISIBLE
                    } else {
                        draftToolboxItems.getChildAt(5).visibility = View.GONE
                    }
                    val window: PopupWindow = PopupWindow(
                        v,
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        true
                    )
                    window.setClippingEnabled(false)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        window.setElevation(30f)
                    }
                    draftToolboxItems.getChildAt(3)
                        .setOnLongClickListener { view12: View? ->
                            if (!examFileVisibility && isDraftDrawingHintShown) {
                                window.dismiss()
                                if (vibrator.hasVibrator()) {
                                    vibrator.vibrate(50)
                                }
                                clearDraftCanvas()
                                draftPathErasingEnabled = false
                                Toast.Companion.makeText(
                                    this@MainActivity,
                                    "صفحه چرک نویس، پاکسازی شد.",
                                    Toast.Companion.WARNING_SIGN,
                                    Toast.Companion.LENGTH_SHORT
                                ).show()
                            }
                            true
                        }
                    draftToolboxItemIndex = 0
                    while (draftToolboxItemIndex < draftToolboxItems.childCount) {
                        draftToolboxItems.getChildAt(draftToolboxItemIndex)
                            .setOnClickListener(View.OnClickListener { v1: View ->
                                if (v1.id == R.id.show_draft_options) {
                                    window.dismiss()
                                    val v3: View = LayoutInflater.from(this@MainActivity)
                                        .inflate(R.layout.popup_draft_options, null)
                                    val draftOptions: LinearLayout =
                                        v3.findViewById<LinearLayout>(R.id.drawing_options_layout)
                                    val window3: PopupWindow = PopupWindow(
                                        v3,
                                        WindowManager.LayoutParams.WRAP_CONTENT,
                                        WindowManager.LayoutParams.WRAP_CONTENT,
                                        true
                                    )
                                    window3.setClippingEnabled(true)
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        window3.setElevation(30f)
                                    }
                                    dismissDrawOptionsWindowManually = false
                                    updateDraftOptionsStatus(draftOptions)
                                    draftOptionsItemIndex = 0
                                    while (draftOptionsItemIndex < draftOptions.childCount) {
                                        draftOptions.getChildAt(draftOptionsItemIndex)
                                            .setOnClickListener(
                                                View.OnClickListener { view13: View ->
                                                    if (view13.id == R.id.save_draft) {
                                                        dismissDrawOptionsWindowManually = true
                                                        window3.dismiss()
                                                        saveDraftScreenshot()
                                                    } else if (view13.id == R.id.undo_changes) {
                                                        examDraftPage.undo()
                                                        if (!examDraftPage.canUndo() && !examFileVisibility) {
                                                            draftDrawingHint.visibility =
                                                                View.VISIBLE
                                                            isDraftDrawingHintShown = false
                                                        }
                                                        updateDraftOptionsStatus(draftOptions)
                                                    } else if (view13.id == R.id.redo_changes) {
                                                        examDraftPage.redo()
                                                        if (examDraftPage.canUndo() && !examFileVisibility) {
                                                            draftDrawingHint.visibility = View.GONE
                                                            isDraftDrawingHintShown = true
                                                        }
                                                        updateDraftOptionsStatus(draftOptions)
                                                    } else if (view13.id == R.id.close_window) {
                                                        dismissDrawOptionsWindowManually = true
                                                        window3.dismiss()
                                                    }
                                                })
                                        draftOptionsItemIndex++
                                    }
                                    try {
                                        window3.setOnDismissListener(PopupWindow.OnDismissListener {
                                            if (!dismissDrawOptionsWindowManually) window3.showAsDropDown(
                                                draftViewOptions,
                                                0,
                                                -300
                                            )
                                        })
                                        window3.showAsDropDown(draftViewOptions, 0, -300)
                                    } catch (e: Exception) {
                                        Toast.Companion.makeText(
                                            this,
                                            "درخواست شما اجرا نشد!",
                                            Toast.Companion.WARNING_SIGN,
                                            Toast.Companion.LENGTH_SHORT
                                        ).show()
                                    }
                                } else if (v1.id == R.id.select_drawing_mode) {
                                    if (!draftPathErasingEnabled) {
                                        val v4: View = LayoutInflater.from(this@MainActivity)
                                            .inflate(R.layout.popup_drawing_modes, null)
                                        val modes: LinearLayout =
                                            v4.findViewById<LinearLayout>(R.id.drawing_modes_layout)
                                        val window1: PopupWindow = PopupWindow(
                                            v4,
                                            WindowManager.LayoutParams.WRAP_CONTENT,
                                            WindowManager.LayoutParams.WRAP_CONTENT,
                                            true
                                        )
                                        window1.setClippingEnabled(false)
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                            window1.setElevation(30f)
                                        }
                                        penModelIndex = 0
                                        while (penModelIndex < modes.childCount) {
                                            modes.getChildAt(penModelIndex)
                                                .setOnClickListener(View.OnClickListener { view2: View ->
                                                    val currentDrawerModeResId = selectedModeResId
                                                    if (view2.id == R.id.mode_circle) {
                                                        selectedMode = CanvasView.Drawer.ELLIPSE
                                                        selectedModeResId = R.drawable.circle
                                                        selectedDrawingMode = CanvasView.Mode.DRAW
                                                        selectedStyle = Paint.Style.STROKE
                                                    } else if (view2.id == R.id.mode_rectangle) {
                                                        selectedMode = CanvasView.Drawer.RECTANGLE
                                                        selectedModeResId = R.drawable.rectangle
                                                        selectedDrawingMode = CanvasView.Mode.DRAW
                                                        examDraftPage.paintStyle =
                                                            Paint.Style.STROKE
                                                    } else if (view2.id == R.id.mode_line) {
                                                        selectedMode = CanvasView.Drawer.LINE
                                                        selectedModeResId = R.drawable.remove_object
                                                        selectedDrawingMode = CanvasView.Mode.DRAW
                                                        selectedStyle = Paint.Style.STROKE
                                                    } else if (view2.id == R.id.mode_path) {
                                                        selectedMode = CanvasView.Drawer.PEN
                                                        selectedDrawingMode = CanvasView.Mode.DRAW
                                                        selectedModeResId = R.drawable.path_drawing
                                                        selectedStyle = Paint.Style.STROKE
                                                    } else if (view2.id == R.id.mode_text) {
                                                        window1.dismiss()
                                                        window.dismiss()
                                                        showSubmitTextWindow()
                                                    } else if (view2.id == R.id.mode_circle_filled) {
                                                        selectedMode = CanvasView.Drawer.ELLIPSE
                                                        selectedModeResId = R.drawable.circle_filled
                                                        selectedDrawingMode = CanvasView.Mode.DRAW
                                                        selectedStyle = Paint.Style.FILL_AND_STROKE
                                                    } else if (view2.id == R.id.mode_rectangle_filled) {
                                                        selectedMode = CanvasView.Drawer.RECTANGLE
                                                        selectedModeResId =
                                                            R.drawable.rectangle_filled
                                                        selectedDrawingMode = CanvasView.Mode.DRAW
                                                        selectedStyle = Paint.Style.FILL_AND_STROKE
                                                    }
                                                    if (currentDrawerModeResId != selectedModeResId) {
                                                        (draftToolboxItems.getChildAt(0) as ImageButton).setImageResource(
                                                            selectedModeResId
                                                        )
                                                        examDraftPage.drawer = selectedMode
                                                        examDraftPage.mode = selectedDrawingMode
                                                        examDraftPage.paintStyle = selectedStyle
                                                        window1.dismiss()
                                                        window.dismiss()
                                                        Toast.Companion.makeText(
                                                            this,
                                                            "حالت قلم تغییر یافت.",
                                                            Toast.Companion.WARNING_SIGN,
                                                            Toast.Companion.LENGTH_SHORT
                                                        ).show()
                                                    }
                                                })
                                            penModelIndex++
                                        }
                                        try {
                                            window1.showAsDropDown(v, 0, 30)
                                        } catch (e: Exception) {
                                            Toast.Companion.makeText(
                                                this,
                                                "درخواست شما اجرا نشد!",
                                                Toast.Companion.WARNING_SIGN,
                                                Toast.Companion.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                } else if (v1.id == R.id.delete_pen_strokes) {
                                    if (isDraftDrawingHintShown) {
                                        window.dismiss()
                                        if (!examFileVisibility) {
                                            if (!draftPathErasingEnabled) {
                                                (draftToolboxItems.getChildAt(3) as ImageButton).setImageResource(
                                                    R.drawable.search_by_exam_correcting
                                                )
                                                (draftToolboxItems.getChildAt(3) as ImageButton).getDrawable()
                                                    .setColorFilter(
                                                        getResources().getColor(R.color.colorAccent),
                                                        PorterDuff.Mode.SRC_IN
                                                    )
                                                examDraftPage.paintStrokeColor =
                                                    getResources().getColor(
                                                        R.color.element_background_color
                                                    )
                                                examDraftPage.drawer = CanvasView.Drawer.PEN
                                                examDraftPage.paintStrokeWidth =
                                                    draftEraserStrokeSize
                                                examDraftPage.mode = CanvasView.Mode.DRAW
                                                examDraftPage.paintStyle = Paint.Style.STROKE
                                                draftPathErasingEnabled = true
                                            } else {
                                                (draftToolboxItems.getChildAt(3) as ImageButton).setImageResource(
                                                    R.drawable.draft_pen_eraser
                                                )
                                                (draftToolboxItems.getChildAt(3) as ImageButton).getDrawable()
                                                    .setColorFilter(
                                                        getResources().getColor(R.color.elements_color_tint),
                                                        PorterDuff.Mode.SRC_IN
                                                    )
                                                examDraftPage.mode = selectedDrawingMode
                                                examDraftPage.paintStrokeColor = selectedColor
                                                examDraftPage.paintStrokeWidth = draftPenStrokeSize
                                                examDraftPage.drawer = selectedMode
                                                examDraftPage.paintStyle = selectedStyle
                                                draftPathErasingEnabled = false
                                            }
                                        } else {
                                            clearDraftCanvas()
                                            draftPathErasingEnabled = false
                                        }
                                    }
                                } else if (v1.id == R.id.change_pen_stroke) {
                                    val v2: View = LayoutInflater.from(this@MainActivity)
                                        .inflate(R.layout.popup_pen_stroke, null)
                                    val strokeSize: EditText =
                                        v2.findViewById<EditText>(R.id.pen_stroke_text)
                                    val strokeSubmit: ImageButton =
                                        v2.findViewById<ImageButton>(R.id.pen_stroke_submit)
                                    val window2: PopupWindow = PopupWindow(
                                        v2,
                                        WindowManager.LayoutParams.WRAP_CONTENT,
                                        WindowManager.LayoutParams.WRAP_CONTENT,
                                        true
                                    )
                                    window2.setClippingEnabled(false)
                                    window2.setElevation(30f)
                                    if (isDraftEraserStrokeSizeEdited && draftPathErasingEnabled) {
                                        strokeSize.setText(draftEraserStrokeSize.toInt().toString())
                                    } else {
                                        if (isDraftPenStrokeSizeEdited && !draftPathErasingEnabled) {
                                            strokeSize.setText(
                                                draftPenStrokeSize.toInt().toString()
                                            )
                                        } else strokeSize.setText("")
                                    }
                                    if (draftPathErasingEnabled) {
                                        strokeSize.setHint("ضخامت پاک کن")
                                    } else {
                                        strokeSize.setHint("ضخامت قلم")
                                    }
                                    strokeSize.addTextChangedListener(object : TextWatcher {
                                        override fun beforeTextChanged(
                                            charSequence: CharSequence,
                                            i: Int,
                                            i1: Int,
                                            i2: Int
                                        ) {
                                        }

                                        override fun onTextChanged(
                                            charSequence: CharSequence,
                                            i: Int,
                                            i1: Int,
                                            i2: Int
                                        ) {
                                            strokeSize.error = null
                                        }

                                        override fun afterTextChanged(editable: Editable) {
                                        }
                                    })
                                    strokeSubmit.setOnClickListener(View.OnClickListener { v3: View? ->
                                        if (strokeSize.getText().length != 0) {
                                            val wantedStrokeSize: Int =
                                                strokeSize.getText().toString().toInt()
                                            if (wantedStrokeSize >= 1) {
                                                if (!draftPathErasingEnabled) {
                                                    if (wantedStrokeSize <= MAX_OF_PEN_STROKE) {
                                                        if (wantedStrokeSize.toFloat() != draftPenStrokeSize || !isDraftPenStrokeSizeEdited) {
                                                            setPenStroke(
                                                                strokeSize,
                                                                window2,
                                                                window,
                                                                wantedStrokeSize.toFloat()
                                                            )
                                                        } else {
                                                            strokeSize.error =
                                                                "نباید با مقدار فعلی برابر باشد!"
                                                        }
                                                    } else {
                                                        strokeSize.error =
                                                            "حداکثر ضخامت قلم، ۱۰۰ می باشد!"
                                                    }
                                                } else {
                                                    if (wantedStrokeSize <= MAX_OF_ERASER_STROKE) {
                                                        if (wantedStrokeSize.toFloat() != draftEraserStrokeSize || !isDraftEraserStrokeSizeEdited) {
                                                            setPenStroke(
                                                                strokeSize,
                                                                window2,
                                                                window,
                                                                wantedStrokeSize.toFloat()
                                                            )
                                                        } else {
                                                            strokeSize.error =
                                                                "نباید با مقدار فعلی برابر باشد!"
                                                        }
                                                    } else {
                                                        strokeSize.error =
                                                            "حداکثر ضخامت پاک کن، ۲۵۰ می باشد!"
                                                    }
                                                }
                                            } else {
                                                strokeSize.error = "مقدار ضخامت، عدد طبیعی است!"
                                            }
                                        } else {
                                            strokeSize.error = "لطفاً مقدار ضخامت را تعیین کنید!"
                                        }
                                    })
                                    try {
                                        window2.showAsDropDown(v, 0, 30)
                                    } catch (e: Exception) {
                                        Toast.Companion.makeText(
                                            this,
                                            "درخواست شما اجرا نشد!",
                                            Toast.Companion.WARNING_SIGN,
                                            Toast.Companion.LENGTH_SHORT
                                        ).show()
                                    }
                                } else if (v1.id == R.id.change_pen_color) {
                                    if (!draftPathErasingEnabled) {
                                        (draftToolboxItems.getChildAt(1) as ImageButton).getDrawable()
                                            .setColorFilter(selectedColor, PorterDuff.Mode.SRC_IN)
                                        val v3: View = LayoutInflater.from(this@MainActivity)
                                            .inflate(R.layout.popup_bookmarks_color, null)
                                        val colors: LinearLayout =
                                            v3.findViewById<LinearLayout>(R.id.bookmark_colors)
                                        val window3: PopupWindow = PopupWindow(
                                            v3,
                                            WindowManager.LayoutParams.WRAP_CONTENT,
                                            WindowManager.LayoutParams.WRAP_CONTENT,
                                            true
                                        )
                                        window3.setClippingEnabled(false)
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                            window3.setElevation(30f)
                                        }
                                        strokePenColorIndex = 0
                                        while (strokePenColorIndex < colors.childCount) {
                                            colors.getChildAt(strokePenColorIndex)
                                                .setOnClickListener { view2: View ->
                                                    if (view2.id == R.id.bookmark_color_r) {
                                                        selectedColor = PEN_STROKE_COLORS[0]
                                                    } else if (view2.id == R.id.bookmark_color_o) {
                                                        selectedColor = PEN_STROKE_COLORS[1]
                                                    } else if (view2.id == R.id.bookmark_color_y) {
                                                        selectedColor = PEN_STROKE_COLORS[2]
                                                    } else if (view2.id == R.id.bookmark_color_c) {
                                                        selectedColor = PEN_STROKE_COLORS[3]
                                                    } else if (view2.id == R.id.bookmark_color_b) {
                                                        selectedColor = PEN_STROKE_COLORS[4]
                                                    } else if (view2.id == R.id.bookmark_color_p) {
                                                        selectedColor = PEN_STROKE_COLORS[5]
                                                    } else if (view2.id == R.id.bookmark_color_br) {
                                                        selectedColor = PEN_STROKE_COLORS[6]
                                                    } else if (view2.id == R.id.bookmark_color_g) {
                                                        selectedColor = PEN_STROKE_COLORS[7]
                                                    } else if (view2.id == R.id.bookmark_color_w) {
                                                        selectedColor = PEN_STROKE_COLORS[8]
                                                    }
                                                    (draftToolboxItems.getChildAt(1) as ImageButton).getDrawable()
                                                        .setColorFilter(
                                                            selectedColor,
                                                            PorterDuff.Mode.SRC_IN
                                                        )
                                                    examDraftPage.paintStrokeColor = selectedColor
                                                    window3.dismiss()
                                                    window.dismiss()
                                                    Toast.Companion.makeText(
                                                        this,
                                                        "رنگ قلم تغییر یافت.",
                                                        Toast.Companion.WARNING_SIGN,
                                                        Toast.Companion.LENGTH_SHORT
                                                    ).show()
                                                }
                                            strokePenColorIndex++
                                        }
                                        try {
                                            window3.showAsDropDown(v, convertDpToPx(55).toInt(), 30)
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                            Toast.Companion.makeText(
                                                this,
                                                "درخواست شما اجرا نشد!",
                                                Toast.Companion.WARNING_SIGN,
                                                Toast.Companion.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }
                            })
                        draftToolboxItemIndex++
                    }
                    try {
                        window.showAsDropDown(draftViewOptions, -350, -300)
                    } catch (e: Exception) {
                        Toast.Companion.makeText(
                            this,
                            "درخواست شما اجرا نشد!",
                            Toast.Companion.WARNING_SIGN,
                            Toast.Companion.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        })
        openDraftBox.setOnClickListener(View.OnClickListener { v: View? ->
            if (examDraftLayout.visibility != View.VISIBLE) {
                examDraftLayout.visibility = View.VISIBLE
            }
        })
        startCurrentExam.setOnClickListener { v: View? ->
            if (questions.size >= MIN_OF_QUESTIONS_COUNT) {
                val ad: MaterialAlertDialog = MaterialAlertDialog(this)
                ad.setTitle("شروع آزمون")
                ad.setIcon(R.drawable.start_exam)
                ad.setMessage("آیا از شروع این آزمون اطمینان دارید؟")
                ad.setPositiveButton("بله، آماده ام", View.OnClickListener { v1: View? ->
                    ad.dismiss(ad)
                    startExam()
                })
                ad.setNegativeButton(
                    "هنوز نه",
                    View.OnClickListener { v1: View? -> ad.dismiss(ad) })
                ad.show(this)
            } else {
                Toast.Companion.makeText(
                    this,
                    "هنوز سؤالی وارد نکردید!\nحداقل تعداد سؤال، ۵ سؤال هست.",
                    Toast.Companion.WARNING_SIGN,
                    Toast.Companion.LENGTH_SHORT
                ).show()
            }
        }
        addQuestionButton.setOnClickListener(View.OnClickListener { view: View? ->
            if (questions.size < MAX_OF_QUESTIONS_COUNT) {
                /*Saver.getInstance(MainActivity.this).setDismissSide(SIDE_FRAGMENT_SHOWER);
                MaterialFragmentShower shower = new MaterialFragmentShower(MainActivity.this);
                shower.setFragment(new AddQuestionDialog(shower, newQuestion -> {
                    emptyQuestionsView.setVisibility(GONE);
                    removeQuestionButton.setVisibility(VISIBLE);
                    Questions questionsL = currentExam.answerSheet;
                    Log.i("TAG", "iii: " + newQuestion.getQuestionNumber());
                    questions.add(newQuestion);
                    questionsL.setQuestions(questions);
                    Saver.getInstance(MainActivity.this).saveQuestions(questionsL);
                    currentExam.setAnswerSheet(questionsL);
                    updateRecentExams();
                    showAnswerSheet();
                    showHideKeyboardLayout(false, MainActivity.this.getWindow().getDecorView());
                }, questions));
                shower.show(MainActivity.this, shower);*/
            } else {
                Toast.Companion.makeText(
                    this,
                    "حداکثر تعداد سؤال، ۱۰,۰۰۰ سؤال هست.",
                    Toast.Companion.WARNING_SIGN,
                    Toast.Companion.LENGTH_SHORT
                ).show()
            }
        })
        removeQuestionButton.setOnClickListener(View.OnClickListener { view: View? ->
            if (questions.size > MIN_OF_QUESTIONS_COUNT) {
            } else {
                Toast.Companion.makeText(
                    this,
                    "حداقل تعداد سؤال، ۵ سؤال هست.",
                    Toast.Companion.WARNING_SIGN,
                    Toast.Companion.LENGTH_SHORT
                ).show()
            }
        })
        resetChronometer.setOnClickListener(View.OnClickListener { view: View? ->
            if (useChronometer && resetEnable) {
                val builder: MaterialAlertDialog = MaterialAlertDialog(this@MainActivity)
                builder.setIcon(R.drawable.reset_chronometer)
                builder.setTitle("شروع مجدد کرنومتر")
                builder.setMessage(
                    """آیا برای شروع مجدد کرونومتر اطمینان دارید؟

دکمه «شروع مجدد کرنومتر»، پس از این عمل، به مدت ۶۰ ثانیه غیرفعال خواهد شد!
مقدار کنونی کرنومتر: ${
                        String.format(
                            Locale.getDefault(),
                            "%d",
                            currentExam.secondsOfThinkingOnQuestion
                        )
                    } ثانیه"""
                )
                builder.setPositiveButton(
                    "خیر",
                    View.OnClickListener { v4: View? -> builder.dismiss(builder) })
                builder.setNegativeButton("بله", View.OnClickListener { v4: View? ->
                    currentExam.secondsOfThinkingOnQuestion = (0)
                    currentExam.answerSheet = (
                            Saver.Companion.getInstance(this@MainActivity).loadQuestions()
                            )
                    updateRecentExams()
                    changeResetChronoButtonState(false)
                    builder.dismiss(builder)
                    Toast.Companion.makeText(
                        this@MainActivity,
                        "شروع مجدد کرنومتر ⏲️🔁",
                        Toast.Companion.LENGTH_SHORT
                    ).show()
                })
                builder.show(this@MainActivity)
            } else if (!resetEnable) {
                Toast.Companion.makeText(
                    this@MainActivity,
                    String.format(
                        Locale.getDefault(),
                        "%d ثانیه دیگر تا فعالسازی دکمه",
                        RESET_BUTTON_ENABLE_DELAY - buttonDisableSeconds
                    ),
                    Toast.Companion.LENGTH_SHORT
                ).show()
            }
        })
        examAction.setOnLongClickListener { view: View? ->
            if (currentExam.isStarted) {
                Toast.Companion.makeText(this, "اتمام آزمون", Toast.Companion.LENGTH_SHORT).show()
            } else if (currentExam.isCreating) {
                Toast.Companion.makeText(this, "ساخت آزمون", Toast.Companion.LENGTH_SHORT).show()
            } else if (currentExam.isCorrecting) {
                Toast.Companion.makeText(this, "تصحیح آزمون", Toast.Companion.LENGTH_SHORT).show()
            } else {
                Toast.Companion.makeText(this, "عملکرد آزمون", Toast.Companion.LENGTH_SHORT).show()
            }
            true
        }
        examAction.setOnClickListener(View.OnClickListener { v: View? ->
            if (currentExam.examStatus == Exam.ExamStatus.Started) {
                val ad: MaterialAlertDialog = MaterialAlertDialog(this@MainActivity)
                ad.setTitle("اتمام آزمون")
                ad.setIcon(R.drawable.done_exam)
                ad.setMessage(showAnsweredQuestions() + "آیا از اتمام این آزمون، اطمینان کامل دارید؟")
                ad.setNegativeButton("بله", View.OnClickListener { v7: View? ->
                    ad.dismiss(ad)
                    isExamStoppedManually = true
                    currentExam.isExamStoppedManually = (true)

                    updateRecentExams()
                    if (checkExamHasTime() && examTime != 0L) {
                        if (examTimeLeft != null) examTimeLeft.cancel()
                    }
                    endTheExam()
                })
                ad.setPositiveButton("خیر", View.OnClickListener { v7: View? -> ad.dismiss(ad) })
                ad.show(this@MainActivity)
            } else if (currentExam.examStatus == Exam.ExamStatus.Finished) {
                val ad: MaterialAlertDialog = MaterialAlertDialog(this@MainActivity)
                ad.setTitle("شروع مجدد آزمون")
                ad.setIcon(R.drawable.reset_exam)
                ad.setMessage("آزمون تمام شده است\nآیا تمایل به شرکت دوباره در این آزمون دارید؟")
                ad.setNegativeButton("بله", View.OnClickListener { v7: View? ->
                    ad.dismiss(ad)
                    Toast.Companion.makeText(
                        this,
                        "** Feature Unavailable **\nComing soon...",
                        Toast.Companion.LENGTH_SHORT
                    ).show()
                })
                ad.setPositiveButton("خیر", View.OnClickListener { v7: View? -> ad.dismiss(ad) })
                ad.show(this@MainActivity)
            }
        })
        userDashboardButton.setOnClickListener(View.OnClickListener { v: View? ->
            showMainViewLayout(MainView.UserDashboard)
        })
        examsListBack.setOnClickListener(View.OnClickListener { v: View? -> hideExamsListAnimation() })
        backToMainView.setOnClickListener(View.OnClickListener { v: View? ->
            // TODO: currentExam = null
            resetCustomExamFields()
            startExamButtonsLayout.visibility = View.GONE
            createExamLayoutContainer.visibility = View.GONE
            selectExamImmediately.visibility = View.GONE
            showMainViewLayout(MainView.RecentExams)
        })
        appMoreOptions.setOnClickListener(View.OnClickListener { v: View? ->
            if (optionsMenuOpened) {
                dismissOptionsMenu()
            } else {
                showOptionsMenu()
            }
        })
        clickableArea.setOnClickListener { v: View? ->
            if (optionsMenuOpened) {
                dismissOptionsMenu()
            }
        }
        enterToExamRoom.setOnClickListener { v: View? ->
            if (!checkFieldsHasError()) {
                showAdiveryAd(getString(R.string.adivery_interstitial_ad_id))
                setupExam(0)
            }
        }
    }

    private fun showAdiveryAd(placementId: String) {
        if (Adivery.isLoaded(placementId)) {
            Adivery.showAd(placementId)
        }
    }

    //    private void createNewExam(long time) {
    //        currentExam = new Exam();
    //        currentExam.setId(generateExamId());
    //        currentExam.setExamName(currentExamName);
    //        //currentExam.isCreating = (true);
    //        //currentExam.isStarted = (false);
    //        //currentExam.isChecked = (false);
    //        if (examFile != null) {
    //            currentExam.setExamFile(examFile);
    //        }
    //        if (questions != null) {
    //            currentExam.setAnswerSheet(new Questions(questions));
    //            recentExams.updateCurrentExam(currentExam);
    //            Saver.getInstance(MainActivity.this).saveRecentExams(recentExams);
    //        }
    //        currentExam.examTime =(time);
    //        currentExam.examTimeLeft =(time);
    //        currentExam.setUsedChronometer(useChronometer);
    //        currentExam.setChornoThreshold(chronoThreshold);
    //        currentExam.setUsedRandomQuestions(selectedQRandomly);
    //        currentExam.setUsedCategorize(useExamCategorize);
    //        currentExam.setUsedCorrection(correctionMode != CorrectionMode.None);
    //        currentExam.setCanCalculateTimeForCategory(useCategoryTiming);
    //        currentExam.setUsedCorrectionByCorrectAnswers(correctionMode == CorrectionMode.Keys);
    //        currentExam.setUsedTiming(checkExamHasTime());
    //        currentExam.setCanCalculateScoreOfCategory(useCategoryScore);
    //        currentExam.setStartExamTime(startedExamTime);
    //        currentExam.setSelectQuestionsManually(isQuestionsManually);
    //        currentExam.setAnswerSheet(new Questions(questions));
    //        recentExams.addExam(currentExam);
    //        Saver.getInstance(MainActivity.this).saveRecentExams(recentExams);
    //    }
    private fun updateDraftPage(index: Int) {
        for (i in 0 until examDraftPages.childCount) {
            if (i == index) {
                examDraftPages.getChildAt(i).visibility = View.VISIBLE
                examDraftPage = examDraftPages.getChildAt(i) as CanvasView
            } else {
                examDraftPages.getChildAt(i).visibility = View.GONE
            }
        }
    }

    private fun updateDraftPagesCount() {
        val count: Int = examDraftPages.childCount
        when (count) {
            0 -> draftPagesOptions.setImageResource(R.drawable.canvas_view_count_0)
            1 -> draftPagesOptions.setImageResource(R.drawable.canvas_view_count_1)
            2 -> draftPagesOptions.setImageResource(R.drawable.canvas_view_count_2)
            3 -> draftPagesOptions.setImageResource(R.drawable.canvas_view_count_3)
            4 -> draftPagesOptions.setImageResource(R.drawable.canvas_view_count_4)
            5 -> draftPagesOptions.setImageResource(R.drawable.canvas_view_count_5)
            6 -> draftPagesOptions.setImageResource(R.drawable.canvas_view_count_6)
            7 -> draftPagesOptions.setImageResource(R.drawable.canvas_view_count_7)
            8 -> draftPagesOptions.setImageResource(R.drawable.canvas_view_count_8)
            9 -> draftPagesOptions.setImageResource(R.drawable.canvas_view_count_9)
            else -> draftPagesOptions.setImageResource(R.drawable.canvas_view_count_9_plus)
        }
        draftPagesOptions.getDrawable()
            .setColorFilter(getResources().getColor(R.color.disable_button), PorterDuff.Mode.SRC_IN)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setExamDraftTouchEvents() {
        examDraftPages.getChildAt(currentDraftViewIndex)
            .setOnTouchListener { v: View?, event: MotionEvent? ->
                draftDrawingHint.visibility = View.GONE
                isDraftCanvasCleared = false
                isDraftDrawingHintShown = true
                false
            }
    }

    private fun saveDraftScreenshot() {
        //examDraftView.setBackgroundColor(getResources().getColor(R.color.background_color));
        if (draftPathErasingEnabled) {
            draftViewOptions.setVisibility(View.GONE)
            //closeDraftView.setVisibility(GONE);
        }
        val handler = Handler()
        Toast.Companion.makeText(
            this@MainActivity,
            "در حال تصویر برداری...",
            Toast.Companion.LENGTH_SHORT
        ).show()
        handler.postDelayed({ this.takeDraftScreenshot() }, 1000)
    }

    private fun takeDraftScreenshot() {
        val calendar = Calendar.getInstance()
        val now = printEnglishDate(
            intArrayOf(
                calendar[Calendar.YEAR],
                calendar[Calendar.MONTH],
                calendar[Calendar.DAY_OF_MONTH]
            )
        )
        try {
            val mPath: File = File(
                Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + "/Pictures/SalTech/Answer Sheet/Drafts"
            )
            if (!mPath.exists()) {
                mPath.mkdirs()
            }
            examDraftPage.setDrawingCacheEnabled(true)
            val bitmap: Bitmap = Bitmap.createBitmap(examDraftPage.drawingCache)
            examDraftPage.setDrawingCacheEnabled(false)
            val random = Random()
            val imageFile = File(
                mPath.absolutePath + "/" + "چرک_نویس_" + examNameText.getText().toString()
                    .replace(" ", "_") + "_" + now + random.nextInt(100) + ".jpg"
            )
            Log.i("TAG", "Draft Image Path:" + imageFile.absolutePath)
            val outputStream = FileOutputStream(imageFile)
            val quality = 100
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            outputStream.flush()
            outputStream.close()
            if (draftPathErasingEnabled) {
                draftViewOptions.setVisibility(View.VISIBLE)
                //closeDraftView.setVisibility(VISIBLE);
            }
            Toast.Companion.makeText(
                this@MainActivity,
                "تصویر ذخیره شد.",
                Toast.Companion.WARNING_SIGN,
                Toast.Companion.LENGTH_LONG
            ).show()
        } catch (e: Throwable) {
            e.printStackTrace()
            Toast.Companion.makeText(
                this@MainActivity,
                "تصویر گرفته نشد!",
                Toast.Companion.LENGTH_SHORT
            ).show()
        }
    }

    private fun updateDraftOptionsStatus(draftOptions: LinearLayout) {
        if (draftDrawingHint.visibility != View.VISIBLE && examDraftPage.canUndo() && !isDraftCanvasCleared) {
            draftOptions.getChildAt(0).visibility = View.VISIBLE
        } else {
            draftOptions.getChildAt(0).visibility = View.GONE
        }
        if (examDraftPage.canRedo() && !isDraftCanvasCleared) {
            draftOptions.getChildAt(1).visibility = View.VISIBLE
        } else {
            draftOptions.getChildAt(1).visibility = View.GONE
        }
        if (draftDrawingHint.visibility != View.VISIBLE && !isDraftCanvasCleared) {
            draftOptions.getChildAt(2).visibility = View.VISIBLE
        } else {
            draftOptions.getChildAt(2).visibility = View.GONE
        }
    }

    private fun showSubmitTextWindow() {
        val v: View = LayoutInflater.from(this@MainActivity).inflate(R.layout.popup_draw_text, null)
        val submitText: ImageButton = v.findViewById<ImageButton>(R.id.text_submit)
        val wantedText: EditText = v.findViewById<EditText>(R.id.draw_edit_text)
        val textSize: EditText = v.findViewById<EditText>(R.id.draw_text_size)
        val window: PopupWindow = PopupWindow(
            v,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            true
        )
        window.setClippingEnabled(false)
        window.setElevation(30f)
        wantedText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (charSequence.length >= 1) {
                    submitText.setImageResource(R.drawable.done_exam)
                } else {
                    submitText.setImageResource(R.drawable.close)
                }
            }

            override fun afterTextChanged(editable: Editable) {
            }
        })
        dismissSubmitTextWindowManually = false
        submitText.setOnClickListener { view: View? ->
            if (wantedText.getText().length != 0) {
                if (textSize.getText().length != 0) {
                    val wTextSizeI: Int = textSize.getText().toString().toInt()
                    if (wTextSizeI >= 1) {
                        if (wTextSizeI >= 30) {
                            if (wTextSizeI <= 200) {
                                setDraftTextDrawing(
                                    window,
                                    wantedText.getText().toString(),
                                    wTextSizeI.toFloat()
                                )
                            } else {
                                textSize.error = "حداکثر سایز متن، ۲۰۰ می باشد!"
                            }
                        } else {
                            textSize.error = "حداقل سایز متن، ۳۰ می باشد!"
                        }
                    } else {
                        textSize.error = "سایز متن، عدد طبیعی است!"
                    }
                } else {
                    setDraftTextDrawing(window, wantedText.getText().toString(), 50f)
                }
            } else {
                dismissSubmitTextWindowManually = true
                window.dismiss()
            }
        }
        try {
            window.showAsDropDown(draftViewOptions, -400, -300)
            window.setOnDismissListener(PopupWindow.OnDismissListener {
                if (!dismissSubmitTextWindowManually) window.showAsDropDown(
                    draftViewOptions,
                    -400,
                    -300
                )
            })
        } catch (e: Exception) {
            Toast.Companion.makeText(
                this,
                "درخواست شما اجرا نشد!",
                Toast.Companion.WARNING_SIGN,
                Toast.Companion.LENGTH_SHORT
            ).show()
        }
    }

    private fun setDraftTextDrawing(window: PopupWindow, text: String, textSize: Float) {
        examDraftPage.mode = CanvasView.Mode.TEXT
        examDraftPage.fontFamily = ResourcesCompat.getFont(
            this@MainActivity,
            R.font.estedad_light
        )
        examDraftPage.text = text
        examDraftPage.fontSize = textSize
        examDraftPage.paintStyle = Paint.Style.FILL_AND_STROKE
        selectedDrawingMode = CanvasView.Mode.TEXT
        selectedModeResId = R.drawable.text
        dismissSubmitTextWindowManually = true
        window.dismiss()
        Toast.Companion.makeText(
            this,
            "متن شما تنظیم شد. برای استفاده روی صفحه ضربه بزنید.",
            Toast.Companion.WARNING_SIGN,
            Toast.Companion.LENGTH_LONG
        ).show()
    }

    private fun setPenStroke(
        strokeSize: EditText,
        window2: PopupWindow,
        window: PopupWindow,
        wantedStrokeSize: Float
    ) {
        strokeSize.error = null
        examDraftPage.paintStrokeWidth = wantedStrokeSize
        if (draftPathErasingEnabled) draftEraserStrokeSize = wantedStrokeSize
        else draftPenStrokeSize = wantedStrokeSize
        window2.dismiss()
        window.dismiss()
        showHideKeyboardLayout(false, strokeSize)
        if (draftPathErasingEnabled) {
            isDraftEraserStrokeSizeEdited = true
            Toast.Companion.makeText(
                this,
                "مقدار ضخامت پاک کن روی " + wantedStrokeSize.toInt() + " تنظیم شد.",
                Toast.Companion.WARNING_SIGN,
                Toast.Companion.LENGTH_SHORT
            ).show()
        } else {
            Toast.Companion.makeText(
                this,
                "مقدار ضخامت قلم روی " + wantedStrokeSize.toInt() + " تنظیم شد.",
                Toast.Companion.WARNING_SIGN,
                Toast.Companion.LENGTH_SHORT
            ).show()
            isDraftPenStrokeSizeEdited = true
        }
    }

    private fun convertDpToPx(dp: Int): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            getResources().displayMetrics
        )
    }

    private fun clearDraftCanvas() {
        isDraftCanvasCleared = true
        examDraftPage.text = ""
        examDraftPage.paintStyle = selectedStyle
        for (k in 0..1000000 - 1) {
            examDraftPage.undo()
        }
        if (!examFileVisibility) {
            isDraftDrawingHintShown = false
        }
        if (selectedDrawingMode == CanvasView.Mode.TEXT) {
            selectedDrawingMode = CanvasView.Mode.DRAW
            selectedModeResId = R.drawable.path_drawing
        }
        examDraftPage.mode = selectedDrawingMode
        examDraftPage.paintStrokeColor = selectedColor
        examDraftPage.paintStrokeWidth = draftPenStrokeSize
        examDraftPage.drawer = selectedMode
        if (draftToolboxItems != null) {
            if (draftToolboxItems.childCount == 6) draftToolboxItems.getChildAt(5).visibility =
                View.GONE
        }
        draftPathErasingEnabled = false
    }

    private fun showAnsweredQuestions(): String {
        //questions = Saver.getInstance(MainActivity.this).loadQuestions().questions;
        var answeredQuestionsNumber = 0
        for (q in questions) {
            if (!q.isWhite) {
                answeredQuestionsNumber++
            }
        }
        return if (answeredQuestionsNumber != questions.size) {
            if (answeredQuestionsNumber == 0) {
                "شما به هیچ یک از سؤالات پاسخ نداده اید!" + "\n"
            } else {
                if ((questions.size - answeredQuestionsNumber <= APP_VERSION_CODE_DIGITS) && questions.size >= 20) {
                    "شما تقریباً به همه سؤالات، پاسخ داده اید." + "\n"
                } else if ((questions.size - answeredQuestionsNumber <= 5) && (questions.size > 10) && (questions.size < 20)) {
                    "شما تقریباً به همه سؤالات، پاسخ داده اید." + "\n"
                } else if ((questions.size - answeredQuestionsNumber <= 3) && questions.size <= 10) {
                    "شما تقریباً به همه سؤالات، پاسخ داده اید." + "\n"
                } else {
                    String.format(
                        Locale.getDefault(),
                        "شما به %d سؤال از %d سؤال، پاسخ داده اید.",
                        answeredQuestionsNumber,
                        questions.size
                    ) + "\n"
                }
            }
        } else {
            ""
        }
    }

    private fun checkFieldsHasError(): Boolean {
        // TODO: Setup this....
        return false
    }

    @Contract(pure = true)
    private fun resetCustomExamFields() {
        // TODO: Setup this.....
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setControlButtonStates(
        buttonState: CButtonState,
        controlButton: ImageButton?,
        visibility: Int
    ) {
        when (buttonState) {
            CButtonState.Clicked -> {
                // controlButton.setBackground(getResources().getDrawable(R.drawable.tiny_button_bg));
                //controlButton.getBackground().setColorFilter(defaultPaletteColor, SRC_IN);
                controlButton!!.getDrawable()
                    .setColorFilter(defaultPaletteColor, PorterDuff.Mode.SRC_IN)
                controlButton!!.setAlpha(1f)
                controlButton!!.setEnabled(true)
                controlButton!!.setClickable(true)
            }

            CButtonState.Idle -> {
                controlButton!!.setBackground(null)
                controlButton!!.getDrawable().setColorFilter(
                    getResources().getColor(R.color.elements_color_tint),
                    PorterDuff.Mode.SRC_IN
                )
                controlButton!!.setAlpha(1f)
                controlButton!!.setEnabled(true)
                controlButton!!.setClickable(true)
            }

            CButtonState.Disable -> {
                controlButton!!.setBackground(null)
                controlButton!!.getDrawable().setColorFilter(
                    getResources().getColor(R.color.disable_button_fade),
                    PorterDuff.Mode.SRC_IN
                )
                controlButton.setAlpha(0.85f)
                controlButton.setEnabled(false)
                controlButton!!.setClickable(false)
            }

            else -> {}
        }
        controlButton!!.setVisibility(visibility)
    }

    private fun setToggleButtonsClick(toggleButton: LinearLayout) {
        val count: Int = toggleButton.childCount
        setToggleButtonDefaults(toggleButton)
        toggleButton.getChildAt(0).setOnClickListener(View.OnClickListener { v: View ->
            setTogglePartColor(v as TextView, true, 0, count)
            setTogglePartColor(toggleButton.getChildAt(1) as TextView, false, 1, count)
            if (count > 2) {
                setTogglePartColor(toggleButton.getChildAt(2) as TextView, false, 2, count)
            }
            this.onToggleButtonPartClicked(v.id)
        })
        toggleButton.getChildAt(1).setOnClickListener(View.OnClickListener { v: View ->
            setTogglePartColor(v as TextView, true, 1, count)
            setTogglePartColor(toggleButton.getChildAt(0) as TextView, false, 0, count)
            if (count > 2) {
                setTogglePartColor(toggleButton.getChildAt(2) as TextView, false, 2, count)
            }
            this.onToggleButtonPartClicked(v.id)
        })
        if (count > 2) {
            toggleButton.getChildAt(2).setOnClickListener(View.OnClickListener { v: View ->
                setTogglePartColor(v as TextView, true, 2, count)
                setTogglePartColor(toggleButton.getChildAt(0) as TextView, false, 0, count)
                setTogglePartColor(toggleButton.getChildAt(1) as TextView, false, 1, count)
                this.onToggleButtonPartClicked(v.id)
            })
        }
    }

    private fun setToggleButtonDefaults(toggleButton: LinearLayout) {
        val count: Int = toggleButton.childCount
        if (count > 2) {
            setTogglePartColor(toggleButton.getChildAt(2) as TextView, true, 2, count)
            setTogglePartColor(toggleButton.getChildAt(1) as TextView, false, 1, count)
            setTogglePartColor(toggleButton.getChildAt(0) as TextView, false, 0, count)
        } else {
            setTogglePartColor(toggleButton.getChildAt(1) as TextView, true, 1, count)
            setTogglePartColor(toggleButton.getChildAt(0) as TextView, false, 0, count)
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setTogglePartColor(togglePart: TextView, clicked: Boolean, index: Int, count: Int) {
        if (!clicked) {
            if (index == 0) {
                togglePart.background = getResources().getDrawable(R.drawable.half_toggle_left)
            } else if (index == 1) {
                if (count > 2) {
                    togglePart.background = getResources().getDrawable(R.drawable.middle_toggle)
                } else {
                    togglePart.background = getResources().getDrawable(R.drawable.half_toggle_right)
                }
            } else if (index == 2) {
                togglePart.background = getResources().getDrawable(R.drawable.half_toggle_right)
            }
            togglePart.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.disable_button)))
            togglePart.setTextColor(getResources().getColor(R.color.disable_button))
        } else {
            if (index == 0) {
                togglePart.background =
                    getResources().getDrawable(R.drawable.half_toggle_left_selected)
            } else if (index == 1) {
                if (count > 2) {
                    togglePart.background =
                        getResources().getDrawable(R.drawable.middle_toggle_selected)
                } else {
                    togglePart.background =
                        getResources().getDrawable(R.drawable.half_toggle_right_selected)
                }
            } else if (index == 2) {
                togglePart.background =
                    getResources().getDrawable(R.drawable.half_toggle_right_selected)
            }
            togglePart.setBackgroundTintList(null)
            togglePart.setTextColor(Color.rgb(245, 245, 245))
        }
    }

    @SuppressLint("NonConstantResourceId")
    private fun setRowToggleButtonsClicks() {
        setRowToggleButtonsDefaults()
        createCustomExam.setOnClickListener { v: View? ->
            selectExamImmediately.visibility = View.GONE
            createExamLayoutContainer.visibility = View.VISIBLE
            startExamButtonsLayout.visibility = View.VISIBLE
        }
        setRowToggleButtonPressed(createCustomExam)
        selectExamName.setOnClickListener { v: View? ->
            val examNames: ExamNames =
                Saver.Companion.getInstance(this@MainActivity).loadExamNames()
            for (i in 0..4) {
                val en: ExamName = ExamName()
                en.setName("درس " + (i + 1))
                examNames.addExamName(en)
            }
            val shower: MaterialFragmentShower = MaterialFragmentShower(this)
            shower.setCancelable(true)
            shower.fragment = (
                    SelectThingsDialog(
                        shower,
                        examNames.things!!,
                        Thing.thingName!!,
                        false,
                        object : ThingSelectedListener {
                            override fun onSelected(thing: Thing?) {
                                val en2 = ExamName()
                                en2.setName(thing!!.title!!)
                                currentExamName = en2
                                setRowToggleButtonText(selectExamName, thing.title!!)
                                setRowToggleButtonSwitched(selectExamName, true)
                            }
                        })
                    )
            shower.show(this, shower)
        }
        selectExamName.setOnLongClickListener { v: View? ->
            if (currentExamName != null) {
                currentExamName = null
                setRowToggleButtonSwitched(selectExamName, false)
                setRowToggleButtonText(selectExamName, getString(R.string.select_exam_name))
                Toast.Companion.makeText(
                    this,
                    "نام آزمون لغو شد.",
                    Toast.Companion.WARNING_SIGN,
                    Toast.Companion.LENGTH_SHORT
                ).show()
                return@setOnLongClickListener true
            } else {
                return@setOnLongClickListener false
            }
        }
        setRowToggleButtonPressed(selectExamName)
        selectChronometerEnabled.setOnClickListener { v: View? ->
            useChronometer = !useChronometer
            chronoThresholdNP.isEnabled = useChronometer
            val enabledColor: Int =
                if (useChronometer && chronoThresholdNP.value != 0) getResources().getColor(R.color.colorAccent)
                else getResources().getColor(R.color.disable_button)
            chronoThresholdNP.selectedTextColor = enabledColor
            setRowToggleButtonSwitched(selectChronometerEnabled, useChronometer)
        }
        setRowToggleButtonPressed(selectChronometerEnabled)
        useCategoryTimingEnabled.setOnClickListener { v: View? ->
            if (useCategoryTimingEnabled.isActivated) {
                useCategoryTiming = !useCategoryTiming
                setRowToggleButtonSwitched(useCategoryTimingEnabled, useCategoryTiming)
            }
        }
        setRowToggleButtonPressed(useCategoryTimingEnabled)
        useCategoryScoreEnabled.setOnClickListener { v: View? ->
            if (useCategoryScoreEnabled.isActivated) {
                useCategoryScore = !useCategoryScore
                setRowToggleButtonSwitched(useCategoryScoreEnabled, useCategoryScore)
            }
        }
        setRowToggleButtonPressed(useCategoryScoreEnabled)
        selectCategoryEnabled.setOnClickListener { v: View? ->
            if (!useExamCategorize) {
                useCategoryScoreEnabled.visibility = View.VISIBLE
                useCategoryTimingEnabled.visibility = View.VISIBLE
                useExamCategorize = true
            } else {
                useCategoryScoreEnabled.visibility = View.GONE
                useCategoryTimingEnabled.visibility = View.GONE
                useExamCategorize = false
            }
            setRowToggleButtonSwitched(selectCategoryEnabled, useExamCategorize)
        }
        setRowToggleButtonPressed(selectCategoryEnabled)
        selectQuestionsRandomly.setOnClickListener { v: View? ->
            if (selectQuestionsRandomly.isActivated) {
                if (!selectedQRandomly) {
                    questionsCPatternText.visibility = View.GONE
                    lastQuestionNoText.visibility = View.VISIBLE
                    selectedQRandomly = true
                } else {
                    questionsCPatternText.visibility = View.VISIBLE
                    lastQuestionNoText.visibility = View.GONE
                    selectedQRandomly = false
                }
                setRowToggleButtonSwitched(selectQuestionsRandomly, selectedQRandomly)
            }
        }
        setRowToggleButtonPressed(selectQuestionsRandomly)
    }

    private fun setRowToggleButtonText(toggleButton: LinearLayout, text: String) {
        (toggleButton.getChildAt(0) as TextView).text = text
    }

    private fun setRowToggleButtonsDefaults() {
        useChronometer = false
        useCategoryScoreEnabled.visibility = View.GONE
        useCategoryTimingEnabled.visibility = View.GONE
        setEnabledRowButton(useCategoryScoreEnabled, false, useCategoryScore)
        setEnabledRowButton(useCategoryTimingEnabled, checkExamHasTime(), useCategoryTiming)
        selectChronometerEnabled.getChildAt(0).isSelected = true
        setRowToggleButtonSwitched(selectChronometerEnabled, false)
        useExamCategorize = false
        setRowToggleButtonSwitched(selectCategoryEnabled, false)
        selectedQRandomly = false
        setRowToggleButtonSwitched(selectQuestionsRandomly, false)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setRowToggleButtonPressed(rowToggleBtn: LinearLayout) {
        rowToggleBtn.isActivated = true
        rowToggleBtn.setOnTouchListener { v: View?, event: MotionEvent ->
            if (rowToggleBtn.isActivated) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    val scaleX: ObjectAnimator =
                        ObjectAnimator.ofFloat(v, "scaleX", 1f, ROW_TOGGLE_BUTTON_PRESSED_SCALE)
                    val scaleY: ObjectAnimator =
                        ObjectAnimator.ofFloat(v, "scaleY", 1f, ROW_TOGGLE_BUTTON_PRESSED_SCALE)
                    animatorSet = AnimatorSet()
                    animatorSet.playTogether(scaleX, scaleY)
                    animatorSet.setDuration(125)
                    animatorSet.start()
                } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    val scaleX: ObjectAnimator =
                        ObjectAnimator.ofFloat(v, "scaleX", ROW_TOGGLE_BUTTON_PRESSED_SCALE, 1f)
                    val scaleY: ObjectAnimator =
                        ObjectAnimator.ofFloat(v, "scaleY", ROW_TOGGLE_BUTTON_PRESSED_SCALE, 1f)
                    animatorSet = AnimatorSet()
                    animatorSet.playTogether(scaleX, scaleY)
                    animatorSet.setDuration(125)
                    animatorSet.start()
                }
            }
            false
        }
    }

    private fun setRowToggleButtonSwitched(rowToggleBtn: LinearLayout, switched: Boolean) {
        try {
            if (switched) {
                (rowToggleBtn.getChildAt(0) as TextView).setTextColor(getResources().getColor(R.color.colorAccent))
                rowToggleBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)))
                (rowToggleBtn.getChildAt(1) as ImageView).drawable.setColorFilter(
                    getResources().getColor(
                        R.color.colorAccent
                    ), PorterDuff.Mode.SRC_IN
                )
            } else {
                (rowToggleBtn.getChildAt(0) as TextView).setTextColor(getResources().getColor(R.color.disable_button))
                rowToggleBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.disable_button)))
                (rowToggleBtn.getChildAt(1) as ImageView).drawable.setColorFilter(
                    getResources().getColor(
                        R.color.disable_button
                    ), PorterDuff.Mode.SRC_IN
                )
            }
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
    }

    private fun setExamsListClicks() {
        addExamButton.setOnClickListener(View.OnClickListener { v: View? ->
            showMainViewLayout(
                MainView.ExamSetup
            )
        })
        startedExamsButton.setOnClickListener(View.OnClickListener { v: View? -> showCurrentExamView() })
        finishedExamsButton.setOnClickListener(View.OnClickListener { v: View? -> showRecentExamsView() })
    }

    private fun showSplashScreen() {
        showMainViewLayout(MainView.SplashScreen)
        setupDraftPenStrokeColor()
        val appIconScaleX: ObjectAnimator = ObjectAnimator.ofFloat(appIcon, "scaleX", 0.5f, 1.1f)
        val appIconScaleY: ObjectAnimator = ObjectAnimator.ofFloat(appIcon, "scaleY", 0.5f, 1.1f)
        val appIconAlpha: ObjectAnimator = ObjectAnimator.ofFloat(appIcon, "alpha", 0f, 1f)
        val saltechIconScaleX: ObjectAnimator =
            ObjectAnimator.ofFloat(splashSalTechImg, "scaleX", 0.5f, 1.1f)
        saltechIconScaleX.setStartDelay(100)
        val saltechIconScaleY: ObjectAnimator =
            ObjectAnimator.ofFloat(splashSalTechImg, "scaleY", 0.5f, 1f)
        saltechIconScaleY.setStartDelay(100)
        val saltechIconAlpha: ObjectAnimator =
            ObjectAnimator.ofFloat(splashSalTechImg, "alpha", 0f, 1f)
        saltechIconAlpha.setStartDelay(100)
        animatorSet = AnimatorSet()
        animatorSet.playTogether(
            appIconAlpha,
            appIconScaleX,
            appIconScaleY,
            saltechIconAlpha,
            saltechIconScaleX,
            saltechIconScaleY
        )
        animatorSet.setStartDelay(500)
        animatorSet.setDuration(400)
        animatorSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
            }

            override fun onAnimationEnd(animation: Animator) {
                Handler().postDelayed({
                    appTitle.visibility = View.VISIBLE
                    Handler().postDelayed({ this@MainActivity.hideAppTitleAnimation() }, 100)
                }, 100)
            }

            override fun onAnimationCancel(animation: Animator) {
            }

            override fun onAnimationRepeat(animation: Animator) {
            }
        })
        animatorSet.start()
    }

    private fun setupDraftPenStrokeColor() {
        if (examDraftPages.childCount >= 1) {
            if (selectedColor == 0) {
                if (checkDarkModeTurnedOn()) {
                    isPdfNightModeEnabled = true
                    selectedColor = PEN_STROKE_COLORS[8]
                } else {
                    selectedColor = PEN_STROKE_COLORS[7]
                }
            }
            examDraftPage = examDraftPages.getChildAt(currentDraftViewIndex) as CanvasView
            if (examDraftPage != null) examDraftPage.paintStrokeColor = selectedColor
        }
    }

    private fun checkDarkModeTurnedOn(): Boolean {
        var hasDarkMode = false
        val nightModeFlags: Int =
            getResources().configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        when (nightModeFlags) {
            Configuration.UI_MODE_NIGHT_YES -> hasDarkMode = true
            Configuration.UI_MODE_NIGHT_NO -> hasDarkMode = false
            Configuration.UI_MODE_NIGHT_UNDEFINED -> hasDarkMode = false
        }
        return hasDarkMode
    }

    private fun hideAppTitleAnimation() {
        val nAppIconScaleX: ObjectAnimator = ObjectAnimator.ofFloat(appIcon, "scaleX", 1.1f, 1f)
        val nAppIconScaleY: ObjectAnimator = ObjectAnimator.ofFloat(appIcon, "scaleY", 1.1f, 1f)
        animatorSet = AnimatorSet()
        animatorSet.playTogether(nAppIconScaleX, nAppIconScaleY)
        animatorSet.setStartDelay(1000)
        animatorSet.setDuration(200)
        animatorSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                Handler().postDelayed({ appTitle.visibility = View.GONE }, 500)
            }

            override fun onAnimationEnd(animation: Animator) {
                splashAnim.setVisibility(View.VISIBLE)
                Handler().postDelayed({ this@MainActivity.playSplashAnimation() }, 100)
            }

            override fun onAnimationCancel(animation: Animator) {
            }

            override fun onAnimationRepeat(animation: Animator) {
            }
        })
        animatorSet.start()
    }

    private fun playSplashAnimation() {
        vibrator.vibrate(
            longArrayOf(
                0,
                VIBRATE_SPLASH.toLong(),
                VIBRATE_SPLASH_SNOOZE.toLong(),
                VIBRATE_SPLASH.toLong(),
                VIBRATE_SPLASH_SNOOZE.toLong(),
                VIBRATE_SPLASH.toLong(),
                VIBRATE_SPLASH_SNOOZE.toLong(),
                VIBRATE_SPLASH.toLong()
            ), -1
        )
        splashAnim.playAnimation()
        splashAnim.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
            }

            override fun onAnimationEnd(animation: Animator) {
                launchApp(90)
            }

            override fun onAnimationCancel(animation: Animator) {
            }

            override fun onAnimationRepeat(animation: Animator) {
                if (repeatCount >= 0) {
                    splashAnim.cancelAnimation()
                } else {
                    repeatIconAnimationPlay()
                    repeatCount++
                }
            }
        })
    }

    private fun repeatIconAnimationPlay() {
        val rpAppIconScaleX: ObjectAnimator =
            ObjectAnimator.ofFloat(appIcon, "scaleX", 1f, 1.05f, 1f)
        val rpAppIconScaleY: ObjectAnimator =
            ObjectAnimator.ofFloat(appIcon, "scaleY", 1f, 1.05f, 1f)
        animatorSet = AnimatorSet()
        animatorSet.playTogether(rpAppIconScaleX, rpAppIconScaleY)
        animatorSet.setDuration(150)
        animatorSet.start()
    }

    private fun showOptionsMenu() {
        clickableArea.visibility = View.VISIBLE
        menuItemsLayout.visibility = View.VISIBLE
        //ObjectAnimator tapsellAlphaAnim = ObjectAnimator.ofFloat(standardTapsellBanner, "alpha", 1f, 0f);
        val mainLayoutScaleXAnim: ObjectAnimator =
            ObjectAnimator.ofFloat(recentExamsLayout, "scaleX", 1.015f, 0.9f)
        val mainLayoutScaleYAnim: ObjectAnimator =
            ObjectAnimator.ofFloat(recentExamsLayout, "scaleY", 1.015f, 0.9f)
        val mainLayoutTranslationYAnim: ObjectAnimator =
            ObjectAnimator.ofFloat(recentExamsLayout, "translationY", 1f, 120f)
        val closeMenuButtonShowAnim: ValueAnimator = ValueAnimator.ofFloat(0f, 360f)
        closeMenuButtonShowAnim.addUpdateListener { valueAnimator1: ValueAnimator ->
            val rotationValue = valueAnimator1.getAnimatedValue() as Float
            appMoreOptions.rotation = rotationValue
            if (rotationValue >= 180) {
                appMoreOptions.setImageResource(R.drawable.close_menu)
            }
        }
        animatorSet = AnimatorSet()
        animatorSet.playTogether(
            closeMenuButtonShowAnim,
            mainLayoutScaleXAnim,
            mainLayoutScaleYAnim,
            mainLayoutTranslationYAnim
        )
        animatorSet.setStartDelay(50)
        animatorSet.setDuration(400)
        animatorSet.addListener(object : Animator.AnimatorListener {
            @SuppressLint("UseCompatLoadingForDrawables")
            override fun onAnimationStart(animator: Animator) {
                recentExamsLayout.setPadding(0, 0, 0, 0)
                recentExamsLayout.background =
                    getResources().getDrawable(R.drawable.recent_layout_collapsed)
                recentExamsLayout.elevation = 30f
            }

            override fun onAnimationEnd(animator: Animator) {
                setOptionItems()
                optionsMenuOpened = true
            }

            override fun onAnimationCancel(animator: Animator) {
            }

            override fun onAnimationRepeat(animator: Animator) {
            }
        })
        animatorSet.start()
    }

    private fun setOptionItems() {
        val settings: ImageButton = menuItemsLayout.findViewById<ImageButton>(R.id.app_settings)
        settings.setOnClickListener(View.OnClickListener { v: View? ->
            dismissOptionsMenu()
            val collapsablePanel: CollapsablePanelFragment = CollapsablePanelFragment()
            collapsablePanel.setContentFragment(SettingsFragment(collapsablePanel))
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, collapsablePanel).addToBackStack(null).commit()
        })
    }

    private fun dismissOptionsMenu() {
        //ObjectAnimator tapsellAlphaAnim = ObjectAnimator.ofFloat(standardTapsellBanner, "alpha", 0f, 1f);
        val mainLayoutScaleXAnim: ObjectAnimator =
            ObjectAnimator.ofFloat(recentExamsLayout, "scaleX", 0.9f, 1.015f)
        val mainLayoutScaleYAnim: ObjectAnimator =
            ObjectAnimator.ofFloat(recentExamsLayout, "scaleY", 0.9f, 1.015f)
        val mainLayoutTranslationYAnim: ObjectAnimator =
            ObjectAnimator.ofFloat(recentExamsLayout, "translationY", 120f, 1f)
        val closeMenuButtonShowAnim: ValueAnimator = ValueAnimator.ofFloat(360f, 0f)
        closeMenuButtonShowAnim.addUpdateListener { valueAnimator1: ValueAnimator ->
            val rotationValue = valueAnimator1.getAnimatedValue() as Float
            appMoreOptions.rotation = rotationValue
            if (rotationValue <= 180) {
                appMoreOptions.setImageResource(R.drawable.more_app_options)
            }
        }
        animatorSet = AnimatorSet()
        animatorSet.playTogether(
            closeMenuButtonShowAnim,
            mainLayoutScaleXAnim,
            mainLayoutScaleYAnim,
            mainLayoutTranslationYAnim
        )
        animatorSet.setStartDelay(50)
        animatorSet.setDuration(400)
        animatorSet.addListener(object : Animator.AnimatorListener {
            @SuppressLint("UseCompatLoadingForDrawables")
            override fun onAnimationStart(animator: Animator) {
                recentExamsLayout.background =
                    getResources().getDrawable(R.drawable.recent_layout_collapsed)
                if (recentExamsLayout.elevation != 0f) {
                    recentExamsLayout.elevation = 30f
                }
                menuItemsLayout.visibility = View.GONE
            }

            @SuppressLint("UseCompatLoadingForDrawables")
            override fun onAnimationEnd(animator: Animator) {
                recentExamsLayout.elevation = 0f
                recentExamsLayout.setPadding(
                    0,
                    getResources().getDimension(R.dimen.status_bar_margin).toInt(),
                    0,
                    0
                )
                //recentExamsLayout.setScaleX(1f);
                //recentExamsLayout.setScaleY(1f);
                recentExamsLayout.background = getResources().getDrawable(R.color.background_color)
                clickableArea.visibility = View.GONE
                optionsMenuOpened = false
            }

            override fun onAnimationCancel(animator: Animator) {
            }

            override fun onAnimationRepeat(animator: Animator) {
            }
        })
        animatorSet.start()
    }

    private fun deleteAddCourseButton() {
        var checkHasAddCourseButton = false
        var i1 = 0
        for (i in recentExams.getExamList().indices) {
            val en: ExamName = recentExams.getExamList().get(i).getExamName(0)!!
            if (en != null) {
                if (en.getName() == ADD_NEW_EXAM_BUTTON_TITLE) {
                    checkHasAddCourseButton = true
                    i1 = i
                    break
                }
            }
        }
        if (checkHasAddCourseButton) {
            recentExams.removeExam(i1)
            Saver.Companion.getInstance(this@MainActivity).saveRecentExams(recentExams)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showExamsView(
        viewType: Int,
        list: MutableList<Exam>,
        examListener: ExamSelectedListener,
        listSize: Int
    ) {
        deleteAddCourseButton()
        examsViewAdapter = ExamsViewAdapter(viewType, list, examListener, this@MainActivity)
        examsViewTitle.text = getExamsView(viewType)
        if (listSize != 0) {
            sortExamsByDate(list)
            examsViewEmpty.visibility = View.GONE
        } else {
            examsViewEmpty.visibility = View.VISIBLE
        }
        try {
            examsView.setLayoutManager(
                GridLayoutManager(
                    this@MainActivity,
                    getResources().getInteger(R.integer.exams_view_span_count)
                )
            )
            examsView.setAdapter(examsViewAdapter)
            examsView.requestFocus()
        } catch (e: Exception) {
            e.printStackTrace()
            examsViewAdapter.notifyDataSetChanged()
        }
        showExamsViewAnimation()
    }

    private fun showRecentExamsView() {
        recentExamsList = ArrayList<Exam>()
        for (o in recentExams.getExamList().indices) {
            val exam: Exam = recentExams.getExamList().get(o)
            if (!exam.isStarted && !exam.isCreating && !exam.isCorrecting && !exam.isSuspended) {
                var isExamConflict = false
                for (rExam in recentExamsList) {
                    if (rExam.id == exam.id && rExam.getExamName(0)!!
                            .getName() == exam.getExamName(0)!!.getName()
                    ) {
                        isExamConflict = true
                        break
                    }
                }
                if (!isExamConflict) {
                    recentExamsList.add(exam)
                } else {
                    recentExams.removeExam(o)
                    Saver.Companion.getInstance(this@MainActivity).saveRecentExams(recentExams)
                }
            }
        }
        val recentExamsListener: ExamSelectedListener = object : ExamSelectedListener {
            override fun onExamDeleted(exam: Exam?, position: Int, side: String) {
                recentExams = Saver.Companion.getInstance(this@MainActivity).loadRecentExams()
                if (recentExamsList.size == 0) {
                    examsViewEmpty.visibility = View.VISIBLE
                } else {
                    examsViewEmpty.visibility = View.GONE
                }
                setExamsFeatures()
                if (side == SIDE_RECENT_EXAMS) {
                    showRecentExamsView()
                }
            }

            override fun onExamResumed(exam: Exam) {
                prepareExamForResume(exam)
            }

            override fun onExamSuspended(exam: Exam?) {
            }

            override fun onExamClicked(exam: Exam, side: String) {
                if (side == SIDE_RECENT_EXAMS) prepareExamForResume(exam)
            }

            override fun onAddExamWanted() {
            }

            override fun onExamEdited(exam: Exam?) {
                showRecentExams()
            }
        }
        showExamsView(
            ExamViewHolder.Companion.TEMPLATE_VIEW_FINISHED_EXAMS,
            recentExamsList,
            recentExamsListener,
            recentExamsList.size
        )
    }

    private fun showRecentExams() {
        showMainViewLayout(MainView.RecentExams)
        appMoreOptions.setVisibility(View.VISIBLE)
        //measurableViewForSearchBox.setVisibility(VISIBLE);
        setExamsFeatures()
        //setCoursesScores();
    }

    private fun setExamsFeatures() {
        for (e in recentExams.getExamList()) {
            examFeatures = ""
            if (e.startExamTime != null) {
                setExamFeatures(FILTER_START_TIME, e)
            }
            if (!e.isSelectQuestionsManually) {
                setExamFeatures(FILTER_QUESTIONS_NUMBER, e)
            }
            if (e.isStarted && !e.isSuspended) {
                setExamFeatures(FILTER_EXAM_RUNNING, e)
            } else if (!e.isStarted && e.isCreating && !e.isSuspended) {
                setExamFeatures(FILTER_EXAM_CREATING, e)
            } else if (!e.isStarted && e.isSuspended) {
                setExamFeatures(FILTER_EXAM_SUSPENDED, e)
            } else if (!e.isStarted && !e.isSuspended && e.isCorrecting) {
                setExamFeatures(FILTER_EXAM_CORRECTING, e)
            } else if (!e.isStarted && !e.isSuspended && !e.isCorrecting) {
                setExamFeatures(FILTER_EXAM_ENDED, e)
            }
            Log.d("TAG", "EDRO: TILE: " + e.getExamName(0) + " OI: " + e.features)
        }
        Saver.Companion.getInstance(this@MainActivity).saveRecentExams(recentExams)
    }

    private fun setExamFeatures(feature: String, exam: Exam) {
        examFeatures += feature + FILTERS_DELIMITERS
        exam.features = (examFeatures.trim { it <= ' ' })
    }

    private fun showCurrentExamView() {
        currentExams = ArrayList<Exam>()
        for (o in recentExams.getExamList().indices) {
            val exam: Exam = recentExams.getExamList().get(o)
            if (exam.isStarted && !exam.isCreating && !exam.isSuspended) {
                var isExamConflict = false
                for (cExam in currentExams) {
                    if (cExam == exam) {
                        isExamConflict = true
                        break
                    }
                }
                if (!isExamConflict) {
                    currentExams.add(exam)
                } else {
                    recentExams.removeExam(o)
                    Saver.Companion.getInstance(this@MainActivity).saveRecentExams(recentExams)
                }
            }
        }
        val startedExamsListener: ExamSelectedListener = object : ExamSelectedListener {
            override fun onExamDeleted(exam: Exam?, position: Int, side: String) {
                recentExams = Saver.Companion.getInstance(this@MainActivity).loadRecentExams()
                setExamsFeatures()
                if (currentExams.size == 0) {
                    examsViewEmpty.visibility = View.VISIBLE
                } else {
                    examsViewEmpty.visibility = View.GONE
                }
                if (side == SIDE_CURRENT_EXAMS) {
                    showCurrentExamView()
                }
            }

            override fun onExamResumed(exam: Exam) {
                prepareExamForResume(exam)
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onExamSuspended(e: Exam?) {
                suspendAnExam(currentExams)
                if (currentExams.size == 0) {
                    examsViewEmpty.visibility = View.VISIBLE
                } else {
                    examsViewEmpty.visibility = View.GONE
                }
            }

            override fun onExamClicked(exam: Exam, side: String) {
                if (side == SIDE_CURRENT_EXAMS) {
                    prepareExamForResume(exam)
                }
            }

            override fun onAddExamWanted() {
            }

            override fun onExamEdited(exam: Exam?) {
            }
        }
        showExamsView(
            ExamViewHolder.Companion.TEMPLATE_VIEW_RUNNING_EXAMS,
            currentExams,
            startedExamsListener,
            currentExams.size
        )
    }

    @Contract(pure = true)
    private fun prepareExamForResume(exam: Exam) {
        currentExam = exam
        if (currentExam.isStarted && currentExam.examStatus != Exam.ExamStatus.Started) currentExamStatus =
            Exam.ExamStatus.Creating
        currentExam.examStatus = (currentExamStatus)
        recentExams.updateCurrentExam(currentExam)
        addExamButton.setEnabled(false)
        isRecentExamLoaded = true
        runningCategory = currentExam.runningCategory
        examFile = currentExam.examFile
        for (question in currentExam.answerSheet!!.questions!!) {
            question.isNowSelected = (false)
        }
        isStartedManualExam = !currentExam.isCreating
        isExamStoppedManually = currentExam.isExamStoppedManually
        if (currentExam.isUsedTiming) {
            var examTimeLi: Long =
                if (!currentExam.isSuspended && !currentExam.isEditingCategoryTimes) {
                    currentExam.examTimeLeft
                } else {
                    currentExam.examTime
                }
            examTime = examTimeLi
            minute = examTimeLi / 60000
            examTimeLi %= 60000
            second = examTimeLi / 1000
        }
        startedExamTime = currentExam.startExamTime
        canUsingAdditionalSubtraction = currentExam.hasAdditionalScore
        /*useChronometer.isChecked = (currentExam.isUsedChronometer());
        useCategorize.isChecked = (currentExam.isUsedCategorize());
        canCalculateTimeForCategory.isChecked = (currentExam.isCanCalculateTimeForCategory());
        if (currentExam.isUsedCorrection()) {
            useAdditionalSubtraction.isChecked = (currentExam.isHasAdditionalScore());
            canUsingAdditionalSubtraction = currentExam.isHasAdditionalScore();
        }*/
        //useCalculateScoreOfCategory.isChecked = (currentExam.isCanCalculateScoreOfCategory());
        currentExamName = currentExam.getExamName(0)
        currentExam.secondsOfThinkingOnQuestion = (0)
        recentExams.updateCurrentExam(currentExam)
        Saver.Companion.getInstance(this@MainActivity).saveRecentExams(recentExams)
        if (!currentExam.isCreating && currentExam.isStarted) {
            examAction.setImageResource(R.drawable.done_select_questions)
        }
        firstQuestion = currentExam.getQuestionRange().firstQuestion
        lastQuestion = currentExam.getQuestionRange().lastQuestion
        questionsCount = currentExam.getQuestionRange().questionsCount
        questionsCPattern = currentExam.getQuestionRange().countPattern
        chronoThreshold = currentExam.chornoThreshold
        useChronometer = chronoThreshold != 0
        useExamCategorize = currentExam.isUsedCategorize
        useCategoryTiming = currentExam.isCanCalculateTimeForCategory
        useCategoryScore = currentExam.isCanCalculateScoreOfCategory
        correctedAsNow = currentExam.isChecked
        correctionMode = currentExam.correctionMode!!
        currentExamStatus = currentExam.examStatus
        if (!currentExam.isSuspended) {
            try {
                questions = currentExam.answerSheet!!.questions!!
                Saver.Companion.getInstance(this@MainActivity)
                    .saveQuestions(currentExam.answerSheet)
                var hasBookmarkQuestion = false
                for (question in questions) {
                    if (question.getBookmark().name != Bookmark.Companion.NONE) {
                        hasBookmarkQuestion = true
                        break
                    }
                }
                if (hasBookmarkQuestion) showBookmarkedQuestions()
            } catch (e: Exception) {
                Toast.Companion.makeText(
                    this@MainActivity,
                    "⚠️ ناتوان در بارگذاری سؤالات آزمون!",
                    Toast.Companion.LENGTH_SHORT
                ).show()
                e.printStackTrace()
            }
        } else {
            questions = ArrayList<Question>()
            val questionsQ: Questions = Questions()
            val qq: MutableList<Question> = ArrayList<Question>()
            for (e in currentExam.answerSheet!!.questions!!) {
                e.isWhite = (true)
                e.category = (null)
                e.setBookmark(Bookmark(Bookmark.Companion.NONE))
                e.timeOfThinking = (0)
                e.selectedChoice = (0)
                e.isCorrect = (false)
                e.correctAnswerChoice = (0)
                e.isSelected = (false)
                qq.add(e)
            }
            questionsQ.questions = (qq)
            questionsQ.categories = (ArrayList<Category>())
            currentExam.runningCategory = (-1)
            runningCategory = -1
            currentExam.answerSheet = (questionsQ)
            currentExam.lastScrollPosition = (0)
            updateRecentExams()
            Saver.Companion.getInstance(this@MainActivity).saveQuestions(questionsQ)
            //prepareAnswerSheet();
        }
        if (currentExam.isSuspended) {
            startedExamTime = null
            currentExam.startExamTime = (null)
            currentExam.isSuspended = (false)
            currentExam.isStarted = (true)
            currentExam.isEditingCategoryTimes = (true)
            startedTimeExam = false
            recentExams.updateCurrentExam(currentExam)
            Saver.Companion.getInstance(this@MainActivity).saveRecentExams(recentExams)
        }
        setupExam(750)
        endTheLastExamLoading()
    }

    private fun showBookmarkedQuestions() {
        // TODO: Setup this...
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun suspendAnExam(currentExams: MutableList<Exam>?) {
        recentExams = Saver.Companion.getInstance(this@MainActivity).loadRecentExams()
        suspendedExamsList.clear()
        for (o in recentExams.getExamList().indices) {
            val exam: Exam = recentExams.getExamList().get(o)
            if (!exam.isStarted && exam.isSuspended) {
                var isExamConflict = false
                for (sExam in suspendedExamsList) {
                    if (sExam.id == exam.id && sExam.getExamName() == exam.getExamName()) {
                        isExamConflict = true
                        break
                    }
                }
                if (!isExamConflict) {
                    suspendedExamsList.add(exam)
                } else {
                    recentExams.removeExam(o)
                    Saver.Companion.getInstance(this@MainActivity).saveRecentExams(recentExams)
                }
            }
        }
        Saver.Companion.getInstance(this@MainActivity).saveRecentExams(recentExams)
        examsViewAdapter.notifyDataSetChanged()
        currentExams!!.clear()
        for (o in recentExams.getExamList().indices) {
            val exam: Exam = recentExams.getExamList().get(o)
            if ((exam.isStarted || exam.isCreating) && !exam.isSuspended) {
                var isExamConflict = false
                for (cExam in currentExams) {
                    if (cExam.id == exam.id && cExam.getExamName() == exam.getExamName()) {
                        isExamConflict = true
                        break
                    }
                }

                if (!isExamConflict) {
                    currentExams.add(exam)
                } else {
                    recentExams.removeExam(o)
                    Saver.Companion.getInstance(this@MainActivity).saveRecentExams(recentExams)
                }
            }
        }
        sortExamsByDate(currentExams)
        examsViewAdapter.notifyDataSetChanged()
        setExamsFeatures()
    }

    private fun setBlurContainer(container: ViewGroup?) {
        Blurry.with(this@MainActivity).sampling(5).radius(25).onto(container)
    }

    private fun hideBlurContainer(container: ViewGroup?) {
        Blurry.delete(container)
    }

    private fun sortExamsByDate(exams: List<Exam>?) {
        try {
            Collections.sort<Exam>(exams, java.util.Comparator<Exam> { e1: Exam, e2: Exam ->
                val d1P: Array<String> =
                    e1.startExamTime?.split(" ".toRegex())!!.dropLastWhile { it.isEmpty() }
                        .toTypedArray()
                val d2P: Array<String> =
                    e2.startExamTime?.split(" ".toRegex())!!.dropLastWhile { it.isEmpty() }
                        .toTypedArray()
                val d1 = d1P[1] + " " + d1P[0]
                val d2 = d2P[1] + " " + d2P[0]
                Log.d("TAG", "Date 1 : $d1")
                Log.d("TAG", "Date 2 : $d2")
                val date1 = Date(
                    d1.substring(0, 4).toInt(),
                    d1.substring(5, 7).toInt(),
                    d1.substring(8, 10).toInt(),
                    d1.substring(11, 13).toInt(),
                    d1.substring(14, 16).toInt()
                )
                val date2 = Date(
                    d2.substring(0, 4).toInt(),
                    d2.substring(5, 7).toInt(),
                    d2.substring(8, 10).toInt(),
                    d2.substring(11, 13).toInt(),
                    d2.substring(14, 16).toInt()
                )
                date1.compareTo(date2)
            })
        } catch (e: NullPointerException) {
            e.printStackTrace()
            Toast.Companion.makeText(
                this@MainActivity,
                "در حین مرتب سازی فهرست آزمون ها، خطایی رخ داد!",
                Toast.Companion.WARNING_SIGN,
                Toast.Companion.LENGTH_LONG
            ).show()
        }
    }

    private fun showExamsViewAnimation() {
        val examsViewCardScaleX: ObjectAnimator =
            ObjectAnimator.ofFloat(examsViewCard, "scaleX", 0.5f, 1f)
        val examsViewCardScaleY: ObjectAnimator =
            ObjectAnimator.ofFloat(examsViewCard, "scaleY", 0.5f, 1f)
        val examsViewCardAlpha: ObjectAnimator =
            ObjectAnimator.ofFloat(examsViewCard, "alpha", 0f, 1f)
        animatorSet = AnimatorSet()
        animatorSet.playTogether(examsViewCardScaleX, examsViewCardScaleY, examsViewCardAlpha)
        animatorSet.setDuration(EXAMS_LIST_ANIMATION_DURATION.toLong())
        animatorSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animator: Animator) {
                examsListBack.isClickable = false
                setBlurContainer(recentExamsLayout)
                examsViewLayout.visibility = View.VISIBLE
            }

            override fun onAnimationEnd(animator: Animator) {
                examsListBack.isClickable = true
            }

            override fun onAnimationCancel(animator: Animator) {
            }

            override fun onAnimationRepeat(animator: Animator) {
            }
        })
        animatorSet.start()
    }

    private fun hideExamsListAnimation() {
        val examsViewCardScaleX: ObjectAnimator =
            ObjectAnimator.ofFloat(examsViewCard, "scaleX", 1f, 0.5f)
        val examsViewCardScaleY: ObjectAnimator =
            ObjectAnimator.ofFloat(examsViewCard, "scaleY", 1f, 0.5f)
        val examsViewCardAlpha: ObjectAnimator =
            ObjectAnimator.ofFloat(examsViewCard, "alpha", 1f, 0f)
        animatorSet = AnimatorSet()
        animatorSet.playTogether(examsViewCardScaleX, examsViewCardScaleY, examsViewCardAlpha)
        animatorSet.setDuration(EXAMS_LIST_ANIMATION_DURATION.toLong())
        animatorSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animator: Animator) {
                examsListBack.isClickable = false
                hideBlurContainer(recentExamsLayout)
            }

            override fun onAnimationEnd(animator: Animator) {
                examsViewLayout.visibility = View.GONE
            }

            override fun onAnimationCancel(animator: Animator) {
            }

            override fun onAnimationRepeat(animator: Animator) {
            }
        })
        animatorSet.start()
    }

    private fun getExamsView(viewType: Int): String? {
        var examTitle: String? = null
        when (viewType) {
            ExamViewHolder.Companion.TEMPLATE_VIEW_RUNNING_EXAMS -> examTitle =
                getString(R.string.started_exams_title)

            ExamViewHolder.Companion.TEMPLATE_VIEW_SUSPENDED_EXAMS -> examTitle =
                getString(R.string.suspended_exams_title)

            ExamViewHolder.Companion.TEMPLATE_VIEW_FINISHED_EXAMS -> examTitle =
                getString(R.string.finished_exams_title)

            ExamViewHolder.Companion.TEMPLATE_VIEW_CREATING_EXAMS -> examTitle =
                getString(R.string.creating_exams_title)

            ExamViewHolder.Companion.TEMPLATE_VIEW_CORRECTING_EXAMS -> examTitle =
                getString(R.string.correcting_exams_title)

            else -> {}
        }
        return examTitle
    }

    private fun init() {
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        recentExams = Saver.Companion.getInstance(this).loadRecentExams()
        splashScreenLayout = findViewById<ConstraintLayout>(R.id.splash_screen_layout)
        examRunningLayout = findViewById<ConstraintLayout>(R.id.exam_running_layout)
        examSetupLayout = findViewById<ConstraintLayout>(R.id.exam_setup_layout)
        backToMainView = findViewById<ImageButton>(R.id.back_to_main_view_layout)
        selectExamImmediately = findViewById<ConstraintLayout>(R.id.select_exam_immediately)
        createExamLayoutContainer = findViewById<ScrollView>(R.id.create_exam_layout)
        // Row 1 | Static Functions | Main Parameters
        examAction = findViewById<ImageButton>(R.id.exam_action)
        standardTapsellBanner = findViewById<RelativeLayout>(R.id.standard_tapsell_banner)
        openDraftBox = findViewById<ImageButton>(R.id.open_draft_board)
        jumpToQuestion = findViewById<ImageButton>(R.id.jump_to_question)
        // Row 2 | Dynamic Functions | Secondary Parameters
        openExamFileBox = findViewById<ImageButton>(R.id.open_exam_doc)
        addedBookmarksButton = findViewById<ImageButton>(R.id.show_added_bookmarks)
        addQuestionButton = findViewById<ImageButton>(R.id.add_question)
        removeQuestionButton = findViewById<ImageButton>(R.id.remove_question)
        resetChronometer = findViewById<ImageButton>(R.id.reset_chronometer)
        shareWorksheetButton = findViewById<ImageButton>(R.id.share_worksheet)
        enableNegativePoint = findViewById<ImageButton>(R.id.enable_negative_point)
        // ....... End of Rows .......
        answerSheetLayout = findViewById<ConstraintLayout>(R.id.answer_sheet_layout)
        collapseExamHeader = findViewById<ImageButton>(R.id.collapse_exam_control_panel)
        collapseExamTimeBar = findViewById<RoundCornerProgressBar>(R.id.collapse_exam_time_bar)
        createCustomExam = findViewById<LinearLayout>(R.id.add_custom_exam)
        shortcutExamPreparingLayout = findViewById<ConstraintLayout>(R.id.exam_loading_layout)
        debugVerWatermark = findViewById<TextView>(R.id.debug_ver_watermark)
        enterToExamRoom = findViewById<Button>(R.id.enter_to_exam_button)
        userDashboardButton = findViewById<ImageButton>(R.id.edu_dashboard_button)
        userDashboardLayout = findViewById<ConstraintLayout>(R.id.user_dashboard_layout)
        examAnimView = findViewById<LottieAnimationView>(R.id.exam_background_anim)
        examPictureView = findViewById<ImageView>(R.id.exam_background_image)
        examNameText = findViewById<TextView>(R.id.exam_name)
        examTimeBar = findViewById<CircularProgressBar>(R.id.exam_time_rem_bar)
        examTimeBoard = findViewById<RollingTextView>(R.id.exam_time_seconds)
        selectChronometerEnabled = findViewById<LinearLayout>(R.id.select_chronometer_enabled)
        startCurrentExam = findViewById<Button>(R.id.start_exam_button)
        runningExamOptions = findViewById<LinearLayout>(R.id.running_exam_options)
        examControlPanel = findViewById<BlurView>(R.id.exam_control_panel)
        examAnswerSheetEmptyError = findViewById<BlurView>(R.id.answersheet_empty)
        examDraftPages = findViewById<FrameLayout>(R.id.draft_view_layout)
        addDraftPage = findViewById<ImageButton>(R.id.add_draft_page)
        draftPagesOptions = findViewById<ImageButton>(R.id.draft_pages_options)
        examDraftLayout = findViewById<BlurView>(R.id.draft_page_layout)
        draftViewOptions = findViewById<ImageButton>(R.id.draft_page_tools)
        collapseDraftView = findViewById<ImageButton>(R.id.collapse_draft_page)
        draftDrawingHint = findViewById<TextView>(R.id.draft_drawing_hint)
        answerSheetView = findViewById<RecyclerView>(R.id.answersheet_view)
        startExamButtonsLayout = findViewById<LinearLayout>(R.id.start_exam_control_layout)
        recentExamsScrollContainer =
            findViewById<ScrollView>(R.id.recent_exams_layout_scroll_container)
        selectCategoryEnabled = findViewById<LinearLayout>(R.id.select_category_enabled)
        useCategoryScoreEnabled = findViewById<LinearLayout>(R.id.category_correction_enable)
        useCategoryTimingEnabled = findViewById<LinearLayout>(R.id.category_timing_enable)
        setupExamTimeColon = findViewById<TextView>(R.id.setup_exam_time_colon)
        scheduleCurrentExam = findViewById<ImageButton>(R.id.schedule_exam_start)
        splashAnim = findViewById<LottieAnimationView>(R.id.splash_icon_anim)
        minuteNP = findViewById<NumberPicker>(R.id.minute_number_picker)
        secondNP = findViewById<NumberPicker>(R.id.second_number_picker)
        selectExamName = findViewById<LinearLayout>(R.id.select_exam_name)
        selectExamDocument = findViewById<LinearLayout>(R.id.attach_exam_file)
        selectQuestionsRandomly = findViewById<LinearLayout>(R.id.select_question_randomly)
        questionsCPatternText = findViewById<TextInputLayout>(R.id.questions_count_pattern_text)
        firstQuestionNoText = findViewById<TextInputLayout>(R.id.first_question_no_text)
        lastQuestionNoText = findViewById<TextInputLayout>(R.id.last_question_no_text)
        questionsCountText = findViewById<TextInputLayout>(R.id.questions_count_text)
        chronoThresholdNP = findViewById<NumberPicker>(R.id.chrono_thre_number_picker)
        splashSalTechImg = findViewById<ImageView>(R.id.splash_saltech_img)
        appIcon = findViewById<ImageView>(R.id.splash_app_icon)
        appTitle = findViewById<TextView>(R.id.splash_app_title)
        selectQuestionsMode = findViewById<LinearLayout>(R.id.select_questions_mode)
        selectCorrectionMode = findViewById<LinearLayout>(R.id.select_correction_mode)
        parentLayout = findViewById<ConstraintLayout>(R.id.parent_layout)
        menuItemsLayout = findViewById<LinearLayout>(R.id.menu_items_layout)
        recentExamsLayout = findViewById<ConstraintLayout>(R.id.recent_exams_layout)
        appMoreOptions = findViewById<ImageButton>(R.id.app_more_options)
        clickableArea = findViewById<View>(R.id.clickable_area)
        addExamButton = findViewById<CardView>(R.id.add_exam_card)
        welcomeLayout = findViewById<ConstraintLayout>(R.id.welcome_to_app_layout)
        welcomeImage = findViewById<LottieAnimationView>(R.id.welcome_image)
        welcomeTitle = findViewById<TextView>(R.id.welcome_title)
        welcomeAppDesc = findViewById<TextView>(R.id.welcome_app_desc)
        welcomeClickContinue = findViewById<TextView>(R.id.welcome_click_continue)
        welcomeCompanyLogo = findViewById<ImageView>(R.id.welcome_company_logo)
        startedExamsButton = findViewById<CardView>(R.id.started_exams_card)
        suspendedExamsButton = findViewById<CardView>(R.id.suspended_exams_card)
        finishedExamsButton = findViewById<CardView>(R.id.finished_exams_card)
        creatingExamsButton = findViewById<CardView>(R.id.creating_exams_card)
        correctingExamButton = findViewById<CardView>(R.id.correcting_exams_card)
        examsViewLayout = findViewById<ConstraintLayout>(R.id.exams_list_view_layout)
        examsViewTitle = findViewById<TextView>(R.id.exams_list_title)
        examsViewCard = findViewById<CardView>(R.id.exams_list_card)
        examsListBack = findViewById<ImageButton>(R.id.exams_list_back)
        examsView = findViewById<RecyclerView>(R.id.exams_view)
        examsViewEmpty = findViewById<TextView>(R.id.exams_list_empty)
        chronoThresholdNP.isEnabled = false
    }

    @SuppressLint("NonConstantResourceId")
    override fun onToggleButtonPartClicked(tbPartId: Int) {
        when (tbPartId) {
            R.id.select_questions_auto -> {
                isQuestionsManually = false
                questionsCPatternText.isEnabled = true
                lastQuestionNoText.isEnabled = true
                firstQuestionNoText.isEnabled = true
                questionsCountText.isEnabled = true
                setEnabledRowButton(selectQuestionsRandomly, true, selectedQRandomly)
            }

            R.id.select_questions_manual -> {
                isQuestionsManually = true
                questionsCPatternText.isEnabled = false
                lastQuestionNoText.isEnabled = false
                firstQuestionNoText.isEnabled = false
                questionsCountText.isEnabled = false
                setEnabledRowButton(selectQuestionsRandomly, false, selectedQRandomly)
            }

            R.id.exam_correction_none -> {
                setEnabledRowButton(useCategoryScoreEnabled, false, useCategoryScore)
                correctionMode = Exam.CorrectionMode.None
            }

            R.id.exam_correction_normal -> {
                setEnabledRowButton(useCategoryScoreEnabled, true, useCategoryScore)
                correctionMode = Exam.CorrectionMode.Normal
            }

            R.id.exam_correction_keys -> {
                setEnabledRowButton(useCategoryScoreEnabled, true, useCategoryScore)
                correctionMode = Exam.CorrectionMode.Keys
            }

            else -> {}
        }
    }

    private fun setEnabledRowButton(row: LinearLayout, enabled: Boolean, checked: Boolean) {
        val disabledColor: Int = getResources().getColor(R.color.disable_button_fade)
        val enabledColor: Int = if (checked) {
            getResources().getColor(R.color.colorAccent)
        } else {
            getResources().getColor(R.color.disable_button)
        }
        row.isActivated = enabled
        for (i in 0 until row.childCount) {
            row.getChildAt(i).isActivated = enabled
        }
        if (enabled) {
            row.setBackgroundTintList(ColorStateList.valueOf(enabledColor))
            (row.getChildAt(1) as ImageView).drawable.setColorFilter(
                enabledColor,
                PorterDuff.Mode.SRC_IN
            )
            (row.getChildAt(0) as TextView).setTextColor(enabledColor)
        } else {
            row.setBackgroundTintList(ColorStateList.valueOf(disabledColor))
            (row.getChildAt(1) as ImageView).drawable.setColorFilter(
                disabledColor,
                PorterDuff.Mode.SRC_IN
            )
            (row.getChildAt(0) as TextView).setTextColor(disabledColor)
        }
    }

    override fun onDestroy() {
        hideTapsellAds()
        super.onDestroy()
    }

    private enum class CollapseBarMode {
        Collapse, Fullscreen, Both, None
    }

    private enum class CButtonState {
        Clicked, Idle, Disable
    }

    private enum class MainView {
        SplashScreen, WelcomePage, RecentExams, ExamRunning, ExamSetup, UserDashboard
    }

    enum class ColorBrightness {
        Lighten, Darken, Moderate
    }

    companion object {
        const val APPLICATION_PUBLISHER: Boolean = true // MyKet: True , CafeBazaar: False
        const val ADD_NEW_EXAM_BUTTON_TITLE: String = "add_exam"
        const val CATEGORY_ADDING_RECEIVER_RESULT: String = "category_adding_result"
        const val CATEGORY_ADDING_RECEIVER_INTENT: String = "category_adding_receiver"
        const val FREEZE_TIME: String = "freeze_time"
        const val CONTINUE_TIME: String = "continue_time"
        const val CATEGORY_ADDING_STATUS_CANCELED: String = "canceled"
        const val SIDE_RECENT_EXAMS: String = "side_recent_exams"
        const val SIDE_CREATING_EXAMS: String = "side_creating_exams"
        const val SIDE_CORRECTING_EXAMS: String = "side_correcting_exams"
        const val SIDE_SUSPENDED_EXAMS: String = "side_suspended_exams"
        const val SIDE_CURRENT_EXAMS: String = "side_current_exams"
        const val SIDE_SEARCH_EXAMS: String = "side_search_exams"
        const val APPLICATION_ID: Int = 9342
        const val MAX_OF_QUESTIONS_COUNT: Int = 10000
        const val TAKE_PROBLEM_SCREENSHOT_RECEIVER_INTENT: String = "take_problem_screenshot"
        const val SHORTCUT_BUNDLE_KEY_CURRENT_FAVORITE_EXAM: String = "current_favorite_exam"
        const val MIN_OF_NORMAL_SCREEN_SIZE: Double = 6.0
        const val MIN_OF_DEVICE_RAM_CAPACITY: Long = 4294967296L
        const val EXAM_ID_RANGE_MAX: Int = 10000000
        const val EXAM_ID_RANGE_MIN: Int = 1000000
        const val ANSWER_SHEET_EXAM_WAKE_LOCK_TAG: String = "AnswerSheet::ExamWakeLockTag"
        const val EXAM_TIME_LAYOUT_DEFAULT_COLOR: Int = R.color.elements_color_tint
        const val ITEM_COLLAPSED: String = "collapsed"
        const val ITEM_FULLSCREEN: String = "fullscreen"
        private const val APP_INFO_FIRST_LINE_SPACE = "  "
        private const val WORKSHEET_TEXT_TYPE = "text/plain"
        private const val FILTER_START_TIME = "start_time"
        private const val FILTER_QUESTIONS_NUMBER = "questions_number"
        private const val FILTER_EXAM_RUNNING = "exam_running"
        private const val FILTER_EXAM_CREATING = "exam_creating"
        private const val FILTER_EXAM_ENDED = "exam_ended"
        private const val FILTER_EXAM_SUSPENDED = "exam_suspended"
        private const val FILTER_EXAM_CORRECTING = "exam_correcting"
        private const val PERMISSION_SIDE_BACKUPS = "permission_side_backups"
        private const val PERMISSION_SIDE_DOCUMENTS = "permission_side_documents"
        private const val FILTERS_DELIMITERS = " "
        private const val INTERVAL: Long = 1000
        private const val DELAY_PREPARE_EXAM: Long = 500
        private const val PERMISSIONS_REQUEST_CODE = 9324
        private const val TWO_DIGIT_NUM = 10
        private const val EXAM_ENDED = 192
        private const val EXAM_CORRECTION_ENDED = 182
        private const val RESET_BUTTON_ENABLE_DELAY = 60
        private const val MIN_OF_QUESTIONS_COUNT = 5
        private const val APP_VERSION_CODE_DIGITS = 8
        private val PEN_STROKE_COLORS = intArrayOf(
            Color.rgb(220, 53, 69),  //  RED
            Color.rgb(253, 126, 20),  // ORANGE
            Color.rgb(255, 193, 7),  // YELLOW
            Color.rgb(32, 201, 151),  // CYAN
            Color.rgb(13, 110, 253),  //  BLUE
            Color.rgb(214, 51, 132),  // PINK
            Color.rgb(121, 85, 72),  //  BROWN
            Color.rgb(108, 117, 125),  // BLACK
            Color.rgb(224, 224, 224) //  WHITE
        )
        private const val MAX_OF_PEN_STROKE = 100
        private const val MAX_OF_ERASER_STROKE = 250
        private const val EXAMS_VIEW_SPAN_COUNT = 2
        private const val EXAMS_LIST_ANIMATION_DURATION = 100
        private const val WANT_TO_RESTART_INTENT = "want_to_restart"
        private const val VIBRATE_SPLASH = 45
        private const val VIBRATE_SPLASH_SNOOZE = 150
        private const val NP_VALUE_CHANGE_VIBRATION = 15
        private const val ROW_TOGGLE_BUTTON_PRESSED_SCALE = 0.98f
        private const val DEFAULT_MILLIS = -1
        private const val DISPLAY_PIXEL_DIFFERENCE = 200
        fun setStatusBarTheme(activity: Activity, light: Boolean) {
            Saver.Companion.getInstance(activity).lastStatusBarColorState = (light)
            if (light) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    activity.window.decorView.systemUiVisibility =
                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                } else {
                    activity.window.decorView.systemUiVisibility = 0
                }
            } else {
                activity.window.decorView.systemUiVisibility = 0
            }
        }

        fun checkDarkModeEnabled(context: Context): Boolean {
            val nightModeFlags =
                context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            return nightModeFlags == Configuration.UI_MODE_NIGHT_YES
        }
    }
}