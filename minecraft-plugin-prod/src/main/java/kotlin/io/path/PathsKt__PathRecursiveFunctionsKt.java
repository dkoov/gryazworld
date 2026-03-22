/*
 * Decompiled with CFR 0.152.
 */
package kotlin.io.path;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystemException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.SecureDirectoryStream;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import kotlin.ExceptionsKt;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.SinceKotlin;
import kotlin.Unit;
import kotlin.io.CloseableKt;
import kotlin.io.path.CopyActionContext;
import kotlin.io.path.CopyActionResult;
import kotlin.io.path.DefaultCopyActionContext;
import kotlin.io.path.ExceptionsCollector;
import kotlin.io.path.ExperimentalPathApi;
import kotlin.io.path.FileVisitorBuilder;
import kotlin.io.path.LinkFollowing;
import kotlin.io.path.OnErrorResult;
import kotlin.io.path.PathsKt;
import kotlin.io.path.PathsKt__PathReadWriteKt;
import kotlin.io.path.PathsKt__PathRecursiveFunctionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.jvm.internal.SpreadBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 8, 0}, k=5, xi=49, d1={"\u0000v\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a$\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00010\u0005H\u0082\b\u00a2\u0006\u0002\b\u0006\u001a\u001d\u0010\u0007\u001a\u00020\u00012\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\u0002\u001a\u00020\u0003H\u0002\u00a2\u0006\u0002\b\n\u001a\u001d\u0010\u000b\u001a\u00020\u00012\u0006\u0010\f\u001a\u00020\t2\u0006\u0010\u0002\u001a\u00020\u0003H\u0002\u00a2\u0006\u0002\b\r\u001a&\u0010\u000e\u001a\u0004\u0018\u0001H\u000f\"\u0004\b\u0000\u0010\u000f2\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u0002H\u000f0\u0005H\u0082\b\u00a2\u0006\u0004\b\u0010\u0010\u0011\u001aw\u0010\u0012\u001a\u00020\t*\u00020\t2\u0006\u0010\u0013\u001a\u00020\t2Q\b\u0002\u0010\u0014\u001aK\u0012\u0013\u0012\u00110\t\u00a2\u0006\f\b\u0016\u0012\b\b\u0017\u0012\u0004\b\b(\u0018\u0012\u0013\u0012\u00110\t\u00a2\u0006\f\b\u0016\u0012\b\b\u0017\u0012\u0004\b\b(\u0013\u0012\u0017\u0012\u00150\u0019j\u0002`\u001a\u00a2\u0006\f\b\u0016\u0012\b\b\u0017\u0012\u0004\b\b(\u001b\u0012\u0004\u0012\u00020\u001c0\u00152\u0006\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u001f\u001a\u00020\u001eH\u0007\u001a\u00b4\u0001\u0010\u0012\u001a\u00020\t*\u00020\t2\u0006\u0010\u0013\u001a\u00020\t2Q\b\u0002\u0010\u0014\u001aK\u0012\u0013\u0012\u00110\t\u00a2\u0006\f\b\u0016\u0012\b\b\u0017\u0012\u0004\b\b(\u0018\u0012\u0013\u0012\u00110\t\u00a2\u0006\f\b\u0016\u0012\b\b\u0017\u0012\u0004\b\b(\u0013\u0012\u0017\u0012\u00150\u0019j\u0002`\u001a\u00a2\u0006\f\b\u0016\u0012\b\b\u0017\u0012\u0004\b\b(\u001b\u0012\u0004\u0012\u00020\u001c0\u00152\u0006\u0010\u001d\u001a\u00020\u001e2C\b\u0002\u0010 \u001a=\u0012\u0004\u0012\u00020!\u0012\u0013\u0012\u00110\t\u00a2\u0006\f\b\u0016\u0012\b\b\u0017\u0012\u0004\b\b(\u0018\u0012\u0013\u0012\u00110\t\u00a2\u0006\f\b\u0016\u0012\b\b\u0017\u0012\u0004\b\b(\u0013\u0012\u0004\u0012\u00020\"0\u0015\u00a2\u0006\u0002\b#H\u0007\u001a\f\u0010$\u001a\u00020\u0001*\u00020\tH\u0007\u001a\u001b\u0010%\u001a\f\u0012\b\u0012\u00060\u0019j\u0002`\u001a0&*\u00020\tH\u0002\u00a2\u0006\u0002\b'\u001a'\u0010(\u001a\u00020\u0001*\b\u0012\u0004\u0012\u00020\t0)2\u0006\u0010\u0017\u001a\u00020\t2\u0006\u0010\u0002\u001a\u00020\u0003H\u0002\u00a2\u0006\u0002\b*\u001a'\u0010+\u001a\u00020\u0001*\b\u0012\u0004\u0012\u00020\t0)2\u0006\u0010\u0017\u001a\u00020\t2\u0006\u0010\u0002\u001a\u00020\u0003H\u0002\u00a2\u0006\u0002\b,\u001a5\u0010-\u001a\u00020\u001e*\b\u0012\u0004\u0012\u00020\t0)2\u0006\u0010.\u001a\u00020\t2\u0012\u0010/\u001a\n\u0012\u0006\b\u0001\u0012\u00020100\"\u000201H\u0002\u00a2\u0006\u0004\b2\u00103\u001a\u0011\u00104\u001a\u000205*\u00020\"H\u0003\u00a2\u0006\u0002\b6\u001a\u0011\u00104\u001a\u000205*\u00020\u001cH\u0003\u00a2\u0006\u0002\b6\u00a8\u00067"}, d2={"collectIfThrows", "", "collector", "Lkotlin/io/path/ExceptionsCollector;", "function", "Lkotlin/Function0;", "collectIfThrows$PathsKt__PathRecursiveFunctionsKt", "insecureEnterDirectory", "path", "Ljava/nio/file/Path;", "insecureEnterDirectory$PathsKt__PathRecursiveFunctionsKt", "insecureHandleEntry", "entry", "insecureHandleEntry$PathsKt__PathRecursiveFunctionsKt", "tryIgnoreNoSuchFileException", "R", "tryIgnoreNoSuchFileException$PathsKt__PathRecursiveFunctionsKt", "(Lkotlin/jvm/functions/Function0;)Ljava/lang/Object;", "copyToRecursively", "target", "onError", "Lkotlin/Function3;", "Lkotlin/ParameterName;", "name", "source", "Ljava/lang/Exception;", "Lkotlin/Exception;", "exception", "Lkotlin/io/path/OnErrorResult;", "followLinks", "", "overwrite", "copyAction", "Lkotlin/io/path/CopyActionContext;", "Lkotlin/io/path/CopyActionResult;", "Lkotlin/ExtensionFunctionType;", "deleteRecursively", "deleteRecursivelyImpl", "", "deleteRecursivelyImpl$PathsKt__PathRecursiveFunctionsKt", "enterDirectory", "Ljava/nio/file/SecureDirectoryStream;", "enterDirectory$PathsKt__PathRecursiveFunctionsKt", "handleEntry", "handleEntry$PathsKt__PathRecursiveFunctionsKt", "isDirectory", "entryName", "options", "", "Ljava/nio/file/LinkOption;", "isDirectory$PathsKt__PathRecursiveFunctionsKt", "(Ljava/nio/file/SecureDirectoryStream;Ljava/nio/file/Path;[Ljava/nio/file/LinkOption;)Z", "toFileVisitResult", "Ljava/nio/file/FileVisitResult;", "toFileVisitResult$PathsKt__PathRecursiveFunctionsKt", "kotlin-stdlib-jdk7"}, xs="kotlin/io/path/PathsKt")
@SourceDebugExtension(value={"SMAP\nPathRecursiveFunctions.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PathRecursiveFunctions.kt\nkotlin/io/path/PathsKt__PathRecursiveFunctionsKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,420:1\n336#1,2:424\n344#1:426\n344#1:427\n338#1,4:428\n336#1,2:432\n344#1:434\n338#1,4:435\n344#1:439\n336#1,6:440\n336#1,2:446\n344#1:448\n338#1,4:449\n1#2:421\n1855#3,2:422\n*S KotlinDebug\n*F\n+ 1 PathRecursiveFunctions.kt\nkotlin/io/path/PathsKt__PathRecursiveFunctionsKt\n*L\n352#1:424,2\n361#1:426\n364#1:427\n352#1:428,4\n372#1:432,2\n373#1:434\n372#1:435,4\n384#1:439\n392#1:440,6\n410#1:446,2\n411#1:448\n410#1:449,4\n274#1:422,2\n*E\n"})
class PathsKt__PathRecursiveFunctionsKt
extends PathsKt__PathReadWriteKt {
    @ExperimentalPathApi
    @SinceKotlin(version="1.8")
    @NotNull
    public static final Path copyToRecursively(@NotNull Path $this$copyToRecursively, @NotNull Path target, @NotNull Function3<? super Path, ? super Path, ? super Exception, ? extends OnErrorResult> onError, boolean followLinks, boolean overwrite) {
        Intrinsics.checkNotNullParameter($this$copyToRecursively, "<this>");
        Intrinsics.checkNotNullParameter(target, "target");
        Intrinsics.checkNotNullParameter(onError, "onError");
        return overwrite ? PathsKt.copyToRecursively($this$copyToRecursively, target, onError, followLinks, (Function3<? super CopyActionContext, ? super Path, ? super Path, ? extends CopyActionResult>)new Function3<CopyActionContext, Path, Path, CopyActionResult>(followLinks){
            final /* synthetic */ boolean $followLinks;
            {
                this.$followLinks = $followLinks;
                super(3);
            }

            @NotNull
            public final CopyActionResult invoke(@NotNull CopyActionContext $this$copyToRecursively, @NotNull Path src, @NotNull Path dst) {
                Intrinsics.checkNotNullParameter($this$copyToRecursively, "$this$copyToRecursively");
                Intrinsics.checkNotNullParameter(src, "src");
                Intrinsics.checkNotNullParameter(dst, "dst");
                LinkOption[] options = LinkFollowing.INSTANCE.toLinkOptions(this.$followLinks);
                Path path = dst;
                Object object = new LinkOption[]{LinkOption.NOFOLLOW_LINKS};
                boolean dstIsDirectory = Files.isDirectory(path, Arrays.copyOf(object, ((LinkOption[])object).length));
                LinkOption[] linkOptionArray = Arrays.copyOf(options, options.length);
                boolean srcIsDirectory = Files.isDirectory(src, Arrays.copyOf(linkOptionArray, linkOptionArray.length));
                if (!srcIsDirectory || !dstIsDirectory) {
                    if (dstIsDirectory) {
                        PathsKt.deleteRecursively(dst);
                    }
                    object = src;
                    CopyOption[] copyOptionArray = new SpreadBuilder(2);
                    copyOptionArray.addSpread(options);
                    copyOptionArray.add(StandardCopyOption.REPLACE_EXISTING);
                    copyOptionArray = (CopyOption[])copyOptionArray.toArray(new CopyOption[copyOptionArray.size()]);
                    Intrinsics.checkNotNullExpressionValue(Files.copy((Path)object, dst, Arrays.copyOf(copyOptionArray, copyOptionArray.length)), "copy(this, target, *options)");
                }
                return CopyActionResult.CONTINUE;
            }
        }) : PathsKt.copyToRecursively$default($this$copyToRecursively, target, onError, followLinks, null, 8, null);
    }

    public static /* synthetic */ Path copyToRecursively$default(Path path, Path path2, Function3 function3, boolean bl, boolean bl2, int n, Object object) {
        if ((n & 2) != 0) {
            function3 = copyToRecursively.1.INSTANCE;
        }
        return PathsKt.copyToRecursively(path, path2, (Function3<? super Path, ? super Path, ? super Exception, ? extends OnErrorResult>)function3, bl, bl2);
    }

    @ExperimentalPathApi
    @SinceKotlin(version="1.8")
    @NotNull
    public static final Path copyToRecursively(@NotNull Path $this$copyToRecursively, @NotNull Path target, @NotNull Function3<? super Path, ? super Path, ? super Exception, ? extends OnErrorResult> onError, boolean followLinks, @NotNull Function3<? super CopyActionContext, ? super Path, ? super Path, ? extends CopyActionResult> copyAction) {
        block11: {
            boolean isSubdirectory;
            boolean bl;
            boolean targetExistsAndNotSymlink;
            block12: {
                Intrinsics.checkNotNullParameter($this$copyToRecursively, "<this>");
                Intrinsics.checkNotNullParameter(target, "target");
                Intrinsics.checkNotNullParameter(onError, "onError");
                Intrinsics.checkNotNullParameter(copyAction, "copyAction");
                Path path = $this$copyToRecursively;
                LinkOption[] linkOptionArray = LinkFollowing.INSTANCE.toLinkOptions(followLinks);
                linkOptionArray = Arrays.copyOf(linkOptionArray, linkOptionArray.length);
                if (!Files.exists(path, Arrays.copyOf(linkOptionArray, linkOptionArray.length))) {
                    throw new NoSuchFileException(((Object)$this$copyToRecursively).toString(), ((Object)target).toString(), "The source file doesn't exist.");
                }
                LinkOption[] linkOptionArray2 = new LinkOption[]{};
                if (!Files.exists($this$copyToRecursively, Arrays.copyOf(linkOptionArray2, linkOptionArray2.length)) || !followLinks && Files.isSymbolicLink($this$copyToRecursively)) break block11;
                LinkOption[] linkOptionArray3 = new LinkOption[]{};
                boolean bl2 = targetExistsAndNotSymlink = Files.exists(target, Arrays.copyOf(linkOptionArray3, linkOptionArray3.length)) && !Files.isSymbolicLink(target);
                if (!targetExistsAndNotSymlink) break block12;
                if (Files.isSameFile($this$copyToRecursively, target)) break block11;
            }
            if (!Intrinsics.areEqual($this$copyToRecursively.getFileSystem(), target.getFileSystem())) {
                bl = false;
            } else if (targetExistsAndNotSymlink) {
                bl = target.toRealPath(new LinkOption[0]).startsWith($this$copyToRecursively.toRealPath(new LinkOption[0]));
            } else {
                Path path = target.getParent();
                if (path != null) {
                    Path it = path;
                    boolean bl3 = false;
                    LinkOption[] linkOptionArray = new LinkOption[]{};
                    bl = Files.exists(it, Arrays.copyOf(linkOptionArray, linkOptionArray.length)) && it.toRealPath(new LinkOption[0]).startsWith($this$copyToRecursively.toRealPath(new LinkOption[0]));
                } else {
                    bl = isSubdirectory = false;
                }
            }
            if (isSubdirectory) {
                throw new FileSystemException(((Object)$this$copyToRecursively).toString(), ((Object)target).toString(), "Recursively copying a directory into its subdirectory is prohibited.");
            }
        }
        PathsKt.visitFileTree$default($this$copyToRecursively, 0, followLinks, new Function1<FileVisitorBuilder, Unit>(copyAction, $this$copyToRecursively, target, onError){
            final /* synthetic */ Function3<CopyActionContext, Path, Path, CopyActionResult> $copyAction;
            final /* synthetic */ Path $this_copyToRecursively;
            final /* synthetic */ Path $target;
            final /* synthetic */ Function3<Path, Path, Exception, OnErrorResult> $onError;
            {
                this.$copyAction = $copyAction;
                this.$this_copyToRecursively = $receiver;
                this.$target = $target;
                this.$onError = $onError;
                super(1);
            }

            public final void invoke(@NotNull FileVisitorBuilder $this$visitFileTree) {
                Intrinsics.checkNotNullParameter($this$visitFileTree, "$this$visitFileTree");
                $this$visitFileTree.onPreVisitDirectory((Function2<? super Path, ? super BasicFileAttributes, ? extends FileVisitResult>)new Function2<Path, BasicFileAttributes, FileVisitResult>(this.$copyAction, this.$this_copyToRecursively, this.$target, this.$onError){
                    final /* synthetic */ Function3<CopyActionContext, Path, Path, CopyActionResult> $copyAction;
                    final /* synthetic */ Path $this_copyToRecursively;
                    final /* synthetic */ Path $target;
                    final /* synthetic */ Function3<Path, Path, Exception, OnErrorResult> $onError;
                    {
                        this.$copyAction = $copyAction;
                        this.$this_copyToRecursively = $receiver;
                        this.$target = $target;
                        this.$onError = $onError;
                        super(2, Intrinsics.Kotlin.class, "copy", "copyToRecursively$copy$PathsKt__PathRecursiveFunctionsKt(Lkotlin/jvm/functions/Function3;Ljava/nio/file/Path;Ljava/nio/file/Path;Lkotlin/jvm/functions/Function3;Ljava/nio/file/Path;Ljava/nio/file/attribute/BasicFileAttributes;)Ljava/nio/file/FileVisitResult;", 0);
                    }

                    @NotNull
                    public final FileVisitResult invoke(@NotNull Path p0, @NotNull BasicFileAttributes p1) {
                        Intrinsics.checkNotNullParameter(p0, "p0");
                        Intrinsics.checkNotNullParameter(p1, "p1");
                        return PathsKt__PathRecursiveFunctionsKt.access$copyToRecursively$copy(this.$copyAction, this.$this_copyToRecursively, this.$target, this.$onError, p0, p1);
                    }
                });
                $this$visitFileTree.onVisitFile((Function2<? super Path, ? super BasicFileAttributes, ? extends FileVisitResult>)new Function2<Path, BasicFileAttributes, FileVisitResult>(this.$copyAction, this.$this_copyToRecursively, this.$target, this.$onError){
                    final /* synthetic */ Function3<CopyActionContext, Path, Path, CopyActionResult> $copyAction;
                    final /* synthetic */ Path $this_copyToRecursively;
                    final /* synthetic */ Path $target;
                    final /* synthetic */ Function3<Path, Path, Exception, OnErrorResult> $onError;
                    {
                        this.$copyAction = $copyAction;
                        this.$this_copyToRecursively = $receiver;
                        this.$target = $target;
                        this.$onError = $onError;
                        super(2, Intrinsics.Kotlin.class, "copy", "copyToRecursively$copy$PathsKt__PathRecursiveFunctionsKt(Lkotlin/jvm/functions/Function3;Ljava/nio/file/Path;Ljava/nio/file/Path;Lkotlin/jvm/functions/Function3;Ljava/nio/file/Path;Ljava/nio/file/attribute/BasicFileAttributes;)Ljava/nio/file/FileVisitResult;", 0);
                    }

                    @NotNull
                    public final FileVisitResult invoke(@NotNull Path p0, @NotNull BasicFileAttributes p1) {
                        Intrinsics.checkNotNullParameter(p0, "p0");
                        Intrinsics.checkNotNullParameter(p1, "p1");
                        return PathsKt__PathRecursiveFunctionsKt.access$copyToRecursively$copy(this.$copyAction, this.$this_copyToRecursively, this.$target, this.$onError, p0, p1);
                    }
                });
                $this$visitFileTree.onVisitFileFailed((Function2<? super Path, ? super IOException, ? extends FileVisitResult>)new Function2<Path, Exception, FileVisitResult>(this.$onError, this.$this_copyToRecursively, this.$target){
                    final /* synthetic */ Function3<Path, Path, Exception, OnErrorResult> $onError;
                    final /* synthetic */ Path $this_copyToRecursively;
                    final /* synthetic */ Path $target;
                    {
                        this.$onError = $onError;
                        this.$this_copyToRecursively = $receiver;
                        this.$target = $target;
                        super(2, Intrinsics.Kotlin.class, "error", "copyToRecursively$error$PathsKt__PathRecursiveFunctionsKt(Lkotlin/jvm/functions/Function3;Ljava/nio/file/Path;Ljava/nio/file/Path;Ljava/nio/file/Path;Ljava/lang/Exception;)Ljava/nio/file/FileVisitResult;", 0);
                    }

                    @NotNull
                    public final FileVisitResult invoke(@NotNull Path p0, @NotNull Exception p1) {
                        Intrinsics.checkNotNullParameter(p0, "p0");
                        Intrinsics.checkNotNullParameter(p1, "p1");
                        return PathsKt__PathRecursiveFunctionsKt.access$copyToRecursively$error(this.$onError, this.$this_copyToRecursively, this.$target, p0, p1);
                    }
                });
                $this$visitFileTree.onPostVisitDirectory((Function2<? super Path, ? super IOException, ? extends FileVisitResult>)new Function2<Path, IOException, FileVisitResult>(this.$onError, this.$this_copyToRecursively, this.$target){
                    final /* synthetic */ Function3<Path, Path, Exception, OnErrorResult> $onError;
                    final /* synthetic */ Path $this_copyToRecursively;
                    final /* synthetic */ Path $target;
                    {
                        this.$onError = $onError;
                        this.$this_copyToRecursively = $receiver;
                        this.$target = $target;
                        super(2);
                    }

                    @NotNull
                    public final FileVisitResult invoke(@NotNull Path directory, @Nullable IOException exception) {
                        Intrinsics.checkNotNullParameter(directory, "directory");
                        return exception == null ? FileVisitResult.CONTINUE : PathsKt__PathRecursiveFunctionsKt.access$copyToRecursively$error(this.$onError, this.$this_copyToRecursively, this.$target, directory, exception);
                    }
                });
            }
        }, 1, null);
        return target;
    }

    public static /* synthetic */ Path copyToRecursively$default(Path path, Path path2, Function3 function3, boolean bl, Function3 function32, int n, Object object) {
        if ((n & 2) != 0) {
            function3 = copyToRecursively.3.INSTANCE;
        }
        if ((n & 8) != 0) {
            function32 = new Function3<CopyActionContext, Path, Path, CopyActionResult>(bl){
                final /* synthetic */ boolean $followLinks;
                {
                    this.$followLinks = $followLinks;
                    super(3);
                }

                @NotNull
                public final CopyActionResult invoke(@NotNull CopyActionContext $this$null, @NotNull Path src, @NotNull Path dst) {
                    Intrinsics.checkNotNullParameter($this$null, "$this$null");
                    Intrinsics.checkNotNullParameter(src, "src");
                    Intrinsics.checkNotNullParameter(dst, "dst");
                    return $this$null.copyToIgnoringExistingDirectory(src, dst, this.$followLinks);
                }
            };
        }
        return PathsKt.copyToRecursively(path, path2, (Function3<? super Path, ? super Path, ? super Exception, ? extends OnErrorResult>)function3, bl, function32);
    }

    @ExperimentalPathApi
    private static final FileVisitResult toFileVisitResult$PathsKt__PathRecursiveFunctionsKt(CopyActionResult $this$toFileVisitResult) {
        FileVisitResult fileVisitResult;
        switch (WhenMappings.$EnumSwitchMapping$0[$this$toFileVisitResult.ordinal()]) {
            case 1: {
                fileVisitResult = FileVisitResult.CONTINUE;
                break;
            }
            case 2: {
                fileVisitResult = FileVisitResult.TERMINATE;
                break;
            }
            case 3: {
                fileVisitResult = FileVisitResult.SKIP_SUBTREE;
                break;
            }
            default: {
                throw new NoWhenBranchMatchedException();
            }
        }
        return fileVisitResult;
    }

    @ExperimentalPathApi
    private static final FileVisitResult toFileVisitResult$PathsKt__PathRecursiveFunctionsKt(OnErrorResult $this$toFileVisitResult) {
        FileVisitResult fileVisitResult;
        switch (WhenMappings.$EnumSwitchMapping$1[$this$toFileVisitResult.ordinal()]) {
            case 1: {
                fileVisitResult = FileVisitResult.TERMINATE;
                break;
            }
            case 2: {
                fileVisitResult = FileVisitResult.SKIP_SUBTREE;
                break;
            }
            default: {
                throw new NoWhenBranchMatchedException();
            }
        }
        return fileVisitResult;
    }

    @ExperimentalPathApi
    @SinceKotlin(version="1.8")
    public static final void deleteRecursively(@NotNull Path $this$deleteRecursively) {
        Intrinsics.checkNotNullParameter($this$deleteRecursively, "<this>");
        List<Exception> suppressedExceptions = PathsKt__PathRecursiveFunctionsKt.deleteRecursivelyImpl$PathsKt__PathRecursiveFunctionsKt($this$deleteRecursively);
        if (!((Collection)suppressedExceptions).isEmpty()) {
            FileSystemException fileSystemException;
            FileSystemException $this$deleteRecursively_u24lambda_u242 = fileSystemException = new FileSystemException("Failed to delete one or more files. See suppressed exceptions for details.");
            boolean bl = false;
            Iterable $this$forEach$iv = suppressedExceptions;
            boolean $i$f$forEach = false;
            for (Object element$iv : $this$forEach$iv) {
                Exception it = (Exception)element$iv;
                boolean bl2 = false;
                ExceptionsKt.addSuppressed($this$deleteRecursively_u24lambda_u242, it);
            }
            throw (Throwable)fileSystemException;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static final List<Exception> deleteRecursivelyImpl$PathsKt__PathRecursiveFunctionsKt(Path $this$deleteRecursivelyImpl) {
        ExceptionsCollector collector = new ExceptionsCollector(0, 1, null);
        boolean useInsecure = false;
        useInsecure = true;
        Path path = $this$deleteRecursivelyImpl.getParent();
        if (path != null) {
            DirectoryStream<Path> directoryStream;
            Closeable closeable;
            Path parent = path;
            boolean bl = false;
            try {
                closeable = Files.newDirectoryStream(parent);
            }
            catch (Throwable _) {
                closeable = null;
            }
            DirectoryStream<Path> directoryStream2 = directoryStream = closeable;
            if (directoryStream2 != null) {
                closeable = directoryStream2;
                Throwable throwable = null;
                try {
                    Closeable stream = closeable;
                    boolean bl2 = false;
                    if (stream instanceof SecureDirectoryStream) {
                        useInsecure = false;
                        collector.setPath(parent);
                        SecureDirectoryStream secureDirectoryStream = (SecureDirectoryStream)stream;
                        Path path2 = $this$deleteRecursivelyImpl.getFileName();
                        Intrinsics.checkNotNullExpressionValue(path2, "this.fileName");
                        PathsKt__PathRecursiveFunctionsKt.handleEntry$PathsKt__PathRecursiveFunctionsKt(secureDirectoryStream, path2, collector);
                    }
                    Unit unit = Unit.INSTANCE;
                }
                catch (Throwable throwable2) {
                    throwable = throwable2;
                    throw throwable2;
                }
                finally {
                    CloseableKt.closeFinally(closeable, throwable);
                }
            }
        }
        if (useInsecure) {
            PathsKt__PathRecursiveFunctionsKt.insecureHandleEntry$PathsKt__PathRecursiveFunctionsKt($this$deleteRecursivelyImpl, collector);
        }
        return collector.getCollectedExceptions();
    }

    private static final void collectIfThrows$PathsKt__PathRecursiveFunctionsKt(ExceptionsCollector collector, Function0<Unit> function) {
        boolean $i$f$collectIfThrows = false;
        try {
            function.invoke();
        }
        catch (Exception exception) {
            collector.collect(exception);
        }
    }

    private static final <R> R tryIgnoreNoSuchFileException$PathsKt__PathRecursiveFunctionsKt(Function0<? extends R> function) {
        R r;
        boolean $i$f$tryIgnoreNoSuchFileException = false;
        try {
            r = function.invoke();
        }
        catch (NoSuchFileException _) {
            r = null;
        }
        return r;
    }

    private static final void handleEntry$PathsKt__PathRecursiveFunctionsKt(SecureDirectoryStream<Path> $this$handleEntry, Path name, ExceptionsCollector collector) {
        collector.enterEntry(name);
        boolean $i$f$collectIfThrows = false;
        try {
            boolean bl = false;
            LinkOption[] linkOptionArray = new LinkOption[]{LinkOption.NOFOLLOW_LINKS};
            if (PathsKt__PathRecursiveFunctionsKt.isDirectory$PathsKt__PathRecursiveFunctionsKt($this$handleEntry, name, linkOptionArray)) {
                int preEnterTotalExceptions = collector.getTotalExceptions();
                PathsKt__PathRecursiveFunctionsKt.enterDirectory$PathsKt__PathRecursiveFunctionsKt($this$handleEntry, name, collector);
                if (preEnterTotalExceptions == collector.getTotalExceptions()) {
                    boolean $i$f$tryIgnoreNoSuchFileException = false;
                    try {
                        boolean bl2 = false;
                        $this$handleEntry.deleteDirectory(name);
                        Unit unit = Unit.INSTANCE;
                    }
                    catch (NoSuchFileException _$iv) {
                        Object var8_14 = null;
                    }
                }
            } else {
                boolean $i$f$tryIgnoreNoSuchFileException = false;
                try {
                    boolean bl3 = false;
                    $this$handleEntry.deleteFile(name);
                    Unit unit = Unit.INSTANCE;
                }
                catch (NoSuchFileException _$iv) {
                    Object var7_12 = null;
                }
            }
        }
        catch (Exception exception$iv) {
            collector.collect(exception$iv);
        }
        collector.exitEntry(name);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static final void enterDirectory$PathsKt__PathRecursiveFunctionsKt(SecureDirectoryStream<Path> $this$enterDirectory, Path name, ExceptionsCollector collector) {
        block10: {
            boolean $i$f$collectIfThrows = false;
            try {
                SecureDirectoryStream<Path> secureDirectoryStream;
                Object object;
                boolean bl = false;
                boolean $i$f$tryIgnoreNoSuchFileException = false;
                try {
                    boolean bl2 = false;
                    object = new LinkOption[]{LinkOption.NOFOLLOW_LINKS};
                    secureDirectoryStream = $this$enterDirectory.newDirectoryStream(name, (LinkOption)object);
                }
                catch (NoSuchFileException _$iv) {
                    secureDirectoryStream = null;
                }
                SecureDirectoryStream<Path> secureDirectoryStream2 = secureDirectoryStream;
                if (secureDirectoryStream2 == null) break block10;
                Closeable closeable = secureDirectoryStream2;
                Throwable throwable = null;
                try {
                    SecureDirectoryStream directoryStream = (SecureDirectoryStream)closeable;
                    boolean bl3 = false;
                    for (Path entry : directoryStream) {
                        Path path = entry.getFileName();
                        Intrinsics.checkNotNullExpressionValue(path, "entry.fileName");
                        PathsKt__PathRecursiveFunctionsKt.handleEntry$PathsKt__PathRecursiveFunctionsKt(directoryStream, path, collector);
                    }
                    object = Unit.INSTANCE;
                }
                catch (Throwable throwable2) {
                    throwable = throwable2;
                    throw throwable2;
                }
                finally {
                    CloseableKt.closeFinally(closeable, throwable);
                }
            }
            catch (Exception exception$iv) {
                collector.collect(exception$iv);
            }
        }
    }

    private static final boolean isDirectory$PathsKt__PathRecursiveFunctionsKt(SecureDirectoryStream<Path> $this$isDirectory, Path entryName, LinkOption ... options) {
        Boolean bl;
        boolean $i$f$tryIgnoreNoSuchFileException = false;
        try {
            boolean bl2 = false;
            bl = $this$isDirectory.getFileAttributeView(entryName, BasicFileAttributeView.class, Arrays.copyOf(options, options.length)).readAttributes().isDirectory();
        }
        catch (NoSuchFileException _$iv) {
            bl = null;
        }
        Boolean bl3 = bl;
        return bl3 != null ? bl3 : false;
    }

    private static final void insecureHandleEntry$PathsKt__PathRecursiveFunctionsKt(Path entry, ExceptionsCollector collector) {
        boolean $i$f$collectIfThrows = false;
        try {
            boolean bl = false;
            Path path = entry;
            LinkOption[] linkOptionArray = new LinkOption[]{LinkOption.NOFOLLOW_LINKS};
            if (Files.isDirectory(path, Arrays.copyOf(linkOptionArray, linkOptionArray.length))) {
                int preEnterTotalExceptions = collector.getTotalExceptions();
                PathsKt__PathRecursiveFunctionsKt.insecureEnterDirectory$PathsKt__PathRecursiveFunctionsKt(entry, collector);
                if (preEnterTotalExceptions == collector.getTotalExceptions()) {
                    Files.deleteIfExists(entry);
                }
            } else {
                Files.deleteIfExists(entry);
            }
        }
        catch (Exception exception$iv) {
            collector.collect(exception$iv);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static final void insecureEnterDirectory$PathsKt__PathRecursiveFunctionsKt(Path path, ExceptionsCollector collector) {
        block10: {
            boolean $i$f$collectIfThrows = false;
            try {
                Object object;
                boolean bl = false;
                boolean $i$f$tryIgnoreNoSuchFileException = false;
                try {
                    boolean bl2 = false;
                    object = Files.newDirectoryStream(path);
                }
                catch (NoSuchFileException _$iv) {
                    object = null;
                }
                DirectoryStream<Path> directoryStream = object;
                if (directoryStream == null) break block10;
                Closeable closeable = directoryStream;
                Throwable throwable = null;
                try {
                    DirectoryStream directoryStream2 = (DirectoryStream)closeable;
                    boolean bl3 = false;
                    for (Path entry : directoryStream2) {
                        Intrinsics.checkNotNullExpressionValue(entry, "entry");
                        PathsKt__PathRecursiveFunctionsKt.insecureHandleEntry$PathsKt__PathRecursiveFunctionsKt(entry, collector);
                    }
                    object = Unit.INSTANCE;
                }
                catch (Throwable throwable2) {
                    throwable = throwable2;
                    throw throwable2;
                }
                finally {
                    CloseableKt.closeFinally(closeable, throwable);
                }
            }
            catch (Exception exception$iv) {
                collector.collect(exception$iv);
            }
        }
    }

    private static final Path copyToRecursively$destination$PathsKt__PathRecursiveFunctionsKt(Path $this_copyToRecursively, Path $target, Path source2) {
        Path relativePath = PathsKt.relativeTo(source2, $this_copyToRecursively);
        Path path = $target.resolve(((Object)relativePath).toString());
        Intrinsics.checkNotNullExpressionValue(path, "target.resolve(relativePath.pathString)");
        return path;
    }

    private static final FileVisitResult copyToRecursively$error$PathsKt__PathRecursiveFunctionsKt(Function3<? super Path, ? super Path, ? super Exception, ? extends OnErrorResult> $onError, Path $this_copyToRecursively, Path $target, Path source2, Exception exception) {
        return PathsKt__PathRecursiveFunctionsKt.toFileVisitResult$PathsKt__PathRecursiveFunctionsKt($onError.invoke(source2, PathsKt__PathRecursiveFunctionsKt.copyToRecursively$destination$PathsKt__PathRecursiveFunctionsKt($this_copyToRecursively, $target, source2), exception));
    }

    private static final FileVisitResult copyToRecursively$copy$PathsKt__PathRecursiveFunctionsKt(Function3<? super CopyActionContext, ? super Path, ? super Path, ? extends CopyActionResult> $copyAction, Path $this_copyToRecursively, Path $target, Function3<? super Path, ? super Path, ? super Exception, ? extends OnErrorResult> $onError, Path source2, BasicFileAttributes attributes) {
        FileVisitResult fileVisitResult;
        try {
            fileVisitResult = PathsKt__PathRecursiveFunctionsKt.toFileVisitResult$PathsKt__PathRecursiveFunctionsKt($copyAction.invoke(DefaultCopyActionContext.INSTANCE, source2, PathsKt__PathRecursiveFunctionsKt.copyToRecursively$destination$PathsKt__PathRecursiveFunctionsKt($this_copyToRecursively, $target, source2)));
        }
        catch (Exception exception) {
            fileVisitResult = PathsKt__PathRecursiveFunctionsKt.copyToRecursively$error$PathsKt__PathRecursiveFunctionsKt($onError, $this_copyToRecursively, $target, source2, exception);
        }
        return fileVisitResult;
    }

    public static final /* synthetic */ FileVisitResult access$copyToRecursively$copy(Function3 $copyAction, Path $this_copyToRecursively, Path $target, Function3 $onError, Path source2, BasicFileAttributes attributes) {
        return PathsKt__PathRecursiveFunctionsKt.copyToRecursively$copy$PathsKt__PathRecursiveFunctionsKt($copyAction, $this_copyToRecursively, $target, $onError, source2, attributes);
    }

    public static final /* synthetic */ FileVisitResult access$copyToRecursively$error(Function3 $onError, Path $this_copyToRecursively, Path $target, Path source2, Exception exception) {
        return PathsKt__PathRecursiveFunctionsKt.copyToRecursively$error$PathsKt__PathRecursiveFunctionsKt($onError, $this_copyToRecursively, $target, source2, exception);
    }

    @Metadata(mv={1, 8, 0}, k=3, xi=48)
    public final class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;
        public static final /* synthetic */ int[] $EnumSwitchMapping$1;

        static {
            int[] nArray = new int[CopyActionResult.values().length];
            try {
                nArray[CopyActionResult.CONTINUE.ordinal()] = 1;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[CopyActionResult.TERMINATE.ordinal()] = 2;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[CopyActionResult.SKIP_SUBTREE.ordinal()] = 3;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            $EnumSwitchMapping$0 = nArray;
            nArray = new int[OnErrorResult.values().length];
            try {
                nArray[OnErrorResult.TERMINATE.ordinal()] = 1;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[OnErrorResult.SKIP_SUBTREE.ordinal()] = 2;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            $EnumSwitchMapping$1 = nArray;
        }
    }
}

