/*
 * Decompiled with CFR 0.152.
 */
package dev.serverpanel.lib.okhttp3.internal.http2;

import dev.serverpanel.lib.okhttp3.internal.Util;
import java.io.IOException;
import kotlin.Metadata;
import kotlin.collections.ArraysKt;
import kotlin.jvm.internal.Intrinsics;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.ByteString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 8, 0}, k=1, xi=48, d1={"\u0000F\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0015\n\u0000\n\u0002\u0010\u0012\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u00c6\u0002\u0018\u00002\u00020\u0001:\u0001\u001aB\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J \u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\f2\u0006\u0010\u000e\u001a\u00020\fH\u0002J\u001e\u0010\u000f\u001a\u00020\n2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u0015J\u0016\u0010\u0016\u001a\u00020\n2\u0006\u0010\u0010\u001a\u00020\u00172\u0006\u0010\u0014\u001a\u00020\u0015J\u000e\u0010\u0018\u001a\u00020\f2\u0006\u0010\u0019\u001a\u00020\u0017R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001b"}, d2={"Ldev/serverpanel/lib/okhttp3/internal/http2/Huffman;", "", "()V", "CODES", "", "CODE_BIT_COUNTS", "", "root", "Ldev/serverpanel/lib/okhttp3/internal/http2/Huffman$Node;", "addCode", "", "symbol", "", "code", "codeBitCount", "decode", "source", "Lokio/BufferedSource;", "byteCount", "", "sink", "Lokio/BufferedSink;", "encode", "Lokio/ByteString;", "encodedLength", "bytes", "Node", "okhttp"})
public final class Huffman {
    @NotNull
    public static final Huffman INSTANCE = new Huffman();
    @NotNull
    private static final int[] CODES;
    @NotNull
    private static final byte[] CODE_BIT_COUNTS;
    @NotNull
    private static final Node root;

    private Huffman() {
    }

    public final void encode(@NotNull ByteString source2, @NotNull BufferedSink sink2) throws IOException {
        Intrinsics.checkNotNullParameter(source2, "source");
        Intrinsics.checkNotNullParameter(sink2, "sink");
        long accumulator = 0L;
        int accumulatorBitCount = 0;
        int n = source2.size();
        for (int i = 0; i < n; ++i) {
            int symbol = Util.and(source2.getByte(i), 255);
            int code = CODES[symbol];
            byte codeBitCount = CODE_BIT_COUNTS[symbol];
            accumulator = accumulator << codeBitCount | (long)code;
            accumulatorBitCount += codeBitCount;
            while (accumulatorBitCount >= 8) {
                sink2.writeByte((int)(accumulator >> (accumulatorBitCount -= 8)));
            }
        }
        if (accumulatorBitCount > 0) {
            accumulator <<= 8 - accumulatorBitCount;
            sink2.writeByte((int)(accumulator |= 255L >>> accumulatorBitCount));
        }
    }

    public final int encodedLength(@NotNull ByteString bytes) {
        Intrinsics.checkNotNullParameter(bytes, "bytes");
        long bitCount = 0L;
        int n = bytes.size();
        for (int i = 0; i < n; ++i) {
            int byteIn = Util.and(bytes.getByte(i), 255);
            bitCount += (long)CODE_BIT_COUNTS[byteIn];
        }
        return (int)(bitCount + (long)7 >> 3);
    }

