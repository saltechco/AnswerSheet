package ir.saltech.answersheet.object.container;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;

import ir.saltech.answersheet.object.data.Activities;
import ir.saltech.answersheet.object.data.Bookmark;
import ir.saltech.answersheet.object.data.BookmarkColor;
import ir.saltech.answersheet.object.data.Bookmarks;
import ir.saltech.answersheet.object.data.Device;
import ir.saltech.answersheet.object.data.Exam;
import ir.saltech.answersheet.object.data.ExamNames;
import ir.saltech.answersheet.object.data.ExamWallpapers;
import ir.saltech.answersheet.object.data.Exams;
import ir.saltech.answersheet.object.data.Feedback;
import ir.saltech.answersheet.object.data.Product;
import ir.saltech.answersheet.object.data.Questions;

public class Saver {
    public static final int DEF_PASSKEY = 0;
    private static final String BOOKMARKS = "bookmarks";
    private static final String COURSES = "examNames";
    private static final String QUESTIONS = "questions";
    private static final String RECENT_EXAM = "recent_exams";
    private static final String WELCOME_PAGE_SHOWED = "welcome_page_showed";
    private static final String DISMISS_SIDE = "dismiss_side";
    private static final String KEEP_SCREEN_ON = "keep_screen_on";
    private static final String VIBRATION_EFFECTS = "vibration_effects";
    private static final String MUSIC_EFFECTS = "music_effects";
    private static final String CATEGORIZE_FIRST_USE = "categorize_first_use";
    private static final String BOOKMARKS_LIST_FIRST_USE = "bookmarks_list_first_use";
    private static final String BOOKMARK_FIRST_USE = "bookmark_first_use";
    private static final String MANUAL_QUESTIONS_FIRST_USE = "manual_questions_first_use";
    private static final String HOW_TO_USE_HELP_SHOWING_FIRST_USE = "how_to_use_help_showing_first_use";
    private static final String HOW_TO_DELETE_COURSE_FIRST_USE = "how_to_delete_course_first_use";
    private static final String HOW_TO_DELETE_BOOKMARK_FIRST_USE = "how_to_delete_bookmark_first_use";
    private static final String TAKE_PROBLEM_SCREENSHOT_FIRST_USE = "take_problem_screenshot_first_use";
    private static final String BACKUP_CREATING_STATUS = "backup_creating_status";
    private static final String BACKUP_RESTORING_STATUS = "backup_restoring_status";
    private static final String PRODUCT_INFO = "product_info";
    private static final String RECENT_FEEDBACK = "recent_feedback";
    private static final String CANCELLING_COURSE_FIRST_USE = "cancelling_course_first_use";
    private static final String CANCELLING_EXAM_FILE_FIRST_USE = "cancelling_exam_file_first_use";
    private static final String DRAFT_DRAWING_FIRST_USE = "draft_drawing_first_use";
    private static final String EXAM_ACTION_FIRST_USE = "exam_action_first_use";
    private static final String RESET_CHRONOMETER_FIRST_USE = "reset_chronometer_first_use";
    private static final String CHANGE_PDF_PAGE_FIRST_USE = "change_pdf_page_first_use";
    private static final String HOW_TO_FULL_SCREEN_FIRST_USE = "how_to_full_screen_first_use";
    private static final String NEW_FEATURES_SHOWED = "new_features_showed";
    private static final String APP_RESTART_WANTED = "app_restart_wanted";
    private static final String NEW_CONTINUE_EXAM_FIRST_USE = "new_continue_exam_first_use";
    private static final String ADD_DEVICE_WAS_COMMITTED = "add_device_was_committed";
    private static final String DEVICE_INFO = "device_info";
    private static final String RECENT_ACTIVITIES = "recent_activities";
    private static final String CURRENT_FAVORITE_EXAM = "current_favorite_exam";
    private static final String APP_PASSWORD = "app_password";
    private static final String STATUS_BAR_COLOR_STATE = "status_bar_color_state";
    private static final String EXAM_WALLPAPERS = "exam_wallpapers";
    private static Saver instance;
    private final SharedPreferences sp;
    private final SharedPreferences.Editor editor;
    private final Context context;

