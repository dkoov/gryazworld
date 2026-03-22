/*
 * Decompiled with CFR 0.152.
 */
package okio.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.JvmName;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import okio.Buffer;
import okio.ByteString;
import okio.Path;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 9, 0}, k=2, xi=48, d1={"\u0000H\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0010\b\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010\u000b\n\u0002\u0010\u0000\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0004\n\u0002\u0010\f\n\u0002\b\u0006\n\u0002\u0010\u0005\n\u0000\u001a\u0015\u0010\u0014\u001a\u00020\r*\u00020\u000e2\u0006\u0010\u0015\u001a\u00020\u000eH\u0080\b\u001a\u0017\u0010\u0016\u001a\u00020\u0017*\u00020\u000e2\b\u0010\u0015\u001a\u0004\u0018\u00010\u0018H\u0080\b\u001a\r\u0010\u0019\u001a\u00020\r*\u00020\u000eH\u0080\b\u001a\r\u0010\u001a\u001a\u00020\u0017*\u00020\u000eH\u0080\b\u001a\r\u0010\u001b\u001a\u00020\u0017*\u00020\u000eH\u0080\b\u001a\r\u0010\u001c\u001a\u00020\u0017*\u00020\u000eH\u0080\b\u001a\r\u0010\u001d\u001a\u00020\u001e*\u00020\u000eH\u0080\b\u001a\r\u0010\u001f\u001a\u00020\u0001*\u00020\u000eH\u0080\b\u001a\r\u0010 \u001a\u00020\u000e*\u00020\u000eH\u0080\b\u001a\u000f\u0010!\u001a\u0004\u0018\u00010\u000e*\u00020\u000eH\u0080\b\u001a\u0015\u0010\"\u001a\u00020\u000e*\u00020\u000e2\u0006\u0010\u0015\u001a\u00020\u000eH\u0080\b\u001a\u001d\u0010#\u001a\u00020\u000e*\u00020\u000e2\u0006\u0010$\u001a\u00020\u001e2\u0006\u0010%\u001a\u00020\u0017H\u0080\b\u001a\u001d\u0010#\u001a\u00020\u000e*\u00020\u000e2\u0006\u0010$\u001a\u00020&2\u0006\u0010%\u001a\u00020\u0017H\u0080\b\u001a\u001d\u0010#\u001a\u00020\u000e*\u00020\u000e2\u0006\u0010$\u001a\u00020\u00012\u0006\u0010%\u001a\u00020\u0017H\u0080\b\u001a\u001c\u0010#\u001a\u00020\u000e*\u00020\u000e2\u0006\u0010$\u001a\u00020\u000e2\u0006\u0010%\u001a\u00020\u0017H\u0000\u001a\u000f\u0010'\u001a\u0004\u0018\u00010\u000e*\u00020\u000eH\u0080\b\u001a\u0013\u0010(\u001a\b\u0012\u0004\u0012\u00020\u001e0)*\u00020\u000eH\u0080\b\u001a\u0013\u0010*\u001a\b\u0012\u0004\u0012\u00020\u00010)*\u00020\u000eH\u0080\b\u001a\u0014\u0010+\u001a\u00020\u000e*\u00020\u001e2\u0006\u0010%\u001a\u00020\u0017H\u0000\u001a\r\u0010,\u001a\u00020\u001e*\u00020\u000eH\u0080\b\u001a\u0014\u0010-\u001a\u0004\u0018\u00010.*\u00020\u000eH\u0080\b\u00a2\u0006\u0002\u0010/\u001a\f\u00100\u001a\u00020\u0017*\u00020\u000eH\u0002\u001a\f\u00101\u001a\u00020\r*\u00020\u000eH\u0002\u001a\u0014\u00102\u001a\u00020\u0017*\u00020&2\u0006\u0010\u0011\u001a\u00020\u0001H\u0002\u001a\u0014\u00103\u001a\u00020\u000e*\u00020&2\u0006\u0010%\u001a\u00020\u0017H\u0000\u001a\f\u00104\u001a\u00020\u0001*\u000205H\u0002\u001a\f\u00104\u001a\u00020\u0001*\u00020\u001eH\u0002\"\u0016\u0010\u0000\u001a\u00020\u00018\u0002X\u0083\u0004\u00a2\u0006\b\n\u0000\u0012\u0004\b\u0002\u0010\u0003\"\u0016\u0010\u0004\u001a\u00020\u00018\u0002X\u0083\u0004\u00a2\u0006\b\n\u0000\u0012\u0004\b\u0005\u0010\u0003\"\u0016\u0010\u0006\u001a\u00020\u00018\u0002X\u0083\u0004\u00a2\u0006\b\n\u0000\u0012\u0004\b\u0007\u0010\u0003\"\u0016\u0010\b\u001a\u00020\u00018\u0002X\u0083\u0004\u00a2\u0006\b\n\u0000\u0012\u0004\b\t\u0010\u0003\"\u0016\u0010\n\u001a\u00020\u00018\u0002X\u0083\u0004\u00a2\u0006\b\n\u0000\u0012\u0004\b\u000b\u0010\u0003\"\u0018\u0010\f\u001a\u00020\r*\u00020\u000e8BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b\u000f\u0010\u0010\"\u001a\u0010\u0011\u001a\u0004\u0018\u00010\u0001*\u00020\u000e8BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b\u0012\u0010\u0013\u00a8\u00066"}, d2={"ANY_SLASH", "Lokio/ByteString;", "getANY_SLASH$annotations", "()V", "BACKSLASH", "getBACKSLASH$annotations", "DOT", "getDOT$annotations", "DOT_DOT", "getDOT_DOT$annotations", "SLASH", "getSLASH$annotations", "indexOfLastSlash", "", "Lokio/Path;", "getIndexOfLastSlash", "(Lokio/Path;)I", "slash", "getSlash", "(Lokio/Path;)Lokio/ByteString;", "commonCompareTo", "other", "commonEquals", "", "", "commonHashCode", "commonIsAbsolute", "commonIsRelative", "commonIsRoot", "commonName", "", "commonNameBytes", "commonNormalized", "commonParent", "commonRelativeTo", "commonResolve", "child", "normalize", "Lokio/Buffer;", "commonRoot", "commonSegments", "", "commonSegmentsBytes", "commonToPath", "commonToString", "commonVolumeLetter", "", "(Lokio/Path;)Ljava/lang/Character;", "lastSegmentIsDotDot", "rootLength", "startsWithVolumeLetterAndColon", "toPath", "toSlash", "", "okio"})
@JvmName(name="-Path")
@SourceDebugExtension(value={"SMAP\nPath.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Path.kt\nokio/internal/-Path\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,406:1\n59#1,22:407\n209#1:433\n209#1:434\n1549#2:429\n1620#2,3:430\n*S KotlinDebug\n*F\n+ 1 Path.kt\nokio/internal/-Path\n*L\n53#1:407,22\n199#1:433\n204#1:434\n53#1:429\n53#1:430,3\n*E\n"})
public final class -Path {
    @NotNull
    private static final ByteString SLASH = ByteString.Companion.encodeUtf8("/");
    @NotNull
    private static final ByteString BACKSLASH = ByteString.Companion.encodeUtf8("\\");
    @NotNull
    private static final ByteString ANY_SLASH = ByteString.Companion.encodeUtf8("/\\");
    @NotNull
    private static final ByteString DOT = ByteString.Companion.encodeUtf8(".");
    @NotNull
    private static final ByteString DOT_DOT = ByteString.Companion.encodeUtf8("..");

