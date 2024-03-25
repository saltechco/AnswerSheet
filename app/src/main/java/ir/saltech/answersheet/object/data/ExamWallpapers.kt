package ir.saltech.answersheet.object.data;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import ir.saltech.answersheet.R;
import ir.saltech.answersheet.object.enums.WallpaperType;

public class ExamWallpapers {
    private List<ExamWallpaper> wallpapers;

    public ExamWallpapers() {
        this.wallpapers = new ArrayList<>();
    }

    public ExamWallpapers(Context context) {
        this.wallpapers = new ArrayList<>();
        setDefaultWallpapers(context, wallpapers);
    }

    public ExamWallpapers(List<ExamWallpaper> wallpapers) {
        this.wallpapers = wallpapers;
    }

    public List<ExamWallpaper> getWallpapers() {
        return wallpapers;
    }

    public void setWallpapers(List<ExamWallpaper> wallpapers) {
        this.wallpapers = wallpapers;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public static void setDefaultWallpapers(Context context, @NonNull List<ExamWallpaper> wallpapers) {
        wallpapers.add(new ExamWallpaper(WallpaperType.Picture, false));
        wallpapers.add(new ExamWallpaper(WallpaperType.Picture, false));
        wallpapers.add(new ExamWallpaper(WallpaperType.Picture, false));
        wallpapers.add(new ExamWallpaper(WallpaperType.Picture, false));
        wallpapers.add(new ExamWallpaper(WallpaperType.Picture, false));
        wallpapers.add(new ExamWallpaper(WallpaperType.Picture, true));
        wallpapers.add(new ExamWallpaper(WallpaperType.Animation, false));
        wallpapers.add(new ExamWallpaper(WallpaperType.Animation, false));
        wallpapers.add(new ExamWallpaper(WallpaperType.Animation, false));
        wallpapers.add(new ExamWallpaper(WallpaperType.Animation, false));
        wallpapers.add(new ExamWallpaper(WallpaperType.Animation, false));
        wallpapers.add(new ExamWallpaper(WallpaperType.Animation, false));
        wallpapers.add(new ExamWallpaper(WallpaperType.Animation, false));
        wallpapers.add(new ExamWallpaper(WallpaperType.Animation, false));
        wallpapers.add(new ExamWallpaper(WallpaperType.Animation, false));
    }

//    @SuppressLint("UseCompatLoadingForDrawables")
//    public static void setDefaultWallpapers(Context context, @NonNull List<ExamWallpaper> wallpapers) {
//        wallpapers.add(new ExamWallpaper(context.getResources().getDrawable(R.drawable.wallpaper), WallpaperType.Picture, false));
//        wallpapers.add(new ExamWallpaper(context.getResources().getDrawable(R.drawable.wallpaper2), WallpaperType.Picture, false));
//        wallpapers.add(new ExamWallpaper(context.getResources().getDrawable(R.drawable.wallpaper3), WallpaperType.Picture, false));
//        wallpapers.add(new ExamWallpaper(context.getResources().getDrawable(R.drawable.wallpaper4), WallpaperType.Picture, false));
//        wallpapers.add(new ExamWallpaper(context.getResources().getDrawable(R.drawable.wallpaper5), WallpaperType.Picture, false));
//        wallpapers.add(new ExamWallpaper(context.getResources().getDrawable(R.drawable.wallpaper6), WallpaperType.Picture, true));
//        //wallpapers.add(new ExamWallpaper(null, WallpaperType.Picture, false));
//        wallpapers.add(new ExamWallpaper(R.raw.turning_wave, WallpaperType.Animation, false));
//        wallpapers.add(new ExamWallpaper(R.raw.turning_wave2, WallpaperType.Animation, false));
//        wallpapers.add(new ExamWallpaper(R.raw.turning_wave3, WallpaperType.Animation, false));
//        wallpapers.add(new ExamWallpaper(R.raw.turning_wave4, WallpaperType.Animation, false));
//        wallpapers.add(new ExamWallpaper(R.raw.turning_wave5, WallpaperType.Animation, false));
//        wallpapers.add(new ExamWallpaper(R.raw.turning_wave6, WallpaperType.Animation, false));
//        wallpapers.add(new ExamWallpaper(R.raw.turning_wave7, WallpaperType.Animation, false));
//        wallpapers.add(new ExamWallpaper(R.raw.turning_wave8, WallpaperType.Animation, false));
//        wallpapers.add(new ExamWallpaper(R.raw.turning_wave9, WallpaperType.Animation, false));
//    }

    public void updateWallpaper(@NonNull ExamWallpaper currentWallpaper, int position) {
        wallpapers.remove(position);
        wallpapers.add(position, currentWallpaper);
    }


    @Override
    public String toString() {
        return "ExamWallpapers{" +
                "wallpapers=" + wallpapers +
                '}';
    }

    public void addWallpaper(ExamWallpaper wallpaper) {
        wallpapers.add(wallpaper);
    }
}
