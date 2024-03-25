package ir.saltech.answersheet.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.saltech.answersheet.R
import ir.saltech.answersheet.intf.listener.ThingSelectedListener
import ir.saltech.answersheet.`object`.data.Thing
import ir.saltech.answersheet.view.holder.ThingViewHolder

class SelectThingViewAdapter(
    private val thingsList: List<Thing?>,
    private val thingSelectedListener: ThingSelectedListener
) : RecyclerView.Adapter<ThingViewHolder?>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThingViewHolder {
        return ThingViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.view_template_thing, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ThingViewHolder, position: Int) {
        holder.thingText!!.setSelected(true)
        holder.thingIcon!!.setImageResource(thingsList[position]!!.iconResource)
        holder.thingText!!.text = thingsList[position]!!.title
        holder.thingLayout!!.setOnClickListener { v: View? ->
            thingSelectedListener.onSelected(
                thingsList[position]
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount() = thingsList.size
}
