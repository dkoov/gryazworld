/*
 * Decompiled with CFR 0.152.
 */
package dev.serverpanel.lib.okhttp3.internal.http2;

import dev.serverpanel.lib.okhttp3.internal.Util;
import kotlin.Metadata;
import kotlin.jvm.JvmField;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import okio.ByteString;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={1, 8, 0}, k=1, xi=48, d1={"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0011\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u001a\n\u0002\u0010\u000b\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0016\u0010\u001f\u001a\u00020\u00052\u0006\u0010 \u001a\u00020\u000b2\u0006\u0010!\u001a\u00020\u000bJ\u0015\u0010\"\u001a\u00020\u00052\u0006\u0010 \u001a\u00020\u000bH\u0000\u00a2\u0006\u0002\b#J.\u0010$\u001a\u00020\u00052\u0006\u0010%\u001a\u00020&2\u0006\u0010'\u001a\u00020\u000b2\u0006\u0010(\u001a\u00020\u000b2\u0006\u0010 \u001a\u00020\u000b2\u0006\u0010!\u001a\u00020\u000bR\u0016\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004X\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\u0006R\u0010\u0010\u0007\u001a\u00020\b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0018\u0010\t\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00050\u0004X\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\u0006R\u000e\u0010\n\u001a\u00020\u000bX\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\u000bX\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000bX\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000bX\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u000bX\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u000bX\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u000bX\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u000bX\u0086T\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004X\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\u0006R\u000e\u0010\u0014\u001a\u00020\u000bX\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0015\u001a\u00020\u000bX\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0016\u001a\u00020\u000bX\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0017\u001a\u00020\u000bX\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0018\u001a\u00020\u000bX\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0019\u001a\u00020\u000bX\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001a\u001a\u00020\u000bX\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001b\u001a\u00020\u000bX\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001c\u001a\u00020\u000bX\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001d\u001a\u00020\u000bX\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001e\u001a\u00020\u000bX\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006)"}, d2={"Ldev/serverpanel/lib/okhttp3/internal/http2/Http2;", "", "()V", "BINARY", "", "", "[Ljava/lang/String;", "CONNECTION_PREFACE", "Lokio/ByteString;", "FLAGS", "FLAG_ACK", "", "FLAG_COMPRESSED", "FLAG_END_HEADERS", "FLAG_END_PUSH_PROMISE", "FLAG_END_STREAM", "FLAG_NONE", "FLAG_PADDED", "FLAG_PRIORITY", "FRAME_NAMES", "INITIAL_MAX_FRAME_SIZE", "TYPE_CONTINUATION", "TYPE_DATA", "TYPE_GOAWAY", "TYPE_HEADERS", "TYPE_PING", "TYPE_PRIORITY", "TYPE_PUSH_PROMISE", "TYPE_RST_STREAM", "TYPE_SETTINGS", "TYPE_WINDOW_UPDATE", "formatFlags", "type", "flags", "formattedType", "formattedType$okhttp", "frameLog", "inbound", "", "streamId", "length", "okhttp"})
public final class Http2 {
    @NotNull
    public static final Http2 INSTANCE;
    @JvmField
    @NotNull
    public static final ByteString CONNECTION_PREFACE;
    public static final int INITIAL_MAX_FRAME_SIZE = 16384;
    public static final int TYPE_DATA = 0;
    public static final int TYPE_HEADERS = 1;
    public static final int TYPE_PRIORITY = 2;
    public static final int TYPE_RST_STREAM = 3;
    public static final int TYPE_SETTINGS = 4;
    public static final int TYPE_PUSH_PROMISE = 5;
    public static final int TYPE_PING = 6;
    public static final int TYPE_GOAWAY = 7;
    public static final int TYPE_WINDOW_UPDATE = 8;
    public static final int TYPE_CONTINUATION = 9;
    public static final int FLAG_NONE = 0;
    public static final int FLAG_ACK = 1;
    public static final int FLAG_END_STREAM = 1;
    public static final int FLAG_END_HEADERS = 4;
    public static final int FLAG_END_PUSH_PROMISE = 4;
    public static final int FLAG_PADDED = 8;
    public static final int FLAG_PRIORITY = 32;
    public static final int FLAG_COMPRESSED = 32;
    @NotNull
    private static final String[] FRAME_NAMES;
    @NotNull
    private static final String[] FLAGS;
    @NotNull
    private static final String[] BINARY;

