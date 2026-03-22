/*
 * Decompiled with CFR 0.152.
 */
package dev.serverpanel.lib.okhttp3;

import dev.serverpanel.lib.okhttp3.Handshake;
import dev.serverpanel.lib.okhttp3.Protocol;
import dev.serverpanel.lib.okhttp3.Route;
import java.net.Socket;
import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 8, 0}, k=1, xi=48, d1={"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\bf\u0018\u00002\u00020\u0001J\n\u0010\u0002\u001a\u0004\u0018\u00010\u0003H&J\b\u0010\u0004\u001a\u00020\u0005H&J\b\u0010\u0006\u001a\u00020\u0007H&J\b\u0010\b\u001a\u00020\tH&\u00a8\u0006\n"}, d2={"Ldev/serverpanel/lib/okhttp3/Connection;", "", "handshake", "Ldev/serverpanel/lib/okhttp3/Handshake;", "protocol", "Ldev/serverpanel/lib/okhttp3/Protocol;", "route", "Ldev/serverpanel/lib/okhttp3/Route;", "socket", "Ljava/net/Socket;", "okhttp"})
public interface Connection {
    @NotNull
    public Route route();

    @NotNull
    public Socket socket();

    @Nullable
    public Handshake handshake();

    @NotNull
    public Protocol protocol();
}

