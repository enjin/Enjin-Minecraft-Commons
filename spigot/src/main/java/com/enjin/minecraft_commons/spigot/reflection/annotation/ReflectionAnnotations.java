package com.enjin.minecraft_commons.spigot.reflection.annotation;

import com.enjin.minecraft_commons.spigot.reflection.Minecraft;
import com.enjin.minecraft_commons.spigot.reflection.resolver.ClassResolver;
import com.enjin.minecraft_commons.spigot.reflection.resolver.FieldResolver;
import com.enjin.minecraft_commons.spigot.reflection.resolver.MethodResolver;
import com.enjin.minecraft_commons.spigot.reflection.resolver.wrapper.ClassWrapper;
import com.enjin.minecraft_commons.spigot.reflection.resolver.wrapper.FieldWrapper;
import com.enjin.minecraft_commons.spigot.reflection.resolver.wrapper.MethodWrapper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReflectionAnnotations {

    public static final ReflectionAnnotations INSTANCE = new ReflectionAnnotations();

    static final Pattern classRefPattern = Pattern.compile("@Class\\((.*)\\)");

    private ReflectionAnnotations() {
    }

    public void load(Object toLoad) {
        if (toLoad == null) {
            throw new IllegalArgumentException("toLoad cannot be null");
        }

        ClassResolver classResolver = new ClassResolver();

        for (Field field : toLoad.getClass().getDeclaredFields()) {
            ClassDefinition classAnnotation = field.getAnnotation(ClassDefinition.class);
            FieldDefinition fieldAnnotation = field.getAnnotation(FieldDefinition.class);
            MethodDefinition methodAnnotation = field.getAnnotation(MethodDefinition.class);

            if (classAnnotation == null && fieldAnnotation == null && methodAnnotation == null) {
                continue;
            } else {
                field.setAccessible(true);
            }

            if (classAnnotation != null) {
                List<String> nameList = parseAnnotationVersions(ClassDefinition.class, classAnnotation);

                if (nameList.isEmpty()) {
                    throw new IllegalArgumentException("@ClassDefinition names cannot be empty");
                }

                String[] names = nameList.toArray(new String[0]);

                for (int i = 0; i < names.length; i++) {
                    names[i] = names[i]
                            .replace("{nms}", "net.minecraft.server." + Minecraft.VERSION.name())
                            .replace("{obc}", "org.bukkit.craftbukkit." + Minecraft.VERSION.name());
                }

                try {
                    if (ClassWrapper.class.isAssignableFrom(field.getType())) {
                        field.set(toLoad, classResolver.resolveWrapper(names));
                    } else if (Class.class.isAssignableFrom(field.getType())) {
                        field.set(toLoad, classResolver.resolve(names));
                    } else {
                        throwInvalidFieldType(field, toLoad, "Class or ClassWrapper");
                        return;
                    }
                } catch (ReflectiveOperationException e) {
                    if (!classAnnotation.ignoreExceptions()) {
                        throwReflectionException("@ClassDefinition", field, toLoad, e);
                        return;
                    }
                }
            } else if (fieldAnnotation != null) {
                List<String> nameList = parseAnnotationVersions(FieldDefinition.class, fieldAnnotation);

                if (nameList.isEmpty()) {
                    throw new IllegalArgumentException("@FieldDefenition names cannot be empty");
                }

                String[] names = nameList.toArray(new String[0]);

                try {
                    FieldResolver fieldResolver = new FieldResolver(parseClass(FieldDefinition.class, fieldAnnotation, toLoad));

                    if (FieldWrapper.class.isAssignableFrom(field.getType())) {
                        field.set(toLoad, fieldResolver.resolveWrapper(names));
                    } else if (Field.class.isAssignableFrom(field.getType())) {
                        field.set(toLoad, fieldResolver.resolve(names));
                    } else {
                        throwInvalidFieldType(field, toLoad, "Field or FieldWrapper");
                    }
                } catch (ReflectiveOperationException e) {
                    if (!fieldAnnotation.ignoreExceptions()) {
                        throwReflectionException("@FieldDefinition", field, toLoad, e);
                    }
                }
            } else if (methodAnnotation != null) {
                List<String> nameList = parseAnnotationVersions(MethodDefinition.class, methodAnnotation);

                if (nameList.isEmpty()) {
                    throw new IllegalArgumentException("@MethodDefenition names cannot be empty");
                }

                String[] names = nameList.toArray(new String[0]);
                boolean isSignature = names[0].contains(" ");

                for (String name : names) {
                    if (name.contains(" ") != isSignature) {
                        throw new IllegalArgumentException("Inconsistent method names: Cannot have mixed signatures/names");
                    }
                }

                try {
                    MethodResolver methodResolver = new MethodResolver(parseClass(MethodDefinition.class, methodAnnotation, toLoad));

                    if (MethodWrapper.class.isAssignableFrom(field.getType())) {
                        if (isSignature) {
                            field.set(toLoad, methodResolver.resolveSignatureWrapper(names));
                        } else {
                            field.set(toLoad, methodResolver.resolveWrapper(names));
                        }
                    } else if (Method.class.isAssignableFrom(field.getType())) {
                        if (isSignature) {
                            field.set(toLoad, methodResolver.resolveSignature(names));
                        } else {
                            field.set(toLoad, methodResolver.resolve(names));
                        }
                    } else {
                        throwInvalidFieldType(field, toLoad, "Method or MethodWrapper");
                    }
                } catch (ReflectiveOperationException e) {
                    if (!fieldAnnotation.ignoreExceptions()) {
                        throwReflectionException("@FieldDefinition", field, toLoad, e);
                    }
                }
            }
        }
    }

    <A extends Annotation> List<String> parseAnnotationVersions(java.lang.Class<A> clazz, A annotation) {
        List<String> list = new ArrayList<>();

        try {
            String[] names = (String[]) clazz.getMethod("value").invoke(annotation);
            Minecraft.Version[] versions = (Minecraft.Version[]) clazz.getMethod("versions").invoke(annotation);

            if (versions.length == 0) {
                for (String name : names) {
                    list.add(name);
                }
            } else {
                if (versions.length > names.length) {
                    throw new RuntimeException(String.format("versions array cannot have more elements than the names (%s)", clazz));
                }

                for (int i = 0; i < versions.length; i++) {
                    if (Minecraft.VERSION == versions[i]) {
                        list.add(names[i]);
                    } else {
                        if (names[i].startsWith(">") && Minecraft.VERSION.newerThan(versions[i])) {
                            list.add(names[i].substring(1));
                        } else if (names[i].startsWith("<" ) && Minecraft.VERSION.olderThan(versions[i])) {
                            list.add(names[i].substring(1));
                        }
                    }
                }
            }
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    <A extends Annotation> String parseClass(Class<A> clazz, A annotation, Object toLoad) {
        try {
            String className = (String) clazz.getMethod("className").invoke(annotation);
            Matcher matcher = classRefPattern.matcher(className);

            while (matcher.find()) {
                if (matcher.groupCount() != 1) {
                    continue;
                }

                String fieldName = matcher.group(1);
                Field field = toLoad.getClass().getField(fieldName);

                if (ClassWrapper.class.isAssignableFrom(field.getType())) {
                    return ((ClassWrapper) field.get(toLoad)).getName();
                } else if (Class.class.isAssignableFrom(field.getType())) {
                    return ((Class) field.get(toLoad)).getName();
                }
            }

            return className;
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    void throwInvalidFieldType(Field field, Object toLoad, String expected) {
        throw new IllegalArgumentException(String.format("Field %s in %s is not of type %s, it's %s", field.getName(), toLoad.getClass(), expected, field.getType()));
    }

    void throwReflectionException(String annotation, Field field, Object toLoad, ReflectiveOperationException exception) {
        throw new RuntimeException(String.format("Failed to set %s field %s in %s", annotation, field.getName(), toLoad.getClass()), exception);
    }

}
