/*
 * Decompiled with CFR 0.152.
 */
package dev.serverpanel.lib.gson;

import dev.serverpanel.lib.gson.FieldAttributes;

public interface ExclusionStrategy {
    public boolean shouldSkipField(FieldAttributes var1);

    public boolean shouldSkipClass(Class<?> var1);
}

