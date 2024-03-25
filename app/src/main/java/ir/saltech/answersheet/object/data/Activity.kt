package ir.saltech.answersheet.`object`.data

import java.util.Date

class Activity : Thing() {
    var message: String? = null
    var submitDate: Date? = null

    override fun toString(): String {
        return "Activity{" +
                "message='" + message + '\'' +
                ", submitDate=" + submitDate +
                '}'
    }
}
