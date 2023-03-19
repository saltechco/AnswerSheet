package ir.saltech.answersheet.object.util;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Load a tensorflow model and use it
 * for predict many things..
 * Input and Output (Predicts) Type based on Model
 */
public class TensorModelLoader {
    public MappedByteBuffer loadModelFile(@NonNull Activity activity, String modelFileName) throws IOException {
        AssetFileDescriptor assetFileDescriptor = activity.getAssets().openFd(modelFileName);
        FileInputStream fileInputStream = new FileInputStream(assetFileDescriptor.getFileDescriptor());
        FileChannel fileChannel = fileInputStream.getChannel();
        long startOffset = assetFileDescriptor.getStartOffset();
        long len = assetFileDescriptor.getLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, len);
    }

    @Nullable
    public Interpreter loadModel(@NonNull Activity activity, String modelFileName, Interpreter.Options options) {
        try {
            return new Interpreter(loadModelFile(activity, modelFileName), new Interpreter.Options());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
