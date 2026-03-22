/*
 * Decompiled with CFR 0.152.
 */
package okio;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import kotlin.Metadata;
import kotlin.collections.ArraysKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import okio.Buffer;
import okio.ByteString;
import okio.Segment;
import okio.internal.-SegmentedByteString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000j\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0011\n\u0002\u0010\u0012\n\u0000\n\u0002\u0010\u0015\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0002\b\f\n\u0002\u0010\u0005\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u0000\u0018\u00002\u00020\u0001B\u001d\b\u0000\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\u0002\u0010\u0007J\b\u0010\r\u001a\u00020\u000eH\u0016J\b\u0010\u000f\u001a\u00020\u0010H\u0016J\b\u0010\u0011\u001a\u00020\u0010H\u0016J(\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u00042\u0006\u0010\u0017\u001a\u00020\u00152\u0006\u0010\u0018\u001a\u00020\u0015H\u0016J\u0015\u0010\u0019\u001a\u00020\u00012\u0006\u0010\u001a\u001a\u00020\u0010H\u0010\u00a2\u0006\u0002\b\u001bJ\u0013\u0010\u001c\u001a\u00020\u001d2\b\u0010\u001e\u001a\u0004\u0018\u00010\u001fH\u0096\u0002J\r\u0010 \u001a\u00020\u0015H\u0010\u00a2\u0006\u0002\b!J\b\u0010\"\u001a\u00020\u0015H\u0016J\b\u0010#\u001a\u00020\u0010H\u0016J\u001d\u0010$\u001a\u00020\u00012\u0006\u0010\u001a\u001a\u00020\u00102\u0006\u0010%\u001a\u00020\u0001H\u0010\u00a2\u0006\u0002\b&J\u0018\u0010'\u001a\u00020\u00152\u0006\u0010\u001e\u001a\u00020\u00042\u0006\u0010(\u001a\u00020\u0015H\u0016J\r\u0010)\u001a\u00020\u0004H\u0010\u00a2\u0006\u0002\b*J\u0015\u0010+\u001a\u00020,2\u0006\u0010-\u001a\u00020\u0015H\u0010\u00a2\u0006\u0002\b.J\u0018\u0010/\u001a\u00020\u00152\u0006\u0010\u001e\u001a\u00020\u00042\u0006\u0010(\u001a\u00020\u0015H\u0016J(\u00100\u001a\u00020\u001d2\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u001e\u001a\u00020\u00042\u0006\u00101\u001a\u00020\u00152\u0006\u0010\u0018\u001a\u00020\u0015H\u0016J(\u00100\u001a\u00020\u001d2\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u001e\u001a\u00020\u00012\u0006\u00101\u001a\u00020\u00152\u0006\u0010\u0018\u001a\u00020\u0015H\u0016J\u0010\u00102\u001a\u00020\u00102\u0006\u00103\u001a\u000204H\u0016J\u0018\u00105\u001a\u00020\u00012\u0006\u00106\u001a\u00020\u00152\u0006\u00107\u001a\u00020\u0015H\u0016J\b\u00108\u001a\u00020\u0001H\u0016J\b\u00109\u001a\u00020\u0001H\u0016J\b\u0010:\u001a\u00020\u0004H\u0016J\b\u0010;\u001a\u00020\u0001H\u0002J\b\u0010<\u001a\u00020\u0010H\u0016J\u0010\u0010=\u001a\u00020\u00132\u0006\u0010>\u001a\u00020?H\u0016J%\u0010=\u001a\u00020\u00132\u0006\u0010@\u001a\u00020A2\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0018\u001a\u00020\u0015H\u0010\u00a2\u0006\u0002\bBJ\b\u0010C\u001a\u00020DH\u0002R\u0014\u0010\u0005\u001a\u00020\u0006X\u0080\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u001c\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003X\u0080\u0004\u00a2\u0006\n\n\u0002\u0010\f\u001a\u0004\b\n\u0010\u000b\u00a8\u0006E"}, d2={"Lokio/SegmentedByteString;", "Lokio/ByteString;", "segments", "", "", "directory", "", "([[B[I)V", "getDirectory$okio", "()[I", "getSegments$okio", "()[[B", "[[B", "asByteBuffer", "Ljava/nio/ByteBuffer;", "base64", "", "base64Url", "copyInto", "", "offset", "", "target", "targetOffset", "byteCount", "digest", "algorithm", "digest$okio", "equals", "", "other", "", "getSize", "getSize$okio", "hashCode", "hex", "hmac", "key", "hmac$okio", "indexOf", "fromIndex", "internalArray", "internalArray$okio", "internalGet", "", "pos", "internalGet$okio", "lastIndexOf", "rangeEquals", "otherOffset", "string", "charset", "Ljava/nio/charset/Charset;", "substring", "beginIndex", "endIndex", "toAsciiLowercase", "toAsciiUppercase", "toByteArray", "toByteString", "toString", "write", "out", "Ljava/io/OutputStream;", "buffer", "Lokio/Buffer;", "write$okio", "writeReplace", "Ljava/lang/Object;", "okio"})
@SourceDebugExtension(value={"SMAP\nSegmentedByteString.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SegmentedByteString.kt\nokio/SegmentedByteString\n+ 2 SegmentedByteString.kt\nokio/internal/-SegmentedByteString\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,140:1\n63#2,12:141\n63#2,12:153\n104#2,2:165\n106#2,26:168\n135#2,5:194\n142#2:199\n145#2,3:200\n63#2,8:203\n148#2,8:211\n71#2,4:219\n156#2:223\n63#2,12:224\n160#2:236\n85#2,10:237\n161#2,9:247\n95#2,4:256\n170#2,2:260\n179#2,4:262\n85#2,10:266\n183#2,3:276\n95#2,4:279\n186#2:283\n195#2,8:284\n85#2,10:292\n203#2,3:302\n95#2,4:305\n206#2:309\n215#2,5:310\n85#2,10:315\n220#2,3:325\n95#2,4:328\n223#2:332\n226#2,4:333\n234#2,6:337\n63#2,8:343\n240#2,7:351\n71#2,4:358\n247#2,2:362\n1#3:167\n*S KotlinDebug\n*F\n+ 1 SegmentedByteString.kt\nokio/SegmentedByteString\n*L\n54#1:141,12\n66#1:153,12\n78#1:165,2\n78#1:168,26\n80#1:194,5\n82#1:199\n84#1:200,3\n84#1:203,8\n84#1:211,8\n84#1:219,4\n84#1:223\n90#1:224,12\n96#1:236\n96#1:237,10\n96#1:247,9\n96#1:256,4\n96#1:260,2\n103#1:262,4\n103#1:266,10\n103#1:276,3\n103#1:279,4\n103#1:283\n110#1:284,8\n110#1:292,10\n110#1:302,3\n110#1:305,4\n110#1:309\n117#1:310,5\n117#1:315,10\n117#1:325,3\n117#1:328,4\n117#1:332\n131#1:333,4\n133#1:337,6\n133#1:343,8\n133#1:351,7\n133#1:358,4\n133#1:362,2\n78#1:167\n*E\n"})
public final class SegmentedByteString
extends ByteString {
    @NotNull
    private final transient byte[][] segments;
    @NotNull
    private final transient int[] directory;

    public SegmentedByteString(@NotNull byte[][] segments, @NotNull int[] directory) {
        Intrinsics.checkNotNullParameter(segments, "segments");
        Intrinsics.checkNotNullParameter(directory, "directory");
        super(ByteString.EMPTY.getData$okio());
        this.segments = segments;
        this.directory = directory;
    }

    @NotNull
    public final byte[][] getSegments$okio() {
        return this.segments;
    }

    @NotNull
    public final int[] getDirectory$okio() {
        return this.directory;
    }

    @Override
    @NotNull
    public String string(@NotNull Charset charset) {
        Intrinsics.checkNotNullParameter(charset, "charset");
        return this.toByteString().string(charset);
    }

    @Override
    @NotNull
    public String base64() {
        return this.toByteString().base64();
    }

    @Override
    @NotNull
    public String hex() {
        return this.toByteString().hex();
    }

    @Override
    @NotNull
    public ByteString toAsciiLowercase() {
        return this.toByteString().toAsciiLowercase();
    }

    @Override
    @NotNull
    public ByteString toAsciiUppercase() {
        return this.toByteString().toAsciiUppercase();
    }

    /*
     * WARNING - void declaration
     */
    @Override
    @NotNull
    public ByteString digest$okio(@NotNull String algorithm) {
        Intrinsics.checkNotNullParameter(algorithm, "algorithm");
        MessageDigest $this$digest_u24lambda_u241 = MessageDigest.getInstance(algorithm);
        boolean bl = false;
        SegmentedByteString $this$forEachSegment$iv = this;
        boolean $i$f$forEachSegment = false;
        int segmentCount$iv = ((Object[])$this$forEachSegment$iv.getSegments$okio()).length;
        int pos$iv = 0;
        for (int s$iv = 0; s$iv < segmentCount$iv; ++s$iv) {
            void byteCount;
            void offset;
            int segmentPos$iv = $this$forEachSegment$iv.getDirectory$okio()[segmentCount$iv + s$iv];
            int nextSegmentOffset$iv = $this$forEachSegment$iv.getDirectory$okio()[s$iv];
            int n = nextSegmentOffset$iv - pos$iv;
            int n2 = segmentPos$iv;
            byte[] data = $this$forEachSegment$iv.getSegments$okio()[s$iv];
            boolean bl2 = false;
            $this$digest_u24lambda_u241.update(data, (int)offset, (int)byteCount);
            pos$iv = nextSegmentOffset$iv;
        }
        byte[] digestBytes = $this$digest_u24lambda_u241.digest();
        Intrinsics.checkNotNull(digestBytes);
        return new ByteString(digestBytes);
    }

    /*
     * WARNING - void declaration
     */
    @Override
    @NotNull
    public ByteString hmac$okio(@NotNull String algorithm, @NotNull ByteString key) {
        Intrinsics.checkNotNullParameter(algorithm, "algorithm");
        Intrinsics.checkNotNullParameter(key, "key");
        try {
            Mac mac = Mac.getInstance(algorithm);
            mac.init(new SecretKeySpec(key.toByteArray(), algorithm));
            SegmentedByteString $this$forEachSegment$iv = this;
            boolean $i$f$forEachSegment = false;
            int segmentCount$iv = ((Object[])$this$forEachSegment$iv.getSegments$okio()).length;
            int pos$iv = 0;
            for (int s$iv = 0; s$iv < segmentCount$iv; ++s$iv) {
                void byteCount;
                void offset;
                int segmentPos$iv = $this$forEachSegment$iv.getDirectory$okio()[segmentCount$iv + s$iv];
                int nextSegmentOffset$iv = $this$forEachSegment$iv.getDirectory$okio()[s$iv];
                int n = nextSegmentOffset$iv - pos$iv;
                int n2 = segmentPos$iv;
                byte[] data = $this$forEachSegment$iv.getSegments$okio()[s$iv];
                boolean bl = false;
                mac.update(data, (int)offset, (int)byteCount);
                pos$iv = nextSegmentOffset$iv;
            }
            byte[] byArray = mac.doFinal();
            Intrinsics.checkNotNullExpressionValue(byArray, "doFinal(...)");
            return new ByteString(byArray);
        }
        catch (InvalidKeyException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    @NotNull
    public String base64Url() {
        return this.toByteString().base64Url();
    }

    @Override
    @NotNull
    public ByteString substring(int beginIndex, int endIndex) {
        ByteString byteString;
        SegmentedByteString $this$commonSubstring$iv = this;
        boolean $i$f$commonSubstring = false;
        int endIndex$iv = okio.-SegmentedByteString.resolveDefaultParameter($this$commonSubstring$iv, endIndex);
        if (!(beginIndex >= 0)) {
            boolean $i$a$-require--SegmentedByteString$commonSubstring$1$iv22 = false;
            String $i$a$-require--SegmentedByteString$commonSubstring$1$iv22 = "beginIndex=" + beginIndex + " < 0";
            throw new IllegalArgumentException($i$a$-require--SegmentedByteString$commonSubstring$1$iv22.toString());
        }
        if (!(endIndex$iv <= $this$commonSubstring$iv.size())) {
            boolean $i$a$-require--SegmentedByteString$commonSubstring$2$iv22 = false;
            String $i$a$-require--SegmentedByteString$commonSubstring$2$iv22 = "endIndex=" + endIndex$iv + " > length(" + $this$commonSubstring$iv.size() + ')';
            throw new IllegalArgumentException($i$a$-require--SegmentedByteString$commonSubstring$2$iv22.toString());
        }
        int subLen$iv = endIndex$iv - beginIndex;
        if (!(subLen$iv >= 0)) {
            boolean $i$a$-require--SegmentedByteString$commonSubstring$3$iv22 = false;
            String $i$a$-require--SegmentedByteString$commonSubstring$3$iv22 = "endIndex=" + endIndex$iv + " < beginIndex=" + beginIndex;
            throw new IllegalArgumentException($i$a$-require--SegmentedByteString$commonSubstring$3$iv22.toString());
        }
        if (beginIndex == 0 && endIndex$iv == $this$commonSubstring$iv.size()) {
            byteString = $this$commonSubstring$iv;
        } else if (beginIndex == endIndex$iv) {
            byteString = ByteString.EMPTY;
        } else {
            int beginSegment$iv = -SegmentedByteString.segment($this$commonSubstring$iv, beginIndex);
            int endSegment$iv = -SegmentedByteString.segment($this$commonSubstring$iv, endIndex$iv - 1);
            Object[] objectArray = (Object[])$this$commonSubstring$iv.getSegments$okio();
            int n = endSegment$iv + 1;
            byte[][] newSegments$iv = (byte[][])ArraysKt.copyOfRange(objectArray, beginSegment$iv, n);
            int[] newDirectory$iv = new int[((Object[])newSegments$iv).length * 2];
            int index$iv = 0;
            int s$iv = beginSegment$iv;
            if (s$iv <= endSegment$iv) {
                while (true) {
                    newDirectory$iv[index$iv] = Math.min($this$commonSubstring$iv.getDirectory$okio()[s$iv] - beginIndex, subLen$iv);
                    newDirectory$iv[index$iv++ + ((Object[])newSegments$iv).length] = $this$commonSubstring$iv.getDirectory$okio()[s$iv + ((Object[])$this$commonSubstring$iv.getSegments$okio()).length];
                    if (s$iv == endSegment$iv) break;
                    ++s$iv;
                }
            }
            int segmentOffset$iv = beginSegment$iv == 0 ? 0 : $this$commonSubstring$iv.getDirectory$okio()[beginSegment$iv - 1];
            int n2 = ((Object[])newSegments$iv).length;
            newDirectory$iv[n2] = newDirectory$iv[n2] + (beginIndex - segmentOffset$iv);
            byteString = new SegmentedByteString(newSegments$iv, newDirectory$iv);
        }
        return byteString;
    }

    @Override
    public byte internalGet$okio(int pos) {
        SegmentedByteString $this$commonInternalGet$iv = this;
        boolean $i$f$commonInternalGet = false;
        okio.-SegmentedByteString.checkOffsetAndCount($this$commonInternalGet$iv.getDirectory$okio()[((Object[])$this$commonInternalGet$iv.getSegments$okio()).length - 1], pos, 1L);
        int segment$iv = -SegmentedByteString.segment($this$commonInternalGet$iv, pos);
        int segmentOffset$iv = segment$iv == 0 ? 0 : $this$commonInternalGet$iv.getDirectory$okio()[segment$iv - 1];
        int segmentPos$iv = $this$commonInternalGet$iv.getDirectory$okio()[segment$iv + ((Object[])$this$commonInternalGet$iv.getSegments$okio()).length];
        return $this$commonInternalGet$iv.getSegments$okio()[segment$iv][pos - segmentOffset$iv + segmentPos$iv];
    }

    @Override
    public int getSize$okio() {
        SegmentedByteString $this$commonGetSize$iv = this;
        boolean $i$f$commonGetSize = false;
        return $this$commonGetSize$iv.getDirectory$okio()[((Object[])$this$commonGetSize$iv.getSegments$okio()).length - 1];
    }

    /*
     * WARNING - void declaration
     */
    @Override
    @NotNull
    public byte[] toByteArray() {
        void var3_3;
        SegmentedByteString $this$commonToByteArray$iv = this;
        boolean $i$f$commonToByteArray = false;
        byte[] result$iv = new byte[$this$commonToByteArray$iv.size()];
        int resultPos$iv = 0;
        SegmentedByteString $this$forEachSegment$iv$iv = $this$commonToByteArray$iv;
        boolean $i$f$forEachSegment = false;
        int segmentCount$iv$iv = ((Object[])$this$forEachSegment$iv$iv.getSegments$okio()).length;
        int pos$iv$iv = 0;
        for (int s$iv$iv = 0; s$iv$iv < segmentCount$iv$iv; ++s$iv$iv) {
            void byteCount$iv;
            void offset$iv;
            int segmentPos$iv$iv = $this$forEachSegment$iv$iv.getDirectory$okio()[segmentCount$iv$iv + s$iv$iv];
            int nextSegmentOffset$iv$iv = $this$forEachSegment$iv$iv.getDirectory$okio()[s$iv$iv];
            int n = nextSegmentOffset$iv$iv - pos$iv$iv;
            int n2 = segmentPos$iv$iv;
            byte[] data$iv = $this$forEachSegment$iv$iv.getSegments$okio()[s$iv$iv];
            boolean bl = false;
            ArraysKt.copyInto(data$iv, result$iv, resultPos$iv, (int)offset$iv, (int)(offset$iv + byteCount$iv));
            resultPos$iv += byteCount$iv;
            pos$iv$iv = nextSegmentOffset$iv$iv;
        }
        return var3_3;
    }

    @Override
    @NotNull
    public ByteBuffer asByteBuffer() {
        ByteBuffer byteBuffer = ByteBuffer.wrap(this.toByteArray()).asReadOnlyBuffer();
        Intrinsics.checkNotNullExpressionValue(byteBuffer, "asReadOnlyBuffer(...)");
        return byteBuffer;
    }

    /*
     * WARNING - void declaration
     */
    @Override
    public void write(@NotNull OutputStream out) throws IOException {
        Intrinsics.checkNotNullParameter(out, "out");
        SegmentedByteString $this$forEachSegment$iv = this;
        boolean $i$f$forEachSegment = false;
        int segmentCount$iv = ((Object[])$this$forEachSegment$iv.getSegments$okio()).length;
        int pos$iv = 0;
        for (int s$iv = 0; s$iv < segmentCount$iv; ++s$iv) {
            void byteCount;
            void offset;
            int segmentPos$iv = $this$forEachSegment$iv.getDirectory$okio()[segmentCount$iv + s$iv];
            int nextSegmentOffset$iv = $this$forEachSegment$iv.getDirectory$okio()[s$iv];
            int n = nextSegmentOffset$iv - pos$iv;
            int n2 = segmentPos$iv;
            byte[] data = $this$forEachSegment$iv.getSegments$okio()[s$iv];
            boolean bl = false;
            out.write(data, (int)offset, (int)byteCount);
            pos$iv = nextSegmentOffset$iv;
        }
    }

    /*
     * WARNING - void declaration
     */
    @Override
    public void write$okio(@NotNull Buffer buffer, int offset, int byteCount) {
        void $this$forEachSegment$iv$iv;
        Intrinsics.checkNotNullParameter(buffer, "buffer");
        SegmentedByteString $this$commonWrite$iv = this;
        boolean $i$f$commonWrite = false;
        SegmentedByteString segmentedByteString = $this$commonWrite$iv;
        int endIndex$iv$iv = offset + byteCount;
        boolean $i$f$forEachSegment = false;
        int s$iv$iv = -SegmentedByteString.segment((SegmentedByteString)$this$forEachSegment$iv$iv, offset);
        int pos$iv$iv = offset;
        while (pos$iv$iv < endIndex$iv$iv) {
            void byteCount$iv;
            void offset$iv;
            int segmentOffset$iv$iv = s$iv$iv == 0 ? 0 : $this$forEachSegment$iv$iv.getDirectory$okio()[s$iv$iv - 1];
            int segmentSize$iv$iv = $this$forEachSegment$iv$iv.getDirectory$okio()[s$iv$iv] - segmentOffset$iv$iv;
            int segmentPos$iv$iv = $this$forEachSegment$iv$iv.getDirectory$okio()[((Object[])$this$forEachSegment$iv$iv.getSegments$okio()).length + s$iv$iv];
            int byteCount$iv$iv = Math.min(endIndex$iv$iv, segmentOffset$iv$iv + segmentSize$iv$iv) - pos$iv$iv;
            int offset$iv$iv = segmentPos$iv$iv + (pos$iv$iv - segmentOffset$iv$iv);
            int n = byteCount$iv$iv;
            int n2 = offset$iv$iv;
            byte[] data$iv = $this$forEachSegment$iv$iv.getSegments$okio()[s$iv$iv];
            boolean bl = false;
            Segment segment$iv = new Segment(data$iv, (int)offset$iv, (int)(offset$iv + byteCount$iv), true, false);
            if (buffer.head == null) {
                buffer.head = segment$iv.next = (segment$iv.prev = segment$iv);
            } else {
                Segment segment = buffer.head;
                Intrinsics.checkNotNull(segment);
                Segment segment2 = segment.prev;
                Intrinsics.checkNotNull(segment2);
                segment2.push(segment$iv);
            }
            pos$iv$iv += byteCount$iv$iv;
            ++s$iv$iv;
        }
        buffer.setSize$okio(buffer.size() + (long)byteCount);
    }

    /*
     * WARNING - void declaration
     */
    @Override
    public boolean rangeEquals(int offset, @NotNull ByteString other, int otherOffset, int byteCount) {
        boolean bl;
        block4: {
            Intrinsics.checkNotNullParameter(other, "other");
            SegmentedByteString $this$commonRangeEquals$iv = this;
            boolean $i$f$commonRangeEquals = false;
            if (offset < 0 || offset > $this$commonRangeEquals$iv.size() - byteCount) {
                bl = false;
            } else {
                void $this$forEachSegment$iv$iv;
                int otherOffset$iv = 0;
                otherOffset$iv = otherOffset;
                SegmentedByteString segmentedByteString = $this$commonRangeEquals$iv;
                int endIndex$iv$iv = offset + byteCount;
                boolean $i$f$forEachSegment = false;
                int s$iv$iv = -SegmentedByteString.segment((SegmentedByteString)$this$forEachSegment$iv$iv, offset);
                int pos$iv$iv = offset;
                while (pos$iv$iv < endIndex$iv$iv) {
                    void byteCount$iv;
                    void offset$iv;
                    int segmentOffset$iv$iv = s$iv$iv == 0 ? 0 : $this$forEachSegment$iv$iv.getDirectory$okio()[s$iv$iv - 1];
                    int segmentSize$iv$iv = $this$forEachSegment$iv$iv.getDirectory$okio()[s$iv$iv] - segmentOffset$iv$iv;
                    int segmentPos$iv$iv = $this$forEachSegment$iv$iv.getDirectory$okio()[((Object[])$this$forEachSegment$iv$iv.getSegments$okio()).length + s$iv$iv];
                    int byteCount$iv$iv = Math.min(endIndex$iv$iv, segmentOffset$iv$iv + segmentSize$iv$iv) - pos$iv$iv;
                    int offset$iv$iv = segmentPos$iv$iv + (pos$iv$iv - segmentOffset$iv$iv);
                    int n = byteCount$iv$iv;
                    int n2 = offset$iv$iv;
                    byte[] data$iv = $this$forEachSegment$iv$iv.getSegments$okio()[s$iv$iv];
                    boolean bl2 = false;
                    if (!other.rangeEquals(otherOffset$iv, data$iv, (int)offset$iv, (int)byteCount$iv)) {
                        bl = false;
                        break block4;
                    }
                    otherOffset$iv += byteCount$iv;
                    pos$iv$iv += byteCount$iv$iv;
                    ++s$iv$iv;
                }
                bl = true;
            }
        }
        return bl;
    }

    /*
     * WARNING - void declaration
     */
    @Override
    public boolean rangeEquals(int offset, @NotNull byte[] other, int otherOffset, int byteCount) {
        boolean bl;
        block4: {
            Intrinsics.checkNotNullParameter(other, "other");
            SegmentedByteString $this$commonRangeEquals$iv = this;
            boolean $i$f$commonRangeEquals = false;
            if (offset < 0 || offset > $this$commonRangeEquals$iv.size() - byteCount || otherOffset < 0 || otherOffset > other.length - byteCount) {
                bl = false;
            } else {
                void $this$forEachSegment$iv$iv;
                int otherOffset$iv = 0;
                otherOffset$iv = otherOffset;
                SegmentedByteString segmentedByteString = $this$commonRangeEquals$iv;
                int endIndex$iv$iv = offset + byteCount;
                boolean $i$f$forEachSegment = false;
                int s$iv$iv = -SegmentedByteString.segment((SegmentedByteString)$this$forEachSegment$iv$iv, offset);
                int pos$iv$iv = offset;
                while (pos$iv$iv < endIndex$iv$iv) {
                    void byteCount$iv;
                    void offset$iv;
                    int segmentOffset$iv$iv = s$iv$iv == 0 ? 0 : $this$forEachSegment$iv$iv.getDirectory$okio()[s$iv$iv - 1];
                    int segmentSize$iv$iv = $this$forEachSegment$iv$iv.getDirectory$okio()[s$iv$iv] - segmentOffset$iv$iv;
                    int segmentPos$iv$iv = $this$forEachSegment$iv$iv.getDirectory$okio()[((Object[])$this$forEachSegment$iv$iv.getSegments$okio()).length + s$iv$iv];
                    int byteCount$iv$iv = Math.min(endIndex$iv$iv, segmentOffset$iv$iv + segmentSize$iv$iv) - pos$iv$iv;
                    int offset$iv$iv = segmentPos$iv$iv + (pos$iv$iv - segmentOffset$iv$iv);
                    int n = byteCount$iv$iv;
                    int n2 = offset$iv$iv;
                    byte[] data$iv = $this$forEachSegment$iv$iv.getSegments$okio()[s$iv$iv];
                    boolean bl2 = false;
                    if (!okio.-SegmentedByteString.arrayRangeEquals(data$iv, (int)offset$iv, other, otherOffset$iv, (int)byteCount$iv)) {
                        bl = false;
                        break block4;
                    }
                    otherOffset$iv += byteCount$iv;
                    pos$iv$iv += byteCount$iv$iv;
                    ++s$iv$iv;
                }
                bl = true;
            }
        }
        return bl;
    }

    /*
     * WARNING - void declaration
     */
    @Override
    public void copyInto(int offset, @NotNull byte[] target, int targetOffset, int byteCount) {
        void $this$forEachSegment$iv$iv;
        Intrinsics.checkNotNullParameter(target, "target");
        SegmentedByteString $this$commonCopyInto$iv = this;
        boolean $i$f$commonCopyInto = false;
        okio.-SegmentedByteString.checkOffsetAndCount($this$commonCopyInto$iv.size(), offset, byteCount);
        okio.-SegmentedByteString.checkOffsetAndCount(target.length, targetOffset, byteCount);
        int targetOffset$iv = 0;
        targetOffset$iv = targetOffset;
        SegmentedByteString segmentedByteString = $this$commonCopyInto$iv;
        int endIndex$iv$iv = offset + byteCount;
        boolean $i$f$forEachSegment = false;
        int s$iv$iv = -SegmentedByteString.segment((SegmentedByteString)$this$forEachSegment$iv$iv, offset);
        int pos$iv$iv = offset;
        while (pos$iv$iv < endIndex$iv$iv) {
            void byteCount$iv;
            void offset$iv;
            int segmentOffset$iv$iv = s$iv$iv == 0 ? 0 : $this$forEachSegment$iv$iv.getDirectory$okio()[s$iv$iv - 1];
            int segmentSize$iv$iv = $this$forEachSegment$iv$iv.getDirectory$okio()[s$iv$iv] - segmentOffset$iv$iv;
            int segmentPos$iv$iv = $this$forEachSegment$iv$iv.getDirectory$okio()[((Object[])$this$forEachSegment$iv$iv.getSegments$okio()).length + s$iv$iv];
            int byteCount$iv$iv = Math.min(endIndex$iv$iv, segmentOffset$iv$iv + segmentSize$iv$iv) - pos$iv$iv;
            int offset$iv$iv = segmentPos$iv$iv + (pos$iv$iv - segmentOffset$iv$iv);
            int n = byteCount$iv$iv;
            int n2 = offset$iv$iv;
            byte[] data$iv = $this$forEachSegment$iv$iv.getSegments$okio()[s$iv$iv];
            boolean bl = false;
            ArraysKt.copyInto(data$iv, target, targetOffset$iv, (int)offset$iv, (int)(offset$iv + byteCount$iv));
            targetOffset$iv += byteCount$iv;
            pos$iv$iv += byteCount$iv$iv;
            ++s$iv$iv;
        }
    }

    @Override
    public int indexOf(@NotNull byte[] other, int fromIndex) {
        Intrinsics.checkNotNullParameter(other, "other");
        return this.toByteString().indexOf(other, fromIndex);
    }

    @Override
    public int lastIndexOf(@NotNull byte[] other, int fromIndex) {
        Intrinsics.checkNotNullParameter(other, "other");
        return this.toByteString().lastIndexOf(other, fromIndex);
    }

    private final ByteString toByteString() {
        return new ByteString(this.toByteArray());
    }

    @Override
    @NotNull
    public byte[] internalArray$okio() {
        return this.toByteArray();
    }

    @Override
    public boolean equals(@Nullable Object other) {
        SegmentedByteString $this$commonEquals$iv = this;
        boolean $i$f$commonEquals = false;
        return other == $this$commonEquals$iv ? true : (other instanceof ByteString ? ((ByteString)other).size() == $this$commonEquals$iv.size() && $this$commonEquals$iv.rangeEquals(0, (ByteString)other, 0, $this$commonEquals$iv.size()) : false);
    }

    /*
     * WARNING - void declaration
     */
    @Override
    public int hashCode() {
        int n;
        SegmentedByteString $this$commonHashCode$iv = this;
        boolean $i$f$commonHashCode = false;
        int result$iv = 0;
        result$iv = $this$commonHashCode$iv.getHashCode$okio();
        if (result$iv != 0) {
            n = result$iv;
        } else {
            void var3_3;
            result$iv = 1;
            SegmentedByteString $this$forEachSegment$iv$iv = $this$commonHashCode$iv;
            boolean $i$f$forEachSegment = false;
            int segmentCount$iv$iv = ((Object[])$this$forEachSegment$iv$iv.getSegments$okio()).length;
            int pos$iv$iv = 0;
            for (int s$iv$iv = 0; s$iv$iv < segmentCount$iv$iv; ++s$iv$iv) {
                void byteCount$iv;
                void offset$iv;
                int segmentPos$iv$iv = $this$forEachSegment$iv$iv.getDirectory$okio()[segmentCount$iv$iv + s$iv$iv];
                int nextSegmentOffset$iv$iv = $this$forEachSegment$iv$iv.getDirectory$okio()[s$iv$iv];
                int n2 = nextSegmentOffset$iv$iv - pos$iv$iv;
                int n3 = segmentPos$iv$iv;
                byte[] data$iv = $this$forEachSegment$iv$iv.getSegments$okio()[s$iv$iv];
                boolean bl = false;
                void limit$iv = offset$iv + byteCount$iv;
                for (void i$iv = offset$iv; i$iv < limit$iv; ++i$iv) {
                    result$iv = 31 * result$iv + data$iv[i$iv];
                }
                pos$iv$iv = nextSegmentOffset$iv$iv;
            }
            $this$commonHashCode$iv.setHashCode$okio(result$iv);
            n = var3_3;
        }
        return n;
    }

    @Override
    @NotNull
    public String toString() {
        return this.toByteString().toString();
    }

    private final Object writeReplace() {
        ByteString byteString = this.toByteString();
        Intrinsics.checkNotNull(byteString, "null cannot be cast to non-null type java.lang.Object");
        return byteString;
    }
}

