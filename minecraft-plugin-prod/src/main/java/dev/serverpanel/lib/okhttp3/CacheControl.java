/*
 * Decompiled with CFR 0.152.
 */
package dev.serverpanel.lib.okhttp3;

import dev.serverpanel.lib.okhttp3.Headers;
import dev.serverpanel.lib.okhttp3.internal.Util;
import java.util.concurrent.TimeUnit;
import kotlin.Deprecated;
import kotlin.DeprecationLevel;
import kotlin.Metadata;
import kotlin.ReplaceWith;
import kotlin.jvm.JvmField;
import kotlin.jvm.JvmName;
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
@Metadata(mv={1, 8, 0}, k=1, xi=48, d1={"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\n\n\u0002\u0010\u000e\n\u0002\b\u0011\u0018\u0000 !2\u00020\u0001:\u0002 !Bq\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\u0006\u0012\u0006\u0010\b\u001a\u00020\u0003\u0012\u0006\u0010\t\u001a\u00020\u0003\u0012\u0006\u0010\n\u001a\u00020\u0003\u0012\u0006\u0010\u000b\u001a\u00020\u0006\u0012\u0006\u0010\f\u001a\u00020\u0006\u0012\u0006\u0010\r\u001a\u00020\u0003\u0012\u0006\u0010\u000e\u001a\u00020\u0003\u0012\u0006\u0010\u000f\u001a\u00020\u0003\u0012\b\u0010\u0010\u001a\u0004\u0018\u00010\u0011\u00a2\u0006\u0002\u0010\u0012J\r\u0010\u000f\u001a\u00020\u0003H\u0007\u00a2\u0006\u0002\b\u0015J\r\u0010\u0005\u001a\u00020\u0006H\u0007\u00a2\u0006\u0002\b\u0016J\r\u0010\u000b\u001a\u00020\u0006H\u0007\u00a2\u0006\u0002\b\u0017J\r\u0010\f\u001a\u00020\u0006H\u0007\u00a2\u0006\u0002\b\u0018J\r\u0010\n\u001a\u00020\u0003H\u0007\u00a2\u0006\u0002\b\u0019J\r\u0010\u0002\u001a\u00020\u0003H\u0007\u00a2\u0006\u0002\b\u001aJ\r\u0010\u0004\u001a\u00020\u0003H\u0007\u00a2\u0006\u0002\b\u001bJ\r\u0010\u000e\u001a\u00020\u0003H\u0007\u00a2\u0006\u0002\b\u001cJ\r\u0010\r\u001a\u00020\u0003H\u0007\u00a2\u0006\u0002\b\u001dJ\r\u0010\u0007\u001a\u00020\u0006H\u0007\u00a2\u0006\u0002\b\u001eJ\b\u0010\u001f\u001a\u00020\u0011H\u0016R\u0010\u0010\u0010\u001a\u0004\u0018\u00010\u0011X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0013\u0010\u000f\u001a\u00020\u00038\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0013R\u0011\u0010\b\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\u0013R\u0011\u0010\t\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\u0013R\u0013\u0010\u0005\u001a\u00020\u00068\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0014R\u0013\u0010\u000b\u001a\u00020\u00068\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\u0014R\u0013\u0010\f\u001a\u00020\u00068\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\u0014R\u0013\u0010\n\u001a\u00020\u00038\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u0013R\u0013\u0010\u0002\u001a\u00020\u00038\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0002\u0010\u0013R\u0013\u0010\u0004\u001a\u00020\u00038\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0004\u0010\u0013R\u0013\u0010\u000e\u001a\u00020\u00038\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u0013R\u0013\u0010\r\u001a\u00020\u00038\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u0013R\u0013\u0010\u0007\u001a\u00020\u00068\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\u0014\u00a8\u0006\""}, d2={"Ldev/serverpanel/lib/okhttp3/CacheControl;", "", "noCache", "", "noStore", "maxAgeSeconds", "", "sMaxAgeSeconds", "isPrivate", "isPublic", "mustRevalidate", "maxStaleSeconds", "minFreshSeconds", "onlyIfCached", "noTransform", "immutable", "headerValue", "", "(ZZIIZZZIIZZZLjava/lang/String;)V", "()Z", "()I", "-deprecated_immutable", "-deprecated_maxAgeSeconds", "-deprecated_maxStaleSeconds", "-deprecated_minFreshSeconds", "-deprecated_mustRevalidate", "-deprecated_noCache", "-deprecated_noStore", "-deprecated_noTransform", "-deprecated_onlyIfCached", "-deprecated_sMaxAgeSeconds", "toString", "Builder", "Companion", "okhttp"})
public final class CacheControl {
    @NotNull
    public static final Companion Companion = new Companion(null);
    private final boolean noCache;
    private final boolean noStore;
    private final int maxAgeSeconds;
    private final int sMaxAgeSeconds;
    private final boolean isPrivate;
    private final boolean isPublic;
    private final boolean mustRevalidate;
    private final int maxStaleSeconds;
    private final int minFreshSeconds;
    private final boolean onlyIfCached;
    private final boolean noTransform;
    private final boolean immutable;
    @Nullable
    private String headerValue;
    @JvmField
    @NotNull
    public static final CacheControl FORCE_NETWORK = new Builder().noCache().build();
    @JvmField
    @NotNull
    public static final CacheControl FORCE_CACHE = new Builder().onlyIfCached().maxStale(Integer.MAX_VALUE, TimeUnit.SECONDS).build();

