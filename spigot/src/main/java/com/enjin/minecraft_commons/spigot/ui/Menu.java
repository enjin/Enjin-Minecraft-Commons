package com.enjin.minecraft_commons.spigot.ui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.Stream;

public abstract class Menu extends AbstractMenu implements Listener {

    private static final InventoryAction[] placeActions = {
            InventoryAction.PLACE_ONE,
            InventoryAction.PLACE_SOME,
            InventoryAction.PLACE_ALL
    };
    private static final InventoryAction[] pickupActions = {
            InventoryAction.PICKUP_ONE,
            InventoryAction.PICKUP_SOME,
            InventoryAction.PICKUP_HALF,
            InventoryAction.PICKUP_ALL
    };

    public Menu(String name, Dimension dimension) {
        super(name, dimension);
        Bukkit.getServer().getPluginManager().registerEvents(this, getHolder());
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getPlayer();
        if (!isNameSwitch() && hasOpen(player)) {
            getComponents().keySet().forEach(component -> {
                try {
                    component.onClose(player);
                } catch (Throwable t) {
                    getHolder().getLogger().log(Level.WARNING, "An exception was caught while handling a component.", t);
                }
            });
            removePlayer(player);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        if (hasOpen(player)) {
            InventoryView snapshot = event.getView();

            ItemStack cursor = event.getCursor();
            Map<Integer, ItemStack> rollback = new HashMap<>();
            for (Map.Entry<Integer, ItemStack> entry : event.getNewItems().entrySet()) {
                int slot = entry.getKey();

                if (slot < getSize()) {
                    Optional<Component> optional = getComponent(slot);
                    ItemStack item = entry.getValue();

                    if (!optional.isPresent() || (optional.isPresent() && !optional.get().isAllowDrag())) {
                        ItemStack old = snapshot.getItem(slot);
                        int diff = old == null ? item.getAmount() : item.getAmount() - old.getAmount();

                        if (cursor == null) {
                            cursor = item.clone();
                            cursor.setAmount(diff);
                        } else {
                            cursor.setAmount(cursor.getAmount() + diff);
                        }

                        rollback.put(slot, old);
                    } else {
                        Component component = optional.get();
                        Position inventoryPosition = Position.toPosition(this, slot);
                        Position componentPosition = getComponents().get(component);
                        Position normalizedPosition = Position.normalize(inventoryPosition, componentPosition);
                        component.setItem(player, normalizedPosition, item);
                        component.getSlotUpdateHandler().ifPresent(handler -> handler.handle(player, slot, snapshot.getItem(slot), item));
                    }
                }
            }

            if (!rollback.isEmpty()) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(getHolder(), () -> {
                    rollback.forEach((slot, item) -> player.getOpenInventory().setItem(slot, item));
                    player.updateInventory();
                }, 1);
            }

            event.setCursor(cursor);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        if (hasOpen(player)) {
            int slot = event.getSlot();
            if (slot >= getSize()) {
                return;
            }

            Inventory inventory = event.getClickedInventory();
            if (inventory == player.getInventory()) {
                if (!isPlayerInventoryInteractionsAllowed()) {
                    event.setResult(Event.Result.DENY);
                } else {
                    validatePlayerInventoryAction(event);
                }
            } else {
                event.setResult(Event.Result.DENY);
                if (!(event.getClickedInventory() == event.getInventory())) {
                    return;
                }

                Optional<Component> optionalComponent = getComponent(slot);
                if (optionalComponent.isPresent()) {
                    Component component = optionalComponent.get();
                    if (component.isAllowPlace() && Stream.of(placeActions).anyMatch(a -> a.equals(event.getAction()))) {
                        event.setResult(Event.Result.DEFAULT);
                        handlePlace(player, slot, event.getCurrentItem(), event.getCursor(), component);

                        ItemStack existing = event.getCurrentItem();
                        ItemStack placed = event.getCursor();

                        if (existing != null && placed != null) {
                            placed = placed.clone();
                            placed.setAmount(placed.getAmount() + existing.getAmount());
                        }

                        final ItemStack newItem = placed;

                        component.getSlotUpdateHandler().ifPresent(handler -> handler.handle(player, slot, existing, newItem));
                    } else if (component.isAllowPickup() && Stream.of(pickupActions).anyMatch(a -> a.equals(event.getAction()))) {
                        event.setResult(Event.Result.DEFAULT);
                        handlePickup(player, slot, event.getCurrentItem(), event.getCursor(), component);
                        component.getSlotUpdateHandler().ifPresent(handler -> handler.handle(player, slot, event.getCurrentItem(), event.getCursor()));
                    } else {
                        handleClick(player, event.getClick(), slot, component);
                    }
                }
            }
        }
    }

    private void handlePickup(Player player, int slot, ItemStack current, ItemStack cursor, Component component) {
        Position pos = getComponents().get(component);
        Position slotPos = Position.toPosition(this, slot);
        Position offsetPos = Position.of(slotPos.getX() - pos.getX(), slotPos.getY() - pos.getY());
        Bukkit.getScheduler().scheduleSyncDelayedTask(getHolder(), () -> {
            component.onPickup(player, current, cursor, offsetPos);
            player.updateInventory();
        }, 1);
    }

    private void handlePlace(Player player, int slot, ItemStack current, ItemStack cursor, Component component) {
        Position pos = getComponents().get(component);
        Position slotPos = Position.toPosition(this, slot);
        Position offsetPos = Position.of(slotPos.getX() - pos.getX(), slotPos.getY() - pos.getY());
        Bukkit.getScheduler().scheduleSyncDelayedTask(getHolder(), () -> {
            component.onPlace(player, current, cursor, offsetPos);
            player.updateInventory();
        }, 1);
    }

    private void handleClick(Player player, ClickType type, int slot, Component component) {
        Position pos = getComponents().get(component);
        Position slotPos = Position.toPosition(this, slot);
        Position offsetPos = Position.of(slotPos.getX() - pos.getX(), slotPos.getY() - pos.getY());
        Bukkit.getScheduler().scheduleSyncDelayedTask(getHolder(),
                () -> component.onClick(player, type, offsetPos));
    }

    private void validatePlayerInventoryAction(InventoryClickEvent event) {
        switch (event.getAction()) {
            case MOVE_TO_OTHER_INVENTORY:
            case COLLECT_TO_CURSOR:
                event.setResult(Event.Result.DENY);
        }
    }

}
