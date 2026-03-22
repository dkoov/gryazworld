/*
 * Decompiled with CFR 0.152.
 */
package kotlin.io.encoding;

import java.nio.charset.Charset;
import kotlin.Metadata;
import kotlin.SinceKotlin;
import kotlin.collections.AbstractList;
import kotlin.io.encoding.Base64Kt;
import kotlin.io.encoding.ExperimentalEncodingApi;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.CharsKt;
import kotlin.text.Charsets;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={1, 8, 0}, k=1, xi=48, d1={"\u0000B\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0006\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0012\n\u0002\b\u0002\n\u0002\u0010\r\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0012\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\n\b\u0017\u0018\u0000 22\u00020\u0001:\u00012B\u0017\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0005J\u0015\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fH\u0000\u00a2\u0006\u0002\b\rJ%\u0010\u000e\u001a\u00020\f2\u0006\u0010\u000b\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0011H\u0000\u00a2\u0006\u0002\b\u0013J \u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u00112\u0006\u0010\u0017\u001a\u00020\u00112\u0006\u0010\u0018\u001a\u00020\u0011H\u0002J%\u0010\u0019\u001a\u00020\u00152\u0006\u0010\u001a\u001a\u00020\u00112\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0011H\u0000\u00a2\u0006\u0002\b\u001bJ\"\u0010\u001c\u001a\u00020\f2\u0006\u0010\u000b\u001a\u00020\f2\b\b\u0002\u0010\u0010\u001a\u00020\u00112\b\b\u0002\u0010\u0012\u001a\u00020\u0011J\"\u0010\u001c\u001a\u00020\f2\u0006\u0010\u000b\u001a\u00020\u000f2\b\b\u0002\u0010\u0010\u001a\u00020\u00112\b\b\u0002\u0010\u0012\u001a\u00020\u0011J0\u0010\u001d\u001a\u00020\u00112\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\u001e\u001a\u00020\f2\u0006\u0010\u0017\u001a\u00020\u00112\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0011H\u0002J4\u0010\u001f\u001a\u00020\u00112\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\u001e\u001a\u00020\f2\b\b\u0002\u0010\u0017\u001a\u00020\u00112\b\b\u0002\u0010\u0010\u001a\u00020\u00112\b\b\u0002\u0010\u0012\u001a\u00020\u0011J4\u0010\u001f\u001a\u00020\u00112\u0006\u0010\u000b\u001a\u00020\u000f2\u0006\u0010\u001e\u001a\u00020\f2\b\b\u0002\u0010\u0017\u001a\u00020\u00112\b\b\u0002\u0010\u0010\u001a\u00020\u00112\b\b\u0002\u0010\u0012\u001a\u00020\u0011J \u0010 \u001a\u00020\u00112\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0011H\u0002J\"\u0010!\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\b\b\u0002\u0010\u0010\u001a\u00020\u00112\b\b\u0002\u0010\u0012\u001a\u00020\u0011J4\u0010\"\u001a\u00020\u00112\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\u001e\u001a\u00020\f2\b\b\u0002\u0010\u0017\u001a\u00020\u00112\b\b\u0002\u0010\u0010\u001a\u00020\u00112\b\b\u0002\u0010\u0012\u001a\u00020\u0011J5\u0010#\u001a\u00020\u00112\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\u001e\u001a\u00020\f2\u0006\u0010\u0017\u001a\u00020\u00112\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0011H\u0000\u00a2\u0006\u0002\b$J\u0010\u0010%\u001a\u00020\u00112\u0006\u0010\u001a\u001a\u00020\u0011H\u0002J=\u0010&\u001a\u0002H'\"\f\b\u0000\u0010'*\u00060(j\u0002`)2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\u001e\u001a\u0002H'2\b\b\u0002\u0010\u0010\u001a\u00020\u00112\b\b\u0002\u0010\u0012\u001a\u00020\u0011\u00a2\u0006\u0002\u0010*J\"\u0010+\u001a\u00020\f2\u0006\u0010\u000b\u001a\u00020\f2\b\b\u0002\u0010\u0010\u001a\u00020\u00112\b\b\u0002\u0010\u0012\u001a\u00020\u0011J%\u0010,\u001a\u00020\f2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0011H\u0000\u00a2\u0006\u0002\b-J(\u0010.\u001a\u00020\u00112\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010/\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u00112\u0006\u00100\u001a\u00020\u0011H\u0002J \u00101\u001a\u00020\u00112\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0011H\u0002R\u0014\u0010\u0004\u001a\u00020\u0003X\u0080\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u0014\u0010\u0002\u001a\u00020\u0003X\u0080\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\u0007\u00a8\u00063"}, d2={"Lkotlin/io/encoding/Base64;", "", "isUrlSafe", "", "isMimeScheme", "(ZZ)V", "isMimeScheme$kotlin_stdlib", "()Z", "isUrlSafe$kotlin_stdlib", "bytesToStringImpl", "", "source", "", "bytesToStringImpl$kotlin_stdlib", "charsToBytesImpl", "", "startIndex", "", "endIndex", "charsToBytesImpl$kotlin_stdlib", "checkDestinationBounds", "", "destinationSize", "destinationOffset", "capacityNeeded", "checkSourceBounds", "sourceSize", "checkSourceBounds$kotlin_stdlib", "decode", "decodeImpl", "destination", "decodeIntoByteArray", "decodeSize", "encode", "encodeIntoByteArray", "encodeIntoByteArrayImpl", "encodeIntoByteArrayImpl$kotlin_stdlib", "encodeSize", "encodeToAppendable", "A", "Ljava/lang/Appendable;", "Lkotlin/text/Appendable;", "([BLjava/lang/Appendable;II)Ljava/lang/Appendable;", "encodeToByteArray", "encodeToByteArrayImpl", "encodeToByteArrayImpl$kotlin_stdlib", "handlePaddingSymbol", "padIndex", "byteStart", "skipIllegalSymbolsIfMime", "Default", "kotlin-stdlib"})
@SinceKotlin(version="1.8")
@ExperimentalEncodingApi
public class Base64 {
    @NotNull
    public static final Default Default = new Default(null);
    private final boolean isUrlSafe;
    private final boolean isMimeScheme;
    private static final int bitsPerByte = 8;
    private static final int bitsPerSymbol = 6;
    public static final int bytesPerGroup = 3;
    public static final int symbolsPerGroup = 4;
    public static final byte padSymbol = 61;
    public static final int mimeLineLength = 76;
    private static final int mimeGroupsPerLine = 19;
    @NotNull
    private static final byte[] mimeLineSeparatorSymbols;
    @NotNull
    private static final Base64 UrlSafe;
    @NotNull
    private static final Base64 Mime;

