/*
 * Decompiled with CFR 0.152.
 */
package okio;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import kotlin.Deprecated;
import kotlin.DeprecationLevel;
import kotlin.Metadata;
import kotlin.ReplaceWith;
import kotlin.Unit;
import kotlin.jvm.JvmName;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import okio.Buffer;
import okio.ByteString;
import okio.ForwardingSource;
import okio.Segment;
import okio.Source;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000>\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u0000 \u00192\u00020\u00012\u00020\u0002:\u0001\u0019B\u0017\b\u0010\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006B\u0017\b\u0010\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0007\u001a\u00020\b\u00a2\u0006\u0002\u0010\tB\u0017\b\u0010\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\n\u001a\u00020\u000b\u00a2\u0006\u0002\u0010\fB\u001f\b\u0010\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\r\u001a\u00020\u000e\u0012\u0006\u0010\u0007\u001a\u00020\b\u00a2\u0006\u0002\u0010\u000fJ\r\u0010\u0010\u001a\u00020\u000eH\u0007\u00a2\u0006\u0002\b\u0013J\u0018\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u0015H\u0016R\u0011\u0010\u0010\u001a\u00020\u000e8G\u00a2\u0006\u0006\u001a\u0004\b\u0010\u0010\u0011R\u0010\u0010\n\u001a\u0004\u0018\u00010\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0012\u001a\u0004\u0018\u00010\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001a"}, d2={"Lokio/HashingSource;", "Lokio/ForwardingSource;", "Lokio/Source;", "source", "digest", "Ljava/security/MessageDigest;", "(Lokio/Source;Ljava/security/MessageDigest;)V", "algorithm", "", "(Lokio/Source;Ljava/lang/String;)V", "mac", "Ljavax/crypto/Mac;", "(Lokio/Source;Ljavax/crypto/Mac;)V", "key", "Lokio/ByteString;", "(Lokio/Source;Lokio/ByteString;Ljava/lang/String;)V", "hash", "()Lokio/ByteString;", "messageDigest", "-deprecated_hash", "read", "", "sink", "Lokio/Buffer;", "byteCount", "Companion", "okio"})
public final class HashingSource
extends ForwardingSource
implements Source {
    @NotNull
    public static final Companion Companion = new Companion(null);
    @Nullable
    private final MessageDigest messageDigest;
    @Nullable
    private final Mac mac;

    public HashingSource(@NotNull Source source2, @NotNull MessageDigest digest) {
        Intrinsics.checkNotNullParameter(source2, "source");
        Intrinsics.checkNotNullParameter(digest, "digest");
        super(source2);
        this.messageDigest = digest;
        this.mac = null;
    }

    public HashingSource(@NotNull Source source2, @NotNull String algorithm) {
        Intrinsics.checkNotNullParameter(source2, "source");
        Intrinsics.checkNotNullParameter(algorithm, "algorithm");
        MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
        Intrinsics.checkNotNullExpressionValue(messageDigest, "getInstance(...)");
        this(source2, messageDigest);
    }

    public HashingSource(@NotNull Source source2, @NotNull Mac mac) {
        Intrinsics.checkNotNullParameter(source2, "source");
        Intrinsics.checkNotNullParameter(mac, "mac");
        super(source2);
        this.mac = mac;
        this.messageDigest = null;
    }

    /*
     * WARNING - void declaration
     */
    public HashingSource(@NotNull Source source2, @NotNull ByteString key, @NotNull String algorithm) {
        Source source3;
        HashingSource hashingSource;
        Mac mac;
        Intrinsics.checkNotNullParameter(source2, "source");
        Intrinsics.checkNotNullParameter(key, "key");
        Intrinsics.checkNotNullParameter(algorithm, "algorithm");
        Source source4 = source2;
        HashingSource hashingSource2 = this;
        try {
            void $this$_init__u24lambda_u240;
            Mac mac2 = mac = Mac.getInstance(algorithm);
            Source source5 = source4;
            HashingSource hashingSource3 = hashingSource2;
            boolean bl = false;
            $this$_init__u24lambda_u240.init(new SecretKeySpec(key.toByteArray(), algorithm));
            Unit unit = Unit.INSTANCE;
            hashingSource = hashingSource3;
            source3 = source5;
        }
        catch (InvalidKeyException $this$_init__u24lambda_u240) {
            void e;
            HashingSource hashingSource4 = hashingSource2;
            Source source6 = source4;
            throw new IllegalArgumentException((Throwable)e);
        }
        Mac mac3 = mac;
        Intrinsics.checkNotNull(mac3);
        hashingSource(source3, mac3);
    }

    @Override
    public long read(@NotNull Buffer sink2, long byteCount) throws IOException {
        Intrinsics.checkNotNullParameter(sink2, "sink");
        long result = super.read(sink2, byteCount);
        if (result != -1L) {
            long offset;
            long start = sink2.size() - result;
            Segment segment = sink2.head;
            Intrinsics.checkNotNull(segment);
            Segment s = segment;
            for (offset = sink2.size(); offset > start; offset -= (long)(s.limit - s.pos)) {
                Intrinsics.checkNotNull(s.prev);
            }
            while (offset < sink2.size()) {
                int pos = (int)((long)s.pos + start - offset);
                if (this.messageDigest != null) {
                    this.messageDigest.update(s.data, pos, s.limit - pos);
                } else {
                    Mac mac = this.mac;
                    Intrinsics.checkNotNull(mac);
                    mac.update(s.data, pos, s.limit - pos);
                }
                start = offset += (long)(s.limit - s.pos);
                Intrinsics.checkNotNull(s.next);
            }
        }
        return result;
    }

    @JvmName(name="hash")
    @NotNull
    public final ByteString hash() {
        byte[] byArray;
        if (this.messageDigest != null) {
            byArray = this.messageDigest.digest();
        } else {
            Mac mac = this.mac;
            Intrinsics.checkNotNull(mac);
            byArray = mac.doFinal();
        }
        byte[] result = byArray;
        Intrinsics.checkNotNull(result);
        return new ByteString(result);
    }

    @Deprecated(message="moved to val", replaceWith=@ReplaceWith(expression="hash", imports={}), level=DeprecationLevel.ERROR)
    @JvmName(name="-deprecated_hash")
    @NotNull
    public final ByteString -deprecated_hash() {
        return this.hash();
    }

    @JvmStatic
    @NotNull
    public static final HashingSource md5(@NotNull Source source2) {
        return Companion.md5(source2);
    }

    @JvmStatic
    @NotNull
    public static final HashingSource sha1(@NotNull Source source2) {
        return Companion.sha1(source2);
    }

    @JvmStatic
    @NotNull
    public static final HashingSource sha256(@NotNull Source source2) {
        return Companion.sha256(source2);
    }

    @JvmStatic
    @NotNull
    public static final HashingSource sha512(@NotNull Source source2) {
        return Companion.sha512(source2);
    }

    @JvmStatic
    @NotNull
    public static final HashingSource hmacSha1(@NotNull Source source2, @NotNull ByteString key) {
        return Companion.hmacSha1(source2, key);
    }

    @JvmStatic
    @NotNull
    public static final HashingSource hmacSha256(@NotNull Source source2, @NotNull ByteString key) {
        return Companion.hmacSha256(source2, key);
    }

    @JvmStatic
    @NotNull
    public static final HashingSource hmacSha512(@NotNull Source source2, @NotNull ByteString key) {
        return Companion.hmacSha512(source2, key);
    }

    @Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0018\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0007J\u0018\u0010\t\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0007J\u0018\u0010\n\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0007J\u0010\u0010\u000b\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0007J\u0010\u0010\f\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0007J\u0010\u0010\r\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0007J\u0010\u0010\u000e\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0007\u00a8\u0006\u000f"}, d2={"Lokio/HashingSource$Companion;", "", "()V", "hmacSha1", "Lokio/HashingSource;", "source", "Lokio/Source;", "key", "Lokio/ByteString;", "hmacSha256", "hmacSha512", "md5", "sha1", "sha256", "sha512", "okio"})
    public static final class Companion {
        private Companion() {
        }

        @JvmStatic
        @NotNull
        public final HashingSource md5(@NotNull Source source2) {
            Intrinsics.checkNotNullParameter(source2, "source");
            return new HashingSource(source2, "MD5");
        }

        @JvmStatic
        @NotNull
        public final HashingSource sha1(@NotNull Source source2) {
            Intrinsics.checkNotNullParameter(source2, "source");
            return new HashingSource(source2, "SHA-1");
        }

        @JvmStatic
        @NotNull
        public final HashingSource sha256(@NotNull Source source2) {
            Intrinsics.checkNotNullParameter(source2, "source");
            return new HashingSource(source2, "SHA-256");
        }

        @JvmStatic
        @NotNull
        public final HashingSource sha512(@NotNull Source source2) {
            Intrinsics.checkNotNullParameter(source2, "source");
            return new HashingSource(source2, "SHA-512");
        }

        @JvmStatic
        @NotNull
        public final HashingSource hmacSha1(@NotNull Source source2, @NotNull ByteString key) {
            Intrinsics.checkNotNullParameter(source2, "source");
            Intrinsics.checkNotNullParameter(key, "key");
            return new HashingSource(source2, key, "HmacSHA1");
        }

        @JvmStatic
        @NotNull
        public final HashingSource hmacSha256(@NotNull Source source2, @NotNull ByteString key) {
            Intrinsics.checkNotNullParameter(source2, "source");
            Intrinsics.checkNotNullParameter(key, "key");
            return new HashingSource(source2, key, "HmacSHA256");
        }

        @JvmStatic
        @NotNull
        public final HashingSource hmacSha512(@NotNull Source source2, @NotNull ByteString key) {
            Intrinsics.checkNotNullParameter(source2, "source");
            Intrinsics.checkNotNullParameter(key, "key");
            return new HashingSource(source2, key, "HmacSHA512");
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }
}

