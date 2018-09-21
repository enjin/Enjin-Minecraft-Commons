package com.enjin.minecraft_commons.spigot.reflection.annotation;

import com.enjin.minecraft_commons.spigot.reflection.Minecraft;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MethodDefinition {

    String className();

    String[] value();

    Minecraft.Version[] versions() default {};

    boolean ignoreExceptions() default true;
}
