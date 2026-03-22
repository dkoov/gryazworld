/*
 * Decompiled with CFR 0.152.
 */
package dev.serverpanel.lib.okhttp3.internal.http;

import dev.serverpanel.lib.okhttp3.Cookie;
import dev.serverpanel.lib.okhttp3.CookieJar;
import dev.serverpanel.lib.okhttp3.Headers;
import dev.serverpanel.lib.okhttp3.Interceptor;
import dev.serverpanel.lib.okhttp3.MediaType;
import dev.serverpanel.lib.okhttp3.Request;
import dev.serverpanel.lib.okhttp3.RequestBody;
import dev.serverpanel.lib.okhttp3.Response;
import dev.serverpanel.lib.okhttp3.ResponseBody;
import dev.serverpanel.lib.okhttp3.internal.Util;
import dev.serverpanel.lib.okhttp3.internal.http.HttpHeaders;
import dev.serverpanel.lib.okhttp3.internal.http.RealResponseBody;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import okio.GzipSource;
import okio.Okio;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={1, 8, 0}, k=1, xi=48, d1={"\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0016\u0010\u0005\u001a\u00020\u00062\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\bH\u0002J\u0010\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rH\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000e"}, d2={"Ldev/serverpanel/lib/okhttp3/internal/http/BridgeInterceptor;", "Ldev/serverpanel/lib/okhttp3/Interceptor;", "cookieJar", "Ldev/serverpanel/lib/okhttp3/CookieJar;", "(Lokhttp3/CookieJar;)V", "cookieHeader", "", "cookies", "", "Ldev/serverpanel/lib/okhttp3/Cookie;", "intercept", "Ldev/serverpanel/lib/okhttp3/Response;", "chain", "Ldev/serverpanel/lib/okhttp3/Interceptor$Chain;", "okhttp"})
@SourceDebugExtension(value={"SMAP\nBridgeInterceptor.kt\nKotlin\n*S Kotlin\n*F\n+ 1 BridgeInterceptor.kt\nokhttp3/internal/http/BridgeInterceptor\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,117:1\n1864#2,3:118\n*S KotlinDebug\n*F\n+ 1 BridgeInterceptor.kt\nokhttp3/internal/http/BridgeInterceptor\n*L\n111#1:118,3\n*E\n"})
public final class BridgeInterceptor
implements Interceptor {
    @NotNull
    private final CookieJar cookieJar;

    public BridgeInterceptor(@NotNull CookieJar cookieJar) {
        Intrinsics.checkNotNullParameter(cookieJar, "cookieJar");
        this.cookieJar = cookieJar;
    }

    @Override
    @NotNull
    public Response intercept(@NotNull Interceptor.Chain chain) throws IOException {
        ResponseBody responseBody;
        List<Cookie> cookies;
        Intrinsics.checkNotNullParameter(chain, "chain");
        Request userRequest = chain.request();
        Request.Builder requestBuilder = userRequest.newBuilder();
        RequestBody body = userRequest.body();
        if (body != null) {
            long contentLength;
            MediaType contentType = body.contentType();
            if (contentType != null) {
                requestBuilder.header("Content-Type", contentType.toString());
            }
            if ((contentLength = body.contentLength()) != -1L) {
                requestBuilder.header("Content-Length", String.valueOf(contentLength));
                requestBuilder.removeHeader("Transfer-Encoding");
            } else {
                requestBuilder.header("Transfer-Encoding", "chunked");
                requestBuilder.removeHeader("Content-Length");
            }
        }
        if (userRequest.header("Host") == null) {
            requestBuilder.header("Host", Util.toHostHeader$default(userRequest.url(), false, 1, null));
        }
        if (userRequest.header("Connection") == null) {
            requestBuilder.header("Connection", "Keep-Alive");
        }
        boolean transparentGzip = false;
        if (userRequest.header("Accept-Encoding") == null && userRequest.header("Range") == null) {
            transparentGzip = true;
            requestBuilder.header("Accept-Encoding", "gzip");
        }
        if (!((Collection)(cookies = this.cookieJar.loadForRequest(userRequest.url()))).isEmpty()) {
            requestBuilder.header("Cookie", this.cookieHeader(cookies));
        }
        if (userRequest.header("User-Agent") == null) {
            requestBuilder.header("User-Agent", "okhttp/4.12.0");
        }
        Response networkResponse = chain.proceed(requestBuilder.build());
        HttpHeaders.receiveHeaders(this.cookieJar, userRequest.url(), networkResponse.headers());
        Response.Builder responseBuilder = networkResponse.newBuilder().request(userRequest);
        if (transparentGzip && StringsKt.equals("gzip", Response.header$default(networkResponse, "Content-Encoding", null, 2, null), true) && HttpHeaders.promisesBody(networkResponse) && (responseBody = networkResponse.body()) != null) {
            GzipSource gzipSource = new GzipSource(responseBody.source());
            Headers strippedHeaders = networkResponse.headers().newBuilder().removeAll("Content-Encoding").removeAll("Content-Length").build();
            responseBuilder.headers(strippedHeaders);
            String contentType = Response.header$default(networkResponse, "Content-Type", null, 2, null);
            responseBuilder.body(new RealResponseBody(contentType, -1L, Okio.buffer(gzipSource)));
        }
        return responseBuilder.build();
    }

    /*
     * WARNING - void declaration
     */
    private final String cookieHeader(List<Cookie> cookies) {
        StringBuilder stringBuilder;
        StringBuilder $this$cookieHeader_u24lambda_u241 = stringBuilder = new StringBuilder();
        boolean bl = false;
        Iterable $this$forEachIndexed$iv = cookies;
        boolean $i$f$forEachIndexed = false;
        int index$iv = 0;
        for (Object item$iv : $this$forEachIndexed$iv) {
            void cookie;
            int n;
            if ((n = index$iv++) < 0) {
                CollectionsKt.throwIndexOverflow();
            }
            Cookie cookie2 = (Cookie)item$iv;
            int index = n;
            boolean bl2 = false;
            if (index > 0) {
                $this$cookieHeader_u24lambda_u241.append("; ");
            }
            $this$cookieHeader_u24lambda_u241.append(cookie.name()).append('=').append(cookie.value());
        }
        String string = stringBuilder.toString();
        Intrinsics.checkNotNullExpressionValue(string, "StringBuilder().apply(builderAction).toString()");
        return string;
    }
}

