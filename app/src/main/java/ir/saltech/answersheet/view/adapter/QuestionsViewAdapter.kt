package ir.saltech.answersheet.view.adapter;

import static android.graphics.PorterDuff.Mode.SRC_IN;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static ir.saltech.answersheet.view.container.BlurViewHolder.setBlurView;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Vibrator;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import eightbitlab.com.blurview.BlurView;
import ir.saltech.answersheet.R;
import ir.saltech.answersheet.intf.listener.QuestionClickedListener;
import ir.saltech.answersheet.intf.listener.ThingSelectedListener;
import ir.saltech.answersheet.object.container.Saver;
import ir.saltech.answersheet.object.data.Bookmark;
import ir.saltech.answersheet.object.data.Bookmarks;
import ir.saltech.answersheet.object.data.Category;
import ir.saltech.answersheet.object.data.CategoryColor;
import ir.saltech.answersheet.object.data.Exam;
import ir.saltech.answersheet.object.data.Exams;
import ir.saltech.answersheet.object.data.Question;
import ir.saltech.answersheet.object.data.Questions;
import ir.saltech.answersheet.object.data.Thing;
import ir.saltech.answersheet.view.activity.MainActivity;
import ir.saltech.answersheet.view.container.MaterialAlertDialog;
import ir.saltech.answersheet.view.container.MaterialFragmentShower;
import ir.saltech.answersheet.view.container.Toast;
import ir.saltech.answersheet.view.dialog.SelectThingsDialog;

public class QuestionsViewAdapter extends RecyclerView.Adapter<QuestionsViewAdapter.QuestionViewHolder> {
    public static final int MAX_OF_CHOICES = 4;
    private static final int TWO_DIGIT_NUM = 10;
    private final Exam currentExam;
    private final QuestionClickedListener questionClickedListener;
    private final Random random = new Random();
    private final Activity activity;
    private final int accentColor;
    private List<Category> categories;
    private List<Question> questions;
    private boolean isCorrectingByCorrectAnswer;
    private Vibrator vibrator;
    private Context context;
    private boolean questionNumberClicked;

    public QuestionsViewAdapter(List<Question> questions, int accentColor, @Nullable List<Category> categories, Exam currentExam, @NonNull QuestionClickedListener questionClickedListener, Activity activity) {
        this.questions = questions;
        this.currentExam = currentExam;
        this.accentColor = accentColor;
        this.activity = activity;
        if (categories == null) this.categories = new ArrayList<>();
        else this.categories = categories;
        if (questions == null) this.questions = new ArrayList<>();
        else this.questions = questions;
        this.questionClickedListener = questionClickedListener;
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        return new QuestionViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_template_question, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
        setQuestionDefaults(holder, position);
        if (currentExam != null) {
            setQuestionSelected(holder, position);
            setupCategoryOption(holder, position);
            loadCurrentBookmark(holder, position);
            loadQuestionSelectedChoice(holder, position);
            onQuestionChoiceSelected(holder, position);
            setupQuestionCorrection(holder, position);
            setupQuestionCorrected(holder, position);
            setupQuestionExamNotStarted(holder, position);
            setupQuestionExamStarted(holder, position);
        }
    }

    private void setupQuestionExamStarted(QuestionViewHolder holder, int position) {
        if (currentExam.isStarted() && !currentExam.isCreating()) {
            holder.questionNumber.setClickable(true);
            if (!currentExam.isUsedCategorize())
                holder.questionNumber.setTextColor(accentColor);
            else {
                if (currentExam.isCanCalculateTimeForCategory()) {
                    if (questions.get(position).getCategory() != null) {
                        int categoryPosition = getCategoryPosition(questions.get(position).getCategory());
                        if (questions.get(position).getCategory().getTime() >= 1000) {
                            //holder.disableQuestion.setVisibility(GONE);
                            long examTimeLi = categories.get(categoryPosition).getTime();
                            if (examTimeLi >= 60_000)
                                Log.i("TAG", "PPPP " + categories.get(categoryPosition).getTitle() + " " + printTime((int) ((((double) examTimeLi) % 60_000) / 1000), (int) (((double) examTimeLi) / 60_000)));
                            else
                                Log.i("TAG", "PPPP " + categories.get(categoryPosition).getTitle() + " " + printTime((int) (((double) examTimeLi) / 1000), 0));
                        } else {
                            if (!questions.get(position).getCategory().isFinished()) {
                                Toast.makeText(activity, "زمان شما برای انجام دسته «" + questions.get(position).getCategory().getTitle() + "» تمام شد.", Toast.WARNING_SIGN, Toast.LENGTH_LONG).show();
                                categories.get(categoryPosition).setFinished(true);
                                updateCategory(categories.get(categoryPosition));
                            }
                            clearChoicesBackground(holder.choices, position);
                            enableDisableChoicesGroup(holder, false);
                            holder.editCategory.setVisibility(GONE);
                        }
                    }
                }
            }
            showClickBookmarks(holder, position);
        }
    }

    private void submitThisBookmark(QuestionViewHolder holder, int position, Bookmark selectedBookmark) {
        holder.questionNumber.setClickable(true);
        questions.get(position).setBookmark(selectedBookmark);
        questions.get(position).setBookmark(0);
        if (selectedBookmark.getName().equals(Bookmark.NONE)) {
            holder.questionNumber.setBackground(null);
        } else {
            currentExam.setSecondsOfThinkingOnQuestion(0);
            holder.questionNumber.setBackground(context.getResources().getDrawable(R.drawable.bookmark_pin));
            holder.questionNumber.getBackground().setColorFilter(selectedBookmark.getPinColor().getColor(), SRC_IN);
            vibrateDevice(50);
        }
        updateRecentExams();
        updateQuestionsList(position);
        questionClickedListener.onQuestionBookmarkChanged(questions.get(position));
        notifyDataSetChanged();
    }

