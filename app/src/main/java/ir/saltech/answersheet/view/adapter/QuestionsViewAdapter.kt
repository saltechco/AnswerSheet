package ir.saltech.answersheet.view.adapter

import android.animation.Animator
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.os.Vibrator
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yy.mobile.rollingtextview.RollingTextView
import eightbitlab.com.blurview.BlurView
import ir.saltech.answersheet.R
import ir.saltech.answersheet.intf.listener.QuestionClickedListener
import ir.saltech.answersheet.intf.listener.ThingSelectedListener
import ir.saltech.answersheet.`object`.container.Saver
import ir.saltech.answersheet.`object`.data.Bookmark
import ir.saltech.answersheet.`object`.data.Bookmarks
import ir.saltech.answersheet.`object`.data.Category
import ir.saltech.answersheet.`object`.data.CategoryColor
import ir.saltech.answersheet.`object`.data.Exam
import ir.saltech.answersheet.`object`.data.Exams
import ir.saltech.answersheet.`object`.data.Question
import ir.saltech.answersheet.`object`.data.Questions
import ir.saltech.answersheet.`object`.data.Thing
import ir.saltech.answersheet.view.activity.MainActivity
import ir.saltech.answersheet.view.container.BlurViewHolder
import ir.saltech.answersheet.view.container.MaterialAlertDialog
import ir.saltech.answersheet.view.container.MaterialFragmentShower
import ir.saltech.answersheet.view.container.Toast
import ir.saltech.answersheet.view.dialog.SelectThingsDialog
import java.util.Locale
import java.util.Random

