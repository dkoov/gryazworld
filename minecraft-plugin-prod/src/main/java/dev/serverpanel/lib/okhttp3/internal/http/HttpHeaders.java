/*
 * Decompiled with CFR 0.152.
 */
package dev.serverpanel.lib.okhttp3.internal.http;

import dev.serverpanel.lib.okhttp3.Challenge;
import dev.serverpanel.lib.okhttp3.Cookie;
import dev.serverpanel.lib.okhttp3.CookieJar;
import dev.serverpanel.lib.okhttp3.Headers;
import dev.serverpanel.lib.okhttp3.HttpUrl;
import dev.serverpanel.lib.okhttp3.Response;
import dev.serverpanel.lib.okhttp3.internal.Util;
import dev.serverpanel.lib.okhttp3.internal.platform.Platform;
import java.io.EOFException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import kotlin.Deprecated;
import kotlin.DeprecationLevel;
import kotlin.Metadata;
import kotlin.ReplaceWith;
import kotlin.collections.MapsKt;
import kotlin.jvm.JvmName;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import okio.Buffer;
import okio.ByteString;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={1, 8, 0}, k=2, xi=48, d1={"\u0000R\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010!\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0005\n\u0000\u001a\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0007\u001a\u0018\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\b*\u00020\n2\u0006\u0010\u000b\u001a\u00020\f\u001a\n\u0010\r\u001a\u00020\u0004*\u00020\u0006\u001a\u001a\u0010\u000e\u001a\u00020\u000f*\u00020\u00102\f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\t0\u0012H\u0002\u001a\u000e\u0010\u0013\u001a\u0004\u0018\u00010\f*\u00020\u0010H\u0002\u001a\u000e\u0010\u0014\u001a\u0004\u0018\u00010\f*\u00020\u0010H\u0002\u001a\u001a\u0010\u0015\u001a\u00020\u000f*\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\n\u001a\f\u0010\u001a\u001a\u00020\u0004*\u00020\u0010H\u0002\u001a\u0014\u0010\u001b\u001a\u00020\u0004*\u00020\u00102\u0006\u0010\u001c\u001a\u00020\u001dH\u0002\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0082\u0004\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\u0002\u001a\u00020\u0001X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001e"}, d2={"QUOTED_STRING_DELIMITERS", "Lokio/ByteString;", "TOKEN_DELIMITERS", "hasBody", "", "response", "Ldev/serverpanel/lib/okhttp3/Response;", "parseChallenges", "", "Ldev/serverpanel/lib/okhttp3/Challenge;", "Ldev/serverpanel/lib/okhttp3/Headers;", "headerName", "", "promisesBody", "readChallengeHeader", "", "Lokio/Buffer;", "result", "", "readQuotedString", "readToken", "receiveHeaders", "Ldev/serverpanel/lib/okhttp3/CookieJar;", "url", "Ldev/serverpanel/lib/okhttp3/HttpUrl;", "headers", "skipCommasAndWhitespace", "startsWith", "prefix", "", "okhttp"})
@JvmName(name="HttpHeaders")
public final class HttpHeaders {
    @NotNull
    private static final ByteString QUOTED_STRING_DELIMITERS = ByteString.Companion.encodeUtf8("\"\\");
    @NotNull
    private static final ByteString TOKEN_DELIMITERS = ByteString.Companion.encodeUtf8("\t ,=");

    @NotNull
    public static final List<Challenge> parseChallenges(@NotNull Headers $this$parseChallenges, @NotNull String headerName) {
        Intrinsics.checkNotNullParameter($this$parseChallenges, "<this>");
        Intrinsics.checkNotNullParameter(headerName, "headerName");
        List result = new ArrayList();
        int n = $this$parseChallenges.size();
        for (int h = 0; h < n; ++h) {
            if (!StringsKt.equals(headerName, $this$parseChallenges.name(h), true)) continue;
            Buffer header = new Buffer().writeUtf8($this$parseChallenges.value(h));
            try {
                HttpHeaders.readChallengeHeader(header, result);
                continue;
            }
            catch (EOFException e) {
                Platform.Companion.get().log("Unable to parse challenge", 5, e);
            }
        }
        return result;
    }

