package com.weberbox.pifire.utils.adapters;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

import timber.log.Timber;

public class CustomTypeAdapterFactory implements TypeAdapterFactory {

    public final <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        final TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);
        return createCustomTypeAdapter(delegate);
    }

    private <T> TypeAdapter<T> createCustomTypeAdapter(TypeAdapter<T> delegate) {
        return new TypeAdapter<>() {
            @Override
            public void write(JsonWriter out, T value) throws IOException {
                delegate.write(out, value);
            }

            @Override
            public T read(JsonReader in) throws IOException {
                try {
                    return delegate.read(in);
                } catch (Exception e) {
                    Timber.e(e, "Error parsing JSON");
                    in.skipValue();
                    return null;
                }
            }
        };
    }
}
