package ir.saltech.answersheet.`object`.data

import android.annotation.SuppressLint
import android.content.Context
import ir.saltech.answersheet.`object`.enums.WallpaperType

class ExamWallpapers {
    private var wallpapers: MutableList<ExamWallpaper>

    constructor() {
        this.wallpapers = ArrayList()
    }

    constructor(context: Context?) {
        this.wallpapers = ArrayList()
        setDefaultWallpapers(context, wallpapers)
    }

    constructor(wallpapers: MutableList<ExamWallpaper>) {
        this.wallpapers = wallpapers
    }

    fun getWallpapers(): List<ExamWallpaper> {
        return wallpapers
    }

    fun setWallpapers(wallpapers: MutableList<ExamWallpaper>) {
        this.wallpapers = wallpapers
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
    fun updateWallpaper(currentWallpaper: ExamWallpaper, position: Int) {
        wallpapers.removeAt(position)
        wallpapers.add(position, currentWallpaper)
    }


    override fun toString(): String {
        return "ExamWallpapers{" +
                "wallpapers=" + wallpapers +
                '}'
    }

    fun addWallpaper(wallpaper: ExamWallpaper) {
        wallpapers.add(wallpaper)
    }

    companion object {
        @SuppressLint("UseCompatLoadingForDrawables")
        fun setDefaultWallpapers(context: Context?, wallpapers: MutableList<ExamWallpaper>) {
            wallpapers.add(ExamWallpaper(WallpaperType.Picture, false))
            wallpapers.add(ExamWallpaper(WallpaperType.Picture, false))
            wallpapers.add(ExamWallpaper(WallpaperType.Picture, false))
            wallpapers.add(ExamWallpaper(WallpaperType.Picture, false))
            wallpapers.add(ExamWallpaper(WallpaperType.Picture, false))
            wallpapers.add(ExamWallpaper(WallpaperType.Picture, true))
            wallpapers.add(ExamWallpaper(WallpaperType.Animation, false))
            wallpapers.add(ExamWallpaper(WallpaperType.Animation, false))
            wallpapers.add(ExamWallpaper(WallpaperType.Animation, false))
            wallpapers.add(ExamWallpaper(WallpaperType.Animation, false))
            wallpapers.add(ExamWallpaper(WallpaperType.Animation, false))
            wallpapers.add(ExamWallpaper(WallpaperType.Animation, false))
            wallpapers.add(ExamWallpaper(WallpaperType.Animation, false))
            wallpapers.add(ExamWallpaper(WallpaperType.Animation, false))
            wallpapers.add(ExamWallpaper(WallpaperType.Animation, false))
        }
    }
}
