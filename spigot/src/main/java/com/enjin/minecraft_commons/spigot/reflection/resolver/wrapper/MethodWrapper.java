package com.enjin.minecraft_commons.spigot.reflection.resolver.wrapper;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MethodWrapper<R> extends WrapperAbstract {

    private final Method method;

    public MethodWrapper(Method method) {
        this.method = method;
    }

    @Override
    public boolean exists() {
        return method != null;
    }

    public String getName() {
        return method.getName();
    }

    public R invoke(Object object, Object... args) {
        try {
            return (R) method.invoke(object, args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public R invokeSilent(Object object, Object... args) {
        try {
            return (R) method.invoke(object, args);
        } catch (Exception e) {
        }

        return null;
    }

    public Method getMethod() {
        return method;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        MethodWrapper<?> that = (MethodWrapper<?>) object;
        return Objects.equals(method, that.method);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method);
    }

    public static String getMethodSignature(Method method, boolean fullClassNames) {
        return MethodSignature.of(method, fullClassNames).getSignature();
    }

    public static String getMethodSignature(Method method) {
        return getMethodSignature(method, false);
    }

    public static class MethodSignature {

        static final Pattern SIGNATURE_STRING_PATTERN = Pattern.compile("(.+) (.*)\\\\((.*)\\\\)");

        private final String   returnType;
        private final Pattern  returnTypePattern;
        private final String   name;
        private final Pattern  namePattern;
        private final String[] parameterTypes;
        private final String   signature;

        public MethodSignature(String returnType, String name, String[] parameterTypes) {
            this.returnType = returnType;
            this.returnTypePattern = Pattern.compile(returnType.replace("?", "\\w")
                    .replace("*", "\\w")
                    .replace("[", "\\[")
                    .replace("]", "\\]"));
            this.name = name;
            this.namePattern = Pattern.compile(name.replace("?", "\\w")
                    .replace("*", "\\w"));
            this.parameterTypes = parameterTypes;

            StringBuilder builder = new StringBuilder();
            builder.append(returnType).append(' ').append(name).append('(');

            boolean first = true;
            for (String parameterType : parameterTypes) {
                if (!first) {
                    builder.append(',');
                }

                builder.append(parameterType);
                first = false;
            }

            this.signature = builder.append(')').toString();
        }

        public static MethodSignature of(Method method, boolean fullClassNames) {
            Class<?>   returnType           = method.getReturnType();
            Class<?>[] parameterTypes       = method.getParameterTypes();
            String     methodName           = method.getName();
            String[]   parameterTypeStrings = new String[parameterTypes.length];
            String     returnTypeString;

            for (int i = 0; i < parameterTypes.length; i++) {
                if (parameterTypes[i].isPrimitive()) {
                    parameterTypeStrings[i] = parameterTypes[i].toString();
                } else {
                    parameterTypeStrings[i] = fullClassNames ? parameterTypes[i].getName() : parameterTypes[i].getSimpleName();
                }
            }

            if (returnType.isPrimitive()) {
                returnTypeString = returnType.toString();
            } else {
                returnTypeString = fullClassNames ? returnType.getName() : returnType.getSimpleName();
            }

            return new MethodSignature(returnTypeString, methodName, parameterTypeStrings);
        }

        public static MethodSignature fromString(String signatureString) {
            if (signatureString == null) {
                return null;
            }

            Matcher matcher = SIGNATURE_STRING_PATTERN.matcher(signatureString);
            if (matcher.find()) {
                if (matcher.groupCount() != 3) {
                    throw new IllegalArgumentException("invalid signature");
                }

                return new MethodSignature(matcher.group(1), matcher.group(2), matcher.group(3).split(","));
            } else {
                throw new IllegalArgumentException("invalid signature");
            }
        }

        public String getReturnType() {
            return returnType;
        }

        public boolean isReturnTypeWildcard() {
            return "?".equals(returnType) || "*".equals(returnType);
        }

        public String getName() {
            return name;
        }

        public boolean isNameWildcard() {
            return "?".equals(name) || "*".equals(name);
        }

        public String[] getParameterTypes() {
            return parameterTypes;
        }

        public String getParameterType(int index) throws IndexOutOfBoundsException {
            return parameterTypes[index];
        }

        public boolean isParameterWildcard(int index) throws IndexOutOfBoundsException {
            return "?".equals(getParameterType(index)) || "*".equals(getParameterType(index));
        }

        public String getSignature() {
            return signature;
        }

        public boolean matches(MethodSignature other) {
            if (other == null) {
                return false;
            }

            if (!returnTypePattern.matcher(other.returnType).matches()) {
                return false;
            }

            if (!namePattern.matcher(other.name).matches()) {
                return false;
            }

            if (parameterTypes.length != other.parameterTypes.length) {
                return false;
            }

            for (int i = 0; i < parameterTypes.length; i++) {
                if (!Pattern.compile(getParameterType(i).replace("?", "\\w")
                        .replace("*", "\\w*"))
                        .matcher(other.getParameterType(i)).matches()) {
                    return false;
                }
            }

            return true;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) return true;
            if (object == null || getClass() != object.getClass()) return false;
            MethodSignature that = (MethodSignature) object;
            return Objects.equals(returnType, that.returnType) &&
                    Objects.equals(name, that.name) &&
                    Arrays.equals(parameterTypes, that.parameterTypes) &&
                    Objects.equals(signature, that.signature);
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(returnType, name, signature);
            result = 31 * result + Arrays.hashCode(parameterTypes);
            return result;
        }

        @Override
        public String toString() {
            return getSignature();
        }
    }
}
