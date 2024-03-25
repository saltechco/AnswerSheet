package ir.saltech.answersheet.view.container;

import static android.content.Context.ACTIVITY_SERVICE;
import static ir.saltech.answersheet.view.activity.MainActivity.MIN_OF_DEVICE_RAM_CAPACITY;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderEffectBlur;
import eightbitlab.com.blurview.RenderScriptBlur;
import ir.saltech.answersheet.R;

public class BlurViewHolder {

    @SuppressLint("UseCompatLoadingForDrawables")
    public static void setBlurView(Activity activity, BlurView container) {
        if (checkDeviceSupport(activity)) {
            View decorView = activity.getWindow().getDecorView();
            ViewGroup rootView = decorView.findViewById(android.R.id.content);
            Drawable windowBackground = decorView.getBackground();
            container.setupWith(rootView, (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) ? new RenderEffectBlur() : new RenderScriptBlur(activity)) // or RenderEffectBlur
                    .setFrameClearDrawable(windowBackground).setBlurRadius(25f);
            container.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
            container.setClipToOutline(true);
            container.setBackground(activity.getResources().getDrawable(R.drawable.card_background_acrylic));
        } else {
            Log.e("ir.saltech.answersheet2", "Your device cannot support Acrylic Effect!");
        }
    }

    private static boolean checkDeviceSupport(Activity activity) {
        ActivityManager am = (ActivityManager) activity.getSystemService(ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        Log.i("TAG", "Total Memory: " + mi.totalMem);
        return mi.totalMem >= MIN_OF_DEVICE_RAM_CAPACITY;
    }
}
