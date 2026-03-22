/*
 * Decompiled with CFR 0.152.
 */
package dev.serverpanel.lib.okhttp3.internal.ws;

import dev.serverpanel.lib.okhttp3.Headers;
import dev.serverpanel.lib.okhttp3.internal.Util;
import java.io.IOException;
import kotlin.Metadata;
import kotlin.jvm.JvmField;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 8, 0}, k=1, xi=48, d1={"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0002\b\u0015\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0086\b\u0018\u0000 \u001c2\u00020\u0001:\u0001\u001cBE\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u0005\u0012\b\b\u0002\u0010\b\u001a\u00020\u0003\u0012\b\b\u0002\u0010\t\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\nJ\t\u0010\f\u001a\u00020\u0003H\u00c6\u0003J\u0010\u0010\r\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003\u00a2\u0006\u0002\u0010\u000eJ\t\u0010\u000f\u001a\u00020\u0003H\u00c6\u0003J\u0010\u0010\u0010\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003\u00a2\u0006\u0002\u0010\u000eJ\t\u0010\u0011\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0012\u001a\u00020\u0003H\u00c6\u0003JN\u0010\u0013\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00032\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u00052\b\b\u0002\u0010\b\u001a\u00020\u00032\b\b\u0002\u0010\t\u001a\u00020\u0003H\u00c6\u0001\u00a2\u0006\u0002\u0010\u0014J\u0013\u0010\u0015\u001a\u00020\u00032\b\u0010\u0016\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0017\u001a\u00020\u0005H\u00d6\u0001J\u000e\u0010\u0018\u001a\u00020\u00032\u0006\u0010\u0019\u001a\u00020\u0003J\t\u0010\u001a\u001a\u00020\u001bH\u00d6\u0001R\u0014\u0010\u0004\u001a\u0004\u0018\u00010\u00058\u0006X\u0087\u0004\u00a2\u0006\u0004\n\u0002\u0010\u000bR\u0010\u0010\u0006\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0007\u001a\u0004\u0018\u00010\u00058\u0006X\u0087\u0004\u00a2\u0006\u0004\n\u0002\u0010\u000bR\u0010\u0010\b\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\t\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001d"}, d2={"Ldev/serverpanel/lib/okhttp3/internal/ws/WebSocketExtensions;", "", "perMessageDeflate", "", "clientMaxWindowBits", "", "clientNoContextTakeover", "serverMaxWindowBits", "serverNoContextTakeover", "unknownValues", "(ZLjava/lang/Integer;ZLjava/lang/Integer;ZZ)V", "Ljava/lang/Integer;", "component1", "component2", "()Ljava/lang/Integer;", "component3", "component4", "component5", "component6", "copy", "(ZLjava/lang/Integer;ZLjava/lang/Integer;ZZ)Lokhttp3/internal/ws/WebSocketExtensions;", "equals", "other", "hashCode", "noContextTakeover", "clientOriginated", "toString", "", "Companion", "okhttp"})
public final class WebSocketExtensions {
    @NotNull
    public static final Companion Companion = new Companion(null);
    @JvmField
    public final boolean perMessageDeflate;
    @JvmField
    @Nullable
    public final Integer clientMaxWindowBits;
    @JvmField
    public final boolean clientNoContextTakeover;
    @JvmField
    @Nullable
    public final Integer serverMaxWindowBits;
    @JvmField
    public final boolean serverNoContextTakeover;
    @JvmField
    public final boolean unknownValues;
    @NotNull
    private static final String HEADER_WEB_SOCKET_EXTENSION = "Sec-WebSocket-Extensions";

    public WebSocketExtensions(boolean perMessageDeflate, @Nullable Integer clientMaxWindowBits, boolean clientNoContextTakeover, @Nullable Integer serverMaxWindowBits, boolean serverNoContextTakeover, boolean unknownValues) {
        this.perMessageDeflate = perMessageDeflate;
        this.clientMaxWindowBits = clientMaxWindowBits;
        this.clientNoContextTakeover = clientNoContextTakeover;
        this.serverMaxWindowBits = serverMaxWindowBits;
        this.serverNoContextTakeover = serverNoContextTakeover;
        this.unknownValues = unknownValues;
    }

