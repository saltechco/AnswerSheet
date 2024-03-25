package ir.saltech.answersheet.view.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Handler
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yy.mobile.rollingtextview.RollingTextView
import ir.saltech.answersheet.R
import ir.saltech.answersheet.intf.listener.ExamSelectedListener
import ir.saltech.answersheet.`object`.container.Saver
import ir.saltech.answersheet.`object`.data.Exam
import ir.saltech.answersheet.`object`.data.Exams
import ir.saltech.answersheet.view.activity.MainActivity
import ir.saltech.answersheet.view.container.MaterialAlertDialog
import ir.saltech.answersheet.view.container.Toast
import ir.saltech.answersheet.view.holder.ExamViewHolder

//import ir.saltech.answersheet.view.dialog.EditExamFeaturesDialog;
class ExamsViewAdapter(
    private val viewType: Int,
    exams: MutableList<Exam>,
    examSelectedListener: ExamSelectedListener,
    private val activity: Activity
) : RecyclerView.Adapter<ExamViewHolder?>() {
    private val examSelectedListener: ExamSelectedListener = examSelectedListener
    private val exams: MutableList<Exam> = exams
    private var context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExamViewHolder {
        context = parent.context
        return ExamViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.view_template_exam, parent, false)
        )
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(
        holder: ExamViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        holder.setTemplateView(context!!, viewType)
        if (position % 2 == 0) {
            val params: GridLayoutManager.LayoutParams =
                holder.examCard!!.layoutParams as GridLayoutManager.LayoutParams
            params.leftMargin = convertDpToPx(3f).toInt()
            holder.examCard!!.setLayoutParams(params)
        }
        setTexts(holder, position)
        onClicks(holder, position)
        if (exams[position].isLoading) {
            holder.onMainButtonClicked(context!!)
        }
    }

    private fun convertDpToPx(dp: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context!!.resources.displayMetrics
        )
    }

    private fun onClicks(holder: ExamViewHolder, position: Int) {
        holder.examAction!!.setOnClickListener { view: View? ->
            if (viewType == ExamViewHolder.TEMPLATE_VIEW_CORRECTING_EXAMS) {
                exams[position].isLoading = (true)
                holder.onMainButtonClicked(context!!)
                loadExamAsync(position, WORK_RECENT_EXAM_CLICKED)
            } else if (viewType == ExamViewHolder.Companion.TEMPLATE_VIEW_CREATING_EXAMS) {
                exams[position].isLoading = (true)
                holder.onMainButtonClicked(context!!)
                loadExamAsync(position, WORK_CURRENT_EXAM_CLICKED)
            } else if (viewType == ExamViewHolder.Companion.TEMPLATE_VIEW_RUNNING_EXAMS) {
                resumeSelectedExam(holder, position)
            } else if (viewType == ExamViewHolder.TEMPLATE_VIEW_SUSPENDED_EXAMS) {
                restartSelectedExam(holder, position)
            }
            exams[position].reviewCount = (exams[position].reviewCount + 1)
            updateRecentExams(position)
        }
        if (viewType == ExamViewHolder.TEMPLATE_VIEW_FINISHED_EXAMS && !exams[position].isStarted && !exams[position].isCreating &&
            !exams[position].isSuspended && !exams[position].isCorrecting
        ) {
            holder.viewRecentExam!!.setOnClickListener { view: View? ->
                exams[position].reviewCount = (exams[position].reviewCount + 1)
                exams[position].isLoading = (true)
                updateRecentExams(position)
                holder.onMainButtonClicked(context!!)
                loadExamAsync(position, WORK_RECENT_EXAM_CLICKED)
            }
        }
        holder.setExamFavoriteIconView(context!!, exams[position].isFavorite)
        holder.addToFavorite!!.setOnClickListener { view: View? ->
            exams[position].isFavorite = (!exams[position].isFavorite)
            holder.setExamFavoriteIconView(context!!, exams[position].isFavorite)
            updateRecentExams(position)
        }
        if (!exams[position].isSuspended) {
            holder.editExamFeatures!!.setOnClickListener { view: View? ->
                editSelectedExam(
                    position
                )
            }
        }
        if (!exams[position].isStarted || exams[position].isSuspended ||
            exams[position].isCreating || exams[position].isCorrecting
        ) {
            holder.deleteExam!!.setOnClickListener {
                deleteSelectedExam(
                    holder,
                    position
                )
            }
        }
        if (exams[position].isStarted && !exams[position].isSuspended && !exams[position].isCreating) {
            holder.suspendExam!!.setOnClickListener {
                suspendSelectedExam(
                    holder,
                    position
                )
            }
        }
        if (!exams[position].isStarted && !exams[position].isSuspended &&
            !exams[position].isCorrecting && !exams[position].isCreating
        ) {
            holder.resetExam!!.setOnClickListener { view: View? ->
                exams[position].reviewCount = (exams[position].reviewCount + 1)
                updateRecentExams(position)
                restartSelectedExam(holder, position)
            }
        }
        if (exams[position].isUsedTiming && exams[position].examTime != 0L && exams[position].isStarted) {
            holder.showExamRemainingTime(
                context!!,
                exams[position].examTime,
                exams[position].examTimeLeft
            )
        }
        holder.examInfo!!.setOnClickListener { view: View? ->
            holder.showExamInfoDialog(
                context!!,
                activity as FragmentActivity,
                exams[position]
            )
        }
    }

    private fun setTexts(holder: ExamViewHolder, position: Int) {
        holder.examName!!.setSelected(true)
        holder.examName!!.text = exams[position].getExamName(0)?.getName()
    }

    private fun resumeSelectedExam(holder: ExamViewHolder, position: Int) {
        val builder: MaterialAlertDialog = MaterialAlertDialog(context!!)
        builder.setIcon(R.drawable.continue_exam)
        builder.setTitle("ادامه دادن آزمون " + exams[position].getExamName(0)?.getName())
        builder.setMessage("آیا می خواهید این آزمون را ادامه دهید؟")
        builder.setPositiveButton("بله") { v4: View? ->
            builder.dismiss(builder)
            holder.onMainButtonClicked(context!!)
            exams[position].isLoading = (true)
            updateRecentExams(position)
            loadExamAsync(position, WORK_EXAM_RESUMED)
        }
        builder.setNegativeButton(
            "خیر",
            View.OnClickListener { v4: View? -> builder.dismiss(builder) })
        builder.show(activity as FragmentActivity)
    }

    private fun editSelectedExam(position: Int) {
        /*Saver.getInstance(context).setDismissSide(MaterialDialogFragment.SIDE_FRAGMENT_SHOWER);
		MaterialFragmentShower shower = new MaterialFragmentShower(context);
		shower.setFragment(new EditExamFeaturesDialog(true, shower, exams.get(position), edited -> {
			exams.set(position, edited);
			notifyDataSetChanged();
			examSelectedListener.onExamEdited(edited);
		}));
		shower.setLayoutMatchParent(true);
		shower.show((FragmentActivity) activity, shower);*/
    }

    private fun suspendSelectedExam(holder: ExamViewHolder, position: Int) {
        val builder: MaterialAlertDialog = MaterialAlertDialog(context!!)
        builder.setIcon(R.drawable.suspend_exam)
        builder.setTitle("معلق کردن آزمون " + exams[position].getExamName())
        builder.setMessage("آیا مطمئن هستید که می خواهید این آزمون را معلق کنید؟\n\nامکان شروع مجدد این آزمون وجود خواهد داشت؛ اما گزینه ها و سایر اطلاعات از بین خواهد رفت!")
        builder.setPositiveButton("بله") { v4: View? ->
            builder.dismiss(builder)
            holder.examProgressBar!!.visibility = View.GONE
            exams[position].isSuspended = (true)
            exams[position].isStarted = (false)
            val examsL: Exams = Saver.getInstance(context!!).loadRecentExams()
            for (exam in examsL.getExamList()) {
                if (exam.id == exams[position].id) {
                    exam.isStarted = (false)
                    exam.isSuspended = (true)
                    exam.secondsOfThinkingOnQuestion = (0)
                }
            }
            Saver.Companion.getInstance(context!!).saveRecentExams(examsL)
            notifyDataSetChanged()
            //ir.saltech.answersheet.object.data.Activity.setCurrentActivity(context, ir.saltech.answersheet.object.data.Activity.ACTIVITY_TYPE_EXAM_SUSPENDED, exams.get(position).getExamName(), 0, 0);
            examSelectedListener.onExamSuspended(exams[position])
        }
        builder.setNegativeButton(
            "خیر"
        ) { v4: View? -> builder.dismiss(builder) }
        builder.show(activity as FragmentActivity)
    }

    private fun restartSelectedExam(holder: ExamViewHolder, position: Int) {
        val builder: MaterialAlertDialog = MaterialAlertDialog(context!!)
        builder.setIcon(R.drawable.reset_exam)
        builder.setTitle("شروع مجدد آزمون " + exams[position].getExamName())
        builder.setMessage("آیا می خواهید این آزمون را از ابتدا آغاز کنید؟")
        builder.setPositiveButton("بله", View.OnClickListener { v4: View? ->
            builder.dismiss(builder)
            holder.onMainButtonClicked(context!!)
            exams[position].isSuspended = (true)
            exams[position].isStarted = (false)
            exams[position].isLoading = (true)
            loadExamAsync(position, WORK_EXAM_RESUMED)
            updateRecentExams(position)
        })
        builder.setNegativeButton(
            "خیر"
        ) { v4: View? -> builder.dismiss(builder) }
        builder.show(activity as FragmentActivity)
    }

    private fun loadExamAsync(position: Int, workType: Int) {
        Handler().postDelayed({
            when (workType) {
                WORK_EXAM_RESUMED -> examSelectedListener.onExamResumed(
                    exams[position]
                )

                WORK_CURRENT_EXAM_CLICKED -> examSelectedListener.onExamClicked(
                    exams[position], MainActivity.SIDE_CURRENT_EXAMS
                )

                WORK_RECENT_EXAM_CLICKED -> examSelectedListener.onExamClicked(
                    exams[position], MainActivity.SIDE_RECENT_EXAMS
                )

                else -> {}
            }
        }, LOAD_EXAM_DELAY_MILLIS.toLong())
    }

    private fun deleteSelectedExam(holder: ExamViewHolder, position: Int) {
        holder.examProgressLayout!!.visibility = View.GONE
        val builder: MaterialAlertDialog = MaterialAlertDialog(context!!)
        builder.setIcon(R.drawable.delete)
        builder.setTitle("حذف آزمون " + exams[position].getExamName())
        builder.setMessage("آیا مطمئن هستید که می خواهید این آزمون را حذف کنید؟\n\nامکان بازگشت دیگر وجود نخواهد داشت!")
        builder.setPositiveButton("بله") { v6: View? ->
            builder.dismiss(builder)
            val exams: Exams = Saver.getInstance(context!!).loadRecentExams()
            val removeExam: Exam = this.exams[position]
            for (i in exams.getExamList().indices) {
                if (exams.getExamList()[i].id == removeExam.id) {
                    this.exams.removeAt(position)
                    exams.removeExam(i)
                    Saver.getInstance(context!!).saveRecentExams(exams)
                    examSelectedListener.onExamDeleted(
                        removeExam,
                        position,
                        MainActivity.Companion.SIDE_RECENT_EXAMS
                    )
                    Toast.Companion.makeText(
                        context!!,
                        "آزمون «" + removeExam.getExamName() + "» با موفقیت حذف شد.",
                        Toast.Companion.LENGTH_SHORT
                    ).show()
                    notifyDataSetChanged()
                    break
                }
            }
        }
        builder.setNegativeButton(
            "خیر"
        ) { v4: View? -> builder.dismiss(builder) }
        builder.show(activity as FragmentActivity)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateRecentExams(position: Int) {
        val recentExams: Exams = Saver.getInstance(context!!).loadRecentExams()
        recentExams.updateCurrentExam(exams[position])
        Saver.getInstance(context!!).saveRecentExams(recentExams)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount() = exams.size

    companion object {
        private const val LOAD_EXAM_DELAY_MILLIS = 2000
        private const val WORK_EXAM_RESUMED = 0
        private const val WORK_RECENT_EXAM_CLICKED = 1
        private const val WORK_CURRENT_EXAM_CLICKED = 2
    }
}
