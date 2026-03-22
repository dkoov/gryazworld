/*
 * Decompiled with CFR 0.152.
 */
package kotlin.io.encoding;

import java.nio.charset.Charset;
import kotlin.Metadata;
import kotlin.SinceKotlin;
import kotlin.internal.InlineOnly;
import kotlin.io.encoding.Base64;
import kotlin.io.encoding.ExperimentalEncodingApi;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.Charsets;

@Metadata(mv={1, 8, 0}, k=2, xi=48, d1={"\u0000 \n\u0000\n\u0002\u0010\u0012\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\r\n\u0000\n\u0002\u0010\b\n\u0002\b\u0006\n\u0002\u0010\u000e\n\u0000\u001a%\u0010\u0000\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u0006H\u0081\b\u001a5\u0010\b\u001a\u00020\u0006*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u00012\u0006\u0010\t\u001a\u00020\u00012\u0006\u0010\n\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u0006H\u0081\b\u001a%\u0010\u000b\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u0006H\u0081\b\u001a%\u0010\f\u001a\u00020\r*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u0006H\u0081\b\u00a8\u0006\u000e"}, d2={"platformCharsToBytes", "", "Lkotlin/io/encoding/Base64;", "source", "", "startIndex", "", "endIndex", "platformEncodeIntoByteArray", "destination", "destinationOffset", "platformEncodeToByteArray", "platformEncodeToString", "", "kotlin-stdlib"})
public final class Base64JVMKt {
    @SinceKotlin(version="1.8")
    @ExperimentalEncodingApi
    @InlineOnly
    private static final byte[] platformCharsToBytes(Base64 $this$platformCharsToBytes, CharSequence source2, int startIndex, int endIndex) {
        byte[] byArray;
        Intrinsics.checkNotNullParameter($this$platformCharsToBytes, "<this>");
        Intrinsics.checkNotNullParameter(source2, "source");
        if (source2 instanceof String) {
            $this$platformCharsToBytes.checkSourceBounds$kotlin_stdlib(source2.length(), startIndex, endIndex);
            String string = ((String)source2).substring(startIndex, endIndex);
            Intrinsics.checkNotNullExpressionValue(string, "this as java.lang.String\u2026ing(startIndex, endIndex)");
            String string2 = string;
            Charset charset = Charsets.ISO_8859_1;
            Intrinsics.checkNotNull(string2, "null cannot be cast to non-null type java.lang.String");
            byte[] byArray2 = string2.getBytes(charset);
            byArray = byArray2;
            Intrinsics.checkNotNullExpressionValue(byArray2, "this as java.lang.String).getBytes(charset)");
        } else {
            byArray = $this$platformCharsToBytes.charsToBytesImpl$kotlin_stdlib(source2, startIndex, endIndex);
        }
        return byArray;
    }

    @SinceKotlin(version="1.8")
    @ExperimentalEncodingApi
    @InlineOnly
    private static final String platformEncodeToString(Base64 $this$platformEncodeToString, byte[] source2, int startIndex, int endIndex) {
        Intrinsics.checkNotNullParameter($this$platformEncodeToString, "<this>");
        Intrinsics.checkNotNullParameter(source2, "source");
        byte[] byteResult = $this$platformEncodeToString.encodeToByteArrayImpl$kotlin_stdlib(source2, startIndex, endIndex);
        return new String(byteResult, Charsets.ISO_8859_1);
    }

    @SinceKotlin(version="1.8")
    @ExperimentalEncodingApi
    @InlineOnly
    private static final int platformEncodeIntoByteArray(Base64 $this$platformEncodeIntoByteArray, byte[] source2, byte[] destination, int destinationOffset, int startIndex, int endIndex) {
        Intrinsics.checkNotNullParameter($this$platformEncodeIntoByteArray, "<this>");
        Intrinsics.checkNotNullParameter(source2, "source");
        Intrinsics.checkNotNullParameter(destination, "destination");
        return $this$platformEncodeIntoByteArray.encodeIntoByteArrayImpl$kotlin_stdlib(source2, destination, destinationOffset, startIndex, endIndex);
    }

    @SinceKotlin(version="1.8")
    @ExperimentalEncodingApi
    @InlineOnly
    private static final byte[] platformEncodeToByteArray(Base64 $this$platformEncodeToByteArray, byte[] source2, int startIndex, int endIndex) {
        Intrinsics.checkNotNullParameter($this$platformEncodeToByteArray, "<this>");
        Intrinsics.checkNotNullParameter(source2, "source");
        return $this$platformEncodeToByteArray.encodeToByteArrayImpl$kotlin_stdlib(source2, startIndex, endIndex);
    }
}