    @SuppressLint("CommitPrefEdits")
    private Saver(Context context) {
        this.context = context;
        sp = context.getSharedPreferences("application_preferences", Context.MODE_PRIVATE);
        editor = sp.edit();
    }

    @NonNull
    public static Saver getInstance(Context context) {
        if (instance == null) {
            instance = new Saver(context);
        }
        return instance;
    }

    public void saveCurrentFavoriteExam(@NonNull Exam exam) {
        editor.putString(CURRENT_FAVORITE_EXAM, new Gson().toJson(exam, Exam.class));
        editor.apply();
    }

    @NonNull
    public Exam loadCurrentFavoriteExam() {
        String favoriteExamJ = sp.getString(CURRENT_FAVORITE_EXAM, null);
        if (favoriteExamJ != null) {
            return new Gson().fromJson(favoriteExamJ, Exam.class);
        } else {
            return new Exam();
        }
    }

    public void saveExamNames(ExamNames examNames) {
        editor.putString(COURSES, new Gson().toJson(examNames));
        editor.apply();
    }

    @NonNull
    public ExamNames loadExamNames() {
        String examNamesJ = sp.getString(COURSES, null);
        if (examNamesJ != null)
            return new Gson().fromJson(examNamesJ, ExamNames.class);
        else
            return new ExamNames();
    }

    public void saveBookmarks(Bookmarks bookmarks) {
        editor.putString(BOOKMARKS, new Gson().toJson(bookmarks));
        editor.apply();
    }

    @NonNull
    public Bookmarks loadBookmarks() {
        String bookmarksJ = sp.getString(BOOKMARKS, null);
        if (bookmarksJ != null) {
            return new Gson().fromJson(bookmarksJ, Bookmarks.class);
        } else {
            Bookmarks bookmarks = new Bookmarks();
            Bookmark defaultBookmarkMostPriority = new Bookmark();
            defaultBookmarkMostPriority.setName("پر اهمیّت");
            defaultBookmarkMostPriority.setPinColor(new BookmarkColor(1, false));
            bookmarks.addBookmark(defaultBookmarkMostPriority);
            Bookmark defaultBookmarkDifficult = new Bookmark();
            defaultBookmarkDifficult.setName("دشوار");
            defaultBookmarkDifficult.setPinColor(new BookmarkColor(0, false));
            bookmarks.addBookmark(defaultBookmarkDifficult);
            saveBookmarks(bookmarks);
            return bookmarks;
        }
    }

    public void saveQuestions(Questions questions) {
        editor.putString(QUESTIONS, new Gson().toJson(questions));
        editor.apply();
    }

    public Questions loadQuestions() {
        String questionsJ = sp.getString(QUESTIONS, null);
        if (questionsJ != null)
            return new Gson().fromJson(questionsJ, Questions.class);
        else
            return new Questions();
    }

    public void saveRecentExams(@Nullable Exams recentExam) {
        editor.putString(RECENT_EXAM, new Gson().toJson(recentExam));
        editor.apply();
    }

    @NonNull
    public Exams loadRecentExams() {
        String examJ = sp.getString(RECENT_EXAM, null);
        if (examJ != null)
            return new Gson().fromJson(examJ, Exams.class);
        else
            return new Exams();
    }

    public boolean getWelcomePageShowed() {
        return sp.getBoolean(WELCOME_PAGE_SHOWED, false);
    }

    public void setWelcomePageShowed(boolean welcomePageShowed) {
        editor.putBoolean(WELCOME_PAGE_SHOWED, welcomePageShowed);
        editor.apply();
    }

    @Nullable
    public String getDismissSide() {
        return sp.getString(DISMISS_SIDE, null);
    }

