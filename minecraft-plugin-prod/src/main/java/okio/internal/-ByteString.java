/*
 * Decompiled with CFR 0.152.
 */
package okio.internal;

import java.util.Arrays;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.ArraysKt;
import kotlin.jvm.JvmName;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import okio.-Base64;
import okio.-SegmentedByteString;
import okio.Buffer;
import okio.ByteString;
import okio._JvmPlatformKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 9, 0}, k=2, xi=48, d1={"\u0000R\n\u0000\n\u0002\u0010\u0019\n\u0002\b\u0005\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0012\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\f\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\b\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0005\n\u0002\b\u0017\n\u0002\u0018\u0002\n\u0000\u001a\u0018\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u0007H\u0002\u001a\u0011\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\tH\u0080\b\u001a\u0010\u0010\u000e\u001a\u00020\u00072\u0006\u0010\u000f\u001a\u00020\u0010H\u0002\u001a\r\u0010\u0011\u001a\u00020\u0012*\u00020\fH\u0080\b\u001a\r\u0010\u0013\u001a\u00020\u0012*\u00020\fH\u0080\b\u001a\u0015\u0010\u0014\u001a\u00020\u0007*\u00020\f2\u0006\u0010\u0015\u001a\u00020\fH\u0080\b\u001a-\u0010\u0016\u001a\u00020\u0017*\u00020\f2\u0006\u0010\u0018\u001a\u00020\u00072\u0006\u0010\u0019\u001a\u00020\t2\u0006\u0010\u001a\u001a\u00020\u00072\u0006\u0010\u001b\u001a\u00020\u0007H\u0080\b\u001a\u000f\u0010\u001c\u001a\u0004\u0018\u00010\f*\u00020\u0012H\u0080\b\u001a\r\u0010\u001d\u001a\u00020\f*\u00020\u0012H\u0080\b\u001a\r\u0010\u001e\u001a\u00020\f*\u00020\u0012H\u0080\b\u001a\u0015\u0010\u001f\u001a\u00020 *\u00020\f2\u0006\u0010!\u001a\u00020\tH\u0080\b\u001a\u0015\u0010\u001f\u001a\u00020 *\u00020\f2\u0006\u0010!\u001a\u00020\fH\u0080\b\u001a\u0017\u0010\"\u001a\u00020 *\u00020\f2\b\u0010\u0015\u001a\u0004\u0018\u00010#H\u0080\b\u001a\u0015\u0010$\u001a\u00020%*\u00020\f2\u0006\u0010&\u001a\u00020\u0007H\u0080\b\u001a\r\u0010'\u001a\u00020\u0007*\u00020\fH\u0080\b\u001a\r\u0010(\u001a\u00020\u0007*\u00020\fH\u0080\b\u001a\r\u0010)\u001a\u00020\u0012*\u00020\fH\u0080\b\u001a\u001d\u0010*\u001a\u00020\u0007*\u00020\f2\u0006\u0010\u0015\u001a\u00020\t2\u0006\u0010+\u001a\u00020\u0007H\u0080\b\u001a\r\u0010,\u001a\u00020\t*\u00020\fH\u0080\b\u001a\u001d\u0010-\u001a\u00020\u0007*\u00020\f2\u0006\u0010\u0015\u001a\u00020\t2\u0006\u0010+\u001a\u00020\u0007H\u0080\b\u001a\u001d\u0010-\u001a\u00020\u0007*\u00020\f2\u0006\u0010\u0015\u001a\u00020\f2\u0006\u0010+\u001a\u00020\u0007H\u0080\b\u001a-\u0010.\u001a\u00020 *\u00020\f2\u0006\u0010\u0018\u001a\u00020\u00072\u0006\u0010\u0015\u001a\u00020\t2\u0006\u0010/\u001a\u00020\u00072\u0006\u0010\u001b\u001a\u00020\u0007H\u0080\b\u001a-\u0010.\u001a\u00020 *\u00020\f2\u0006\u0010\u0018\u001a\u00020\u00072\u0006\u0010\u0015\u001a\u00020\f2\u0006\u0010/\u001a\u00020\u00072\u0006\u0010\u001b\u001a\u00020\u0007H\u0080\b\u001a\u0015\u00100\u001a\u00020 *\u00020\f2\u0006\u00101\u001a\u00020\tH\u0080\b\u001a\u0015\u00100\u001a\u00020 *\u00020\f2\u0006\u00101\u001a\u00020\fH\u0080\b\u001a\u001d\u00102\u001a\u00020\f*\u00020\f2\u0006\u00103\u001a\u00020\u00072\u0006\u00104\u001a\u00020\u0007H\u0080\b\u001a\r\u00105\u001a\u00020\f*\u00020\fH\u0080\b\u001a\r\u00106\u001a\u00020\f*\u00020\fH\u0080\b\u001a\r\u00107\u001a\u00020\t*\u00020\fH\u0080\b\u001a\u001d\u00108\u001a\u00020\f*\u00020\t2\u0006\u0010\u0018\u001a\u00020\u00072\u0006\u0010\u001b\u001a\u00020\u0007H\u0080\b\u001a\r\u00109\u001a\u00020\u0012*\u00020\fH\u0080\b\u001a\r\u0010:\u001a\u00020\u0012*\u00020\fH\u0080\b\u001a$\u0010;\u001a\u00020\u0017*\u00020\f2\u0006\u0010<\u001a\u00020=2\u0006\u0010\u0018\u001a\u00020\u00072\u0006\u0010\u001b\u001a\u00020\u0007H\u0000\"\u001c\u0010\u0000\u001a\u00020\u00018\u0000X\u0081\u0004\u00a2\u0006\u000e\n\u0000\u0012\u0004\b\u0002\u0010\u0003\u001a\u0004\b\u0004\u0010\u0005\u00a8\u0006>"}, d2={"HEX_DIGIT_CHARS", "", "getHEX_DIGIT_CHARS$annotations", "()V", "getHEX_DIGIT_CHARS", "()[C", "codePointIndexToCharIndex", "", "s", "", "codePointCount", "commonOf", "Lokio/ByteString;", "data", "decodeHexDigit", "c", "", "commonBase64", "", "commonBase64Url", "commonCompareTo", "other", "commonCopyInto", "", "offset", "target", "targetOffset", "byteCount", "commonDecodeBase64", "commonDecodeHex", "commonEncodeUtf8", "commonEndsWith", "", "suffix", "commonEquals", "", "commonGetByte", "", "pos", "commonGetSize", "commonHashCode", "commonHex", "commonIndexOf", "fromIndex", "commonInternalArray", "commonLastIndexOf", "commonRangeEquals", "otherOffset", "commonStartsWith", "prefix", "commonSubstring", "beginIndex", "endIndex", "commonToAsciiLowercase", "commonToAsciiUppercase", "commonToByteArray", "commonToByteString", "commonToString", "commonUtf8", "commonWrite", "buffer", "Lokio/Buffer;", "okio"})
@JvmName(name="-ByteString")
@SourceDebugExtension(value={"SMAP\nByteString.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ByteString.kt\nokio/internal/-ByteString\n+ 2 Util.kt\nokio/-SegmentedByteString\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 Utf8.kt\nokio/Utf8\n*L\n1#1,363:1\n131#1,2:369\n133#1,9:372\n68#2:364\n74#2:365\n74#2:367\n74#2:368\n68#2:396\n74#2:408\n1#3:366\n1#3:371\n212#4,7:381\n122#4:388\n219#4,5:389\n122#4:394\n226#4:395\n228#4:397\n397#4,2:398\n122#4:400\n400#4,6:401\n127#4:407\n406#4:409\n122#4:410\n407#4,13:411\n122#4:424\n422#4:425\n122#4:426\n425#4:427\n230#4,3:428\n440#4,3:431\n122#4:434\n443#4:435\n127#4:436\n446#4,10:437\n127#4:447\n456#4:448\n122#4:449\n457#4,4:450\n127#4:454\n461#4:455\n122#4:456\n462#4,14:457\n122#4:471\n477#4,2:472\n122#4:474\n481#4:475\n122#4:476\n484#4:477\n234#4,3:478\n500#4,3:481\n122#4:484\n503#4:485\n127#4:486\n506#4,2:487\n127#4:489\n510#4,10:490\n127#4:500\n520#4:501\n122#4:502\n521#4,4:503\n127#4:507\n525#4:508\n122#4:509\n526#4,4:510\n127#4:514\n530#4:515\n122#4:516\n531#4,15:517\n122#4:532\n547#4,2:533\n122#4:535\n550#4,2:536\n122#4:538\n554#4:539\n122#4:540\n557#4:541\n241#4:542\n122#4:543\n242#4,5:544\n*S KotlinDebug\n*F\n+ 1 ByteString.kt\nokio/internal/-ByteString\n*L\n329#1:369,2\n329#1:372,9\n67#1:364\n68#1:365\n258#1:367\n259#1:368\n348#1:396\n348#1:408\n329#1:371\n348#1:381,7\n353#1:388\n348#1:389,5\n353#1:394\n348#1:395\n348#1:397\n348#1:398,2\n353#1:400\n348#1:401,6\n348#1:407\n348#1:409\n353#1:410\n348#1:411,13\n353#1:424\n348#1:425\n353#1:426\n348#1:427\n348#1:428,3\n348#1:431,3\n353#1:434\n348#1:435\n348#1:436\n348#1:437,10\n348#1:447\n348#1:448\n353#1:449\n348#1:450,4\n348#1:454\n348#1:455\n353#1:456\n348#1:457,14\n353#1:471\n348#1:472,2\n353#1:474\n348#1:475\n353#1:476\n348#1:477\n348#1:478,3\n348#1:481,3\n353#1:484\n348#1:485\n348#1:486\n348#1:487,2\n348#1:489\n348#1:490,10\n348#1:500\n348#1:501\n353#1:502\n348#1:503,4\n348#1:507\n348#1:508\n353#1:509\n348#1:510,4\n348#1:514\n348#1:515\n353#1:516\n348#1:517,15\n353#1:532\n348#1:533,2\n353#1:535\n348#1:536,2\n353#1:538\n348#1:539\n353#1:540\n348#1:541\n348#1:542\n353#1:543\n348#1:544,5\n*E\n"})
public final class -ByteString {
    @NotNull
    private static final char[] HEX_DIGIT_CHARS;

    @NotNull
    public static final String commonUtf8(@NotNull ByteString $this$commonUtf8) {
        Intrinsics.checkNotNullParameter($this$commonUtf8, "<this>");
        boolean $i$f$commonUtf8 = false;
        String result = $this$commonUtf8.getUtf8$okio();
        if (result == null) {
            result = _JvmPlatformKt.toUtf8String($this$commonUtf8.internalArray$okio());
            $this$commonUtf8.setUtf8$okio(result);
        }
        return result;
    }

