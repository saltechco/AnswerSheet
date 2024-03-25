package ir.saltech.answersheet.`object`.container

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import ir.saltech.answersheet.`object`.data.Activities
import ir.saltech.answersheet.`object`.data.Bookmark
import ir.saltech.answersheet.`object`.data.BookmarkColor
import ir.saltech.answersheet.`object`.data.Bookmarks
import ir.saltech.answersheet.`object`.data.Device
import ir.saltech.answersheet.`object`.data.Exam
import ir.saltech.answersheet.`object`.data.ExamNames
import ir.saltech.answersheet.`object`.data.ExamWallpapers
import ir.saltech.answersheet.`object`.data.Exams
import ir.saltech.answersheet.`object`.data.Feedback
import ir.saltech.answersheet.`object`.data.Product
import ir.saltech.answersheet.`object`.data.Questions

class Saver @SuppressLint("CommitPrefEdits") private constructor(private val context: Context) {
    private val sp: SharedPreferences =
        context.getSharedPreferences("application_preferences", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sp.edit()

    fun saveCurrentFavoriteExam(exam: Exam) {
        editor.putString(CURRENT_FAVORITE_EXAM, Gson().toJson(exam, Exam::class.java))
        editor.apply()
    }

    fun loadCurrentFavoriteExam(): Exam {
        val favoriteExamJ = sp.getString(CURRENT_FAVORITE_EXAM, null)
        return if (favoriteExamJ != null) {
            Gson().fromJson<Exam>(favoriteExamJ, Exam::class.java)
        } else {
            Exam()
        }
    }

    fun saveExamNames(examNames: ExamNames?) {
        editor.putString(COURSES, Gson().toJson(examNames))
        editor.apply()
    }

    fun loadExamNames(): ExamNames {
        val examNamesJ = sp.getString(COURSES, null)
        return if (examNamesJ != null) Gson().fromJson(examNamesJ, ExamNames::class.java)
        else ExamNames()
    }

    fun saveBookmarks(bookmarks: Bookmarks?) {
        editor.putString(BOOKMARKS, Gson().toJson(bookmarks))
        editor.apply()
    }

    fun loadBookmarks(): Bookmarks {
        val bookmarksJ = sp.getString(BOOKMARKS, null)
        if (bookmarksJ != null) {
            return Gson().fromJson<Bookmarks>(bookmarksJ, Bookmarks::class.java)
        } else {
            val bookmarks: Bookmarks = Bookmarks()
            val defaultBookmarkMostPriority = Bookmark()
            defaultBookmarkMostPriority.name = "پر اهمیّت"
            defaultBookmarkMostPriority.pinColor = BookmarkColor(1, false)
            bookmarks.addBookmark(defaultBookmarkMostPriority)
            val defaultBookmarkDifficult = Bookmark()
            defaultBookmarkDifficult.name = "دشوار"
            defaultBookmarkDifficult.pinColor = BookmarkColor(0, false)
            bookmarks.addBookmark(defaultBookmarkDifficult)
            saveBookmarks(bookmarks)
            return bookmarks
        }
    }

    fun saveQuestions(questions: Questions?) {
        editor.putString(QUESTIONS, Gson().toJson(questions))
        editor.apply()
    }

    fun loadQuestions(): Questions {
        val questionsJ = sp.getString(QUESTIONS, null)
        return if (questionsJ != null) Gson().fromJson<Questions>(questionsJ, Questions::class.java)
        else Questions()
    }

    fun saveRecentExams(recentExam: Exams?) {
        editor.putString(RECENT_EXAM, Gson().toJson(recentExam))
        editor.apply()
    }

    fun loadRecentExams(): Exams {
        val examJ = sp.getString(RECENT_EXAM, null)
        return if (examJ != null) Gson().fromJson<Exams>(examJ, Exams::class.java)
        else Exams()
    }

    var welcomePageShowed: Boolean
        get() = sp.getBoolean(WELCOME_PAGE_SHOWED, false)
        set(welcomePageShowed) {
            editor.putBoolean(WELCOME_PAGE_SHOWED, welcomePageShowed)
            editor.apply()
        }

    var dismissSide: String?
        get() = sp.getString(DISMISS_SIDE, null)
        set(dismissSide) {
            editor.putString(DISMISS_SIDE, dismissSide)
            editor.apply()
        }

    var appPassword: Int
        get() = sp.getInt(APP_PASSWORD, DEF_PASSKEY)
        set(appPassword) {
            editor.putInt(APP_PASSWORD, appPassword)
            editor.apply()
        }

    var courseCancellingFirstUse: Boolean
        get() = sp.getBoolean(CANCELLING_COURSE_FIRST_USE, false)
        set(fuCourseCancelling) {
            editor.putBoolean(CANCELLING_COURSE_FIRST_USE, fuCourseCancelling)
            editor.apply()
        }

    var examFileCancellingFirstUse: Boolean
        get() = sp.getBoolean(CANCELLING_EXAM_FILE_FIRST_USE, false)
        set(fuExamFileCancelling) {
            editor.putBoolean(CANCELLING_EXAM_FILE_FIRST_USE, fuExamFileCancelling)
            editor.apply()
        }

    var keepScreenOn: Boolean
        get() = sp.getBoolean(KEEP_SCREEN_ON, false)
        set(keepScreenOn) {
            editor.putBoolean(KEEP_SCREEN_ON, keepScreenOn)
            editor.apply()
        }

    var vibrationEffects: Boolean
        get() = sp.getBoolean(VIBRATION_EFFECTS, true)
        set(vibrationEffects) {
            editor.putBoolean(VIBRATION_EFFECTS, vibrationEffects)
            editor.apply()
        }

    var musicEffects: Boolean
        get() = sp.getBoolean(MUSIC_EFFECTS, true)
        set(musicEffects) {
            editor.putBoolean(MUSIC_EFFECTS, musicEffects)
            editor.apply()
        }

    var categorizeFirstUse: Boolean
        get() = sp.getBoolean(CATEGORIZE_FIRST_USE, false)
        set(fuCategorize) {
            editor.putBoolean(CATEGORIZE_FIRST_USE, fuCategorize)
            editor.apply()
        }

    var bookmarksListFirstUse: Boolean
        get() = sp.getBoolean(BOOKMARKS_LIST_FIRST_USE, false)
        set(fuBookmarksList) {
            editor.putBoolean(BOOKMARKS_LIST_FIRST_USE, fuBookmarksList)
            editor.apply()
        }

    var bookmarkFirstUse: Boolean
        get() = sp.getBoolean(BOOKMARK_FIRST_USE, false)
        set(fuBookmarksList) {
            editor.putBoolean(BOOKMARK_FIRST_USE, fuBookmarksList)
            editor.apply()
        }

    var manualQuestionsFirstUse: Boolean
        get() = sp.getBoolean(MANUAL_QUESTIONS_FIRST_USE, false)
        set(fuManualQuestions) {
            editor.putBoolean(MANUAL_QUESTIONS_FIRST_USE, fuManualQuestions)
            editor.apply()
        }

    var howToUseHelpShowingFirstUse: Boolean
        get() = sp.getBoolean(HOW_TO_USE_HELP_SHOWING_FIRST_USE, false)
        set(fuHowToUseHelpShowing) {
            editor.putBoolean(HOW_TO_USE_HELP_SHOWING_FIRST_USE, fuHowToUseHelpShowing)
            editor.apply()
        }

    var howToDeleteCourseFirstUse: Boolean
        get() = sp.getBoolean(HOW_TO_DELETE_COURSE_FIRST_USE, false)
        set(fuHowToDeleteCourse) {
            editor.putBoolean(HOW_TO_DELETE_COURSE_FIRST_USE, fuHowToDeleteCourse)
            editor.apply()
        }

    var howToDeleteBookmarkFirstUse: Boolean
        get() = sp.getBoolean(HOW_TO_DELETE_BOOKMARK_FIRST_USE, false)
        set(fuDeleteBookmark) {
            editor.putBoolean(HOW_TO_DELETE_BOOKMARK_FIRST_USE, fuDeleteBookmark)
            editor.apply()
        }

    var backupCreatingStatus: Boolean
        get() = sp.getBoolean(BACKUP_CREATING_STATUS, false)
        set(bkpCreating) {
            editor.putBoolean(BACKUP_CREATING_STATUS, bkpCreating)
            editor.apply()
        }

    var backupRestoringStatus: Boolean
        get() = sp.getBoolean(BACKUP_RESTORING_STATUS, false)
        set(bkpRestoring) {
            editor.putBoolean(BACKUP_RESTORING_STATUS, bkpRestoring)
            editor.apply()
        }

    fun saveProductInfo(product: Product?) {
        editor.putString(PRODUCT_INFO, Gson().toJson(product))
        editor.apply()
    }

    fun loadProductInfo(): Product {
        val productJ = sp.getString(PRODUCT_INFO, null)
        return if (productJ != null) Gson().fromJson(productJ, Product::class.java)
        else Product()
    }

    fun saveRecentFeedback(feedback: Feedback?) {
        editor.putString(RECENT_FEEDBACK, Gson().toJson(feedback))
        editor.apply()
    }

    fun loadRecentFeedback(): Feedback? {
        val feedbackJ = sp.getString(RECENT_FEEDBACK, null)
        return if (feedbackJ != null) Gson().fromJson(feedbackJ, Feedback::class.java)
        else null
    }

    var takeProblemScreenshotFirstUse: Boolean
        get() = sp.getBoolean(TAKE_PROBLEM_SCREENSHOT_FIRST_USE, false)
        set(fuTakeScreenshot) {
            editor.putBoolean(TAKE_PROBLEM_SCREENSHOT_FIRST_USE, fuTakeScreenshot)
            editor.apply()
        }

    var draftDrawingFirstUse: Boolean
        get() = sp.getBoolean(DRAFT_DRAWING_FIRST_USE, false)
        set(fuDraftDrawing) {
            editor.putBoolean(DRAFT_DRAWING_FIRST_USE, fuDraftDrawing)
            editor.apply()
        }

    var examActionFirstUse: Boolean
        get() = sp.getBoolean(EXAM_ACTION_FIRST_USE, false)
        set(fuExamAction) {
            editor.putBoolean(EXAM_ACTION_FIRST_USE, fuExamAction)
            editor.apply()
        }

    var resetChronometerFirstUse: Boolean
        get() = sp.getBoolean(RESET_CHRONOMETER_FIRST_USE, false)
        set(fuResetChronometer) {
            editor.putBoolean(RESET_CHRONOMETER_FIRST_USE, fuResetChronometer)
            editor.apply()
        }

    var changePDFPageFirstUse: Boolean
        get() = sp.getBoolean(CHANGE_PDF_PAGE_FIRST_USE, false)
        set(fuChangePDFPage) {
            editor.putBoolean(CHANGE_PDF_PAGE_FIRST_USE, fuChangePDFPage)
            editor.apply()
        }

    var howToFullScreenFirstUse: Boolean
        get() = sp.getBoolean(HOW_TO_FULL_SCREEN_FIRST_USE, false)
        set(fuHowToFullScreenFirstUse) {
            editor.putBoolean(HOW_TO_FULL_SCREEN_FIRST_USE, fuHowToFullScreenFirstUse)
            editor.apply()
        }

    val newContinueExamScreenFirstUse: Boolean
        get() = sp.getBoolean(NEW_CONTINUE_EXAM_FIRST_USE, false)

    fun setNewContinueExamFirstUse(fuNewContinueExam: Boolean) {
        editor.putBoolean(NEW_CONTINUE_EXAM_FIRST_USE, fuNewContinueExam)
        editor.apply()
    }

    var newFeaturesShowed: Boolean
        get() = sp.getBoolean(NEW_FEATURES_SHOWED, false)
        set(newFeaturesShowed) {
            editor.putBoolean(NEW_FEATURES_SHOWED, newFeaturesShowed)
            editor.apply()
        }

    fun checkAppRestartWanted(): Boolean {
        return sp.getBoolean(APP_RESTART_WANTED, false)
    }

    fun setAppRestartWanted(appRestartWanted: Boolean) {
        editor.putBoolean(APP_RESTART_WANTED, appRestartWanted)
        editor.apply()
    }

    fun checkAddDeviceWasCommitted(): Boolean {
        return sp.getBoolean(ADD_DEVICE_WAS_COMMITTED, false)
    }

    fun setAddDeviceWasCommitted(addDeviceWasCommitted: Boolean) {
        editor.putBoolean(ADD_DEVICE_WAS_COMMITTED, addDeviceWasCommitted)
        editor.apply()
    }

    fun saveDeviceInfo(device: Device) {
        editor.putString(DEVICE_INFO, Gson().toJson(device))
        editor.apply()
    }

    fun loadDeviceInfo(): Device {
        val deviceJ = sp.getString(DEVICE_INFO, null)
        return if (deviceJ != null) Gson().fromJson<Device>(
            deviceJ,
            Device::class.java
        )
        else Device()
    }

    fun saveRecentActivities(activities: Activities) {
        editor.putString(RECENT_ACTIVITIES, Gson().toJson(activities))
        editor.apply()
    }

    fun loadRecentActivities(): Activities {
        val activitiesJ = sp.getString(RECENT_ACTIVITIES, null)
        return if (activitiesJ != null) Gson().fromJson<Activities>(
            activitiesJ,
            Activities::class.java
        )
        else Activities()
    }

    fun saveExamWallpapers(wallpapers: ExamWallpapers?) {
        editor.putString(EXAM_WALLPAPERS, Gson().toJson(wallpapers, ExamWallpapers::class.java))
        editor.apply()
    }

    fun loadExamWallpapers(): ExamWallpapers {
        val activitiesJ = sp.getString(EXAM_WALLPAPERS, null)
        return if (activitiesJ != null) Gson().fromJson<ExamWallpapers>(
            activitiesJ,
            ExamWallpapers::class.java
        )
        else ExamWallpapers(context)
    }

    var lastStatusBarColorState: Boolean
        /**
         * Get StatusBar color
         *
         * @return true if is Light and false if is Dark, Default: true
         */
        get() = sp.getBoolean(STATUS_BAR_COLOR_STATE, true)
        /**
         * Set StatusBar color
         *
         * @param colorState true is Light, false is Dark
         */
        set(colorState) {
            editor.putBoolean(STATUS_BAR_COLOR_STATE, colorState)
            editor.apply()
        }

    companion object {
        const val DEF_PASSKEY: Int = 0
        private const val BOOKMARKS = "bookmarks"
        private const val COURSES = "examNames"
        private const val QUESTIONS = "questions"
        private const val RECENT_EXAM = "recent_exams"
        private const val WELCOME_PAGE_SHOWED = "welcome_page_showed"
        private const val DISMISS_SIDE = "dismiss_side"
        private const val KEEP_SCREEN_ON = "keep_screen_on"
        private const val VIBRATION_EFFECTS = "vibration_effects"
        private const val MUSIC_EFFECTS = "music_effects"
        private const val CATEGORIZE_FIRST_USE = "categorize_first_use"
        private const val BOOKMARKS_LIST_FIRST_USE = "bookmarks_list_first_use"
        private const val BOOKMARK_FIRST_USE = "bookmark_first_use"
        private const val MANUAL_QUESTIONS_FIRST_USE = "manual_questions_first_use"
        private const val HOW_TO_USE_HELP_SHOWING_FIRST_USE = "how_to_use_help_showing_first_use"
        private const val HOW_TO_DELETE_COURSE_FIRST_USE = "how_to_delete_course_first_use"
        private const val HOW_TO_DELETE_BOOKMARK_FIRST_USE = "how_to_delete_bookmark_first_use"
        private const val TAKE_PROBLEM_SCREENSHOT_FIRST_USE = "take_problem_screenshot_first_use"
        private const val BACKUP_CREATING_STATUS = "backup_creating_status"
        private const val BACKUP_RESTORING_STATUS = "backup_restoring_status"
        private const val PRODUCT_INFO = "product_info"
        private const val RECENT_FEEDBACK = "recent_feedback"
        private const val CANCELLING_COURSE_FIRST_USE = "cancelling_course_first_use"
        private const val CANCELLING_EXAM_FILE_FIRST_USE = "cancelling_exam_file_first_use"
        private const val DRAFT_DRAWING_FIRST_USE = "draft_drawing_first_use"
        private const val EXAM_ACTION_FIRST_USE = "exam_action_first_use"
        private const val RESET_CHRONOMETER_FIRST_USE = "reset_chronometer_first_use"
        private const val CHANGE_PDF_PAGE_FIRST_USE = "change_pdf_page_first_use"
        private const val HOW_TO_FULL_SCREEN_FIRST_USE = "how_to_full_screen_first_use"
        private const val NEW_FEATURES_SHOWED = "new_features_showed"
        private const val APP_RESTART_WANTED = "app_restart_wanted"
        private const val NEW_CONTINUE_EXAM_FIRST_USE = "new_continue_exam_first_use"
        private const val ADD_DEVICE_WAS_COMMITTED = "add_device_was_committed"
        private const val DEVICE_INFO = "device_info"
        private const val RECENT_ACTIVITIES = "recent_activities"
        private const val CURRENT_FAVORITE_EXAM = "current_favorite_exam"
        private const val APP_PASSWORD = "app_password"
        private const val STATUS_BAR_COLOR_STATE = "status_bar_color_state"
        private const val EXAM_WALLPAPERS = "exam_wallpapers"
        private var instance: Saver? = null
        fun getInstance(context: Context): Saver {
            if (instance == null) {
                instance = Saver(context)
            }
            return instance!!
        }
    }
}
