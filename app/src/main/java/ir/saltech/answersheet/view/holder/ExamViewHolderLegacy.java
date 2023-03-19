package ir.saltech.answersheet.view.holder;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import ir.saltech.answersheet.R;

public class ExamViewHolderLegacy extends RecyclerView.ViewHolder {

	public CardView examCard;
	public TextView examName;
	public TextView examStartedTime;
	public ProgressBar examProgressBar;
	public ImageButton examOptions;
	public ImageView examImage;

	public ExamViewHolderLegacy(@NonNull View itemView) {
		super(itemView);
		init(itemView);
	}

	private void init(View v) {
		examCard = v.findViewById(R.id.exam_card);
		examName = v.findViewById(R.id.exam_name_text);
		examStartedTime = v.findViewById(R.id.exam_started_time);
		examImage = v.findViewById(R.id.exam_image);
		examOptions = v.findViewById(R.id.exam_options);
		examProgressBar = v.findViewById(R.id.load_exam_bar);
	}
}