    public void setDismissSide(String dismissSide) {
        editor.putString(DISMISS_SIDE, dismissSide);
        editor.apply();
    }

    public int getAppPassword() {
        return sp.getInt(APP_PASSWORD, DEF_PASSKEY);
    }

    public void setAppPassword(int appPassword) {
        editor.putInt(APP_PASSWORD, appPassword);
        editor.apply();
    }

    public boolean getCourseCancellingFirstUse() {
        return sp.getBoolean(CANCELLING_COURSE_FIRST_USE, false);
    }

    public void setCourseCancellingFirstUse(boolean fuCourseCancelling) {
        editor.putBoolean(CANCELLING_COURSE_FIRST_USE, fuCourseCancelling);
        editor.apply();
    }

    public boolean getExamFileCancellingFirstUse() {
        return sp.getBoolean(CANCELLING_EXAM_FILE_FIRST_USE, false);
    }

    public void setExamFileCancellingFirstUse(boolean fuExamFileCancelling) {
        editor.putBoolean(CANCELLING_EXAM_FILE_FIRST_USE, fuExamFileCancelling);
        editor.apply();
    }

    public boolean getKeepScreenOn() {
        return sp.getBoolean(KEEP_SCREEN_ON, false);
    }

    public void setKeepScreenOn(boolean keepScreenOn) {
        editor.putBoolean(KEEP_SCREEN_ON, keepScreenOn);
        editor.apply();
    }

    public boolean getVibrationEffects() {
        return sp.getBoolean(VIBRATION_EFFECTS, true);
    }

    public void setVibrationEffects(boolean vibrationEffects) {
        editor.putBoolean(VIBRATION_EFFECTS, vibrationEffects);
        editor.apply();
    }

    public boolean getMusicEffects() {
        return sp.getBoolean(MUSIC_EFFECTS, true);
    }

    public void setMusicEffects(boolean musicEffects) {
        editor.putBoolean(MUSIC_EFFECTS, musicEffects);
        editor.apply();
    }

    public boolean getCategorizeFirstUse() {
        return sp.getBoolean(CATEGORIZE_FIRST_USE, false);
    }

    public void setCategorizeFirstUse(boolean fuCategorize) {
        editor.putBoolean(CATEGORIZE_FIRST_USE, fuCategorize);
        editor.apply();
    }

    public boolean getBookmarksListFirstUse() {
        return sp.getBoolean(BOOKMARKS_LIST_FIRST_USE, false);
    }

    public void setBookmarksListFirstUse(boolean fuBookmarksList) {
        editor.putBoolean(BOOKMARKS_LIST_FIRST_USE, fuBookmarksList);
        editor.apply();
    }

    public boolean getBookmarkFirstUse() {
        return sp.getBoolean(BOOKMARK_FIRST_USE, false);
    }

    public void setBookmarkFirstUse(boolean fuBookmarksList) {
        editor.putBoolean(BOOKMARK_FIRST_USE, fuBookmarksList);
        editor.apply();
    }

    public boolean getManualQuestionsFirstUse() {
        return sp.getBoolean(MANUAL_QUESTIONS_FIRST_USE, false);
    }

    public void setManualQuestionsFirstUse(boolean fuManualQuestions) {
        editor.putBoolean(MANUAL_QUESTIONS_FIRST_USE, fuManualQuestions);
        editor.apply();
    }

    public boolean getHowToUseHelpShowingFirstUse() {
        return sp.getBoolean(HOW_TO_USE_HELP_SHOWING_FIRST_USE, false);
    }

    public void setHowToUseHelpShowingFirstUse(boolean fuHowToUseHelpShowing) {
        editor.putBoolean(HOW_TO_USE_HELP_SHOWING_FIRST_USE, fuHowToUseHelpShowing);
        editor.apply();
    }

