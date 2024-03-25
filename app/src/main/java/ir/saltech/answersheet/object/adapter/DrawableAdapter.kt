package ir.saltech.answersheet.`object`.adapter

import android.graphics.drawable.Drawable
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.io.IOException

class DrawableAdapter : TypeAdapter<Drawable?>() {
    @Throws(IOException::class)
    override fun write(out: JsonWriter?, value: Drawable?) {
    }

    @Throws(IOException::class)
    override fun read(`in`: JsonReader): Drawable {
        return Gson().fromJson<Drawable>(`in`, Drawable::class.java)
    }
}
