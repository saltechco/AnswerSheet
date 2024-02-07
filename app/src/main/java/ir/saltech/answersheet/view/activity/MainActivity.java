package ir.saltech.answersheet.view.activity;

import static android.graphics.PorterDuff.Mode.SRC_IN;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static ir.saltech.answersheet.view.container.BlurViewHolder.setBlurView;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adivery.sdk.Adivery;
import com.adivery.sdk.AdiveryAdListener;
import com.adivery.sdk.AdiveryBannerAdView;
import com.adivery.sdk.AdiveryListener;
import com.airbnb.lottie.LottieAnimationView;
import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.android.graphics.CanvasView;
import com.google.android.material.textfield.TextInputLayout;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.shawnlin.numberpicker.NumberPicker;
import com.yy.mobile.rollingtextview.CharOrder;
import com.yy.mobile.rollingtextview.RollingTextView;
import com.yy.mobile.rollingtextview.strategy.Direction;
import com.yy.mobile.rollingtextview.strategy.Strategy;

import org.jetbrains.annotations.Contract;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

import eightbitlab.com.blurview.BlurView;
import ir.saltech.answersheet.R;
import ir.saltech.answersheet.intf.listener.CollapseBarChangedListener;
import ir.saltech.answersheet.intf.listener.ExamSelectedListener;
import ir.saltech.answersheet.intf.listener.QuestionClickedListener;
import ir.saltech.answersheet.intf.listener.ToggleButtonPartClickedListener;
import ir.saltech.answersheet.object.container.Saver;
import ir.saltech.answersheet.object.data.Bookmark;
import ir.saltech.answersheet.object.data.Category;
import ir.saltech.answersheet.object.data.Document;
import ir.saltech.answersheet.object.data.Exam;
import ir.saltech.answersheet.object.data.Exam.CorrectionMode;
import ir.saltech.answersheet.object.data.Exam.ExamStatus;
import ir.saltech.answersheet.object.data.ExamName;
import ir.saltech.answersheet.object.data.ExamNames;
import ir.saltech.answersheet.object.data.ExamWallpaper;
import ir.saltech.answersheet.object.data.Exams;
import ir.saltech.answersheet.object.data.Question;
import ir.saltech.answersheet.object.data.Questions;
import ir.saltech.answersheet.object.util.DateConverter;
import ir.saltech.answersheet.object.util.TensorModelLoader;
import ir.saltech.answersheet.view.adapter.ExamsViewAdapter;
import ir.saltech.answersheet.view.adapter.QuestionsViewAdapter;
import ir.saltech.answersheet.view.container.MaterialAlert;
import ir.saltech.answersheet.view.container.MaterialAlertDialog;
import ir.saltech.answersheet.view.container.MaterialFragmentShower;
import ir.saltech.answersheet.view.container.Toast;
import ir.saltech.answersheet.view.dialog.SelectThingsDialog;
import ir.saltech.answersheet.view.fragment.AuthFragment;
import ir.saltech.answersheet.view.fragment.CollapsablePanelFragment;
import ir.saltech.answersheet.view.fragment.SettingsFragment;
import ir.saltech.answersheet.view.holder.ExamViewHolder;
import ir.tapsell.plus.AdRequestCallback;
import ir.tapsell.plus.AdShowListener;
import ir.tapsell.plus.TapsellPlus;
import ir.tapsell.plus.TapsellPlusBannerType;
import ir.tapsell.plus.TapsellPlusInitListener;
import ir.tapsell.plus.model.AdNetworkError;
import ir.tapsell.plus.model.AdNetworks;
import ir.tapsell.plus.model.TapsellPlusAdModel;
import ir.tapsell.plus.model.TapsellPlusErrorModel;
import jp.wasabeef.blurry.Blurry;

public class MainActivity extends AppCompatActivity implements ToggleButtonPartClickedListener {
    public static final boolean APPLICATION_PUBLISHER = true; // MyKet: True , CafeBazaar: False
    public static final String ADD_NEW_EXAM_BUTTON_TITLE = "add_exam";
    public static final String CATEGORY_ADDING_RECEIVER_RESULT = "category_adding_result";
    public static final String CATEGORY_ADDING_RECEIVER_INTENT = "category_adding_receiver";
    public static final String FREEZE_TIME = "freeze_time";
    public static final String CONTINUE_TIME = "continue_time";
    public static final String CATEGORY_ADDING_STATUS_CANCELED = "canceled";
    public static final String SIDE_RECENT_EXAMS = "side_recent_exams";
    public static final String SIDE_CREATING_EXAMS = "side_creating_exams";
    public static final String SIDE_CORRECTING_EXAMS = "side_correcting_exams";
    public static final String SIDE_SUSPENDED_EXAMS = "side_suspended_exams";
    public static final String SIDE_CURRENT_EXAMS = "side_current_exams";
    public static final String SIDE_SEARCH_EXAMS = "side_search_exams";
    public static final int APPLICATION_ID = 9342;
    public static final int MAX_OF_QUESTIONS_COUNT = 10000;
    public static final String TAKE_PROBLEM_SCREENSHOT_RECEIVER_INTENT = "take_problem_screenshot";
    public static final String SHORTCUT_BUNDLE_KEY_CURRENT_FAVORITE_EXAM = "current_favorite_exam";
    public static final double MIN_OF_NORMAL_SCREEN_SIZE = 6.0;
    public static final long MIN_OF_DEVICE_RAM_CAPACITY = 4294967296L;
    public static final int EXAM_ID_RANGE_MAX = 10000000;
    public static final int EXAM_ID_RANGE_MIN = 1000000;
    public static final String ANSWER_SHEET_EXAM_WAKE_LOCK_TAG = "AnswerSheet::ExamWakeLockTag";
    public static final int EXAM_TIME_LAYOUT_DEFAULT_COLOR = R.color.elements_color_tint;
    public static final String ITEM_COLLAPSED = "collapsed";
    public static final String ITEM_FULLSCREEN = "fullscreen";
    private static final String APP_INFO_FIRST_LINE_SPACE = "  ";
    private static final String WORKSHEET_TEXT_TYPE = "text/plain";
    private static final String FILTER_START_TIME = "start_time";
    private static final String FILTER_QUESTIONS_NUMBER = "questions_number";
    private static final String FILTER_EXAM_RUNNING = "exam_running";
    private static final String FILTER_EXAM_CREATING = "exam_creating";
    private static final String FILTER_EXAM_ENDED = "exam_ended";
    private static final String FILTER_EXAM_SUSPENDED = "exam_suspended";
    private static final String FILTER_EXAM_CORRECTING = "exam_correcting";
    private static final String PERMISSION_SIDE_BACKUPS = "permission_side_backups";
    private static final String PERMISSION_SIDE_DOCUMENTS = "permission_side_documents";
    private static final String FILTERS_DELIMITERS = " ";
    private static final long INTERVAL = 1_000;
    private static final long DELAY_PREPARE_EXAM = 500;
    private static final int PERMISSIONS_REQUEST_CODE = 9324;
    private static final int TWO_DIGIT_NUM = 10;
    private static final int EXAM_ENDED = 192;
    private static final int EXAM_CORRECTION_ENDED = 182;
    private static final int RESET_BUTTON_ENABLE_DELAY = 60;
    private static final int MIN_OF_QUESTIONS_COUNT = 5;
    private static final int APP_VERSION_CODE_DIGITS = 8;
    private static final int[] PEN_STROKE_COLORS = {
            Color.rgb(220, 53, 69),    //  RED
            Color.rgb(253, 126, 20),  // ORANGE
            Color.rgb(255, 193, 7),    // YELLOW
            Color.rgb(32, 201, 151),  // CYAN
            Color.rgb(13, 110, 253),   //  BLUE
            Color.rgb(214, 51, 132),  // PINK
            Color.rgb(121, 85, 72),    //  BROWN
            Color.rgb(108, 117, 125), // BLACK
            Color.rgb(224, 224, 224)   //  WHITE
    };
    private static final int MAX_OF_PEN_STROKE = 100;
    private static final int MAX_OF_ERASER_STROKE = 250;
    private static final int EXAMS_VIEW_SPAN_COUNT = 2;
    private static final int EXAMS_LIST_ANIMATION_DURATION = 100;
    private static final String WANT_TO_RESTART_INTENT = "want_to_restart";
    private static final int VIBRATE_SPLASH = 45;
    private static final int VIBRATE_SPLASH_SNOOZE = 150;
    private static final int NP_VALUE_CHANGE_VIBRATION = 15;
    private static final float ROW_TOGGLE_BUTTON_PRESSED_SCALE = 0.98f;
    private static final int DEFAULT_MILLIS = -1;
    private static final int DISPLAY_PIXEL_DIFFERENCE = 200;
    private final Random random = new Random();
    boolean animShow;
    private Exams recentExams;
    private ConstraintLayout parentLayout;
    private LinearLayout menuItemsLayout;
    private ConstraintLayout recentExamsLayout;
    private ConstraintLayout userDashboardLayout;
    private ConstraintLayout examsViewLayout;
    private ImageButton appMoreOptions;
    private View clickableArea;
    private CardView addExamButton;
    private CardView startedExamsButton;
    private CardView suspendedExamsButton;
    private CardView finishedExamsButton;
    private CardView creatingExamsButton;
    private CardView correctingExamButton;
    private CardView examsViewCard;
    private TextInputLayout lastQuestionNoText;
    private TextInputLayout questionsCPatternText;
    private TextInputLayout firstQuestionNoText;
    private TextInputLayout questionsCountText;
    private LinearLayout selectQuestionsRandomly;
    private TextView examsViewEmpty;
    private LinearLayout selectQuestionsMode;
    private LinearLayout selectCorrectionMode;
    private ImageButton examsListBack;
    private TextView examsViewTitle;
    private RecyclerView examsView;
    private ImageView splashSalTechImg;
    private LottieAnimationView splashAnim;
    private ImageView appIcon;
    private TextView appTitle;
    private AnimatorSet animatorSet;
    private List<Exam> recentExamsList;
    private List<Exam> currentExams;
    private List<Exam> suspendedExamsList;
    private List<Exam> creatingExamsList;
    private List<Exam> correctingExamsList;
    private ExamsViewAdapter examsViewAdapter;
    private ConstraintLayout welcomeLayout;
    private ConstraintLayout selectExamImmediately;
    private LinearLayout createCustomExam;
    private LinearLayout startExamButtonsLayout;
    private LottieAnimationView welcomeImage;
    private ImageView welcomeCompanyLogo;
    private TextView welcomeTitle;
    private TextView welcomeAppDesc;
    private TextView welcomeClickContinue;
    private NumberPicker minuteNP;
    private NumberPicker secondNP;
    private NumberPicker chronoThresholdNP;
    private LinearLayout selectCategoryEnabled;
    private LinearLayout useCategoryTimingEnabled;
    private LinearLayout useCategoryScoreEnabled;
    private ConstraintLayout splashScreenLayout;
    private ImageButton userDashboardButton;
    private boolean optionsMenuOpened;
    private String examFeatures;
    private Vibrator vibrator;
    private int repeatCount;
    private boolean welcomePageShowed;
    private TextView setupExamTimeColon;
    private ScrollView recentExamsScrollContainer;
    private ScrollView createExamLayoutContainer;
    private ConstraintLayout examRunningLayout;
    private ConstraintLayout examSetupLayout;
    private boolean selectedQRandomly;
    private boolean useExamCategorize;
    private LinearLayout selectChronometerEnabled;
    private boolean useChronometer;
    private int defaultPaletteColor;
    private LottieAnimationView examAnimView;
    private ImageView examPictureView;
    private TextView examNameText;
    private CircularProgressBar examTimeBar;
    private Button startCurrentExam;
    private LinearLayout runningExamOptions;
    private BlurView examControlPanel;
    private BlurView examAnswerSheetEmptyError;
    private FrameLayout examDraftPages;
    private RecyclerView answerSheetView;
    private boolean questionLayoutClosed;
    private List<Question> questions = new ArrayList<>();
    private LinearLayout selectExamName;
    private LinearLayout selectExamDocument;
    private Exam currentExam;
    private ImageButton backToMainView;
    private Button enterToExamRoom;
    private ImageButton scheduleCurrentExam;
    private ExamName currentExamName;
    private int firstQuestion = 1;
    private int lastQuestion;
    private int questionsCPattern = 1;
    private int questionsCount = 10;
    private ConstraintLayout shortcutExamPreparingLayout;
    private TextView debugVerWatermark;
    private long minute;
    private long second;
    private boolean isStartedManualExam;
    private Exam.CorrectionMode correctionMode = Exam.CorrectionMode.None;
    private boolean useCategoryScore;
    private boolean useCategoryTiming;
    private int chronoThreshold;
    private long examTime;
    private RollingTextView examTimeBoard;
    private boolean isQuestionsManually;
    private String startedExamTime;
    private Document examFile;
    private QuestionsViewAdapter questionsAdapter;
    private ImageButton examAction;
    private ImageButton jumpToQuestion;
    private ImageButton openDraftBox;
    private ImageButton openExamFileBox;
    private ImageButton addedBookmarksButton;
    private ImageButton addQuestionButton;
    private ImageButton removeQuestionButton;
    private ImageButton resetChronometer;
    private ImageButton shareWorksheetButton;
    private ImageButton enableNegativePoint;
    private CountDownTimer timerForThinkingTime;
    private long buttonDisableSeconds;
    private CountDownTimer examTimeLeft;
    private boolean examHeaderCollapsed;
    private boolean isRecentExamLoaded;
    private ImageButton collapseExamHeader;
    private RoundCornerProgressBar collapseExamTimeBar;
    private boolean resetEnable;
    private boolean startedTimeExam;
    private long examTimeLeftUntilFinished;
    private boolean criticalTimeVibrationRang;
    private boolean warningTimeVibrationRang;
    private MediaPlayer stopWatchEffectPlayer;
    private int runningCategory;
    private boolean isExamStoppedManually;
    private boolean isExamNowEnded;
    private boolean correctedAsNow;
    private boolean isExamStarted;
    private TextView draftDrawingHint;
    private ImageButton collapseDraftView;
    private ImageButton draftViewOptions;
    private BlurView examDraftLayout;
    private ConstraintLayout answerSheetLayout;
    private LinearLayout draftToolboxItems;
    private boolean examFileVisibility;
    private boolean draftPathErasingEnabled;
    private boolean examFileLoaded;
    private float draftPenStrokeSize = 3;
    private float draftEraserStrokeSize = 10;
    private int selectedColor;
    private boolean isDraftCanvasCleared = true;
    private boolean isDraftDrawingHintShown;
    private int draftToolboxItemIndex;
    private boolean dismissDrawOptionsWindowManually;
    private int draftOptionsItemIndex;
    private int penModelIndex;
    private CanvasView.Drawer selectedMode = CanvasView.Drawer.PEN;
    private int selectedModeResId = R.drawable.path_drawing;
    private CanvasView.Mode selectedDrawingMode = CanvasView.Mode.DRAW;
    private Paint.Style selectedStyle = Paint.Style.STROKE;
    private boolean isDraftEraserStrokeSizeEdited;
    private boolean isDraftPenStrokeSizeEdited;
    private int strokePenColorIndex;
    private boolean dismissSubmitTextWindowManually;
    private boolean isPdfNightModeEnabled;
    private int currentDraftViewIndex;
    private CanvasView examDraftPage;
    private ImageButton addDraftPage;
    private ImageButton draftPagesOptions;
    private ExamStatus currentExamStatus = ExamStatus.Creating;
    private boolean canUsingAdditionalSubtraction;
    private String standardBannerResponseId;
    private RelativeLayout standardTapsellBanner;

