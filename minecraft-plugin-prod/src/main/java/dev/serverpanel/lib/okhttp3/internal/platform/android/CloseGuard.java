/*
 * Decompiled with CFR 0.152.
 */
package dev.serverpanel.lib.okhttp3.internal.platform.android;

import java.lang.reflect.Method;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 8, 0}, k=1, xi=48, d1={"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\b\u0000\u0018\u0000 \r2\u00020\u0001:\u0001\rB#\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0005\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\u0002\u0010\u0006J\u0010\u0010\u0007\u001a\u0004\u0018\u00010\u00012\u0006\u0010\b\u001a\u00020\tJ\u0010\u0010\n\u001a\u00020\u000b2\b\u0010\f\u001a\u0004\u0018\u00010\u0001R\u0010\u0010\u0002\u001a\u0004\u0018\u00010\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0004\u001a\u0004\u0018\u00010\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0005\u001a\u0004\u0018\u00010\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000e"}, d2={"Ldev/serverpanel/lib/okhttp3/internal/platform/android/CloseGuard;", "", "getMethod", "Ljava/lang/reflect/Method;", "openMethod", "warnIfOpenMethod", "(Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;)V", "createAndOpen", "closer", "", "warnIfOpen", "", "closeGuardInstance", "Companion", "okhttp"})
public final class CloseGuard {
    @NotNull
    public static final Companion Companion = new Companion(null);
    @Nullable
    private final Method getMethod;
    @Nullable
    private final Method openMethod;
    @Nullable
    private final Method warnIfOpenMethod;

    public CloseGuard(@Nullable Method getMethod, @Nullable Method openMethod, @Nullable Method warnIfOpenMethod) {
        this.getMethod = getMethod;
        this.openMethod = openMethod;
        this.warnIfOpenMethod = warnIfOpenMethod;
    }

    @Nullable
    public final Object createAndOpen(@NotNull String closer) {
        Intrinsics.checkNotNullParameter(closer, "closer");
        if (this.getMethod != null) {
            try {
                Object closeGuardInstance = this.getMethod.invoke(null, new Object[0]);
                Method method = this.openMethod;
                Intrinsics.checkNotNull(method);
                Object[] objectArray = new Object[]{closer};
                method.invoke(closeGuardInstance, objectArray);
                return closeGuardInstance;
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return null;
    }

    public final boolean warnIfOpen(@Nullable Object closeGuardInstance) {
        boolean reported = false;
        if (closeGuardInstance != null) {
            try {
                Method method = this.warnIfOpenMethod;
                Intrinsics.checkNotNull(method);
                method.invoke(closeGuardInstance, new Object[0]);
                reported = true;
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return reported;
    }

    @Metadata(mv={1, 8, 0}, k=1, xi=48, d1={"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0006\u0010\u0003\u001a\u00020\u0004\u00a8\u0006\u0005"}, d2={"Ldev/serverpanel/lib/okhttp3/internal/platform/android/CloseGuard$Companion;", "", "()V", "get", "Ldev/serverpanel/lib/okhttp3/internal/platform/android/CloseGuard;", "okhttp"})
    public static final class Companion {
        private Companion() {
        }

        @NotNull
        public final CloseGuard get() {
            Method getMethod = null;
            Method openMethod = null;
            Method warnIfOpenMethod = null;
            try {
                Class<?> closeGuardClass = Class.forName("dalvik.system.CloseGuard");
                getMethod = closeGuardClass.getMethod("get", new Class[0]);
                Class[] classArray = new Class[]{String.class};
                openMethod = closeGuardClass.getMethod("open", classArray);
                warnIfOpenMethod = closeGuardClass.getMethod("warnIfOpen", new Class[0]);
            }
            catch (Exception _) {
                getMethod = null;
                openMethod = null;
                warnIfOpenMethod = null;
            }
            return new CloseGuard(getMethod, openMethod, warnIfOpenMethod);
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }
}

