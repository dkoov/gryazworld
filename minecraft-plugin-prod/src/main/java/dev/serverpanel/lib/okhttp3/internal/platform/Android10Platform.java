/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.annotation.SuppressLint
 *  android.os.Build$VERSION
 *  android.security.NetworkSecurityPolicy
 */
package dev.serverpanel.lib.okhttp3.internal.platform;

import android.annotation.SuppressLint;
import android.os.Build;
import android.security.NetworkSecurityPolicy;
import dev.serverpanel.lib.okhttp3.Protocol;
import dev.serverpanel.lib.okhttp3.internal.SuppressSignatureCheck;
import dev.serverpanel.lib.okhttp3.internal.platform.Platform;
import dev.serverpanel.lib.okhttp3.internal.platform.android.Android10SocketAdapter;
import dev.serverpanel.lib.okhttp3.internal.platform.android.AndroidCertificateChainCleaner;
import dev.serverpanel.lib.okhttp3.internal.platform.android.AndroidSocketAdapter;
import dev.serverpanel.lib.okhttp3.internal.platform.android.BouncyCastleSocketAdapter;
import dev.serverpanel.lib.okhttp3.internal.platform.android.ConscryptSocketAdapter;
import dev.serverpanel.lib.okhttp3.internal.platform.android.DeferredSocketAdapter;
import dev.serverpanel.lib.okhttp3.internal.platform.android.SocketAdapter;
import dev.serverpanel.lib.okhttp3.internal.tls.CertificateChainCleaner;
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

@Metadata(mv={1, 8, 0}, k=1, xi=48, d1={"\u0000J\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0007\u0018\u0000 \u00172\u00020\u0001:\u0001\u0017B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tH\u0016J(\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\r2\b\u0010\u000e\u001a\u0004\u0018\u00010\u000f2\f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00110\u0004H\u0016J\u0012\u0010\u0012\u001a\u0004\u0018\u00010\u000f2\u0006\u0010\f\u001a\u00020\rH\u0016J\u0010\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u000e\u001a\u00020\u000fH\u0017J\u0012\u0010\b\u001a\u0004\u0018\u00010\t2\u0006\u0010\u0015\u001a\u00020\u0016H\u0016R\u0014\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0018"}, d2={"Ldev/serverpanel/lib/okhttp3/internal/platform/Android10Platform;", "Ldev/serverpanel/lib/okhttp3/internal/platform/Platform;", "()V", "socketAdapters", "", "Ldev/serverpanel/lib/okhttp3/internal/platform/android/SocketAdapter;", "buildCertificateChainCleaner", "Ldev/serverpanel/lib/okhttp3/internal/tls/CertificateChainCleaner;", "trustManager", "Ljavax/net/ssl/X509TrustManager;", "configureTlsExtensions", "", "sslSocket", "Ljavax/net/ssl/SSLSocket;", "hostname", "", "protocols", "Ldev/serverpanel/lib/okhttp3/Protocol;", "getSelectedProtocol", "isCleartextTrafficPermitted", "", "sslSocketFactory", "Ljavax/net/ssl/SSLSocketFactory;", "Companion", "okhttp"})
@SuppressSignatureCheck
@SourceDebugExtension(value={"SMAP\nAndroid10Platform.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Android10Platform.kt\nokhttp3/internal/platform/Android10Platform\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,72:1\n766#2:73\n857#2,2:74\n1#3:76\n*S KotlinDebug\n*F\n+ 1 Android10Platform.kt\nokhttp3/internal/platform/Android10Platform\n*L\n43#1:73\n43#1:74,2\n*E\n"})
public final class Android10Platform
extends Platform {
    @NotNull
    public static final Companion Companion = new Companion(null);
    @NotNull
    private final List<SocketAdapter> socketAdapters;
    private static final boolean isSupported = Platform.Companion.isAndroid() && Build.VERSION.SDK_INT >= 29;

    /*
     * WARNING - void declaration
     */
    public Android10Platform() {
        void $this$filterTo$iv$iv;
        void $this$filter$iv;
        Object object = new SocketAdapter[]{Android10SocketAdapter.Companion.buildIfSupported(), new DeferredSocketAdapter(AndroidSocketAdapter.Companion.getPlayProviderFactory()), new DeferredSocketAdapter(ConscryptSocketAdapter.Companion.getFactory()), new DeferredSocketAdapter(BouncyCastleSocketAdapter.Companion.getFactory())};
        object = CollectionsKt.listOfNotNull(object);
        Android10Platform android10Platform = this;
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
        android10Platform.socketAdapters = (List)destination$iv$iv;
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

    public void configureTlsExtensions(@NotNull SSLSocket sslSocket, @Nullable String hostname, @NotNull List<? extends Protocol> protocols) {
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
    @SuppressLint(value={"NewApi"})
    public boolean isCleartextTrafficPermitted(@NotNull String hostname) {
        Intrinsics.checkNotNullParameter(hostname, "hostname");
        return NetworkSecurityPolicy.getInstance().isCleartextTrafficPermitted(hostname);
    }

    @Override
    @NotNull
    public CertificateChainCleaner buildCertificateChainCleaner(@NotNull X509TrustManager trustManager) {
        Intrinsics.checkNotNullParameter(trustManager, "trustManager");
        AndroidCertificateChainCleaner androidCertificateChainCleaner = AndroidCertificateChainCleaner.Companion.buildIfSupported(trustManager);
        return androidCertificateChainCleaner != null ? (CertificateChainCleaner)androidCertificateChainCleaner : super.buildCertificateChainCleaner(trustManager);
    }

    @Metadata(mv={1, 8, 0}, k=1, xi=48, d1={"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007R\u0011\u0010\u0003\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0003\u0010\u0005\u00a8\u0006\b"}, d2={"Ldev/serverpanel/lib/okhttp3/internal/platform/Android10Platform$Companion;", "", "()V", "isSupported", "", "()Z", "buildIfSupported", "Ldev/serverpanel/lib/okhttp3/internal/platform/Platform;", "okhttp"})
    public static final class Companion {
        private Companion() {
        }

        public final boolean isSupported() {
            return isSupported;
        }

        @Nullable
        public final Platform buildIfSupported() {
            return this.isSupported() ? (Platform)new Android10Platform() : null;
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }
}