    private CacheControl(boolean noCache, boolean noStore, int maxAgeSeconds, int sMaxAgeSeconds, boolean isPrivate, boolean isPublic, boolean mustRevalidate, int maxStaleSeconds, int minFreshSeconds, boolean onlyIfCached, boolean noTransform, boolean immutable, String headerValue) {
        this.noCache = noCache;
        this.noStore = noStore;
        this.maxAgeSeconds = maxAgeSeconds;
        this.sMaxAgeSeconds = sMaxAgeSeconds;
        this.isPrivate = isPrivate;
        this.isPublic = isPublic;
        this.mustRevalidate = mustRevalidate;
        this.maxStaleSeconds = maxStaleSeconds;
        this.minFreshSeconds = minFreshSeconds;
        this.onlyIfCached = onlyIfCached;
        this.noTransform = noTransform;
        this.immutable = immutable;
        this.headerValue = headerValue;
    }

    @JvmName(name="noCache")
    public final boolean noCache() {
        return this.noCache;
    }

    @JvmName(name="noStore")
    public final boolean noStore() {
        return this.noStore;
    }

    @JvmName(name="maxAgeSeconds")
    public final int maxAgeSeconds() {
        return this.maxAgeSeconds;
    }

    @JvmName(name="sMaxAgeSeconds")
    public final int sMaxAgeSeconds() {
        return this.sMaxAgeSeconds;
    }

    public final boolean isPrivate() {
        return this.isPrivate;
    }

    public final boolean isPublic() {
        return this.isPublic;
    }

    @JvmName(name="mustRevalidate")
    public final boolean mustRevalidate() {
        return this.mustRevalidate;
    }

    @JvmName(name="maxStaleSeconds")
    public final int maxStaleSeconds() {
        return this.maxStaleSeconds;
    }

    @JvmName(name="minFreshSeconds")
    public final int minFreshSeconds() {
        return this.minFreshSeconds;
    }

    @JvmName(name="onlyIfCached")
    public final boolean onlyIfCached() {
        return this.onlyIfCached;
    }

    @JvmName(name="noTransform")
    public final boolean noTransform() {
        return this.noTransform;
    }

    @JvmName(name="immutable")
    public final boolean immutable() {
        return this.immutable;
    }

    @Deprecated(message="moved to val", replaceWith=@ReplaceWith(expression="noCache", imports={}), level=DeprecationLevel.ERROR)
    @JvmName(name="-deprecated_noCache")
    public final boolean -deprecated_noCache() {
        return this.noCache;
    }

    @Deprecated(message="moved to val", replaceWith=@ReplaceWith(expression="noStore", imports={}), level=DeprecationLevel.ERROR)
    @JvmName(name="-deprecated_noStore")
    public final boolean -deprecated_noStore() {
        return this.noStore;
    }

    @Deprecated(message="moved to val", replaceWith=@ReplaceWith(expression="maxAgeSeconds", imports={}), level=DeprecationLevel.ERROR)
    @JvmName(name="-deprecated_maxAgeSeconds")
    public final int -deprecated_maxAgeSeconds() {
        return this.maxAgeSeconds;
    }