    private Http2() {
    }

    @NotNull
    public final String frameLog(boolean inbound, int streamId, int length, int type, int flags) {
        String formattedType = this.formattedType$okhttp(type);
        String formattedFlags = this.formatFlags(type, flags);
        String direction = inbound ? "<<" : ">>";
        Object[] objectArray = new Object[]{direction, streamId, length, formattedType, formattedFlags};
        return Util.format("%s 0x%08x %5d %-13s %s", objectArray);
    }

    @NotNull
    public final String formattedType$okhttp(int type) {
        String string;
        if (type < FRAME_NAMES.length) {
            string = FRAME_NAMES[type];
        } else {
            Object[] objectArray = new Object[]{type};
            string = Util.format("0x%02x", objectArray);
        }
        return string;
    }

    @NotNull
    public final String formatFlags(int type, int flags) {
        String string;
        if (flags == 0) {
            return "";
        }
        switch (type) {
            case 4: 
            case 6: {
                return flags == 1 ? "ACK" : BINARY[flags];
            }
            case 2: 
            case 3: 
            case 7: 
            case 8: {
                return BINARY[flags];
            }
        }
        if (flags < FLAGS.length) {
            String string2 = FLAGS[flags];
            string = string2;
            Intrinsics.checkNotNull(string2);
        } else {
            string = BINARY[flags];
        }
        String result = string;
        return type == 5 && (flags & 4) != 0 ? StringsKt.replace$default(result, "HEADERS", "PUSH_PROMISE", false, 4, null) : (type == 0 && (flags & 0x20) != 0 ? StringsKt.replace$default(result, "PRIORITY", "COMPRESSED", false, 4, null) : result);
    }

    /*
     * WARNING - void declaration
     */
    static {
        int nArray;
        void var0_2;
        INSTANCE = new Http2();
        CONNECTION_PREFACE = ByteString.Companion.encodeUtf8("PRI * HTTP/2.0\r\n\r\nSM\r\n\r\n");
        String[] stringArray = new String[]{"DATA", "HEADERS", "PRIORITY", "RST_STREAM", "SETTINGS", "PUSH_PROMISE", "PING", "GOAWAY", "WINDOW_UPDATE", "CONTINUATION"};
        FRAME_NAMES = stringArray;
        FLAGS = new String[64];
        boolean n2 = false;
        Object[] i = new String[256];
        while (var0_2 < 256) {
            nArray = var0_2++;
            Object[] objectArray = new Object[1];
            Intrinsics.checkNotNullExpressionValue(Integer.toBinaryString(nArray), "toBinaryString(it)");
            i[nArray] = StringsKt.replace$default(Util.format("%8s", objectArray), ' ', '0', false, 4, null);
        }
        BINARY = i;
        Http2.FLAGS[0] = "";
        Http2.FLAGS[1] = "END_STREAM";
        i = new int[1];
        i[0] = (String)true;
        Object[] objectArray = i;
        Http2.FLAGS[8] = "PADDED";
        nArray = objectArray.length;
        for (int j = 0; j < nArray; ++j) {
            String prefixFlag = objectArray[j];
            Http2.FLAGS[prefixFlag | 8] = FLAGS[prefixFlag] + "|PADDED";
        }
        Http2.FLAGS[4] = "END_HEADERS";
        Http2.FLAGS[32] = "PRIORITY";
        Http2.FLAGS[36] = "END_HEADERS|PRIORITY";
        int[] nArray2 = new int[]{4, 32, 36};
        for (int frameFlag : nArray2) {
            for (String prefixFlag : objectArray) {
                Http2.FLAGS[prefixFlag | frameFlag] = FLAGS[prefixFlag] + '|' + FLAGS[frameFlag];
                Http2.FLAGS[prefixFlag | frameFlag | 8] = FLAGS[prefixFlag] + '|' + FLAGS[frameFlag] + "|PADDED";
            }
        }
        int n = FLAGS.length;
        for (int i2 = 0; i2 < n; ++i2) {
            if (FLAGS[i2] != null) continue;
            Http2.FLAGS[i2] = BINARY[i2];
        }
    }
}

