/*
 * Decompiled with CFR 0.152.
 */
package okio;

import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import okio.FileHandle;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u0012\n\u0002\b\u0007\b\u0000\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\b\u0010\u0007\u001a\u00020\bH\u0014J\b\u0010\t\u001a\u00020\bH\u0014J(\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u000b2\u0006\u0010\u0011\u001a\u00020\u000bH\u0014J\u0010\u0010\u0012\u001a\u00020\b2\u0006\u0010\u0013\u001a\u00020\rH\u0014J\b\u0010\u0014\u001a\u00020\rH\u0014J(\u0010\u0015\u001a\u00020\b2\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u000b2\u0006\u0010\u0011\u001a\u00020\u000bH\u0014R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0016"}, d2={"Lokio/NioFileSystemFileHandle;", "Lokio/FileHandle;", "readWrite", "", "fileChannel", "Ljava/nio/channels/FileChannel;", "(ZLjava/nio/channels/FileChannel;)V", "protectedClose", "", "protectedFlush", "protectedRead", "", "fileOffset", "", "array", "", "arrayOffset", "byteCount", "protectedResize", "size", "protectedSize", "protectedWrite", "okio"})
public final class NioFileSystemFileHandle
extends FileHandle {
    @NotNull
    private final FileChannel fileChannel;

    public NioFileSystemFileHandle(boolean readWrite, @NotNull FileChannel fileChannel) {
        Intrinsics.checkNotNullParameter(fileChannel, "fileChannel");
        super(readWrite);
        this.fileChannel = fileChannel;
    }

    @Override
    protected synchronized void protectedResize(long size) {
        long currentSize = this.size();
        long delta = size - currentSize;
        if (delta > 0L) {
            this.protectedWrite(currentSize, new byte[(int)delta], 0, (int)delta);
        } else {
            this.fileChannel.truncate(size);
        }
    }

    @Override
    protected synchronized long protectedSize() {
        return this.fileChannel.size();
    }

    @Override
    protected synchronized int protectedRead(long fileOffset, @NotNull byte[] array, int arrayOffset, int byteCount) {
        int bytesRead;
        int readResult;
        Intrinsics.checkNotNullParameter(array, "array");
        this.fileChannel.position(fileOffset);
        ByteBuffer byteBuffer = ByteBuffer.wrap(array, arrayOffset, byteCount);
        for (bytesRead = 0; bytesRead < byteCount; bytesRead += readResult) {
            readResult = this.fileChannel.read(byteBuffer);
            if (readResult != -1) continue;
            if (bytesRead != 0) break;
            return -1;
        }
        return bytesRead;
    }

    @Override
    protected synchronized void protectedWrite(long fileOffset, @NotNull byte[] array, int arrayOffset, int byteCount) {
        Intrinsics.checkNotNullParameter(array, "array");
        this.fileChannel.position(fileOffset);
        ByteBuffer byteBuffer = ByteBuffer.wrap(array, arrayOffset, byteCount);
        this.fileChannel.write(byteBuffer);
    }

    @Override
    protected synchronized void protectedFlush() {
        this.fileChannel.force(true);
    }

    @Override
    protected synchronized void protectedClose() {
        this.fileChannel.close();
    }
}

