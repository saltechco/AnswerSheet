package ir.saltech.answersheet.`object`.data

import com.google.gson.annotations.SerializedName

class Product {
    var id: Int = 0
    var name: String? = null

    @SerializedName("version_code")
    var versionCode: Long = 0

    @SerializedName("version_name")
    var versionName: String? = null

    @SerializedName("package_name")
    var packageName: String? = null
    var platform: String? = null
    var description: String? = null
    var changes: String? = null

    @SerializedName("file_size")
    var fileSize: Long = 0

    @SerializedName("release_date")
    private var releaseDate: String? = null
    var isReleased: Boolean = false
    var isActive: Boolean = false
    var isBeta: Boolean = false

    fun getReleaseDate(): String {
        return releaseDate!!
    }

    fun setReleaseDate(releaseDate: String) {
        this.releaseDate = releaseDate
    }

    fun isUpdateAvailable(versionCode: Int): Boolean {
        return this.versionCode > versionCode && !isBeta
    }

    fun isBetaUpdateAvailable(versionCode: Int): Boolean {
        return this.versionCode > versionCode && this.isBeta && (versionCode.toString().length == 10)
    }

    override fun toString(): String {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", versionCode=" + versionCode +
                ", versionName='" + versionName + '\'' +
                ", packageName='" + packageName + '\'' +
                ", platform='" + platform + '\'' +
                ", description='" + description + '\'' +
                ", changes='" + changes + '\'' +
                ", fileSize=" + fileSize +
                ", releaseDate='" + releaseDate + '\'' +
                ", released=" + isReleased +
                ", active=" + isActive +
                ", beta=" + isBeta +
                '}'
    }
}
