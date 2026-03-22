/*
 * Decompiled with CFR 0.152.
 */
package dev.serverpanel.lib.gson;

import dev.serverpanel.lib.gson.JsonElement;
import dev.serverpanel.lib.gson.JsonSerializationContext;
import java.lang.reflect.Type;

public interface JsonSerializer<T> {
    public JsonElement serialize(T var1, Type var2, JsonSerializationContext var3);
}

