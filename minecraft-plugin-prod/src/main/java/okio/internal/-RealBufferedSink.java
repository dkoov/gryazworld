/*
 * Decompiled with CFR 0.152.
 */
package okio.internal;

import java.io.EOFException;
import kotlin.Metadata;
import kotlin.jvm.JvmName;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import okio.Buffer;
import okio.BufferedSink;
import okio.ByteString;
import okio.RealBufferedSink;
import okio.Source;
import okio.Timeout;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={1, 9, 0}, k=2, xi=48, d1={"\u0000D\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0012\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0015\u001a\r\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\u0080\b\u001a\r\u0010\u0003\u001a\u00020\u0004*\u00020\u0002H\u0080\b\u001a\r\u0010\u0005\u001a\u00020\u0004*\u00020\u0002H\u0080\b\u001a\r\u0010\u0006\u001a\u00020\u0001*\u00020\u0002H\u0080\b\u001a\r\u0010\u0007\u001a\u00020\b*\u00020\u0002H\u0080\b\u001a\r\u0010\t\u001a\u00020\n*\u00020\u0002H\u0080\b\u001a\u0015\u0010\u000b\u001a\u00020\u0004*\u00020\u00022\u0006\u0010\f\u001a\u00020\rH\u0080\b\u001a%\u0010\u000b\u001a\u00020\u0004*\u00020\u00022\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u000fH\u0080\b\u001a\u001d\u0010\u000b\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\f\u001a\u00020\u00112\u0006\u0010\u0010\u001a\u00020\u0012H\u0080\b\u001a\u0015\u0010\u000b\u001a\u00020\u0004*\u00020\u00022\u0006\u0010\u0013\u001a\u00020\u0014H\u0080\b\u001a%\u0010\u000b\u001a\u00020\u0004*\u00020\u00022\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u000fH\u0080\b\u001a\u001d\u0010\u000b\u001a\u00020\u0004*\u00020\u00022\u0006\u0010\f\u001a\u00020\u00152\u0006\u0010\u0010\u001a\u00020\u0012H\u0080\b\u001a\u0015\u0010\u0016\u001a\u00020\u0012*\u00020\u00022\u0006\u0010\f\u001a\u00020\u0015H\u0080\b\u001a\u0015\u0010\u0017\u001a\u00020\u0004*\u00020\u00022\u0006\u0010\u0018\u001a\u00020\u000fH\u0080\b\u001a\u0015\u0010\u0019\u001a\u00020\u0004*\u00020\u00022\u0006\u0010\u001a\u001a\u00020\u0012H\u0080\b\u001a\u0015\u0010\u001b\u001a\u00020\u0004*\u00020\u00022\u0006\u0010\u001a\u001a\u00020\u0012H\u0080\b\u001a\u0015\u0010\u001c\u001a\u00020\u0004*\u00020\u00022\u0006\u0010\u001d\u001a\u00020\u000fH\u0080\b\u001a\u0015\u0010\u001e\u001a\u00020\u0004*\u00020\u00022\u0006\u0010\u001d\u001a\u00020\u000fH\u0080\b\u001a\u0015\u0010\u001f\u001a\u00020\u0004*\u00020\u00022\u0006\u0010\u001a\u001a\u00020\u0012H\u0080\b\u001a\u0015\u0010 \u001a\u00020\u0004*\u00020\u00022\u0006\u0010\u001a\u001a\u00020\u0012H\u0080\b\u001a\u0015\u0010!\u001a\u00020\u0004*\u00020\u00022\u0006\u0010\"\u001a\u00020\u000fH\u0080\b\u001a\u0015\u0010#\u001a\u00020\u0004*\u00020\u00022\u0006\u0010\"\u001a\u00020\u000fH\u0080\b\u001a\u0015\u0010$\u001a\u00020\u0004*\u00020\u00022\u0006\u0010%\u001a\u00020\nH\u0080\b\u001a%\u0010$\u001a\u00020\u0004*\u00020\u00022\u0006\u0010%\u001a\u00020\n2\u0006\u0010&\u001a\u00020\u000f2\u0006\u0010'\u001a\u00020\u000fH\u0080\b\u001a\u0015\u0010(\u001a\u00020\u0004*\u00020\u00022\u0006\u0010)\u001a\u00020\u000fH\u0080\b\u00a8\u0006*"}, d2={"commonClose", "", "Lokio/RealBufferedSink;", "commonEmit", "Lokio/BufferedSink;", "commonEmitCompleteSegments", "commonFlush", "commonTimeout", "Lokio/Timeout;", "commonToString", "", "commonWrite", "source", "", "offset", "", "byteCount", "Lokio/Buffer;", "", "byteString", "Lokio/ByteString;", "Lokio/Source;", "commonWriteAll", "commonWriteByte", "b", "commonWriteDecimalLong", "v", "commonWriteHexadecimalUnsignedLong", "commonWriteInt", "i", "commonWriteIntLe", "commonWriteLong", "commonWriteLongLe", "commonWriteShort", "s", "commonWriteShortLe", "commonWriteUtf8", "string", "beginIndex", "endIndex", "commonWriteUtf8CodePoint", "codePoint", "okio"})
@JvmName(name="-RealBufferedSink")
@SourceDebugExtension(value={"SMAP\nRealBufferedSink.kt\nKotlin\n*S Kotlin\n*F\n+ 1 RealBufferedSink.kt\nokio/internal/-RealBufferedSink\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 RealBufferedSink.kt\nokio/RealBufferedSink\n*L\n1#1,219:1\n1#2:220\n51#3:221\n51#3:222\n51#3:223\n51#3:224\n51#3:225\n51#3:226\n51#3:227\n51#3:228\n51#3:229\n51#3:230\n51#3:231\n51#3:232\n51#3:233\n51#3:234\n51#3:235\n51#3:236\n51#3:237\n51#3:238\n51#3:239\n51#3:240\n51#3:241\n51#3:242\n51#3:243\n51#3:244\n51#3:245\n51#3:246\n51#3:247\n*S KotlinDebug\n*F\n+ 1 RealBufferedSink.kt\nokio/internal/-RealBufferedSink\n*L\n35#1:221\n41#1:222\n51#1:223\n57#1:224\n67#1:225\n73#1:226\n79#1:227\n89#1:228\n96#1:229\n107#1:230\n117#1:231\n123#1:232\n129#1:233\n135#1:234\n141#1:235\n147#1:236\n153#1:237\n159#1:238\n165#1:239\n171#1:240\n172#1:241\n178#1:242\n179#1:243\n185#1:244\n186#1:245\n198#1:246\n199#1:247\n*E\n"})
public final class -RealBufferedSink {
    public static final void commonWrite(@NotNull RealBufferedSink $this$commonWrite, @NotNull Buffer source2, long byteCount) {
        Intrinsics.checkNotNullParameter($this$commonWrite, "<this>");
        Intrinsics.checkNotNullParameter(source2, "source");
        boolean $i$f$commonWrite = false;
        if (!(!$this$commonWrite.closed)) {
            boolean $i$a$-check--RealBufferedSink$commonWrite$22 = false;
            String $i$a$-check--RealBufferedSink$commonWrite$22 = "closed";
            throw new IllegalStateException($i$a$-check--RealBufferedSink$commonWrite$22.toString());
        }
        RealBufferedSink this_$iv = $this$commonWrite;
        boolean $i$f$getBuffer = false;
        this_$iv.bufferField.write(source2, byteCount);
        $this$commonWrite.emitCompleteSegments();
    }

