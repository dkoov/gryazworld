/*
 * Decompiled with CFR 0.152.
 */
package okio;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.OpenOption;
import java.security.MessageDigest;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import kotlin.Metadata;
import kotlin.jvm.JvmName;
import kotlin.jvm.JvmOverloads;
import kotlin.jvm.functions.Function1;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.CipherSink;
import okio.CipherSource;
import okio.FileSystem;
import okio.HashingSink;
import okio.HashingSource;
import okio.Okio__JvmOkioKt;
import okio.Okio__OkioKt;
import okio.Path;
import okio.Sink;
import okio.Source;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={1, 9, 0}, k=4, xi=48, d1={"okio/Okio__JvmOkioKt", "okio/Okio__OkioKt"})
public final class Okio {
    @NotNull
    public static final Sink sink(@NotNull OutputStream $this$sink) {
        return Okio__JvmOkioKt.sink($this$sink);
    }

    @NotNull
    public static final Source source(@NotNull InputStream $this$source) {
        return Okio__JvmOkioKt.source($this$source);
    }

    @NotNull
    public static final Sink sink(@NotNull Socket $this$sink) throws IOException {
        return Okio__JvmOkioKt.sink($this$sink);
    }

    @NotNull
    public static final Source source(@NotNull Socket $this$source) throws IOException {
        return Okio__JvmOkioKt.source($this$source);
    }

    @JvmOverloads
    @NotNull
    public static final Sink sink(@NotNull File $this$sink, boolean append) throws FileNotFoundException {
        return Okio__JvmOkioKt.sink($this$sink, append);
    }

    public static /* synthetic */ Sink sink$default(File file, boolean bl, int n, Object object) throws FileNotFoundException {
        return Okio__JvmOkioKt.sink$default(file, bl, n, object);
    }

    @NotNull
    public static final Sink appendingSink(@NotNull File $this$appendingSink) throws FileNotFoundException {
        return Okio__JvmOkioKt.appendingSink($this$appendingSink);
    }

    @NotNull
    public static final Source source(@NotNull File $this$source) throws FileNotFoundException {
        return Okio__JvmOkioKt.source($this$source);
    }

    @NotNull
    public static final Sink sink(@NotNull java.nio.file.Path $this$sink, OpenOption ... options) throws IOException {
        return Okio__JvmOkioKt.sink($this$sink, options);
    }

    @NotNull
    public static final Source source(@NotNull java.nio.file.Path $this$source, OpenOption ... options) throws IOException {
        return Okio__JvmOkioKt.source($this$source, options);
    }

    @NotNull
    public static final CipherSink cipherSink(@NotNull Sink $this$cipherSink, @NotNull Cipher cipher) {
        return Okio__JvmOkioKt.cipherSink($this$cipherSink, cipher);
    }

    @NotNull
    public static final CipherSource cipherSource(@NotNull Source $this$cipherSource, @NotNull Cipher cipher) {
        return Okio__JvmOkioKt.cipherSource($this$cipherSource, cipher);
    }

    @NotNull
    public static final HashingSink hashingSink(@NotNull Sink $this$hashingSink, @NotNull Mac mac) {
        return Okio__JvmOkioKt.hashingSink($this$hashingSink, mac);
    }

    @NotNull
    public static final HashingSource hashingSource(@NotNull Source $this$hashingSource, @NotNull Mac mac) {
        return Okio__JvmOkioKt.hashingSource($this$hashingSource, mac);
    }

    @NotNull
    public static final HashingSink hashingSink(@NotNull Sink $this$hashingSink, @NotNull MessageDigest digest) {
        return Okio__JvmOkioKt.hashingSink($this$hashingSink, digest);
    }

    @NotNull
    public static final HashingSource hashingSource(@NotNull Source $this$hashingSource, @NotNull MessageDigest digest) {
        return Okio__JvmOkioKt.hashingSource($this$hashingSource, digest);
    }

    @NotNull
    public static final FileSystem openZip(@NotNull FileSystem $this$openZip, @NotNull Path zipPath) throws IOException {
        return Okio__JvmOkioKt.openZip($this$openZip, zipPath);
    }

    @NotNull
    public static final FileSystem asResourceFileSystem(@NotNull ClassLoader $this$asResourceFileSystem) {
        return Okio__JvmOkioKt.asResourceFileSystem($this$asResourceFileSystem);
    }

    public static final boolean isAndroidGetsocknameError(@NotNull AssertionError $this$isAndroidGetsocknameError) {
        return Okio__JvmOkioKt.isAndroidGetsocknameError($this$isAndroidGetsocknameError);
    }

    @JvmOverloads
    @NotNull
    public static final Sink sink(@NotNull File $this$sink) throws FileNotFoundException {
        return Okio__JvmOkioKt.sink($this$sink);
    }

    @NotNull
    public static final BufferedSource buffer(@NotNull Source $this$buffer) {
        return Okio__OkioKt.buffer($this$buffer);
    }

    @NotNull
    public static final BufferedSink buffer(@NotNull Sink $this$buffer) {
        return Okio__OkioKt.buffer($this$buffer);
    }

    @JvmName(name="blackhole")
    @NotNull
    public static final Sink blackhole() {
        return Okio__OkioKt.blackhole();
    }

    public static final <T extends Closeable, R> R use(T $this$use, @NotNull Function1<? super T, ? extends R> block) {
        return Okio__OkioKt.use($this$use, block);
    }
}

