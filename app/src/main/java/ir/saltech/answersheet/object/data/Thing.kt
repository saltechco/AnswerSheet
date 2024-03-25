package ir.saltech.answersheet.`object`.data

import org.jetbrains.annotations.Contract

open class Thing {
    var title: String? = null
    var iconResource: Int = 0

    override fun toString(): String {
        return "Thing{" +
                "title='" + title + '\'' +
                ", icon=" + iconResource +
                '}'
    }

    companion object {
        var thingName: String? = null
    }
}
