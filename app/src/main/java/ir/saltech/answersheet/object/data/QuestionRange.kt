package ir.saltech.answersheet.`object`.data

class QuestionRange {
    var firstQuestion: Int
    var lastQuestion: Int = 0
    var questionsCount: Int
    var countPattern: Int

    var range: IntArray

    constructor() {
        this.firstQuestion = 1
        this.questionsCount = 10
        this.countPattern = 1
        range = IntArray(4)
    }

    constructor(questionsCount: Int, lastQuestion: Int, firstQuestion: Int, countPattern: Int) {
        this.firstQuestion = firstQuestion
        this.lastQuestion = lastQuestion
        this.questionsCount = questionsCount
        this.countPattern = countPattern
        range = intArrayOf(firstQuestion, lastQuestion, questionsCount, countPattern)
    }

    override fun toString(): String {
        return "QuestionRange{" +
                "firstQuestion=" + firstQuestion +
                ", lastQuestion=" + lastQuestion +
                ", questionsCount=" + questionsCount +
                ", countPattern=" + countPattern +
                '}'
    }
}
