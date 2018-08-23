package com.enjin.minecraft_commons.spigot.ui.menu.component;

import com.enjin.minecraft_commons.spigot.ui.ClickAction;
import com.enjin.minecraft_commons.spigot.ui.ClickHandler;
import com.enjin.minecraft_commons.spigot.ui.Component;
import com.enjin.minecraft_commons.spigot.ui.Container;
import com.enjin.minecraft_commons.spigot.ui.Dimension;
import com.enjin.minecraft_commons.spigot.ui.ItemStackKey;
import com.enjin.minecraft_commons.spigot.ui.Position;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class MenuComponent implements Component {

    private Container parent;
    private final Map<ItemStackKey, ClickAction> actions;
    private Dimension dimension;
    private ClickHandler clickHandler;
    private Collection<Consumer<Player>> cleanupTasks;
    private boolean allowPlace;
    private boolean allowPickup;

    public MenuComponent(Dimension dimension) {
        this.dimension = dimension;
        this.actions = Maps.newHashMap();
        this.cleanupTasks = Lists.newLinkedList();
    }

    @Override
    public Dimension getDimension() {
        return this.dimension;
    }

    public void addAction(ItemStack stack, Consumer<Player> action, ClickType type, ClickType... moreTypes) {
        if (stack == null) return;
        this.actions.put(ItemStackKey.of(stack), new ClickAction(action, type, moreTypes));
    }

    @Override
    public void onClick(Player player, ClickType click, Position position) {
        if (this.clickHandler != null && !this.clickHandler.handle(player, click, position)) {
            return;
        }

        Optional<ItemStack> optionalItem = getParent().getItem(player, this, position);
        if (!optionalItem.isPresent()) {
            return;
        }
        Optional<ClickAction> optionalAction = optionalItem.map(ItemStackKey::of).map(this.actions::get).filter(Objects::nonNull);
        if (!optionalAction.isPresent()) {
            return;
        }
        optionalAction = optionalAction.filter(action -> action.shouldAct(click));
        if (!optionalAction.isPresent()) {
            return;
        }
        optionalAction.ifPresent(action -> action.act(player));
    }

    public void onClick(ClickHandler handler) {
        this.clickHandler = handler;
    }

    @Override
    public void draw(Player player) {
    }

    @Override
    public final Container getParent() {
        return this.parent;
    }

    @Override
    public final void setParent(Container parent) {
        this.parent = parent;
    }

    @Override
    public boolean isAllowPlace() {
        return allowPlace;
    }

    @Override
    public boolean isAllowPickup() {
        return allowPickup;
    }

    @Override
    public void setAllowPlace(boolean allowPlace) {
        this.allowPlace = allowPlace;
    }

    @Override
    public void setAllowPickup(boolean allowPickup) {
        this.allowPickup = allowPickup;
    }

    protected <T> Map<Player, T> createPlayerMap(Supplier<Map<Player, T>> constructor) {
        Map<Player, T> map = constructor.get();
        this.cleanupTasks.add(map::remove);
        return map;
    }

    protected <T extends Collection<Player>> T createPlayerCollection(Supplier<T> constructor) {
        T collection = constructor.get();
        this.cleanupTasks.add(collection::remove);
        return collection;
    }
}