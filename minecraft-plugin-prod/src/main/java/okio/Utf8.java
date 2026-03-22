/*
 * Decompiled with CFR 0.152.
 */
package okio;

import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.JvmName;
import kotlin.jvm.JvmOverloads;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={1, 9, 0}, k=2, xi=48, d1={"\u0000D\n\u0000\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0010\u0005\n\u0000\n\u0002\u0010\f\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0010\u0012\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0002\u001a\u0011\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u0001H\u0080\b\u001a\u0011\u0010\u000e\u001a\u00020\f2\u0006\u0010\u000f\u001a\u00020\u0007H\u0080\b\u001a4\u0010\u0010\u001a\u00020\u0001*\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u00012\u0006\u0010\u0013\u001a\u00020\u00012\u0012\u0010\u0014\u001a\u000e\u0012\u0004\u0012\u00020\u0001\u0012\u0004\u0012\u00020\u00160\u0015H\u0080\b\u00f8\u0001\u0000\u001a4\u0010\u0017\u001a\u00020\u0001*\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u00012\u0006\u0010\u0013\u001a\u00020\u00012\u0012\u0010\u0014\u001a\u000e\u0012\u0004\u0012\u00020\u0001\u0012\u0004\u0012\u00020\u00160\u0015H\u0080\b\u00f8\u0001\u0000\u001a4\u0010\u0018\u001a\u00020\u0001*\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u00012\u0006\u0010\u0013\u001a\u00020\u00012\u0012\u0010\u0014\u001a\u000e\u0012\u0004\u0012\u00020\u0001\u0012\u0004\u0012\u00020\u00160\u0015H\u0080\b\u00f8\u0001\u0000\u001a4\u0010\u0019\u001a\u00020\u0016*\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u00012\u0006\u0010\u0013\u001a\u00020\u00012\u0012\u0010\u0014\u001a\u000e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\u00160\u0015H\u0080\b\u00f8\u0001\u0000\u001a4\u0010\u001a\u001a\u00020\u0016*\u00020\u001b2\u0006\u0010\u0012\u001a\u00020\u00012\u0006\u0010\u0013\u001a\u00020\u00012\u0012\u0010\u0014\u001a\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\u00160\u0015H\u0080\b\u00f8\u0001\u0000\u001a4\u0010\u001c\u001a\u00020\u0016*\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u00012\u0006\u0010\u0013\u001a\u00020\u00012\u0012\u0010\u0014\u001a\u000e\u0012\u0004\u0012\u00020\u0001\u0012\u0004\u0012\u00020\u00160\u0015H\u0080\b\u00f8\u0001\u0000\u001a%\u0010\u001d\u001a\u00020\u001e*\u00020\u001b2\b\b\u0002\u0010\u0012\u001a\u00020\u00012\b\b\u0002\u0010\u0013\u001a\u00020\u0001H\u0007\u00a2\u0006\u0002\b\u001f\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0080T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\u0002\u001a\u00020\u0001X\u0080T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\u0003\u001a\u00020\u0001X\u0080T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\u0004\u001a\u00020\u0001X\u0080T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\u0005\u001a\u00020\u0001X\u0080T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\u0006\u001a\u00020\u0007X\u0080T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\b\u001a\u00020\tX\u0080T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\n\u001a\u00020\u0001X\u0080T\u00a2\u0006\u0002\n\u0000\u0082\u0002\u0007\n\u0005\b\u009920\u0001\u00a8\u0006 "}, d2={"HIGH_SURROGATE_HEADER", "", "LOG_SURROGATE_HEADER", "MASK_2BYTES", "MASK_3BYTES", "MASK_4BYTES", "REPLACEMENT_BYTE", "", "REPLACEMENT_CHARACTER", "", "REPLACEMENT_CODE_POINT", "isIsoControl", "", "codePoint", "isUtf8Continuation", "byte", "process2Utf8Bytes", "", "beginIndex", "endIndex", "yield", "Lkotlin/Function1;", "", "process3Utf8Bytes", "process4Utf8Bytes", "processUtf16Chars", "processUtf8Bytes", "", "processUtf8CodePoints", "utf8Size", "", "size", "okio"})
@JvmName(name="Utf8")
@SourceDebugExtension(value={"SMAP\nUtf8.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Utf8.kt\nokio/Utf8\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 Util.kt\nokio/-SegmentedByteString\n*L\n1#1,559:1\n397#1,9:563\n127#1:572\n406#1,20:574\n440#1,4:595\n127#1:599\n446#1,10:601\n127#1:611\n456#1,5:612\n127#1:617\n461#1,24:618\n500#1,4:643\n127#1:647\n506#1,2:649\n127#1:651\n510#1,10:652\n127#1:662\n520#1,5:663\n127#1:668\n525#1,5:669\n127#1:674\n530#1,28:675\n397#1,9:704\n127#1:713\n406#1,20:715\n440#1,4:736\n127#1:740\n446#1,10:742\n127#1:752\n456#1,5:753\n127#1:758\n461#1,24:759\n500#1,4:784\n127#1:788\n506#1,2:790\n127#1:792\n510#1,10:793\n127#1:803\n520#1,5:804\n127#1:809\n525#1,5:810\n127#1:815\n530#1,28:816\n127#1:844\n127#1:846\n127#1:848\n127#1:850\n127#1:852\n127#1:854\n127#1:856\n127#1:858\n127#1:860\n1#2:560\n74#3:561\n68#3:562\n74#3:573\n68#3:594\n74#3:600\n68#3:642\n74#3:648\n68#3:703\n74#3:714\n68#3:735\n74#3:741\n68#3:783\n74#3:789\n74#3:845\n74#3:847\n74#3:849\n74#3:851\n74#3:853\n74#3:855\n74#3:857\n74#3:859\n74#3:861\n*S KotlinDebug\n*F\n+ 1 Utf8.kt\nokio/Utf8\n*L\n228#1:563,9\n228#1:572\n228#1:574,20\n232#1:595,4\n232#1:599\n232#1:601,10\n232#1:611\n232#1:612,5\n232#1:617\n232#1:618,24\n236#1:643,4\n236#1:647\n236#1:649,2\n236#1:651\n236#1:652,10\n236#1:662\n236#1:663,5\n236#1:668\n236#1:669,5\n236#1:674\n236#1:675,28\n277#1:704,9\n277#1:713\n277#1:715,20\n281#1:736,4\n281#1:740\n281#1:742,10\n281#1:752\n281#1:753,5\n281#1:758\n281#1:759,24\n285#1:784,4\n285#1:788\n285#1:790,2\n285#1:792\n285#1:793,10\n285#1:803\n285#1:804,5\n285#1:809\n285#1:810,5\n285#1:815\n285#1:816,28\n405#1:844\n443#1:846\n455#1:848\n460#1:850\n503#1:852\n507#1:854\n519#1:856\n524#1:858\n529#1:860\n127#1:561\n226#1:562\n228#1:573\n230#1:594\n232#1:600\n234#1:642\n236#1:648\n275#1:703\n277#1:714\n279#1:735\n281#1:741\n283#1:783\n285#1:789\n405#1:845\n443#1:847\n455#1:849\n460#1:851\n503#1:853\n507#1:855\n519#1:857\n524#1:859\n529#1:861\n*E\n"})
public final class Utf8 {
    public static final byte REPLACEMENT_BYTE = 63;
    public static final char REPLACEMENT_CHARACTER = '\ufffd';
    public static final int REPLACEMENT_CODE_POINT = 65533;
    public static final int HIGH_SURROGATE_HEADER = 55232;
    public static final int LOG_SURROGATE_HEADER = 56320;
    public static final int MASK_2BYTES = 3968;
    public static final int MASK_3BYTES = -123008;
    public static final int MASK_4BYTES = 3678080;