    @Deprecated(message="moved to val", replaceWith=@ReplaceWith(expression="sMaxAgeSeconds", imports={}), level=DeprecationLevel.ERROR)
    @JvmName(name="-deprecated_sMaxAgeSeconds")
    public final int -deprecated_sMaxAgeSeconds() {
        return this.sMaxAgeSeconds;
    }

    @Deprecated(message="moved to val", replaceWith=@ReplaceWith(expression="mustRevalidate", imports={}), level=DeprecationLevel.ERROR)
    @JvmName(name="-deprecated_mustRevalidate")
    public final boolean -deprecated_mustRevalidate() {
        return this.mustRevalidate;
    }

    @Deprecated(message="moved to val", replaceWith=@ReplaceWith(expression="maxStaleSeconds", imports={}), level=DeprecationLevel.ERROR)
    @JvmName(name="-deprecated_maxStaleSeconds")
    public final int -deprecated_maxStaleSeconds() {
        return this.maxStaleSeconds;
    }

    @Deprecated(message="moved to val", replaceWith=@ReplaceWith(expression="minFreshSeconds", imports={}), level=DeprecationLevel.ERROR)
    @JvmName(name="-deprecated_minFreshSeconds")
    public final int -deprecated_minFreshSeconds() {
        return this.minFreshSeconds;
    }

    @Deprecated(message="moved to val", replaceWith=@ReplaceWith(expression="onlyIfCached", imports={}), level=DeprecationLevel.ERROR)
    @JvmName(name="-deprecated_onlyIfCached")
    public final boolean -deprecated_onlyIfCached() {
        return this.onlyIfCached;
    }

    @Deprecated(message="moved to val", replaceWith=@ReplaceWith(expression="noTransform", imports={}), level=DeprecationLevel.ERROR)
    @JvmName(name="-deprecated_noTransform")
    public final boolean -deprecated_noTransform() {
        return this.noTransform;
    }

    @Deprecated(message="moved to val", replaceWith=@ReplaceWith(expression="immutable", imports={}), level=DeprecationLevel.ERROR)
    @JvmName(name="-deprecated_immutable")
    public final boolean -deprecated_immutable() {
        return this.immutable;
    }

    @NotNull
    public String toString() {
        String result = this.headerValue;
        if (result == null) {
            StringBuilder stringBuilder;
            StringBuilder $this$toString_u24lambda_u240 = stringBuilder = new StringBuilder();
            boolean bl = false;
            if (this.noCache) {
                $this$toString_u24lambda_u240.append("no-cache, ");
            }
            if (this.noStore) {
                $this$toString_u24lambda_u240.append("no-store, ");
            }
            if (this.maxAgeSeconds != -1) {
                $this$toString_u24lambda_u240.append("max-age=").append(this.maxAgeSeconds).append(", ");
            }
            if (this.sMaxAgeSeconds != -1) {
                $this$toString_u24lambda_u240.append("s-maxage=").append(this.sMaxAgeSeconds).append(", ");
            }
            if (this.isPrivate) {
                $this$toString_u24lambda_u240.append("private, ");
            }
            if (this.isPublic) {
                $this$toString_u24lambda_u240.append("public, ");
            }
            if (this.mustRevalidate) {
                $this$toString_u24lambda_u240.append("must-revalidate, ");
            }
            if (this.maxStaleSeconds != -1) {
                $this$toString_u24lambda_u240.append("max-stale=").append(this.maxStaleSeconds).append(", ");
            }
            if (this.minFreshSeconds != -1) {
                $this$toString_u24lambda_u240.append("min-fresh=").append(this.minFreshSeconds).append(", ");
            }
            if (this.onlyIfCached) {
                $this$toString_u24lambda_u240.append("only-if-cached, ");
            }
            if (this.noTransform) {
                $this$toString_u24lambda_u240.append("no-transform, ");
            }
            if (this.immutable) {
                $this$toString_u24lambda_u240.append("immutable, ");
            }
            if (((CharSequence)$this$toString_u24lambda_u240).length() == 0) {
                return "";
            }
            $this$toString_u24lambda_u240.delete($this$toString_u24lambda_u240.length() - 2, $this$toString_u24lambda_u240.length());
            String string = stringBuilder.toString();
            Intrinsics.checkNotNullExpressionValue(string, "StringBuilder().apply(builderAction).toString()");
            this.headerValue = result = string;
        }
        return result;
    }