    private Base64(boolean isUrlSafe, boolean isMimeScheme) {
        boolean bl;
        this.isUrlSafe = isUrlSafe;
        this.isMimeScheme = isMimeScheme;
        boolean bl2 = bl = !this.isUrlSafe || !this.isMimeScheme;
        if (!bl) {
            String string = "Failed requirement.";
            throw new IllegalArgumentException(string.toString());
        }
    }

    public final boolean isUrlSafe$kotlin_stdlib() {
        return this.isUrlSafe;
    }

    public final boolean isMimeScheme$kotlin_stdlib() {
        return this.isMimeScheme;
    }

    @NotNull
    public final byte[] encodeToByteArray(@NotNull byte[] source2, int startIndex, int endIndex) {
        Intrinsics.checkNotNullParameter(source2, "source");
        return this.encodeToByteArrayImpl$kotlin_stdlib(source2, startIndex, endIndex);
    }

    public static /* synthetic */ byte[] encodeToByteArray$default(Base64 base64, byte[] byArray, int n, int n2, int n3, Object object) {
        if (object != null) {
            throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: encodeToByteArray");
        }
        if ((n3 & 2) != 0) {
            n = 0;
        }
        if ((n3 & 4) != 0) {
            n2 = byArray.length;
        }
        return base64.encodeToByteArray(byArray, n, n2);
    }

    public final int encodeIntoByteArray(@NotNull byte[] source2, @NotNull byte[] destination, int destinationOffset, int startIndex, int endIndex) {
        Intrinsics.checkNotNullParameter(source2, "source");
        Intrinsics.checkNotNullParameter(destination, "destination");
        return this.encodeIntoByteArrayImpl$kotlin_stdlib(source2, destination, destinationOffset, startIndex, endIndex);
    }

