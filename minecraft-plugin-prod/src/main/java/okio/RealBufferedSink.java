/*
 * Decompiled with CFR 0.152.
 */
package okio;

import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import kotlin.Metadata;
import kotlin.jvm.JvmField;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import okio.Buffer;
import okio.BufferedSink;
import okio.ByteString;
import okio.Sink;
import okio.Source;
import okio.Timeout;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0012\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0011\n\u0002\u0018\u0002\n\u0002\b\u0006\b\u0000\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\b\u0010\u0005\u001a\u00020\u0006H\u0016J\b\u0010\u000e\u001a\u00020\u000fH\u0016J\b\u0010\u0010\u001a\u00020\u0001H\u0016J\b\u0010\u0011\u001a\u00020\u0001H\u0016J\b\u0010\u0012\u001a\u00020\u000fH\u0016J\b\u0010\u0013\u001a\u00020\rH\u0016J\b\u0010\u0014\u001a\u00020\u0015H\u0016J\b\u0010\u0016\u001a\u00020\u0017H\u0016J\b\u0010\u0018\u001a\u00020\u0019H\u0016J\u0010\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\u001dH\u0016J\u0010\u0010\u001a\u001a\u00020\u00012\u0006\u0010\u001c\u001a\u00020\u001eH\u0016J \u0010\u001a\u001a\u00020\u00012\u0006\u0010\u001c\u001a\u00020\u001e2\u0006\u0010\u001f\u001a\u00020\u001b2\u0006\u0010 \u001a\u00020\u001bH\u0016J\u0018\u0010\u001a\u001a\u00020\u000f2\u0006\u0010\u001c\u001a\u00020\u00062\u0006\u0010 \u001a\u00020!H\u0016J\u0010\u0010\u001a\u001a\u00020\u00012\u0006\u0010\"\u001a\u00020#H\u0016J \u0010\u001a\u001a\u00020\u00012\u0006\u0010\"\u001a\u00020#2\u0006\u0010\u001f\u001a\u00020\u001b2\u0006\u0010 \u001a\u00020\u001bH\u0016J\u0018\u0010\u001a\u001a\u00020\u00012\u0006\u0010\u001c\u001a\u00020$2\u0006\u0010 \u001a\u00020!H\u0016J\u0010\u0010%\u001a\u00020!2\u0006\u0010\u001c\u001a\u00020$H\u0016J\u0010\u0010&\u001a\u00020\u00012\u0006\u0010'\u001a\u00020\u001bH\u0016J\u0010\u0010(\u001a\u00020\u00012\u0006\u0010)\u001a\u00020!H\u0016J\u0010\u0010*\u001a\u00020\u00012\u0006\u0010)\u001a\u00020!H\u0016J\u0010\u0010+\u001a\u00020\u00012\u0006\u0010,\u001a\u00020\u001bH\u0016J\u0010\u0010-\u001a\u00020\u00012\u0006\u0010,\u001a\u00020\u001bH\u0016J\u0010\u0010.\u001a\u00020\u00012\u0006\u0010)\u001a\u00020!H\u0016J\u0010\u0010/\u001a\u00020\u00012\u0006\u0010)\u001a\u00020!H\u0016J\u0010\u00100\u001a\u00020\u00012\u0006\u00101\u001a\u00020\u001bH\u0016J\u0010\u00102\u001a\u00020\u00012\u0006\u00101\u001a\u00020\u001bH\u0016J\u0018\u00103\u001a\u00020\u00012\u0006\u00104\u001a\u00020\u00192\u0006\u00105\u001a\u000206H\u0016J(\u00103\u001a\u00020\u00012\u0006\u00104\u001a\u00020\u00192\u0006\u00107\u001a\u00020\u001b2\u0006\u00108\u001a\u00020\u001b2\u0006\u00105\u001a\u000206H\u0016J\u0010\u00109\u001a\u00020\u00012\u0006\u00104\u001a\u00020\u0019H\u0016J \u00109\u001a\u00020\u00012\u0006\u00104\u001a\u00020\u00192\u0006\u00107\u001a\u00020\u001b2\u0006\u00108\u001a\u00020\u001bH\u0016J\u0010\u0010:\u001a\u00020\u00012\u0006\u0010;\u001a\u00020\u001bH\u0016R\u001b\u0010\u0005\u001a\u00020\u00068\u00d6\u0002X\u0096\u0004\u00a2\u0006\f\u0012\u0004\b\u0007\u0010\b\u001a\u0004\b\t\u0010\nR\u0010\u0010\u000b\u001a\u00020\u00068\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0012\u0010\f\u001a\u00020\r8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006<"}, d2={"Lokio/RealBufferedSink;", "Lokio/BufferedSink;", "sink", "Lokio/Sink;", "(Lokio/Sink;)V", "buffer", "Lokio/Buffer;", "getBuffer$annotations", "()V", "getBuffer", "()Lokio/Buffer;", "bufferField", "closed", "", "close", "", "emit", "emitCompleteSegments", "flush", "isOpen", "outputStream", "Ljava/io/OutputStream;", "timeout", "Lokio/Timeout;", "toString", "", "write", "", "source", "Ljava/nio/ByteBuffer;", "", "offset", "byteCount", "", "byteString", "Lokio/ByteString;", "Lokio/Source;", "writeAll", "writeByte", "b", "writeDecimalLong", "v", "writeHexadecimalUnsignedLong", "writeInt", "i", "writeIntLe", "writeLong", "writeLongLe", "writeShort", "s", "writeShortLe", "writeString", "string", "charset", "Ljava/nio/charset/Charset;", "beginIndex", "endIndex", "writeUtf8", "writeUtf8CodePoint", "codePoint", "okio"})
@SourceDebugExtension(value={"SMAP\nRealBufferedSink.kt\nKotlin\n*S Kotlin\n*F\n+ 1 RealBufferedSink.kt\nokio/RealBufferedSink\n+ 2 RealBufferedSink.kt\nokio/internal/-RealBufferedSink\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,142:1\n51#1:146\n51#1:152\n51#1:157\n51#1:162\n51#1:167\n51#1:172\n51#1:175\n51#1:176\n51#1:180\n51#1:185\n51#1:188\n51#1:192\n51#1:201\n51#1:210\n51#1:215\n51#1:220\n51#1:225\n51#1:230\n51#1:235\n51#1:240\n51#1:245\n51#1:250\n51#1:255\n51#1:261\n51#1:267\n51#1:279\n34#2:143\n35#2:145\n36#2,2:147\n40#2:149\n41#2:151\n42#2:153\n50#2:154\n51#2:156\n52#2:158\n56#2:159\n57#2:161\n58#2:163\n66#2:164\n67#2:166\n68#2:168\n72#2:169\n73#2:171\n74#2:173\n78#2:177\n79#2:179\n80#2:181\n88#2:182\n89#2:184\n90#2:186\n94#2,3:189\n97#2,5:193\n105#2,3:198\n108#2,5:202\n116#2:207\n117#2:209\n118#2:211\n122#2:212\n123#2:214\n124#2:216\n128#2:217\n129#2:219\n130#2:221\n134#2:222\n135#2:224\n136#2:226\n140#2:227\n141#2:229\n142#2:231\n146#2:232\n147#2:234\n148#2:236\n152#2:237\n153#2:239\n154#2:241\n158#2:242\n159#2:244\n160#2:246\n164#2:247\n165#2:249\n166#2:251\n170#2:252\n171#2:254\n172#2,2:256\n177#2:258\n178#2:260\n179#2,2:262\n184#2:264\n185#2:266\n186#2,4:268\n192#2,7:272\n199#2,16:280\n216#2:296\n218#2:297\n1#3:144\n1#3:150\n1#3:155\n1#3:160\n1#3:165\n1#3:170\n1#3:174\n1#3:178\n1#3:183\n1#3:187\n1#3:208\n1#3:213\n1#3:218\n1#3:223\n1#3:228\n1#3:233\n1#3:238\n1#3:243\n1#3:248\n1#3:253\n1#3:259\n1#3:265\n*S KotlinDebug\n*F\n+ 1 RealBufferedSink.kt\nokio/RealBufferedSink\n*L\n55#1:146\n56#1:152\n58#1:157\n59#1:162\n61#1:167\n63#1:172\n67#1:175\n78#1:176\n82#1:180\n84#1:185\n88#1:188\n93#1:192\n94#1:201\n95#1:210\n96#1:215\n97#1:220\n98#1:225\n99#1:230\n100#1:235\n101#1:240\n102#1:245\n103#1:250\n104#1:255\n105#1:261\n134#1:267\n138#1:279\n55#1:143\n55#1:145\n55#1:147,2\n56#1:149\n56#1:151\n56#1:153\n58#1:154\n58#1:156\n58#1:158\n59#1:159\n59#1:161\n59#1:163\n61#1:164\n61#1:166\n61#1:168\n63#1:169\n63#1:171\n63#1:173\n82#1:177\n82#1:179\n82#1:181\n84#1:182\n84#1:184\n84#1:186\n93#1:189,3\n93#1:193,5\n94#1:198,3\n94#1:202,5\n95#1:207\n95#1:209\n95#1:211\n96#1:212\n96#1:214\n96#1:216\n97#1:217\n97#1:219\n97#1:221\n98#1:222\n98#1:224\n98#1:226\n99#1:227\n99#1:229\n99#1:231\n100#1:232\n100#1:234\n100#1:236\n101#1:237\n101#1:239\n101#1:241\n102#1:242\n102#1:244\n102#1:246\n103#1:247\n103#1:249\n103#1:251\n104#1:252\n104#1:254\n104#1:256,2\n105#1:258\n105#1:260\n105#1:262,2\n134#1:264\n134#1:266\n134#1:268,4\n138#1:272,7\n138#1:280,16\n139#1:296\n140#1:297\n55#1:144\n56#1:150\n58#1:155\n59#1:160\n61#1:165\n63#1:170\n82#1:178\n84#1:183\n95#1:208\n96#1:213\n97#1:218\n98#1:223\n99#1:228\n100#1:233\n101#1:238\n102#1:243\n103#1:248\n104#1:253\n105#1:259\n134#1:265\n*E\n"})
public final class RealBufferedSink
implements BufferedSink {
    @JvmField
    @NotNull
    public final Sink sink;
    @JvmField
    @NotNull
    public final Buffer bufferField;
    @JvmField
    public boolean closed;

    public RealBufferedSink(@NotNull Sink sink2) {
        Intrinsics.checkNotNullParameter(sink2, "sink");
        this.sink = sink2;
        this.bufferField = new Buffer();
    }

    @Override
    @NotNull
    public Buffer getBuffer() {
        boolean $i$f$getBuffer = false;
        return this.bufferField;
    }

    public static /* synthetic */ void getBuffer$annotations() {
    }

    @Override
    @NotNull
    public Buffer buffer() {
        return this.bufferField;
    }

    @Override
    public void write(@NotNull Buffer source2, long byteCount) {
        Intrinsics.checkNotNullParameter(source2, "source");
        RealBufferedSink $this$commonWrite$iv = this;
        boolean $i$f$commonWrite = false;
        if (!(!$this$commonWrite$iv.closed)) {
            boolean $i$a$-check--RealBufferedSink$commonWrite$1$iv22 = false;
            String $i$a$-check--RealBufferedSink$commonWrite$1$iv22 = "closed";
            throw new IllegalStateException($i$a$-check--RealBufferedSink$commonWrite$1$iv22.toString());
        }
        RealBufferedSink this_$iv$iv = $this$commonWrite$iv;
        boolean $i$f$getBuffer = false;
        this_$iv$iv.bufferField.write(source2, byteCount);
        $this$commonWrite$iv.emitCompleteSegments();
    }

    @Override
    @NotNull
    public BufferedSink write(@NotNull ByteString byteString) {
        Intrinsics.checkNotNullParameter(byteString, "byteString");
        RealBufferedSink $this$commonWrite$iv = this;
        boolean $i$f$commonWrite = false;
        if (!(!$this$commonWrite$iv.closed)) {
            boolean $i$a$-check--RealBufferedSink$commonWrite$2$iv22 = false;
            String $i$a$-check--RealBufferedSink$commonWrite$2$iv22 = "closed";
            throw new IllegalStateException($i$a$-check--RealBufferedSink$commonWrite$2$iv22.toString());
        }
        RealBufferedSink this_$iv$iv = $this$commonWrite$iv;
        boolean $i$f$getBuffer = false;
        this_$iv$iv.bufferField.write(byteString);
        return $this$commonWrite$iv.emitCompleteSegments();
    }

    @Override
    @NotNull
    public BufferedSink write(@NotNull ByteString byteString, int offset, int byteCount) {
        Intrinsics.checkNotNullParameter(byteString, "byteString");
        RealBufferedSink $this$commonWrite$iv = this;
        boolean $i$f$commonWrite = false;
        if (!(!$this$commonWrite$iv.closed)) {
            boolean $i$a$-check--RealBufferedSink$commonWrite$3$iv22 = false;
            String $i$a$-check--RealBufferedSink$commonWrite$3$iv22 = "closed";
            throw new IllegalStateException($i$a$-check--RealBufferedSink$commonWrite$3$iv22.toString());
        }
        RealBufferedSink this_$iv$iv = $this$commonWrite$iv;
        boolean $i$f$getBuffer = false;
        this_$iv$iv.bufferField.write(byteString, offset, byteCount);
        return $this$commonWrite$iv.emitCompleteSegments();
    }

    @Override
    @NotNull
    public BufferedSink writeUtf8(@NotNull String string) {
        Intrinsics.checkNotNullParameter(string, "string");
        RealBufferedSink $this$commonWriteUtf8$iv = this;
        boolean $i$f$commonWriteUtf8 = false;
        if (!(!$this$commonWriteUtf8$iv.closed)) {
            boolean $i$a$-check--RealBufferedSink$commonWriteUtf8$1$iv22 = false;
            String $i$a$-check--RealBufferedSink$commonWriteUtf8$1$iv22 = "closed";
            throw new IllegalStateException($i$a$-check--RealBufferedSink$commonWriteUtf8$1$iv22.toString());
        }
        RealBufferedSink this_$iv$iv = $this$commonWriteUtf8$iv;
        boolean $i$f$getBuffer = false;
        this_$iv$iv.bufferField.writeUtf8(string);
        return $this$commonWriteUtf8$iv.emitCompleteSegments();
    }

    @Override
    @NotNull
    public BufferedSink writeUtf8(@NotNull String string, int beginIndex, int endIndex) {
        Intrinsics.checkNotNullParameter(string, "string");
        RealBufferedSink $this$commonWriteUtf8$iv = this;
        boolean $i$f$commonWriteUtf8 = false;
        if (!(!$this$commonWriteUtf8$iv.closed)) {
            boolean $i$a$-check--RealBufferedSink$commonWriteUtf8$2$iv22 = false;
            String $i$a$-check--RealBufferedSink$commonWriteUtf8$2$iv22 = "closed";
            throw new IllegalStateException($i$a$-check--RealBufferedSink$commonWriteUtf8$2$iv22.toString());
        }
        RealBufferedSink this_$iv$iv = $this$commonWriteUtf8$iv;
        boolean $i$f$getBuffer = false;
        this_$iv$iv.bufferField.writeUtf8(string, beginIndex, endIndex);
        return $this$commonWriteUtf8$iv.emitCompleteSegments();
    }

    @Override
    @NotNull
    public BufferedSink writeUtf8CodePoint(int codePoint) {
        RealBufferedSink $this$commonWriteUtf8CodePoint$iv = this;
        boolean $i$f$commonWriteUtf8CodePoint = false;
        if (!(!$this$commonWriteUtf8CodePoint$iv.closed)) {
            boolean $i$a$-check--RealBufferedSink$commonWriteUtf8CodePoint$1$iv22 = false;
            String $i$a$-check--RealBufferedSink$commonWriteUtf8CodePoint$1$iv22 = "closed";
            throw new IllegalStateException($i$a$-check--RealBufferedSink$commonWriteUtf8CodePoint$1$iv22.toString());
        }
        RealBufferedSink this_$iv$iv = $this$commonWriteUtf8CodePoint$iv;
        boolean $i$f$getBuffer = false;
        this_$iv$iv.bufferField.writeUtf8CodePoint(codePoint);
        return $this$commonWriteUtf8CodePoint$iv.emitCompleteSegments();
    }

    @Override
    @NotNull
    public BufferedSink writeString(@NotNull String string, @NotNull Charset charset) {
        Intrinsics.checkNotNullParameter(string, "string");
        Intrinsics.checkNotNullParameter(charset, "charset");
        if (!(!this.closed)) {
            boolean $i$a$-check-RealBufferedSink$writeString$22 = false;
            String $i$a$-check-RealBufferedSink$writeString$22 = "closed";
            throw new IllegalStateException($i$a$-check-RealBufferedSink$writeString$22.toString());
        }
        RealBufferedSink this_$iv = this;
        boolean $i$f$getBuffer = false;
        this_$iv.bufferField.writeString(string, charset);
        return this.emitCompleteSegments();
    }

    @Override
    @NotNull
    public BufferedSink writeString(@NotNull String string, int beginIndex, int endIndex, @NotNull Charset charset) {
        Intrinsics.checkNotNullParameter(string, "string");
        Intrinsics.checkNotNullParameter(charset, "charset");
        if (!(!this.closed)) {
            boolean $i$a$-check-RealBufferedSink$writeString$32 = false;
            String $i$a$-check-RealBufferedSink$writeString$32 = "closed";
            throw new IllegalStateException($i$a$-check-RealBufferedSink$writeString$32.toString());
        }
        RealBufferedSink this_$iv = this;
        boolean $i$f$getBuffer = false;
        this_$iv.bufferField.writeString(string, beginIndex, endIndex, charset);
        return this.emitCompleteSegments();
    }

    @Override
    @NotNull
    public BufferedSink write(@NotNull byte[] source2) {
        Intrinsics.checkNotNullParameter(source2, "source");
        RealBufferedSink $this$commonWrite$iv = this;
        boolean $i$f$commonWrite = false;
        if (!(!$this$commonWrite$iv.closed)) {
            boolean $i$a$-check--RealBufferedSink$commonWrite$4$iv22 = false;
            String $i$a$-check--RealBufferedSink$commonWrite$4$iv22 = "closed";
            throw new IllegalStateException($i$a$-check--RealBufferedSink$commonWrite$4$iv22.toString());
        }
        RealBufferedSink this_$iv$iv = $this$commonWrite$iv;
        boolean $i$f$getBuffer = false;
        this_$iv$iv.bufferField.write(source2);
        return $this$commonWrite$iv.emitCompleteSegments();
    }

    @Override
    @NotNull
    public BufferedSink write(@NotNull byte[] source2, int offset, int byteCount) {
        Intrinsics.checkNotNullParameter(source2, "source");
        RealBufferedSink $this$commonWrite$iv = this;
        boolean $i$f$commonWrite = false;
        if (!(!$this$commonWrite$iv.closed)) {
            boolean $i$a$-check--RealBufferedSink$commonWrite$5$iv22 = false;
            String $i$a$-check--RealBufferedSink$commonWrite$5$iv22 = "closed";
            throw new IllegalStateException($i$a$-check--RealBufferedSink$commonWrite$5$iv22.toString());
        }
        RealBufferedSink this_$iv$iv = $this$commonWrite$iv;
        boolean $i$f$getBuffer = false;
        this_$iv$iv.bufferField.write(source2, offset, byteCount);
        return $this$commonWrite$iv.emitCompleteSegments();
    }

    @Override
    public int write(@NotNull ByteBuffer source2) {
        Intrinsics.checkNotNullParameter(source2, "source");
        if (!(!this.closed)) {
            boolean $i$a$-check-RealBufferedSink$write$22 = false;
            String $i$a$-check-RealBufferedSink$write$22 = "closed";
            throw new IllegalStateException($i$a$-check-RealBufferedSink$write$22.toString());
        }
        RealBufferedSink this_$iv = this;
        boolean $i$f$getBuffer = false;
        int result = this_$iv.bufferField.write(source2);
        this.emitCompleteSegments();
        return result;
    }

    @Override
    public long writeAll(@NotNull Source source2) {
        Intrinsics.checkNotNullParameter(source2, "source");
        RealBufferedSink $this$commonWriteAll$iv = this;
        boolean $i$f$commonWriteAll = false;
        long totalBytesRead$iv = 0L;
        while (true) {
            RealBufferedSink this_$iv$iv = $this$commonWriteAll$iv;
            boolean $i$f$getBuffer = false;
            long readCount$iv = source2.read(this_$iv$iv.bufferField, 8192L);
            if (readCount$iv == -1L) break;
            totalBytesRead$iv += readCount$iv;
            $this$commonWriteAll$iv.emitCompleteSegments();
        }
        return totalBytesRead$iv;
    }

    @Override
    @NotNull
    public BufferedSink write(@NotNull Source source2, long byteCount) {
        long read$iv;
        Intrinsics.checkNotNullParameter(source2, "source");
        RealBufferedSink $this$commonWrite$iv = this;
        boolean $i$f$commonWrite = false;
        for (long byteCount$iv = byteCount; byteCount$iv > 0L; byteCount$iv -= read$iv) {
            RealBufferedSink this_$iv$iv = $this$commonWrite$iv;
            boolean $i$f$getBuffer = false;
            read$iv = source2.read(this_$iv$iv.bufferField, byteCount$iv);
            if (read$iv == -1L) {
                throw new EOFException();
            }
            $this$commonWrite$iv.emitCompleteSegments();
        }
        return $this$commonWrite$iv;
    }

    @Override
    @NotNull
    public BufferedSink writeByte(int b) {
        RealBufferedSink $this$commonWriteByte$iv = this;
        boolean $i$f$commonWriteByte = false;
        if (!(!$this$commonWriteByte$iv.closed)) {
            boolean $i$a$-check--RealBufferedSink$commonWriteByte$1$iv22 = false;
            String $i$a$-check--RealBufferedSink$commonWriteByte$1$iv22 = "closed";
            throw new IllegalStateException($i$a$-check--RealBufferedSink$commonWriteByte$1$iv22.toString());
        }
        RealBufferedSink this_$iv$iv = $this$commonWriteByte$iv;
        boolean $i$f$getBuffer = false;
        this_$iv$iv.bufferField.writeByte(b);
        return $this$commonWriteByte$iv.emitCompleteSegments();
    }

    @Override
    @NotNull
    public BufferedSink writeShort(int s) {
        RealBufferedSink $this$commonWriteShort$iv = this;
        boolean $i$f$commonWriteShort = false;
        if (!(!$this$commonWriteShort$iv.closed)) {
            boolean $i$a$-check--RealBufferedSink$commonWriteShort$1$iv22 = false;
            String $i$a$-check--RealBufferedSink$commonWriteShort$1$iv22 = "closed";
            throw new IllegalStateException($i$a$-check--RealBufferedSink$commonWriteShort$1$iv22.toString());
        }
        RealBufferedSink this_$iv$iv = $this$commonWriteShort$iv;
        boolean $i$f$getBuffer = false;
        this_$iv$iv.bufferField.writeShort(s);
        return $this$commonWriteShort$iv.emitCompleteSegments();
    }

    @Override
    @NotNull
    public BufferedSink writeShortLe(int s) {
        RealBufferedSink $this$commonWriteShortLe$iv = this;
        boolean $i$f$commonWriteShortLe = false;
        if (!(!$this$commonWriteShortLe$iv.closed)) {
            boolean $i$a$-check--RealBufferedSink$commonWriteShortLe$1$iv22 = false;
            String $i$a$-check--RealBufferedSink$commonWriteShortLe$1$iv22 = "closed";
            throw new IllegalStateException($i$a$-check--RealBufferedSink$commonWriteShortLe$1$iv22.toString());
        }
        RealBufferedSink this_$iv$iv = $this$commonWriteShortLe$iv;
        boolean $i$f$getBuffer = false;
        this_$iv$iv.bufferField.writeShortLe(s);
        return $this$commonWriteShortLe$iv.emitCompleteSegments();
    }

    @Override
    @NotNull
    public BufferedSink writeInt(int i) {
        RealBufferedSink $this$commonWriteInt$iv = this;
        boolean $i$f$commonWriteInt = false;
        if (!(!$this$commonWriteInt$iv.closed)) {
            boolean $i$a$-check--RealBufferedSink$commonWriteInt$1$iv22 = false;
            String $i$a$-check--RealBufferedSink$commonWriteInt$1$iv22 = "closed";
            throw new IllegalStateException($i$a$-check--RealBufferedSink$commonWriteInt$1$iv22.toString());
        }
        RealBufferedSink this_$iv$iv = $this$commonWriteInt$iv;
        boolean $i$f$getBuffer = false;
        this_$iv$iv.bufferField.writeInt(i);
        return $this$commonWriteInt$iv.emitCompleteSegments();
    }

    @Override
    @NotNull
    public BufferedSink writeIntLe(int i) {
        RealBufferedSink $this$commonWriteIntLe$iv = this;
        boolean $i$f$commonWriteIntLe = false;
        if (!(!$this$commonWriteIntLe$iv.closed)) {
            boolean $i$a$-check--RealBufferedSink$commonWriteIntLe$1$iv22 = false;
            String $i$a$-check--RealBufferedSink$commonWriteIntLe$1$iv22 = "closed";
            throw new IllegalStateException($i$a$-check--RealBufferedSink$commonWriteIntLe$1$iv22.toString());
        }
        RealBufferedSink this_$iv$iv = $this$commonWriteIntLe$iv;
        boolean $i$f$getBuffer = false;
        this_$iv$iv.bufferField.writeIntLe(i);
        return $this$commonWriteIntLe$iv.emitCompleteSegments();
    }

    @Override
    @NotNull
    public BufferedSink writeLong(long v) {
        RealBufferedSink $this$commonWriteLong$iv = this;
        boolean $i$f$commonWriteLong = false;
        if (!(!$this$commonWriteLong$iv.closed)) {
            boolean $i$a$-check--RealBufferedSink$commonWriteLong$1$iv22 = false;
            String $i$a$-check--RealBufferedSink$commonWriteLong$1$iv22 = "closed";
            throw new IllegalStateException($i$a$-check--RealBufferedSink$commonWriteLong$1$iv22.toString());
        }
        RealBufferedSink this_$iv$iv = $this$commonWriteLong$iv;
        boolean $i$f$getBuffer = false;
        this_$iv$iv.bufferField.writeLong(v);
        return $this$commonWriteLong$iv.emitCompleteSegments();
    }

    @Override
    @NotNull
    public BufferedSink writeLongLe(long v) {
        RealBufferedSink $this$commonWriteLongLe$iv = this;
        boolean $i$f$commonWriteLongLe = false;
        if (!(!$this$commonWriteLongLe$iv.closed)) {
            boolean $i$a$-check--RealBufferedSink$commonWriteLongLe$1$iv22 = false;
            String $i$a$-check--RealBufferedSink$commonWriteLongLe$1$iv22 = "closed";
            throw new IllegalStateException($i$a$-check--RealBufferedSink$commonWriteLongLe$1$iv22.toString());
        }
        RealBufferedSink this_$iv$iv = $this$commonWriteLongLe$iv;
        boolean $i$f$getBuffer = false;
        this_$iv$iv.bufferField.writeLongLe(v);
        return $this$commonWriteLongLe$iv.emitCompleteSegments();
    }

    @Override
    @NotNull
    public BufferedSink writeDecimalLong(long v) {
        RealBufferedSink $this$commonWriteDecimalLong$iv = this;
        boolean $i$f$commonWriteDecimalLong = false;
        if (!(!$this$commonWriteDecimalLong$iv.closed)) {
            boolean $i$a$-check--RealBufferedSink$commonWriteDecimalLong$1$iv22 = false;
            String $i$a$-check--RealBufferedSink$commonWriteDecimalLong$1$iv22 = "closed";
            throw new IllegalStateException($i$a$-check--RealBufferedSink$commonWriteDecimalLong$1$iv22.toString());
        }
        RealBufferedSink this_$iv$iv = $this$commonWriteDecimalLong$iv;
        boolean $i$f$getBuffer = false;
        this_$iv$iv.bufferField.writeDecimalLong(v);
        return $this$commonWriteDecimalLong$iv.emitCompleteSegments();
    }

    @Override
    @NotNull
    public BufferedSink writeHexadecimalUnsignedLong(long v) {
        RealBufferedSink $this$commonWriteHexadecimalUnsignedLong$iv = this;
        boolean $i$f$commonWriteHexadecimalUnsignedLong = false;
        if (!(!$this$commonWriteHexadecimalUnsignedLong$iv.closed)) {
            boolean $i$a$-check--RealBufferedSink$commonWriteHexadecimalUnsignedLong$1$iv22 = false;
            String $i$a$-check--RealBufferedSink$commonWriteHexadecimalUnsignedLong$1$iv22 = "closed";
            throw new IllegalStateException($i$a$-check--RealBufferedSink$commonWriteHexadecimalUnsignedLong$1$iv22.toString());
        }
        RealBufferedSink this_$iv$iv = $this$commonWriteHexadecimalUnsignedLong$iv;
        boolean $i$f$getBuffer = false;
        this_$iv$iv.bufferField.writeHexadecimalUnsignedLong(v);
        return $this$commonWriteHexadecimalUnsignedLong$iv.emitCompleteSegments();
    }

    @Override
    @NotNull
    public BufferedSink emitCompleteSegments() {
        RealBufferedSink $this$commonEmitCompleteSegments$iv = this;
        boolean $i$f$commonEmitCompleteSegments = false;
        if (!(!$this$commonEmitCompleteSegments$iv.closed)) {
            boolean bl = false;
            String string = "closed";
            throw new IllegalStateException(string.toString());
        }
        RealBufferedSink this_$iv$iv = $this$commonEmitCompleteSegments$iv;
        boolean $i$f$getBuffer = false;
        long byteCount$iv = this_$iv$iv.bufferField.completeSegmentByteCount();
        if (byteCount$iv > 0L) {
            this_$iv$iv = $this$commonEmitCompleteSegments$iv;
            $i$f$getBuffer = false;
            $this$commonEmitCompleteSegments$iv.sink.write(this_$iv$iv.bufferField, byteCount$iv);
        }
        return $this$commonEmitCompleteSegments$iv;
    }

    @Override
    @NotNull
    public BufferedSink emit() {
        RealBufferedSink $this$commonEmit$iv = this;
        boolean $i$f$commonEmit = false;
        if (!(!$this$commonEmit$iv.closed)) {
            boolean bl = false;
            String string = "closed";
            throw new IllegalStateException(string.toString());
        }
        RealBufferedSink this_$iv$iv = $this$commonEmit$iv;
        boolean $i$f$getBuffer = false;
        long byteCount$iv = this_$iv$iv.bufferField.size();
        if (byteCount$iv > 0L) {
            this_$iv$iv = $this$commonEmit$iv;
            $i$f$getBuffer = false;
            $this$commonEmit$iv.sink.write(this_$iv$iv.bufferField, byteCount$iv);
        }
        return $this$commonEmit$iv;
    }

    @Override
    @NotNull
    public OutputStream outputStream() {
        return new OutputStream(this){
            final /* synthetic */ RealBufferedSink this$0;
            {
                this.this$0 = $receiver;
            }

            public void write(int b) {
                if (this.this$0.closed) {
                    throw new IOException("closed");
                }
                RealBufferedSink this_$iv = this.this$0;
                boolean $i$f$getBuffer = false;
                this_$iv.bufferField.writeByte((byte)b);
                this.this$0.emitCompleteSegments();
            }

            public void write(@NotNull byte[] data, int offset, int byteCount) {
                Intrinsics.checkNotNullParameter(data, "data");
                if (this.this$0.closed) {
                    throw new IOException("closed");
                }
                RealBufferedSink this_$iv = this.this$0;
                boolean $i$f$getBuffer = false;
                this_$iv.bufferField.write(data, offset, byteCount);
                this.this$0.emitCompleteSegments();
            }

            public void flush() {
                if (!this.this$0.closed) {
                    this.this$0.flush();
                }
            }

            public void close() {
                this.this$0.close();
            }

            @NotNull
            public String toString() {
                return this.this$0 + ".outputStream()";
            }
        };
    }

    @Override
    public void flush() {
        RealBufferedSink $this$commonFlush$iv = this;
        boolean $i$f$commonFlush = false;
        if (!(!$this$commonFlush$iv.closed)) {
            boolean $i$a$-check--RealBufferedSink$commonFlush$1$iv22 = false;
            String $i$a$-check--RealBufferedSink$commonFlush$1$iv22 = "closed";
            throw new IllegalStateException($i$a$-check--RealBufferedSink$commonFlush$1$iv22.toString());
        }
        RealBufferedSink this_$iv$iv = $this$commonFlush$iv;
        boolean $i$f$getBuffer = false;
        if (this_$iv$iv.bufferField.size() > 0L) {
            this_$iv$iv = $this$commonFlush$iv;
            $i$f$getBuffer = false;
            this_$iv$iv = $this$commonFlush$iv;
            $i$f$getBuffer = false;
            $this$commonFlush$iv.sink.write(this_$iv$iv.bufferField, this_$iv$iv.bufferField.size());
        }
        $this$commonFlush$iv.sink.flush();
    }

    @Override
    public boolean isOpen() {
        return !this.closed;
    }

    @Override
    public void close() {
        RealBufferedSink $this$commonClose$iv = this;
        boolean $i$f$commonClose = false;
        if (!$this$commonClose$iv.closed) {
            Throwable thrown$iv;
            block7: {
                thrown$iv = null;
                try {
                    RealBufferedSink this_$iv$iv = $this$commonClose$iv;
                    boolean $i$f$getBuffer = false;
                    if (this_$iv$iv.bufferField.size() > 0L) {
                        this_$iv$iv = $this$commonClose$iv;
                        $i$f$getBuffer = false;
                        this_$iv$iv = $this$commonClose$iv;
                        $i$f$getBuffer = false;
                        $this$commonClose$iv.sink.write(this_$iv$iv.bufferField, this_$iv$iv.bufferField.size());
                    }
                }
                catch (Throwable e$iv) {
                    thrown$iv = e$iv;
                }
                try {
                    $this$commonClose$iv.sink.close();
                }
                catch (Throwable e$iv) {
                    if (thrown$iv != null) break block7;
                    thrown$iv = e$iv;
                }
            }
            $this$commonClose$iv.closed = true;
            Throwable throwable = thrown$iv;
            if (throwable != null) {
                throw throwable;
            }
        }
    }

    @Override
    @NotNull
    public Timeout timeout() {
        RealBufferedSink $this$commonTimeout$iv = this;
        boolean $i$f$commonTimeout = false;
        return $this$commonTimeout$iv.sink.timeout();
    }

    @NotNull
    public String toString() {
        RealBufferedSink $this$commonToString$iv = this;
        boolean $i$f$commonToString = false;
        return "buffer(" + $this$commonToString$iv.sink + ')';
    }
}

