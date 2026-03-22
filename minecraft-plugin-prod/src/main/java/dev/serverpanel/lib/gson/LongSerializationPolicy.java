/*
 * Decompiled with CFR 0.152.
 */
package dev.serverpanel.lib.gson;

import dev.serverpanel.lib.gson.JsonElement;
import dev.serverpanel.lib.gson.JsonNull;
import dev.serverpanel.lib.gson.JsonPrimitive;

public enum LongSerializationPolicy {
    DEFAULT{

        @Override
        public JsonElement serialize(Long value) {
            if (value == null) {
                return JsonNull.INSTANCE;
            }
            return new JsonPrimitive(value);
        }
    }
    ,
    STRING{

        @Override
        public JsonElement serialize(Long value) {
            if (value == null) {
                return JsonNull.INSTANCE;
            }
            return new JsonPrimitive(value.toString());
        }
    };


    public abstract JsonElement serialize(Long var1);
}