    @NotNull
    public static final String commonBase64(@NotNull ByteString $this$commonBase64) {
        Intrinsics.checkNotNullParameter($this$commonBase64, "<this>");
        boolean $i$f$commonBase64 = false;
        return -Base64.encodeBase64$default($this$commonBase64.getData$okio(), null, 1, null);
    }

    @NotNull
    public static final String commonBase64Url(@NotNull ByteString $this$commonBase64Url) {
        Intrinsics.checkNotNullParameter($this$commonBase64Url, "<this>");
        boolean $i$f$commonBase64Url = false;
        return -Base64.encodeBase64($this$commonBase64Url.getData$okio(), -Base64.getBASE64_URL_SAFE());
    }

    @NotNull
    public static final char[] getHEX_DIGIT_CHARS() {
        return HEX_DIGIT_CHARS;
    }

    public static /* synthetic */ void getHEX_DIGIT_CHARS$annotations() {
    }

    /*
     * WARNING - void declaration
     */
    @NotNull
    public static final String commonHex(@NotNull ByteString $this$commonHex) {
        Intrinsics.checkNotNullParameter($this$commonHex, "<this>");
        boolean $i$f$commonHex = false;
        char[] result = new char[$this$commonHex.getData$okio().length * 2];
        int c = 0;
        for (byte b : $this$commonHex.getData$okio()) {
            void $this$and$iv;
            byte $this$shr$iv;
            int n = c++;
            byte by = b;
            int other$iv = 4;
            boolean $i$f$shr = false;
            result[n] = -ByteString.getHEX_DIGIT_CHARS()[$this$shr$iv >> other$iv & 0xF];
            int n2 = c++;
            $this$shr$iv = b;
            other$iv = 15;
            boolean $i$f$and = false;
            result[n2] = -ByteString.getHEX_DIGIT_CHARS()[$this$and$iv & other$iv];
        }
        return StringsKt.concatToString(result);
    }

    @NotNull
    public static final ByteString commonToAsciiLowercase(@NotNull ByteString $this$commonToAsciiLowercase) {
        Intrinsics.checkNotNullParameter($this$commonToAsciiLowercase, "<this>");
        boolean $i$f$commonToAsciiLowercase = false;
        for (int i = 0; i < $this$commonToAsciiLowercase.getData$okio().length; ++i) {
            byte c = $this$commonToAsciiLowercase.getData$okio()[i];
            if (c < 65 || c > 90) {
                continue;
            }
            byte[] byArray = $this$commonToAsciiLowercase.getData$okio();
            byte[] byArray2 = Arrays.copyOf(byArray, byArray.length);
            Intrinsics.checkNotNullExpressionValue(byArray2, "copyOf(this, size)");
            byte[] lowercase = byArray2;
            lowercase[i++] = (byte)(c - -32);
            while (i < lowercase.length) {
                c = lowercase[i];
                if (c < 65 || c > 90) {
                    ++i;
                    continue;
                }
                lowercase[i] = (byte)(c - -32);
                ++i;
            }
            return new ByteString(lowercase);
        }
        return $this$commonToAsciiLowercase;
    }

    @NotNull
    public static final ByteString commonToAsciiUppercase(@NotNull ByteString $this$commonToAsciiUppercase) {
        Intrinsics.checkNotNullParameter($this$commonToAsciiUppercase, "<this>");
        boolean $i$f$commonToAsciiUppercase = false;
        for (int i = 0; i < $this$commonToAsciiUppercase.getData$okio().length; ++i) {
            byte c = $this$commonToAsciiUppercase.getData$okio()[i];
            if (c < 97 || c > 122) {
                continue;
            }
            byte[] byArray = $this$commonToAsciiUppercase.getData$okio();
            byte[] byArray2 = Arrays.copyOf(byArray, byArray.length);
            Intrinsics.checkNotNullExpressionValue(byArray2, "copyOf(this, size)");
            byte[] lowercase = byArray2;
            lowercase[i++] = (byte)(c - 32);
            while (i < lowercase.length) {
                c = lowercase[i];
                if (c < 97 || c > 122) {
                    ++i;
                    continue;
                }
                lowercase[i] = (byte)(c - 32);
                ++i;
            }
            return new ByteString(lowercase);
        }
        return $this$commonToAsciiUppercase;
    }

    @NotNull
    public static final ByteString commonSubstring(@NotNull ByteString $this$commonSubstring, int beginIndex, int endIndex) {
        Intrinsics.checkNotNullParameter($this$commonSubstring, "<this>");
        boolean $i$f$commonSubstring = false;
        int endIndex2 = -SegmentedByteString.resolveDefaultParameter($this$commonSubstring, endIndex);
        if (!(beginIndex >= 0)) {
            boolean $i$a$-require--ByteString$commonSubstring$42 = false;
            String $i$a$-require--ByteString$commonSubstring$42 = "beginIndex < 0";
            throw new IllegalArgumentException($i$a$-require--ByteString$commonSubstring$42.toString());
        }
        if (!(endIndex2 <= $this$commonSubstring.getData$okio().length)) {
            boolean bl = false;
            String string = "endIndex > length(" + $this$commonSubstring.getData$okio().length + ')';
            throw new IllegalArgumentException(string.toString());
        }
        int subLen = endIndex2 - beginIndex;
        if (!(subLen >= 0)) {
            boolean bl = false;
            String string = "endIndex < beginIndex";
            throw new IllegalArgumentException(string.toString());
        }
        if (beginIndex == 0 && endIndex2 == $this$commonSubstring.getData$okio().length) {
            return $this$commonSubstring;
        }
        byte[] byArray = $this$commonSubstring.getData$okio();
        return new ByteString(ArraysKt.copyOfRange(byArray, beginIndex, endIndex2));
    }

    public static final byte commonGetByte(@NotNull ByteString $this$commonGetByte, int pos) {
        Intrinsics.checkNotNullParameter($this$commonGetByte, "<this>");
        boolean $i$f$commonGetByte = false;
        return $this$commonGetByte.getData$okio()[pos];
    }

    public static final int commonGetSize(@NotNull ByteString $this$commonGetSize) {
        Intrinsics.checkNotNullParameter($this$commonGetSize, "<this>");
        boolean $i$f$commonGetSize = false;
        return $this$commonGetSize.getData$okio().length;
    }

    @NotNull
    public static final byte[] commonToByteArray(@NotNull ByteString $this$commonToByteArray) {
        Intrinsics.checkNotNullParameter($this$commonToByteArray, "<this>");
        boolean $i$f$commonToByteArray = false;
        byte[] byArray = $this$commonToByteArray.getData$okio();
        byte[] byArray2 = Arrays.copyOf(byArray, byArray.length);
        Intrinsics.checkNotNullExpressionValue(byArray2, "copyOf(this, size)");
        return byArray2;
    }

    @NotNull
    public static final byte[] commonInternalArray(@NotNull ByteString $this$commonInternalArray) {
        Intrinsics.checkNotNullParameter($this$commonInternalArray, "<this>");
        boolean $i$f$commonInternalArray = false;
        return $this$commonInternalArray.getData$okio();
    }

    public static final boolean commonRangeEquals(@NotNull ByteString $this$commonRangeEquals, int offset, @NotNull ByteString other, int otherOffset, int byteCount) {
        Intrinsics.checkNotNullParameter($this$commonRangeEquals, "<this>");
        Intrinsics.checkNotNullParameter(other, "other");
        boolean $i$f$commonRangeEquals = false;
        return other.rangeEquals(otherOffset, $this$commonRangeEquals.getData$okio(), offset, byteCount);
    }

    public static final boolean commonRangeEquals(@NotNull ByteString $this$commonRangeEquals, int offset, @NotNull byte[] other, int otherOffset, int byteCount) {
        Intrinsics.checkNotNullParameter($this$commonRangeEquals, "<this>");
        Intrinsics.checkNotNullParameter(other, "other");
        boolean $i$f$commonRangeEquals = false;
        return offset >= 0 && offset <= $this$commonRangeEquals.getData$okio().length - byteCount && otherOffset >= 0 && otherOffset <= other.length - byteCount && -SegmentedByteString.arrayRangeEquals($this$commonRangeEquals.getData$okio(), offset, other, otherOffset, byteCount);
    }

    public static final void commonCopyInto(@NotNull ByteString $this$commonCopyInto, int offset, @NotNull byte[] target, int targetOffset, int byteCount) {
        Intrinsics.checkNotNullParameter($this$commonCopyInto, "<this>");
        Intrinsics.checkNotNullParameter(target, "target");
        boolean $i$f$commonCopyInto = false;
        ArraysKt.copyInto($this$commonCopyInto.getData$okio(), target, targetOffset, offset, offset + byteCount);
    }

    public static final boolean commonStartsWith(@NotNull ByteString $this$commonStartsWith, @NotNull ByteString prefix) {
        Intrinsics.checkNotNullParameter($this$commonStartsWith, "<this>");
        Intrinsics.checkNotNullParameter(prefix, "prefix");
        boolean $i$f$commonStartsWith = false;
        return $this$commonStartsWith.rangeEquals(0, prefix, 0, prefix.size());
    }

    public static final boolean commonStartsWith(@NotNull ByteString $this$commonStartsWith, @NotNull byte[] prefix) {
        Intrinsics.checkNotNullParameter($this$commonStartsWith, "<this>");
        Intrinsics.checkNotNullParameter(prefix, "prefix");
        boolean $i$f$commonStartsWith = false;
        return $this$commonStartsWith.rangeEquals(0, prefix, 0, prefix.length);
    }

    public static final boolean commonEndsWith(@NotNull ByteString $this$commonEndsWith, @NotNull ByteString suffix) {
        Intrinsics.checkNotNullParameter($this$commonEndsWith, "<this>");
        Intrinsics.checkNotNullParameter(suffix, "suffix");
        boolean $i$f$commonEndsWith = false;
        return $this$commonEndsWith.rangeEquals($this$commonEndsWith.size() - suffix.size(), suffix, 0, suffix.size());
    }

    public static final boolean commonEndsWith(@NotNull ByteString $this$commonEndsWith, @NotNull byte[] suffix) {
        Intrinsics.checkNotNullParameter($this$commonEndsWith, "<this>");
        Intrinsics.checkNotNullParameter(suffix, "suffix");
        boolean $i$f$commonEndsWith = false;
        return $this$commonEndsWith.rangeEquals($this$commonEndsWith.size() - suffix.length, suffix, 0, suffix.length);
    }