    public /* synthetic */ WebSocketExtensions(boolean bl, Integer n, boolean bl2, Integer n2, boolean bl3, boolean bl4, int n3, DefaultConstructorMarker defaultConstructorMarker) {
        if ((n3 & 1) != 0) {
            bl = false;
        }
        if ((n3 & 2) != 0) {
            n = null;
        }
        if ((n3 & 4) != 0) {
            bl2 = false;
        }
        if ((n3 & 8) != 0) {
            n2 = null;
        }
        if ((n3 & 0x10) != 0) {
            bl3 = false;
        }
        if ((n3 & 0x20) != 0) {
            bl4 = false;
        }
        this(bl, n, bl2, n2, bl3, bl4);
    }

    public final boolean noContextTakeover(boolean clientOriginated) {
        return clientOriginated ? this.clientNoContextTakeover : this.serverNoContextTakeover;
    }

    public final boolean component1() {
        return this.perMessageDeflate;
    }

    @Nullable
    public final Integer component2() {
        return this.clientMaxWindowBits;
    }

    public final boolean component3() {
        return this.clientNoContextTakeover;
    }

    @Nullable
    public final Integer component4() {
        return this.serverMaxWindowBits;
    }

    public final boolean component5() {
        return this.serverNoContextTakeover;
    }

    public final boolean component6() {
        return this.unknownValues;
    }

    @NotNull
    public final WebSocketExtensions copy(boolean perMessageDeflate, @Nullable Integer clientMaxWindowBits, boolean clientNoContextTakeover, @Nullable Integer serverMaxWindowBits, boolean serverNoContextTakeover, boolean unknownValues) {
        return new WebSocketExtensions(perMessageDeflate, clientMaxWindowBits, clientNoContextTakeover, serverMaxWindowBits, serverNoContextTakeover, unknownValues);
    }

    public static /* synthetic */ WebSocketExtensions copy$default(WebSocketExtensions webSocketExtensions, boolean bl, Integer n, boolean bl2, Integer n2, boolean bl3, boolean bl4, int n3, Object object) {
        if ((n3 & 1) != 0) {
            bl = webSocketExtensions.perMessageDeflate;
        }
        if ((n3 & 2) != 0) {
            n = webSocketExtensions.clientMaxWindowBits;
        }
        if ((n3 & 4) != 0) {
            bl2 = webSocketExtensions.clientNoContextTakeover;
        }
        if ((n3 & 8) != 0) {
            n2 = webSocketExtensions.serverMaxWindowBits;
        }
        if ((n3 & 0x10) != 0) {
            bl3 = webSocketExtensions.serverNoContextTakeover;
        }
        if ((n3 & 0x20) != 0) {
            bl4 = webSocketExtensions.unknownValues;
        }
        return webSocketExtensions.copy(bl, n, bl2, n2, bl3, bl4);
    }

    @NotNull
    public String toString() {
        return "WebSocketExtensions(perMessageDeflate=" + this.perMessageDeflate + ", clientMaxWindowBits=" + this.clientMaxWindowBits + ", clientNoContextTakeover=" + this.clientNoContextTakeover + ", serverMaxWindowBits=" + this.serverMaxWindowBits + ", serverNoContextTakeover=" + this.serverNoContextTakeover + ", unknownValues=" + this.unknownValues + ')';
    }

