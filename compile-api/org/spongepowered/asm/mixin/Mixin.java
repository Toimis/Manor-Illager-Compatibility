package org.spongepowered.asm.mixin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Match the real Mixin API: the loader reads RuntimeInvisibleAnnotations.
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface Mixin {
    String[] targets();
    boolean remap();
}
