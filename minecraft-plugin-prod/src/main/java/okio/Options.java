/*
 * Decompiled with CFR 0.152.
 */
package okio;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.RandomAccess;
import kotlin.Metadata;
import kotlin.collections.AbstractList;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import okio.Buffer;
import okio.ByteString;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0011\n\u0000\n\u0002\u0010\u0015\n\u0002\b\u0005\n\u0002\u0010\b\n\u0002\b\b\u0018\u0000 \u00152\b\u0012\u0004\u0012\u00020\u00020\u00012\u00060\u0003j\u0002`\u0004:\u0001\u0015B\u001f\b\u0002\u0012\u000e\u0010\u0005\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\b\u00a2\u0006\u0002\u0010\tJ\u0011\u0010\u0013\u001a\u00020\u00022\u0006\u0010\u0014\u001a\u00020\u000eH\u0096\u0002R\u001e\u0010\u0005\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00020\u0006X\u0080\u0004\u00a2\u0006\n\n\u0002\u0010\f\u001a\u0004\b\n\u0010\u000bR\u0014\u0010\r\u001a\u00020\u000e8VX\u0096\u0004\u00a2\u0006\u0006\u001a\u0004\b\u000f\u0010\u0010R\u0014\u0010\u0007\u001a\u00020\bX\u0080\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012\u00a8\u0006\u0016"}, d2={"Lokio/Options;", "Lkotlin/collections/AbstractList;", "Lokio/ByteString;", "Ljava/util/RandomAccess;", "Lkotlin/collections/RandomAccess;", "byteStrings", "", "trie", "", "([Lokio/ByteString;[I)V", "getByteStrings$okio", "()[Lokio/ByteString;", "[Lokio/ByteString;", "size", "", "getSize", "()I", "getTrie$okio", "()[I", "get", "index", "Companion", "okio"})
public final class Options
extends AbstractList<ByteString>
implements RandomAccess {
    @NotNull
    public static final Companion Companion = new Companion(null);
    @NotNull
    private final ByteString[] byteStrings;
    @NotNull
    private final int[] trie;

    private Options(ByteString[] byteStrings, int[] trie) {
        this.byteStrings = byteStrings;
        this.trie = trie;
    }

    @NotNull
    public final ByteString[] getByteStrings$okio() {
        return this.byteStrings;
    }

    @NotNull
    public final int[] getTrie$okio() {
        return this.trie;
    }

    @Override
    public int getSize() {
        return this.byteStrings.length;
    }

    @Override
    @NotNull
    public ByteString get(int index) {
        return this.byteStrings[index];
    }

    @JvmStatic
    @NotNull
    public static final Options of(ByteString ... byteStrings) {
        return Companion.of(byteStrings);
    }

    public /* synthetic */ Options(ByteString[] byteStrings, int[] trie, DefaultConstructorMarker $constructor_marker) {
        this(byteStrings, trie);
    }

    @Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000>\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0010\u0011\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002JT\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\u00042\u0006\u0010\u000b\u001a\u00020\u00052\b\b\u0002\u0010\f\u001a\u00020\r2\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00100\u000f2\b\b\u0002\u0010\u0011\u001a\u00020\r2\b\b\u0002\u0010\u0012\u001a\u00020\r2\f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\r0\u000fH\u0002J!\u0010\u0014\u001a\u00020\u00152\u0012\u0010\u000e\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00100\u0016\"\u00020\u0010H\u0007\u00a2\u0006\u0002\u0010\u0017R\u0018\u0010\u0003\u001a\u00020\u0004*\u00020\u00058BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b\u0006\u0010\u0007\u00a8\u0006\u0018"}, d2={"Lokio/Options$Companion;", "", "()V", "intCount", "", "Lokio/Buffer;", "getIntCount", "(Lokio/Buffer;)J", "buildTrieRecursive", "", "nodeOffset", "node", "byteStringOffset", "", "byteStrings", "", "Lokio/ByteString;", "fromIndex", "toIndex", "indexes", "of", "Lokio/Options;", "", "([Lokio/ByteString;)Lokio/Options;", "okio"})
    @SourceDebugExtension(value={"SMAP\nOptions.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Options.kt\nokio/Options$Companion\n+ 2 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n+ 3 ArraysJVM.kt\nkotlin/collections/ArraysKt__ArraysJVMKt\n+ 4 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 5 Util.kt\nokio/-SegmentedByteString\n*L\n1#1,236:1\n11065#2:237\n11400#2,3:238\n13374#2,3:243\n37#3,2:241\n1#4:246\n74#5:247\n74#5:248\n*S KotlinDebug\n*F\n+ 1 Options.kt\nokio/Options$Companion\n*L\n43#1:237\n43#1:238,3\n44#1:243,3\n43#1:241,2\n151#1:247\n208#1:248\n*E\n"})
    public static final class Companion {
        private Companion() {
        }

        /*
         * WARNING - void declaration
         */
        @JvmStatic
        @NotNull
        public final Options of(ByteString ... byteStrings) {
            void $this$toTypedArray$iv;
            void $this$mapTo$iv$iv;
            Intrinsics.checkNotNullParameter(byteStrings, "byteStrings");
            if (byteStrings.length == 0) {
                int[] nArray = new int[]{0, -1};
                return new Options(new ByteString[0], nArray, null);
            }
            List<ByteString> list = ArraysKt.toMutableList(byteStrings);
            CollectionsKt.sort(list);
            Object $this$map$iv = byteStrings;
            boolean $i$f$map = false;
            ByteString[] byteStringArray = $this$map$iv;
            Collection destination$iv$iv = new ArrayList(((ByteString[])$this$map$iv).length);
            boolean $i$f$mapTo = false;
            int n = ((void)$this$mapTo$iv$iv).length;
            for (int i = 0; i < n; ++i) {
                void item$iv$iv;
                void var13_25 = item$iv$iv = $this$mapTo$iv$iv[i];
                Collection collection = destination$iv$iv;
                boolean bl = false;
                collection.add(-1);
            }
            $this$map$iv = (List)destination$iv$iv;
            boolean $i$f$toTypedArray = false;
            void thisCollection$iv = $this$toTypedArray$iv;
            Integer[] integerArray = thisCollection$iv.toArray(new Integer[0]);
            List<Integer> indexes = CollectionsKt.mutableListOf(Arrays.copyOf(integerArray, integerArray.length));
            ByteString[] $this$forEachIndexed$iv = byteStrings;
            boolean $i$f$forEachIndexed = false;
            int index$iv = 0;
            for (ByteString item$iv : $this$forEachIndexed$iv) {
                void byteString;
                int n2 = index$iv++;
                ByteString byteString2 = item$iv;
                int callerIndex = n2;
                boolean bl = false;
                int sortedIndex = CollectionsKt.binarySearch$default(list, (Comparable)byteString, 0, 0, 6, null);
                indexes.set(sortedIndex, callerIndex);
            }
            if (!(list.get(0).size() > 0)) {
                boolean $i$a$-require-Options$Companion$of$42 = false;
                String $i$a$-require-Options$Companion$of$42 = "the empty byte string is not a supported option";
                throw new IllegalArgumentException($i$a$-require-Options$Companion$of$42.toString());
            }
            for (int a = 0; a < list.size(); ++a) {
                ByteString byteString;
                ByteString prefix = list.get(a);
                int b = a + 1;
                while (b < list.size() && (byteString = list.get(b)).startsWith(prefix)) {
                    if (!(byteString.size() != prefix.size())) {
                        boolean bl = false;
                        String string = "duplicate option: " + byteString;
                        throw new IllegalArgumentException(string.toString());
                    }
                    if (((Number)indexes.get(b)).intValue() > ((Number)indexes.get(a)).intValue()) {
                        list.remove(b);
                        indexes.remove(b);
                        continue;
                    }
                    ++b;
                }
            }
            Buffer trieBytes = new Buffer();
            okio.Options$Companion.buildTrieRecursive$default(this, 0L, trieBytes, 0, list, 0, 0, indexes, 53, null);
            int[] trie = new int[(int)this.getIntCount(trieBytes)];
            int i = 0;
            while (!trieBytes.exhausted()) {
                trie[i++] = trieBytes.readInt();
            }
            ByteString[] byteStringArray2 = Arrays.copyOf(byteStrings, byteStrings.length);
            Intrinsics.checkNotNullExpressionValue(byteStringArray2, "copyOf(this, size)");
            return new Options(byteStringArray2, trie, null);
        }

        /*
         * WARNING - void declaration
         */
        private final void buildTrieRecursive(long nodeOffset, Buffer node, int byteStringOffset, List<? extends ByteString> byteStrings, int fromIndex, int toIndex, List<Integer> indexes) {
            if (!(fromIndex < toIndex)) {
                String string = "Failed requirement.";
                throw new IllegalArgumentException(string.toString());
            }
            for (int i = fromIndex; i < toIndex; ++i) {
                if (byteStrings.get(i).size() >= byteStringOffset) continue;
                String string = "Failed requirement.";
                throw new IllegalArgumentException(string.toString());
            }
            int fromIndex2 = fromIndex;
            ByteString from = byteStrings.get(fromIndex2);
            ByteString to = byteStrings.get(toIndex - 1);
            int prefixIndex = -1;
            if (byteStringOffset == from.size()) {
                prefixIndex = ((Number)indexes.get(fromIndex2)).intValue();
                from = byteStrings.get(++fromIndex2);
            }
            if (from.getByte(byteStringOffset) != to.getByte(byteStringOffset)) {
                int selectChoiceCount = 1;
                for (int i = fromIndex2 + 1; i < toIndex; ++i) {
                    if (byteStrings.get(i - 1).getByte(byteStringOffset) == byteStrings.get(i).getByte(byteStringOffset)) continue;
                    ++selectChoiceCount;
                }
                long childNodesOffset = nodeOffset + this.getIntCount(node) + (long)2 + (long)(selectChoiceCount * 2);
                node.writeInt(selectChoiceCount);
                node.writeInt(prefixIndex);
                for (int i = fromIndex2; i < toIndex; ++i) {
                    void $this$and$iv;
                    byte rangeByte = byteStrings.get(i).getByte(byteStringOffset);
                    if (i != fromIndex2 && rangeByte == byteStrings.get(i - 1).getByte(byteStringOffset)) continue;
                    byte by = rangeByte;
                    int other$iv = 255;
                    boolean $i$f$and = false;
                    node.writeInt($this$and$iv & other$iv);
                }
                Buffer childNodes = new Buffer();
                int rangeStart = fromIndex2;
                while (rangeStart < toIndex) {
                    byte rangeByte = byteStrings.get(rangeStart).getByte(byteStringOffset);
                    int rangeEnd = toIndex;
                    for (int i = rangeStart + 1; i < toIndex; ++i) {
                        if (rangeByte == byteStrings.get(i).getByte(byteStringOffset)) continue;
                        rangeEnd = i;
                        break;
                    }
                    if (rangeStart + 1 == rangeEnd && byteStringOffset + 1 == byteStrings.get(rangeStart).size()) {
                        node.writeInt(((Number)indexes.get(rangeStart)).intValue());
                    } else {
                        node.writeInt(-1 * (int)(childNodesOffset + this.getIntCount(childNodes)));
                        this.buildTrieRecursive(childNodesOffset, childNodes, byteStringOffset + 1, byteStrings, rangeStart, rangeEnd, indexes);
                    }
                    rangeStart = rangeEnd;
                }
                node.writeAll(childNodes);
            } else {
                int scanByteCount = 0;
                int n = Math.min(from.size(), to.size());
                for (int i = byteStringOffset; i < n && from.getByte(i) == to.getByte(i); ++i) {
                    ++scanByteCount;
                }
                long childNodesOffset = nodeOffset + this.getIntCount(node) + (long)2 + (long)scanByteCount + 1L;
                node.writeInt(-scanByteCount);
                node.writeInt(prefixIndex);
                int n2 = byteStringOffset + scanByteCount;
                for (int i = byteStringOffset; i < n2; ++i) {
                    void $this$and$iv;
                    byte rangeByte = from.getByte(i);
                    int other$iv = 255;
                    boolean $i$f$and = false;
                    node.writeInt($this$and$iv & other$iv);
                }
                if (fromIndex2 + 1 == toIndex) {
                    if (!(byteStringOffset + scanByteCount == byteStrings.get(fromIndex2).size())) {
                        String string = "Check failed.";
                        throw new IllegalStateException(string.toString());
                    }
                    node.writeInt(((Number)indexes.get(fromIndex2)).intValue());
                } else {
                    Buffer childNodes = new Buffer();
                    node.writeInt(-1 * (int)(childNodesOffset + this.getIntCount(childNodes)));
                    this.buildTrieRecursive(childNodesOffset, childNodes, byteStringOffset + scanByteCount, byteStrings, fromIndex2, toIndex, indexes);
                    node.writeAll(childNodes);
                }
            }
        }

        static /* synthetic */ void buildTrieRecursive$default(Companion companion, long l, Buffer buffer, int n, List list, int n2, int n3, List list2, int n4, Object object) {
            if ((n4 & 1) != 0) {
                l = 0L;
            }
            if ((n4 & 4) != 0) {
                n = 0;
            }
            if ((n4 & 0x10) != 0) {
                n2 = 0;
            }
            if ((n4 & 0x20) != 0) {
                n3 = list.size();
            }
            companion.buildTrieRecursive(l, buffer, n, list, n2, n3, list2);
        }

        private final long getIntCount(Buffer $this$intCount) {
            return $this$intCount.size() / (long)4;
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }
}