    @NotNull
    public static final BufferedSink commonWrite(@NotNull RealBufferedSink $this$commonWrite, @NotNull ByteString byteString) {
        Intrinsics.checkNotNullParameter($this$commonWrite, "<this>");
        Intrinsics.checkNotNullParameter(byteString, "byteString");
        boolean $i$f$commonWrite = false;
        if (!(!$this$commonWrite.closed)) {
            boolean $i$a$-check--RealBufferedSink$commonWrite$32 = false;
            String $i$a$-check--RealBufferedSink$commonWrite$32 = "closed";
            throw new IllegalStateException($i$a$-check--RealBufferedSink$commonWrite$32.toString());
        }
        RealBufferedSink this_$iv = $this$commonWrite;
        boolean $i$f$getBuffer = false;
        this_$iv.bufferField.write(byteString);
        return $this$commonWrite.emitCompleteSegments();
    }

    @NotNull
    public static final BufferedSink commonWrite(@NotNull RealBufferedSink $this$commonWrite, @NotNull ByteString byteString, int offset, int byteCount) {
        Intrinsics.checkNotNullParameter($this$commonWrite, "<this>");
        Intrinsics.checkNotNullParameter(byteString, "byteString");
        boolean $i$f$commonWrite = false;
        if (!(!$this$commonWrite.closed)) {
            boolean $i$a$-check--RealBufferedSink$commonWrite$42 = false;
            String $i$a$-check--RealBufferedSink$commonWrite$42 = "closed";
            throw new IllegalStateException($i$a$-check--RealBufferedSink$commonWrite$42.toString());
        }
        RealBufferedSink this_$iv = $this$commonWrite;
        boolean $i$f$getBuffer = false;
        this_$iv.bufferField.write(byteString, offset, byteCount);
        return $this$commonWrite.emitCompleteSegments();
    }

