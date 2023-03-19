package ir.saltech.answersheet.object.data;

import android.graphics.drawable.Drawable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ir.saltech.answersheet.object.adapter.DrawableAdapter;
import ir.saltech.answersheet.object.enums.WallpaperType;

public class ExamWallpaper {
    private WallpaperType type;
    private boolean selected;

    public ExamWallpaper(WallpaperType type, boolean selected) {
        this.type = type;
        this.selected = selected;
    }
//
//    public ExamWallpaper(WallpaperType type, boolean selected) {
//        this.type = type;
//        this.selected = selected;
//    }

//    public Drawable getDrawable() {
//        return gson;
//    }
//
//    public void setDrawable(Drawable drawable) {
//        this.drawable = drawable;
//    }
//
//    public int getRawResId() {
//        return rawResId;
//    }
//
//    public void setRawResId(int rawResId) {
//        this.rawResId = rawResId;
//    }

    public WallpaperType getType() {
        return type;
    }

    public void setType(WallpaperType type) {
        this.type = type;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public String toString() {
        return "ExamWallpaper{" +
                "type=" + type +
                ", selected=" + selected +
                '}';
    }
}
