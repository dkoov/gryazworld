/*
 * Decompiled with CFR 0.152.
 */
package kotlin.enums;

import java.io.Serializable;
import kotlin.ExperimentalStdlibApi;
import kotlin.Metadata;
import kotlin.SinceKotlin;
import kotlin.collections.AbstractList;
import kotlin.collections.ArraysKt;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesSerializationProxy;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 8, 0}, k=1, xi=48, d1={"\u0000>\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0010\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0011\n\u0002\b\u0007\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\t\n\u0002\u0010\u0000\n\u0000\b\u0003\u0018\u0000*\u000e\b\u0000\u0010\u0001*\b\u0012\u0004\u0012\u0002H\u00010\u00022\b\u0012\u0004\u0012\u0002H\u00010\u00032\b\u0012\u0004\u0012\u0002H\u00010\u00042\u00060\u0005j\u0002`\u0006B\u0019\u0012\u0012\u0010\u0007\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00028\u00000\t0\b\u00a2\u0006\u0002\u0010\nJ\u0016\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00028\u0000H\u0096\u0002\u00a2\u0006\u0002\u0010\u0017J\u0016\u0010\u0018\u001a\u00028\u00002\u0006\u0010\u0019\u001a\u00020\u0011H\u0096\u0002\u00a2\u0006\u0002\u0010\u001aJ\u0015\u0010\u001b\u001a\u00020\u00112\u0006\u0010\u0016\u001a\u00028\u0000H\u0016\u00a2\u0006\u0002\u0010\u001cJ\u0015\u0010\u001d\u001a\u00020\u00112\u0006\u0010\u0016\u001a\u00028\u0000H\u0016\u00a2\u0006\u0002\u0010\u001cJ\b\u0010\u001e\u001a\u00020\u001fH\u0002R\u0018\u0010\u000b\u001a\n\u0012\u0004\u0012\u00028\u0000\u0018\u00010\tX\u0082\u000e\u00a2\u0006\u0004\n\u0002\u0010\fR\u001a\u0010\r\u001a\b\u0012\u0004\u0012\u00028\u00000\t8BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b\u000e\u0010\u000fR\u001a\u0010\u0007\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00028\u00000\t0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0010\u001a\u00020\u00118VX\u0096\u0004\u00a2\u0006\u0006\u001a\u0004\b\u0012\u0010\u0013\u00a8\u0006 "}, d2={"Lkotlin/enums/EnumEntriesList;", "T", "", "Lkotlin/enums/EnumEntries;", "Lkotlin/collections/AbstractList;", "Ljava/io/Serializable;", "Lkotlin/io/Serializable;", "entriesProvider", "Lkotlin/Function0;", "", "(Lkotlin/jvm/functions/Function0;)V", "_entries", "[Ljava/lang/Enum;", "entries", "getEntries", "()[Ljava/lang/Enum;", "size", "", "getSize", "()I", "contains", "", "element", "(Ljava/lang/Enum;)Z", "get", "index", "(I)Ljava/lang/Enum;", "indexOf", "(Ljava/lang/Enum;)I", "lastIndexOf", "writeReplace", "", "kotlin-stdlib"})
@SinceKotlin(version="1.8")
@ExperimentalStdlibApi
final class EnumEntriesList<T extends Enum<T>>
extends AbstractList<T>
implements EnumEntries<T>,
Serializable {
    @NotNull
    private final Function0<T[]> entriesProvider;
    @Nullable
    private volatile T[] _entries;

    public EnumEntriesList(@NotNull Function0<T[]> entriesProvider) {
        Intrinsics.checkNotNullParameter(entriesProvider, "entriesProvider");
        this.entriesProvider = entriesProvider;
    }

    private final T[] getEntries() {
        Object[] e = this._entries;
        if (e != null) {
            return e;
        }
        this._entries = e = (Enum[])this.entriesProvider.invoke();
        return e;
    }

    @Override
    public int getSize() {
        return this.getEntries().length;
    }

    @Override
    @NotNull
    public T get(int index) {
        Enum[] entries = this.getEntries();
        AbstractList.Companion.checkElementIndex$kotlin_stdlib(index, entries.length);
        return (T)entries[index];
    }

    @Override
    public boolean contains(@NotNull T element) {
        Intrinsics.checkNotNullParameter(element, "element");
        Enum target = ArraysKt.getOrNull(this.getEntries(), ((Enum)element).ordinal());
        return target == element;
    }

    @Override
    public int indexOf(@NotNull T element) {
        Intrinsics.checkNotNullParameter(element, "element");
        int ordinal = ((Enum)element).ordinal();
        Enum target = ArraysKt.getOrNull(this.getEntries(), ordinal);
        return target == element ? ordinal : -1;
    }

    @Override
    public int lastIndexOf(@NotNull T element) {
        Intrinsics.checkNotNullParameter(element, "element");
        return this.indexOf((Object)element);
    }

    private final Object writeReplace() {
        return new EnumEntriesSerializationProxy(this.getEntries());
    }
}