    public int hashCode() {
        int n = this.perMessageDeflate ? 1 : 0;
        if (n != 0) {
            n = 1;
        }
        int result = n;
        result = result * 31 + (this.clientMaxWindowBits == null ? 0 : ((Object)this.clientMaxWindowBits).hashCode());
        int n2 = this.clientNoContextTakeover ? 1 : 0;
        if (n2 != 0) {
            n2 = 1;
        }
        result = result * 31 + n2;
        result = result * 31 + (this.serverMaxWindowBits == null ? 0 : ((Object)this.serverMaxWindowBits).hashCode());
        int n3 = this.serverNoContextTakeover ? 1 : 0;
        if (n3 != 0) {
            n3 = 1;
        }
        result = result * 31 + n3;
        int n4 = this.unknownValues ? 1 : 0;
        if (n4 != 0) {
            n4 = 1;
        }
        result = result * 31 + n4;
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof WebSocketExtensions)) {
            return false;
        }
        WebSocketExtensions webSocketExtensions = (WebSocketExtensions)other;
        if (this.perMessageDeflate != webSocketExtensions.perMessageDeflate) {
            return false;
        }
        if (!Intrinsics.areEqual(this.clientMaxWindowBits, webSocketExtensions.clientMaxWindowBits)) {
            return false;
        }
        if (this.clientNoContextTakeover != webSocketExtensions.clientNoContextTakeover) {
            return false;
        }
        if (!Intrinsics.areEqual(this.serverMaxWindowBits, webSocketExtensions.serverMaxWindowBits)) {
            return false;
        }
        if (this.serverNoContextTakeover != webSocketExtensions.serverNoContextTakeover) {
            return false;
        }
        return this.unknownValues == webSocketExtensions.unknownValues;
    }

    public WebSocketExtensions() {
        this(false, null, false, null, false, false, 63, null);
    }

    @Metadata(mv={1, 8, 0}, k=1, xi=48, d1={"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bR\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\t"}, d2={"Ldev/serverpanel/lib/okhttp3/internal/ws/WebSocketExtensions$Companion;", "", "()V", "HEADER_WEB_SOCKET_EXTENSION", "", "parse", "Ldev/serverpanel/lib/okhttp3/internal/ws/WebSocketExtensions;", "responseHeaders", "Ldev/serverpanel/lib/okhttp3/Headers;", "okhttp"})
    public static final class Companion {
        private Companion() {
        }

        @NotNull
        public final WebSocketExtensions parse(@NotNull Headers responseHeaders) throws IOException {
            Intrinsics.checkNotNullParameter(responseHeaders, "responseHeaders");
            boolean compressionEnabled = false;
            Integer clientMaxWindowBits = null;
            boolean clientNoContextTakeover = false;
            Integer serverMaxWindowBits = null;
            boolean serverNoContextTakeover = false;
            boolean unexpectedValues = false;
            int n = responseHeaders.size();
            for (int i = 0; i < n; ++i) {
                if (!StringsKt.equals(responseHeaders.name(i), WebSocketExtensions.HEADER_WEB_SOCKET_EXTENSION, true)) continue;
                String header = responseHeaders.value(i);
                int pos = 0;
                while (pos < header.length()) {
                    int extensionEnd = Util.delimiterOffset$default(header, ',', pos, 0, 4, null);
                    int extensionTokenEnd = Util.delimiterOffset(header, ';', pos, extensionEnd);
                    String extensionToken = Util.trimSubstring(header, pos, extensionTokenEnd);
                    pos = extensionTokenEnd + 1;
                    if (StringsKt.equals(extensionToken, "permessage-deflate", true)) {
                        if (compressionEnabled) {
                            unexpectedValues = true;
                        }
                        compressionEnabled = true;
                        while (pos < extensionEnd) {
                            int parameterEnd = Util.delimiterOffset(header, ';', pos, extensionEnd);
                            int equals = Util.delimiterOffset(header, '=', pos, parameterEnd);
                            String name = Util.trimSubstring(header, pos, equals);
                            String value = equals < parameterEnd ? StringsKt.removeSurrounding(Util.trimSubstring(header, equals + 1, parameterEnd), (CharSequence)"\"") : null;
                            pos = parameterEnd + 1;
                            if (StringsKt.equals(name, "client_max_window_bits", true)) {
                                if (clientMaxWindowBits != null) {
                                    unexpectedValues = true;
                                }
                                String string = value;
                                clientMaxWindowBits = string != null ? StringsKt.toIntOrNull(string) : null;
                                if (clientMaxWindowBits != null) continue;
                                unexpectedValues = true;
                                continue;
                            }
                            if (StringsKt.equals(name, "client_no_context_takeover", true)) {
                                if (clientNoContextTakeover) {
                                    unexpectedValues = true;
                                }
                                if (value != null) {
                                    unexpectedValues = true;
                                }
                                clientNoContextTakeover = true;
                                continue;
                            }
                            if (StringsKt.equals(name, "server_max_window_bits", true)) {
                                if (serverMaxWindowBits != null) {
                                    unexpectedValues = true;
                                }
                                String string = value;
                                serverMaxWindowBits = string != null ? StringsKt.toIntOrNull(string) : null;
                                if (serverMaxWindowBits != null) continue;
                                unexpectedValues = true;
                                continue;
                            }
                            if (StringsKt.equals(name, "server_no_context_takeover", true)) {
                                if (serverNoContextTakeover) {
                                    unexpectedValues = true;
                                }
                                if (value != null) {
                                    unexpectedValues = true;
                                }
                                serverNoContextTakeover = true;
                                continue;
                            }
                            unexpectedValues = true;
                        }
                        continue;
                    }
                    unexpectedValues = true;
                }
            }
            return new WebSocketExtensions(compressionEnabled, clientMaxWindowBits, clientNoContextTakeover, serverMaxWindowBits, serverNoContextTakeover, unexpectedValues);
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }
}