    @NotNull
    public static final BufferedSink commonWriteUtf8(@NotNull RealBufferedSink $this$commonWriteUtf8, @NotNull String string) {
        Intrinsics.checkNotNullParameter($this$commonWriteUtf8, "<this>");
        Intrinsics.checkNotNullParameter(string, "string");
        boolean $i$f$commonWriteUtf8 = false;
        if (!(!$this$commonWriteUtf8.closed)) {
            boolean $i$a$-check--RealBufferedSink$commonWriteUtf8$22 = false;
            String $i$a$-check--RealBufferedSink$commonWriteUtf8$22 = "closed";
            throw new IllegalStateException($i$a$-check--RealBufferedSink$commonWriteUtf8$22.toString());
        }
        RealBufferedSink this_$iv = $this$commonWriteUtf8;
        boolean $i$f$getBuffer = false;
        this_$iv.bufferField.writeUtf8(string);
        return $this$commonWriteUtf8.emitCompleteSegments();
    }

    @NotNull
    public static final BufferedSink commonWriteUtf8(@NotNull RealBufferedSink $this$commonWriteUtf8, @NotNull String string, int beginIndex, int endIndex) {
        Intrinsics.checkNotNullParameter($this$commonWriteUtf8, "<this>");
        Intrinsics.checkNotNullParameter(string, "string");
        boolean $i$f$commonWriteUtf8 = false;
        if (!(!$this$commonWriteUtf8.closed)) {
            boolean $i$a$-check--RealBufferedSink$commonWriteUtf8$32 = false;
            String $i$a$-check--RealBufferedSink$commonWriteUtf8$32 = "closed";
            throw new IllegalStateException($i$a$-check--RealBufferedSink$commonWriteUtf8$32.toString());
        }
        RealBufferedSink this_$iv = $this$commonWriteUtf8;
        boolean $i$f$getBuffer = false;
        this_$iv.bufferField.writeUtf8(string, beginIndex, endIndex);
        return $this$commonWriteUtf8.emitCompleteSegments();
    }

    @NotNull
    public static final BufferedSink commonWriteUtf8CodePoint(@NotNull RealBufferedSink $this$commonWriteUtf8CodePoint, int codePoint) {
        Intrinsics.checkNotNullParameter($this$commonWriteUtf8CodePoint, "<this>");
        boolean $i$f$commonWriteUtf8CodePoint = false;
        if (!(!$this$commonWriteUtf8CodePoint.closed)) {
            boolean $i$a$-check--RealBufferedSink$commonWriteUtf8CodePoint$22 = false;
            String $i$a$-check--RealBufferedSink$commonWriteUtf8CodePoint$22 = "closed";
            throw new IllegalStateException($i$a$-check--RealBufferedSink$commonWriteUtf8CodePoint$22.toString());
        }
        RealBufferedSink this_$iv = $this$commonWriteUtf8CodePoint;
        boolean $i$f$getBuffer = false;
        this_$iv.bufferField.writeUtf8CodePoint(codePoint);
        return $this$commonWriteUtf8CodePoint.emitCompleteSegments();
    }

