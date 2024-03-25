package ir.saltech.answersheet.`object`.util

import android.app.Activity
import android.content.res.AssetFileDescriptor
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.IOException
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

/**
 * Load a tensorflow model and use it
 * for predict many things..
 * Input and Output (Predicts) Type based on Model
 */
class TensorModelLoader {
    @Throws(IOException::class)
    fun loadModelFile(activity: Activity, modelFileName: String?): MappedByteBuffer {
        val assetFileDescriptor: AssetFileDescriptor = activity.assets.openFd(modelFileName!!)
        val fileInputStream: FileInputStream =
            FileInputStream(assetFileDescriptor.fileDescriptor)
        val fileChannel = fileInputStream.channel
        val startOffset: Long = assetFileDescriptor.startOffset
        val len: Long = assetFileDescriptor.getLength()
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, len)
    }

    fun loadModel(
        activity: Activity,
        modelFileName: String?,
        options: Interpreter.Options?
    ): Interpreter? {
        try {
            return Interpreter(loadModelFile(activity, modelFileName), Interpreter.Options())
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }
}