    private CategoryColor generateCategoryColor() {
        int redColor = random.nextInt(255);
        int greenColor = random.nextInt(255);
        int blueColor = random.nextInt(255);
        CategoryColor newCColor = new CategoryColor(new int[]{redColor, greenColor, blueColor});
        boolean duplicatedColor = false;
        if (categories != null) {
            for (Category category : categories) {
                if (category.getColor().getAccentColor() == newCColor.getAccentColor()) {
                    duplicatedColor = true;
                    break;
                }
            }
        }
        if (duplicatedColor) {
            return generateCategoryColor();
        } else {
            return newCColor;
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateCategory(Category currentCategory) {
        Questions questionsL = currentExam.getAnswerSheet();
        categories = questionsL.getCategories();
        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).getId() == currentCategory.getId()) {
                categories.remove(i);
                categories.add(i, currentCategory);
                questionsL.setCategories(categories);
                Saver.getInstance(context).saveQuestions(questionsL);
                currentExam.setAnswerSheet(questionsL);
                updateRecentExams();
                for (int j = 0; j < questions.size(); j++) {
                    if (questions.get(j).getCategory() != null) {
                        if (questions.get(j).getCategory().getId() == currentCategory.getId()) {
                            questions.get(j).setCategory(currentCategory);
                            updateQuestionsList(j);
                        }
                    }
                }
                break;
            }
        }
        try {
            notifyDataSetChanged();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    private void showClickBookmarks(QuestionViewHolder holder, int position) {
        holder.questionNumber.setOnClickListener(v2 -> {
            List<Bookmark> bookmarks = Saver.getInstance(context).loadBookmarks().getBookmarks();
            if (!questions.get(position).getBookmark().getName().equals(Bookmark.NONE)) {
                bookmarks.add(0, new Bookmark(Bookmark.NONE));
            }
            MaterialFragmentShower shower = new MaterialFragmentShower(context);
            shower.setFragment(new SelectThingsDialog(shower, new Bookmarks(bookmarks).getThings(), "انتخاب نشانه", false, new ThingSelectedListener() {
                @Override
                public void onSelected(Thing thing) {

                }
            }));
            shower.setCancelable(true);
            shower.show((FragmentActivity) activity, shower);
            /*View v = LayoutInflater.from(context).inflate(R.layout.popup_select_bookmark, null);
            ImageButton closeWindow = v.findViewById(R.id.close_dialog_button);
            RecyclerView availableBookmarksView = v.findViewById(R.id.available_bookmarks);
            PopupWindow window = new PopupWindow(v, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
            window.setClippingEnabled(false);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.setElevation(30f);
            }
            closeWindow.setOnClickListener(view -> window.dismiss());
            List<Bookmark> bookmarks = Saver.getInstance(context).loadBookmarks().getBookmarks();
            if (!questions.get(position).getBookmark().getName().equals(Bookmark.NONE)) {
                bookmarks.add(0, new Bookmark(Bookmark.NONE));
            }
            BookmarksViewAdapter adapter = new BookmarksViewAdapter(activity, bookmarks, new BookmarkChangedListener() {
                @Override
                public void onBookmarkAdded(Bookmark newBookmark) {
                }

                @Override
                public void onBookmarkRemoved(Bookmark bookmark, int position) {
                }

                @Override
                public void onBookmarkSelected(Bookmark bookmark) {
                    submitThisBookmark(holder, position, bookmark);
                    window.dismiss();
                }
            });
            adapter.setSelectionMode(true, questions.get(position).getBookmark());
            availableBookmarksView.setLayoutManager(new GridLayoutManager(context, 4));
            availableBookmarksView.setAdapter(adapter);
            int yoff = -500;
            window.showAsDropDown(holder.questionNumber, (int) -convertDpToPx(SELECT_BOOKMARK_WINDOW_X_OFFSET), yoff);*/
            // TODO: Setup this..
        });
    }

    private float convertDpToPx(int dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    private int getCategoryPosition(Category c) {
        categories = currentExam.getAnswerSheet().getCategories();
        int i = 0;
        for (int j = 0; j < categories.size(); j++) {
            if (categories.get(j).getTitle().equals(c.getTitle())) {
                i = j;
                break;
            }
        }
        return i;
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

    private void setupQuestionExamNotStarted(QuestionViewHolder holder, int position) {
        if (!currentExam.isStarted() && !currentExam.isChecked()) {
            for (int i = 0; i < holder.choices.getChildCount(); i++) {
                holder.choices.getChildAt(i).setEnabled(true);
            }

            setChoicesBackground(holder, position);
            //holder.categoryIndicator.setEnabled(false);
            holder.editCategory.setVisibility(GONE);
            holder.categoryTimeRemainingLayout.setVisibility(GONE);
            if (!currentExam.isCreating()) {
                holder.questionNumber.setClickable(true);
                showClickBookmarks(holder, position);
            } else {
                if (currentExam.isSelectQuestionsManually()) clearCheck(holder.choices);
                holder.questionNumber.setOnLongClickListener(view -> {
                    MaterialAlertDialog builder = new MaterialAlertDialog(context);
                    builder.setIcon(R.drawable.delete);
                    builder.setTitle("حذف سؤال " + context.getString(R.string.num, questions.get(position).getQuestionNumber()));
                    builder.setMessage("آیا از حذف این سؤال اطمینان دارید؟");
                    builder.setNegativeButton("بله", v4 -> {
                        questions.remove(position);
                        Questions questionsE = new Questions();
                        questionsE.setQuestions(questions);
                        questionsE.setCategories(categories);
                        Saver.getInstance(context).saveQuestions(questionsE);
                        currentExam.setAnswerSheet(questionsE);
                        updateRecentExams();
                        notifyDataSetChanged();
                        questionClickedListener.onQuestionDeleted(position);
                        builder.dismiss(builder);
                    });
                    builder.setPositiveButton("خیر", v4 -> builder.dismiss(builder));
                    builder.setCancelable(false);
                    builder.show((FragmentActivity) activity);
                    return true;
                });
            }
            calculateAveTimeOfThinking(holder, position);
            if (questions.get(position).getTimeOfThinking() != 0) {
                holder.timeOfThinkingLayout.setVisibility(View.VISIBLE);
                holder.timeOfThinking.setText(holder.itemView.getContext().getString(R.string.time_of_thinking, questions.get(position).getTimeOfThinking()));
            } else {
                holder.timeOfThinkingLayout.setVisibility(GONE);
            }
            if (currentExam.isUsedCorrection() && !currentExam.isCreating()) {
                if (currentExam.isUsedCorrectionByCorrectAnswers()) {
                    holder.isQuestionAnsweredCorrectly.setVisibility(GONE);
                    isCorrectingByCorrectAnswer = true;
                    for (int childIndex = 0; childIndex < holder.choices.getChildCount(); childIndex++) {
                        holder.choices.getChildAt(childIndex).setOnClickListener(view -> {
                            int selectedChoice = getSelectedChoiceNumber(view.getId());
                            if (questions.get(position).getCorrectAnswerChoice() != selectedChoice) {
                                questions.get(position).setCorrectAnswerChoice(selectedChoice);
                                questions.get(position).setCorrect(getCorrectAnswerChoiceIndex(position) == getSelectedChoiceIndex(position));
                                updateQuestionsList(position);
                                clearChoicesBackground(holder.choices, position);
                                if (getCorrectAnswerChoiceIndex(position) == getSelectedChoiceIndex(position)) {
                                    setChoiceCorrectChose(true, holder, position);
                                    vibrateDevice(new long[]{0, 50, 50, 50});
                                } else {
                                    setChoiceCorrectChose(false, holder, position);
                                    submitNewCorrectChoice(holder, position, view);
                                }
                            }
                        });
                    }
                } else {
                    disableChoicesGroup(holder);
                    if (questions.get(position).isWhite()) {
                        for (int i = 0; i < holder.choices.getChildCount(); i++) {
                            holder.choices.getChildAt(i).setClickable(false);
                        }
                        holder.isQuestionAnsweredCorrectly.setVisibility(GONE);
                    } else {
                        for (int i = 0; i < holder.choices.getChildCount(); i++) {
                            if (i != getSelectedChoiceIndex(position) && !holder.isQuestionAnsweredCorrectly.isChecked()) {
                                holder.choices.getChildAt(i).setClickable(true);
                                holder.choices.getChildAt(i).setOnClickListener(view -> {
                                    int selectedChoice = getSelectedChoiceNumber(view.getId());
                                    if (questions.get(position).getCorrectAnswerChoice() != selectedChoice) {
                                        questions.get(position).setCorrectAnswerChoice(selectedChoice);
                                        questions.get(position).setCorrect(getCorrectAnswerChoiceIndex(position) == getSelectedChoiceIndex(position));
                                        updateQuestionsList(position);
                                        clearChoicesBackground(holder.choices, position);
                                        if (getCorrectAnswerChoiceIndex(position) == getSelectedChoiceIndex(position)) {
                                            setChoiceCorrectChose(true, holder, position);
                                            vibrateDevice(new long[]{0, 50, 50, 50});
                                        } else {
                                            setChoiceCorrectChose(false, holder, position);
                                            submitNewCorrectChoice(holder, position, view);
                                        }
                                    }
                                });
                            }
                        }
                        holder.isQuestionAnsweredCorrectly.setVisibility(View.VISIBLE);
                    }
                }
            } else {
                disableChoicesGroup(holder);
            }
            /*if (questions.get(position).getCategory() == null) {
                holder.categoryIndicator.setVisibility(GONE);
            } else {
                holder.categoryIndicator.setVisibility(View.VISIBLE);
            }*/
            if (!currentExam.isCreating())
                holder.choices.setOnClickListener(view -> Toast.makeText(context, "⚠️ امکان تغییر گزینه وجود ندارد!", Toast.LENGTH_SHORT).show());
            holder.isQuestionAnsweredCorrectly.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (currentExam.isUsedCorrection()) {
                    if (isChecked) {
                        vibrateDevice(new long[]{0, 50, 50, 50});
                        for (int i = 0; i < holder.choices.getChildCount(); i++) {
                            View view = holder.choices.getChildAt(i);
                            view.setEnabled(false);
                        }
                    } else {
                        for (int i = 0; i < holder.choices.getChildCount(); i++) {
                            View view = holder.choices.getChildAt(i);
                            view.setEnabled(true);
                        }
                    }
                    questions.get(position).setCorrect(isChecked);
                    setChoiceCorrectChose(isChecked, holder, position);
                }
            });
            if (!currentExam.isUsedCategorize())
                holder.questionNumber.setTextColor(accentColor);
            else {
                holder.choices.setVisibility(View.INVISIBLE);
                holder.categoryIndicator.setVisibility(VISIBLE);
            }
        }
    }

    private void setupQuestionCorrected(QuestionViewHolder holder, int position) {
        if (!currentExam.isStarted() && currentExam.isChecked()) {
            for (int i = 0; i < holder.choices.getChildCount(); i++) {
                holder.choices.getChildAt(i).setEnabled(true);
            }
            if (!currentExam.isUsedCategorize())
                holder.questionNumber.setTextColor(accentColor);
            holder.categoryIndicator.setEnabled(false);
            holder.editCategory.setVisibility(GONE);
            holder.categoryTimeRemainingLayout.setVisibility(GONE);
            holder.questionNumber.setClickable(true);
            showClickBookmarks(holder, position);
            if (questions.get(position).getCategory() == null)
                holder.categoryIndicator.setVisibility(GONE);
            setChoicesBackground(holder, position);
            disableChoicesGroup(holder);
            if (questions.get(position).getTimeOfThinking() != 0) {
                holder.timeOfThinkingLayout.setVisibility(View.VISIBLE);
                holder.timeOfThinking.setText(holder.itemView.getContext().getString(R.string.time_of_thinking, questions.get(position).getTimeOfThinking()));
            } else {
                holder.timeOfThinkingLayout.setVisibility(GONE);
            }
            if (!currentExam.isCreating())
                holder.choices.setOnClickListener(view -> Toast.makeText(context, "⚠️ امکان تغییر گزینه وجود ندارد!", Toast.LENGTH_SHORT).show());
            if (currentExam.isUsedCorrection()) {
                holder.isQuestionAnsweredCorrectly.setVisibility(GONE);
                categories = Saver.getInstance(context).loadQuestions().getCategories();
                if (questions.get(position).getCategory() != null && categories.size() >= 1 && currentExam.isCanCalculateScoreOfCategory()) {
                    for (Category category : categories) {
                        if (questions.get(position).getCategory().getTitle().equals(category.getTitle()) && holder.categoryTitle.getVisibility() == View.VISIBLE) {
                            holder.categoryScore.setSelected(true);
                            holder.categoryScore.setVisibility(View.VISIBLE);
                            questions.get(position).getCategory().setScore(category.getScore());
                            holder.categoryScore.setTextColor(category.getColor().getAccentColor());
                            holder.categoryScore.setText(holder.itemView.getContext().getString(R.string.category_score_ui, (category.getScore() >= 0) ? context.getString(R.string.score_not_minus_ui, category.getScore()) : context.getString(R.string.score_minus_ui, category.getScore() * -1), ""));
                            updateQuestionsList(position);
                        }
                    }
                } else {
                    holder.categoryScore.setVisibility(GONE);
                    Log.d("TAG", "Sfse category " + Arrays.toString(categories.toArray()));
                }
                //setChoiceCorrectChose(questions.get(position).isCorrect(), holder, position);
            } else holder.isQuestionAnsweredCorrectly.setVisibility(GONE);
            calculateAveTimeOfThinking(holder, position);
        }
    }

    private void setupQuestionCorrection(QuestionViewHolder holder, int position) {
        if (currentExam.isUsedCorrection() && !currentExam.isUsedCorrectionByCorrectAnswers() && !currentExam.isStarted()) {
            holder.isQuestionAnsweredCorrectly.setChecked(questions.get(position).isCorrect());
            if (!questions.get(position).isCorrect() && !questions.get(position).isWhite()) {
                for (int i = 0; i < holder.choices.getChildCount(); i++) {
                    if (i != getSelectedChoiceIndex(position)) {
                        View view = holder.choices.getChildAt(i);
                        view.setClickable(true);
                    }
                }
            } else {
                for (int i = 0; i < holder.choices.getChildCount(); i++) {
                    holder.choices.getChildAt(i).setClickable(false);
                }
            }
            setChoiceCorrectChose(questions.get(position).isCorrect(), holder, position);
        }
    }

    private void onQuestionChoiceSelected(QuestionViewHolder holder, int position) {
        for (int childIndex = 0; childIndex < holder.choices.getChildCount(); childIndex++) {
            holder.choices.getChildAt(childIndex).setOnClickListener(view -> {
                int selectedChoice = getSelectedChoiceNumber(view.getId());
                if (questions.get(position).getSelectedChoice() != selectedChoice) {
                    questions.get(position).setSelectedChoice(selectedChoice);
                    questions.get(position).setWhite(false);
                    questions.get(position).setNowSelected(true);
                    submitNewChoice(holder, position, selectedChoice, view);
                    vibrateDevice(50);
                } else {
                    clearCheck(holder.choices);
                    questions.get(position).setTimeOfThinking(0);
                    //currentExam.setSecondsOfThinkingOnQuestion(0);
                    questions.get(position).setSelectedChoice(0);
                    questions.get(position).setWhite(true);
                    updateQuestionsList(position);
                    questionClickedListener.onQuestionAnswerDeleted(questions.get(position));
                    vibrateDevice(35);
                }
            });
        }
    }

    private void vibrateDevice(long[] pattern) {
        if (Saver.getInstance(context).getVibrationEffects()) {
            if (vibrator.hasVibrator()) {
                vibrator.vibrate(pattern, -1);
            }
        }
    }

    private void vibrateDevice(long millis) {
        if (Saver.getInstance(context).getVibrationEffects()) {
            if (vibrator.hasVibrator()) {
                vibrator.vibrate(millis);
            }
        }
    }

    @SuppressLint("NonConstantResourceId")
    private int getSelectedChoiceNumber(int checkedId) {
        int choiceNumber = 0;
        switch (checkedId) {
            case R.id.choice_1: {
                choiceNumber = 1;
                break;
            }
            case R.id.choice_2: {
                choiceNumber = 2;
                break;
            }
            case R.id.choice_3: {
                choiceNumber = 3;
                break;
            }
            case R.id.choice_4: {
                choiceNumber = 4;
                break;
            }
            default:
                break;
        }
        return choiceNumber;
    }

    private void sendAddingCategoryBroadcast(String status) {
        Intent intent = new Intent(MainActivity.CATEGORY_ADDING_RECEIVER_INTENT);
        intent.putExtra(MainActivity.CATEGORY_ADDING_RECEIVER_RESULT, status);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    private void calculateAveTimeOfThinking(QuestionViewHolder holder, int position) {
        categories = Saver.getInstance(holder.itemView.getContext()).loadQuestions().getCategories();
        if (questions.get(position).getCategory() != null && categories.size() >= 1) {
            for (Category category : categories) {
                if (questions.get(position).getCategory().getTitle().equals(category.getTitle()) && holder.categoryTitle.getVisibility() == View.VISIBLE) {
                    double averageOfTimesOfThinking = 0;
                    int numbers = 0;
                    for (Question q : questions) {
                        if (q.getCategory() != null) {
                            if (q.getCategory().getTitle().equals(category.getTitle())) {
                                if (q.getTimeOfThinking() != 0) {
                                    averageOfTimesOfThinking += q.getTimeOfThinking();
                                    numbers++;
                                }
                                Log.d("TAG", "gsgsrg " + averageOfTimesOfThinking);

                            }
                        }
                    }
                    if (averageOfTimesOfThinking != 0 && numbers != 0) {
                        averageOfTimesOfThinking /= numbers;
                        averageOfTimesOfThinking = Math.round(averageOfTimesOfThinking);
                        holder.mTimeOfThinkingLayout.setVisibility(View.VISIBLE);
                        holder.mTimeOfThinking.setText(holder.itemView.getContext().getString(R.string.time_of_thinking_m, (int) averageOfTimesOfThinking));
                        category.setSecondsOfThinkingOnQuestion((int) averageOfTimesOfThinking);
                        updateQuestionsList(position);
                    } else {
                        holder.mTimeOfThinkingLayout.setVisibility(View.GONE);
                    }
                }
            }
        } else {
            holder.mTimeOfThinkingLayout.setVisibility(GONE);
        }
    }

    private void setChoicesBackground(QuestionViewHolder holder, int position) {
        if (currentExam.isUsedCorrection() && currentExam.isUsedCorrectionByCorrectAnswers() && !currentExam.isStarted() && getCorrectAnswerChoiceIndex(position) != 4) {
            clearChoicesBackground(holder.choices, position);
            for (int childIndex = 0; childIndex < holder.choices.getChildCount(); childIndex++) {
                if (childIndex == getCorrectAnswerChoiceIndex(position)) {
                    if (getCorrectAnswerChoiceIndex(position) == getSelectedChoiceIndex(position)) {
                        setChoiceCorrectChose(true, holder, position);
                    } else {
                        setChoiceCorrectChose(false, holder, position);
                        submitNewCorrectChoice(holder, position, holder.choices.getChildAt(childIndex));
                    }
                }
            }
        } else {
            clearChoicesBackground(holder.choices, position);
            if (!questions.get(position).isWhite() && currentExam.isUsedCorrection() && !questions.get(position).isCorrect()) {
                for (int i = 0; i < holder.choices.getChildCount(); i++) {
                    if (i != getSelectedChoiceIndex(position)) {
                        View view = holder.choices.getChildAt(i);
                        if (i == getCorrectAnswerChoiceIndex(position)) {
                            submitNewCorrectChoice(holder, position, view);
                        }
                    }
                }
            }
        }
    }

    private void submitNewCorrectChoice(QuestionViewHolder holder, int position, View view) {
        submitNewChoice(holder, position, view);
        if (currentExam.isShowCorrectsWrongsWithColor() && currentExam.isChecked()) {
            view.setBackground(context.getResources().getDrawable(R.drawable.btn_correct_material));
            ((Button) view).setTextColor(context.getResources().getColor(R.color.elements_color_tint_rev));
        } else {
            view.setBackground(context.getResources().getDrawable(R.drawable.btn_correct_answer_material));
            ((Button) view).setTextColor(context.getResources().getColor(R.color.edu_level_very_good));
        }
        updateQuestionsList(position);
    }

    private void disableChoicesGroup(QuestionViewHolder holder) {
        holder.choices.setEnabled(false);
        for (int i = 0; i < holder.choices.getChildCount(); i++) {
            holder.choices.getChildAt(i).setClickable(false);
        }
    }

    private void enableDisableChoicesGroup(QuestionViewHolder holder, boolean enableDisable) {
        holder.choices.setEnabled(enableDisable);
        for (int i = 0; i < holder.choices.getChildCount(); i++) {
            holder.choices.getChildAt(i).setEnabled(enableDisable);
        }
    }

    private void setChoiceCorrectChose(boolean isChecked, QuestionViewHolder holder, int position) {
        Log.i("ir.saltech.", "BUTTONPosition: " + getSelectedChoiceIndex(position));
        clearChoicesBackground(holder.choices, position);
        if (getSelectedChoiceIndex(position) != 4) {
            Button selectedChoice = ((Button) holder.choices.getChildAt(getSelectedChoiceIndex(position)));
            if (selectedChoice != null) {
                if (currentExam.isShowCorrectsWrongsWithColor() && currentExam.isChecked()) {
                    if (isChecked) {
                        selectedChoice.setBackground(context.getResources().getDrawable(R.drawable.btn_correct_material));
                        selectedChoice.setTextColor(context.getResources().getColor(android.R.color.background_light));
                    } else {
                        selectedChoice.setBackground(context.getResources().getDrawable(R.drawable.btn_wrong_material));
                        selectedChoice.setTextColor(context.getResources().getColor(android.R.color.background_light));
                    }
                } else {
                    if (isChecked) {
                        selectedChoice.setBackground(context.getResources().getDrawable(R.drawable.btn_correct_material));
                        selectedChoice.setTextColor(context.getResources().getColor(android.R.color.background_light));
                    } else {
                        selectedChoice.setBackground(context.getResources().getDrawable(R.drawable.btn_filled_material));
                        selectedChoice.setTextColor(context.getResources().getColor(R.color.elements_color_tint_rev));
                    }
                }
            }
            updateQuestionsList(position);
        }
    }

    private void loadQuestionSelectedChoice(QuestionViewHolder holder, int position) {
        enableDisableChoicesGroup(holder, currentExam != null);
        if (questions.get(position).getSelectedChoice() != 0 && !isCorrectingByCorrectAnswer) {
            for (int childIndex = 0; childIndex < holder.choices.getChildCount(); childIndex++) {
                if (currentExam != null) {
                    if (!currentExam.isStarted()) {
                        clearCheck(holder.choices);
                        clearChoicesBackground(holder.choices, position);
                    }
                } else {
                    clearCheck(holder.choices);
                    clearChoicesBackground(holder.choices, position);
                }
                if (childIndex == getSelectedChoiceIndex(position)) {
                    submitNewChoice(holder, position, getSelectedChoiceIndex(position), holder.choices.getChildAt(childIndex));
                    //holder.deleteSelectedChoice.setAlpha(1f);
                }
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void submitNewChoice(QuestionViewHolder holder, int position, View view) {
        clearChoicesBackground(holder.choices, position);
        view.setElevation(3);
        view.setBackground(context.getResources().getDrawable(R.drawable.btn_filled_material));
        ((Button) view).setTextColor(context.getResources().getColor(R.color.elements_color_tint_rev));
        updateQuestionsList(position);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void clearChoicesBackground(LinearLayout parent, int position) {
        parent.setFocusable(true);
        for (int choiceIndex = 0; choiceIndex < parent.getChildCount(); choiceIndex++) {
            if (choiceIndex != getSelectedChoiceIndex(position)) {
                parent.getChildAt(choiceIndex).setBackground(null);
                ((Button) parent.getChildAt(choiceIndex)).setTextColor(context.getResources().getColor(R.color.elements_color_tint));
                parent.getChildAt(choiceIndex).setElevation(2.2f);
            } else {
                boolean isChecked = questions.get(position).isCorrect();
                Button selectedChoice = (Button) parent.getChildAt(choiceIndex);
                if (currentExam.isShowCorrectsWrongsWithColor() && currentExam.isChecked()) {
                    if (isChecked) {
                        selectedChoice.setBackground(context.getResources().getDrawable(R.drawable.btn_correct_material));
                        selectedChoice.setTextColor(context.getResources().getColor(android.R.color.background_light));
                    } else {
                        selectedChoice.setBackground(context.getResources().getDrawable(R.drawable.btn_wrong_material));
                        selectedChoice.setTextColor(context.getResources().getColor(android.R.color.background_light));
                    }
                } else {
                    if (isChecked) {
                        selectedChoice.setBackground(context.getResources().getDrawable(R.drawable.btn_correct_material));
                        selectedChoice.setTextColor(context.getResources().getColor(android.R.color.background_light));
                    } else {
                        selectedChoice.setBackground(context.getResources().getDrawable(R.drawable.btn_filled_material));
                        selectedChoice.setTextColor(context.getResources().getColor(R.color.elements_color_tint_rev));
                    }
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    selectedChoice.setElevation(3f);
                }
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void submitNewChoice(QuestionViewHolder holder, int position, int selectedChoice, View view) {
        clearCheck(holder.choices);
        view.setElevation(3);
        view.setBackground(context.getResources().getDrawable(R.drawable.btn_filled_material));
        ((Button) view).setTextColor(context.getResources().getColor(R.color.elements_color_tint_rev));
        if (selectedChoice != 0) {
            questions.get(position).setWhite(false);
        } else {
            questions.get(position).setNowSelected(false);
        }
        if (!currentExam.isStarted()) {
            clearChoicesBackground(holder.choices, position);
        }
        Log.v("TAG", "Time Of Thinking on the question: " + currentExam.getSecondsOfThinkingOnQuestion() + " Qn: " + questions.get(position).getQuestionNumber());
        if (currentExam.isUsedChronometer()) {
            if (questions.get(position).getTimeOfThinking() == 0 || (questions.get(position).getTimeOfThinking() != 0 && currentExam.getSecondsOfThinkingOnQuestion() > 5)) {
                questions.get(position).setTimeOfThinking(currentExam.getSecondsOfThinkingOnQuestion());
                currentExam.setSecondsOfThinkingOnQuestion(0);
                updateRecentExams();
            }
        }
        updateQuestionsList(position);
        questionClickedListener.onQuestionAnswered(questions.get(position));
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void clearCheck(LinearLayout parent) {
        for (int childIndex = 0; childIndex < parent.getChildCount(); childIndex++) {
            parent.getChildAt(childIndex).setBackground(context.getResources().getDrawable(R.drawable.btn_default_material));
            ((Button) parent.getChildAt(childIndex)).setTextColor(context.getResources().getColor(R.color.elements_color_tint));
        }
    }

    private int getSelectedChoiceIndex(int position) {
        return questions.get(position).getSelectedChoice() - 1;
    }

    private int getCorrectAnswerChoiceIndex(int position) {
        return questions.get(position).getCorrectAnswerChoice() - 1;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void loadCurrentBookmark(QuestionViewHolder holder, int position) {
        if (questions.get(position).getBookmark().getName().equals(Bookmark.NONE)) {
            setQuestionDefaults(holder, position);
        } else {
            holder.questionNumber.setVisibility(VISIBLE);
            holder.questionNumber.setBackground(context.getDrawable(R.drawable.tiny_button_bg));
            holder.questionNumber.getBackground().setColorFilter(questions.get(position).getBookmark().getPinColor().getColor(), SRC_IN);
            holder.questionNumber.setTextColor(questions.get(position).getBookmark().getPinColor().getColor());
            questionClickedListener.onQuestionBookmarkChanged(questions.get(position));
            holder.questionNumber.setClickable(true);
        }
    }

    private void setupCategoryOption(QuestionViewHolder holder, int position) {
        // TODO: Setup this...
       /* if (currentExam.isUsedCategorize()) {
            enableDisableChoicesGroup(holder, !(currentExam.isStarted() && currentExam.isCanCalculateTimeForCategory() && currentExam.isEditingCategoryTimes() && !currentExam.isCreating()));
            holder.categoryIndicator.setOnClickListener(view -> {
                if (questions.get(position).getCategory() == null) {
                    sendAddingCategoryBroadcast(MainActivity.FREEZE_TIME);
                    MaterialFragmentShower shower = new MaterialFragmentShower(context);
                    shower.setFragment(new InsertQCategoryDialog(shower, new CategoryInsertedListener() {
                        @Override
                        public void onCategoryAdded(Category insertedCategory) {
                            categories.add(insertedCategory);
                            for (int i = insertedCategory.getQuestionsRange()[0]; i <= insertedCategory.getQuestionsRange()[1]; i++) {
                                questions.get(i).setCategory(insertedCategory);
                            }
                            Log.v("TAG", "categories " + Arrays.toString(categories.toArray()));
                            Questions questionsE = new Questions();
                            questionsE.setQuestions(questions);
                            questionsE.setCategories(categories);
                            currentExam.setSecondsOfThinkingOnQuestion(0);
                            updateRecentExams();
                            Saver.getInstance(holder.itemView.getContext()).saveQuestions(questionsE);
                            updateQuestionsList(position);
                            QuestionsViewAdapter.this.notifyDataSetChanged();
                            sendAddingCategoryBroadcast(MainActivity.CONTINUE_TIME);
                        }

                        @Override
                        public void onCategoryAddingCanceled() {
                            sendAddingCategoryBroadcast(MainActivity.CATEGORY_ADDING_STATUS_CANCELED);
                        }
                    }, position, getItemCount(), categories, questions, currentExam));
                    shower.show((FragmentActivity) activity, shower);
                }
            });
            holder.editCategory.setOnClickListener(view -> {
                sendAddingCategoryBroadcast(MainActivity.FREEZE_TIME);
                Category currentCategory = questions.get(position).getCategory();
                MaterialFragmentShower shower = new MaterialFragmentShower(context);
                EditCategoryDialog categoryDialog = new EditCategoryDialog(shower, currentCategory, currentExam.isStarted() && currentExam.isCanCalculateTimeForCategory() && currentExam.isEditingCategoryTimes() && !currentExam.isCreating());
                categoryDialog.setOnCategoryChoiceSelectedListener(work -> {
                    switch (work) {
                        case EditCategoryChoiceSelectedListener.WORK_RENAME_CATEGORY: {
                            Saver.getInstance(context).setDismissSide(MaterialDialogFragment.SIDE_FRAGMENT_SHOWER);
                            RenameCategoryDialog dialog = new RenameCategoryDialog(shower, new CategoryChangedListener() {
                                @Override
                                public void onCategoryRenamed(String newCategoryName) {
                                    currentCategory.setTitle(newCategoryName);
                                    updateCategory(currentCategory);
                                    try {
                                        categoryDialog.dialogTitle.setText(context.getString(R.string.edit_category_title, newCategoryName));
                                    } catch (NullPointerException npe) {
                                        npe.printStackTrace();
                                    }
                                }

                                @Override
                                public void onCategoryElementsDeleted(Category category, List<Question> questions) {
                                }

                                @Override
                                public void onCategoryElementsAdded(Category category, List<Question> questions) {
                                }

                                @Override
                                public void onCategoryTimeEdited(long newCategoryTime) {
                                }
                            }, currentCategory, categories);
                            shower.setHasContent(true);
                            shower.setContentFragment((FragmentActivity) activity, dialog, categoryDialog);
                            break;
                        }
                        case EditCategoryChoiceSelectedListener.WORK_EDIT_TIME_CATEGORY: {
                            Saver.getInstance(context).setDismissSide(MaterialDialogFragment.SIDE_FRAGMENT_SHOWER);
                            EditCategoryTimeDialog dialog = new EditCategoryTimeDialog(shower, new CategoryChangedListener() {
                                @Override
                                public void onCategoryRenamed(String newCategoryName) {
                                }

                                @Override
                                public void onCategoryElementsDeleted(Category category, List<Question> questions) {
                                }

                                @Override
                                public void onCategoryElementsAdded(Category category, List<Question> questions) {
                                }

                                @Override
                                public void onCategoryTimeEdited(long newCategoryTime) {
                                    if (newCategoryTime != 0) {
                                        currentCategory.setTime(newCategoryTime);
                                        updateCategory(currentCategory);
                                    }
                                }
                            }, currentCategory, currentExam, categories);
                            shower.setHasContent(true);
                            shower.setContentFragment((FragmentActivity) activity, dialog, categoryDialog);
                            break;
                        }
                        case EditCategoryChoiceSelectedListener.WORK_EDIT_COLOR_CATEGORY: {
                            MaterialAlertDialog builder = new MaterialAlertDialog(context);
                            builder.setIcon(R.drawable.reformat_category_color);
                            builder.setTitle("تولید رنگ جدید برای دسته " + currentCategory.getTitle());
                            builder.setMessage("آیا موافق هستید که رنگ جدیدی برای این دسته تولید کنیم؟");
                            builder.setPositiveButton("بله", v4 -> {
                                currentCategory.setCategoryColor(generateCategoryColor());
                                updateCategory(currentCategory);
                                Toast.makeText(context, "رنگ این دسته با موفقیت تغییر یافت!", Toast.LENGTH_SHORT).show();
                                builder.dismiss(builder);
                            });
                            builder.setNegativeButton("خیر", v4 -> builder.dismiss(builder));
                            builder.show((FragmentActivity) activity);
                            break;
                        }
                        case EditCategoryChoiceSelectedListener.WORK_CHILD_ELEMENTS_DELETE_CATEGORY: {
                            Saver.getInstance(context).setDismissSide(MaterialDialogFragment.SIDE_FRAGMENT_SHOWER);
                            RemoveCategoryElementsDialog dialog1 = new RemoveCategoryElementsDialog(shower, questions, currentCategory, new CategoryChangedListener() {
                                @Override
                                public void onCategoryRenamed(String newCategoryName) {
                                }

                                @Override
                                public void onCategoryElementsDeleted(Category category, List<Question> questionsN) {
                                    for (Category c : categories) {
                                        if (c.getId() == category.getId()) {
                                            c.setQuestionsRange(category.getQuestionsRange());
                                        }
                                    }
                                    for (int i = 0; i < questionsN.size(); i++) {
                                        updateQuestionsList(i);
                                    }
                                    Questions questionsE = new Questions();
                                    questionsE.setQuestions(questions);
                                    questionsE.setCategories(categories);
                                    Saver.getInstance(context).saveQuestions(questionsE);
                                    currentExam.setAnswerSheet(questionsE);
                                    updateRecentExams();
                                    notifyDataSetChanged();
                                }

                                @Override
                                public void onCategoryElementsAdded(Category category, List<Question> questions) {
                                }

                                @Override
                                public void onCategoryTimeEdited(long newCategoryTime) {
                                }
                            });
                            shower.setHasContent(true);
                            shower.setContentFragment((FragmentActivity) activity, dialog1, categoryDialog);
                            break;
                        }
                        case EditCategoryChoiceSelectedListener.WORK_CHILD_ELEMENTS_ADD_CATEGORY: {
                            Saver.getInstance(context).setDismissSide(MaterialDialogFragment.SIDE_FRAGMENT_SHOWER);
                            AddCategoryElementsDialog dialog2 = new AddCategoryElementsDialog(shower, questions, currentCategory, new CategoryChangedListener() {
                                @Override
                                public void onCategoryRenamed(String newCategoryName) {
                                }

                                @Override
                                public void onCategoryElementsDeleted(Category category, List<Question> questionsN) {
                                }

                                @Override
                                public void onCategoryElementsAdded(Category category, List<Question> questionsN) {
                                    for (Category c : categories) {
                                        if (c.getId() == category.getId()) {
                                            c.setQuestionsRange(category.getQuestionsRange());
                                        }
                                    }
                                    for (int i = 0; i < questionsN.size(); i++) {
                                        updateQuestionsList(i);
                                    }
                                    Questions questionsE = new Questions();
                                    questionsE.setQuestions(questions);
                                    questionsE.setCategories(categories);
                                    Saver.getInstance(context).saveQuestions(questionsE);
                                    currentExam.setAnswerSheet(questionsE);
                                    updateRecentExams();
                                    notifyDataSetChanged();
                                }

                                @Override
                                public void onCategoryTimeEdited(long newCategoryTime) {
                                }
                            });
                            shower.setHasContent(true);
                            shower.setContentFragment((FragmentActivity) activity, dialog2, categoryDialog);
                            break;
                        }
                        case EditCategoryChoiceSelectedListener.WORK_DELETE_CATEGORY: {
                            MaterialAlertDialog builder = new MaterialAlertDialog(context);
                            builder.setIcon(R.drawable.delete);
                            builder.setTitle("حذف دسته " + currentCategory.getTitle());
                            builder.setMessage("آیا از حذف کامل این دسته اطمینان دارید؟\n\nاین عملیات، غیر قابل بازگشت خواهد بود!");
                            builder.setNegativeButton("بله", v4 -> {
                                removeCategory(currentCategory);
                                Toast.makeText(context, "دسته «" + currentCategory.getTitle() + "» موفقیت حذف شد!", Toast.LENGTH_SHORT).show();
                                builder.dismiss(builder);
                            });
                            builder.setPositiveButton("خیر", v4 -> builder.dismiss(builder));
                            builder.show((FragmentActivity) activity);
                            break;
                        }
                        default:
                            break;
                    }
                });
                shower.setFragment(categoryDialog);
                shower.setOnDismissListener(() -> sendAddingCategoryBroadcast(MainActivity.CONTINUE_TIME));
                shower.show((FragmentActivity) activity, shower);
            });
            holder.categoryIndicator.setVisibility(View.VISIBLE);
            if (questions.get(position).getCategory() != null) {
                Log.d("TAG", "CATEGORY Q RANGE: FROM " + questions.get(position).getCategory().getQuestionsRange()[0] + " TO " + questions.get(position).getCategory().getQuestionsRange()[1] + ", POSITION: " + position);
                if (position == questions.get(position).getCategory().getQuestionsRange()[0]) {
                    if (currentExam.isCanCalculateScoreOfCategory() && currentExam.isChecked() && questions.get(position).getCategory().getScore() != 0) {
                        holder.categoryScore.setSelected(true);
                        holder.categoryScore.setVisibility(View.VISIBLE);
                        holder.categoryScore.setTextColor(questions.get(position).getCategory().getColor().getAccentColor());
                        holder.categoryScore.setText(holder.itemView.getContext().getString(R.string.category_score_ui, (questions.get(position).getCategory().getScore() >= 0) ? context.getString(R.string.score_not_minus_ui, questions.get(position).getCategory().getScore()) : context.getString(R.string.score_minus_ui, questions.get(position).getCategory().getScore() * -1), checkUserEducationalStatus(questions.get(position).getCategory().getScore())));
                    } else {
                        holder.categoryScore.setVisibility(GONE);
                    }
                    if (currentExam.isCanCalculateTimeForCategory() && questions.get(position).getCategory().getTime() != 0) {
                        holder.categoryTimeRemainingLayout.setVisibility(VISIBLE);
                        long examTimeLi = questions.get(position).getCategory().getTime();
                        if (examTimeLi >= 60_000)
                            holder.categoryTimeRemaining.setText(printTime((int) ((((double) examTimeLi) % 60_000) / 1000), (int) (((double) examTimeLi) / 60_000)));
                        else
                            holder.categoryTimeRemaining.setText(printTime((int) (((double) examTimeLi) / 1000), 0));
                        if (examTimeLi <= 10000) {
                            holder.categoryTimeRemaining.setTextColor(context.getResources().getColor(R.color.edu_level_bad));
                            holder.categoryTimeRemainingImage.getDrawable().setColorFilter(context.getResources().getColor(R.color.edu_level_bad), SRC_IN);
                        } else {
                            holder.categoryTimeRemaining.setTextColor(context.getResources().getColor(R.color.elements_color_tint));
                            holder.categoryTimeRemainingImage.getDrawable().setColorFilter(context.getResources().getColor(R.color.elements_color_tint), SRC_IN);
                        }
                    } else {
                        holder.categoryTimeRemainingLayout.setVisibility(GONE);
                        holder.editCategory.setVisibility(View.INVISIBLE);
                    }
                    holder.categoryTitle.setSelected(true);
                    holder.categoryTitle.setVisibility(View.VISIBLE);
                    holder.categoryTitle.setTextColor(questions.get(position).getCategory().getColor().getAccentColor());
                    holder.categoryTitle.setText(questions.get(position).getCategory().getTitle());
                    holder.categoryTitle.setOnClickListener(view -> questionClickedListener.onQuestionCategoryClicked());
                    holder.editCategory.setVisibility(View.VISIBLE);
                } else {
                    holder.categoryTimeRemainingLayout.setVisibility(GONE);
                    holder.editCategory.setVisibility(GONE);
                    holder.categoryTitle.setVisibility(GONE);
                }
                holder.editCategory.getDrawable().setColorFilter(questions.get(position).getCategory().getColor().getEditButtonColor(), SRC_IN);
                holder.categoryIndicator.getDrawable().setColorFilter(questions.get(position).getCategory().getColor().getAccentColor(), SRC_IN);
                holder.questionLayout.getDrawable().setColorFilter(questions.get(position).getCategory().getColor().getBackgroundColor(), SRC_IN);
                holder.questionNumber.setTextColor(questions.get(position).getCategory().getColor().getAccentColor());
            } else {
                holder.questionLayout.getDrawable().setColorFilter(context.getResources().getColor(R.color.background_color), SRC_IN);
                holder.questionNumber.setTextColor(context.getResources().getColor(R.color.add_category_button));
                //holder.categoryIndicator.getDrawable().setColorFilter(context.getResources().getColor(R.color.add_category_button), SRC_IN);
                holder.editCategory.setVisibility(GONE);
                holder.categoryTitle.setVisibility(GONE);
                holder.categoryTimeRemainingLayout.setVisibility(GONE);
            }
        } else {
            holder.categoryIndicator.setVisibility(GONE);
            //holder.questionLayout.getDrawable().setColorFilter(context.getResources().getColor(R.color.background_color), SRC_IN);
        }
        */
    }

    private void setQuestionSelected(QuestionViewHolder holder, int position) {
        if (questions.get(position).isSelected()) {
            int selectedQuestionColor = Color.argb(60, 255, 193, 7);
            int selectedQuestionColorNonAlpha = Color.argb(0, 255, 193, 7);
            @SuppressLint("Recycle") ValueAnimator valueAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), selectedQuestionColorNonAlpha, selectedQuestionColor);
            valueAnimator.addUpdateListener(valueAnimator1 -> holder.questionLayout.setOverlayColor((int) valueAnimator1.getAnimatedValue()));
            valueAnimator.setDuration(250);
            valueAnimator.setStartDelay(100);
            valueAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    @SuppressLint("Recycle") ValueAnimator valueAnimator2 = ValueAnimator.ofObject(new ArgbEvaluator(), selectedQuestionColor, selectedQuestionColorNonAlpha);
                    valueAnimator2.addUpdateListener(valueAnimator1 -> holder.questionLayout.setOverlayColor((int) valueAnimator1.getAnimatedValue()));
                    valueAnimator2.setDuration(250);
                    valueAnimator2.setStartDelay(150);
                    valueAnimator2.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {
                        }

                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void onAnimationEnd(Animator animator) {
                            questions.get(position).setSelected(false);
                            updateQuestionsList(position);
                            notifyDataSetChanged();
                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {
                        }
                    });
                    valueAnimator2.start();
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                }
            });
            valueAnimator.start();
        }
    }

    private void updateQuestionsList(int position) {
        Question q = questions.get(position);
        questions.remove(position);
        questions.add(position, q);
        Questions questionsE = new Questions();
        questionsE.setQuestions(questions);
        questionsE.setCategories(categories);
        Saver.getInstance(context).saveQuestions(questionsE);
        currentExam.setAnswerSheet(questionsE);
        updateRecentExams();
        Log.v("TAG", "com.saltechgroup. " + questions.get(position));
        Log.v("TAG", "com.saltechgroup. " + questions);
    }

    private void updateRecentExams() {
        Exams recentExams = Saver.getInstance(context).loadRecentExams();
        recentExams.updateCurrentExam(currentExam);
        Saver.getInstance(context).saveRecentExams(recentExams);
    }

    private void setQuestionDefaults(@NonNull QuestionViewHolder holder, int position) {
        setBlurView(activity, holder.questionLayout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            holder.isQuestionAnsweredCorrectly.getButtonDrawable().setColorFilter(accentColor, SRC_IN);
        }
        holder.choices.setVisibility(VISIBLE);
        holder.questionNumber.setSelected(true);
        holder.questionNumber.setTextColor(accentColor);
        holder.questionNumber.setBackground(null);
        holder.questionNumber.setText(context.getString(R.string.num, questions.get(position).getQuestionNumber()));
        //prepareQuestionForGridLayout(holder, position);
    }

    @Deprecated
    @SuppressLint("ClickableViewAccessibility")
    private void prepareQuestionForGridLayout(@NonNull QuestionViewHolder holder, int position) {
        holder.choices.setVisibility(GONE);
        try {
            GridLayoutManager.LayoutParams params = (GridLayoutManager.LayoutParams) holder.questionLayout.getLayoutParams();
            int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, context.getResources().getDisplayMetrics());
            params.leftMargin = margin;
            params.rightMargin = margin;
            holder.questionLayout.setLayoutParams(params);
        } catch (ClassCastException e) {
            e.printStackTrace();
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.questionLayout.getLayoutParams();
            int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, context.getResources().getDisplayMetrics());
            params.leftMargin = margin;
            params.rightMargin = margin;
            holder.questionLayout.setLayoutParams(params);
        }
        holder.questionNumber.setOnTouchListener((v, e) -> {
            if (e.getAction() == MotionEvent.ACTION_DOWN) {
                questionNumberClicked = true;
            } else if (e.getAction() == MotionEvent.ACTION_UP && questionNumberClicked) {
                questionClickedListener.onQuestionClicked(questions.get(position), e.getRawX(), e.getRawY());
                questionNumberClicked = false;
            }
            return true;
        });
    }

    private void prepareQuestionForLinearLayout(@NonNull QuestionViewHolder holder, int position) {
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class QuestionViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout parentLayout;
        public BlurView questionLayout;
        public LinearLayout categoryHeaderLayout;
        public TextView questionNumber;
        public LinearLayout choices;
        public LinearLayout timeOfThinkingLayout;
        public TextView categoryTitle;
        public TextView categoryScore;
        public TextView timeOfThinking;
        public LinearLayout mTimeOfThinkingLayout;
        public TextView mTimeOfThinking;
        public CheckBox isQuestionAnsweredCorrectly;
        public Button categoryIndicator;
        public View disableQuestion;
        public ImageButton editCategory;
        public TextView categoryTimeRemaining;
        public LinearLayout categoryTimeRemainingLayout;
        private ImageView categoryTimeRemainingImage;

        public QuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            init(itemView);
        }

        private void init(@NonNull View v) {

            questionLayout = v.findViewById(R.id.question_parent_layout);
            questionNumber = v.findViewById(R.id.question_number);
            choices = v.findViewById(R.id.choices_group);
            timeOfThinkingLayout = v.findViewById(R.id.time_of_thinking_layout);
            mTimeOfThinkingLayout = v.findViewById(R.id.time_of_thinking_layout_m);
            categoryIndicator = v.findViewById(R.id.add_new_category);
            categoryScore = v.findViewById(R.id.category_score);
            categoryTitle = v.findViewById(R.id.category_title);
            disableQuestion = v.findViewById(R.id.disable_question);
            timeOfThinking = v.findViewById(R.id.time_of_thinking);
            mTimeOfThinking = v.findViewById(R.id.time_of_thinking_m);
            isQuestionAnsweredCorrectly = v.findViewById(R.id.is_correct_choice);
            editCategory = v.findViewById(R.id.edit_category);
            categoryHeaderLayout = v.findViewById(R.id.category_header_layout);
            parentLayout = v.findViewById(R.id.t_parent_layout);
            categoryTimeRemainingImage = v.findViewById(R.id.time_remaining_image_c);
            categoryTimeRemaining = v.findViewById(R.id.time_remaining_c);
            categoryTimeRemainingLayout = v.findViewById(R.id.time_remaining_layout_c);
        }
    }
}
