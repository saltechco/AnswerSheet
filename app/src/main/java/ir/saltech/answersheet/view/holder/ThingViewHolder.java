package ir.saltech.answersheet.view.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ir.saltech.answersheet.R;

public class ThingViewHolder extends RecyclerView.ViewHolder {
    public LinearLayout thingLayout;
    public ImageView thingIcon;
    public TextView thingText;

    public ThingViewHolder(@NonNull View itemView) {
        super(itemView);
        init(itemView);
    }

    private void init(View v) {
        thingLayout = v.findViewById(R.id.row_thing_layout);
        thingIcon = v.findViewById(R.id.row_thing_icon);
        thingText = v.findViewById(R.id.row_thing_text);
    }
}