    @JvmStatic
    @NotNull
    public static final CacheControl parse(@NotNull Headers headers) {
        return Companion.parse(headers);
    }

    public /* synthetic */ CacheControl(boolean noCache, boolean noStore, int maxAgeSeconds, int sMaxAgeSeconds, boolean isPrivate, boolean isPublic, boolean mustRevalidate, int maxStaleSeconds, int minFreshSeconds, boolean onlyIfCached, boolean noTransform, boolean immutable, String headerValue, DefaultConstructorMarker $constructor_marker) {
        this(noCache, noStore, maxAgeSeconds, sMaxAgeSeconds, isPrivate, isPublic, mustRevalidate, maxStaleSeconds, minFreshSeconds, onlyIfCached, noTransform, immutable, headerValue);
    }

    @Metadata(mv={1, 8, 0}, k=1, xi=48, d1={"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\t\n\u0000\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0006\u0010\r\u001a\u00020\u000eJ\u0006\u0010\u0003\u001a\u00020\u0000J\u0016\u0010\u000f\u001a\u00020\u00002\u0006\u0010\u000f\u001a\u00020\u00062\u0006\u0010\u0010\u001a\u00020\u0011J\u0016\u0010\u0012\u001a\u00020\u00002\u0006\u0010\u0012\u001a\u00020\u00062\u0006\u0010\u0010\u001a\u00020\u0011J\u0016\u0010\u0013\u001a\u00020\u00002\u0006\u0010\u0013\u001a\u00020\u00062\u0006\u0010\u0010\u001a\u00020\u0011J\u0006\u0010\t\u001a\u00020\u0000J\u0006\u0010\n\u001a\u00020\u0000J\u0006\u0010\u000b\u001a\u00020\u0000J\u0006\u0010\f\u001a\u00020\u0000J\f\u0010\u0014\u001a\u00020\u0006*\u00020\u0015H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0016"}, d2={"Ldev/serverpanel/lib/okhttp3/CacheControl$Builder;", "", "()V", "immutable", "", "maxAgeSeconds", "", "maxStaleSeconds", "minFreshSeconds", "noCache", "noStore", "noTransform", "onlyIfCached", "build", "Ldev/serverpanel/lib/okhttp3/CacheControl;", "maxAge", "timeUnit", "Ljava/util/concurrent/TimeUnit;", "maxStale", "minFresh", "clampToInt", "", "okhttp"})
    @SourceDebugExtension(value={"SMAP\nCacheControl.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CacheControl.kt\nokhttp3/CacheControl$Builder\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,416:1\n1#2:417\n*E\n"})
    public static final class Builder {
        private boolean noCache;
        private boolean noStore;
        private int maxAgeSeconds = -1;
        private int maxStaleSeconds = -1;
        private int minFreshSeconds = -1;
        private boolean onlyIfCached;
        private boolean noTransform;
        private boolean immutable;

        @NotNull
        public final Builder noCache() {
            Builder builder;
            Builder $this$noCache_u24lambda_u240 = builder = this;
            boolean bl = false;
            $this$noCache_u24lambda_u240.noCache = true;
            return builder;
        }

        @NotNull
        public final Builder noStore() {
            Builder builder;
            Builder $this$noStore_u24lambda_u241 = builder = this;
            boolean bl = false;
            $this$noStore_u24lambda_u241.noStore = true;
            return builder;
        }

        @NotNull
        public final Builder maxAge(int maxAge, @NotNull TimeUnit timeUnit) {
            Builder builder;
            Intrinsics.checkNotNullParameter((Object)timeUnit, "timeUnit");
            Builder $this$maxAge_u24lambda_u243 = builder = this;
            boolean bl = false;
            if (!(maxAge >= 0)) {
                boolean bl2 = false;
                String string = "maxAge < 0: " + maxAge;
                throw new IllegalArgumentException(string.toString());
            }
            long maxAgeSecondsLong = timeUnit.toSeconds(maxAge);
            $this$maxAge_u24lambda_u243.maxAgeSeconds = $this$maxAge_u24lambda_u243.clampToInt(maxAgeSecondsLong);
            return builder;
        }