    @NotNull
    public static final BufferedSink commonWrite(@NotNull RealBufferedSink $this$commonWrite, @NotNull byte[] source2) {
        Intrinsics.checkNotNullParameter($this$commonWrite, "<this>");
        Intrinsics.checkNotNullParameter(source2, "source");
        boolean $i$f$commonWrite = false;
        if (!(!$this$commonWrite.closed)) {
            boolean $i$a$-check--RealBufferedSink$commonWrite$52 = false;
            String $i$a$-check--RealBufferedSink$commonWrite$52 = "closed";
            throw new IllegalStateException($i$a$-check--RealBufferedSink$commonWrite$52.toString());
        }
        RealBufferedSink this_$iv = $this$commonWrite;
        boolean $i$f$getBuffer = false;
        this_$iv.bufferField.write(source2);
        return $this$commonWrite.emitCompleteSegments();
    }

    @NotNull
    public static final BufferedSink commonWrite(@NotNull RealBufferedSink $this$commonWrite, @NotNull byte[] source2, int offset, int byteCount) {
        Intrinsics.checkNotNullParameter($this$commonWrite, "<this>");
        Intrinsics.checkNotNullParameter(source2, "source");
        boolean $i$f$commonWrite = false;
        if (!(!$this$commonWrite.closed)) {
            boolean $i$a$-check--RealBufferedSink$commonWrite$62 = false;
            String $i$a$-check--RealBufferedSink$commonWrite$62 = "closed";
            throw new IllegalStateException($i$a$-check--RealBufferedSink$commonWrite$62.toString());
        }
        RealBufferedSink this_$iv = $this$commonWrite;
        boolean $i$f$getBuffer = false;
        this_$iv.bufferField.write(source2, offset, byteCount);
        return $this$commonWrite.emitCompleteSegments();
    }

    public static final long commonWriteAll(@NotNull RealBufferedSink $this$commonWriteAll, @NotNull Source source2) {
        Intrinsics.checkNotNullParameter($this$commonWriteAll, "<this>");
        Intrinsics.checkNotNullParameter(source2, "source");
        boolean $i$f$commonWriteAll = false;
        long totalBytesRead = 0L;
        while (true) {
            RealBufferedSink this_$iv = $this$commonWriteAll;
            boolean $i$f$getBuffer = false;
            long readCount = source2.read(this_$iv.bufferField, 8192L);
            if (readCount == -1L) break;
            totalBytesRead += readCount;
            $this$commonWriteAll.emitCompleteSegments();
        }
        return totalBytesRead;
    }

    @NotNull
    public static final BufferedSink commonWrite(@NotNull RealBufferedSink $this$commonWrite, @NotNull Source source2, long byteCount) {
        long read;
        Intrinsics.checkNotNullParameter($this$commonWrite, "<this>");
        Intrinsics.checkNotNullParameter(source2, "source");
        boolean $i$f$commonWrite = false;
        for (long byteCount2 = byteCount; byteCount2 > 0L; byteCount2 -= read) {
            RealBufferedSink this_$iv = $this$commonWrite;
            boolean $i$f$getBuffer = false;
            read = source2.read(this_$iv.bufferField, byteCount2);
            if (read == -1L) {
                throw new EOFException();
            }
            $this$commonWrite.emitCompleteSegments();
        }
        return $this$commonWrite;
    }

    @NotNull
    public static final BufferedSink commonWriteByte(@NotNull RealBufferedSink $this$commonWriteByte, int b) {
        Intrinsics.checkNotNullParameter($this$commonWriteByte, "<this>");
        boolean $i$f$commonWriteByte = false;
        if (!(!$this$commonWriteByte.closed)) {
            boolean $i$a$-check--RealBufferedSink$commonWriteByte$22 = false;
            String $i$a$-check--RealBufferedSink$commonWriteByte$22 = "closed";
            throw new IllegalStateException($i$a$-check--RealBufferedSink$commonWriteByte$22.toString());
        }
        RealBufferedSink this_$iv = $this$commonWriteByte;
        boolean $i$f$getBuffer = false;
        this_$iv.bufferField.writeByte(b);
        return $this$commonWriteByte.emitCompleteSegments();
    }

