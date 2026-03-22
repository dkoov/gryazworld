/*
 * Decompiled with CFR 0.152.
 */
package okio.internal;

import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.ArraysKt;
import kotlin.jvm.JvmName;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import okio.Buffer;
import okio.ByteString;
import okio.Segment;
import okio.SegmentedByteString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 9, 0}, k=2, xi=48, d1={"\u0000T\n\u0000\n\u0002\u0010\b\n\u0002\u0010\u0015\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0012\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0005\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\u001a$\u0010\u0000\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u00012\u0006\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u0001H\u0000\u001a-\u0010\u0006\u001a\u00020\u0007*\u00020\b2\u0006\u0010\t\u001a\u00020\u00012\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u00012\u0006\u0010\r\u001a\u00020\u0001H\u0080\b\u001a\u0017\u0010\u000e\u001a\u00020\u000f*\u00020\b2\b\u0010\u0010\u001a\u0004\u0018\u00010\u0011H\u0080\b\u001a\r\u0010\u0012\u001a\u00020\u0001*\u00020\bH\u0080\b\u001a\r\u0010\u0013\u001a\u00020\u0001*\u00020\bH\u0080\b\u001a\u0015\u0010\u0014\u001a\u00020\u0015*\u00020\b2\u0006\u0010\u0016\u001a\u00020\u0001H\u0080\b\u001a-\u0010\u0017\u001a\u00020\u000f*\u00020\b2\u0006\u0010\t\u001a\u00020\u00012\u0006\u0010\u0010\u001a\u00020\u000b2\u0006\u0010\u0018\u001a\u00020\u00012\u0006\u0010\r\u001a\u00020\u0001H\u0080\b\u001a-\u0010\u0017\u001a\u00020\u000f*\u00020\b2\u0006\u0010\t\u001a\u00020\u00012\u0006\u0010\u0010\u001a\u00020\u00192\u0006\u0010\u0018\u001a\u00020\u00012\u0006\u0010\r\u001a\u00020\u0001H\u0080\b\u001a\u001d\u0010\u001a\u001a\u00020\u0019*\u00020\b2\u0006\u0010\u001b\u001a\u00020\u00012\u0006\u0010\u001c\u001a\u00020\u0001H\u0080\b\u001a\r\u0010\u001d\u001a\u00020\u000b*\u00020\bH\u0080\b\u001a%\u0010\u001e\u001a\u00020\u0007*\u00020\b2\u0006\u0010\u001f\u001a\u00020 2\u0006\u0010\t\u001a\u00020\u00012\u0006\u0010\r\u001a\u00020\u0001H\u0080\b\u001a]\u0010!\u001a\u00020\u0007*\u00020\b2K\u0010\"\u001aG\u0012\u0013\u0012\u00110\u000b\u00a2\u0006\f\b$\u0012\b\b%\u0012\u0004\b\b(&\u0012\u0013\u0012\u00110\u0001\u00a2\u0006\f\b$\u0012\b\b%\u0012\u0004\b\b(\t\u0012\u0013\u0012\u00110\u0001\u00a2\u0006\f\b$\u0012\b\b%\u0012\u0004\b\b(\r\u0012\u0004\u0012\u00020\u00070#H\u0080\b\u00f8\u0001\u0000\u001aj\u0010!\u001a\u00020\u0007*\u00020\b2\u0006\u0010\u001b\u001a\u00020\u00012\u0006\u0010\u001c\u001a\u00020\u00012K\u0010\"\u001aG\u0012\u0013\u0012\u00110\u000b\u00a2\u0006\f\b$\u0012\b\b%\u0012\u0004\b\b(&\u0012\u0013\u0012\u00110\u0001\u00a2\u0006\f\b$\u0012\b\b%\u0012\u0004\b\b(\t\u0012\u0013\u0012\u00110\u0001\u00a2\u0006\f\b$\u0012\b\b%\u0012\u0004\b\b(\r\u0012\u0004\u0012\u00020\u00070#H\u0082\b\u001a\u0014\u0010'\u001a\u00020\u0001*\u00020\b2\u0006\u0010\u0016\u001a\u00020\u0001H\u0000\u0082\u0002\u0007\n\u0005\b\u009920\u0001\u00a8\u0006("}, d2={"binarySearch", "", "", "value", "fromIndex", "toIndex", "commonCopyInto", "", "Lokio/SegmentedByteString;", "offset", "target", "", "targetOffset", "byteCount", "commonEquals", "", "other", "", "commonGetSize", "commonHashCode", "commonInternalGet", "", "pos", "commonRangeEquals", "otherOffset", "Lokio/ByteString;", "commonSubstring", "beginIndex", "endIndex", "commonToByteArray", "commonWrite", "buffer", "Lokio/Buffer;", "forEachSegment", "action", "Lkotlin/Function3;", "Lkotlin/ParameterName;", "name", "data", "segment", "okio"})
@JvmName(name="-SegmentedByteString")
@SourceDebugExtension(value={"SMAP\nSegmentedByteString.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SegmentedByteString.kt\nokio/internal/-SegmentedByteString\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,250:1\n63#1,12:252\n85#1,14:264\n85#1,14:278\n85#1,14:292\n85#1,14:306\n63#1,12:320\n1#2:251\n*S KotlinDebug\n*F\n+ 1 SegmentedByteString.kt\nokio/internal/-SegmentedByteString\n*L\n147#1:252,12\n160#1:264,14\n182#1:278,14\n202#1:292,14\n219#1:306,14\n239#1:320,12\n*E\n"})
public final class -SegmentedByteString {
    public static final int binarySearch(@NotNull int[] $this$binarySearch, int value, int fromIndex, int toIndex) {
        Intrinsics.checkNotNullParameter($this$binarySearch, "<this>");
        int left = fromIndex;
        int right = toIndex - 1;
        while (left <= right) {
            int mid = left + right >>> 1;
            int midVal = $this$binarySearch[mid];
            if (midVal < value) {
                left = mid + 1;
                continue;
            }
            if (midVal > value) {
                right = mid - 1;
                continue;
            }
            return mid;
        }
        return -left - 1;
    }