    public static final int commonIndexOf(@NotNull ByteString $this$commonIndexOf, @NotNull byte[] other, int fromIndex) {
        Intrinsics.checkNotNullParameter($this$commonIndexOf, "<this>");
        Intrinsics.checkNotNullParameter(other, "other");
        boolean $i$f$commonIndexOf = false;
        int limit = $this$commonIndexOf.getData$okio().length - other.length;
        int i = Math.max(fromIndex, 0);
        if (i <= limit) {
            while (true) {
                if (-SegmentedByteString.arrayRangeEquals($this$commonIndexOf.getData$okio(), i, other, 0, other.length)) {
                    return i;
                }
                if (i == limit) break;
                ++i;
            }
        }
        return -1;
    }

    public static final int commonLastIndexOf(@NotNull ByteString $this$commonLastIndexOf, @NotNull ByteString other, int fromIndex) {
        Intrinsics.checkNotNullParameter($this$commonLastIndexOf, "<this>");
        Intrinsics.checkNotNullParameter(other, "other");
        boolean $i$f$commonLastIndexOf = false;
        return $this$commonLastIndexOf.lastIndexOf(other.internalArray$okio(), fromIndex);
    }

    public static final int commonLastIndexOf(@NotNull ByteString $this$commonLastIndexOf, @NotNull byte[] other, int fromIndex) {
        Intrinsics.checkNotNullParameter($this$commonLastIndexOf, "<this>");
        Intrinsics.checkNotNullParameter(other, "other");
        boolean $i$f$commonLastIndexOf = false;
        int fromIndex2 = -SegmentedByteString.resolveDefaultParameter($this$commonLastIndexOf, fromIndex);
        int limit = $this$commonLastIndexOf.getData$okio().length - other.length;
        for (int i = Math.min(fromIndex2, limit); -1 < i; --i) {
            if (!-SegmentedByteString.arrayRangeEquals($this$commonLastIndexOf.getData$okio(), i, other, 0, other.length)) continue;
            return i;
        }
        return -1;
    }

    public static final boolean commonEquals(@NotNull ByteString $this$commonEquals, @Nullable Object other) {
        Intrinsics.checkNotNullParameter($this$commonEquals, "<this>");
        boolean $i$f$commonEquals = false;
        return other == $this$commonEquals ? true : (other instanceof ByteString ? ((ByteString)other).size() == $this$commonEquals.getData$okio().length && ((ByteString)other).rangeEquals(0, $this$commonEquals.getData$okio(), 0, $this$commonEquals.getData$okio().length) : false);
    }

    public static final int commonHashCode(@NotNull ByteString $this$commonHashCode) {
        int n;
        Intrinsics.checkNotNullParameter($this$commonHashCode, "<this>");
        boolean $i$f$commonHashCode = false;
        int result = $this$commonHashCode.getHashCode$okio();
        if (result != 0) {
            return result;
        }
        int it = n = Arrays.hashCode($this$commonHashCode.getData$okio());
        boolean bl = false;
        $this$commonHashCode.setHashCode$okio(it);
        return n;
    }

    /*
     * WARNING - void declaration
     */
    public static final int commonCompareTo(@NotNull ByteString $this$commonCompareTo, @NotNull ByteString other) {
        Intrinsics.checkNotNullParameter($this$commonCompareTo, "<this>");
        Intrinsics.checkNotNullParameter(other, "other");
        boolean $i$f$commonCompareTo = false;
        int sizeA = $this$commonCompareTo.size();
        int sizeB = other.size();
        int size = Math.min(sizeA, sizeB);
        for (int i = 0; i < size; ++i) {
            void $this$and$iv;
            void $this$and$iv2;
            byte by = $this$commonCompareTo.getByte(i);
            int other$iv = 255;
            boolean $i$f$and = false;
            int byteA = $this$and$iv2 & other$iv;
            other$iv = other.getByte(i);
            int other$iv2 = 255;
            boolean $i$f$and2 = false;
            int byteB = $this$and$iv & other$iv2;
            if (byteA == byteB) {
                continue;
            }
            return byteA < byteB ? -1 : 1;
        }
        if (sizeA == sizeB) {
            return 0;
        }
        return sizeA < sizeB ? -1 : 1;
    }

    @NotNull
    public static final ByteString commonOf(@NotNull byte[] data) {
        Intrinsics.checkNotNullParameter(data, "data");
        boolean $i$f$commonOf = false;
        byte[] byArray = Arrays.copyOf(data, data.length);
        Intrinsics.checkNotNullExpressionValue(byArray, "copyOf(this, size)");
        return new ByteString(byArray);
    }

    @NotNull
    public static final ByteString commonToByteString(@NotNull byte[] $this$commonToByteString, int offset, int byteCount) {
        Intrinsics.checkNotNullParameter($this$commonToByteString, "<this>");
        boolean $i$f$commonToByteString = false;
        int byteCount2 = -SegmentedByteString.resolveDefaultParameter($this$commonToByteString, byteCount);
        -SegmentedByteString.checkOffsetAndCount($this$commonToByteString.length, offset, byteCount2);
        byte[] byArray = $this$commonToByteString;
        int n = offset + byteCount2;
        return new ByteString(ArraysKt.copyOfRange(byArray, offset, n));
    }

    @NotNull
    public static final ByteString commonEncodeUtf8(@NotNull String $this$commonEncodeUtf8) {
        Intrinsics.checkNotNullParameter($this$commonEncodeUtf8, "<this>");
        boolean $i$f$commonEncodeUtf8 = false;
        ByteString byteString = new ByteString(_JvmPlatformKt.asUtf8ToByteArray($this$commonEncodeUtf8));
        byteString.setUtf8$okio($this$commonEncodeUtf8);
        return byteString;
    }

    @Nullable
    public static final ByteString commonDecodeBase64(@NotNull String $this$commonDecodeBase64) {
        Intrinsics.checkNotNullParameter($this$commonDecodeBase64, "<this>");
        boolean $i$f$commonDecodeBase64 = false;
        byte[] decoded = -Base64.decodeBase64ToArray($this$commonDecodeBase64);
        return decoded != null ? new ByteString(decoded) : null;
    }

    @NotNull
    public static final ByteString commonDecodeHex(@NotNull String $this$commonDecodeHex) {
        Intrinsics.checkNotNullParameter($this$commonDecodeHex, "<this>");
        boolean $i$f$commonDecodeHex = false;
        if (!($this$commonDecodeHex.length() % 2 == 0)) {
            boolean $i$a$-require--ByteString$commonDecodeHex$22 = false;
            String $i$a$-require--ByteString$commonDecodeHex$22 = "Unexpected hex string: " + $this$commonDecodeHex;
            throw new IllegalArgumentException($i$a$-require--ByteString$commonDecodeHex$22.toString());
        }
        byte[] result = new byte[$this$commonDecodeHex.length() / 2];
        int n = result.length;
        for (int i = 0; i < n; ++i) {
            int d1 = -ByteString.decodeHexDigit($this$commonDecodeHex.charAt(i * 2)) << 4;
            int d2 = -ByteString.decodeHexDigit($this$commonDecodeHex.charAt(i * 2 + 1));
            result[i] = (byte)(d1 + d2);
        }
        return new ByteString(result);
    }

    public static final void commonWrite(@NotNull ByteString $this$commonWrite, @NotNull Buffer buffer, int offset, int byteCount) {
        Intrinsics.checkNotNullParameter($this$commonWrite, "<this>");
        Intrinsics.checkNotNullParameter(buffer, "buffer");
        buffer.write($this$commonWrite.getData$okio(), offset, byteCount);
    }

    private static final int decodeHexDigit(char c) {
        int n;
        char c2 = c;
        boolean bl = '0' <= c2 ? c2 < ':' : false;
        if (bl) {
            n = c - 48;
        } else {
            boolean bl2 = 'a' <= c2 ? c2 < 'g' : false;
            if (bl2) {
                n = c - 97 + 10;
            } else {
                boolean bl3 = 'A' <= c2 ? c2 < 'G' : false;
                if (bl3) {
                    n = c - 65 + 10;
                } else {
                    throw new IllegalArgumentException("Unexpected hex digit: " + c);
                }
            }
        }
        return n;
    }

    /*
     * WARNING - void declaration
     */
    @NotNull
    public static final String commonToString(@NotNull ByteString $this$commonToString) {
        Intrinsics.checkNotNullParameter($this$commonToString, "<this>");
        boolean $i$f$commonToString = false;
        if ($this$commonToString.getData$okio().length == 0) {
            return "[size=0]";
        }
        int i = -ByteString.codePointIndexToCharIndex($this$commonToString.getData$okio(), 64);
        if (i == -1) {
            String string;
            if ($this$commonToString.getData$okio().length <= 64) {
                string = "[hex=" + $this$commonToString.hex() + ']';
            } else {
                ByteString byteString;
                void beginIndex$iv;
                void $this$commonSubstring$iv;
                StringBuilder stringBuilder = new StringBuilder().append("[size=").append($this$commonToString.getData$okio().length).append(" hex=");
                ByteString byteString2 = $this$commonToString;
                boolean bl = false;
                int endIndex$iv = 64;
                boolean $i$f$commonSubstring = false;
                int endIndex$iv2 = -SegmentedByteString.resolveDefaultParameter((ByteString)$this$commonSubstring$iv, endIndex$iv);
                if (!(endIndex$iv2 <= $this$commonSubstring$iv.getData$okio().length)) {
                    boolean bl2 = false;
                    String string2 = "endIndex > length(" + $this$commonSubstring$iv.getData$okio().length + ')';
                    throw new IllegalArgumentException(string2.toString());
                }
                int subLen$iv = endIndex$iv2 - beginIndex$iv;
                if (!(subLen$iv >= 0)) {
                    boolean bl3 = false;
                    String string3 = "endIndex < beginIndex";
                    throw new IllegalArgumentException(string3.toString());
                }
                if (endIndex$iv2 == $this$commonSubstring$iv.getData$okio().length) {
                    byteString = $this$commonSubstring$iv;
                } else {
                    byte[] byArray = $this$commonSubstring$iv.getData$okio();
                    ByteString byteString3 = new ByteString(ArraysKt.copyOfRange(byArray, (int)beginIndex$iv, endIndex$iv2));
                    byteString = byteString3;
                }
                string = stringBuilder.append(byteString.hex()).append("\u2026]").toString();
            }
            return string;
        }
        String text = $this$commonToString.utf8();
        String string = text.substring(0, i);
        Intrinsics.checkNotNullExpressionValue(string, "this as java.lang.String\u2026ing(startIndex, endIndex)");
        String safeText = StringsKt.replace$default(StringsKt.replace$default(StringsKt.replace$default(string, "\\", "\\\\", false, 4, null), "\n", "\\n", false, 4, null), "\r", "\\r", false, 4, null);
        return i < text.length() ? "[size=" + $this$commonToString.getData$okio().length + " text=" + safeText + "\u2026]" : "[text=" + safeText + ']';
    }