    public static /* synthetic */ int encodeIntoByteArray$default(Base64 base64, byte[] byArray, byte[] byArray2, int n, int n2, int n3, int n4, Object object) {
        if (object != null) {
            throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: encodeIntoByteArray");
        }
        if ((n4 & 4) != 0) {
            n = 0;
        }
        if ((n4 & 8) != 0) {
            n2 = 0;
        }
        if ((n4 & 0x10) != 0) {
            n3 = byArray.length;
        }
        return base64.encodeIntoByteArray(byArray, byArray2, n, n2, n3);
    }

    @NotNull
    public final String encode(@NotNull byte[] source2, int startIndex, int endIndex) {
        Intrinsics.checkNotNullParameter(source2, "source");
        byte[] byArray = this.encodeToByteArrayImpl$kotlin_stdlib(source2, startIndex, endIndex);
        return new String(byArray, Charsets.ISO_8859_1);
    }

    public static /* synthetic */ String encode$default(Base64 base64, byte[] byArray, int n, int n2, int n3, Object object) {
        if (object != null) {
            throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: encode");
        }
        if ((n3 & 2) != 0) {
            n = 0;
        }
        if ((n3 & 4) != 0) {
            n2 = byArray.length;
        }
        return base64.encode(byArray, n, n2);
    }

    @NotNull
    public final <A extends Appendable> A encodeToAppendable(@NotNull byte[] source2, @NotNull A destination, int startIndex, int endIndex) {
        Intrinsics.checkNotNullParameter(source2, "source");
        Intrinsics.checkNotNullParameter(destination, "destination");
        byte[] byArray = this.encodeToByteArrayImpl$kotlin_stdlib(source2, startIndex, endIndex);
        String stringResult = new String(byArray, Charsets.ISO_8859_1);
        destination.append(stringResult);
        return destination;
    }

    public static /* synthetic */ Appendable encodeToAppendable$default(Base64 base64, byte[] byArray, Appendable appendable, int n, int n2, int n3, Object object) {
        if (object != null) {
            throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: encodeToAppendable");
        }
        if ((n3 & 4) != 0) {
            n = 0;
        }
        if ((n3 & 8) != 0) {
            n2 = byArray.length;
        }
        return base64.encodeToAppendable(byArray, appendable, n, n2);
    }

    @NotNull
    public final byte[] decode(@NotNull byte[] source2, int startIndex, int endIndex) {
        boolean bl;
        Intrinsics.checkNotNullParameter(source2, "source");
        this.checkSourceBounds$kotlin_stdlib(source2.length, startIndex, endIndex);
        int decodeSize = this.decodeSize(source2, startIndex, endIndex);
        byte[] destination = new byte[decodeSize];
        int bytesWritten = this.decodeImpl(source2, destination, 0, startIndex, endIndex);
        boolean bl2 = bl = bytesWritten == destination.length;
        if (!bl) {
            String string = "Check failed.";
            throw new IllegalStateException(string.toString());
        }
        return destination;
    }

    public static /* synthetic */ byte[] decode$default(Base64 base64, byte[] byArray, int n, int n2, int n3, Object object) {
        if (object != null) {
            throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: decode");
        }
        if ((n3 & 2) != 0) {
            n = 0;
        }
        if ((n3 & 4) != 0) {
            n2 = byArray.length;
        }
        return base64.decode(byArray, n, n2);
    }

    public final int decodeIntoByteArray(@NotNull byte[] source2, @NotNull byte[] destination, int destinationOffset, int startIndex, int endIndex) {
        Intrinsics.checkNotNullParameter(source2, "source");
        Intrinsics.checkNotNullParameter(destination, "destination");
        this.checkSourceBounds$kotlin_stdlib(source2.length, startIndex, endIndex);
        this.checkDestinationBounds(destination.length, destinationOffset, this.decodeSize(source2, startIndex, endIndex));
        return this.decodeImpl(source2, destination, destinationOffset, startIndex, endIndex);
    }

    public static /* synthetic */ int decodeIntoByteArray$default(Base64 base64, byte[] byArray, byte[] byArray2, int n, int n2, int n3, int n4, Object object) {
        if (object != null) {
            throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: decodeIntoByteArray");
        }
        if ((n4 & 4) != 0) {
            n = 0;
        }
        if ((n4 & 8) != 0) {
            n2 = 0;
        }
        if ((n4 & 0x10) != 0) {
            n3 = byArray.length;
        }
        return base64.decodeIntoByteArray(byArray, byArray2, n, n2, n3);
    }

