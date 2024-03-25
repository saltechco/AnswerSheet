package ir.saltech.answersheet.view.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ir.saltech.answersheet.R;
import ir.saltech.answersheet.intf.listener.ThingSelectedListener;
import ir.saltech.answersheet.object.data.Thing;
import ir.saltech.answersheet.view.adapter.SelectThingViewAdapter;
import ir.saltech.answersheet.view.container.MaterialFragmentShower;

public class SelectThingsDialog extends Fragment {
    private final MaterialFragmentShower shower;
    private final ThingSelectedListener thingSelectedListener;
    private final String thingType;
    private final boolean dialogOpened;
    private final List<Thing> thingsList;
    private RecyclerView things;
    private TextView dialogTitle;

    public SelectThingsDialog(@NonNull MaterialFragmentShower shower, List<Thing> thingsList, String thingType, boolean dialogOpened, ThingSelectedListener thingSelectedListener) {
        this.shower = shower;
        this.thingsList = thingsList;
        this.thingType = thingType;
        this.dialogOpened = dialogOpened;
        this.thingSelectedListener = thingSelectedListener;
    }

    @Nullable

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (dialogOpened)
            return inflater.inflate(R.layout.dialog_select_thing_match, container, false);
        else
            return inflater.inflate(R.layout.dialog_select_thing, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        dialogTitle.setText(getString(R.string.select_thing_title, thingType));
        SelectThingViewAdapter cva = new SelectThingViewAdapter(thingsList, c -> {
            thingSelectedListener.onSelected(c);
            if (dialogOpened)
                dismiss();
            else
                shower.dismiss(shower);
        });
        this.things.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        this.things.setAdapter(cva);
    }

    private void dismiss() {
        shower.setHasContent(false);
        requireActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (dialogOpened)
            shower.setHasContent(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (dialogOpened)
            shower.setHasContent(true);
    }

    private void init(View v) {
        dialogTitle = v.findViewById(R.id.select_thing_title);
        things = v.findViewById(R.id.things_view_selection);
    }
}
