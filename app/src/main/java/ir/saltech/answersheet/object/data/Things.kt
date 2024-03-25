package ir.saltech.answersheet.object.data;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class Things {
    private final List<Thing> things;

    public Things() {
        things = new ArrayList<>();
    }

    protected List<Thing> convertToThings(@NonNull List target) {
        Log.i("TAG", "TAJ " + target.size());
        for (int i = 0; i < target.size() && target.size() != 0; i++) {
            addThing((Thing) target.get(i));
        }
        return things;
    }

    public void addThing(Thing asThing) {
        things.add(asThing);
    }

    public void removeThing(int index) {
        things.remove(index);
    }

    public void clearList() {
        things.clear();
    }

    @NonNull
    @Override
    public String toString() {
        return "Things{" +
                "things=" + things +
                '}';
    }
}