        @NotNull
        public final Builder maxStale(int maxStale, @NotNull TimeUnit timeUnit) {
            Builder builder;
            Intrinsics.checkNotNullParameter((Object)timeUnit, "timeUnit");
            Builder $this$maxStale_u24lambda_u245 = builder = this;
            boolean bl = false;
            if (!(maxStale >= 0)) {
                boolean bl2 = false;
                String string = "maxStale < 0: " + maxStale;
                throw new IllegalArgumentException(string.toString());
            }
            long maxStaleSecondsLong = timeUnit.toSeconds(maxStale);
            $this$maxStale_u24lambda_u245.maxStaleSeconds = $this$maxStale_u24lambda_u245.clampToInt(maxStaleSecondsLong);
            return builder;
        }

        @NotNull
        public final Builder minFresh(int minFresh, @NotNull TimeUnit timeUnit) {
            Builder builder;
            Intrinsics.checkNotNullParameter((Object)timeUnit, "timeUnit");
            Builder $this$minFresh_u24lambda_u247 = builder = this;
            boolean bl = false;
            if (!(minFresh >= 0)) {
                boolean bl2 = false;
                String string = "minFresh < 0: " + minFresh;
                throw new IllegalArgumentException(string.toString());
            }
            long minFreshSecondsLong = timeUnit.toSeconds(minFresh);
            $this$minFresh_u24lambda_u247.minFreshSeconds = $this$minFresh_u24lambda_u247.clampToInt(minFreshSecondsLong);
            return builder;
        }

        @NotNull
        public final Builder onlyIfCached() {
            Builder builder;
            Builder $this$onlyIfCached_u24lambda_u248 = builder = this;
            boolean bl = false;
            $this$onlyIfCached_u24lambda_u248.onlyIfCached = true;
            return builder;
        }

        @NotNull
        public final Builder noTransform() {
            Builder builder;
            Builder $this$noTransform_u24lambda_u249 = builder = this;
            boolean bl = false;
            $this$noTransform_u24lambda_u249.noTransform = true;
            return builder;
        }

        @NotNull
        public final Builder immutable() {
            Builder builder;
            Builder $this$immutable_u24lambda_u2410 = builder = this;
            boolean bl = false;
            $this$immutable_u24lambda_u2410.immutable = true;
            return builder;
        }

        private final int clampToInt(long $this$clampToInt) {
            return $this$clampToInt > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)$this$clampToInt;
        }

