package ir.saltech.answersheet.`object`.data

class Exam : Thing() {
    enum class CorrectionMode {
        None, Normal, Keys
    }


    enum class ExamStatus {
        Creating, Started, Suspended, Finished, Correcting, Checked
    }

    var examStatus: ExamStatus? = null
        get() {
            if (field == null) {
                field = if (isStarted) {
                    ExamStatus.Started
                } else if (isCreating) {
                    ExamStatus.Creating
                } else if (isSuspended) {
                    ExamStatus.Suspended
                } else if (isChecked) {
                    ExamStatus.Checked
                } else if (isCorrecting) {
                    ExamStatus.Correcting
                } else {
                    ExamStatus.Finished
                }
            }
            return field
        }
        set(status) {
            when (status) {
                ExamStatus.Creating -> {
                    this.isCreating = true
                    this.isStarted = false
                    this.isChecked = false
                    this.isCorrecting = false
                    this.isSuspended = false
                }

                ExamStatus.Started -> {
                    this.isCreating = false
                    this.isStarted = true
                    this.isChecked = false
                    this.isCorrecting = false
                    this.isSuspended = false
                }

                ExamStatus.Suspended -> {
                    this.isCreating = false
                    this.isStarted = false
                    this.isChecked = false
                    this.isCorrecting = false
                    this.isSuspended = true
                }

                ExamStatus.Finished -> {
                    this.isCreating = false
                    this.isStarted = false
                    this.isChecked = false
                    this.isCorrecting = false
                    this.isSuspended = false
                }

                ExamStatus.Correcting -> {
                    this.isCreating = false
                    this.isStarted = false
                    this.isChecked = false
                    this.isCorrecting = true
                    this.isSuspended = false
                }

                ExamStatus.Checked -> {
                    this.isCreating = false
                    this.isStarted = false
                    this.isChecked = true
                    this.isCorrecting = false
                    this.isSuspended = false
                }

                else -> {}
            }
            field = status
        }

    var id: Int = 0

    @Deprecated("")
    private val examName: String? = null
    private var name: ExamName? = null
    var answerSheet: Questions? = null

    @Deprecated("")
    private var examQuestionsRange = IntArray(4) // MIN , MAX , COUNT , PATTERN
    private var questionRange: QuestionRange? = null
    var reviewCount: Int = 0
    var examTime: Long = 0
    var chornoThreshold: Int = 0
    var examTimeLeft: Long = 0
    var features: String? = null
    private var explain = "-"
    var startExamTime: String? = null
    var examFile: Document? = null
    var correctionMode: CorrectionMode? = null
        get() {
            if (field == null) {
                field = if (isUsedCorrection) {
                    if (isUsedCorrectionByCorrectAnswers) {
                        CorrectionMode.Keys
                    } else {
                        CorrectionMode.Normal
                    }
                } else {
                    CorrectionMode.None
                }
            }
            return field
        }
    var examScore: Double = 0.0
    var examFilePagesCount: Int = 0
    var currentFilePage: Int = 0
    var lastScrollPosition: Int = 0
    var secondsOfThinkingOnQuestion: Long = 0
    var runningCategory: Int = -1
    var isCreating: Boolean = false
    var isSuspended: Boolean = false // When isStarted false, but user wants to continue it (exam).
    var isStarted: Boolean = false // When exam started, true
    var isChecked: Boolean = false // When the exam correction process was ended by user
    var isCorrecting: Boolean = false
    var isUsedCorrection: Boolean = false
    var isUsedCategorize: Boolean = false
    var isUsedRandomQuestions: Boolean = false
    var isUsedTiming: Boolean = false
    var isShowCorrectsWrongsWithColor: Boolean = true
    var isUsedCorrectionByCorrectAnswers: Boolean = false
    var isCanCalculateScoreOfCategory: Boolean = false
    var isUsedChronometer: Boolean = false
    var hasAdditionalScore: Boolean = false
    var isExamStoppedManually: Boolean = false
    var isSelectQuestionsManually: Boolean = false
    var isExamTimeEdited: Boolean = false
    var isLoading: Boolean = false
    var isExamFileCollapsed: Boolean = false
    var isAnswerSheetCollapsed: Boolean = false
    var isExamHeaderCollapsed: Boolean = false
    var isCanCalculateTimeForCategory: Boolean = false
    var isHasReducedSecond: Boolean = false
    var isFavorite: Boolean = false
    var isEditingCategoryTimes: Boolean = false

    fun getExplain(): String? {
        return explain
    }

    fun setExplain(explain: String) {
        this.explain = explain
    }

    /**
     * Get exam name as ExamName
     * @param idle this parameter is disabled!
     * @return exam name as ExamName
     */
    fun getExamName(idle: Int): ExamName? {
        return name
    }

    @Deprecated("")
    fun getExamName(): String? {
        return if (name != null) name!!.getName()
        else {
            examName
        }
    }

    fun setExamName(name: ExamName?) {
        this.name = name
    }

    @Deprecated("")
    fun getExamQuestionsRange(): IntArray? {
        if (questionRange != null) {
            return questionRange!!.range
        } else {
            val range = QuestionRange()
            range.range = examQuestionsRange
            setQuestionRange(range)
            return examQuestionsRange
        }
    }

    @Deprecated("")
    fun setExamQuestionsRange(examQuestionsRange: IntArray) {
        this.examQuestionsRange = examQuestionsRange
        val range = QuestionRange()
        range.range = examQuestionsRange
        setQuestionRange(range)
    }

    fun getQuestionRange(): QuestionRange {
        if (questionRange == null) {
            questionRange = QuestionRange()
            questionRange!!.range = examQuestionsRange
        }
        return questionRange!!
    }

    fun setQuestionRange(questionRange: QuestionRange?) {
        this.questionRange = questionRange
    }

    override fun toString(): String {
        return "Exam{" +
                "examCourseName='" + examName + '\'' +
                ", answerSheet=" + answerSheet +
                ", examQuestionsRange=" + getExamQuestionsRange().contentToString() +
                ", examTime=" + examTime +
                ", examTimeLeft=" + examTimeLeft +
                ", explain='" + explain + '\'' +
                ", isStarted=" + isStarted +
                ", isChecked=" + isChecked +
                ", examScore=" + examScore +
                ", usedCorrection=" + isUsedCorrection +
                '}'
    }
}