    @NotNull
    public static final BufferedSink commonWriteShort(@NotNull RealBufferedSink $this$commonWriteShort, int s) {
        Intrinsics.checkNotNullParameter($this$commonWriteShort, "<this>");
        boolean $i$f$commonWriteShort = false;
        if (!(!$this$commonWriteShort.closed)) {
            boolean $i$a$-check--RealBufferedSink$commonWriteShort$22 = false;
            String $i$a$-check--RealBufferedSink$commonWriteShort$22 = "closed";
            throw new IllegalStateException($i$a$-check--RealBufferedSink$commonWriteShort$22.toString());
        }
        RealBufferedSink this_$iv = $this$commonWriteShort;
        boolean $i$f$getBuffer = false;
        this_$iv.bufferField.writeShort(s);
        return $this$commonWriteShort.emitCompleteSegments();
    }

    @NotNull
    public static final BufferedSink commonWriteShortLe(@NotNull RealBufferedSink $this$commonWriteShortLe, int s) {
        Intrinsics.checkNotNullParameter($this$commonWriteShortLe, "<this>");
        boolean $i$f$commonWriteShortLe = false;
        if (!(!$this$commonWriteShortLe.closed)) {
            boolean $i$a$-check--RealBufferedSink$commonWriteShortLe$22 = false;
            String $i$a$-check--RealBufferedSink$commonWriteShortLe$22 = "closed";
            throw new IllegalStateException($i$a$-check--RealBufferedSink$commonWriteShortLe$22.toString());
        }
        RealBufferedSink this_$iv = $this$commonWriteShortLe;
        boolean $i$f$getBuffer = false;
        this_$iv.bufferField.writeShortLe(s);
        return $this$commonWriteShortLe.emitCompleteSegments();
    }

    @NotNull
    public static final BufferedSink commonWriteInt(@NotNull RealBufferedSink $this$commonWriteInt, int i) {
        Intrinsics.checkNotNullParameter($this$commonWriteInt, "<this>");
        boolean $i$f$commonWriteInt = false;
        if (!(!$this$commonWriteInt.closed)) {
            boolean $i$a$-check--RealBufferedSink$commonWriteInt$22 = false;
            String $i$a$-check--RealBufferedSink$commonWriteInt$22 = "closed";
            throw new IllegalStateException($i$a$-check--RealBufferedSink$commonWriteInt$22.toString());
        }
        RealBufferedSink this_$iv = $this$commonWriteInt;
        boolean $i$f$getBuffer = false;
        this_$iv.bufferField.writeInt(i);
        return $this$commonWriteInt.emitCompleteSegments();
    }

    @NotNull
    public static final BufferedSink commonWriteIntLe(@NotNull RealBufferedSink $this$commonWriteIntLe, int i) {
        Intrinsics.checkNotNullParameter($this$commonWriteIntLe, "<this>");
        boolean $i$f$commonWriteIntLe = false;
        if (!(!$this$commonWriteIntLe.closed)) {
            boolean $i$a$-check--RealBufferedSink$commonWriteIntLe$22 = false;
            String $i$a$-check--RealBufferedSink$commonWriteIntLe$22 = "closed";
            throw new IllegalStateException($i$a$-check--RealBufferedSink$commonWriteIntLe$22.toString());
        }
        RealBufferedSink this_$iv = $this$commonWriteIntLe;
        boolean $i$f$getBuffer = false;
        this_$iv.bufferField.writeIntLe(i);
        return $this$commonWriteIntLe.emitCompleteSegments();
    }