    public static final int segment(@NotNull SegmentedByteString $this$segment, int pos) {
        Intrinsics.checkNotNullParameter($this$segment, "<this>");
        int i = -SegmentedByteString.binarySearch($this$segment.getDirectory$okio(), pos + 1, 0, ((Object[])$this$segment.getSegments$okio()).length);
        return i >= 0 ? i : ~i;
    }

    public static final void forEachSegment(@NotNull SegmentedByteString $this$forEachSegment, @NotNull Function3<? super byte[], ? super Integer, ? super Integer, Unit> action) {
        Intrinsics.checkNotNullParameter($this$forEachSegment, "<this>");
        Intrinsics.checkNotNullParameter(action, "action");
        boolean $i$f$forEachSegment = false;
        int segmentCount = ((Object[])$this$forEachSegment.getSegments$okio()).length;
        int pos = 0;
        for (int s = 0; s < segmentCount; ++s) {
            int segmentPos = $this$forEachSegment.getDirectory$okio()[segmentCount + s];
            int nextSegmentOffset = $this$forEachSegment.getDirectory$okio()[s];
            action.invoke((byte[])$this$forEachSegment.getSegments$okio()[s], (Integer)segmentPos, (Integer)(nextSegmentOffset - pos));
            pos = nextSegmentOffset;
        }
    }

