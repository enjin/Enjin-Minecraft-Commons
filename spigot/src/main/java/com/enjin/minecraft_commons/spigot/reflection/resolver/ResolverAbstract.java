package com.enjin.minecraft_commons.spigot.reflection.resolver;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class ResolverAbstract<T> {

    private final Map<ResolverQuery, T> resolvedObjects = new ConcurrentHashMap<>();

    protected T resolveSilent(ResolverQuery... queries) {
        try {
            return resolve(queries);
        } catch (Exception e) {
        }

        return null;
    }

    protected T resolve(ResolverQuery... queries) throws ReflectiveOperationException {
        if (queries == null || queries.length <= 0) {
            throw new IllegalArgumentException("queries must not be null or empty");
        }

        for (ResolverQuery query : queries) {
            if (resolvedObjects.containsKey(query)) {
                return resolvedObjects.get(query);
            }

            try {
                T resolved = resolveObject(query);
                resolvedObjects.put(query, resolved);
                return resolved;
            } catch (ReflectiveOperationException e) {
            }
        }

        throw notFoundException(Arrays.asList(queries).toString());
    }

    protected abstract T resolveObject(ResolverQuery query) throws ReflectiveOperationException;

    protected ReflectiveOperationException notFoundException(String joinedNames) {
        return new ClassNotFoundException(String.format("Objects could not be resolved: %s", joinedNames));
    }
}