    private static final void readChallengeHeader(Buffer $this$readChallengeHeader, List<Challenge> result) throws EOFException {
        String peek = null;
        while (true) {
            Map parameters;
            String schemeName;
            block11: {
                if (peek == null) {
                    HttpHeaders.skipCommasAndWhitespace($this$readChallengeHeader);
                    peek = HttpHeaders.readToken($this$readChallengeHeader);
                    if (peek == null) {
                        return;
                    }
                }
                schemeName = peek;
                boolean commaPrefixed = HttpHeaders.skipCommasAndWhitespace($this$readChallengeHeader);
                peek = HttpHeaders.readToken($this$readChallengeHeader);
                if (peek == null) {
                    if (!$this$readChallengeHeader.exhausted()) {
                        return;
                    }
                    result.add(new Challenge(schemeName, MapsKt.emptyMap()));
                    return;
                }
                int eqCount = Util.skipAll($this$readChallengeHeader, (byte)61);
                boolean commaSuffixed = HttpHeaders.skipCommasAndWhitespace($this$readChallengeHeader);
                if (!commaPrefixed && (commaSuffixed || $this$readChallengeHeader.exhausted())) {
                    Map<Object, String> map = Collections.singletonMap(null, peek + StringsKt.repeat("=", eqCount));
                    Intrinsics.checkNotNullExpressionValue(map, "singletonMap<String, Str\u2026ek + \"=\".repeat(eqCount))");
                    result.add(new Challenge(schemeName, map));
                    peek = null;
                    continue;
                }
                parameters = new LinkedHashMap();
                eqCount += Util.skipAll($this$readChallengeHeader, (byte)61);
                do {
                    String parameterValue;
                    if (peek == null) {
                        peek = HttpHeaders.readToken($this$readChallengeHeader);
                        if (HttpHeaders.skipCommasAndWhitespace($this$readChallengeHeader)) break block11;
                        eqCount = Util.skipAll($this$readChallengeHeader, (byte)61);
                    }
                    if (eqCount == 0) break block11;
                    if (eqCount > 1) {
                        return;
                    }
                    if (HttpHeaders.skipCommasAndWhitespace($this$readChallengeHeader)) {
                        return;
                    }
                    if ((HttpHeaders.startsWith($this$readChallengeHeader, (byte)34) ? HttpHeaders.readQuotedString($this$readChallengeHeader) : HttpHeaders.readToken($this$readChallengeHeader)) == null) {
                        return;
                    }
                    parameterValue = parameterValue;
                    String replaced = parameters.put(peek, parameterValue);
                    peek = null;
                    if (replaced == null) continue;
                    return;
                } while (HttpHeaders.skipCommasAndWhitespace($this$readChallengeHeader) || $this$readChallengeHeader.exhausted());
                return;
            }
            result.add(new Challenge(schemeName, parameters));
        }
    }

    private static final boolean skipCommasAndWhitespace(Buffer $this$skipCommasAndWhitespace) {
        boolean commaFound = false;
        while (!$this$skipCommasAndWhitespace.exhausted()) {
            byte by = $this$skipCommasAndWhitespace.getByte(0L);
            if (by == 44) {
                $this$skipCommasAndWhitespace.readByte();
                commaFound = true;
                continue;
            }
            if (!(by == 32 ? true : by == 9)) break;
            $this$skipCommasAndWhitespace.readByte();
        }
        return commaFound;
    }

    private static final boolean startsWith(Buffer $this$startsWith, byte prefix) {
        return !$this$startsWith.exhausted() && $this$startsWith.getByte(0L) == prefix;
    }

    private static final String readQuotedString(Buffer $this$readQuotedString) throws EOFException {
        if (!($this$readQuotedString.readByte() == 34)) {
            String string = "Failed requirement.";
            throw new IllegalArgumentException(string.toString());
        }
        Buffer result = new Buffer();
        long i;
        while ((i = $this$readQuotedString.indexOfElement(QUOTED_STRING_DELIMITERS)) != -1L) {
            if ($this$readQuotedString.getByte(i) == 34) {
                result.write($this$readQuotedString, i);
                $this$readQuotedString.readByte();
                return result.readUtf8();
            }
            if ($this$readQuotedString.size() == i + 1L) {
                return null;
            }
            result.write($this$readQuotedString, i);
            $this$readQuotedString.readByte();
            result.write($this$readQuotedString, 1L);
        }
        return null;
    }

    private static final String readToken(Buffer $this$readToken) {
        long tokenSize = $this$readToken.indexOfElement(TOKEN_DELIMITERS);
        if (tokenSize == -1L) {
            tokenSize = $this$readToken.size();
        }
        return tokenSize != 0L ? $this$readToken.readUtf8(tokenSize) : null;
    }

    public static final void receiveHeaders(@NotNull CookieJar $this$receiveHeaders, @NotNull HttpUrl url, @NotNull Headers headers) {
        Intrinsics.checkNotNullParameter($this$receiveHeaders, "<this>");
        Intrinsics.checkNotNullParameter(url, "url");
        Intrinsics.checkNotNullParameter(headers, "headers");
        if ($this$receiveHeaders == CookieJar.NO_COOKIES) {
            return;
        }
        List<Cookie> cookies = Cookie.Companion.parseAll(url, headers);
        if (cookies.isEmpty()) {
            return;
        }
        $this$receiveHeaders.saveFromResponse(url, cookies);
    }

    public static final boolean promisesBody(@NotNull Response $this$promisesBody) {
        Intrinsics.checkNotNullParameter($this$promisesBody, "<this>");
        if (Intrinsics.areEqual($this$promisesBody.request().method(), "HEAD")) {
            return false;
        }
        int responseCode = $this$promisesBody.code();
        if ((responseCode < 100 || responseCode >= 200) && responseCode != 204 && responseCode != 304) {
            return true;
        }
        return Util.headersContentLength($this$promisesBody) != -1L || StringsKt.equals("chunked", Response.header$default($this$promisesBody, "Transfer-Encoding", null, 2, null), true);
    }

    @Deprecated(message="No longer supported", replaceWith=@ReplaceWith(expression="response.promisesBody()", imports={}), level=DeprecationLevel.ERROR)
    public static final boolean hasBody(@NotNull Response response) {
        Intrinsics.checkNotNullParameter(response, "response");
        return HttpHeaders.promisesBody(response);
    }
}

