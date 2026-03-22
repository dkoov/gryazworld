/*
 * Decompiled with CFR 0.152.
 */
package dev.serverpanel.lib.gson;

import dev.serverpanel.lib.gson.stream.JsonReader;
import java.io.IOException;

public interface ToNumberStrategy {
    public Number readNumber(JsonReader var1) throws IOException;
}

