/*
 * Decompiled with CFR 0.152.
 */
package okio.internal;

import java.io.EOFException;
import kotlin.Metadata;
import kotlin.jvm.JvmName;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.CharsKt;
import okio.-SegmentedByteString;
import okio.Buffer;
import okio.BufferedSource;
import okio.ByteString;
import okio.Okio;
import okio.Options;
import okio.PeekSource;
import okio.RealBufferedSource;
import okio.Sink;
import okio.Timeout;
import okio.internal.-Buffer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 9, 0}, k=2, xi=48, d1={"\u0000j\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u0005\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u0012\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0010\n\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a\r\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\u0080\b\u001a\r\u0010\u0003\u001a\u00020\u0004*\u00020\u0002H\u0080\b\u001a%\u0010\u0005\u001a\u00020\u0006*\u00020\u00022\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\u00062\u0006\u0010\n\u001a\u00020\u0006H\u0080\b\u001a\u001d\u0010\u0005\u001a\u00020\u0006*\u00020\u00022\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\t\u001a\u00020\u0006H\u0080\b\u001a\u001d\u0010\r\u001a\u00020\u0006*\u00020\u00022\u0006\u0010\u000e\u001a\u00020\f2\u0006\u0010\t\u001a\u00020\u0006H\u0080\b\u001a\r\u0010\u000f\u001a\u00020\u0010*\u00020\u0002H\u0080\b\u001a-\u0010\u0011\u001a\u00020\u0004*\u00020\u00022\u0006\u0010\u0012\u001a\u00020\u00062\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u0014H\u0080\b\u001a%\u0010\u0016\u001a\u00020\u0014*\u00020\u00022\u0006\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0012\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u0014H\u0080\b\u001a\u001d\u0010\u0016\u001a\u00020\u0006*\u00020\u00022\u0006\u0010\u0017\u001a\u00020\u00192\u0006\u0010\u0015\u001a\u00020\u0006H\u0080\b\u001a\u0015\u0010\u001a\u001a\u00020\u0006*\u00020\u00022\u0006\u0010\u0017\u001a\u00020\u001bH\u0080\b\u001a\r\u0010\u001c\u001a\u00020\b*\u00020\u0002H\u0080\b\u001a\r\u0010\u001d\u001a\u00020\u0018*\u00020\u0002H\u0080\b\u001a\u0015\u0010\u001d\u001a\u00020\u0018*\u00020\u00022\u0006\u0010\u0015\u001a\u00020\u0006H\u0080\b\u001a\r\u0010\u001e\u001a\u00020\f*\u00020\u0002H\u0080\b\u001a\u0015\u0010\u001e\u001a\u00020\f*\u00020\u00022\u0006\u0010\u0015\u001a\u00020\u0006H\u0080\b\u001a\r\u0010\u001f\u001a\u00020\u0006*\u00020\u0002H\u0080\b\u001a\u0015\u0010 \u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0017\u001a\u00020\u0018H\u0080\b\u001a\u001d\u0010 \u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0017\u001a\u00020\u00192\u0006\u0010\u0015\u001a\u00020\u0006H\u0080\b\u001a\r\u0010!\u001a\u00020\u0006*\u00020\u0002H\u0080\b\u001a\r\u0010\"\u001a\u00020\u0014*\u00020\u0002H\u0080\b\u001a\r\u0010#\u001a\u00020\u0014*\u00020\u0002H\u0080\b\u001a\r\u0010$\u001a\u00020\u0006*\u00020\u0002H\u0080\b\u001a\r\u0010%\u001a\u00020\u0006*\u00020\u0002H\u0080\b\u001a\r\u0010&\u001a\u00020'*\u00020\u0002H\u0080\b\u001a\r\u0010(\u001a\u00020'*\u00020\u0002H\u0080\b\u001a\r\u0010)\u001a\u00020**\u00020\u0002H\u0080\b\u001a\u0015\u0010)\u001a\u00020**\u00020\u00022\u0006\u0010\u0015\u001a\u00020\u0006H\u0080\b\u001a\r\u0010+\u001a\u00020\u0014*\u00020\u0002H\u0080\b\u001a\u000f\u0010,\u001a\u0004\u0018\u00010**\u00020\u0002H\u0080\b\u001a\u0015\u0010-\u001a\u00020**\u00020\u00022\u0006\u0010.\u001a\u00020\u0006H\u0080\b\u001a\u0015\u0010/\u001a\u00020\u0004*\u00020\u00022\u0006\u0010\u0015\u001a\u00020\u0006H\u0080\b\u001a\u0015\u00100\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0015\u001a\u00020\u0006H\u0080\b\u001a\u0015\u00101\u001a\u00020\u0014*\u00020\u00022\u0006\u00102\u001a\u000203H\u0080\b\u001a\u0015\u00104\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0015\u001a\u00020\u0006H\u0080\b\u001a\r\u00105\u001a\u000206*\u00020\u0002H\u0080\b\u001a\r\u00107\u001a\u00020**\u00020\u0002H\u0080\b\u00a8\u00068"}, d2={"commonClose", "", "Lokio/RealBufferedSource;", "commonExhausted", "", "commonIndexOf", "", "b", "", "fromIndex", "toIndex", "bytes", "Lokio/ByteString;", "commonIndexOfElement", "targetBytes", "commonPeek", "Lokio/BufferedSource;", "commonRangeEquals", "offset", "bytesOffset", "", "byteCount", "commonRead", "sink", "", "Lokio/Buffer;", "commonReadAll", "Lokio/Sink;", "commonReadByte", "commonReadByteArray", "commonReadByteString", "commonReadDecimalLong", "commonReadFully", "commonReadHexadecimalUnsignedLong", "commonReadInt", "commonReadIntLe", "commonReadLong", "commonReadLongLe", "commonReadShort", "", "commonReadShortLe", "commonReadUtf8", "", "commonReadUtf8CodePoint", "commonReadUtf8Line", "commonReadUtf8LineStrict", "limit", "commonRequest", "commonRequire", "commonSelect", "options", "Lokio/Options;", "commonSkip", "commonTimeout", "Lokio/Timeout;", "commonToString", "okio"})
@JvmName(name="-RealBufferedSource")
@SourceDebugExtension(value={"SMAP\nRealBufferedSource.kt\nKotlin\n*S Kotlin\n*F\n+ 1 RealBufferedSource.kt\nokio/internal/-RealBufferedSource\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 RealBufferedSource.kt\nokio/RealBufferedSource\n+ 4 Util.kt\nokio/-SegmentedByteString\n*L\n1#1,402:1\n1#2:403\n62#3:404\n62#3:405\n62#3:406\n62#3:407\n62#3:408\n62#3:409\n62#3:410\n62#3:411\n62#3:412\n62#3:413\n62#3:414\n62#3:415\n62#3:416\n62#3:417\n62#3:418\n62#3:419\n62#3:420\n62#3:421\n62#3:422\n62#3:423\n62#3:424\n62#3:425\n62#3:426\n62#3:428\n62#3:429\n62#3:430\n62#3:431\n62#3:432\n62#3:433\n62#3:434\n62#3:435\n62#3:436\n62#3:437\n62#3:438\n62#3:439\n62#3:440\n62#3:441\n62#3:442\n62#3:443\n62#3:444\n62#3:445\n62#3:446\n62#3:447\n62#3:449\n62#3:450\n62#3:451\n62#3:452\n62#3:453\n62#3:454\n62#3:455\n62#3:456\n62#3:457\n62#3:458\n62#3:459\n62#3:460\n62#3:461\n62#3:462\n62#3:463\n62#3:464\n62#3:465\n62#3:466\n62#3:467\n62#3:468\n62#3:469\n62#3:470\n62#3:471\n62#3:472\n62#3:473\n62#3:474\n62#3:475\n89#4:427\n89#4:448\n*S KotlinDebug\n*F\n+ 1 RealBufferedSource.kt\nokio/internal/-RealBufferedSource\n*L\n41#1:404\n42#1:405\n46#1:406\n47#1:407\n52#1:408\n62#1:409\n63#1:410\n70#1:411\n74#1:412\n75#1:413\n80#1:414\n87#1:415\n94#1:416\n99#1:417\n107#1:418\n108#1:419\n113#1:420\n122#1:421\n123#1:422\n130#1:423\n136#1:424\n137#1:425\n141#1:426\n142#1:428\n150#1:429\n154#1:430\n159#1:431\n160#1:432\n163#1:433\n166#1:434\n167#1:435\n168#1:436\n174#1:437\n175#1:438\n180#1:439\n187#1:440\n188#1:441\n193#1:442\n201#1:443\n203#1:444\n204#1:445\n206#1:446\n209#1:447\n211#1:449\n219#1:450\n226#1:451\n231#1:452\n236#1:453\n241#1:454\n246#1:455\n251#1:456\n256#1:457\n264#1:458\n275#1:459\n283#1:460\n297#1:461\n304#1:462\n307#1:463\n308#1:464\n319#1:465\n324#1:466\n325#1:467\n338#1:468\n341#1:469\n342#1:470\n354#1:471\n357#1:472\n358#1:473\n383#1:474\n396#1:475\n141#1:427\n209#1:448\n*E\n"})
public final class -RealBufferedSource {
    public static final long commonRead(@NotNull RealBufferedSource $this$commonRead, @NotNull Buffer sink2, long byteCount) {
        boolean $i$f$getBuffer;
        RealBufferedSource this_$iv22;
        Intrinsics.checkNotNullParameter($this$commonRead, "<this>");
        Intrinsics.checkNotNullParameter(sink2, "sink");
        boolean $i$f$commonRead = false;
        if (!(byteCount >= 0L)) {
            boolean $i$a$-require--RealBufferedSource$commonRead$22 = false;
            String $i$a$-require--RealBufferedSource$commonRead$22 = "byteCount < 0: " + byteCount;
            throw new IllegalArgumentException($i$a$-require--RealBufferedSource$commonRead$22.toString());
        }
        if (!(!$this$commonRead.closed)) {
            boolean $i$a$-check--RealBufferedSource$commonRead$32 = false;
            String $i$a$-check--RealBufferedSource$commonRead$32 = "closed";
            throw new IllegalStateException($i$a$-check--RealBufferedSource$commonRead$32.toString());
        }
        RealBufferedSource this_$iv = $this$commonRead;
        boolean $i$f$getBuffer2 = false;
        if (this_$iv.bufferField.size() == 0L) {
            this_$iv22 = $this$commonRead;
            $i$f$getBuffer = false;
            long read = $this$commonRead.source.read(this_$iv22.bufferField, 8192L);
            if (read == -1L) {
                return -1L;
            }
        }
        this_$iv22 = $this$commonRead;
        $i$f$getBuffer = false;
        long this_$iv22 = this_$iv22.bufferField.size();
        long toRead = Math.min(byteCount, this_$iv22);
        RealBufferedSource this_$iv3 = $this$commonRead;
        $i$f$getBuffer = false;
        return this_$iv3.bufferField.read(sink2, toRead);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static final boolean commonExhausted(@NotNull RealBufferedSource $this$commonExhausted) {
        Intrinsics.checkNotNullParameter($this$commonExhausted, "<this>");
        boolean $i$f$commonExhausted = false;
        if (!(!$this$commonExhausted.closed)) {
            boolean $i$a$-check--RealBufferedSource$commonExhausted$22 = false;
            String $i$a$-check--RealBufferedSource$commonExhausted$22 = "closed";
            throw new IllegalStateException($i$a$-check--RealBufferedSource$commonExhausted$22.toString());
        }
        RealBufferedSource this_$iv = $this$commonExhausted;
        boolean $i$f$getBuffer = false;
        if (!this_$iv.bufferField.exhausted()) return false;
        this_$iv = $this$commonExhausted;
        $i$f$getBuffer = false;
        if ($this$commonExhausted.source.read(this_$iv.bufferField, 8192L) != -1L) return false;
        return true;
    }

    public static final void commonRequire(@NotNull RealBufferedSource $this$commonRequire, long byteCount) {
        Intrinsics.checkNotNullParameter($this$commonRequire, "<this>");
        boolean $i$f$commonRequire = false;
        if (!$this$commonRequire.request(byteCount)) {
            throw new EOFException();
        }
    }

    public static final boolean commonRequest(@NotNull RealBufferedSource $this$commonRequest, long byteCount) {
        block3: {
            RealBufferedSource this_$iv;
            Intrinsics.checkNotNullParameter($this$commonRequest, "<this>");
            boolean $i$f$commonRequest = false;
            if (!(byteCount >= 0L)) {
                boolean $i$a$-require--RealBufferedSource$commonRequest$22 = false;
                String $i$a$-require--RealBufferedSource$commonRequest$22 = "byteCount < 0: " + byteCount;
                throw new IllegalArgumentException($i$a$-require--RealBufferedSource$commonRequest$22.toString());
            }
            if (!(!$this$commonRequest.closed)) {
                boolean $i$a$-check--RealBufferedSource$commonRequest$32 = false;
                String $i$a$-check--RealBufferedSource$commonRequest$32 = "closed";
                throw new IllegalStateException($i$a$-check--RealBufferedSource$commonRequest$32.toString());
            }
            do {
                this_$iv = $this$commonRequest;
                boolean $i$f$getBuffer = false;
                if (this_$iv.bufferField.size() >= byteCount) break block3;
                this_$iv = $this$commonRequest;
                $i$f$getBuffer = false;
            } while ($this$commonRequest.source.read(this_$iv.bufferField, 8192L) != -1L);
            return false;
        }
        return true;
    }

    public static final byte commonReadByte(@NotNull RealBufferedSource $this$commonReadByte) {
        Intrinsics.checkNotNullParameter($this$commonReadByte, "<this>");
        boolean $i$f$commonReadByte = false;
        $this$commonReadByte.require(1L);
        RealBufferedSource this_$iv = $this$commonReadByte;
        boolean $i$f$getBuffer = false;
        return this_$iv.bufferField.readByte();
    }

    @NotNull
    public static final ByteString commonReadByteString(@NotNull RealBufferedSource $this$commonReadByteString) {
        Intrinsics.checkNotNullParameter($this$commonReadByteString, "<this>");
        boolean $i$f$commonReadByteString = false;
        RealBufferedSource this_$iv = $this$commonReadByteString;
        boolean $i$f$getBuffer = false;
        this_$iv.bufferField.writeAll($this$commonReadByteString.source);
        this_$iv = $this$commonReadByteString;
        $i$f$getBuffer = false;
        return this_$iv.bufferField.readByteString();
    }

    @NotNull
    public static final ByteString commonReadByteString(@NotNull RealBufferedSource $this$commonReadByteString, long byteCount) {
        Intrinsics.checkNotNullParameter($this$commonReadByteString, "<this>");
        boolean $i$f$commonReadByteString = false;
        $this$commonReadByteString.require(byteCount);
        RealBufferedSource this_$iv = $this$commonReadByteString;
        boolean $i$f$getBuffer = false;
        return this_$iv.bufferField.readByteString(byteCount);
    }

    public static final int commonSelect(@NotNull RealBufferedSource $this$commonSelect, @NotNull Options options) {
        int index;
        Intrinsics.checkNotNullParameter($this$commonSelect, "<this>");
        Intrinsics.checkNotNullParameter(options, "options");
        boolean $i$f$commonSelect = false;
        if (!(!$this$commonSelect.closed)) {
            boolean $i$a$-check--RealBufferedSource$commonSelect$22 = false;
            String $i$a$-check--RealBufferedSource$commonSelect$22 = "closed";
            throw new IllegalStateException($i$a$-check--RealBufferedSource$commonSelect$22.toString());
        }
        block4: while (true) {
            RealBufferedSource this_$iv = $this$commonSelect;
            boolean $i$f$getBuffer = false;
            index = -Buffer.selectPrefix(this_$iv.bufferField, options, true);
            switch (index) {
                case -1: {
                    return -1;
                }
                case -2: {
                    RealBufferedSource this_$iv2 = $this$commonSelect;
                    boolean $i$f$getBuffer2 = false;
                    if ($this$commonSelect.source.read(this_$iv2.bufferField, 8192L) != -1L) continue block4;
                    return -1;
                }
            }
            break;
        }
        int selectedSize = options.getByteStrings$okio()[index].size();
        RealBufferedSource this_$iv = $this$commonSelect;
        boolean $i$f$getBuffer = false;
        this_$iv.bufferField.skip(selectedSize);
        return index;
    }

    @NotNull
    public static final byte[] commonReadByteArray(@NotNull RealBufferedSource $this$commonReadByteArray) {
        Intrinsics.checkNotNullParameter($this$commonReadByteArray, "<this>");
        boolean $i$f$commonReadByteArray = false;
        RealBufferedSource this_$iv = $this$commonReadByteArray;
        boolean $i$f$getBuffer = false;
        this_$iv.bufferField.writeAll($this$commonReadByteArray.source);
        this_$iv = $this$commonReadByteArray;
        $i$f$getBuffer = false;
        return this_$iv.bufferField.readByteArray();
    }

    @NotNull
    public static final byte[] commonReadByteArray(@NotNull RealBufferedSource $this$commonReadByteArray, long byteCount) {
        Intrinsics.checkNotNullParameter($this$commonReadByteArray, "<this>");
        boolean $i$f$commonReadByteArray = false;
        $this$commonReadByteArray.require(byteCount);
        RealBufferedSource this_$iv = $this$commonReadByteArray;
        boolean $i$f$getBuffer = false;
        return this_$iv.bufferField.readByteArray(byteCount);
    }

    public static final void commonReadFully(@NotNull RealBufferedSource $this$commonReadFully, @NotNull byte[] sink2) {
        Intrinsics.checkNotNullParameter($this$commonReadFully, "<this>");
        Intrinsics.checkNotNullParameter(sink2, "sink");
        boolean $i$f$commonReadFully = false;
        try {
            $this$commonReadFully.require(sink2.length);
        }
        catch (EOFException e) {
            int offset = 0;
            while (true) {
                RealBufferedSource this_$iv = $this$commonReadFully;
                boolean $i$f$getBuffer = false;
                if (this_$iv.bufferField.size() <= 0L) break;
                RealBufferedSource this_$iv2 = $this$commonReadFully;
                boolean $i$f$getBuffer2 = false;
                this_$iv2 = $this$commonReadFully;
                $i$f$getBuffer2 = false;
                int read = this_$iv2.bufferField.read(sink2, offset, (int)this_$iv2.bufferField.size());
                if (read == -1) {
                    throw new AssertionError();
                }
                offset += read;
            }
            throw e;
        }
        RealBufferedSource this_$iv = $this$commonReadFully;
        boolean $i$f$getBuffer = false;
        this_$iv.bufferField.readFully(sink2);
    }

    public static final int commonRead(@NotNull RealBufferedSource $this$commonRead, @NotNull byte[] sink2, int offset, int byteCount) {
        Intrinsics.checkNotNullParameter($this$commonRead, "<this>");
        Intrinsics.checkNotNullParameter(sink2, "sink");
        boolean $i$f$commonRead = false;
        -SegmentedByteString.checkOffsetAndCount(sink2.length, offset, byteCount);
        RealBufferedSource this_$iv = $this$commonRead;
        boolean $i$f$getBuffer = false;
        if (this_$iv.bufferField.size() == 0L) {
            RealBufferedSource this_$iv2 = $this$commonRead;
            boolean $i$f$getBuffer2 = false;
            long read = $this$commonRead.source.read(this_$iv2.bufferField, 8192L);
            if (read == -1L) {
                return -1;
            }
        }
        RealBufferedSource this_$iv3 = $this$commonRead;
        boolean $i$f$getBuffer3 = false;
        long b$iv = this_$iv3.bufferField.size();
        boolean $i$f$minOf = false;
        int toRead = (int)Math.min((long)byteCount, b$iv);
        this_$iv = $this$commonRead;
        $i$f$getBuffer3 = false;
        return this_$iv.bufferField.read(sink2, offset, toRead);
    }

    public static final void commonReadFully(@NotNull RealBufferedSource $this$commonReadFully, @NotNull Buffer sink2, long byteCount) {
        Intrinsics.checkNotNullParameter($this$commonReadFully, "<this>");
        Intrinsics.checkNotNullParameter(sink2, "sink");
        boolean $i$f$commonReadFully = false;
        try {
            $this$commonReadFully.require(byteCount);
        }
        catch (EOFException e) {
            RealBufferedSource this_$iv = $this$commonReadFully;
            boolean $i$f$getBuffer = false;
            sink2.writeAll(this_$iv.bufferField);
            throw e;
        }
        RealBufferedSource this_$iv = $this$commonReadFully;
        boolean $i$f$getBuffer = false;
        this_$iv.bufferField.readFully(sink2, byteCount);
    }

    public static final long commonReadAll(@NotNull RealBufferedSource $this$commonReadAll, @NotNull Sink sink2) {
        boolean $i$f$getBuffer;
        RealBufferedSource this_$iv;
        Intrinsics.checkNotNullParameter($this$commonReadAll, "<this>");
        Intrinsics.checkNotNullParameter(sink2, "sink");
        boolean $i$f$commonReadAll = false;
        long totalBytesWritten = 0L;
        while (true) {
            this_$iv = $this$commonReadAll;
            $i$f$getBuffer = false;
            if ($this$commonReadAll.source.read(this_$iv.bufferField, 8192L) == -1L) break;
            RealBufferedSource this_$iv2 = $this$commonReadAll;
            boolean $i$f$getBuffer2 = false;
            long emitByteCount = this_$iv2.bufferField.completeSegmentByteCount();
            if (emitByteCount <= 0L) continue;
            totalBytesWritten += emitByteCount;
            this_$iv2 = $this$commonReadAll;
            $i$f$getBuffer2 = false;
            sink2.write(this_$iv2.bufferField, emitByteCount);
        }
        this_$iv = $this$commonReadAll;
        $i$f$getBuffer = false;
        if (this_$iv.bufferField.size() > 0L) {
            this_$iv = $this$commonReadAll;
            $i$f$getBuffer = false;
            totalBytesWritten += this_$iv.bufferField.size();
            this_$iv = $this$commonReadAll;
            $i$f$getBuffer = false;
            this_$iv = $this$commonReadAll;
            $i$f$getBuffer = false;
            sink2.write(this_$iv.bufferField, this_$iv.bufferField.size());
        }
        return totalBytesWritten;
    }

    @NotNull
    public static final String commonReadUtf8(@NotNull RealBufferedSource $this$commonReadUtf8) {
        Intrinsics.checkNotNullParameter($this$commonReadUtf8, "<this>");
        boolean $i$f$commonReadUtf8 = false;
        RealBufferedSource this_$iv = $this$commonReadUtf8;
        boolean $i$f$getBuffer = false;
        this_$iv.bufferField.writeAll($this$commonReadUtf8.source);
        this_$iv = $this$commonReadUtf8;
        $i$f$getBuffer = false;
        return this_$iv.bufferField.readUtf8();
    }

    @NotNull
    public static final String commonReadUtf8(@NotNull RealBufferedSource $this$commonReadUtf8, long byteCount) {
        Intrinsics.checkNotNullParameter($this$commonReadUtf8, "<this>");
        boolean $i$f$commonReadUtf8 = false;
        $this$commonReadUtf8.require(byteCount);
        RealBufferedSource this_$iv = $this$commonReadUtf8;
        boolean $i$f$getBuffer = false;
        return this_$iv.bufferField.readUtf8(byteCount);
    }

    @Nullable
    public static final String commonReadUtf8Line(@NotNull RealBufferedSource $this$commonReadUtf8Line) {
        String string;
        Intrinsics.checkNotNullParameter($this$commonReadUtf8Line, "<this>");
        boolean $i$f$commonReadUtf8Line = false;
        long newline = $this$commonReadUtf8Line.indexOf((byte)10);
        if (newline == -1L) {
            RealBufferedSource this_$iv = $this$commonReadUtf8Line;
            boolean $i$f$getBuffer = false;
            if (this_$iv.bufferField.size() != 0L) {
                this_$iv = $this$commonReadUtf8Line;
                $i$f$getBuffer = false;
                string = $this$commonReadUtf8Line.readUtf8(this_$iv.bufferField.size());
            } else {
                string = null;
            }
        } else {
            RealBufferedSource this_$iv = $this$commonReadUtf8Line;
            boolean $i$f$getBuffer = false;
            string = -Buffer.readUtf8Line(this_$iv.bufferField, newline);
        }
        return string;
    }

    /*
     * WARNING - void declaration
     */
    @NotNull
    public static final String commonReadUtf8LineStrict(@NotNull RealBufferedSource $this$commonReadUtf8LineStrict, long limit) {
        void a$iv;
        Intrinsics.checkNotNullParameter($this$commonReadUtf8LineStrict, "<this>");
        boolean $i$f$commonReadUtf8LineStrict = false;
        if (!(limit >= 0L)) {
            boolean bl = false;
            String string = "limit < 0: " + limit;
            throw new IllegalArgumentException(string.toString());
        }
        long scanLength = limit == Long.MAX_VALUE ? Long.MAX_VALUE : limit + 1L;
        long newline = $this$commonReadUtf8LineStrict.indexOf((byte)10, 0L, scanLength);
        if (newline != -1L) {
            RealBufferedSource this_$iv = $this$commonReadUtf8LineStrict;
            boolean $i$f$getBuffer = false;
            return -Buffer.readUtf8Line(this_$iv.bufferField, newline);
        }
        if (scanLength < Long.MAX_VALUE && $this$commonReadUtf8LineStrict.request(scanLength)) {
            RealBufferedSource this_$iv = $this$commonReadUtf8LineStrict;
            boolean $i$f$getBuffer = false;
            if (this_$iv.bufferField.getByte(scanLength - 1L) == 13 && $this$commonReadUtf8LineStrict.request(scanLength + 1L)) {
                this_$iv = $this$commonReadUtf8LineStrict;
                $i$f$getBuffer = false;
                if (this_$iv.bufferField.getByte(scanLength) == 10) {
                    this_$iv = $this$commonReadUtf8LineStrict;
                    $i$f$getBuffer = false;
                    return -Buffer.readUtf8Line(this_$iv.bufferField, scanLength);
                }
            }
        }
        Buffer data = new Buffer();
        RealBufferedSource this_$iv22 = $this$commonReadUtf8LineStrict;
        boolean $i$f$getBuffer = false;
        int this_$iv22 = 32;
        RealBufferedSource this_$iv = $this$commonReadUtf8LineStrict;
        boolean $i$f$getBuffer2 = false;
        long b$iv = this_$iv.bufferField.size();
        boolean $i$f$minOf = false;
        this_$iv22.bufferField.copyTo(data, 0L, Math.min((long)a$iv, b$iv));
        RealBufferedSource this_$iv3 = $this$commonReadUtf8LineStrict;
        $i$f$getBuffer = false;
        throw new EOFException("\\n not found: limit=" + Math.min(this_$iv3.bufferField.size(), limit) + " content=" + data.readByteString().hex() + '\u2026');
    }

    public static final int commonReadUtf8CodePoint(@NotNull RealBufferedSource $this$commonReadUtf8CodePoint) {
        Intrinsics.checkNotNullParameter($this$commonReadUtf8CodePoint, "<this>");
        boolean $i$f$commonReadUtf8CodePoint = false;
        $this$commonReadUtf8CodePoint.require(1L);
        RealBufferedSource this_$iv = $this$commonReadUtf8CodePoint;
        boolean $i$f$getBuffer = false;
        byte b0 = this_$iv.bufferField.getByte(0L);
        if ((b0 & 0xE0) == 192) {
            $this$commonReadUtf8CodePoint.require(2L);
        } else if ((b0 & 0xF0) == 224) {
            $this$commonReadUtf8CodePoint.require(3L);
        } else if ((b0 & 0xF8) == 240) {
            $this$commonReadUtf8CodePoint.require(4L);
        }
        this_$iv = $this$commonReadUtf8CodePoint;
        $i$f$getBuffer = false;
        return this_$iv.bufferField.readUtf8CodePoint();
    }

    public static final short commonReadShort(@NotNull RealBufferedSource $this$commonReadShort) {
        Intrinsics.checkNotNullParameter($this$commonReadShort, "<this>");
        boolean $i$f$commonReadShort = false;
        $this$commonReadShort.require(2L);
        RealBufferedSource this_$iv = $this$commonReadShort;
        boolean $i$f$getBuffer = false;
        return this_$iv.bufferField.readShort();
    }

    public static final short commonReadShortLe(@NotNull RealBufferedSource $this$commonReadShortLe) {
        Intrinsics.checkNotNullParameter($this$commonReadShortLe, "<this>");
        boolean $i$f$commonReadShortLe = false;
        $this$commonReadShortLe.require(2L);
        RealBufferedSource this_$iv = $this$commonReadShortLe;
        boolean $i$f$getBuffer = false;
        return this_$iv.bufferField.readShortLe();
    }

    public static final int commonReadInt(@NotNull RealBufferedSource $this$commonReadInt) {
        Intrinsics.checkNotNullParameter($this$commonReadInt, "<this>");
        boolean $i$f$commonReadInt = false;
        $this$commonReadInt.require(4L);
        RealBufferedSource this_$iv = $this$commonReadInt;
        boolean $i$f$getBuffer = false;
        return this_$iv.bufferField.readInt();
    }

    public static final int commonReadIntLe(@NotNull RealBufferedSource $this$commonReadIntLe) {
        Intrinsics.checkNotNullParameter($this$commonReadIntLe, "<this>");
        boolean $i$f$commonReadIntLe = false;
        $this$commonReadIntLe.require(4L);
        RealBufferedSource this_$iv = $this$commonReadIntLe;
        boolean $i$f$getBuffer = false;
        return this_$iv.bufferField.readIntLe();
    }

    public static final long commonReadLong(@NotNull RealBufferedSource $this$commonReadLong) {
        Intrinsics.checkNotNullParameter($this$commonReadLong, "<this>");
        boolean $i$f$commonReadLong = false;
        $this$commonReadLong.require(8L);
        RealBufferedSource this_$iv = $this$commonReadLong;
        boolean $i$f$getBuffer = false;
        return this_$iv.bufferField.readLong();
    }

    public static final long commonReadLongLe(@NotNull RealBufferedSource $this$commonReadLongLe) {
        Intrinsics.checkNotNullParameter($this$commonReadLongLe, "<this>");
        boolean $i$f$commonReadLongLe = false;
        $this$commonReadLongLe.require(8L);
        RealBufferedSource this_$iv = $this$commonReadLongLe;
        boolean $i$f$getBuffer = false;
        return this_$iv.bufferField.readLongLe();
    }

    public static final long commonReadDecimalLong(@NotNull RealBufferedSource $this$commonReadDecimalLong) {
        Intrinsics.checkNotNullParameter($this$commonReadDecimalLong, "<this>");
        boolean $i$f$commonReadDecimalLong = false;
        $this$commonReadDecimalLong.require(1L);
        long pos = 0L;
        while ($this$commonReadDecimalLong.request(pos + 1L)) {
            RealBufferedSource this_$iv22 = $this$commonReadDecimalLong;
            boolean $i$f$getBuffer = false;
            byte b = this_$iv22.bufferField.getByte(pos);
            if (!(b >= 48 && b <= 57 || pos == 0L && b == 45)) {
                if (pos != 0L) break;
                StringBuilder stringBuilder = new StringBuilder().append("Expected a digit or '-' but was 0x");
                String string = Integer.toString(b, CharsKt.checkRadix(CharsKt.checkRadix(16)));
                Intrinsics.checkNotNullExpressionValue(string, "toString(this, checkRadix(radix))");
                throw new NumberFormatException(stringBuilder.append(string).toString());
            }
            long this_$iv22 = pos;
            pos = this_$iv22 + 1L;
        }
        RealBufferedSource this_$iv = $this$commonReadDecimalLong;
        boolean $i$f$getBuffer = false;
        return this_$iv.bufferField.readDecimalLong();
    }

    public static final long commonReadHexadecimalUnsignedLong(@NotNull RealBufferedSource $this$commonReadHexadecimalUnsignedLong) {
        Intrinsics.checkNotNullParameter($this$commonReadHexadecimalUnsignedLong, "<this>");
        boolean $i$f$commonReadHexadecimalUnsignedLong = false;
        $this$commonReadHexadecimalUnsignedLong.require(1L);
        int pos = 0;
        while ($this$commonReadHexadecimalUnsignedLong.request(pos + 1)) {
            RealBufferedSource this_$iv = $this$commonReadHexadecimalUnsignedLong;
            boolean $i$f$getBuffer = false;
            byte b = this_$iv.bufferField.getByte(pos);
            if (!(b >= 48 && b <= 57 || b >= 97 && b <= 102 || b >= 65 && b <= 70)) {
                if (pos != 0) break;
                StringBuilder stringBuilder = new StringBuilder().append("Expected leading [0-9a-fA-F] character but was 0x");
                String string = Integer.toString(b, CharsKt.checkRadix(CharsKt.checkRadix(16)));
                Intrinsics.checkNotNullExpressionValue(string, "toString(this, checkRadix(radix))");
                throw new NumberFormatException(stringBuilder.append(string).toString());
            }
            ++pos;
        }
        RealBufferedSource this_$iv = $this$commonReadHexadecimalUnsignedLong;
        boolean $i$f$getBuffer = false;
        return this_$iv.bufferField.readHexadecimalUnsignedLong();
    }

    public static final void commonSkip(@NotNull RealBufferedSource $this$commonSkip, long byteCount) {
        long toSkip;
        Intrinsics.checkNotNullParameter($this$commonSkip, "<this>");
        boolean $i$f$commonSkip = false;
        if (!(!$this$commonSkip.closed)) {
            boolean $i$a$-check--RealBufferedSource$commonSkip$22 = false;
            String $i$a$-check--RealBufferedSource$commonSkip$22 = "closed";
            throw new IllegalStateException($i$a$-check--RealBufferedSource$commonSkip$22.toString());
        }
        for (long byteCount2 = byteCount; byteCount2 > 0L; byteCount2 -= toSkip) {
            RealBufferedSource this_$iv = $this$commonSkip;
            boolean $i$f$getBuffer = false;
            if (this_$iv.bufferField.size() == 0L) {
                this_$iv = $this$commonSkip;
                $i$f$getBuffer = false;
                if ($this$commonSkip.source.read(this_$iv.bufferField, 8192L) == -1L) {
                    throw new EOFException();
                }
            }
            RealBufferedSource this_$iv32 = $this$commonSkip;
            boolean $i$f$getBuffer2 = false;
            long this_$iv32 = this_$iv32.bufferField.size();
            toSkip = Math.min(byteCount2, this_$iv32);
            this_$iv32 = $this$commonSkip;
            $i$f$getBuffer2 = false;
            this_$iv32.bufferField.skip(toSkip);
        }
    }

    public static final long commonIndexOf(@NotNull RealBufferedSource $this$commonIndexOf, byte b, long fromIndex, long toIndex) {
        Intrinsics.checkNotNullParameter($this$commonIndexOf, "<this>");
        boolean $i$f$commonIndexOf = false;
        long fromIndex2 = 0L;
        fromIndex2 = fromIndex;
        if (!(!$this$commonIndexOf.closed)) {
            boolean $i$a$-check--RealBufferedSource$commonIndexOf$22 = false;
            String $i$a$-check--RealBufferedSource$commonIndexOf$22 = "closed";
            throw new IllegalStateException($i$a$-check--RealBufferedSource$commonIndexOf$22.toString());
        }
        if (!(0L <= fromIndex2 ? fromIndex2 <= toIndex : false)) {
            boolean bl = false;
            String string = "fromIndex=" + fromIndex2 + " toIndex=" + toIndex;
            throw new IllegalArgumentException(string.toString());
        }
        while (fromIndex2 < toIndex) {
            long lastBufferSize;
            block8: {
                block7: {
                    RealBufferedSource this_$iv = $this$commonIndexOf;
                    boolean $i$f$getBuffer = false;
                    long result = this_$iv.bufferField.indexOf(b, fromIndex2, toIndex);
                    if (result != -1L) {
                        return result;
                    }
                    RealBufferedSource this_$iv2 = $this$commonIndexOf;
                    boolean $i$f$getBuffer2 = false;
                    lastBufferSize = this_$iv2.bufferField.size();
                    if (lastBufferSize >= toIndex) break block7;
                    this_$iv2 = $this$commonIndexOf;
                    $i$f$getBuffer2 = false;
                    if ($this$commonIndexOf.source.read(this_$iv2.bufferField, 8192L) != -1L) break block8;
                }
                return -1L;
            }
            fromIndex2 = Math.max(fromIndex2, lastBufferSize);
        }
        return -1L;
    }

    public static final long commonIndexOf(@NotNull RealBufferedSource $this$commonIndexOf, @NotNull ByteString bytes, long fromIndex) {
        Intrinsics.checkNotNullParameter($this$commonIndexOf, "<this>");
        Intrinsics.checkNotNullParameter(bytes, "bytes");
        boolean $i$f$commonIndexOf = false;
        long fromIndex2 = fromIndex;
        if (!(!$this$commonIndexOf.closed)) {
            boolean bl = false;
            String string = "closed";
            throw new IllegalStateException(string.toString());
        }
        while (true) {
            RealBufferedSource this_$iv = $this$commonIndexOf;
            boolean $i$f$getBuffer = false;
            long result = this_$iv.bufferField.indexOf(bytes, fromIndex2);
            if (result != -1L) {
                return result;
            }
            RealBufferedSource this_$iv2 = $this$commonIndexOf;
            boolean $i$f$getBuffer2 = false;
            long lastBufferSize = this_$iv2.bufferField.size();
            this_$iv2 = $this$commonIndexOf;
            $i$f$getBuffer2 = false;
            if ($this$commonIndexOf.source.read(this_$iv2.bufferField, 8192L) == -1L) {
                return -1L;
            }
            fromIndex2 = Math.max(fromIndex2, lastBufferSize - (long)bytes.size() + 1L);
        }
    }

    public static final long commonIndexOfElement(@NotNull RealBufferedSource $this$commonIndexOfElement, @NotNull ByteString targetBytes, long fromIndex) {
        Intrinsics.checkNotNullParameter($this$commonIndexOfElement, "<this>");
        Intrinsics.checkNotNullParameter(targetBytes, "targetBytes");
        boolean $i$f$commonIndexOfElement = false;
        long fromIndex2 = fromIndex;
        if (!(!$this$commonIndexOfElement.closed)) {
            boolean bl = false;
            String string = "closed";
            throw new IllegalStateException(string.toString());
        }
        while (true) {
            RealBufferedSource this_$iv = $this$commonIndexOfElement;
            boolean $i$f$getBuffer = false;
            long result = this_$iv.bufferField.indexOfElement(targetBytes, fromIndex2);
            if (result != -1L) {
                return result;
            }
            RealBufferedSource this_$iv2 = $this$commonIndexOfElement;
            boolean $i$f$getBuffer2 = false;
            long lastBufferSize = this_$iv2.bufferField.size();
            this_$iv2 = $this$commonIndexOfElement;
            $i$f$getBuffer2 = false;
            if ($this$commonIndexOfElement.source.read(this_$iv2.bufferField, 8192L) == -1L) {
                return -1L;
            }
            fromIndex2 = Math.max(fromIndex2, lastBufferSize);
        }
    }

    public static final boolean commonRangeEquals(@NotNull RealBufferedSource $this$commonRangeEquals, long offset, @NotNull ByteString bytes, int bytesOffset, int byteCount) {
        Intrinsics.checkNotNullParameter($this$commonRangeEquals, "<this>");
        Intrinsics.checkNotNullParameter(bytes, "bytes");
        boolean $i$f$commonRangeEquals = false;
        if (!(!$this$commonRangeEquals.closed)) {
            boolean $i$a$-check--RealBufferedSource$commonRangeEquals$22 = false;
            String $i$a$-check--RealBufferedSource$commonRangeEquals$22 = "closed";
            throw new IllegalStateException($i$a$-check--RealBufferedSource$commonRangeEquals$22.toString());
        }
        if (offset < 0L || bytesOffset < 0 || byteCount < 0 || bytes.size() - bytesOffset < byteCount) {
            return false;
        }
        for (int i = 0; i < byteCount; ++i) {
            long bufferOffset = offset + (long)i;
            if (!$this$commonRangeEquals.request(bufferOffset + 1L)) {
                return false;
            }
            RealBufferedSource this_$iv = $this$commonRangeEquals;
            boolean $i$f$getBuffer = false;
            if (this_$iv.bufferField.getByte(bufferOffset) == bytes.getByte(bytesOffset + i)) continue;
            return false;
        }
        return true;
    }

    @NotNull
    public static final BufferedSource commonPeek(@NotNull RealBufferedSource $this$commonPeek) {
        Intrinsics.checkNotNullParameter($this$commonPeek, "<this>");
        boolean $i$f$commonPeek = false;
        return Okio.buffer(new PeekSource($this$commonPeek));
    }

    public static final void commonClose(@NotNull RealBufferedSource $this$commonClose) {
        Intrinsics.checkNotNullParameter($this$commonClose, "<this>");
        boolean $i$f$commonClose = false;
        if ($this$commonClose.closed) {
            return;
        }
        $this$commonClose.closed = true;
        $this$commonClose.source.close();
        RealBufferedSource this_$iv = $this$commonClose;
        boolean $i$f$getBuffer = false;
        this_$iv.bufferField.clear();
    }

    @NotNull
    public static final Timeout commonTimeout(@NotNull RealBufferedSource $this$commonTimeout) {
        Intrinsics.checkNotNullParameter($this$commonTimeout, "<this>");
        boolean $i$f$commonTimeout = false;
        return $this$commonTimeout.source.timeout();
    }

    @NotNull
    public static final String commonToString(@NotNull RealBufferedSource $this$commonToString) {
        Intrinsics.checkNotNullParameter($this$commonToString, "<this>");
        boolean $i$f$commonToString = false;
        return "buffer(" + $this$commonToString.source + ')';
    }
}

