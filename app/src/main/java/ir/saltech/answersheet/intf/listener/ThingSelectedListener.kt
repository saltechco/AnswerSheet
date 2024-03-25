package ir.saltech.answersheet.intf.listener

import ir.saltech.answersheet.`object`.data.Thing

interface ThingSelectedListener {
    fun onSelected(thing: Thing?)
}
