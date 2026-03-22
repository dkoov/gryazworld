/*
 * Decompiled with CFR 0.152.
 */
package okio;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileTime;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import okio.FileMetadata;
import okio.JvmSystemFileSystem;
import okio.Path;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000:\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\t\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0010\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0018\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u0006H\u0016J\u0018\u0010\b\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u0006H\u0016J\u0012\u0010\t\u001a\u0004\u0018\u00010\n2\u0006\u0010\u000b\u001a\u00020\fH\u0004J\u0012\u0010\t\u001a\u0004\u0018\u00010\n2\u0006\u0010\r\u001a\u00020\u0006H\u0016J\b\u0010\u000e\u001a\u00020\u000fH\u0016J\u0013\u0010\u0010\u001a\u0004\u0018\u00010\u0011*\u00020\u0012H\u0002\u00a2\u0006\u0002\u0010\u0013\u00a8\u0006\u0014"}, d2={"Lokio/NioSystemFileSystem;", "Lokio/JvmSystemFileSystem;", "()V", "atomicMove", "", "source", "Lokio/Path;", "target", "createSymlink", "metadataOrNull", "Lokio/FileMetadata;", "nioPath", "Ljava/nio/file/Path;", "path", "toString", "", "zeroToNull", "", "Ljava/nio/file/attribute/FileTime;", "(Ljava/nio/file/attribute/FileTime;)Ljava/lang/Long;", "okio"})
@SourceDebugExtension(value={"SMAP\nNioSystemFileSystem.kt\nKotlin\n*S Kotlin\n*F\n+ 1 NioSystemFileSystem.kt\nokio/NioSystemFileSystem\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,92:1\n1#2:93\n*E\n"})
public class NioSystemFileSystem
extends JvmSystemFileSystem {
    @Override
    @Nullable
    public FileMetadata metadataOrNull(@NotNull Path path) {
        Intrinsics.checkNotNullParameter(path, "path");
        return this.metadataOrNull(path.toNioPath());
    }

    @Nullable
    protected final FileMetadata metadataOrNull(@NotNull java.nio.file.Path nioPath) {
        Object object;
        Intrinsics.checkNotNullParameter(nioPath, "nioPath");
        try {
            object = new LinkOption[]{LinkOption.NOFOLLOW_LINKS};
            object = Files.readAttributes(nioPath, BasicFileAttributes.class, (LinkOption[])object);
        }
        catch (NoSuchFileException _) {
            return null;
        }
        catch (FileSystemException _) {
            return null;
        }
        Object attributes = object;
        java.nio.file.Path symlinkTarget = attributes.isSymbolicLink() ? Files.readSymbolicLink(nioPath) : null;
        java.nio.file.Path path = symlinkTarget;
        FileTime fileTime = attributes.creationTime();
        FileTime fileTime2 = attributes.lastModifiedTime();
        FileTime fileTime3 = attributes.lastAccessTime();
        return new FileMetadata(attributes.isRegularFile(), attributes.isDirectory(), path != null ? Path.Companion.get$default(Path.Companion, path, false, 1, null) : null, attributes.size(), fileTime != null ? this.zeroToNull(fileTime) : null, fileTime2 != null ? this.zeroToNull(fileTime2) : null, fileTime3 != null ? this.zeroToNull(fileTime3) : null, null, 128, null);
    }

    private final Long zeroToNull(FileTime $this$zeroToNull) {
        Long l = $this$zeroToNull.toMillis();
        long it = ((Number)l).longValue();
        boolean bl = false;
        return it != 0L ? l : null;
    }

    @Override
    public void atomicMove(@NotNull Path source2, @NotNull Path target) {
        Intrinsics.checkNotNullParameter(source2, "source");
        Intrinsics.checkNotNullParameter(target, "target");
        try {
            CopyOption[] copyOptionArray = new CopyOption[]{StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING};
            Files.move(source2.toNioPath(), target.toNioPath(), copyOptionArray);
        }
        catch (NoSuchFileException e) {
            throw new FileNotFoundException(e.getMessage());
        }
        catch (UnsupportedOperationException e) {
            throw new IOException("atomic move not supported");
        }
    }

    @Override
    public void createSymlink(@NotNull Path source2, @NotNull Path target) {
        Intrinsics.checkNotNullParameter(source2, "source");
        Intrinsics.checkNotNullParameter(target, "target");
        Files.createSymbolicLink(source2.toNioPath(), target.toNioPath(), new FileAttribute[0]);
    }

    @Override
    @NotNull
    public String toString() {
        return "NioSystemFileSystem";
    }
}

