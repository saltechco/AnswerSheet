package ir.saltech.answersheet.view.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ir.saltech.answersheet.R
import ir.saltech.answersheet.intf.listener.ThingSelectedListener
import ir.saltech.answersheet.`object`.data.Thing
import ir.saltech.answersheet.view.adapter.SelectThingViewAdapter
import ir.saltech.answersheet.view.container.MaterialFragmentShower

class SelectThingsDialog(
    private val shower: MaterialFragmentShower,
    private val thingsList: List<Thing?>,
    private val thingType: String,
    private val dialogOpened: Boolean,
    private val thingSelectedListener: ThingSelectedListener
) : Fragment() {
    private var things: RecyclerView? = null
    private var dialogTitle: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return if (dialogOpened) inflater.inflate(
            R.layout.dialog_select_thing_match,
            container,
            false
        )
        else inflater.inflate(R.layout.dialog_select_thing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        dialogTitle!!.text = getString(R.string.select_thing_title, thingType)
        val cva =
            SelectThingViewAdapter(thingsList, object : ThingSelectedListener {
                override fun onSelected(thing: Thing?) {
                    thingSelectedListener.onSelected(thing)
                    if (dialogOpened) dismiss()
                    else shower.dismiss(shower)
                }
            })
        things!!.setLayoutManager(LinearLayoutManager(context, RecyclerView.VERTICAL, false))
        things!!.setAdapter(cva)
    }

    private fun dismiss() {
        shower.hasContent = (false)
        requireActivity().supportFragmentManager.beginTransaction().remove(this).commit()
    }

    override fun onPause() {
        super.onPause()
        if (dialogOpened) shower.hasContent = (false)
    }

    override fun onResume() {
        super.onResume()
        if (dialogOpened) shower.hasContent = (true)
    }

    private fun init(v: View) {
        dialogTitle = v.findViewById<TextView>(R.id.select_thing_title)
        things = v.findViewById<RecyclerView>(R.id.things_view_selection)
    }
}
