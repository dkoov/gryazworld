/*
 * Decompiled with CFR 0.152.
 */
package dev.serverpanel.lib.okhttp3;

import dev.serverpanel.lib.okhttp3.Call;
import dev.serverpanel.lib.okhttp3.Connection;
import dev.serverpanel.lib.okhttp3.Request;
import dev.serverpanel.lib.okhttp3.Response;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import kotlin.Metadata;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 8, 0}, k=1, xi=48, d1={"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00e6\u0080\u0001\u0018\u0000 \u00072\u00020\u0001:\u0002\u0006\u0007J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H&\u00a8\u0006\b"}, d2={"Ldev/serverpanel/lib/okhttp3/Interceptor;", "", "intercept", "Ldev/serverpanel/lib/okhttp3/Response;", "chain", "Ldev/serverpanel/lib/okhttp3/Interceptor$Chain;", "Chain", "Companion", "okhttp"})
public interface Interceptor {
    @NotNull
    public static final Companion Companion = dev.serverpanel.lib.okhttp3.Interceptor$Companion.$$INSTANCE;

    @NotNull
    public Response intercept(@NotNull Chain var1) throws IOException;

    @Metadata(mv={1, 8, 0}, k=1, xi=48, d1={"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\bf\u0018\u00002\u00020\u0001J\b\u0010\u0002\u001a\u00020\u0003H&J\b\u0010\u0004\u001a\u00020\u0005H&J\n\u0010\u0006\u001a\u0004\u0018\u00010\u0007H&J\u0010\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bH&J\b\u0010\f\u001a\u00020\u0005H&J\b\u0010\n\u001a\u00020\u000bH&J\u0018\u0010\r\u001a\u00020\u00002\u0006\u0010\u000e\u001a\u00020\u00052\u0006\u0010\u000f\u001a\u00020\u0010H&J\u0018\u0010\u0011\u001a\u00020\u00002\u0006\u0010\u000e\u001a\u00020\u00052\u0006\u0010\u000f\u001a\u00020\u0010H&J\u0018\u0010\u0012\u001a\u00020\u00002\u0006\u0010\u000e\u001a\u00020\u00052\u0006\u0010\u000f\u001a\u00020\u0010H&J\b\u0010\u0013\u001a\u00020\u0005H&\u00a8\u0006\u0014"}, d2={"Ldev/serverpanel/lib/okhttp3/Interceptor$Chain;", "", "call", "Ldev/serverpanel/lib/okhttp3/Call;", "connectTimeoutMillis", "", "connection", "Ldev/serverpanel/lib/okhttp3/Connection;", "proceed", "Ldev/serverpanel/lib/okhttp3/Response;", "request", "Ldev/serverpanel/lib/okhttp3/Request;", "readTimeoutMillis", "withConnectTimeout", "timeout", "unit", "Ljava/util/concurrent/TimeUnit;", "withReadTimeout", "withWriteTimeout", "writeTimeoutMillis", "okhttp"})
    public static interface Chain {
        @NotNull
        public Request request();

        @NotNull
        public Response proceed(@NotNull Request var1) throws IOException;

        @Nullable
        public Connection connection();

        @NotNull
        public Call call();

        public int connectTimeoutMillis();

        @NotNull
        public Chain withConnectTimeout(int var1, @NotNull TimeUnit var2);

        public int readTimeoutMillis();

        @NotNull
        public Chain withReadTimeout(int var1, @NotNull TimeUnit var2);

        public int writeTimeoutMillis();

        @NotNull
        public Chain withWriteTimeout(int var1, @NotNull TimeUnit var2);
    }

    @Metadata(mv={1, 8, 0}, k=1, xi=48, d1={"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J1\u0010\u0003\u001a\u00020\u00042#\b\u0004\u0010\u0005\u001a\u001d\u0012\u0013\u0012\u00110\u0007\u00a2\u0006\f\b\b\u0012\b\b\t\u0012\u0004\b\b(\n\u0012\u0004\u0012\u00020\u000b0\u0006H\u0086\n\u00f8\u0001\u0000\u0082\u0002\u0007\n\u0005\b\u009920\u0001\u00a8\u0006\f"}, d2={"Ldev/serverpanel/lib/okhttp3/Interceptor$Companion;", "", "()V", "invoke", "Ldev/serverpanel/lib/okhttp3/Interceptor;", "block", "Lkotlin/Function1;", "Ldev/serverpanel/lib/okhttp3/Interceptor$Chain;", "Lkotlin/ParameterName;", "name", "chain", "Ldev/serverpanel/lib/okhttp3/Response;", "okhttp"})
    public static final class Companion {
        static final /* synthetic */ Companion $$INSTANCE;

        private Companion() {
        }

        @NotNull
        public final Interceptor invoke(@NotNull Function1<? super Chain, Response> block) {
            Intrinsics.checkNotNullParameter(block, "block");
            boolean $i$f$invoke = false;
            return new Interceptor(block){
                final /* synthetic */ Function1<Chain, Response> $block;
                {
                    this.$block = $block;
                }

                @NotNull
                public final Response intercept(@NotNull Chain it) {
                    Intrinsics.checkNotNullParameter(it, "it");
                    return this.$block.invoke(it);
                }
            };
        }

        static {
            $$INSTANCE = new Companion();
        }
    }
}

