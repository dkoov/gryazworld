/*
 * Decompiled with CFR 0.152.
 */
package okio.internal;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import okio.FileHandle;
import okio.FileMetadata;
import okio.FileSystem;
import okio.Okio;
import okio.Path;
import okio.Sink;
import okio.Source;
import okio.ZipFileSystem;
import okio.internal.ResourceFileSystem;
import okio.internal.ZipFilesKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000^\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\r\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0000\u0018\u0000 /2\u00020\u0001:\u0001/B!\b\u0000\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0001\u00a2\u0006\u0002\u0010\u0007J\u0018\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u000b2\u0006\u0010\u0013\u001a\u00020\u0005H\u0016J\u0018\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u000b2\u0006\u0010\u0017\u001a\u00020\u000bH\u0016J\u0010\u0010\u0018\u001a\u00020\u000b2\u0006\u0010\u0019\u001a\u00020\u000bH\u0016J\u0010\u0010\u001a\u001a\u00020\u000b2\u0006\u0010\u0019\u001a\u00020\u000bH\u0002J\u0018\u0010\u001b\u001a\u00020\u00152\u0006\u0010\u001c\u001a\u00020\u000b2\u0006\u0010\u001d\u001a\u00020\u0005H\u0016J\u0018\u0010\u001e\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u000b2\u0006\u0010\u0017\u001a\u00020\u000bH\u0016J\u0018\u0010\u001f\u001a\u00020\u00152\u0006\u0010\u0019\u001a\u00020\u000b2\u0006\u0010\u0013\u001a\u00020\u0005H\u0016J\u0016\u0010 \u001a\b\u0012\u0004\u0012\u00020\u000b0\t2\u0006\u0010\u001c\u001a\u00020\u000bH\u0016J\u0018\u0010!\u001a\n\u0012\u0004\u0012\u00020\u000b\u0018\u00010\t2\u0006\u0010\u001c\u001a\u00020\u000bH\u0016J\u0012\u0010\"\u001a\u0004\u0018\u00010#2\u0006\u0010\u0019\u001a\u00020\u000bH\u0016J\u0010\u0010$\u001a\u00020%2\u0006\u0010\u0012\u001a\u00020\u000bH\u0016J \u0010&\u001a\u00020%2\u0006\u0010\u0012\u001a\u00020\u000b2\u0006\u0010\u001d\u001a\u00020\u00052\u0006\u0010\u0013\u001a\u00020\u0005H\u0016J\u0018\u0010'\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u000b2\u0006\u0010\u001d\u001a\u00020\u0005H\u0016J\u0010\u0010\u0016\u001a\u00020(2\u0006\u0010\u0012\u001a\u00020\u000bH\u0016J\u001e\u0010)\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0001\u0012\u0004\u0012\u00020\u000b0\n0\t*\u00020\u0003H\u0002J\u001a\u0010*\u001a\u0010\u0012\u0004\u0012\u00020\u0001\u0012\u0004\u0012\u00020\u000b\u0018\u00010\n*\u00020+H\u0002J\u001a\u0010,\u001a\u0010\u0012\u0004\u0012\u00020\u0001\u0012\u0004\u0012\u00020\u000b\u0018\u00010\n*\u00020+H\u0002J\f\u0010-\u001a\u00020.*\u00020\u000bH\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R-\u0010\b\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0001\u0012\u0004\u0012\u00020\u000b0\n0\t8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u000e\u0010\u000f\u001a\u0004\b\f\u0010\rR\u000e\u0010\u0006\u001a\u00020\u0001X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u00060"}, d2={"Lokio/internal/ResourceFileSystem;", "Lokio/FileSystem;", "classLoader", "Ljava/lang/ClassLoader;", "indexEagerly", "", "systemFileSystem", "(Ljava/lang/ClassLoader;ZLokio/FileSystem;)V", "roots", "", "Lkotlin/Pair;", "Lokio/Path;", "getRoots", "()Ljava/util/List;", "roots$delegate", "Lkotlin/Lazy;", "appendingSink", "Lokio/Sink;", "file", "mustExist", "atomicMove", "", "source", "target", "canonicalize", "path", "canonicalizeInternal", "createDirectory", "dir", "mustCreate", "createSymlink", "delete", "list", "listOrNull", "metadataOrNull", "Lokio/FileMetadata;", "openReadOnly", "Lokio/FileHandle;", "openReadWrite", "sink", "Lokio/Source;", "toClasspathRoots", "toFileRoot", "Ljava/net/URL;", "toJarRoot", "toRelativePath", "", "Companion", "okio"})
@SourceDebugExtension(value={"SMAP\nResourceFileSystem.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ResourceFileSystem.kt\nokio/internal/ResourceFileSystem\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,210:1\n766#2:211\n857#2,2:212\n1549#2:214\n1620#2,3:215\n766#2:218\n857#2,2:219\n1549#2:221\n1620#2,3:222\n1603#2,9:225\n1855#2:234\n1856#2:236\n1612#2:237\n1603#2,9:238\n1855#2:247\n1856#2:249\n1612#2:250\n1#3:235\n1#3:248\n*S KotlinDebug\n*F\n+ 1 ResourceFileSystem.kt\nokio/internal/ResourceFileSystem\n*L\n74#1:211\n74#1:212,2\n75#1:214\n75#1:215,3\n90#1:218\n90#1:219,2\n91#1:221\n91#1:222,3\n173#1:225,9\n173#1:234\n173#1:236\n173#1:237\n174#1:238,9\n174#1:247\n174#1:249\n174#1:250\n173#1:235\n174#1:248\n*E\n"})
public final class ResourceFileSystem
extends FileSystem {
    @NotNull
    private static final Companion Companion = new Companion(null);
    @NotNull
    private final ClassLoader classLoader;
    @NotNull
    private final FileSystem systemFileSystem;
    @NotNull
    private final Lazy roots$delegate;
    @NotNull
    private static final Path ROOT = Path.Companion.get$default(Path.Companion, "/", false, 1, null);

    public ResourceFileSystem(@NotNull ClassLoader classLoader, boolean indexEagerly, @NotNull FileSystem systemFileSystem) {
        Intrinsics.checkNotNullParameter(classLoader, "classLoader");
        Intrinsics.checkNotNullParameter(systemFileSystem, "systemFileSystem");
        this.classLoader = classLoader;
        this.systemFileSystem = systemFileSystem;
        this.roots$delegate = LazyKt.lazy((Function0)new Function0<List<? extends Pair<? extends FileSystem, ? extends Path>>>(this){
            final /* synthetic */ ResourceFileSystem this$0;
            {
                this.this$0 = $receiver;
                super(0);
            }

            @NotNull
            public final List<Pair<FileSystem, Path>> invoke() {
                return ResourceFileSystem.access$toClasspathRoots(this.this$0, ResourceFileSystem.access$getClassLoader$p(this.this$0));
            }
        });
        if (indexEagerly) {
            this.getRoots().size();
        }
    }

    public /* synthetic */ ResourceFileSystem(ClassLoader classLoader, boolean bl, FileSystem fileSystem, int n, DefaultConstructorMarker defaultConstructorMarker) {
        if ((n & 4) != 0) {
            fileSystem = FileSystem.SYSTEM;
        }
        this(classLoader, bl, fileSystem);
    }

    private final List<Pair<FileSystem, Path>> getRoots() {
        Lazy lazy = this.roots$delegate;
        return (List)lazy.getValue();
    }

    @Override
    @NotNull
    public Path canonicalize(@NotNull Path path) {
        Intrinsics.checkNotNullParameter(path, "path");
        return this.canonicalizeInternal(path);
    }

    private final Path canonicalizeInternal(Path path) {
        return ROOT.resolve(path, true);
    }

    /*
     * WARNING - void declaration
     */
    @Override
    @NotNull
    public List<Path> list(@NotNull Path dir) {
        Intrinsics.checkNotNullParameter(dir, "dir");
        String relativePath = this.toRelativePath(dir);
        Set result = new LinkedHashSet();
        boolean foundAny = false;
        for (Pair<FileSystem, Path> pair : this.getRoots()) {
            FileSystem fileSystem = pair.component1();
            Path base = pair.component2();
            try {
                void $this$mapTo$iv$iv;
                void $this$map$iv;
                Path it;
                void $this$filterTo$iv$iv;
                Iterable $this$filter$iv;
                Collection collection = result;
                Iterable iterable = fileSystem.list(base.resolve(relativePath));
                boolean $i$f$filter = false;
                void var12_13 = $this$filter$iv;
                Collection destination$iv$iv = new ArrayList();
                boolean $i$f$filterTo = false;
                for (Object element$iv$iv : $this$filterTo$iv$iv) {
                    it = (Path)element$iv$iv;
                    boolean bl = false;
                    if (!ResourceFileSystem.Companion.keepPath(it)) continue;
                    destination$iv$iv.add(element$iv$iv);
                }
                $this$filter$iv = (List)destination$iv$iv;
                boolean $i$f$map = false;
                $this$filterTo$iv$iv = $this$map$iv;
                destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10));
                boolean $i$f$mapTo = false;
                for (Object item$iv$iv : $this$mapTo$iv$iv) {
                    it = (Path)item$iv$iv;
                    Collection collection2 = destination$iv$iv;
                    boolean bl = false;
                    collection2.add(Companion.removeBase(it, base));
                }
                iterable = (List)destination$iv$iv;
                CollectionsKt.addAll(collection, iterable);
                foundAny = true;
            }
            catch (IOException iOException) {}
        }
        if (!foundAny) {
            throw new FileNotFoundException("file not found: " + dir);
        }
        return CollectionsKt.toList(result);
    }

    /*
     * WARNING - void declaration
     */
    @Override
    @Nullable
    public List<Path> listOrNull(@NotNull Path dir) {
        Intrinsics.checkNotNullParameter(dir, "dir");
        String relativePath = this.toRelativePath(dir);
        Set result = new LinkedHashSet();
        boolean foundAny = false;
        for (Pair<FileSystem, Path> pair : this.getRoots()) {
            List baseResult;
            List list;
            Path base;
            FileSystem fileSystem = pair.component1();
            List<Path> list2 = fileSystem.listOrNull((base = pair.component2()).resolve(relativePath));
            if (list2 != null) {
                void $this$mapTo$iv$iv;
                void $this$map$iv;
                Path it;
                void $this$filterTo$iv$iv;
                Iterable $this$filter$iv;
                Iterable iterable = list2;
                boolean $i$f$filter = false;
                void var13_13 = $this$filter$iv;
                Collection destination$iv$iv = new ArrayList();
                boolean $i$f$filterTo = false;
                for (Object element$iv$iv : $this$filterTo$iv$iv) {
                    it = (Path)element$iv$iv;
                    boolean bl = false;
                    if (!ResourceFileSystem.Companion.keepPath(it)) continue;
                    destination$iv$iv.add(element$iv$iv);
                }
                $this$filter$iv = (List)destination$iv$iv;
                boolean $i$f$map = false;
                $this$filterTo$iv$iv = $this$map$iv;
                destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10));
                boolean $i$f$mapTo = false;
                for (Object item$iv$iv : $this$mapTo$iv$iv) {
                    it = (Path)item$iv$iv;
                    Collection collection = destination$iv$iv;
                    boolean bl = false;
                    collection.add(Companion.removeBase(it, base));
                }
                list = (List)destination$iv$iv;
            } else {
                list = null;
            }
            if ((baseResult = list) == null) continue;
            CollectionsKt.addAll((Collection)result, baseResult);
            foundAny = true;
        }
        return foundAny ? CollectionsKt.toList(result) : null;
    }

    @Override
    @NotNull
    public FileHandle openReadOnly(@NotNull Path file) {
        Intrinsics.checkNotNullParameter(file, "file");
        if (!ResourceFileSystem.Companion.keepPath(file)) {
            throw new FileNotFoundException("file not found: " + file);
        }
        String relativePath = this.toRelativePath(file);
        for (Pair<FileSystem, Path> pair : this.getRoots()) {
            FileSystem fileSystem = pair.component1();
            Path base = pair.component2();
            try {
                return fileSystem.openReadOnly(base.resolve(relativePath));
            }
            catch (FileNotFoundException fileNotFoundException) {
            }
        }
        throw new FileNotFoundException("file not found: " + file);
    }

    @Override
    @NotNull
    public FileHandle openReadWrite(@NotNull Path file, boolean mustCreate, boolean mustExist) {
        Intrinsics.checkNotNullParameter(file, "file");
        throw new IOException("resources are not writable");
    }

    @Override
    @Nullable
    public FileMetadata metadataOrNull(@NotNull Path path) {
        Intrinsics.checkNotNullParameter(path, "path");
        if (!ResourceFileSystem.Companion.keepPath(path)) {
            return null;
        }
        String relativePath = this.toRelativePath(path);
        for (Pair<FileSystem, Path> pair : this.getRoots()) {
            FileSystem fileSystem = pair.component1();
            Path base = pair.component2();
            FileMetadata fileMetadata = fileSystem.metadataOrNull(base.resolve(relativePath));
            if (fileMetadata == null) continue;
            return fileMetadata;
        }
        return null;
    }

    @Override
    @NotNull
    public Source source(@NotNull Path file) {
        Intrinsics.checkNotNullParameter(file, "file");
        if (!ResourceFileSystem.Companion.keepPath(file)) {
            throw new FileNotFoundException("file not found: " + file);
        }
        Path relativePath = Path.resolve$default(ROOT, file, false, 2, null).relativeTo(ROOT);
        Closeable closeable = this.classLoader.getResourceAsStream(relativePath.toString());
        if (closeable == null || (closeable = Okio.source(closeable)) == null) {
            throw new FileNotFoundException("file not found: " + file);
        }
        return closeable;
    }

    @Override
    @NotNull
    public Sink sink(@NotNull Path file, boolean mustCreate) {
        Intrinsics.checkNotNullParameter(file, "file");
        throw new IOException(this + " is read-only");
    }

    @Override
    @NotNull
    public Sink appendingSink(@NotNull Path file, boolean mustExist) {
        Intrinsics.checkNotNullParameter(file, "file");
        throw new IOException(this + " is read-only");
    }

    @Override
    public void createDirectory(@NotNull Path dir, boolean mustCreate) {
        Intrinsics.checkNotNullParameter(dir, "dir");
        throw new IOException(this + " is read-only");
    }

    @Override
    public void atomicMove(@NotNull Path source2, @NotNull Path target) {
        Intrinsics.checkNotNullParameter(source2, "source");
        Intrinsics.checkNotNullParameter(target, "target");
        throw new IOException(this + " is read-only");
    }

    @Override
    public void delete(@NotNull Path path, boolean mustExist) {
        Intrinsics.checkNotNullParameter(path, "path");
        throw new IOException(this + " is read-only");
    }

    @Override
    public void createSymlink(@NotNull Path source2, @NotNull Path target) {
        Intrinsics.checkNotNullParameter(source2, "source");
        Intrinsics.checkNotNullParameter(target, "target");
        throw new IOException(this + " is read-only");
    }

    private final String toRelativePath(Path $this$toRelativePath) {
        Path canonicalThis = this.canonicalizeInternal($this$toRelativePath);
        return canonicalThis.relativeTo(ROOT).toString();
    }

    private final List<Pair<FileSystem, Path>> toClasspathRoots(ClassLoader $this$toClasspathRoots) {
        Pair<FileSystem, Path> it$iv$iv;
        boolean bl;
        URL it;
        boolean bl2;
        Object element$iv$iv;
        Object element$iv$iv$iv;
        Iterable $this$mapNotNullTo$iv$iv;
        Enumeration<URL> enumeration = $this$toClasspathRoots.getResources("");
        Intrinsics.checkNotNullExpressionValue(enumeration, "getResources(...)");
        ArrayList<URL> arrayList = Collections.list(enumeration);
        Intrinsics.checkNotNullExpressionValue(arrayList, "list(this)");
        Iterable $this$mapNotNull$iv = arrayList;
        boolean $i$f$mapNotNull = false;
        Iterable iterable = $this$mapNotNull$iv;
        Collection destination$iv$iv = new ArrayList();
        boolean $i$f$mapNotNullTo = false;
        Iterable $this$forEach$iv$iv$iv = $this$mapNotNullTo$iv$iv;
        boolean $i$f$forEach = false;
        Iterator iterator2 = $this$forEach$iv$iv$iv.iterator();
        while (iterator2.hasNext()) {
            element$iv$iv = element$iv$iv$iv = iterator2.next();
            bl2 = false;
            it = (URL)element$iv$iv;
            boolean bl3 = false;
            Intrinsics.checkNotNull(it);
            if (this.toFileRoot(it) == null) continue;
            bl = false;
            destination$iv$iv.add(it$iv$iv);
        }
        Collection collection = (List)destination$iv$iv;
        Enumeration<URL> enumeration2 = $this$toClasspathRoots.getResources("META-INF/MANIFEST.MF");
        Intrinsics.checkNotNullExpressionValue(enumeration2, "getResources(...)");
        ArrayList<URL> arrayList2 = Collections.list(enumeration2);
        Intrinsics.checkNotNullExpressionValue(arrayList2, "list(this)");
        $this$mapNotNull$iv = arrayList2;
        Collection collection2 = collection;
        $i$f$mapNotNull = false;
        $this$mapNotNullTo$iv$iv = $this$mapNotNull$iv;
        destination$iv$iv = new ArrayList();
        $i$f$mapNotNullTo = false;
        $this$forEach$iv$iv$iv = $this$mapNotNullTo$iv$iv;
        $i$f$forEach = false;
        iterator2 = $this$forEach$iv$iv$iv.iterator();
        while (iterator2.hasNext()) {
            element$iv$iv = element$iv$iv$iv = iterator2.next();
            bl2 = false;
            it = (URL)element$iv$iv;
            boolean bl4 = false;
            Intrinsics.checkNotNull(it);
            if (this.toJarRoot(it) == null) continue;
            bl = false;
            destination$iv$iv.add(it$iv$iv);
        }
        return CollectionsKt.plus(collection2, (Iterable)((List)destination$iv$iv));
    }

    private final Pair<FileSystem, Path> toFileRoot(URL $this$toFileRoot) {
        if (!Intrinsics.areEqual($this$toFileRoot.getProtocol(), "file")) {
            return null;
        }
        return TuplesKt.to(this.systemFileSystem, Path.Companion.get$default(Path.Companion, new File($this$toFileRoot.toURI()), false, 1, null));
    }

    private final Pair<FileSystem, Path> toJarRoot(URL $this$toJarRoot) {
        String string = $this$toJarRoot.toString();
        Intrinsics.checkNotNullExpressionValue(string, "toString(...)");
        String urlString = string;
        if (!StringsKt.startsWith$default(urlString, "jar:file:", false, 2, null)) {
            return null;
        }
        int suffixStart = StringsKt.lastIndexOf$default((CharSequence)urlString, "!", 0, false, 6, null);
        if (suffixStart == -1) {
            return null;
        }
        String string2 = urlString.substring(4, suffixStart);
        Intrinsics.checkNotNullExpressionValue(string2, "this as java.lang.String\u2026ing(startIndex, endIndex)");
        Path path = Path.Companion.get$default(Path.Companion, new File(URI.create(string2)), false, 1, null);
        ZipFileSystem zip2 = ZipFilesKt.openZip(path, this.systemFileSystem, toJarRoot.zip.1.INSTANCE);
        return TuplesKt.to(zip2, ROOT);
    }

    public static final /* synthetic */ Companion access$getCompanion$p() {
        return Companion;
    }

    public static final /* synthetic */ List access$toClasspathRoots(ResourceFileSystem $this, ClassLoader $receiver) {
        return $this.toClasspathRoots($receiver);
    }

    public static final /* synthetic */ ClassLoader access$getClassLoader$p(ResourceFileSystem $this) {
        return $this.classLoader;
    }

    @Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0082\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\u0004H\u0002J\u0012\u0010\n\u001a\u00020\u0004*\u00020\u00042\u0006\u0010\u000b\u001a\u00020\u0004R\u0011\u0010\u0003\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006\u00a8\u0006\f"}, d2={"Lokio/internal/ResourceFileSystem$Companion;", "", "()V", "ROOT", "Lokio/Path;", "getROOT", "()Lokio/Path;", "keepPath", "", "path", "removeBase", "base", "okio"})
    private static final class Companion {
        private Companion() {
        }

        @NotNull
        public final Path getROOT() {
            return ROOT;
        }

        @NotNull
        public final Path removeBase(@NotNull Path $this$removeBase, @NotNull Path base) {
            Intrinsics.checkNotNullParameter($this$removeBase, "<this>");
            Intrinsics.checkNotNullParameter(base, "base");
            String prefix = base.toString();
            return this.getROOT().resolve(StringsKt.replace$default(StringsKt.removePrefix($this$removeBase.toString(), (CharSequence)prefix), '\\', '/', false, 4, null));
        }

        private final boolean keepPath(Path path) {
            return !StringsKt.endsWith(path.name(), ".class", true);
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }
}

