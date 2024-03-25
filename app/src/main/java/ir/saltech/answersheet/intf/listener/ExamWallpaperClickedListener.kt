package ir.saltech.answersheet.intf.listener

import ir.saltech.answersheet.`object`.data.ExamWallpaper

interface ExamWallpaperClickedListener {
    fun onClicked(wallpaper: ExamWallpaper?, position: Int)
}
