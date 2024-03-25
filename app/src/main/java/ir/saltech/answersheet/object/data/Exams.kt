package ir.saltech.answersheet.`object`.data

class Exams : Things {
    private var examList: MutableList<Exam>

    constructor() {
        examList = ArrayList()
    }

    constructor(examList: MutableList<Exam>) {
        this.examList = examList
    }

    val things: List<Thing?>?
        get() = super.convertToThings(examList)

    fun getExamList(): List<Exam> {
        return examList
    }

    fun setExamList(examList: MutableList<Exam>) {
        this.examList = examList
    }

    fun updateCurrentExam(currentExam: Exam) {
        for (i in examList.indices) {
            if (examList[i].id == currentExam.id) {
                removeExam(i)
                examList.add(i, currentExam)
                break
            }
        }
    }

    fun addExam(newExam: Exam) {
        examList.add(newExam)
    }

    fun removeExam(examIndex: Int) {
        examList.removeAt(examIndex)
    }

    override fun clearList() {
        examList.clear()
    }

    override fun toString(): String {
        return "Exams{" +
                "examList=" + examList +
                '}'
    }
}