    /*
     * Unable to fully structure code
     */
    private static final int codePointIndexToCharIndex(byte[] s, int codePointCount) {
        charCount = 0;
        j = 0;
        var4_4 = s;
        var5_5 = false;
        endIndex$iv = s.length;
        $i$f$processUtf8CodePoints = false;
        index$iv = beginIndex$iv;
        while (index$iv < endIndex$iv) {
            block221: {
                block220: {
                    block219: {
                        block183: {
                            block190: {
                                block207: {
                                    block218: {
                                        block217: {
                                            block216: {
                                                block212: {
                                                    block215: {
                                                        block214: {
                                                            block213: {
                                                                block208: {
                                                                    block211: {
                                                                        block210: {
                                                                            block209: {
                                                                                block203: {
                                                                                    block206: {
                                                                                        block205: {
                                                                                            block204: {
                                                                                                block199: {
                                                                                                    block202: {
                                                                                                        block201: {
                                                                                                            block200: {
                                                                                                                block195: {
                                                                                                                    block198: {
                                                                                                                        block197: {
                                                                                                                            block196: {
                                                                                                                                block191: {
                                                                                                                                    block194: {
                                                                                                                                        block193: {
                                                                                                                                            block192: {
                                                                                                                                                block184: {
                                                                                                                                                    block189: {
                                                                                                                                                        block188: {
                                                                                                                                                            block187: {
                                                                                                                                                                block186: {
                                                                                                                                                                    block185: {
                                                                                                                                                                        block157: {
                                                                                                                                                                            block162: {
                                                                                                                                                                                block175: {
                                                                                                                                                                                    block182: {
                                                                                                                                                                                        block181: {
                                                                                                                                                                                            block180: {
                                                                                                                                                                                                block176: {
                                                                                                                                                                                                    block179: {
                                                                                                                                                                                                        block178: {
                                                                                                                                                                                                            block177: {
                                                                                                                                                                                                                block171: {
                                                                                                                                                                                                                    block174: {
                                                                                                                                                                                                                        block173: {
                                                                                                                                                                                                                            block172: {
                                                                                                                                                                                                                                block167: {
                                                                                                                                                                                                                                    block170: {
                                                                                                                                                                                                                                        block169: {
                                                                                                                                                                                                                                            block168: {
                                                                                                                                                                                                                                                block163: {
                                                                                                                                                                                                                                                    block166: {
                                                                                                                                                                                                                                                        block165: {
                                                                                                                                                                                                                                                            block164: {
                                                                                                                                                                                                                                                                block158: {
                                                                                                                                                                                                                                                                    block161: {
                                                                                                                                                                                                                                                                        block160: {
                                                                                                                                                                                                                                                                            block159: {
                                                                                                                                                                                                                                                                                block139: {
                                                                                                                                                                                                                                                                                    block144: {
                                                                                                                                                                                                                                                                                        block153: {
                                                                                                                                                                                                                                                                                            block156: {
                                                                                                                                                                                                                                                                                                block155: {
                                                                                                                                                                                                                                                                                                    block154: {
                                                                                                                                                                                                                                                                                                        block149: {
                                                                                                                                                                                                                                                                                                            block152: {
                                                                                                                                                                                                                                                                                                                block151: {
                                                                                                                                                                                                                                                                                                                    block150: {
                                                                                                                                                                                                                                                                                                                        block145: {
                                                                                                                                                                                                                                                                                                                            block148: {
                                                                                                                                                                                                                                                                                                                                block147: {
                                                                                                                                                                                                                                                                                                                                    block146: {
                                                                                                                                                                                                                                                                                                                                        block140: {
                                                                                                                                                                                                                                                                                                                                            block143: {
                                                                                                                                                                                                                                                                                                                                                block142: {
                                                                                                                                                                                                                                                                                                                                                    block141: {
                                                                                                                                                                                                                                                                                                                                                        block132: {
                                                                                                                                                                                                                                                                                                                                                            block135: {
                                                                                                                                                                                                                                                                                                                                                                block134: {
                                                                                                                                                                                                                                                                                                                                                                    block133: {
                                                                                                                                                                                                                                                                                                                                                                        b0$iv = $this$processUtf8CodePoints$iv[index$iv];
                                                                                                                                                                                                                                                                                                                                                                        if (b0$iv < 0) break block132;
                                                                                                                                                                                                                                                                                                                                                                        c = b0$iv;
                                                                                                                                                                                                                                                                                                                                                                        $i$a$-processUtf8CodePoints--ByteString$codePointIndexToCharIndex$1 = false;
                                                                                                                                                                                                                                                                                                                                                                        var12_12 = j;
                                                                                                                                                                                                                                                                                                                                                                        j = var12_12 + 1;
                                                                                                                                                                                                                                                                                                                                                                        if (var12_12 == codePointCount) {
                                                                                                                                                                                                                                                                                                                                                                            return charCount;
                                                                                                                                                                                                                                                                                                                                                                        }
                                                                                                                                                                                                                                                                                                                                                                        if (c == 10 || c == 13) break block133;
                                                                                                                                                                                                                                                                                                                                                                        $i$f$isIsoControl = 0;
                                                                                                                                                                                                                                                                                                                                                                        if (0 <= c ? c < 32 : false) ** GOTO lbl-1000
                                                                                                                                                                                                                                                                                                                                                                        v0 = 127 <= c ? c < 160 : false;
                                                                                                                                                                                                                                                                                                                                                                        if (v0) lbl-1000:
                                                                                                                                                                                                                                                                                                                                                                        // 2 sources

                                                                                                                                                                                                                                                                                                                                                                        {
                                                                                                                                                                                                                                                                                                                                                                            v1 = true;
                                                                                                                                                                                                                                                                                                                                                                        } else {
                                                                                                                                                                                                                                                                                                                                                                            v1 = false;
                                                                                                                                                                                                                                                                                                                                                                        }
                                                                                                                                                                                                                                                                                                                                                                        if (v1) break block134;
                                                                                                                                                                                                                                                                                                                                                                    }
                                                                                                                                                                                                                                                                                                                                                                    if (c != 65533) break block135;
                                                                                                                                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                                                                                                                                return -1;
                                                                                                                                                                                                                                                                                                                                                            }
                                                                                                                                                                                                                                                                                                                                                            charCount += c < 65536 ? 1 : 2;
                                                                                                                                                                                                                                                                                                                                                            ++index$iv;
                                                                                                                                                                                                                                                                                                                                                            while (index$iv < endIndex$iv && $this$processUtf8CodePoints$iv[index$iv] >= 0) {
                                                                                                                                                                                                                                                                                                                                                                block138: {
                                                                                                                                                                                                                                                                                                                                                                    block137: {
                                                                                                                                                                                                                                                                                                                                                                        block136: {
                                                                                                                                                                                                                                                                                                                                                                            c = $this$processUtf8CodePoints$iv[index$iv++];
                                                                                                                                                                                                                                                                                                                                                                            $i$a$-processUtf8CodePoints--ByteString$codePointIndexToCharIndex$1 = false;
                                                                                                                                                                                                                                                                                                                                                                            $i$f$isIsoControl = j;
                                                                                                                                                                                                                                                                                                                                                                            j = $i$f$isIsoControl + 1;
                                                                                                                                                                                                                                                                                                                                                                            if ($i$f$isIsoControl == codePointCount) {
                                                                                                                                                                                                                                                                                                                                                                                return charCount;
                                                                                                                                                                                                                                                                                                                                                                            }
                                                                                                                                                                                                                                                                                                                                                                            if (c == 10 || c == 13) break block136;
                                                                                                                                                                                                                                                                                                                                                                            $i$f$isIsoControl = 0;
                                                                                                                                                                                                                                                                                                                                                                            if (0 <= c ? c < 32 : false) ** GOTO lbl-1000
                                                                                                                                                                                                                                                                                                                                                                            v2 = 127 <= c ? c < 160 : false;
                                                                                                                                                                                                                                                                                                                                                                            if (v2) lbl-1000:
                                                                                                                                                                                                                                                                                                                                                                            // 2 sources

                                                                                                                                                                                                                                                                                                                                                                            {
                                                                                                                                                                                                                                                                                                                                                                                v3 = true;
                                                                                                                                                                                                                                                                                                                                                                            } else {
                                                                                                                                                                                                                                                                                                                                                                                v3 = false;
                                                                                                                                                                                                                                                                                                                                                                            }
                                                                                                                                                                                                                                                                                                                                                                            if (v3) break block137;
                                                                                                                                                                                                                                                                                                                                                                        }
                                                                                                                                                                                                                                                                                                                                                                        if (c != 65533) break block138;
                                                                                                                                                                                                                                                                                                                                                                    }
                                                                                                                                                                                                                                                                                                                                                                    return -1;
                                                                                                                                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                                                                                                                                charCount += c < 65536 ? 1 : 2;
                                                                                                                                                                                                                                                                                                                                                            }
                                                                                                                                                                                                                                                                                                                                                            continue;
                                                                                                                                                                                                                                                                                                                                                        }
                                                                                                                                                                                                                                                                                                                                                        var13_13 = b0$iv;
                                                                                                                                                                                                                                                                                                                                                        other$iv$iv = 5;
                                                                                                                                                                                                                                                                                                                                                        $i$f$shr = 0;
                                                                                                                                                                                                                                                                                                                                                        if ($this$shr$iv$iv >> other$iv$iv != -2) break block139;
                                                                                                                                                                                                                                                                                                                                                        $this$process2Utf8Bytes$iv$iv = $this$processUtf8CodePoints$iv;
                                                                                                                                                                                                                                                                                                                                                        $i$f$process2Utf8Bytes = false;
                                                                                                                                                                                                                                                                                                                                                        if (endIndex$iv > index$iv + true) break block140;
                                                                                                                                                                                                                                                                                                                                                        $i$f$shr = 65533;
                                                                                                                                                                                                                                                                                                                                                        var16_19 = index$iv;
                                                                                                                                                                                                                                                                                                                                                        $i$a$-process2Utf8Bytes-Utf8$processUtf8CodePoints$1$iv = false;
                                                                                                                                                                                                                                                                                                                                                        c = it$iv;
                                                                                                                                                                                                                                                                                                                                                        $i$a$-processUtf8CodePoints--ByteString$codePointIndexToCharIndex$1 = false;
                                                                                                                                                                                                                                                                                                                                                        $i$f$isIsoControl = j;
                                                                                                                                                                                                                                                                                                                                                        j = $i$f$isIsoControl + 1;
                                                                                                                                                                                                                                                                                                                                                        if ($i$f$isIsoControl == codePointCount) {
                                                                                                                                                                                                                                                                                                                                                            return charCount;
                                                                                                                                                                                                                                                                                                                                                        }
                                                                                                                                                                                                                                                                                                                                                        if (c == 10 || c == 13) break block141;
                                                                                                                                                                                                                                                                                                                                                        $i$f$isIsoControl = 0;
                                                                                                                                                                                                                                                                                                                                                        if (0 <= c ? c < 32 : false) ** GOTO lbl-1000
                                                                                                                                                                                                                                                                                                                                                        v4 = 127 <= c ? c < 160 : false;
                                                                                                                                                                                                                                                                                                                                                        if (v4) lbl-1000:
                                                                                                                                                                                                                                                                                                                                                        // 2 sources

                                                                                                                                                                                                                                                                                                                                                        {
                                                                                                                                                                                                                                                                                                                                                            v5 = true;
                                                                                                                                                                                                                                                                                                                                                        } else {
                                                                                                                                                                                                                                                                                                                                                            v5 = false;
                                                                                                                                                                                                                                                                                                                                                        }
                                                                                                                                                                                                                                                                                                                                                        if (v5) break block142;
                                                                                                                                                                                                                                                                                                                                                    }
                                                                                                                                                                                                                                                                                                                                                    if (c != 65533) break block143;
                                                                                                                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                                                                                                                return -1;
                                                                                                                                                                                                                                                                                                                                            }
                                                                                                                                                                                                                                                                                                                                            charCount += c < 65536 ? 1 : 2;
                                                                                                                                                                                                                                                                                                                                            var18_21 = Unit.INSTANCE;
                                                                                                                                                                                                                                                                                                                                            v6 = var16_19;
                                                                                                                                                                                                                                                                                                                                            v7 = 1;
                                                                                                                                                                                                                                                                                                                                            break block144;
                                                                                                                                                                                                                                                                                                                                        }
                                                                                                                                                                                                                                                                                                                                        b0$iv$iv = $this$process2Utf8Bytes$iv$iv[index$iv];
                                                                                                                                                                                                                                                                                                                                        b1$iv$iv = $this$process2Utf8Bytes$iv$iv[index$iv + true];
                                                                                                                                                                                                                                                                                                                                        $i$f$isUtf8Continuation = false;
                                                                                                                                                                                                                                                                                                                                        var22_25 = b1$iv$iv;
                                                                                                                                                                                                                                                                                                                                        other$iv$iv$iv$iv = 192;
                                                                                                                                                                                                                                                                                                                                        $i$f$and = false;
                                                                                                                                                                                                                                                                                                                                        if (($this$and$iv$iv$iv$iv & other$iv$iv$iv$iv) == 128) break block145;
                                                                                                                                                                                                                                                                                                                                        it$iv = 65533;
                                                                                                                                                                                                                                                                                                                                        $i$a$-process2Utf8Bytes-Utf8$processUtf8CodePoints$1$iv = false;
                                                                                                                                                                                                                                                                                                                                        c = it$iv;
                                                                                                                                                                                                                                                                                                                                        $i$a$-processUtf8CodePoints--ByteString$codePointIndexToCharIndex$1 = false;
                                                                                                                                                                                                                                                                                                                                        $i$f$isIsoControl = j;
                                                                                                                                                                                                                                                                                                                                        j = $i$f$isIsoControl + 1;
                                                                                                                                                                                                                                                                                                                                        if ($i$f$isIsoControl == codePointCount) {
                                                                                                                                                                                                                                                                                                                                            return charCount;
                                                                                                                                                                                                                                                                                                                                        }
                                                                                                                                                                                                                                                                                                                                        if (c == 10 || c == 13) break block146;
                                                                                                                                                                                                                                                                                                                                        $i$f$isIsoControl = 0;
                                                                                                                                                                                                                                                                                                                                        if (0 <= c ? c < 32 : false) ** GOTO lbl-1000
                                                                                                                                                                                                                                                                                                                                        v8 = 127 <= c ? c < 160 : false;
                                                                                                                                                                                                                                                                                                                                        if (v8) lbl-1000:
                                                                                                                                                                                                                                                                                                                                        // 2 sources

                                                                                                                                                                                                                                                                                                                                        {
                                                                                                                                                                                                                                                                                                                                            v9 = true;
                                                                                                                                                                                                                                                                                                                                        } else {
                                                                                                                                                                                                                                                                                                                                            v9 = false;
                                                                                                                                                                                                                                                                                                                                        }
                                                                                                                                                                                                                                                                                                                                        if (v9) break block147;
                                                                                                                                                                                                                                                                                                                                    }
                                                                                                                                                                                                                                                                                                                                    if (c != 65533) break block148;
                                                                                                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                                                                                                return -1;
                                                                                                                                                                                                                                                                                                                            }
                                                                                                                                                                                                                                                                                                                            charCount += c < 65536 ? 1 : 2;
                                                                                                                                                                                                                                                                                                                            var18_21 = Unit.INSTANCE;
                                                                                                                                                                                                                                                                                                                            v6 = var16_19;
                                                                                                                                                                                                                                                                                                                            v7 = 1;
                                                                                                                                                                                                                                                                                                                            break block144;
                                                                                                                                                                                                                                                                                                                        }
                                                                                                                                                                                                                                                                                                                        codePoint$iv$iv = 3968 ^ b1$iv$iv ^ b0$iv$iv << 6;
                                                                                                                                                                                                                                                                                                                        if (codePoint$iv$iv >= 128) break block149;
                                                                                                                                                                                                                                                                                                                        it$iv = 65533;
                                                                                                                                                                                                                                                                                                                        $i$a$-process2Utf8Bytes-Utf8$processUtf8CodePoints$1$iv = false;
                                                                                                                                                                                                                                                                                                                        c = it$iv;
                                                                                                                                                                                                                                                                                                                        $i$a$-processUtf8CodePoints--ByteString$codePointIndexToCharIndex$1 = false;
                                                                                                                                                                                                                                                                                                                        $i$f$isIsoControl = j;
                                                                                                                                                                                                                                                                                                                        j = $i$f$isIsoControl + 1;
                                                                                                                                                                                                                                                                                                                        if ($i$f$isIsoControl == codePointCount) {
                                                                                                                                                                                                                                                                                                                            return charCount;
                                                                                                                                                                                                                                                                                                                        }
                                                                                                                                                                                                                                                                                                                        if (c == 10 || c == 13) break block150;
                                                                                                                                                                                                                                                                                                                        $i$f$isIsoControl = 0;
                                                                                                                                                                                                                                                                                                                        if (0 <= c ? c < 32 : false) ** GOTO lbl-1000
                                                                                                                                                                                                                                                                                                                        v10 = 127 <= c ? c < 160 : false;
                                                                                                                                                                                                                                                                                                                        if (v10) lbl-1000:
                                                                                                                                                                                                                                                                                                                        // 2 sources

                                                                                                                                                                                                                                                                                                                        {
                                                                                                                                                                                                                                                                                                                            v11 = true;
                                                                                                                                                                                                                                                                                                                        } else {
                                                                                                                                                                                                                                                                                                                            v11 = false;
                                                                                                                                                                                                                                                                                                                        }
                                                                                                                                                                                                                                                                                                                        if (v11) break block151;
                                                                                                                                                                                                                                                                                                                    }
                                                                                                                                                                                                                                                                                                                    if (c != 65533) break block152;
                                                                                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                                                                                return -1;
                                                                                                                                                                                                                                                                                                            }
                                                                                                                                                                                                                                                                                                            charCount += c < 65536 ? 1 : 2;
                                                                                                                                                                                                                                                                                                            var18_21 = Unit.INSTANCE;
                                                                                                                                                                                                                                                                                                            v6 = var16_19;
                                                                                                                                                                                                                                                                                                            break block153;
                                                                                                                                                                                                                                                                                                        }
                                                                                                                                                                                                                                                                                                        it$iv = codePoint$iv$iv;
                                                                                                                                                                                                                                                                                                        $i$a$-process2Utf8Bytes-Utf8$processUtf8CodePoints$1$iv = false;
                                                                                                                                                                                                                                                                                                        c = it$iv;
                                                                                                                                                                                                                                                                                                        $i$a$-processUtf8CodePoints--ByteString$codePointIndexToCharIndex$1 = false;
                                                                                                                                                                                                                                                                                                        $i$f$isIsoControl = j;
                                                                                                                                                                                                                                                                                                        j = $i$f$isIsoControl + 1;
                                                                                                                                                                                                                                                                                                        if ($i$f$isIsoControl == codePointCount) {
                                                                                                                                                                                                                                                                                                            return charCount;
                                                                                                                                                                                                                                                                                                        }
                                                                                                                                                                                                                                                                                                        if (c == 10 || c == 13) break block154;
                                                                                                                                                                                                                                                                                                        $i$f$isIsoControl = 0;
                                                                                                                                                                                                                                                                                                        if (0 <= c ? c < 32 : false) ** GOTO lbl-1000
                                                                                                                                                                                                                                                                                                        v12 = 127 <= c ? c < 160 : false;
                                                                                                                                                                                                                                                                                                        if (v12) lbl-1000:
                                                                                                                                                                                                                                                                                                        // 2 sources

                                                                                                                                                                                                                                                                                                        {
                                                                                                                                                                                                                                                                                                            v13 = true;
                                                                                                                                                                                                                                                                                                        } else {
                                                                                                                                                                                                                                                                                                            v13 = false;
                                                                                                                                                                                                                                                                                                        }
                                                                                                                                                                                                                                                                                                        if (v13) break block155;
                                                                                                                                                                                                                                                                                                    }
                                                                                                                                                                                                                                                                                                    if (c != 65533) break block156;
                                                                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                                                                return -1;
                                                                                                                                                                                                                                                                                            }
                                                                                                                                                                                                                                                                                            charCount += c < 65536 ? 1 : 2;
                                                                                                                                                                                                                                                                                            var18_21 = Unit.INSTANCE;
                                                                                                                                                                                                                                                                                            v6 = var16_19;
                                                                                                                                                                                                                                                                                        }
                                                                                                                                                                                                                                                                                        v7 = 2;
                                                                                                                                                                                                                                                                                    }
                                                                                                                                                                                                                                                                                    index$iv = v6 + v7;
                                                                                                                                                                                                                                                                                    continue;
                                                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                                                $this$process2Utf8Bytes$iv$iv = b0$iv;
                                                                                                                                                                                                                                                                                other$iv$iv = 4;
                                                                                                                                                                                                                                                                                $i$f$shr = 0;
                                                                                                                                                                                                                                                                                if ($this$shr$iv$iv >> other$iv$iv != -2) break block157;
                                                                                                                                                                                                                                                                                $this$process3Utf8Bytes$iv$iv = $this$processUtf8CodePoints$iv;
                                                                                                                                                                                                                                                                                $i$f$process3Utf8Bytes = false;
                                                                                                                                                                                                                                                                                if (endIndex$iv > index$iv + 2) break block158;
                                                                                                                                                                                                                                                                                $i$f$shr = 65533;
                                                                                                                                                                                                                                                                                var16_19 = index$iv;
                                                                                                                                                                                                                                                                                $i$a$-process3Utf8Bytes-Utf8$processUtf8CodePoints$2$iv = false;
                                                                                                                                                                                                                                                                                c = it$iv;
                                                                                                                                                                                                                                                                                $i$a$-processUtf8CodePoints--ByteString$codePointIndexToCharIndex$1 = false;
                                                                                                                                                                                                                                                                                $i$f$isIsoControl = j;
                                                                                                                                                                                                                                                                                j = $i$f$isIsoControl + 1;
                                                                                                                                                                                                                                                                                if ($i$f$isIsoControl == codePointCount) {
                                                                                                                                                                                                                                                                                    return charCount;
                                                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                                                if (c == 10 || c == 13) break block159;
                                                                                                                                                                                                                                                                                $i$f$isIsoControl = 0;
                                                                                                                                                                                                                                                                                if (0 <= c ? c < 32 : false) ** GOTO lbl-1000
                                                                                                                                                                                                                                                                                v14 = 127 <= c ? c < 160 : false;
                                                                                                                                                                                                                                                                                if (v14) lbl-1000:
                                                                                                                                                                                                                                                                                // 2 sources

                                                                                                                                                                                                                                                                                {
                                                                                                                                                                                                                                                                                    v15 = true;
                                                                                                                                                                                                                                                                                } else {
                                                                                                                                                                                                                                                                                    v15 = false;
                                                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                                                if (v15) break block160;
                                                                                                                                                                                                                                                                            }
                                                                                                                                                                                                                                                                            if (c != 65533) break block161;
                                                                                                                                                                                                                                                                        }
                                                                                                                                                                                                                                                                        return -1;
                                                                                                                                                                                                                                                                    }
                                                                                                                                                                                                                                                                    charCount += c < 65536 ? 1 : 2;
                                                                                                                                                                                                                                                                    var18_21 = Unit.INSTANCE;
                                                                                                                                                                                                                                                                    v16 = var16_19;
                                                                                                                                                                                                                                                                    if (endIndex$iv <= index$iv + true) ** GOTO lbl-1000
                                                                                                                                                                                                                                                                    byte$iv$iv$iv = $this$process3Utf8Bytes$iv$iv[index$iv + true];
                                                                                                                                                                                                                                                                    $i$f$isUtf8Continuation = false;
                                                                                                                                                                                                                                                                    codePoint$iv$iv = byte$iv$iv$iv;
                                                                                                                                                                                                                                                                    other$iv$iv$iv$iv = 192;
                                                                                                                                                                                                                                                                    $i$f$and = false;
                                                                                                                                                                                                                                                                    if (!(($this$and$iv$iv$iv$iv & other$iv$iv$iv$iv) == 128)) lbl-1000:
                                                                                                                                                                                                                                                                    // 2 sources

                                                                                                                                                                                                                                                                    {
                                                                                                                                                                                                                                                                        v17 = 1;
                                                                                                                                                                                                                                                                    } else {
                                                                                                                                                                                                                                                                        v17 = 2;
                                                                                                                                                                                                                                                                    }
                                                                                                                                                                                                                                                                    break block162;
                                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                                b0$iv$iv = $this$process3Utf8Bytes$iv$iv[index$iv];
                                                                                                                                                                                                                                                                b1$iv$iv = $this$process3Utf8Bytes$iv$iv[index$iv + true];
                                                                                                                                                                                                                                                                $i$f$isUtf8Continuation = false;
                                                                                                                                                                                                                                                                other$iv$iv$iv$iv = b1$iv$iv;
                                                                                                                                                                                                                                                                other$iv$iv$iv$iv = 192;
                                                                                                                                                                                                                                                                $i$f$and = false;
                                                                                                                                                                                                                                                                if (($this$and$iv$iv$iv$iv & other$iv$iv$iv$iv) == 128) break block163;
                                                                                                                                                                                                                                                                it$iv = 65533;
                                                                                                                                                                                                                                                                $i$a$-process3Utf8Bytes-Utf8$processUtf8CodePoints$2$iv = false;
                                                                                                                                                                                                                                                                c = it$iv;
                                                                                                                                                                                                                                                                $i$a$-processUtf8CodePoints--ByteString$codePointIndexToCharIndex$1 = false;
                                                                                                                                                                                                                                                                $i$f$isIsoControl = j;
                                                                                                                                                                                                                                                                j = $i$f$isIsoControl + 1;
                                                                                                                                                                                                                                                                if ($i$f$isIsoControl == codePointCount) {
                                                                                                                                                                                                                                                                    return charCount;
                                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                                if (c == 10 || c == 13) break block164;
                                                                                                                                                                                                                                                                $i$f$isIsoControl = 0;
                                                                                                                                                                                                                                                                if (0 <= c ? c < 32 : false) ** GOTO lbl-1000
                                                                                                                                                                                                                                                                v18 = 127 <= c ? c < 160 : false;
                                                                                                                                                                                                                                                                if (v18) lbl-1000:
                                                                                                                                                                                                                                                                // 2 sources

                                                                                                                                                                                                                                                                {
                                                                                                                                                                                                                                                                    v19 = true;
                                                                                                                                                                                                                                                                } else {
                                                                                                                                                                                                                                                                    v19 = false;
                                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                                if (v19) break block165;
                                                                                                                                                                                                                                                            }
                                                                                                                                                                                                                                                            if (c != 65533) break block166;
                                                                                                                                                                                                                                                        }
                                                                                                                                                                                                                                                        return -1;
                                                                                                                                                                                                                                                    }
                                                                                                                                                                                                                                                    charCount += c < 65536 ? 1 : 2;
                                                                                                                                                                                                                                                    var18_21 = Unit.INSTANCE;
                                                                                                                                                                                                                                                    v16 = var16_19;
                                                                                                                                                                                                                                                    v17 = 1;
                                                                                                                                                                                                                                                    break block162;
                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                b2$iv$iv = $this$process3Utf8Bytes$iv$iv[index$iv + 2];
                                                                                                                                                                                                                                                $i$f$isUtf8Continuation = false;
                                                                                                                                                                                                                                                other$iv$iv$iv$iv = b2$iv$iv;
                                                                                                                                                                                                                                                other$iv$iv$iv$iv = 192;
                                                                                                                                                                                                                                                $i$f$and = false;
                                                                                                                                                                                                                                                if (($this$and$iv$iv$iv$iv & other$iv$iv$iv$iv) == 128) break block167;
                                                                                                                                                                                                                                                it$iv = 65533;
                                                                                                                                                                                                                                                $i$a$-process3Utf8Bytes-Utf8$processUtf8CodePoints$2$iv = false;
                                                                                                                                                                                                                                                c = it$iv;
                                                                                                                                                                                                                                                $i$a$-processUtf8CodePoints--ByteString$codePointIndexToCharIndex$1 = false;
                                                                                                                                                                                                                                                $i$f$isIsoControl = j;
                                                                                                                                                                                                                                                j = $i$f$isIsoControl + 1;
                                                                                                                                                                                                                                                if ($i$f$isIsoControl == codePointCount) {
                                                                                                                                                                                                                                                    return charCount;
                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                if (c == 10 || c == 13) break block168;
                                                                                                                                                                                                                                                $i$f$isIsoControl = 0;
                                                                                                                                                                                                                                                if (0 <= c ? c < 32 : false) ** GOTO lbl-1000
                                                                                                                                                                                                                                                v20 = 127 <= c ? c < 160 : false;
                                                                                                                                                                                                                                                if (v20) lbl-1000:
                                                                                                                                                                                                                                                // 2 sources

                                                                                                                                                                                                                                                {
                                                                                                                                                                                                                                                    v21 = true;
                                                                                                                                                                                                                                                } else {
                                                                                                                                                                                                                                                    v21 = false;
                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                if (v21) break block169;
                                                                                                                                                                                                                                            }
                                                                                                                                                                                                                                            if (c != 65533) break block170;
                                                                                                                                                                                                                                        }
                                                                                                                                                                                                                                        return -1;
                                                                                                                                                                                                                                    }
                                                                                                                                                                                                                                    charCount += c < 65536 ? 1 : 2;
                                                                                                                                                                                                                                    var18_21 = Unit.INSTANCE;
                                                                                                                                                                                                                                    v16 = var16_19;
                                                                                                                                                                                                                                    v17 = 2;
                                                                                                                                                                                                                                    break block162;
                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                codePoint$iv$iv = -123008 ^ b2$iv$iv ^ b1$iv$iv << 6 ^ b0$iv$iv << 12;
                                                                                                                                                                                                                                if (codePoint$iv$iv >= 2048) break block171;
                                                                                                                                                                                                                                it$iv = 65533;
                                                                                                                                                                                                                                $i$a$-process3Utf8Bytes-Utf8$processUtf8CodePoints$2$iv = false;
                                                                                                                                                                                                                                c = it$iv;
                                                                                                                                                                                                                                $i$a$-processUtf8CodePoints--ByteString$codePointIndexToCharIndex$1 = false;
                                                                                                                                                                                                                                $i$f$isIsoControl = j;
                                                                                                                                                                                                                                j = $i$f$isIsoControl + 1;
                                                                                                                                                                                                                                if ($i$f$isIsoControl == codePointCount) {
                                                                                                                                                                                                                                    return charCount;
                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                if (c == 10 || c == 13) break block172;
                                                                                                                                                                                                                                $i$f$isIsoControl = 0;
                                                                                                                                                                                                                                if (0 <= c ? c < 32 : false) ** GOTO lbl-1000
                                                                                                                                                                                                                                v22 = 127 <= c ? c < 160 : false;
                                                                                                                                                                                                                                if (v22) lbl-1000:
                                                                                                                                                                                                                                // 2 sources

                                                                                                                                                                                                                                {
                                                                                                                                                                                                                                    v23 = true;
                                                                                                                                                                                                                                } else {
                                                                                                                                                                                                                                    v23 = false;
                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                if (v23) break block173;
                                                                                                                                                                                                                            }
                                                                                                                                                                                                                            if (c != 65533) break block174;
                                                                                                                                                                                                                        }
                                                                                                                                                                                                                        return -1;
                                                                                                                                                                                                                    }
                                                                                                                                                                                                                    charCount += c < 65536 ? 1 : 2;
                                                                                                                                                                                                                    var18_21 = Unit.INSTANCE;
                                                                                                                                                                                                                    v16 = var16_19;
                                                                                                                                                                                                                    break block175;
                                                                                                                                                                                                                }
                                                                                                                                                                                                                v24 = 55296 <= codePoint$iv$iv ? codePoint$iv$iv < 57344 : false;
                                                                                                                                                                                                                if (!v24) break block176;
                                                                                                                                                                                                                it$iv = 65533;
                                                                                                                                                                                                                $i$a$-process3Utf8Bytes-Utf8$processUtf8CodePoints$2$iv = false;
                                                                                                                                                                                                                c = it$iv;
                                                                                                                                                                                                                $i$a$-processUtf8CodePoints--ByteString$codePointIndexToCharIndex$1 = false;
                                                                                                                                                                                                                $i$f$isIsoControl = j;
                                                                                                                                                                                                                j = $i$f$isIsoControl + 1;
                                                                                                                                                                                                                if ($i$f$isIsoControl == codePointCount) {
                                                                                                                                                                                                                    return charCount;
                                                                                                                                                                                                                }
                                                                                                                                                                                                                if (c == 10 || c == 13) break block177;
                                                                                                                                                                                                                $i$f$isIsoControl = 0;
                                                                                                                                                                                                                if (0 <= c ? c < 32 : false) ** GOTO lbl-1000
                                                                                                                                                                                                                v25 = 127 <= c ? c < 160 : false;
                                                                                                                                                                                                                if (v25) lbl-1000:
                                                                                                                                                                                                                // 2 sources

                                                                                                                                                                                                                {
                                                                                                                                                                                                                    v26 = true;
                                                                                                                                                                                                                } else {
                                                                                                                                                                                                                    v26 = false;
                                                                                                                                                                                                                }
                                                                                                                                                                                                                if (v26) break block178;
                                                                                                                                                                                                            }
                                                                                                                                                                                                            if (c != 65533) break block179;
                                                                                                                                                                                                        }
                                                                                                                                                                                                        return -1;
                                                                                                                                                                                                    }
                                                                                                                                                                                                    charCount += c < 65536 ? 1 : 2;
                                                                                                                                                                                                    var18_21 = Unit.INSTANCE;
                                                                                                                                                                                                    v16 = var16_19;
                                                                                                                                                                                                    break block175;
                                                                                                                                                                                                }
                                                                                                                                                                                                it$iv = codePoint$iv$iv;
                                                                                                                                                                                                $i$a$-process3Utf8Bytes-Utf8$processUtf8CodePoints$2$iv = false;
                                                                                                                                                                                                c = it$iv;
                                                                                                                                                                                                $i$a$-processUtf8CodePoints--ByteString$codePointIndexToCharIndex$1 = false;
                                                                                                                                                                                                $i$f$isIsoControl = j;
                                                                                                                                                                                                j = $i$f$isIsoControl + 1;
                                                                                                                                                                                                if ($i$f$isIsoControl == codePointCount) {
                                                                                                                                                                                                    return charCount;
                                                                                                                                                                                                }
                                                                                                                                                                                                if (c == 10 || c == 13) break block180;
                                                                                                                                                                                                $i$f$isIsoControl = 0;
                                                                                                                                                                                                if (0 <= c ? c < 32 : false) ** GOTO lbl-1000
                                                                                                                                                                                                v27 = 127 <= c ? c < 160 : false;
                                                                                                                                                                                                if (v27) lbl-1000:
                                                                                                                                                                                                // 2 sources

                                                                                                                                                                                                {
                                                                                                                                                                                                    v28 = true;
                                                                                                                                                                                                } else {
                                                                                                                                                                                                    v28 = false;
                                                                                                                                                                                                }
                                                                                                                                                                                                if (v28) break block181;
                                                                                                                                                                                            }
                                                                                                                                                                                            if (c != 65533) break block182;
                                                                                                                                                                                        }
                                                                                                                                                                                        return -1;
                                                                                                                                                                                    }
                                                                                                                                                                                    charCount += c < 65536 ? 1 : 2;
                                                                                                                                                                                    var18_21 = Unit.INSTANCE;
                                                                                                                                                                                    v16 = var16_19;
                                                                                                                                                                                }
                                                                                                                                                                                v17 = 3;
                                                                                                                                                                            }
                                                                                                                                                                            index$iv = v16 + v17;
                                                                                                                                                                            continue;
                                                                                                                                                                        }
                                                                                                                                                                        $this$process3Utf8Bytes$iv$iv = b0$iv;
                                                                                                                                                                        other$iv$iv = 3;
                                                                                                                                                                        $i$f$shr = 0;
                                                                                                                                                                        if ($this$shr$iv$iv >> other$iv$iv != -2) break block183;
                                                                                                                                                                        $this$process4Utf8Bytes$iv$iv = $this$processUtf8CodePoints$iv;
                                                                                                                                                                        $i$f$process4Utf8Bytes = false;
                                                                                                                                                                        if (endIndex$iv > index$iv + 3) break block184;
                                                                                                                                                                        $i$f$shr = 65533;
                                                                                                                                                                        var16_19 = index$iv;
                                                                                                                                                                        $i$a$-process4Utf8Bytes-Utf8$processUtf8CodePoints$3$iv = false;
                                                                                                                                                                        c = it$iv;
                                                                                                                                                                        $i$a$-processUtf8CodePoints--ByteString$codePointIndexToCharIndex$1 = false;
                                                                                                                                                                        $i$f$isIsoControl = j;
                                                                                                                                                                        j = $i$f$isIsoControl + 1;
                                                                                                                                                                        if ($i$f$isIsoControl == codePointCount) {
                                                                                                                                                                            return charCount;
                                                                                                                                                                        }
                                                                                                                                                                        if (c == 10 || c == 13) break block185;
                                                                                                                                                                        $i$f$isIsoControl = 0;
                                                                                                                                                                        if (0 <= c ? c < 32 : false) ** GOTO lbl-1000
                                                                                                                                                                        v29 = 127 <= c ? c < 160 : false;
                                                                                                                                                                        if (v29) lbl-1000:
                                                                                                                                                                        // 2 sources

                                                                                                                                                                        {
                                                                                                                                                                            v30 = true;
                                                                                                                                                                        } else {
                                                                                                                                                                            v30 = false;
                                                                                                                                                                        }
                                                                                                                                                                        if (v30) break block186;
                                                                                                                                                                    }
                                                                                                                                                                    if (c != 65533) break block187;
                                                                                                                                                                }
                                                                                                                                                                return -1;
                                                                                                                                                            }
                                                                                                                                                            charCount += c < 65536 ? 1 : 2;
                                                                                                                                                            var18_21 = Unit.INSTANCE;
                                                                                                                                                            v31 = var16_19;
                                                                                                                                                            if (endIndex$iv <= index$iv + true) break block188;
                                                                                                                                                            byte$iv$iv$iv = $this$process4Utf8Bytes$iv$iv[index$iv + true];
                                                                                                                                                            $i$f$isUtf8Continuation = false;
                                                                                                                                                            b2$iv$iv = byte$iv$iv$iv;
                                                                                                                                                            other$iv$iv$iv$iv = 192;
                                                                                                                                                            $i$f$and = false;
                                                                                                                                                            if (($this$and$iv$iv$iv$iv & other$iv$iv$iv$iv) == 128) break block189;
                                                                                                                                                        }
                                                                                                                                                        v32 = 1;
                                                                                                                                                        break block190;
                                                                                                                                                    }
                                                                                                                                                    if (endIndex$iv <= index$iv + 2) ** GOTO lbl-1000
                                                                                                                                                    byte$iv$iv$iv = $this$process4Utf8Bytes$iv$iv[index$iv + 2];
                                                                                                                                                    $i$f$isUtf8Continuation = false;
                                                                                                                                                    $this$and$iv$iv$iv$iv = byte$iv$iv$iv;
                                                                                                                                                    other$iv$iv$iv$iv = 192;
                                                                                                                                                    $i$f$and = false;
                                                                                                                                                    if (!(($this$and$iv$iv$iv$iv & other$iv$iv$iv$iv) == 128)) lbl-1000:
                                                                                                                                                    // 2 sources

                                                                                                                                                    {
                                                                                                                                                        v32 = 2;
                                                                                                                                                    } else {
                                                                                                                                                        v32 = 3;
                                                                                                                                                    }
                                                                                                                                                    break block190;
                                                                                                                                                }
                                                                                                                                                b0$iv$iv = $this$process4Utf8Bytes$iv$iv[index$iv];
                                                                                                                                                b1$iv$iv = $this$process4Utf8Bytes$iv$iv[index$iv + true];
                                                                                                                                                $i$f$isUtf8Continuation = false;
                                                                                                                                                other$iv$iv$iv$iv = b1$iv$iv;
                                                                                                                                                other$iv$iv$iv$iv = 192;
                                                                                                                                                $i$f$and = false;
                                                                                                                                                if (($this$and$iv$iv$iv$iv & other$iv$iv$iv$iv) == 128) break block191;
                                                                                                                                                it$iv = 65533;
                                                                                                                                                $i$a$-process4Utf8Bytes-Utf8$processUtf8CodePoints$3$iv = false;
                                                                                                                                                c = it$iv;
                                                                                                                                                $i$a$-processUtf8CodePoints--ByteString$codePointIndexToCharIndex$1 = false;
                                                                                                                                                $i$f$isIsoControl = j;
                                                                                                                                                j = $i$f$isIsoControl + 1;
                                                                                                                                                if ($i$f$isIsoControl == codePointCount) {
                                                                                                                                                    return charCount;
                                                                                                                                                }
                                                                                                                                                if (c == 10 || c == 13) break block192;
                                                                                                                                                $i$f$isIsoControl = 0;
                                                                                                                                                if (0 <= c ? c < 32 : false) ** GOTO lbl-1000
                                                                                                                                                v33 = 127 <= c ? c < 160 : false;
                                                                                                                                                if (v33) lbl-1000:
                                                                                                                                                // 2 sources

                                                                                                                                                {
                                                                                                                                                    v34 = true;
                                                                                                                                                } else {
                                                                                                                                                    v34 = false;
                                                                                                                                                }
                                                                                                                                                if (v34) break block193;
                                                                                                                                            }
                                                                                                                                            if (c != 65533) break block194;
                                                                                                                                        }
                                                                                                                                        return -1;
                                                                                                                                    }
                                                                                                                                    charCount += c < 65536 ? 1 : 2;
                                                                                                                                    var18_21 = Unit.INSTANCE;
                                                                                                                                    v31 = var16_19;
                                                                                                                                    v32 = 1;
                                                                                                                                    break block190;
                                                                                                                                }
                                                                                                                                b2$iv$iv = $this$process4Utf8Bytes$iv$iv[index$iv + 2];
                                                                                                                                $i$f$isUtf8Continuation = false;
                                                                                                                                other$iv$iv$iv$iv = b2$iv$iv;
                                                                                                                                other$iv$iv$iv$iv = 192;
                                                                                                                                $i$f$and = false;
                                                                                                                                if (($this$and$iv$iv$iv$iv & other$iv$iv$iv$iv) == 128) break block195;
                                                                                                                                it$iv = 65533;
                                                                                                                                $i$a$-process4Utf8Bytes-Utf8$processUtf8CodePoints$3$iv = false;
                                                                                                                                c = it$iv;
                                                                                                                                $i$a$-processUtf8CodePoints--ByteString$codePointIndexToCharIndex$1 = false;
                                                                                                                                $i$f$isIsoControl = j;
                                                                                                                                j = $i$f$isIsoControl + 1;
                                                                                                                                if ($i$f$isIsoControl == codePointCount) {
                                                                                                                                    return charCount;
                                                                                                                                }
                                                                                                                                if (c == 10 || c == 13) break block196;
                                                                                                                                $i$f$isIsoControl = 0;
                                                                                                                                if (0 <= c ? c < 32 : false) ** GOTO lbl-1000
                                                                                                                                v35 = 127 <= c ? c < 160 : false;
                                                                                                                                if (v35) lbl-1000:
                                                                                                                                // 2 sources

                                                                                                                                {
                                                                                                                                    v36 = true;
                                                                                                                                } else {
                                                                                                                                    v36 = false;
                                                                                                                                }
                                                                                                                                if (v36) break block197;
                                                                                                                            }
                                                                                                                            if (c != 65533) break block198;
                                                                                                                        }
                                                                                                                        return -1;
                                                                                                                    }
                                                                                                                    charCount += c < 65536 ? 1 : 2;
                                                                                                                    var18_21 = Unit.INSTANCE;
                                                                                                                    v31 = var16_19;
                                                                                                                    v32 = 2;
                                                                                                                    break block190;
                                                                                                                }
                                                                                                                b3$iv$iv = $this$process4Utf8Bytes$iv$iv[index$iv + 3];
                                                                                                                $i$f$isUtf8Continuation = false;
                                                                                                                other$iv$iv$iv$iv = b3$iv$iv;
                                                                                                                other$iv$iv$iv$iv = 192;
                                                                                                                $i$f$and = false;
                                                                                                                if (($this$and$iv$iv$iv$iv & other$iv$iv$iv$iv) == 128) break block199;
                                                                                                                it$iv = 65533;
                                                                                                                $i$a$-process4Utf8Bytes-Utf8$processUtf8CodePoints$3$iv = false;
                                                                                                                c = it$iv;
                                                                                                                $i$a$-processUtf8CodePoints--ByteString$codePointIndexToCharIndex$1 = false;
                                                                                                                $i$f$isIsoControl = j;
                                                                                                                j = $i$f$isIsoControl + 1;
                                                                                                                if ($i$f$isIsoControl == codePointCount) {
                                                                                                                    return charCount;
                                                                                                                }
                                                                                                                if (c == 10 || c == 13) break block200;
                                                                                                                $i$f$isIsoControl = 0;
                                                                                                                if (0 <= c ? c < 32 : false) ** GOTO lbl-1000
                                                                                                                v37 = 127 <= c ? c < 160 : false;
                                                                                                                if (v37) lbl-1000:
                                                                                                                // 2 sources

                                                                                                                {
                                                                                                                    v38 = true;
                                                                                                                } else {
                                                                                                                    v38 = false;
                                                                                                                }
                                                                                                                if (v38) break block201;
                                                                                                            }
                                                                                                            if (c != 65533) break block202;
                                                                                                        }
                                                                                                        return -1;
                                                                                                    }
                                                                                                    charCount += c < 65536 ? 1 : 2;
                                                                                                    var18_21 = Unit.INSTANCE;
                                                                                                    v31 = var16_19;
                                                                                                    v32 = 3;
                                                                                                    break block190;
                                                                                                }
                                                                                                codePoint$iv$iv = 3678080 ^ b3$iv$iv ^ b2$iv$iv << 6 ^ b1$iv$iv << 12 ^ b0$iv$iv << 18;
                                                                                                if (codePoint$iv$iv <= 0x10FFFF) break block203;
                                                                                                it$iv = 65533;
                                                                                                $i$a$-process4Utf8Bytes-Utf8$processUtf8CodePoints$3$iv = false;
                                                                                                c = it$iv;
                                                                                                $i$a$-processUtf8CodePoints--ByteString$codePointIndexToCharIndex$1 = false;
                                                                                                $i$f$isIsoControl = j;
                                                                                                j = $i$f$isIsoControl + 1;
                                                                                                if ($i$f$isIsoControl == codePointCount) {
                                                                                                    return charCount;
                                                                                                }
                                                                                                if (c == 10 || c == 13) break block204;
                                                                                                $i$f$isIsoControl = 0;
                                                                                                if (0 <= c ? c < 32 : false) ** GOTO lbl-1000
                                                                                                v39 = 127 <= c ? c < 160 : false;
                                                                                                if (v39) lbl-1000:
                                                                                                // 2 sources

                                                                                                {
                                                                                                    v40 = true;
                                                                                                } else {
                                                                                                    v40 = false;
                                                                                                }
                                                                                                if (v40) break block205;
                                                                                            }
                                                                                            if (c != 65533) break block206;
                                                                                        }
                                                                                        return -1;
                                                                                    }
                                                                                    charCount += c < 65536 ? 1 : 2;
                                                                                    var18_21 = Unit.INSTANCE;
                                                                                    v31 = var16_19;
                                                                                    break block207;
                                                                                }
                                                                                v41 = 55296 <= codePoint$iv$iv ? codePoint$iv$iv < 57344 : false;
                                                                                if (!v41) break block208;
                                                                                it$iv = 65533;
                                                                                $i$a$-process4Utf8Bytes-Utf8$processUtf8CodePoints$3$iv = false;
                                                                                c = it$iv;
                                                                                $i$a$-processUtf8CodePoints--ByteString$codePointIndexToCharIndex$1 = false;
                                                                                $i$f$isIsoControl = j;
                                                                                j = $i$f$isIsoControl + 1;
                                                                                if ($i$f$isIsoControl == codePointCount) {
                                                                                    return charCount;
                                                                                }
                                                                                if (c == 10 || c == 13) break block209;
                                                                                $i$f$isIsoControl = 0;
                                                                                if (0 <= c ? c < 32 : false) ** GOTO lbl-1000
                                                                                v42 = 127 <= c ? c < 160 : false;
                                                                                if (v42) lbl-1000:
                                                                                // 2 sources

                                                                                {
                                                                                    v43 = true;
                                                                                } else {
                                                                                    v43 = false;
                                                                                }
                                                                                if (v43) break block210;
                                                                            }
                                                                            if (c != 65533) break block211;
                                                                        }
                                                                        return -1;
                                                                    }
                                                                    charCount += c < 65536 ? 1 : 2;
                                                                    var18_21 = Unit.INSTANCE;
                                                                    v31 = var16_19;
                                                                    break block207;
                                                                }
                                                                if (codePoint$iv$iv >= 65536) break block212;
                                                                it$iv = 65533;
                                                                $i$a$-process4Utf8Bytes-Utf8$processUtf8CodePoints$3$iv = false;
                                                                c = it$iv;
                                                                $i$a$-processUtf8CodePoints--ByteString$codePointIndexToCharIndex$1 = false;
                                                                $i$f$isIsoControl = j;
                                                                j = $i$f$isIsoControl + 1;
                                                                if ($i$f$isIsoControl == codePointCount) {
                                                                    return charCount;
                                                                }
                                                                if (c == 10 || c == 13) break block213;
                                                                $i$f$isIsoControl = 0;
                                                                if (0 <= c ? c < 32 : false) ** GOTO lbl-1000
                                                                v44 = 127 <= c ? c < 160 : false;
                                                                if (v44) lbl-1000:
                                                                // 2 sources

                                                                {
                                                                    v45 = true;
                                                                } else {
                                                                    v45 = false;
                                                                }
                                                                if (v45) break block214;
                                                            }
                                                            if (c != 65533) break block215;
                                                        }
                                                        return -1;
                                                    }
                                                    charCount += c < 65536 ? 1 : 2;
                                                    var18_21 = Unit.INSTANCE;
                                                    v31 = var16_19;
                                                    break block207;
                                                }
                                                it$iv = codePoint$iv$iv;
                                                $i$a$-process4Utf8Bytes-Utf8$processUtf8CodePoints$3$iv = false;
                                                c = it$iv;
                                                $i$a$-processUtf8CodePoints--ByteString$codePointIndexToCharIndex$1 = false;
                                                $i$f$isIsoControl = j;
                                                j = $i$f$isIsoControl + 1;
                                                if ($i$f$isIsoControl == codePointCount) {
                                                    return charCount;
                                                }
                                                if (c == 10 || c == 13) break block216;
                                                $i$f$isIsoControl = 0;
                                                if (0 <= c ? c < 32 : false) ** GOTO lbl-1000
                                                v46 = 127 <= c ? c < 160 : false;
                                                if (v46) lbl-1000:
                                                // 2 sources

                                                {
                                                    v47 = true;
                                                } else {
                                                    v47 = false;
                                                }
                                                if (v47) break block217;
                                            }
                                            if (c != 65533) break block218;
                                        }
                                        return -1;
                                    }
                                    charCount += c < 65536 ? 1 : 2;
                                    var18_21 = Unit.INSTANCE;
                                    v31 = var16_19;
                                }
                                v32 = 4;
                            }
                            index$iv = v31 + v32;
                            continue;
                        }
                        c = 65533;
                        $i$a$-processUtf8CodePoints--ByteString$codePointIndexToCharIndex$1 = false;
                        $i$f$isIsoControl = j;
                        j = $i$f$isIsoControl + 1;
                        if ($i$f$isIsoControl == codePointCount) {
                            return charCount;
                        }
                        if (c == 10 || c == 13) break block219;
                        $i$f$isIsoControl = 0;
                        if (0 <= c ? c < 32 : false) ** GOTO lbl-1000
                        v48 = 127 <= c ? c < 160 : false;
                        if (v48) lbl-1000:
                        // 2 sources

                        {
                            v49 = true;
                        } else {
                            v49 = false;
                        }
                        if (v49) break block220;
                    }
                    if (c != 65533) break block221;
                }
                return -1;
            }
            charCount += c < 65536 ? 1 : 2;
            ++index$iv;
        }
        return charCount;
    }

    static {
        char[] cArray = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        HEX_DIGIT_CHARS = cArray;
    }
}

