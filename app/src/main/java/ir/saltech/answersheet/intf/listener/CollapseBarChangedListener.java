package ir.saltech.answersheet.intf.listener;

import android.view.View;
import android.view.ViewGroup;

public interface CollapseBarChangedListener {
    void onClosed(View v, ViewGroup parent);

    void onCollapsed(View v, ViewGroup parent);
    void onFullscreen(View v, ViewGroup parent);
    void onRestored(View v, ViewGroup parent);
}
