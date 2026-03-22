/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
 */
package dev.serverpanel.lib.okhttp3;

import dev.serverpanel.lib.okhttp3.internal.Util;
import dev.serverpanel.lib.okhttp3.internal.http.DatesKt;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import kotlin.Deprecated;
import kotlin.DeprecationLevel;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.ReplaceWith;
import kotlin.TuplesKt;
import kotlin.collections.CollectionsKt;
import kotlin.internal.ProgressionUtilKt;
import kotlin.jvm.JvmName;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.ArrayIteratorKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.jvm.internal.StringCompanionObject;
import kotlin.jvm.internal.markers.KMappedMarker;
import kotlin.text.StringsKt;
import org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
@Metadata(mv={1, 8, 0}, k=1, xi=48, d1={"\u0000f\n\u0002\u0018\u0002\n\u0002\u0010\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0011\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010(\n\u0002\b\u0002\n\u0002\u0010\"\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010$\n\u0002\u0010 \n\u0002\b\u0006\u0018\u0000 '2\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00030\u00020\u0001:\u0002&'B\u0015\b\u0002\u0012\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00030\u0005\u00a2\u0006\u0002\u0010\u0006J\u0006\u0010\u000b\u001a\u00020\fJ\u0013\u0010\r\u001a\u00020\u000e2\b\u0010\u000f\u001a\u0004\u0018\u00010\u0010H\u0096\u0002J\u0013\u0010\u0011\u001a\u0004\u0018\u00010\u00032\u0006\u0010\u0012\u001a\u00020\u0003H\u0086\u0002J\u0010\u0010\u0013\u001a\u0004\u0018\u00010\u00142\u0006\u0010\u0012\u001a\u00020\u0003J\u0012\u0010\u0015\u001a\u0004\u0018\u00010\u00162\u0006\u0010\u0012\u001a\u00020\u0003H\u0007J\b\u0010\u0017\u001a\u00020\tH\u0016J\u001b\u0010\u0018\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00030\u00020\u0019H\u0096\u0002J\u000e\u0010\u0012\u001a\u00020\u00032\u0006\u0010\u001a\u001a\u00020\tJ\f\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u00030\u001cJ\u0006\u0010\u001d\u001a\u00020\u001eJ\r\u0010\b\u001a\u00020\tH\u0007\u00a2\u0006\u0002\b\u001fJ\u0018\u0010 \u001a\u0014\u0012\u0004\u0012\u00020\u0003\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00030\"0!J\b\u0010#\u001a\u00020\u0003H\u0016J\u000e\u0010$\u001a\u00020\u00032\u0006\u0010\u001a\u001a\u00020\tJ\u0014\u0010%\u001a\b\u0012\u0004\u0012\u00020\u00030\"2\u0006\u0010\u0012\u001a\u00020\u0003R\u0016\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00030\u0005X\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\u0007R\u0011\u0010\b\u001a\u00020\t8G\u00a2\u0006\u0006\u001a\u0004\b\b\u0010\n\u00a8\u0006("}, d2={"Ldev/serverpanel/lib/okhttp3/Headers;", "", "Lkotlin/Pair;", "", "namesAndValues", "", "([Ljava/lang/String;)V", "[Ljava/lang/String;", "size", "", "()I", "byteCount", "", "equals", "", "other", "", "get", "name", "getDate", "Ljava/util/Date;", "getInstant", "Ljava/time/Instant;", "hashCode", "iterator", "", "index", "names", "", "newBuilder", "Ldev/serverpanel/lib/okhttp3/Headers$Builder;", "-deprecated_size", "toMultimap", "", "", "toString", "value", "values", "Builder", "Companion", "okhttp"})
public final class Headers
implements Iterable<Pair<? extends String, ? extends String>>,
KMappedMarker {
    @NotNull
    public static final Companion Companion = new Companion(null);
    @NotNull
    private final String[] namesAndValues;

    private Headers(String[] namesAndValues) {
        this.namesAndValues = namesAndValues;
    }

    @Nullable
    public final String get(@NotNull String name) {
        Intrinsics.checkNotNullParameter(name, "name");
        return Headers.Companion.get(this.namesAndValues, name);
    }

    @Nullable
    public final Date getDate(@NotNull String name) {
        Intrinsics.checkNotNullParameter(name, "name");
        String string = this.get(name);
        return string != null ? DatesKt.toHttpDateOrNull(string) : null;
    }

    @IgnoreJRERequirement
    @Nullable
    public final Instant getInstant(@NotNull String name) {
        Date value;
        Intrinsics.checkNotNullParameter(name, "name");
        Date date = value = this.getDate(name);
        return date != null ? date.toInstant() : null;
    }

    @JvmName(name="size")
    public final int size() {
        return this.namesAndValues.length / 2;
    }

    @Deprecated(message="moved to val", replaceWith=@ReplaceWith(expression="size", imports={}), level=DeprecationLevel.ERROR)
    @JvmName(name="-deprecated_size")
    public final int -deprecated_size() {
        return this.size();
    }

    @NotNull
    public final String name(int index) {
        return this.namesAndValues[index * 2];
    }

    @NotNull
    public final String value(int index) {
        return this.namesAndValues[index * 2 + 1];
    }

    @NotNull
    public final Set<String> names() {
        TreeSet<String> result = new TreeSet<String>(StringsKt.getCASE_INSENSITIVE_ORDER(StringCompanionObject.INSTANCE));
        int n = this.size();
        for (int i = 0; i < n; ++i) {
            result.add(this.name(i));
        }
        Set<String> set = Collections.unmodifiableSet((Set)result);
        Intrinsics.checkNotNullExpressionValue(set, "unmodifiableSet(result)");
        return set;
    }

    @NotNull
    public final List<String> values(@NotNull String name) {
        List<String> list;
        Intrinsics.checkNotNullParameter(name, "name");
        List result = null;
        int n = this.size();
        for (int i = 0; i < n; ++i) {
            if (!StringsKt.equals(name, this.name(i), true)) continue;
            if (result == null) {
                result = new ArrayList(2);
            }
            result.add(this.value(i));
        }
        if (result != null) {
            List list2 = Collections.unmodifiableList(result);
            list = list2;
            Intrinsics.checkNotNullExpressionValue(list2, "{\n      Collections.unmodifiableList(result)\n    }");
        } else {
            list = CollectionsKt.emptyList();
        }
        return list;
    }

    public final long byteCount() {
        long result = this.namesAndValues.length * 2;
        int n = this.namesAndValues.length;
        for (int i = 0; i < n; ++i) {
            result += (long)this.namesAndValues[i].length();
        }
        return result;
    }

    @Override
    @NotNull
    public Iterator<Pair<String, String>> iterator() {
        int n = 0;
        int n2 = this.size();
        Pair[] pairArray = new Pair[n2];
        while (n < n2) {
            int n3 = n++;
            pairArray[n3] = TuplesKt.to(this.name(n3), this.value(n3));
        }
        return ArrayIteratorKt.iterator(pairArray);
    }

    @NotNull
    public final Builder newBuilder() {
        Builder result = new Builder();
        CollectionsKt.addAll((Collection)result.getNamesAndValues$okhttp(), this.namesAndValues);
        return result;
    }

    public boolean equals(@Nullable Object other) {
        return other instanceof Headers && Arrays.equals(this.namesAndValues, ((Headers)other).namesAndValues);
    }

    public int hashCode() {
        return Arrays.hashCode(this.namesAndValues);
    }

    @NotNull
    public String toString() {
        StringBuilder stringBuilder;
        StringBuilder $this$toString_u24lambda_u240 = stringBuilder = new StringBuilder();
        boolean bl = false;
        int n = this.size();
        for (int i = 0; i < n; ++i) {
            String name = this.name(i);
            String value = this.value(i);
            $this$toString_u24lambda_u240.append(name);
            $this$toString_u24lambda_u240.append(": ");
            $this$toString_u24lambda_u240.append(Util.isSensitiveHeader(name) ? "\u2588\u2588" : value);
            $this$toString_u24lambda_u240.append("\n");
        }
        String string = stringBuilder.toString();
        Intrinsics.checkNotNullExpressionValue(string, "StringBuilder().apply(builderAction).toString()");
        return string;
    }

    @NotNull
    public final Map<String, List<String>> toMultimap() {
        TreeMap result = new TreeMap(StringsKt.getCASE_INSENSITIVE_ORDER(StringCompanionObject.INSTANCE));
        int n = this.size();
        for (int i = 0; i < n; ++i) {
            String name;
            String string = this.name(i);
            Locale locale = Locale.US;
            Intrinsics.checkNotNullExpressionValue(locale, "US");
            Intrinsics.checkNotNullExpressionValue(string.toLowerCase(locale), "this as java.lang.String).toLowerCase(locale)");
            List values2 = (List)result.get(name);
            if (values2 == null) {
                values2 = new ArrayList(2);
                ((Map)result).put(name, values2);
            }
            values2.add(this.value(i));
        }
        return result;
    }

    @JvmStatic
    @JvmName(name="of")
    @NotNull
    public static final Headers of(String ... namesAndValues) {
        return Companion.of(namesAndValues);
    }

    @JvmStatic
    @JvmName(name="of")
    @NotNull
    public static final Headers of(@NotNull Map<String, String> $this$of) {
        return Companion.of($this$of);
    }

    public /* synthetic */ Headers(String[] namesAndValues, DefaultConstructorMarker $constructor_marker) {
        this(namesAndValues);
    }

    @Metadata(mv={1, 8, 0}, k=1, xi=48, d1={"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010!\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\b\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\b\u001a\u00020\u00002\u0006\u0010\t\u001a\u00020\u0005J\u0018\u0010\b\u001a\u00020\u00002\u0006\u0010\n\u001a\u00020\u00052\u0006\u0010\u000b\u001a\u00020\fH\u0007J\u0016\u0010\b\u001a\u00020\u00002\u0006\u0010\n\u001a\u00020\u00052\u0006\u0010\u000b\u001a\u00020\rJ\u0016\u0010\b\u001a\u00020\u00002\u0006\u0010\n\u001a\u00020\u00052\u0006\u0010\u000b\u001a\u00020\u0005J\u000e\u0010\u000e\u001a\u00020\u00002\u0006\u0010\u000f\u001a\u00020\u0010J\u0015\u0010\u0011\u001a\u00020\u00002\u0006\u0010\t\u001a\u00020\u0005H\u0000\u00a2\u0006\u0002\b\u0012J\u001d\u0010\u0011\u001a\u00020\u00002\u0006\u0010\n\u001a\u00020\u00052\u0006\u0010\u000b\u001a\u00020\u0005H\u0000\u00a2\u0006\u0002\b\u0012J\u0016\u0010\u0013\u001a\u00020\u00002\u0006\u0010\n\u001a\u00020\u00052\u0006\u0010\u000b\u001a\u00020\u0005J\u0006\u0010\u0014\u001a\u00020\u0010J\u0013\u0010\u0015\u001a\u0004\u0018\u00010\u00052\u0006\u0010\n\u001a\u00020\u0005H\u0086\u0002J\u000e\u0010\u0016\u001a\u00020\u00002\u0006\u0010\n\u001a\u00020\u0005J\u0019\u0010\u0017\u001a\u00020\u00002\u0006\u0010\n\u001a\u00020\u00052\u0006\u0010\u000b\u001a\u00020\fH\u0087\u0002J\u0019\u0010\u0017\u001a\u00020\u00002\u0006\u0010\n\u001a\u00020\u00052\u0006\u0010\u000b\u001a\u00020\rH\u0086\u0002J\u0019\u0010\u0017\u001a\u00020\u00002\u0006\u0010\n\u001a\u00020\u00052\u0006\u0010\u000b\u001a\u00020\u0005H\u0086\u0002R\u001a\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004X\u0080\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007\u00a8\u0006\u0018"}, d2={"Ldev/serverpanel/lib/okhttp3/Headers$Builder;", "", "()V", "namesAndValues", "", "", "getNamesAndValues$okhttp", "()Ljava/util/List;", "add", "line", "name", "value", "Ljava/time/Instant;", "Ljava/util/Date;", "addAll", "headers", "Ldev/serverpanel/lib/okhttp3/Headers;", "addLenient", "addLenient$okhttp", "addUnsafeNonAscii", "build", "get", "removeAll", "set", "okhttp"})
    @SourceDebugExtension(value={"SMAP\nHeaders.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Headers.kt\nokhttp3/Headers$Builder\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 ArraysJVM.kt\nkotlin/collections/ArraysKt__ArraysJVMKt\n*L\n1#1,458:1\n1#2:459\n37#3,2:460\n*S KotlinDebug\n*F\n+ 1 Headers.kt\nokhttp3/Headers$Builder\n*L\n359#1:460,2\n*E\n"})
    public static final class Builder {
        @NotNull
        private final List<String> namesAndValues = new ArrayList(20);

        @NotNull
        public final List<String> getNamesAndValues$okhttp() {
            return this.namesAndValues;
        }

        @NotNull
        public final Builder addLenient$okhttp(@NotNull String line) {
            Builder builder;
            Intrinsics.checkNotNullParameter(line, "line");
            Builder $this$addLenient_u24lambda_u240 = builder = this;
            boolean bl = false;
            int index = StringsKt.indexOf$default((CharSequence)line, ':', 1, false, 4, null);
            if (index != -1) {
                String string = line.substring(0, index);
                Intrinsics.checkNotNullExpressionValue(string, "this as java.lang.String\u2026ing(startIndex, endIndex)");
                String string2 = line.substring(index + 1);
                Intrinsics.checkNotNullExpressionValue(string2, "this as java.lang.String).substring(startIndex)");
                $this$addLenient_u24lambda_u240.addLenient$okhttp(string, string2);
            } else if (line.charAt(0) == ':') {
                String string = line.substring(1);
                Intrinsics.checkNotNullExpressionValue(string, "this as java.lang.String).substring(startIndex)");
                $this$addLenient_u24lambda_u240.addLenient$okhttp("", string);
            } else {
                $this$addLenient_u24lambda_u240.addLenient$okhttp("", line);
            }
            return builder;
        }

        @NotNull
        public final Builder add(@NotNull String line) {
            Builder builder;
            Intrinsics.checkNotNullParameter(line, "line");
            Builder $this$add_u24lambda_u242 = builder = this;
            boolean bl = false;
            int index = StringsKt.indexOf$default((CharSequence)line, ':', 0, false, 6, null);
            if (!(index != -1)) {
                boolean bl2 = false;
                String string = "Unexpected header: " + line;
                throw new IllegalArgumentException(string.toString());
            }
            String string = line.substring(0, index);
            Intrinsics.checkNotNullExpressionValue(string, "this as java.lang.String\u2026ing(startIndex, endIndex)");
            String string2 = ((Object)StringsKt.trim((CharSequence)string)).toString();
            String string3 = line.substring(index + 1);
            Intrinsics.checkNotNullExpressionValue(string3, "this as java.lang.String).substring(startIndex)");
            $this$add_u24lambda_u242.add(string2, string3);
            return builder;
        }

        @NotNull
        public final Builder add(@NotNull String name, @NotNull String value) {
            Builder builder;
            Intrinsics.checkNotNullParameter(name, "name");
            Intrinsics.checkNotNullParameter(value, "value");
            Builder $this$add_u24lambda_u243 = builder = this;
            boolean bl = false;
            Companion.checkName(name);
            Companion.checkValue(value, name);
            $this$add_u24lambda_u243.addLenient$okhttp(name, value);
            return builder;
        }

        @NotNull
        public final Builder addUnsafeNonAscii(@NotNull String name, @NotNull String value) {
            Builder builder;
            Intrinsics.checkNotNullParameter(name, "name");
            Intrinsics.checkNotNullParameter(value, "value");
            Builder $this$addUnsafeNonAscii_u24lambda_u244 = builder = this;
            boolean bl = false;
            Companion.checkName(name);
            $this$addUnsafeNonAscii_u24lambda_u244.addLenient$okhttp(name, value);
            return builder;
        }

        @NotNull
        public final Builder addAll(@NotNull Headers headers) {
            Builder builder;
            Intrinsics.checkNotNullParameter(headers, "headers");
            Builder $this$addAll_u24lambda_u245 = builder = this;
            boolean bl = false;
            int n = headers.size();
            for (int i = 0; i < n; ++i) {
                $this$addAll_u24lambda_u245.addLenient$okhttp(headers.name(i), headers.value(i));
            }
            return builder;
        }

        @NotNull
        public final Builder add(@NotNull String name, @NotNull Date value) {
            Builder builder;
            Intrinsics.checkNotNullParameter(name, "name");
            Intrinsics.checkNotNullParameter(value, "value");
            Builder $this$add_u24lambda_u246 = builder = this;
            boolean bl = false;
            $this$add_u24lambda_u246.add(name, DatesKt.toHttpDateString(value));
            return builder;
        }

        @IgnoreJRERequirement
        @NotNull
        public final Builder add(@NotNull String name, @NotNull Instant value) {
            Builder builder;
            Intrinsics.checkNotNullParameter(name, "name");
            Intrinsics.checkNotNullParameter(value, "value");
            Builder $this$add_u24lambda_u247 = builder = this;
            boolean bl = false;
            $this$add_u24lambda_u247.add(name, new Date(value.toEpochMilli()));
            return builder;
        }

        @NotNull
        public final Builder set(@NotNull String name, @NotNull Date value) {
            Builder builder;
            Intrinsics.checkNotNullParameter(name, "name");
            Intrinsics.checkNotNullParameter(value, "value");
            Builder $this$set_u24lambda_u248 = builder = this;
            boolean bl = false;
            $this$set_u24lambda_u248.set(name, DatesKt.toHttpDateString(value));
            return builder;
        }

        @IgnoreJRERequirement
        @NotNull
        public final Builder set(@NotNull String name, @NotNull Instant value) {
            Intrinsics.checkNotNullParameter(name, "name");
            Intrinsics.checkNotNullParameter(value, "value");
            Builder $this$set_u24lambda_u249 = this;
            boolean bl = false;
            return $this$set_u24lambda_u249.set(name, new Date(value.toEpochMilli()));
        }

        @NotNull
        public final Builder addLenient$okhttp(@NotNull String name, @NotNull String value) {
            Builder builder;
            Intrinsics.checkNotNullParameter(name, "name");
            Intrinsics.checkNotNullParameter(value, "value");
            Builder $this$addLenient_u24lambda_u2410 = builder = this;
            boolean bl = false;
            $this$addLenient_u24lambda_u2410.namesAndValues.add(name);
            $this$addLenient_u24lambda_u2410.namesAndValues.add(((Object)StringsKt.trim((CharSequence)value)).toString());
            return builder;
        }

        @NotNull
        public final Builder removeAll(@NotNull String name) {
            Builder builder;
            Intrinsics.checkNotNullParameter(name, "name");
            Builder $this$removeAll_u24lambda_u2411 = builder = this;
            boolean bl = false;
            for (int i = 0; i < $this$removeAll_u24lambda_u2411.namesAndValues.size(); i += 2) {
                if (!StringsKt.equals(name, $this$removeAll_u24lambda_u2411.namesAndValues.get(i), true)) continue;
                $this$removeAll_u24lambda_u2411.namesAndValues.remove(i);
                $this$removeAll_u24lambda_u2411.namesAndValues.remove(i);
                i -= 2;
            }
            return builder;
        }

        @NotNull
        public final Builder set(@NotNull String name, @NotNull String value) {
            Builder builder;
            Intrinsics.checkNotNullParameter(name, "name");
            Intrinsics.checkNotNullParameter(value, "value");
            Builder $this$set_u24lambda_u2412 = builder = this;
            boolean bl = false;
            Companion.checkName(name);
            Companion.checkValue(value, name);
            $this$set_u24lambda_u2412.removeAll(name);
            $this$set_u24lambda_u2412.addLenient$okhttp(name, value);
            return builder;
        }

        @Nullable
        public final String get(@NotNull String name) {
            int n;
            Intrinsics.checkNotNullParameter(name, "name");
            int i = n = this.namesAndValues.size() - 2;
            int n2 = ProgressionUtilKt.getProgressionLastElement(n, 0, -2);
            if (n2 <= i) {
                while (true) {
                    if (StringsKt.equals(name, this.namesAndValues.get(i), true)) {
                        return this.namesAndValues.get(i + 1);
                    }
                    if (i == n2) break;
                    i -= 2;
                }
            }
            return null;
        }

        @NotNull
        public final Headers build() {
            Collection $this$toTypedArray$iv = this.namesAndValues;
            boolean $i$f$toTypedArray = false;
            Collection thisCollection$iv = $this$toTypedArray$iv;
            return new Headers(thisCollection$iv.toArray(new String[0]), null);
        }
    }

    /*
     * Illegal identifiers - consider using --renameillegalidents true
     */
    @Metadata(mv={1, 8, 0}, k=1, xi=48, d1={"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\u0011\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010$\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0002J\u0018\u0010\u0007\u001a\u00020\u00042\u0006\u0010\b\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u0006H\u0002J%\u0010\t\u001a\u0004\u0018\u00010\u00062\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00060\u000b2\u0006\u0010\u0005\u001a\u00020\u0006H\u0002\u00a2\u0006\u0002\u0010\fJ#\u0010\r\u001a\u00020\u000e2\u0012\u0010\n\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00060\u000b\"\u00020\u0006H\u0007\u00a2\u0006\u0004\b\u000f\u0010\u0010J#\u0010\u000f\u001a\u00020\u000e2\u0012\u0010\n\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00060\u000b\"\u00020\u0006H\u0007\u00a2\u0006\u0004\b\u0011\u0010\u0010J!\u0010\u000f\u001a\u00020\u000e2\u0012\u0010\u0012\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00060\u0013H\u0007\u00a2\u0006\u0002\b\u0011J\u001d\u0010\u0014\u001a\u00020\u000e*\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00060\u0013H\u0007\u00a2\u0006\u0002\b\u000f\u00a8\u0006\u0015"}, d2={"Ldev/serverpanel/lib/okhttp3/Headers$Companion;", "", "()V", "checkName", "", "name", "", "checkValue", "value", "get", "namesAndValues", "", "([Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", "headersOf", "Ldev/serverpanel/lib/okhttp3/Headers;", "of", "([Ljava/lang/String;)Lokhttp3/Headers;", "-deprecated_of", "headers", "", "toHeaders", "okhttp"})
    @SourceDebugExtension(value={"SMAP\nHeaders.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Headers.kt\nokhttp3/Headers$Companion\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,458:1\n1#2:459\n*E\n"})
    public static final class Companion {
        private Companion() {
        }

        private final String get(String[] namesAndValues, String name) {
            int n;
            int i = n = namesAndValues.length - 2;
            int n2 = ProgressionUtilKt.getProgressionLastElement(n, 0, -2);
            if (n2 <= i) {
                while (true) {
                    if (StringsKt.equals(name, namesAndValues[i], true)) {
                        return namesAndValues[i + 1];
                    }
                    if (i == n2) break;
                    i -= 2;
                }
            }
            return null;
        }

        @JvmStatic
        @JvmName(name="of")
        @NotNull
        public final Headers of(String ... namesAndValues) {
            Intrinsics.checkNotNullParameter(namesAndValues, "namesAndValues");
            if (!(namesAndValues.length % 2 == 0)) {
                boolean $i$a$-require-Headers$Companion$headersOf$32 = false;
                String $i$a$-require-Headers$Companion$headersOf$32 = "Expected alternating header names and values";
                throw new IllegalArgumentException($i$a$-require-Headers$Companion$headersOf$32.toString());
            }
            String[] namesAndValues2 = (String[])namesAndValues.clone();
            int n = namesAndValues2.length;
            for (int i = 0; i < n; ++i) {
                if (!(namesAndValues2[i] != null)) {
                    boolean $i$a$-require-Headers$Companion$headersOf$42 = false;
                    String $i$a$-require-Headers$Companion$headersOf$42 = "Headers cannot be null";
                    throw new IllegalArgumentException($i$a$-require-Headers$Companion$headersOf$42.toString());
                }
                namesAndValues2[i] = ((Object)StringsKt.trim((CharSequence)namesAndValues2[i])).toString();
            }
            int i = 0;
            int n2 = namesAndValues2.length + -1;
            int n3 = ProgressionUtilKt.getProgressionLastElement(0, n2, 2);
            if (i <= n3) {
                while (true) {
                    String name = namesAndValues2[i];
                    String value = namesAndValues2[i + 1];
                    this.checkName(name);
                    this.checkValue(value, name);
                    if (i == n3) break;
                    i += 2;
                }
            }
            return new Headers(namesAndValues2, null);
        }

        @Deprecated(message="function name changed", replaceWith=@ReplaceWith(expression="headersOf(*namesAndValues)", imports={}), level=DeprecationLevel.ERROR)
        @JvmName(name="-deprecated_of")
        @NotNull
        public final Headers -deprecated_of(String ... namesAndValues) {
            Intrinsics.checkNotNullParameter(namesAndValues, "namesAndValues");
            return this.of(Arrays.copyOf(namesAndValues, namesAndValues.length));
        }

        @JvmStatic
        @JvmName(name="of")
        @NotNull
        public final Headers of(@NotNull Map<String, String> $this$toHeaders) {
            Intrinsics.checkNotNullParameter($this$toHeaders, "<this>");
            String[] namesAndValues = new String[$this$toHeaders.size() * 2];
            int i = 0;
            for (Map.Entry<String, String> entry : $this$toHeaders.entrySet()) {
                String k = entry.getKey();
                String v = entry.getValue();
                String name = ((Object)StringsKt.trim((CharSequence)k)).toString();
                String value = ((Object)StringsKt.trim((CharSequence)v)).toString();
                this.checkName(name);
                this.checkValue(value, name);
                namesAndValues[i] = name;
                namesAndValues[i + 1] = value;
                i += 2;
            }
            return new Headers(namesAndValues, null);
        }

        @Deprecated(message="function moved to extension", replaceWith=@ReplaceWith(expression="headers.toHeaders()", imports={}), level=DeprecationLevel.ERROR)
        @JvmName(name="-deprecated_of")
        @NotNull
        public final Headers -deprecated_of(@NotNull Map<String, String> headers) {
            Intrinsics.checkNotNullParameter(headers, "headers");
            return this.of(headers);
        }

        private final void checkName(String name) {
            if (!(((CharSequence)name).length() > 0)) {
                boolean bl = false;
                String string = "name is empty";
                throw new IllegalArgumentException(string.toString());
            }
            int n = name.length();
            for (int i = 0; i < n; ++i) {
                char c = name.charAt(i);
                if ('!' <= c ? c < '\u007f' : false) continue;
                boolean bl = false;
                Object[] objectArray = new Object[]{(int)c, i, name};
                String string = Util.format("Unexpected char %#04x at %d in header name: %s", objectArray);
                throw new IllegalArgumentException(string.toString());
            }
        }

        /*
         * Unable to fully structure code
         */
        private final void checkValue(String value, String name) {
            var4_4 = value.length();
            for (i = 0; i < var4_4; ++i) {
                c = value.charAt(i);
                if (c == '\t') ** GOTO lbl-1000
                v0 = ' ' <= c ? c < '\u007f' : false;
                if (v0) lbl-1000:
                // 2 sources

                {
                    v1 = true;
                } else {
                    v1 = false;
                }
                if (v1) continue;
                $i$a$-require-Headers$Companion$checkValue$1 = false;
                var7_8 = new Object[]{(int)c, i, name};
                var6_7 = Util.format("Unexpected char %#04x at %d in %s value", var7_8) + (Util.isSensitiveHeader(name) != false ? "" : ": " + value);
                throw new IllegalArgumentException(var6_7.toString());
            }
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }
}

