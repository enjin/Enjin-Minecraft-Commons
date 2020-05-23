package com.enjin.minecraft_commons.spigot.ui;

import java.util.Objects;

public class Position {

    private final int x;
    private final int y;

    private Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Position position = (Position) o;
        return x == position.x && y == position.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return String.format("[%d,%d]", this.x, this.y);
    }

    public static Position of(int x, int y) {
        return new Position(x, y);
    }

    public static int toSlot(Container container, int x, int y) {
        return y * container.getDimension().getWidth() + x;
    }

    public static int toSlot(Container container, Position position) {
        return toSlot(container, position.x, position.y);
    }

    public static Position toPosition(Container container, int slot) {
        return new Position(slot % container.getDimension().getWidth(), slot / container.getDimension().getWidth());
    }

    public static Position toPosition(Component component, int slot) {
        return new Position(slot % component.getDimension().getWidth(), slot / component.getDimension().getWidth());
    }

    public static Position normalize(Position parentPosition, Position childOriginPosition) {
        return new Position(parentPosition.x - childOriginPosition.x, parentPosition.y - childOriginPosition.y);
    }

}