    @NotNull
    public final byte[] decode(@NotNull CharSequence source2, int startIndex, int endIndex) {
        byte[] byArray;
        Intrinsics.checkNotNullParameter(source2, "source");
        Base64 base64 = this;
        if (source2 instanceof String) {
            base64.checkSourceBounds$kotlin_stdlib(source2.length(), startIndex, endIndex);
            String string = ((String)source2).substring(startIndex, endIndex);
            Intrinsics.checkNotNullExpressionValue(string, "this as java.lang.String\u2026ing(startIndex, endIndex)");
            String string2 = string;
            Charset charset = Charsets.ISO_8859_1;
            Intrinsics.checkNotNull(string2, "null cannot be cast to non-null type java.lang.String");
            byte[] byArray2 = string2.getBytes(charset);
            byArray = byArray2;
            Intrinsics.checkNotNullExpressionValue(byArray2, "this as java.lang.String).getBytes(charset)");
        } else {
            byArray = base64.charsToBytesImpl$kotlin_stdlib(source2, startIndex, endIndex);
        }
        byte[] byteSource = byArray;
        return Base64.decode$default(this, byteSource, 0, 0, 6, null);
    }

    public static /* synthetic */ byte[] decode$default(Base64 base64, CharSequence charSequence, int n, int n2, int n3, Object object) {
        if (object != null) {
            throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: decode");
        }
        if ((n3 & 2) != 0) {
            n = 0;
        }
        if ((n3 & 4) != 0) {
            n2 = charSequence.length();
        }
        return base64.decode(charSequence, n, n2);
    }

    public final int decodeIntoByteArray(@NotNull CharSequence source2, @NotNull byte[] destination, int destinationOffset, int startIndex, int endIndex) {
        byte[] byArray;
        Intrinsics.checkNotNullParameter(source2, "source");
        Intrinsics.checkNotNullParameter(destination, "destination");
        Base64 base64 = this;
        if (source2 instanceof String) {
            base64.checkSourceBounds$kotlin_stdlib(source2.length(), startIndex, endIndex);
            String string = ((String)source2).substring(startIndex, endIndex);
            Intrinsics.checkNotNullExpressionValue(string, "this as java.lang.String\u2026ing(startIndex, endIndex)");
            String string2 = string;
            Charset charset = Charsets.ISO_8859_1;
            Intrinsics.checkNotNull(string2, "null cannot be cast to non-null type java.lang.String");
            byte[] byArray2 = string2.getBytes(charset);
            byArray = byArray2;
            Intrinsics.checkNotNullExpressionValue(byArray2, "this as java.lang.String).getBytes(charset)");
        } else {
            byArray = base64.charsToBytesImpl$kotlin_stdlib(source2, startIndex, endIndex);
        }
        byte[] byteSource = byArray;
        return Base64.decodeIntoByteArray$default(this, byteSource, destination, destinationOffset, 0, 0, 24, null);
    }

    public static /* synthetic */ int decodeIntoByteArray$default(Base64 base64, CharSequence charSequence, byte[] byArray, int n, int n2, int n3, int n4, Object object) {
        if (object != null) {
            throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: decodeIntoByteArray");
        }
        if ((n4 & 4) != 0) {
            n = 0;
        }
        if ((n4 & 8) != 0) {
            n2 = 0;
        }
        if ((n4 & 0x10) != 0) {
            n3 = charSequence.length();
        }
        return base64.decodeIntoByteArray(charSequence, byArray, n, n2, n3);
    }

    @NotNull
    public final byte[] encodeToByteArrayImpl$kotlin_stdlib(@NotNull byte[] source2, int startIndex, int endIndex) {
        Intrinsics.checkNotNullParameter(source2, "source");
        this.checkSourceBounds$kotlin_stdlib(source2.length, startIndex, endIndex);
        int encodeSize = this.encodeSize(endIndex - startIndex);
        byte[] destination = new byte[encodeSize];
        this.encodeIntoByteArrayImpl$kotlin_stdlib(source2, destination, 0, startIndex, endIndex);
        return destination;
    }