    public boolean getHowToDeleteCourseFirstUse() {
        return sp.getBoolean(HOW_TO_DELETE_COURSE_FIRST_USE, false);
    }

    public void setHowToDeleteCourseFirstUse(boolean fuHowToDeleteCourse) {
        editor.putBoolean(HOW_TO_DELETE_COURSE_FIRST_USE, fuHowToDeleteCourse);
        editor.apply();
    }

    public boolean getHowToDeleteBookmarkFirstUse() {
        return sp.getBoolean(HOW_TO_DELETE_BOOKMARK_FIRST_USE, false);
    }

    public void setHowToDeleteBookmarkFirstUse(boolean fuDeleteBookmark) {
        editor.putBoolean(HOW_TO_DELETE_BOOKMARK_FIRST_USE, fuDeleteBookmark);
        editor.apply();
    }

    public boolean getBackupCreatingStatus() {
        return sp.getBoolean(BACKUP_CREATING_STATUS, false);
    }

    public void setBackupCreatingStatus(boolean bkpCreating) {
        editor.putBoolean(BACKUP_CREATING_STATUS, bkpCreating);
        editor.apply();
    }

    public boolean getBackupRestoringStatus() {
        return sp.getBoolean(BACKUP_RESTORING_STATUS, false);
    }

    public void setBackupRestoringStatus(boolean bkpRestoring) {
        editor.putBoolean(BACKUP_RESTORING_STATUS, bkpRestoring);
        editor.apply();
    }

    public void saveProductInfo(@Nullable Product product) {
        editor.putString(PRODUCT_INFO, new Gson().toJson(product));
        editor.apply();
    }

    @NonNull
    public Product loadProductInfo() {
        String productJ = sp.getString(PRODUCT_INFO, null);
        if (productJ != null)
            return new Gson().fromJson(productJ, Product.class);
        else
            return new Product();
    }

    public void saveRecentFeedback(@Nullable Feedback feedback) {
        editor.putString(RECENT_FEEDBACK, new Gson().toJson(feedback));
        editor.apply();
    }

    @Nullable
    public Feedback loadRecentFeedback() {
        String feedbackJ = sp.getString(RECENT_FEEDBACK, null);
        if (feedbackJ != null)
            return new Gson().fromJson(feedbackJ, Feedback.class);
        else
            return null;
    }

    public boolean getTakeProblemScreenshotFirstUse() {
        return sp.getBoolean(TAKE_PROBLEM_SCREENSHOT_FIRST_USE, false);
    }

    public void setTakeProblemScreenshotFirstUse(boolean fuTakeScreenshot) {
        editor.putBoolean(TAKE_PROBLEM_SCREENSHOT_FIRST_USE, fuTakeScreenshot);
        editor.apply();
    }

    public boolean getDraftDrawingFirstUse() {
        return sp.getBoolean(DRAFT_DRAWING_FIRST_USE, false);
    }

    public void setDraftDrawingFirstUse(boolean fuDraftDrawing) {
        editor.putBoolean(DRAFT_DRAWING_FIRST_USE, fuDraftDrawing);
        editor.apply();
    }

    public boolean getExamActionFirstUse() {
        return sp.getBoolean(EXAM_ACTION_FIRST_USE, false);
    }

    public void setExamActionFirstUse(boolean fuExamAction) {
        editor.putBoolean(EXAM_ACTION_FIRST_USE, fuExamAction);
        editor.apply();
    }

    public boolean getResetChronometerFirstUse() {
        return sp.getBoolean(RESET_CHRONOMETER_FIRST_USE, false);
    }

    public void setResetChronometerFirstUse(boolean fuResetChronometer) {
        editor.putBoolean(RESET_CHRONOMETER_FIRST_USE, fuResetChronometer);
        editor.apply();
    }

    public boolean getChangePDFPageFirstUse() {
        return sp.getBoolean(CHANGE_PDF_PAGE_FIRST_USE, false);
    }

