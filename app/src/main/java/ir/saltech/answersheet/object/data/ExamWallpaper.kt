package ir.saltech.answersheet.`object`.data

import ir.saltech.answersheet.`object`.enums.WallpaperType

class ExamWallpaper(type: WallpaperType, selected: Boolean) {
    private var type: WallpaperType
    var isSelected: Boolean

    init {
        this.type = type
        this.isSelected = selected
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
    fun getType(): WallpaperType {
        return type
    }

    fun setType(type: WallpaperType) {
        this.type = type
    }

    override fun toString(): String {
        return "ExamWallpaper{" +
                "type=" + type +
                ", selected=" + isSelected +
                '}'
    }
}
