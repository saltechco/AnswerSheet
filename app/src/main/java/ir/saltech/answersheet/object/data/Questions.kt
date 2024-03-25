package ir.saltech.answersheet.`object`.data

class Questions : Things {
    var questions: MutableList<Question>? = null
    var categories: MutableList<Category>

    constructor(questions: MutableList<Question>?) {
        if (questions != null) {
            this.questions = questions
        } else {
            this.questions = ArrayList()
        }
        categories = ArrayList()
    }

    constructor() {
        questions = ArrayList()
        categories = ArrayList()
    }

    val questionThings: List<Thing?>?
        get() = super.convertToThings(questions!!)

    val categoryThings: List<Thing?>?
        get() = super.convertToThings(categories)

    override fun toString(): String {
        return "Questions{" +
                "answerSheet=" + questions +
                ", categories=" + categories +
                '}'
    }
}
