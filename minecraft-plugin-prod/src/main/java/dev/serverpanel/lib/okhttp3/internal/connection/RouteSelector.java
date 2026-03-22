/*
 * Decompiled with CFR 0.152.
 */
package dev.serverpanel.lib.okhttp3.internal.connection;

import dev.serverpanel.lib.okhttp3.Address;
import dev.serverpanel.lib.okhttp3.Call;
import dev.serverpanel.lib.okhttp3.EventListener;
import dev.serverpanel.lib.okhttp3.HttpUrl;
import dev.serverpanel.lib.okhttp3.Route;
import dev.serverpanel.lib.okhttp3.internal.Util;
import dev.serverpanel.lib.okhttp3.internal.connection.RouteDatabase;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={1, 8, 0}, k=1, xi=48, d1={"\u0000d\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u0000 !2\u00020\u0001:\u0002!\"B%\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u00a2\u0006\u0002\u0010\nJ\t\u0010\u0015\u001a\u00020\u0016H\u0086\u0002J\b\u0010\u0017\u001a\u00020\u0016H\u0002J\t\u0010\u0018\u001a\u00020\u0019H\u0086\u0002J\b\u0010\u001a\u001a\u00020\u0014H\u0002J\u0010\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u001d\u001a\u00020\u0014H\u0002J\u001a\u0010\u001e\u001a\u00020\u001c2\u0006\u0010\u001f\u001a\u00020 2\b\u0010\u001d\u001a\u0004\u0018\u00010\u0014H\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\r0\fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00120\u0011X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00140\fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006#"}, d2={"Ldev/serverpanel/lib/okhttp3/internal/connection/RouteSelector;", "", "address", "Ldev/serverpanel/lib/okhttp3/Address;", "routeDatabase", "Ldev/serverpanel/lib/okhttp3/internal/connection/RouteDatabase;", "call", "Ldev/serverpanel/lib/okhttp3/Call;", "eventListener", "Ldev/serverpanel/lib/okhttp3/EventListener;", "(Lokhttp3/Address;Lokhttp3/internal/connection/RouteDatabase;Lokhttp3/Call;Lokhttp3/EventListener;)V", "inetSocketAddresses", "", "Ljava/net/InetSocketAddress;", "nextProxyIndex", "", "postponedRoutes", "", "Ldev/serverpanel/lib/okhttp3/Route;", "proxies", "Ljava/net/Proxy;", "hasNext", "", "hasNextProxy", "next", "Ldev/serverpanel/lib/okhttp3/internal/connection/RouteSelector$Selection;", "nextProxy", "resetNextInetSocketAddress", "", "proxy", "resetNextProxy", "url", "Ldev/serverpanel/lib/okhttp3/HttpUrl;", "Companion", "Selection", "okhttp"})
public final class RouteSelector {
    @NotNull
    public static final Companion Companion = new Companion(null);
    @NotNull
    private final Address address;
    @NotNull
    private final RouteDatabase routeDatabase;
    @NotNull
    private final Call call;
    @NotNull
    private final EventListener eventListener;
    @NotNull
    private List<? extends Proxy> proxies;
    private int nextProxyIndex;
    @NotNull
    private List<? extends InetSocketAddress> inetSocketAddresses;
    @NotNull
    private final List<Route> postponedRoutes;

    public RouteSelector(@NotNull Address address, @NotNull RouteDatabase routeDatabase, @NotNull Call call, @NotNull EventListener eventListener) {
        Intrinsics.checkNotNullParameter(address, "address");
        Intrinsics.checkNotNullParameter(routeDatabase, "routeDatabase");
        Intrinsics.checkNotNullParameter(call, "call");
        Intrinsics.checkNotNullParameter(eventListener, "eventListener");
        this.address = address;
        this.routeDatabase = routeDatabase;
        this.call = call;
        this.eventListener = eventListener;
        this.proxies = CollectionsKt.emptyList();
        this.inetSocketAddresses = CollectionsKt.emptyList();
        this.postponedRoutes = new ArrayList();
        this.resetNextProxy(this.address.url(), this.address.proxy());
    }

