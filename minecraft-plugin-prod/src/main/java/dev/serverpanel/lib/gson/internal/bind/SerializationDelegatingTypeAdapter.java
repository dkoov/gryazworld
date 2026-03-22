/*
 * Decompiled with CFR 0.152.
 */
package dev.serverpanel.lib.gson.internal.bind;

import dev.serverpanel.lib.gson.TypeAdapter;

public abstract class SerializationDelegatingTypeAdapter<T>
extends TypeAdapter<T> {
    public abstract TypeAdapter<T> getSerializationDelegate();
}

