package ir.saltech.answersheet.view.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ir.saltech.answersheet.R;
import ir.saltech.answersheet.intf.listener.ThingSelectedListener;
import ir.saltech.answersheet.object.data.Thing;
import ir.saltech.answersheet.view.holder.ThingViewHolder;

public class SelectThingViewAdapter extends RecyclerView.Adapter<ThingViewHolder> {
    private final List<Thing> thingsList;
    private final ThingSelectedListener thingSelectedListener;

    public SelectThingViewAdapter(List<Thing> thingsList, ThingSelectedListener thingSelectedListener) {
        this.thingsList = thingsList;
        this.thingSelectedListener = thingSelectedListener;
    }

    @NonNull
    @Override
    public ThingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ThingViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_template_thing, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ThingViewHolder holder, int position) {
        holder.thingText.setSelected(true);
        holder.thingIcon.setImageResource(thingsList.get(position).getIconResource());
        holder.thingText.setText(thingsList.get(position).getTitle());
        holder.thingLayout.setOnClickListener(v -> thingSelectedListener.onSelected(thingsList.get(position)));
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return thingsList.size();
    }
}
