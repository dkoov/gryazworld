/*
 * Decompiled with CFR 0.152.
 */
package dev.serverpanel.lib.gson.internal;

import dev.serverpanel.lib.gson.stream.JsonReader;
import java.io.IOException;

public abstract class JsonReaderInternalAccess {
    public static JsonReaderInternalAccess INSTANCE;

    public abstract void promoteNameToValue(JsonReader var1) throws IOException;
}