    public final int encodeIntoByteArrayImpl$kotlin_stdlib(@NotNull byte[] source2, @NotNull byte[] destination, int destinationOffset, int startIndex, int endIndex) {
        boolean bl;
        int groupsPerLine;
        Intrinsics.checkNotNullParameter(source2, "source");
        Intrinsics.checkNotNullParameter(destination, "destination");
        this.checkSourceBounds$kotlin_stdlib(source2.length, startIndex, endIndex);
        this.checkDestinationBounds(destination.length, destinationOffset, this.encodeSize(endIndex - startIndex));
        byte[] encodeMap = this.isUrlSafe ? Base64Kt.access$getBase64UrlEncodeMap$p() : Base64Kt.access$getBase64EncodeMap$p();
        int sourceIndex = startIndex;
        int destinationIndex = destinationOffset;
        int n = groupsPerLine = this.isMimeScheme ? 19 : Integer.MAX_VALUE;
        while (sourceIndex + 2 < endIndex) {
            int groups2 = Math.min((endIndex - sourceIndex) / 3, groupsPerLine);
            for (int i = 0; i < groups2; ++i) {
                int byte1 = source2[sourceIndex++] & 0xFF;
                int byte2 = source2[sourceIndex++] & 0xFF;
                int byte3 = source2[sourceIndex++] & 0xFF;
                int bits = byte1 << 16 | byte2 << 8 | byte3;
                destination[destinationIndex++] = encodeMap[bits >>> 18];
                destination[destinationIndex++] = encodeMap[bits >>> 12 & 0x3F];
                destination[destinationIndex++] = encodeMap[bits >>> 6 & 0x3F];
                destination[destinationIndex++] = encodeMap[bits & 0x3F];
            }
            if (groups2 != groupsPerLine || sourceIndex == endIndex) continue;
            destination[destinationIndex++] = mimeLineSeparatorSymbols[0];
            destination[destinationIndex++] = mimeLineSeparatorSymbols[1];
        }
        switch (endIndex - sourceIndex) {
            case 1: {
                int byte1 = source2[sourceIndex++] & 0xFF;
                int bits = byte1 << 4;
                destination[destinationIndex++] = encodeMap[bits >>> 6];
                destination[destinationIndex++] = encodeMap[bits & 0x3F];
                destination[destinationIndex++] = 61;
                destination[destinationIndex++] = 61;
                break;
            }
            case 2: {
                int byte1 = source2[sourceIndex++] & 0xFF;
                int byte2 = source2[sourceIndex++] & 0xFF;
                int bits = byte1 << 10 | byte2 << 2;
                destination[destinationIndex++] = encodeMap[bits >>> 12];
                destination[destinationIndex++] = encodeMap[bits >>> 6 & 0x3F];
                destination[destinationIndex++] = encodeMap[bits & 0x3F];
                destination[destinationIndex++] = 61;
            }
        }
        boolean bl2 = bl = sourceIndex == endIndex;
        if (!bl) {
            String string = "Check failed.";
            throw new IllegalStateException(string.toString());
        }
        return destinationIndex - destinationOffset;
    }

    private final int encodeSize(int sourceSize) {
        int groups2 = (sourceSize + 3 - 1) / 3;
        int lineSeparators = this.isMimeScheme ? (groups2 - 1) / 19 : 0;
        int size = groups2 * 4 + lineSeparators * 2;
        if (size < 0) {
            throw new IllegalArgumentException("Input is too big");
        }
        return size;
    }

