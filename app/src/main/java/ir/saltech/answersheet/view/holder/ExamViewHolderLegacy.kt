package ir.saltech.answersheet.view.holder

import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import ir.saltech.answersheet.R

class ExamViewHolderLegacy(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var examCard: CardView? = null
    var examName: TextView? = null
    var examStartedTime: TextView? = null
    var examProgressBar: ProgressBar? = null
    var examOptions: ImageButton? = null
    var examImage: ImageView? = null

    init {
        init(itemView)
    }

    private fun init(v: View) {
        examCard = v.findViewById<CardView>(R.id.exam_card)
        examName = v.findViewById<TextView>(R.id.exam_name_text)
        examStartedTime = v.findViewById<TextView>(R.id.exam_started_time)
        examImage = v.findViewById<ImageView>(R.id.exam_image)
        examOptions = v.findViewById<ImageButton>(R.id.exam_options)
        examProgressBar = v.findViewById<ProgressBar>(R.id.load_exam_bar)
    }
}
