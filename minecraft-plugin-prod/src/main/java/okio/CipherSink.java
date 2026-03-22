/*
 * Decompiled with CFR 0.152.
 */
package okio;

import java.io.IOException;
import javax.crypto.Cipher;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import okio.-SegmentedByteString;
import okio.Buffer;
import okio.BufferedSink;
import okio.Segment;
import okio.SegmentPool;
import okio.Sink;
import okio.Timeout;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000J\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u0003\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\b\u0010\r\u001a\u00020\u000eH\u0016J\n\u0010\u000f\u001a\u0004\u0018\u00010\u0010H\u0002J\b\u0010\u0011\u001a\u00020\u000eH\u0016J\b\u0010\u0012\u001a\u00020\u0013H\u0016J\u0018\u0010\u0014\u001a\u00020\b2\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u0018H\u0002J\u0018\u0010\u0019\u001a\u00020\u000e2\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u001a\u001a\u00020\u0018H\u0016R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u000e\u0010\u000b\u001a\u00020\fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001b"}, d2={"Lokio/CipherSink;", "Lokio/Sink;", "sink", "Lokio/BufferedSink;", "cipher", "Ljavax/crypto/Cipher;", "(Lokio/BufferedSink;Ljavax/crypto/Cipher;)V", "blockSize", "", "getCipher", "()Ljavax/crypto/Cipher;", "closed", "", "close", "", "doFinal", "", "flush", "timeout", "Lokio/Timeout;", "update", "source", "Lokio/Buffer;", "remaining", "", "write", "byteCount", "okio"})
@SourceDebugExtension(value={"SMAP\nCipherSink.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CipherSink.kt\nokio/CipherSink\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 Util.kt\nokio/-SegmentedByteString\n*L\n1#1,148:1\n1#2:149\n86#3:150\n*S KotlinDebug\n*F\n+ 1 CipherSink.kt\nokio/CipherSink\n*L\n47#1:150\n*E\n"})
public final class CipherSink
implements Sink {
    @NotNull
    private final BufferedSink sink;
    @NotNull
    private final Cipher cipher;
    private final int blockSize;
    private boolean closed;

    public CipherSink(@NotNull BufferedSink sink2, @NotNull Cipher cipher) {
        Intrinsics.checkNotNullParameter(sink2, "sink");
        Intrinsics.checkNotNullParameter(cipher, "cipher");
        this.sink = sink2;
        this.cipher = cipher;
        this.blockSize = this.cipher.getBlockSize();
        if (!(this.blockSize > 0)) {
            boolean bl = false;
            String string = "Block cipher required " + this.cipher;
            throw new IllegalArgumentException(string.toString());
        }
    }

    @NotNull
    public final Cipher getCipher() {
        return this.cipher;
    }

    @Override
    public void write(@NotNull Buffer source2, long byteCount) throws IOException {
        int size;
        Intrinsics.checkNotNullParameter(source2, "source");
        -SegmentedByteString.checkOffsetAndCount(source2.size(), 0L, byteCount);
        if (!(!this.closed)) {
            boolean bl = false;
            String string = "closed";
            throw new IllegalStateException(string.toString());
        }
        for (long remaining = byteCount; remaining > 0L; remaining -= (long)size) {
            size = this.update(source2, remaining);
        }
    }

    private final int update(Buffer source2, long remaining) {
        Segment segment = source2.head;
        Intrinsics.checkNotNull(segment);
        Segment head = segment;
        int b$iv = head.limit - head.pos;
        boolean $i$f$minOf = false;
        int size = (int)Math.min(remaining, (long)b$iv);
        Buffer buffer = this.sink.getBuffer();
        int outputSize = this.cipher.getOutputSize(size);
        while (outputSize > 8192) {
            if (size <= this.blockSize) {
                byte[] byArray = this.cipher.update(source2.readByteArray(remaining));
                Intrinsics.checkNotNullExpressionValue(byArray, "update(...)");
                this.sink.write(byArray);
                return (int)remaining;
            }
            outputSize = this.cipher.getOutputSize(size -= this.blockSize);
        }
        Segment s = buffer.writableSegment$okio(outputSize);
        int ciphered = this.cipher.update(head.data, head.pos, size, s.data, s.limit);
        s.limit += ciphered;
        buffer.setSize$okio(buffer.size() + (long)ciphered);
        if (s.pos == s.limit) {
            buffer.head = s.pop();
            SegmentPool.recycle(s);
        }
        this.sink.emitCompleteSegments();
        source2.setSize$okio(source2.size() - (long)size);
        head.pos += size;
        if (head.pos == head.limit) {
            source2.head = head.pop();
            SegmentPool.recycle(head);
        }
        return size;
    }

    @Override
    public void flush() {
        this.sink.flush();
    }

    @Override
    @NotNull
    public Timeout timeout() {
        return this.sink.timeout();
    }

    @Override
    public void close() throws IOException {
        Throwable thrown;
        block4: {
            if (this.closed) {
                return;
            }
            this.closed = true;
            thrown = this.doFinal();
            try {
                this.sink.close();
            }
            catch (Throwable e) {
                if (thrown != null) break block4;
                thrown = e;
            }
        }
        Throwable throwable = thrown;
        if (throwable != null) {
            throw throwable;
        }
    }

    private final Throwable doFinal() {
        int outputSize = this.cipher.getOutputSize(0);
        if (outputSize == 0) {
            return null;
        }
        if (outputSize > 8192) {
            try {
                byte[] byArray = this.cipher.doFinal();
                Intrinsics.checkNotNullExpressionValue(byArray, "doFinal(...)");
                this.sink.write(byArray);
            }
            catch (Throwable t) {
                return t;
            }
            return null;
        }
        Throwable thrown = null;
        Buffer buffer = this.sink.getBuffer();
        Segment s = buffer.writableSegment$okio(outputSize);
        try {
            int ciphered = this.cipher.doFinal(s.data, s.limit);
            s.limit += ciphered;
            buffer.setSize$okio(buffer.size() + (long)ciphered);
        }
        catch (Throwable e) {
            thrown = e;
        }
        if (s.pos == s.limit) {
            buffer.head = s.pop();
            SegmentPool.recycle(s);
        }
        return thrown;
    }
}

