/*
 * Decompiled with CFR 0.152.
 */
package okio;

import java.io.Closeable;
import kotlin.ExceptionsKt;
import kotlin.Metadata;
import kotlin.jvm.JvmName;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.InlineMarker;
import kotlin.jvm.internal.Intrinsics;
import okio.BlackholeSink;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.RealBufferedSink;
import okio.RealBufferedSource;
import okio.Sink;
import okio.Source;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={1, 9, 0}, k=5, xi=48, d1={"\u0000,\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a\r\u0010\u0000\u001a\u00020\u0001H\u0007\u00a2\u0006\u0002\b\u0002\u001a\n\u0010\u0003\u001a\u00020\u0004*\u00020\u0001\u001a\n\u0010\u0003\u001a\u00020\u0005*\u00020\u0006\u001aA\u0010\u0007\u001a\u0002H\b\"\u0010\b\u0000\u0010\t*\n\u0018\u00010\nj\u0004\u0018\u0001`\u000b\"\u0004\b\u0001\u0010\b*\u0002H\t2\u0012\u0010\f\u001a\u000e\u0012\u0004\u0012\u0002H\t\u0012\u0004\u0012\u0002H\b0\rH\u0086\b\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u000e\u0082\u0002\u0007\n\u0005\b\u009920\u0001\u00a8\u0006\u000f"}, d2={"blackholeSink", "Lokio/Sink;", "blackhole", "buffer", "Lokio/BufferedSink;", "Lokio/BufferedSource;", "Lokio/Source;", "use", "R", "T", "Ljava/io/Closeable;", "Lokio/Closeable;", "block", "Lkotlin/Function1;", "(Ljava/io/Closeable;Lkotlin/jvm/functions/Function1;)Ljava/lang/Object;", "okio"}, xs="okio/Okio")
final class Okio__OkioKt {
    @NotNull
    public static final BufferedSource buffer(@NotNull Source $this$buffer) {
        Intrinsics.checkNotNullParameter($this$buffer, "<this>");
        return new RealBufferedSource($this$buffer);
    }

    @NotNull
    public static final BufferedSink buffer(@NotNull Sink $this$buffer) {
        Intrinsics.checkNotNullParameter($this$buffer, "<this>");
        return new RealBufferedSink($this$buffer);
    }

    @JvmName(name="blackhole")
    @NotNull
    public static final Sink blackhole() {
        return new BlackholeSink();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public static final <T extends Closeable, R> R use(T $this$use, @NotNull Function1<? super T, ? extends R> block) {
        Throwable thrown;
        R result;
        block16: {
            Intrinsics.checkNotNullParameter(block, "block");
            boolean $i$f$use = false;
            result = null;
            thrown = null;
            result = block.invoke($this$use);
            InlineMarker.finallyStart(1);
            try {
                T t = $this$use;
                if (t != null) {
                    t.close();
                }
            }
            catch (Throwable t) {
                thrown = t;
            }
            InlineMarker.finallyEnd(1);
            break block16;
            catch (Throwable t) {
                block15: {
                    try {
                        thrown = t;
                    }
                    catch (Throwable throwable) {
                        InlineMarker.finallyStart(1);
                        try {
                            T t2 = $this$use;
                            if (t2 != null) {
                                t2.close();
                            }
                        }
                        catch (Throwable t3) {
                            thrown = t3;
                        }
                        InlineMarker.finallyEnd(1);
                        throw throwable;
                    }
                    InlineMarker.finallyStart(1);
                    try {
                        T t4 = $this$use;
                        if (t4 != null) {
                            t4.close();
                        }
                    }
                    catch (Throwable t5) {
                        if (thrown == null) {
                            thrown = t5;
                            break block15;
                        }
                        ExceptionsKt.addSuppressed(thrown, t5);
                    }
                }
                InlineMarker.finallyEnd(1);
            }
        }
        Throwable throwable = thrown;
        if (throwable != null) {
            throw throwable;
        }
        R r = result;
        Intrinsics.checkNotNull(r);
        return r;
    }
}

