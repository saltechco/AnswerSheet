package ir.saltech.answersheet.`object`.data

class Activities : Things {
    private var activities: MutableList<Activity>

    constructor() {
        activities = ArrayList()
    }

    constructor(Activities: MutableList<Activity>) {
        this.activities = Activities
    }

    fun getActivities(): List<Activity> {
        return activities
    }

    val things: List<Thing?>?
        get() = super.convertToThings(activities)

    fun setActivities(Activities: MutableList<Activity>) {
        this.activities = Activities
    }

    fun addActivity(c: Activity, position: Int) {
        activities.add(position, c)
    }

    fun addActivity(c: Activity) {
        activities.add(c)
    }

    fun removeActivity(index: Int) {
        activities.removeAt(index)
    }

    override fun toString(): String {
        return "Activities{" +
                "Activities=" + activities +
                '}'
    }
}