    public final void decode(@NotNull BufferedSource source2, long byteCount, @NotNull BufferedSink sink2) {
        Intrinsics.checkNotNullParameter(source2, "source");
        Intrinsics.checkNotNullParameter(sink2, "sink");
        Node node = root;
        int accumulator = 0;
        int accumulatorBitCount = 0;
        for (long i = 0L; i < byteCount; ++i) {
            int byteIn = Util.and(source2.readByte(), 255);
            accumulator = accumulator << 8 | byteIn;
            accumulatorBitCount += 8;
            while (accumulatorBitCount >= 8) {
                int childIndex = accumulator >>> accumulatorBitCount - 8 & 0xFF;
                Node[] nodeArray = node.getChildren();
                Intrinsics.checkNotNull(nodeArray);
                Intrinsics.checkNotNull(nodeArray[childIndex]);
                if (node.getChildren() == null) {
                    sink2.writeByte(node.getSymbol());
                    accumulatorBitCount -= node.getTerminalBitCount();
                    node = root;
                    continue;
                }
                accumulatorBitCount -= 8;
            }
        }
        while (accumulatorBitCount > 0) {
            int childIndex = accumulator << 8 - accumulatorBitCount & 0xFF;
            Node[] nodeArray = node.getChildren();
            Intrinsics.checkNotNull(nodeArray);
            Node node2 = nodeArray[childIndex];
            Intrinsics.checkNotNull(node2);
            node = node2;
            if (node.getChildren() != null || node.getTerminalBitCount() > accumulatorBitCount) break;
            sink2.writeByte(node.getSymbol());
            accumulatorBitCount -= node.getTerminalBitCount();
            node = root;
        }
    }

    private final void addCode(int symbol, int code, int codeBitCount) {
        Node terminal = new Node(symbol, codeBitCount);
        int accumulatorBitCount = codeBitCount;
        Node node = root;
        while (accumulatorBitCount > 8) {
            Node[] children;
            int childIndex = code >>> (accumulatorBitCount -= 8) & 0xFF;
            Intrinsics.checkNotNull(node.getChildren());
            Node child = children[childIndex];
            if (child == null) {
                children[childIndex] = child = new Node();
            }
            node = child;
        }
        int shift = 8 - accumulatorBitCount;
        int start = code << shift & 0xFF;
        int end = 1 << shift;
        Node[] nodeArray = node.getChildren();
        Intrinsics.checkNotNull(nodeArray);
        ArraysKt.fill(nodeArray, terminal, start, start + end);
    }