    public final boolean hasNext() {
        return this.hasNextProxy() || !((Collection)this.postponedRoutes).isEmpty();
    }

    @NotNull
    public final Selection next() throws IOException {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        List routes = new ArrayList();
        while (this.hasNextProxy()) {
            Proxy proxy = this.nextProxy();
            for (InetSocketAddress inetSocketAddress : this.inetSocketAddresses) {
                Route route = new Route(this.address, proxy, inetSocketAddress);
                if (this.routeDatabase.shouldPostpone(route)) {
                    ((Collection)this.postponedRoutes).add(route);
                    continue;
                }
                ((Collection)routes).add(route);
            }
            if (!(!((Collection)routes).isEmpty())) continue;
        }
        if (routes.isEmpty()) {
            CollectionsKt.addAll((Collection)routes, (Iterable)this.postponedRoutes);
            this.postponedRoutes.clear();
        }
        return new Selection(routes);
    }

    private final void resetNextProxy(HttpUrl url, Proxy proxy) {
        this.eventListener.proxySelectStart(this.call, url);
        this.proxies = RouteSelector.resetNextProxy$selectProxies(proxy, url, this);
        this.nextProxyIndex = 0;
        this.eventListener.proxySelectEnd(this.call, url, this.proxies);
    }

    private final boolean hasNextProxy() {
        return this.nextProxyIndex < this.proxies.size();
    }

    private final Proxy nextProxy() throws IOException {
        if (!this.hasNextProxy()) {
            throw new SocketException("No route to " + this.address.url().host() + "; exhausted proxy configurations: " + this.proxies);
        }
        int n = this.nextProxyIndex;
        this.nextProxyIndex = n + 1;
        Proxy result = this.proxies.get(n);
        this.resetNextInetSocketAddress(result);
        return result;
    }

    private final void resetNextInetSocketAddress(Proxy proxy) throws IOException {
        List mutableInetSocketAddresses;
        this.inetSocketAddresses = mutableInetSocketAddresses = (List)new ArrayList();
        String socketHost = null;
        int socketPort = 0;
        if (proxy.type() == Proxy.Type.DIRECT || proxy.type() == Proxy.Type.SOCKS) {
            socketHost = this.address.url().host();
            socketPort = this.address.url().port();
        } else {
            SocketAddress proxyAddress = proxy.address();
            if (!(proxyAddress instanceof InetSocketAddress)) {
                boolean $i$a$-require-RouteSelector$resetNextInetSocketAddress$22 = false;
                String $i$a$-require-RouteSelector$resetNextInetSocketAddress$22 = "Proxy.address() is not an InetSocketAddress: " + proxyAddress.getClass();
                throw new IllegalArgumentException($i$a$-require-RouteSelector$resetNextInetSocketAddress$22.toString());
            }
            Intrinsics.checkNotNullExpressionValue(proxyAddress, "proxyAddress");
            socketHost = Companion.getSocketHost((InetSocketAddress)proxyAddress);
            socketPort = ((InetSocketAddress)proxyAddress).getPort();
        }
        if (!(1 <= socketPort ? socketPort < 65536 : false)) {
            throw new SocketException("No route to " + socketHost + ':' + socketPort + "; port is out of range");
        }
        if (proxy.type() == Proxy.Type.SOCKS) {
            ((Collection)mutableInetSocketAddresses).add(InetSocketAddress.createUnresolved(socketHost, socketPort));
        } else {
            List<InetAddress> list;
            if (Util.canParseAsIpAddress(socketHost)) {
                list = CollectionsKt.listOf(InetAddress.getByName(socketHost));
            } else {
                this.eventListener.dnsStart(this.call, socketHost);
                List<InetAddress> result = this.address.dns().lookup(socketHost);
                if (result.isEmpty()) {
                    throw new UnknownHostException(this.address.dns() + " returned no addresses for " + socketHost);
                }
                this.eventListener.dnsEnd(this.call, socketHost, result);
                list = result;
            }
            List<InetAddress> addresses = list;
            for (InetAddress inetAddress : addresses) {
                ((Collection)mutableInetSocketAddresses).add(new InetSocketAddress(inetAddress, socketPort));
            }
        }
    }

