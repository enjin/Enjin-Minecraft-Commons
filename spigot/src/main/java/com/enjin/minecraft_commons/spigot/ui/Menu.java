package com.enjin.minecraft_commons.spigot.ui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import java.util.Optional;
import java.util.logging.Level;

public abstract class Menu extends AbstractMenu implements Listener {

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
            if (event.getClickedInventory() == player.getInventory()) {
                if (!isPlayerInventoryInteractionsAllowed()) {
                    event.setCancelled(true);
                }
            } else {
                event.setCancelled(true);
                if (!(event.getClickedInventory() == event.getInventory())) {
                    return;
                }

                Optional<Component> optionalComponent = getComponent(slot);
                optionalComponent.ifPresent(component -> {
                    Position pos = getComponents().get(component);
                    Position slotPos = Position.toPosition(this, slot);
                    Position offsetPos = Position.of(slotPos.getX() - pos.getX(), slotPos.getY() - pos.getY());
                    Bukkit.getScheduler().scheduleSyncDelayedTask(getHolder(),
                            () -> component.onClick(player, event.getClick(), offsetPos));
                });
            }
        }
    }

}
