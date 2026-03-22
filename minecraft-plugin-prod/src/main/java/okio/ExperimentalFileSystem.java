/*
 * Decompiled with CFR 0.152.
 */
package okio;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import kotlin.Deprecated;
import kotlin.DeprecationLevel;
import kotlin.Metadata;
import kotlin.annotation.AnnotationRetention;
import kotlin.annotation.AnnotationTarget;

@Deprecated(message="This annotation is obsolete and should be removed.", level=DeprecationLevel.HIDDEN)
@kotlin.annotation.Retention(value=AnnotationRetention.SOURCE)
@kotlin.annotation.Target(allowedTargets={AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY})
@Retention(value=RetentionPolicy.SOURCE)
@Target(value={ElementType.TYPE, ElementType.METHOD})
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000\n\n\u0002\u0018\u0002\n\u0002\u0010\u001b\n\u0000\b\u0087\u0002\u0018\u00002\u00020\u0001B\u0000\u00a8\u0006\u0002"}, d2={"Lokio/ExperimentalFileSystem;", "", "okio"})
public @interface ExperimentalFileSystem {
}