        @NotNull
        public final CacheControl build() {
            return new CacheControl(this.noCache, this.noStore, this.maxAgeSeconds, -1, false, false, false, this.maxStaleSeconds, this.minFreshSeconds, this.onlyIfCached, this.noTransform, this.immutable, null, null);
        }
    }

    @Metadata(mv={1, 8, 0}, k=1, xi=48, d1={"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\u0010\u000e\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0006\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\bH\u0007J\u001e\u0010\t\u001a\u00020\n*\u00020\u000b2\u0006\u0010\f\u001a\u00020\u000b2\b\b\u0002\u0010\r\u001a\u00020\nH\u0002R\u0010\u0010\u0003\u001a\u00020\u00048\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0005\u001a\u00020\u00048\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000e"}, d2={"Ldev/serverpanel/lib/okhttp3/CacheControl$Companion;", "", "()V", "FORCE_CACHE", "Ldev/serverpanel/lib/okhttp3/CacheControl;", "FORCE_NETWORK", "parse", "headers", "Ldev/serverpanel/lib/okhttp3/Headers;", "indexOfElement", "", "", "characters", "startIndex", "okhttp"})
    public static final class Companion {
        private Companion() {
        }

        @JvmStatic
        @NotNull
        public final CacheControl parse(@NotNull Headers headers) {
            Intrinsics.checkNotNullParameter(headers, "headers");
            boolean noCache = false;
            boolean noStore = false;
            int maxAgeSeconds = -1;
            int sMaxAgeSeconds = -1;
            boolean isPrivate = false;
            boolean isPublic = false;
            boolean mustRevalidate = false;
            int maxStaleSeconds = -1;
            int minFreshSeconds = -1;
            boolean onlyIfCached = false;
            boolean noTransform = false;
            boolean immutable = false;
            boolean canUseHeaderValue = true;
            String headerValue = null;
            int n = headers.size();
            for (int i = 0; i < n; ++i) {
                String name = headers.name(i);
                String value = headers.value(i);
                if (StringsKt.equals(name, "Cache-Control", true)) {
                    if (headerValue != null) {
                        canUseHeaderValue = false;
                    } else {
                        headerValue = value;
                    }
                } else {
                    if (!StringsKt.equals(name, "Pragma", true)) continue;
                    canUseHeaderValue = false;
                }
                int pos = 0;
                while (pos < value.length()) {
                    int tokenStart = pos;
                    pos = this.indexOfElement(value, "=,;", pos);
                    String string = value.substring(tokenStart, pos);
                    Intrinsics.checkNotNullExpressionValue(string, "this as java.lang.String\u2026ing(startIndex, endIndex)");
                    String directive = ((Object)StringsKt.trim((CharSequence)string)).toString();
                    String parameter = null;
                    if (pos == value.length() || value.charAt(pos) == ',' || value.charAt(pos) == ';') {
                        ++pos;
                        parameter = null;
                    } else {
                        int parameterStart;
                        ++pos;
                        if ((pos = Util.indexOfNonWhitespace(value, pos)) < value.length() && value.charAt(pos) == '\"') {
                            parameterStart = ++pos;
                            pos = StringsKt.indexOf$default((CharSequence)value, '\"', pos, false, 4, null);
                            String string2 = value.substring(parameterStart, pos);
                            Intrinsics.checkNotNullExpressionValue(string2, "this as java.lang.String\u2026ing(startIndex, endIndex)");
                            parameter = string2;
                            ++pos;
                        } else {
                            parameterStart = pos;
                            pos = this.indexOfElement(value, ",;", pos);
                            String string3 = value.substring(parameterStart, pos);
                            Intrinsics.checkNotNullExpressionValue(string3, "this as java.lang.String\u2026ing(startIndex, endIndex)");
                            parameter = ((Object)StringsKt.trim((CharSequence)string3)).toString();
                        }
                    }
                    if (StringsKt.equals("no-cache", directive, true)) {
                        noCache = true;
                        continue;
                    }
                    if (StringsKt.equals("no-store", directive, true)) {
                        noStore = true;
                        continue;
                    }
                    if (StringsKt.equals("max-age", directive, true)) {
                        maxAgeSeconds = Util.toNonNegativeInt(parameter, -1);
                        continue;
                    }
                    if (StringsKt.equals("s-maxage", directive, true)) {
                        sMaxAgeSeconds = Util.toNonNegativeInt(parameter, -1);
                        continue;
                    }
                    if (StringsKt.equals("private", directive, true)) {
                        isPrivate = true;
                        continue;
                    }
                    if (StringsKt.equals("public", directive, true)) {
                        isPublic = true;
                        continue;
                    }
                    if (StringsKt.equals("must-revalidate", directive, true)) {
                        mustRevalidate = true;
                        continue;
                    }
                    if (StringsKt.equals("max-stale", directive, true)) {
                        maxStaleSeconds = Util.toNonNegativeInt(parameter, Integer.MAX_VALUE);
                        continue;
                    }
                    if (StringsKt.equals("min-fresh", directive, true)) {
                        minFreshSeconds = Util.toNonNegativeInt(parameter, -1);
                        continue;
                    }
                    if (StringsKt.equals("only-if-cached", directive, true)) {
                        onlyIfCached = true;
                        continue;
                    }
                    if (StringsKt.equals("no-transform", directive, true)) {
                        noTransform = true;
                        continue;
                    }
                    if (!StringsKt.equals("immutable", directive, true)) continue;
                    immutable = true;
                }
            }
            if (!canUseHeaderValue) {
                headerValue = null;
            }
            return new CacheControl(noCache, noStore, maxAgeSeconds, sMaxAgeSeconds, isPrivate, isPublic, mustRevalidate, maxStaleSeconds, minFreshSeconds, onlyIfCached, noTransform, immutable, headerValue, null);
        }

        private final int indexOfElement(String $this$indexOfElement, String characters, int startIndex) {
            int n = $this$indexOfElement.length();
            for (int i = startIndex; i < n; ++i) {
                if (!StringsKt.contains$default((CharSequence)characters, $this$indexOfElement.charAt(i), false, 2, null)) continue;
                return i;
            }
            return $this$indexOfElement.length();
        }

        static /* synthetic */ int indexOfElement$default(Companion companion, String string, String string2, int n, int n2, Object object) {
            if ((n2 & 2) != 0) {
                n = 0;
            }
            return companion.indexOfElement(string, string2, n);
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }
}

