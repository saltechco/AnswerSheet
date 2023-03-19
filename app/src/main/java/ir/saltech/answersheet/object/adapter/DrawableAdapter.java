package ir.saltech.answersheet.object.adapter;

import android.graphics.drawable.Drawable;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class DrawableAdapter extends TypeAdapter<Drawable> {
    @Override
    public void write(JsonWriter out, Drawable value) throws IOException {

    }

    @Override
    public Drawable read(JsonReader in) throws IOException {
        return new Gson().fromJson(in, Drawable.class);
    }
}