    public void setChangePDFPageFirstUse(boolean fuChangePDFPage) {
        editor.putBoolean(CHANGE_PDF_PAGE_FIRST_USE, fuChangePDFPage);
        editor.apply();
    }

    public boolean getHowToFullScreenFirstUse() {
        return sp.getBoolean(HOW_TO_FULL_SCREEN_FIRST_USE, false);
    }

    public void setHowToFullScreenFirstUse(boolean fuHowToFullScreenFirstUse) {
        editor.putBoolean(HOW_TO_FULL_SCREEN_FIRST_USE, fuHowToFullScreenFirstUse);
        editor.apply();
    }

    public boolean getNewContinueExamScreenFirstUse() {
        return sp.getBoolean(NEW_CONTINUE_EXAM_FIRST_USE, false);
    }

    public void setNewContinueExamFirstUse(boolean fuNewContinueExam) {
        editor.putBoolean(NEW_CONTINUE_EXAM_FIRST_USE, fuNewContinueExam);
        editor.apply();
    }

    public boolean getNewFeaturesShowed() {
        return sp.getBoolean(NEW_FEATURES_SHOWED, false);
    }

    public void setNewFeaturesShowed(boolean newFeaturesShowed) {
        editor.putBoolean(NEW_FEATURES_SHOWED, newFeaturesShowed);
        editor.apply();
    }

    public boolean checkAppRestartWanted() {
        return sp.getBoolean(APP_RESTART_WANTED, false);
    }

    public void setAppRestartWanted(boolean appRestartWanted) {
        editor.putBoolean(APP_RESTART_WANTED, appRestartWanted);
        editor.apply();
    }

    public boolean checkAddDeviceWasCommitted() {
        return sp.getBoolean(ADD_DEVICE_WAS_COMMITTED, false);
    }

    public void setAddDeviceWasCommitted(boolean addDeviceWasCommitted) {
        editor.putBoolean(ADD_DEVICE_WAS_COMMITTED, addDeviceWasCommitted);
        editor.apply();
    }

    public void saveDeviceInfo(@NonNull Device device) {
        editor.putString(DEVICE_INFO, new Gson().toJson(device));
        editor.apply();
    }

    @NonNull
    public Device loadDeviceInfo() {
        String deviceJ = sp.getString(DEVICE_INFO, null);
        if (deviceJ != null)
            return new Gson().fromJson(deviceJ, Device.class);
        else
            return new Device();
    }

    public void saveRecentActivities(@NonNull Activities activities) {
        editor.putString(RECENT_ACTIVITIES, new Gson().toJson(activities));
        editor.apply();
    }

    @NonNull
    public Activities loadRecentActivities() {
        String activitiesJ = sp.getString(RECENT_ACTIVITIES, null);
        if (activitiesJ != null)
            return new Gson().fromJson(activitiesJ, Activities.class);
        else
            return new Activities();
    }

    public void saveExamWallpapers(ExamWallpapers wallpapers) {
        editor.putString(EXAM_WALLPAPERS, new Gson().toJson(wallpapers, ExamWallpapers.class));
        editor.apply();
    }

    @NonNull
    public ExamWallpapers loadExamWallpapers() {
        String activitiesJ = sp.getString(EXAM_WALLPAPERS, null);
        if (activitiesJ != null)
            return new Gson().fromJson(activitiesJ, ExamWallpapers.class);
        else
            return new ExamWallpapers(context);
    }

    /**
     * Get StatusBar color
     *
     * @return true if is Light and false if is Dark, Default: true
     */
    public boolean getLastStatusBarColorState() {
        return sp.getBoolean(STATUS_BAR_COLOR_STATE, true);
    }

    /**
     * Set StatusBar color
     *
     * @param colorState true is Light, false is Dark
     */
    public void setLastStatusBarColorState(boolean colorState) {
        editor.putBoolean(STATUS_BAR_COLOR_STATE, colorState);
        editor.apply();
    }
}
