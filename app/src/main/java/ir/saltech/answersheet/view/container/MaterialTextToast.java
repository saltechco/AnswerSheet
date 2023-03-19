package ir.saltech.answersheet.view.container;

import android.content.Context;

import androidx.annotation.NonNull;

public class MaterialTextToast extends MaterialToast {
    private long duration;

    public MaterialTextToast(@NonNull Context context) {
        super(context);
        super.init(getContentView());
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void showToast() {
        if (duration != 0) {
            super.show(duration);
        }
    }
}
