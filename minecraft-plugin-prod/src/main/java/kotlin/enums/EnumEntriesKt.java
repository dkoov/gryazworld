/*
 * Decompiled with CFR 0.152.
 */
package kotlin.enums;

import kotlin.ExperimentalStdlibApi;
import kotlin.Metadata;
import kotlin.PublishedApi;
import kotlin.SinceKotlin;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesList;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={1, 8, 0}, k=2, xi=48, d1={"\u0000\u001a\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0010\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0011\n\u0002\b\u0003\u001a2\u0010\u0000\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0001\"\u000e\b\u0000\u0010\u0002*\b\u0012\u0004\u0012\u0002H\u00020\u00032\u0012\u0010\u0004\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u0002H\u00020\u00060\u0005H\u0001\u001a1\u0010\u0000\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0001\"\u000e\b\u0000\u0010\u0002*\b\u0012\u0004\u0012\u0002H\u00020\u00032\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0006H\u0001\u00a2\u0006\u0002\u0010\b\u00a8\u0006\t"}, d2={"enumEntries", "Lkotlin/enums/EnumEntries;", "E", "", "entriesProvider", "Lkotlin/Function0;", "", "entries", "([Ljava/lang/Enum;)Lkotlin/enums/EnumEntries;", "kotlin-stdlib"})
public final class EnumEntriesKt {
    @PublishedApi
    @ExperimentalStdlibApi
    @SinceKotlin(version="1.8")
    @NotNull
    public static final <E extends Enum<E>> EnumEntries<E> enumEntries(@NotNull Function0<E[]> entriesProvider) {
        Intrinsics.checkNotNullParameter(entriesProvider, "entriesProvider");
        return new EnumEntriesList(entriesProvider);
    }

    @PublishedApi
    @ExperimentalStdlibApi
    @SinceKotlin(version="1.8")
    @NotNull
    public static final <E extends Enum<E>> EnumEntries<E> enumEntries(@NotNull E[] entries) {
        EnumEntriesList enumEntriesList;
        Intrinsics.checkNotNullParameter(entries, "entries");
        EnumEntriesList it = enumEntriesList = new EnumEntriesList((Function0<T[]>)new Function0<E[]>(entries){
            final /* synthetic */ E[] $entries;
            {
                this.$entries = $entries;
                super(0);
            }

            @NotNull
            public final E[] invoke() {
                return this.$entries;
            }
        });
        boolean bl = false;
        it.size();
        return enumEntriesList;
    }
}