    @JvmOverloads
    @JvmName(name="size")
    public static final long size(@NotNull String $this$utf8Size, int beginIndex, int endIndex) {
        Intrinsics.checkNotNullParameter($this$utf8Size, "<this>");
        if (!(beginIndex >= 0)) {
            boolean $i$a$-require-Utf8$utf8Size$42 = false;
            String $i$a$-require-Utf8$utf8Size$42 = "beginIndex < 0: " + beginIndex;
            throw new IllegalArgumentException($i$a$-require-Utf8$utf8Size$42.toString());
        }
        if (!(endIndex >= beginIndex)) {
            boolean $i$a$-require-Utf8$utf8Size$52 = false;
            String $i$a$-require-Utf8$utf8Size$52 = "endIndex < beginIndex: " + endIndex + " < " + beginIndex;
            throw new IllegalArgumentException($i$a$-require-Utf8$utf8Size$52.toString());
        }
        if (!(endIndex <= $this$utf8Size.length())) {
            boolean bl = false;
            String string = "endIndex > string.length: " + endIndex + " > " + $this$utf8Size.length();
            throw new IllegalArgumentException(string.toString());
        }
        long result = 0L;
        int i = beginIndex;
        while (i < endIndex) {
            char low;
            char c = $this$utf8Size.charAt(i);
            if (c < '\u0080') {
                long l = result;
                result = l + 1L;
                ++i;
                continue;
            }
            if (c < '\u0800') {
                result += (long)2;
                ++i;
                continue;
            }
            if (c < '\ud800' || c > '\udfff') {
                result += (long)3;
                ++i;
                continue;
            }
            char c2 = low = i + 1 < endIndex ? $this$utf8Size.charAt(i + 1) : (char)'\u0000';
            if (c > '\udbff' || low < '\udc00' || low > '\udfff') {
                long l = result;
                result = l + 1L;
                ++i;
                continue;
            }
            result += (long)4;
            i += 2;
        }
        return result;
    }

