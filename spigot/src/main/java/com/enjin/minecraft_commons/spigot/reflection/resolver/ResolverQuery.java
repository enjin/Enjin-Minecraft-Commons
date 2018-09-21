package com.enjin.minecraft_commons.spigot.reflection.resolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ResolverQuery {

    private String     name;
    private Class<?>[] types;

    public ResolverQuery(String name, Class<?>... types) {
        this.name = name;
        this.types = types;
    }

    public ResolverQuery(String name) {
        this.name = name;
        this.types = new Class[0];
    }

    public ResolverQuery(Class<?>... types) {
        this.types = types;
    }

    public String getName() {
        return name;
    }

    public Class<?>[] getTypes() {
        return types;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ResolverQuery that = (ResolverQuery) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return Arrays.equals(types, that.types);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(name);
        result = 31 * result + Arrays.hashCode(types);
        return result;
    }

    @Override
    public String toString() {
        return "ResolverQuery{" +
                "name='" + name + '\'' +
                ", types=" + Arrays.toString(types) +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private List<ResolverQuery> queryList = new ArrayList<>();

        private Builder() {
        }

        public Builder with(String name, Class<?>[] types) {
            queryList.add(new ResolverQuery(name, types));
            return this;
        }

        public Builder with(String name) {
            queryList.add(new ResolverQuery(name));
            return this;
        }

        public Builder with(Class<?>[] types) {
            queryList.add(new ResolverQuery(types));
            return this;
        }

        public ResolverQuery[] build() {
            return queryList.toArray(new ResolverQuery[0]);
        }
    }
}
