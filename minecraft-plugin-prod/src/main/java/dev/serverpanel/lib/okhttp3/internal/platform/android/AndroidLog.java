/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.util.Log
 */
package dev.serverpanel.lib.okhttp3.internal.platform.android;

import android.util.Log;
import dev.serverpanel.lib.okhttp3.OkHttpClient;
import dev.serverpanel.lib.okhttp3.internal.SuppressSignatureCheck;
import dev.serverpanel.lib.okhttp3.internal.concurrent.TaskRunner;
import dev.serverpanel.lib.okhttp3.internal.http2.Http2;
import dev.serverpanel.lib.okhttp3.internal.platform.android.AndroidLogHandler;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Level;
import java.util.logging.Logger;
import kotlin.Metadata;
import kotlin.collections.MapsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 8, 0}, k=1, xi=48, d1={"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010$\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0010\u0003\n\u0002\b\u0007\b\u00c7\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J/\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\n2\u0006\u0010\u000e\u001a\u00020\u00042\u0006\u0010\u000f\u001a\u00020\n2\b\u0010\u0010\u001a\u0004\u0018\u00010\u0011H\u0000\u00a2\u0006\u0002\b\u0012J\u0006\u0010\u0013\u001a\u00020\fJ\u0018\u0010\u0014\u001a\u00020\f2\u0006\u0010\u0015\u001a\u00020\n2\u0006\u0010\u0016\u001a\u00020\nH\u0002J\u0010\u0010\u0017\u001a\u00020\n2\u0006\u0010\r\u001a\u00020\nH\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010\b\u001a\u000e\u0012\u0004\u0012\u00020\n\u0012\u0004\u0012\u00020\n0\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0018"}, d2={"Ldev/serverpanel/lib/okhttp3/internal/platform/android/AndroidLog;", "", "()V", "MAX_LOG_LENGTH", "", "configuredLoggers", "Ljava/util/concurrent/CopyOnWriteArraySet;", "Ljava/util/logging/Logger;", "knownLoggers", "", "", "androidLog", "", "loggerName", "logLevel", "message", "t", "", "androidLog$okhttp", "enable", "enableLogging", "logger", "tag", "loggerTag", "okhttp"})
@SuppressSignatureCheck
public final class AndroidLog {
    @NotNull
    public static final AndroidLog INSTANCE;
    private static final int MAX_LOG_LENGTH = 4000;
    @NotNull
    private static final CopyOnWriteArraySet<Logger> configuredLoggers;
    @NotNull
    private static final Map<String, String> knownLoggers;

    private AndroidLog() {
    }

    public final void androidLog$okhttp(@NotNull String loggerName, int logLevel, @NotNull String message, @Nullable Throwable t) {
        Intrinsics.checkNotNullParameter(loggerName, "loggerName");
        Intrinsics.checkNotNullParameter(message, "message");
        String tag = this.loggerTag(loggerName);
        if (Log.isLoggable((String)tag, (int)logLevel)) {
            String logMessage = message;
            if (t != null) {
                logMessage = logMessage + '\n' + Log.getStackTraceString((Throwable)t);
            }
            int length = logMessage.length();
            for (int i = 0; i < length; ++i) {
                int end;
                int newline = StringsKt.indexOf$default((CharSequence)logMessage, '\n', i, false, 4, null);
                newline = newline != -1 ? newline : length;
                do {
                    end = Math.min(newline, i + 4000);
                    String string = logMessage.substring(i, end);
                    Intrinsics.checkNotNullExpressionValue(string, "this as java.lang.String\u2026ing(startIndex, endIndex)");
                    Log.println((int)logLevel, (String)tag, (String)string);
                } while ((i = end) < newline);
            }
        }
    }

    private final String loggerTag(String loggerName) {
        String string = knownLoggers.get(loggerName);
        if (string == null) {
            string = StringsKt.take(loggerName, 23);
        }
        return string;
    }

    public final void enable() {
        for (Map.Entry<String, String> entry : knownLoggers.entrySet()) {
            String logger = entry.getKey();
            String tag = entry.getValue();
            this.enableLogging(logger, tag);
        }
    }

    private final void enableLogging(String logger, String tag) {
        Logger logger2 = Logger.getLogger(logger);
        if (configuredLoggers.add(logger2)) {
            logger2.setUseParentHandlers(false);
            logger2.setLevel(Log.isLoggable((String)tag, (int)3) ? Level.FINE : (Log.isLoggable((String)tag, (int)4) ? Level.INFO : Level.WARNING));
            logger2.addHandler(AndroidLogHandler.INSTANCE);
        }
    }

    static {
        String packageName;
        LinkedHashMap linkedHashMap;
        INSTANCE = new AndroidLog();
        configuredLoggers = new CopyOnWriteArraySet();
        LinkedHashMap $this$knownLoggers_u24lambda_u240 = linkedHashMap = new LinkedHashMap();
        boolean bl = false;
        Package package_ = OkHttpClient.class.getPackage();
        String string = packageName = package_ != null ? package_.getName() : null;
        if (packageName != null) {
            ((Map)$this$knownLoggers_u24lambda_u240).put(packageName, "OkHttp");
        }
        Map map = $this$knownLoggers_u24lambda_u240;
        String string2 = OkHttpClient.class.getName();
        Intrinsics.checkNotNullExpressionValue(string2, "OkHttpClient::class.java.name");
        String string3 = string2;
        String string4 = "okhttp.OkHttpClient";
        map.put(string3, string4);
        map = $this$knownLoggers_u24lambda_u240;
        String string5 = Http2.class.getName();
        Intrinsics.checkNotNullExpressionValue(string5, "Http2::class.java.name");
        string3 = string5;
        string4 = "okhttp.Http2";
        map.put(string3, string4);
        map = $this$knownLoggers_u24lambda_u240;
        String string6 = TaskRunner.class.getName();
        Intrinsics.checkNotNullExpressionValue(string6, "TaskRunner::class.java.name");
        string3 = string6;
        string4 = "okhttp.TaskRunner";
        map.put(string3, string4);
        ((Map)$this$knownLoggers_u24lambda_u240).put("dev.serverpanel.lib.okhttp3.mockwebserver.MockWebServer", "okhttp.MockWebServer");
        knownLoggers = MapsKt.toMap(linkedHashMap);
    }
}

