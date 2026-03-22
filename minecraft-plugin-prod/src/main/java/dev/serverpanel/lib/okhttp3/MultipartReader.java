/*
 * Decompiled with CFR 0.152.
 */
package dev.serverpanel.lib.okhttp3;

import dev.serverpanel.lib.okhttp3.Headers;
import dev.serverpanel.lib.okhttp3.MediaType;
import dev.serverpanel.lib.okhttp3.ResponseBody;
import dev.serverpanel.lib.okhttp3.internal.http1.HeadersReader;
import java.io.Closeable;
import java.io.IOException;
import java.net.ProtocolException;
import java.util.concurrent.TimeUnit;
import kotlin.Metadata;
import kotlin.jvm.JvmName;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import okio.Buffer;
import okio.BufferedSource;
import okio.ByteString;
import okio.Okio;
import okio.Options;
import okio.Source;
import okio.Timeout;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 8, 0}, k=1, xi=48, d1={"\u0000P\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u0000 \u001c2\u00020\u0001:\u0003\u001c\u001d\u001eB\u000f\b\u0016\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004B\u0015\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\b\u00a2\u0006\u0002\u0010\tJ\b\u0010\u0015\u001a\u00020\u0016H\u0016J\u0010\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\u0018H\u0002J\b\u0010\u001a\u001a\u0004\u0018\u00010\u001bR\u0013\u0010\u0007\u001a\u00020\b8\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\nR\u000e\u0010\u000b\u001a\u00020\fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000eX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u000f\u001a\b\u0018\u00010\u0010R\u00020\u0000X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u000eX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u0014X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001f"}, d2={"Ldev/serverpanel/lib/okhttp3/MultipartReader;", "Ljava/io/Closeable;", "response", "Ldev/serverpanel/lib/okhttp3/ResponseBody;", "(Lokhttp3/ResponseBody;)V", "source", "Lokio/BufferedSource;", "boundary", "", "(Lokio/BufferedSource;Ljava/lang/String;)V", "()Ljava/lang/String;", "closed", "", "crlfDashDashBoundary", "Lokio/ByteString;", "currentPart", "Ldev/serverpanel/lib/okhttp3/MultipartReader$PartSource;", "dashDashBoundary", "noMoreParts", "partCount", "", "close", "", "currentPartBytesRemaining", "", "maxResult", "nextPart", "Ldev/serverpanel/lib/okhttp3/MultipartReader$Part;", "Companion", "Part", "PartSource", "okhttp"})
@SourceDebugExtension(value={"SMAP\nMultipartReader.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MultipartReader.kt\nokhttp3/MultipartReader\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,210:1\n1#2:211\n*E\n"})
public final class MultipartReader
implements Closeable {
    @NotNull
    public static final Companion Companion = new Companion(null);
    @NotNull
    private final BufferedSource source;
    @NotNull
    private final String boundary;
    @NotNull
    private final ByteString dashDashBoundary;
    @NotNull
    private final ByteString crlfDashDashBoundary;
    private int partCount;
    private boolean closed;
    private boolean noMoreParts;
    @Nullable
    private PartSource currentPart;
    @NotNull
    private static final Options afterBoundaryOptions;

    public MultipartReader(@NotNull BufferedSource source2, @NotNull String boundary) throws IOException {
        Intrinsics.checkNotNullParameter(source2, "source");
        Intrinsics.checkNotNullParameter(boundary, "boundary");
        this.source = source2;
        this.boundary = boundary;
        this.dashDashBoundary = new Buffer().writeUtf8("--").writeUtf8(this.boundary).readByteString();
        this.crlfDashDashBoundary = new Buffer().writeUtf8("\r\n--").writeUtf8(this.boundary).readByteString();
    }

    @JvmName(name="boundary")
    @NotNull
    public final String boundary() {
        return this.boundary;
    }

    public MultipartReader(@NotNull ResponseBody response) throws IOException {
        Intrinsics.checkNotNullParameter(response, "response");
        Object object = response.contentType();
        if (object == null || (object = ((MediaType)object).parameter("boundary")) == null) {
            throw new ProtocolException("expected the Content-Type to have a boundary parameter");
        }
        this(response.source(), (String)object);
    }

    @Nullable
    public final Part nextPart() throws IOException {
        PartSource partSource;
        if (!(!this.closed)) {
            boolean $i$a$-check-MultipartReader$nextPart$22 = false;
            String $i$a$-check-MultipartReader$nextPart$22 = "closed";
            throw new IllegalStateException($i$a$-check-MultipartReader$nextPart$22.toString());
        }
        if (this.noMoreParts) {
            return null;
        }
        if (this.partCount == 0 && this.source.rangeEquals(0L, this.dashDashBoundary)) {
            this.source.skip(this.dashDashBoundary.size());
        } else {
            long toSkip;
            while ((toSkip = this.currentPartBytesRemaining(8192L)) != 0L) {
                this.source.skip(toSkip);
            }
            this.source.skip(this.crlfDashDashBoundary.size());
        }
        boolean whitespace = false;
        block7: while (true) {
            switch (this.source.select(afterBoundaryOptions)) {
                case 0: {
                    int n = this.partCount;
                    this.partCount = n + 1;
                    break block7;
                }
                case 1: {
                    if (whitespace) {
                        throw new ProtocolException("unexpected characters after boundary");
                    }
                    if (this.partCount == 0) {
                        throw new ProtocolException("expected at least 1 part");
                    }
                    this.noMoreParts = true;
                    return null;
                }
                case 2: 
                case 3: {
                    whitespace = true;
                    continue block7;
                }
                case -1: {
                    throw new ProtocolException("unexpected characters after boundary");
                }
                default: {
                    continue block7;
                }
            }
            break;
        }
        Headers headers = new HeadersReader(this.source).readHeaders();
        this.currentPart = partSource = new PartSource();
        return new Part(headers, Okio.buffer(partSource));
    }

    private final long currentPartBytesRemaining(long maxResult) {
        this.source.require(this.crlfDashDashBoundary.size());
        long delimiterIndex = this.source.getBuffer().indexOf(this.crlfDashDashBoundary);
        return delimiterIndex == -1L ? Math.min(maxResult, this.source.getBuffer().size() - (long)this.crlfDashDashBoundary.size() + 1L) : Math.min(maxResult, delimiterIndex);
    }

    @Override
    public void close() throws IOException {
        if (this.closed) {
            return;
        }
        this.closed = true;
        this.currentPart = null;
        this.source.close();
    }

    static {
        ByteString[] byteStringArray = new ByteString[]{ByteString.Companion.encodeUtf8("\r\n"), ByteString.Companion.encodeUtf8("--"), ByteString.Companion.encodeUtf8(" "), ByteString.Companion.encodeUtf8("\t")};
        afterBoundaryOptions = Options.Companion.of(byteStringArray);
    }

    @Metadata(mv={1, 8, 0}, k=1, xi=48, d1={"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0080\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u0011\u0010\u0003\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006\u00a8\u0006\u0007"}, d2={"Ldev/serverpanel/lib/okhttp3/MultipartReader$Companion;", "", "()V", "afterBoundaryOptions", "Lokio/Options;", "getAfterBoundaryOptions", "()Lokio/Options;", "okhttp"})
    public static final class Companion {
        private Companion() {
        }

        @NotNull
        public final Options getAfterBoundaryOptions() {
            return afterBoundaryOptions;
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }

    @Metadata(mv={1, 8, 0}, k=1, xi=48, d1={"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\t\u0010\t\u001a\u00020\nH\u0096\u0001R\u0013\u0010\u0004\u001a\u00020\u00058\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0004\u0010\u0007R\u0013\u0010\u0002\u001a\u00020\u00038\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0002\u0010\b\u00a8\u0006\u000b"}, d2={"Ldev/serverpanel/lib/okhttp3/MultipartReader$Part;", "Ljava/io/Closeable;", "headers", "Ldev/serverpanel/lib/okhttp3/Headers;", "body", "Lokio/BufferedSource;", "(Lokhttp3/Headers;Lokio/BufferedSource;)V", "()Lokio/BufferedSource;", "()Lokhttp3/Headers;", "close", "", "okhttp"})
    public static final class Part
    implements Closeable {
        @NotNull
        private final Headers headers;
        @NotNull
        private final BufferedSource body;

        public Part(@NotNull Headers headers, @NotNull BufferedSource body) {
            Intrinsics.checkNotNullParameter(headers, "headers");
            Intrinsics.checkNotNullParameter(body, "body");
            this.headers = headers;
            this.body = body;
        }

        @JvmName(name="headers")
        @NotNull
        public final Headers headers() {
            return this.headers;
        }

        @JvmName(name="body")
        @NotNull
        public final BufferedSource body() {
            return this.body;
        }

        @Override
        public void close() {
            this.body.close();
        }
    }

    @Metadata(mv={1, 8, 0}, k=1, xi=48, d1={"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0082\u0004\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0005\u001a\u00020\u0006H\u0016J\u0018\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\bH\u0016J\b\u0010\u0003\u001a\u00020\u0004H\u0016R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\f"}, d2={"Ldev/serverpanel/lib/okhttp3/MultipartReader$PartSource;", "Lokio/Source;", "(Lokhttp3/MultipartReader;)V", "timeout", "Lokio/Timeout;", "close", "", "read", "", "sink", "Lokio/Buffer;", "byteCount", "okhttp"})
    @SourceDebugExtension(value={"SMAP\nMultipartReader.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MultipartReader.kt\nokhttp3/MultipartReader$PartSource\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 Timeout.kt\nokio/Timeout\n*L\n1#1,210:1\n1#2:211\n268#3,26:212\n*S KotlinDebug\n*F\n+ 1 MultipartReader.kt\nokhttp3/MultipartReader$PartSource\n*L\n159#1:212,26\n*E\n"})
    private final class PartSource
    implements Source {
        @NotNull
        private final Timeout timeout = new Timeout();

        @Override
        public void close() {
            if (Intrinsics.areEqual(MultipartReader.this.currentPart, this)) {
                MultipartReader.this.currentPart = null;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         * WARNING - void declaration
         */
        @Override
        public long read(@NotNull Buffer sink2, long byteCount) {
            void other$iv;
            void this_$iv;
            Intrinsics.checkNotNullParameter(sink2, "sink");
            if (!(byteCount >= 0L)) {
                boolean $i$a$-require-MultipartReader$PartSource$read$22 = false;
                String $i$a$-require-MultipartReader$PartSource$read$22 = "byteCount < 0: " + byteCount;
                throw new IllegalArgumentException($i$a$-require-MultipartReader$PartSource$read$22.toString());
            }
            if (!Intrinsics.areEqual(MultipartReader.this.currentPart, this)) {
                boolean $i$a$-check-MultipartReader$PartSource$read$32 = false;
                String $i$a$-check-MultipartReader$PartSource$read$32 = "closed";
                throw new IllegalStateException($i$a$-check-MultipartReader$PartSource$read$32.toString());
            }
            Timeout timeout2 = MultipartReader.this.source.timeout();
            Timeout $i$a$-check-MultipartReader$PartSource$read$32 = this.timeout;
            MultipartReader multipartReader = MultipartReader.this;
            boolean $i$f$intersectWith = false;
            long originalTimeout$iv = this_$iv.timeoutNanos();
            this_$iv.timeout(Timeout.Companion.minTimeout(other$iv.timeoutNanos(), this_$iv.timeoutNanos()), TimeUnit.NANOSECONDS);
            if (this_$iv.hasDeadline()) {
                long originalDeadline$iv = this_$iv.deadlineNanoTime();
                if (other$iv.hasDeadline()) {
                    this_$iv.deadlineNanoTime(Math.min(this_$iv.deadlineNanoTime(), other$iv.deadlineNanoTime()));
                }
                try {
                    boolean bl = false;
                    long limit = multipartReader.currentPartBytesRemaining(byteCount);
                    long l = limit == 0L ? -1L : multipartReader.source.read(sink2, limit);
                    return l;
                }
                finally {
                    this_$iv.timeout(originalTimeout$iv, TimeUnit.NANOSECONDS);
                    if (other$iv.hasDeadline()) {
                        this_$iv.deadlineNanoTime(originalDeadline$iv);
                    }
                }
            }
            if (other$iv.hasDeadline()) {
                this_$iv.deadlineNanoTime(other$iv.deadlineNanoTime());
            }
            try {
                boolean bl = false;
                long limit = multipartReader.currentPartBytesRemaining(byteCount);
                long l = limit == 0L ? -1L : multipartReader.source.read(sink2, limit);
                return l;
            }
            finally {
                this_$iv.timeout(originalTimeout$iv, TimeUnit.NANOSECONDS);
                if (other$iv.hasDeadline()) {
                    this_$iv.clearDeadline();
                }
            }
        }

        @Override
        @NotNull
        public Timeout timeout() {
            return this.timeout;
        }
    }
}

