package ir.saltech.answersheet.`object`.data

import com.google.gson.annotations.SerializedName
import java.io.File

@Deprecated("We don't use feedback")
class Feedback {
    var id: Long = 0
    var subject: String? = null
    var description: String? = null
    var email: String? = null
    var date: String? = null
    var screenshot: File? = null

    @SerializedName("mode")
    var mode: String = MODE_PROBLEM

    @SerializedName("target_id")
    var targetId: Int = 0

    @SerializedName("target_ver")
    var targetVersion: Int = 0
    var os: String? = null
    var device: String? = null
    var solution: String? = null
    var isSolved: Boolean = false

    override fun toString(): String {
        return "Feedback{" +
                "id=" + id +
                ", subject='" + subject + '\'' +
                ", description='" + description + '\'' +
                ", email='" + email + '\'' +
                ", date='" + date + '\'' +
                ", mode='" + mode + '\'' +
                ", targetId=" + targetId +
                ", targetVersion=" + targetVersion +
                ", os='" + os + '\'' +
                ", device='" + device + '\'' +
                ", solution='" + solution + '\'' +
                ", solved=" + isSolved +
                '}'
    }

    companion object {
        const val MODE_PROBLEM: String = "problem"
        const val MODE_SUGGESTION: String = "suggestion"
    }
}
