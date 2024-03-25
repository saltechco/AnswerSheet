package ir.saltech.answersheet.view.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ir.saltech.answersheet.R;
import ir.saltech.answersheet.intf.listener.ExamSelectedListener;
import ir.saltech.answersheet.object.container.Saver;
import ir.saltech.answersheet.object.data.Exam;
import ir.saltech.answersheet.object.data.Exams;
import ir.saltech.answersheet.view.activity.MainActivity;
import ir.saltech.answersheet.view.container.MaterialAlertDialog;
import ir.saltech.answersheet.view.container.Toast;
//import ir.saltech.answersheet.view.dialog.EditExamFeaturesDialog;
import ir.saltech.answersheet.view.holder.ExamViewHolder;

public class ExamsViewAdapter extends RecyclerView.Adapter<ExamViewHolder> {
	private static final int LOAD_EXAM_DELAY_MILLIS = 2000;
	private static final int WORK_EXAM_RESUMED = 0;
	private static final int WORK_RECENT_EXAM_CLICKED = 1;
	private static final int WORK_CURRENT_EXAM_CLICKED = 2;
	private final ExamSelectedListener examSelectedListener;
	private final List<Exam> exams;
	private final Activity activity;
	private final int viewType;
	private Context context;

	public ExamsViewAdapter(int viewType, @NonNull List<Exam> exams, @NonNull ExamSelectedListener examSelectedListener, @NonNull Activity activity) {
		this.viewType = viewType;
		this.examSelectedListener = examSelectedListener;
		this.exams = exams;
		this.activity = activity;
	}

