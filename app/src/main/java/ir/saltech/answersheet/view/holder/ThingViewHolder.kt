package ir.saltech.answersheet.view.holder

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ir.saltech.answersheet.R

class ThingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var thingLayout: LinearLayout? = null
    var thingIcon: ImageView? = null
    var thingText: TextView? = null

    init {
        init(itemView)
    }

    private fun init(v: View) {
        thingLayout = v.findViewById<LinearLayout>(R.id.row_thing_layout)
        thingIcon = v.findViewById<ImageView>(R.id.row_thing_icon)
        thingText = v.findViewById<TextView>(R.id.row_thing_text)
    }
}
