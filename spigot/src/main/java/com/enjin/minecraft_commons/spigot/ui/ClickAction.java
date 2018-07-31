package com.enjin.minecraft_commons.spigot.ui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.Arrays;
import java.util.function.Consumer;

public class ClickAction {

    private static final ClickAction NONE = new ClickAction(null, ClickType.DROP) {
        @Override
        public boolean shouldAct(ClickType clickType) {
            return false;
        }
    };

    private final Consumer<Player> action;

    private final short types;

    public ClickAction(Consumer<Player> action, ClickType type, ClickType... types) {
        this.action = action;
        this.types = Arrays.stream(types).map(Enum::ordinal).map(ord -> 1 << ord)
                .reduce(1 << type.ordinal(), (a, b) -> a | b).shortValue();
    }

    public boolean shouldAct(ClickType clickType) {
        return (this.types & (1 << clickType.ordinal())) > 0;
    }

    public void act(Player player) {
        this.action.accept(player);
    }
}
