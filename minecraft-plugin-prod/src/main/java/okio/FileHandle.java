/*
 * Decompiled with CFR 0.152.
 */
package okio;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import okio.-SegmentedByteString;
import okio.Buffer;
import okio.RealBufferedSink;
import okio.RealBufferedSource;
import okio.Segment;
import okio.SegmentPool;
import okio.Sink;
import okio.Source;
import okio.Timeout;
import okio._JvmPlatformKt;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000X\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0012\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\b\b&\u0018\u00002\u00060\u0001j\u0002`\u0002:\u0002-.B\r\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u00a2\u0006\u0002\u0010\u0005J\u0006\u0010\u0010\u001a\u00020\u0011J\u0006\u0010\u0012\u001a\u00020\u0013J\u0006\u0010\u0014\u001a\u00020\u0013J\u000e\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u0011J\u000e\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0018\u001a\u00020\u0019J\b\u0010\u001a\u001a\u00020\u0013H$J\b\u0010\u001b\u001a\u00020\u0013H$J(\u0010\u001c\u001a\u00020\r2\u0006\u0010\u001d\u001a\u00020\u00162\u0006\u0010\u001e\u001a\u00020\u001f2\u0006\u0010 \u001a\u00020\r2\u0006\u0010!\u001a\u00020\rH$J\u0010\u0010\"\u001a\u00020\u00132\u0006\u0010#\u001a\u00020\u0016H$J\b\u0010$\u001a\u00020\u0016H$J(\u0010%\u001a\u00020\u00132\u0006\u0010\u001d\u001a\u00020\u00162\u0006\u0010\u001e\u001a\u00020\u001f2\u0006\u0010 \u001a\u00020\r2\u0006\u0010!\u001a\u00020\rH$J&\u0010&\u001a\u00020\r2\u0006\u0010\u001d\u001a\u00020\u00162\u0006\u0010\u001e\u001a\u00020\u001f2\u0006\u0010 \u001a\u00020\r2\u0006\u0010!\u001a\u00020\rJ\u001e\u0010&\u001a\u00020\u00162\u0006\u0010\u001d\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020'2\u0006\u0010!\u001a\u00020\u0016J \u0010(\u001a\u00020\u00162\u0006\u0010\u001d\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020'2\u0006\u0010!\u001a\u00020\u0016H\u0002J\u0016\u0010)\u001a\u00020\u00132\u0006\u0010\u0017\u001a\u00020\u00112\u0006\u0010\u0015\u001a\u00020\u0016J\u0016\u0010)\u001a\u00020\u00132\u0006\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u0015\u001a\u00020\u0016J\u000e\u0010*\u001a\u00020\u00132\u0006\u0010#\u001a\u00020\u0016J\u0010\u0010\u0017\u001a\u00020\u00112\b\b\u0002\u0010\u001d\u001a\u00020\u0016J\u0006\u0010#\u001a\u00020\u0016J\u0010\u0010\u0018\u001a\u00020\u00192\b\b\u0002\u0010\u001d\u001a\u00020\u0016J&\u0010+\u001a\u00020\u00132\u0006\u0010\u001d\u001a\u00020\u00162\u0006\u0010\u001e\u001a\u00020\u001f2\u0006\u0010 \u001a\u00020\r2\u0006\u0010!\u001a\u00020\rJ\u001e\u0010+\u001a\u00020\u00132\u0006\u0010\u001d\u001a\u00020\u00162\u0006\u0010\u0018\u001a\u00020'2\u0006\u0010!\u001a\u00020\u0016J \u0010,\u001a\u00020\u00132\u0006\u0010\u001d\u001a\u00020\u00162\u0006\u0010\u0018\u001a\u00020'2\u0006\u0010!\u001a\u00020\u0016H\u0002R\u000e\u0010\u0006\u001a\u00020\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0015\u0010\u0007\u001a\u00060\bj\u0002`\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u000e\u0010\f\u001a\u00020\rX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0011\u0010\u0003\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000f\u00a8\u0006/"}, d2={"Lokio/FileHandle;", "Ljava/io/Closeable;", "Lokio/Closeable;", "readWrite", "", "(Z)V", "closed", "lock", "Ljava/util/concurrent/locks/ReentrantLock;", "Lokio/Lock;", "getLock", "()Ljava/util/concurrent/locks/ReentrantLock;", "openStreamCount", "", "getReadWrite", "()Z", "appendingSink", "Lokio/Sink;", "close", "", "flush", "position", "", "sink", "source", "Lokio/Source;", "protectedClose", "protectedFlush", "protectedRead", "fileOffset", "array", "", "arrayOffset", "byteCount", "protectedResize", "size", "protectedSize", "protectedWrite", "read", "Lokio/Buffer;", "readNoCloseCheck", "reposition", "resize", "write", "writeNoCloseCheck", "FileHandleSink", "FileHandleSource", "okio"})
@SourceDebugExtension(value={"SMAP\nFileHandle.kt\nKotlin\n*S Kotlin\n*F\n+ 1 FileHandle.kt\nokio/FileHandle\n+ 2 -JvmPlatform.kt\nokio/_JvmPlatformKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 RealBufferedSource.kt\nokio/RealBufferedSource\n+ 5 RealBufferedSink.kt\nokio/RealBufferedSink\n+ 6 Util.kt\nokio/-SegmentedByteString\n*L\n1#1,444:1\n33#2:445\n33#2:447\n33#2:448\n33#2:449\n33#2:450\n33#2:451\n33#2:452\n33#2:453\n33#2:457\n33#2:459\n1#3:446\n62#4:454\n62#4:455\n62#4:456\n51#5:458\n86#6:460\n86#6:461\n*S KotlinDebug\n*F\n+ 1 FileHandle.kt\nokio/FileHandle\n*L\n69#1:445\n81#1:447\n92#1:448\n105#1:449\n119#1:450\n129#1:451\n139#1:452\n151#1:453\n221#1:457\n287#1:459\n169#1:454\n195#1:455\n202#1:456\n248#1:458\n345#1:460\n374#1:461\n*E\n"})
public abstract class FileHandle
implements Closeable {
    private final boolean readWrite;
    private boolean closed;
    private int openStreamCount;
    @NotNull
    private final ReentrantLock lock;

    public FileHandle(boolean readWrite) {
        this.readWrite = readWrite;
        this.lock = _JvmPlatformKt.newLock();
    }

    public final boolean getReadWrite() {
        return this.readWrite;
    }

    @NotNull
    public final ReentrantLock getLock() {
        return this.lock;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final int read(long fileOffset, @NotNull byte[] array, int arrayOffset, int byteCount) throws IOException {
        Intrinsics.checkNotNullParameter(array, "array");
        ReentrantLock $this$withLock$iv = this.lock;
        boolean $i$f$withLock = false;
        Lock lock = $this$withLock$iv;
        lock.lock();
        try {
            boolean bl = false;
            if (!(!this.closed)) {
                boolean bl2 = false;
                String string = "closed";
                throw new IllegalStateException(string.toString());
            }
            Unit unit = Unit.INSTANCE;
        }
        finally {
            lock.unlock();
        }
        return this.protectedRead(fileOffset, array, arrayOffset, byteCount);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final long read(long fileOffset, @NotNull Buffer sink2, long byteCount) throws IOException {
        Intrinsics.checkNotNullParameter(sink2, "sink");
        ReentrantLock $this$withLock$iv = this.lock;
        boolean $i$f$withLock = false;
        Lock lock = $this$withLock$iv;
        lock.lock();
        try {
            boolean bl = false;
            if (!(!this.closed)) {
                boolean bl2 = false;
                String string = "closed";
                throw new IllegalStateException(string.toString());
            }
            Unit unit = Unit.INSTANCE;
        }
        finally {
            lock.unlock();
        }
        return this.readNoCloseCheck(fileOffset, sink2, byteCount);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final long size() throws IOException {
        ReentrantLock $this$withLock$iv = this.lock;
        boolean $i$f$withLock = false;
        Lock lock = $this$withLock$iv;
        lock.lock();
        try {
            boolean bl = false;
            if (!(!this.closed)) {
                boolean bl2 = false;
                String string = "closed";
                throw new IllegalStateException(string.toString());
            }
            Unit unit = Unit.INSTANCE;
        }
        finally {
            lock.unlock();
        }
        return this.protectedSize();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void resize(long size) throws IOException {
        if (!this.readWrite) {
            boolean $i$a$-check-FileHandle$resize$22 = false;
            String $i$a$-check-FileHandle$resize$22 = "file handle is read-only";
            throw new IllegalStateException($i$a$-check-FileHandle$resize$22.toString());
        }
        ReentrantLock $this$withLock$iv = this.lock;
        boolean $i$f$withLock = false;
        Lock lock = $this$withLock$iv;
        lock.lock();
        try {
            boolean bl = false;
            if (!(!this.closed)) {
                boolean bl2 = false;
                String string = "closed";
                throw new IllegalStateException(string.toString());
            }
            Unit unit = Unit.INSTANCE;
        }
        finally {
            lock.unlock();
        }
        this.protectedResize(size);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void write(long fileOffset, @NotNull byte[] array, int arrayOffset, int byteCount) {
        Intrinsics.checkNotNullParameter(array, "array");
        if (!this.readWrite) {
            boolean $i$a$-check-FileHandle$write$22 = false;
            String $i$a$-check-FileHandle$write$22 = "file handle is read-only";
            throw new IllegalStateException($i$a$-check-FileHandle$write$22.toString());
        }
        ReentrantLock $this$withLock$iv = this.lock;
        boolean $i$f$withLock = false;
        Lock lock = $this$withLock$iv;
        lock.lock();
        try {
            boolean bl = false;
            if (!(!this.closed)) {
                boolean bl2 = false;
                String string = "closed";
                throw new IllegalStateException(string.toString());
            }
            Unit unit = Unit.INSTANCE;
        }
        finally {
            lock.unlock();
        }
        this.protectedWrite(fileOffset, array, arrayOffset, byteCount);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void write(long fileOffset, @NotNull Buffer source2, long byteCount) throws IOException {
        Intrinsics.checkNotNullParameter(source2, "source");
        if (!this.readWrite) {
            boolean $i$a$-check-FileHandle$write$42 = false;
            String $i$a$-check-FileHandle$write$42 = "file handle is read-only";
            throw new IllegalStateException($i$a$-check-FileHandle$write$42.toString());
        }
        ReentrantLock $this$withLock$iv = this.lock;
        boolean $i$f$withLock = false;
        Lock lock = $this$withLock$iv;
        lock.lock();
        try {
            boolean bl = false;
            if (!(!this.closed)) {
                boolean bl2 = false;
                String string = "closed";
                throw new IllegalStateException(string.toString());
            }
            Unit unit = Unit.INSTANCE;
        }
        finally {
            lock.unlock();
        }
        this.writeNoCloseCheck(fileOffset, source2, byteCount);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void flush() throws IOException {
        if (!this.readWrite) {
            boolean $i$a$-check-FileHandle$flush$22 = false;
            String $i$a$-check-FileHandle$flush$22 = "file handle is read-only";
            throw new IllegalStateException($i$a$-check-FileHandle$flush$22.toString());
        }
        ReentrantLock $this$withLock$iv = this.lock;
        boolean $i$f$withLock = false;
        Lock lock = $this$withLock$iv;
        lock.lock();
        try {
            boolean bl = false;
            if (!(!this.closed)) {
                boolean bl2 = false;
                String string = "closed";
                throw new IllegalStateException(string.toString());
            }
            Unit unit = Unit.INSTANCE;
        }
        finally {
            lock.unlock();
        }
        this.protectedFlush();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @NotNull
    public final Source source(long fileOffset) throws IOException {
        ReentrantLock $this$withLock$iv = this.lock;
        boolean $i$f$withLock = false;
        Lock lock = $this$withLock$iv;
        lock.lock();
        try {
            boolean bl = false;
            if (!(!this.closed)) {
                boolean bl2 = false;
                String string = "closed";
                throw new IllegalStateException(string.toString());
            }
            int n = this.openStreamCount;
            this.openStreamCount = n + 1;
            int n2 = n;
        }
        finally {
            lock.unlock();
        }
        return new FileHandleSource(this, fileOffset);
    }

    public static /* synthetic */ Source source$default(FileHandle fileHandle, long l, int n, Object object) throws IOException {
        if (object != null) {
            throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: source");
        }
        if ((n & 1) != 0) {
            l = 0L;
        }
        return fileHandle.source(l);
    }

    public final long position(@NotNull Source source2) throws IOException {
        Intrinsics.checkNotNullParameter(source2, "source");
        Source source3 = source2;
        long bufferSize = 0L;
        if (source3 instanceof RealBufferedSource) {
            RealBufferedSource this_$iv = (RealBufferedSource)source3;
            boolean $i$f$getBuffer = false;
            bufferSize = this_$iv.bufferField.size();
            source3 = ((RealBufferedSource)source3).source;
        }
        if (!(source3 instanceof FileHandleSource && ((FileHandleSource)source3).getFileHandle() == this)) {
            boolean $i$a$-require-FileHandle$position$22 = false;
            String $i$a$-require-FileHandle$position$22 = "source was not created by this FileHandle";
            throw new IllegalArgumentException($i$a$-require-FileHandle$position$22.toString());
        }
        if (!(!((FileHandleSource)source3).getClosed())) {
            boolean bl = false;
            String string = "closed";
            throw new IllegalStateException(string.toString());
        }
        return ((FileHandleSource)source3).getPosition() - bufferSize;
    }

    public final void reposition(@NotNull Source source2, long position) throws IOException {
        Intrinsics.checkNotNullParameter(source2, "source");
        if (source2 instanceof RealBufferedSource) {
            Source fileHandleSource = ((RealBufferedSource)source2).source;
            if (!(fileHandleSource instanceof FileHandleSource && ((FileHandleSource)fileHandleSource).getFileHandle() == this)) {
                boolean $i$a$-require-FileHandle$reposition$22 = false;
                String $i$a$-require-FileHandle$reposition$22 = "source was not created by this FileHandle";
                throw new IllegalArgumentException($i$a$-require-FileHandle$reposition$22.toString());
            }
            if (!(!((FileHandleSource)fileHandleSource).getClosed())) {
                boolean bl = false;
                String string = "closed";
                throw new IllegalStateException(string.toString());
            }
            RealBufferedSource this_$iv = (RealBufferedSource)source2;
            boolean $i$f$getBuffer = false;
            long bufferSize = this_$iv.bufferField.size();
            long toSkip = position - (((FileHandleSource)fileHandleSource).getPosition() - bufferSize);
            boolean bl = 0L <= toSkip ? toSkip < bufferSize : false;
            if (bl) {
                ((RealBufferedSource)source2).skip(toSkip);
            } else {
                RealBufferedSource this_$iv2 = (RealBufferedSource)source2;
                boolean $i$f$getBuffer2 = false;
                this_$iv2.bufferField.clear();
                ((FileHandleSource)fileHandleSource).setPosition(position);
            }
        } else {
            if (!(source2 instanceof FileHandleSource && ((FileHandleSource)source2).getFileHandle() == this)) {
                boolean $i$a$-require-FileHandle$reposition$42 = false;
                String $i$a$-require-FileHandle$reposition$42 = "source was not created by this FileHandle";
                throw new IllegalArgumentException($i$a$-require-FileHandle$reposition$42.toString());
            }
            if (!(!((FileHandleSource)source2).getClosed())) {
                boolean bl = false;
                String string = "closed";
                throw new IllegalStateException(string.toString());
            }
            ((FileHandleSource)source2).setPosition(position);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @NotNull
    public final Sink sink(long fileOffset) throws IOException {
        if (!this.readWrite) {
            boolean $i$a$-check-FileHandle$sink$22 = false;
            String $i$a$-check-FileHandle$sink$22 = "file handle is read-only";
            throw new IllegalStateException($i$a$-check-FileHandle$sink$22.toString());
        }
        ReentrantLock $this$withLock$iv = this.lock;
        boolean $i$f$withLock = false;
        Lock lock = $this$withLock$iv;
        lock.lock();
        try {
            boolean bl = false;
            if (!(!this.closed)) {
                boolean bl2 = false;
                String string = "closed";
                throw new IllegalStateException(string.toString());
            }
            int n = this.openStreamCount;
            this.openStreamCount = n + 1;
            int n2 = n;
        }
        finally {
            lock.unlock();
        }
        return new FileHandleSink(this, fileOffset);
    }

    public static /* synthetic */ Sink sink$default(FileHandle fileHandle, long l, int n, Object object) throws IOException {
        if (object != null) {
            throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: sink");
        }
        if ((n & 1) != 0) {
            l = 0L;
        }
        return fileHandle.sink(l);
    }

    @NotNull
    public final Sink appendingSink() throws IOException {
        return this.sink(this.size());
    }

    public final long position(@NotNull Sink sink2) throws IOException {
        Intrinsics.checkNotNullParameter(sink2, "sink");
        Sink sink3 = sink2;
        long bufferSize = 0L;
        if (sink3 instanceof RealBufferedSink) {
            RealBufferedSink this_$iv = (RealBufferedSink)sink3;
            boolean $i$f$getBuffer = false;
            bufferSize = this_$iv.bufferField.size();
            sink3 = ((RealBufferedSink)sink3).sink;
        }
        if (!(sink3 instanceof FileHandleSink && ((FileHandleSink)sink3).getFileHandle() == this)) {
            boolean $i$a$-require-FileHandle$position$42 = false;
            String $i$a$-require-FileHandle$position$42 = "sink was not created by this FileHandle";
            throw new IllegalArgumentException($i$a$-require-FileHandle$position$42.toString());
        }
        if (!(!((FileHandleSink)sink3).getClosed())) {
            boolean bl = false;
            String string = "closed";
            throw new IllegalStateException(string.toString());
        }
        return ((FileHandleSink)sink3).getPosition() + bufferSize;
    }

    public final void reposition(@NotNull Sink sink2, long position) throws IOException {
        Intrinsics.checkNotNullParameter(sink2, "sink");
        if (sink2 instanceof RealBufferedSink) {
            Sink fileHandleSink = ((RealBufferedSink)sink2).sink;
            if (!(fileHandleSink instanceof FileHandleSink && ((FileHandleSink)fileHandleSink).getFileHandle() == this)) {
                boolean $i$a$-require-FileHandle$reposition$62 = false;
                String $i$a$-require-FileHandle$reposition$62 = "sink was not created by this FileHandle";
                throw new IllegalArgumentException($i$a$-require-FileHandle$reposition$62.toString());
            }
            if (!(!((FileHandleSink)fileHandleSink).getClosed())) {
                boolean bl = false;
                String string = "closed";
                throw new IllegalStateException(string.toString());
            }
            ((RealBufferedSink)sink2).emit();
            ((FileHandleSink)fileHandleSink).setPosition(position);
        } else {
            if (!(sink2 instanceof FileHandleSink && ((FileHandleSink)sink2).getFileHandle() == this)) {
                boolean $i$a$-require-FileHandle$reposition$82 = false;
                String $i$a$-require-FileHandle$reposition$82 = "sink was not created by this FileHandle";
                throw new IllegalArgumentException($i$a$-require-FileHandle$reposition$82.toString());
            }
            if (!(!((FileHandleSink)sink2).getClosed())) {
                boolean bl = false;
                String string = "closed";
                throw new IllegalStateException(string.toString());
            }
            ((FileHandleSink)sink2).setPosition(position);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final void close() throws IOException {
        ReentrantLock $this$withLock$iv = this.lock;
        boolean $i$f$withLock = false;
        Lock lock = $this$withLock$iv;
        lock.lock();
        try {
            boolean bl = false;
            if (this.closed) {
                return;
            }
            this.closed = true;
            if (this.openStreamCount != 0) {
                return;
            }
            Unit unit = Unit.INSTANCE;
        }
        finally {
            lock.unlock();
        }
        this.protectedClose();
    }

    protected abstract int protectedRead(long var1, @NotNull byte[] var3, int var4, int var5) throws IOException;

    protected abstract void protectedWrite(long var1, @NotNull byte[] var3, int var4, int var5) throws IOException;

    protected abstract void protectedFlush() throws IOException;

    protected abstract void protectedResize(long var1) throws IOException;

    protected abstract long protectedSize() throws IOException;

    protected abstract void protectedClose() throws IOException;

    /*
     * WARNING - void declaration
     */
    private final long readNoCloseCheck(long fileOffset, Buffer sink2, long byteCount) {
        long currentOffset;
        int readByteCount;
        if (!(byteCount >= 0L)) {
            boolean bl = false;
            String string = "byteCount < 0: " + byteCount;
            throw new IllegalArgumentException(string.toString());
        }
        long targetOffset = fileOffset + byteCount;
        for (currentOffset = fileOffset; currentOffset < targetOffset; currentOffset += (long)readByteCount) {
            void a$iv;
            Segment tail = sink2.writableSegment$okio(1);
            long l = targetOffset - currentOffset;
            int b$iv = 8192 - tail.limit;
            boolean $i$f$minOf = false;
            readByteCount = this.protectedRead(currentOffset, tail.data, tail.limit, (int)Math.min((long)a$iv, (long)b$iv));
            if (readByteCount == -1) {
                if (tail.pos == tail.limit) {
                    sink2.head = tail.pop();
                    SegmentPool.recycle(tail);
                }
                if (fileOffset != currentOffset) break;
                return -1L;
            }
            tail.limit += readByteCount;
            sink2.setSize$okio(sink2.size() + (long)readByteCount);
        }
        return currentOffset - fileOffset;
    }

    /*
     * WARNING - void declaration
     */
    private final void writeNoCloseCheck(long fileOffset, Buffer source2, long byteCount) {
        int toCopy;
        -SegmentedByteString.checkOffsetAndCount(source2.size(), 0L, byteCount);
        long targetOffset = fileOffset + byteCount;
        for (long currentOffset = fileOffset; currentOffset < targetOffset; currentOffset += (long)toCopy) {
            void a$iv;
            Segment head;
            Intrinsics.checkNotNull(source2.head);
            long l = targetOffset - currentOffset;
            int b$iv = head.limit - head.pos;
            boolean $i$f$minOf = false;
            toCopy = (int)Math.min((long)a$iv, (long)b$iv);
            this.protectedWrite(currentOffset, head.data, head.pos, toCopy);
            head.pos += toCopy;
            source2.setSize$okio(source2.size() - (long)toCopy);
            if (head.pos != head.limit) continue;
            source2.head = head.pop();
            SegmentPool.recycle(head);
        }
    }

    @Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u00008\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u000b\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\b\u0010\u0013\u001a\u00020\u0014H\u0016J\b\u0010\u0015\u001a\u00020\u0014H\u0016J\b\u0010\u0016\u001a\u00020\u0017H\u0016J\u0018\u0010\u0018\u001a\u00020\u00142\u0006\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u0005H\u0016R\u001a\u0010\u0007\u001a\u00020\bX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\t\u0010\n\"\u0004\b\u000b\u0010\fR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u001a\u0010\u0004\u001a\u00020\u0005X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u000f\u0010\u0010\"\u0004\b\u0011\u0010\u0012\u00a8\u0006\u001c"}, d2={"Lokio/FileHandle$FileHandleSink;", "Lokio/Sink;", "fileHandle", "Lokio/FileHandle;", "position", "", "(Lokio/FileHandle;J)V", "closed", "", "getClosed", "()Z", "setClosed", "(Z)V", "getFileHandle", "()Lokio/FileHandle;", "getPosition", "()J", "setPosition", "(J)V", "close", "", "flush", "timeout", "Lokio/Timeout;", "write", "source", "Lokio/Buffer;", "byteCount", "okio"})
    @SourceDebugExtension(value={"SMAP\nFileHandle.kt\nKotlin\n*S Kotlin\n*F\n+ 1 FileHandle.kt\nokio/FileHandle$FileHandleSink\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 -JvmPlatform.kt\nokio/_JvmPlatformKt\n*L\n1#1,444:1\n1#2:445\n33#3:446\n*S KotlinDebug\n*F\n+ 1 FileHandle.kt\nokio/FileHandle$FileHandleSink\n*L\n410#1:446\n*E\n"})
    private static final class FileHandleSink
    implements Sink {
        @NotNull
        private final FileHandle fileHandle;
        private long position;
        private boolean closed;

        public FileHandleSink(@NotNull FileHandle fileHandle, long position) {
            Intrinsics.checkNotNullParameter(fileHandle, "fileHandle");
            this.fileHandle = fileHandle;
            this.position = position;
        }

        @NotNull
        public final FileHandle getFileHandle() {
            return this.fileHandle;
        }

        public final long getPosition() {
            return this.position;
        }

        public final void setPosition(long l) {
            this.position = l;
        }

        public final boolean getClosed() {
            return this.closed;
        }

        public final void setClosed(boolean bl) {
            this.closed = bl;
        }

        @Override
        public void write(@NotNull Buffer source2, long byteCount) {
            Intrinsics.checkNotNullParameter(source2, "source");
            if (!(!this.closed)) {
                boolean bl = false;
                String string = "closed";
                throw new IllegalStateException(string.toString());
            }
            this.fileHandle.writeNoCloseCheck(this.position, source2, byteCount);
            this.position += byteCount;
        }

        @Override
        public void flush() {
            if (!(!this.closed)) {
                boolean bl = false;
                String string = "closed";
                throw new IllegalStateException(string.toString());
            }
            this.fileHandle.protectedFlush();
        }

        @Override
        @NotNull
        public Timeout timeout() {
            return Timeout.NONE;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void close() {
            if (this.closed) {
                return;
            }
            this.closed = true;
            ReentrantLock $this$withLock$iv = this.fileHandle.getLock();
            boolean $i$f$withLock = false;
            Lock lock = $this$withLock$iv;
            lock.lock();
            try {
                boolean bl = false;
                FileHandle fileHandle = this.fileHandle;
                int n = fileHandle.openStreamCount;
                fileHandle.openStreamCount = n + -1;
                if (this.fileHandle.openStreamCount != 0 || !this.fileHandle.closed) {
                    return;
                }
                Unit unit = Unit.INSTANCE;
            }
            finally {
                lock.unlock();
            }
            this.fileHandle.protectedClose();
        }
    }

    @Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u00006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u000b\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u0002\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\b\u0010\u0013\u001a\u00020\u0014H\u0016J\u0018\u0010\u0015\u001a\u00020\u00052\u0006\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u0005H\u0016J\b\u0010\u0019\u001a\u00020\u001aH\u0016R\u001a\u0010\u0007\u001a\u00020\bX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\t\u0010\n\"\u0004\b\u000b\u0010\fR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u001a\u0010\u0004\u001a\u00020\u0005X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u000f\u0010\u0010\"\u0004\b\u0011\u0010\u0012\u00a8\u0006\u001b"}, d2={"Lokio/FileHandle$FileHandleSource;", "Lokio/Source;", "fileHandle", "Lokio/FileHandle;", "position", "", "(Lokio/FileHandle;J)V", "closed", "", "getClosed", "()Z", "setClosed", "(Z)V", "getFileHandle", "()Lokio/FileHandle;", "getPosition", "()J", "setPosition", "(J)V", "close", "", "read", "sink", "Lokio/Buffer;", "byteCount", "timeout", "Lokio/Timeout;", "okio"})
    @SourceDebugExtension(value={"SMAP\nFileHandle.kt\nKotlin\n*S Kotlin\n*F\n+ 1 FileHandle.kt\nokio/FileHandle$FileHandleSource\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 -JvmPlatform.kt\nokio/_JvmPlatformKt\n*L\n1#1,444:1\n1#2:445\n33#3:446\n*S KotlinDebug\n*F\n+ 1 FileHandle.kt\nokio/FileHandle$FileHandleSource\n*L\n436#1:446\n*E\n"})
    private static final class FileHandleSource
    implements Source {
        @NotNull
        private final FileHandle fileHandle;
        private long position;
        private boolean closed;

        public FileHandleSource(@NotNull FileHandle fileHandle, long position) {
            Intrinsics.checkNotNullParameter(fileHandle, "fileHandle");
            this.fileHandle = fileHandle;
            this.position = position;
        }

        @NotNull
        public final FileHandle getFileHandle() {
            return this.fileHandle;
        }

        public final long getPosition() {
            return this.position;
        }

        public final void setPosition(long l) {
            this.position = l;
        }

        public final boolean getClosed() {
            return this.closed;
        }

        public final void setClosed(boolean bl) {
            this.closed = bl;
        }

        @Override
        public long read(@NotNull Buffer sink2, long byteCount) {
            Intrinsics.checkNotNullParameter(sink2, "sink");
            if (!(!this.closed)) {
                boolean bl = false;
                String string = "closed";
                throw new IllegalStateException(string.toString());
            }
            long result = this.fileHandle.readNoCloseCheck(this.position, sink2, byteCount);
            if (result != -1L) {
                this.position += result;
            }
            return result;
        }

        @Override
        @NotNull
        public Timeout timeout() {
            return Timeout.NONE;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void close() {
            if (this.closed) {
                return;
            }
            this.closed = true;
            ReentrantLock $this$withLock$iv = this.fileHandle.getLock();
            boolean $i$f$withLock = false;
            Lock lock = $this$withLock$iv;
            lock.lock();
            try {
                boolean bl = false;
                FileHandle fileHandle = this.fileHandle;
                int n = fileHandle.openStreamCount;
                fileHandle.openStreamCount = n + -1;
                if (this.fileHandle.openStreamCount != 0 || !this.fileHandle.closed) {
                    return;
                }
                Unit unit = Unit.INSTANCE;
            }
            finally {
                lock.unlock();
            }
            this.fileHandle.protectedClose();
        }
    }
}

