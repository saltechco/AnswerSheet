package ir.saltech.answersheet.object.data;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Contract;

public class Thing {
    public static String thingName;
    private String title;
    private int iconResId;

    @Contract(pure = true)
    public static String getThingName() {
        return thingName;
    }

    public static void setThingName(@NonNull String name) {
        thingName = name;
    }

    public int getIconResource() {
        return iconResId;
    }

    public void setIconResource(int iconResId) {
        this.iconResId = iconResId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @NonNull
    @Override
    public String toString() {
        return "Thing{" +
                "title='" + title + '\'' +
                ", icon=" + iconResId +
                '}';
    }
}