class QuestionsViewAdapter(
    questions: MutableList<Question>?,
    accentColor: Int,
    categories: MutableList<Category>?,
    currentExam: Exam?,
    questionClickedListener: QuestionClickedListener,
    activity: Activity
) : RecyclerView.Adapter<QuestionsViewAdapter.QuestionViewHolder?>() {
    private val currentExam: Exam?
    private val questionClickedListener: QuestionClickedListener
    private val random = Random()
    private val activity: Activity
    private val accentColor: Int
    private var categories: MutableList<Category>? = null
    private var questions: MutableList<Question>?
    private var isCorrectingByCorrectAnswer = false
    private var vibrator: Vibrator? = null
    private var context: Context? = null
    private var questionNumberClicked = false

    init {
        this.questions = questions
        this.currentExam = currentExam
        this.accentColor = accentColor
        this.activity = activity
        if (categories == null) this.categories = ArrayList()
        else this.categories = categories
        if (questions == null) this.questions = ArrayList<Question>()
        else this.questions = questions
        this.questionClickedListener = questionClickedListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        context = parent.context
        vibrator = context!!.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        return QuestionViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.view_template_question, parent, false)
        )
    }

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        setQuestionDefaults(holder, position)
        if (currentExam != null) {
            setQuestionSelected(holder, position)
            setupCategoryOption(holder, position)
            loadCurrentBookmark(holder, position)
            loadQuestionSelectedChoice(holder, position)
            onQuestionChoiceSelected(holder, position)
            setupQuestionCorrection(holder, position)
            setupQuestionCorrected(holder, position)
            setupQuestionExamNotStarted(holder, position)
            setupQuestionExamStarted(holder, position)
        }
    }

    private fun setupQuestionExamStarted(holder: QuestionViewHolder, position: Int) {
        if (currentExam!!.isStarted && !currentExam.isCreating) {
            holder.questionNumber!!.isClickable = true
            if (!currentExam.isUsedCategorize) holder.questionNumber!!.setTextColor(accentColor)
            else {
                if (currentExam.isCanCalculateTimeForCategory) {
                    if (questions!![position].category != null) {
                        val categoryPosition =
                            getCategoryPosition(questions!![position].category!!)
                        if (questions!![position].category!!.time >= 1000) {
                            //holder.disableQuestion.setVisibility(GONE);
                            val examTimeLi = categories!![categoryPosition].time
                            if (examTimeLi >= 60000) Log.i(
                                "TAG",
                                "PPPP " + categories!![categoryPosition].title + " " + printTime(
                                    (((examTimeLi.toDouble()) % 60000) / 1000).toInt()
                                        .toLong(),
                                    ((examTimeLi.toDouble()) / 60000).toInt().toLong()
                                )
                            )
                            else Log.i(
                                "TAG",
                                "PPPP " + categories!![categoryPosition].title + " " + printTime(
                                    ((examTimeLi.toDouble()) / 1000).toInt().toLong(), 0
                                )
                            )
                        } else {
                            if (!questions!![position].category!!.isFinished) {
                                Toast.Companion.makeText(
                                    activity,
                                    "زمان شما برای انجام دسته «" + questions!![position].category!!
                                        .title + "» تمام شد.",
                                    Toast.Companion.WARNING_SIGN,
                                    Toast.Companion.LENGTH_LONG
                                ).show()
                                categories!![categoryPosition].isFinished = true
                                updateCategory(categories!![categoryPosition])
                            }
                            clearChoicesBackground(holder.choices, position)
                            enableDisableChoicesGroup(holder, false)
                            holder.editCategory!!.setVisibility(View.GONE)
                        }
                    }
                }
            }
            showClickBookmarks(holder, position)
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun submitThisBookmark(
        holder: QuestionViewHolder,
        position: Int,
        selectedBookmark: Bookmark
    ) {
        holder.questionNumber!!.isClickable = true
        questions!![position].setBookmark(selectedBookmark)
        questions!![position].setBookmark(0)
        if (selectedBookmark.name == Bookmark.Companion.NONE) {
            holder.questionNumber!!.background = null
        } else {
            currentExam!!.secondsOfThinkingOnQuestion = (0)
            holder.questionNumber!!.background = context!!.resources.getDrawable(R.drawable.bookmark_pin)
            holder.questionNumber!!.background
                .setColorFilter(selectedBookmark.pinColor.color, PorterDuff.Mode.SRC_IN)
            vibrateDevice(50)
        }
        updateRecentExams()
        updateQuestionsList(position)
        questionClickedListener.onQuestionBookmarkChanged(questions!![position])
        notifyDataSetChanged()
    }

    private fun generateCategoryColor(): CategoryColor {
        val redColor = random.nextInt(255)
        val greenColor = random.nextInt(255)
        val blueColor = random.nextInt(255)
        val newCColor: CategoryColor = CategoryColor(intArrayOf(redColor, greenColor, blueColor))
        var duplicatedColor = false
        if (categories != null) {
            for (category in categories!!) {
                if (category.color!!.accentColor == newCColor.accentColor) {
                    duplicatedColor = true
                    break
                }
            }
        }
        return if (duplicatedColor) {
            generateCategoryColor()
        } else {
            newCColor
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateCategory(currentCategory: Category) {
        val questionsL: Questions = currentExam!!.answerSheet!!
        categories = questionsL.categories
        for (i in categories!!.indices) {
            if (categories!![i].id == currentCategory.id) {
                categories!!.removeAt(i)
                categories!!.add(i, currentCategory)
                questionsL.categories = (categories!!)
                Saver.getInstance(context!!).saveQuestions(questionsL)
                currentExam.answerSheet = (questionsL)
                updateRecentExams()
                for (j in questions!!.indices) {
                    if (questions!![j].category!! != null) {
                        if (questions!![j].category!!.id == currentCategory.id) {
                            questions!![j].category = (currentCategory)
                            updateQuestionsList(j)
                        }
                    }
                }
                break
            }
        }
        try {
            notifyDataSetChanged()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    private fun showClickBookmarks(holder: QuestionViewHolder, position: Int) {
        holder.questionNumber!!.setOnClickListener(View.OnClickListener { v2: View? ->
            val bookmarks: MutableList<Bookmark> =
                Saver.Companion.getInstance(context!!).loadBookmarks().getBookmarks()
            if (questions!![position].getBookmark().name != Bookmark.Companion.NONE) {
                bookmarks.add(0, Bookmark(Bookmark.Companion.NONE))
            }
            val shower: MaterialFragmentShower = MaterialFragmentShower(context!!)
            shower.fragment = (
                SelectThingsDialog(
                    shower,
                    Bookmarks(bookmarks).things!!,
                    "انتخاب نشانه",
                    false,
                    object : ThingSelectedListener {
                        override fun onSelected(thing: Thing?) {
                        }
                    })
            )
            shower.setCancelable(true)
            shower.show(activity as FragmentActivity, shower)
        })
    }

    private fun convertDpToPx(dp: Int): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            context!!.resources.displayMetrics
        )
    }

    private fun getCategoryPosition(c: Category): Int {
        categories = currentExam!!.answerSheet!!.categories
        var i = 0
        for (j in categories!!.indices) {
            if (categories!![j].title == c.title) {
                i = j
                break
            }
        }
        return i
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

    @SuppressLint("NotifyDataSetChanged")
    private fun setupQuestionExamNotStarted(holder: QuestionViewHolder, position: Int) {
        if (!currentExam!!.isStarted && !currentExam.isChecked) {
            for (i in 0 until holder.choices!!.childCount) {
                holder.choices!!.getChildAt(i).setEnabled(true)
            }

            setChoicesBackground(holder, position)
            //holder.categoryIndicator.setEnabled(false);
            holder.editCategory!!.setVisibility(View.GONE)
            holder.categoryTimeRemainingLayout!!.visibility = View.GONE
            if (!currentExam.isCreating) {
                holder.questionNumber!!.isClickable = true
                showClickBookmarks(holder, position)
            } else {
                if (currentExam.isSelectQuestionsManually) clearCheck(holder.choices)
                holder.questionNumber!!.setOnLongClickListener(View.OnLongClickListener { view: View? ->
                    val builder: MaterialAlertDialog = MaterialAlertDialog(context!!)
                    builder.setIcon(R.drawable.delete)
                    builder.setTitle(
                        "حذف سؤال " + context!!.getString(
                            R.string.num,
                            questions!![position].questionNumber
                        )
                    )
                    builder.setMessage("آیا از حذف این سؤال اطمینان دارید؟")
                    builder.setNegativeButton("بله", View.OnClickListener { v4: View? ->
                        questions!!.removeAt(position)
                        val questionsE: Questions = Questions()
                        questionsE.questions = (questions)
                        questionsE.categories = (categories!!)
                        Saver.getInstance(context!!).saveQuestions(questionsE)
                        currentExam.answerSheet = (questionsE)
                        updateRecentExams()
                        notifyDataSetChanged()
                        questionClickedListener.onQuestionDeleted(position)
                        builder.dismiss(builder)
                    })
                    builder.setPositiveButton(
                        "خیر"
                    ) { v4: View? -> builder.dismiss(builder) }
                    builder.setCancelable(false)
                    builder.show(activity as FragmentActivity)
                    true
                })
            }
            calculateAveTimeOfThinking(holder, position)
            if (questions!![position].timeOfThinking != 0L) {
                holder.timeOfThinkingLayout!!.visibility = View.VISIBLE
                holder.timeOfThinking!!.text = holder.itemView.context.getString(
                    R.string.time_of_thinking,
                    questions!![position].timeOfThinking
                )
            } else {
                holder.timeOfThinkingLayout!!.visibility = View.GONE
            }
            if (currentExam.isUsedCorrection && !currentExam.isCreating) {
                if (currentExam.isUsedCorrectionByCorrectAnswers) {
                    holder.isQuestionAnsweredCorrectly!!.visibility = View.GONE
                    isCorrectingByCorrectAnswer = true
                    for (childIndex in 0 until holder.choices!!.childCount) {
                        holder.choices!!.getChildAt(childIndex)
                            .setOnClickListener(View.OnClickListener { view: View ->
                                val selectedChoice = getSelectedChoiceNumber(view.id)
                                if (questions!![position].correctAnswerChoice != selectedChoice) {
                                    questions!![position].correctAnswerChoice = (selectedChoice)
                                    questions!![position].isCorrect = (
                                        getCorrectAnswerChoiceIndex(
                                            position
                                        ) == getSelectedChoiceIndex(position)
                                    )
                                    updateQuestionsList(position)
                                    clearChoicesBackground(holder.choices, position)
                                    if (getCorrectAnswerChoiceIndex(position) == getSelectedChoiceIndex(
                                            position
                                        )
                                    ) {
                                        setChoiceCorrectChose(true, holder, position)
                                        vibrateDevice(longArrayOf(0, 50, 50, 50))
                                    } else {
                                        setChoiceCorrectChose(false, holder, position)
                                        submitNewCorrectChoice(holder, position, view)
                                    }
                                }
                            })
                    }
                } else {
                    disableChoicesGroup(holder)
                    if (questions!![position].isWhite) {
                        for (i in 0 until holder.choices!!.childCount) {
                            holder.choices!!.getChildAt(i).isClickable = false
                        }
                        holder.isQuestionAnsweredCorrectly!!.visibility = View.GONE
                    } else {
                        for (i in 0 until holder.choices!!.childCount) {
                            if (i != getSelectedChoiceIndex(position) && !holder.isQuestionAnsweredCorrectly!!.isChecked) {
                                holder.choices!!.getChildAt(i).isClickable = true
                                holder.choices!!.getChildAt(i)
                                    .setOnClickListener(View.OnClickListener { view: View ->
                                        val selectedChoice = getSelectedChoiceNumber(view.id)
                                        if (questions!![position].correctAnswerChoice != selectedChoice) {
                                            questions!![position].correctAnswerChoice = (
                                                selectedChoice
                                            )
                                            questions!![position].isCorrect = (
                                                getCorrectAnswerChoiceIndex(position) == getSelectedChoiceIndex(
                                                    position
                                                )
                                            )
                                            updateQuestionsList(position)
                                            clearChoicesBackground(holder.choices, position)
                                            if (getCorrectAnswerChoiceIndex(position) == getSelectedChoiceIndex(
                                                    position
                                                )
                                            ) {
                                                setChoiceCorrectChose(true, holder, position)
                                                vibrateDevice(longArrayOf(0, 50, 50, 50))
                                            } else {
                                                setChoiceCorrectChose(false, holder, position)
                                                submitNewCorrectChoice(holder, position, view)
                                            }
                                        }
                                    })
                            }
                        }
                        holder.isQuestionAnsweredCorrectly!!.visibility = View.VISIBLE
                    }
                }
            } else {
                disableChoicesGroup(holder)
            }
            /*if (questions.get(position).getCategory() == null) {
                holder.categoryIndicator.setVisibility(GONE);
            } else {
                holder.categoryIndicator.setVisibility(View.VISIBLE);
            }*/
            if (!currentExam.isCreating) holder.choices!!.setOnClickListener(View.OnClickListener { view: View? ->
                Toast.Companion.makeText(
                    context!!, "⚠️ امکان تغییر گزینه وجود ندارد!", Toast.Companion.LENGTH_SHORT
                ).show()
            })
            holder.isQuestionAnsweredCorrectly!!.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView: CompoundButton?, isChecked: Boolean ->
                if (currentExam.isUsedCorrection) {
                    if (isChecked) {
                        vibrateDevice(longArrayOf(0, 50, 50, 50))
                        for (i in 0 until holder.choices!!.childCount) {
                            val view: View = holder.choices!!.getChildAt(i)
                            view.isEnabled = false
                        }
                    } else {
                        for (i in 0 until holder.choices!!.childCount) {
                            val view: View = holder.choices!!.getChildAt(i)
                            view.isEnabled = true
                        }
                    }
                    questions!![position].isCorrect = (isChecked)
                    setChoiceCorrectChose(isChecked, holder, position)
                }
            })
            if (!currentExam.isUsedCategorize) holder.questionNumber!!.setTextColor(accentColor)
            else {
                holder.choices!!.visibility = View.INVISIBLE
                holder.categoryIndicator!!.visibility = View.VISIBLE
            }
        }
    }

    private fun setupQuestionCorrected(holder: QuestionViewHolder, position: Int) {
        if (!currentExam!!.isStarted && currentExam.isChecked) {
            for (i in 0 until holder.choices!!.childCount) {
                holder.choices!!.getChildAt(i).setEnabled(true)
            }
            if (!currentExam.isUsedCategorize) holder.questionNumber!!.setTextColor(accentColor)
            holder.categoryIndicator!!.isEnabled = false
            holder.editCategory!!.setVisibility(View.GONE)
            holder.categoryTimeRemainingLayout!!.visibility = View.GONE
            holder.questionNumber!!.isClickable = true
            showClickBookmarks(holder, position)
            if (questions!![position].category == null) holder.categoryIndicator!!.visibility =
                View.GONE
            setChoicesBackground(holder, position)
            disableChoicesGroup(holder)
            if (questions!![position].timeOfThinking != 0L) {
                holder.timeOfThinkingLayout!!.visibility = View.VISIBLE
                holder.timeOfThinking!!.text = holder.itemView.context.getString(
                    R.string.time_of_thinking,
                    questions!![position].timeOfThinking
                )
            } else {
                holder.timeOfThinkingLayout!!.visibility = View.GONE
            }
            if (!currentExam.isCreating) holder.choices!!.setOnClickListener(View.OnClickListener { view: View? ->
                Toast.Companion.makeText(
                    context!!, "⚠️ امکان تغییر گزینه وجود ندارد!", Toast.Companion.LENGTH_SHORT
                ).show()
            })
            if (currentExam.isUsedCorrection) {
                holder.isQuestionAnsweredCorrectly!!.visibility = View.GONE
                categories = Saver.Companion.getInstance(context!!).loadQuestions().categories
                if (questions!![position].category != null && categories!!.size >= 1 && currentExam.isCanCalculateScoreOfCategory) {
                    for (category in categories!!) {
                        if (questions!![position].category!!
                                .title == category.title && holder.categoryTitle!!.visibility == View.VISIBLE
                        ) {
                            holder.categoryScore!!.setSelected(true)
                            holder.categoryScore!!.visibility = View.VISIBLE
                            questions!![position].category!!.score = (category.score)
                            holder.categoryScore!!.setTextColor(category.color!!.accentColor)
                            holder.categoryScore!!.text = holder.itemView.context.getString(
                                R.string.category_score_ui,
                                if ((category.score >= 0)) context!!.getString(
                                    R.string.score_not_minus_ui,
                                    category.score
                                ) else context!!.getString(
                                    R.string.score_minus_ui,
                                    category.score * -1
                                ),
                                ""
                            )
                            updateQuestionsList(position)
                        }
                    }
                } else {
                    holder.categoryScore!!.visibility = View.GONE
                    Log.d("TAG", "Sfse category " + categories!!.toTypedArray().contentToString())
                }
                //setChoiceCorrectChose(questions.get(position).isCorrect(), holder, position);
            } else holder.isQuestionAnsweredCorrectly!!.visibility = View.GONE
            calculateAveTimeOfThinking(holder, position)
        }
    }

    private fun setupQuestionCorrection(holder: QuestionViewHolder, position: Int) {
        if (currentExam!!.isUsedCorrection && !currentExam.isUsedCorrectionByCorrectAnswers && !currentExam.isStarted) {
            holder.isQuestionAnsweredCorrectly!!.setChecked(questions!![position].isCorrect)
            if (!questions!![position].isCorrect && !questions!![position].isWhite) {
                for (i in 0 until holder.choices!!.childCount) {
                    if (i != getSelectedChoiceIndex(position)) {
                        val view: View = holder.choices!!.getChildAt(i)
                        view.isClickable = true
                    }
                }
            } else {
                for (i in 0 until holder.choices!!.childCount) {
                    holder.choices!!.getChildAt(i).isClickable = false
                }
            }
            setChoiceCorrectChose(questions!![position].isCorrect, holder, position)
        }
    }

    private fun onQuestionChoiceSelected(holder: QuestionViewHolder, position: Int) {
        for (childIndex in 0 until holder.choices!!.childCount) {
            holder.choices!!.getChildAt(childIndex)
                .setOnClickListener { view: View ->
                    val selectedChoice = getSelectedChoiceNumber(view.id)
                    if (questions!![position].selectedChoice != selectedChoice) {
                        questions!![position].selectedChoice = (selectedChoice)
                        questions!![position].isWhite = (false)
                        questions!![position].isNowSelected = (true)
                        submitNewChoice(holder, position, selectedChoice, view)
                        vibrateDevice(50)
                    } else {
                        clearCheck(holder.choices)
                        questions!![position].timeOfThinking = (0)
                        //currentExam.setSecondsOfThinkingOnQuestion(0);
                        questions!![position].selectedChoice = (0)
                        questions!![position].isWhite = (true)
                        updateQuestionsList(position)
                        questionClickedListener.onQuestionAnswerDeleted(questions!![position])
                        vibrateDevice(35)
                    }
                }
        }
    }

    private fun vibrateDevice(pattern: LongArray) {
        if (Saver.Companion.getInstance(context!!).vibrationEffects) {
            if (vibrator!!.hasVibrator()) {
                vibrator!!.vibrate(pattern, -1)
            }
        }
    }

    private fun vibrateDevice(millis: Long) {
        if (Saver.Companion.getInstance(context!!).vibrationEffects) {
            if (vibrator!!.hasVibrator()) {
                vibrator!!.vibrate(millis)
            }
        }
    }

    @SuppressLint("NonConstantResourceId")
    private fun getSelectedChoiceNumber(checkedId: Int): Int {
        var choiceNumber = 0
        when (checkedId) {
            R.id.choice_1 -> {
                choiceNumber = 1
            }

            R.id.choice_2 -> {
                choiceNumber = 2
            }

            R.id.choice_3 -> {
                choiceNumber = 3
            }

            R.id.choice_4 -> {
                choiceNumber = 4
            }

            else -> {}
        }
        return choiceNumber
    }

    private fun sendAddingCategoryBroadcast(status: String) {
        val intent: Intent = Intent(MainActivity.Companion.CATEGORY_ADDING_RECEIVER_INTENT)
        intent.putExtra(MainActivity.Companion.CATEGORY_ADDING_RECEIVER_RESULT, status)
        LocalBroadcastManager.getInstance(context!!).sendBroadcast(intent)
    }

    private fun calculateAveTimeOfThinking(holder: QuestionViewHolder, position: Int) {
        categories = Saver.Companion.getInstance(holder.itemView.context).loadQuestions()
            .categories
        if (questions!![position].category != null && categories!!.size >= 1) {
            for (category in categories!!) {
                if (questions!![position].category!!
                        .title == category.title && holder.categoryTitle!!.visibility == View.VISIBLE
                ) {
                    var averageOfTimesOfThinking = 0.0
                    var numbers = 0
                    for (q in questions!!) {
                        if (q.category != null) {
                            if (q.category!!.title == category.title) {
                                if (q.timeOfThinking != 0L) {
                                    averageOfTimesOfThinking += q.timeOfThinking.toDouble()
                                    numbers++
                                }
                                Log.d("TAG", "gsgsrg $averageOfTimesOfThinking")
                            }
                        }
                    }
                    if (averageOfTimesOfThinking != 0.0 && numbers != 0) {
                        averageOfTimesOfThinking /= numbers.toDouble()
                        averageOfTimesOfThinking = Math.round(averageOfTimesOfThinking).toDouble()
                        holder.mTimeOfThinkingLayout!!.visibility = View.VISIBLE
                        holder.mTimeOfThinking!!.text = holder.itemView.context.getString(
                            R.string.time_of_thinking_m,
                            averageOfTimesOfThinking.toInt()
                        )
                        category.secondsOfThinkingOnQuestion =
                            averageOfTimesOfThinking.toInt().toLong()
                        updateQuestionsList(position)
                    } else {
                        holder.mTimeOfThinkingLayout!!.visibility = View.GONE
                    }
                }
            }
        } else {
            holder.mTimeOfThinkingLayout!!.visibility = View.GONE
        }
    }

    private fun setChoicesBackground(holder: QuestionViewHolder, position: Int) {
        if (currentExam!!.isUsedCorrection && currentExam.isUsedCorrectionByCorrectAnswers && !currentExam.isStarted && getCorrectAnswerChoiceIndex(
                position
            ) != 4
        ) {
            clearChoicesBackground(holder.choices, position)
            for (childIndex in 0 until holder.choices!!.childCount) {
                if (childIndex == getCorrectAnswerChoiceIndex(position)) {
                    if (getCorrectAnswerChoiceIndex(position) == getSelectedChoiceIndex(position)) {
                        setChoiceCorrectChose(true, holder, position)
                    } else {
                        setChoiceCorrectChose(false, holder, position)
                        submitNewCorrectChoice(
                            holder,
                            position,
                            holder.choices!!.getChildAt(childIndex)
                        )
                    }
                }
            }
        } else {
            clearChoicesBackground(holder.choices, position)
            if (!questions!![position].isWhite && currentExam.isUsedCorrection && !questions!![position].isCorrect) {
                for (i in 0 until holder.choices!!.childCount) {
                    if (i != getSelectedChoiceIndex(position)) {
                        val view: View = holder.choices!!.getChildAt(i)
                        if (i == getCorrectAnswerChoiceIndex(position)) {
                            submitNewCorrectChoice(holder, position, view)
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun submitNewCorrectChoice(holder: QuestionViewHolder, position: Int, view: View) {
        submitNewChoice(holder, position, view)
        if (currentExam!!.isShowCorrectsWrongsWithColor && currentExam.isChecked) {
            view.background = context!!.resources.getDrawable(R.drawable.btn_correct_material)
            (view as Button).setTextColor(context!!.resources.getColor(R.color.elements_color_tint_rev))
        } else {
            view.background =
                context!!.resources.getDrawable(R.drawable.btn_correct_answer_material)
            (view as Button).setTextColor(context!!.resources.getColor(R.color.edu_level_very_good))
        }
        updateQuestionsList(position)
    }

    private fun disableChoicesGroup(holder: QuestionViewHolder) {
        holder.choices!!.setEnabled(false)
        for (i in 0 until holder.choices!!.childCount) {
            holder.choices!!.getChildAt(i).isClickable = false
        }
    }

    private fun enableDisableChoicesGroup(holder: QuestionViewHolder, enableDisable: Boolean) {
        holder.choices!!.setEnabled(enableDisable)
        for (i in 0 until holder.choices!!.childCount) {
            holder.choices!!.getChildAt(i).setEnabled(enableDisable)
        }
    }

    private fun setChoiceCorrectChose(
        isChecked: Boolean,
        holder: QuestionViewHolder,
        position: Int
    ) {
        Log.i("ir.saltech.", "BUTTONPosition: " + getSelectedChoiceIndex(position))
        clearChoicesBackground(holder.choices, position)
        if (getSelectedChoiceIndex(position) != 4) {
            val selectedChoice =
                (holder.choices!!.getChildAt(getSelectedChoiceIndex(position)) as Button)
            if (selectedChoice != null) {
                if (currentExam!!.isShowCorrectsWrongsWithColor && currentExam.isChecked) {
                    if (isChecked) {
                        selectedChoice.background =
                            context!!.resources.getDrawable(R.drawable.btn_correct_material)
                        selectedChoice.setTextColor(context!!.resources.getColor(android.R.color.background_light))
                    } else {
                        selectedChoice.background =
                            context!!.resources.getDrawable(R.drawable.btn_wrong_material)
                        selectedChoice.setTextColor(context!!.resources.getColor(android.R.color.background_light))
                    }
                } else {
                    if (isChecked) {
                        selectedChoice.background =
                            context!!.resources.getDrawable(R.drawable.btn_correct_material)
                        selectedChoice.setTextColor(context!!.resources.getColor(android.R.color.background_light))
                    } else {
                        selectedChoice.background =
                            context!!.resources.getDrawable(R.drawable.btn_filled_material)
                        selectedChoice.setTextColor(context!!.resources.getColor(R.color.elements_color_tint_rev))
                    }
                }
            }
            updateQuestionsList(position)
        }
    }

    private fun loadQuestionSelectedChoice(holder: QuestionViewHolder, position: Int) {
        enableDisableChoicesGroup(holder, currentExam != null)
        if (questions!![position].selectedChoice != 0 && !isCorrectingByCorrectAnswer) {
            for (childIndex in 0 until holder.choices!!.childCount) {
                if (currentExam != null) {
                    if (!currentExam.isStarted) {
                        clearCheck(holder.choices)
                        clearChoicesBackground(holder.choices, position)
                    }
                } else {
                    clearCheck(holder.choices)
                    clearChoicesBackground(holder.choices, position)
                }
                if (childIndex == getSelectedChoiceIndex(position)) {
                    submitNewChoice(
                        holder,
                        position,
                        getSelectedChoiceIndex(position),
                        holder.choices!!.getChildAt(childIndex)
                    )
                    //holder.deleteSelectedChoice.setAlpha(1f);
                }
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun submitNewChoice(holder: QuestionViewHolder, position: Int, view: View) {
        clearChoicesBackground(holder.choices, position)
        view.elevation = 3f
        view.background = context!!.resources.getDrawable(R.drawable.btn_filled_material)
        (view as Button).setTextColor(context!!.resources.getColor(R.color.elements_color_tint_rev))
        updateQuestionsList(position)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun clearChoicesBackground(parent: LinearLayout?, position: Int) {
        parent!!.isFocusable = true
        for (choiceIndex in 0 until parent.childCount) {
            if (choiceIndex != getSelectedChoiceIndex(position)) {
                parent.getChildAt(choiceIndex).background = null
                (parent.getChildAt(choiceIndex) as Button).setTextColor(
                    context!!.resources.getColor(R.color.elements_color_tint)
                )
                parent.getChildAt(choiceIndex).elevation = 2.2f
            } else {
                val isChecked: Boolean = questions!![position].isCorrect
                val selectedChoice = parent.getChildAt(choiceIndex) as Button
                if (currentExam!!.isShowCorrectsWrongsWithColor && currentExam.isChecked) {
                    if (isChecked) {
                        selectedChoice.background =
                            context!!.resources.getDrawable(R.drawable.btn_correct_material)
                        selectedChoice.setTextColor(context!!.resources.getColor(android.R.color.background_light))
                    } else {
                        selectedChoice.background =
                            context!!.resources.getDrawable(R.drawable.btn_wrong_material)
                        selectedChoice.setTextColor(context!!.resources.getColor(android.R.color.background_light))
                    }
                } else {
                    if (isChecked) {
                        selectedChoice.background =
                            context!!.resources.getDrawable(R.drawable.btn_correct_material)
                        selectedChoice.setTextColor(context!!.resources.getColor(android.R.color.background_light))
                    } else {
                        selectedChoice.background =
                            context!!.resources.getDrawable(R.drawable.btn_filled_material)
                        selectedChoice.setTextColor(context!!.resources.getColor(R.color.elements_color_tint_rev))
                    }
                }
                selectedChoice.elevation = 3f
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun submitNewChoice(
        holder: QuestionViewHolder,
        position: Int,
        selectedChoice: Int,
        view: View
    ) {
        clearCheck(holder.choices)
        view.elevation = 3f
        view.background = context!!.resources.getDrawable(R.drawable.btn_filled_material)
        (view as Button).setTextColor(context!!.resources.getColor(R.color.elements_color_tint_rev))
        if (selectedChoice != 0) {
            questions!![position].isWhite = (false)
        } else {
            questions!![position].isNowSelected = (false)
        }
        if (!currentExam!!.isStarted) {
            clearChoicesBackground(holder.choices, position)
        }
        Log.v(
            "TAG",
            "Time Of Thinking on the question: " + currentExam.secondsOfThinkingOnQuestion + " Qn: " + questions!![position].questionNumber + " Qn: " + questions!![position].questionNumber
        )
        if (currentExam.isUsedChronometer) {
            if (questions!![position].timeOfThinking == 0L || (questions!![position].timeOfThinking != 0L && currentExam.secondsOfThinkingOnQuestion > 5)) {
                questions!![position].timeOfThinking = (currentExam.secondsOfThinkingOnQuestion)
                currentExam.secondsOfThinkingOnQuestion = (0)
                updateRecentExams()
            }
        }
        updateQuestionsList(position)
        questionClickedListener.onQuestionAnswered(questions!![position])
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun clearCheck(parent: LinearLayout?) {
        for (childIndex in 0 until parent!!.childCount) {
            parent.getChildAt(childIndex).background = context!!.resources.getDrawable(R.drawable.btn_default_material)
            (parent.getChildAt(childIndex) as Button).setTextColor(
                context!!.resources.getColor(R.color.elements_color_tint)
            )
        }
    }

    private fun getSelectedChoiceIndex(position: Int): Int {
        return questions!![position].selectedChoice - 1
    }

    private fun getCorrectAnswerChoiceIndex(position: Int): Int {
        return questions!![position].correctAnswerChoice - 1
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun loadCurrentBookmark(holder: QuestionViewHolder, position: Int) {
        if (questions!![position].getBookmark().name == Bookmark.Companion.NONE) {
            setQuestionDefaults(holder, position)
        } else {
            holder.questionNumber!!.visibility = View.VISIBLE
            holder.questionNumber!!.background = context!!.getDrawable(R.drawable.tiny_button_bg)
            holder.questionNumber!!.background.setColorFilter(
                questions!![position].getBookmark().pinColor.color,
                PorterDuff.Mode.SRC_IN
            )
            holder.questionNumber!!.setTextColor(
                questions!![position].getBookmark().pinColor.color
            )
            questionClickedListener.onQuestionBookmarkChanged(questions!![position])
            holder.questionNumber!!.isClickable = true
        }
    }

    private fun setupCategoryOption(holder: QuestionViewHolder, position: Int) {
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

    private fun setQuestionSelected(holder: QuestionViewHolder, position: Int) {
        if (questions!![position].isSelected) {
            val selectedQuestionColor = Color.argb(60, 255, 193, 7)
            val selectedQuestionColorNonAlpha = Color.argb(0, 255, 193, 7)
            @SuppressLint("Recycle") val valueAnimator: ValueAnimator = ValueAnimator.ofObject(
                ArgbEvaluator(), selectedQuestionColorNonAlpha, selectedQuestionColor
            )
            valueAnimator.addUpdateListener { valueAnimator1: ValueAnimator ->
                holder.questionLayout!!.setOverlayColor(
                    valueAnimator1.getAnimatedValue() as Int
                )
            }
            valueAnimator.setDuration(250)
            valueAnimator.setStartDelay(100)
            valueAnimator.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animator: Animator) {
                }

                override fun onAnimationEnd(animator: Animator) {
                    @SuppressLint("Recycle") val valueAnimator2: ValueAnimator =
                        ValueAnimator.ofObject(
                            ArgbEvaluator(), selectedQuestionColor, selectedQuestionColorNonAlpha
                        )
                    valueAnimator2.addUpdateListener { valueAnimator1: ValueAnimator ->
                        holder.questionLayout!!.setOverlayColor(
                            valueAnimator1.getAnimatedValue() as Int
                        )
                    }
                    valueAnimator2.setDuration(250)
                    valueAnimator2.setStartDelay(150)
                    valueAnimator2.addListener(object : Animator.AnimatorListener {
                        override fun onAnimationStart(animator: Animator) {
                        }

                        @SuppressLint("NotifyDataSetChanged")
                        override fun onAnimationEnd(animator: Animator) {
                            questions!![position].isSelected = (false)
                            updateQuestionsList(position)
                            notifyDataSetChanged()
                        }

                        override fun onAnimationCancel(animator: Animator) {
                        }

                        override fun onAnimationRepeat(animator: Animator) {
                        }
                    })
                    valueAnimator2.start()
                }

                override fun onAnimationCancel(animator: Animator) {
                }

                override fun onAnimationRepeat(animator: Animator) {
                }
            })
            valueAnimator.start()
        }
    }

    private fun updateQuestionsList(position: Int) {
        val q: Question = questions!![position]
        questions!!.removeAt(position)
        questions!!.add(position, q)
        val questionsE: Questions = Questions()
        questionsE.questions = (questions)
        questionsE.categories = (categories!!)
        Saver.Companion.getInstance(context!!).saveQuestions(questionsE)
        currentExam!!.answerSheet = (questionsE)
        updateRecentExams()
        Log.v("TAG", "com.saltechgroup. " + questions!![position])
        Log.v("TAG", "com.saltechgroup. $questions")
    }

    private fun updateRecentExams() {
        val recentExams: Exams = Saver.Companion.getInstance(context!!).loadRecentExams()
        recentExams.updateCurrentExam(currentExam!!)
        Saver.Companion.getInstance(context!!).saveRecentExams(recentExams)
    }

    private fun setQuestionDefaults(holder: QuestionViewHolder, position: Int) {
        BlurViewHolder.setBlurView(activity, holder.questionLayout!!)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            holder.isQuestionAnsweredCorrectly!!.buttonDrawable!!
                .setColorFilter(accentColor, PorterDuff.Mode.SRC_IN)
        }
        holder.choices!!.visibility = View.VISIBLE
        holder.questionNumber!!.setSelected(true)
        holder.questionNumber!!.setTextColor(accentColor)
        holder.questionNumber!!.background = null
        holder.questionNumber!!.text = context!!.getString(
            R.string.num,
            questions!![position].questionNumber
        )
        //prepareQuestionForGridLayout(holder, position);
    }

    @Deprecated("")
    @SuppressLint("ClickableViewAccessibility")
    private fun prepareQuestionForGridLayout(holder: QuestionViewHolder, position: Int) {
        holder.choices!!.visibility = View.GONE
        try {
            val params: GridLayoutManager.LayoutParams =
                holder.questionLayout!!.layoutParams as GridLayoutManager.LayoutParams
            val margin: Int = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                8f,
                context!!.resources.displayMetrics
            ).toInt()
            params.leftMargin = margin
            params.rightMargin = margin
            holder.questionLayout!!.setLayoutParams(params)
        } catch (e: ClassCastException) {
            e.printStackTrace()
            val params: LinearLayout.LayoutParams =
                holder.questionLayout!!.layoutParams as LinearLayout.LayoutParams
            val margin: Int = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                8f,
                context!!.resources.displayMetrics
            ).toInt()
            params.leftMargin = margin
            params.rightMargin = margin
            holder.questionLayout!!.setLayoutParams(params)
        }
        holder.questionNumber!!.setOnTouchListener { v: View?, e: MotionEvent ->
            if (e.action == MotionEvent.ACTION_DOWN) {
                questionNumberClicked = true
            } else if (e.action == MotionEvent.ACTION_UP && questionNumberClicked) {
                questionClickedListener.onQuestionClicked(
                    questions!![position],
                    e.rawX,
                    e.rawY
                )
                questionNumberClicked = false
            }
            true
        }
    }

    private fun prepareQuestionForLinearLayout(holder: QuestionViewHolder, position: Int) {
    }

    override fun getItemCount() = questions!!.size

    override fun getItemViewType(position: Int): Int {
        return position
    }

    class QuestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var parentLayout: LinearLayout? = null
        var questionLayout: BlurView? = null
        var categoryHeaderLayout: LinearLayout? = null
        var questionNumber: TextView? = null
        var choices: LinearLayout? = null
        var timeOfThinkingLayout: LinearLayout? = null
        var categoryTitle: TextView? = null
        var categoryScore: TextView? = null
        var timeOfThinking: TextView? = null
        var mTimeOfThinkingLayout: LinearLayout? = null
        var mTimeOfThinking: TextView? = null
        var isQuestionAnsweredCorrectly: CheckBox? = null
        var categoryIndicator: Button? = null
        var disableQuestion: View? = null
        var editCategory: ImageButton? = null
        var categoryTimeRemaining: TextView? = null
        var categoryTimeRemainingLayout: LinearLayout? = null
        private var categoryTimeRemainingImage: ImageView? = null

        init {
            init(itemView)
        }

        private fun init(v: View) {
            questionLayout = v.findViewById<BlurView>(R.id.question_parent_layout)
            questionNumber = v.findViewById<TextView>(R.id.question_number)
            choices = v.findViewById<LinearLayout>(R.id.choices_group)
            timeOfThinkingLayout = v.findViewById<LinearLayout>(R.id.time_of_thinking_layout)
            mTimeOfThinkingLayout = v.findViewById<LinearLayout>(R.id.time_of_thinking_layout_m)
            categoryIndicator = v.findViewById<Button>(R.id.add_new_category)
            categoryScore = v.findViewById<TextView>(R.id.category_score)
            categoryTitle = v.findViewById<TextView>(R.id.category_title)
            disableQuestion = v.findViewById<View>(R.id.disable_question)
            timeOfThinking = v.findViewById<TextView>(R.id.time_of_thinking)
            mTimeOfThinking = v.findViewById<TextView>(R.id.time_of_thinking_m)
            isQuestionAnsweredCorrectly = v.findViewById<CheckBox>(R.id.is_correct_choice)
            editCategory = v.findViewById<ImageButton>(R.id.edit_category)
            categoryHeaderLayout = v.findViewById<LinearLayout>(R.id.category_header_layout)
            parentLayout = v.findViewById<LinearLayout>(R.id.t_parent_layout)
            categoryTimeRemainingImage = v.findViewById<ImageView>(R.id.time_remaining_image_c)
            categoryTimeRemaining = v.findViewById<TextView>(R.id.time_remaining_c)
            categoryTimeRemainingLayout = v.findViewById<LinearLayout>(R.id.time_remaining_layout_c)
        }
    }

    companion object {
        const val MAX_OF_CHOICES: Int = 4
        private const val TWO_DIGIT_NUM = 10
    }
}
