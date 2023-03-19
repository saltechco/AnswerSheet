package ir.saltech.answersheet.view.holder;

import static android.graphics.PorterDuff.Mode.SRC_IN;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Locale;

import ir.saltech.answersheet.R;
import ir.saltech.answersheet.object.data.Exam;
import ir.saltech.answersheet.view.container.MaterialAlertDialog;

public class ExamViewHolder extends RecyclerView.ViewHolder {
	public static final int TEMPLATE_VIEW_RUNNING_EXAMS = 1;
	public static final int TEMPLATE_VIEW_SUSPENDED_EXAMS = 2;
	public static final int TEMPLATE_VIEW_FINISHED_EXAMS = 3;
	public static final int TEMPLATE_VIEW_CREATING_EXAMS = 4;
	public static final int TEMPLATE_VIEW_CORRECTING_EXAMS = 5;
	private static final int TWO_DIGIT_NUM = 10;
	private static final int MAX_OF_NORMAL_PROGRESS = 58;

	public CardView examCard;
	public TextView examName;
	public ConstraintLayout examProgressLayout;
	public ProgressBar examProgressBar;
	public TextView examRemainingTime;
	public Button examAction;
	public ImageButton viewRecentExam;
	public ImageButton addToFavorite;
	public ImageButton resetExam;
	public ImageButton examInfo;
	public ImageButton deleteExam;
	public ImageButton suspendExam;
	public ImageButton editExamFeatures;


	public ExamViewHolder(@NonNull View itemView) {
		super(itemView);
		init(itemView);
	}

	private void init(View v) {
		examCard = v.findViewById(R.id.exam_card);
		examName = v.findViewById(R.id.exam_name);
		examProgressBar = v.findViewById(R.id.load_progress_bar);
		examRemainingTime = v.findViewById(R.id.remaining_time);
		examProgressLayout = v.findViewById(R.id.load_progress_bar_layout);
		viewRecentExam = v.findViewById(R.id.recent_exam_view);
		addToFavorite = v.findViewById(R.id.add_to_favorite);
		examAction = v.findViewById(R.id.exam_action);
		resetExam = v.findViewById(R.id.reset_exam);
		examInfo = v.findViewById(R.id.exam_info);
		suspendExam = v.findViewById(R.id.exam_suspend);
		deleteExam = v.findViewById(R.id.exam_delete);
		editExamFeatures = v.findViewById(R.id.exam_edit_features);
	}

	private String printTime(int second, int minute) {
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

	public void showExamRemainingTime(@NonNull Context context, long total, long now) {
		if (total > now) {
			int progress = (int) ((((float) now) / ((float) total)) * 100f);
			examProgressBar.setVisibility(View.VISIBLE);
			examProgressBar.setIndeterminate(false);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
				examProgressBar.setProgress(progress, true);
			} else {
				examProgressBar.setProgress(progress);
			}
			long[] nowTime = {now / 60000, now % 60000 / 1000};
			printExamTime(context, progress, nowTime);
			examRemainingTime.setVisibility(View.VISIBLE);
			examProgressLayout.setVisibility(View.VISIBLE);
		}
	}

	private void printExamTime(Context context, int progress, long[] nowTime) {
		examRemainingTime.setText(printTime((int) nowTime[1], (int) nowTime[0]));
		if (progress >= MAX_OF_NORMAL_PROGRESS) {
			examRemainingTime.setTextColor(context.getResources().getColor(R.color.exam_remaining_time));
		} else {
			examRemainingTime.setTextColor(context.getResources().getColor(R.color.exam_remaining_time_2));
		}
	}

	@SuppressLint("DefaultLocale")
	public void showExamInfoDialog(Context context, FragmentActivity activity, Exam currentExam) {
		if (currentExam != null) {
			MaterialAlertDialog dialog = new MaterialAlertDialog(context);
			dialog.setIcon(R.drawable.app_info);
			dialog.setTitle(context.getString(R.string.exam_name_text, currentExam.getExamName()));
			String time = null;
			if (currentExam.isUsedTiming()) {
				time = printTime((int) currentExam.getExamTime() % 60000, (int) currentExam.getExamTime() / 60000);
			}
			dialog.setMessage(((time != null && !currentExam.isCreating()) ? (String.format("زمان آزمون: %s", time) + "\n") : "") + String.format("تعداد تست: %d", currentExam.getExamQuestionsRange()[2] + 1) + ((currentExam.isUsedCategorize()) ? "\n" + "سؤالات دسته بندی شده" : "") + "\n" + String.format("زمان شروع: %s", currentExam.getStartExamTime()));
			dialog.setPositiveButton("متوجه شدم", v -> dialog.dismiss(dialog));
			dialog.setCancelable(false);
			dialog.show(activity);
		}
	}