    public static /* synthetic */ long size$default(String string, int n, int n2, int n3, Object object) {
        if ((n3 & 1) != 0) {
            n = 0;
        }
        if ((n3 & 2) != 0) {
            n2 = string.length();
        }
        return Utf8.size(string, n, n2);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static final boolean isIsoControl(int codePoint) {
        boolean bl;
        boolean $i$f$isIsoControl = false;
        if (0 <= codePoint) {
            if (codePoint < 32) {
                return true;
            }
            bl = false;
        } else {
            bl = false;
        }
        if (bl) return true;
        if (127 > codePoint) return false;
        if (codePoint >= 160) return false;
        return true;
    }

    /*
     * WARNING - void declaration
     */
    public static final boolean isUtf8Continuation(byte by) {
        void $this$and$iv;
        boolean $i$f$isUtf8Continuation = false;
        byte by2 = by;
        int other$iv = 192;
        boolean $i$f$and = false;
        return ($this$and$iv & other$iv) == 128;
    }

    public static final void processUtf8Bytes(@NotNull String $this$processUtf8Bytes, int beginIndex, int endIndex, @NotNull Function1<? super Byte, Unit> yield) {
        Intrinsics.checkNotNullParameter($this$processUtf8Bytes, "<this>");
        Intrinsics.checkNotNullParameter(yield, "yield");
        boolean $i$f$processUtf8Bytes = false;
        int index = beginIndex;
        while (index < endIndex) {
            char c;
            block10: {
                block9: {
                    c = $this$processUtf8Bytes.charAt(index);
                    if (Intrinsics.compare(c, 128) < 0) {
                        yield.invoke((Byte)((byte)c));
                        ++index;
                        while (index < endIndex && Intrinsics.compare($this$processUtf8Bytes.charAt(index), 128) < 0) {
                            yield.invoke((Byte)((byte)$this$processUtf8Bytes.charAt(index++)));
                        }
                        continue;
                    }
                    if (Intrinsics.compare(c, 2048) < 0) {
                        yield.invoke((Byte)((byte)(c >> 6 | 0xC0)));
                        yield.invoke((Byte)((byte)(c & 0x3F | 0x80)));
                        ++index;
                        continue;
                    }
                    if (!('\ud800' <= c ? c < '\ue000' : false)) {
                        yield.invoke((Byte)((byte)(c >> 12 | 0xE0)));
                        yield.invoke((Byte)((byte)(c >> 6 & 0x3F | 0x80)));
                        yield.invoke((Byte)((byte)(c & 0x3F | 0x80)));
                        ++index;
                        continue;
                    }
                    if (Intrinsics.compare(c, 56319) > 0 || endIndex <= index + 1) break block9;
                    char c2 = $this$processUtf8Bytes.charAt(index + 1);
                    if ('\udc00' <= c2 ? c2 < '\ue000' : false) break block10;
                }
                yield.invoke((Byte)((byte)63));
                ++index;
                continue;
            }
            int codePoint = (c << 10) + $this$processUtf8Bytes.charAt(index + 1) + -56613888;
            yield.invoke((Byte)((byte)(codePoint >> 18 | 0xF0)));
            yield.invoke((Byte)((byte)(codePoint >> 12 & 0x3F | 0x80)));
            yield.invoke((Byte)((byte)(codePoint >> 6 & 0x3F | 0x80)));
            yield.invoke((Byte)((byte)(codePoint & 0x3F | 0x80)));
            index += 2;
        }
    }

    /*
     * Unable to fully structure code
     */
    public static final void processUtf8CodePoints(@NotNull byte[] $this$processUtf8CodePoints, int beginIndex, int endIndex, @NotNull Function1<? super Integer, Unit> yield) {
        Intrinsics.checkNotNullParameter($this$processUtf8CodePoints, "<this>");
        Intrinsics.checkNotNullParameter(yield, "yield");
        $i$f$processUtf8CodePoints = false;
        index = beginIndex;
        while (index < endIndex) {
            block37: {
                block41: {
                    block38: {
                        block40: {
                            block39: {
                                block34: {
                                    block36: {
                                        block35: {
                                            b0 = $this$processUtf8CodePoints[index];
                                            if (b0 >= 0) {
                                                yield.invoke(Integer.valueOf(b0));
                                                ++index;
                                                while (index < endIndex && $this$processUtf8CodePoints[index] >= 0) {
                                                    yield.invoke(Integer.valueOf($this$processUtf8CodePoints[index++]));
                                                }
                                                continue;
                                            }
                                            var7_7 = b0;
                                            other$iv = 5;
                                            $i$f$shr = 0;
                                            if ($this$shr$iv >> other$iv == -2) {
                                                $this$process2Utf8Bytes$iv = $this$processUtf8CodePoints;
                                                $i$f$process2Utf8Bytes = false;
                                                if (endIndex <= index + 1) {
                                                    $i$f$shr = 65533;
                                                    var19_22 = index;
                                                    $i$a$-process2Utf8Bytes-Utf8$processUtf8CodePoints$1 = false;
                                                    yield.invoke((Integer)it);
                                                    var20_23 = Unit.INSTANCE;
                                                    v0 = var19_22;
                                                    v1 = 1;
                                                } else {
                                                    b0$iv = $this$process2Utf8Bytes$iv[index];
                                                    b1$iv = $this$process2Utf8Bytes$iv[index + 1];
                                                    $i$f$isUtf8Continuation = false;
                                                    var14_17 = b1$iv;
                                                    other$iv$iv$iv = 192;
                                                    $i$f$and = false;
                                                    if (!(($this$and$iv$iv$iv & other$iv$iv$iv) == 128)) {
                                                        it = 65533;
                                                        $i$a$-process2Utf8Bytes-Utf8$processUtf8CodePoints$1 = false;
                                                        yield.invoke((Integer)it);
                                                        var20_23 = Unit.INSTANCE;
                                                        v0 = var19_22;
                                                        v1 = 1;
                                                    } else {
                                                        codePoint$iv = 3968 ^ b1$iv ^ b0$iv << 6;
                                                        if (codePoint$iv < 128) {
                                                            it = 65533;
                                                            $i$a$-process2Utf8Bytes-Utf8$processUtf8CodePoints$1 = false;
                                                            yield.invoke((Integer)it);
                                                            var20_23 = Unit.INSTANCE;
                                                            v0 = var19_22;
                                                        } else {
                                                            it = codePoint$iv;
                                                            $i$a$-process2Utf8Bytes-Utf8$processUtf8CodePoints$1 = false;
                                                            yield.invoke((Integer)it);
                                                            var20_23 = Unit.INSTANCE;
                                                            v0 = var19_22;
                                                        }
                                                        v1 = 2;
                                                    }
                                                }
                                                index = v0 + v1;
                                                continue;
                                            }
                                            $this$process2Utf8Bytes$iv = b0;
                                            other$iv = 4;
                                            $i$f$shr = 0;
                                            if ($this$shr$iv >> other$iv != -2) break block34;
                                            $this$process3Utf8Bytes$iv = $this$processUtf8CodePoints;
                                            $i$f$process3Utf8Bytes = false;
                                            if (endIndex > index + 2) break block35;
                                            $i$f$shr = 65533;
                                            var19_22 = index;
                                            $i$a$-process3Utf8Bytes-Utf8$processUtf8CodePoints$2 = false;
                                            yield.invoke((Integer)it);
                                            var20_23 = Unit.INSTANCE;
                                            v2 = var19_22;
                                            if (endIndex <= index + 1) ** GOTO lbl-1000
                                            byte$iv$iv = $this$process3Utf8Bytes$iv[index + 1];
                                            $i$f$isUtf8Continuation = false;
                                            codePoint$iv = byte$iv$iv;
                                            other$iv$iv$iv = 192;
                                            $i$f$and = false;
                                            if (!(($this$and$iv$iv$iv & other$iv$iv$iv) == 128)) lbl-1000:
                                            // 2 sources

                                            {
                                                v3 = 1;
                                            } else {
                                                v3 = 2;
                                            }
                                            break block36;
                                        }
                                        b0$iv = $this$process3Utf8Bytes$iv[index];
                                        b1$iv = $this$process3Utf8Bytes$iv[index + 1];
                                        $i$f$isUtf8Continuation = false;
                                        other$iv$iv$iv = b1$iv;
                                        other$iv$iv$iv = 192;
                                        $i$f$and = false;
                                        if (!(($this$and$iv$iv$iv & other$iv$iv$iv) == 128)) {
                                            it = 65533;
                                            $i$a$-process3Utf8Bytes-Utf8$processUtf8CodePoints$2 = false;
                                            yield.invoke((Integer)it);
                                            var20_23 = Unit.INSTANCE;
                                            v2 = var19_22;
                                            v3 = 1;
                                        } else {
                                            b2$iv = $this$process3Utf8Bytes$iv[index + 2];
                                            $i$f$isUtf8Continuation = false;
                                            other$iv$iv$iv = b2$iv;
                                            other$iv$iv$iv = 192;
                                            $i$f$and = false;
                                            if (!(($this$and$iv$iv$iv & other$iv$iv$iv) == 128)) {
                                                it = 65533;
                                                $i$a$-process3Utf8Bytes-Utf8$processUtf8CodePoints$2 = false;
                                                yield.invoke((Integer)it);
                                                var20_23 = Unit.INSTANCE;
                                                v2 = var19_22;
                                                v3 = 2;
                                            } else {
                                                codePoint$iv = -123008 ^ b2$iv ^ b1$iv << 6 ^ b0$iv << 12;
                                                if (codePoint$iv < 2048) {
                                                    it = 65533;
                                                    $i$a$-process3Utf8Bytes-Utf8$processUtf8CodePoints$2 = false;
                                                    yield.invoke((Integer)it);
                                                    var20_23 = Unit.INSTANCE;
                                                    v2 = var19_22;
                                                } else {
                                                    v4 = 55296 <= codePoint$iv ? codePoint$iv < 57344 : false;
                                                    if (v4) {
                                                        it = 65533;
                                                        $i$a$-process3Utf8Bytes-Utf8$processUtf8CodePoints$2 = false;
                                                        yield.invoke((Integer)it);
                                                        var20_23 = Unit.INSTANCE;
                                                        v2 = var19_22;
                                                    } else {
                                                        it = codePoint$iv;
                                                        $i$a$-process3Utf8Bytes-Utf8$processUtf8CodePoints$2 = false;
                                                        yield.invoke((Integer)it);
                                                        var20_23 = Unit.INSTANCE;
                                                        v2 = var19_22;
                                                    }
                                                }
                                                v3 = 3;
                                            }
                                        }
                                    }
                                    index = v2 + v3;
                                    continue;
                                }
                                $this$process3Utf8Bytes$iv = b0;
                                other$iv = 3;
                                $i$f$shr = 0;
                                if ($this$shr$iv >> other$iv != -2) break block37;
                                $this$process4Utf8Bytes$iv = $this$processUtf8CodePoints;
                                $i$f$process4Utf8Bytes = false;
                                if (endIndex > index + 3) break block38;
                                $i$f$shr = 65533;
                                var19_22 = index;
                                $i$a$-process4Utf8Bytes-Utf8$processUtf8CodePoints$3 = false;
                                yield.invoke((Integer)it);
                                var20_23 = Unit.INSTANCE;
                                v5 = var19_22;
                                if (endIndex <= index + 1) break block39;
                                byte$iv$iv = $this$process4Utf8Bytes$iv[index + 1];
                                $i$f$isUtf8Continuation = false;
                                b2$iv = byte$iv$iv;
                                other$iv$iv$iv = 192;
                                $i$f$and = false;
                                if (($this$and$iv$iv$iv & other$iv$iv$iv) == 128) break block40;
                            }
                            v6 = 1;
                            break block41;
                        }
                        if (endIndex <= index + 2) ** GOTO lbl-1000
                        byte$iv$iv = $this$process4Utf8Bytes$iv[index + 2];
                        $i$f$isUtf8Continuation = false;
                        $this$and$iv$iv$iv = byte$iv$iv;
                        other$iv$iv$iv = 192;
                        $i$f$and = false;
                        if (!(($this$and$iv$iv$iv & other$iv$iv$iv) == 128)) lbl-1000:
                        // 2 sources

                        {
                            v6 = 2;
                        } else {
                            v6 = 3;
                        }
                        break block41;
                    }
                    b0$iv = $this$process4Utf8Bytes$iv[index];
                    b1$iv = $this$process4Utf8Bytes$iv[index + 1];
                    $i$f$isUtf8Continuation = false;
                    other$iv$iv$iv = b1$iv;
                    other$iv$iv$iv = 192;
                    $i$f$and = false;
                    if (!(($this$and$iv$iv$iv & other$iv$iv$iv) == 128)) {
                        it = 65533;
                        $i$a$-process4Utf8Bytes-Utf8$processUtf8CodePoints$3 = false;
                        yield.invoke((Integer)it);
                        var20_23 = Unit.INSTANCE;
                        v5 = var19_22;
                        v6 = 1;
                    } else {
                        b2$iv = $this$process4Utf8Bytes$iv[index + 2];
                        $i$f$isUtf8Continuation = false;
                        other$iv$iv$iv = b2$iv;
                        other$iv$iv$iv = 192;
                        $i$f$and = false;
                        if (!(($this$and$iv$iv$iv & other$iv$iv$iv) == 128)) {
                            it = 65533;
                            $i$a$-process4Utf8Bytes-Utf8$processUtf8CodePoints$3 = false;
                            yield.invoke((Integer)it);
                            var20_23 = Unit.INSTANCE;
                            v5 = var19_22;
                            v6 = 2;
                        } else {
                            b3$iv = $this$process4Utf8Bytes$iv[index + 3];
                            $i$f$isUtf8Continuation = false;
                            other$iv$iv$iv = b3$iv;
                            other$iv$iv$iv = 192;
                            $i$f$and = false;
                            if (!(($this$and$iv$iv$iv & other$iv$iv$iv) == 128)) {
                                it = 65533;
                                $i$a$-process4Utf8Bytes-Utf8$processUtf8CodePoints$3 = false;
                                yield.invoke((Integer)it);
                                var20_23 = Unit.INSTANCE;
                                v5 = var19_22;
                                v6 = 3;
                            } else {
                                codePoint$iv = 3678080 ^ b3$iv ^ b2$iv << 6 ^ b1$iv << 12 ^ b0$iv << 18;
                                if (codePoint$iv > 0x10FFFF) {
                                    it = 65533;
                                    $i$a$-process4Utf8Bytes-Utf8$processUtf8CodePoints$3 = false;
                                    yield.invoke((Integer)it);
                                    var20_23 = Unit.INSTANCE;
                                    v5 = var19_22;
                                } else {
                                    v7 = 55296 <= codePoint$iv ? codePoint$iv < 57344 : false;
                                    if (v7) {
                                        it = 65533;
                                        $i$a$-process4Utf8Bytes-Utf8$processUtf8CodePoints$3 = false;
                                        yield.invoke((Integer)it);
                                        var20_23 = Unit.INSTANCE;
                                        v5 = var19_22;
                                    } else if (codePoint$iv < 65536) {
                                        it = 65533;
                                        $i$a$-process4Utf8Bytes-Utf8$processUtf8CodePoints$3 = false;
                                        yield.invoke((Integer)it);
                                        var20_23 = Unit.INSTANCE;
                                        v5 = var19_22;
                                    } else {
                                        it = codePoint$iv;
                                        $i$a$-process4Utf8Bytes-Utf8$processUtf8CodePoints$3 = false;
                                        yield.invoke((Integer)it);
                                        var20_23 = Unit.INSTANCE;
                                        v5 = var19_22;
                                    }
                                }
                                v6 = 4;
                            }
                        }
                    }
                }
                index = v5 + v6;
                continue;
            }
            yield.invoke((Integer)65533);
            ++index;
        }
    }

    /*
     * Unable to fully structure code
     */
    public static final void processUtf16Chars(@NotNull byte[] $this$processUtf16Chars, int beginIndex, int endIndex, @NotNull Function1<? super Character, Unit> yield) {
        Intrinsics.checkNotNullParameter($this$processUtf16Chars, "<this>");
        Intrinsics.checkNotNullParameter(yield, "yield");
        $i$f$processUtf16Chars = false;
        index = beginIndex;
        while (index < endIndex) {
            block53: {
                block57: {
                    block54: {
                        block56: {
                            block55: {
                                block50: {
                                    block52: {
                                        block51: {
                                            b0 = $this$processUtf16Chars[index];
                                            if (b0 >= 0) {
                                                yield.invoke(Character.valueOf((char)b0));
                                                ++index;
                                                while (index < endIndex && $this$processUtf16Chars[index] >= 0) {
                                                    yield.invoke(Character.valueOf((char)$this$processUtf16Chars[index++]));
                                                }
                                                continue;
                                            }
                                            var7_7 = b0;
                                            other$iv = 5;
                                            $i$f$shr = 0;
                                            if ($this$shr$iv >> other$iv == -2) {
                                                $this$process2Utf8Bytes$iv = $this$processUtf16Chars;
                                                $i$f$process2Utf8Bytes = false;
                                                if (endIndex <= index + 1) {
                                                    $i$f$shr = 65533;
                                                    var19_22 = index;
                                                    $i$a$-process2Utf8Bytes-Utf8$processUtf16Chars$1 = false;
                                                    yield.invoke(Character.valueOf((char)it));
                                                    var20_23 = Unit.INSTANCE;
                                                    v0 = var19_22;
                                                    v1 = 1;
                                                } else {
                                                    b0$iv = $this$process2Utf8Bytes$iv[index];
                                                    b1$iv = $this$process2Utf8Bytes$iv[index + 1];
                                                    $i$f$isUtf8Continuation = false;
                                                    var14_17 = b1$iv;
                                                    other$iv$iv$iv = 192;
                                                    $i$f$and = false;
                                                    if (!(($this$and$iv$iv$iv & other$iv$iv$iv) == 128)) {
                                                        it = 65533;
                                                        $i$a$-process2Utf8Bytes-Utf8$processUtf16Chars$1 = false;
                                                        yield.invoke(Character.valueOf((char)it));
                                                        var20_23 = Unit.INSTANCE;
                                                        v0 = var19_22;
                                                        v1 = 1;
                                                    } else {
                                                        codePoint$iv = 3968 ^ b1$iv ^ b0$iv << 6;
                                                        if (codePoint$iv < 128) {
                                                            it = 65533;
                                                            $i$a$-process2Utf8Bytes-Utf8$processUtf16Chars$1 = false;
                                                            yield.invoke(Character.valueOf((char)it));
                                                            var20_23 = Unit.INSTANCE;
                                                            v0 = var19_22;
                                                        } else {
                                                            it = codePoint$iv;
                                                            $i$a$-process2Utf8Bytes-Utf8$processUtf16Chars$1 = false;
                                                            yield.invoke(Character.valueOf((char)it));
                                                            var20_23 = Unit.INSTANCE;
                                                            v0 = var19_22;
                                                        }
                                                        v1 = 2;
                                                    }
                                                }
                                                index = v0 + v1;
                                                continue;
                                            }
                                            $this$process2Utf8Bytes$iv = b0;
                                            other$iv = 4;
                                            $i$f$shr = 0;
                                            if ($this$shr$iv >> other$iv != -2) break block50;
                                            $this$process3Utf8Bytes$iv = $this$processUtf16Chars;
                                            $i$f$process3Utf8Bytes = false;
                                            if (endIndex > index + 2) break block51;
                                            $i$f$shr = 65533;
                                            var19_22 = index;
                                            $i$a$-process3Utf8Bytes-Utf8$processUtf16Chars$2 = false;
                                            yield.invoke(Character.valueOf((char)it));
                                            var20_23 = Unit.INSTANCE;
                                            v2 = var19_22;
                                            if (endIndex <= index + 1) ** GOTO lbl-1000
                                            byte$iv$iv = $this$process3Utf8Bytes$iv[index + 1];
                                            $i$f$isUtf8Continuation = false;
                                            codePoint$iv = byte$iv$iv;
                                            other$iv$iv$iv = 192;
                                            $i$f$and = false;
                                            if (!(($this$and$iv$iv$iv & other$iv$iv$iv) == 128)) lbl-1000:
                                            // 2 sources

                                            {
                                                v3 = 1;
                                            } else {
                                                v3 = 2;
                                            }
                                            break block52;
                                        }
                                        b0$iv = $this$process3Utf8Bytes$iv[index];
                                        b1$iv = $this$process3Utf8Bytes$iv[index + 1];
                                        $i$f$isUtf8Continuation = false;
                                        other$iv$iv$iv = b1$iv;
                                        other$iv$iv$iv = 192;
                                        $i$f$and = false;
                                        if (!(($this$and$iv$iv$iv & other$iv$iv$iv) == 128)) {
                                            it = 65533;
                                            $i$a$-process3Utf8Bytes-Utf8$processUtf16Chars$2 = false;
                                            yield.invoke(Character.valueOf((char)it));
                                            var20_23 = Unit.INSTANCE;
                                            v2 = var19_22;
                                            v3 = 1;
                                        } else {
                                            b2$iv = $this$process3Utf8Bytes$iv[index + 2];
                                            $i$f$isUtf8Continuation = false;
                                            other$iv$iv$iv = b2$iv;
                                            other$iv$iv$iv = 192;
                                            $i$f$and = false;
                                            if (!(($this$and$iv$iv$iv & other$iv$iv$iv) == 128)) {
                                                it = 65533;
                                                $i$a$-process3Utf8Bytes-Utf8$processUtf16Chars$2 = false;
                                                yield.invoke(Character.valueOf((char)it));
                                                var20_23 = Unit.INSTANCE;
                                                v2 = var19_22;
                                                v3 = 2;
                                            } else {
                                                codePoint$iv = -123008 ^ b2$iv ^ b1$iv << 6 ^ b0$iv << 12;
                                                if (codePoint$iv < 2048) {
                                                    it = 65533;
                                                    $i$a$-process3Utf8Bytes-Utf8$processUtf16Chars$2 = false;
                                                    yield.invoke(Character.valueOf((char)it));
                                                    var20_23 = Unit.INSTANCE;
                                                    v2 = var19_22;
                                                } else {
                                                    v4 = 55296 <= codePoint$iv ? codePoint$iv < 57344 : false;
                                                    if (v4) {
                                                        it = 65533;
                                                        $i$a$-process3Utf8Bytes-Utf8$processUtf16Chars$2 = false;
                                                        yield.invoke(Character.valueOf((char)it));
                                                        var20_23 = Unit.INSTANCE;
                                                        v2 = var19_22;
                                                    } else {
                                                        it = codePoint$iv;
                                                        $i$a$-process3Utf8Bytes-Utf8$processUtf16Chars$2 = false;
                                                        yield.invoke(Character.valueOf((char)it));
                                                        var20_23 = Unit.INSTANCE;
                                                        v2 = var19_22;
                                                    }
                                                }
                                                v3 = 3;
                                            }
                                        }
                                    }
                                    index = v2 + v3;
                                    continue;
                                }
                                $this$process3Utf8Bytes$iv = b0;
                                other$iv = 3;
                                $i$f$shr = 0;
                                if ($this$shr$iv >> other$iv != -2) break block53;
                                $this$process4Utf8Bytes$iv = $this$processUtf16Chars;
                                $i$f$process4Utf8Bytes = false;
                                if (endIndex > index + 3) break block54;
                                $i$f$shr = 65533;
                                var19_22 = index;
                                $i$a$-process4Utf8Bytes-Utf8$processUtf16Chars$3 = false;
                                if (codePoint != 65533) {
                                    yield.invoke(Character.valueOf((char)((codePoint >>> 10) + 55232)));
                                    yield.invoke(Character.valueOf((char)((codePoint & 1023) + 56320)));
                                } else {
                                    yield.invoke(Character.valueOf('\ufffd'));
                                }
                                var20_23 = Unit.INSTANCE;
                                v5 = var19_22;
                                if (endIndex <= index + 1) break block55;
                                byte$iv$iv = $this$process4Utf8Bytes$iv[index + 1];
                                $i$f$isUtf8Continuation = false;
                                b2$iv = byte$iv$iv;
                                other$iv$iv$iv = 192;
                                $i$f$and = false;
                                if (($this$and$iv$iv$iv & other$iv$iv$iv) == 128) break block56;
                            }
                            v6 = 1;
                            break block57;
                        }
                        if (endIndex <= index + 2) ** GOTO lbl-1000
                        byte$iv$iv = $this$process4Utf8Bytes$iv[index + 2];
                        $i$f$isUtf8Continuation = false;
                        $this$and$iv$iv$iv = byte$iv$iv;
                        other$iv$iv$iv = 192;
                        $i$f$and = false;
                        if (!(($this$and$iv$iv$iv & other$iv$iv$iv) == 128)) lbl-1000:
                        // 2 sources

                        {
                            v6 = 2;
                        } else {
                            v6 = 3;
                        }
                        break block57;
                    }
                    b0$iv = $this$process4Utf8Bytes$iv[index];
                    b1$iv = $this$process4Utf8Bytes$iv[index + 1];
                    $i$f$isUtf8Continuation = false;
                    other$iv$iv$iv = b1$iv;
                    other$iv$iv$iv = 192;
                    $i$f$and = false;
                    if (!(($this$and$iv$iv$iv & other$iv$iv$iv) == 128)) {
                        codePoint = 65533;
                        $i$a$-process4Utf8Bytes-Utf8$processUtf16Chars$3 = false;
                        if (codePoint != 65533) {
                            yield.invoke(Character.valueOf((char)((codePoint >>> 10) + 55232)));
                            yield.invoke(Character.valueOf((char)((codePoint & 1023) + 56320)));
                        } else {
                            yield.invoke(Character.valueOf('\ufffd'));
                        }
                        var20_23 = Unit.INSTANCE;
                        v5 = var19_22;
                        v6 = 1;
                    } else {
                        b2$iv = $this$process4Utf8Bytes$iv[index + 2];
                        $i$f$isUtf8Continuation = false;
                        other$iv$iv$iv = b2$iv;
                        other$iv$iv$iv = 192;
                        $i$f$and = false;
                        if (!(($this$and$iv$iv$iv & other$iv$iv$iv) == 128)) {
                            codePoint = 65533;
                            $i$a$-process4Utf8Bytes-Utf8$processUtf16Chars$3 = false;
                            if (codePoint != 65533) {
                                yield.invoke(Character.valueOf((char)((codePoint >>> 10) + 55232)));
                                yield.invoke(Character.valueOf((char)((codePoint & 1023) + 56320)));
                            } else {
                                yield.invoke(Character.valueOf('\ufffd'));
                            }
                            var20_23 = Unit.INSTANCE;
                            v5 = var19_22;
                            v6 = 2;
                        } else {
                            b3$iv = $this$process4Utf8Bytes$iv[index + 3];
                            $i$f$isUtf8Continuation = false;
                            other$iv$iv$iv = b3$iv;
                            other$iv$iv$iv = 192;
                            $i$f$and = false;
                            if (!(($this$and$iv$iv$iv & other$iv$iv$iv) == 128)) {
                                codePoint = 65533;
                                $i$a$-process4Utf8Bytes-Utf8$processUtf16Chars$3 = false;
                                if (codePoint != 65533) {
                                    yield.invoke(Character.valueOf((char)((codePoint >>> 10) + 55232)));
                                    yield.invoke(Character.valueOf((char)((codePoint & 1023) + 56320)));
                                } else {
                                    yield.invoke(Character.valueOf('\ufffd'));
                                }
                                var20_23 = Unit.INSTANCE;
                                v5 = var19_22;
                                v6 = 3;
                            } else {
                                codePoint$iv = 3678080 ^ b3$iv ^ b2$iv << 6 ^ b1$iv << 12 ^ b0$iv << 18;
                                if (codePoint$iv > 0x10FFFF) {
                                    codePoint = 65533;
                                    $i$a$-process4Utf8Bytes-Utf8$processUtf16Chars$3 = false;
                                    if (codePoint != 65533) {
                                        yield.invoke(Character.valueOf((char)((codePoint >>> 10) + 55232)));
                                        yield.invoke(Character.valueOf((char)((codePoint & 1023) + 56320)));
                                    } else {
                                        yield.invoke(Character.valueOf('\ufffd'));
                                    }
                                    var20_23 = Unit.INSTANCE;
                                    v5 = var19_22;
                                } else {
                                    v7 = 55296 <= codePoint$iv ? codePoint$iv < 57344 : false;
                                    if (v7) {
                                        codePoint = 65533;
                                        $i$a$-process4Utf8Bytes-Utf8$processUtf16Chars$3 = false;
                                        if (codePoint != 65533) {
                                            yield.invoke(Character.valueOf((char)((codePoint >>> 10) + 55232)));
                                            yield.invoke(Character.valueOf((char)((codePoint & 1023) + 56320)));
                                        } else {
                                            yield.invoke(Character.valueOf('\ufffd'));
                                        }
                                        var20_23 = Unit.INSTANCE;
                                        v5 = var19_22;
                                    } else if (codePoint$iv < 65536) {
                                        codePoint = 65533;
                                        $i$a$-process4Utf8Bytes-Utf8$processUtf16Chars$3 = false;
                                        if (codePoint != 65533) {
                                            yield.invoke(Character.valueOf((char)((codePoint >>> 10) + 55232)));
                                            yield.invoke(Character.valueOf((char)((codePoint & 1023) + 56320)));
                                        } else {
                                            yield.invoke(Character.valueOf('\ufffd'));
                                        }
                                        var20_23 = Unit.INSTANCE;
                                        v5 = var19_22;
                                    } else {
                                        codePoint = codePoint$iv;
                                        $i$a$-process4Utf8Bytes-Utf8$processUtf16Chars$3 = false;
                                        if (codePoint != 65533) {
                                            yield.invoke(Character.valueOf((char)((codePoint >>> 10) + 55232)));
                                            yield.invoke(Character.valueOf((char)((codePoint & 1023) + 56320)));
                                        } else {
                                            yield.invoke(Character.valueOf('\ufffd'));
                                        }
                                        var20_23 = Unit.INSTANCE;
                                        v5 = var19_22;
                                    }
                                }
                                v6 = 4;
                            }
                        }
                    }
                }
                index = v5 + v6;
                continue;
            }
            yield.invoke(Character.valueOf('\ufffd'));
            ++index;
        }
    }

    /*
     * WARNING - void declaration
     */
    public static final int process2Utf8Bytes(@NotNull byte[] $this$process2Utf8Bytes, int beginIndex, int endIndex, @NotNull Function1<? super Integer, Unit> yield) {
        void $this$and$iv$iv;
        Intrinsics.checkNotNullParameter($this$process2Utf8Bytes, "<this>");
        Intrinsics.checkNotNullParameter(yield, "yield");
        boolean $i$f$process2Utf8Bytes = false;
        if (endIndex <= beginIndex + 1) {
            yield.invoke((Integer)65533);
            return 1;
        }
        byte b0 = $this$process2Utf8Bytes[beginIndex];
        byte b1 = $this$process2Utf8Bytes[beginIndex + 1];
        boolean $i$f$isUtf8Continuation = false;
        byte by = b1;
        int other$iv$iv = 192;
        boolean $i$f$and = false;
        if (!(($this$and$iv$iv & other$iv$iv) == 128)) {
            yield.invoke((Integer)65533);
            return 1;
        }
        int codePoint = 0xF80 ^ b1 ^ b0 << 6;
        if (codePoint < 128) {
            yield.invoke((Integer)65533);
        } else {
            yield.invoke((Integer)codePoint);
        }
        return 2;
    }

    /*
     * WARNING - void declaration
     */
    public static final int process3Utf8Bytes(@NotNull byte[] $this$process3Utf8Bytes, int beginIndex, int endIndex, @NotNull Function1<? super Integer, Unit> yield) {
        void $this$and$iv$iv;
        void $this$and$iv$iv2;
        block9: {
            block11: {
                block10: {
                    void $this$and$iv$iv3;
                    Intrinsics.checkNotNullParameter($this$process3Utf8Bytes, "<this>");
                    Intrinsics.checkNotNullParameter(yield, "yield");
                    boolean $i$f$process3Utf8Bytes = false;
                    if (endIndex > beginIndex + 2) break block9;
                    yield.invoke((Integer)65533);
                    if (endIndex <= beginIndex + 1) break block10;
                    byte byte$iv = $this$process3Utf8Bytes[beginIndex + 1];
                    boolean $i$f$isUtf8Continuation = false;
                    byte by = byte$iv;
                    int other$iv$iv = 192;
                    boolean $i$f$and = false;
                    if (($this$and$iv$iv3 & other$iv$iv) == 128) break block11;
                }
                return 1;
            }
            return 2;
        }
        byte b0 = $this$process3Utf8Bytes[beginIndex];
        byte b1 = $this$process3Utf8Bytes[beginIndex + 1];
        boolean $i$f$isUtf8Continuation = false;
        byte other$iv$iv = b1;
        int other$iv$iv2 = 192;
        boolean $i$f$and = false;
        if (!(($this$and$iv$iv2 & other$iv$iv2) == 128)) {
            yield.invoke((Integer)65533);
            return 1;
        }
        int b2 = $this$process3Utf8Bytes[beginIndex + 2];
        boolean $i$f$isUtf8Continuation2 = false;
        other$iv$iv2 = b2;
        int other$iv$iv3 = 192;
        boolean $i$f$and2 = false;
        if (!(($this$and$iv$iv & other$iv$iv3) == 128)) {
            yield.invoke((Integer)65533);
            return 2;
        }
        int codePoint = 0xFFFE1F80 ^ b2 ^ b1 << 6 ^ b0 << 12;
        if (codePoint < 2048) {
            yield.invoke((Integer)65533);
        } else {
            boolean bl = 55296 <= codePoint ? codePoint < 57344 : false;
            if (bl) {
                yield.invoke((Integer)65533);
            } else {
                yield.invoke((Integer)codePoint);
            }
        }
        return 3;
    }

    /*
     * WARNING - void declaration
     */
    public static final int process4Utf8Bytes(@NotNull byte[] $this$process4Utf8Bytes, int beginIndex, int endIndex, @NotNull Function1<? super Integer, Unit> yield) {
        void $this$and$iv$iv;
        void $this$and$iv$iv2;
        void $this$and$iv$iv3;
        block15: {
            block19: {
                block18: {
                    byte $this$and$iv$iv4;
                    boolean $i$f$and;
                    int other$iv$iv;
                    boolean $i$f$isUtf8Continuation;
                    byte byte$iv;
                    block17: {
                        block16: {
                            Intrinsics.checkNotNullParameter($this$process4Utf8Bytes, "<this>");
                            Intrinsics.checkNotNullParameter(yield, "yield");
                            boolean $i$f$process4Utf8Bytes = false;
                            if (endIndex > beginIndex + 3) break block15;
                            yield.invoke((Integer)65533);
                            if (endIndex <= beginIndex + 1) break block16;
                            byte$iv = $this$process4Utf8Bytes[beginIndex + 1];
                            $i$f$isUtf8Continuation = false;
                            byte by = byte$iv;
                            other$iv$iv = 192;
                            $i$f$and = false;
                            if (($this$and$iv$iv4 & other$iv$iv) == 128) break block17;
                        }
                        return 1;
                    }
                    if (endIndex <= beginIndex + 2) break block18;
                    byte$iv = $this$process4Utf8Bytes[beginIndex + 2];
                    $i$f$isUtf8Continuation = false;
                    $this$and$iv$iv4 = byte$iv;
                    other$iv$iv = 192;
                    $i$f$and = false;
                    if (($this$and$iv$iv4 & other$iv$iv) == 128) break block19;
                }
                return 2;
            }
            return 3;
        }
        byte b0 = $this$process4Utf8Bytes[beginIndex];
        byte b1 = $this$process4Utf8Bytes[beginIndex + 1];
        boolean $i$f$isUtf8Continuation = false;
        byte other$iv$iv = b1;
        int other$iv$iv2 = 192;
        boolean $i$f$and = false;
        if (!(($this$and$iv$iv3 & other$iv$iv2) == 128)) {
            yield.invoke((Integer)65533);
            return 1;
        }
        int b2 = $this$process4Utf8Bytes[beginIndex + 2];
        boolean $i$f$isUtf8Continuation2 = false;
        other$iv$iv2 = b2;
        int other$iv$iv3 = 192;
        boolean $i$f$and2 = false;
        if (!(($this$and$iv$iv2 & other$iv$iv3) == 128)) {
            yield.invoke((Integer)65533);
            return 2;
        }
        int b3 = $this$process4Utf8Bytes[beginIndex + 3];
        boolean $i$f$isUtf8Continuation3 = false;
        other$iv$iv3 = b3;
        int other$iv$iv4 = 192;
        boolean $i$f$and3 = false;
        if (!(($this$and$iv$iv & other$iv$iv4) == 128)) {
            yield.invoke((Integer)65533);
            return 3;
        }
        int codePoint = 0x381F80 ^ b3 ^ b2 << 6 ^ b1 << 12 ^ b0 << 18;
        if (codePoint > 0x10FFFF) {
            yield.invoke((Integer)65533);
        } else {
            boolean bl = 55296 <= codePoint ? codePoint < 57344 : false;
            if (bl) {
                yield.invoke((Integer)65533);
            } else if (codePoint < 65536) {
                yield.invoke((Integer)65533);
            } else {
                yield.invoke((Integer)codePoint);
            }
        }
        return 4;
    }

    @JvmOverloads
    @JvmName(name="size")
    public static final long size(@NotNull String $this$utf8Size, int beginIndex) {
        Intrinsics.checkNotNullParameter($this$utf8Size, "<this>");
        return Utf8.size$default($this$utf8Size, beginIndex, 0, 2, null);
    }

    @JvmOverloads
    @JvmName(name="size")
    public static final long size(@NotNull String $this$utf8Size) {
        Intrinsics.checkNotNullParameter($this$utf8Size, "<this>");
        return Utf8.size$default($this$utf8Size, 0, 0, 3, null);
    }
}

