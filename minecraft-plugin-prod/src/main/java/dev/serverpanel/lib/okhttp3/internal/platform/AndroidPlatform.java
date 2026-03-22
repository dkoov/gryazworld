/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.os.Build$VERSION
 *  android.security.NetworkSecurityPolicy
 */
package dev.serverpanel.lib.okhttp3.internal.platform;

import android.os.Build;
import android.security.NetworkSecurityPolicy;
import dev.serverpanel.lib.okhttp3.Protocol;
import dev.serverpanel.lib.okhttp3.internal.SuppressSignatureCheck;
import dev.serverpanel.lib.okhttp3.internal.platform.Platform;
import dev.serverpanel.lib.okhttp3.internal.platform.android.AndroidCertificateChainCleaner;
import dev.serverpanel.lib.okhttp3.internal.platform.android.AndroidSocketAdapter;
import dev.serverpanel.lib.okhttp3.internal.platform.android.BouncyCastleSocketAdapter;
import dev.serverpanel.lib.okhttp3.internal.platform.android.CloseGuard;
import dev.serverpanel.lib.okhttp3.internal.platform.android.ConscryptSocketAdapter;
import dev.serverpanel.lib.okhttp3.internal.platform.android.DeferredSocketAdapter;
import dev.serverpanel.lib.okhttp3.internal.platform.android.SocketAdapter;
import dev.serverpanel.lib.okhttp3.internal.platform.android.StandardAndroidSocketAdapter;
import dev.serverpanel.lib.okhttp3.internal.tls.CertificateChainCleaner;
import dev.serverpanel.lib.okhttp3.internal.tls.TrustRootIndex;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 8, 0}, k=1, xi=48, d1={"\u0000x\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0007\u0018\u0000 )2\u00020\u0001:\u0002)*B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bH\u0016J\u0010\u0010\f\u001a\u00020\r2\u0006\u0010\n\u001a\u00020\u000bH\u0016J-\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u00112\b\u0010\u0012\u001a\u0004\u0018\u00010\u00132\u0011\u0010\u0014\u001a\r\u0012\t\u0012\u00070\u0015\u00a2\u0006\u0002\b\u00160\u0006H\u0016J \u0010\u0017\u001a\u00020\u000f2\u0006\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\u001dH\u0016J\u0012\u0010\u001e\u001a\u0004\u0018\u00010\u00132\u0006\u0010\u0010\u001a\u00020\u0011H\u0016J\u0012\u0010\u001f\u001a\u0004\u0018\u00010 2\u0006\u0010!\u001a\u00020\u0013H\u0016J\u0010\u0010\"\u001a\u00020#2\u0006\u0010\u0012\u001a\u00020\u0013H\u0016J\u001a\u0010$\u001a\u00020\u000f2\u0006\u0010%\u001a\u00020\u00132\b\u0010&\u001a\u0004\u0018\u00010 H\u0016J\u0012\u0010\n\u001a\u0004\u0018\u00010\u000b2\u0006\u0010'\u001a\u00020(H\u0016R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006+"}, d2={"Ldev/serverpanel/lib/okhttp3/internal/platform/AndroidPlatform;", "Ldev/serverpanel/lib/okhttp3/internal/platform/Platform;", "()V", "closeGuard", "Ldev/serverpanel/lib/okhttp3/internal/platform/android/CloseGuard;", "socketAdapters", "", "Ldev/serverpanel/lib/okhttp3/internal/platform/android/SocketAdapter;", "buildCertificateChainCleaner", "Ldev/serverpanel/lib/okhttp3/internal/tls/CertificateChainCleaner;", "trustManager", "Ljavax/net/ssl/X509TrustManager;", "buildTrustRootIndex", "Ldev/serverpanel/lib/okhttp3/internal/tls/TrustRootIndex;", "configureTlsExtensions", "", "sslSocket", "Ljavax/net/ssl/SSLSocket;", "hostname", "", "protocols", "Ldev/serverpanel/lib/okhttp3/Protocol;", "Lkotlin/jvm/JvmSuppressWildcards;", "connectSocket", "socket", "Ljava/net/Socket;", "address", "Ljava/net/InetSocketAddress;", "connectTimeout", "", "getSelectedProtocol", "getStackTraceForCloseable", "", "closer", "isCleartextTrafficPermitted", "", "logCloseableLeak", "message", "stackTrace", "sslSocketFactory", "Ljavax/net/ssl/SSLSocketFactory;", "Companion", "CustomTrustRootIndex", "okhttp"})
@SuppressSignatureCheck
@SourceDebugExtension(value={"SMAP\nAndroidPlatform.kt\nKotlin\n*S Kotlin\n*F\n+ 1 AndroidPlatform.kt\nokhttp3/internal/platform/AndroidPlatform\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,163:1\n766#2:164\n857#2,2:165\n1#3:167\n*S KotlinDebug\n*F\n+ 1 AndroidPlatform.kt\nokhttp3/internal/platform/AndroidPlatform\n*L\n52#1:164\n52#1:165,2\n*E\n"})
public final class AndroidPlatform
extends Platform {
    @NotNull
    public static final Companion Companion;
    @NotNull
    private final List<SocketAdapter> socketAdapters;
    @NotNull
    private final CloseGuard closeGuard;
    private static final boolean isSupported;

    /*
     * WARNING - void declaration
     */
    public AndroidPlatform() {
        void $this$filterTo$iv$iv;
        void $this$filter$iv;
        Object object = new SocketAdapter[]{StandardAndroidSocketAdapter.Companion.buildIfSupported$default(StandardAndroidSocketAdapter.Companion, null, 1, null), new DeferredSocketAdapter(AndroidSocketAdapter.Companion.getPlayProviderFactory()), new DeferredSocketAdapter(ConscryptSocketAdapter.Companion.getFactory()), new DeferredSocketAdapter(BouncyCastleSocketAdapter.Companion.getFactory())};
        object = CollectionsKt.listOfNotNull(object);
        AndroidPlatform androidPlatform = this;
        boolean $i$f$filter = false;
        void var3_4 = $this$filter$iv;
        Collection destination$iv$iv = new ArrayList();
        boolean $i$f$filterTo = false;
        for (Object element$iv$iv : $this$filterTo$iv$iv) {
            SocketAdapter it = (SocketAdapter)element$iv$iv;
            boolean bl = false;
            if (!it.isSupported()) continue;
            destination$iv$iv.add(element$iv$iv);
        }
        androidPlatform.socketAdapters = (List)destination$iv$iv;
        this.closeGuard = CloseGuard.Companion.get();
    }

    @Override
    public void connectSocket(@NotNull Socket socket, @NotNull InetSocketAddress address, int connectTimeout) throws IOException {
        Intrinsics.checkNotNullParameter(socket, "socket");
        Intrinsics.checkNotNullParameter(address, "address");
        try {
            socket.connect(address, connectTimeout);
        }
        catch (ClassCastException e) {
            if (Build.VERSION.SDK_INT == 26) {
                throw new IOException("Exception in connect", e);
            }
            throw e;
        }
    }

    @Override
    @Nullable
    public X509TrustManager trustManager(@NotNull SSLSocketFactory sslSocketFactory) {
        Object v0;
        block1: {
            Intrinsics.checkNotNullParameter(sslSocketFactory, "sslSocketFactory");
            Iterable iterable = this.socketAdapters;
            for (Object t : iterable) {
                SocketAdapter it = (SocketAdapter)t;
                boolean bl = false;
                if (!it.matchesSocketFactory(sslSocketFactory)) continue;
                v0 = t;
                break block1;
            }
            v0 = null;
        }
        SocketAdapter socketAdapter = v0;
        return socketAdapter != null ? socketAdapter.trustManager(sslSocketFactory) : null;
    }

    @Override
    public void configureTlsExtensions(@NotNull SSLSocket sslSocket, @Nullable String hostname, @NotNull List<Protocol> protocols) {
        Object v0;
        block2: {
            Intrinsics.checkNotNullParameter(sslSocket, "sslSocket");
            Intrinsics.checkNotNullParameter(protocols, "protocols");
            Iterable iterable = this.socketAdapters;
            for (Object t : iterable) {
                SocketAdapter it = (SocketAdapter)t;
                boolean bl = false;
                if (!it.matchesSocket(sslSocket)) continue;
                v0 = t;
                break block2;
            }
            v0 = null;
        }
        SocketAdapter socketAdapter = v0;
        if (socketAdapter != null) {
            socketAdapter.configureTlsExtensions(sslSocket, hostname, protocols);
        }
    }

    @Override
    @Nullable
    public String getSelectedProtocol(@NotNull SSLSocket sslSocket) {
        Object v0;
        block1: {
            Intrinsics.checkNotNullParameter(sslSocket, "sslSocket");
            Iterable iterable = this.socketAdapters;
            for (Object t : iterable) {
                SocketAdapter it = (SocketAdapter)t;
                boolean bl = false;
                if (!it.matchesSocket(sslSocket)) continue;
                v0 = t;
                break block1;
            }
            v0 = null;
        }
        SocketAdapter socketAdapter = v0;
        return socketAdapter != null ? socketAdapter.getSelectedProtocol(sslSocket) : null;
    }

    @Override
    @Nullable
    public Object getStackTraceForCloseable(@NotNull String closer) {
        Intrinsics.checkNotNullParameter(closer, "closer");
        return this.closeGuard.createAndOpen(closer);
    }

    @Override
    public void logCloseableLeak(@NotNull String message, @Nullable Object stackTrace) {
        Intrinsics.checkNotNullParameter(message, "message");
        boolean reported = this.closeGuard.warnIfOpen(stackTrace);
        if (!reported) {
            Platform.log$default(this, message, 5, null, 4, null);
        }
    }

    @Override
    public boolean isCleartextTrafficPermitted(@NotNull String hostname) {
        Intrinsics.checkNotNullParameter(hostname, "hostname");
        return Build.VERSION.SDK_INT >= 24 ? NetworkSecurityPolicy.getInstance().isCleartextTrafficPermitted(hostname) : (Build.VERSION.SDK_INT >= 23 ? NetworkSecurityPolicy.getInstance().isCleartextTrafficPermitted() : true);
    }

    @Override
    @NotNull
    public CertificateChainCleaner buildCertificateChainCleaner(@NotNull X509TrustManager trustManager) {
        Intrinsics.checkNotNullParameter(trustManager, "trustManager");
        AndroidCertificateChainCleaner androidCertificateChainCleaner = AndroidCertificateChainCleaner.Companion.buildIfSupported(trustManager);
        return androidCertificateChainCleaner != null ? (CertificateChainCleaner)androidCertificateChainCleaner : super.buildCertificateChainCleaner(trustManager);
    }

    @Override
    @NotNull
    public TrustRootIndex buildTrustRootIndex(@NotNull X509TrustManager trustManager) {
        TrustRootIndex trustRootIndex;
        Intrinsics.checkNotNullParameter(trustManager, "trustManager");
        try {
            Class[] classArray = new Class[]{X509Certificate.class};
            Method method = trustManager.getClass().getDeclaredMethod("findTrustAnchorByIssuerAndSignature", classArray);
            method.setAccessible(true);
            Intrinsics.checkNotNullExpressionValue(method, "method");
            trustRootIndex = new CustomTrustRootIndex(trustManager, method);
        }
        catch (NoSuchMethodException e) {
            trustRootIndex = super.buildTrustRootIndex(trustManager);
        }
        return trustRootIndex;
    }

    static {
        boolean bl;
        Companion = new Companion(null);
        if (!Platform.Companion.isAndroid()) {
            bl = false;
        } else if (Build.VERSION.SDK_INT >= 30) {
            bl = false;
        } else {
            if (!(Build.VERSION.SDK_INT >= 21)) {
                boolean bl2 = false;
                String string = "Expected Android API level 21+ but was " + Build.VERSION.SDK_INT;
                throw new IllegalStateException(string.toString());
            }
            bl = true;
        }
        isSupported = bl;
    }

    @Metadata(mv={1, 8, 0}, k=1, xi=48, d1={"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007R\u0011\u0010\u0003\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0003\u0010\u0005\u00a8\u0006\b"}, d2={"Ldev/serverpanel/lib/okhttp3/internal/platform/AndroidPlatform$Companion;", "", "()V", "isSupported", "", "()Z", "buildIfSupported", "Ldev/serverpanel/lib/okhttp3/internal/platform/Platform;", "okhttp"})
    public static final class Companion {
        private Companion() {
        }

        public final boolean isSupported() {
            return isSupported;
        }

        @Nullable
        public final Platform buildIfSupported() {
            return this.isSupported() ? (Platform)new AndroidPlatform() : null;
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }

    @Metadata(mv={1, 8, 0}, k=1, xi=48, d1={"\u00008\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0080\b\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\t\u0010\u0007\u001a\u00020\u0003H\u00c2\u0003J\t\u0010\b\u001a\u00020\u0005H\u00c2\u0003J\u001d\u0010\t\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005H\u00c6\u0001J\u0013\u0010\n\u001a\u00020\u000b2\b\u0010\f\u001a\u0004\u0018\u00010\rH\u00d6\u0003J\u0012\u0010\u000e\u001a\u0004\u0018\u00010\u000f2\u0006\u0010\u0010\u001a\u00020\u000fH\u0016J\t\u0010\u0011\u001a\u00020\u0012H\u00d6\u0001J\t\u0010\u0013\u001a\u00020\u0014H\u00d6\u0001R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0015"}, d2={"Ldev/serverpanel/lib/okhttp3/internal/platform/AndroidPlatform$CustomTrustRootIndex;", "Ldev/serverpanel/lib/okhttp3/internal/tls/TrustRootIndex;", "trustManager", "Ljavax/net/ssl/X509TrustManager;", "findByIssuerAndSignatureMethod", "Ljava/lang/reflect/Method;", "(Ljavax/net/ssl/X509TrustManager;Ljava/lang/reflect/Method;)V", "component1", "component2", "copy", "equals", "", "other", "", "findByIssuerAndSignature", "Ljava/security/cert/X509Certificate;", "cert", "hashCode", "", "toString", "", "okhttp"})
    public static final class CustomTrustRootIndex
    implements TrustRootIndex {
        @NotNull
        private final X509TrustManager trustManager;
        @NotNull
        private final Method findByIssuerAndSignatureMethod;

        public CustomTrustRootIndex(@NotNull X509TrustManager trustManager, @NotNull Method findByIssuerAndSignatureMethod) {
            Intrinsics.checkNotNullParameter(trustManager, "trustManager");
            Intrinsics.checkNotNullParameter(findByIssuerAndSignatureMethod, "findByIssuerAndSignatureMethod");
            this.trustManager = trustManager;
            this.findByIssuerAndSignatureMethod = findByIssuerAndSignatureMethod;
        }

        @Override
        @Nullable
        public X509Certificate findByIssuerAndSignature(@NotNull X509Certificate cert) {
            X509Certificate x509Certificate;
            Intrinsics.checkNotNullParameter(cert, "cert");
            try {
                Object[] objectArray = new Object[]{cert};
                Object object = this.findByIssuerAndSignatureMethod.invoke((Object)this.trustManager, objectArray);
                Intrinsics.checkNotNull(object, "null cannot be cast to non-null type java.security.cert.TrustAnchor");
                TrustAnchor trustAnchor = (TrustAnchor)object;
                x509Certificate = trustAnchor.getTrustedCert();
            }
            catch (IllegalAccessException e) {
                throw new AssertionError("unable to get issues and signature", e);
            }
            catch (InvocationTargetException _) {
                x509Certificate = null;
            }
            return x509Certificate;
        }

        private final X509TrustManager component1() {
            return this.trustManager;
        }

        private final Method component2() {
            return this.findByIssuerAndSignatureMethod;
        }

        @NotNull
        public final CustomTrustRootIndex copy(@NotNull X509TrustManager trustManager, @NotNull Method findByIssuerAndSignatureMethod) {
            Intrinsics.checkNotNullParameter(trustManager, "trustManager");
            Intrinsics.checkNotNullParameter(findByIssuerAndSignatureMethod, "findByIssuerAndSignatureMethod");
            return new CustomTrustRootIndex(trustManager, findByIssuerAndSignatureMethod);
        }

        public static /* synthetic */ CustomTrustRootIndex copy$default(CustomTrustRootIndex customTrustRootIndex, X509TrustManager x509TrustManager, Method method, int n, Object object) {
            if ((n & 1) != 0) {
                x509TrustManager = customTrustRootIndex.trustManager;
            }
            if ((n & 2) != 0) {
                method = customTrustRootIndex.findByIssuerAndSignatureMethod;
            }
            return customTrustRootIndex.copy(x509TrustManager, method);
        }

        @NotNull
        public String toString() {
            return "CustomTrustRootIndex(trustManager=" + this.trustManager + ", findByIssuerAndSignatureMethod=" + this.findByIssuerAndSignatureMethod + ')';
        }

        public int hashCode() {
            int result = this.trustManager.hashCode();
            result = result * 31 + this.findByIssuerAndSignatureMethod.hashCode();
            return result;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof CustomTrustRootIndex)) {
                return false;
            }
            CustomTrustRootIndex customTrustRootIndex = (CustomTrustRootIndex)other;
            if (!Intrinsics.areEqual(this.trustManager, customTrustRootIndex.trustManager)) {
                return false;
            }
            return Intrinsics.areEqual(this.findByIssuerAndSignatureMethod, customTrustRootIndex.findByIssuerAndSignatureMethod);
        }
    }
}

