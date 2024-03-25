package ir.saltech.answersheet.`object`.data

import ir.saltech.answersheet.R

class ExamName : Thing() {
    private var name: String? = null
    var scores: List<Double> = ArrayList()
    var examsDate: List<String> = ArrayList()
    var isOldScoresRecovered: Boolean = false
    var isOldExamsDateRecovered: Boolean = false

    init {
        Thing.Companion.thingName = "نام آزمون"
        super.iconResource = R.drawable.exam_name
    }

    fun getName(): String? {
        return name
    }

    fun setName(name: String) {
        this.name = name
        super.title = name
    }

    override fun toString(): String {
        return "Course{" +
                "name='" + name + '\'' +
                "scores='" + scores.toString() + '\'' +
                '}'
    }
}
