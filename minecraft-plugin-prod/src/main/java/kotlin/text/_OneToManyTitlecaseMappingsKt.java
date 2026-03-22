/*
 * Decompiled with CFR 0.152.
 */
package kotlin.text;

import java.util.Locale;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={1, 8, 0}, k=2, xi=48, d1={"\u0000\f\n\u0000\n\u0002\u0010\u000e\n\u0002\u0010\f\n\u0000\u001a\f\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\u0000\u00a8\u0006\u0003"}, d2={"titlecaseImpl", "", "", "kotlin-stdlib"})
public final class _OneToManyTitlecaseMappingsKt {
    @NotNull
    public static final String titlecaseImpl(char $this$titlecaseImpl) {
        String string = String.valueOf($this$titlecaseImpl);
        Intrinsics.checkNotNull(string, "null cannot be cast to non-null type java.lang.String");
        String string2 = string.toUpperCase(Locale.ROOT);
        Intrinsics.checkNotNullExpressionValue(string2, "this as java.lang.String).toUpperCase(Locale.ROOT)");
        String uppercase = string2;
        if (uppercase.length() > 1) {
            String string3;
            if ($this$titlecaseImpl == '\u0149') {
                string3 = uppercase;
            } else {
                char c = uppercase.charAt(0);
                String string4 = uppercase;
                int n = 1;
                Intrinsics.checkNotNull(string4, "null cannot be cast to non-null type java.lang.String");
                String string5 = string4.substring(n);
                Intrinsics.checkNotNullExpressionValue(string5, "this as java.lang.String).substring(startIndex)");
                string4 = string5;
                Intrinsics.checkNotNull(string4, "null cannot be cast to non-null type java.lang.String");
                String string6 = string4.toLowerCase(Locale.ROOT);
                Intrinsics.checkNotNullExpressionValue(string6, "this as java.lang.String).toLowerCase(Locale.ROOT)");
                string4 = string6;
                string3 = c + string4;
            }
            return string3;
        }
        return String.valueOf(Character.toTitleCase($this$titlecaseImpl));
    }
}

