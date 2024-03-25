package ir.saltech.answersheet.view.container

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentActivity
import ir.saltech.answersheet.R
import ir.saltech.answersheet.`object`.container.Saver

class MaterialAlertDialog(private val context: Context) : MaterialDialogFragment() {
    private var icon: Drawable? = null
    private var title: String? = null
    private var message: String? = null
    private var primaryButtonTitle: String? = null
    private var secondaryButtonTitle: String? = null
    private var naturalButtonTitle: String? = null
    private var primaryClickListener: View.OnClickListener? = null
    private var secondaryClickListener: View.OnClickListener? = null
    private var naturalClickListener: View.OnClickListener? = null
    private var cancelable = false
    private var progressbarEnabled = false

    init {
        Saver.Companion.getInstance(requireContext())
            .dismissSide = (SIDE_ALERT_DIALOG)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        super.setDismissReceiver(object : BroadcastReceiver() {
            @SuppressLint("SyntheticAccessor")
            override fun onReceive(context: Context, intent: Intent) {
                if (Saver.Companion.getInstance(requireContext()).dismissSide != null) {
                    if (Saver.Companion.getInstance(requireContext())
                            .dismissSide == SIDE_ALERT_DIALOG
                    ) {
                        try {
                            dismiss(this@MaterialAlertDialog)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        })
        super.setIcon(icon)
        super.setTitle(title)
        super.setMessage(message)
        super.setPositiveButton(primaryButtonTitle, primaryClickListener)
        super.setNegativeButton(secondaryButtonTitle, secondaryClickListener)
        super.setNaturalButton(naturalButtonTitle, naturalClickListener)
        super.setCancelable(cancelable)
        super.showProgressBar(progressbarEnabled)
        super.show()
    }

    fun setProgressbarEnabled(progressbarEnabled: Boolean) {
        this.progressbarEnabled = progressbarEnabled
    }

    fun isCancelable(): Boolean {
        return cancelable
    }

    public override fun setCancelable(cancelable: Boolean) {
        this.cancelable = cancelable
    }

    fun getIcon(): Drawable? {
        return icon
    }

    public override fun setIcon(icon: Drawable?) {
        this.icon = icon
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun setIcon(resId: Int) {
        this.icon = context.resources.getDrawable(resId)
    }

    fun getTitle(): String? {
        return title
    }

    public override fun setTitle(title: String?) {
        this.title = title
    }

    fun getMessage(): String? {
        return message
    }

    public override fun setMessage(message: String?) {
        this.message = message
    }

    public override fun setPositiveButton(
        primaryButtonTitle: String?,
        primaryClickListener: View.OnClickListener?
    ) {
        this.primaryButtonTitle = primaryButtonTitle
        this.primaryClickListener = primaryClickListener
    }

    public override fun setNaturalButton(
        naturalButtonTitle: String?,
        naturalClickListener: View.OnClickListener?
    ) {
        this.naturalButtonTitle = naturalButtonTitle
        this.naturalClickListener = naturalClickListener
    }

    public override fun setNegativeButton(
        secondaryButtonTitle: String?,
        clickListener: View.OnClickListener?
    ) {
        this.secondaryButtonTitle = secondaryButtonTitle
        this.secondaryClickListener = clickListener
    }

    fun show(activity: FragmentActivity) {
        activity.supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, this@MaterialAlertDialog)
            .addToBackStack(DIALOG_BACKSTACK).commit()
    }
}
