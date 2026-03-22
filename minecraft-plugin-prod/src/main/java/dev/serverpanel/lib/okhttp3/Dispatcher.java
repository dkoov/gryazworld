/*
 * Decompiled with CFR 0.152.
 */
package dev.serverpanel.lib.okhttp3;

import dev.serverpanel.lib.okhttp3.Call;
import dev.serverpanel.lib.okhttp3.internal.Util;
import dev.serverpanel.lib.okhttp3.internal.connection.RealCall;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import kotlin.Deprecated;
import kotlin.DeprecationLevel;
import kotlin.Metadata;
import kotlin.ReplaceWith;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.JvmName;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
@Metadata(mv={1, 8, 0}, k=1, xi=48, d1={"\u0000\\\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\b\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\b\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u00002\u00020\u0001B\u000f\b\u0016\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004B\u0005\u00a2\u0006\u0002\u0010\u0005J\u0006\u0010\u001e\u001a\u00020\u001fJ\u0019\u0010 \u001a\u00020\u001f2\n\u0010!\u001a\u00060\u001aR\u00020\u001bH\u0000\u00a2\u0006\u0002\b\"J\u0015\u0010#\u001a\u00020\u001f2\u0006\u0010!\u001a\u00020\u001bH\u0000\u00a2\u0006\u0002\b$J\r\u0010\u0002\u001a\u00020\u0003H\u0007\u00a2\u0006\u0002\b%J\u0016\u0010&\u001a\b\u0018\u00010\u001aR\u00020\u001b2\u0006\u0010'\u001a\u00020(H\u0002J)\u0010)\u001a\u00020\u001f\"\u0004\b\u0000\u0010*2\f\u0010+\u001a\b\u0012\u0004\u0012\u0002H*0,2\u0006\u0010!\u001a\u0002H*H\u0002\u00a2\u0006\u0002\u0010-J\u0015\u0010)\u001a\u00020\u001f2\u0006\u0010!\u001a\u00020\u001bH\u0000\u00a2\u0006\u0002\b.J\u0019\u0010)\u001a\u00020\u001f2\n\u0010!\u001a\u00060\u001aR\u00020\u001bH\u0000\u00a2\u0006\u0002\b.J\b\u0010/\u001a\u000200H\u0002J\f\u00101\u001a\b\u0012\u0004\u0012\u00020302J\u0006\u00104\u001a\u00020\u0010J\f\u00105\u001a\b\u0012\u0004\u0012\u00020302J\u0006\u00106\u001a\u00020\u0010R\u0011\u0010\u0002\u001a\u00020\u00038G\u00a2\u0006\u0006\u001a\u0004\b\u0002\u0010\u0006R\u0010\u0010\u0007\u001a\u0004\u0018\u00010\u0003X\u0082\u000e\u00a2\u0006\u0002\n\u0000R*\u0010\n\u001a\u0004\u0018\u00010\t2\b\u0010\b\u001a\u0004\u0018\u00010\t8F@FX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u000b\u0010\f\"\u0004\b\r\u0010\u000eR&\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u000f\u001a\u00020\u00108F@FX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0011\u0010\u0012\"\u0004\b\u0013\u0010\u0014R&\u0010\u0015\u001a\u00020\u00102\u0006\u0010\u0015\u001a\u00020\u00108F@FX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0016\u0010\u0012\"\u0004\b\u0017\u0010\u0014R\u0018\u0010\u0018\u001a\f\u0012\b\u0012\u00060\u001aR\u00020\u001b0\u0019X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0018\u0010\u001c\u001a\f\u0012\b\u0012\u00060\u001aR\u00020\u001b0\u0019X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u001b0\u0019X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u00067"}, d2={"Ldev/serverpanel/lib/okhttp3/Dispatcher;", "", "executorService", "Ljava/util/concurrent/ExecutorService;", "(Ljava/util/concurrent/ExecutorService;)V", "()V", "()Ljava/util/concurrent/ExecutorService;", "executorServiceOrNull", "<set-?>", "Ljava/lang/Runnable;", "idleCallback", "getIdleCallback", "()Ljava/lang/Runnable;", "setIdleCallback", "(Ljava/lang/Runnable;)V", "maxRequests", "", "getMaxRequests", "()I", "setMaxRequests", "(I)V", "maxRequestsPerHost", "getMaxRequestsPerHost", "setMaxRequestsPerHost", "readyAsyncCalls", "Ljava/util/ArrayDeque;", "Ldev/serverpanel/lib/okhttp3/internal/connection/RealCall$AsyncCall;", "Ldev/serverpanel/lib/okhttp3/internal/connection/RealCall;", "runningAsyncCalls", "runningSyncCalls", "cancelAll", "", "enqueue", "call", "enqueue$okhttp", "executed", "executed$okhttp", "-deprecated_executorService", "findExistingCallWithHost", "host", "", "finished", "T", "calls", "Ljava/util/Deque;", "(Ljava/util/Deque;Ljava/lang/Object;)V", "finished$okhttp", "promoteAndExecute", "", "queuedCalls", "", "Ldev/serverpanel/lib/okhttp3/Call;", "queuedCallsCount", "runningCalls", "runningCallsCount", "okhttp"})
@SourceDebugExtension(value={"SMAP\nDispatcher.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Dispatcher.kt\nokhttp3/Dispatcher\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 Util.kt\nokhttp3/internal/Util\n+ 4 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,241:1\n1#2:242\n615#3,4:243\n1549#4:247\n1620#4,3:248\n1549#4:251\n1620#4,3:252\n*S KotlinDebug\n*F\n+ 1 Dispatcher.kt\nokhttp3/Dispatcher\n*L\n162#1:243,4\n222#1:247\n222#1:248,3\n227#1:251\n227#1:252,3\n*E\n"})
public final class Dispatcher {
    private int maxRequests;
    private int maxRequestsPerHost;
    @Nullable
    private Runnable idleCallback;
    @Nullable
    private ExecutorService executorServiceOrNull;
    @NotNull
    private final ArrayDeque<RealCall.AsyncCall> readyAsyncCalls;
    @NotNull
    private final ArrayDeque<RealCall.AsyncCall> runningAsyncCalls;
    @NotNull
    private final ArrayDeque<RealCall> runningSyncCalls;