    private final int decodeImpl(byte[] source2, byte[] destination, int destinationOffset, int startIndex, int endIndex) {
        int symbol;
        int[] decodeMap = this.isUrlSafe ? Base64Kt.access$getBase64UrlDecodeMap$p() : Base64Kt.access$getBase64DecodeMap$p();
        int payload = 0;
        int byteStart = -8;
        int sourceIndex = startIndex;
        int destinationIndex = destinationOffset;
        while (sourceIndex < endIndex) {
            int symbolBits;
            if (byteStart == -8 && sourceIndex + 3 < endIndex) {
                int symbol4;
                int symbol3;
                int bits;
                int symbol1 = decodeMap[source2[sourceIndex++] & 0xFF];
                int symbol2 = decodeMap[source2[sourceIndex++] & 0xFF];
                if ((bits = symbol1 << 18 | symbol2 << 12 | (symbol3 = decodeMap[source2[sourceIndex++] & 0xFF]) << 6 | (symbol4 = decodeMap[source2[sourceIndex++] & 0xFF])) >= 0) {
                    destination[destinationIndex++] = (byte)(bits >> 16);
                    destination[destinationIndex++] = (byte)(bits >> 8);
                    destination[destinationIndex++] = (byte)bits;
                    continue;
                }
                sourceIndex -= 4;
            }
            if ((symbolBits = decodeMap[symbol = source2[sourceIndex] & 0xFF]) < 0) {
                if (symbolBits == -2) {
                    sourceIndex = this.handlePaddingSymbol(source2, sourceIndex, endIndex, byteStart);
                    break;
                }
                if (this.isMimeScheme) {
                    ++sourceIndex;
                    continue;
                }
                StringBuilder stringBuilder = new StringBuilder().append("Invalid symbol '").append((char)symbol).append("'(");
                String string = Integer.toString(symbol, CharsKt.checkRadix(8));
                Intrinsics.checkNotNullExpressionValue(string, "toString(this, checkRadix(radix))");
                throw new IllegalArgumentException(stringBuilder.append(string).append(") at index ").append(sourceIndex).toString());
            }
            ++sourceIndex;
            payload = payload << 6 | symbolBits;
            if ((byteStart += 6) < 0) continue;
            destination[destinationIndex++] = (byte)(payload >>> byteStart);
            payload &= (1 << byteStart) - 1;
            byteStart -= 8;
        }
        if (byteStart == -2) {
            throw new IllegalArgumentException("The last unit of input does not have enough bits");
        }
        if ((sourceIndex = this.skipIllegalSymbolsIfMime(source2, sourceIndex, endIndex)) < endIndex) {
            symbol = source2[sourceIndex] & 0xFF;
            StringBuilder stringBuilder = new StringBuilder().append("Symbol '").append((char)symbol).append("'(");
            String string = Integer.toString(symbol, CharsKt.checkRadix(8));
            Intrinsics.checkNotNullExpressionValue(string, "toString(this, checkRadix(radix))");
            throw new IllegalArgumentException(stringBuilder.append(string).append(") at index ").append(sourceIndex - 1).append(" is prohibited after the pad character").toString());
        }
        return destinationIndex - destinationOffset;
    }

    private final int decodeSize(byte[] source2, int startIndex, int endIndex) {
        int symbols = endIndex - startIndex;
        if (symbols == 0) {
            return 0;
        }
        if (symbols == 1) {
            throw new IllegalArgumentException("Input should have at list 2 symbols for Base64 decoding, startIndex: " + startIndex + ", endIndex: " + endIndex);
        }
        if (this.isMimeScheme) {
            for (int index = startIndex; index < endIndex; ++index) {
                int symbol = source2[index] & 0xFF;
                int symbolBits = Base64Kt.access$getBase64DecodeMap$p()[symbol];
                if (symbolBits >= 0) continue;
                if (symbolBits == -2) {
                    symbols -= endIndex - index;
                    break;
                }
                --symbols;
            }
        } else if (source2[endIndex - 1] == 61) {
            --symbols;
            if (source2[endIndex - 2] == 61) {
                --symbols;
            }
        }
        return (int)((long)symbols * (long)6 / (long)8);
    }

    @NotNull
    public final byte[] charsToBytesImpl$kotlin_stdlib(@NotNull CharSequence source2, int startIndex, int endIndex) {
        Intrinsics.checkNotNullParameter(source2, "source");
        this.checkSourceBounds$kotlin_stdlib(source2.length(), startIndex, endIndex);
        byte[] byteArray = new byte[endIndex - startIndex];
        int length = 0;
        for (int index = startIndex; index < endIndex; ++index) {
            char symbol = source2.charAt(index);
            byteArray[length++] = symbol <= '\u00ff' ? (int)symbol : 63;
        }
        return byteArray;
    }

    @NotNull
    public final String bytesToStringImpl$kotlin_stdlib(@NotNull byte[] source2) {
        Intrinsics.checkNotNullParameter(source2, "source");
        StringBuilder stringBuilder = new StringBuilder(source2.length);
        for (byte by : source2) {
            stringBuilder.append((char)by);
        }
        String string = stringBuilder.toString();
        Intrinsics.checkNotNullExpressionValue(string, "stringBuilder.toString()");
        return string;
    }

