/*
 * Decompiled with CFR 0.152.
 */
package dev.serverpanel.lib.gson.internal.sql;

import dev.serverpanel.lib.gson.Gson;
import dev.serverpanel.lib.gson.TypeAdapter;
import dev.serverpanel.lib.gson.TypeAdapterFactory;
import dev.serverpanel.lib.gson.reflect.TypeToken;
import dev.serverpanel.lib.gson.stream.JsonReader;
import dev.serverpanel.lib.gson.stream.JsonWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

class SqlTimestampTypeAdapter
extends TypeAdapter<Timestamp> {
    static final TypeAdapterFactory FACTORY = new TypeAdapterFactory(){

        @Override
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
            if (typeToken.getRawType() == Timestamp.class) {
                TypeAdapter<Date> dateTypeAdapter = gson.getAdapter(Date.class);
                return new SqlTimestampTypeAdapter(dateTypeAdapter);
            }
            return null;
        }
    };
    private final TypeAdapter<Date> dateTypeAdapter;

    private SqlTimestampTypeAdapter(TypeAdapter<Date> dateTypeAdapter) {
        this.dateTypeAdapter = dateTypeAdapter;
    }

    @Override
    public Timestamp read(JsonReader in) throws IOException {
        Date date = this.dateTypeAdapter.read(in);
        return date != null ? new Timestamp(date.getTime()) : null;
    }

    @Override
    public void write(JsonWriter out, Timestamp value) throws IOException {
        this.dateTypeAdapter.write(out, value);
    }
}