    public Dispatcher() {
        this.maxRequests = 64;
        this.maxRequestsPerHost = 5;
        this.readyAsyncCalls = new ArrayDeque();
        this.runningAsyncCalls = new ArrayDeque();
        this.runningSyncCalls = new ArrayDeque();
    }

    public final synchronized int getMaxRequests() {
        return this.maxRequests;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void setMaxRequests(int maxRequests) {
        if (!(maxRequests >= 1)) {
            boolean $i$a$-require-Dispatcher$maxRequests$22 = false;
            String $i$a$-require-Dispatcher$maxRequests$22 = "max < 1: " + maxRequests;
            throw new IllegalArgumentException($i$a$-require-Dispatcher$maxRequests$22.toString());
        }
        Dispatcher dispatcher = this;
        synchronized (dispatcher) {
            boolean bl = false;
            this.maxRequests = maxRequests;
            Unit unit = Unit.INSTANCE;
        }
        this.promoteAndExecute();
    }

    public final synchronized int getMaxRequestsPerHost() {
        return this.maxRequestsPerHost;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void setMaxRequestsPerHost(int maxRequestsPerHost) {
        if (!(maxRequestsPerHost >= 1)) {
            boolean $i$a$-require-Dispatcher$maxRequestsPerHost$22 = false;
            String $i$a$-require-Dispatcher$maxRequestsPerHost$22 = "max < 1: " + maxRequestsPerHost;
            throw new IllegalArgumentException($i$a$-require-Dispatcher$maxRequestsPerHost$22.toString());
        }
        Dispatcher dispatcher = this;
        synchronized (dispatcher) {
            boolean bl = false;
            this.maxRequestsPerHost = maxRequestsPerHost;
            Unit unit = Unit.INSTANCE;
        }
        this.promoteAndExecute();
    }

    @Nullable
    public final synchronized Runnable getIdleCallback() {
        return this.idleCallback;
    }

    public final synchronized void setIdleCallback(@Nullable Runnable runnable2) {
        this.idleCallback = runnable2;
    }

    @JvmName(name="executorService")
    @NotNull
    public final synchronized ExecutorService executorService() {
        if (this.executorServiceOrNull == null) {
            this.executorServiceOrNull = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, (BlockingQueue<Runnable>)new SynchronousQueue(), Util.threadFactory(Util.okHttpName + " Dispatcher", false));
        }
        ExecutorService executorService = this.executorServiceOrNull;
        Intrinsics.checkNotNull(executorService);
        return executorService;
    }

    public Dispatcher(@NotNull ExecutorService executorService) {
        Intrinsics.checkNotNullParameter(executorService, "executorService");
        this();
        this.executorServiceOrNull = executorService;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void enqueue$okhttp(@NotNull RealCall.AsyncCall call) {
        Intrinsics.checkNotNullParameter(call, "call");
        Dispatcher dispatcher = this;
        synchronized (dispatcher) {
            RealCall.AsyncCall existingCall;
            boolean bl = false;
            this.readyAsyncCalls.add(call);
            if (!call.getCall().getForWebSocket() && (existingCall = this.findExistingCallWithHost(call.getHost())) != null) {
                call.reuseCallsPerHostFrom(existingCall);
            }
            Unit unit = Unit.INSTANCE;
        }
        this.promoteAndExecute();
    }

    private final RealCall.AsyncCall findExistingCallWithHost(String host) {
        for (RealCall.AsyncCall existingCall : this.runningAsyncCalls) {
            if (!Intrinsics.areEqual(existingCall.getHost(), host)) continue;
            return existingCall;
        }
        for (RealCall.AsyncCall existingCall : this.readyAsyncCalls) {
            if (!Intrinsics.areEqual(existingCall.getHost(), host)) continue;
            return existingCall;
        }
        return null;
    }

    public final synchronized void cancelAll() {
        for (RealCall.AsyncCall asyncCall : this.readyAsyncCalls) {
            asyncCall.getCall().cancel();
        }
        for (RealCall.AsyncCall asyncCall : this.runningAsyncCalls) {
            asyncCall.getCall().cancel();
        }
        for (RealCall realCall : this.runningSyncCalls) {
            realCall.cancel();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private final boolean promoteAndExecute() {
        Dispatcher $this$assertThreadDoesntHoldLock$iv = this;
        boolean $i$f$assertThreadDoesntHoldLock = false;
        if (Util.assertionsEnabled && Thread.holdsLock($this$assertThreadDoesntHoldLock$iv)) {
            throw new AssertionError((Object)("Thread " + Thread.currentThread().getName() + " MUST NOT hold lock on " + $this$assertThreadDoesntHoldLock$iv));
        }
        List executableCalls = new ArrayList();
        boolean isRunning = false;
        Dispatcher dispatcher = this;
        synchronized (dispatcher) {
            boolean bl = false;
            Iterator<RealCall.AsyncCall> iterator2 = this.readyAsyncCalls.iterator();
            Intrinsics.checkNotNullExpressionValue(iterator2, "readyAsyncCalls.iterator()");
            Iterator<RealCall.AsyncCall> i = iterator2;
            while (i.hasNext()) {
                RealCall.AsyncCall asyncCall = i.next();
                if (this.runningAsyncCalls.size() >= this.maxRequests) break;
                if (asyncCall.getCallsPerHost().get() >= this.maxRequestsPerHost) continue;
                i.remove();
                asyncCall.getCallsPerHost().incrementAndGet();
                Intrinsics.checkNotNullExpressionValue(asyncCall, "asyncCall");
                executableCalls.add(asyncCall);
                this.runningAsyncCalls.add(asyncCall);
            }
            isRunning = this.runningCallsCount() > 0;
            Unit unit = Unit.INSTANCE;
        }
        int n = executableCalls.size();
        for (int i = 0; i < n; ++i) {
            RealCall.AsyncCall asyncCall = (RealCall.AsyncCall)executableCalls.get(i);
            asyncCall.executeOn(this.executorService());
        }
        return isRunning;
    }

    public final synchronized void executed$okhttp(@NotNull RealCall call) {
        Intrinsics.checkNotNullParameter(call, "call");
        this.runningSyncCalls.add(call);
    }

    public final void finished$okhttp(@NotNull RealCall.AsyncCall call) {
        Intrinsics.checkNotNullParameter(call, "call");
        call.getCallsPerHost().decrementAndGet();
        this.finished((Deque)this.runningAsyncCalls, call);
    }

    public final void finished$okhttp(@NotNull RealCall call) {
        Intrinsics.checkNotNullParameter(call, "call");
        this.finished((Deque)this.runningSyncCalls, call);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private final <T> void finished(Deque<T> calls, T call) {
        Runnable idleCallback = null;
        Dispatcher dispatcher = this;
        synchronized (dispatcher) {
            boolean bl = false;
            if (!calls.remove(call)) {
                throw new AssertionError((Object)"Call wasn't in-flight!");
            }
            idleCallback = this.idleCallback;
            Unit unit = Unit.INSTANCE;
        }
        boolean isRunning = this.promoteAndExecute();
        if (!isRunning && idleCallback != null) {
            idleCallback.run();
        }
    }

    /*
     * WARNING - void declaration
     */
    @NotNull
    public final synchronized List<Call> queuedCalls() {
        void $this$mapTo$iv$iv;
        Iterable $this$map$iv = this.readyAsyncCalls;
        boolean $i$f$map = false;
        Iterable iterable = $this$map$iv;
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10));
        boolean $i$f$mapTo = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv) {
            void it;
            RealCall.AsyncCall asyncCall = (RealCall.AsyncCall)item$iv$iv;
            Collection collection = destination$iv$iv;
            boolean bl = false;
            collection.add(it.getCall());
        }
        List<Call> list = Collections.unmodifiableList((List)destination$iv$iv);
        Intrinsics.checkNotNullExpressionValue(list, "unmodifiableList(readyAsyncCalls.map { it.call })");
        return list;
    }

    /*
     * WARNING - void declaration
     */
    @NotNull
    public final synchronized List<Call> runningCalls() {
        void $this$mapTo$iv$iv;
        void $this$map$iv;
        Iterable iterable = this.runningAsyncCalls;
        Collection collection = this.runningSyncCalls;
        boolean $i$f$map = false;
        void var3_4 = $this$map$iv;
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10));
        boolean $i$f$mapTo = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv) {
            void it;
            RealCall.AsyncCall asyncCall = (RealCall.AsyncCall)item$iv$iv;
            Collection collection2 = destination$iv$iv;
            boolean bl = false;
            collection2.add(it.getCall());
        }
        List<Call> list = Collections.unmodifiableList(CollectionsKt.plus(collection, (Iterable)((List)destination$iv$iv)));
        Intrinsics.checkNotNullExpressionValue(list, "unmodifiableList(running\u2026yncCalls.map { it.call })");
        return list;
    }

    public final synchronized int queuedCallsCount() {
        return this.readyAsyncCalls.size();
    }

    public final synchronized int runningCallsCount() {
        return this.runningAsyncCalls.size() + this.runningSyncCalls.size();
    }

    @Deprecated(message="moved to val", replaceWith=@ReplaceWith(expression="executorService", imports={}), level=DeprecationLevel.ERROR)
    @JvmName(name="-deprecated_executorService")
    @NotNull
    public final ExecutorService -deprecated_executorService() {
        return this.executorService();
    }
}

