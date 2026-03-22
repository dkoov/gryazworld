/*
 * Decompiled with CFR 0.152.
 */
package kotlin.io.path;

import java.nio.file.FileSystemLoopException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.ArrayDeque;
import kotlin.collections.ArraysKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.io.path.DirectoryEntriesReader;
import kotlin.io.path.ExperimentalPathApi;
import kotlin.io.path.LinkFollowing;
import kotlin.io.path.PathNode;
import kotlin.io.path.PathTreeWalkKt;
import kotlin.io.path.PathWalkOption;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.InlineMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.sequences.Sequence;
import kotlin.sequences.SequenceScope;
import kotlin.sequences.SequencesKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 8, 0}, k=1, xi=48, d1={"\u0000V\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010(\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\b\u0002\b\u0001\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u001d\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u000e\u0010\u0004\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00060\u0005\u00a2\u0006\u0002\u0010\u0007J\u000e\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00020\u0015H\u0002J\u000e\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00020\u0015H\u0002J\u000f\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00020\u0015H\u0096\u0002JE\u0010\u0018\u001a\u00020\u0019*\b\u0012\u0004\u0012\u00020\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u001d\u001a\u00020\u001e2\u0018\u0010\u001f\u001a\u0014\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001c0!\u0012\u0004\u0012\u00020\u00190 H\u0082H\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\"R\u0014\u0010\b\u001a\u00020\t8BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b\n\u0010\u000bR\u0014\u0010\f\u001a\u00020\t8BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b\r\u0010\u000bR\u0014\u0010\u000e\u001a\u00020\t8BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b\u000e\u0010\u000bR\u001a\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00100\u00058BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b\u0011\u0010\u0012R\u0018\u0010\u0004\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00060\u0005X\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\u0013R\u000e\u0010\u0003\u001a\u00020\u0002X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u0082\u0002\u0004\n\u0002\b\u0019\u00a8\u0006#"}, d2={"Lkotlin/io/path/PathTreeWalk;", "Lkotlin/sequences/Sequence;", "Ljava/nio/file/Path;", "start", "options", "", "Lkotlin/io/path/PathWalkOption;", "(Ljava/nio/file/Path;[Lkotlin/io/path/PathWalkOption;)V", "followLinks", "", "getFollowLinks", "()Z", "includeDirectories", "getIncludeDirectories", "isBFS", "linkOptions", "Ljava/nio/file/LinkOption;", "getLinkOptions", "()[Ljava/nio/file/LinkOption;", "[Lkotlin/io/path/PathWalkOption;", "bfsIterator", "", "dfsIterator", "iterator", "yieldIfNeeded", "", "Lkotlin/sequences/SequenceScope;", "node", "Lkotlin/io/path/PathNode;", "entriesReader", "Lkotlin/io/path/DirectoryEntriesReader;", "entriesAction", "Lkotlin/Function1;", "", "(Lkotlin/sequences/SequenceScope;Lkotlin/io/path/PathNode;Lkotlin/io/path/DirectoryEntriesReader;Lkotlin/jvm/functions/Function1;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "kotlin-stdlib-jdk7"})
@ExperimentalPathApi
public final class PathTreeWalk
implements Sequence<Path> {
    @NotNull
    private final Path start;
    @NotNull
    private final PathWalkOption[] options;

    public PathTreeWalk(@NotNull Path start, @NotNull PathWalkOption[] options) {
        Intrinsics.checkNotNullParameter(start, "start");
        Intrinsics.checkNotNullParameter(options, "options");
        this.start = start;
        this.options = options;
    }

    private final boolean getFollowLinks() {
        return ArraysKt.contains(this.options, PathWalkOption.FOLLOW_LINKS);
    }

    private final LinkOption[] getLinkOptions() {
        return LinkFollowing.INSTANCE.toLinkOptions(this.getFollowLinks());
    }

    private final boolean getIncludeDirectories() {
        return ArraysKt.contains(this.options, PathWalkOption.INCLUDE_DIRECTORIES);
    }

    private final boolean isBFS() {
        return ArraysKt.contains(this.options, PathWalkOption.BREADTH_FIRST);
    }

    @Override
    @NotNull
    public Iterator<Path> iterator() {
        return this.isBFS() ? this.bfsIterator() : this.dfsIterator();
    }

    private final Object yieldIfNeeded(SequenceScope<? super Path> $this$yieldIfNeeded, PathNode node, DirectoryEntriesReader entriesReader, Function1<? super List<PathNode>, Unit> entriesAction, Continuation<? super Unit> $completion) {
        Path path;
        boolean $i$f$yieldIfNeeded = false;
        Path path2 = path = node.getPath();
        LinkOption[] linkOptionArray = this.getLinkOptions();
        linkOptionArray = Arrays.copyOf(linkOptionArray, linkOptionArray.length);
        if (Files.isDirectory(path2, Arrays.copyOf(linkOptionArray, linkOptionArray.length))) {
            if (PathTreeWalkKt.access$createsCycle(node)) {
                throw new FileSystemLoopException(((Object)path).toString());
            }
            if (this.getIncludeDirectories()) {
                InlineMarker.mark(0);
                $this$yieldIfNeeded.yield(path, $completion);
                InlineMarker.mark(1);
            }
            path2 = path;
            linkOptionArray = this.getLinkOptions();
            linkOptionArray = Arrays.copyOf(linkOptionArray, linkOptionArray.length);
            if (Files.isDirectory(path2, Arrays.copyOf(linkOptionArray, linkOptionArray.length))) {
                entriesAction.invoke(entriesReader.readEntries(node));
            }
        } else {
            path2 = path;
            linkOptionArray = new LinkOption[]{LinkOption.NOFOLLOW_LINKS};
            if (Files.exists(path2, Arrays.copyOf(linkOptionArray, linkOptionArray.length))) {
                InlineMarker.mark(0);
                $this$yieldIfNeeded.yield(path, $completion);
                InlineMarker.mark(1);
                return Unit.INSTANCE;
            }
        }
        return Unit.INSTANCE;
    }

    private final Iterator<Path> dfsIterator() {
        return SequencesKt.iterator((Function2)new Function2<SequenceScope<? super Path>, Continuation<? super Unit>, Object>(this, null){
            Object L$1;
            Object L$2;
            Object L$3;
            Object L$4;
            Object L$5;
            int label;
            private /* synthetic */ Object L$0;
            final /* synthetic */ PathTreeWalk this$0;
            {
                this.this$0 = $receiver;
                super(2, $completion);
            }

            /*
             * Unable to fully structure code
             */
            @Nullable
            public final Object invokeSuspend(@NotNull Object var1_1) {
                block17: {
                    var17_2 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
                    block0 : switch (this.label) {
                        case 0: {
                            ResultKt.throwOnFailure(var1_1);
                            $this$iterator = (SequenceScope)this.L$0;
                            stack = new ArrayDeque();
                            entriesReader = new DirectoryEntriesReader(PathTreeWalk.access$getFollowLinks(this.this$0));
                            startNode = new PathNode(PathTreeWalk.access$getStart$p(this.this$0), PathTreeWalkKt.access$keyOf(PathTreeWalk.access$getStart$p(this.this$0), PathTreeWalk.access$getLinkOptions(this.this$0)), null);
                            var6_7 = this.this$0;
                            $this$yieldIfNeeded$iv = $this$iterator;
                            $i$f$yieldIfNeeded = false;
                            var10_12 = path$iv = startNode.getPath();
                            var11_13 = PathTreeWalk.access$getLinkOptions(this_$iv);
                            var11_13 = Arrays.copyOf(var11_13, var11_13.length);
                            if (!Files.isDirectory((Path)var10_12, Arrays.copyOf(var11_13, var11_13.length))) break;
                            if (PathTreeWalkKt.access$createsCycle(startNode)) {
                                throw new FileSystemLoopException(path$iv.toString());
                            }
                            if (PathTreeWalk.access$getIncludeDirectories(this_$iv)) {
                                this.L$0 = $this$iterator;
                                this.L$1 = stack;
                                this.L$2 = entriesReader;
                                this.L$3 = startNode;
                                this.L$4 = this_$iv;
                                this.L$5 = path$iv;
                                this.label = 1;
                                v0 = $this$yieldIfNeeded$iv.yield(path$iv, this);
                                if (v0 == var17_2) {
                                    return var17_2;
                                }
                            }
                            ** GOTO lbl40
                        }
                        case 1: {
                            $i$f$yieldIfNeeded = false;
                            path$iv = (Path)this.L$5;
                            this_$iv = (PathTreeWalk)this.L$4;
                            startNode = (PathNode)this.L$3;
                            entriesReader = (DirectoryEntriesReader)this.L$2;
                            stack = (ArrayDeque)this.L$1;
                            $this$iterator = (SequenceScope)this.L$0;
                            ResultKt.throwOnFailure($result);
                            v0 = $result;
lbl40:
                            // 2 sources

                            var10_12 = path$iv;
                            var11_13 = PathTreeWalk.access$getLinkOptions(this_$iv);
                            if (Files.isDirectory((Path)var10_12, Arrays.copyOf(var11_13 = Arrays.copyOf(var11_13, var11_13.length), var11_13.length))) {
                                entries = entriesReader.readEntries(startNode);
                                $i$a$-yieldIfNeeded-PathTreeWalk$dfsIterator$1$1 = false;
                                startNode.setContentIterator(entries.iterator());
                                stack.addLast(startNode);
                            }
                            ** GOTO lbl67
                        }
                    }
                    var10_12 = path$iv;
                    var11_13 = new LinkOption[]{LinkOption.NOFOLLOW_LINKS};
                    if (Files.exists((Path)var10_12, Arrays.copyOf(var11_13, var11_13.length))) {
                        this.L$0 = $this$iterator;
                        this.L$1 = stack;
                        this.L$2 = entriesReader;
                        this.label = 2;
                        v1 = $this$yieldIfNeeded$iv.yield(path$iv, this);
                        if (v1 == var17_2) {
                            return var17_2;
                        }
                    }
                    ** GOTO lbl67
                    {
                        case 2: {
                            $i$f$yieldIfNeeded = false;
                            entriesReader = (DirectoryEntriesReader)this.L$2;
                            stack = (ArrayDeque)this.L$1;
                            $this$iterator = (SequenceScope)this.L$0;
                            ResultKt.throwOnFailure($result);
                            v1 = $result;
lbl67:
                            // 9 sources

                            while (((Collection)stack).isEmpty() == false) {
                                topNode = (PathNode)stack.last();
                                Intrinsics.checkNotNull(topNode.getContentIterator());
                                if (!topIterator.hasNext()) break block0;
                                pathNode = topIterator.next();
                                path$iv = this.this$0;
                                $this$yieldIfNeeded$iv = $this$iterator;
                                $i$f$yieldIfNeeded = false;
                                var13_17 = path$iv = pathNode.getPath();
                                var14_18 = PathTreeWalk.access$getLinkOptions(this_$iv);
                                if (!Files.isDirectory(var13_17, Arrays.copyOf(var14_18 = Arrays.copyOf(var14_18, var14_18.length), var14_18.length))) break block0;
                                if (PathTreeWalkKt.access$createsCycle(pathNode)) {
                                    throw new FileSystemLoopException(path$iv.toString());
                                }
                                if (PathTreeWalk.access$getIncludeDirectories(this_$iv)) {
                                    this.L$0 = $this$iterator;
                                    this.L$1 = stack;
                                    this.L$2 = entriesReader;
                                    this.L$3 = pathNode;
                                    this.L$4 = this_$iv;
                                    this.L$5 = path$iv;
                                    this.label = 3;
                                    v2 = $this$yieldIfNeeded$iv.yield(path$iv, this);
                                    if (v2 == var17_2) {
                                        return var17_2;
                                    }
                                }
                                ** GOTO lbl103
                            }
                            break block17;
                        }
                        case 3: {
                            $i$f$yieldIfNeeded = false;
                            path$iv = (Path)this.L$5;
                            this_$iv = (PathTreeWalk)this.L$4;
                            pathNode = (PathNode)this.L$3;
                            entriesReader = (DirectoryEntriesReader)this.L$2;
                            stack = (ArrayDeque)this.L$1;
                            $this$iterator = (SequenceScope)this.L$0;
                            ResultKt.throwOnFailure($result);
                            v2 = $result;
lbl103:
                            // 2 sources

                            var13_17 = path$iv;
                            var14_18 = PathTreeWalk.access$getLinkOptions(this_$iv);
                            if (!Files.isDirectory(var13_17, Arrays.copyOf(var14_18 = Arrays.copyOf(var14_18, var14_18.length), var14_18.length))) ** GOTO lbl67
                            entries = entriesReader.readEntries(pathNode);
                            $i$a$-yieldIfNeeded-PathTreeWalk$dfsIterator$1$2 = false;
                            pathNode.setContentIterator(entries.iterator());
                            stack.addLast(pathNode);
                            ** GOTO lbl67
                        }
                    }
                    var13_17 = path$iv;
                    var14_18 = new LinkOption[]{LinkOption.NOFOLLOW_LINKS};
                    if (!Files.exists(var13_17, Arrays.copyOf(var14_18, var14_18.length))) ** GOTO lbl67
                    this.L$0 = $this$iterator;
                    this.L$1 = stack;
                    this.L$2 = entriesReader;
                    this.L$3 = null;
                    this.L$4 = null;
                    this.L$5 = null;
                    this.label = 4;
                    v3 = $this$yieldIfNeeded$iv.yield(path$iv, this);
                    if (v3 != var17_2) ** GOTO lbl67
                    return var17_2;
                    {
                        case 4: {
                            $i$f$yieldIfNeeded = false;
                            entriesReader = (DirectoryEntriesReader)this.L$2;
                            stack = (ArrayDeque)this.L$1;
                            $this$iterator = (SequenceScope)this.L$0;
                            ResultKt.throwOnFailure($result);
                            v3 = $result;
                            ** GOTO lbl67
                        }
                    }
                    stack.removeLast();
                    ** GOTO lbl67
                }
                return Unit.INSTANCE;
                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
            }

            @NotNull
            public final Continuation<Unit> create(@Nullable Object value, @NotNull Continuation<?> $completion) {
                Function2<SequenceScope<? super Path>, Continuation<? super Unit>, Object> function2 = new /* invalid duplicate definition of identical inner class */;
                function2.L$0 = value;
                return (Continuation)((Object)function2);
            }

            @Nullable
            public final Object invoke(@NotNull SequenceScope<? super Path> p1, @Nullable Continuation<? super Unit> p2) {
                return (this.create(p1, p2)).invokeSuspend(Unit.INSTANCE);
            }
        });
    }

    private final Iterator<Path> bfsIterator() {
        return SequencesKt.iterator((Function2)new Function2<SequenceScope<? super Path>, Continuation<? super Unit>, Object>(this, null){
            Object L$1;
            Object L$2;
            Object L$3;
            Object L$4;
            Object L$5;
            int label;
            private /* synthetic */ Object L$0;
            final /* synthetic */ PathTreeWalk this$0;
            {
                this.this$0 = $receiver;
                super(2, $completion);
            }

            /*
             * Unable to fully structure code
             */
            @Nullable
            public final Object invokeSuspend(@NotNull Object var1_1) {
                var14_2 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
                block0 : switch (this.label) {
                    case 0: {
                        ResultKt.throwOnFailure(var1_1);
                        $this$iterator = (SequenceScope)this.L$0;
                        queue = new ArrayDeque();
                        entriesReader = new DirectoryEntriesReader(PathTreeWalk.access$getFollowLinks(this.this$0));
                        queue.addLast(new PathNode(PathTreeWalk.access$getStart$p(this.this$0), PathTreeWalkKt.access$keyOf(PathTreeWalk.access$getStart$p(this.this$0), PathTreeWalk.access$getLinkOptions(this.this$0)), null));
lbl9:
                        // 6 sources

                        while (((Collection)queue).isEmpty() == false) {
                            pathNode = (PathNode)queue.removeFirst();
                            var6_7 = this.this$0;
                            $this$yieldIfNeeded$iv = $this$iterator;
                            $i$f$yieldIfNeeded = false;
                            var10_11 = path$iv = pathNode.getPath();
                            var11_12 = PathTreeWalk.access$getLinkOptions(this_$iv);
                            if (!Files.isDirectory(var10_11, Arrays.copyOf(var11_12 = Arrays.copyOf(var11_12, var11_12.length), var11_12.length))) break block0;
                            if (PathTreeWalkKt.access$createsCycle(pathNode)) {
                                throw new FileSystemLoopException(path$iv.toString());
                            }
                            if (PathTreeWalk.access$getIncludeDirectories(this_$iv)) {
                                this.L$0 = $this$iterator;
                                this.L$1 = queue;
                                this.L$2 = entriesReader;
                                this.L$3 = pathNode;
                                this.L$4 = this_$iv;
                                this.L$5 = path$iv;
                                this.label = 1;
                                v0 = $this$yieldIfNeeded$iv.yield(path$iv, this);
                                if (v0 == var14_2) {
                                    return var14_2;
                                }
                            }
                            ** GOTO lbl42
                        }
                        break;
                    }
                    case 1: {
                        $i$f$yieldIfNeeded = false;
                        path$iv = (Path)this.L$5;
                        this_$iv = (PathTreeWalk)this.L$4;
                        pathNode = (PathNode)this.L$3;
                        entriesReader = (DirectoryEntriesReader)this.L$2;
                        queue = (ArrayDeque)this.L$1;
                        $this$iterator = (SequenceScope)this.L$0;
                        ResultKt.throwOnFailure($result);
                        v0 = $result;
lbl42:
                        // 2 sources

                        var10_11 = path$iv;
                        var11_12 = PathTreeWalk.access$getLinkOptions(this_$iv);
                        if (!Files.isDirectory(var10_11, Arrays.copyOf(var11_12 = Arrays.copyOf(var11_12, var11_12.length), var11_12.length))) ** GOTO lbl9
                        entries = entriesReader.readEntries(pathNode);
                        $i$a$-yieldIfNeeded-PathTreeWalk$bfsIterator$1$1 = false;
                        queue.addAll((Collection)entries);
                        ** GOTO lbl9
                    }
                }
                var10_11 = path$iv;
                var11_12 = new LinkOption[]{LinkOption.NOFOLLOW_LINKS};
                if (!Files.exists(var10_11, Arrays.copyOf(var11_12, var11_12.length))) ** GOTO lbl9
                this.L$0 = $this$iterator;
                this.L$1 = queue;
                this.L$2 = entriesReader;
                this.L$3 = null;
                this.L$4 = null;
                this.L$5 = null;
                this.label = 2;
                v1 = $this$yieldIfNeeded$iv.yield(path$iv, this);
                if (v1 != var14_2) ** GOTO lbl9
                return var14_2;
                {
                    case 2: {
                        $i$f$yieldIfNeeded = false;
                        entriesReader = (DirectoryEntriesReader)this.L$2;
                        queue = (ArrayDeque)this.L$1;
                        $this$iterator = (SequenceScope)this.L$0;
                        ResultKt.throwOnFailure($result);
                        v1 = $result;
                        ** GOTO lbl9
                    }
                }
                return Unit.INSTANCE;
                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
            }

            @NotNull
            public final Continuation<Unit> create(@Nullable Object value, @NotNull Continuation<?> $completion) {
                Function2<SequenceScope<? super Path>, Continuation<? super Unit>, Object> function2 = new /* invalid duplicate definition of identical inner class */;
                function2.L$0 = value;
                return (Continuation)((Object)function2);
            }

            @Nullable
            public final Object invoke(@NotNull SequenceScope<? super Path> p1, @Nullable Continuation<? super Unit> p2) {
                return (this.create(p1, p2)).invokeSuspend(Unit.INSTANCE);
            }
        });
    }

    public static final /* synthetic */ boolean access$getFollowLinks(PathTreeWalk $this) {
        return $this.getFollowLinks();
    }

    public static final /* synthetic */ Path access$getStart$p(PathTreeWalk $this) {
        return $this.start;
    }
}

