/*
 * Decompiled with CFR 0.152.
 */
package dev.serverpanel.lib.okhttp3.internal.platform.android;

import dev.serverpanel.lib.okhttp3.Protocol;
import dev.serverpanel.lib.okhttp3.internal.platform.AndroidPlatform;
import dev.serverpanel.lib.okhttp3.internal.platform.Platform;
import dev.serverpanel.lib.okhttp3.internal.platform.android.DeferredSocketAdapter;
import dev.serverpanel.lib.okhttp3.internal.platform.android.SocketAdapter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.Charsets;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 8, 0}, k=1, xi=48, d1={"\u0000@\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0003\b\u0016\u0018\u0000 \u00182\u00020\u0001:\u0001\u0018B\u0015\u0012\u000e\u0010\u0002\u001a\n\u0012\u0006\b\u0000\u0012\u00020\u00040\u0003\u00a2\u0006\u0002\u0010\u0005J(\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u00042\b\u0010\u000f\u001a\u0004\u0018\u00010\u00102\f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00130\u0012H\u0016J\u0012\u0010\u0014\u001a\u0004\u0018\u00010\u00102\u0006\u0010\u000e\u001a\u00020\u0004H\u0016J\b\u0010\u0015\u001a\u00020\u0016H\u0016J\u0010\u0010\u0017\u001a\u00020\u00162\u0006\u0010\u000e\u001a\u00020\u0004H\u0016R\u0016\u0010\u0006\u001a\n \b*\u0004\u0018\u00010\u00070\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\t\u001a\n \b*\u0004\u0018\u00010\u00070\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\n\u001a\n \b*\u0004\u0018\u00010\u00070\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0002\u001a\n\u0012\u0006\b\u0000\u0012\u00020\u00040\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0019"}, d2={"Ldev/serverpanel/lib/okhttp3/internal/platform/android/AndroidSocketAdapter;", "Ldev/serverpanel/lib/okhttp3/internal/platform/android/SocketAdapter;", "sslSocketClass", "Ljava/lang/Class;", "Ljavax/net/ssl/SSLSocket;", "(Ljava/lang/Class;)V", "getAlpnSelectedProtocol", "Ljava/lang/reflect/Method;", "kotlin.jvm.PlatformType", "setAlpnProtocols", "setHostname", "setUseSessionTickets", "configureTlsExtensions", "", "sslSocket", "hostname", "", "protocols", "", "Ldev/serverpanel/lib/okhttp3/Protocol;", "getSelectedProtocol", "isSupported", "", "matchesSocket", "Companion", "okhttp"})
public class AndroidSocketAdapter
implements SocketAdapter {
    @NotNull
    public static final Companion Companion = new Companion(null);
    @NotNull
    private final Class<? super SSLSocket> sslSocketClass;
    @NotNull
    private final Method setUseSessionTickets;
    private final Method setHostname;
    private final Method getAlpnSelectedProtocol;
    private final Method setAlpnProtocols;
    @NotNull
    private static final DeferredSocketAdapter.Factory playProviderFactory = Companion.factory("com.google.android.gms.org.conscrypt");

    public AndroidSocketAdapter(@NotNull Class<? super SSLSocket> sslSocketClass) {
        Intrinsics.checkNotNullParameter(sslSocketClass, "sslSocketClass");
        this.sslSocketClass = sslSocketClass;
        Class[] classArray = new Class[]{Boolean.TYPE};
        Method method = this.sslSocketClass.getDeclaredMethod("setUseSessionTickets", classArray);
        Intrinsics.checkNotNullExpressionValue(method, "sslSocketClass.getDeclar\u2026:class.javaPrimitiveType)");
        this.setUseSessionTickets = method;
        Class[] classArray2 = new Class[]{String.class};
        this.setHostname = this.sslSocketClass.getMethod("setHostname", classArray2);
        this.getAlpnSelectedProtocol = this.sslSocketClass.getMethod("getAlpnSelectedProtocol", new Class[0]);
        classArray2 = new Class[]{byte[].class};
        this.setAlpnProtocols = this.sslSocketClass.getMethod("setAlpnProtocols", classArray2);
    }

    @Override
    public boolean isSupported() {
        return AndroidPlatform.Companion.isSupported();
    }

    @Override
    public boolean matchesSocket(@NotNull SSLSocket sslSocket) {
        Intrinsics.checkNotNullParameter(sslSocket, "sslSocket");
        return this.sslSocketClass.isInstance(sslSocket);
    }

    @Override
    public void configureTlsExtensions(@NotNull SSLSocket sslSocket, @Nullable String hostname, @NotNull List<? extends Protocol> protocols) {
        Intrinsics.checkNotNullParameter(sslSocket, "sslSocket");
        Intrinsics.checkNotNullParameter(protocols, "protocols");
        if (this.matchesSocket(sslSocket)) {
            try {
                Object[] objectArray = new Object[]{true};
                this.setUseSessionTickets.invoke((Object)sslSocket, objectArray);
                if (hostname != null) {
                    objectArray = new Object[]{hostname};
                    this.setHostname.invoke((Object)sslSocket, objectArray);
                }
                objectArray = new Object[]{Platform.Companion.concatLengthPrefixed(protocols)};
                this.setAlpnProtocols.invoke((Object)sslSocket, objectArray);
            }
            catch (IllegalAccessException e) {
                throw new AssertionError((Object)e);
            }
            catch (InvocationTargetException e) {
                throw new AssertionError((Object)e);
            }
        }
    }

    @Override
    @Nullable
    public String getSelectedProtocol(@NotNull SSLSocket sslSocket) {
        String string;
        Intrinsics.checkNotNullParameter(sslSocket, "sslSocket");
        if (!this.matchesSocket(sslSocket)) {
            return null;
        }
        try {
            byte[] alpnResult = (byte[])this.getAlpnSelectedProtocol.invoke((Object)sslSocket, new Object[0]);
            string = alpnResult != null ? new String(alpnResult, Charsets.UTF_8) : null;
        }
        catch (IllegalAccessException e) {
            throw new AssertionError((Object)e);
        }
        catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (!(cause instanceof NullPointerException) || !Intrinsics.areEqual(((NullPointerException)cause).getMessage(), "ssl == null")) {
                throw new AssertionError((Object)e);
            }
            string = null;
        }
        return string;
    }

    @Override
    @Nullable
    public X509TrustManager trustManager(@NotNull SSLSocketFactory sslSocketFactory) {
        return SocketAdapter.DefaultImpls.trustManager(this, sslSocketFactory);
    }

    @Override
    public boolean matchesSocketFactory(@NotNull SSLSocketFactory sslSocketFactory) {
        return SocketAdapter.DefaultImpls.matchesSocketFactory(this, sslSocketFactory);
    }

    @Metadata(mv={1, 8, 0}, k=1, xi=48, d1={"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0018\u0010\u0007\u001a\u00020\b2\u000e\u0010\t\u001a\n\u0012\u0006\b\u0000\u0012\u00020\u000b0\nH\u0002J\u000e\u0010\f\u001a\u00020\u00042\u0006\u0010\r\u001a\u00020\u000eR\u0011\u0010\u0003\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006\u00a8\u0006\u000f"}, d2={"Ldev/serverpanel/lib/okhttp3/internal/platform/android/AndroidSocketAdapter$Companion;", "", "()V", "playProviderFactory", "Ldev/serverpanel/lib/okhttp3/internal/platform/android/DeferredSocketAdapter$Factory;", "getPlayProviderFactory", "()Lokhttp3/internal/platform/android/DeferredSocketAdapter$Factory;", "build", "Ldev/serverpanel/lib/okhttp3/internal/platform/android/AndroidSocketAdapter;", "actualSSLSocketClass", "Ljava/lang/Class;", "Ljavax/net/ssl/SSLSocket;", "factory", "packageName", "", "okhttp"})
    public static final class Companion {
        private Companion() {
        }

        @NotNull
        public final DeferredSocketAdapter.Factory getPlayProviderFactory() {
            return playProviderFactory;
        }

        private final AndroidSocketAdapter build(Class<? super SSLSocket> actualSSLSocketClass) {
            Class<? super SSLSocket> possibleClass = actualSSLSocketClass;
            while (possibleClass != null && !Intrinsics.areEqual(possibleClass.getSimpleName(), "OpenSSLSocketImpl")) {
                if ((possibleClass = possibleClass.getSuperclass()) == null) {
                    throw new AssertionError((Object)("No OpenSSLSocketImpl superclass of socket of type " + actualSSLSocketClass));
                }
            }
            Class<? super SSLSocket> clazz = possibleClass;
            Intrinsics.checkNotNull(clazz);
            return new AndroidSocketAdapter(clazz);
        }

        @NotNull
        public final DeferredSocketAdapter.Factory factory(@NotNull String packageName) {
            Intrinsics.checkNotNullParameter(packageName, "packageName");
            return new DeferredSocketAdapter.Factory(packageName){
                final /* synthetic */ String $packageName;
                {
                    this.$packageName = $packageName;
                }

                public boolean matchesSocket(@NotNull SSLSocket sslSocket) {
                    Intrinsics.checkNotNullParameter(sslSocket, "sslSocket");
                    String string = sslSocket.getClass().getName();
                    Intrinsics.checkNotNullExpressionValue(string, "sslSocket.javaClass.name");
                    return StringsKt.startsWith$default(string, this.$packageName + '.', false, 2, null);
                }

                @NotNull
                public SocketAdapter create(@NotNull SSLSocket sslSocket) {
                    Intrinsics.checkNotNullParameter(sslSocket, "sslSocket");
                    return dev.serverpanel.lib.okhttp3.internal.platform.android.AndroidSocketAdapter$Companion.access$build(AndroidSocketAdapter.Companion, sslSocket.getClass());
                }
            };
        }

        public static final /* synthetic */ AndroidSocketAdapter access$build(Companion $this, Class actualSSLSocketClass) {
            return $this.build(actualSSLSocketClass);
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }
}