    private static final List<Proxy> resetNextProxy$selectProxies(Proxy $proxy, HttpUrl $url, RouteSelector this$0) {
        Proxy proxy = $proxy;
        if (proxy != null) {
            return CollectionsKt.listOf(proxy);
        }
        URI uri = $url.uri();
        if (uri.getHost() == null) {
            Proxy[] proxyArray = new Proxy[]{Proxy.NO_PROXY};
            return Util.immutableListOf(proxyArray);
        }
        List<Proxy> proxiesOrNull = this$0.address.proxySelector().select(uri);
        Proxy[] proxyArray = (Proxy[])proxiesOrNull;
        if (proxyArray == null || proxyArray.isEmpty()) {
            proxyArray = new Proxy[]{Proxy.NO_PROXY};
            return Util.immutableListOf(proxyArray);
        }
        Intrinsics.checkNotNullExpressionValue(proxiesOrNull, "proxiesOrNull");
        return Util.toImmutableList(proxiesOrNull);
    }

    @Metadata(mv={1, 8, 0}, k=1, xi=48, d1={"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u0015\u0010\u0003\u001a\u00020\u0004*\u00020\u00058F\u00a2\u0006\u0006\u001a\u0004\b\u0006\u0010\u0007\u00a8\u0006\b"}, d2={"Ldev/serverpanel/lib/okhttp3/internal/connection/RouteSelector$Companion;", "", "()V", "socketHost", "", "Ljava/net/InetSocketAddress;", "getSocketHost", "(Ljava/net/InetSocketAddress;)Ljava/lang/String;", "okhttp"})
    public static final class Companion {
        private Companion() {
        }

        @NotNull
        public final String getSocketHost(@NotNull InetSocketAddress $this$socketHost) {
            Intrinsics.checkNotNullParameter($this$socketHost, "<this>");
            InetAddress inetAddress = $this$socketHost.getAddress();
            if (inetAddress == null) {
                String string = $this$socketHost.getHostName();
                Intrinsics.checkNotNullExpressionValue(string, "hostName");
                return string;
            }
            InetAddress address = inetAddress;
            String string = address.getHostAddress();
            Intrinsics.checkNotNullExpressionValue(string, "address.hostAddress");
            return string;
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }

    @Metadata(mv={1, 8, 0}, k=1, xi=48, d1={"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0013\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\u0002\u0010\u0005J\t\u0010\n\u001a\u00020\u000bH\u0086\u0002J\t\u0010\f\u001a\u00020\u0004H\u0086\u0002R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\t\u00a8\u0006\r"}, d2={"Ldev/serverpanel/lib/okhttp3/internal/connection/RouteSelector$Selection;", "", "routes", "", "Ldev/serverpanel/lib/okhttp3/Route;", "(Ljava/util/List;)V", "nextRouteIndex", "", "getRoutes", "()Ljava/util/List;", "hasNext", "", "next", "okhttp"})
    public static final class Selection {
        @NotNull
        private final List<Route> routes;
        private int nextRouteIndex;

        public Selection(@NotNull List<Route> routes) {
            Intrinsics.checkNotNullParameter(routes, "routes");
            this.routes = routes;
        }

        @NotNull
        public final List<Route> getRoutes() {
            return this.routes;
        }

        public final boolean hasNext() {
            return this.nextRouteIndex < this.routes.size();
        }

        @NotNull
        public final Route next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            int n = this.nextRouteIndex;
            this.nextRouteIndex = n + 1;
            return this.routes.get(n);
        }
    }
}