    static {
        Object[] objectArray = new int[]{8184, 8388568, 0xFFFFFE2, 0xFFFFFE3, 0xFFFFFE4, 0xFFFFFE5, 0xFFFFFE6, 0xFFFFFE7, 0xFFFFFE8, 0xFFFFEA, 0x3FFFFFFC, 0xFFFFFE9, 0xFFFFFEA, 0x3FFFFFFD, 0xFFFFFEB, 0xFFFFFEC, 0xFFFFFED, 0xFFFFFEE, 0xFFFFFEF, 0xFFFFFF0, 0xFFFFFF1, 0xFFFFFF2, 0x3FFFFFFE, 0xFFFFFF3, 0xFFFFFF4, 0xFFFFFF5, 0xFFFFFF6, 0xFFFFFF7, 0xFFFFFF8, 0xFFFFFF9, 0xFFFFFFA, 0xFFFFFFB, 20, 1016, 1017, 4090, 8185, 21, 248, 2042, 1018, 1019, 249, 2043, 250, 22, 23, 24, 0, 1, 2, 25, 26, 27, 28, 29, 30, 31, 92, 251, 32764, 32, 4091, 1020, 8186, 33, 93, 94, 95, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 252, 115, 253, 8187, 524272, 8188, 16380, 34, 32765, 3, 35, 4, 36, 5, 37, 38, 39, 6, 116, 117, 40, 41, 42, 7, 43, 118, 44, 8, 9, 45, 119, 120, 121, 122, 123, 32766, 2044, 16381, 8189, 0xFFFFFFC, 1048550, 4194258, 1048551, 1048552, 0x3FFFD3, 4194260, 4194261, 8388569, 4194262, 8388570, 8388571, 8388572, 0x7FFFDD, 8388574, 0xFFFFEB, 0x7FFFDF, 0xFFFFEC, 0xFFFFED, 4194263, 8388576, 0xFFFFEE, 8388577, 8388578, 8388579, 8388580, 2097116, 4194264, 8388581, 4194265, 8388582, 0x7FFFE7, 0xFFFFEF, 4194266, 0x1FFFDD, 1048553, 4194267, 4194268, 8388584, 8388585, 2097118, 8388586, 0x3FFFDD, 4194270, 0xFFFFF0, 0x1FFFDF, 0x3FFFDF, 8388587, 8388588, 2097120, 0x1FFFE1, 4194272, 2097122, 8388589, 4194273, 0x7FFFEE, 0x7FFFEF, 1048554, 4194274, 0x3FFFE3, 4194276, 0x7FFFF0, 4194277, 4194278, 0x7FFFF1, 67108832, 67108833, 1048555, 524273, 4194279, 0x7FFFF2, 4194280, 33554412, 67108834, 0x3FFFFE3, 67108836, 134217694, 0x7FFFFDF, 67108837, 0xFFFFF1, 33554413, 524274, 2097123, 67108838, 134217696, 134217697, 67108839, 134217698, 0xFFFFF2, 2097124, 2097125, 67108840, 67108841, 0xFFFFFFD, 134217699, 134217700, 134217701, 1048556, 0xFFFFF3, 1048557, 2097126, 4194281, 2097127, 2097128, 0x7FFFF3, 4194282, 4194283, 0x1FFFFEE, 0x1FFFFEF, 0xFFFFF4, 0xFFFFF5, 67108842, 0x7FFFF4, 67108843, 134217702, 67108844, 67108845, 0x7FFFFE7, 134217704, 134217705, 134217706, 134217707, 0xFFFFFFE, 134217708, 134217709, 0x7FFFFEE, 0x7FFFFEF, 0x7FFFFF0, 0x3FFFFEE};
        CODES = objectArray;
        objectArray = new byte[256];
        objectArray[0] = 13;
        objectArray[1] = 23;
        objectArray[2] = 28;
        objectArray[3] = 28;
        objectArray[4] = 28;
        objectArray[5] = 28;
        objectArray[6] = 28;
        objectArray[7] = 28;
        objectArray[8] = 28;
        objectArray[9] = 24;
        objectArray[10] = 30;
        objectArray[11] = 28;
        objectArray[12] = 28;
        objectArray[13] = 30;
        objectArray[14] = 28;
        objectArray[15] = 28;
        objectArray[16] = 28;
        objectArray[17] = 28;
        objectArray[18] = 28;
        objectArray[19] = 28;
        objectArray[20] = 28;
        objectArray[21] = 28;
        objectArray[22] = 30;
        objectArray[23] = 28;
        objectArray[24] = 28;
        objectArray[25] = 28;
        objectArray[26] = 28;
        objectArray[27] = 28;
        objectArray[28] = 28;
        objectArray[29] = 28;
        objectArray[30] = 28;
        objectArray[31] = 28;
        objectArray[32] = 6;
        objectArray[33] = 10;
        objectArray[34] = 10;
        objectArray[35] = 12;
        objectArray[36] = 13;
        objectArray[37] = 6;
        objectArray[38] = 8;
        objectArray[39] = 11;
        objectArray[40] = 10;
        objectArray[41] = 10;
        objectArray[42] = 8;
        objectArray[43] = 11;
        objectArray[44] = 8;
        objectArray[45] = 6;
        objectArray[46] = 6;
        objectArray[47] = 6;
        objectArray[48] = 5;
        objectArray[49] = 5;
        objectArray[50] = 5;
        objectArray[51] = 6;
        objectArray[52] = 6;
        objectArray[53] = 6;
        objectArray[54] = 6;
        objectArray[55] = 6;
        objectArray[56] = 6;
        objectArray[57] = 6;
        objectArray[58] = 7;
        objectArray[59] = 8;
        objectArray[60] = 15;
        objectArray[61] = 6;
        objectArray[62] = 12;
        objectArray[63] = 10;
        objectArray[64] = 13;
        objectArray[65] = 6;
        objectArray[66] = 7;
        objectArray[67] = 7;
        objectArray[68] = 7;
        objectArray[69] = 7;
        objectArray[70] = 7;
        objectArray[71] = 7;
        objectArray[72] = 7;
        objectArray[73] = 7;
        objectArray[74] = 7;
        objectArray[75] = 7;
        objectArray[76] = 7;
        objectArray[77] = 7;
        objectArray[78] = 7;
        objectArray[79] = 7;
        objectArray[80] = 7;
        objectArray[81] = 7;
        objectArray[82] = 7;
        objectArray[83] = 7;
        objectArray[84] = 7;
        objectArray[85] = 7;
        objectArray[86] = 7;
        objectArray[87] = 7;
        objectArray[88] = 8;
        objectArray[89] = 7;
        objectArray[90] = 8;
        objectArray[91] = 13;
        objectArray[92] = 19;
        objectArray[93] = 13;
        objectArray[94] = 14;
        objectArray[95] = 6;
        objectArray[96] = 15;
        objectArray[97] = 5;
        objectArray[98] = 6;
        objectArray[99] = 5;
        objectArray[100] = 6;
        objectArray[101] = 5;
        objectArray[102] = 6;
        objectArray[103] = 6;
        objectArray[104] = 6;
        objectArray[105] = 5;
        objectArray[106] = 7;
        objectArray[107] = 7;
        objectArray[108] = 6;
        objectArray[109] = 6;
        objectArray[110] = 6;
        objectArray[111] = 5;
        objectArray[112] = 6;
        objectArray[113] = 7;
        objectArray[114] = 6;
        objectArray[115] = 5;
        objectArray[116] = 5;
        objectArray[117] = 6;
        objectArray[118] = 7;
        objectArray[119] = 7;
        objectArray[120] = 7;
        objectArray[121] = 7;
        objectArray[122] = 7;
        objectArray[123] = 15;
        objectArray[124] = 11;
        objectArray[125] = 14;
        objectArray[126] = 13;
        objectArray[127] = 28;
        objectArray[128] = 20;
        objectArray[129] = 22;
        objectArray[130] = 20;
        objectArray[131] = 20;
        objectArray[132] = 22;
        objectArray[133] = 22;
        objectArray[134] = 22;
        objectArray[135] = 23;
        objectArray[136] = 22;
        objectArray[137] = 23;
        objectArray[138] = 23;
        objectArray[139] = 23;
        objectArray[140] = 23;
        objectArray[141] = 23;
        objectArray[142] = 24;
        objectArray[143] = 23;
        objectArray[144] = 24;
        objectArray[145] = 24;
        objectArray[146] = 22;
        objectArray[147] = 23;
        objectArray[148] = 24;
        objectArray[149] = 23;
        objectArray[150] = 23;
        objectArray[151] = 23;
        objectArray[152] = 23;
        objectArray[153] = 21;
        objectArray[154] = 22;
        objectArray[155] = 23;
        objectArray[156] = 22;
        objectArray[157] = 23;
        objectArray[158] = 23;
        objectArray[159] = 24;
        objectArray[160] = 22;
        objectArray[161] = 21;
        objectArray[162] = 20;
        objectArray[163] = 22;
        objectArray[164] = 22;
        objectArray[165] = 23;
        objectArray[166] = 23;
        objectArray[167] = 21;
        objectArray[168] = 23;
        objectArray[169] = 22;
        objectArray[170] = 22;
        objectArray[171] = 24;
        objectArray[172] = 21;
        objectArray[173] = 22;
        objectArray[174] = 23;
        objectArray[175] = 23;
        objectArray[176] = 21;
        objectArray[177] = 21;
        objectArray[178] = 22;
        objectArray[179] = 21;
        objectArray[180] = 23;
        objectArray[181] = 22;
        objectArray[182] = 23;
        objectArray[183] = 23;
        objectArray[184] = 20;
        objectArray[185] = 22;
        objectArray[186] = 22;
        objectArray[187] = 22;
        objectArray[188] = 23;
        objectArray[189] = 22;
        objectArray[190] = 22;
        objectArray[191] = 23;
        objectArray[192] = 26;
        objectArray[193] = 26;
        objectArray[194] = 20;
        objectArray[195] = 19;
        objectArray[196] = 22;
        objectArray[197] = 23;
        objectArray[198] = 22;
        objectArray[199] = 25;
        objectArray[200] = 26;
        objectArray[201] = 26;
        objectArray[202] = 26;
        objectArray[203] = 27;
        objectArray[204] = 27;
        objectArray[205] = 26;
        objectArray[206] = 24;
        objectArray[207] = 25;
        objectArray[208] = 19;
        objectArray[209] = 21;
        objectArray[210] = 26;
        objectArray[211] = 27;
        objectArray[212] = 27;
        objectArray[213] = 26;
        objectArray[214] = 27;
        objectArray[215] = 24;
        objectArray[216] = 21;
        objectArray[217] = 21;
        objectArray[218] = 26;
        objectArray[219] = 26;
        objectArray[220] = 28;
        objectArray[221] = 27;
        objectArray[222] = 27;
        objectArray[223] = 27;
        objectArray[224] = 20;
        objectArray[225] = 24;
        objectArray[226] = 20;
        objectArray[227] = 21;
        objectArray[228] = 22;
        objectArray[229] = 21;
        objectArray[230] = 21;
        objectArray[231] = 23;
        objectArray[232] = 22;
        objectArray[233] = 22;
        objectArray[234] = 25;
        objectArray[235] = 25;
        objectArray[236] = 24;
        objectArray[237] = 24;
        objectArray[238] = 26;
        objectArray[239] = 23;
        objectArray[240] = 26;
        objectArray[241] = 27;
        objectArray[242] = 26;
        objectArray[243] = 26;
        objectArray[244] = 27;
        objectArray[245] = 27;
        objectArray[246] = 27;
        objectArray[247] = 27;
        objectArray[248] = 27;
        objectArray[249] = 28;
        objectArray[250] = 27;
        objectArray[251] = 27;
        objectArray[252] = 27;
        objectArray[253] = 27;
        objectArray[254] = 27;
        objectArray[255] = 26;
        CODE_BIT_COUNTS = (byte[])objectArray;
        root = new Node();
        int n = CODE_BIT_COUNTS.length;
        for (int i = 0; i < n; ++i) {
            INSTANCE.addCode(i, CODES[i], CODE_BIT_COUNTS[i]);
        }
    }