    private static final void forEachSegment(SegmentedByteString $this$forEachSegment, int beginIndex, int endIndex, Function3<? super byte[], ? super Integer, ? super Integer, Unit> action) {
        boolean $i$f$forEachSegment = false;
        int s = -SegmentedByteString.segment($this$forEachSegment, beginIndex);
        int pos = beginIndex;
        while (pos < endIndex) {
            int segmentOffset = s == 0 ? 0 : $this$forEachSegment.getDirectory$okio()[s - 1];
            int segmentSize = $this$forEachSegment.getDirectory$okio()[s] - segmentOffset;
            int segmentPos = $this$forEachSegment.getDirectory$okio()[((Object[])$this$forEachSegment.getSegments$okio()).length + s];
            int byteCount = Math.min(endIndex, segmentOffset + segmentSize) - pos;
            int offset = segmentPos + (pos - segmentOffset);
            action.invoke((byte[])$this$forEachSegment.getSegments$okio()[s], (Integer)offset, (Integer)byteCount);
            pos += byteCount;
            ++s;
        }
    }

    @NotNull
    public static final ByteString commonSubstring(@NotNull SegmentedByteString $this$commonSubstring, int beginIndex, int endIndex) {
        Intrinsics.checkNotNullParameter($this$commonSubstring, "<this>");
        boolean $i$f$commonSubstring = false;
        int endIndex2 = okio.-SegmentedByteString.resolveDefaultParameter($this$commonSubstring, endIndex);
        if (!(beginIndex >= 0)) {
            boolean $i$a$-require--SegmentedByteString$commonSubstring$42 = false;
            String $i$a$-require--SegmentedByteString$commonSubstring$42 = "beginIndex=" + beginIndex + " < 0";
            throw new IllegalArgumentException($i$a$-require--SegmentedByteString$commonSubstring$42.toString());
        }
        if (!(endIndex2 <= $this$commonSubstring.size())) {
            boolean $i$a$-require--SegmentedByteString$commonSubstring$52 = false;
            String $i$a$-require--SegmentedByteString$commonSubstring$52 = "endIndex=" + endIndex2 + " > length(" + $this$commonSubstring.size() + ')';
            throw new IllegalArgumentException($i$a$-require--SegmentedByteString$commonSubstring$52.toString());
        }
        int subLen = endIndex2 - beginIndex;
        if (!(subLen >= 0)) {
            boolean $i$a$-require--SegmentedByteString$commonSubstring$62 = false;
            String $i$a$-require--SegmentedByteString$commonSubstring$62 = "endIndex=" + endIndex2 + " < beginIndex=" + beginIndex;
            throw new IllegalArgumentException($i$a$-require--SegmentedByteString$commonSubstring$62.toString());
        }
        if (beginIndex == 0 && endIndex2 == $this$commonSubstring.size()) {
            return $this$commonSubstring;
        }
        if (beginIndex == endIndex2) {
            return ByteString.EMPTY;
        }
        int beginSegment = -SegmentedByteString.segment($this$commonSubstring, beginIndex);
        int endSegment = -SegmentedByteString.segment($this$commonSubstring, endIndex2 - 1);
        Object[] objectArray = (Object[])$this$commonSubstring.getSegments$okio();
        int n = endSegment + 1;
        byte[][] newSegments = (byte[][])ArraysKt.copyOfRange(objectArray, beginSegment, n);
        int[] newDirectory = new int[((Object[])newSegments).length * 2];
        int index = 0;
        int s = beginSegment;
        if (s <= endSegment) {
            while (true) {
                newDirectory[index] = Math.min($this$commonSubstring.getDirectory$okio()[s] - beginIndex, subLen);
                newDirectory[index++ + ((Object[])newSegments).length] = $this$commonSubstring.getDirectory$okio()[s + ((Object[])$this$commonSubstring.getSegments$okio()).length];
                if (s == endSegment) break;
                ++s;
            }
        }
        int segmentOffset = beginSegment == 0 ? 0 : $this$commonSubstring.getDirectory$okio()[beginSegment - 1];
        int n2 = ((Object[])newSegments).length;
        newDirectory[n2] = newDirectory[n2] + (beginIndex - segmentOffset);
        return new SegmentedByteString(newSegments, newDirectory);
    }

