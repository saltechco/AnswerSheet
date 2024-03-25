package ir.saltech.answersheet.`object`.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.LightingColorFilter
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur

object ImageHelper {
    fun blurBitmapWithRenderscript(rs: RenderScript?, bitmap2: Bitmap?) {
        //this will blur the bitmapOriginal with a radius of 25 and save it in bitmapOriginal
        val input: Allocation = Allocation.createFromBitmap(
            rs,
            bitmap2
        ) //use this constructor for best performance, because it uses USAGE_SHARED mode which reuses memory
        val output: Allocation = Allocation.createTyped(rs, input.type)
        val script: ScriptIntrinsicBlur = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
        // must be >0 and <= 25
        script.setRadius(25f)

        script.setInput(input)
        script.forEach(output)
        output.copyTo(bitmap2)
    }

    fun roundCorners(
        bitmap: Bitmap,
        cornerRadiusInPixels: Int,
        captureCircle: Boolean
    ): Bitmap {
        val output = Bitmap.createBitmap(
            bitmap.width,
            bitmap.height,
            Bitmap.Config.ARGB_4444
        )
        val canvas = Canvas(output)

        val color = -0x10000
        val paint = Paint()
        val rect = Rect(
            0,
            0,
            bitmap.width,
            bitmap.height
        )
        val rectF: RectF = RectF(rect)

        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = color
        if (captureCircle) {
            canvas.drawCircle(
                rectF.centerX(),
                rectF.centerY(),
                bitmap.width.toFloat() / 2,
                paint
            )
        } else {
            canvas.drawRoundRect(
                rectF,
                cornerRadiusInPixels.toFloat(),
                cornerRadiusInPixels.toFloat(),
                paint
            )
        }

        paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC_IN))
        canvas.drawBitmap(bitmap, rect, rect, paint)

        return output
    }

    fun getRoundedCornerAndLightenBitmap(
        bitmap: Bitmap,
        cornerRadiusInPixels: Int,
        captureCircle: Boolean
    ): Bitmap {
        val output = Bitmap.createBitmap(
            bitmap.width,
            bitmap.height,
            Bitmap.Config.ARGB_4444
        )
        val canvas = Canvas(output)

        val color = -0x1
        val paint = Paint()
        val rect = Rect(
            0,
            0,
            bitmap.width,
            bitmap.height
        )
        val rectF: RectF = RectF(rect)

        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = color
        if (captureCircle) {
            canvas.drawCircle(
                rectF.centerX(),
                rectF.centerY(),
                bitmap.width.toFloat() / 2,
                paint
            )
        } else {
            canvas.drawRoundRect(
                rectF,
                cornerRadiusInPixels.toFloat(),
                cornerRadiusInPixels.toFloat(),
                paint
            )
        }

        paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC_IN))
        val filter: ColorFilter = LightingColorFilter(-0x1, 0x00222222) // lighten
        //ColorFilter filter = new LightingColorFilter(0xFF7F7F7F, 0x00000000);    // darken
        paint.setColorFilter(filter)
        canvas.drawBitmap(bitmap, rect, rect, paint)

        return output
    }
}