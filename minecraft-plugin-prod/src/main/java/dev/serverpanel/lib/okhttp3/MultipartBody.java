/*
 * Decompiled with CFR 0.152.
 */
package dev.serverpanel.lib.okhttp3;

import dev.serverpanel.lib.okhttp3.Headers;
import dev.serverpanel.lib.okhttp3.MediaType;
import dev.serverpanel.lib.okhttp3.RequestBody;
import dev.serverpanel.lib.okhttp3.internal.Util;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import kotlin.Deprecated;
import kotlin.DeprecationLevel;
import kotlin.Metadata;
import kotlin.ReplaceWith;
import kotlin.jvm.JvmField;
import kotlin.jvm.JvmName;
import kotlin.jvm.JvmOverloads;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import okio.Buffer;
import okio.BufferedSink;
import okio.ByteString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
@Metadata(mv={1, 8, 0}, k=1, xi=48, d1={"\u0000N\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\n\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\u0018\u0000 #2\u00020\u0001:\u0003\"#$B%\b\u0000\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007\u00a2\u0006\u0002\u0010\tJ\r\u0010\n\u001a\u00020\u000bH\u0007\u00a2\u0006\u0002\b\u0015J\b\u0010\r\u001a\u00020\u000eH\u0016J\b\u0010\u000f\u001a\u00020\u0005H\u0016J\u000e\u0010\u0016\u001a\u00020\b2\u0006\u0010\u0017\u001a\u00020\u0012J\u0013\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007H\u0007\u00a2\u0006\u0002\b\u0018J\r\u0010\u0011\u001a\u00020\u0012H\u0007\u00a2\u0006\u0002\b\u0019J\r\u0010\u0004\u001a\u00020\u0005H\u0007\u00a2\u0006\u0002\b\u001aJ\u001a\u0010\u001b\u001a\u00020\u000e2\b\u0010\u001c\u001a\u0004\u0018\u00010\u001d2\u0006\u0010\u001e\u001a\u00020\u001fH\u0002J\u0010\u0010 \u001a\u00020!2\u0006\u0010\u001c\u001a\u00020\u001dH\u0016R\u0011\u0010\n\u001a\u00020\u000b8G\u00a2\u0006\u0006\u001a\u0004\b\n\u0010\fR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000eX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0019\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u00078\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0010R\u0011\u0010\u0011\u001a\u00020\u00128G\u00a2\u0006\u0006\u001a\u0004\b\u0011\u0010\u0013R\u0013\u0010\u0004\u001a\u00020\u00058\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0004\u0010\u0014\u00a8\u0006%"}, d2={"Ldev/serverpanel/lib/okhttp3/MultipartBody;", "Ldev/serverpanel/lib/okhttp3/RequestBody;", "boundaryByteString", "Lokio/ByteString;", "type", "Ldev/serverpanel/lib/okhttp3/MediaType;", "parts", "", "Ldev/serverpanel/lib/okhttp3/MultipartBody$Part;", "(Lokio/ByteString;Lokhttp3/MediaType;Ljava/util/List;)V", "boundary", "", "()Ljava/lang/String;", "contentLength", "", "contentType", "()Ljava/util/List;", "size", "", "()I", "()Lokhttp3/MediaType;", "-deprecated_boundary", "part", "index", "-deprecated_parts", "-deprecated_size", "-deprecated_type", "writeOrCountBytes", "sink", "Lokio/BufferedSink;", "countBytes", "", "writeTo", "", "Builder", "Companion", "Part", "okhttp"})
public final class MultipartBody
extends RequestBody {
    @NotNull
    public static final Companion Companion = new Companion(null);
    @NotNull
    private final ByteString boundaryByteString;
    @NotNull
    private final MediaType type;
    @NotNull
    private final List<Part> parts;
    @NotNull
    private final MediaType contentType;
    private long contentLength;
    @JvmField
    @NotNull
    public static final MediaType MIXED = MediaType.Companion.get("multipart/mixed");
    @JvmField
    @NotNull
    public static final MediaType ALTERNATIVE = MediaType.Companion.get("multipart/alternative");
    @JvmField
    @NotNull
    public static final MediaType DIGEST = MediaType.Companion.get("multipart/digest");
    @JvmField
    @NotNull
    public static final MediaType PARALLEL = MediaType.Companion.get("multipart/parallel");
    @JvmField
    @NotNull
    public static final MediaType FORM = MediaType.Companion.get("multipart/form-data");
    @NotNull
    private static final byte[] COLONSPACE;
    @NotNull
    private static final byte[] CRLF;
    @NotNull
    private static final byte[] DASHDASH;

    public MultipartBody(@NotNull ByteString boundaryByteString, @NotNull MediaType type, @NotNull List<Part> parts) {
        Intrinsics.checkNotNullParameter(boundaryByteString, "boundaryByteString");
        Intrinsics.checkNotNullParameter(type, "type");
        Intrinsics.checkNotNullParameter(parts, "parts");
        this.boundaryByteString = boundaryByteString;
        this.type = type;
        this.parts = parts;
        this.contentType = MediaType.Companion.get(this.type + "; boundary=" + this.boundary());
        this.contentLength = -1L;
    }

    @JvmName(name="type")
    @NotNull
    public final MediaType type() {
        return this.type;
    }

    @JvmName(name="parts")
    @NotNull
    public final List<Part> parts() {
        return this.parts;
    }

    @JvmName(name="boundary")
    @NotNull
    public final String boundary() {
        return this.boundaryByteString.utf8();
    }

    @JvmName(name="size")
    public final int size() {
        return this.parts.size();
    }

    @NotNull
    public final Part part(int index) {
        return this.parts.get(index);
    }

    @Override
    @NotNull
    public MediaType contentType() {
        return this.contentType;
    }

    @Deprecated(message="moved to val", replaceWith=@ReplaceWith(expression="type", imports={}), level=DeprecationLevel.ERROR)
    @JvmName(name="-deprecated_type")
    @NotNull
    public final MediaType -deprecated_type() {
        return this.type;
    }

    @Deprecated(message="moved to val", replaceWith=@ReplaceWith(expression="boundary", imports={}), level=DeprecationLevel.ERROR)
    @JvmName(name="-deprecated_boundary")
    @NotNull
    public final String -deprecated_boundary() {
        return this.boundary();
    }

    @Deprecated(message="moved to val", replaceWith=@ReplaceWith(expression="size", imports={}), level=DeprecationLevel.ERROR)
    @JvmName(name="-deprecated_size")
    public final int -deprecated_size() {
        return this.size();
    }

    @Deprecated(message="moved to val", replaceWith=@ReplaceWith(expression="parts", imports={}), level=DeprecationLevel.ERROR)
    @JvmName(name="-deprecated_parts")
    @NotNull
    public final List<Part> -deprecated_parts() {
        return this.parts;
    }

    @Override
    public long contentLength() throws IOException {
        long result = this.contentLength;
        if (result == -1L) {
            this.contentLength = result = this.writeOrCountBytes(null, true);
        }
        return result;
    }

    @Override
    public void writeTo(@NotNull BufferedSink sink2) throws IOException {
        Intrinsics.checkNotNullParameter(sink2, "sink");
        this.writeOrCountBytes(sink2, false);
    }

    private final long writeOrCountBytes(BufferedSink sink2, boolean countBytes) throws IOException {
        BufferedSink sink3 = sink2;
        long byteCount = 0L;
        Buffer byteCountBuffer = null;
        if (countBytes) {
            byteCountBuffer = new Buffer();
            sink3 = byteCountBuffer;
        }
        int n = this.parts.size();
        for (int p = 0; p < n; ++p) {
            long contentLength;
            MediaType contentType;
            Part part = this.parts.get(p);
            Headers headers = part.headers();
            RequestBody body = part.body();
            BufferedSink bufferedSink = sink3;
            Intrinsics.checkNotNull(bufferedSink);
            bufferedSink.write(DASHDASH);
            sink3.write(this.boundaryByteString);
            sink3.write(CRLF);
            if (headers != null) {
                int n2 = headers.size();
                for (int h = 0; h < n2; ++h) {
                    sink3.writeUtf8(headers.name(h)).write(COLONSPACE).writeUtf8(headers.value(h)).write(CRLF);
                }
            }
            if ((contentType = body.contentType()) != null) {
                sink3.writeUtf8("Content-Type: ").writeUtf8(contentType.toString()).write(CRLF);
            }
            if ((contentLength = body.contentLength()) != -1L) {
                sink3.writeUtf8("Content-Length: ").writeDecimalLong(contentLength).write(CRLF);
            } else if (countBytes) {
                Buffer buffer = byteCountBuffer;
                Intrinsics.checkNotNull(buffer);
                buffer.clear();
                return -1L;
            }
            sink3.write(CRLF);
            if (countBytes) {
                byteCount += contentLength;
            } else {
                body.writeTo(sink3);
            }
            sink3.write(CRLF);
        }
        BufferedSink bufferedSink = sink3;
        Intrinsics.checkNotNull(bufferedSink);
        bufferedSink.write(DASHDASH);
        sink3.write(this.boundaryByteString);
        sink3.write(DASHDASH);
        sink3.write(CRLF);
        if (countBytes) {
            Buffer buffer = byteCountBuffer;
            Intrinsics.checkNotNull(buffer);
            byteCount += buffer.size();
            byteCountBuffer.clear();
        }
        return byteCount;
    }

    static {
        byte[] byArray = new byte[]{58, 32};
        COLONSPACE = byArray;
        byArray = new byte[]{13, 10};
        CRLF = byArray;
        byArray = new byte[]{45, 45};
        DASHDASH = byArray;
    }

    @Metadata(mv={1, 8, 0}, k=1, xi=48, d1={"\u0000@\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0011\b\u0007\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0016\u0010\u000b\u001a\u00020\u00002\u0006\u0010\f\u001a\u00020\u00032\u0006\u0010\r\u001a\u00020\u0003J \u0010\u000b\u001a\u00020\u00002\u0006\u0010\f\u001a\u00020\u00032\b\u0010\u000e\u001a\u0004\u0018\u00010\u00032\u0006\u0010\u000f\u001a\u00020\u0010J\u0018\u0010\u0011\u001a\u00020\u00002\b\u0010\u0012\u001a\u0004\u0018\u00010\u00132\u0006\u0010\u000f\u001a\u00020\u0010J\u000e\u0010\u0011\u001a\u00020\u00002\u0006\u0010\u0014\u001a\u00020\bJ\u000e\u0010\u0011\u001a\u00020\u00002\u0006\u0010\u000f\u001a\u00020\u0010J\u0006\u0010\u0015\u001a\u00020\u0016J\u000e\u0010\u0017\u001a\u00020\u00002\u0006\u0010\t\u001a\u00020\nR\u000e\u0010\u0002\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0018"}, d2={"Ldev/serverpanel/lib/okhttp3/MultipartBody$Builder;", "", "boundary", "", "(Ljava/lang/String;)V", "Lokio/ByteString;", "parts", "", "Ldev/serverpanel/lib/okhttp3/MultipartBody$Part;", "type", "Ldev/serverpanel/lib/okhttp3/MediaType;", "addFormDataPart", "name", "value", "filename", "body", "Ldev/serverpanel/lib/okhttp3/RequestBody;", "addPart", "headers", "Ldev/serverpanel/lib/okhttp3/Headers;", "part", "build", "Ldev/serverpanel/lib/okhttp3/MultipartBody;", "setType", "okhttp"})
    @SourceDebugExtension(value={"SMAP\nMultipartBody.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MultipartBody.kt\nokhttp3/MultipartBody$Builder\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,345:1\n1#2:346\n*E\n"})
    public static final class Builder {
        @NotNull
        private final ByteString boundary;
        @NotNull
        private MediaType type;
        @NotNull
        private final List<Part> parts;

        @JvmOverloads
        public Builder(@NotNull String boundary) {
            Intrinsics.checkNotNullParameter(boundary, "boundary");
            this.boundary = ByteString.Companion.encodeUtf8(boundary);
            this.type = MIXED;
            this.parts = new ArrayList();
        }

        public /* synthetic */ Builder(String string, int n, DefaultConstructorMarker defaultConstructorMarker) {
            if ((n & 1) != 0) {
                String string2 = UUID.randomUUID().toString();
                Intrinsics.checkNotNullExpressionValue(string2, "randomUUID().toString()");
                string = string2;
            }
            this(string);
        }

        @NotNull
        public final Builder setType(@NotNull MediaType type) {
            Builder builder;
            Intrinsics.checkNotNullParameter(type, "type");
            Builder $this$setType_u24lambda_u241 = builder = this;
            boolean bl = false;
            if (!Intrinsics.areEqual(type.type(), "multipart")) {
                boolean bl2 = false;
                String string = "multipart != " + type;
                throw new IllegalArgumentException(string.toString());
            }
            $this$setType_u24lambda_u241.type = type;
            return builder;
        }

        @NotNull
        public final Builder addPart(@NotNull RequestBody body) {
            Builder builder;
            Intrinsics.checkNotNullParameter(body, "body");
            Builder $this$addPart_u24lambda_u242 = builder = this;
            boolean bl = false;
            $this$addPart_u24lambda_u242.addPart(Part.Companion.create(body));
            return builder;
        }

        @NotNull
        public final Builder addPart(@Nullable Headers headers, @NotNull RequestBody body) {
            Builder builder;
            Intrinsics.checkNotNullParameter(body, "body");
            Builder $this$addPart_u24lambda_u243 = builder = this;
            boolean bl = false;
            $this$addPart_u24lambda_u243.addPart(Part.Companion.create(headers, body));
            return builder;
        }

        @NotNull
        public final Builder addFormDataPart(@NotNull String name, @NotNull String value) {
            Builder builder;
            Intrinsics.checkNotNullParameter(name, "name");
            Intrinsics.checkNotNullParameter(value, "value");
            Builder $this$addFormDataPart_u24lambda_u244 = builder = this;
            boolean bl = false;
            $this$addFormDataPart_u24lambda_u244.addPart(Part.Companion.createFormData(name, value));
            return builder;
        }

        @NotNull
        public final Builder addFormDataPart(@NotNull String name, @Nullable String filename, @NotNull RequestBody body) {
            Builder builder;
            Intrinsics.checkNotNullParameter(name, "name");
            Intrinsics.checkNotNullParameter(body, "body");
            Builder $this$addFormDataPart_u24lambda_u245 = builder = this;
            boolean bl = false;
            $this$addFormDataPart_u24lambda_u245.addPart(Part.Companion.createFormData(name, filename, body));
            return builder;
        }

        @NotNull
        public final Builder addPart(@NotNull Part part) {
            Builder builder;
            Intrinsics.checkNotNullParameter(part, "part");
            Builder $this$addPart_u24lambda_u246 = builder = this;
            boolean bl = false;
            ((Collection)$this$addPart_u24lambda_u246.parts).add(part);
            return builder;
        }

        @NotNull
        public final MultipartBody build() {
            if (!(!((Collection)this.parts).isEmpty())) {
                boolean bl = false;
                String string = "Multipart body must have at least one part.";
                throw new IllegalStateException(string.toString());
            }
            return new MultipartBody(this.boundary, this.type, Util.toImmutableList(this.parts));
        }

        @JvmOverloads
        public Builder() {
            this(null, 1, null);
        }
    }

    @Metadata(mv={1, 8, 0}, k=1, xi=48, d1={"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0012\n\u0002\b\u0007\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u001d\u0010\r\u001a\u00020\u000e*\u00060\u000fj\u0002`\u00102\u0006\u0010\u0011\u001a\u00020\u0012H\u0000\u00a2\u0006\u0002\b\u0013R\u0010\u0010\u0003\u001a\u00020\u00048\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\t\u001a\u00020\u00048\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\n\u001a\u00020\u00048\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u000b\u001a\u00020\u00048\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\f\u001a\u00020\u00048\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0014"}, d2={"Ldev/serverpanel/lib/okhttp3/MultipartBody$Companion;", "", "()V", "ALTERNATIVE", "Ldev/serverpanel/lib/okhttp3/MediaType;", "COLONSPACE", "", "CRLF", "DASHDASH", "DIGEST", "FORM", "MIXED", "PARALLEL", "appendQuotedString", "", "Ljava/lang/StringBuilder;", "Lkotlin/text/StringBuilder;", "key", "", "appendQuotedString$okhttp", "okhttp"})
    public static final class Companion {
        private Companion() {
        }

        public final void appendQuotedString$okhttp(@NotNull StringBuilder $this$appendQuotedString, @NotNull String key) {
            Intrinsics.checkNotNullParameter($this$appendQuotedString, "<this>");
            Intrinsics.checkNotNullParameter(key, "key");
            $this$appendQuotedString.append('\"');
            int n = key.length();
            for (int i = 0; i < n; ++i) {
                char ch = key.charAt(i);
                if (ch == '\n') {
                    $this$appendQuotedString.append("%0A");
                    continue;
                }
                if (ch == '\r') {
                    $this$appendQuotedString.append("%0D");
                    continue;
                }
                if (ch == '\"') {
                    $this$appendQuotedString.append("%22");
                    continue;
                }
                $this$appendQuotedString.append(ch);
            }
            $this$appendQuotedString.append('\"');
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }

    /*
     * Illegal identifiers - consider using --renameillegalidents true
     */
    @Metadata(mv={1, 8, 0}, k=1, xi=48, d1={"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\u0018\u0000 \u000b2\u00020\u0001:\u0001\u000bB\u0019\b\u0002\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\r\u0010\u0004\u001a\u00020\u0005H\u0007\u00a2\u0006\u0002\b\tJ\u000f\u0010\u0002\u001a\u0004\u0018\u00010\u0003H\u0007\u00a2\u0006\u0002\b\nR\u0013\u0010\u0004\u001a\u00020\u00058\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0004\u0010\u0007R\u0015\u0010\u0002\u001a\u0004\u0018\u00010\u00038\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0002\u0010\b\u00a8\u0006\f"}, d2={"Ldev/serverpanel/lib/okhttp3/MultipartBody$Part;", "", "headers", "Ldev/serverpanel/lib/okhttp3/Headers;", "body", "Ldev/serverpanel/lib/okhttp3/RequestBody;", "(Lokhttp3/Headers;Lokhttp3/RequestBody;)V", "()Lokhttp3/RequestBody;", "()Lokhttp3/Headers;", "-deprecated_body", "-deprecated_headers", "Companion", "okhttp"})
    public static final class Part {
        @NotNull
        public static final Companion Companion = new Companion(null);
        @Nullable
        private final Headers headers;
        @NotNull
        private final RequestBody body;

        private Part(Headers headers, RequestBody body) {
            this.headers = headers;
            this.body = body;
        }

        @JvmName(name="headers")
        @Nullable
        public final Headers headers() {
            return this.headers;
        }

        @JvmName(name="body")
        @NotNull
        public final RequestBody body() {
            return this.body;
        }

        @Deprecated(message="moved to val", replaceWith=@ReplaceWith(expression="headers", imports={}), level=DeprecationLevel.ERROR)
        @JvmName(name="-deprecated_headers")
        @Nullable
        public final Headers -deprecated_headers() {
            return this.headers;
        }

        @Deprecated(message="moved to val", replaceWith=@ReplaceWith(expression="body", imports={}), level=DeprecationLevel.ERROR)
        @JvmName(name="-deprecated_body")
        @NotNull
        public final RequestBody -deprecated_body() {
            return this.body;
        }

        @JvmStatic
        @NotNull
        public static final Part create(@NotNull RequestBody body) {
            return Companion.create(body);
        }

        @JvmStatic
        @NotNull
        public static final Part create(@Nullable Headers headers, @NotNull RequestBody body) {
            return Companion.create(headers, body);
        }

        @JvmStatic
        @NotNull
        public static final Part createFormData(@NotNull String name, @NotNull String value) {
            return Companion.createFormData(name, value);
        }

        @JvmStatic
        @NotNull
        public static final Part createFormData(@NotNull String name, @Nullable String filename, @NotNull RequestBody body) {
            return Companion.createFormData(name, filename, body);
        }

        public /* synthetic */ Part(Headers headers, RequestBody body, DefaultConstructorMarker $constructor_marker) {
            this(headers, body);
        }

        @Metadata(mv={1, 8, 0}, k=1, xi=48, d1={"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u001a\u0010\u0003\u001a\u00020\u00042\b\u0010\u0005\u001a\u0004\u0018\u00010\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0007J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\bH\u0007J\u0018\u0010\t\u001a\u00020\u00042\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u000bH\u0007J\"\u0010\t\u001a\u00020\u00042\u0006\u0010\n\u001a\u00020\u000b2\b\u0010\r\u001a\u0004\u0018\u00010\u000b2\u0006\u0010\u0007\u001a\u00020\bH\u0007\u00a8\u0006\u000e"}, d2={"Ldev/serverpanel/lib/okhttp3/MultipartBody$Part$Companion;", "", "()V", "create", "Ldev/serverpanel/lib/okhttp3/MultipartBody$Part;", "headers", "Ldev/serverpanel/lib/okhttp3/Headers;", "body", "Ldev/serverpanel/lib/okhttp3/RequestBody;", "createFormData", "name", "", "value", "filename", "okhttp"})
        @SourceDebugExtension(value={"SMAP\nMultipartBody.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MultipartBody.kt\nokhttp3/MultipartBody$Part$Companion\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,345:1\n1#2:346\n*E\n"})
        public static final class Companion {
            private Companion() {
            }

            @JvmStatic
            @NotNull
            public final Part create(@NotNull RequestBody body) {
                Intrinsics.checkNotNullParameter(body, "body");
                return this.create(null, body);
            }

            @JvmStatic
            @NotNull
            public final Part create(@Nullable Headers headers, @NotNull RequestBody body) {
                Intrinsics.checkNotNullParameter(body, "body");
                Headers headers2 = headers;
                if (!((headers2 != null ? headers2.get("Content-Type") : null) == null)) {
                    boolean $i$a$-require-MultipartBody$Part$Companion$create$32 = false;
                    String $i$a$-require-MultipartBody$Part$Companion$create$32 = "Unexpected header: Content-Type";
                    throw new IllegalArgumentException($i$a$-require-MultipartBody$Part$Companion$create$32.toString());
                }
                Headers headers3 = headers;
                if (!((headers3 != null ? headers3.get("Content-Length") : null) == null)) {
                    boolean bl = false;
                    String string = "Unexpected header: Content-Length";
                    throw new IllegalArgumentException(string.toString());
                }
                return new Part(headers, body, null);
            }

            @JvmStatic
            @NotNull
            public final Part createFormData(@NotNull String name, @NotNull String value) {
                Intrinsics.checkNotNullParameter(name, "name");
                Intrinsics.checkNotNullParameter(value, "value");
                return this.createFormData(name, null, RequestBody.Companion.create$default(RequestBody.Companion, value, null, 1, null));
            }

            @JvmStatic
            @NotNull
            public final Part createFormData(@NotNull String name, @Nullable String filename, @NotNull RequestBody body) {
                StringBuilder stringBuilder;
                Intrinsics.checkNotNullParameter(name, "name");
                Intrinsics.checkNotNullParameter(body, "body");
                StringBuilder $this$createFormData_u24lambda_u242 = stringBuilder = new StringBuilder();
                boolean bl = false;
                $this$createFormData_u24lambda_u242.append("form-data; name=");
                MultipartBody.Companion.appendQuotedString$okhttp($this$createFormData_u24lambda_u242, name);
                if (filename != null) {
                    $this$createFormData_u24lambda_u242.append("; filename=");
                    MultipartBody.Companion.appendQuotedString$okhttp($this$createFormData_u24lambda_u242, filename);
                }
                String string = stringBuilder.toString();
                Intrinsics.checkNotNullExpressionValue(string, "StringBuilder().apply(builderAction).toString()");
                String disposition = string;
                Headers headers = new Headers.Builder().addUnsafeNonAscii("Content-Disposition", disposition).build();
                return this.create(headers, body);
            }

            public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
                this();
            }
        }
    }
}

