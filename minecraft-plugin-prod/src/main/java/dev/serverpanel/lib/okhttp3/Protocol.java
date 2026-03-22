/*
 * Decompiled with CFR 0.152.
 */
package dev.serverpanel.lib.okhttp3;

import java.io.IOException;
import kotlin.Deprecated;
import kotlin.Metadata;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={1, 8, 0}, k=1, xi=48, d1={"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0000\n\u0002\u0010\u000e\n\u0002\b\n\b\u0086\u0001\u0018\u0000 \f2\b\u0012\u0004\u0012\u00020\u00000\u0001:\u0001\fB\u000f\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\b\u0010\u0005\u001a\u00020\u0003H\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000j\u0002\b\u0006j\u0002\b\u0007j\u0002\b\bj\u0002\b\tj\u0002\b\nj\u0002\b\u000b\u00a8\u0006\r"}, d2={"Ldev/serverpanel/lib/okhttp3/Protocol;", "", "protocol", "", "(Ljava/lang/String;ILjava/lang/String;)V", "toString", "HTTP_1_0", "HTTP_1_1", "SPDY_3", "HTTP_2", "H2_PRIOR_KNOWLEDGE", "QUIC", "Companion", "okhttp"})
public final class Protocol
extends Enum<Protocol> {
    @NotNull
    public static final Companion Companion;
    @NotNull
    private final String protocol;
    public static final /* enum */ Protocol HTTP_1_0;
    public static final /* enum */ Protocol HTTP_1_1;
    @Deprecated(message="OkHttp has dropped support for SPDY. Prefer {@link #HTTP_2}.")
    public static final /* enum */ Protocol SPDY_3;
    public static final /* enum */ Protocol HTTP_2;
    public static final /* enum */ Protocol H2_PRIOR_KNOWLEDGE;
    public static final /* enum */ Protocol QUIC;
    private static final /* synthetic */ Protocol[] $VALUES;

    private Protocol(String protocol) {
        this.protocol = protocol;
    }

    @NotNull
    public String toString() {
        return this.protocol;
    }

    public static Protocol[] values() {
        return (Protocol[])$VALUES.clone();
    }

    public static Protocol valueOf(String value) {
        return Enum.valueOf(Protocol.class, value);
    }

    @JvmStatic
    @NotNull
    public static final Protocol get(@NotNull String protocol) throws IOException {
        return Companion.get(protocol);
    }

    static {
        HTTP_1_0 = new Protocol("http/1.0");
        HTTP_1_1 = new Protocol("http/1.1");
        SPDY_3 = new Protocol("spdy/3.1");
        HTTP_2 = new Protocol("h2");
        H2_PRIOR_KNOWLEDGE = new Protocol("h2_prior_knowledge");
        QUIC = new Protocol("quic");
        $VALUES = protocolArray = new Protocol[]{Protocol.HTTP_1_0, Protocol.HTTP_1_1, Protocol.SPDY_3, Protocol.HTTP_2, Protocol.H2_PRIOR_KNOWLEDGE, Protocol.QUIC};
        Companion = new Companion(null);
    }

    @Metadata(mv={1, 8, 0}, k=1, xi=48, d1={"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0007\u00a8\u0006\u0007"}, d2={"Ldev/serverpanel/lib/okhttp3/Protocol$Companion;", "", "()V", "get", "Ldev/serverpanel/lib/okhttp3/Protocol;", "protocol", "", "okhttp"})
    public static final class Companion {
        private Companion() {
        }

        @JvmStatic
        @NotNull
        public final Protocol get(@NotNull String protocol) throws IOException {
            Protocol protocol2;
            Intrinsics.checkNotNullParameter(protocol, "protocol");
            String string = protocol;
            if (Intrinsics.areEqual(string, HTTP_1_0.protocol)) {
                protocol2 = HTTP_1_0;
            } else if (Intrinsics.areEqual(string, HTTP_1_1.protocol)) {
                protocol2 = HTTP_1_1;
            } else if (Intrinsics.areEqual(string, H2_PRIOR_KNOWLEDGE.protocol)) {
                protocol2 = H2_PRIOR_KNOWLEDGE;
            } else if (Intrinsics.areEqual(string, HTTP_2.protocol)) {
                protocol2 = HTTP_2;
            } else if (Intrinsics.areEqual(string, SPDY_3.protocol)) {
                protocol2 = SPDY_3;
            } else if (Intrinsics.areEqual(string, QUIC.protocol)) {
                protocol2 = QUIC;
            } else {
                throw new IOException("Unexpected protocol: " + protocol);
            }
            return protocol2;
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }
}

