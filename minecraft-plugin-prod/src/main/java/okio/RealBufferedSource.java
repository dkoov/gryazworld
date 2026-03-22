/*
 * Decompiled with CFR 0.152.
 */
package okio;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import kotlin.Metadata;
import kotlin.jvm.JvmField;
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
import okio.Sink;
import okio.Source;
import okio.Timeout;
import okio.internal.-Buffer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000\u0086\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u0005\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010\u0012\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0010\n\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0000\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\b\u0010\u0005\u001a\u00020\u0006H\u0016J\b\u0010\u000e\u001a\u00020\u000fH\u0016J\b\u0010\u0010\u001a\u00020\rH\u0016J\u0010\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u0014H\u0016J\u0018\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u0012H\u0016J \u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u00122\u0006\u0010\u0016\u001a\u00020\u0012H\u0016J\u0010\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0017\u001a\u00020\u0018H\u0016J\u0018\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0015\u001a\u00020\u0012H\u0016J\u0010\u0010\u0019\u001a\u00020\u00122\u0006\u0010\u001a\u001a\u00020\u0018H\u0016J\u0018\u0010\u0019\u001a\u00020\u00122\u0006\u0010\u001a\u001a\u00020\u00182\u0006\u0010\u0015\u001a\u00020\u0012H\u0016J\b\u0010\u001b\u001a\u00020\u001cH\u0016J\b\u0010\u001d\u001a\u00020\rH\u0016J\b\u0010\u001e\u001a\u00020\u0001H\u0016J\u0018\u0010\u001f\u001a\u00020\r2\u0006\u0010 \u001a\u00020\u00122\u0006\u0010\u0017\u001a\u00020\u0018H\u0016J(\u0010\u001f\u001a\u00020\r2\u0006\u0010 \u001a\u00020\u00122\u0006\u0010\u0017\u001a\u00020\u00182\u0006\u0010!\u001a\u00020\"2\u0006\u0010#\u001a\u00020\"H\u0016J\u0010\u0010$\u001a\u00020\"2\u0006\u0010%\u001a\u00020&H\u0016J\u0010\u0010$\u001a\u00020\"2\u0006\u0010%\u001a\u00020'H\u0016J \u0010$\u001a\u00020\"2\u0006\u0010%\u001a\u00020'2\u0006\u0010 \u001a\u00020\"2\u0006\u0010#\u001a\u00020\"H\u0016J\u0018\u0010$\u001a\u00020\u00122\u0006\u0010%\u001a\u00020\u00062\u0006\u0010#\u001a\u00020\u0012H\u0016J\u0010\u0010(\u001a\u00020\u00122\u0006\u0010%\u001a\u00020)H\u0016J\b\u0010*\u001a\u00020\u0014H\u0016J\b\u0010+\u001a\u00020'H\u0016J\u0010\u0010+\u001a\u00020'2\u0006\u0010#\u001a\u00020\u0012H\u0016J\b\u0010,\u001a\u00020\u0018H\u0016J\u0010\u0010,\u001a\u00020\u00182\u0006\u0010#\u001a\u00020\u0012H\u0016J\b\u0010-\u001a\u00020\u0012H\u0016J\u0010\u0010.\u001a\u00020\u000f2\u0006\u0010%\u001a\u00020'H\u0016J\u0018\u0010.\u001a\u00020\u000f2\u0006\u0010%\u001a\u00020\u00062\u0006\u0010#\u001a\u00020\u0012H\u0016J\b\u0010/\u001a\u00020\u0012H\u0016J\b\u00100\u001a\u00020\"H\u0016J\b\u00101\u001a\u00020\"H\u0016J\b\u00102\u001a\u00020\u0012H\u0016J\b\u00103\u001a\u00020\u0012H\u0016J\b\u00104\u001a\u000205H\u0016J\b\u00106\u001a\u000205H\u0016J\u0010\u00107\u001a\u0002082\u0006\u00109\u001a\u00020:H\u0016J\u0018\u00107\u001a\u0002082\u0006\u0010#\u001a\u00020\u00122\u0006\u00109\u001a\u00020:H\u0016J\b\u0010;\u001a\u000208H\u0016J\u0010\u0010;\u001a\u0002082\u0006\u0010#\u001a\u00020\u0012H\u0016J\b\u0010<\u001a\u00020\"H\u0016J\n\u0010=\u001a\u0004\u0018\u000108H\u0016J\b\u0010>\u001a\u000208H\u0016J\u0010\u0010>\u001a\u0002082\u0006\u0010?\u001a\u00020\u0012H\u0016J\u0010\u0010@\u001a\u00020\r2\u0006\u0010#\u001a\u00020\u0012H\u0016J\u0010\u0010A\u001a\u00020\u000f2\u0006\u0010#\u001a\u00020\u0012H\u0016J\u0010\u0010B\u001a\u00020\"2\u0006\u0010C\u001a\u00020DH\u0016J\u0010\u0010E\u001a\u00020\u000f2\u0006\u0010#\u001a\u00020\u0012H\u0016J\b\u0010F\u001a\u00020GH\u0016J\b\u0010H\u001a\u000208H\u0016R\u001b\u0010\u0005\u001a\u00020\u00068\u00d6\u0002X\u0096\u0004\u00a2\u0006\f\u0012\u0004\b\u0007\u0010\b\u001a\u0004\b\t\u0010\nR\u0010\u0010\u000b\u001a\u00020\u00068\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0012\u0010\f\u001a\u00020\r8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006I"}, d2={"Lokio/RealBufferedSource;", "Lokio/BufferedSource;", "source", "Lokio/Source;", "(Lokio/Source;)V", "buffer", "Lokio/Buffer;", "getBuffer$annotations", "()V", "getBuffer", "()Lokio/Buffer;", "bufferField", "closed", "", "close", "", "exhausted", "indexOf", "", "b", "", "fromIndex", "toIndex", "bytes", "Lokio/ByteString;", "indexOfElement", "targetBytes", "inputStream", "Ljava/io/InputStream;", "isOpen", "peek", "rangeEquals", "offset", "bytesOffset", "", "byteCount", "read", "sink", "Ljava/nio/ByteBuffer;", "", "readAll", "Lokio/Sink;", "readByte", "readByteArray", "readByteString", "readDecimalLong", "readFully", "readHexadecimalUnsignedLong", "readInt", "readIntLe", "readLong", "readLongLe", "readShort", "", "readShortLe", "readString", "", "charset", "Ljava/nio/charset/Charset;", "readUtf8", "readUtf8CodePoint", "readUtf8Line", "readUtf8LineStrict", "limit", "request", "require", "select", "options", "Lokio/Options;", "skip", "timeout", "Lokio/Timeout;", "toString", "okio"})
@SourceDebugExtension(value={"SMAP\nRealBufferedSource.kt\nKotlin\n*S Kotlin\n*F\n+ 1 RealBufferedSource.kt\nokio/RealBufferedSource\n+ 2 RealBufferedSource.kt\nokio/internal/-RealBufferedSource\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 Util.kt\nokio/-SegmentedByteString\n*L\n1#1,185:1\n62#1:191\n62#1:201\n62#1:208\n62#1:214\n62#1:216\n62#1:220\n62#1:225\n62#1:240\n62#1:244\n62#1:251\n62#1:264\n62#1:272\n62#1:273\n62#1:274\n62#1:280\n62#1:288\n62#1:301\n62#1:305\n62#1:306\n62#1:307\n62#1:308\n62#1:313\n62#1:325\n62#1:341\n62#1:351\n62#1:354\n62#1:357\n62#1:360\n62#1:363\n62#1:366\n62#1:372\n62#1:389\n62#1:409\n62#1:424\n62#1:441\n62#1:454\n62#1:475\n62#1:482\n38#2:186\n39#2,3:188\n42#2,6:192\n51#2:198\n52#2:200\n56#2,2:202\n60#2:204\n61#2,2:206\n63#2,3:209\n69#2,2:212\n74#2:215\n75#2:217\n79#2,2:218\n84#2:221\n86#2,2:223\n88#2,13:226\n107#2:239\n108#2:241\n112#2,2:242\n117#2,6:245\n123#2,9:252\n134#2,3:261\n137#2,5:265\n142#2:271\n146#2,5:275\n151#2,5:281\n158#2,2:286\n160#2,11:289\n174#2:300\n175#2:302\n179#2,2:303\n184#2,4:309\n188#2,6:314\n198#2:320\n199#2,3:322\n202#2,8:326\n210#2,3:335\n217#2,3:338\n220#2,7:342\n230#2,2:349\n235#2,2:352\n240#2,2:355\n245#2,2:358\n250#2,2:361\n255#2,2:364\n260#2,5:367\n265#2,11:373\n279#2,5:384\n284#2,14:390\n301#2,2:404\n303#2,2:407\n305#2,7:410\n314#2,2:417\n316#2,4:420\n320#2,11:425\n334#2,2:436\n337#2,2:439\n339#2,7:442\n350#2,2:449\n353#2,2:452\n355#2,7:455\n371#2:462\n373#2,11:464\n385#2:476\n389#2:477\n393#2,4:478\n397#2:483\n399#2:484\n401#2:485\n1#3:187\n1#3:199\n1#3:205\n1#3:222\n1#3:321\n1#3:406\n1#3:419\n1#3:438\n1#3:451\n1#3:463\n89#4:270\n89#4:334\n*S KotlinDebug\n*F\n+ 1 RealBufferedSource.kt\nokio/RealBufferedSource\n*L\n66#1:191\n67#1:201\n69#1:208\n70#1:214\n71#1:216\n72#1:220\n73#1:225\n74#1:240\n75#1:244\n77#1:251\n79#1:264\n82#1:272\n83#1:273\n87#1:274\n90#1:280\n91#1:288\n92#1:301\n93#1:305\n96#1:306\n97#1:307\n102#1:308\n105#1:313\n107#1:325\n108#1:341\n109#1:351\n110#1:354\n111#1:357\n112#1:360\n113#1:363\n114#1:366\n115#1:372\n116#1:389\n117#1:409\n121#1:424\n124#1:441\n127#1:454\n141#1:475\n181#1:482\n66#1:186\n66#1:188,3\n66#1:192,6\n67#1:198\n67#1:200\n68#1:202,2\n69#1:204\n69#1:206,2\n69#1:209,3\n70#1:212,2\n71#1:215\n71#1:217\n72#1:218,2\n73#1:221\n73#1:223,2\n73#1:226,13\n74#1:239\n74#1:241\n75#1:242,2\n77#1:245,6\n77#1:252,9\n79#1:261,3\n79#1:265,5\n79#1:271\n90#1:275,5\n90#1:281,5\n91#1:286,2\n91#1:289,11\n92#1:300\n92#1:302\n93#1:303,2\n105#1:309,4\n105#1:314,6\n107#1:320\n107#1:322,3\n107#1:326,8\n107#1:335,3\n108#1:338,3\n108#1:342,7\n109#1:349,2\n110#1:352,2\n111#1:355,2\n112#1:358,2\n113#1:361,2\n114#1:364,2\n115#1:367,5\n115#1:373,11\n116#1:384,5\n116#1:390,14\n117#1:404,2\n117#1:407,2\n117#1:410,7\n121#1:417,2\n121#1:420,4\n121#1:425,11\n124#1:436,2\n124#1:439,2\n124#1:442,7\n127#1:449,2\n127#1:452,2\n127#1:455,7\n141#1:462\n141#1:464,11\n141#1:476\n143#1:477\n181#1:478,4\n181#1:483\n182#1:484\n183#1:485\n66#1:187\n67#1:199\n69#1:205\n73#1:222\n107#1:321\n117#1:406\n121#1:419\n124#1:438\n127#1:451\n141#1:463\n79#1:270\n107#1:334\n*E\n"})
public final class RealBufferedSource
implements BufferedSource {
    @JvmField
    @NotNull
    public final Source source;
    @JvmField
    @NotNull
    public final Buffer bufferField;
    @JvmField
    public boolean closed;

    public RealBufferedSource(@NotNull Source source2) {
        Intrinsics.checkNotNullParameter(source2, "source");
        this.source = source2;
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

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public long read(@NotNull Buffer sink2, long byteCount) {
        boolean $i$f$getBuffer;
        RealBufferedSource this_$iv$iv;
        Intrinsics.checkNotNullParameter(sink2, "sink");
        RealBufferedSource $this$commonRead$iv = this;
        boolean $i$f$commonRead = false;
        if (!(byteCount >= 0L)) {
            boolean $i$a$-require--RealBufferedSource$commonRead$1$iv22 = false;
            String $i$a$-require--RealBufferedSource$commonRead$1$iv22 = "byteCount < 0: " + byteCount;
            throw new IllegalArgumentException($i$a$-require--RealBufferedSource$commonRead$1$iv22.toString());
        }
        if (!(!$this$commonRead$iv.closed)) {
            boolean $i$a$-check--RealBufferedSource$commonRead$2$iv22 = false;
            String $i$a$-check--RealBufferedSource$commonRead$2$iv22 = "closed";
            throw new IllegalStateException($i$a$-check--RealBufferedSource$commonRead$2$iv22.toString());
        }
        RealBufferedSource this_$iv$iv2 = $this$commonRead$iv;
        boolean $i$f$getBuffer2 = false;
        if (this_$iv$iv2.bufferField.size() == 0L) {
            this_$iv$iv = $this$commonRead$iv;
            $i$f$getBuffer = false;
            long read$iv = $this$commonRead$iv.source.read(this_$iv$iv.bufferField, 8192L);
            if (read$iv == -1L) {
                return -1L;
            }
        }
        this_$iv$iv = $this$commonRead$iv;
        $i$f$getBuffer = false;
        long l = this_$iv$iv.bufferField.size();
        long toRead$iv = Math.min(byteCount, l);
        this_$iv$iv = $this$commonRead$iv;
        $i$f$getBuffer = false;
        long l2 = this_$iv$iv.bufferField.read(sink2, toRead$iv);
        return l2;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public boolean exhausted() {
        RealBufferedSource $this$commonExhausted$iv = this;
        boolean $i$f$commonExhausted = false;
        if (!(!$this$commonExhausted$iv.closed)) {
            boolean $i$a$-check--RealBufferedSource$commonExhausted$1$iv22 = false;
            String $i$a$-check--RealBufferedSource$commonExhausted$1$iv22 = "closed";
            throw new IllegalStateException($i$a$-check--RealBufferedSource$commonExhausted$1$iv22.toString());
        }
        RealBufferedSource this_$iv$iv = $this$commonExhausted$iv;
        boolean $i$f$getBuffer = false;
        if (!this_$iv$iv.bufferField.exhausted()) return false;
        this_$iv$iv = $this$commonExhausted$iv;
        $i$f$getBuffer = false;
        if ($this$commonExhausted$iv.source.read(this_$iv$iv.bufferField, 8192L) != -1L) return false;
        return true;
    }

    @Override
    public void require(long byteCount) {
        RealBufferedSource $this$commonRequire$iv = this;
        boolean $i$f$commonRequire = false;
        if (!$this$commonRequire$iv.request(byteCount)) {
            throw new EOFException();
        }
    }

    @Override
    public boolean request(long byteCount) {
        boolean bl;
        block4: {
            block3: {
                RealBufferedSource this_$iv$iv;
                RealBufferedSource $this$commonRequest$iv = this;
                boolean $i$f$commonRequest = false;
                if (!(byteCount >= 0L)) {
                    boolean $i$a$-require--RealBufferedSource$commonRequest$1$iv22 = false;
                    String $i$a$-require--RealBufferedSource$commonRequest$1$iv22 = "byteCount < 0: " + byteCount;
                    throw new IllegalArgumentException($i$a$-require--RealBufferedSource$commonRequest$1$iv22.toString());
                }
                if (!(!$this$commonRequest$iv.closed)) {
                    boolean $i$a$-check--RealBufferedSource$commonRequest$2$iv22 = false;
                    String $i$a$-check--RealBufferedSource$commonRequest$2$iv22 = "closed";
                    throw new IllegalStateException($i$a$-check--RealBufferedSource$commonRequest$2$iv22.toString());
                }
                do {
                    this_$iv$iv = $this$commonRequest$iv;
                    boolean $i$f$getBuffer = false;
                    if (this_$iv$iv.bufferField.size() >= byteCount) break block3;
                    this_$iv$iv = $this$commonRequest$iv;
                    $i$f$getBuffer = false;
                } while ($this$commonRequest$iv.source.read(this_$iv$iv.bufferField, 8192L) != -1L);
                bl = false;
                break block4;
            }
            bl = true;
        }
        return bl;
    }

    @Override
    public byte readByte() {
        RealBufferedSource $this$commonReadByte$iv = this;
        boolean $i$f$commonReadByte = false;
        $this$commonReadByte$iv.require(1L);
        RealBufferedSource this_$iv$iv = $this$commonReadByte$iv;
        boolean $i$f$getBuffer = false;
        return this_$iv$iv.bufferField.readByte();
    }

    @Override
    @NotNull
    public ByteString readByteString() {
        RealBufferedSource $this$commonReadByteString$iv = this;
        boolean $i$f$commonReadByteString = false;
        RealBufferedSource this_$iv$iv = $this$commonReadByteString$iv;
        boolean $i$f$getBuffer = false;
        this_$iv$iv.bufferField.writeAll($this$commonReadByteString$iv.source);
        this_$iv$iv = $this$commonReadByteString$iv;
        $i$f$getBuffer = false;
        return this_$iv$iv.bufferField.readByteString();
    }

    @Override
    @NotNull
    public ByteString readByteString(long byteCount) {
        RealBufferedSource $this$commonReadByteString$iv = this;
        boolean $i$f$commonReadByteString = false;
        $this$commonReadByteString$iv.require(byteCount);
        RealBufferedSource this_$iv$iv = $this$commonReadByteString$iv;
        boolean $i$f$getBuffer = false;
        return this_$iv$iv.bufferField.readByteString(byteCount);
    }

    @Override
    public int select(@NotNull Options options) {
        int n;
        Intrinsics.checkNotNullParameter(options, "options");
        RealBufferedSource $this$commonSelect$iv = this;
        boolean $i$f$commonSelect = false;
        if (!(!$this$commonSelect$iv.closed)) {
            boolean $i$a$-check--RealBufferedSource$commonSelect$1$iv22 = false;
            String $i$a$-check--RealBufferedSource$commonSelect$1$iv22 = "closed";
            throw new IllegalStateException($i$a$-check--RealBufferedSource$commonSelect$1$iv22.toString());
        }
        block4: while (true) {
            RealBufferedSource this_$iv$iv = $this$commonSelect$iv;
            boolean $i$f$getBuffer = false;
            int index$iv = -Buffer.selectPrefix(this_$iv$iv.bufferField, options, true);
            switch (index$iv) {
                case -1: {
                    n = -1;
                    break block4;
                }
                case -2: {
                    RealBufferedSource this_$iv$iv2 = $this$commonSelect$iv;
                    boolean $i$f$getBuffer2 = false;
                    if ($this$commonSelect$iv.source.read(this_$iv$iv2.bufferField, 8192L) != -1L) continue block4;
                    n = -1;
                    break block4;
                }
                default: {
                    int selectedSize$iv = options.getByteStrings$okio()[index$iv].size();
                    RealBufferedSource this_$iv$iv3 = $this$commonSelect$iv;
                    boolean $i$f$getBuffer3 = false;
                    this_$iv$iv3.bufferField.skip(selectedSize$iv);
                    n = index$iv;
                    break block4;
                }
            }
            break;
        }
        return n;
    }

    @Override
    @NotNull
    public byte[] readByteArray() {
        RealBufferedSource $this$commonReadByteArray$iv = this;
        boolean $i$f$commonReadByteArray = false;
        RealBufferedSource this_$iv$iv = $this$commonReadByteArray$iv;
        boolean $i$f$getBuffer = false;
        this_$iv$iv.bufferField.writeAll($this$commonReadByteArray$iv.source);
        this_$iv$iv = $this$commonReadByteArray$iv;
        $i$f$getBuffer = false;
        return this_$iv$iv.bufferField.readByteArray();
    }

    @Override
    @NotNull
    public byte[] readByteArray(long byteCount) {
        RealBufferedSource $this$commonReadByteArray$iv = this;
        boolean $i$f$commonReadByteArray = false;
        $this$commonReadByteArray$iv.require(byteCount);
        RealBufferedSource this_$iv$iv = $this$commonReadByteArray$iv;
        boolean $i$f$getBuffer = false;
        return this_$iv$iv.bufferField.readByteArray(byteCount);
    }

    @Override
    public int read(@NotNull byte[] sink2) {
        Intrinsics.checkNotNullParameter(sink2, "sink");
        return this.read(sink2, 0, sink2.length);
    }

    @Override
    public void readFully(@NotNull byte[] sink2) {
        Intrinsics.checkNotNullParameter(sink2, "sink");
        RealBufferedSource $this$commonReadFully$iv = this;
        boolean $i$f$commonReadFully = false;
        try {
            $this$commonReadFully$iv.require(sink2.length);
        }
        catch (EOFException e$iv) {
            int offset$iv = 0;
            while (true) {
                RealBufferedSource this_$iv$iv = $this$commonReadFully$iv;
                boolean $i$f$getBuffer = false;
                if (this_$iv$iv.bufferField.size() <= 0L) break;
                RealBufferedSource this_$iv$iv2 = $this$commonReadFully$iv;
                boolean $i$f$getBuffer2 = false;
                this_$iv$iv2 = $this$commonReadFully$iv;
                $i$f$getBuffer2 = false;
                int read$iv = this_$iv$iv2.bufferField.read(sink2, offset$iv, (int)this_$iv$iv2.bufferField.size());
                if (read$iv == -1) {
                    throw new AssertionError();
                }
                offset$iv += read$iv;
            }
            throw e$iv;
        }
        RealBufferedSource this_$iv$iv = $this$commonReadFully$iv;
        boolean $i$f$getBuffer = false;
        this_$iv$iv.bufferField.readFully(sink2);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public int read(@NotNull byte[] sink2, int offset, int byteCount) {
        Intrinsics.checkNotNullParameter(sink2, "sink");
        RealBufferedSource $this$commonRead$iv = this;
        boolean $i$f$commonRead = false;
        -SegmentedByteString.checkOffsetAndCount(sink2.length, offset, byteCount);
        RealBufferedSource this_$iv$iv = $this$commonRead$iv;
        boolean $i$f$getBuffer = false;
        if (this_$iv$iv.bufferField.size() == 0L) {
            RealBufferedSource this_$iv$iv2 = $this$commonRead$iv;
            boolean $i$f$getBuffer2 = false;
            long read$iv = $this$commonRead$iv.source.read(this_$iv$iv2.bufferField, 8192L);
            if (read$iv == -1L) {
                return -1;
            }
        }
        RealBufferedSource this_$iv$iv3 = $this$commonRead$iv;
        boolean $i$f$getBuffer3 = false;
        long b$iv$iv = this_$iv$iv3.bufferField.size();
        boolean $i$f$minOf = false;
        int toRead$iv = (int)Math.min((long)byteCount, b$iv$iv);
        this_$iv$iv3 = $this$commonRead$iv;
        $i$f$getBuffer3 = false;
        int n = this_$iv$iv3.bufferField.read(sink2, offset, toRead$iv);
        return n;
    }

    @Override
    public int read(@NotNull ByteBuffer sink2) {
        Intrinsics.checkNotNullParameter(sink2, "sink");
        RealBufferedSource this_$iv = this;
        boolean $i$f$getBuffer = false;
        if (this_$iv.bufferField.size() == 0L) {
            RealBufferedSource this_$iv2 = this;
            boolean $i$f$getBuffer2 = false;
            long read = this.source.read(this_$iv2.bufferField, 8192L);
            if (read == -1L) {
                return -1;
            }
        }
        this_$iv = this;
        $i$f$getBuffer = false;
        return this_$iv.bufferField.read(sink2);
    }

    @Override
    public void readFully(@NotNull Buffer sink2, long byteCount) {
        Intrinsics.checkNotNullParameter(sink2, "sink");
        RealBufferedSource $this$commonReadFully$iv = this;
        boolean $i$f$commonReadFully = false;
        try {
            $this$commonReadFully$iv.require(byteCount);
        }
        catch (EOFException e$iv) {
            RealBufferedSource this_$iv$iv = $this$commonReadFully$iv;
            boolean $i$f$getBuffer = false;
            sink2.writeAll(this_$iv$iv.bufferField);
            throw e$iv;
        }
        RealBufferedSource this_$iv$iv = $this$commonReadFully$iv;
        boolean $i$f$getBuffer = false;
        this_$iv$iv.bufferField.readFully(sink2, byteCount);
    }

    @Override
    public long readAll(@NotNull Sink sink2) {
        boolean $i$f$getBuffer;
        RealBufferedSource this_$iv$iv;
        Intrinsics.checkNotNullParameter(sink2, "sink");
        RealBufferedSource $this$commonReadAll$iv = this;
        boolean $i$f$commonReadAll = false;
        long totalBytesWritten$iv = 0L;
        while (true) {
            this_$iv$iv = $this$commonReadAll$iv;
            $i$f$getBuffer = false;
            if ($this$commonReadAll$iv.source.read(this_$iv$iv.bufferField, 8192L) == -1L) break;
            RealBufferedSource this_$iv$iv2 = $this$commonReadAll$iv;
            boolean $i$f$getBuffer2 = false;
            long emitByteCount$iv = this_$iv$iv2.bufferField.completeSegmentByteCount();
            if (emitByteCount$iv <= 0L) continue;
            totalBytesWritten$iv += emitByteCount$iv;
            this_$iv$iv2 = $this$commonReadAll$iv;
            $i$f$getBuffer2 = false;
            sink2.write(this_$iv$iv2.bufferField, emitByteCount$iv);
        }
        this_$iv$iv = $this$commonReadAll$iv;
        $i$f$getBuffer = false;
        if (this_$iv$iv.bufferField.size() > 0L) {
            this_$iv$iv = $this$commonReadAll$iv;
            $i$f$getBuffer = false;
            totalBytesWritten$iv += this_$iv$iv.bufferField.size();
            this_$iv$iv = $this$commonReadAll$iv;
            $i$f$getBuffer = false;
            this_$iv$iv = $this$commonReadAll$iv;
            $i$f$getBuffer = false;
            sink2.write(this_$iv$iv.bufferField, this_$iv$iv.bufferField.size());
        }
        return totalBytesWritten$iv;
    }

    @Override
    @NotNull
    public String readUtf8() {
        RealBufferedSource $this$commonReadUtf8$iv = this;
        boolean $i$f$commonReadUtf8 = false;
        RealBufferedSource this_$iv$iv = $this$commonReadUtf8$iv;
        boolean $i$f$getBuffer = false;
        this_$iv$iv.bufferField.writeAll($this$commonReadUtf8$iv.source);
        this_$iv$iv = $this$commonReadUtf8$iv;
        $i$f$getBuffer = false;
        return this_$iv$iv.bufferField.readUtf8();
    }

    @Override
    @NotNull
    public String readUtf8(long byteCount) {
        RealBufferedSource $this$commonReadUtf8$iv = this;
        boolean $i$f$commonReadUtf8 = false;
        $this$commonReadUtf8$iv.require(byteCount);
        RealBufferedSource this_$iv$iv = $this$commonReadUtf8$iv;
        boolean $i$f$getBuffer = false;
        return this_$iv$iv.bufferField.readUtf8(byteCount);
    }

    @Override
    @NotNull
    public String readString(@NotNull Charset charset) {
        Intrinsics.checkNotNullParameter(charset, "charset");
        RealBufferedSource this_$iv = this;
        boolean $i$f$getBuffer = false;
        this_$iv.bufferField.writeAll(this.source);
        this_$iv = this;
        $i$f$getBuffer = false;
        return this_$iv.bufferField.readString(charset);
    }

    @Override
    @NotNull
    public String readString(long byteCount, @NotNull Charset charset) {
        Intrinsics.checkNotNullParameter(charset, "charset");
        this.require(byteCount);
        RealBufferedSource this_$iv = this;
        boolean $i$f$getBuffer = false;
        return this_$iv.bufferField.readString(byteCount, charset);
    }

    @Override
    @Nullable
    public String readUtf8Line() {
        String string;
        RealBufferedSource $this$commonReadUtf8Line$iv = this;
        boolean $i$f$commonReadUtf8Line = false;
        long newline$iv = $this$commonReadUtf8Line$iv.indexOf((byte)10);
        if (newline$iv == -1L) {
            RealBufferedSource this_$iv$iv = $this$commonReadUtf8Line$iv;
            boolean $i$f$getBuffer = false;
            if (this_$iv$iv.bufferField.size() != 0L) {
                this_$iv$iv = $this$commonReadUtf8Line$iv;
                $i$f$getBuffer = false;
                string = $this$commonReadUtf8Line$iv.readUtf8(this_$iv$iv.bufferField.size());
            } else {
                string = null;
            }
        } else {
            RealBufferedSource this_$iv$iv = $this$commonReadUtf8Line$iv;
            boolean $i$f$getBuffer = false;
            string = -Buffer.readUtf8Line(this_$iv$iv.bufferField, newline$iv);
        }
        return string;
    }

    @Override
    @NotNull
    public String readUtf8LineStrict() {
        return this.readUtf8LineStrict(Long.MAX_VALUE);
    }

    /*
     * WARNING - void declaration
     * Enabled aggressive block sorting
     */
    @Override
    @NotNull
    public String readUtf8LineStrict(long limit) {
        void a$iv$iv;
        String string;
        RealBufferedSource $this$commonReadUtf8LineStrict$iv = this;
        boolean $i$f$commonReadUtf8LineStrict = false;
        if (!(limit >= 0L)) {
            boolean bl = false;
            String string2 = "limit < 0: " + limit;
            throw new IllegalArgumentException(string2.toString());
        }
        long scanLength$iv = limit == Long.MAX_VALUE ? Long.MAX_VALUE : limit + 1L;
        long newline$iv = $this$commonReadUtf8LineStrict$iv.indexOf((byte)10, 0L, scanLength$iv);
        if (newline$iv != -1L) {
            RealBufferedSource this_$iv$iv = $this$commonReadUtf8LineStrict$iv;
            boolean $i$f$getBuffer = false;
            string = -Buffer.readUtf8Line(this_$iv$iv.bufferField, newline$iv);
            return string;
        }
        if (scanLength$iv < Long.MAX_VALUE && $this$commonReadUtf8LineStrict$iv.request(scanLength$iv)) {
            RealBufferedSource this_$iv$iv = $this$commonReadUtf8LineStrict$iv;
            boolean $i$f$getBuffer = false;
            if (this_$iv$iv.bufferField.getByte(scanLength$iv - 1L) == 13 && $this$commonReadUtf8LineStrict$iv.request(scanLength$iv + 1L)) {
                this_$iv$iv = $this$commonReadUtf8LineStrict$iv;
                $i$f$getBuffer = false;
                if (this_$iv$iv.bufferField.getByte(scanLength$iv) == 10) {
                    this_$iv$iv = $this$commonReadUtf8LineStrict$iv;
                    $i$f$getBuffer = false;
                    string = -Buffer.readUtf8Line(this_$iv$iv.bufferField, scanLength$iv);
                    return string;
                }
            }
        }
        Buffer data$iv = new Buffer();
        RealBufferedSource this_$iv$iv22 = $this$commonReadUtf8LineStrict$iv;
        boolean $i$f$getBuffer = false;
        int this_$iv$iv22 = 32;
        RealBufferedSource this_$iv$iv = $this$commonReadUtf8LineStrict$iv;
        boolean $i$f$getBuffer2 = false;
        long b$iv$iv = this_$iv$iv.bufferField.size();
        boolean $i$f$minOf = false;
        this_$iv$iv22.bufferField.copyTo(data$iv, 0L, Math.min((long)a$iv$iv, b$iv$iv));
        RealBufferedSource this_$iv$iv3 = $this$commonReadUtf8LineStrict$iv;
        $i$f$getBuffer = false;
        throw new EOFException("\\n not found: limit=" + Math.min(this_$iv$iv3.bufferField.size(), limit) + " content=" + data$iv.readByteString().hex() + '\u2026');
    }

    @Override
    public int readUtf8CodePoint() {
        RealBufferedSource $this$commonReadUtf8CodePoint$iv = this;
        boolean $i$f$commonReadUtf8CodePoint = false;
        $this$commonReadUtf8CodePoint$iv.require(1L);
        RealBufferedSource this_$iv$iv = $this$commonReadUtf8CodePoint$iv;
        boolean $i$f$getBuffer = false;
        byte b0$iv = this_$iv$iv.bufferField.getByte(0L);
        if ((b0$iv & 0xE0) == 192) {
            $this$commonReadUtf8CodePoint$iv.require(2L);
        } else if ((b0$iv & 0xF0) == 224) {
            $this$commonReadUtf8CodePoint$iv.require(3L);
        } else if ((b0$iv & 0xF8) == 240) {
            $this$commonReadUtf8CodePoint$iv.require(4L);
        }
        this_$iv$iv = $this$commonReadUtf8CodePoint$iv;
        $i$f$getBuffer = false;
        return this_$iv$iv.bufferField.readUtf8CodePoint();
    }

    @Override
    public short readShort() {
        RealBufferedSource $this$commonReadShort$iv = this;
        boolean $i$f$commonReadShort = false;
        $this$commonReadShort$iv.require(2L);
        RealBufferedSource this_$iv$iv = $this$commonReadShort$iv;
        boolean $i$f$getBuffer = false;
        return this_$iv$iv.bufferField.readShort();
    }

    @Override
    public short readShortLe() {
        RealBufferedSource $this$commonReadShortLe$iv = this;
        boolean $i$f$commonReadShortLe = false;
        $this$commonReadShortLe$iv.require(2L);
        RealBufferedSource this_$iv$iv = $this$commonReadShortLe$iv;
        boolean $i$f$getBuffer = false;
        return this_$iv$iv.bufferField.readShortLe();
    }

    @Override
    public int readInt() {
        RealBufferedSource $this$commonReadInt$iv = this;
        boolean $i$f$commonReadInt = false;
        $this$commonReadInt$iv.require(4L);
        RealBufferedSource this_$iv$iv = $this$commonReadInt$iv;
        boolean $i$f$getBuffer = false;
        return this_$iv$iv.bufferField.readInt();
    }

    @Override
    public int readIntLe() {
        RealBufferedSource $this$commonReadIntLe$iv = this;
        boolean $i$f$commonReadIntLe = false;
        $this$commonReadIntLe$iv.require(4L);
        RealBufferedSource this_$iv$iv = $this$commonReadIntLe$iv;
        boolean $i$f$getBuffer = false;
        return this_$iv$iv.bufferField.readIntLe();
    }

    @Override
    public long readLong() {
        RealBufferedSource $this$commonReadLong$iv = this;
        boolean $i$f$commonReadLong = false;
        $this$commonReadLong$iv.require(8L);
        RealBufferedSource this_$iv$iv = $this$commonReadLong$iv;
        boolean $i$f$getBuffer = false;
        return this_$iv$iv.bufferField.readLong();
    }

    @Override
    public long readLongLe() {
        RealBufferedSource $this$commonReadLongLe$iv = this;
        boolean $i$f$commonReadLongLe = false;
        $this$commonReadLongLe$iv.require(8L);
        RealBufferedSource this_$iv$iv = $this$commonReadLongLe$iv;
        boolean $i$f$getBuffer = false;
        return this_$iv$iv.bufferField.readLongLe();
    }

    @Override
    public long readDecimalLong() {
        RealBufferedSource $this$commonReadDecimalLong$iv = this;
        boolean $i$f$commonReadDecimalLong = false;
        $this$commonReadDecimalLong$iv.require(1L);
        long pos$iv = 0L;
        while ($this$commonReadDecimalLong$iv.request(pos$iv + 1L)) {
            RealBufferedSource this_$iv$iv = $this$commonReadDecimalLong$iv;
            boolean $i$f$getBuffer = false;
            byte b$iv = this_$iv$iv.bufferField.getByte(pos$iv);
            if (!(b$iv >= 48 && b$iv <= 57 || pos$iv == 0L && b$iv == 45)) {
                if (pos$iv != 0L) break;
                StringBuilder stringBuilder = new StringBuilder().append("Expected a digit or '-' but was 0x");
                String string = Integer.toString(b$iv, CharsKt.checkRadix(CharsKt.checkRadix(16)));
                Intrinsics.checkNotNullExpressionValue(string, "toString(this, checkRadix(radix))");
                throw new NumberFormatException(stringBuilder.append(string).toString());
            }
            ++pos$iv;
        }
        RealBufferedSource this_$iv$iv = $this$commonReadDecimalLong$iv;
        boolean $i$f$getBuffer = false;
        return this_$iv$iv.bufferField.readDecimalLong();
    }

    @Override
    public long readHexadecimalUnsignedLong() {
        RealBufferedSource $this$commonReadHexadecimalUnsignedLong$iv = this;
        boolean $i$f$commonReadHexadecimalUnsignedLong = false;
        $this$commonReadHexadecimalUnsignedLong$iv.require(1L);
        int pos$iv = 0;
        while ($this$commonReadHexadecimalUnsignedLong$iv.request(pos$iv + 1)) {
            RealBufferedSource this_$iv$iv = $this$commonReadHexadecimalUnsignedLong$iv;
            boolean $i$f$getBuffer = false;
            byte b$iv = this_$iv$iv.bufferField.getByte(pos$iv);
            if (!(b$iv >= 48 && b$iv <= 57 || b$iv >= 97 && b$iv <= 102 || b$iv >= 65 && b$iv <= 70)) {
                if (pos$iv != 0) break;
                StringBuilder stringBuilder = new StringBuilder().append("Expected leading [0-9a-fA-F] character but was 0x");
                String string = Integer.toString(b$iv, CharsKt.checkRadix(CharsKt.checkRadix(16)));
                Intrinsics.checkNotNullExpressionValue(string, "toString(this, checkRadix(radix))");
                throw new NumberFormatException(stringBuilder.append(string).toString());
            }
            ++pos$iv;
        }
        RealBufferedSource this_$iv$iv = $this$commonReadHexadecimalUnsignedLong$iv;
        boolean $i$f$getBuffer = false;
        return this_$iv$iv.bufferField.readHexadecimalUnsignedLong();
    }

    @Override
    public void skip(long byteCount) {
        long toSkip$iv;
        RealBufferedSource $this$commonSkip$iv = this;
        boolean $i$f$commonSkip = false;
        if (!(!$this$commonSkip$iv.closed)) {
            boolean $i$a$-check--RealBufferedSource$commonSkip$1$iv22 = false;
            String $i$a$-check--RealBufferedSource$commonSkip$1$iv22 = "closed";
            throw new IllegalStateException($i$a$-check--RealBufferedSource$commonSkip$1$iv22.toString());
        }
        for (long byteCount$iv = byteCount; byteCount$iv > 0L; byteCount$iv -= toSkip$iv) {
            RealBufferedSource this_$iv$iv = $this$commonSkip$iv;
            boolean $i$f$getBuffer = false;
            if (this_$iv$iv.bufferField.size() == 0L) {
                this_$iv$iv = $this$commonSkip$iv;
                $i$f$getBuffer = false;
                if ($this$commonSkip$iv.source.read(this_$iv$iv.bufferField, 8192L) == -1L) {
                    throw new EOFException();
                }
            }
            RealBufferedSource this_$iv$iv2 = $this$commonSkip$iv;
            boolean $i$f$getBuffer2 = false;
            long l = this_$iv$iv2.bufferField.size();
            toSkip$iv = Math.min(byteCount$iv, l);
            this_$iv$iv2 = $this$commonSkip$iv;
            $i$f$getBuffer2 = false;
            this_$iv$iv2.bufferField.skip(toSkip$iv);
        }
    }

    @Override
    public long indexOf(byte b) {
        return this.indexOf(b, 0L, Long.MAX_VALUE);
    }

    @Override
    public long indexOf(byte b, long fromIndex) {
        return this.indexOf(b, fromIndex, Long.MAX_VALUE);
    }

    @Override
    public long indexOf(byte b, long fromIndex, long toIndex) {
        long l;
        block6: {
            RealBufferedSource $this$commonIndexOf$iv = this;
            boolean $i$f$commonIndexOf = false;
            long fromIndex$iv = 0L;
            fromIndex$iv = fromIndex;
            if (!(!$this$commonIndexOf$iv.closed)) {
                boolean $i$a$-check--RealBufferedSource$commonIndexOf$1$iv22 = false;
                String $i$a$-check--RealBufferedSource$commonIndexOf$1$iv22 = "closed";
                throw new IllegalStateException($i$a$-check--RealBufferedSource$commonIndexOf$1$iv22.toString());
            }
            if (!(0L <= fromIndex$iv ? fromIndex$iv <= toIndex : false)) {
                boolean bl = false;
                String string = "fromIndex=" + fromIndex$iv + " toIndex=" + toIndex;
                throw new IllegalArgumentException(string.toString());
            }
            while (fromIndex$iv < toIndex) {
                long lastBufferSize$iv;
                block8: {
                    block7: {
                        RealBufferedSource this_$iv$iv = $this$commonIndexOf$iv;
                        boolean $i$f$getBuffer = false;
                        long result$iv = this_$iv$iv.bufferField.indexOf(b, fromIndex$iv, toIndex);
                        if (result$iv != -1L) {
                            l = result$iv;
                            break block6;
                        }
                        RealBufferedSource this_$iv$iv2 = $this$commonIndexOf$iv;
                        boolean $i$f$getBuffer2 = false;
                        lastBufferSize$iv = this_$iv$iv2.bufferField.size();
                        if (lastBufferSize$iv >= toIndex) break block7;
                        this_$iv$iv2 = $this$commonIndexOf$iv;
                        $i$f$getBuffer2 = false;
                        if ($this$commonIndexOf$iv.source.read(this_$iv$iv2.bufferField, 8192L) != -1L) break block8;
                    }
                    l = -1L;
                    break block6;
                }
                fromIndex$iv = Math.max(fromIndex$iv, lastBufferSize$iv);
            }
            l = -1L;
        }
        return l;
    }

    @Override
    public long indexOf(@NotNull ByteString bytes) {
        Intrinsics.checkNotNullParameter(bytes, "bytes");
        return this.indexOf(bytes, 0L);
    }

    @Override
    public long indexOf(@NotNull ByteString bytes, long fromIndex) {
        long l;
        Intrinsics.checkNotNullParameter(bytes, "bytes");
        RealBufferedSource $this$commonIndexOf$iv = this;
        boolean $i$f$commonIndexOf = false;
        long fromIndex$iv = fromIndex;
        if (!(!$this$commonIndexOf$iv.closed)) {
            boolean bl = false;
            String string = "closed";
            throw new IllegalStateException(string.toString());
        }
        while (true) {
            RealBufferedSource this_$iv$iv = $this$commonIndexOf$iv;
            boolean $i$f$getBuffer = false;
            long result$iv = this_$iv$iv.bufferField.indexOf(bytes, fromIndex$iv);
            if (result$iv != -1L) {
                l = result$iv;
                break;
            }
            RealBufferedSource this_$iv$iv2 = $this$commonIndexOf$iv;
            boolean $i$f$getBuffer2 = false;
            long lastBufferSize$iv = this_$iv$iv2.bufferField.size();
            this_$iv$iv2 = $this$commonIndexOf$iv;
            $i$f$getBuffer2 = false;
            if ($this$commonIndexOf$iv.source.read(this_$iv$iv2.bufferField, 8192L) == -1L) {
                l = -1L;
                break;
            }
            fromIndex$iv = Math.max(fromIndex$iv, lastBufferSize$iv - (long)bytes.size() + 1L);
        }
        return l;
    }

    @Override
    public long indexOfElement(@NotNull ByteString targetBytes) {
        Intrinsics.checkNotNullParameter(targetBytes, "targetBytes");
        return this.indexOfElement(targetBytes, 0L);
    }

    @Override
    public long indexOfElement(@NotNull ByteString targetBytes, long fromIndex) {
        long l;
        Intrinsics.checkNotNullParameter(targetBytes, "targetBytes");
        RealBufferedSource $this$commonIndexOfElement$iv = this;
        boolean $i$f$commonIndexOfElement = false;
        long fromIndex$iv = fromIndex;
        if (!(!$this$commonIndexOfElement$iv.closed)) {
            boolean bl = false;
            String string = "closed";
            throw new IllegalStateException(string.toString());
        }
        while (true) {
            RealBufferedSource this_$iv$iv = $this$commonIndexOfElement$iv;
            boolean $i$f$getBuffer = false;
            long result$iv = this_$iv$iv.bufferField.indexOfElement(targetBytes, fromIndex$iv);
            if (result$iv != -1L) {
                l = result$iv;
                break;
            }
            RealBufferedSource this_$iv$iv2 = $this$commonIndexOfElement$iv;
            boolean $i$f$getBuffer2 = false;
            long lastBufferSize$iv = this_$iv$iv2.bufferField.size();
            this_$iv$iv2 = $this$commonIndexOfElement$iv;
            $i$f$getBuffer2 = false;
            if ($this$commonIndexOfElement$iv.source.read(this_$iv$iv2.bufferField, 8192L) == -1L) {
                l = -1L;
                break;
            }
            fromIndex$iv = Math.max(fromIndex$iv, lastBufferSize$iv);
        }
        return l;
    }

    @Override
    public boolean rangeEquals(long offset, @NotNull ByteString bytes) {
        Intrinsics.checkNotNullParameter(bytes, "bytes");
        return this.rangeEquals(offset, bytes, 0, bytes.size());
    }

    @Override
    public boolean rangeEquals(long offset, @NotNull ByteString bytes, int bytesOffset, int byteCount) {
        boolean bl;
        block6: {
            Intrinsics.checkNotNullParameter(bytes, "bytes");
            RealBufferedSource $this$commonRangeEquals$iv = this;
            boolean $i$f$commonRangeEquals = false;
            if (!(!$this$commonRangeEquals$iv.closed)) {
                boolean bl2 = false;
                String string = "closed";
                throw new IllegalStateException(string.toString());
            }
            if (offset < 0L || bytesOffset < 0 || byteCount < 0 || bytes.size() - bytesOffset < byteCount) {
                bl = false;
            } else {
                for (int i$iv = 0; i$iv < byteCount; ++i$iv) {
                    long bufferOffset$iv = offset + (long)i$iv;
                    if (!$this$commonRangeEquals$iv.request(bufferOffset$iv + 1L)) {
                        bl = false;
                    } else {
                        RealBufferedSource this_$iv$iv = $this$commonRangeEquals$iv;
                        boolean $i$f$getBuffer = false;
                        if (this_$iv$iv.bufferField.getByte(bufferOffset$iv) == bytes.getByte(bytesOffset + i$iv)) continue;
                        bl = false;
                    }
                    break block6;
                }
                bl = true;
            }
        }
        return bl;
    }

    @Override
    @NotNull
    public BufferedSource peek() {
        RealBufferedSource $this$commonPeek$iv = this;
        boolean $i$f$commonPeek = false;
        return Okio.buffer(new PeekSource($this$commonPeek$iv));
    }

    @Override
    @NotNull
    public InputStream inputStream() {
        return new InputStream(this){
            final /* synthetic */ RealBufferedSource this$0;
            {
                this.this$0 = $receiver;
            }

            /*
             * WARNING - void declaration
             */
            public int read() {
                void $this$and$iv;
                if (this.this$0.closed) {
                    throw new IOException("closed");
                }
                RealBufferedSource this_$iv = this.this$0;
                boolean $i$f$getBuffer = false;
                if (this_$iv.bufferField.size() == 0L) {
                    RealBufferedSource this_$iv2 = this.this$0;
                    boolean $i$f$getBuffer2 = false;
                    long count = this.this$0.source.read(this_$iv2.bufferField, 8192L);
                    if (count == -1L) {
                        return -1;
                    }
                }
                this_$iv = this.this$0;
                $i$f$getBuffer = false;
                byte this_$iv2 = this_$iv.bufferField.readByte();
                int other$iv = 255;
                boolean $i$f$and = false;
                return $this$and$iv & other$iv;
            }

            public int read(@NotNull byte[] data, int offset, int byteCount) {
                Intrinsics.checkNotNullParameter(data, "data");
                if (this.this$0.closed) {
                    throw new IOException("closed");
                }
                -SegmentedByteString.checkOffsetAndCount(data.length, offset, byteCount);
                RealBufferedSource this_$iv = this.this$0;
                boolean $i$f$getBuffer = false;
                if (this_$iv.bufferField.size() == 0L) {
                    RealBufferedSource this_$iv2 = this.this$0;
                    boolean $i$f$getBuffer2 = false;
                    long count = this.this$0.source.read(this_$iv2.bufferField, 8192L);
                    if (count == -1L) {
                        return -1;
                    }
                }
                this_$iv = this.this$0;
                $i$f$getBuffer = false;
                return this_$iv.bufferField.read(data, offset, byteCount);
            }

            /*
             * WARNING - void declaration
             */
            public int available() {
                void a$iv;
                if (this.this$0.closed) {
                    throw new IOException("closed");
                }
                RealBufferedSource this_$iv22 = this.this$0;
                boolean $i$f$getBuffer = false;
                long this_$iv22 = this_$iv22.bufferField.size();
                int b$iv = Integer.MAX_VALUE;
                boolean $i$f$minOf = false;
                return (int)Math.min((long)a$iv, (long)b$iv);
            }

            public void close() {
                this.this$0.close();
            }

            @NotNull
            public String toString() {
                return this.this$0 + ".inputStream()";
            }
        };
    }

    @Override
    public boolean isOpen() {
        return !this.closed;
    }

    @Override
    public void close() {
        RealBufferedSource $this$commonClose$iv = this;
        boolean $i$f$commonClose = false;
        if (!$this$commonClose$iv.closed) {
            $this$commonClose$iv.closed = true;
            $this$commonClose$iv.source.close();
            RealBufferedSource this_$iv$iv = $this$commonClose$iv;
            boolean $i$f$getBuffer = false;
            this_$iv$iv.bufferField.clear();
        }
    }

    @Override
    @NotNull
    public Timeout timeout() {
        RealBufferedSource $this$commonTimeout$iv = this;
        boolean $i$f$commonTimeout = false;
        return $this$commonTimeout$iv.source.timeout();
    }

    @NotNull
    public String toString() {
        RealBufferedSource $this$commonToString$iv = this;
        boolean $i$f$commonToString = false;
        return "buffer(" + $this$commonToString$iv.source + ')';
    }
}

