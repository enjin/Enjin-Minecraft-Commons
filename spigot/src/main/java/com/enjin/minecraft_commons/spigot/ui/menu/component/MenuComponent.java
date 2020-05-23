package com.enjin.minecraft_commons.spigot.ui.menu.component;

import com.enjin.minecraft_commons.spigot.ui.ClickAction;
import com.enjin.minecraft_commons.spigot.ui.ClickHandler;
import com.enjin.minecraft_commons.spigot.ui.Component;
import com.enjin.minecraft_commons.spigot.ui.Container;
import com.enjin.minecraft_commons.spigot.ui.Dimension;
import com.enjin.minecraft_commons.spigot.ui.Position;
import com.enjin.minecraft_commons.spigot.ui.SlotUpdateHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class MenuComponent implements Component {

    private Container parent;
    private final Map<Position, Stack<ClickAction>> actions = new HashMap<>();
    private Dimension dimension;
    private ClickHandler clickHandler;
    private SlotUpdateHandler slotUpdateHandler;
    private Collection<Consumer<Player>> cleanupTasks = new LinkedList<>();
    private boolean allowPlace;
    private boolean allowDrag;
    private boolean allowPickup;

    public MenuComponent(Dimension dimension) {
        this.dimension = dimension;
    }

    @Override
    public Dimension getDimension() {
        return this.dimension;
    }

    public void addAction(Position position, Consumer<Player> action, ClickType type, ClickType... moreTypes) {
        if (position != null
                && (position.getX() >= 0 && position.getX() < dimension.getWidth())
                && (position.getY() >= 0 && position.getY() < dimension.getHeight())) {
            ClickAction clickAction = new ClickAction(action, type, moreTypes);
            if (this.actions.containsKey(position)) {
                this.actions.get(position).add(clickAction);
            } else {
                Stack<ClickAction> actions = new Stack<>();
                actions.add(clickAction);
                this.actions.put(position, actions);
            }
        }
    }

    public void removeAction(Position position) {
        if (position != null)
            this.actions.remove(position);
    }

    public void removeAllActions() {
        this.actions.clear();
    }

    @Override
    public void onClick(Player player, ClickType click, Position position) {
        if (this.clickHandler != null && !this.clickHandler.handle(player, click, position) && position != null)
            return;

        Stack<ClickAction> actions = this.actions.get(position);
        if (actions == null)
            return;

        actions.forEach(clickAction -> {
            if (clickAction.shouldAct(click))
                clickAction.act(player);
        });
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
        return this.allowPlace;
    }

    @Override
    public boolean isAllowDrag() {
        return this.allowDrag;
    }

    @Override
    public boolean isAllowPickup() {
        return this.allowPickup;
    }

    @Override
    public void setAllowPlace(boolean allowPlace) {
        this.allowPlace = allowPlace;
    }

    @Override
    public void setAllowDrag(boolean allowDrag) {
        this.allowDrag = allowDrag;
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

    public Optional<ClickHandler> getClickHandler() {
        return Optional.ofNullable(this.clickHandler);
    }

    public void setClickHandler(ClickHandler clickHandler) {
        this.clickHandler = clickHandler;
    }

    public Optional<SlotUpdateHandler> getSlotUpdateHandler() {
        return Optional.ofNullable(this.slotUpdateHandler);
    }

    public void setSlotUpdateHandler(SlotUpdateHandler slotUpdateHandler) {
        this.slotUpdateHandler = slotUpdateHandler;
    }

    public int size() {
        return this.dimension.getWidth() * this.dimension.getHeight();
    }
}
