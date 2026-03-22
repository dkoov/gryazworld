/*
 * Decompiled with CFR 0.152.
 */
package okio;

import java.io.IOException;
import javax.crypto.Cipher;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import okio.Buffer;
import okio.BufferedSource;
import okio.Segment;
import okio.SegmentPool;
import okio.Source;
import okio.Timeout;
import org.jetbrains.annotations.NotNull;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000F\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\b\u0010\u0010\u001a\u00020\u0011H\u0016J\b\u0010\u0012\u001a\u00020\u0011H\u0002J\u0018\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\n2\u0006\u0010\u0016\u001a\u00020\u0014H\u0016J\b\u0010\u0017\u001a\u00020\u0011H\u0002J\b\u0010\u0018\u001a\u00020\u0019H\u0016J\b\u0010\u001a\u001a\u00020\u0011H\u0002R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u000e\u0010\r\u001a\u00020\u000eX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u000eX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001b"}, d2={"Lokio/CipherSource;", "Lokio/Source;", "source", "Lokio/BufferedSource;", "cipher", "Ljavax/crypto/Cipher;", "(Lokio/BufferedSource;Ljavax/crypto/Cipher;)V", "blockSize", "", "buffer", "Lokio/Buffer;", "getCipher", "()Ljavax/crypto/Cipher;", "closed", "", "final", "close", "", "doFinal", "read", "", "sink", "byteCount", "refill", "timeout", "Lokio/Timeout;", "update", "okio"})
@SourceDebugExtension(value={"SMAP\nCipherSource.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CipherSource.kt\nokio/CipherSource\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,120:1\n1#2:121\n*E\n"})
public final class CipherSource
implements Source {
    @NotNull
    private final BufferedSource source;
    @NotNull
    private final Cipher cipher;
    private final int blockSize;
    @NotNull
    private final Buffer buffer;
    private boolean final;
    private boolean closed;

    public CipherSource(@NotNull BufferedSource source2, @NotNull Cipher cipher) {
        Intrinsics.checkNotNullParameter(source2, "source");
        Intrinsics.checkNotNullParameter(cipher, "cipher");
        this.source = source2;
        this.cipher = cipher;
        this.blockSize = this.cipher.getBlockSize();
        this.buffer = new Buffer();
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
    public long read(@NotNull Buffer sink2, long byteCount) throws IOException {
        Intrinsics.checkNotNullParameter(sink2, "sink");
        if (!(byteCount >= 0L)) {
            boolean $i$a$-require-CipherSource$read$22 = false;
            String $i$a$-require-CipherSource$read$22 = "byteCount < 0: " + byteCount;
            throw new IllegalArgumentException($i$a$-require-CipherSource$read$22.toString());
        }
        if (!(!this.closed)) {
            boolean bl = false;
            String string = "closed";
            throw new IllegalStateException(string.toString());
        }
        if (byteCount == 0L) {
            return 0L;
        }
        this.refill();
        return this.buffer.read(sink2, byteCount);
    }

    private final void refill() {
        while (this.buffer.size() == 0L && !this.final) {
            if (this.source.exhausted()) {
                this.final = true;
                this.doFinal();
                break;
            }
            this.update();
        }
    }

    private final void update() {
        Segment segment = this.source.getBuffer().head;
        Intrinsics.checkNotNull(segment);
        Segment head = segment;
        int size = head.limit - head.pos;
        int outputSize = this.cipher.getOutputSize(size);
        while (outputSize > 8192) {
            if (size <= this.blockSize) {
                this.final = true;
                byte[] byArray = this.cipher.doFinal(this.source.readByteArray());
                Intrinsics.checkNotNullExpressionValue(byArray, "doFinal(...)");
                this.buffer.write(byArray);
                return;
            }
            outputSize = this.cipher.getOutputSize(size -= this.blockSize);
        }
        Segment s = this.buffer.writableSegment$okio(outputSize);
        int ciphered = this.cipher.update(head.data, head.pos, size, s.data, s.pos);
        this.source.skip(size);
        s.limit += ciphered;
        Buffer buffer = this.buffer;
        buffer.setSize$okio(buffer.size() + (long)ciphered);
        if (s.pos == s.limit) {
            this.buffer.head = s.pop();
            SegmentPool.recycle(s);
        }
    }

    private final void doFinal() {
        int outputSize = this.cipher.getOutputSize(0);
        if (outputSize == 0) {
            return;
        }
        Segment s = this.buffer.writableSegment$okio(outputSize);
        int ciphered = this.cipher.doFinal(s.data, s.pos);
        s.limit += ciphered;
        Buffer buffer = this.buffer;
        buffer.setSize$okio(buffer.size() + (long)ciphered);
        if (s.pos == s.limit) {
            this.buffer.head = s.pop();
            SegmentPool.recycle(s);
        }
    }

    @Override
    @NotNull
    public Timeout timeout() {
        return this.source.timeout();
    }

    @Override
    public void close() throws IOException {
        this.closed = true;
        this.source.close();
    }
}

