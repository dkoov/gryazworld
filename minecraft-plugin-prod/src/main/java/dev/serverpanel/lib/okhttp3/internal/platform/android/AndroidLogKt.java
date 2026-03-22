/*
 * Decompiled with CFR 0.152.
 */
package dev.serverpanel.lib.okhttp3.internal.platform.android;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import kotlin.Metadata;

@Metadata(mv={1, 8, 0}, k=2, xi=48, d1={"\u0000\u000e\n\u0000\n\u0002\u0010\b\n\u0002\u0018\u0002\n\u0002\b\u0003\"\u0018\u0010\u0000\u001a\u00020\u0001*\u00020\u00028BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b\u0003\u0010\u0004\u00a8\u0006\u0005"}, d2={"androidLevel", "", "Ljava/util/logging/LogRecord;", "getAndroidLevel", "(Ljava/util/logging/LogRecord;)I", "okhttp"})
public final class AndroidLogKt {
    private static final int getAndroidLevel(LogRecord $this$androidLevel) {
        return $this$androidLevel.getLevel().intValue() > Level.INFO.intValue() ? 5 : ($this$androidLevel.getLevel().intValue() == Level.INFO.intValue() ? 4 : 3);
    }

    public static final /* synthetic */ int access$getAndroidLevel(LogRecord $receiver) {
        return AndroidLogKt.getAndroidLevel($receiver);
    }
}

