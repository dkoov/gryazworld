/*
 * Decompiled with CFR 0.152.
 */
package dev.serverpanel.lib.gson;

import dev.serverpanel.lib.gson.Gson;
import dev.serverpanel.lib.gson.TypeAdapter;
import dev.serverpanel.lib.gson.reflect.TypeToken;

public interface TypeAdapterFactory {
    public <T> TypeAdapter<T> create(Gson var1, TypeToken<T> var2);
}