    public static void setStatusBarTheme(Activity activity, boolean light) {
        Saver.getInstance(activity).setLastStatusBarColorState(light);
        if (light) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                activity.getWindow().getDecorView().setSystemUiVisibility(0);
            }
        } else {
            activity.getWindow().getDecorView().setSystemUiVisibility(0);
        }
    }

    public static boolean checkDarkModeEnabled(Context context) {
        int nightModeFlags = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setStatusBarTheme(this, !checkDarkModeEnabled(this));
        init();
        setNavigationBarMargin();
        if (!Saver.getInstance(this).getWelcomePageShowed()) {
            showWelcomePage();
        } else {
            if (Saver.getInstance(this).getAppPassword() != 0) {
                getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new AuthFragment(this::showSplashScreen)).commit();
            } else {
                showSplashScreen();
            }
        }
    }
    private void usingMLModel() {
        TensorModelLoader modelLoader = new TensorModelLoader();
        String val = "10";
        float[] input = new float[1];
        input[0] = Float.parseFloat(val);
        float[][] output = new float[1][1];
        Objects.requireNonNull(modelLoader.loadModel(this, "linear.tflite", null)).run(input, output);
        Toast.makeText(this, "Predict is: " + output[0][0], Toast.LENGTH_SHORT).show();
    }

    /**
     * Launches application after millis.
     *
     * @param millis 0 is NO_WAIT, DEFAULT_MILLIS is 750.
     */
    private void launchApp(long millis) {
        new Handler().postDelayed(() -> {
            splashScreenLayout.setVisibility(GONE);
            setAppParameters();
        }, (millis == DEFAULT_MILLIS) ? 750 : millis);
    }

    private void showMainViewLayout(@NonNull MainView which) {
        switch (which) {
            case UserDashboard:
                userDashboardLayout.setVisibility(VISIBLE);
                examRunningLayout.setVisibility(GONE);
                recentExamsLayout.setVisibility(GONE);
                examSetupLayout.setVisibility(View.GONE);
                welcomeLayout.setVisibility(GONE);
                splashScreenLayout.setVisibility(GONE);
                break;
            case SplashScreen:
                userDashboardLayout.setVisibility(GONE);
                examRunningLayout.setVisibility(GONE);
                recentExamsLayout.setVisibility(GONE);
                examSetupLayout.setVisibility(View.GONE);
                welcomeLayout.setVisibility(GONE);
                splashScreenLayout.setVisibility(VISIBLE);
                break;
            case ExamSetup:
                selectExamImmediately.setVisibility(VISIBLE);
                userDashboardLayout.setVisibility(GONE);
                examRunningLayout.setVisibility(GONE);
                recentExamsLayout.setVisibility(GONE);
                examSetupLayout.setVisibility(View.VISIBLE);
                welcomeLayout.setVisibility(GONE);
                splashScreenLayout.setVisibility(GONE);
                break;
            case ExamRunning:
                userDashboardLayout.setVisibility(GONE);
                examRunningLayout.setVisibility(View.VISIBLE);
                recentExamsLayout.setVisibility(GONE);
                examSetupLayout.setVisibility(GONE);
                welcomeLayout.setVisibility(GONE);
                splashScreenLayout.setVisibility(GONE);
                playExamRunning();
                break;
            case RecentExams:
                userDashboardLayout.setVisibility(GONE);
                examRunningLayout.setVisibility(GONE);
                recentExamsLayout.setVisibility(View.VISIBLE);
                examSetupLayout.setVisibility(GONE);
                welcomeLayout.setVisibility(GONE);
                splashScreenLayout.setVisibility(GONE);
                break;
            case WelcomePage:
                userDashboardLayout.setVisibility(GONE);
                examRunningLayout.setVisibility(GONE);
                recentExamsLayout.setVisibility(GONE);
                examSetupLayout.setVisibility(GONE);
                welcomeLayout.setVisibility(VISIBLE);
                splashScreenLayout.setVisibility(GONE);
                break;
            default:
                break;
        }
    }

    private void playExamRunning() {
        ObjectAnimator eRScaleXAnim = ObjectAnimator.ofFloat(examRunningLayout, "scaleX", 0.75f, 1f);
        ObjectAnimator eRScaleYAnim = ObjectAnimator.ofFloat(examRunningLayout, "scaleY", 0.75f, 1f);
        ObjectAnimator eRAlphaAnim = ObjectAnimator.ofFloat(examRunningLayout, "alpha", 0f, 1f);
        ObjectAnimator eRTranslationYAnim = ObjectAnimator.ofFloat(examRunningLayout, "translationY", 250f, 1f);
        animatorSet = new AnimatorSet();
        animatorSet.playTogether(eRScaleXAnim, eRScaleYAnim, eRAlphaAnim, eRTranslationYAnim);
        animatorSet.setDuration(300);
        animatorSet.setStartDelay(150);
        animatorSet.start();
    }

    private void showWelcomePage() {
        welcomePageShowed = true;
        showMainViewLayout(MainView.WelcomePage);
        welcomeTitle.setText("ســـلام!!! 🙋🏻‍♂️😃😄");
        ObjectAnimator showTitle1Alpha = ObjectAnimator.ofFloat(welcomeTitle, "alpha", 0f, 1f);
        ObjectAnimator showTitle1ScaleX = ObjectAnimator.ofFloat(welcomeTitle, "scaleX", 0f, 1.3f);
        ObjectAnimator showTitle1ScaleY = ObjectAnimator.ofFloat(welcomeTitle, "scaleY", 0f, 1.3f);
        animatorSet = new AnimatorSet();
        animatorSet.playTogether(showTitle1Alpha, showTitle1ScaleY, showTitle1ScaleX);
        animatorSet.setStartDelay(300);
        animatorSet.setDuration(300);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                ObjectAnimator hideTitle1Alpha = ObjectAnimator.ofFloat(welcomeTitle, "alpha", 1f, 0f);
                ObjectAnimator hideTitle1ScaleX = ObjectAnimator.ofFloat(welcomeTitle, "scaleX", 1.3f, 0f);
                ObjectAnimator hideTitle1ScaleY = ObjectAnimator.ofFloat(welcomeTitle, "scaleY", 1.3f, 0f);
                animatorSet = new AnimatorSet();
                animatorSet.playTogether(hideTitle1Alpha, hideTitle1ScaleY, hideTitle1ScaleX);
                animatorSet.setStartDelay(1050);
                animatorSet.setDuration(300);
                animatorSet.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        welcomeTitle.setText(getString(R.string.welcome_title));
                        ObjectAnimator showTitle2Alpha = ObjectAnimator.ofFloat(welcomeTitle, "alpha", 0f, 1f);
                        ObjectAnimator showTitle2ScaleX = ObjectAnimator.ofFloat(welcomeTitle, "scaleX", 0f, 1.05f);
                        ObjectAnimator showTitle2ScaleY = ObjectAnimator.ofFloat(welcomeTitle, "scaleY", 0f, 1.05f);
                        animatorSet = new AnimatorSet();
                        animatorSet.playTogether(showTitle2Alpha, showTitle2ScaleY, showTitle2ScaleX);
                        animatorSet.setStartDelay(450);
                        animatorSet.setDuration(300);
                        animatorSet.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animator) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animator) {
                                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) welcomeTitle.getLayoutParams();
                                ValueAnimator hideTitle2VBias = ValueAnimator.ofFloat(0.5f, 0.55f);
                                hideTitle2VBias.addUpdateListener(valueAnimator -> {
                                    params.verticalBias = (float) valueAnimator.getAnimatedValue();
                                    welcomeTitle.setLayoutParams(params);
                                });
                                ObjectAnimator hideTitle2ScaleX = ObjectAnimator.ofFloat(welcomeTitle, "scaleX", 1.05f, 1f);
                                ObjectAnimator hideTitle2ScaleY = ObjectAnimator.ofFloat(welcomeTitle, "scaleY", 1.05f, 1f);
                                ConstraintLayout.LayoutParams params2 = (ConstraintLayout.LayoutParams) welcomeImage.getLayoutParams();
                                ValueAnimator showImageVBias = ValueAnimator.ofFloat(0.5f, 0.2f);
                                showImageVBias.addUpdateListener(valueAnimator -> {
                                    params2.verticalBias = (float) valueAnimator.getAnimatedValue();
                                    welcomeImage.setLayoutParams(params2);
                                });
                                ObjectAnimator showImageAlpha = ObjectAnimator.ofFloat(welcomeImage, "alpha", 0f, 1f);
                                ObjectAnimator showImageScaleX = ObjectAnimator.ofFloat(welcomeImage, "scaleX", 0f, 1f);
                                ObjectAnimator showImageScaleY = ObjectAnimator.ofFloat(welcomeImage, "scaleY", 0f, 1f);
                                animatorSet = new AnimatorSet();
                                animatorSet.playTogether(hideTitle2VBias, hideTitle2ScaleY, hideTitle2ScaleX);
                                animatorSet.playTogether(showImageVBias, showImageAlpha, showImageScaleY, showImageScaleX);
                                animatorSet.setStartDelay(1050);
                                animatorSet.setDuration(1000);
                                animatorSet.addListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animator) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animator) {
                                        ObjectAnimator showText1Alpha = ObjectAnimator.ofFloat(welcomeAppDesc, "alpha", 0f, 1f);
                                        ObjectAnimator showText1TranslationY = ObjectAnimator.ofFloat(welcomeAppDesc, "translationY", 100f, 1f);
                                        ObjectAnimator showText2Alpha = ObjectAnimator.ofFloat(welcomeClickContinue, "alpha", 0f, 1f);
                                        showText2Alpha.setStartDelay(300);
                                        ObjectAnimator showText2TranslationY = ObjectAnimator.ofFloat(welcomeClickContinue, "translationY", 100f, 1f);
                                        animatorSet = new AnimatorSet();
                                        animatorSet.playTogether(showText1Alpha, showText1TranslationY);
                                        animatorSet.playTogether(showText2Alpha, showText2TranslationY);
                                        animatorSet.setStartDelay(500);
                                        animatorSet.setDuration(500);
                                        animatorSet.addListener(new Animator.AnimatorListener() {
                                            @Override
                                            public void onAnimationStart(Animator animator) {

                                            }

                                            @Override
                                            public void onAnimationEnd(Animator animator) {
                                                ObjectAnimator showCompanyLogo = ObjectAnimator.ofFloat(welcomeCompanyLogo, "alpha", 0f, 1f);
                                                showCompanyLogo.setStartDelay(300);
                                                showCompanyLogo.setDuration(500);
                                                showCompanyLogo.addListener(new Animator.AnimatorListener() {
                                                    @Override
                                                    public void onAnimationStart(Animator animator) {

                                                    }

                                                    @Override
                                                    public void onAnimationEnd(Animator animator) {
                                                        welcomeLayout.setOnClickListener(view -> launchApp(100));
                                                        welcomeClickContinue.setOnClickListener(view -> launchApp(100));
                                                    }

                                                    @Override
                                                    public void onAnimationCancel(Animator animator) {

                                                    }

                                                    @Override
                                                    public void onAnimationRepeat(Animator animator) {

                                                    }
                                                });
                                                showCompanyLogo.start();
                                                ObjectAnimator showText2ScaleX = ObjectAnimator.ofFloat(welcomeClickContinue, "scaleX", 1f, 1.04f, 1f);
                                                ObjectAnimator showText2ScaleY = ObjectAnimator.ofFloat(welcomeClickContinue, "scaleY", 1f, 1.04f, 1f);
                                                showText2ScaleX.setStartDelay(500);
                                                showText2ScaleX.setDuration(1000);
                                                showText2ScaleX.setRepeatCount(-1);
                                                showText2ScaleX.start();
                                                showText2ScaleY.setStartDelay(500);
                                                showText2ScaleY.setDuration(1000);
                                                showText2ScaleY.setRepeatCount(-1);
                                                showText2ScaleY.start();
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

                                    @Override
                                    public void onAnimationCancel(Animator animator) {

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animator) {

                                    }
                                });
                                animatorSet.start();
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

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                });
                animatorSet.start();
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

    /**
     * This method checks if phone has < 4GB RAM, visual effects will be disabled.
     */
    private boolean checkDeviceSupport() {
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        Log.i("TAG", "Total Memory: " + mi.totalMem);
        return mi.totalMem >= MIN_OF_DEVICE_RAM_CAPACITY;
    }

    private void setAppParameters() {
        welcomePageShowed = false;
        Saver.getInstance(MainActivity.this).setAppRestartWanted(false);
        Saver.getInstance(MainActivity.this).setWelcomePageShowed(true);
        showRecentExams();
        setTexts();
        onClicks();
        showAds();
        usingMLModel();
    }

    private void hideTapsellAds() {
        if (standardBannerResponseId != null)
            TapsellPlus.destroyStandardBanner(this, standardBannerResponseId, standardTapsellBanner);
    }

    private void showAds() {
        Adivery.configure(getApplication(), getString(R.string.adivery_app_id));
        Adivery.prepareInterstitialAd(MainActivity.this, getString(R.string.adivery_interstitial_ad_id));
        Adivery.addGlobalListener(new AdiveryListener() {

            @Override
            public void onAppOpenAdLoaded(@NonNull String placementId) {
                // تبلیغ اجرای اپلیکیشن بارگذاری شده است.
            }

            @Override
            public void onInterstitialAdLoaded(@NonNull String placementId) {
                Log.d("TAG", "Interstitial Ad has been loaded");
            }

            @Override
            public void onRewardedAdLoaded(@NonNull String placementId) {
                // تبلیغ جایزه‌ای بارگذاری شده
                Log.d("TAG", "Rewarded Ad has been loaded");
                if (Adivery.isLoaded(placementId)) {
                    Adivery.showAd(placementId);
                }
            }

            @Override
            public void onRewardedAdClosed(@NonNull String placementId, boolean isRewarded) {
                // بررسی کنید که آیا کاربر جایزه دریافت می‌کند یا خیر
            }

            @Override
            public void log(@NonNull String placementId, @NonNull String log) {
                Log.i("TAG", "Adivery said " + log + "\nPlacementId: " + placementId);
                // پیغام را چاپ کنید
            }
        });
        TapsellPlus.initialize(this, getString(R.string.tapsell_plus_id),
                new TapsellPlusInitListener() {
                    @Override
                    public void onInitializeSuccess(AdNetworks adNetworks) {
                        Log.d("onInitializeSuccess", adNetworks.name());
                        TapsellPlus.setGDPRConsent(MainActivity.this, true);
                        TapsellPlus.requestStandardBannerAd(
                                MainActivity.this, getString(R.string.tapsell_banner_zone_id),
                                TapsellPlusBannerType.BANNER_320x50,
                                new AdRequestCallback() {
                                    @Override
                                    public void response(TapsellPlusAdModel tapsellPlusAdModel) {
                                        super.response(tapsellPlusAdModel);

                                        //Ad is ready to show
                                        //Put the ad's responseId to your responseId variable
                                        standardBannerResponseId = tapsellPlusAdModel.getResponseId();
                                        TapsellPlus.showStandardBannerAd(MainActivity.this, standardBannerResponseId,
                                                standardTapsellBanner,
                                                new AdShowListener() {
                                                    @Override
                                                    public void onOpened(TapsellPlusAdModel tapsellPlusAdModel) {
                                                        super.onOpened(tapsellPlusAdModel);
                                                    }

                                                    @Override
                                                    public void onError(TapsellPlusErrorModel tapsellPlusErrorModel) {
                                                        super.onError(tapsellPlusErrorModel);
                                                    }
                                                });
                                    }

                                    @Override
                                    public void error(@NonNull String message) {
                                    }
                                });
                    }

                    @Override
                    public void onInitializeFailed(AdNetworks adNetworks,
                                                   AdNetworkError adNetworkError) {
                        Log.e("onInitializeFailed", "ad network: " + adNetworks.name() + ", error: " + adNetworkError.getErrorMessage());
                    }
                });
        AdiveryBannerAdView bannerAd = findViewById(R.id.adivery_banner_ad);
        bannerAd.setBannerAdListener(new AdiveryAdListener() {
            @Override
            public void onAdLoaded() {
                Log.d("TAG", "Adivery banner has been loaded.");
                // تبلیغ به‌طور خودکار نمایش داده می‌شود، هر کار دیگری لازم است اینجا انجام دهید.
            }

            @Override
            public void onError(String reason) {
                Log.e("TAG", "Error at adivery banner loading -> " + reason);
                // خطا را چاپ کنید تا از دلیل آن مطلع شوید
            }

            @Override
            public void onAdClicked() {
                // کاربر روی بنر کلیک کرده
            }
        });
        bannerAd.loadAd();
        TapsellPlus.setDebugMode(Log.DEBUG);
    }

    private void prepareAnswerSheet() {
        if (questionsCount > 0 && firstQuestion > 0 && questionsCPattern > 0) {
            if (currentExam != null && currentExam.getAnswerSheet() != null) {
                if (currentExam.getAnswerSheet().getQuestions().size() != 0)
                    questions = currentExam.getAnswerSheet().getQuestions();
            }
            if (questions.size() == 0) {
                int questionsCounter = 0;
                List<Integer> qNumbers = new ArrayList<>();
                if (selectedQRandomly) {
                    for (int i = 0; i < questionsCount; ) {
                        int generatedRQN = generateRandomQNumber(firstQuestion);
                        boolean hasSame = false;
                        for (int j = 0; j < qNumbers.size(); j++) {
                            if (qNumbers.get(j) == generatedRQN) {
                                hasSame = true;
                                break;
                            }
                        }
                        if (!hasSame) {
                            qNumbers.add(generatedRQN);
                            i++;
                        }
                    }
                    Collections.sort(qNumbers, Integer::compareTo);
                }
                for (int qIndex = firstQuestion - 1; questionsCounter < questionsCount; qIndex += questionsCPattern) {
                    Question q = new Question();
                    if (selectedQRandomly) {
                        q.setQuestionNumber(qNumbers.get(questionsCounter));
                    } else {
                        q.setQuestionNumber(qIndex + 1);
                    }
                    q.setWhite(true);
                    questions.add(q);
                    questionsCounter++;
                }
                Saver.getInstance(MainActivity.this).saveQuestions(new Questions(questions));
                if (currentExam != null) {
                    if (currentExam.getAnswerSheet() == null || isQuestionsManually) {
                        currentExam.setAnswerSheet(new Questions(questions));
                        recentExams.updateCurrentExam(currentExam);
                        Saver.getInstance(MainActivity.this).saveRecentExams(recentExams);
                    }
                }
            }
            showAnswerSheetLinear();
            // TODO: If you want to have a different screen layout for any screen sizes, enable this
//        if (getScreenSize() >= MIN_OF_NORMAL_SCREEN_SIZE) {
//            showAnswerSheetLinear();
//        } else {
//            showAnswerSheetGrid();
//        }
        }
    }

    private int generateRandomQNumber(int questionFrom) {
        return questionFrom + random.nextInt(lastQuestion - questionFrom);
    }

    private double getScreenSize() {
        Point point = new Point();
        ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRealSize(point);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int width = point.x;
        int height = point.y;
        double wi = (double) width / (double) displayMetrics.xdpi;
        double hi = (double) height / (double) displayMetrics.ydpi;
        double x = Math.pow(wi, 2);
        double y = Math.pow(hi, 2);
        return Math.round((Math.sqrt(x + y)) * 10.0) / 10.0;
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

    private void showAnswerSheetLinear() {
        questionsAdapter = new QuestionsViewAdapter(questions, defaultPaletteColor, null, currentExam, new QuestionClickedListener() {
            @Override
            public void onQuestionClicked(Question question, float x, float y) {

            }

            @Override
            public void onQuestionAnswered(Question q) {

            }

            @Override
            public void onQuestionAnswerDeleted(Question q) {

            }

            @Override
            public void onQuestionBookmarkChanged(Question q) {

            }

            @Override
            public void onQuestionDeleted(int qPosition) {

            }

            @Override
            public void onQuestionCategoryClicked() {

            }
        }, this);
        answerSheetView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        answerSheetView.setAdapter(questionsAdapter);
        examAnswerSheetEmptyError.setVisibility((questions.size() == 0) ? VISIBLE : GONE);
    }

    private void setupShortcutLayout() {
        if (shortcutExamPreparingLayout.getVisibility() == VISIBLE) {
            ObjectAnimator layoutAlphaOut = ObjectAnimator.ofFloat(shortcutExamPreparingLayout, "alpha", 1f, 0f);
            layoutAlphaOut.setStartDelay(50);
            layoutAlphaOut.setDuration(250);
            layoutAlphaOut.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(@NonNull Animator animator) {

                }

                @Override
                public void onAnimationEnd(@NonNull Animator animator) {
                    shortcutExamPreparingLayout.setVisibility(GONE);
                    setupExam(0);
                }

                @Override
                public void onAnimationCancel(@NonNull Animator animator) {

                }

                @Override
                public void onAnimationRepeat(@NonNull Animator animator) {

                }
            });
            layoutAlphaOut.start();
        }
    }

    private void setupExam(long delay) {
        new Handler().postDelayed(() -> {
            if (currentExam == null) {
                resetStartExamButtonToDefault();
                if (currentExamName != null) {
                    if (!isQuestionsManually) {
                        // Set Default Parameters
                        if (Objects.requireNonNull(questionsCPatternText.getEditText()).getText().toString().isEmpty()) {
                            questionsCPatternText.getEditText().setText(String.valueOf(1));
                        }
                        if (Objects.requireNonNull(firstQuestionNoText.getEditText()).getText().toString().isEmpty()) {
                            firstQuestionNoText.getEditText().setText(String.valueOf(1));
                        }
                        if (Objects.requireNonNull(questionsCountText.getEditText()).getText().toString().isEmpty()) {
                            questionsCountText.getEditText().setText(String.valueOf(10));
                        }
                        firstQuestion = Integer.parseInt(firstQuestionNoText.getEditText().getText().toString());
                        if (selectedQRandomly) {
                            questionsCPatternText.getEditText().setText(getString(R.string.num, 1));
                            if (lastQuestionNoText.getVisibility() == View.VISIBLE) {
                                if (lastQuestionNoText.getEditText().getText().toString().isEmpty()) {
                                    setTextInputError(lastQuestionNoText, "شماره آخرین سؤال را وارد نکرده اید!");
                                } else {
                                    setTextInputError(lastQuestionNoText, null);
                                    lastQuestion = Integer.parseInt(lastQuestionNoText.getEditText().getText().toString());
                                }
                            }
                            questionsCPattern = 1;
                        } else {
                            questionsCPattern = Integer.parseInt(questionsCPatternText.getEditText().getText().toString());
                        }
                        questionsCount = Integer.parseInt(questionsCountText.getEditText().getText().toString());
                        if (questionsCount >= MIN_OF_QUESTIONS_COUNT) {
                            if (questionsCount <= MAX_OF_QUESTIONS_COUNT) {
                                if (firstQuestion >= 1) {
                                    if (questionsCPattern >= 1) {
                                        setTextInputError(questionsCountText, null);
                                        setTextInputError(firstQuestionNoText, null);
                                        setTextInputError(questionsCPatternText, null);
                                        if (selectedQRandomly) {
                                            if ((lastQuestion >= questionsCount + firstQuestion) && lastQuestion > 3) {
                                                setTextInputError(lastQuestionNoText, null);
                                                questionsCPattern = 1;
                                            } else {
                                                setTextInputError(lastQuestionNoText, String.format(Locale.getDefault(), "شماره آخرین سؤال، باید بزرگتر از %d باشد!", firstQuestion + questionsCount));
                                            }
                                            if (lastQuestionNoText.getError() == null) {
                                                setupExamFeatures();
                                            }
                                        } else {
                                            setupExamFeatures();
                                        }
                                    } else {
                                        setTextInputError(questionsCPatternText, "الگوی شمارش سؤالات، باید بزرگتر از ۱ باشد!");
                                    }
                                } else {
                                    setTextInputError(firstQuestionNoText, "شماره اولین سؤال آزمون، باید بزرگتر از ۱ باشد!");
                                }
                            } else {
                                setTextInputError(questionsCountText, "تعداد سؤال، نمی تواند بیشتر از ۱۰۰۰۰ باشد!");
                            }
                        } else {
                            setTextInputError(questionsCountText, "تعداد سؤال، نباید کمتر از ۵ باشد!");
                        }
                    } else {
                        setupExamFeatures();
                    }
                } else {
                    Toast.makeText(this, "لطفاً نام آزمون را مشخص کنید.", Toast.WARNING_SIGN, Toast.LENGTH_SHORT).show();
                }
            } else {
                prepareExam();
            }
        }, delay);
    }

    private void resetStartExamButtonToDefault() {
        enterToExamRoom.setEnabled(true);
        //if (preparingExamDialog != null) preparingExamDialog.dismiss(preparingExamDialog);
    }

    private void showHideKeyboardLayout(boolean show, View anchor) {
        InputMethodManager keyboardManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (show) {
            keyboardManager.toggleSoftInputFromWindow(anchor.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
        } else {
            keyboardManager.hideSoftInputFromWindow(anchor.getApplicationWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
        }
    }

    private void setupExamFeatures() {
        chronoThreshold = chronoThresholdNP.getValue();
        if (checkExamHasTime()) {
            minute = minuteNP.getValue();
            second = secondNP.getValue();
            if (checkToggleButtonChecked(selectChronometerEnabled)) {
                if (chronoThreshold != 0) {
                    prepareExamWithTiming();
                } else {
                    Toast.makeText(this, "لطفاً مقدار حد مجاز کرنومتر را تعیین کنید!", Toast.WARNING_SIGN, Toast.LENGTH_SHORT).show();
                }
            } else {
                prepareExamWithTiming();
            }
        } else {
            if (checkToggleButtonChecked(selectChronometerEnabled)) {
                if (chronoThreshold != 0) {
                    prepareExam();
                } else {
                    Toast.makeText(this, "لطفاً مقدار حد مجاز کرنومتر را تعیین کنید!", Toast.WARNING_SIGN, Toast.LENGTH_SHORT).show();
                }
            } else {
                prepareExam();
            }
        }
    }

    private void prepareExamWithTiming() {
        if (examTime == 0) {
            examTime = (minute * 60_000L) + (second * 1_000L);
        }
        prepareExam();
    }

    private void prepareExam() {
        setupShortcutLayout();
        if (Saver.getInstance(MainActivity.this).getKeepScreenOn()) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            setDeviceWakeLock();
        }
        showHideKeyboardLayout(false, enterToExamRoom);
        showMainViewLayout(MainView.ExamRunning);
        currentExamStatus = ExamStatus.Creating;
        examNameText.setText(currentExamName.getName());
        examNameText.setVisibility(View.VISIBLE);
        examNameText.setSelected(true);
        examAction.setVisibility(View.VISIBLE);
        if (isQuestionsManually && !isStartedManualExam)
            MaterialAlert.getInstance(MainActivity.this).show("سؤالات آزمون را تعیین کنید.", MaterialAlert.LENGTH_SHORT);
        else
            MaterialAlert.getInstance(MainActivity.this).show("سؤالات آزمون را ویرایش کنید\nیا اینکه آزمون را شروع کنید.", MaterialAlert.LENGTH_SHORT);
        answerSheetView.setKeepScreenOn(Saver.getInstance(MainActivity.this).getKeepScreenOn());
        addQuestionButton.setVisibility(View.VISIBLE);
        if (questions.size() >= 1) {
            removeQuestionButton.setVisibility(View.VISIBLE);
        }
        if (checkExamHasTime() && examTime != 0) {
            showExamTime();
        }
        startCurrentExam.setVisibility(VISIBLE);
        if (currentExam == null) createNewExam();
        else {
            currentExam.setLoading(false);
            recentExams.updateCurrentExam(currentExam);
        }
        setExamWallpaper();
        setExamDraftBackground(0);
        setDynamicColor(examAnimView, examPictureView, examNameText, examTimeBar, startCurrentExam, runningExamOptions);
        playExamVisualEffects();
    }

    private void setExamWallpaper() {
        List<ExamWallpaper> wallpapers = Saver.getInstance(this).loadExamWallpapers().getWallpapers();
        if (wallpapers.size() != 0) {
            ExamWallpaper selectedWallpaper = null;
            for (ExamWallpaper wallpaper : wallpapers) {
                if (wallpaper.isSelected()) {
                    selectedWallpaper = wallpaper;
                    break;
                }
            }
            for (int i = 0; i < wallpapers.size(); i++) {
                if (wallpapers.get(i).isSelected()) {
                    switch (wallpapers.get(i).getType()) {
                        case Picture:
                            examAnimView.setVisibility(GONE);
                            examPictureView.setVisibility(VISIBLE);
                            if (i == 0) {
                                examPictureView.setImageDrawable(getResources().getDrawable(R.drawable.wallpaper));
                            } else if (i == 1) {
                                examPictureView.setImageDrawable(getResources().getDrawable(R.drawable.wallpaper2));
                            } else if (i == 2) {
                                examPictureView.setImageDrawable(getResources().getDrawable(R.drawable.wallpaper3));
                            } else if (i == 3) {
                                examPictureView.setImageDrawable(getResources().getDrawable(R.drawable.wallpaper4));
                            } else if (i == 4) {
                                examPictureView.setImageDrawable(getResources().getDrawable(R.drawable.wallpaper5));
                            } else if (i == 5) {
                                examPictureView.setImageDrawable(getResources().getDrawable(R.drawable.wallpaper6));
                            }
                            examAnimView.invalidate();
                            break;
                        case Animation:
                            examAnimView.setVisibility(VISIBLE);
                            examPictureView.setVisibility(GONE);
                            if (i - 6 == 0) {
                                examAnimView.setAnimation(R.raw.turning_wave);
                            } else if (i - 6 == 1) {
                                examAnimView.setAnimation(R.raw.turning_wave2);
                            } else if (i - 6 == 2) {
                                examAnimView.setAnimation(R.raw.turning_wave3);
                            } else if (i - 6 == 3) {
                                examAnimView.setAnimation(R.raw.turning_wave4);
                            } else if (i - 6 == 4) {
                                examAnimView.setAnimation(R.raw.turning_wave5);
                            } else if (i - 6 == 5) {
                                examAnimView.setAnimation(R.raw.turning_wave6);
                            } else if (i - 6 == 6) {
                                examAnimView.setAnimation(R.raw.turning_wave7);
                            } else if (i - 6 == 7) {
                                examAnimView.setAnimation(R.raw.turning_wave8);
                            } else if (i - 6 == 8) {
                                examAnimView.setAnimation(R.raw.turning_wave9);
                            }
                            examAnimView.playAnimation();
                            break;
                        default:
                            setDefaultExamWallpaper();
                            break;
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
            setDefaultExamWallpaper();
        }
    }

    private void setDefaultExamWallpaper() {
        examAnimView.setVisibility(GONE);
        examAnimView.invalidate();
        examPictureView.setVisibility(VISIBLE);
        examPictureView.setImageResource(R.drawable.wallpaper);
    }

    private void setExamDraftBackground(int draftIndex) {
        examDraftPage = (CanvasView) examDraftPages.getChildAt(draftIndex);
        examDraftPage.setBaseColor(getResources().getColor(R.color.element_background_color));
    }

    @SuppressLint("WakelockTimeout")
    private void setDeviceWakeLock() {
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, ANSWER_SHEET_EXAM_WAKE_LOCK_TAG);
        wakeLock.acquire();
    }

    private void setExamStartedTime() {
        Date date = new Date();
        int[] jalaliDate = DateConverter.gregorianToJalali((date.getYear() + 1900), (date.getMonth() + 1), date.getDate());
        startedExamTime = printTime(date.getMinutes(), date.getHours()) + " " + printDate(jalaliDate);
        if (currentExam != null)
            currentExam.setStartExamTime(startedExamTime);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateDataSetStatus(int examStatus) {
        if (questionsAdapter != null) {
            for (Question question : questions) {
                if (examStatus == EXAM_ENDED) question.setExamEnded(true);
                else if (examStatus == EXAM_CORRECTION_ENDED) question.setExamCorrectionEnded(true);
            }
            questionsAdapter.notifyDataSetChanged();
        }
    }

    private String printDate(int[] jalaliDate) {
        if (jalaliDate[1] >= TWO_DIGIT_NUM && jalaliDate[2] >= TWO_DIGIT_NUM) {
            return String.format(Locale.getDefault(), "%d/%d/%d", jalaliDate[0], jalaliDate[1], jalaliDate[2]);
        } else if (jalaliDate[1] < TWO_DIGIT_NUM && jalaliDate[2] >= TWO_DIGIT_NUM) {
            if (Locale.getDefault() == Locale.US)
                return String.format(Locale.getDefault(), "%d/0%d/%d", jalaliDate[0], jalaliDate[1], jalaliDate[2]);
            else
                return String.format(Locale.getDefault(), "%d/۰%d/%d", jalaliDate[0], jalaliDate[1], jalaliDate[2]);
        } else if (jalaliDate[1] >= TWO_DIGIT_NUM) {
            if (Locale.getDefault() == Locale.US)
                return String.format(Locale.getDefault(), "%d/%d/0%d", jalaliDate[0], jalaliDate[1], jalaliDate[2]);
            else
                return String.format(Locale.getDefault(), "%d/%d/۰%d", jalaliDate[0], jalaliDate[1], jalaliDate[2]);
        } else {
            if (Locale.getDefault() == Locale.US)
                return String.format(Locale.getDefault(), "%d/0%d/0%d", jalaliDate[0], jalaliDate[1], jalaliDate[2]);
            else
                return String.format(Locale.getDefault(), "%d/۰%d/۰%d", jalaliDate[0], jalaliDate[1], jalaliDate[2]);
        }
    }

    private String printEnglishDate(int[] jalaliDate) {
        if (jalaliDate[1] >= TWO_DIGIT_NUM && jalaliDate[2] >= TWO_DIGIT_NUM) {
            return String.format(Locale.ENGLISH, "%d%d%d", jalaliDate[0], jalaliDate[1], jalaliDate[2]);
        } else if (jalaliDate[1] < TWO_DIGIT_NUM && jalaliDate[2] >= TWO_DIGIT_NUM) {
            return String.format(Locale.ENGLISH, "%d0%d%d", jalaliDate[0], jalaliDate[1], jalaliDate[2]);
        } else if (jalaliDate[1] >= TWO_DIGIT_NUM) {
            return String.format(Locale.ENGLISH, "%d%d0%d", jalaliDate[0], jalaliDate[1], jalaliDate[2]);
        } else {
            return String.format(Locale.ENGLISH, "%d0%d0%d", jalaliDate[0], jalaliDate[1], jalaliDate[2]);
        }
    }

    private String[] printTimeElements(long second, long minute) {
        String out;
        if (second >= TWO_DIGIT_NUM && minute >= TWO_DIGIT_NUM) {
            out = String.format(Locale.getDefault(), "%d:%d", minute, second);
        } else if (second < TWO_DIGIT_NUM && minute >= TWO_DIGIT_NUM) {
            if (Locale.getDefault() == Locale.US)
                out = String.format(Locale.getDefault(), "%d:0%d", minute, second);
            else out = String.format(Locale.getDefault(), "%d:۰%d", minute, second);
        } else if (second >= TWO_DIGIT_NUM) {
            if (Locale.getDefault() == Locale.US)
                out = String.format(Locale.getDefault(), "0%d:%d", minute, second);
            else out = String.format(Locale.getDefault(), "۰%d:%d", minute, second);
        } else {
            if (Locale.getDefault() == Locale.US)
                out = String.format(Locale.getDefault(), "0%d:0%d", minute, second);
            else out = String.format(Locale.getDefault(), "۰%d:۰%d", minute, second);
        }
        return out.split(":");
    }

    private String printTime(long second, long minute) {
        if (second >= TWO_DIGIT_NUM && minute >= TWO_DIGIT_NUM) {
            return String.format(Locale.getDefault(), "%d:%d", minute, second);
        } else if (second < TWO_DIGIT_NUM && minute >= TWO_DIGIT_NUM) {
            if (Locale.getDefault() == Locale.US)
                return String.format(Locale.getDefault(), "%d:0%d", minute, second);
            else return String.format(Locale.getDefault(), "%d:۰%d", minute, second);
        } else if (second >= TWO_DIGIT_NUM) {
            if (Locale.getDefault() == Locale.US)
                return String.format(Locale.getDefault(), "0%d:%d", minute, second);
            else return String.format(Locale.getDefault(), "۰%d:%d", minute, second);
        } else {
            if (Locale.getDefault() == Locale.US)
                return String.format(Locale.getDefault(), "0%d:0%d", minute, second);
            else return String.format(Locale.getDefault(), "۰%d:۰%d", minute, second);
        }
    }

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    private void startExam() {
        isExamStarted = true;
        if (Saver.getInstance(MainActivity.this).getKeepScreenOn()) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            setDeviceWakeLock();
        }
        if (currentExam != null) {
            currentExamStatus = ExamStatus.Started;
            currentExam.setExamStatus(currentExamStatus);
            if (!isRecentExamLoaded && checkExamHasTime() && examTime != 0) {
                currentExam.setExamTime(examTime);
                currentExam.setExamTimeLeft(examTime);
            }
            if (startedExamTime == null) {
                setExamStartedTime();
            }
            setupExamQuestions(questionsCount, lastQuestion, firstQuestion, questionsCPattern);
            int[] examQuestionsCounter = currentExam.getExamQuestionsRange();
            examQuestionsCounter[3] = questions.size();
            currentExam.setExamQuestionsRange(examQuestionsCounter);
            updateCurrentExam();
            questionsAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(this, "آزمون نامشخص!", Toast.WARNING_SIGN, Toast.LENGTH_SHORT).show();
        }
        startCurrentExam.setVisibility(GONE);
        changeExamAction(R.drawable.done_select_questions);
        setControlButtonStates(CButtonState.Clicked, examAction, VISIBLE);
        answerSheetView.setKeepScreenOn(Saver.getInstance(MainActivity.this).getKeepScreenOn());
        if (checkExamHasTime() && examTime != 0) {
            examTimeLeft = new CountDownTimer(examTime, INTERVAL) {

                @SuppressLint("SyntheticAccessor")
                @Override
                public void onTick(long tl) {
                    // TODO: ...
                    //updateCategoryTime();
                    updateExamTime(examTime);
                }

                @SuppressLint("SyntheticAccessor")
                @Override
                public void onFinish() {
                    // TODO: Set exam header collapse bar
                    if (examHeaderCollapsed)
                        collapseExamHeader.getDrawable().setColorFilter(getResources().getColor(R.color.colorAccent), SRC_IN);
                    else
                        collapseExamHeader.getDrawable().setColorFilter(getResources().getColor(R.color.exam_body_movement_bar_color2), SRC_IN);
                    second = 0;
                    minute = 0;
                    MaterialAlert.getInstance(MainActivity.this).show("زمان شما به اتمام رسید!", MaterialAlert.LENGTH_SHORT);
                    endTheExam();
                }
            };
            showExamTime();
            startExamTime();
        }
        if (useChronometer) {
            timerForThinkingTime = new CountDownTimer(1_000_000_000, INTERVAL) {

                @SuppressLint("SyntheticAccessor")
                @Override
                public void onTick(long l) {
                    if (buttonDisableSeconds >= RESET_BUTTON_ENABLE_DELAY) {
                        buttonDisableSeconds = 0;
                        changeResetChronoButtonState(true);
                    }
                    buttonDisableSeconds++;
                    currentExam.setSecondsOfThinkingOnQuestion(currentExam.getSecondsOfThinkingOnQuestion() + 1);
                    currentExam.setAnswerSheet(Saver.getInstance(MainActivity.this).loadQuestions());
                    updateRecentExams();
                }

                @SuppressLint("SyntheticAccessor")
                @Override
                public void onFinish() {
                    timerForThinkingTime.start();
                }
            };
            timerForThinkingTime.start();
            setControlButtonStates(CButtonState.Idle, resetChronometer, VISIBLE);
        } else {
            setControlButtonStates(CButtonState.Disable, resetChronometer, VISIBLE);
        }
        setControlButtonStates(CButtonState.Clicked, addedBookmarksButton, VISIBLE);
        setControlButtonStates(CButtonState.Disable, removeQuestionButton, GONE);
        setControlButtonStates(CButtonState.Disable, addQuestionButton, GONE);
        //prepareExamWorkSpace();
        endTheLastExamLoading();
        updateRecentExams();
        showAnswerSheetLinear();
    }

    private void changeExamAction(int actionId) {
        examAction.setImageResource(actionId);
        setControlButtonStates(CButtonState.Clicked, examAction, VISIBLE);
    }

    private void updateRecentExams() {
        recentExams = Saver.getInstance(MainActivity.this).loadRecentExams();
        recentExams.updateCurrentExam(currentExam);
        Saver.getInstance(MainActivity.this).saveRecentExams(recentExams);
    }

    private void changeResetChronoButtonState(boolean enable) {
        resetEnable = enable;
        if (enable) {
            setControlButtonStates(CButtonState.Clicked, resetChronometer, VISIBLE);
            //resetChronometer.getDrawable().setColorFilter(getResources().getColor(R.color.reset_chronometer_button), SRC_IN);
        } else {
            buttonDisableSeconds = 0;
            setControlButtonStates(CButtonState.Idle, resetChronometer, VISIBLE);
            //resetChronometer.getDrawable().setColorFilter(getResources().getColor(R.color.disable_button), SRC_IN);
        }
    }

    private void endTheExam() {
        if (useChronometer && timerForThinkingTime != null) {
            timerForThinkingTime.onFinish();
            timerForThinkingTime.cancel();
        }
        if (collapseExamHeader.getTag() != null) {
            setCollapseBarAnimation(collapseExamHeader, collapseExamHeader.getTag().equals(ITEM_COLLAPSED));
        }

        startCurrentExam.setVisibility(GONE);
        collapseExamHeader.setVisibility(VISIBLE);
        collapseExamTimeBar.setVisibility(GONE);
        examTimeBoard.setText("--:--");
        examTimeBoard.setTextColor(getResources().getColor(R.color.disable_button));
        setExamTimeLayoutColor(EXAM_TIME_LAYOUT_DEFAULT_COLOR);
        examTimeBar.setProgressWithAnimation(0);
        changeExamAction(R.drawable.reset_exam);
        setControlButtonStates(CButtonState.Idle, resetChronometer, GONE);
        setControlButtonStates(CButtonState.Clicked, addedBookmarksButton, VISIBLE);
        setControlButtonStates(CButtonState.Disable, removeQuestionButton, GONE);
        setControlButtonStates(CButtonState.Disable, addQuestionButton, GONE);
        if (currentExam.isUsedCorrection() && currentExam.isCorrecting() && correctionMode != CorrectionMode.None) {
            if (currentExam.isHasAdditionalScore())
                setControlButtonStates(CButtonState.Clicked, enableNegativePoint, VISIBLE);
            else
                setControlButtonStates(CButtonState.Idle, enableNegativePoint, VISIBLE);
            setControlButtonStates(CButtonState.Disable, shareWorksheetButton, GONE);
            changeExamAction(R.drawable.correcting_exam);
            setControlButtonStates(CButtonState.Clicked, examAction, VISIBLE);
            currentExamStatus = ExamStatus.Correcting;
            currentExam.setExamStatus(currentExamStatus);
            updateRecentExams();
        } else {
            setControlButtonStates(CButtonState.Disable, enableNegativePoint, GONE);
            setControlButtonStates(CButtonState.Clicked, shareWorksheetButton, VISIBLE);
            changeExamAction(R.drawable.reset_exam);
            currentExamStatus = ExamStatus.Finished;
            currentExam.setExamStatus(currentExamStatus);
            updateRecentExams();
        }
        isExamNowEnded = true;
        if (stopWatchEffectPlayer != null) {
            if (stopWatchEffectPlayer.isPlaying()) stopWatchEffectPlayer.stop();
        }
        answerSheetView.scrollToPosition(0);
//        categoryTitle.setVisibility(GONE);
//        categoryTimeRemainingLayout.setVisibility(GONE);
//        categoryScore.setVisibility(GONE);
//        categoryTimeOfThinkingLayout.setVisibility(GONE);
        resetChronometer.setVisibility(GONE);
        currentExam.setExamTimeLeft(0);
        currentExam.setSecondsOfThinkingOnQuestion(0);
        currentExam.setStarted(false);
        currentExam.setLoading(false);
        if (currentExam.isUsedCorrection()) {
            currentExam.setCorrecting(true);
        }
        updateRecentExams();
        showWhiteAnsweredQuestionsCount();
        if (currentExam.isUsedCorrection()) {
            changeExamAction(R.drawable.correcting_exam);
            if (!currentExam.isUsedCorrectionByCorrectAnswers()) {
                MaterialAlert.getInstance(MainActivity.this).show(getString(R.string.exam_end), MaterialAlert.LENGTH_SHORT);
            } else {
                MaterialAlert.getInstance(MainActivity.this).show(getString(R.string.exam_end_c_a), MaterialAlert.LENGTH_SHORT);
            }
            //examAction.setImageResource(R.drawable.correcting_exam);
            updateDataSetStatus(EXAM_ENDED);
            examAction.setOnClickListener(v -> {
                if (questions.size() >= 5) {
                    MaterialAlertDialog ad = new MaterialAlertDialog(MainActivity.this);
                    ad.setCancelable(false);
                    ad.setIcon(R.drawable.correcting_exam);
                    ad.setTitle("اتمام تصحیح آزمون");
                    ad.setMessage("آیا همه سؤالات را تصحیح و بررسی کردید؟!");
                    ad.setNegativeButton("بله", v7 -> {
                        correctedAsNow = true;
                        correctTheExam();
                        ad.dismiss(ad);
                    });
                    ad.setPositiveButton("خیر", v7 -> ad.dismiss(ad));
                    if (currentExam.isUsedCorrectionByCorrectAnswers()) {
                        questions = currentExam.getAnswerSheet().getQuestions();
                        boolean hasNoCorrectedQuestion = false;
                        for (Question q2 : questions) {
                            if (q2.getCorrectAnswerChoice() == 0) {
                                hasNoCorrectedQuestion = true;
                                break;
                            }
                        }
                        if (!hasNoCorrectedQuestion) {
                            ad.show(MainActivity.this);
                        } else {
                            Toast.makeText(this, "نمی توان آزمون را تصحیح کرد!\nزیرا هنوز کلید تمام سؤالات را وارد نکرده اید!", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        questions = currentExam.getAnswerSheet().getQuestions();
                        boolean hasNoCorrectedQuestion = false;
                        for (Question q2 : questions) {
                            if (!q2.isCorrect() && !q2.isWhite() && q2.getCorrectAnswerChoice() == 0) {
                                hasNoCorrectedQuestion = true;
                                break;
                            }
                        }
                        if (!hasNoCorrectedQuestion) {
                            ad.show(MainActivity.this);
                        } else {
                            Toast.makeText(this, "نمی توان آزمون را تصحیح کرد!\nزیرا هنوز گزینه درست، برای سؤالات نادرست را وارد نکرده اید!", Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    Toast.makeText(MainActivity.this, "کمینه تعداد سؤالات، ۵ سؤال است!", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            //examAction.setVisibility(INVISIBLE);
            updateDataSetStatus(EXAM_ENDED);
            updateDataSetStatus(EXAM_CORRECTION_ENDED);
            calculateAverageOfTimeThinking();
            //timeRemaining.setOnClickListener(v1 -> resetExam());
        }
    }

    private void calculateAverageOfTimeThinking() {
        // TODO: Setup this..
    }

    private void showWhiteAnsweredQuestionsCount() {
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
            if (currentExam.getAnswerSheet().getCategories().size() != 0) {
                List<Category> categories = currentExam.getAnswerSheet().getCategories();
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
                        if (runningCategory < currentExam.getAnswerSheet().getCategories().size()) {
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

    private long getHighCriticalTimeLeft(long time) {
        return (time * 3) / 100;
    }

    private long getCriticalTimeLeft(long time) {
        return time / 10;
    }

    private long getWarningTimeLeft(long time) {
        return (time * 4) / 10;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void startExamTime() {
        if (currentExam.isUsedCategorize() && currentExam.isCanCalculateTimeForCategory() && currentExam.getRunningCategory() == -1) {
            int hasCategoryQuestions = 0;
            for (Question question : questions) {
                if (question.getCategory() != null) {
                    hasCategoryQuestions++;
                }
            }
            if (questions.size() == hasCategoryQuestions) {
                long categoriesTime = 0;
                for (Category category : currentExam.getAnswerSheet().getCategories()) {
                    if (category.getTime() != 0) {
                        categoriesTime += category.getTime();
                    }
                }
                if (categoriesTime == currentExam.getExamTime()) {
                    examAction.setVisibility(VISIBLE);
                    currentExam.setEditingCategoryTimes(false);
                    currentExam.setLastScrollPosition(0);
                    updateRecentExams();
                    answerSheetView.scrollToPosition(0);
                    // TODO: Setup Category Docked Header for scrolled category!
                    //categoryHeader.setVisibility(GONE);
                    examTimeLeft.start();
                    startedTimeExam = true;
                } else {
                    examAction.setVisibility(GONE);
                    currentExam.setEditingCategoryTimes(true);
                    updateRecentExams();
                }
                if (questionsAdapter != null)
                    questionsAdapter.notifyDataSetChanged();
            } else {
                examAction.setVisibility(GONE);
                currentExam.setEditingCategoryTimes(true);
                updateRecentExams();
                startedTimeExam = false;
                if (questionsAdapter != null)
                    questionsAdapter.notifyDataSetChanged();
                MaterialAlert.getInstance(MainActivity.this).show("ابتدا سؤالات را دسته بندی کنید!", MaterialAlert.LENGTH_SHORT);
                Log.w("TAG", "All Categories aren't time adjusted yet!");
            }
        } else {
            startedTimeExam = true;
            examTimeLeft.start();
        }
    }

    private void updateExamTime(long time) {
        examTimeLeftUntilFinished = (minute * 60000L) + (second * 1000L);
        if (examTimeLeftUntilFinished <= getCriticalTimeLeft((currentExam != null) ? currentExam.getExamTime() : time)) {
            if (!criticalTimeVibrationRang) {
                if (vibrator.hasVibrator()) {
                    vibrator.vibrate(new long[]{0, 100, 200, 100, 200, 100}, -1);
                }
                criticalTimeVibrationRang = true;
            }
            setExamTimeLayoutColor(getResources().getColor(R.color.error));
            if (examTimeLeftUntilFinished <= getHighCriticalTimeLeft((currentExam != null) ? currentExam.getExamTime() : time)) {
                if (Saver.getInstance(MainActivity.this).getVibrationEffects())
                    vibrator.vibrate(75);
                if (stopWatchEffectPlayer != null) {
                    if (!stopWatchEffectPlayer.isPlaying())
                        stopWatchEffectPlayer.start();
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
            if (examTimeLeftUntilFinished <= getWarningTimeLeft((currentExam != null) ? currentExam.getExamTime() : time)) {
                setExamTimeLayoutColor(getResources().getColor(R.color.edu_level_middle));
                // TODO: Update header collapse bar with timing bar
                /*if (examHeaderCollapsed) {
                    collapseExamHeader.getDrawable().setColorFilter(getResources().getColor(R.color.edu_level_middle), SRC_IN);
                } else {
                    collapseExamHeader.getDrawable().setColorFilter(getResources().getColor(R.color.exam_body_movement_bar_color2), SRC_IN);
                }*/
                if (!warningTimeVibrationRang) {
                    if (vibrator.hasVibrator()) {
                        vibrator.vibrate(new long[]{0, 100, 200, 100}, -1);
                    }
                    warningTimeVibrationRang = true;
                }
            } else {
                setExamTimeLayoutColor(EXAM_TIME_LAYOUT_DEFAULT_COLOR);
            }
        }
        if (second <= 0) {
            showExamTime();
            if (minute >= 1) {
                minute--;
                second = 60;
            }
//            else {
//                currentExam.setExamTimeLeft(0);
//                updateRecentExams();
//                examTimeLeft.cancel();
//                examTimeLeft.onFinish();
//            }
        }
        second--;
        showExamTime();
        Log.d("TAG", "Time millis " + examTimeLeftUntilFinished);
        currentExam.setExamTimeLeft(examTimeLeftUntilFinished);
        if (useChronometer) {
            if (buttonDisableSeconds >= RESET_BUTTON_ENABLE_DELAY) {
                buttonDisableSeconds = 0;
                changeResetChronoButtonState(true);
            }
            buttonDisableSeconds++;
            currentExam.setSecondsOfThinkingOnQuestion(currentExam.getSecondsOfThinkingOnQuestion() + 1);
        }
        updateRecentExams();
    }

    private void showExamTime() {
        collapseExamTimeBar.setProgress((int) calculateRemOfTimePercent());
        examTimeBar.setProgressWithAnimation(calculateRemOfTimePercent());
        examTimeBoard.setAnimationDuration(150);
        examTimeBoard.setCharStrategy(Strategy.SameDirectionAnimation(Direction.SCROLL_UP));
        examTimeBoard.addCharOrder(CharOrder.Number);
        examTimeBoard.setTypeface(ResourcesCompat.getFont(this, R.font.estedad_light));
        examTimeBoard.setAnimationInterpolator(new FastOutSlowInInterpolator());
        examTimeBoard.setText(printTime(second, minute), true);
    }

    private void setExamTimeLayoutColor(int color) {
        if (color != EXAM_TIME_LAYOUT_DEFAULT_COLOR) {
            examTimeBar.setProgressBarColor(color);
            collapseExamTimeBar.setProgressColor(color);
            examTimeBoard.setTextColor(color);
        } else {
            examTimeBar.setProgressBarColor(defaultPaletteColor);
            collapseExamTimeBar.setProgressColor(defaultPaletteColor);
            examTimeBoard.setTextColor(getResources().getColor(R.color.elements_color_tint));
        }
    }

    private float calculateRemOfTimePercent() {
        if (currentExam != null) {
            return ((float) currentExam.getExamTimeLeft() / (float) currentExam.getExamTime()) * 100f;
        } else
            return 100f;
    }

    private void endTheLastExamLoading() {
        if (!currentExam.isStarted() && !currentExam.isCreating()) {
            if (currentExam.isUsedCorrection()) {
                if (currentExam.isChecked()) correctTheExam();
                else endTheExam();
            } else {
                endTheExam();
            }
        }
    }

    private void correctTheExam() {
        currentExamStatus = ExamStatus.Checked;
        currentExam.setExamStatus(currentExamStatus);
        updateRecentExams();
        enableNegativePoint.setVisibility(GONE);
        shareWorksheetButton.setVisibility(VISIBLE);
        // TODO: Setup this...
    }

    private boolean checkExamHasTime() {
        if (currentExam != null)
            return !currentExam.isStarted() || currentExam.isCanCalculateTimeForCategory() || minuteNP.getValue() >= 5 || (currentExam.getExamTime() >= 300_000 && currentExam.getExamTimeLeft() > 0) || examTime > 0;
        else
            return minuteNP.getValue() >= 5;
    }

    private void setTextInputError(@NonNull TextInputLayout textInput, @Nullable String error) {
        if (error == null)
            textInput.setBoxStrokeColor(getResources().getColor(R.color.text_input_bg_color));
        else
            textInput.setBoxStrokeColor(getResources().getColor(R.color.error));
        textInput.setError(error);
    }

    private void setNavigationBarMargin() {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) parentLayout.getLayoutParams();
        params.bottomMargin = getNavigationBarHeight(this);
        parentLayout.setLayoutParams(params);
        Log.i("TAG", "Device nav bar height: " + getNavigationBarHeight(this));
    }

    @SuppressLint("DiscouragedApi")
    public int getNavigationBarHeight(Context c) {
        int result = 0;
        boolean hasMenuKey = ViewConfiguration.get(c).hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
        if (!hasMenuKey && !hasBackKey) {
            Resources resources = c.getResources();
            int orientation = resources.getConfiguration().orientation;
            int resourceId;
            if ((c.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE) {
                resourceId = resources.getIdentifier(orientation == Configuration.ORIENTATION_PORTRAIT ? "navigation_bar_height" : "navigation_bar_height_landscape", "dimen", "android");
            } else {
                resourceId = resources.getIdentifier(orientation == Configuration.ORIENTATION_PORTRAIT ? "navigation_bar_height" : "navigation_bar_width", "dimen", "android");
            }
            if (resourceId > 0) {
                return resources.getDimensionPixelSize(resourceId);
            }
        }
        return result;
    }

    private void playExamVisualEffects() {
        playExamBackgroundAnim();
        setBlurView(this, examControlPanel);
        setBlurView(this, examAnswerSheetEmptyError);
    }

    private void playExamBackgroundAnim() {
        if (checkDeviceSupport()) {
            if (examAnimView.getVisibility() == VISIBLE)
                examAnimView.playAnimation();
        }
    }

    private void setDynamicColor(LottieAnimationView animView, ImageView pictureView, TextView examNameText, CircularProgressBar examTimeRemBar, Button startExam, LinearLayout l) {
        try {
            new Palette.Builder(getBitmapFromView((animView.getVisibility() == VISIBLE) ? animView : pictureView)).generate(palette -> {
                int defLightColor = getResources().getColor(R.color.colorLightAccent2);
                int defColor = getResources().getColor(R.color.colorAccent);
                if (palette != null) {
                    Log.e("TAG", "Dominant Color Palette: " + printColor(palette.getDominantColor(defColor)));
                    Log.i("TAG", "Muted Color Palette: " + printColor(palette.getMutedColor(defColor)));
                    Log.i("TAG", "Vibrant Color Palette: " + printColor(palette.getVibrantColor(defColor)));
                    Log.d("TAG", "Light Muted Color Palette: " + printColor(palette.getLightMutedColor(defColor)));
                    Log.d("TAG", "Light Vibrant Color Palette: " + printColor(palette.getLightVibrantColor(defColor)));
                    Log.w("TAG", "Dark Muted Color Palette: " + printColor(palette.getDarkMutedColor(defColor)));
                    Log.w("TAG", "Dark Vibrant Color Palette: " + printColor(palette.getDarkVibrantColor(defColor)));
                    defaultPaletteColor = setColorPalette(palette, defColor);
                    int dominateColor = palette.getDominantColor(defColor);
                    if (checkDarkModeEnabled(this)) {
                        setStatusBarTheme(this, checkColorBrightness(dominateColor, ColorBrightness.Darken));
                    } else {
                        setStatusBarTheme(this, checkColorBrightness(dominateColor, ColorBrightness.Lighten));
                    }
                    examNameText.setTextColor(defaultPaletteColor);
                    examTimeRemBar.setProgressBarColor(defaultPaletteColor);
                    startExam.setBackgroundTintList(ColorStateList.valueOf(defaultPaletteColor));
                    setMaterialButtonTheme(startExam, checkColorBrightness(defaultPaletteColor));
                    setButtonsPaletteColor();
                    prepareAnswerSheet();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            defaultPaletteColor = getResources().getColor(R.color.colorAccent);
            prepareAnswerSheet();
        }
    }

    private void setMaterialButtonTheme(Button b, boolean light) {
        if (light) {
            b.setTextColor(Color.parseColor("#323232"));
        } else {
            b.setTextColor(Color.parseColor("#f5f5f5"));
        }
    }

    private boolean checkColorBrightness(int color, @NonNull ColorBrightness brightness) {
        double colorDarknessRidge;
        double colorDarkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        switch (brightness) {
            case Darken:
                colorDarknessRidge = 0.25;
                break;
            case Moderate:
                colorDarknessRidge = 0.5;
                break;
            case Lighten:
                colorDarknessRidge = 0.75;
                break;
            default:
                colorDarknessRidge = 0;
                break;
        }
        return colorDarkness < colorDarknessRidge;
    }

    private boolean checkColorBrightness(int color) {
        return checkColorBrightness(color, ColorBrightness.Moderate);
    }

    private void setButtonsPaletteColor() {
        setControlButtonStates(CButtonState.Disable, examAction, VISIBLE);
        setControlButtonStates(CButtonState.Clicked, openDraftBox, VISIBLE);
        setControlButtonStates(CButtonState.Clicked, jumpToQuestion, VISIBLE);
        if (examFile != null)
            setControlButtonStates(CButtonState.Clicked, openExamFileBox, VISIBLE);
        else
            setControlButtonStates(CButtonState.Disable, openExamFileBox, VISIBLE);
        setControlButtonStates(CButtonState.Clicked, addQuestionButton, VISIBLE);
        setControlButtonStates(CButtonState.Clicked, removeQuestionButton, VISIBLE);
    }

    private int setColorPalette(@NonNull Palette palette, int defColor) {
        int vibColor = palette.getVibrantColor(defColor);
        int lightVibColor = palette.getLightVibrantColor(defColor);
        int darkVibColor = palette.getDarkVibrantColor(defColor);
        if (checkDarkModeEnabled(this)) {
            if (lightVibColor != defColor && checkColorBrightness(lightVibColor, ColorBrightness.Lighten)) {
                return lightVibColor;
            } else {
                if (checkColorBrightness(vibColor)) {
                    return vibColor;
                } else {
                    return defColor;
                }
            }
        } else {
            if (darkVibColor != defColor && checkColorBrightness(darkVibColor, ColorBrightness.Darken)) {
                return darkVibColor;
            } else {
                if (checkColorBrightness(vibColor)) {
                    return vibColor;
                } else {
                    return darkVibColor;
                }
            }
        }
    }

    @NonNull
    private String printColor(int color) {
        return String.format("#%06X", (0xFFFFFF & color));
    }

    private Bitmap getBitmapFromView(@NonNull View v) {
        int specWidth = View.MeasureSpec.makeMeasureSpec(0 /* any */, View.MeasureSpec.UNSPECIFIED);
        v.measure(specWidth, specWidth);
        int questionWidth = v.getMeasuredWidth();
        Bitmap b = Bitmap.createBitmap(questionWidth, questionWidth, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
        v.draw(c);
        return b;
    }

    private void setTexts() {
//        String[] minutes = new String[236];
//        minutes[0] = "0";
//        for (int i = 1; i < minutes.length; i++) {
//            minutes[i] = String.valueOf(i + 4);
//        }
//        minuteNP.setDisplayedValues(minutes);
        setTILs();
        setNPParameters(minuteNP);
        setNPParameters(secondNP);
        setNPParameters(chronoThresholdNP);
    }

    private void setTILs() {
        Objects.requireNonNull(questionsCountText.getEditText()).setOnEditorActionListener((v, actionId, event) -> {
            firstQuestionNoText.getEditText().requestFocus();
            return true;
        });
        Objects.requireNonNull(firstQuestionNoText.getEditText()).setOnEditorActionListener((v, actionId, event) -> {
            if (selectedQRandomly)
                lastQuestionNoText.getEditText().requestFocus();
            else
                questionsCPatternText.getEditText().requestFocus();
            return true;
        });
        Objects.requireNonNull(questionsCPatternText.getEditText()).setOnEditorActionListener((v, actionId, event) -> {
            showHideKeyboardLayout(false, v);
            v.clearFocus();
            return true;
        });
        Objects.requireNonNull(lastQuestionNoText.getEditText()).setOnEditorActionListener((v, actionId, event) -> {
            showHideKeyboardLayout(false, v);
            v.clearFocus();
            return true;
        });
    }

    private void setNPParameters(@NonNull NumberPicker np) {
        np.setSelectedTextColor(getResources().getColor(R.color.disable_button));
        np.setOnValueChangedListener((picker, oldVal, newVal) -> {
            if (picker.getId() == R.id.second_number_picker && newVal != 0 && minuteNP.getValue() < 5) {
                minuteNP.setValue(5);
            }
            setEnabledRowButton(useCategoryTimingEnabled, checkExamHasTime(), useCategoryTiming);
            setNPTextColor(np, newVal);
            vibrator.vibrate(NP_VALUE_CHANGE_VIBRATION);
        });
        np.setTypeface(ResourcesCompat.getFont(this, R.font.estedad_light));
        np.setSelectedTypeface(ResourcesCompat.getFont(this, R.font.estedad_light));
    }

    private void setNPTextColor(NumberPicker np, int value) {
        int color;
        if (value != 0) {
            color = getResources().getColor(R.color.colorAccent);
        } else {
            color = getResources().getColor(R.color.disable_button);
        }
        np.setSelectedTextColor(color);
        if (checkExamHasTime()) {
            minuteNP.setSelectedTextColor(getResources().getColor(R.color.colorAccent));
            secondNP.setSelectedTextColor(getResources().getColor(R.color.colorAccent));
            setupExamTimeColon.setTextColor(getResources().getColor(R.color.colorAccent));
            setupExamTimeColon.setAlpha(1f);
        } else {
            minuteNP.setSelectedTextColor(getResources().getColor(R.color.disable_button));
            secondNP.setSelectedTextColor(getResources().getColor(R.color.disable_button));
            setupExamTimeColon.setTextColor(getResources().getColor(R.color.disable_button));
            setupExamTimeColon.setAlpha(0.7f);
        }
        refreshNPs();
    }

    private void refreshNPs() {
        minuteNP.scrollTo(0, 10);
        minuteNP.scrollTo(0, 0);
        secondNP.scrollTo(0, 10);
        secondNP.scrollTo(0, 0);
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "ClickableViewAccessibility"})
    private void onClicks() {
        setToggleButtonsClick(selectQuestionsMode);
        setToggleButtonsClick(selectCorrectionMode);
        setNormalButtonsClicks();
        setDividerButtonsClicks();
        setRowToggleButtonsClicks();
        setExamsListClicks();
    }

    private void setDividerButtonsClicks() {
        //collapseExamTimeBar.initLayout(); // TODO: For last build time
        collapseExamTimeBar.setOnClickListener(this::setControlPanelCollapseBar);
        collapseExamHeader.setOnClickListener(this::setControlPanelCollapseBar);
        collapseDraftView.setOnClickListener(v -> setDefaultCollapseBar(v, examDraftLayout, CollapseBarMode.Fullscreen, null));
    }

    private void setDefaultCollapseBar(@NonNull View v, @NonNull ViewGroup parent, @NonNull CollapseBarMode mode, @Nullable CollapseBarChangedListener collapseBarChangedListener) {
        View root = LayoutInflater.from(MainActivity.this).inflate(R.layout.popup_collapse_options, parent, false);
        BlurView collapseViewCard = root.findViewById(R.id.collapse_options_card);
        ImageButton closeWindow = root.findViewById(R.id.close_window);
        ImageButton collapseWindow = root.findViewById(R.id.collapse_window);
        ImageButton fullscreenWindow = root.findViewById(R.id.fullscreen_window);
        PopupWindow window = new PopupWindow(root, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
        String tag = (String) ((v.getTag() != null) ? v.getTag() : "");
        closeWindow.setOnClickListener(v2 -> {
            window.dismiss();
            if (parent.getVisibility() == VISIBLE)
                parent.setVisibility(GONE);
            if (collapseBarChangedListener != null)
                collapseBarChangedListener.onClosed(v, parent);
        });
        setImageButtonEnableStatus(closeWindow, !(mode != CollapseBarMode.None && tag.equals(ITEM_FULLSCREEN)), true);
        if (mode != CollapseBarMode.None) {
            if (tag.equals(ITEM_FULLSCREEN)) {
                setImageButtonEnableStatus(fullscreenWindow, false, false);
                setImageButtonEnableStatus(collapseWindow, true, false);
                collapseWindow.setOnClickListener(v2 -> {
                    window.dismiss();
                    v.setTag(null);
                    restoreControlPanel();
                    answerSheetLayout.setVisibility(VISIBLE);
                    if (collapseBarChangedListener != null)
                        collapseBarChangedListener.onRestored(v, parent);
                });
            } else if (tag.equals(ITEM_COLLAPSED)) {
                setImageButtonEnableStatus(fullscreenWindow, true, false);
                setImageButtonEnableStatus(collapseWindow, false, false);
                fullscreenWindow.setOnClickListener(v2 -> {
                    window.dismiss();
                    v.setTag(null);
                    if (collapseBarChangedListener != null)
                        collapseBarChangedListener.onRestored(v, parent);
                });
            } else {
                setImageButtonEnableStatus(fullscreenWindow, mode == CollapseBarMode.Both || mode == CollapseBarMode.Fullscreen, false);
                setImageButtonEnableStatus(collapseWindow, mode == CollapseBarMode.Both || mode == CollapseBarMode.Collapse, false);
                fullscreenWindow.setOnClickListener(v2 -> {
                    window.dismiss();
                    v.setTag(ITEM_FULLSCREEN);
                    collapseControlPanel(collapseExamHeader);
                    answerSheetLayout.setVisibility(GONE);
                    if (collapseBarChangedListener != null)
                        collapseBarChangedListener.onFullscreen(v, parent);
                });
                collapseWindow.setOnClickListener(v2 -> {
                    window.dismiss();
                    v.setTag(ITEM_COLLAPSED);
                    setCollapseBarAnimation((ImageButton) v, true);
                    if (collapseBarChangedListener != null)
                        collapseBarChangedListener.onCollapsed(v, parent);
                });
            }
        } else {
            setImageButtonEnableStatus(fullscreenWindow, false, false);
            setImageButtonEnableStatus(collapseWindow, false, false);
        }
        window.setElevation(30f);
        setBlurView(this, collapseViewCard);
        window.showAsDropDown(v, -125, -200, Gravity.CENTER);
    }

    private void setControlPanelCollapseBar(View v) {
        View root = LayoutInflater.from(MainActivity.this).inflate(R.layout.popup_collapse_options, examControlPanel, false);
        BlurView collapseViewCard = root.findViewById(R.id.collapse_options_card);
        ImageButton closeWindow = root.findViewById(R.id.close_window);
        ImageButton collapseWindow = root.findViewById(R.id.collapse_window);
        ImageButton fullscreenWindow = root.findViewById(R.id.fullscreen_window);
        PopupWindow window = new PopupWindow(root, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
        String tag = (String) ((collapseExamHeader.getTag() != null) ? collapseExamHeader.getTag() : "");
        setImageButtonEnableStatus(closeWindow, false, true);
        closeWindow.setOnClickListener(v2 -> Toast.makeText(this, "کنترل پنل را نمی توان بست!", Toast.WARNING_SIGN, Toast.LENGTH_SHORT).show());
        if (!tag.equals(ITEM_COLLAPSED)) {
            setImageButtonEnableStatus(fullscreenWindow, false, false);
            setImageButtonEnableStatus(collapseWindow, true, false);
            fullscreenWindow.setOnClickListener(v2 -> Toast.makeText(this, "کنترل پنل را نمی توان بزرگ کرد!", Toast.WARNING_SIGN, Toast.LENGTH_SHORT).show());
            collapseWindow.setOnClickListener(v2 -> {
                window.dismiss();
                collapseControlPanel(v);
            });
        } else {
            setImageButtonEnableStatus(fullscreenWindow, true, false);
            setImageButtonEnableStatus(collapseWindow, false, false);
            collapseWindow.setOnClickListener(v2 -> Toast.makeText(this, "کنترل پنل کوچک شده است.", Toast.WARNING_SIGN, Toast.LENGTH_SHORT).show());
            fullscreenWindow.setOnClickListener(v2 -> {
                window.dismiss();
                restoreControlPanel();
            });
        }
        window.setElevation(30f);
        setBlurView(this, collapseViewCard);
        window.showAsDropDown(v, -50, 0, Gravity.CENTER);
    }

    private void collapseControlPanel(View v) {
        collapseExamHeader.setTag(ITEM_COLLAPSED);
        examNameText.setVisibility(GONE);
        examTimeBar.setVisibility(GONE);
        examTimeBoard.setVisibility(GONE);
        runningExamOptions.setVisibility(GONE);
        startCurrentExam.setVisibility(GONE);
        if (currentExam != null) {
            if (currentExam.isUsedTiming() && checkExamHasTime() && examTime != 0) {
                collapseExamHeader.setVisibility(GONE);
                collapseExamTimeBar.setVisibility(VISIBLE);
                collapseExamTimeBar.setProgress((int) calculateRemOfTimePercent());
            } else {
                setCollapseBarAnimation(collapseExamHeader, true);
                collapseExamHeader.setVisibility(VISIBLE);
                collapseExamTimeBar.setVisibility(GONE);
            }
        } else {
            setCollapseBarAnimation(collapseExamHeader, true);
            collapseExamHeader.setVisibility(VISIBLE);
            collapseExamTimeBar.setVisibility(GONE);
        }
    }

    private void restoreControlPanel() {
        if (collapseExamHeader.getTag() != null) {
            collapseExamHeader.setTag(null);
            examNameText.setVisibility(VISIBLE);
            examTimeBar.setVisibility(VISIBLE);
            examTimeBoard.setVisibility(VISIBLE);
            runningExamOptions.setVisibility(VISIBLE);
            if (isExamStarted)
                startCurrentExam.setVisibility(GONE);
            else
                startCurrentExam.setVisibility(VISIBLE);
            if (currentExam != null) {
                if (!currentExam.isUsedTiming() || !checkExamHasTime() || examTime == 0) {
                    setCollapseBarAnimation(collapseExamHeader, false);
                }
            } else {
                setCollapseBarAnimation(collapseExamHeader, false);
            }
            collapseExamTimeBar.setVisibility(GONE);
            collapseExamHeader.setVisibility(VISIBLE);
        }
    }

    private void setImageButtonEnableStatus(@NonNull ImageButton button, boolean enabled, boolean reverseColor) {
        button.setEnabled(enabled);
        int colorId = (reverseColor) ? R.color.elements_color_tint_rev : R.color.elements_color_tint;
        if (enabled) {
            button.getDrawable().setColorFilter(getResources().getColor(colorId), SRC_IN);
            button.setAlpha(1f);
        } else {
            button.getDrawable().setColorFilter(getResources().getColor(R.color.disable_button_fade), SRC_IN);
            button.setAlpha(0.88f);
        }
    }

    private void setCollapseBarAnimation(ImageButton collapseBar, boolean collapsed) {
        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) collapseBar.getLayoutParams();
        ValueAnimator v;
        if (collapsed) {
            v = ValueAnimator.ofInt(lp.width, lp.width + 50);
            v.setDuration(250);
            v.setStartDelay(50);
            v.addUpdateListener(animation -> {
                lp.width = (int) animation.getAnimatedValue();
                collapseBar.setLayoutParams(lp);
                collapseBar.setAlpha(1f);
                collapseBar.getDrawable().setColorFilter(defaultPaletteColor, SRC_IN);
            });
        } else {
            v = ValueAnimator.ofInt(lp.width, lp.width - 50);
            v.setDuration(250);
            v.setStartDelay(50);
            v.addUpdateListener(animation -> {
                lp.width = (int) animation.getAnimatedValue();
                collapseBar.setLayoutParams(lp);
                collapseBar.setAlpha(0.9f);
                collapseBar.getDrawable().setColorFilter(getResources().getColor(R.color.disable_button_fade), SRC_IN);
            });
        }
        v.start();
    }

    private boolean checkToggleButtonChecked(@NonNull LinearLayout toggleButton) {
        return toggleButton.getBackgroundTintList().equals(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
    }

    private void vibrateDevice(long[] pattern) {
        if (Saver.getInstance(this).getVibrationEffects()) {
            if (vibrator.hasVibrator()) {
                vibrator.vibrate(pattern, -1);
            }
        }
    }

    private void vibrateDevice(long millis) {
        if (Saver.getInstance(this).getVibrationEffects()) {
            if (vibrator.hasVibrator()) {
                vibrator.vibrate(millis);
            }
        }
    }

    private int generateExamId() {
        int id = random.nextInt(EXAM_ID_RANGE_MAX);
        if (id >= EXAM_ID_RANGE_MIN) {
            boolean isConflictID = false;
            for (Exam e : recentExams.getExamList()) {
                if (e.getId() == id) {
                    isConflictID = true;
                    break;
                }
            }
            if (!isConflictID) return id;
            else return generateExamId();
        } else {
            return generateExamId();
        }
    }

    private void setupExamQuestions(int questionsCounter, int questionTo, int questionFrom, int questionCountPattern) {
        currentExam.setExamQuestionsRange(new int[]{questionFrom, checkToggleButtonChecked(selectQuestionsRandomly) ? questionTo : 0, questionsCounter, questionCountPattern});
        if (questions != null) {
            currentExam.setAnswerSheet(new Questions(questions));
            recentExams.updateCurrentExam(currentExam);
            Saver.getInstance(MainActivity.this).saveRecentExams(recentExams);
        }
    }

    private void setupExamTime(long time) {
        if (checkExamHasTime() && time != 0) {
            currentExam.setExamTime(time);
            currentExam.setExamTimeLeft(time);
        }
    }

    private void setupExamOptions() {
        currentExam.setUsedChronometer(useChronometer);
        currentExam.setChornoThreshold(chronoThreshold);
        currentExam.setUsedRandomQuestions(selectedQRandomly);
        currentExam.setUsedCategorize(useExamCategorize);
        currentExam.setUsedCorrection(correctionMode != CorrectionMode.None);
        currentExam.setCanCalculateTimeForCategory(useCategoryTiming);
        currentExam.setUsedCorrectionByCorrectAnswers(correctionMode == CorrectionMode.Keys);
        currentExam.setUsedTiming(checkExamHasTime());
        currentExam.setCanCalculateScoreOfCategory(useCategoryScore);
        currentExam.setStartExamTime(startedExamTime);
        currentExam.setSelectQuestionsManually(isQuestionsManually);
    }

    private void setupExamFile() {
        if (examFile != null) {
            currentExam.setExamFile(examFile);
        }
    }

    private void setupExamStatus(ExamStatus status) {
        switch (status) {
            case Creating:
                currentExam.setCreating(true);
                currentExam.setStarted(false);
                currentExam.setChecked(false);
                currentExam.setCorrecting(false);
                currentExam.setSuspended(false);
                break;
            case Started:
                currentExam.setCreating(false);
                currentExam.setStarted(true);
                currentExam.setChecked(false);
                currentExam.setCorrecting(false);
                currentExam.setSuspended(false);
                break;
            case Suspended:
                currentExam.setCreating(false);
                currentExam.setStarted(false);
                currentExam.setChecked(false);
                currentExam.setCorrecting(false);
                currentExam.setSuspended(true);
                break;
            case Finished:
                currentExam.setCreating(false);
                currentExam.setStarted(false);
                currentExam.setChecked(false);
                currentExam.setCorrecting(false);
                currentExam.setSuspended(false);
                break;
            case Correcting:
                currentExam.setCreating(false);
                currentExam.setStarted(false);
                currentExam.setChecked(false);
                currentExam.setCorrecting(true);
                currentExam.setSuspended(false);
                break;
            case Checked:
                currentExam.setCreating(false);
                currentExam.setStarted(false);
                currentExam.setChecked(true);
                currentExam.setCorrecting(false);
                currentExam.setSuspended(false);
                break;
            default:
                break;
        }
        if (currentExamStatus != null && currentExam != null)
            currentExam.setExamStatus(currentExamStatus);
    }

    private void createNewExam() {
        currentExam = new Exam();
        currentExam.setId(generateExamId());
        currentExam.setExamName(currentExamName);
        setupExamTime(examTime);
        setupExamFile();
        setupExamOptions();
        setupExamStatus(currentExamStatus);
        //suspendAnotherLiveExams();
        recentExams.addExam(currentExam);
        Saver.getInstance(MainActivity.this).saveRecentExams(recentExams);
    }

    /**
     * This update operation has not been consistent of questions!
     */
    private void updateCurrentExam() {
        setupExamTime(examTime);
        setupExamFile();
        setupExamOptions();
        setupExamStatus(currentExamStatus);
    }

    @SuppressLint({"NotifyDataSetChanged", "ClickableViewAccessibility"})
    private void setNormalButtonsClicks() {
        setExamDraftTouchEvents();
        addDraftPage.setOnClickListener(v -> {
            currentDraftViewIndex++;
            examDraftPages.addView(new CanvasView(this), currentDraftViewIndex, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER));
            ((CanvasView) examDraftPages.getChildAt(currentDraftViewIndex)).setBaseColor(getResources().getColor(R.color.element_background_color));
            setupDraftPenStrokeColor();
            clearDraftCanvas();
            setExamDraftTouchEvents();
            updateDraftPagesCount();
        });
        draftPagesOptions.setOnClickListener(v2 -> {
            if (examDraftPages.getChildCount() >= 2) {
                View v = LayoutInflater.from(this).inflate(R.layout.popup_draft_page_options, null, false);
                PopupWindow window = new PopupWindow(v, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
                ImageButton backwardPage = v.findViewById(R.id.goto_previous_page);
                ImageButton forwardPage = v.findViewById(R.id.goto_next_page);
                TextView currentPageNumber = v.findViewById(R.id.current_page_number);
                ImageButton deletePage = v.findViewById(R.id.delete_current_page);
                currentPageNumber.setText(getString(R.string.page_counter_text, currentDraftViewIndex + 1, examDraftPages.getChildCount()));
                setImageButtonEnableStatus(backwardPage, currentDraftViewIndex > 0, false);
                setImageButtonEnableStatus(forwardPage, currentDraftViewIndex < examDraftPages.getChildCount() - 1, false);
                setImageButtonEnableStatus(deletePage, currentDraftViewIndex != 0, false);
                if (deletePage.isEnabled())
                    deletePage.getDrawable().setColorFilter(getResources().getColor(R.color.edu_level_bad), SRC_IN);
                backwardPage.setOnClickListener(v3 -> {
                    window.dismiss();
                    if (currentDraftViewIndex > 0) {
                        currentDraftViewIndex--;
                        updateDraftPage(currentDraftViewIndex);
                        currentPageNumber.setText(getString(R.string.page_counter_text, currentDraftViewIndex + 1, examDraftPages.getChildCount()));
                    }
                });
                forwardPage.setOnClickListener(v3 -> {
                    window.dismiss();
                    if (currentDraftViewIndex < examDraftPages.getChildCount() - 1) {
                        currentDraftViewIndex++;
                        updateDraftPage(currentDraftViewIndex);
                        currentPageNumber.setText(getString(R.string.page_counter_text, currentDraftViewIndex + 1, examDraftPages.getChildCount()));
                    }
                });
                deletePage.setOnClickListener(v3 -> {
                    window.dismiss();
                    if (currentDraftViewIndex > 0) {
                        MaterialAlertDialog dialog = new MaterialAlertDialog(this);
                        dialog.setIcon(R.drawable.delete);
                        dialog.setTitle("حذف چرک نویس");
                        dialog.setMessage("آیا از حذف صفحه " + (currentDraftViewIndex + 1) + " چرک نویس اطمینان دارید؟\nهمه داده های داخل آن حذف خواهد شد!");
                        dialog.setPositiveButton("بله", v4 -> {
                            dialog.dismiss(dialog);
                            examDraftPages.removeViewAt(currentDraftViewIndex);
                            currentDraftViewIndex--;
                            updateDraftPage(currentDraftViewIndex);
                            currentPageNumber.setText(getString(R.string.page_counter_text, currentDraftViewIndex + 1, examDraftPages.getChildCount()));
                            Toast.makeText(this, "صفحه حذف شد.", Toast.WARNING_SIGN, Toast.LENGTH_SHORT).show();
                            updateDraftPagesCount();
                        });
                        dialog.setNegativeButton("خیر", v4 -> dialog.dismiss(dialog));
                        dialog.show(this);
                    } else {
                        Toast.makeText(this, "صفحه اصلی چرک نویس، قابل حذف نیست!", Toast.WARNING_SIGN, Toast.LENGTH_SHORT).show();
                    }
                });
                window.setElevation(30f);
                window.showAsDropDown(v2, -250, -250);
            }
        });
        draftViewOptions.setOnClickListener(v9 -> {
            if (examDraftPages.getChildCount() >= 1 && examDraftPages.getVisibility() == VISIBLE) {
                examDraftPage = (CanvasView) examDraftPages.getChildAt(currentDraftViewIndex);
                if (examDraftPage != null) {
                    View v = LayoutInflater.from(MainActivity.this).inflate(R.layout.popup_draft_toolbox, null);
                    draftToolboxItems = v.findViewById(R.id.toolbox_items_layout);
                    if (!examFileVisibility) {
                        if (draftPathErasingEnabled) {
                            ((ImageButton) draftToolboxItems.getChildAt(3)).setImageResource(R.drawable.search_by_exam_correcting);
                            ((ImageButton) draftToolboxItems.getChildAt(3)).getDrawable().setColorFilter(getResources().getColor(R.color.colorAccent), SRC_IN);
                        } else {
                            ((ImageButton) draftToolboxItems.getChildAt(3)).setImageResource(R.drawable.draft_pen_eraser);
                            ((ImageButton) draftToolboxItems.getChildAt(3)).getDrawable().setColorFilter(getResources().getColor(R.color.elements_color_tint), SRC_IN);
                        }
                    } else {
                        ((ImageButton) draftToolboxItems.getChildAt(3)).setImageResource(R.drawable.delete);
                        ((ImageButton) draftToolboxItems.getChildAt(3)).getDrawable().setColorFilter(getResources().getColor(R.color.elements_color_tint), SRC_IN);
                    }
                    if (examFile != null && examFileLoaded) {
                        //draftToolboxItems.getChildAt(4).setVisibility(VISIBLE);
                        if (examFileVisibility) {
                            ((ImageButton) draftToolboxItems.getChildAt(4)).setImageResource(R.drawable.exam_file_visibility_off);
                            ((ImageButton) draftToolboxItems.getChildAt(4)).getDrawable().setColorFilter(getResources().getColor(R.color.colorAccent), SRC_IN);
                        } else {
                            ((ImageButton) draftToolboxItems.getChildAt(4)).setImageResource(R.drawable.exam_file_visibility);
                            ((ImageButton) draftToolboxItems.getChildAt(4)).getDrawable().setColorFilter(getResources().getColor(R.color.elements_color_tint), SRC_IN);
                        }
                    } else {
                        //draftToolboxItems.getChildAt(4).setVisibility(GONE);
                    }
                    ((ImageButton) draftToolboxItems.getChildAt(0)).setImageResource(selectedModeResId);
                    ((ImageButton) draftToolboxItems.getChildAt(1)).getDrawable().setColorFilter(selectedColor, SRC_IN);
                    if (!isDraftCanvasCleared) {
                        draftToolboxItems.getChildAt(5).setVisibility(VISIBLE);
                    } else {
                        draftToolboxItems.getChildAt(5).setVisibility(GONE);
                    }
                    PopupWindow window = new PopupWindow(v, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
                    window.setClippingEnabled(false);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        window.setElevation(30f);
                    }
                    draftToolboxItems.getChildAt(3).setOnLongClickListener(view12 -> {
                        if (!examFileVisibility && isDraftDrawingHintShown) {
                            window.dismiss();
                            if (vibrator.hasVibrator()) {
                                vibrator.vibrate(50);
                            }
                            clearDraftCanvas();
                            draftPathErasingEnabled = false;
                            Toast.makeText(MainActivity.this, "صفحه چرک نویس، پاکسازی شد.", Toast.WARNING_SIGN, Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    });
                    for (draftToolboxItemIndex = 0; draftToolboxItemIndex < draftToolboxItems.getChildCount(); draftToolboxItemIndex++) {
                        draftToolboxItems.getChildAt(draftToolboxItemIndex).setOnClickListener(v1 -> {
                            if (v1.getId() == R.id.show_draft_options) {
                                window.dismiss();
                                View v3 = LayoutInflater.from(MainActivity.this).inflate(R.layout.popup_draft_options, null);
                                LinearLayout draftOptions = v3.findViewById(R.id.drawing_options_layout);
                                PopupWindow window3 = new PopupWindow(v3, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
                                window3.setClippingEnabled(true);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    window3.setElevation(30f);
                                }
                                dismissDrawOptionsWindowManually = false;
                                updateDraftOptionsStatus(draftOptions);
                                for (draftOptionsItemIndex = 0; draftOptionsItemIndex < draftOptions.getChildCount(); draftOptionsItemIndex++) {
                                    draftOptions.getChildAt(draftOptionsItemIndex).setOnClickListener(view13 -> {
                                        if (view13.getId() == R.id.save_draft) {
                                            dismissDrawOptionsWindowManually = true;
                                            window3.dismiss();
                                            saveDraftScreenshot();
                                        } else if (view13.getId() == R.id.undo_changes) {
                                            examDraftPage.undo();
                                            if (!examDraftPage.canUndo() && !examFileVisibility) {
                                                draftDrawingHint.setVisibility(VISIBLE);
                                                isDraftDrawingHintShown = false;
                                            }
                                            updateDraftOptionsStatus(draftOptions);
                                        } else if (view13.getId() == R.id.redo_changes) {
                                            examDraftPage.redo();
                                            if (examDraftPage.canUndo() && !examFileVisibility) {
                                                draftDrawingHint.setVisibility(GONE);
                                                isDraftDrawingHintShown = true;
                                            }
                                            updateDraftOptionsStatus(draftOptions);
                                        } else if (view13.getId() == R.id.close_window) {
                                            dismissDrawOptionsWindowManually = true;
                                            window3.dismiss();
                                        }
                                    });
                                }
                                try {
                                    window3.setOnDismissListener(() -> {
                                        if (!dismissDrawOptionsWindowManually)
                                            window3.showAsDropDown(draftViewOptions, 0, -300);
                                    });
                                    window3.showAsDropDown(draftViewOptions, 0, -300);
                                } catch (Exception e) {
                                    Toast.makeText(this, "درخواست شما اجرا نشد!", Toast.WARNING_SIGN, Toast.LENGTH_SHORT).show();
                                }
                            } else if (v1.getId() == R.id.select_drawing_mode) {
                                if (!draftPathErasingEnabled) {
                                    View v4 = LayoutInflater.from(MainActivity.this).inflate(R.layout.popup_drawing_modes, null);
                                    LinearLayout modes = v4.findViewById(R.id.drawing_modes_layout);
                                    PopupWindow window1 = new PopupWindow(v4, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
                                    window1.setClippingEnabled(false);
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        window1.setElevation(30f);
                                    }
                                    for (penModelIndex = 0; penModelIndex < modes.getChildCount(); penModelIndex++) {
                                        modes.getChildAt(penModelIndex).setOnClickListener(view2 -> {
                                            int currentDrawerModeResId = selectedModeResId;
                                            if (view2.getId() == R.id.mode_circle) {
                                                selectedMode = CanvasView.Drawer.ELLIPSE;
                                                selectedModeResId = R.drawable.circle;
                                                selectedDrawingMode = CanvasView.Mode.DRAW;
                                                selectedStyle = Paint.Style.STROKE;
                                            } else if (view2.getId() == R.id.mode_rectangle) {
                                                selectedMode = CanvasView.Drawer.RECTANGLE;
                                                selectedModeResId = R.drawable.rectangle;
                                                selectedDrawingMode = CanvasView.Mode.DRAW;
                                                examDraftPage.setPaintStyle(Paint.Style.STROKE);
                                            } else if (view2.getId() == R.id.mode_line) {
                                                selectedMode = CanvasView.Drawer.LINE;
                                                selectedModeResId = R.drawable.remove_object;
                                                selectedDrawingMode = CanvasView.Mode.DRAW;
                                                selectedStyle = Paint.Style.STROKE;
                                            } else if (view2.getId() == R.id.mode_path) {
                                                selectedMode = CanvasView.Drawer.PEN;
                                                selectedDrawingMode = CanvasView.Mode.DRAW;
                                                selectedModeResId = R.drawable.path_drawing;
                                                selectedStyle = Paint.Style.STROKE;
                                            } else if (view2.getId() == R.id.mode_text) {
                                                window1.dismiss();
                                                window.dismiss();
                                                showSubmitTextWindow();
                                            } else if (view2.getId() == R.id.mode_circle_filled) {
                                                selectedMode = CanvasView.Drawer.ELLIPSE;
                                                selectedModeResId = R.drawable.circle_filled;
                                                selectedDrawingMode = CanvasView.Mode.DRAW;
                                                selectedStyle = Paint.Style.FILL_AND_STROKE;
                                            } else if (view2.getId() == R.id.mode_rectangle_filled) {
                                                selectedMode = CanvasView.Drawer.RECTANGLE;
                                                selectedModeResId = R.drawable.rectangle_filled;
                                                selectedDrawingMode = CanvasView.Mode.DRAW;
                                                selectedStyle = Paint.Style.FILL_AND_STROKE;
                                            }
                                            if (currentDrawerModeResId != selectedModeResId) {
                                                ((ImageButton) draftToolboxItems.getChildAt(0)).setImageResource(selectedModeResId);
                                                examDraftPage.setDrawer(selectedMode);
                                                examDraftPage.setMode(selectedDrawingMode);
                                                examDraftPage.setPaintStyle(selectedStyle);
                                                window1.dismiss();
                                                window.dismiss();
                                                Toast.makeText(this, "حالت قلم تغییر یافت.", Toast.WARNING_SIGN, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                    try {
                                        window1.showAsDropDown(v, 0, 30);
                                    } catch (Exception e) {
                                        Toast.makeText(this, "درخواست شما اجرا نشد!", Toast.WARNING_SIGN, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } else if (v1.getId() == R.id.delete_pen_strokes) {
                                if (isDraftDrawingHintShown) {
                                    window.dismiss();
                                    if (!examFileVisibility) {
                                        if (!draftPathErasingEnabled) {
                                            ((ImageButton) draftToolboxItems.getChildAt(3)).setImageResource(R.drawable.search_by_exam_correcting);
                                            ((ImageButton) draftToolboxItems.getChildAt(3)).getDrawable().setColorFilter(getResources().getColor(R.color.colorAccent), SRC_IN);
                                            examDraftPage.setPaintStrokeColor(getResources().getColor(R.color.element_background_color));
                                            examDraftPage.setDrawer(CanvasView.Drawer.PEN);
                                            examDraftPage.setPaintStrokeWidth(draftEraserStrokeSize);
                                            examDraftPage.setMode(CanvasView.Mode.DRAW);
                                            examDraftPage.setPaintStyle(Paint.Style.STROKE);
                                            draftPathErasingEnabled = true;
                                        } else {
                                            ((ImageButton) draftToolboxItems.getChildAt(3)).setImageResource(R.drawable.draft_pen_eraser);
                                            ((ImageButton) draftToolboxItems.getChildAt(3)).getDrawable().setColorFilter(getResources().getColor(R.color.elements_color_tint), SRC_IN);
                                            examDraftPage.setMode(selectedDrawingMode);
                                            examDraftPage.setPaintStrokeColor(selectedColor);
                                            examDraftPage.setPaintStrokeWidth(draftPenStrokeSize);
                                            examDraftPage.setDrawer(selectedMode);
                                            examDraftPage.setPaintStyle(selectedStyle);
                                            draftPathErasingEnabled = false;
                                        }
                                    } else {
                                        clearDraftCanvas();
                                        draftPathErasingEnabled = false;
                                    }
                                }
                            } else if (v1.getId() == R.id.change_pen_stroke) {
                                View v2 = LayoutInflater.from(MainActivity.this).inflate(R.layout.popup_pen_stroke, null);
                                EditText strokeSize = v2.findViewById(R.id.pen_stroke_text);
                                ImageButton strokeSubmit = v2.findViewById(R.id.pen_stroke_submit);
                                PopupWindow window2 = new PopupWindow(v2, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
                                window2.setClippingEnabled(false);
                                window2.setElevation(30f);
                                if (isDraftEraserStrokeSizeEdited && draftPathErasingEnabled) {
                                    strokeSize.setText(String.valueOf((int) draftEraserStrokeSize));
                                } else {
                                    if (isDraftPenStrokeSizeEdited && !draftPathErasingEnabled) {
                                        strokeSize.setText(String.valueOf((int) draftPenStrokeSize));
                                    } else strokeSize.setText("");
                                }
                                if (draftPathErasingEnabled) {
                                    strokeSize.setHint("ضخامت پاک کن");
                                } else {
                                    strokeSize.setHint("ضخامت قلم");
                                }
                                strokeSize.addTextChangedListener(new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                    }

                                    @Override
                                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                        strokeSize.setError(null);
                                    }

                                    @Override
                                    public void afterTextChanged(Editable editable) {
                                    }
                                });
                                strokeSubmit.setOnClickListener(v3 -> {
                                    if (strokeSize.getText().length() != 0) {
                                        int wantedStrokeSize = Integer.parseInt(strokeSize.getText().toString());
                                        if (wantedStrokeSize >= 1) {
                                            if (!draftPathErasingEnabled) {
                                                if (wantedStrokeSize <= MAX_OF_PEN_STROKE) {
                                                    if (wantedStrokeSize != draftPenStrokeSize || !isDraftPenStrokeSizeEdited) {
                                                        setPenStroke(strokeSize, window2, window, wantedStrokeSize);
                                                    } else {
                                                        strokeSize.setError("نباید با مقدار فعلی برابر باشد!");
                                                    }
                                                } else {
                                                    strokeSize.setError("حداکثر ضخامت قلم، ۱۰۰ می باشد!");
                                                }
                                            } else {
                                                if (wantedStrokeSize <= MAX_OF_ERASER_STROKE) {
                                                    if (wantedStrokeSize != draftEraserStrokeSize || !isDraftEraserStrokeSizeEdited) {
                                                        setPenStroke(strokeSize, window2, window, wantedStrokeSize);
                                                    } else {
                                                        strokeSize.setError("نباید با مقدار فعلی برابر باشد!");
                                                    }
                                                } else {
                                                    strokeSize.setError("حداکثر ضخامت پاک کن، ۲۵۰ می باشد!");
                                                }
                                            }
                                        } else {
                                            strokeSize.setError("مقدار ضخامت، عدد طبیعی است!");
                                        }
                                    } else {
                                        strokeSize.setError("لطفاً مقدار ضخامت را تعیین کنید!");
                                    }
                                });
                                try {
                                    window2.showAsDropDown(v, 0, 30);
                                } catch (Exception e) {
                                    Toast.makeText(this, "درخواست شما اجرا نشد!", Toast.WARNING_SIGN, Toast.LENGTH_SHORT).show();
                                }
                            } else if (v1.getId() == R.id.change_pen_color) {
                                if (!draftPathErasingEnabled) {
                                    ((ImageButton) draftToolboxItems.getChildAt(1)).getDrawable().setColorFilter(selectedColor, SRC_IN);
                                    View v3 = LayoutInflater.from(MainActivity.this).inflate(R.layout.popup_bookmarks_color, null);
                                    LinearLayout colors = v3.findViewById(R.id.bookmark_colors);
                                    PopupWindow window3 = new PopupWindow(v3, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
                                    window3.setClippingEnabled(false);
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        window3.setElevation(30f);
                                    }
                                    for (strokePenColorIndex = 0; strokePenColorIndex < colors.getChildCount(); strokePenColorIndex++) {
                                        colors.getChildAt(strokePenColorIndex).setOnClickListener(view2 -> {
                                            if (view2.getId() == R.id.bookmark_color_r) {
                                                selectedColor = PEN_STROKE_COLORS[0];
                                            } else if (view2.getId() == R.id.bookmark_color_o) {
                                                selectedColor = PEN_STROKE_COLORS[1];
                                            } else if (view2.getId() == R.id.bookmark_color_y) {
                                                selectedColor = PEN_STROKE_COLORS[2];
                                            } else if (view2.getId() == R.id.bookmark_color_c) {
                                                selectedColor = PEN_STROKE_COLORS[3];
                                            } else if (view2.getId() == R.id.bookmark_color_b) {
                                                selectedColor = PEN_STROKE_COLORS[4];
                                            } else if (view2.getId() == R.id.bookmark_color_p) {
                                                selectedColor = PEN_STROKE_COLORS[5];
                                            } else if (view2.getId() == R.id.bookmark_color_br) {
                                                selectedColor = PEN_STROKE_COLORS[6];
                                            } else if (view2.getId() == R.id.bookmark_color_g) {
                                                selectedColor = PEN_STROKE_COLORS[7];
                                            } else if (view2.getId() == R.id.bookmark_color_w) {
                                                selectedColor = PEN_STROKE_COLORS[8];
                                            }
                                            ((ImageButton) draftToolboxItems.getChildAt(1)).getDrawable().setColorFilter(selectedColor, SRC_IN);
                                            examDraftPage.setPaintStrokeColor(selectedColor);
                                            window3.dismiss();
                                            window.dismiss();
                                            Toast.makeText(this, "رنگ قلم تغییر یافت.", Toast.WARNING_SIGN, Toast.LENGTH_SHORT).show();
                                        });
                                    }
                                    try {
                                        window3.showAsDropDown(v, (int) convertDpToPx(55), 30);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Toast.makeText(this, "درخواست شما اجرا نشد!", Toast.WARNING_SIGN, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
                    }
                    try {
                        window.showAsDropDown(draftViewOptions, -350, -300);
                    } catch (Exception e) {
                        Toast.makeText(this, "درخواست شما اجرا نشد!", Toast.WARNING_SIGN, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        openDraftBox.setOnClickListener(v -> {
            if (examDraftLayout.getVisibility() != VISIBLE) {
                examDraftLayout.setVisibility(VISIBLE);
            }
        });
        startCurrentExam.setOnClickListener(v -> {
            if (questions.size() >= MIN_OF_QUESTIONS_COUNT) {
                MaterialAlertDialog ad = new MaterialAlertDialog(this);
                ad.setTitle("شروع آزمون");
                ad.setIcon(R.drawable.start_exam);
                ad.setMessage("آیا از شروع این آزمون اطمینان دارید؟");
                ad.setPositiveButton("بله، آماده ام", v1 -> {
                    ad.dismiss(ad);
                    startExam();
                });
                ad.setNegativeButton("هنوز نه", v1 -> ad.dismiss(ad));
                ad.show(this);
            } else {
                Toast.makeText(this, "هنوز سؤالی وارد نکردید!\nحداقل تعداد سؤال، ۵ سؤال هست.", Toast.WARNING_SIGN, Toast.LENGTH_SHORT).show();
            }
        });
        addQuestionButton.setOnClickListener(view -> {
            if (questions.size() < MAX_OF_QUESTIONS_COUNT) {
                /*Saver.getInstance(MainActivity.this).setDismissSide(SIDE_FRAGMENT_SHOWER);
                MaterialFragmentShower shower = new MaterialFragmentShower(MainActivity.this);
                shower.setFragment(new AddQuestionDialog(shower, newQuestion -> {
                    emptyQuestionsView.setVisibility(GONE);
                    removeQuestionButton.setVisibility(VISIBLE);
                    Questions questionsL = currentExam.getAnswerSheet();
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
                Toast.makeText(this, "حداکثر تعداد سؤال، ۱۰,۰۰۰ سؤال هست.", Toast.WARNING_SIGN, Toast.LENGTH_SHORT).show();
            }
        });
        removeQuestionButton.setOnClickListener(view -> {
            if (questions.size() > MIN_OF_QUESTIONS_COUNT) {

            } else {
                Toast.makeText(this, "حداقل تعداد سؤال، ۵ سؤال هست.", Toast.WARNING_SIGN, Toast.LENGTH_SHORT).show();
            }
        });
        resetChronometer.setOnClickListener(view -> {
            if (useChronometer && resetEnable) {
                MaterialAlertDialog builder = new MaterialAlertDialog(MainActivity.this);
                builder.setIcon(R.drawable.reset_chronometer);
                builder.setTitle("شروع مجدد کرنومتر");
                builder.setMessage("آیا برای شروع مجدد کرونومتر اطمینان دارید؟\n\nدکمه «شروع مجدد کرنومتر»، پس از این عمل، به مدت ۶۰ ثانیه غیرفعال خواهد شد!\nمقدار کنونی کرنومتر: " + String.format(Locale.getDefault(), "%d", currentExam.getSecondsOfThinkingOnQuestion()) + " ثانیه");
                builder.setPositiveButton("خیر", v4 -> builder.dismiss(builder));
                builder.setNegativeButton("بله", v4 -> {
                    currentExam.setSecondsOfThinkingOnQuestion(0);
                    currentExam.setAnswerSheet(Saver.getInstance(MainActivity.this).loadQuestions());
                    updateRecentExams();
                    changeResetChronoButtonState(false);
                    builder.dismiss(builder);
                    Toast.makeText(MainActivity.this, "شروع مجدد کرنومتر ⏲️🔁", Toast.LENGTH_SHORT).show();
                });
                builder.show(MainActivity.this);
            } else if (!resetEnable) {
                Toast.makeText(MainActivity.this, String.format(Locale.getDefault(), "%d ثانیه دیگر تا فعالسازی دکمه", RESET_BUTTON_ENABLE_DELAY - buttonDisableSeconds), Toast.LENGTH_SHORT).show();
            }
        });
        examAction.setOnLongClickListener(view -> {
            if (currentExam.isStarted()) {
                Toast.makeText(this, "اتمام آزمون", Toast.LENGTH_SHORT).show();
            } else if (currentExam.isCreating()) {
                Toast.makeText(this, "ساخت آزمون", Toast.LENGTH_SHORT).show();
            } else if (currentExam.isCorrecting()) {
                Toast.makeText(this, "تصحیح آزمون", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "عملکرد آزمون", Toast.LENGTH_SHORT).show();
            }
            return true;
        });
        examAction.setOnClickListener(v -> {
            if (currentExam.getExamStatus() == ExamStatus.Started) {
                MaterialAlertDialog ad = new MaterialAlertDialog(MainActivity.this);
                ad.setTitle("اتمام آزمون");
                ad.setIcon(R.drawable.done_exam);
                ad.setMessage(showAnsweredQuestions() + "آیا از اتمام این آزمون، اطمینان کامل دارید؟");
                ad.setNegativeButton("بله", v7 -> {
                    ad.dismiss(ad);
                    isExamStoppedManually = true;
                    currentExam.setExamStoppedManually(true);

                    updateRecentExams();
                    if (checkExamHasTime() && examTime != 0) {
                        if (examTimeLeft != null) examTimeLeft.cancel();
                    }
                    endTheExam();
                });
                ad.setPositiveButton("خیر", v7 -> ad.dismiss(ad));
                ad.show(MainActivity.this);
            } else if (currentExam.getExamStatus() == ExamStatus.Finished) {
                MaterialAlertDialog ad = new MaterialAlertDialog(MainActivity.this);
                ad.setTitle("شروع مجدد آزمون");
                ad.setIcon(R.drawable.reset_exam);
                ad.setMessage("آزمون تمام شده است\nآیا تمایل به شرکت دوباره در این آزمون دارید؟");
                ad.setNegativeButton("بله", v7 -> {
                    ad.dismiss(ad);
                    Toast.makeText(this, "** Feature Unavailable **\nComing soon...", Toast.LENGTH_SHORT).show();
                });
                ad.setPositiveButton("خیر", v7 -> ad.dismiss(ad));
                ad.show(MainActivity.this);
            }
        });
        userDashboardButton.setOnClickListener(v -> {
            showMainViewLayout(MainView.UserDashboard);
        });
        examsListBack.setOnClickListener(v -> hideExamsListAnimation());
        backToMainView.setOnClickListener(v -> {
            currentExam = null;
            resetCustomExamFields();
            startExamButtonsLayout.setVisibility(GONE);
            createExamLayoutContainer.setVisibility(GONE);
            selectExamImmediately.setVisibility(GONE);
            showMainViewLayout(MainView.RecentExams);
        });
        appMoreOptions.setOnClickListener(v -> {
            if (optionsMenuOpened) {
                dismissOptionsMenu();
            } else {
                showOptionsMenu();
            }
        });
        clickableArea.setOnClickListener(v -> {
            if (optionsMenuOpened) {
                dismissOptionsMenu();
            }
        });
        enterToExamRoom.setOnClickListener(v -> {
            if (!checkFieldsHasError()) {
                showAdiveryAd(getString(R.string.adivery_interstitial_ad_id));
                setupExam(0);
            }
        });
    }

    private void showAdiveryAd(String placementId) {
        if (Adivery.isLoaded(placementId)) {
            Adivery.showAd(placementId);
        }
    }

//    private void createNewExam(long time) {
//        currentExam = new Exam();
//        currentExam.setId(generateExamId());
//        currentExam.setExamName(currentExamName);
//        //currentExam.setCreating(true);
//        //currentExam.setStarted(false);
//        //currentExam.setChecked(false);
//        if (examFile != null) {
//            currentExam.setExamFile(examFile);
//        }
//        if (questions != null) {
//            currentExam.setAnswerSheet(new Questions(questions));
//            recentExams.updateCurrentExam(currentExam);
//            Saver.getInstance(MainActivity.this).saveRecentExams(recentExams);
//        }
//        currentExam.setExamTime(time);
//        currentExam.setExamTimeLeft(time);
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

    private void updateDraftPage(int index) {
        for (int i = 0; i < examDraftPages.getChildCount(); i++) {
            if (i == index) {
                examDraftPages.getChildAt(i).setVisibility(VISIBLE);
                examDraftPage = (CanvasView) examDraftPages.getChildAt(i);
            } else {
                examDraftPages.getChildAt(i).setVisibility(GONE);
            }
        }
    }

    private void updateDraftPagesCount() {
        int count = examDraftPages.getChildCount();
        switch (count) {
            case 0:
                draftPagesOptions.setImageResource(R.drawable.canvas_view_count_0);
                break;
            case 1:
                draftPagesOptions.setImageResource(R.drawable.canvas_view_count_1);
                break;
            case 2:
                draftPagesOptions.setImageResource(R.drawable.canvas_view_count_2);
                break;
            case 3:
                draftPagesOptions.setImageResource(R.drawable.canvas_view_count_3);
                break;
            case 4:
                draftPagesOptions.setImageResource(R.drawable.canvas_view_count_4);
                break;
            case 5:
                draftPagesOptions.setImageResource(R.drawable.canvas_view_count_5);
                break;
            case 6:
                draftPagesOptions.setImageResource(R.drawable.canvas_view_count_6);
                break;
            case 7:
                draftPagesOptions.setImageResource(R.drawable.canvas_view_count_7);
                break;
            case 8:
                draftPagesOptions.setImageResource(R.drawable.canvas_view_count_8);
                break;
            case 9:
                draftPagesOptions.setImageResource(R.drawable.canvas_view_count_9);
                break;
            default:
                draftPagesOptions.setImageResource(R.drawable.canvas_view_count_9_plus);
                break;
        }
        draftPagesOptions.getDrawable().setColorFilter(getResources().getColor(R.color.disable_button), SRC_IN);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setExamDraftTouchEvents() {
        examDraftPages.getChildAt(currentDraftViewIndex).setOnTouchListener((v, event) -> {
            draftDrawingHint.setVisibility(GONE);
            isDraftCanvasCleared = false;
            isDraftDrawingHintShown = true;
            return false;
        });
    }

    private void saveDraftScreenshot() {
        //examDraftView.setBackgroundColor(getResources().getColor(R.color.background_color));
        if (draftPathErasingEnabled) {
            draftViewOptions.setVisibility(GONE);
            //closeDraftView.setVisibility(GONE);
        }
        Handler handler = new Handler();
        Toast.makeText(MainActivity.this, "در حال تصویر برداری...", Toast.LENGTH_SHORT).show();
        handler.postDelayed(this::takeDraftScreenshot, 1000);
    }

    private void takeDraftScreenshot() {
        Calendar calendar = Calendar.getInstance();
        String now = printEnglishDate(new int[]{calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)});
        try {
            File mPath = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures/SalTech/Answer Sheet/Drafts");
            if (!mPath.exists()) {
                mPath.mkdirs();
            }
            examDraftPage.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(examDraftPage.getDrawingCache());
            examDraftPage.setDrawingCacheEnabled(false);
            Random random = new Random();
            File imageFile = new File(mPath.getAbsolutePath() + "/" + "چرک_نویس_" + examNameText.getText().toString().replace(" ", "_") + "_" + now + random.nextInt(100) + ".jpg");
            Log.i("TAG", "Draft Image Path:" + imageFile.getAbsolutePath());
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();
            if (draftPathErasingEnabled) {
                draftViewOptions.setVisibility(VISIBLE);
                //closeDraftView.setVisibility(VISIBLE);
            }
            Toast.makeText(MainActivity.this, "تصویر ذخیره شد.", Toast.WARNING_SIGN, Toast.LENGTH_LONG).show();
        } catch (Throwable e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "تصویر گرفته نشد!", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateDraftOptionsStatus(LinearLayout draftOptions) {
        if (draftDrawingHint.getVisibility() != VISIBLE && examDraftPage.canUndo() && !isDraftCanvasCleared) {
            draftOptions.getChildAt(0).setVisibility(VISIBLE);
        } else {
            draftOptions.getChildAt(0).setVisibility(GONE);
        }
        if (examDraftPage.canRedo() && !isDraftCanvasCleared) {
            draftOptions.getChildAt(1).setVisibility(VISIBLE);
        } else {
            draftOptions.getChildAt(1).setVisibility(GONE);
        }
        if (draftDrawingHint.getVisibility() != VISIBLE && !isDraftCanvasCleared) {
            draftOptions.getChildAt(2).setVisibility(VISIBLE);
        } else {
            draftOptions.getChildAt(2).setVisibility(GONE);
        }
    }

    private void showSubmitTextWindow() {
        View v = LayoutInflater.from(MainActivity.this).inflate(R.layout.popup_draw_text, null);
        ImageButton submitText = v.findViewById(R.id.text_submit);
        EditText wantedText = v.findViewById(R.id.draw_edit_text);
        EditText textSize = v.findViewById(R.id.draw_text_size);
        PopupWindow window = new PopupWindow(v, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
        window.setClippingEnabled(false);
        window.setElevation(30f);
        wantedText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() >= 1) {
                    submitText.setImageResource(R.drawable.done_exam);
                } else {
                    submitText.setImageResource(R.drawable.close);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        dismissSubmitTextWindowManually = false;
        submitText.setOnClickListener(view -> {
            if (wantedText.getText().length() != 0) {
                if (textSize.getText().length() != 0) {
                    int wTextSizeI = Integer.parseInt(textSize.getText().toString());
                    if (wTextSizeI >= 1) {
                        if (wTextSizeI >= 30) {
                            if (wTextSizeI <= 200) {
                                setDraftTextDrawing(window, wantedText.getText().toString(), (float) wTextSizeI);
                            } else {
                                textSize.setError("حداکثر سایز متن، ۲۰۰ می باشد!");
                            }
                        } else {
                            textSize.setError("حداقل سایز متن، ۳۰ می باشد!");
                        }
                    } else {
                        textSize.setError("سایز متن، عدد طبیعی است!");
                    }
                } else {
                    setDraftTextDrawing(window, wantedText.getText().toString(), 50);
                }
            } else {
                dismissSubmitTextWindowManually = true;
                window.dismiss();
            }
        });
        try {
            window.showAsDropDown(draftViewOptions, -400, -300);
            window.setOnDismissListener(() -> {
                if (!dismissSubmitTextWindowManually)
                    window.showAsDropDown(draftViewOptions, -400, -300);
            });
        } catch (Exception e) {
            Toast.makeText(this, "درخواست شما اجرا نشد!", Toast.WARNING_SIGN, Toast.LENGTH_SHORT).show();
        }
    }

    private void setDraftTextDrawing(@NonNull PopupWindow window, String text, float textSize) {
        examDraftPage.setMode(CanvasView.Mode.TEXT);
        examDraftPage.setFontFamily(ResourcesCompat.getFont(MainActivity.this, R.font.estedad_light));
        examDraftPage.setText(text);
        examDraftPage.setFontSize(textSize);
        examDraftPage.setPaintStyle(Paint.Style.FILL_AND_STROKE);
        selectedDrawingMode = CanvasView.Mode.TEXT;
        selectedModeResId = R.drawable.text;
        dismissSubmitTextWindowManually = true;
        window.dismiss();
        Toast.makeText(this, "متن شما تنظیم شد. برای استفاده روی صفحه ضربه بزنید.", Toast.WARNING_SIGN, Toast.LENGTH_LONG).show();
    }

    private void setPenStroke(EditText strokeSize, PopupWindow window2, PopupWindow window, float wantedStrokeSize) {
        strokeSize.setError(null);
        examDraftPage.setPaintStrokeWidth(wantedStrokeSize);
        if (draftPathErasingEnabled) draftEraserStrokeSize = wantedStrokeSize;
        else draftPenStrokeSize = wantedStrokeSize;
        window2.dismiss();
        window.dismiss();
        showHideKeyboardLayout(false, strokeSize);
        if (draftPathErasingEnabled) {
            isDraftEraserStrokeSizeEdited = true;
            Toast.makeText(this, "مقدار ضخامت پاک کن روی " + (int) wantedStrokeSize + " تنظیم شد.", Toast.WARNING_SIGN, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "مقدار ضخامت قلم روی " + (int) wantedStrokeSize + " تنظیم شد.", Toast.WARNING_SIGN, Toast.LENGTH_SHORT).show();
            isDraftPenStrokeSizeEdited = true;
        }
    }

    private float convertDpToPx(int dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    private void clearDraftCanvas() {
        isDraftCanvasCleared = true;
        examDraftPage.setText("");
        examDraftPage.setPaintStyle(selectedStyle);
        for (int k = 0; k < 1_000_000; k++) {
            examDraftPage.undo();
        }
        if (!examFileVisibility) {
            isDraftDrawingHintShown = false;
        }
        if (selectedDrawingMode.equals(CanvasView.Mode.TEXT)) {
            selectedDrawingMode = CanvasView.Mode.DRAW;
            selectedModeResId = R.drawable.path_drawing;
        }
        examDraftPage.setMode(selectedDrawingMode);
        examDraftPage.setPaintStrokeColor(selectedColor);
        examDraftPage.setPaintStrokeWidth(draftPenStrokeSize);
        examDraftPage.setDrawer(selectedMode);
        if (draftToolboxItems != null) {
            if (draftToolboxItems.getChildCount() == 6)
                draftToolboxItems.getChildAt(5).setVisibility(GONE);
        }
        draftPathErasingEnabled = false;
    }

    private String showAnsweredQuestions() {
        //questions = Saver.getInstance(MainActivity.this).loadQuestions().getQuestions();
        int answeredQuestionsNumber = 0;
        for (Question q : questions) {
            if (!q.isWhite()) {
                answeredQuestionsNumber++;
            }
        }
        if (answeredQuestionsNumber != questions.size()) {
            if (answeredQuestionsNumber == 0) {
                return "شما به هیچ یک از سؤالات پاسخ نداده اید!" + "\n";
            } else {
                if ((questions.size() - answeredQuestionsNumber <= APP_VERSION_CODE_DIGITS) && questions.size() >= 20) {
                    return "شما تقریباً به همه سؤالات، پاسخ داده اید." + "\n";
                } else if ((questions.size() - answeredQuestionsNumber <= 5) && questions.size() > 10 && questions.size() < 20) {
                    return "شما تقریباً به همه سؤالات، پاسخ داده اید." + "\n";
                } else if ((questions.size() - answeredQuestionsNumber <= 3) && questions.size() <= 10) {
                    return "شما تقریباً به همه سؤالات، پاسخ داده اید." + "\n";
                } else {
                    return String.format(Locale.getDefault(), "شما به %d سؤال از %d سؤال، پاسخ داده اید.", answeredQuestionsNumber, questions.size()) + "\n";
                }
            }
        } else {
            return "";
        }
    }

    private boolean checkFieldsHasError() {
        // TODO: Setup this....
        return false;
    }

    @Contract(pure = true)
    private void resetCustomExamFields() {
        // TODO: Setup this.....
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setControlButtonStates(CButtonState buttonState, ImageButton controlButton, int visibility) {
        switch (buttonState) {
            case Clicked:
                // controlButton.setBackground(getResources().getDrawable(R.drawable.tiny_button_bg));
                //controlButton.getBackground().setColorFilter(defaultPaletteColor, SRC_IN);
                controlButton.getDrawable().setColorFilter(defaultPaletteColor, SRC_IN);
                controlButton.setAlpha(1f);
                controlButton.setEnabled(true);
                controlButton.setClickable(true);
                break;
            case Idle:
                controlButton.setBackground(null);
                controlButton.getDrawable().setColorFilter(getResources().getColor(R.color.elements_color_tint), SRC_IN);
                controlButton.setAlpha(1f);
                controlButton.setEnabled(true);
                controlButton.setClickable(true);
                break;
            case Disable:
                controlButton.setBackground(null);
                controlButton.getDrawable().setColorFilter(getResources().getColor(R.color.disable_button_fade), SRC_IN);
                controlButton.setAlpha(0.85f);
                controlButton.setEnabled(false);
                controlButton.setClickable(false);
                break;
            default:
                break;
        }
        controlButton.setVisibility(visibility);
    }

    private void setToggleButtonsClick(@NonNull LinearLayout toggleButton) {
        int count = toggleButton.getChildCount();
        setToggleButtonDefaults(toggleButton);
        toggleButton.getChildAt(0).setOnClickListener(v -> {
            setTogglePartColor((TextView) v, true, 0, count);
            setTogglePartColor((TextView) toggleButton.getChildAt(1), false, 1, count);
            if (count > 2) {
                setTogglePartColor((TextView) toggleButton.getChildAt(2), false, 2, count);
            }
            this.onToggleButtonPartClicked(v.getId());
        });
        toggleButton.getChildAt(1).setOnClickListener(v -> {
            setTogglePartColor((TextView) v, true, 1, count);
            setTogglePartColor((TextView) toggleButton.getChildAt(0), false, 0, count);
            if (count > 2) {
                setTogglePartColor((TextView) toggleButton.getChildAt(2), false, 2, count);
            }
            this.onToggleButtonPartClicked(v.getId());
        });
        if (count > 2) {
            toggleButton.getChildAt(2).setOnClickListener(v -> {
                setTogglePartColor((TextView) v, true, 2, count);
                setTogglePartColor((TextView) toggleButton.getChildAt(0), false, 0, count);
                setTogglePartColor((TextView) toggleButton.getChildAt(1), false, 1, count);
                this.onToggleButtonPartClicked(v.getId());
            });
        }
    }

    private void setToggleButtonDefaults(@NonNull LinearLayout toggleButton) {
        int count = toggleButton.getChildCount();
        if (count > 2) {
            setTogglePartColor((TextView) toggleButton.getChildAt(2), true, 2, count);
            setTogglePartColor((TextView) toggleButton.getChildAt(1), false, 1, count);
            setTogglePartColor((TextView) toggleButton.getChildAt(0), false, 0, count);
        } else {
            setTogglePartColor((TextView) toggleButton.getChildAt(1), true, 1, count);
            setTogglePartColor((TextView) toggleButton.getChildAt(0), false, 0, count);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setTogglePartColor(TextView togglePart, boolean clicked, int index, int count) {
        if (!clicked) {
            if (index == 0) {
                togglePart.setBackground(getResources().getDrawable(R.drawable.half_toggle_left));
            } else if (index == 1) {
                if (count > 2) {
                    togglePart.setBackground(getResources().getDrawable(R.drawable.middle_toggle));
                } else {
                    togglePart.setBackground(getResources().getDrawable(R.drawable.half_toggle_right));
                }
            } else if (index == 2) {
                togglePart.setBackground(getResources().getDrawable(R.drawable.half_toggle_right));
            }
            togglePart.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.disable_button)));
            togglePart.setTextColor(getResources().getColor(R.color.disable_button));
        } else {
            if (index == 0) {
                togglePart.setBackground(getResources().getDrawable(R.drawable.half_toggle_left_selected));
            } else if (index == 1) {
                if (count > 2) {
                    togglePart.setBackground(getResources().getDrawable(R.drawable.middle_toggle_selected));
                } else {
                    togglePart.setBackground(getResources().getDrawable(R.drawable.half_toggle_right_selected));
                }
            } else if (index == 2) {
                togglePart.setBackground(getResources().getDrawable(R.drawable.half_toggle_right_selected));
            }
            togglePart.setBackgroundTintList(null);
            togglePart.setTextColor(Color.rgb(245, 245, 245));
        }
    }

    @SuppressLint("NonConstantResourceId")
    private void setRowToggleButtonsClicks() {
        setRowToggleButtonsDefaults();
        createCustomExam.setOnClickListener(v -> {
            selectExamImmediately.setVisibility(GONE);
            createExamLayoutContainer.setVisibility(VISIBLE);
            startExamButtonsLayout.setVisibility(VISIBLE);
        });
        setRowToggleButtonPressed(createCustomExam);
        selectExamName.setOnClickListener(v -> {
            ExamNames examNames = Saver.getInstance(MainActivity.this).loadExamNames();
            for (int i = 0; i < 5; i++) {
                ExamName en = new ExamName();
                en.setName("درس " + (i + 1));
                examNames.addExamName(en);
            }
            MaterialFragmentShower shower = new MaterialFragmentShower(this);
            shower.setCancelable(true);
            shower.setFragment(new SelectThingsDialog(shower, examNames.getThings(), ExamName.getThingName(), false, thing -> {
                currentExamName = (ExamName) thing;
                setRowToggleButtonText(selectExamName, thing.getTitle());
                setRowToggleButtonSwitched(selectExamName, true);
            }));
            shower.show(this, shower);
        });
        selectExamName.setOnLongClickListener(v -> {
            if (currentExamName != null) {
                currentExamName = null;
                setRowToggleButtonSwitched(selectExamName, false);
                setRowToggleButtonText(selectExamName, getString(R.string.select_exam_name));
                Toast.makeText(this, "نام آزمون لغو شد.", Toast.WARNING_SIGN, Toast.LENGTH_SHORT).show();
                return true;
            } else {
                return false;
            }
        });
        setRowToggleButtonPressed(selectExamName);
        selectChronometerEnabled.setOnClickListener(v -> {
            useChronometer = !useChronometer;
            chronoThresholdNP.setEnabled(useChronometer);
            int enabledColor;
            if (useChronometer && chronoThresholdNP.getValue() != 0)
                enabledColor = getResources().getColor(R.color.colorAccent);
            else
                enabledColor = getResources().getColor(R.color.disable_button);
            chronoThresholdNP.setSelectedTextColor(enabledColor);
            setRowToggleButtonSwitched(selectChronometerEnabled, useChronometer);
        });
        setRowToggleButtonPressed(selectChronometerEnabled);
        useCategoryTimingEnabled.setOnClickListener(v -> {
            if (useCategoryTimingEnabled.isActivated()) {
                useCategoryTiming = !useCategoryTiming;
                setRowToggleButtonSwitched(useCategoryTimingEnabled, useCategoryTiming);
            }
        });
        setRowToggleButtonPressed(useCategoryTimingEnabled);
        useCategoryScoreEnabled.setOnClickListener(v -> {
            if (useCategoryScoreEnabled.isActivated()) {
                useCategoryScore = !useCategoryScore;
                setRowToggleButtonSwitched(useCategoryScoreEnabled, useCategoryScore);
            }
        });
        setRowToggleButtonPressed(useCategoryScoreEnabled);
        selectCategoryEnabled.setOnClickListener(v -> {
            if (!useExamCategorize) {
                useCategoryScoreEnabled.setVisibility(VISIBLE);
                useCategoryTimingEnabled.setVisibility(VISIBLE);
                useExamCategorize = true;
            } else {
                useCategoryScoreEnabled.setVisibility(GONE);
                useCategoryTimingEnabled.setVisibility(GONE);
                useExamCategorize = false;
            }
            setRowToggleButtonSwitched(selectCategoryEnabled, useExamCategorize);
        });
        setRowToggleButtonPressed(selectCategoryEnabled);
        selectQuestionsRandomly.setOnClickListener(v -> {
            if (selectQuestionsRandomly.isActivated()) {
                if (!selectedQRandomly) {
                    questionsCPatternText.setVisibility(GONE);
                    lastQuestionNoText.setVisibility(VISIBLE);
                    selectedQRandomly = true;
                } else {
                    questionsCPatternText.setVisibility(VISIBLE);
                    lastQuestionNoText.setVisibility(GONE);
                    selectedQRandomly = false;
                }
                setRowToggleButtonSwitched(selectQuestionsRandomly, selectedQRandomly);
            }
        });
        setRowToggleButtonPressed(selectQuestionsRandomly);
    }

    private void setRowToggleButtonText(@NonNull LinearLayout toggleButton, String text) {
        ((TextView) toggleButton.getChildAt(0)).setText(text);
    }

    private void setRowToggleButtonsDefaults() {
        useChronometer = false;
        useCategoryScoreEnabled.setVisibility(GONE);
        useCategoryTimingEnabled.setVisibility(GONE);
        setEnabledRowButton(useCategoryScoreEnabled, false, useCategoryScore);
        setEnabledRowButton(useCategoryTimingEnabled, checkExamHasTime(), useCategoryTiming);
        selectChronometerEnabled.getChildAt(0).setSelected(true);
        setRowToggleButtonSwitched(selectChronometerEnabled, false);
        useExamCategorize = false;
        setRowToggleButtonSwitched(selectCategoryEnabled, false);
        selectedQRandomly = false;
        setRowToggleButtonSwitched(selectQuestionsRandomly, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setRowToggleButtonPressed(@NonNull LinearLayout rowToggleBtn) {
        rowToggleBtn.setActivated(true);
        rowToggleBtn.setOnTouchListener((v, event) -> {
            if (rowToggleBtn.isActivated()) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    ObjectAnimator scaleX = ObjectAnimator.ofFloat(v, "scaleX", 1f, ROW_TOGGLE_BUTTON_PRESSED_SCALE);
                    ObjectAnimator scaleY = ObjectAnimator.ofFloat(v, "scaleY", 1f, ROW_TOGGLE_BUTTON_PRESSED_SCALE);
                    animatorSet = new AnimatorSet();
                    animatorSet.playTogether(scaleX, scaleY);
                    animatorSet.setDuration(125);
                    animatorSet.start();
                } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    ObjectAnimator scaleX = ObjectAnimator.ofFloat(v, "scaleX", ROW_TOGGLE_BUTTON_PRESSED_SCALE, 1f);
                    ObjectAnimator scaleY = ObjectAnimator.ofFloat(v, "scaleY", ROW_TOGGLE_BUTTON_PRESSED_SCALE, 1f);
                    animatorSet = new AnimatorSet();
                    animatorSet.playTogether(scaleX, scaleY);
                    animatorSet.setDuration(125);
                    animatorSet.start();
                }
            }
            return false;
        });
    }

    private void setRowToggleButtonSwitched(@NonNull LinearLayout rowToggleBtn, boolean switched) {
        try {
            if (switched) {
                ((TextView) rowToggleBtn.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorAccent));
                rowToggleBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
                ((ImageView) rowToggleBtn.getChildAt(1)).getDrawable().setColorFilter(getResources().getColor(R.color.colorAccent), SRC_IN);
            } else {
                ((TextView) rowToggleBtn.getChildAt(0)).setTextColor(getResources().getColor(R.color.disable_button));
                rowToggleBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.disable_button)));
                ((ImageView) rowToggleBtn.getChildAt(1)).getDrawable().setColorFilter(getResources().getColor(R.color.disable_button), SRC_IN);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void setExamsListClicks() {
        addExamButton.setOnClickListener(v -> showMainViewLayout(MainView.ExamSetup));
        startedExamsButton.setOnClickListener(v -> showCurrentExamView());
        finishedExamsButton.setOnClickListener(v -> showRecentExamsView());
    }

    private void showSplashScreen() {
        showMainViewLayout(MainView.SplashScreen);
        setupDraftPenStrokeColor();
        ObjectAnimator appIconScaleX = ObjectAnimator.ofFloat(appIcon, "scaleX", 0.5f, 1.1f);
        ObjectAnimator appIconScaleY = ObjectAnimator.ofFloat(appIcon, "scaleY", 0.5f, 1.1f);
        ObjectAnimator appIconAlpha = ObjectAnimator.ofFloat(appIcon, "alpha", 0f, 1f);
        ObjectAnimator saltechIconScaleX = ObjectAnimator.ofFloat(splashSalTechImg, "scaleX", 0.5f, 1.1f);
        saltechIconScaleX.setStartDelay(100);
        ObjectAnimator saltechIconScaleY = ObjectAnimator.ofFloat(splashSalTechImg, "scaleY", 0.5f, 1f);
        saltechIconScaleY.setStartDelay(100);
        ObjectAnimator saltechIconAlpha = ObjectAnimator.ofFloat(splashSalTechImg, "alpha", 0f, 1f);
        saltechIconAlpha.setStartDelay(100);
        animatorSet = new AnimatorSet();
        animatorSet.playTogether(appIconAlpha, appIconScaleX, appIconScaleY, saltechIconAlpha, saltechIconScaleX, saltechIconScaleY);
        animatorSet.setStartDelay(500);
        animatorSet.setDuration(400);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animation) {

            }

            @Override
            public void onAnimationEnd(@NonNull Animator animation) {
                new Handler().postDelayed(() -> {
                    appTitle.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(MainActivity.this::hideAppTitleAnimation, 100);
                }, 100);
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

    private void setupDraftPenStrokeColor() {
        if (examDraftPages.getChildCount() >= 1) {
            if (selectedColor == 0) {
                if (checkDarkModeTurnedOn()) {
                    isPdfNightModeEnabled = true;
                    selectedColor = PEN_STROKE_COLORS[8];
                } else {
                    selectedColor = PEN_STROKE_COLORS[7];
                }
            }
            examDraftPage = (CanvasView) examDraftPages.getChildAt(currentDraftViewIndex);
            if (examDraftPage != null)
                examDraftPage.setPaintStrokeColor(selectedColor);
        }
    }

    private boolean checkDarkModeTurnedOn() {
        boolean hasDarkMode = false;
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                hasDarkMode = true;
                break;

            case Configuration.UI_MODE_NIGHT_NO:
                hasDarkMode = false;
                break;

            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                hasDarkMode = false;
                break;
        }
        return hasDarkMode;
    }

    private void hideAppTitleAnimation() {
        ObjectAnimator nAppIconScaleX = ObjectAnimator.ofFloat(appIcon, "scaleX", 1.1f, 1f);
        ObjectAnimator nAppIconScaleY = ObjectAnimator.ofFloat(appIcon, "scaleY", 1.1f, 1f);
        animatorSet = new AnimatorSet();
        animatorSet.playTogether(nAppIconScaleX, nAppIconScaleY);
        animatorSet.setStartDelay(1000);
        animatorSet.setDuration(200);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animation) {
                new Handler().postDelayed(() -> appTitle.setVisibility(View.GONE), 500);
            }

            @Override
            public void onAnimationEnd(@NonNull Animator animation) {
                splashAnim.setVisibility(View.VISIBLE);
                new Handler().postDelayed(MainActivity.this::playSplashAnimation, 100);
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

    private void playSplashAnimation() {
        vibrator.vibrate(new long[]{0, VIBRATE_SPLASH, VIBRATE_SPLASH_SNOOZE, VIBRATE_SPLASH, VIBRATE_SPLASH_SNOOZE, VIBRATE_SPLASH, VIBRATE_SPLASH_SNOOZE, VIBRATE_SPLASH}, -1);
        splashAnim.playAnimation();
        splashAnim.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animation) {
            }

            @Override
            public void onAnimationEnd(@NonNull Animator animation) {
                launchApp(90);
            }

            @Override
            public void onAnimationCancel(@NonNull Animator animation) {

            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animation) {
                if (repeatCount >= 0) {
                    splashAnim.cancelAnimation();
                } else {
                    repeatIconAnimationPlay();
                    repeatCount++;
                }
            }
        });
    }

    private void repeatIconAnimationPlay() {
        ObjectAnimator rpAppIconScaleX = ObjectAnimator.ofFloat(appIcon, "scaleX", 1f, 1.05f, 1f);
        ObjectAnimator rpAppIconScaleY = ObjectAnimator.ofFloat(appIcon, "scaleY", 1f, 1.05f, 1f);
        animatorSet = new AnimatorSet();
        animatorSet.playTogether(rpAppIconScaleX, rpAppIconScaleY);
        animatorSet.setDuration(150);
        animatorSet.start();
    }

    private void showOptionsMenu() {
        clickableArea.setVisibility(VISIBLE);
        menuItemsLayout.setVisibility(VISIBLE);
        //ObjectAnimator tapsellAlphaAnim = ObjectAnimator.ofFloat(standardTapsellBanner, "alpha", 1f, 0f);
        ObjectAnimator mainLayoutScaleXAnim = ObjectAnimator.ofFloat(recentExamsLayout, "scaleX", 1.015f, 0.9f);
        ObjectAnimator mainLayoutScaleYAnim = ObjectAnimator.ofFloat(recentExamsLayout, "scaleY", 1.015f, 0.9f);
        ObjectAnimator mainLayoutTranslationYAnim = ObjectAnimator.ofFloat(recentExamsLayout, "translationY", 1f, 120f);
        ValueAnimator closeMenuButtonShowAnim = ValueAnimator.ofFloat(0f, 360f);
        closeMenuButtonShowAnim.addUpdateListener(valueAnimator1 -> {
            float rotationValue = (float) valueAnimator1.getAnimatedValue();
            appMoreOptions.setRotation(rotationValue);
            if (rotationValue >= 180) {
                appMoreOptions.setImageResource(R.drawable.close_menu);
            }
        });
        animatorSet = new AnimatorSet();
        animatorSet.playTogether(closeMenuButtonShowAnim, mainLayoutScaleXAnim, mainLayoutScaleYAnim, mainLayoutTranslationYAnim);
        animatorSet.setStartDelay(50);
        animatorSet.setDuration(400);
        animatorSet.addListener(new Animator.AnimatorListener() {

            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onAnimationStart(Animator animator) {
                recentExamsLayout.setPadding(0, 0, 0, 0);
                recentExamsLayout.setBackground(getResources().getDrawable(R.drawable.recent_layout_collapsed));
                recentExamsLayout.setElevation(30f);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                setOptionItems();
                optionsMenuOpened = true;
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

    private void setOptionItems() {
        ImageButton settings = menuItemsLayout.findViewById(R.id.app_settings);
        settings.setOnClickListener(v -> {
            dismissOptionsMenu();
            CollapsablePanelFragment collapsablePanel = new CollapsablePanelFragment();
            collapsablePanel.setContentFragment(new SettingsFragment(collapsablePanel));
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, collapsablePanel).addToBackStack(null).commit();
        });
    }

    private void dismissOptionsMenu() {
        //ObjectAnimator tapsellAlphaAnim = ObjectAnimator.ofFloat(standardTapsellBanner, "alpha", 0f, 1f);
        ObjectAnimator mainLayoutScaleXAnim = ObjectAnimator.ofFloat(recentExamsLayout, "scaleX", 0.9f, 1.015f);
        ObjectAnimator mainLayoutScaleYAnim = ObjectAnimator.ofFloat(recentExamsLayout, "scaleY", 0.9f, 1.015f);
        ObjectAnimator mainLayoutTranslationYAnim = ObjectAnimator.ofFloat(recentExamsLayout, "translationY", 120f, 1f);
        ValueAnimator closeMenuButtonShowAnim = ValueAnimator.ofFloat(360f, 0f);
        closeMenuButtonShowAnim.addUpdateListener(valueAnimator1 -> {
            float rotationValue = (float) valueAnimator1.getAnimatedValue();
            appMoreOptions.setRotation(rotationValue);
            if (rotationValue <= 180) {
                appMoreOptions.setImageResource(R.drawable.more_app_options);
            }
        });
        animatorSet = new AnimatorSet();
        animatorSet.playTogether(closeMenuButtonShowAnim, mainLayoutScaleXAnim, mainLayoutScaleYAnim, mainLayoutTranslationYAnim);
        animatorSet.setStartDelay(50);
        animatorSet.setDuration(400);
        animatorSet.addListener(new Animator.AnimatorListener() {

            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onAnimationStart(Animator animator) {
                recentExamsLayout.setBackground(getResources().getDrawable(R.drawable.recent_layout_collapsed));
                if (recentExamsLayout.getElevation() != 0) {
                    recentExamsLayout.setElevation(30f);
                }
                menuItemsLayout.setVisibility(GONE);
            }

            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onAnimationEnd(Animator animator) {
                recentExamsLayout.setElevation(0);
                recentExamsLayout.setPadding(0, (int) getResources().getDimension(R.dimen.status_bar_margin), 0, 0);
                //recentExamsLayout.setScaleX(1f);
                //recentExamsLayout.setScaleY(1f);
                recentExamsLayout.setBackground(getResources().getDrawable(R.color.background_color));
                clickableArea.setVisibility(GONE);
                optionsMenuOpened = false;
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

    private void deleteAddCourseButton() {
        boolean checkHasAddCourseButton = false;
        int i1 = 0;
        for (int i = 0; i < recentExams.getExamList().size(); i++) {
            ExamName en = recentExams.getExamList().get(i).getExamName(0);
            if (en != null) {
                if (en.getName().equals(ADD_NEW_EXAM_BUTTON_TITLE)) {
                    checkHasAddCourseButton = true;
                    i1 = i;
                    break;
                }
            }
        }
        if (checkHasAddCourseButton) {
            recentExams.removeExam(i1);
            Saver.getInstance(MainActivity.this).saveRecentExams(recentExams);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void showExamsView(int viewType, List<Exam> list, ExamSelectedListener examListener, int listSize) {
        deleteAddCourseButton();
        examsViewAdapter = new ExamsViewAdapter(viewType, list, examListener, MainActivity.this);
        examsViewTitle.setText(getExamsView(viewType));
        if (listSize != 0) {
            sortExamsByDate(list);
            examsViewEmpty.setVisibility(GONE);
        } else {
            examsViewEmpty.setVisibility(VISIBLE);
        }
        try {
            examsView.setLayoutManager(new GridLayoutManager(MainActivity.this, getResources().getInteger(R.integer.exams_view_span_count)));
            examsView.setAdapter(examsViewAdapter);
            examsView.requestFocus();
        } catch (Exception e) {
            e.printStackTrace();
            examsViewAdapter.notifyDataSetChanged();
        }
        showExamsViewAnimation();
    }

    private void showRecentExamsView() {
        recentExamsList = new ArrayList<>();
        for (int o = 0; o < recentExams.getExamList().size(); o++) {
            Exam exam = recentExams.getExamList().get(o);
            if (!exam.isStarted() && !exam.isCreating() && !exam.isCorrecting() && !exam.isSuspended()) {
                boolean isExamConflict = false;
                for (Exam rExam : recentExamsList) {
                    if (rExam.getId() == exam.getId() && rExam.getExamName(0).getName().equals(exam.getExamName(0).getName())) {
                        isExamConflict = true;
                        break;
                    }
                }
                if (!isExamConflict) {
                    recentExamsList.add(exam);
                } else {
                    recentExams.removeExam(o);
                    Saver.getInstance(MainActivity.this).saveRecentExams(recentExams);
                }
            }
        }
        ExamSelectedListener recentExamsListener = new ExamSelectedListener() {
            @Override
            public void onExamDeleted(Exam exam, int position, String side) {
                recentExams = Saver.getInstance(MainActivity.this).loadRecentExams();
                if (recentExamsList.size() == 0) {
                    examsViewEmpty.setVisibility(VISIBLE);
                } else {
                    examsViewEmpty.setVisibility(GONE);
                }
                setExamsFeatures();
                if (side.equals(SIDE_RECENT_EXAMS)) {
                    showRecentExamsView();
                }
            }

            @Override
            public void onExamResumed(Exam exam) {
                prepareExamForResume(exam);
            }

            @Override
            public void onExamSuspended(Exam exam) {
            }

            @Override
            public void onExamClicked(Exam exam, String side) {
                if (side.equals(SIDE_RECENT_EXAMS)) prepareExamForResume(exam);
            }

            @Override
            public void onAddExamWanted() {
            }

            @Override
            public void onExamEdited(Exam exam) {
                showRecentExams();
            }
        };
        showExamsView(ExamViewHolder.TEMPLATE_VIEW_FINISHED_EXAMS, recentExamsList, recentExamsListener, recentExamsList.size());
    }

    private void showRecentExams() {
        showMainViewLayout(MainView.RecentExams);
        appMoreOptions.setVisibility(VISIBLE);
        //measurableViewForSearchBox.setVisibility(VISIBLE);
        setExamsFeatures();
        //setCoursesScores();
    }

    private void setExamsFeatures() {
        for (Exam e : recentExams.getExamList()) {
            examFeatures = "";
            if (e.getStartExamTime() != null) {
                setExamFeatures(FILTER_START_TIME, e);
            }
            if (!e.isSelectQuestionsManually()) {
                setExamFeatures(FILTER_QUESTIONS_NUMBER, e);
            }
            if (e.isStarted() && !e.isSuspended()) {
                setExamFeatures(FILTER_EXAM_RUNNING, e);
            } else if (!e.isStarted() && e.isCreating() && !e.isSuspended()) {
                setExamFeatures(FILTER_EXAM_CREATING, e);
            } else if (!e.isStarted() && e.isSuspended()) {
                setExamFeatures(FILTER_EXAM_SUSPENDED, e);
            } else if (!e.isStarted() && !e.isSuspended() && e.isCorrecting()) {
                setExamFeatures(FILTER_EXAM_CORRECTING, e);
            } else if (!e.isStarted() && !e.isSuspended() && !e.isCorrecting()) {
                setExamFeatures(FILTER_EXAM_ENDED, e);
            }
            Log.d("TAG", "EDRO: TILE: " + e.getExamName(0) + " OI: " + e.getFeatures());
        }
        Saver.getInstance(MainActivity.this).saveRecentExams(recentExams);
    }

    private void setExamFeatures(String feature, @NonNull Exam exam) {
        examFeatures += feature + FILTERS_DELIMITERS;
        exam.setFeatures(examFeatures.trim());
    }

    private void showCurrentExamView() {
        currentExams = new ArrayList<>();
        for (int o = 0; o < recentExams.getExamList().size(); o++) {
            Exam exam = recentExams.getExamList().get(o);
            if (exam.isStarted() && !exam.isCreating() && !exam.isSuspended()) {
                boolean isExamConflict = false;
                for (Exam cExam : currentExams) {
                    if (cExam.equals(exam)) {
                        isExamConflict = true;
                        break;
                    }
                }
                if (!isExamConflict) {
                    currentExams.add(exam);
                } else {
                    recentExams.removeExam(o);
                    Saver.getInstance(MainActivity.this).saveRecentExams(recentExams);
                }
            }
        }
        ExamSelectedListener startedExamsListener = new ExamSelectedListener() {
            @Override
            public void onExamDeleted(Exam exam, int position, String side) {
                recentExams = Saver.getInstance(MainActivity.this).loadRecentExams();
                setExamsFeatures();
                if (currentExams.size() == 0) {
                    examsViewEmpty.setVisibility(VISIBLE);
                } else {
                    examsViewEmpty.setVisibility(GONE);
                }
                if (side.equals(SIDE_CURRENT_EXAMS)) {
                    showCurrentExamView();
                }
            }

            @Override
            public void onExamResumed(Exam exam) {
                prepareExamForResume(exam);
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onExamSuspended(Exam e) {
                suspendAnExam(currentExams);
                if (currentExams.size() == 0) {
                    examsViewEmpty.setVisibility(VISIBLE);
                } else {
                    examsViewEmpty.setVisibility(GONE);
                }
            }

            @Override
            public void onExamClicked(Exam exam, String side) {
                if (side.equals(SIDE_CURRENT_EXAMS)) {
                    prepareExamForResume(exam);
                }
            }

            @Override
            public void onAddExamWanted() {

            }

            @Override
            public void onExamEdited(Exam exam) {

            }
        };
        showExamsView(ExamViewHolder.TEMPLATE_VIEW_RUNNING_EXAMS, currentExams, startedExamsListener, currentExams.size());
    }

    @Contract(pure = true)
    private void prepareExamForResume(Exam exam) {
        currentExam = exam;
        if (currentExam.isStarted() && currentExam.getExamStatus() != ExamStatus.Started)
            currentExamStatus = ExamStatus.Creating;
        currentExam.setExamStatus(currentExamStatus);
        recentExams.updateCurrentExam(currentExam);
        addExamButton.setEnabled(false);
        isRecentExamLoaded = true;
        runningCategory = currentExam.getRunningCategory();
        examFile = currentExam.getExamFile();
        for (Question question : currentExam.getAnswerSheet().getQuestions()) {
            question.setNowSelected(false);
        }
        isStartedManualExam = !currentExam.isCreating();
        isExamStoppedManually = currentExam.isExamStoppedManually();
        if (currentExam.isUsedTiming()) {
            long examTimeLi;
            if (!currentExam.isSuspended() && !currentExam.isEditingCategoryTimes()) {
                examTimeLi = currentExam.getExamTimeLeft();
            } else {
                examTimeLi = currentExam.getExamTime();
            }
            examTime = examTimeLi;
            minute = examTimeLi / 60_000;
            examTimeLi %= 60_000;
            second = examTimeLi / 1_000;
        }
        startedExamTime = currentExam.getStartExamTime();
        canUsingAdditionalSubtraction = currentExam.isHasAdditionalScore();
        /*useChronometer.setChecked(currentExam.isUsedChronometer());
        useCategorize.setChecked(currentExam.isUsedCategorize());
        canCalculateTimeForCategory.setChecked(currentExam.isCanCalculateTimeForCategory());
        if (currentExam.isUsedCorrection()) {
            useAdditionalSubtraction.setChecked(currentExam.isHasAdditionalScore());
            canUsingAdditionalSubtraction = currentExam.isHasAdditionalScore();
        }*/
        //useCalculateScoreOfCategory.setChecked(currentExam.isCanCalculateScoreOfCategory());
        currentExamName = currentExam.getExamName(0);
        currentExam.setSecondsOfThinkingOnQuestion(0);
        recentExams.updateCurrentExam(currentExam);
        Saver.getInstance(MainActivity.this).saveRecentExams(recentExams);
        if (!currentExam.isCreating() && currentExam.isStarted()) {
            examAction.setImageResource(R.drawable.done_select_questions);
        }
        firstQuestion = currentExam.getQuestionRange().getFirstQuestion();
        lastQuestion = currentExam.getQuestionRange().getLastQuestion();
        questionsCount = currentExam.getQuestionRange().getQuestionsCount();
        questionsCPattern = currentExam.getQuestionRange().getCountPattern();
        chronoThreshold = currentExam.getChornoThreshold();
        useChronometer = chronoThreshold != 0;
        useExamCategorize = currentExam.isUsedCategorize();
        useCategoryTiming = currentExam.isCanCalculateTimeForCategory();
        useCategoryScore = currentExam.isCanCalculateScoreOfCategory();
        correctedAsNow = currentExam.isChecked();
        correctionMode = currentExam.getCorrectionMode();
        currentExamStatus = currentExam.getExamStatus();
        if (!currentExam.isSuspended()) {
            try {
                questions = currentExam.getAnswerSheet().getQuestions();
                Saver.getInstance(MainActivity.this).saveQuestions(currentExam.getAnswerSheet());
                boolean hasBookmarkQuestion = false;
                for (Question question : questions) {
                    if (!question.getBookmark().getName().equals(Bookmark.NONE)) {
                        hasBookmarkQuestion = true;
                        break;
                    }
                }
                if (hasBookmarkQuestion) showBookmarkedQuestions();
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "⚠️ ناتوان در بارگذاری سؤالات آزمون!", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } else {
            questions = new ArrayList<>();
            Questions questionsQ = new Questions();
            List<Question> qq = new ArrayList<>();
            for (Question e : currentExam.getAnswerSheet().getQuestions()) {
                e.setWhite(true);
                e.setCategory(null);
                e.setBookmark(new Bookmark(Bookmark.NONE));
                e.setTimeOfThinking(0);
                e.setSelectedChoice(0);
                e.setCorrect(false);
                e.setCorrectAnswerChoice(0);
                e.setSelected(false);
                qq.add(e);
            }
            questionsQ.setQuestions(qq);
            questionsQ.setCategories(new ArrayList<>());
            currentExam.setRunningCategory(-1);
            runningCategory = -1;
            currentExam.setAnswerSheet(questionsQ);
            currentExam.setLastScrollPosition(0);
            updateRecentExams();
            Saver.getInstance(MainActivity.this).saveQuestions(questionsQ);
            //prepareAnswerSheet();
        }
        if (currentExam.isSuspended()) {
            startedExamTime = null;
            currentExam.setStartExamTime(null);
            currentExam.setSuspended(false);
            currentExam.setStarted(true);
            currentExam.setEditingCategoryTimes(true);
            startedTimeExam = false;
            recentExams.updateCurrentExam(currentExam);
            Saver.getInstance(MainActivity.this).saveRecentExams(recentExams);
        }
        setupExam(750);
        endTheLastExamLoading();
    }

    private void showBookmarkedQuestions() {
        // TODO: Setup this...
    }

    @SuppressLint("NotifyDataSetChanged")
    private void suspendAnExam(List<Exam> currentExams) {
        recentExams = Saver.getInstance(MainActivity.this).loadRecentExams();
        suspendedExamsList.clear();
        for (int o = 0; o < recentExams.getExamList().size(); o++) {
            Exam exam = recentExams.getExamList().get(o);
            if (!exam.isStarted() && exam.isSuspended()) {
                boolean isExamConflict = false;
                for (Exam sExam : suspendedExamsList) {
                    if (sExam.getId() == exam.getId() && sExam.getExamName().equals(exam.getExamName())) {
                        isExamConflict = true;
                        break;
                    }
                }
                if (!isExamConflict) {
                    suspendedExamsList.add(exam);
                } else {
                    recentExams.removeExam(o);
                    Saver.getInstance(MainActivity.this).saveRecentExams(recentExams);
                }
            }
        }
        Saver.getInstance(MainActivity.this).saveRecentExams(recentExams);
        examsViewAdapter.notifyDataSetChanged();
        currentExams.clear();
        for (int o = 0; o < recentExams.getExamList().size(); o++) {
            Exam exam = recentExams.getExamList().get(o);
            if ((exam.isStarted() || exam.isCreating()) && !exam.isSuspended()) {
                boolean isExamConflict = false;
                for (Exam cExam : currentExams) {
                    if (cExam.getId() == exam.getId() && cExam.getExamName().equals(exam.getExamName())) {
                        isExamConflict = true;
                        break;
                    }
                }

                if (!isExamConflict) {
                    currentExams.add(exam);
                } else {
                    recentExams.removeExam(o);
                    Saver.getInstance(MainActivity.this).saveRecentExams(recentExams);
                }
            }
        }
        sortExamsByDate(currentExams);
        examsViewAdapter.notifyDataSetChanged();
        setExamsFeatures();
    }

    private void setBlurContainer(ViewGroup container) {
        Blurry.with(MainActivity.this).sampling(5).radius(25).onto(container);
    }

    private void hideBlurContainer(ViewGroup container) {
        Blurry.delete(container);
    }

    private void sortExamsByDate(List<Exam> exams) {
        try {
            Collections.sort(exams, (e1, e2) -> {
                String[] d1P = e1.getStartExamTime().split(" ");
                String[] d2P = e2.getStartExamTime().split(" ");
                String d1 = d1P[1] + " " + d1P[0];
                String d2 = d2P[1] + " " + d2P[0];
                Log.d("TAG", "Date 1 : " + d1);
                Log.d("TAG", "Date 2 : " + d2);
                Date date1 = new Date(Integer.parseInt(d1.substring(0, 4)), Integer.parseInt(d1.substring(5, 7)), Integer.parseInt(d1.substring(8, 10)), Integer.parseInt(d1.substring(11, 13)), Integer.parseInt(d1.substring(14, 16)));
                Date date2 = new Date(Integer.parseInt(d2.substring(0, 4)), Integer.parseInt(d2.substring(5, 7)), Integer.parseInt(d2.substring(8, 10)), Integer.parseInt(d2.substring(11, 13)), Integer.parseInt(d2.substring(14, 16)));
                return date1.compareTo(date2);
            });
        } catch (NullPointerException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "در حین مرتب سازی فهرست آزمون ها، خطایی رخ داد!", Toast.WARNING_SIGN, Toast.LENGTH_LONG).show();
        }
    }

    private void showExamsViewAnimation() {
        ObjectAnimator examsViewCardScaleX = ObjectAnimator.ofFloat(examsViewCard, "scaleX", 0.5f, 1f);
        ObjectAnimator examsViewCardScaleY = ObjectAnimator.ofFloat(examsViewCard, "scaleY", 0.5f, 1f);
        ObjectAnimator examsViewCardAlpha = ObjectAnimator.ofFloat(examsViewCard, "alpha", 0f, 1f);
        animatorSet = new AnimatorSet();
        animatorSet.playTogether(examsViewCardScaleX, examsViewCardScaleY, examsViewCardAlpha);
        animatorSet.setDuration(EXAMS_LIST_ANIMATION_DURATION);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animator) {
                examsListBack.setClickable(false);
                setBlurContainer(recentExamsLayout);
                examsViewLayout.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(@NonNull Animator animator) {
                examsListBack.setClickable(true);
            }

            @Override
            public void onAnimationCancel(@NonNull Animator animator) {

            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animator) {

            }
        });
        animatorSet.start();
    }

    private void hideExamsListAnimation() {
        ObjectAnimator examsViewCardScaleX = ObjectAnimator.ofFloat(examsViewCard, "scaleX", 1f, 0.5f);
        ObjectAnimator examsViewCardScaleY = ObjectAnimator.ofFloat(examsViewCard, "scaleY", 1f, 0.5f);
        ObjectAnimator examsViewCardAlpha = ObjectAnimator.ofFloat(examsViewCard, "alpha", 1f, 0f);
        animatorSet = new AnimatorSet();
        animatorSet.playTogether(examsViewCardScaleX, examsViewCardScaleY, examsViewCardAlpha);
        animatorSet.setDuration(EXAMS_LIST_ANIMATION_DURATION);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animator) {
                examsListBack.setClickable(false);
                hideBlurContainer(recentExamsLayout);
            }

            @Override
            public void onAnimationEnd(@NonNull Animator animator) {
                examsViewLayout.setVisibility(GONE);
            }

            @Override
            public void onAnimationCancel(@NonNull Animator animator) {

            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animator) {

            }
        });
        animatorSet.start();
    }

    private String getExamsView(int viewType) {
        String examTitle = null;
        switch (viewType) {
            case ExamViewHolder.TEMPLATE_VIEW_RUNNING_EXAMS:
                examTitle = getString(R.string.started_exams_title);
                break;
            case ExamViewHolder.TEMPLATE_VIEW_SUSPENDED_EXAMS:
                examTitle = getString(R.string.suspended_exams_title);
                break;
            case ExamViewHolder.TEMPLATE_VIEW_FINISHED_EXAMS:
                examTitle = getString(R.string.finished_exams_title);
                break;
            case ExamViewHolder.TEMPLATE_VIEW_CREATING_EXAMS:
                examTitle = getString(R.string.creating_exams_title);
                break;
            case ExamViewHolder.TEMPLATE_VIEW_CORRECTING_EXAMS:
                examTitle = getString(R.string.correcting_exams_title);
                break;
            default:
                break;
        }
        return examTitle;
    }

    private void init() {
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        recentExams = Saver.getInstance(this).loadRecentExams();
        splashScreenLayout = findViewById(R.id.splash_screen_layout);
        examRunningLayout = findViewById(R.id.exam_running_layout);
        examSetupLayout = findViewById(R.id.exam_setup_layout);
        backToMainView = findViewById(R.id.back_to_main_view_layout);
        selectExamImmediately = findViewById(R.id.select_exam_immediately);
        createExamLayoutContainer = findViewById(R.id.create_exam_layout);
        // Row 1 | Static Functions | Main Parameters
        examAction = findViewById(R.id.exam_action);
        standardTapsellBanner = findViewById(R.id.standard_tapsell_banner);
        openDraftBox = findViewById(R.id.open_draft_board);
        jumpToQuestion = findViewById(R.id.jump_to_question);
        // Row 2 | Dynamic Functions | Secondary Parameters
        openExamFileBox = findViewById(R.id.open_exam_doc);
        addedBookmarksButton = findViewById(R.id.show_added_bookmarks);
        addQuestionButton = findViewById(R.id.add_question);
        removeQuestionButton = findViewById(R.id.remove_question);
        resetChronometer = findViewById(R.id.reset_chronometer);
        shareWorksheetButton = findViewById(R.id.share_worksheet);
        enableNegativePoint = findViewById(R.id.enable_negative_point);
        // ....... End of Rows .......
        answerSheetLayout = findViewById(R.id.answer_sheet_layout);
        collapseExamHeader = findViewById(R.id.collapse_exam_control_panel);
        collapseExamTimeBar = findViewById(R.id.collapse_exam_time_bar);
        createCustomExam = findViewById(R.id.add_custom_exam);
        shortcutExamPreparingLayout = findViewById(R.id.exam_loading_layout);
        debugVerWatermark = findViewById(R.id.debug_ver_watermark);
        enterToExamRoom = findViewById(R.id.enter_to_exam_button);
        userDashboardButton = findViewById(R.id.edu_dashboard_button);
        userDashboardLayout = findViewById(R.id.user_dashboard_layout);
        examAnimView = findViewById(R.id.exam_background_anim);
        examPictureView = findViewById(R.id.exam_background_image);
        examNameText = findViewById(R.id.exam_name);
        examTimeBar = findViewById(R.id.exam_time_rem_bar);
        examTimeBoard = findViewById(R.id.exam_time_seconds);
        selectChronometerEnabled = findViewById(R.id.select_chronometer_enabled);
        startCurrentExam = findViewById(R.id.start_exam_button);
        runningExamOptions = findViewById(R.id.running_exam_options);
        examControlPanel = findViewById(R.id.exam_control_panel);
        examAnswerSheetEmptyError = findViewById(R.id.answersheet_empty);
        examDraftPages = findViewById(R.id.draft_view_layout);
        addDraftPage = findViewById(R.id.add_draft_page);
        draftPagesOptions = findViewById(R.id.draft_pages_options);
        examDraftLayout = findViewById(R.id.draft_page_layout);
        draftViewOptions = findViewById(R.id.draft_page_tools);
        collapseDraftView = findViewById(R.id.collapse_draft_page);
        draftDrawingHint = findViewById(R.id.draft_drawing_hint);
        answerSheetView = findViewById(R.id.answersheet_view);
        startExamButtonsLayout = findViewById(R.id.start_exam_control_layout);
        recentExamsScrollContainer = findViewById(R.id.recent_exams_layout_scroll_container);
        selectCategoryEnabled = findViewById(R.id.select_category_enabled);
        useCategoryScoreEnabled = findViewById(R.id.category_correction_enable);
        useCategoryTimingEnabled = findViewById(R.id.category_timing_enable);
        setupExamTimeColon = findViewById(R.id.setup_exam_time_colon);
        scheduleCurrentExam = findViewById(R.id.schedule_exam_start);
        splashAnim = findViewById(R.id.splash_icon_anim);
        minuteNP = findViewById(R.id.minute_number_picker);
        secondNP = findViewById(R.id.second_number_picker);
        selectExamName = findViewById(R.id.select_exam_name);
        selectExamDocument = findViewById(R.id.attach_exam_file);
        selectQuestionsRandomly = findViewById(R.id.select_question_randomly);
        questionsCPatternText = findViewById(R.id.questions_count_pattern_text);
        firstQuestionNoText = findViewById(R.id.first_question_no_text);
        lastQuestionNoText = findViewById(R.id.last_question_no_text);
        questionsCountText = findViewById(R.id.questions_count_text);
        chronoThresholdNP = findViewById(R.id.chrono_thre_number_picker);
        splashSalTechImg = findViewById(R.id.splash_saltech_img);
        appIcon = findViewById(R.id.splash_app_icon);
        appTitle = findViewById(R.id.splash_app_title);
        selectQuestionsMode = findViewById(R.id.select_questions_mode);
        selectCorrectionMode = findViewById(R.id.select_correction_mode);
        parentLayout = findViewById(R.id.parent_layout);
        menuItemsLayout = findViewById(R.id.menu_items_layout);
        recentExamsLayout = findViewById(R.id.recent_exams_layout);
        appMoreOptions = findViewById(R.id.app_more_options);
        clickableArea = findViewById(R.id.clickable_area);
        addExamButton = findViewById(R.id.add_exam_card);
        welcomeLayout = findViewById(R.id.welcome_to_app_layout);
        welcomeImage = findViewById(R.id.welcome_image);
        welcomeTitle = findViewById(R.id.welcome_title);
        welcomeAppDesc = findViewById(R.id.welcome_app_desc);
        welcomeClickContinue = findViewById(R.id.welcome_click_continue);
        welcomeCompanyLogo = findViewById(R.id.welcome_company_logo);
        startedExamsButton = findViewById(R.id.started_exams_card);
        suspendedExamsButton = findViewById(R.id.suspended_exams_card);
        finishedExamsButton = findViewById(R.id.finished_exams_card);
        creatingExamsButton = findViewById(R.id.creating_exams_card);
        correctingExamButton = findViewById(R.id.correcting_exams_card);
        examsViewLayout = findViewById(R.id.exams_list_view_layout);
        examsViewTitle = findViewById(R.id.exams_list_title);
        examsViewCard = findViewById(R.id.exams_list_card);
        examsListBack = findViewById(R.id.exams_list_back);
        examsView = findViewById(R.id.exams_view);
        examsViewEmpty = findViewById(R.id.exams_list_empty);
        chronoThresholdNP.setEnabled(false);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onToggleButtonPartClicked(int tbPartId) {
        switch (tbPartId) {
            case R.id.select_questions_auto:
                isQuestionsManually = false;
                questionsCPatternText.setEnabled(true);
                lastQuestionNoText.setEnabled(true);
                firstQuestionNoText.setEnabled(true);
                questionsCountText.setEnabled(true);
                setEnabledRowButton(selectQuestionsRandomly, true, selectedQRandomly);
                break;
            case R.id.select_questions_manual:
                isQuestionsManually = true;
                questionsCPatternText.setEnabled(false);
                lastQuestionNoText.setEnabled(false);
                firstQuestionNoText.setEnabled(false);
                questionsCountText.setEnabled(false);
                setEnabledRowButton(selectQuestionsRandomly, false, selectedQRandomly);
                break;
            case R.id.exam_correction_none:
                setEnabledRowButton(useCategoryScoreEnabled, false, useCategoryScore);
                correctionMode = CorrectionMode.None;
                break;
            case R.id.exam_correction_normal:
                setEnabledRowButton(useCategoryScoreEnabled, true, useCategoryScore);
                correctionMode = CorrectionMode.Normal;
                break;
            case R.id.exam_correction_keys:
                setEnabledRowButton(useCategoryScoreEnabled, true, useCategoryScore);
                correctionMode = CorrectionMode.Keys;
                break;
            default:
                break;
        }
    }

    private void setEnabledRowButton(@NonNull LinearLayout row, boolean enabled, boolean checked) {
        int disabledColor = getResources().getColor(R.color.disable_button_fade);
        int enabledColor;
        if (checked) {
            enabledColor = getResources().getColor(R.color.colorAccent);
        } else {
            enabledColor = getResources().getColor(R.color.disable_button);
        }
        row.setActivated(enabled);
        for (int i = 0; i < row.getChildCount(); i++) {
            row.getChildAt(i).setActivated(enabled);
        }
        if (enabled) {
            row.setBackgroundTintList(ColorStateList.valueOf(enabledColor));
            ((ImageView) row.getChildAt(1)).getDrawable().setColorFilter(enabledColor, SRC_IN);
            ((TextView) row.getChildAt(0)).setTextColor(enabledColor);
        } else {
            row.setBackgroundTintList(ColorStateList.valueOf(disabledColor));
            ((ImageView) row.getChildAt(1)).getDrawable().setColorFilter(disabledColor, SRC_IN);
            ((TextView) row.getChildAt(0)).setTextColor(disabledColor);
        }
    }

    @Override
    protected void onDestroy() {
        hideTapsellAds();
        super.onDestroy();
    }

    private enum CollapseBarMode {
        Collapse, Fullscreen, Both, None
    }

    private enum CButtonState {
        Clicked, Idle, Disable
    }

    private enum MainView {
        SplashScreen, WelcomePage, RecentExams, ExamRunning, ExamSetup, UserDashboard
    }

    public enum ColorBrightness {
        Lighten, Darken, Moderate
    }
}