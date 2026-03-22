/*
 * Decompiled with CFR 0.152.
 */
package kotlin.time;

import kotlin.Metadata;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.time.Duration;
import kotlin.time.DurationKt;
import kotlin.time.DurationUnit;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
@Metadata(mv={1, 8, 0}, k=2, xi=48, d1={"\u0000\u0018\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0012\n\u0002\u0010\u000b\n\u0000\u001a*\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00012\u0006\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0001H\u0002\u00f8\u0001\u0000\u00a2\u0006\u0004\b\u0006\u0010\u0007\u001a\"\u0010\b\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00012\u0006\u0010\u0003\u001a\u00020\u0004H\u0000\u00f8\u0001\u0000\u00a2\u0006\u0004\b\t\u0010\n\u001a\"\u0010\u000b\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00012\u0006\u0010\u0003\u001a\u00020\u0004H\u0002\u00f8\u0001\u0000\u00a2\u0006\u0004\b\f\u0010\n\u001a \u0010\r\u001a\u00020\u00042\u0006\u0010\u000e\u001a\u00020\u00012\u0006\u0010\u000f\u001a\u00020\u0001H\u0000\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\n\u001a \u0010\u0010\u001a\u00020\u00042\u0006\u0010\u0011\u001a\u00020\u00012\u0006\u0010\u0012\u001a\u00020\u0001H\u0002\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\n\u001a \u0010\u0013\u001a\u00020\u00042\u0006\u0010\u0014\u001a\u00020\u00012\u0006\u0010\u0015\u001a\u00020\u0001H\u0000\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\n\u001a\r\u0010\u0016\u001a\u00020\u0017*\u00020\u0001H\u0082\b\u0082\u0002\u0004\n\u0002\b\u0019\u00a8\u0006\u0018"}, d2={"checkInfiniteSumDefined", "", "longNs", "duration", "Lkotlin/time/Duration;", "durationNs", "checkInfiniteSumDefined-PjuGub4", "(JJJ)J", "saturatingAdd", "saturatingAdd-pTJri5U", "(JJ)J", "saturatingAddInHalves", "saturatingAddInHalves-pTJri5U", "saturatingDiff", "valueNs", "originNs", "saturatingFiniteDiff", "value1Ns", "value2Ns", "saturatingOriginsDiff", "origin1Ns", "origin2Ns", "isSaturated", "", "kotlin-stdlib"})
@SourceDebugExtension(value={"SMAP\nlongSaturatedMath.kt\nKotlin\n*S Kotlin\n*F\n+ 1 longSaturatedMath.kt\nkotlin/time/LongSaturatedMathKt\n*L\n1#1,75:1\n74#1:76\n74#1:77\n74#1:78\n74#1:79\n74#1:80\n74#1:81\n*S KotlinDebug\n*F\n+ 1 longSaturatedMath.kt\nkotlin/time/LongSaturatedMathKt\n*L\n15#1:76\n18#1:77\n36#1:78\n45#1:79\n52#1:80\n56#1:81\n*E\n"})
public final class LongSaturatedMathKt {
    public static final long saturatingAdd-pTJri5U(long longNs, long duration) {
        long durationNs = Duration.getInWholeNanoseconds-impl(duration);
        long $this$isSaturated$iv = longNs;
        boolean $i$f$isSaturated = false;
        if (($this$isSaturated$iv - 1L | 1L) == Long.MAX_VALUE) {
            return LongSaturatedMathKt.checkInfiniteSumDefined-PjuGub4(longNs, duration, durationNs);
        }
        $this$isSaturated$iv = durationNs;
        $i$f$isSaturated = false;
        if (($this$isSaturated$iv - 1L | 1L) == Long.MAX_VALUE) {
            return LongSaturatedMathKt.saturatingAddInHalves-pTJri5U(longNs, duration);
        }
        long result = longNs + durationNs;
        if (((longNs ^ result) & (durationNs ^ result)) < 0L) {
            return longNs < 0L ? Long.MIN_VALUE : Long.MAX_VALUE;
        }
        return result;
    }

    private static final long checkInfiniteSumDefined-PjuGub4(long longNs, long duration, long durationNs) {
        if (Duration.isInfinite-impl(duration) && (longNs ^ durationNs) < 0L) {
            throw new IllegalArgumentException("Summing infinities of different signs");
        }
        return longNs;
    }

    private static final long saturatingAddInHalves-pTJri5U(long longNs, long duration) {
        long half = Duration.div-UwyO8pc(duration, 2);
        long $this$isSaturated$iv = Duration.getInWholeNanoseconds-impl(half);
        boolean $i$f$isSaturated = false;
        if (($this$isSaturated$iv - 1L | 1L) == Long.MAX_VALUE) {
            return (long)((double)longNs + Duration.toDouble-impl(duration, DurationUnit.NANOSECONDS));
        }
        return LongSaturatedMathKt.saturatingAdd-pTJri5U(LongSaturatedMathKt.saturatingAdd-pTJri5U(longNs, half), Duration.minus-LRDsOJo(duration, half));
    }

    public static final long saturatingDiff(long valueNs, long originNs) {
        long $this$isSaturated$iv = originNs;
        boolean $i$f$isSaturated = false;
        if (($this$isSaturated$iv - 1L | 1L) == Long.MAX_VALUE) {
            return Duration.unaryMinus-UwyO8pc(DurationKt.toDuration(originNs, DurationUnit.DAYS));
        }
        return LongSaturatedMathKt.saturatingFiniteDiff(valueNs, originNs);
    }

    public static final long saturatingOriginsDiff(long origin1Ns, long origin2Ns) {
        long $this$isSaturated$iv = origin2Ns;
        boolean $i$f$isSaturated = false;
        if (($this$isSaturated$iv - 1L | 1L) == Long.MAX_VALUE) {
            if (origin1Ns == origin2Ns) {
                return Duration.Companion.getZERO-UwyO8pc();
            }
            return Duration.unaryMinus-UwyO8pc(DurationKt.toDuration(origin2Ns, DurationUnit.DAYS));
        }
        $this$isSaturated$iv = origin1Ns;
        $i$f$isSaturated = false;
        if (($this$isSaturated$iv - 1L | 1L) == Long.MAX_VALUE) {
            return DurationKt.toDuration(origin1Ns, DurationUnit.DAYS);
        }
        return LongSaturatedMathKt.saturatingFiniteDiff(origin1Ns, origin2Ns);
    }

    private static final long saturatingFiniteDiff(long value1Ns, long value2Ns) {
        long result = value1Ns - value2Ns;
        if (((result ^ value1Ns) & (result ^ value2Ns ^ 0xFFFFFFFFFFFFFFFFL)) < 0L) {
            long resultMs = value1Ns / (long)1000000 - value2Ns / (long)1000000;
            long resultNs = value1Ns % (long)1000000 - value2Ns % (long)1000000;
            return Duration.plus-LRDsOJo(DurationKt.toDuration(resultMs, DurationUnit.MILLISECONDS), DurationKt.toDuration(resultNs, DurationUnit.NANOSECONDS));
        }
        return DurationKt.toDuration(result, DurationUnit.NANOSECONDS);
    }

    private static final boolean isSaturated(long $this$isSaturated) {
        boolean $i$f$isSaturated = false;
        return ($this$isSaturated - 1L | 1L) == Long.MAX_VALUE;
    }
}

