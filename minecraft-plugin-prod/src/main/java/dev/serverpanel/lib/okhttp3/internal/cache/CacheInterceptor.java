/*
 * Decompiled with CFR 0.152.
 */
package dev.serverpanel.lib.okhttp3.internal.cache;

import dev.serverpanel.lib.okhttp3.Cache;
import dev.serverpanel.lib.okhttp3.Call;
import dev.serverpanel.lib.okhttp3.EventListener;
import dev.serverpanel.lib.okhttp3.Headers;
import dev.serverpanel.lib.okhttp3.Interceptor;
import dev.serverpanel.lib.okhttp3.Protocol;
import dev.serverpanel.lib.okhttp3.Request;
import dev.serverpanel.lib.okhttp3.Response;
import dev.serverpanel.lib.okhttp3.ResponseBody;
import dev.serverpanel.lib.okhttp3.internal.Util;
import dev.serverpanel.lib.okhttp3.internal.cache.CacheRequest;
import dev.serverpanel.lib.okhttp3.internal.cache.CacheStrategy;
import dev.serverpanel.lib.okhttp3.internal.connection.RealCall;
import dev.serverpanel.lib.okhttp3.internal.http.HttpHeaders;
import dev.serverpanel.lib.okhttp3.internal.http.HttpMethod;
import dev.serverpanel.lib.okhttp3.internal.http.RealResponseBody;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import okio.Sink;
import okio.Source;
import okio.Timeout;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 8, 0}, k=1, xi=48, d1={"\u0000(\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u0000 \u000f2\u00020\u0001:\u0001\u000fB\u000f\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\u0002\u0010\u0004J\u001a\u0010\u0007\u001a\u00020\b2\b\u0010\t\u001a\u0004\u0018\u00010\n2\u0006\u0010\u000b\u001a\u00020\bH\u0002J\u0010\u0010\f\u001a\u00020\b2\u0006\u0010\r\u001a\u00020\u000eH\u0016R\u0016\u0010\u0002\u001a\u0004\u0018\u00010\u0003X\u0080\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006\u00a8\u0006\u0010"}, d2={"Ldev/serverpanel/lib/okhttp3/internal/cache/CacheInterceptor;", "Ldev/serverpanel/lib/okhttp3/Interceptor;", "cache", "Ldev/serverpanel/lib/okhttp3/Cache;", "(Lokhttp3/Cache;)V", "getCache$okhttp", "()Lokhttp3/Cache;", "cacheWritingResponse", "Ldev/serverpanel/lib/okhttp3/Response;", "cacheRequest", "Ldev/serverpanel/lib/okhttp3/internal/cache/CacheRequest;", "response", "intercept", "chain", "Ldev/serverpanel/lib/okhttp3/Interceptor$Chain;", "Companion", "okhttp"})
public final class CacheInterceptor
implements Interceptor {
    @NotNull
    public static final Companion Companion = new Companion(null);
    @Nullable
    private final Cache cache;

    public CacheInterceptor(@Nullable Cache cache) {
        this.cache = cache;
    }

    @Nullable
    public final Cache getCache$okhttp() {
        return this.cache;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    @NotNull
    public Response intercept(@NotNull Interceptor.Chain chain) throws IOException {
        Response networkResponse;
        Object listener;
        Response cacheResponse;
        Request networkRequest;
        Call call;
        block23: {
            ResponseBody responseBody;
            Object object;
            Intrinsics.checkNotNullParameter(chain, "chain");
            call = chain.call();
            Cache cache = this.cache;
            Response cacheCandidate = cache != null ? cache.get$okhttp(chain.request()) : null;
            long now = System.currentTimeMillis();
            CacheStrategy strategy = new CacheStrategy.Factory(now, chain.request(), cacheCandidate).compute();
            networkRequest = strategy.getNetworkRequest();
            cacheResponse = strategy.getCacheResponse();
            Cache cache2 = this.cache;
            if (cache2 != null) {
                cache2.trackResponse$okhttp(strategy);
            }
            if ((object = call instanceof RealCall ? (RealCall)call : null) == null || (object = ((RealCall)object).getEventListener$okhttp()) == null) {
                object = listener = EventListener.NONE;
            }
            if (cacheCandidate != null && cacheResponse == null) {
                ResponseBody responseBody2 = cacheCandidate.body();
                if (responseBody2 != null) {
                    Util.closeQuietly(responseBody2);
                }
            }
            if (networkRequest == null && cacheResponse == null) {
                Response response;
                Response it = response = new Response.Builder().request(chain.request()).protocol(Protocol.HTTP_1_1).code(504).message("Unsatisfiable Request (only-if-cached)").body(Util.EMPTY_RESPONSE).sentRequestAtMillis(-1L).receivedResponseAtMillis(System.currentTimeMillis()).build();
                boolean bl = false;
                ((EventListener)listener).satisfactionFailure(call, it);
                return response;
            }
            if (networkRequest == null) {
                Response response;
                Response response2 = cacheResponse;
                Intrinsics.checkNotNull(response2);
                Response it = response = response2.newBuilder().cacheResponse(CacheInterceptor.Companion.stripBody(cacheResponse)).build();
                boolean bl2 = false;
                ((EventListener)listener).cacheHit(call, it);
                return response;
            }
            if (cacheResponse != null) {
                ((EventListener)listener).cacheConditionalHit(call, cacheResponse);
            } else if (this.cache != null) {
                ((EventListener)listener).cacheMiss(call);
            }
            networkResponse = null;
            try {
                networkResponse = chain.proceed(networkRequest);
                if (networkResponse != null || cacheCandidate == null) break block23;
                responseBody = cacheCandidate.body();
            }
            catch (Throwable it) {
                if (cacheCandidate != null) {
                    ResponseBody responseBody3 = cacheCandidate.body();
                    if (responseBody3 != null) {
                        Util.closeQuietly(responseBody3);
                    }
                }
                throw it;
            }
            if (responseBody != null) {
                Util.closeQuietly(responseBody);
            }
        }
        if (cacheResponse != null) {
            Response response = networkResponse;
            boolean bl = response != null ? response.code() == 304 : false;
            if (bl) {
                Response bl2;
                Response response3 = cacheResponse.newBuilder().headers(CacheInterceptor.Companion.combine(cacheResponse.headers(), networkResponse.headers())).sentRequestAtMillis(networkResponse.sentRequestAtMillis()).receivedResponseAtMillis(networkResponse.receivedResponseAtMillis()).cacheResponse(CacheInterceptor.Companion.stripBody(cacheResponse)).networkResponse(CacheInterceptor.Companion.stripBody(networkResponse)).build();
                ResponseBody responseBody = networkResponse.body();
                Intrinsics.checkNotNull(responseBody);
                responseBody.close();
                Cache cache = this.cache;
                Intrinsics.checkNotNull(cache);
                cache.trackConditionalCacheHit$okhttp();
                this.cache.update$okhttp(cacheResponse, response3);
                Response it = bl2 = response3;
                boolean bl3 = false;
                ((EventListener)listener).cacheHit(call, it);
                return bl2;
            }
            ResponseBody responseBody = cacheResponse.body();
            if (responseBody != null) {
                Util.closeQuietly(responseBody);
            }
        }
        Response response = networkResponse;
        Intrinsics.checkNotNull(response);
        Response response4 = response.newBuilder().cacheResponse(CacheInterceptor.Companion.stripBody(cacheResponse)).networkResponse(CacheInterceptor.Companion.stripBody(networkResponse)).build();
        if (this.cache != null) {
            if (HttpHeaders.promisesBody(response4) && CacheStrategy.Companion.isCacheable(response4, networkRequest)) {
                Response response5;
                CacheRequest cacheRequest = this.cache.put$okhttp(response4);
                Response it = response5 = this.cacheWritingResponse(cacheRequest, response4);
                boolean bl = false;
                if (cacheResponse != null) {
                    ((EventListener)listener).cacheMiss(call);
                }
                return response5;
            }
            if (HttpMethod.INSTANCE.invalidatesCache(networkRequest.method())) {
                try {
                    this.cache.remove$okhttp(networkRequest);
                }
                catch (IOException iOException) {
                    // empty catch block
                }
            }
        }
        return response4;
    }

    private final Response cacheWritingResponse(CacheRequest cacheRequest, Response response) throws IOException {
        if (cacheRequest == null) {
            return response;
        }
        Sink cacheBodyUnbuffered = cacheRequest.body();
        ResponseBody responseBody = response.body();
        Intrinsics.checkNotNull(responseBody);
        BufferedSource source2 = responseBody.source();
        BufferedSink cacheBody = Okio.buffer(cacheBodyUnbuffered);
        Source cacheWritingSource2 = new Source(source2, cacheRequest, cacheBody){
            private boolean cacheRequestClosed;
            final /* synthetic */ BufferedSource $source;
            final /* synthetic */ CacheRequest $cacheRequest;
            final /* synthetic */ BufferedSink $cacheBody;
            {
                this.$source = $source;
                this.$cacheRequest = $cacheRequest;
                this.$cacheBody = $cacheBody;
            }

            public long read(@NotNull Buffer sink2, long byteCount) throws IOException {
                Intrinsics.checkNotNullParameter(sink2, "sink");
                long bytesRead = 0L;
                try {
                    bytesRead = this.$source.read(sink2, byteCount);
                }
                catch (IOException e) {
                    if (!this.cacheRequestClosed) {
                        this.cacheRequestClosed = true;
                        this.$cacheRequest.abort();
                    }
                    throw e;
                }
                if (bytesRead == -1L) {
                    if (!this.cacheRequestClosed) {
                        this.cacheRequestClosed = true;
                        this.$cacheBody.close();
                    }
                    return -1L;
                }
                sink2.copyTo(this.$cacheBody.getBuffer(), sink2.size() - bytesRead, bytesRead);
                this.$cacheBody.emitCompleteSegments();
                return bytesRead;
            }

            @NotNull
            public Timeout timeout() {
                return this.$source.timeout();
            }

            public void close() throws IOException {
                if (!this.cacheRequestClosed && !Util.discard(this, 100, TimeUnit.MILLISECONDS)) {
                    this.cacheRequestClosed = true;
                    this.$cacheRequest.abort();
                }
                this.$source.close();
            }
        };
        String contentType = Response.header$default(response, "Content-Type", null, 2, null);
        long contentLength = response.body().contentLength();
        return response.newBuilder().body(new RealResponseBody(contentType, contentLength, Okio.buffer(cacheWritingSource2))).build();
    }

    @Metadata(mv={1, 8, 0}, k=1, xi=48, d1={"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0018\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0006\u001a\u00020\u0004H\u0002J\u0010\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0002J\u0010\u0010\u000b\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0002J\u0014\u0010\f\u001a\u0004\u0018\u00010\r2\b\u0010\u000e\u001a\u0004\u0018\u00010\rH\u0002\u00a8\u0006\u000f"}, d2={"Ldev/serverpanel/lib/okhttp3/internal/cache/CacheInterceptor$Companion;", "", "()V", "combine", "Ldev/serverpanel/lib/okhttp3/Headers;", "cachedHeaders", "networkHeaders", "isContentSpecificHeader", "", "fieldName", "", "isEndToEnd", "stripBody", "Ldev/serverpanel/lib/okhttp3/Response;", "response", "okhttp"})
    public static final class Companion {
        private Companion() {
        }

        private final Response stripBody(Response response) {
            Response response2 = response;
            return (response2 != null ? response2.body() : null) != null ? response.newBuilder().body(null).build() : response;
        }

        private final Headers combine(Headers cachedHeaders, Headers networkHeaders) {
            String fieldName;
            int index;
            Headers.Builder result = new Headers.Builder();
            int n = cachedHeaders.size();
            for (index = 0; index < n; ++index) {
                fieldName = cachedHeaders.name(index);
                String value = cachedHeaders.value(index);
                if (StringsKt.equals("Warning", fieldName, true) && StringsKt.startsWith$default(value, "1", false, 2, null) || !this.isContentSpecificHeader(fieldName) && this.isEndToEnd(fieldName) && networkHeaders.get(fieldName) != null) continue;
                result.addLenient$okhttp(fieldName, value);
            }
            n = networkHeaders.size();
            for (index = 0; index < n; ++index) {
                fieldName = networkHeaders.name(index);
                if (this.isContentSpecificHeader(fieldName) || !this.isEndToEnd(fieldName)) continue;
                result.addLenient$okhttp(fieldName, networkHeaders.value(index));
            }
            return result.build();
        }

        private final boolean isEndToEnd(String fieldName) {
            return !StringsKt.equals("Connection", fieldName, true) && !StringsKt.equals("Keep-Alive", fieldName, true) && !StringsKt.equals("Proxy-Authenticate", fieldName, true) && !StringsKt.equals("Proxy-Authorization", fieldName, true) && !StringsKt.equals("TE", fieldName, true) && !StringsKt.equals("Trailers", fieldName, true) && !StringsKt.equals("Transfer-Encoding", fieldName, true) && !StringsKt.equals("Upgrade", fieldName, true);
        }

        private final boolean isContentSpecificHeader(String fieldName) {
            return StringsKt.equals("Content-Length", fieldName, true) || StringsKt.equals("Content-Encoding", fieldName, true) || StringsKt.equals("Content-Type", fieldName, true);
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }
}