    @Metadata(mv={1, 8, 0}, k=1, xi=48, d1={"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u0011\n\u0002\b\b\b\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0016\u00a2\u0006\u0002\u0010\u0002B\u0017\b\u0016\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0002\u0010\u0006R\u001d\u0010\u0007\u001a\f\u0012\u0006\u0012\u0004\u0018\u00010\u0000\u0018\u00010\b\u00a2\u0006\n\n\u0002\u0010\u000b\u001a\u0004\b\t\u0010\nR\u0011\u0010\u0003\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u000e\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\r\u00a8\u0006\u0010"}, d2={"Ldev/serverpanel/lib/okhttp3/internal/http2/Huffman$Node;", "", "()V", "symbol", "", "bits", "(II)V", "children", "", "getChildren", "()[Lokhttp3/internal/http2/Huffman$Node;", "[Ldev/serverpanel/lib/okhttp3/internal/http2/Huffman$Node;", "getSymbol", "()I", "terminalBitCount", "getTerminalBitCount", "okhttp"})
    private static final class Node {
        @Nullable
        private final Node[] children;
        private final int symbol;
        private final int terminalBitCount;

        @Nullable
        public final Node[] getChildren() {
            return this.children;
        }

        public final int getSymbol() {
            return this.symbol;
        }

        public final int getTerminalBitCount() {
            return this.terminalBitCount;
        }

        public Node() {
            this.children = new Node[256];
            this.symbol = 0;
            this.terminalBitCount = 0;
        }

        public Node(int symbol, int bits) {
            this.children = null;
            this.symbol = symbol;
            int b = bits & 7;
            this.terminalBitCount = b == 0 ? 8 : b;
        }
    }
}