    public static final byte commonInternalGet(@NotNull SegmentedByteString $this$commonInternalGet, int pos) {
        Intrinsics.checkNotNullParameter($this$commonInternalGet, "<this>");
        boolean $i$f$commonInternalGet = false;
        okio.-SegmentedByteString.checkOffsetAndCount($this$commonInternalGet.getDirectory$okio()[((Object[])$this$commonInternalGet.getSegments$okio()).length - 1], pos, 1L);
        int segment = -SegmentedByteString.segment($this$commonInternalGet, pos);
        int segmentOffset = segment == 0 ? 0 : $this$commonInternalGet.getDirectory$okio()[segment - 1];
        int segmentPos = $this$commonInternalGet.getDirectory$okio()[segment + ((Object[])$this$commonInternalGet.getSegments$okio()).length];
        return $this$commonInternalGet.getSegments$okio()[segment][pos - segmentOffset + segmentPos];
    }

    public static final int commonGetSize(@NotNull SegmentedByteString $this$commonGetSize) {
        Intrinsics.checkNotNullParameter($this$commonGetSize, "<this>");
        boolean $i$f$commonGetSize = false;
        return $this$commonGetSize.getDirectory$okio()[((Object[])$this$commonGetSize.getSegments$okio()).length - 1];
    }

