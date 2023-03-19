package ir.saltech.answersheet.object.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Arrays;

public class Exam extends Thing {

    public enum CorrectionMode {
        None, Normal, Keys;
    }


    public enum ExamStatus {
        Creating, Started, Suspended, Finished, Correcting, Checked;
    }

    private ExamStatus examStatus;

    private int id;
    @Deprecated
    private String examName;
    private ExamName name;
    private Questions answerSheet;
    @Deprecated
    private int[] examQuestionsRange = new int[4]; // MIN , MAX , COUNT , PATTERN
    private QuestionRange questionRange;
    private int reviewCount;
    private long examTime;
    private int chornoThreshold;
    private long examTimeLeft;
    private String features;
    private String explain = "-";
    private String startExamTime;
    private Document examFile;
    private CorrectionMode correctionMode;
    private double examScore;
    private int examFilePagesCount;
    private int currentFilePage;
    private int lastScrollPosition;
    private long secondsOfThinkingOnQuestion;
    private int runningCategory = -1;
    private boolean isCreating;
    private boolean isSuspended; // When isStarted false, but user wants to continue it (exam).
    private boolean isStarted; // When exam started, true
    private boolean isChecked; // When the exam correction process was ended by user
    private boolean isCorrecting;
    private boolean usedCorrection;
    private boolean usedCategorize;
    private boolean usedRandomQuestions;
    private boolean usedTiming;
    private boolean showCorrectsWrongsWithColor = true;
    private boolean usedCorrectionByCorrectAnswers;
    private boolean canCalculateScoreOfCategory;
    private boolean usedChronometer;
    private boolean hasAdditionalScore;
    private boolean examStoppedManually;
    private boolean selectQuestionsManually;
    private boolean examTimeEdited;
    private boolean loading;
    private boolean examFileCollapsed;
    private boolean answerSheetCollapsed;
    private boolean examHeaderCollapsed;
    private boolean canCalculateTimeForCategory;
    private boolean hasReducedSecond;
    private boolean favorite;
    private boolean isEditingCategoryTimes;

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public boolean isEditingCategoryTimes() {
        return isEditingCategoryTimes;
    }