    @NotNull
    public static final BufferedSink commonWriteLong(@NotNull RealBufferedSink $this$commonWriteLong, long v) {
        Intrinsics.checkNotNullParameter($this$commonWriteLong, "<this>");
        boolean $i$f$commonWriteLong = false;
        if (!(!$this$commonWriteLong.closed)) {
            boolean $i$a$-check--RealBufferedSink$commonWriteLong$22 = false;
            String $i$a$-check--RealBufferedSink$commonWriteLong$22 = "closed";
            throw new IllegalStateException($i$a$-check--RealBufferedSink$commonWriteLong$22.toString());
        }
        RealBufferedSink this_$iv = $this$commonWriteLong;
        boolean $i$f$getBuffer = false;
        this_$iv.bufferField.writeLong(v);
        return $this$commonWriteLong.emitCompleteSegments();
    }

    @NotNull
    public static final BufferedSink commonWriteLongLe(@NotNull RealBufferedSink $this$commonWriteLongLe, long v) {
        Intrinsics.checkNotNullParameter($this$commonWriteLongLe, "<this>");
        boolean $i$f$commonWriteLongLe = false;
        if (!(!$this$commonWriteLongLe.closed)) {
            boolean $i$a$-check--RealBufferedSink$commonWriteLongLe$22 = false;
            String $i$a$-check--RealBufferedSink$commonWriteLongLe$22 = "closed";
            throw new IllegalStateException($i$a$-check--RealBufferedSink$commonWriteLongLe$22.toString());
        }
        RealBufferedSink this_$iv = $this$commonWriteLongLe;
        boolean $i$f$getBuffer = false;
        this_$iv.bufferField.writeLongLe(v);
        return $this$commonWriteLongLe.emitCompleteSegments();
    }

    @NotNull
    public static final BufferedSink commonWriteDecimalLong(@NotNull RealBufferedSink $this$commonWriteDecimalLong, long v) {
        Intrinsics.checkNotNullParameter($this$commonWriteDecimalLong, "<this>");
        boolean $i$f$commonWriteDecimalLong = false;
        if (!(!$this$commonWriteDecimalLong.closed)) {
            boolean $i$a$-check--RealBufferedSink$commonWriteDecimalLong$22 = false;
            String $i$a$-check--RealBufferedSink$commonWriteDecimalLong$22 = "closed";
            throw new IllegalStateException($i$a$-check--RealBufferedSink$commonWriteDecimalLong$22.toString());
        }
        RealBufferedSink this_$iv = $this$commonWriteDecimalLong;
        boolean $i$f$getBuffer = false;
        this_$iv.bufferField.writeDecimalLong(v);
        return $this$commonWriteDecimalLong.emitCompleteSegments();
    }

    @NotNull
    public static final BufferedSink commonWriteHexadecimalUnsignedLong(@NotNull RealBufferedSink $this$commonWriteHexadecimalUnsignedLong, long v) {
        Intrinsics.checkNotNullParameter($this$commonWriteHexadecimalUnsignedLong, "<this>");
        boolean $i$f$commonWriteHexadecimalUnsignedLong = false;
        if (!(!$this$commonWriteHexadecimalUnsignedLong.closed)) {
            boolean $i$a$-check--RealBufferedSink$commonWriteHexadecimalUnsignedLong$22 = false;
            String $i$a$-check--RealBufferedSink$commonWriteHexadecimalUnsignedLong$22 = "closed";
            throw new IllegalStateException($i$a$-check--RealBufferedSink$commonWriteHexadecimalUnsignedLong$22.toString());
        }
        RealBufferedSink this_$iv = $this$commonWriteHexadecimalUnsignedLong;
        boolean $i$f$getBuffer = false;
        this_$iv.bufferField.writeHexadecimalUnsignedLong(v);
        return $this$commonWriteHexadecimalUnsignedLong.emitCompleteSegments();
    }