    private final int handlePaddingSymbol(byte[] source2, int padIndex, int endIndex, int byteStart) {
        int n;
        switch (byteStart) {
            case -8: {
                throw new IllegalArgumentException("Redundant pad character at index " + padIndex);
            }
            case -2: {
                n = padIndex + 1;
                break;
            }
            case -4: {
                int secondPadIndex = this.skipIllegalSymbolsIfMime(source2, padIndex + 1, endIndex);
                if (secondPadIndex == endIndex || source2[secondPadIndex] != 61) {
                    throw new IllegalArgumentException("Missing one pad character at index " + secondPadIndex);
                }
                n = secondPadIndex + 1;
                break;
            }
            case -6: {
                n = padIndex + 1;
                break;
            }
            default: {
                throw new IllegalStateException("Unreachable".toString());
            }
        }
        return n;
    }

    private final int skipIllegalSymbolsIfMime(byte[] source2, int startIndex, int endIndex) {
        int sourceIndex;
        if (!this.isMimeScheme) {
            return startIndex;
        }
        for (sourceIndex = startIndex; sourceIndex < endIndex; ++sourceIndex) {
            int symbol = source2[sourceIndex] & 0xFF;
            if (Base64Kt.access$getBase64DecodeMap$p()[symbol] == -1) continue;
            return sourceIndex;
        }
        return sourceIndex;
    }

    public final void checkSourceBounds$kotlin_stdlib(int sourceSize, int startIndex, int endIndex) {
        AbstractList.Companion.checkBoundsIndexes$kotlin_stdlib(startIndex, endIndex, sourceSize);
    }

    private final void checkDestinationBounds(int destinationSize, int destinationOffset, int capacityNeeded) {
        if (destinationOffset < 0 || destinationOffset > destinationSize) {
            throw new IndexOutOfBoundsException("destination offset: " + destinationOffset + ", destination size: " + destinationSize);
        }
        int destinationEndIndex = destinationOffset + capacityNeeded;
        if (destinationEndIndex < 0 || destinationEndIndex > destinationSize) {
            throw new IndexOutOfBoundsException("The destination array does not have enough capacity, destination offset: " + destinationOffset + ", destination size: " + destinationSize + ", capacity needed: " + capacityNeeded);
        }
    }

    public /* synthetic */ Base64(boolean isUrlSafe, boolean isMimeScheme, DefaultConstructorMarker $constructor_marker) {
        this(isUrlSafe, isMimeScheme);
    }

    static {
        byte[] byArray = new byte[]{13, 10};
        mimeLineSeparatorSymbols = byArray;
        UrlSafe = new Base64(true, false);
        Mime = new Base64(false, true);
    }

    @Metadata(mv={1, 8, 0}, k=1, xi=48, d1={"\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0010\u0012\n\u0002\b\u0003\n\u0002\u0010\u0005\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u0011\u0010\u0003\u001a\u00020\u0001\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0004\u0010\u0005R\u0011\u0010\u0006\u001a\u00020\u0001\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\u0005R\u000e\u0010\b\u001a\u00020\tX\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\tX\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\tX\u0080T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\tX\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\tX\u0080T\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u000e\u001a\u00020\u000fX\u0080\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u000e\u0010\u0012\u001a\u00020\u0013X\u0080T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0014\u001a\u00020\tX\u0080T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0015"}, d2={"Lkotlin/io/encoding/Base64$Default;", "Lkotlin/io/encoding/Base64;", "()V", "Mime", "getMime", "()Lkotlin/io/encoding/Base64;", "UrlSafe", "getUrlSafe", "bitsPerByte", "", "bitsPerSymbol", "bytesPerGroup", "mimeGroupsPerLine", "mimeLineLength", "mimeLineSeparatorSymbols", "", "getMimeLineSeparatorSymbols$kotlin_stdlib", "()[B", "padSymbol", "", "symbolsPerGroup", "kotlin-stdlib"})
    public static final class Default
    extends Base64 {
        private Default() {
            super(false, false, null);
        }

        @NotNull
        public final byte[] getMimeLineSeparatorSymbols$kotlin_stdlib() {
            return mimeLineSeparatorSymbols;
        }

        @NotNull
        public final Base64 getUrlSafe() {
            return UrlSafe;
        }

        @NotNull
        public final Base64 getMime() {
            return Mime;
        }

        public /* synthetic */ Default(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }
}

