/*
 * Decompiled with CFR 0.152.
 */
package dev.serverpanel.lib.okhttp3;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import kotlin.Deprecated;
import kotlin.DeprecationLevel;
import kotlin.Metadata;
import kotlin.ReplaceWith;
import kotlin.internal.ProgressionUtilKt;
import kotlin.jvm.JvmName;
import kotlin.jvm.JvmOverloads;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
@Metadata(mv={1, 8, 0}, k=1, xi=48, d1={"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u0011\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0007\u0018\u0000 \u00182\u00020\u0001:\u0001\u0018B-\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00030\u0007\u00a2\u0006\u0002\u0010\bJ\u0016\u0010\u000b\u001a\u0004\u0018\u00010\f2\n\b\u0002\u0010\r\u001a\u0004\u0018\u00010\fH\u0007J\u0013\u0010\u000e\u001a\u00020\u000f2\b\u0010\u0010\u001a\u0004\u0018\u00010\u0001H\u0096\u0002J\b\u0010\u0011\u001a\u00020\u0012H\u0016J\u0010\u0010\u0013\u001a\u0004\u0018\u00010\u00032\u0006\u0010\u0014\u001a\u00020\u0003J\r\u0010\u0005\u001a\u00020\u0003H\u0007\u00a2\u0006\u0002\b\u0015J\b\u0010\u0016\u001a\u00020\u0003H\u0016J\r\u0010\u0004\u001a\u00020\u0003H\u0007\u00a2\u0006\u0002\b\u0017R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00030\u0007X\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\tR\u0013\u0010\u0005\u001a\u00020\u00038\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\nR\u0013\u0010\u0004\u001a\u00020\u00038\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0004\u0010\n\u00a8\u0006\u0019"}, d2={"Ldev/serverpanel/lib/okhttp3/MediaType;", "", "mediaType", "", "type", "subtype", "parameterNamesAndValues", "", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;)V", "[Ljava/lang/String;", "()Ljava/lang/String;", "charset", "Ljava/nio/charset/Charset;", "defaultValue", "equals", "", "other", "hashCode", "", "parameter", "name", "-deprecated_subtype", "toString", "-deprecated_type", "Companion", "okhttp"})
public final class MediaType {
    @NotNull
    public static final Companion Companion = new Companion(null);
    @NotNull
    private final String mediaType;
    @NotNull
    private final String type;
    @NotNull
    private final String subtype;
    @NotNull
    private final String[] parameterNamesAndValues;
    @NotNull
    private static final String TOKEN = "([a-zA-Z0-9-!#$%&'*+.^_`{|}~]+)";
    @NotNull
    private static final String QUOTED = "\"([^\"]*)\"";
    private static final Pattern TYPE_SUBTYPE = Pattern.compile("([a-zA-Z0-9-!#$%&'*+.^_`{|}~]+)/([a-zA-Z0-9-!#$%&'*+.^_`{|}~]+)");
    private static final Pattern PARAMETER = Pattern.compile(";\\s*(?:([a-zA-Z0-9-!#$%&'*+.^_`{|}~]+)=(?:([a-zA-Z0-9-!#$%&'*+.^_`{|}~]+)|\"([^\"]*)\"))?");

    private MediaType(String mediaType, String type, String subtype, String[] parameterNamesAndValues) {
        this.mediaType = mediaType;
        this.type = type;
        this.subtype = subtype;
        this.parameterNamesAndValues = parameterNamesAndValues;
    }

    @JvmName(name="type")
    @NotNull
    public final String type() {
        return this.type;
    }

    @JvmName(name="subtype")
    @NotNull
    public final String subtype() {
        return this.subtype;
    }

    @JvmOverloads
    @Nullable
    public final Charset charset(@Nullable Charset defaultValue) {
        Charset charset;
        String string = this.parameter("charset");
        if (string == null) {
            return defaultValue;
        }
        String charset2 = string;
        try {
            charset = Charset.forName(charset2);
        }
        catch (IllegalArgumentException _) {
            charset = defaultValue;
        }
        return charset;
    }

    public static /* synthetic */ Charset charset$default(MediaType mediaType, Charset charset, int n, Object object) {
        if ((n & 1) != 0) {
            charset = null;
        }
        return mediaType.charset(charset);
    }

    @Nullable
    public final String parameter(@NotNull String name) {
        Intrinsics.checkNotNullParameter(name, "name");
        int n = this.parameterNamesAndValues.length + -1;
        int i = 0;
        int n2 = ProgressionUtilKt.getProgressionLastElement(0, n, 2);
        if (i <= n2) {
            while (true) {
                if (StringsKt.equals(this.parameterNamesAndValues[i], name, true)) {
                    return this.parameterNamesAndValues[i + 1];
                }
                if (i == n2) break;
                i += 2;
            }
        }
        return null;
    }

    @Deprecated(message="moved to val", replaceWith=@ReplaceWith(expression="type", imports={}), level=DeprecationLevel.ERROR)
    @JvmName(name="-deprecated_type")
    @NotNull
    public final String -deprecated_type() {
        return this.type;
    }

    @Deprecated(message="moved to val", replaceWith=@ReplaceWith(expression="subtype", imports={}), level=DeprecationLevel.ERROR)
    @JvmName(name="-deprecated_subtype")
    @NotNull
    public final String -deprecated_subtype() {
        return this.subtype;
    }

    @NotNull
    public String toString() {
        return this.mediaType;
    }

    public boolean equals(@Nullable Object other) {
        return other instanceof MediaType && Intrinsics.areEqual(((MediaType)other).mediaType, this.mediaType);
    }

    public int hashCode() {
        return this.mediaType.hashCode();
    }

    @JvmOverloads
    @Nullable
    public final Charset charset() {
        return MediaType.charset$default(this, null, 1, null);
    }

    @JvmStatic
    @JvmName(name="get")
    @NotNull
    public static final MediaType get(@NotNull String $this$get) {
        return Companion.get($this$get);
    }

    @JvmStatic
    @JvmName(name="parse")
    @Nullable
    public static final MediaType parse(@NotNull String $this$parse) {
        return Companion.parse($this$parse);
    }

    public /* synthetic */ MediaType(String mediaType, String type, String subtype, String[] parameterNamesAndValues, DefaultConstructorMarker $constructor_marker) {
        this(mediaType, type, subtype, parameterNamesAndValues);
    }

    /*
     * Illegal identifiers - consider using --renameillegalidents true
     */
    @Metadata(mv={1, 8, 0}, k=1, xi=48, d1={"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0007\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0015\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u0007H\u0007\u00a2\u0006\u0002\b\rJ\u0017\u0010\u000e\u001a\u0004\u0018\u00010\u000b2\u0006\u0010\f\u001a\u00020\u0007H\u0007\u00a2\u0006\u0002\b\u000fJ\u0011\u0010\u0010\u001a\u00020\u000b*\u00020\u0007H\u0007\u00a2\u0006\u0002\b\nJ\u0013\u0010\u0011\u001a\u0004\u0018\u00010\u000b*\u00020\u0007H\u0007\u00a2\u0006\u0002\b\u000eR\u0016\u0010\u0003\u001a\n \u0005*\u0004\u0018\u00010\u00040\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0007X\u0082T\u00a2\u0006\u0002\n\u0000R\u0016\u0010\t\u001a\n \u0005*\u0004\u0018\u00010\u00040\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0012"}, d2={"Ldev/serverpanel/lib/okhttp3/MediaType$Companion;", "", "()V", "PARAMETER", "Ljava/util/regex/Pattern;", "kotlin.jvm.PlatformType", "QUOTED", "", "TOKEN", "TYPE_SUBTYPE", "get", "Ldev/serverpanel/lib/okhttp3/MediaType;", "mediaType", "-deprecated_get", "parse", "-deprecated_parse", "toMediaType", "toMediaTypeOrNull", "okhttp"})
    @SourceDebugExtension(value={"SMAP\nMediaType.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MediaType.kt\nokhttp3/MediaType$Companion\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 ArraysJVM.kt\nkotlin/collections/ArraysKt__ArraysJVMKt\n*L\n1#1,181:1\n1#2:182\n37#3,2:183\n*S KotlinDebug\n*F\n+ 1 MediaType.kt\nokhttp3/MediaType$Companion\n*L\n148#1:183,2\n*E\n"})
    public static final class Companion {
        private Companion() {
        }

        @JvmStatic
        @JvmName(name="get")
        @NotNull
        public final MediaType get(@NotNull String $this$toMediaType) {
            Intrinsics.checkNotNullParameter($this$toMediaType, "<this>");
            Matcher typeSubtype = TYPE_SUBTYPE.matcher($this$toMediaType);
            if (!typeSubtype.lookingAt()) {
                boolean $i$a$-require-MediaType$Companion$toMediaType$32 = false;
                String $i$a$-require-MediaType$Companion$toMediaType$32 = "No subtype found for: \"" + $this$toMediaType + '\"';
                throw new IllegalArgumentException($i$a$-require-MediaType$Companion$toMediaType$32.toString());
            }
            String string = typeSubtype.group(1);
            Intrinsics.checkNotNullExpressionValue(string, "typeSubtype.group(1)");
            String $i$a$-require-MediaType$Companion$toMediaType$32 = string;
            Locale locale = Locale.US;
            Intrinsics.checkNotNullExpressionValue(locale, "US");
            String string2 = $i$a$-require-MediaType$Companion$toMediaType$32.toLowerCase(locale);
            Intrinsics.checkNotNullExpressionValue(string2, "this as java.lang.String).toLowerCase(locale)");
            String type = string2;
            String string3 = typeSubtype.group(2);
            Intrinsics.checkNotNullExpressionValue(string3, "typeSubtype.group(2)");
            String string4 = string3;
            Locale locale2 = Locale.US;
            Intrinsics.checkNotNullExpressionValue(locale2, "US");
            String string5 = string4.toLowerCase(locale2);
            Intrinsics.checkNotNullExpressionValue(string5, "this as java.lang.String).toLowerCase(locale)");
            String subtype = string5;
            List parameterNamesAndValues = new ArrayList();
            Matcher parameter = PARAMETER.matcher($this$toMediaType);
            int s = 0;
            s = typeSubtype.end();
            while (s < $this$toMediaType.length()) {
                String string6;
                parameter.region(s, $this$toMediaType.length());
                if (!parameter.lookingAt()) {
                    boolean $i$a$-require-MediaType$Companion$toMediaType$42 = false;
                    StringBuilder stringBuilder = new StringBuilder().append("Parameter is not formatted correctly: \"");
                    String string7 = $this$toMediaType.substring(s);
                    Intrinsics.checkNotNullExpressionValue(string7, "this as java.lang.String).substring(startIndex)");
                    String $i$a$-require-MediaType$Companion$toMediaType$42 = stringBuilder.append(string7).append("\" for: \"").append($this$toMediaType).append('\"').toString();
                    throw new IllegalArgumentException($i$a$-require-MediaType$Companion$toMediaType$42.toString());
                }
                String name = parameter.group(1);
                if (name == null) {
                    s = parameter.end();
                    continue;
                }
                String token = parameter.group(2);
                if (token == null) {
                    string6 = parameter.group(3);
                } else if (StringsKt.startsWith$default(token, "'", false, 2, null) && StringsKt.endsWith$default(token, "'", false, 2, null) && token.length() > 2) {
                    String string8 = token.substring(1, token.length() - 1);
                    string6 = string8;
                    Intrinsics.checkNotNullExpressionValue(string8, "this as java.lang.String\u2026ing(startIndex, endIndex)");
                } else {
                    string6 = token;
                }
                String value = string6;
                ((Collection)parameterNamesAndValues).add(name);
                ((Collection)parameterNamesAndValues).add(value);
                s = parameter.end();
            }
            Collection $this$toTypedArray$iv = parameterNamesAndValues;
            boolean $i$f$toTypedArray = false;
            Collection thisCollection$iv = $this$toTypedArray$iv;
            return new MediaType($this$toMediaType, type, subtype, thisCollection$iv.toArray(new String[0]), null);
        }

        @JvmStatic
        @JvmName(name="parse")
        @Nullable
        public final MediaType parse(@NotNull String $this$toMediaTypeOrNull) {
            MediaType mediaType;
            Intrinsics.checkNotNullParameter($this$toMediaTypeOrNull, "<this>");
            try {
                mediaType = this.get($this$toMediaTypeOrNull);
            }
            catch (IllegalArgumentException _) {
                mediaType = null;
            }
            return mediaType;
        }

        @Deprecated(message="moved to extension function", replaceWith=@ReplaceWith(expression="mediaType.toMediaType()", imports={"dev.serverpanel.lib.okhttp3.MediaType.Companion.toMediaType"}), level=DeprecationLevel.ERROR)
        @JvmName(name="-deprecated_get")
        @NotNull
        public final MediaType -deprecated_get(@NotNull String mediaType) {
            Intrinsics.checkNotNullParameter(mediaType, "mediaType");
            return this.get(mediaType);
        }

        @Deprecated(message="moved to extension function", replaceWith=@ReplaceWith(expression="mediaType.toMediaTypeOrNull()", imports={"dev.serverpanel.lib.okhttp3.MediaType.Companion.toMediaTypeOrNull"}), level=DeprecationLevel.ERROR)
        @JvmName(name="-deprecated_parse")
        @Nullable
        public final MediaType -deprecated_parse(@NotNull String mediaType) {
            Intrinsics.checkNotNullParameter(mediaType, "mediaType");
            return this.parse(mediaType);
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }
}

