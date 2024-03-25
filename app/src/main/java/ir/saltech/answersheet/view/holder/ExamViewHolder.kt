package ir.saltech.answersheet.view.holder

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.os.Build
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.akexorcist.roundcornerprogressbar.common.AnimatedRoundCornerProgressBar
import com.yy.mobile.rollingtextview.RollingTextView
import ir.saltech.answersheet.R
import ir.saltech.answersheet.`object`.data.Exam
import ir.saltech.answersheet.view.container.MaterialAlertDialog
import java.util.Locale

class ExamViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var examCard: CardView? = null
    var examName: TextView? = null
    var examProgressLayout: ConstraintLayout? = null
    var examProgressBar: ProgressBar? = null
    var examRemainingTime: TextView? = null
    var examAction: Button? = null
    var viewRecentExam: ImageButton? = null
    var addToFavorite: ImageButton? = null
    var resetExam: ImageButton? = null
    var examInfo: ImageButton? = null
    var deleteExam: ImageButton? = null
    var suspendExam: ImageButton? = null
    var editExamFeatures: ImageButton? = null


    init {
        init(itemView)
    }

    private fun init(v: View) {
        examCard = v.findViewById<CardView>(R.id.exam_card)
        examName = v.findViewById<TextView>(R.id.exam_name)
        examProgressBar = v.findViewById<ProgressBar>(R.id.load_progress_bar)
        examRemainingTime = v.findViewById<TextView>(R.id.remaining_time)
        examProgressLayout = v.findViewById<ConstraintLayout>(R.id.load_progress_bar_layout)
        viewRecentExam = v.findViewById<ImageButton>(R.id.recent_exam_view)
        addToFavorite = v.findViewById<ImageButton>(R.id.add_to_favorite)
        examAction = v.findViewById<Button>(R.id.exam_action)
        resetExam = v.findViewById<ImageButton>(R.id.reset_exam)
        examInfo = v.findViewById<ImageButton>(R.id.exam_info)
        suspendExam = v.findViewById<ImageButton>(R.id.exam_suspend)
        deleteExam = v.findViewById(R.id.exam_delete)
        editExamFeatures = v.findViewById<ImageButton>(R.id.exam_edit_features)
    }

    private fun printTime(second: Int, minute: Int): String {
        return if (second >= TWO_DIGIT_NUM && minute >= TWO_DIGIT_NUM) {
            String.format(Locale.getDefault(), "%d:%d", minute, second)
        } else if (TWO_DIGIT_NUM in (second + 1)..minute) {
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

    fun showExamRemainingTime(context: Context, total: Long, now: Long) {
        if (total > now) {
            val progress = (((now.toFloat()) / (total.toFloat())) * 100f).toInt()
            examProgressBar!!.visibility = View.VISIBLE
            examProgressBar!!.isIndeterminate = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                examProgressBar!!.setProgress(progress, true)
            } else {
                examProgressBar!!.progress = progress
            }
            val nowTime = longArrayOf(now / 60000, now % 60000 / 1000)
            printExamTime(context, progress, nowTime)
            examRemainingTime!!.visibility = View.VISIBLE
            examProgressLayout!!.visibility = View.VISIBLE
        }
    }

    private fun printExamTime(context: Context, progress: Int, nowTime: LongArray) {
        examRemainingTime!!.text = printTime(nowTime[1].toInt(), nowTime[0].toInt())
        if (progress >= MAX_OF_NORMAL_PROGRESS) {
            examRemainingTime!!.setTextColor(context.resources.getColor(R.color.exam_remaining_time))
        } else {
            examRemainingTime!!.setTextColor(context.resources.getColor(R.color.exam_remaining_time_2))
        }
    }

    @SuppressLint("DefaultLocale")
    fun showExamInfoDialog(context: Context, activity: FragmentActivity?, currentExam: Exam?) {
        if (currentExam != null) {
            val dialog = MaterialAlertDialog(context)
            dialog.setIcon(R.drawable.app_info)
            dialog.setTitle(context.getString(R.string.exam_name_text, currentExam.getExamName()))
            var time: String? = null
            if (currentExam.isUsedTiming) {
                time = printTime(
                    currentExam.examTime.toInt() % 60000,
                    currentExam.examTime.toInt() / 60000
                )
            }
            dialog.setMessage(
                (if ((time != null && !currentExam.isCreating)) (String.format(
                    "زمان آزمون: %s",
                    time
                ) + "\n") else "") + String.format(
                    "تعداد تست: %d",
                    currentExam.getExamQuestionsRange()!![2] + 1
                ) + (if ((currentExam.isUsedCategorize)) """
     
     سؤالات دسته بندی شده
     """.trimIndent() else "") + "\n" + String.format(
                    "زمان شروع: %s",
                    currentExam.startExamTime
                )
            )
            dialog.setPositiveButton(
                "متوجه شدم",
                View.OnClickListener { _: View? -> dialog.dismiss(dialog) })
            dialog.setCancelable(false)
            dialog.show(activity!!)
        }
    }

    fun setExamFavoriteIconView(context: Context, favorite: Boolean) {
        if (favorite) {
            addToFavorite!!.setImageResource(R.drawable.favorite_exam)
            addToFavorite!!.getDrawable().setColorFilter(
                context.resources.getColor(R.color.added_to_favorite),
                PorterDuff.Mode.SRC_IN
            )
            addToFavorite!!.setBackgroundTintList(ColorStateList.valueOf(context.resources.getColor(R.color.added_to_favorite)))
        } else {
            addToFavorite!!.setImageResource(R.drawable.not_favorite)
            addToFavorite!!.getDrawable().setColorFilter(
                context.resources.getColor(R.color.disable_button),
                PorterDuff.Mode.SRC_IN
            )
            addToFavorite!!.setBackgroundTintList(ColorStateList.valueOf(context.resources.getColor(R.color.disable_button)))
        }
    }

    fun onMainButtonClicked(context: Context) {
        if (viewRecentExam!!.visibility != View.VISIBLE) {
            examAction!!.setText(R.string.exam_loading)
            examAction!!.isClickable = false
            examAction!!.setTextColor(context.resources.getColor(R.color.disable_button))
            examAction!!.backgroundTintList =
                ColorStateList.valueOf(context.resources.getColor(R.color.disable_button))
        } else {
            viewRecentExam!!.isClickable = false
            viewRecentExam!!.getDrawable().setColorFilter(
                context.resources.getColor(R.color.disable_button),
                PorterDuff.Mode.SRC_IN
            )
            viewRecentExam!!.setBackgroundTintList(ColorStateList.valueOf(context.resources.getColor(R.color.disable_button)))
        }
        examProgressBar!!.isIndeterminate = true
        examRemainingTime!!.visibility = View.GONE
        examProgressLayout!!.visibility = View.VISIBLE
    }

    @SuppressLint("SetTextI18n")
    fun setTemplateView(context: Context, viewType: Int) {
        examAction!!.setTextColor(context.resources.getColor(R.color.colorAccent))
        examAction!!.backgroundTintList =
            ColorStateList.valueOf(context.resources.getColor(R.color.colorAccent))
        when (viewType) {
            TEMPLATE_VIEW_RUNNING_EXAMS -> {
                examAction!!.setText(R.string.resume_running_exam)
                viewRecentExam!!.setVisibility(View.GONE)
                resetExam!!.setVisibility(View.GONE)
                deleteExam!!.setVisibility(View.GONE)
                suspendExam!!.setVisibility(View.VISIBLE)
            }

            TEMPLATE_VIEW_SUSPENDED_EXAMS -> {
                examAction!!.setText(R.string.restart_exam)
                viewRecentExam!!.setVisibility(View.GONE)
                resetExam!!.setVisibility(View.GONE)
                deleteExam!!.setVisibility(View.VISIBLE)
                editExamFeatures!!.setVisibility(View.GONE)
                suspendExam!!.setVisibility(View.GONE)
            }

            TEMPLATE_VIEW_FINISHED_EXAMS -> {
                examAction!!.text = "NONE"
                examAction!!.visibility = View.GONE
                viewRecentExam!!.setVisibility(View.VISIBLE)
                resetExam!!.setVisibility(View.VISIBLE)
                deleteExam!!.setVisibility(View.VISIBLE)
                editExamFeatures!!.setVisibility(View.VISIBLE)
                suspendExam!!.setVisibility(View.GONE)
            }

            TEMPLATE_VIEW_CREATING_EXAMS -> {
                examAction!!.setText(R.string.create_exam)
                viewRecentExam!!.setVisibility(View.GONE)
                resetExam!!.setVisibility(View.GONE)
                deleteExam!!.setVisibility(View.VISIBLE)
                editExamFeatures!!.setVisibility(View.VISIBLE)
                suspendExam!!.setVisibility(View.GONE)
            }

            TEMPLATE_VIEW_CORRECTING_EXAMS -> {
                examAction!!.setText(R.string.correct_exam)
                viewRecentExam!!.setVisibility(View.GONE)
                resetExam!!.setVisibility(View.GONE)
                deleteExam!!.setVisibility(View.VISIBLE)
                editExamFeatures!!.setVisibility(View.VISIBLE)
                suspendExam!!.setVisibility(View.GONE)
            }

            else -> {}
        }
    }

    companion object {
        const val TEMPLATE_VIEW_RUNNING_EXAMS: Int = 1
        const val TEMPLATE_VIEW_SUSPENDED_EXAMS: Int = 2
        const val TEMPLATE_VIEW_FINISHED_EXAMS: Int = 3
        const val TEMPLATE_VIEW_CREATING_EXAMS: Int = 4
        const val TEMPLATE_VIEW_CORRECTING_EXAMS: Int = 5
        private const val TWO_DIGIT_NUM = 10
        private const val MAX_OF_NORMAL_PROGRESS = 58
    }
}
