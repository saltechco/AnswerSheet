package ir.saltech.answersheet.view.container

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import eightbitlab.com.blurview.BlurView
import eightbitlab.com.blurview.RenderEffectBlur
import eightbitlab.com.blurview.RenderScriptBlur
import ir.saltech.answersheet.R
import ir.saltech.answersheet.view.activity.MainActivity

object BlurViewHolder {
    @SuppressLint("UseCompatLoadingForDrawables")
    fun setBlurView(activity: Activity, container: BlurView) {
        if (checkDeviceSupport(activity)) {
            val decorView = activity.window.decorView
            val rootView: ViewGroup = decorView.findViewById<ViewGroup>(android.R.id.content)
            val windowBackground: Drawable = decorView.background
            container.setupWith(
                rootView,
                if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)) RenderEffectBlur() else RenderScriptBlur(
                    activity
                )
            ) // or RenderEffectBlur
                .setFrameClearDrawable(windowBackground).setBlurRadius(25f)
            container.setOutlineProvider(ViewOutlineProvider.BACKGROUND)
            container.setClipToOutline(true)
            container.setBackground(activity.resources.getDrawable(R.drawable.card_background_acrylic))
        } else {
            Log.e("ir.saltech.answersheet2", "Your device cannot support Acrylic Effect!")
        }
    }

    private fun checkDeviceSupport(activity: Activity): Boolean {
        val am: ActivityManager =
            activity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val mi: ActivityManager.MemoryInfo = ActivityManager.MemoryInfo()
        am.getMemoryInfo(mi)
        Log.i("TAG", "Total Memory: " + mi.totalMem)
        return mi.totalMem >= MainActivity.MIN_OF_DEVICE_RAM_CAPACITY
    }
}