    private static /* synthetic */ void getSLASH$annotations() {
    }

    private static /* synthetic */ void getBACKSLASH$annotations() {
    }

    private static /* synthetic */ void getANY_SLASH$annotations() {
    }

    private static /* synthetic */ void getDOT$annotations() {
    }

    private static /* synthetic */ void getDOT_DOT$annotations() {
    }

    @Nullable
    public static final Path commonRoot(@NotNull Path $this$commonRoot) {
        Intrinsics.checkNotNullParameter($this$commonRoot, "<this>");
        boolean $i$f$commonRoot = false;
        int rootLength = -Path.rootLength($this$commonRoot);
        return rootLength == -1 ? null : new Path($this$commonRoot.getBytes$okio().substring(0, rootLength));
    }

    /*
     * WARNING - void declaration
     */
    @NotNull
    public static final List<String> commonSegments(@NotNull Path $this$commonSegments) {
        void $this$mapTo$iv$iv;
        Intrinsics.checkNotNullParameter($this$commonSegments, "<this>");
        boolean $i$f$commonSegments = false;
        Path $this$commonSegmentsBytes$iv = $this$commonSegments;
        boolean $i$f$commonSegmentsBytes = false;
        Iterable result$iv = new ArrayList();
        int segmentStart$iv = -Path.rootLength($this$commonSegmentsBytes$iv);
        if (segmentStart$iv == -1) {
            segmentStart$iv = 0;
        } else if (segmentStart$iv < $this$commonSegmentsBytes$iv.getBytes$okio().size() && $this$commonSegmentsBytes$iv.getBytes$okio().getByte(segmentStart$iv) == 92) {
            ++segmentStart$iv;
        }
        int n = $this$commonSegmentsBytes$iv.getBytes$okio().size();
        for (int i$iv = segmentStart$iv; i$iv < n; ++i$iv) {
            if ($this$commonSegmentsBytes$iv.getBytes$okio().getByte(i$iv) != 47 && $this$commonSegmentsBytes$iv.getBytes$okio().getByte(i$iv) != 92) continue;
            ((Collection)result$iv).add($this$commonSegmentsBytes$iv.getBytes$okio().substring(segmentStart$iv, i$iv));
            segmentStart$iv = i$iv + 1;
        }
        if (segmentStart$iv < $this$commonSegmentsBytes$iv.getBytes$okio().size()) {
            ((Collection)result$iv).add($this$commonSegmentsBytes$iv.getBytes$okio().substring(segmentStart$iv, $this$commonSegmentsBytes$iv.getBytes$okio().size()));
        }
        Iterable $this$map$iv = result$iv;
        boolean $i$f$map = false;
        result$iv = $this$map$iv;
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10));
        boolean $i$f$mapTo = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv) {
            void it;
            ByteString byteString = (ByteString)item$iv$iv;
            Collection collection = destination$iv$iv;
            boolean bl = false;
            collection.add(it.utf8());
        }
        return (List)destination$iv$iv;
    }

    @NotNull
    public static final List<ByteString> commonSegmentsBytes(@NotNull Path $this$commonSegmentsBytes) {
        Intrinsics.checkNotNullParameter($this$commonSegmentsBytes, "<this>");
        boolean $i$f$commonSegmentsBytes = false;
        List result = new ArrayList();
        int segmentStart = -Path.rootLength($this$commonSegmentsBytes);
        if (segmentStart == -1) {
            segmentStart = 0;
        } else if (segmentStart < $this$commonSegmentsBytes.getBytes$okio().size() && $this$commonSegmentsBytes.getBytes$okio().getByte(segmentStart) == 92) {
            ++segmentStart;
        }
        int n = $this$commonSegmentsBytes.getBytes$okio().size();
        for (int i = segmentStart; i < n; ++i) {
            if ($this$commonSegmentsBytes.getBytes$okio().getByte(i) != 47 && $this$commonSegmentsBytes.getBytes$okio().getByte(i) != 92) continue;
            ((Collection)result).add($this$commonSegmentsBytes.getBytes$okio().substring(segmentStart, i));
            segmentStart = i + 1;
        }
        if (segmentStart < $this$commonSegmentsBytes.getBytes$okio().size()) {
            ((Collection)result).add($this$commonSegmentsBytes.getBytes$okio().substring(segmentStart, $this$commonSegmentsBytes.getBytes$okio().size()));
        }
        return result;
    }

    private static final int rootLength(Path $this$rootLength) {
        if ($this$rootLength.getBytes$okio().size() == 0) {
            return -1;
        }
        if ($this$rootLength.getBytes$okio().getByte(0) == 47) {
            return 1;
        }
        if ($this$rootLength.getBytes$okio().getByte(0) == 92) {
            if ($this$rootLength.getBytes$okio().size() > 2 && $this$rootLength.getBytes$okio().getByte(1) == 92) {
                int uncRootEnd = $this$rootLength.getBytes$okio().indexOf(BACKSLASH, 2);
                if (uncRootEnd == -1) {
                    uncRootEnd = $this$rootLength.getBytes$okio().size();
                }
                return uncRootEnd;
            }
            return 1;
        }
        if ($this$rootLength.getBytes$okio().size() > 2 && $this$rootLength.getBytes$okio().getByte(1) == 58 && $this$rootLength.getBytes$okio().getByte(2) == 92) {
            char c = (char)$this$rootLength.getBytes$okio().getByte(0);
            if (!('a' <= c ? c < '{' : false)) {
                if (!('A' <= c ? c < '[' : false)) {
                    return -1;
                }
            }
            return 3;
        }
        return -1;
    }

    public static final boolean commonIsAbsolute(@NotNull Path $this$commonIsAbsolute) {
        Intrinsics.checkNotNullParameter($this$commonIsAbsolute, "<this>");
        boolean $i$f$commonIsAbsolute = false;
        return -Path.rootLength($this$commonIsAbsolute) != -1;
    }

    public static final boolean commonIsRelative(@NotNull Path $this$commonIsRelative) {
        Intrinsics.checkNotNullParameter($this$commonIsRelative, "<this>");
        boolean $i$f$commonIsRelative = false;
        return -Path.rootLength($this$commonIsRelative) == -1;
    }

    @Nullable
    public static final Character commonVolumeLetter(@NotNull Path $this$commonVolumeLetter) {
        Intrinsics.checkNotNullParameter($this$commonVolumeLetter, "<this>");
        boolean $i$f$commonVolumeLetter = false;
        if (ByteString.indexOf$default($this$commonVolumeLetter.getBytes$okio(), -Path.SLASH, 0, 2, null) != -1) {
            return null;
        }
        if ($this$commonVolumeLetter.getBytes$okio().size() < 2) {
            return null;
        }
        if ($this$commonVolumeLetter.getBytes$okio().getByte(1) != 58) {
            return null;
        }
        char c = (char)$this$commonVolumeLetter.getBytes$okio().getByte(0);
        if (!('a' <= c ? c < '{' : false)) {
            if (!('A' <= c ? c < '[' : false)) {
                return null;
            }
        }
        return Character.valueOf(c);
    }

    private static final int getIndexOfLastSlash(Path $this$indexOfLastSlash) {
        int lastSlash = ByteString.lastIndexOf$default($this$indexOfLastSlash.getBytes$okio(), SLASH, 0, 2, null);
        if (lastSlash != -1) {
            return lastSlash;
        }
        return ByteString.lastIndexOf$default($this$indexOfLastSlash.getBytes$okio(), BACKSLASH, 0, 2, null);
    }

    @NotNull
    public static final ByteString commonNameBytes(@NotNull Path $this$commonNameBytes) {
        Intrinsics.checkNotNullParameter($this$commonNameBytes, "<this>");
        boolean $i$f$commonNameBytes = false;
        int lastSlash = -Path.getIndexOfLastSlash($this$commonNameBytes);
        return lastSlash != -1 ? ByteString.substring$default($this$commonNameBytes.getBytes$okio(), lastSlash + 1, 0, 2, null) : ($this$commonNameBytes.volumeLetter() != null && $this$commonNameBytes.getBytes$okio().size() == 2 ? ByteString.EMPTY : $this$commonNameBytes.getBytes$okio());
    }

    @NotNull
    public static final String commonName(@NotNull Path $this$commonName) {
        Intrinsics.checkNotNullParameter($this$commonName, "<this>");
        boolean $i$f$commonName = false;
        return $this$commonName.nameBytes().utf8();
    }

    @Nullable
    public static final Path commonParent(@NotNull Path $this$commonParent) {
        Intrinsics.checkNotNullParameter($this$commonParent, "<this>");
        boolean $i$f$commonParent = false;
        if (Intrinsics.areEqual($this$commonParent.getBytes$okio(), -Path.DOT) || Intrinsics.areEqual($this$commonParent.getBytes$okio(), -Path.SLASH) || Intrinsics.areEqual($this$commonParent.getBytes$okio(), -Path.BACKSLASH) || -Path.lastSegmentIsDotDot($this$commonParent)) {
            return null;
        }
        int lastSlash = -Path.getIndexOfLastSlash($this$commonParent);
        if (lastSlash == 2 && $this$commonParent.volumeLetter() != null) {
            if ($this$commonParent.getBytes$okio().size() == 3) {
                return null;
            }
            return new Path(ByteString.substring$default($this$commonParent.getBytes$okio(), 0, 3, 1, null));
        }
        if (lastSlash == 1 && $this$commonParent.getBytes$okio().startsWith(-Path.BACKSLASH)) {
            return null;
        }
        if (lastSlash == -1 && $this$commonParent.volumeLetter() != null) {
            if ($this$commonParent.getBytes$okio().size() == 2) {
                return null;
            }
            return new Path(ByteString.substring$default($this$commonParent.getBytes$okio(), 0, 2, 1, null));
        }
        if (lastSlash == -1) {
            return new Path(-Path.DOT);
        }
        if (lastSlash == 0) {
            return new Path(ByteString.substring$default($this$commonParent.getBytes$okio(), 0, 1, 1, null));
        }
        return new Path(ByteString.substring$default($this$commonParent.getBytes$okio(), 0, lastSlash, 1, null));
    }

    private static final boolean lastSegmentIsDotDot(Path $this$lastSegmentIsDotDot) {
        if ($this$lastSegmentIsDotDot.getBytes$okio().endsWith(DOT_DOT)) {
            if ($this$lastSegmentIsDotDot.getBytes$okio().size() == 2) {
                return true;
            }
            if ($this$lastSegmentIsDotDot.getBytes$okio().rangeEquals($this$lastSegmentIsDotDot.getBytes$okio().size() - 3, SLASH, 0, 1)) {
                return true;
            }
            if ($this$lastSegmentIsDotDot.getBytes$okio().rangeEquals($this$lastSegmentIsDotDot.getBytes$okio().size() - 3, BACKSLASH, 0, 1)) {
                return true;
            }
        }
        return false;
    }

    public static final boolean commonIsRoot(@NotNull Path $this$commonIsRoot) {
        Intrinsics.checkNotNullParameter($this$commonIsRoot, "<this>");
        boolean $i$f$commonIsRoot = false;
        return -Path.rootLength($this$commonIsRoot) == $this$commonIsRoot.getBytes$okio().size();
    }

    /*
     * WARNING - void declaration
     */
    @NotNull
    public static final Path commonResolve(@NotNull Path $this$commonResolve, @NotNull String child, boolean normalize) {
        void $this$commonResolve$iv;
        Intrinsics.checkNotNullParameter($this$commonResolve, "<this>");
        Intrinsics.checkNotNullParameter(child, "child");
        boolean $i$f$commonResolve = false;
        Path path = $this$commonResolve;
        Buffer child$iv = new Buffer().writeUtf8(child);
        boolean $i$f$commonResolve2 = false;
        return -Path.commonResolve((Path)$this$commonResolve$iv, -Path.toPath(child$iv, false), normalize);
    }

    /*
     * WARNING - void declaration
     */
    @NotNull
    public static final Path commonResolve(@NotNull Path $this$commonResolve, @NotNull ByteString child, boolean normalize) {
        void $this$commonResolve$iv;
        Intrinsics.checkNotNullParameter($this$commonResolve, "<this>");
        Intrinsics.checkNotNullParameter(child, "child");
        boolean $i$f$commonResolve = false;
        Path path = $this$commonResolve;
        Buffer child$iv = new Buffer().write(child);
        boolean $i$f$commonResolve2 = false;
        return -Path.commonResolve((Path)$this$commonResolve$iv, -Path.toPath(child$iv, false), normalize);
    }

    @NotNull
    public static final Path commonResolve(@NotNull Path $this$commonResolve, @NotNull Buffer child, boolean normalize) {
        Intrinsics.checkNotNullParameter($this$commonResolve, "<this>");
        Intrinsics.checkNotNullParameter(child, "child");
        boolean $i$f$commonResolve = false;
        return -Path.commonResolve($this$commonResolve, -Path.toPath(child, false), normalize);
    }

    @NotNull
    public static final Path commonResolve(@NotNull Path $this$commonResolve, @NotNull Path child, boolean normalize) {
        Intrinsics.checkNotNullParameter($this$commonResolve, "<this>");
        Intrinsics.checkNotNullParameter(child, "child");
        if (child.isAbsolute() || child.volumeLetter() != null) {
            return child;
        }
        ByteString byteString = -Path.getSlash($this$commonResolve);
        if (byteString == null && (byteString = -Path.getSlash(child)) == null) {
            byteString = -Path.toSlash(Path.DIRECTORY_SEPARATOR);
        }
        ByteString slash = byteString;
        Buffer buffer = new Buffer();
        buffer.write($this$commonResolve.getBytes$okio());
        if (buffer.size() > 0L) {
            buffer.write(slash);
        }
        buffer.write(child.getBytes$okio());
        return -Path.toPath(buffer, normalize);
    }

    @NotNull
    public static final Path commonRelativeTo(@NotNull Path $this$commonRelativeTo, @NotNull Path other) {
        int i;
        int firstNewSegmentIndex;
        Intrinsics.checkNotNullParameter($this$commonRelativeTo, "<this>");
        Intrinsics.checkNotNullParameter(other, "other");
        boolean $i$f$commonRelativeTo = false;
        if (!Intrinsics.areEqual($this$commonRelativeTo.getRoot(), other.getRoot())) {
            boolean $i$a$-require--Path$commonRelativeTo$32 = false;
            String $i$a$-require--Path$commonRelativeTo$32 = "Paths of different roots cannot be relative to each other: " + $this$commonRelativeTo + " and " + other;
            throw new IllegalArgumentException($i$a$-require--Path$commonRelativeTo$32.toString());
        }
        List<ByteString> thisSegments = $this$commonRelativeTo.getSegmentsBytes();
        List<ByteString> otherSegments = other.getSegmentsBytes();
        int minSegmentsSize = Math.min(thisSegments.size(), otherSegments.size());
        for (firstNewSegmentIndex = 0; firstNewSegmentIndex < minSegmentsSize && Intrinsics.areEqual(thisSegments.get(firstNewSegmentIndex), otherSegments.get(firstNewSegmentIndex)); ++firstNewSegmentIndex) {
        }
        if (firstNewSegmentIndex == minSegmentsSize && $this$commonRelativeTo.getBytes$okio().size() == other.getBytes$okio().size()) {
            return Path.Companion.get$default(Path.Companion, ".", false, 1, null);
        }
        if (!(otherSegments.subList(firstNewSegmentIndex, otherSegments.size()).indexOf(-Path.DOT_DOT) == -1)) {
            boolean $i$a$-require--Path$commonRelativeTo$42 = false;
            String $i$a$-require--Path$commonRelativeTo$42 = "Impossible relative path to resolve: " + $this$commonRelativeTo + " and " + other;
            throw new IllegalArgumentException($i$a$-require--Path$commonRelativeTo$42.toString());
        }
        Buffer buffer = new Buffer();
        ByteString byteString = -Path.getSlash(other);
        if (byteString == null && (byteString = -Path.getSlash($this$commonRelativeTo)) == null) {
            byteString = -Path.toSlash(Path.DIRECTORY_SEPARATOR);
        }
        ByteString slash = byteString;
        int n = otherSegments.size();
        for (i = firstNewSegmentIndex; i < n; ++i) {
            buffer.write(-Path.DOT_DOT);
            buffer.write(slash);
        }
        n = thisSegments.size();
        for (i = firstNewSegmentIndex; i < n; ++i) {
            buffer.write(thisSegments.get(i));
            buffer.write(slash);
        }
        return -Path.toPath(buffer, false);
    }

    @NotNull
    public static final Path commonNormalized(@NotNull Path $this$commonNormalized) {
        Intrinsics.checkNotNullParameter($this$commonNormalized, "<this>");
        boolean $i$f$commonNormalized = false;
        return Path.Companion.get($this$commonNormalized.toString(), true);
    }

    private static final ByteString getSlash(Path $this$slash) {
        return ByteString.indexOf$default($this$slash.getBytes$okio(), SLASH, 0, 2, null) != -1 ? SLASH : (ByteString.indexOf$default($this$slash.getBytes$okio(), BACKSLASH, 0, 2, null) != -1 ? BACKSLASH : null);
    }

    public static final int commonCompareTo(@NotNull Path $this$commonCompareTo, @NotNull Path other) {
        Intrinsics.checkNotNullParameter($this$commonCompareTo, "<this>");
        Intrinsics.checkNotNullParameter(other, "other");
        boolean $i$f$commonCompareTo = false;
        return $this$commonCompareTo.getBytes$okio().compareTo(other.getBytes$okio());
    }

    public static final boolean commonEquals(@NotNull Path $this$commonEquals, @Nullable Object other) {
        Intrinsics.checkNotNullParameter($this$commonEquals, "<this>");
        boolean $i$f$commonEquals = false;
        return other instanceof Path && Intrinsics.areEqual(((Path)other).getBytes$okio(), $this$commonEquals.getBytes$okio());
    }

    public static final int commonHashCode(@NotNull Path $this$commonHashCode) {
        Intrinsics.checkNotNullParameter($this$commonHashCode, "<this>");
        boolean $i$f$commonHashCode = false;
        return $this$commonHashCode.getBytes$okio().hashCode();
    }

    @NotNull
    public static final String commonToString(@NotNull Path $this$commonToString) {
        Intrinsics.checkNotNullParameter($this$commonToString, "<this>");
        boolean $i$f$commonToString = false;
        return $this$commonToString.getBytes$okio().utf8();
    }

    @NotNull
    public static final Path commonToPath(@NotNull String $this$commonToPath, boolean normalize) {
        Intrinsics.checkNotNullParameter($this$commonToPath, "<this>");
        return -Path.toPath(new Buffer().writeUtf8($this$commonToPath), normalize);
    }

    @NotNull
    public static final Path toPath(@NotNull Buffer $this$toPath, boolean normalize) {
        boolean windowsUncPath;
        Intrinsics.checkNotNullParameter($this$toPath, "<this>");
        ByteString slash = null;
        Buffer result = new Buffer();
        int leadingSlashCount = 0;
        while ($this$toPath.rangeEquals(0L, SLASH) || $this$toPath.rangeEquals(0L, BACKSLASH)) {
            byte by = $this$toPath.readByte();
            ByteString byteString = slash;
            if (byteString == null) {
                byteString = -Path.toSlash(by);
            }
            slash = byteString;
            ++leadingSlashCount;
        }
        boolean bl = windowsUncPath = leadingSlashCount >= 2 && Intrinsics.areEqual(slash, BACKSLASH);
        if (windowsUncPath) {
            ByteString byteString = slash;
            Intrinsics.checkNotNull(byteString);
            result.write(byteString);
            result.write(slash);
        } else if (leadingSlashCount > 0) {
            ByteString byteString = slash;
            Intrinsics.checkNotNull(byteString);
            result.write(byteString);
        } else {
            long limit = $this$toPath.indexOfElement(ANY_SLASH);
            ByteString byteString = slash;
            if (byteString == null) {
                byteString = limit == -1L ? -Path.toSlash(Path.DIRECTORY_SEPARATOR) : -Path.toSlash($this$toPath.getByte(limit));
            }
            if (-Path.startsWithVolumeLetterAndColon($this$toPath, slash = byteString)) {
                if (limit == 2L) {
                    result.write($this$toPath, 3L);
                } else {
                    result.write($this$toPath, 2L);
                }
            }
        }
        boolean absolute = result.size() > 0L;
        List canonicalParts = new ArrayList();
        while (!$this$toPath.exhausted()) {
            long limit = $this$toPath.indexOfElement(ANY_SLASH);
            ByteString part = null;
            if (limit == -1L) {
                part = $this$toPath.readByteString();
            } else {
                part = $this$toPath.readByteString(limit);
                $this$toPath.readByte();
            }
            if (Intrinsics.areEqual(part, DOT_DOT)) {
                if (absolute && canonicalParts.isEmpty()) continue;
                if (!normalize || !absolute && (canonicalParts.isEmpty() || Intrinsics.areEqual(CollectionsKt.last(canonicalParts), DOT_DOT))) {
                    canonicalParts.add(part);
                    continue;
                }
                if (windowsUncPath && canonicalParts.size() == 1) continue;
                CollectionsKt.removeLastOrNull(canonicalParts);
                continue;
            }
            if (Intrinsics.areEqual(part, DOT) || Intrinsics.areEqual(part, ByteString.EMPTY)) continue;
            canonicalParts.add(part);
        }
        int n = canonicalParts.size();
        for (int i = 0; i < n; ++i) {
            if (i > 0) {
                result.write(slash);
            }
            result.write((ByteString)canonicalParts.get(i));
        }
        if (result.size() == 0L) {
            result.write(DOT);
        }
        return new Path(result.readByteString());
    }

    private static final ByteString toSlash(String $this$toSlash) {
        ByteString byteString;
        String string = $this$toSlash;
        if (Intrinsics.areEqual(string, "/")) {
            byteString = SLASH;
        } else if (Intrinsics.areEqual(string, "\\")) {
            byteString = BACKSLASH;
        } else {
            throw new IllegalArgumentException("not a directory separator: " + $this$toSlash);
        }
        return byteString;
    }

    private static final ByteString toSlash(byte $this$toSlash) {
        ByteString byteString;
        switch ($this$toSlash) {
            case 47: {
                byteString = SLASH;
                break;
            }
            case 92: {
                byteString = BACKSLASH;
                break;
            }
            default: {
                throw new IllegalArgumentException("not a directory separator: " + $this$toSlash);
            }
        }
        return byteString;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private static final boolean startsWithVolumeLetterAndColon(Buffer $this$startsWithVolumeLetterAndColon, ByteString slash) {
        boolean bl;
        if (!Intrinsics.areEqual(slash, BACKSLASH)) {
            return false;
        }
        if ($this$startsWithVolumeLetterAndColon.size() < 2L) {
            return false;
        }
        if ($this$startsWithVolumeLetterAndColon.getByte(1L) != 58) {
            return false;
        }
        char b = (char)$this$startsWithVolumeLetterAndColon.getByte(0L);
        if ('a' <= b) {
            if (b < '{') {
                return true;
            }
            bl = false;
        } else {
            bl = false;
        }
        if (bl) return true;
        if ('A' > b) return false;
        if (b >= '[') return false;
        return true;
    }
}

