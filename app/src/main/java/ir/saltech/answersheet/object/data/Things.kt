package ir.saltech.answersheet.`object`.data

import android.util.Log

open class Things {
    private val things: MutableList<Thing> = ArrayList()

    protected fun convertToThings(target: List<*>): List<Thing> {
        Log.i("TAG", "TAJ " + target.size)
        var i = 0
        while (i < target.size && target.size != 0) {
            addThing(target[i] as Thing)
            i++
        }
        return things
    }

    fun addThing(asThing: Thing) {
        things.add(asThing)
    }

    fun removeThing(index: Int) {
        things.removeAt(index)
    }

    open fun clearList() {
        things.clear()
    }

    override fun toString(): String {
        return "Things{" +
                "things=" + things +
                '}'
    }
}
