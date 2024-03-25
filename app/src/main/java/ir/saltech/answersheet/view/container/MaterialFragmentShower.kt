package ir.saltech.answersheet.view.container

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import ir.saltech.answersheet.R
import ir.saltech.answersheet.`object`.container.Saver

class MaterialFragmentShower : MaterialDialogFragment {
    private var context: Context? = null
    private var wanted: Fragment? = null
    var hasContent: Boolean = false
    var isLayoutMatchParent: Boolean = false
    private var cancelable = false
    private var parent: Fragment? = null
    private var contentFragment: Fragment? = null

    constructor() : super()

    constructor(context: Context) : super() {
        this.context = context
        Saver.Companion.getInstance(context)
            .dismissSide = (SIDE_FRAGMENT_SHOWER)
    }

    @SuppressLint("LogConditional")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i("TAG", "Fragment: $wanted Match Parent Wanted? $isLayoutMatchParent")
        super.setDismissReceiver(object : BroadcastReceiver() {
            @SuppressLint("SyntheticAccessor")
            override fun onReceive(context2: Context, intent: Intent) {
                if (Saver.Companion.getInstance(context2).dismissSide != null) {
                    if (Saver.Companion.getInstance(context2)
                            .dismissSide == SIDE_FRAGMENT_SHOWER
                    ) {
                        if (this@MaterialFragmentShower.hasContent) {
                            if (parent != null && contentFragment != null) activity!!.supportFragmentManager.beginTransaction()
                                .remove(
                                    contentFragment!!
                                ).remove(parent!!).add(R.id.dialog_content_frame, parent!!)
                                .addToBackStack(DIALOG_CONTENT_BACKSTACK)
                                .commit()
                            //requireActivity().getSupportFragmentManager().popBackStack(DIALOG_CONTENT_BACKSTACK, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        } else {
                            // TODO: Setup this.......
                            /*if (!wanted.toString().contains("SharingRequestDialog") && !wanted.toString().contains("EditCategoryDialog")) {
								sendAddingCategoryBroadcast(MainActivity.CONTINUE_TIME);
								if (!Saver.getInstance(getContext()).getBackupRestoringStatus() && !Saver.getInstance(getContext()).getBackupCreatingStatus()) {
									dismiss(MaterialFragmentShower.this);
								} else {
									Toast.makeText(getContext(), "امکان بازگشت، به دلیل انجام یک عملیات حساس، وجود ندارد!", Toast.LENGTH_LONG).show();
								}
							}*/
                        }
                    }
                }
            }
        })
        super.setContentView(wanted, isLayoutMatchParent)
        super.setCancelable(cancelable)
        show()
    }

    private fun sendAddingCategoryBroadcast(status: String) {
        // TODO: Setup this.........
        /*Intent intent = new Intent(MainActivity.CATEGORY_ADDING_RECEIVER_INTENT);
		intent.putExtra(MainActivity.CATEGORY_ADDING_RECEIVER_RESULT, status);
		LocalBroadcastManager.getInstance(context).sendBroadcast(intent);*/
    }

    fun isCancelable(): Boolean {
        return cancelable
    }

    public override fun setCancelable(cancelable: Boolean) {
        this.cancelable = cancelable
    }

    var fragment: Fragment?
        get() = wanted
        set(wanted) {
            this.wanted = wanted
        }

    fun show(activity: FragmentActivity, shower: MaterialFragmentShower?) {
        activity.supportFragmentManager.beginTransaction().add(R.id.fragment_container, shower!!)
            .addToBackStack(DIALOG_BACKSTACK).commit()
    }

    /**
     * Uses for set Nested Dialog Pages
     * @param activity FragmentActivity
     * @param fragment Wanted Dialog
     * @param currentFragment Current Dialog
     */
    fun setContentFragment(
        activity: FragmentActivity,
        fragment: Fragment?,
        currentFragment: Fragment?
    ) {
        this.parent = currentFragment
        this.contentFragment = fragment
        activity.supportFragmentManager.beginTransaction()
            .add(R.id.dialog_content_frame, fragment!!)
            .addToBackStack(DIALOG_CONTENT_BACKSTACK).commit()
    }
}