    public void setEditingCategoryTimes(boolean editingCategoryTimes) {
        isEditingCategoryTimes = editingCategoryTimes;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    public boolean isHasReducedSecond() {
        return hasReducedSecond;
    }

    public void setHasReducedSecond(boolean hasReducedSecond) {
        this.hasReducedSecond = hasReducedSecond;
    }

    public int getRunningCategory() {
        return runningCategory;
    }

    public void setRunningCategory(int runningCategory) {
        this.runningCategory = runningCategory;
    }

    public boolean isCanCalculateTimeForCategory() {
        return canCalculateTimeForCategory;
    }

    public void setCanCalculateTimeForCategory(boolean canCalculateTimeForCategory) {
        this.canCalculateTimeForCategory = canCalculateTimeForCategory;
    }

    public int getExamFilePagesCount() {
        return examFilePagesCount;
    }

    public void setExamFilePagesCount(int examFilePagesCount) {
        this.examFilePagesCount = examFilePagesCount;
    }

    public boolean isExamFileCollapsed() {
        return examFileCollapsed;
    }

    public void setExamFileCollapsed(boolean examFileCollapsed) {
        this.examFileCollapsed = examFileCollapsed;
    }

    public boolean isAnswerSheetCollapsed() {
        return answerSheetCollapsed;
    }

    public void setAnswerSheetCollapsed(boolean answerSheetCollapsed) {
        this.answerSheetCollapsed = answerSheetCollapsed;
    }

    public boolean isExamHeaderCollapsed() {
        return examHeaderCollapsed;
    }

    public void setExamHeaderCollapsed(boolean examHeaderCollapsed) {
        this.examHeaderCollapsed = examHeaderCollapsed;
    }

    public int getCurrentFilePage() {
        return currentFilePage;
    }

    public void setCurrentFilePage(int currentFilePage) {
        this.currentFilePage = currentFilePage;
    }

    public boolean isLoading() {
        return loading;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    public boolean isExamTimeEdited() {
        return examTimeEdited;
    }

    public void setExamTimeEdited(boolean examTimeEdited) {
        this.examTimeEdited = examTimeEdited;
    }

    public Document getExamFile() {
        return examFile;
    }

    public void setExamFile(Document examFile) {
        this.examFile = examFile;
    }

    public boolean isShowCorrectsWrongsWithColor() {
        return showCorrectsWrongsWithColor;
    }

    public void setShowCorrectsWrongsWithColor(boolean showCorrectsWrongsWithColor) {
        this.showCorrectsWrongsWithColor = showCorrectsWrongsWithColor;
    }

    public int getLastScrollPosition() {
        return lastScrollPosition;
    }

    public void setLastScrollPosition(int lastScrollPosition) {
        this.lastScrollPosition = lastScrollPosition;
    }

    public String getFeatures() {
        return features;
    }

    public void setFeatures(String features) {
        this.features = features;
    }

    public CorrectionMode getCorrectionMode() {
        if (correctionMode == null) {
            if (usedCorrection) {
                if (usedCorrectionByCorrectAnswers) {
                    correctionMode = CorrectionMode.Keys;
                } else {
                    correctionMode = CorrectionMode.Normal;
                }
            } else {
                correctionMode = CorrectionMode.None;
            }
        }
        return correctionMode;
    }

    public void setCorrectionMode(CorrectionMode correctionMode) {
        this.correctionMode = correctionMode;
    }

    public boolean isCreating() {
        return isCreating;
    }

    public void setCreating(boolean creating) {
        isCreating = creating;
    }

    public boolean isSelectQuestionsManually() {
        return selectQuestionsManually;
    }

    public void setSelectQuestionsManually(boolean selectQuestionsManually) {
        this.selectQuestionsManually = selectQuestionsManually;
    }

    public boolean isExamStoppedManually() {
        return examStoppedManually;
    }

    public void setExamStoppedManually(boolean examStoppedManually) {
        this.examStoppedManually = examStoppedManually;
    }

    public boolean isUsedCorrectionByCorrectAnswers() {
        return usedCorrectionByCorrectAnswers;
    }

    public void setUsedCorrectionByCorrectAnswers(boolean usedCorrectionByCorrectAnswers) {
        this.usedCorrectionByCorrectAnswers = usedCorrectionByCorrectAnswers;
    }

    public boolean isHasAdditionalScore() {
        return hasAdditionalScore;
    }

    public void setHasAdditionalScore(boolean hasAdditionalScore) {
        this.hasAdditionalScore = hasAdditionalScore;
    }

    public boolean isCorrecting() {
        return isCorrecting;
    }

    public void setCorrecting(boolean correcting) {
        isCorrecting = correcting;
    }

    public ExamStatus getExamStatus() {
        if (examStatus == null) {
            if (isStarted) {
                examStatus = ExamStatus.Started;
            } else if (isCreating) {
                examStatus = ExamStatus.Creating;
            } else if (isSuspended) {
                examStatus = ExamStatus.Suspended;
            } else if (isChecked) {
                examStatus = ExamStatus.Checked;
            } else if (isCorrecting) {
                examStatus = ExamStatus.Correcting;
            } else {
                examStatus = ExamStatus.Finished;
            }
        }
        return this.examStatus;
    }

    public void setExamStatus(ExamStatus status) {
        switch (status) {
            case Creating:
                this.setCreating(true);
                this.setStarted(false);
                this.setChecked(false);
                this.setCorrecting(false);
                this.setSuspended(false);
                break;
            case Started:
                this.setCreating(false);
                this.setStarted(true);
                this.setChecked(false);
                this.setCorrecting(false);
                this.setSuspended(false);
                break;
            case Suspended:
                this.setCreating(false);
                this.setStarted(false);
                this.setChecked(false);
                this.setCorrecting(false);
                this.setSuspended(true);
                break;
            case Finished:
                this.setCreating(false);
                this.setStarted(false);
                this.setChecked(false);
                this.setCorrecting(false);
                this.setSuspended(false);
                break;
            case Correcting:
                this.setCreating(false);
                this.setStarted(false);
                this.setChecked(false);
                this.setCorrecting(true);
                this.setSuspended(false);
                break;
            case Checked:
                this.setCreating(false);
                this.setStarted(false);
                this.setChecked(true);
                this.setCorrecting(false);
                this.setSuspended(false);
                break;
            default:
                break;
        }
        this.examStatus = status;
    }

    public boolean isUsedTiming() {
        return usedTiming;
    }

    public void setUsedTiming(boolean usedTiming) {
        this.usedTiming = usedTiming;
    }

    public boolean isSuspended() {
        return isSuspended;
    }

    public void setSuspended(boolean suspended) {
        isSuspended = suspended;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStartExamTime() {
        return startExamTime;
    }

    public void setStartExamTime(String startExamTime) {
        this.startExamTime = startExamTime;
    }

    public boolean isUsedChronometer() {
        return usedChronometer;
    }

    public void setUsedChronometer(boolean usedChronometer) {
        this.usedChronometer = usedChronometer;
    }

    public int getChornoThreshold() {
        return chornoThreshold;
    }

    public void setChornoThreshold(int chornoThreshold) {
        this.chornoThreshold = chornoThreshold;
    }

    public boolean isUsedRandomQuestions() {
        return usedRandomQuestions;
    }

    public void setUsedRandomQuestions(boolean usedRandomQuestions) {
        this.usedRandomQuestions = usedRandomQuestions;
    }

    public long getSecondsOfThinkingOnQuestion() {
        return secondsOfThinkingOnQuestion;
    }

    public void setSecondsOfThinkingOnQuestion(long secondsOfThinkingOnQuestion) {
        this.secondsOfThinkingOnQuestion = secondsOfThinkingOnQuestion;
    }

    public boolean isCanCalculateScoreOfCategory() {
        return canCalculateScoreOfCategory;
    }

    public void setCanCalculateScoreOfCategory(boolean canCalculateScoreOfCategory) {
        this.canCalculateScoreOfCategory = canCalculateScoreOfCategory;
    }

    public boolean isUsedCategorize() {
        return usedCategorize;
    }

    public void setUsedCategorize(boolean usedCategorize) {
        this.usedCategorize = usedCategorize;
    }

    @Nullable
    public String getExplain() {
        return explain;
    }

    public void setExplain(String explain) {
        this.explain = explain;
    }

    /**
     * Get exam name as ExamName
     * @param idle this parameter is disabled!
     * @return exam name as ExamName
     */
    public ExamName getExamName(int idle){
        return name;
    }

    @Deprecated
    public String getExamName() {
        if (name != null)
            return name.getName();
        else {
            return examName;
        }
    }

    public void setExamName(ExamName name) {
        this.name = name;
    }

    @Deprecated
    public int[] getExamQuestionsRange() {
        if (questionRange != null) {
            return questionRange.getRange();
        } else {
            QuestionRange range = new QuestionRange();
            range.setRange(examQuestionsRange);
            setQuestionRange(range);
            return examQuestionsRange;
        }
    }

    @Deprecated
    public void setExamQuestionsRange(@NonNull int[] examQuestionsRange) {
        this.examQuestionsRange = examQuestionsRange;
        QuestionRange range = new QuestionRange();
        range.setRange(examQuestionsRange);
        setQuestionRange(range);
    }

    public QuestionRange getQuestionRange() {
        if (questionRange == null) {
            questionRange = new QuestionRange();
            questionRange.setRange(examQuestionsRange);
        }
        return questionRange;
    }

    public void setQuestionRange(QuestionRange questionRange) {
        this.questionRange = questionRange;
    }

    public long getExamTime() {
        return examTime;
    }

    public void setExamTime(long examTime) {
        this.examTime = examTime;
    }

    public long getExamTimeLeft() {
        return examTimeLeft;
    }

    public void setExamTimeLeft(long examTimeLeft) {
        this.examTimeLeft = examTimeLeft;
    }

    public Questions getAnswerSheet() {
        return answerSheet;
    }

    public void setAnswerSheet(Questions answerSheet) {
        this.answerSheet = answerSheet;
    }

    public boolean isStarted() {
        return isStarted;
    }

    public void setStarted(boolean started) {
        isStarted = started;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public double getExamScore() {
        return examScore;
    }

    public void setExamScore(double examScore) {
        this.examScore = examScore;
    }

    public boolean isUsedCorrection() {
        return usedCorrection;
    }

    public void setUsedCorrection(boolean usedCorrection) {
        this.usedCorrection = usedCorrection;
    }

    @NonNull
    @Override
    public String toString() {
        return "Exam{" +
                "examCourseName='" + examName + '\'' +
                ", answerSheet=" + answerSheet +
                ", examQuestionsRange=" + Arrays.toString(getExamQuestionsRange()) +
                ", examTime=" + examTime +
                ", examTimeLeft=" + examTimeLeft +
                ", explain='" + explain + '\'' +
                ", isStarted=" + isStarted +
                ", isChecked=" + isChecked +
                ", examScore=" + examScore +
                ", usedCorrection=" + usedCorrection +
                '}';
    }
}