    @NotNull
    public static final BufferedSink commonEmitCompleteSegments(@NotNull RealBufferedSink $this$commonEmitCompleteSegments) {
        Intrinsics.checkNotNullParameter($this$commonEmitCompleteSegments, "<this>");
        boolean $i$f$commonEmitCompleteSegments = false;
        if (!(!$this$commonEmitCompleteSegments.closed)) {
            boolean bl = false;
            String string = "closed";
            throw new IllegalStateException(string.toString());
        }
        RealBufferedSink this_$iv = $this$commonEmitCompleteSegments;
        boolean $i$f$getBuffer = false;
        long byteCount = this_$iv.bufferField.completeSegmentByteCount();
        if (byteCount > 0L) {
            this_$iv = $this$commonEmitCompleteSegments;
            $i$f$getBuffer = false;
            $this$commonEmitCompleteSegments.sink.write(this_$iv.bufferField, byteCount);
        }
        return $this$commonEmitCompleteSegments;
    }

    @NotNull
    public static final BufferedSink commonEmit(@NotNull RealBufferedSink $this$commonEmit) {
        Intrinsics.checkNotNullParameter($this$commonEmit, "<this>");
        boolean $i$f$commonEmit = false;
        if (!(!$this$commonEmit.closed)) {
            boolean bl = false;
            String string = "closed";
            throw new IllegalStateException(string.toString());
        }
        RealBufferedSink this_$iv = $this$commonEmit;
        boolean $i$f$getBuffer = false;
        long byteCount = this_$iv.bufferField.size();
        if (byteCount > 0L) {
            this_$iv = $this$commonEmit;
            $i$f$getBuffer = false;
            $this$commonEmit.sink.write(this_$iv.bufferField, byteCount);
        }
        return $this$commonEmit;
    }

    public static final void commonFlush(@NotNull RealBufferedSink $this$commonFlush) {
        Intrinsics.checkNotNullParameter($this$commonFlush, "<this>");
        boolean $i$f$commonFlush = false;
        if (!(!$this$commonFlush.closed)) {
            boolean $i$a$-check--RealBufferedSink$commonFlush$22 = false;
            String $i$a$-check--RealBufferedSink$commonFlush$22 = "closed";
            throw new IllegalStateException($i$a$-check--RealBufferedSink$commonFlush$22.toString());
        }
        RealBufferedSink this_$iv = $this$commonFlush;
        boolean $i$f$getBuffer = false;
        if (this_$iv.bufferField.size() > 0L) {
            this_$iv = $this$commonFlush;
            $i$f$getBuffer = false;
            this_$iv = $this$commonFlush;
            $i$f$getBuffer = false;
            $this$commonFlush.sink.write(this_$iv.bufferField, this_$iv.bufferField.size());
        }
        $this$commonFlush.sink.flush();
    }

    public static final void commonClose(@NotNull RealBufferedSink $this$commonClose) {
        Throwable thrown;
        block7: {
            Intrinsics.checkNotNullParameter($this$commonClose, "<this>");
            boolean $i$f$commonClose = false;
            if ($this$commonClose.closed) {
                return;
            }
            thrown = null;
            try {
                RealBufferedSink this_$iv = $this$commonClose;
                boolean $i$f$getBuffer = false;
                if (this_$iv.bufferField.size() > 0L) {
                    this_$iv = $this$commonClose;
                    $i$f$getBuffer = false;
                    this_$iv = $this$commonClose;
                    $i$f$getBuffer = false;
                    $this$commonClose.sink.write(this_$iv.bufferField, this_$iv.bufferField.size());
                }
            }
            catch (Throwable e) {
                thrown = e;
            }
            try {
                $this$commonClose.sink.close();
            }
            catch (Throwable e) {
                if (thrown != null) break block7;
                thrown = e;
            }
        }
        $this$commonClose.closed = true;
        Throwable throwable = thrown;
        if (throwable != null) {
            throw throwable;
        }
    }

    @NotNull
    public static final Timeout commonTimeout(@NotNull RealBufferedSink $this$commonTimeout) {
        Intrinsics.checkNotNullParameter($this$commonTimeout, "<this>");
        boolean $i$f$commonTimeout = false;
        return $this$commonTimeout.sink.timeout();
    }

    @NotNull
    public static final String commonToString(@NotNull RealBufferedSink $this$commonToString) {
        Intrinsics.checkNotNullParameter($this$commonToString, "<this>");
        boolean $i$f$commonToString = false;
        return "buffer(" + $this$commonToString.sink + ')';
    }
}