    /*
     * WARNING - void declaration
     */
    @NotNull
    public static final byte[] commonToByteArray(@NotNull SegmentedByteString $this$commonToByteArray) {
        Intrinsics.checkNotNullParameter($this$commonToByteArray, "<this>");
        boolean $i$f$commonToByteArray = false;
        byte[] result = new byte[$this$commonToByteArray.size()];
        int resultPos = 0;
        SegmentedByteString $this$forEachSegment$iv = $this$commonToByteArray;
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
            ArraysKt.copyInto(data, result, resultPos, (int)offset, (int)(offset + byteCount));
            resultPos += byteCount;
            pos$iv = nextSegmentOffset$iv;
        }
        return result;
    }

    /*
     * WARNING - void declaration
     */
    public static final void commonWrite(@NotNull SegmentedByteString $this$commonWrite, @NotNull Buffer buffer, int offset, int byteCount) {
        void $this$forEachSegment$iv;
        Intrinsics.checkNotNullParameter($this$commonWrite, "<this>");
        Intrinsics.checkNotNullParameter(buffer, "buffer");
        boolean $i$f$commonWrite = false;
        SegmentedByteString segmentedByteString = $this$commonWrite;
        int endIndex$iv = offset + byteCount;
        boolean $i$f$forEachSegment = false;
        int s$iv = -SegmentedByteString.segment((SegmentedByteString)$this$forEachSegment$iv, offset);
        int pos$iv = offset;
        while (pos$iv < endIndex$iv) {
            void byteCount2;
            void offset2;
            int segmentOffset$iv = s$iv == 0 ? 0 : $this$forEachSegment$iv.getDirectory$okio()[s$iv - 1];
            int segmentSize$iv = $this$forEachSegment$iv.getDirectory$okio()[s$iv] - segmentOffset$iv;
            int segmentPos$iv = $this$forEachSegment$iv.getDirectory$okio()[((Object[])$this$forEachSegment$iv.getSegments$okio()).length + s$iv];
            int byteCount$iv = Math.min(endIndex$iv, segmentOffset$iv + segmentSize$iv) - pos$iv;
            int offset$iv = segmentPos$iv + (pos$iv - segmentOffset$iv);
            int n = byteCount$iv;
            int n2 = offset$iv;
            byte[] data = $this$forEachSegment$iv.getSegments$okio()[s$iv];
            boolean bl = false;
            Segment segment = new Segment(data, (int)offset2, (int)(offset2 + byteCount2), true, false);
            if (buffer.head == null) {
                buffer.head = segment.next = (segment.prev = segment);
            } else {
                Segment segment2 = buffer.head;
                Intrinsics.checkNotNull(segment2);
                Segment segment3 = segment2.prev;
                Intrinsics.checkNotNull(segment3);
                segment3.push(segment);
            }
            pos$iv += byteCount$iv;
            ++s$iv;
        }
        buffer.setSize$okio(buffer.size() + (long)byteCount);
    }

    /*
     * WARNING - void declaration
     */
    public static final boolean commonRangeEquals(@NotNull SegmentedByteString $this$commonRangeEquals, int offset, @NotNull ByteString other, int otherOffset, int byteCount) {
        void $this$forEachSegment$iv;
        Intrinsics.checkNotNullParameter($this$commonRangeEquals, "<this>");
        Intrinsics.checkNotNullParameter(other, "other");
        boolean $i$f$commonRangeEquals = false;
        if (offset < 0 || offset > $this$commonRangeEquals.size() - byteCount) {
            return false;
        }
        int otherOffset2 = 0;
        otherOffset2 = otherOffset;
        SegmentedByteString segmentedByteString = $this$commonRangeEquals;
        int endIndex$iv = offset + byteCount;
        boolean $i$f$forEachSegment = false;
        int s$iv = -SegmentedByteString.segment((SegmentedByteString)$this$forEachSegment$iv, offset);
        int pos$iv = offset;
        while (pos$iv < endIndex$iv) {
            void byteCount2;
            void offset2;
            int segmentOffset$iv = s$iv == 0 ? 0 : $this$forEachSegment$iv.getDirectory$okio()[s$iv - 1];
            int segmentSize$iv = $this$forEachSegment$iv.getDirectory$okio()[s$iv] - segmentOffset$iv;
            int segmentPos$iv = $this$forEachSegment$iv.getDirectory$okio()[((Object[])$this$forEachSegment$iv.getSegments$okio()).length + s$iv];
            int byteCount$iv = Math.min(endIndex$iv, segmentOffset$iv + segmentSize$iv) - pos$iv;
            int offset$iv = segmentPos$iv + (pos$iv - segmentOffset$iv);
            int n = byteCount$iv;
            int n2 = offset$iv;
            byte[] data = $this$forEachSegment$iv.getSegments$okio()[s$iv];
            boolean bl = false;
            if (!other.rangeEquals(otherOffset2, data, (int)offset2, (int)byteCount2)) {
                return false;
            }
            otherOffset2 += byteCount2;
            pos$iv += byteCount$iv;
            ++s$iv;
        }
        return true;
    }

    /*
     * WARNING - void declaration
     */
    public static final boolean commonRangeEquals(@NotNull SegmentedByteString $this$commonRangeEquals, int offset, @NotNull byte[] other, int otherOffset, int byteCount) {
        void $this$forEachSegment$iv;
        Intrinsics.checkNotNullParameter($this$commonRangeEquals, "<this>");
        Intrinsics.checkNotNullParameter(other, "other");
        boolean $i$f$commonRangeEquals = false;
        if (offset < 0 || offset > $this$commonRangeEquals.size() - byteCount || otherOffset < 0 || otherOffset > other.length - byteCount) {
            return false;
        }
        int otherOffset2 = 0;
        otherOffset2 = otherOffset;
        SegmentedByteString segmentedByteString = $this$commonRangeEquals;
        int endIndex$iv = offset + byteCount;
        boolean $i$f$forEachSegment = false;
        int s$iv = -SegmentedByteString.segment((SegmentedByteString)$this$forEachSegment$iv, offset);
        int pos$iv = offset;
        while (pos$iv < endIndex$iv) {
            void byteCount2;
            void offset2;
            int segmentOffset$iv = s$iv == 0 ? 0 : $this$forEachSegment$iv.getDirectory$okio()[s$iv - 1];
            int segmentSize$iv = $this$forEachSegment$iv.getDirectory$okio()[s$iv] - segmentOffset$iv;
            int segmentPos$iv = $this$forEachSegment$iv.getDirectory$okio()[((Object[])$this$forEachSegment$iv.getSegments$okio()).length + s$iv];
            int byteCount$iv = Math.min(endIndex$iv, segmentOffset$iv + segmentSize$iv) - pos$iv;
            int offset$iv = segmentPos$iv + (pos$iv - segmentOffset$iv);
            int n = byteCount$iv;
            int n2 = offset$iv;
            byte[] data = $this$forEachSegment$iv.getSegments$okio()[s$iv];
            boolean bl = false;
            if (!okio.-SegmentedByteString.arrayRangeEquals(data, (int)offset2, other, otherOffset2, (int)byteCount2)) {
                return false;
            }
            otherOffset2 += byteCount2;
            pos$iv += byteCount$iv;
            ++s$iv;
        }
        return true;
    }

    /*
     * WARNING - void declaration
     */
    public static final void commonCopyInto(@NotNull SegmentedByteString $this$commonCopyInto, int offset, @NotNull byte[] target, int targetOffset, int byteCount) {
        void $this$forEachSegment$iv;
        Intrinsics.checkNotNullParameter($this$commonCopyInto, "<this>");
        Intrinsics.checkNotNullParameter(target, "target");
        boolean $i$f$commonCopyInto = false;
        okio.-SegmentedByteString.checkOffsetAndCount($this$commonCopyInto.size(), offset, byteCount);
        okio.-SegmentedByteString.checkOffsetAndCount(target.length, targetOffset, byteCount);
        int targetOffset2 = 0;
        targetOffset2 = targetOffset;
        SegmentedByteString segmentedByteString = $this$commonCopyInto;
        int endIndex$iv = offset + byteCount;
        boolean $i$f$forEachSegment = false;
        int s$iv = -SegmentedByteString.segment((SegmentedByteString)$this$forEachSegment$iv, offset);
        int pos$iv = offset;
        while (pos$iv < endIndex$iv) {
            void byteCount2;
            void offset2;
            int segmentOffset$iv = s$iv == 0 ? 0 : $this$forEachSegment$iv.getDirectory$okio()[s$iv - 1];
            int segmentSize$iv = $this$forEachSegment$iv.getDirectory$okio()[s$iv] - segmentOffset$iv;
            int segmentPos$iv = $this$forEachSegment$iv.getDirectory$okio()[((Object[])$this$forEachSegment$iv.getSegments$okio()).length + s$iv];
            int byteCount$iv = Math.min(endIndex$iv, segmentOffset$iv + segmentSize$iv) - pos$iv;
            int offset$iv = segmentPos$iv + (pos$iv - segmentOffset$iv);
            int n = byteCount$iv;
            int n2 = offset$iv;
            byte[] data = $this$forEachSegment$iv.getSegments$okio()[s$iv];
            boolean bl = false;
            ArraysKt.copyInto(data, target, targetOffset2, (int)offset2, (int)(offset2 + byteCount2));
            targetOffset2 += byteCount2;
            pos$iv += byteCount$iv;
            ++s$iv;
        }
    }

    public static final boolean commonEquals(@NotNull SegmentedByteString $this$commonEquals, @Nullable Object other) {
        Intrinsics.checkNotNullParameter($this$commonEquals, "<this>");
        boolean $i$f$commonEquals = false;
        return other == $this$commonEquals ? true : (other instanceof ByteString ? ((ByteString)other).size() == $this$commonEquals.size() && $this$commonEquals.rangeEquals(0, (ByteString)other, 0, $this$commonEquals.size()) : false);
    }

    /*
     * WARNING - void declaration
     */
    public static final int commonHashCode(@NotNull SegmentedByteString $this$commonHashCode) {
        Intrinsics.checkNotNullParameter($this$commonHashCode, "<this>");
        boolean $i$f$commonHashCode = false;
        int result = 0;
        result = $this$commonHashCode.getHashCode$okio();
        if (result != 0) {
            return result;
        }
        result = 1;
        SegmentedByteString $this$forEachSegment$iv = $this$commonHashCode;
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
            void limit = offset + byteCount;
            for (void i = offset; i < limit; ++i) {
                result = 31 * result + data[i];
            }
            pos$iv = nextSegmentOffset$iv;
        }
        $this$commonHashCode.setHashCode$okio(result);
        return result;
    }
}

