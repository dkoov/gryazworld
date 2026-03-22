/*
 * Decompiled with CFR 0.152.
 */
package kotlin.io.encoding;

import java.io.IOException;
import java.io.InputStream;
import kotlin.Metadata;
import kotlin.collections.ArraysKt;
import kotlin.io.encoding.Base64;
import kotlin.io.encoding.Base64Kt;
import kotlin.io.encoding.ExperimentalEncodingApi;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={1, 8, 0}, k=1, xi=48, d1={"\u00002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0012\n\u0000\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u000f\b\u0003\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0001\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u00a2\u0006\u0002\u0010\u0005J\b\u0010\u0013\u001a\u00020\u0014H\u0016J \u0010\u0015\u001a\u00020\u00142\u0006\u0010\u0016\u001a\u00020\u00072\u0006\u0010\u0017\u001a\u00020\t2\u0006\u0010\u0018\u001a\u00020\tH\u0002J(\u0010\u0019\u001a\u00020\t2\u0006\u0010\u0016\u001a\u00020\u00072\u0006\u0010\u0017\u001a\u00020\t2\u0006\u0010\u001a\u001a\u00020\t2\u0006\u0010\u001b\u001a\u00020\tH\u0002J\u0010\u0010\u001c\u001a\u00020\t2\u0006\u0010\u001b\u001a\u00020\tH\u0002J\b\u0010\u001d\u001a\u00020\tH\u0016J \u0010\u001d\u001a\u00020\t2\u0006\u0010\u001e\u001a\u00020\u00072\u0006\u0010\u001f\u001a\u00020\t2\u0006\u0010\u0018\u001a\u00020\tH\u0016J\b\u0010 \u001a\u00020\tH\u0002J\b\u0010!\u001a\u00020\u0014H\u0002J\b\u0010\"\u001a\u00020\u0014H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010\n\u001a\u00020\t8BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b\u000b\u0010\fR\u000e\u0010\r\u001a\u00020\tX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0001X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u000fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006#"}, d2={"Lkotlin/io/encoding/DecodeInputStream;", "Ljava/io/InputStream;", "input", "base64", "Lkotlin/io/encoding/Base64;", "(Ljava/io/InputStream;Lkotlin/io/encoding/Base64;)V", "byteBuffer", "", "byteBufferEndIndex", "", "byteBufferLength", "getByteBufferLength", "()I", "byteBufferStartIndex", "isClosed", "", "isEOF", "singleByteBuffer", "symbolBuffer", "close", "", "copyByteBufferInto", "dst", "dstOffset", "length", "decodeSymbolBufferInto", "dstEndIndex", "symbolBufferLength", "handlePaddingSymbol", "read", "destination", "offset", "readNextSymbol", "resetByteBufferIfEmpty", "shiftByteBufferToStartIfNeeded", "kotlin-stdlib"})
@ExperimentalEncodingApi
final class DecodeInputStream
extends InputStream {
    @NotNull
    private final InputStream input;
    @NotNull
    private final Base64 base64;
    private boolean isClosed;
    private boolean isEOF;
    @NotNull
    private final byte[] singleByteBuffer;
    @NotNull
    private final byte[] symbolBuffer;
    @NotNull
    private final byte[] byteBuffer;
    private int byteBufferStartIndex;
    private int byteBufferEndIndex;

    public DecodeInputStream(@NotNull InputStream input, @NotNull Base64 base64) {
        Intrinsics.checkNotNullParameter(input, "input");
        Intrinsics.checkNotNullParameter(base64, "base64");
        this.input = input;
        this.base64 = base64;
        this.singleByteBuffer = new byte[1];
        this.symbolBuffer = new byte[1024];
        this.byteBuffer = new byte[1024];
    }

    private final int getByteBufferLength() {
        return this.byteBufferEndIndex - this.byteBufferStartIndex;
    }

    @Override
    public int read() {
        int n;
        if (this.byteBufferStartIndex < this.byteBufferEndIndex) {
            int n2 = this.byteBuffer[this.byteBufferStartIndex] & 0xFF;
            ++this.byteBufferStartIndex;
            this.resetByteBufferIfEmpty();
            return n2;
        }
        switch (this.read(this.singleByteBuffer, 0, 1)) {
            case -1: {
                n = -1;
                break;
            }
            case 1: {
                n = this.singleByteBuffer[0] & 0xFF;
                break;
            }
            default: {
                throw new IllegalStateException("Unreachable".toString());
            }
        }
        return n;
    }

    @Override
    public int read(@NotNull byte[] destination, int offset, int length) {
        Intrinsics.checkNotNullParameter(destination, "destination");
        if (offset < 0 || length < 0 || offset + length > destination.length) {
            throw new IndexOutOfBoundsException("offset: " + offset + ", length: " + length + ", buffer size: " + destination.length);
        }
        if (this.isClosed) {
            throw new IOException("The input stream is closed.");
        }
        if (this.isEOF) {
            return -1;
        }
        if (length == 0) {
            return 0;
        }
        if (this.getByteBufferLength() >= length) {
            this.copyByteBufferInto(destination, offset, length);
            return length;
        }
        int bytesNeeded = length - this.getByteBufferLength();
        int groupsNeeded = (bytesNeeded + 3 - 1) / 3;
        int symbolsNeeded = groupsNeeded * 4;
        int dstOffset = offset;
        while (!this.isEOF && symbolsNeeded > 0) {
            boolean bl;
            int symbolBufferLength = 0;
            int symbolsToRead = Math.min(this.symbolBuffer.length, symbolsNeeded);
            block5: while (!this.isEOF && symbolBufferLength < symbolsToRead) {
                int symbol = this.readNextSymbol();
                switch (symbol) {
                    case -1: {
                        this.isEOF = true;
                        continue block5;
                    }
                    case 61: {
                        symbolBufferLength = this.handlePaddingSymbol(symbolBufferLength);
                        this.isEOF = true;
                        continue block5;
                    }
                }
                this.symbolBuffer[symbolBufferLength] = (byte)symbol;
                ++symbolBufferLength;
            }
            boolean bl2 = bl = this.isEOF || symbolBufferLength == symbolsToRead;
            if (!bl) {
                String string = "Check failed.";
                throw new IllegalStateException(string.toString());
            }
            symbolsNeeded -= symbolBufferLength;
            dstOffset += this.decodeSymbolBufferInto(destination, dstOffset, length + offset, symbolBufferLength);
        }
        return dstOffset == offset && this.isEOF ? -1 : dstOffset - offset;
    }

    @Override
    public void close() {
        if (!this.isClosed) {
            this.isClosed = true;
            this.input.close();
        }
    }

    private final int decodeSymbolBufferInto(byte[] dst, int dstOffset, int dstEndIndex, int symbolBufferLength) {
        this.byteBufferEndIndex += this.base64.decodeIntoByteArray(this.symbolBuffer, this.byteBuffer, this.byteBufferEndIndex, 0, symbolBufferLength);
        int bytesToCopy = Math.min(this.getByteBufferLength(), dstEndIndex - dstOffset);
        this.copyByteBufferInto(dst, dstOffset, bytesToCopy);
        this.shiftByteBufferToStartIfNeeded();
        return bytesToCopy;
    }

    private final void copyByteBufferInto(byte[] dst, int dstOffset, int length) {
        ArraysKt.copyInto(this.byteBuffer, dst, dstOffset, this.byteBufferStartIndex, this.byteBufferStartIndex + length);
        this.byteBufferStartIndex += length;
        this.resetByteBufferIfEmpty();
    }

    private final void resetByteBufferIfEmpty() {
        if (this.byteBufferStartIndex == this.byteBufferEndIndex) {
            this.byteBufferStartIndex = 0;
            this.byteBufferEndIndex = 0;
        }
    }

    private final void shiftByteBufferToStartIfNeeded() {
        int symbolBufferCapacity = this.symbolBuffer.length / 4 * 3;
        int byteBufferCapacity = this.byteBuffer.length - this.byteBufferEndIndex;
        if (symbolBufferCapacity > byteBufferCapacity) {
            ArraysKt.copyInto(this.byteBuffer, this.byteBuffer, 0, this.byteBufferStartIndex, this.byteBufferEndIndex);
            this.byteBufferEndIndex -= this.byteBufferStartIndex;
            this.byteBufferStartIndex = 0;
        }
    }

    private final int handlePaddingSymbol(int symbolBufferLength) {
        int n;
        this.symbolBuffer[symbolBufferLength] = 61;
        if ((symbolBufferLength & 3) == 2) {
            int secondPad = this.readNextSymbol();
            if (secondPad >= 0) {
                this.symbolBuffer[symbolBufferLength + 1] = (byte)secondPad;
            }
            n = symbolBufferLength + 2;
        } else {
            n = symbolBufferLength + 1;
        }
        return n;
    }

    private final int readNextSymbol() {
        if (!this.base64.isMimeScheme$kotlin_stdlib()) {
            return this.input.read();
        }
        int read = 0;
        while ((read = this.input.read()) != -1 && !Base64Kt.isInMimeAlphabet(read)) {
        }
        return read;
    }
}

