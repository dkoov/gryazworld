/*
 * Decompiled with CFR 0.152.
 */
package okio;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.util.Arrays;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import kotlin.Deprecated;
import kotlin.DeprecationLevel;
import kotlin.Metadata;
import kotlin.ReplaceWith;
import kotlin.collections.ArraysKt;
import kotlin.jvm.JvmField;
import kotlin.jvm.JvmName;
import kotlin.jvm.JvmOverloads;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.Charsets;
import kotlin.text.StringsKt;
import okio.-Base64;
import okio.-SegmentedByteString;
import okio.Buffer;
import okio._JvmPlatformKt;
import okio.internal.-ByteString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000r\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000f\n\u0000\n\u0002\u0010\u0012\n\u0002\b\u0004\n\u0002\u0010\b\n\u0002\b\u0006\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\b\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0005\n\u0002\b\u0019\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0016\u0018\u0000 ]2\u00020\u00012\b\u0012\u0004\u0012\u00020\u00000\u0002:\u0001]B\u000f\b\u0000\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u00a2\u0006\u0002\u0010\u0005J\b\u0010\u0015\u001a\u00020\u0016H\u0016J\b\u0010\u0017\u001a\u00020\u0010H\u0016J\b\u0010\u0018\u001a\u00020\u0010H\u0016J\u0011\u0010\u0019\u001a\u00020\t2\u0006\u0010\u001a\u001a\u00020\u0000H\u0096\u0002J,\u0010\u001b\u001a\u00020\u001c2\b\b\u0002\u0010\u001d\u001a\u00020\t2\u0006\u0010\u001e\u001a\u00020\u00042\b\b\u0002\u0010\u001f\u001a\u00020\t2\u0006\u0010 \u001a\u00020\tH\u0016J\u0015\u0010!\u001a\u00020\u00002\u0006\u0010\"\u001a\u00020\u0010H\u0010\u00a2\u0006\u0002\b#J\u000e\u0010$\u001a\u00020%2\u0006\u0010&\u001a\u00020\u0004J\u000e\u0010$\u001a\u00020%2\u0006\u0010&\u001a\u00020\u0000J\u0013\u0010'\u001a\u00020%2\b\u0010\u001a\u001a\u0004\u0018\u00010(H\u0096\u0002J\u0016\u0010)\u001a\u00020*2\u0006\u0010+\u001a\u00020\tH\u0087\u0002\u00a2\u0006\u0002\b,J\u0015\u0010,\u001a\u00020*2\u0006\u0010+\u001a\u00020\tH\u0007\u00a2\u0006\u0002\b-J\r\u0010.\u001a\u00020\tH\u0010\u00a2\u0006\u0002\b/J\b\u0010\b\u001a\u00020\tH\u0016J\b\u00100\u001a\u00020\u0010H\u0016J\u001d\u00101\u001a\u00020\u00002\u0006\u0010\"\u001a\u00020\u00102\u0006\u00102\u001a\u00020\u0000H\u0010\u00a2\u0006\u0002\b3J\u0010\u00104\u001a\u00020\u00002\u0006\u00102\u001a\u00020\u0000H\u0016J\u0010\u00105\u001a\u00020\u00002\u0006\u00102\u001a\u00020\u0000H\u0016J\u0010\u00106\u001a\u00020\u00002\u0006\u00102\u001a\u00020\u0000H\u0016J\u001a\u00107\u001a\u00020\t2\u0006\u0010\u001a\u001a\u00020\u00042\b\b\u0002\u00108\u001a\u00020\tH\u0017J\u001a\u00107\u001a\u00020\t2\u0006\u0010\u001a\u001a\u00020\u00002\b\b\u0002\u00108\u001a\u00020\tH\u0007J\r\u00109\u001a\u00020\u0004H\u0010\u00a2\u0006\u0002\b:J\u0015\u0010;\u001a\u00020*2\u0006\u0010<\u001a\u00020\tH\u0010\u00a2\u0006\u0002\b=J\u001a\u0010>\u001a\u00020\t2\u0006\u0010\u001a\u001a\u00020\u00042\b\b\u0002\u00108\u001a\u00020\tH\u0017J\u001a\u0010>\u001a\u00020\t2\u0006\u0010\u001a\u001a\u00020\u00002\b\b\u0002\u00108\u001a\u00020\tH\u0007J\u0006\u0010?\u001a\u00020\u0000J(\u0010@\u001a\u00020%2\u0006\u0010\u001d\u001a\u00020\t2\u0006\u0010\u001a\u001a\u00020\u00042\u0006\u0010A\u001a\u00020\t2\u0006\u0010 \u001a\u00020\tH\u0016J(\u0010@\u001a\u00020%2\u0006\u0010\u001d\u001a\u00020\t2\u0006\u0010\u001a\u001a\u00020\u00002\u0006\u0010A\u001a\u00020\t2\u0006\u0010 \u001a\u00020\tH\u0016J\u0010\u0010B\u001a\u00020\u001c2\u0006\u0010C\u001a\u00020DH\u0002J\u0006\u0010E\u001a\u00020\u0000J\u0006\u0010F\u001a\u00020\u0000J\u0006\u0010G\u001a\u00020\u0000J\r\u0010\u000e\u001a\u00020\tH\u0007\u00a2\u0006\u0002\bHJ\u000e\u0010I\u001a\u00020%2\u0006\u0010J\u001a\u00020\u0004J\u000e\u0010I\u001a\u00020%2\u0006\u0010J\u001a\u00020\u0000J\u0010\u0010K\u001a\u00020\u00102\u0006\u0010L\u001a\u00020MH\u0016J\u001c\u0010N\u001a\u00020\u00002\b\b\u0002\u0010O\u001a\u00020\t2\b\b\u0002\u0010P\u001a\u00020\tH\u0017J\b\u0010Q\u001a\u00020\u0000H\u0016J\b\u0010R\u001a\u00020\u0000H\u0016J\b\u0010S\u001a\u00020\u0004H\u0016J\b\u0010T\u001a\u00020\u0010H\u0016J\b\u0010\u000f\u001a\u00020\u0010H\u0016J\u0010\u0010U\u001a\u00020\u001c2\u0006\u0010V\u001a\u00020WH\u0016J%\u0010U\u001a\u00020\u001c2\u0006\u0010X\u001a\u00020Y2\u0006\u0010\u001d\u001a\u00020\t2\u0006\u0010 \u001a\u00020\tH\u0010\u00a2\u0006\u0002\bZJ\u0010\u0010[\u001a\u00020\u001c2\u0006\u0010V\u001a\u00020\\H\u0002R\u0014\u0010\u0003\u001a\u00020\u0004X\u0080\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u001a\u0010\b\u001a\u00020\tX\u0080\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\n\u0010\u000b\"\u0004\b\f\u0010\rR\u0011\u0010\u000e\u001a\u00020\t8G\u00a2\u0006\u0006\u001a\u0004\b\u000e\u0010\u000bR\u001c\u0010\u000f\u001a\u0004\u0018\u00010\u0010X\u0080\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0011\u0010\u0012\"\u0004\b\u0013\u0010\u0014\u00a8\u0006^"}, d2={"Lokio/ByteString;", "Ljava/io/Serializable;", "", "data", "", "([B)V", "getData$okio", "()[B", "hashCode", "", "getHashCode$okio", "()I", "setHashCode$okio", "(I)V", "size", "utf8", "", "getUtf8$okio", "()Ljava/lang/String;", "setUtf8$okio", "(Ljava/lang/String;)V", "asByteBuffer", "Ljava/nio/ByteBuffer;", "base64", "base64Url", "compareTo", "other", "copyInto", "", "offset", "target", "targetOffset", "byteCount", "digest", "algorithm", "digest$okio", "endsWith", "", "suffix", "equals", "", "get", "", "index", "getByte", "-deprecated_getByte", "getSize", "getSize$okio", "hex", "hmac", "key", "hmac$okio", "hmacSha1", "hmacSha256", "hmacSha512", "indexOf", "fromIndex", "internalArray", "internalArray$okio", "internalGet", "pos", "internalGet$okio", "lastIndexOf", "md5", "rangeEquals", "otherOffset", "readObject", "in", "Ljava/io/ObjectInputStream;", "sha1", "sha256", "sha512", "-deprecated_size", "startsWith", "prefix", "string", "charset", "Ljava/nio/charset/Charset;", "substring", "beginIndex", "endIndex", "toAsciiLowercase", "toAsciiUppercase", "toByteArray", "toString", "write", "out", "Ljava/io/OutputStream;", "buffer", "Lokio/Buffer;", "write$okio", "writeObject", "Ljava/io/ObjectOutputStream;", "Companion", "okio"})
@SourceDebugExtension(value={"SMAP\nByteString.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ByteString.kt\nokio/ByteString\n+ 2 ByteString.kt\nokio/internal/-ByteString\n+ 3 Util.kt\nokio/-SegmentedByteString\n+ 4 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,364:1\n43#2,7:365\n53#2:372\n56#2:373\n64#2,4:374\n68#2:379\n70#2:381\n76#2,23:382\n104#2,23:405\n131#2,2:428\n133#2,9:431\n145#2:440\n148#2:441\n151#2:442\n154#2:443\n162#2:444\n172#2,3:445\n171#2:448\n185#2,2:449\n190#2:451\n194#2:452\n198#2:453\n202#2:454\n206#2,7:455\n219#2:462\n223#2,8:463\n235#2,4:471\n244#2,5:475\n253#2,6:480\n259#2,9:487\n322#2,8:496\n131#2,2:504\n133#2,9:507\n333#2,9:516\n68#3:378\n74#3:380\n74#3:486\n1#4:430\n1#4:506\n*S KotlinDebug\n*F\n+ 1 ByteString.kt\nokio/ByteString\n*L\n66#1:365,7\n71#1:372\n108#1:373\n110#1:374,4\n110#1:379\n110#1:381\n112#1:382,23\n114#1:405,23\n118#1:428,2\n118#1:431,9\n120#1:440\n129#1:441\n131#1:442\n133#1:443\n152#1:444\n159#1:445,3\n159#1:448\n166#1:449,2\n168#1:451\n170#1:452\n172#1:453\n174#1:454\n180#1:455,7\n183#1:462\n186#1:463,8\n188#1:471,4\n190#1:475,5\n192#1:480,6\n192#1:487,9\n194#1:496,8\n194#1:504,2\n194#1:507,9\n194#1:516,9\n110#1:378\n110#1:380\n192#1:486\n118#1:430\n194#1:506\n*E\n"})
public class ByteString
implements Serializable,
Comparable<ByteString> {
    @NotNull
    public static final Companion Companion = new Companion(null);
    @NotNull
    private final byte[] data;
    private transient int hashCode;
    @Nullable
    private transient String utf8;
    private static final long serialVersionUID = 1L;
    @JvmField
    @NotNull
    public static final ByteString EMPTY = new ByteString(new byte[0]);

    public ByteString(@NotNull byte[] data) {
        Intrinsics.checkNotNullParameter(data, "data");
        this.data = data;
    }

    @NotNull
    public final byte[] getData$okio() {
        return this.data;
    }

    public final int getHashCode$okio() {
        return this.hashCode;
    }

    public final void setHashCode$okio(int n) {
        this.hashCode = n;
    }

    @Nullable
    public final String getUtf8$okio() {
        return this.utf8;
    }

    public final void setUtf8$okio(@Nullable String string) {
        this.utf8 = string;
    }

    /*
     * WARNING - void declaration
     */
    @NotNull
    public String utf8() {
        void var3_3;
        ByteString $this$commonUtf8$iv = this;
        boolean $i$f$commonUtf8 = false;
        String result$iv = $this$commonUtf8$iv.getUtf8$okio();
        if (result$iv == null) {
            result$iv = _JvmPlatformKt.toUtf8String($this$commonUtf8$iv.internalArray$okio());
            $this$commonUtf8$iv.setUtf8$okio(result$iv);
        }
        return var3_3;
    }

    @NotNull
    public String string(@NotNull Charset charset) {
        Intrinsics.checkNotNullParameter(charset, "charset");
        return new String(this.data, charset);
    }

    @NotNull
    public String base64() {
        ByteString $this$commonBase64$iv = this;
        boolean $i$f$commonBase64 = false;
        return -Base64.encodeBase64$default($this$commonBase64$iv.getData$okio(), null, 1, null);
    }

    @NotNull
    public final ByteString md5() {
        return this.digest$okio("MD5");
    }

    @NotNull
    public final ByteString sha1() {
        return this.digest$okio("SHA-1");
    }

    @NotNull
    public final ByteString sha256() {
        return this.digest$okio("SHA-256");
    }

    @NotNull
    public final ByteString sha512() {
        return this.digest$okio("SHA-512");
    }

    @NotNull
    public ByteString digest$okio(@NotNull String algorithm) {
        Intrinsics.checkNotNullParameter(algorithm, "algorithm");
        MessageDigest $this$digest_u24lambda_u240 = MessageDigest.getInstance(algorithm);
        boolean bl = false;
        $this$digest_u24lambda_u240.update(this.data, 0, this.size());
        byte[] digestBytes = $this$digest_u24lambda_u240.digest();
        Intrinsics.checkNotNull(digestBytes);
        return new ByteString(digestBytes);
    }

    @NotNull
    public ByteString hmacSha1(@NotNull ByteString key) {
        Intrinsics.checkNotNullParameter(key, "key");
        return this.hmac$okio("HmacSHA1", key);
    }

    @NotNull
    public ByteString hmacSha256(@NotNull ByteString key) {
        Intrinsics.checkNotNullParameter(key, "key");
        return this.hmac$okio("HmacSHA256", key);
    }

    @NotNull
    public ByteString hmacSha512(@NotNull ByteString key) {
        Intrinsics.checkNotNullParameter(key, "key");
        return this.hmac$okio("HmacSHA512", key);
    }

    @NotNull
    public ByteString hmac$okio(@NotNull String algorithm, @NotNull ByteString key) {
        Intrinsics.checkNotNullParameter(algorithm, "algorithm");
        Intrinsics.checkNotNullParameter(key, "key");
        try {
            Mac mac = Mac.getInstance(algorithm);
            mac.init(new SecretKeySpec(key.toByteArray(), algorithm));
            byte[] byArray = mac.doFinal(this.data);
            Intrinsics.checkNotNullExpressionValue(byArray, "doFinal(...)");
            return new ByteString(byArray);
        }
        catch (InvalidKeyException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @NotNull
    public String base64Url() {
        ByteString $this$commonBase64Url$iv = this;
        boolean $i$f$commonBase64Url = false;
        return -Base64.encodeBase64($this$commonBase64Url$iv.getData$okio(), -Base64.getBASE64_URL_SAFE());
    }

    /*
     * WARNING - void declaration
     */
    @NotNull
    public String hex() {
        ByteString $this$commonHex$iv = this;
        boolean $i$f$commonHex = false;
        char[] result$iv = new char[$this$commonHex$iv.getData$okio().length * 2];
        int c$iv = 0;
        for (byte b$iv : $this$commonHex$iv.getData$okio()) {
            void $this$and$iv$iv;
            byte $this$shr$iv$iv;
            int n = c$iv++;
            byte by = b$iv;
            int other$iv$iv = 4;
            boolean $i$f$shr = false;
            result$iv[n] = -ByteString.getHEX_DIGIT_CHARS()[$this$shr$iv$iv >> other$iv$iv & 0xF];
            int n2 = c$iv++;
            $this$shr$iv$iv = b$iv;
            other$iv$iv = 15;
            boolean $i$f$and = false;
            result$iv[n2] = -ByteString.getHEX_DIGIT_CHARS()[$this$and$iv$iv & other$iv$iv];
        }
        return StringsKt.concatToString(result$iv);
    }

    /*
     * WARNING - void declaration
     */
    @NotNull
    public ByteString toAsciiLowercase() {
        ByteString byteString;
        block4: {
            void var1_1;
            ByteString $this$commonToAsciiLowercase$iv = this;
            boolean $i$f$commonToAsciiLowercase = false;
            for (int i$iv = 0; i$iv < $this$commonToAsciiLowercase$iv.getData$okio().length; ++i$iv) {
                byte c$iv = $this$commonToAsciiLowercase$iv.getData$okio()[i$iv];
                if (c$iv < 65 || c$iv > 90) {
                    continue;
                }
                byte[] byArray = $this$commonToAsciiLowercase$iv.getData$okio();
                byte[] byArray2 = Arrays.copyOf(byArray, byArray.length);
                Intrinsics.checkNotNullExpressionValue(byArray2, "copyOf(this, size)");
                byte[] lowercase$iv = byArray2;
                lowercase$iv[i$iv++] = (byte)(c$iv - -32);
                while (i$iv < lowercase$iv.length) {
                    c$iv = lowercase$iv[i$iv];
                    if (c$iv < 65 || c$iv > 90) {
                        ++i$iv;
                        continue;
                    }
                    lowercase$iv[i$iv] = (byte)(c$iv - -32);
                    ++i$iv;
                }
                byteString = new ByteString(lowercase$iv);
                break block4;
            }
            byteString = var1_1;
        }
        return byteString;
    }

    /*
     * WARNING - void declaration
     */
    @NotNull
    public ByteString toAsciiUppercase() {
        ByteString byteString;
        block4: {
            void var1_1;
            ByteString $this$commonToAsciiUppercase$iv = this;
            boolean $i$f$commonToAsciiUppercase = false;
            for (int i$iv = 0; i$iv < $this$commonToAsciiUppercase$iv.getData$okio().length; ++i$iv) {
                byte c$iv = $this$commonToAsciiUppercase$iv.getData$okio()[i$iv];
                if (c$iv < 97 || c$iv > 122) {
                    continue;
                }
                byte[] byArray = $this$commonToAsciiUppercase$iv.getData$okio();
                byte[] byArray2 = Arrays.copyOf(byArray, byArray.length);
                Intrinsics.checkNotNullExpressionValue(byArray2, "copyOf(this, size)");
                byte[] lowercase$iv = byArray2;
                lowercase$iv[i$iv++] = (byte)(c$iv - 32);
                while (i$iv < lowercase$iv.length) {
                    c$iv = lowercase$iv[i$iv];
                    if (c$iv < 97 || c$iv > 122) {
                        ++i$iv;
                        continue;
                    }
                    lowercase$iv[i$iv] = (byte)(c$iv - 32);
                    ++i$iv;
                }
                byteString = new ByteString(lowercase$iv);
                break block4;
            }
            byteString = var1_1;
        }
        return byteString;
    }

    @JvmOverloads
    @NotNull
    public ByteString substring(int beginIndex, int endIndex) {
        ByteString byteString;
        ByteString $this$commonSubstring$iv = this;
        boolean $i$f$commonSubstring = false;
        int endIndex$iv = -SegmentedByteString.resolveDefaultParameter($this$commonSubstring$iv, endIndex);
        if (!(beginIndex >= 0)) {
            boolean $i$a$-require--ByteString$commonSubstring$1$iv22 = false;
            String $i$a$-require--ByteString$commonSubstring$1$iv22 = "beginIndex < 0";
            throw new IllegalArgumentException($i$a$-require--ByteString$commonSubstring$1$iv22.toString());
        }
        if (!(endIndex$iv <= $this$commonSubstring$iv.getData$okio().length)) {
            boolean bl = false;
            String string = "endIndex > length(" + $this$commonSubstring$iv.getData$okio().length + ')';
            throw new IllegalArgumentException(string.toString());
        }
        int subLen$iv = endIndex$iv - beginIndex;
        if (!(subLen$iv >= 0)) {
            boolean bl = false;
            String string = "endIndex < beginIndex";
            throw new IllegalArgumentException(string.toString());
        }
        if (beginIndex == 0 && endIndex$iv == $this$commonSubstring$iv.getData$okio().length) {
            byteString = $this$commonSubstring$iv;
        } else {
            byte[] byArray = $this$commonSubstring$iv.getData$okio();
            ByteString byteString2 = new ByteString(ArraysKt.copyOfRange(byArray, beginIndex, endIndex$iv));
            byteString = byteString2;
        }
        return byteString;
    }

    public static /* synthetic */ ByteString substring$default(ByteString byteString, int n, int n2, int n3, Object object) {
        if (object != null) {
            throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: substring");
        }
        if ((n3 & 1) != 0) {
            n = 0;
        }
        if ((n3 & 2) != 0) {
            n2 = -SegmentedByteString.getDEFAULT__ByteString_size();
        }
        return byteString.substring(n, n2);
    }

    public byte internalGet$okio(int pos) {
        ByteString $this$commonGetByte$iv = this;
        boolean $i$f$commonGetByte = false;
        return $this$commonGetByte$iv.getData$okio()[pos];
    }

    @JvmName(name="getByte")
    public final byte getByte(int index) {
        return this.internalGet$okio(index);
    }

    @JvmName(name="size")
    public final int size() {
        return this.getSize$okio();
    }

    public int getSize$okio() {
        ByteString $this$commonGetSize$iv = this;
        boolean $i$f$commonGetSize = false;
        return $this$commonGetSize$iv.getData$okio().length;
    }

    @NotNull
    public byte[] toByteArray() {
        ByteString $this$commonToByteArray$iv = this;
        boolean $i$f$commonToByteArray = false;
        byte[] byArray = $this$commonToByteArray$iv.getData$okio();
        byte[] byArray2 = Arrays.copyOf(byArray, byArray.length);
        Intrinsics.checkNotNullExpressionValue(byArray2, "copyOf(this, size)");
        return byArray2;
    }

    @NotNull
    public byte[] internalArray$okio() {
        ByteString $this$commonInternalArray$iv = this;
        boolean $i$f$commonInternalArray = false;
        return $this$commonInternalArray$iv.getData$okio();
    }

    @NotNull
    public ByteBuffer asByteBuffer() {
        ByteBuffer byteBuffer = ByteBuffer.wrap(this.data).asReadOnlyBuffer();
        Intrinsics.checkNotNullExpressionValue(byteBuffer, "asReadOnlyBuffer(...)");
        return byteBuffer;
    }

    public void write(@NotNull OutputStream out) throws IOException {
        Intrinsics.checkNotNullParameter(out, "out");
        out.write(this.data);
    }

    public void write$okio(@NotNull Buffer buffer, int offset, int byteCount) {
        Intrinsics.checkNotNullParameter(buffer, "buffer");
        -ByteString.commonWrite(this, buffer, offset, byteCount);
    }

    public boolean rangeEquals(int offset, @NotNull ByteString other, int otherOffset, int byteCount) {
        Intrinsics.checkNotNullParameter(other, "other");
        ByteString $this$commonRangeEquals$iv = this;
        boolean $i$f$commonRangeEquals = false;
        return other.rangeEquals(otherOffset, $this$commonRangeEquals$iv.getData$okio(), offset, byteCount);
    }

    public boolean rangeEquals(int offset, @NotNull byte[] other, int otherOffset, int byteCount) {
        Intrinsics.checkNotNullParameter(other, "other");
        ByteString $this$commonRangeEquals$iv = this;
        boolean $i$f$commonRangeEquals = false;
        return offset >= 0 && offset <= $this$commonRangeEquals$iv.getData$okio().length - byteCount && otherOffset >= 0 && otherOffset <= other.length - byteCount && -SegmentedByteString.arrayRangeEquals($this$commonRangeEquals$iv.getData$okio(), offset, other, otherOffset, byteCount);
    }

    public void copyInto(int offset, @NotNull byte[] target, int targetOffset, int byteCount) {
        Intrinsics.checkNotNullParameter(target, "target");
        ByteString $this$commonCopyInto$iv = this;
        boolean $i$f$commonCopyInto = false;
        ArraysKt.copyInto($this$commonCopyInto$iv.getData$okio(), target, targetOffset, offset, offset + byteCount);
    }

    public static /* synthetic */ void copyInto$default(ByteString byteString, int n, byte[] byArray, int n2, int n3, int n4, Object object) {
        if (object != null) {
            throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: copyInto");
        }
        if ((n4 & 1) != 0) {
            n = 0;
        }
        if ((n4 & 4) != 0) {
            n2 = 0;
        }
        byteString.copyInto(n, byArray, n2, n3);
    }

    public final boolean startsWith(@NotNull ByteString prefix) {
        Intrinsics.checkNotNullParameter(prefix, "prefix");
        ByteString $this$commonStartsWith$iv = this;
        boolean $i$f$commonStartsWith = false;
        return $this$commonStartsWith$iv.rangeEquals(0, prefix, 0, prefix.size());
    }

    public final boolean startsWith(@NotNull byte[] prefix) {
        Intrinsics.checkNotNullParameter(prefix, "prefix");
        ByteString $this$commonStartsWith$iv = this;
        boolean $i$f$commonStartsWith = false;
        return $this$commonStartsWith$iv.rangeEquals(0, prefix, 0, prefix.length);
    }

    public final boolean endsWith(@NotNull ByteString suffix) {
        Intrinsics.checkNotNullParameter(suffix, "suffix");
        ByteString $this$commonEndsWith$iv = this;
        boolean $i$f$commonEndsWith = false;
        return $this$commonEndsWith$iv.rangeEquals($this$commonEndsWith$iv.size() - suffix.size(), suffix, 0, suffix.size());
    }

    public final boolean endsWith(@NotNull byte[] suffix) {
        Intrinsics.checkNotNullParameter(suffix, "suffix");
        ByteString $this$commonEndsWith$iv = this;
        boolean $i$f$commonEndsWith = false;
        return $this$commonEndsWith$iv.rangeEquals($this$commonEndsWith$iv.size() - suffix.length, suffix, 0, suffix.length);
    }

    @JvmOverloads
    public final int indexOf(@NotNull ByteString other, int fromIndex) {
        Intrinsics.checkNotNullParameter(other, "other");
        return this.indexOf(other.internalArray$okio(), fromIndex);
    }

    public static /* synthetic */ int indexOf$default(ByteString byteString, ByteString byteString2, int n, int n2, Object object) {
        if (object != null) {
            throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: indexOf");
        }
        if ((n2 & 2) != 0) {
            n = 0;
        }
        return byteString.indexOf(byteString2, n);
    }

    @JvmOverloads
    public int indexOf(@NotNull byte[] other, int fromIndex) {
        int n;
        block3: {
            Intrinsics.checkNotNullParameter(other, "other");
            ByteString $this$commonIndexOf$iv = this;
            boolean $i$f$commonIndexOf = false;
            int limit$iv = $this$commonIndexOf$iv.getData$okio().length - other.length;
            int i$iv = Math.max(fromIndex, 0);
            if (i$iv <= limit$iv) {
                while (true) {
                    if (-SegmentedByteString.arrayRangeEquals($this$commonIndexOf$iv.getData$okio(), i$iv, other, 0, other.length)) {
                        n = i$iv;
                        break block3;
                    }
                    if (i$iv == limit$iv) break;
                    ++i$iv;
                }
            }
            n = -1;
        }
        return n;
    }

    public static /* synthetic */ int indexOf$default(ByteString byteString, byte[] byArray, int n, int n2, Object object) {
        if (object != null) {
            throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: indexOf");
        }
        if ((n2 & 2) != 0) {
            n = 0;
        }
        return byteString.indexOf(byArray, n);
    }

    @JvmOverloads
    public final int lastIndexOf(@NotNull ByteString other, int fromIndex) {
        Intrinsics.checkNotNullParameter(other, "other");
        ByteString $this$commonLastIndexOf$iv = this;
        boolean $i$f$commonLastIndexOf = false;
        return $this$commonLastIndexOf$iv.lastIndexOf(other.internalArray$okio(), fromIndex);
    }

    public static /* synthetic */ int lastIndexOf$default(ByteString byteString, ByteString byteString2, int n, int n2, Object object) {
        if (object != null) {
            throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: lastIndexOf");
        }
        if ((n2 & 2) != 0) {
            n = -SegmentedByteString.getDEFAULT__ByteString_size();
        }
        return byteString.lastIndexOf(byteString2, n);
    }

    @JvmOverloads
    public int lastIndexOf(@NotNull byte[] other, int fromIndex) {
        int n;
        block1: {
            Intrinsics.checkNotNullParameter(other, "other");
            ByteString $this$commonLastIndexOf$iv = this;
            boolean $i$f$commonLastIndexOf = false;
            int fromIndex$iv = -SegmentedByteString.resolveDefaultParameter($this$commonLastIndexOf$iv, fromIndex);
            int limit$iv = $this$commonLastIndexOf$iv.getData$okio().length - other.length;
            for (int i$iv = Math.min(fromIndex$iv, limit$iv); -1 < i$iv; --i$iv) {
                if (!-SegmentedByteString.arrayRangeEquals($this$commonLastIndexOf$iv.getData$okio(), i$iv, other, 0, other.length)) continue;
                n = i$iv;
                break block1;
            }
            n = -1;
        }
        return n;
    }

    public static /* synthetic */ int lastIndexOf$default(ByteString byteString, byte[] byArray, int n, int n2, Object object) {
        if (object != null) {
            throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: lastIndexOf");
        }
        if ((n2 & 2) != 0) {
            n = -SegmentedByteString.getDEFAULT__ByteString_size();
        }
        return byteString.lastIndexOf(byArray, n);
    }

    public boolean equals(@Nullable Object other) {
        ByteString $this$commonEquals$iv = this;
        boolean $i$f$commonEquals = false;
        return other == $this$commonEquals$iv ? true : (other instanceof ByteString ? ((ByteString)other).size() == $this$commonEquals$iv.getData$okio().length && ((ByteString)other).rangeEquals(0, $this$commonEquals$iv.getData$okio(), 0, $this$commonEquals$iv.getData$okio().length) : false);
    }

    public int hashCode() {
        int n;
        ByteString $this$commonHashCode$iv = this;
        boolean $i$f$commonHashCode = false;
        int result$iv = $this$commonHashCode$iv.getHashCode$okio();
        if (result$iv != 0) {
            n = result$iv;
        } else {
            int n2;
            int it$iv = n2 = Arrays.hashCode($this$commonHashCode$iv.getData$okio());
            boolean bl = false;
            $this$commonHashCode$iv.setHashCode$okio(it$iv);
            n = n2;
        }
        return n;
    }

    /*
     * WARNING - void declaration
     */
    @Override
    public int compareTo(@NotNull ByteString other) {
        int n;
        block2: {
            Intrinsics.checkNotNullParameter(other, "other");
            ByteString $this$commonCompareTo$iv = this;
            boolean $i$f$commonCompareTo = false;
            int sizeA$iv = $this$commonCompareTo$iv.size();
            int sizeB$iv = other.size();
            int size$iv = Math.min(sizeA$iv, sizeB$iv);
            for (int i$iv = 0; i$iv < size$iv; ++i$iv) {
                void $this$and$iv$iv;
                void $this$and$iv$iv2;
                byte by = $this$commonCompareTo$iv.getByte(i$iv);
                int other$iv$iv = 255;
                boolean $i$f$and = false;
                int byteA$iv = $this$and$iv$iv2 & other$iv$iv;
                other$iv$iv = other.getByte(i$iv);
                int other$iv$iv2 = 255;
                boolean $i$f$and2 = false;
                int byteB$iv = $this$and$iv$iv & other$iv$iv2;
                if (byteA$iv == byteB$iv) {
                    continue;
                }
                n = byteA$iv < byteB$iv ? -1 : 1;
                break block2;
            }
            n = sizeA$iv == sizeB$iv ? 0 : (sizeA$iv < sizeB$iv ? -1 : 1);
        }
        return n;
    }

    /*
     * WARNING - void declaration
     */
    @NotNull
    public String toString() {
        String string;
        ByteString $this$commonToString$iv = this;
        boolean $i$f$commonToString = false;
        if ($this$commonToString$iv.getData$okio().length == 0) {
            string = "[size=0]";
        } else {
            int i$iv = -ByteString.access$codePointIndexToCharIndex($this$commonToString$iv.getData$okio(), 64);
            if (i$iv == -1) {
                if ($this$commonToString$iv.getData$okio().length <= 64) {
                    string = "[hex=" + $this$commonToString$iv.hex() + ']';
                } else {
                    ByteString byteString;
                    void beginIndex$iv$iv;
                    void $this$commonSubstring$iv$iv;
                    StringBuilder stringBuilder = new StringBuilder().append("[size=").append($this$commonToString$iv.getData$okio().length).append(" hex=");
                    ByteString byteString2 = $this$commonToString$iv;
                    boolean bl = false;
                    int endIndex$iv$iv = 64;
                    boolean $i$f$commonSubstring = false;
                    int endIndex$iv$iv2 = -SegmentedByteString.resolveDefaultParameter((ByteString)$this$commonSubstring$iv$iv, endIndex$iv$iv);
                    if (!(endIndex$iv$iv2 <= $this$commonSubstring$iv$iv.getData$okio().length)) {
                        boolean bl2 = false;
                        String string2 = "endIndex > length(" + $this$commonSubstring$iv$iv.getData$okio().length + ')';
                        throw new IllegalArgumentException(string2.toString());
                    }
                    int subLen$iv$iv = endIndex$iv$iv2 - beginIndex$iv$iv;
                    if (!(subLen$iv$iv >= 0)) {
                        boolean bl3 = false;
                        String string3 = "endIndex < beginIndex";
                        throw new IllegalArgumentException(string3.toString());
                    }
                    if (endIndex$iv$iv2 == $this$commonSubstring$iv$iv.getData$okio().length) {
                        byteString = $this$commonSubstring$iv$iv;
                    } else {
                        byte[] byArray = $this$commonSubstring$iv$iv.getData$okio();
                        ByteString byteString3 = new ByteString(ArraysKt.copyOfRange(byArray, (int)beginIndex$iv$iv, endIndex$iv$iv2));
                        byteString = byteString3;
                    }
                    string = stringBuilder.append(byteString.hex()).append("\u2026]").toString();
                }
            } else {
                String text$iv = $this$commonToString$iv.utf8();
                String string4 = text$iv.substring(0, i$iv);
                Intrinsics.checkNotNullExpressionValue(string4, "this as java.lang.String\u2026ing(startIndex, endIndex)");
                String safeText$iv = StringsKt.replace$default(StringsKt.replace$default(StringsKt.replace$default(string4, "\\", "\\\\", false, 4, null), "\n", "\\n", false, 4, null), "\r", "\\r", false, 4, null);
                string = i$iv < text$iv.length() ? "[size=" + $this$commonToString$iv.getData$okio().length + " text=" + safeText$iv + "\u2026]" : "[text=" + safeText$iv + ']';
            }
        }
        return string;
    }

    private final void readObject(ObjectInputStream in) throws IOException {
        int dataLength = in.readInt();
        ByteString byteString = Companion.read(in, dataLength);
        Field field = ByteString.class.getDeclaredField("data");
        field.setAccessible(true);
        field.set(this, byteString.data);
    }

    private final void writeObject(ObjectOutputStream out) throws IOException {
        out.writeInt(this.data.length);
        out.write(this.data);
    }

    @Deprecated(message="moved to operator function", replaceWith=@ReplaceWith(expression="this[index]", imports={}), level=DeprecationLevel.ERROR)
    @JvmName(name="-deprecated_getByte")
    public final byte -deprecated_getByte(int index) {
        return this.getByte(index);
    }

    @Deprecated(message="moved to val", replaceWith=@ReplaceWith(expression="size", imports={}), level=DeprecationLevel.ERROR)
    @JvmName(name="-deprecated_size")
    public final int -deprecated_size() {
        return this.size();
    }

    @JvmOverloads
    @NotNull
    public final ByteString substring(int beginIndex) {
        return ByteString.substring$default(this, beginIndex, 0, 2, null);
    }

    @JvmOverloads
    @NotNull
    public final ByteString substring() {
        return ByteString.substring$default(this, 0, 0, 3, null);
    }

    @JvmOverloads
    public final int indexOf(@NotNull ByteString other) {
        Intrinsics.checkNotNullParameter(other, "other");
        return ByteString.indexOf$default(this, other, 0, 2, null);
    }

    @JvmOverloads
    public final int indexOf(@NotNull byte[] other) {
        Intrinsics.checkNotNullParameter(other, "other");
        return ByteString.indexOf$default(this, other, 0, 2, null);
    }

    @JvmOverloads
    public final int lastIndexOf(@NotNull ByteString other) {
        Intrinsics.checkNotNullParameter(other, "other");
        return ByteString.lastIndexOf$default(this, other, 0, 2, null);
    }

    @JvmOverloads
    public final int lastIndexOf(@NotNull byte[] other) {
        Intrinsics.checkNotNullParameter(other, "other");
        return ByteString.lastIndexOf$default(this, other, 0, 2, null);
    }

    @JvmStatic
    @NotNull
    public static final ByteString of(byte ... data) {
        return Companion.of(data);
    }

    @JvmStatic
    @JvmName(name="of")
    @NotNull
    public static final ByteString of(@NotNull byte[] $this$of, int offset, int byteCount) {
        return Companion.of($this$of, offset, byteCount);
    }

    @JvmStatic
    @JvmName(name="of")
    @NotNull
    public static final ByteString of(@NotNull ByteBuffer $this$of) {
        return Companion.of($this$of);
    }

    @JvmStatic
    @NotNull
    public static final ByteString encodeUtf8(@NotNull String $this$encodeUtf8) {
        return Companion.encodeUtf8($this$encodeUtf8);
    }

    @JvmStatic
    @JvmName(name="encodeString")
    @NotNull
    public static final ByteString encodeString(@NotNull String $this$encodeString, @NotNull Charset charset) {
        return Companion.encodeString($this$encodeString, charset);
    }

    @JvmStatic
    @Nullable
    public static final ByteString decodeBase64(@NotNull String $this$decodeBase64) {
        return Companion.decodeBase64($this$decodeBase64);
    }

    @JvmStatic
    @NotNull
    public static final ByteString decodeHex(@NotNull String $this$decodeHex) {
        return Companion.decodeHex($this$decodeHex);
    }

    @JvmStatic
    @JvmName(name="read")
    @NotNull
    public static final ByteString read(@NotNull InputStream $this$read, int byteCount) throws IOException {
        return Companion.read($this$read, byteCount);
    }

    /*
     * Illegal identifiers - consider using --renameillegalidents true
     */
    @Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000N\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0012\n\u0002\u0010\u0005\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0017\u0010\u0007\u001a\u0004\u0018\u00010\u00042\u0006\u0010\b\u001a\u00020\tH\u0007\u00a2\u0006\u0002\b\nJ\u0015\u0010\u000b\u001a\u00020\u00042\u0006\u0010\b\u001a\u00020\tH\u0007\u00a2\u0006\u0002\b\fJ\u001d\u0010\r\u001a\u00020\u00042\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\u000e\u001a\u00020\u000fH\u0007\u00a2\u0006\u0002\b\u0010J\u0015\u0010\u0011\u001a\u00020\u00042\u0006\u0010\b\u001a\u00020\tH\u0007\u00a2\u0006\u0002\b\u0012J\u0015\u0010\u0013\u001a\u00020\u00042\u0006\u0010\u0014\u001a\u00020\u0015H\u0007\u00a2\u0006\u0002\b\u0016J\u0014\u0010\u0013\u001a\u00020\u00042\n\u0010\u0017\u001a\u00020\u0018\"\u00020\u0019H\u0007J%\u0010\u0013\u001a\u00020\u00042\u0006\u0010\u001a\u001a\u00020\u00182\u0006\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u001d\u001a\u00020\u001cH\u0007\u00a2\u0006\u0002\b\u0016J\u001d\u0010\u001e\u001a\u00020\u00042\u0006\u0010\u001f\u001a\u00020 2\u0006\u0010\u001d\u001a\u00020\u001cH\u0007\u00a2\u0006\u0002\b!J\u000e\u0010\u0007\u001a\u0004\u0018\u00010\u0004*\u00020\tH\u0007J\f\u0010\u000b\u001a\u00020\u0004*\u00020\tH\u0007J\u001b\u0010\"\u001a\u00020\u0004*\u00020\t2\b\b\u0002\u0010\u000e\u001a\u00020\u000fH\u0007\u00a2\u0006\u0002\b\rJ\f\u0010\u0011\u001a\u00020\u0004*\u00020\tH\u0007J\u0019\u0010#\u001a\u00020\u0004*\u00020 2\u0006\u0010\u001d\u001a\u00020\u001cH\u0007\u00a2\u0006\u0002\b\u001eJ\u0011\u0010$\u001a\u00020\u0004*\u00020\u0015H\u0007\u00a2\u0006\u0002\b\u0013J%\u0010$\u001a\u00020\u0004*\u00020\u00182\b\b\u0002\u0010\u001b\u001a\u00020\u001c2\b\b\u0002\u0010\u001d\u001a\u00020\u001cH\u0007\u00a2\u0006\u0002\b\u0013R\u0010\u0010\u0003\u001a\u00020\u00048\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006%"}, d2={"Lokio/ByteString$Companion;", "", "()V", "EMPTY", "Lokio/ByteString;", "serialVersionUID", "", "decodeBase64", "string", "", "-deprecated_decodeBase64", "decodeHex", "-deprecated_decodeHex", "encodeString", "charset", "Ljava/nio/charset/Charset;", "-deprecated_encodeString", "encodeUtf8", "-deprecated_encodeUtf8", "of", "buffer", "Ljava/nio/ByteBuffer;", "-deprecated_of", "data", "", "", "array", "offset", "", "byteCount", "read", "inputstream", "Ljava/io/InputStream;", "-deprecated_read", "encode", "readByteString", "toByteString", "okio"})
    @SourceDebugExtension(value={"SMAP\nByteString.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ByteString.kt\nokio/ByteString$Companion\n+ 2 ByteString.kt\nokio/internal/-ByteString\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,364:1\n271#2:365\n275#2,3:366\n282#2,3:369\n289#2,2:372\n295#2:374\n297#2,7:376\n1#3:375\n1#3:383\n*S KotlinDebug\n*F\n+ 1 ByteString.kt\nokio/ByteString$Companion\n*L\n234#1:365\n239#1:366,3\n251#1:369,3\n259#1:372,2\n262#1:374\n262#1:376,7\n262#1:375\n*E\n"})
    public static final class Companion {
        private Companion() {
        }

        @JvmStatic
        @NotNull
        public final ByteString of(byte ... data) {
            Intrinsics.checkNotNullParameter(data, "data");
            boolean $i$f$commonOf = false;
            byte[] byArray = Arrays.copyOf(data, data.length);
            Intrinsics.checkNotNullExpressionValue(byArray, "copyOf(this, size)");
            return new ByteString(byArray);
        }

        @JvmStatic
        @JvmName(name="of")
        @NotNull
        public final ByteString of(@NotNull byte[] $this$toByteString, int offset, int byteCount) {
            Intrinsics.checkNotNullParameter($this$toByteString, "<this>");
            byte[] $this$commonToByteString$iv = $this$toByteString;
            boolean $i$f$commonToByteString = false;
            int byteCount$iv = -SegmentedByteString.resolveDefaultParameter($this$commonToByteString$iv, byteCount);
            -SegmentedByteString.checkOffsetAndCount($this$commonToByteString$iv.length, offset, byteCount$iv);
            byte[] byArray = $this$commonToByteString$iv;
            int n = offset + byteCount$iv;
            return new ByteString(ArraysKt.copyOfRange(byArray, offset, n));
        }

        public static /* synthetic */ ByteString of$default(Companion companion, byte[] byArray, int n, int n2, int n3, Object object) {
            if ((n3 & 1) != 0) {
                n = 0;
            }
            if ((n3 & 2) != 0) {
                n2 = -SegmentedByteString.getDEFAULT__ByteString_size();
            }
            return companion.of(byArray, n, n2);
        }

        @JvmStatic
        @JvmName(name="of")
        @NotNull
        public final ByteString of(@NotNull ByteBuffer $this$toByteString) {
            Intrinsics.checkNotNullParameter($this$toByteString, "<this>");
            byte[] copy = new byte[$this$toByteString.remaining()];
            $this$toByteString.get(copy);
            return new ByteString(copy);
        }

        @JvmStatic
        @NotNull
        public final ByteString encodeUtf8(@NotNull String $this$encodeUtf8) {
            Intrinsics.checkNotNullParameter($this$encodeUtf8, "<this>");
            String $this$commonEncodeUtf8$iv = $this$encodeUtf8;
            boolean $i$f$commonEncodeUtf8 = false;
            ByteString byteString$iv = new ByteString(_JvmPlatformKt.asUtf8ToByteArray($this$commonEncodeUtf8$iv));
            byteString$iv.setUtf8$okio($this$commonEncodeUtf8$iv);
            return byteString$iv;
        }

        @JvmStatic
        @JvmName(name="encodeString")
        @NotNull
        public final ByteString encodeString(@NotNull String $this$encode, @NotNull Charset charset) {
            Intrinsics.checkNotNullParameter($this$encode, "<this>");
            Intrinsics.checkNotNullParameter(charset, "charset");
            byte[] byArray = $this$encode.getBytes(charset);
            Intrinsics.checkNotNullExpressionValue(byArray, "this as java.lang.String).getBytes(charset)");
            return new ByteString(byArray);
        }

        public static /* synthetic */ ByteString encodeString$default(Companion companion, String string, Charset charset, int n, Object object) {
            if ((n & 1) != 0) {
                charset = Charsets.UTF_8;
            }
            return companion.encodeString(string, charset);
        }

        @JvmStatic
        @Nullable
        public final ByteString decodeBase64(@NotNull String $this$decodeBase64) {
            Intrinsics.checkNotNullParameter($this$decodeBase64, "<this>");
            String $this$commonDecodeBase64$iv = $this$decodeBase64;
            boolean $i$f$commonDecodeBase64 = false;
            byte[] decoded$iv = -Base64.decodeBase64ToArray($this$commonDecodeBase64$iv);
            return decoded$iv != null ? new ByteString(decoded$iv) : null;
        }

        @JvmStatic
        @NotNull
        public final ByteString decodeHex(@NotNull String $this$decodeHex) {
            Intrinsics.checkNotNullParameter($this$decodeHex, "<this>");
            String $this$commonDecodeHex$iv = $this$decodeHex;
            boolean $i$f$commonDecodeHex = false;
            if (!($this$commonDecodeHex$iv.length() % 2 == 0)) {
                boolean $i$a$-require--ByteString$commonDecodeHex$1$iv22 = false;
                String $i$a$-require--ByteString$commonDecodeHex$1$iv22 = "Unexpected hex string: " + $this$commonDecodeHex$iv;
                throw new IllegalArgumentException($i$a$-require--ByteString$commonDecodeHex$1$iv22.toString());
            }
            byte[] result$iv = new byte[$this$commonDecodeHex$iv.length() / 2];
            int n = result$iv.length;
            for (int i$iv = 0; i$iv < n; ++i$iv) {
                int d1$iv = -ByteString.access$decodeHexDigit($this$commonDecodeHex$iv.charAt(i$iv * 2)) << 4;
                int d2$iv = -ByteString.access$decodeHexDigit($this$commonDecodeHex$iv.charAt(i$iv * 2 + 1));
                result$iv[i$iv] = (byte)(d1$iv + d2$iv);
            }
            return new ByteString(result$iv);
        }

        @JvmStatic
        @JvmName(name="read")
        @NotNull
        public final ByteString read(@NotNull InputStream $this$readByteString, int byteCount) throws IOException {
            Intrinsics.checkNotNullParameter($this$readByteString, "<this>");
            if (!(byteCount >= 0)) {
                boolean $i$a$-require-ByteString$Companion$readByteString$22 = false;
                String $i$a$-require-ByteString$Companion$readByteString$22 = "byteCount < 0: " + byteCount;
                throw new IllegalArgumentException($i$a$-require-ByteString$Companion$readByteString$22.toString());
            }
            byte[] result = new byte[byteCount];
            int read = 0;
            for (int offset = 0; offset < byteCount; offset += read) {
                read = $this$readByteString.read(result, offset, byteCount - offset);
                if (read != -1) continue;
                throw new EOFException();
            }
            return new ByteString(result);
        }

        @Deprecated(message="moved to extension function", replaceWith=@ReplaceWith(expression="string.decodeBase64()", imports={"okio.ByteString.Companion.decodeBase64"}), level=DeprecationLevel.ERROR)
        @JvmName(name="-deprecated_decodeBase64")
        @Nullable
        public final ByteString -deprecated_decodeBase64(@NotNull String string) {
            Intrinsics.checkNotNullParameter(string, "string");
            return this.decodeBase64(string);
        }

        @Deprecated(message="moved to extension function", replaceWith=@ReplaceWith(expression="string.decodeHex()", imports={"okio.ByteString.Companion.decodeHex"}), level=DeprecationLevel.ERROR)
        @JvmName(name="-deprecated_decodeHex")
        @NotNull
        public final ByteString -deprecated_decodeHex(@NotNull String string) {
            Intrinsics.checkNotNullParameter(string, "string");
            return this.decodeHex(string);
        }

        @Deprecated(message="moved to extension function", replaceWith=@ReplaceWith(expression="string.encode(charset)", imports={"okio.ByteString.Companion.encode"}), level=DeprecationLevel.ERROR)
        @JvmName(name="-deprecated_encodeString")
        @NotNull
        public final ByteString -deprecated_encodeString(@NotNull String string, @NotNull Charset charset) {
            Intrinsics.checkNotNullParameter(string, "string");
            Intrinsics.checkNotNullParameter(charset, "charset");
            return this.encodeString(string, charset);
        }

        @Deprecated(message="moved to extension function", replaceWith=@ReplaceWith(expression="string.encodeUtf8()", imports={"okio.ByteString.Companion.encodeUtf8"}), level=DeprecationLevel.ERROR)
        @JvmName(name="-deprecated_encodeUtf8")
        @NotNull
        public final ByteString -deprecated_encodeUtf8(@NotNull String string) {
            Intrinsics.checkNotNullParameter(string, "string");
            return this.encodeUtf8(string);
        }

        @Deprecated(message="moved to extension function", replaceWith=@ReplaceWith(expression="buffer.toByteString()", imports={"okio.ByteString.Companion.toByteString"}), level=DeprecationLevel.ERROR)
        @JvmName(name="-deprecated_of")
        @NotNull
        public final ByteString -deprecated_of(@NotNull ByteBuffer buffer) {
            Intrinsics.checkNotNullParameter(buffer, "buffer");
            return this.of(buffer);
        }

        @Deprecated(message="moved to extension function", replaceWith=@ReplaceWith(expression="array.toByteString(offset, byteCount)", imports={"okio.ByteString.Companion.toByteString"}), level=DeprecationLevel.ERROR)
        @JvmName(name="-deprecated_of")
        @NotNull
        public final ByteString -deprecated_of(@NotNull byte[] array, int offset, int byteCount) {
            Intrinsics.checkNotNullParameter(array, "array");
            return this.of(array, offset, byteCount);
        }

        @Deprecated(message="moved to extension function", replaceWith=@ReplaceWith(expression="inputstream.readByteString(byteCount)", imports={"okio.ByteString.Companion.readByteString"}), level=DeprecationLevel.ERROR)
        @JvmName(name="-deprecated_read")
        @NotNull
        public final ByteString -deprecated_read(@NotNull InputStream inputstream, int byteCount) {
            Intrinsics.checkNotNullParameter(inputstream, "inputstream");
            return this.read(inputstream, byteCount);
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }
}