	public void setExamFavoriteIconView(@NonNull Context context, boolean favorite) {
		if (favorite) {
			addToFavorite.setImageResource(R.drawable.favorite_exam);
			addToFavorite.getDrawable().setColorFilter(context.getResources().getColor(R.color.added_to_favorite), SRC_IN);
			addToFavorite.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.added_to_favorite)));
		} else {
			addToFavorite.setImageResource(R.drawable.not_favorite);
			addToFavorite.getDrawable().setColorFilter(context.getResources().getColor(R.color.disable_button), SRC_IN);
			addToFavorite.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.disable_button)));
		}
	}

	public void onMainButtonClicked(@NonNull Context context) {
		if (viewRecentExam.getVisibility() != View.VISIBLE) {
			examAction.setText(R.string.exam_loading);
			examAction.setClickable(false);
			examAction.setTextColor(context.getResources().getColor(R.color.disable_button));
			examAction.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.disable_button)));
		} else {
			viewRecentExam.setClickable(false);
			viewRecentExam.getDrawable().setColorFilter(context.getResources().getColor(R.color.disable_button), SRC_IN);
			viewRecentExam.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.disable_button)));
		}
		examProgressBar.setIndeterminate(true);
		examRemainingTime.setVisibility(View.GONE);
		examProgressLayout.setVisibility(View.VISIBLE);
	}

	public void setTemplateView(@NonNull Context context, int viewType) {
		examAction.setTextColor(context.getResources().getColor(R.color.colorAccent));
		examAction.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorAccent)));
		switch (viewType) {
			case TEMPLATE_VIEW_RUNNING_EXAMS:
				examAction.setText(R.string.resume_running_exam);
				viewRecentExam.setVisibility(View.GONE);
				resetExam.setVisibility(View.GONE);
				deleteExam.setVisibility(View.GONE);
				suspendExam.setVisibility(View.VISIBLE);
				break;
			case TEMPLATE_VIEW_SUSPENDED_EXAMS:
				examAction.setText(R.string.restart_exam);
				viewRecentExam.setVisibility(View.GONE);
				resetExam.setVisibility(View.GONE);
				deleteExam.setVisibility(View.VISIBLE);
				editExamFeatures.setVisibility(View.GONE);
				suspendExam.setVisibility(View.GONE);
				break;
			case TEMPLATE_VIEW_FINISHED_EXAMS:
				examAction.setText("NONE");
				examAction.setVisibility(View.GONE);
				viewRecentExam.setVisibility(View.VISIBLE);
				resetExam.setVisibility(View.VISIBLE);
				deleteExam.setVisibility(View.VISIBLE);
				editExamFeatures.setVisibility(View.VISIBLE);
				suspendExam.setVisibility(View.GONE);
				break;
			case TEMPLATE_VIEW_CREATING_EXAMS:
				examAction.setText(R.string.create_exam);
				viewRecentExam.setVisibility(View.GONE);
				resetExam.setVisibility(View.GONE);
				deleteExam.setVisibility(View.VISIBLE);
				editExamFeatures.setVisibility(View.VISIBLE);
				suspendExam.setVisibility(View.GONE);
				break;
			case TEMPLATE_VIEW_CORRECTING_EXAMS:
				examAction.setText(R.string.correct_exam);
				viewRecentExam.setVisibility(View.GONE);
				resetExam.setVisibility(View.GONE);
				deleteExam.setVisibility(View.VISIBLE);
				editExamFeatures.setVisibility(View.VISIBLE);
				suspendExam.setVisibility(View.GONE);
				break;
			default:
				break;
		}
	}
}