	@NonNull
	@Override
	public ExamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		context = parent.getContext();
		return new ExamViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_template_exam, parent, false));
	}

	@SuppressLint("NotifyDataSetChanged")
	@Override
	public void onBindViewHolder(@NonNull ExamViewHolder holder, @SuppressLint("RecyclerView") int position) {
		holder.setTemplateView(context, viewType);
		if (position % 2 == 0) {
			GridLayoutManager.LayoutParams params = (GridLayoutManager.LayoutParams) holder.examCard.getLayoutParams();
			params.leftMargin = (int) convertDpToPx(3f);
			holder.examCard.setLayoutParams(params);
		}
		setTexts(holder, position);
		onClicks(holder, position);
		if (exams.get(position).isLoading()) {
			holder.onMainButtonClicked(context);
		}
	}

	private float convertDpToPx(float dp) {
		return TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP,
				dp,
				context.getResources().getDisplayMetrics()
		);
	}

	private void onClicks(ExamViewHolder holder, int position) {
		holder.examAction.setOnClickListener(view -> {
			if (viewType == ExamViewHolder.TEMPLATE_VIEW_CORRECTING_EXAMS) {
				exams.get(position).setLoading(true);
				holder.onMainButtonClicked(context);
				loadExamAsync(position, WORK_RECENT_EXAM_CLICKED);
			} else if (viewType == ExamViewHolder.TEMPLATE_VIEW_CREATING_EXAMS) {
				exams.get(position).setLoading(true);
				holder.onMainButtonClicked(context);
				loadExamAsync(position, WORK_CURRENT_EXAM_CLICKED);
			} else if (viewType == ExamViewHolder.TEMPLATE_VIEW_RUNNING_EXAMS) {
				resumeSelectedExam(holder, position);
			} else if (viewType == ExamViewHolder.TEMPLATE_VIEW_SUSPENDED_EXAMS) {
				restartSelectedExam(holder, position);
			}
			exams.get(position).setReviewCount(exams.get(position).getReviewCount() + 1);
			updateRecentExams(position);

		});
		if (viewType == ExamViewHolder.TEMPLATE_VIEW_FINISHED_EXAMS && !exams.get(position).isStarted() && !exams.get(position).isCreating() &&
				!exams.get(position).isSuspended() && !exams.get(position).isCorrecting()) {
			holder.viewRecentExam.setOnClickListener(view -> {
				exams.get(position).setReviewCount(exams.get(position).getReviewCount() + 1);
				exams.get(position).setLoading(true);
				updateRecentExams(position);
				holder.onMainButtonClicked(context);
				loadExamAsync(position, WORK_RECENT_EXAM_CLICKED);
			});
		}
		holder.setExamFavoriteIconView(context, exams.get(position).isFavorite());
		holder.addToFavorite.setOnClickListener(view -> {
			exams.get(position).setFavorite(!exams.get(position).isFavorite());
			holder.setExamFavoriteIconView(context, exams.get(position).isFavorite());
			updateRecentExams(position);
		});
		if (!exams.get(position).isSuspended()) {
			holder.editExamFeatures.setOnClickListener(view -> editSelectedExam(position));
		}
		if (!exams.get(position).isStarted() || exams.get(position).isSuspended() ||
				exams.get(position).isCreating() || exams.get(position).isCorrecting()) {
			holder.deleteExam.setOnClickListener(v1 -> deleteSelectedExam(holder, position));
		}
		if (exams.get(position).isStarted() && !exams.get(position).isSuspended() && !exams.get(position).isCreating()) {
			holder.suspendExam.setOnClickListener(view -> suspendSelectedExam(holder, position));
		}
		if (!exams.get(position).isStarted() && !exams.get(position).isSuspended() &&
				!exams.get(position).isCorrecting() && !exams.get(position).isCreating()) {
			holder.resetExam.setOnClickListener(view -> {
				exams.get(position).setReviewCount(exams.get(position).getReviewCount() + 1);
				updateRecentExams(position);
				restartSelectedExam(holder, position);
			});
		}
		if (exams.get(position).isUsedTiming() && exams.get(position).getExamTime() != 0 && exams.get(position).isStarted()) {
			holder.showExamRemainingTime(context, exams.get(position).getExamTime(), exams.get(position).getExamTimeLeft());
		}
		holder.examInfo.setOnClickListener(view -> holder.showExamInfoDialog(context, (FragmentActivity) activity, exams.get(position)));
	}

	private void setTexts(ExamViewHolder holder, int position) {
		holder.examName.setSelected(true);
		holder.examName.setText(exams.get(position).getExamName(0).getName());
	}

	private void resumeSelectedExam(ExamViewHolder holder, int position) {
		MaterialAlertDialog builder = new MaterialAlertDialog(context);
		builder.setIcon(R.drawable.continue_exam);
		builder.setTitle("ادامه دادن آزمون " + exams.get(position).getExamName(0).getName());
		builder.setMessage("آیا می خواهید این آزمون را ادامه دهید؟");
		builder.setPositiveButton("بله", v4 -> {
			builder.dismiss(builder);
			holder.onMainButtonClicked(context);
			exams.get(position).setLoading(true);
			updateRecentExams(position);
			loadExamAsync(position, WORK_EXAM_RESUMED);
		});
		builder.setNegativeButton("خیر", v4 -> builder.dismiss(builder));
		builder.show((FragmentActivity) activity);
	}

	private void editSelectedExam(int position) {
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

	private void suspendSelectedExam(ExamViewHolder holder, int position) {
		MaterialAlertDialog builder = new MaterialAlertDialog(context);
		builder.setIcon(R.drawable.suspend_exam);
		builder.setTitle("معلق کردن آزمون " + exams.get(position).getExamName());
		builder.setMessage("آیا مطمئن هستید که می خواهید این آزمون را معلق کنید؟\n\nامکان شروع مجدد این آزمون وجود خواهد داشت؛ اما گزینه ها و سایر اطلاعات از بین خواهد رفت!");
		builder.setPositiveButton("بله", v4 -> {
			builder.dismiss(builder);
			holder.examProgressBar.setVisibility(View.GONE);
			exams.get(position).setSuspended(true);
			exams.get(position).setStarted(false);
			Exams examsL = Saver.getInstance(context).loadRecentExams();
			for (Exam exam : examsL.getExamList()) {
				if (exam.getId() == exams.get(position).getId()) {
					exam.setStarted(false);
					exam.setSuspended(true);
					exam.setSecondsOfThinkingOnQuestion(0);
				}
			}
			Saver.getInstance(context).saveRecentExams(examsL);
			notifyDataSetChanged();
			//ir.saltech.answersheet.object.data.Activity.setCurrentActivity(context, ir.saltech.answersheet.object.data.Activity.ACTIVITY_TYPE_EXAM_SUSPENDED, exams.get(position).getExamName(), 0, 0);
			examSelectedListener.onExamSuspended(exams.get(position));
		});
		builder.setNegativeButton("خیر", v4 -> builder.dismiss(builder));
		builder.show((FragmentActivity) activity);
	}

	private void restartSelectedExam(ExamViewHolder holder, int position) {
		MaterialAlertDialog builder = new MaterialAlertDialog(context);
		builder.setIcon(R.drawable.reset_exam);
		builder.setTitle("شروع مجدد آزمون " + exams.get(position).getExamName());
		builder.setMessage("آیا می خواهید این آزمون را از ابتدا آغاز کنید؟");
		builder.setPositiveButton("بله", v4 -> {
			builder.dismiss(builder);
			holder.onMainButtonClicked(context);
			exams.get(position).setSuspended(true);
			exams.get(position).setStarted(false);
			exams.get(position).setLoading(true);
			loadExamAsync(position, WORK_EXAM_RESUMED);
			updateRecentExams(position);
		});
		builder.setNegativeButton("خیر", v4 -> builder.dismiss(builder));
		builder.show((FragmentActivity) activity);
	}

	private void loadExamAsync(int position, int workType) {
		new Handler().postDelayed(() -> {
				switch (workType) {
					case WORK_EXAM_RESUMED:
						examSelectedListener.onExamResumed(exams.get(position));
						break;
					case WORK_CURRENT_EXAM_CLICKED:
						examSelectedListener.onExamClicked(exams.get(position), MainActivity.SIDE_CURRENT_EXAMS);
						break;
					case WORK_RECENT_EXAM_CLICKED:
						examSelectedListener.onExamClicked(exams.get(position), MainActivity.SIDE_RECENT_EXAMS);
						break;
					default: break;
				}
		}, LOAD_EXAM_DELAY_MILLIS);
	}

	private void deleteSelectedExam(ExamViewHolder holder, int position) {
		holder.examProgressLayout.setVisibility(View.GONE);
		MaterialAlertDialog builder = new MaterialAlertDialog(context);
		builder.setIcon(R.drawable.delete);
		builder.setTitle("حذف آزمون " + exams.get(position).getExamName());
		builder.setMessage("آیا مطمئن هستید که می خواهید این آزمون را حذف کنید؟\n\nامکان بازگشت دیگر وجود نخواهد داشت!");
		builder.setPositiveButton("بله", v6 -> {
			builder.dismiss(builder);
			Exams exams = Saver.getInstance(context).loadRecentExams();
			Exam removeExam = this.exams.get(position);
			for (int i = 0; i < exams.getExamList().size(); i++) {
				if (exams.getExamList().get(i).getId() == removeExam.getId()) {
					this.exams.remove(position);
					exams.removeExam(i);
					Saver.getInstance(context).saveRecentExams(exams);
					examSelectedListener.onExamDeleted(removeExam, position, MainActivity.SIDE_RECENT_EXAMS);
					Toast.makeText(context, "آزمون «" + removeExam.getExamName() + "» با موفقیت حذف شد.", Toast.LENGTH_SHORT).show();
					notifyDataSetChanged();
					break;
				}
			}
		});
		builder.setNegativeButton("خیر", v4 -> builder.dismiss(builder));
		builder.show((FragmentActivity) activity);
	}

	private void updateRecentExams(int position) {
		Exams recentExams = Saver.getInstance(context).loadRecentExams();
		recentExams.updateCurrentExam(exams.get(position));
		Saver.getInstance(context).saveRecentExams(recentExams);
		notifyDataSetChanged();
	}

	@Override
	public int getItemViewType(int position) {
		return position;
	}

	@Override
	public int getItemCount() {
		return exams.size();
	}
}
