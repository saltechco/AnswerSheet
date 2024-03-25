package ir.saltech.answersheet.`object`.data

import android.util.Log

class ExamNames : Things {
    private var examNames: MutableList<ExamName>

    constructor() {
        examNames = ArrayList()
    }

    constructor(examNames: MutableList<ExamName>) {
        this.examNames = examNames
    }

    fun getExamNames(): List<ExamName> {
        return examNames
    }

    fun setExamNames(examNames: MutableList<ExamName>) {
        this.examNames = examNames
    }

    val things: List<Thing?>?
        get() = super.convertToThings(examNames)

    fun addExamName(c: ExamName, position: Int) {
        examNames.add(position, c)
    }

    fun addExamName(c: ExamName) {
        examNames.add(c)
    }

    fun removeCourse(index: Int) {
        examNames.removeAt(index)
    }

    fun isCourseAvailable(c: ExamName): Boolean {
        var isExists = false
        for (cse in examNames) {
            Log.v("TAG", "com.saltechgroup. eee$cse")
            if (cse.getName() == c.getName()) {
                isExists = true
                break
            }
        }
        return !isExists
    }

    override fun toString(): String {
        return "Courses{" +
                "examNames=" + examNames +
                '}'
    }
}

