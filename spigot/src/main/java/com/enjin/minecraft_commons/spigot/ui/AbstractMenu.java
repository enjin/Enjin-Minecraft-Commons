package com.enjin.minecraft_commons.spigot.ui;

import com.enjin.minecraft_commons.spigot.ui.event.MenuCloseEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

public abstract class AbstractMenu implements Container {

    protected static final String KEY = "ENJIN:MENU";

    private String defaultName;
    private Dimension dimension;
    private BiConsumer<Player, AbstractMenu> openConsumer;
    private BiConsumer<Player, AbstractMenu> closeConsumer;
    private final Map<Player, Inventory> playerInventories;
    private Map<Component, Position> components;
    private Component[][] bySlot;
    private boolean nameSwitch;
    private Function<Player, String> nameProvider;
    private boolean playerInventoryInteractionsAllowed;

    public AbstractMenu(String name, Dimension dimension) {
        this.defaultName = name;
        this.dimension = dimension;
        this.openConsumer = (player, menu) -> {};
        this.closeConsumer = (player, menu) -> {};
        this.playerInventories = new HashMap<>();
        this.components = new HashMap<>();
        this.bySlot = new Component[dimension.getHeight()][dimension.getWidth()];
        this.nameProvider = player -> null;
        this.playerInventoryInteractionsAllowed = false;
    }

    private void fill(Component component, Position where) {
        Component what = component;
        if (where == null) {
            where = this.components.get(component);
        } else {
            what = null;
        }

        for (int j = where.getY(); j < where.getY() + component.getDimension().getHeight(); j++) {
            for (int i = where.getX(); i < where.getX() + component.getDimension().getWidth(); i++) {
                this.bySlot[j][i] = what;
            }
        }
    }

    protected abstract Inventory createInventory(Player player);

    protected Inventory getInventory(Player player, boolean createIfAbsent) {
        return this.playerInventories.computeIfAbsent(player, createIfAbsent ? this::createInventory : p -> null);
    }

    protected int getSize() {
        return this.dimension.getWidth() * this.dimension.getHeight();
    }

    protected void onOpen(Player player) {
        this.openConsumer.accept(player, this);
    }

    protected void onClose(Player player) {
        if (this.playerInventories.containsKey(player)) {
            this.closeConsumer.accept(player, this);
        }
        this.playerInventories.remove(player);
    }

    protected void closeMenu(Player player) {
        onClose(player);
        player.removeMetadata(KEY, getHolder());
        player.closeInventory();
        Bukkit.getPluginManager().callEvent(new MenuCloseEvent(this, player));
    }

    public String getDefaultName() {
        return this.defaultName;
    }

    public BiConsumer<Player, AbstractMenu> getOpenConsumer() {
        return this.openConsumer;
    }

    public BiConsumer<Player, AbstractMenu> getCloseConsumer() {
        return this.closeConsumer;
    }

    public boolean isNameSwitch() {
        return this.nameSwitch;
    }

    public void setNameProvider(Function<Player, String> provider) {
        this.nameProvider = provider != null ? provider : player -> null;
    }

    public void removePlayer(Player player) {
        closeMenu(player);
    }

    public AbstractMenu setOpenConsumer(BiConsumer<Player, AbstractMenu> consumer) {
        this.openConsumer = consumer != null ? consumer : (player, menu) -> {};
        return this;
    }

    public AbstractMenu setCloseConsumer(BiConsumer<Player, AbstractMenu> consumer) {
        this.closeConsumer = consumer != null ? consumer : (player, menu) -> {};
        return this;
    }

    public boolean hasOpen(Player player) {
        return getMenu(player).filter(this::equals).isPresent();
    }

    public boolean contains(Player player) {
        return this.playerInventories.containsKey(player);
    }

    public Map<Component, Position> getComponents() {
        return new HashMap<>(this.components);
    }

    public Optional<Component> getComponent(int slot) {
        Position position = Position.toPosition(this, slot);
        return getComponent(position);
    }

    public Optional<Component> getComponent(Position position) {
        if (position.getY() < 0 || position.getY() >= this.bySlot.length) {
            return Optional.empty();
        }

        Component[] row = this.bySlot[position.getY()];
        if (position.getX() < 0 || position.getX() >= row.length) {
            return Optional.empty();
        }

        return Optional.ofNullable(row[position.getX()]);
    }

    public void refresh(Player player) {
        if (!this.hasOpen(player)) {
            return;
        }

        if (!getName(player).equalsIgnoreCase(getInventory(player, true).getTitle())) {
            this.nameSwitch = true;
            Inventory inventory = getInventory(player, true);
            InventoryView view = player.openInventory(inventory);

            if (!view.getTopInventory().equals(inventory)) {
                throw new IllegalStateException("Failed to open inventory (was the InventoryOpenEvent cancelled?)");
            }

            this.nameSwitch = false;
            this.playerInventories.put(player, view.getTopInventory());
        }

        this.components.keySet().forEach(component -> component.draw(player));
        player.updateInventory();
    }

    public AbstractMenu open(Player player) {
        if (this.hasOpen(player)) {
            return this;
        }

        Inventory inventory = getInventory(player, true);
        this.getComponents().keySet().forEach(component -> {
            component.draw(player);
            component.onOpen(player);
        });
        setMenu(player, this);

        InventoryView view = player.openInventory(inventory);

        if (!view.getTopInventory().equals(inventory)) {
            throw new IllegalStateException("Failed to open inventory (was the InventoryOpenEvent cancelled?)");
        }

        this.onOpen(player);
        return this;
    }

    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (hasOpen(player) || contains(player)) {
            removePlayer(player);
        }
    }

    @Override
    public Dimension getDimension() {
        return this.dimension;
    }

    @Override
    public String getName(Player player) {
        String name = this.nameProvider.apply(player);
        return name != null ? name : getDefaultName();
    }

    @Override
    public boolean addComponent(Position position, Component component) {
        Position old = this.components.put(component, position);
        if (old != null) {
            if (!Objects.equals(old, position)) {
                this.components.put(component, old);
            }

            return false;
        }

        for (int j = position.getY(); j < position.getY() + component.getDimension().getHeight(); j++) {
            for (int i = position.getX(); i < position.getX() + component.getDimension().getWidth(); i++) {
                if (bySlot[j][i] != null) {
                    this.components.remove(component);
                    return false;
                }
            }
        }

        fill(component, null);
        component.setParent(this);
        return true;
    }

    @Override
    public boolean removeComponent(Component component) {
        Position pos = this.components.remove(component);
        if (pos == null) {
            return false;
        }
        fill(component, pos);
        return true;
    }

    @Override
    public Optional<ItemStack> getItem(Player player, Component component, Position offset) {
        Position pos = this.components.get(component);
        int slot = Position.toSlot(this, pos.getX() + offset.getX(), pos.getY() + offset.getY());
        Inventory inv = getInventory(player, true);
        if (slot < 0 || slot > inv.getSize()) {
            return Optional.empty();
        }
        return Optional.ofNullable(inv.getItem(slot)).filter(stack -> stack.getType() != Material.AIR);
    }

    @Override
    public void setItem(Player player, Component component, Position offset, ItemStack stack) {
        Position pos = this.components.get(component);
        int slot = Position.toSlot(this, pos.getX() + offset.getX(), pos.getY() + offset.getY());
        getInventory(player, true).setItem(slot, stack);
    }

    @Override
    public void setItem(Player player, Position position, ItemStack stack) {
        Optional<Component> optionalComponent = getComponent(position);
        if (optionalComponent.isPresent()) {
            Component component = optionalComponent.get();
            Position pos = this.components.get(component);
            Position normal = Position.of(position.getX() - pos.getX(), position.getY() - pos.getY());
            component.setItem(player, normal, stack);
        }
    }

    @Override
    public void update(Player player) {
        this.getComponents().keySet().forEach(component -> component.draw(player));
        setMenu(player, this);
        player.updateInventory();
    }

    @Override
    public void updateAll() {
        Bukkit.getOnlinePlayers().forEach(this::update);
    }

    @Override
    public void destroy() {
        List<RegisteredListener> regs = HandlerList.getRegisteredListeners(getHolder());
        Optional<Listener> optional = regs.stream().filter(r -> r.getListener() == this)
                .map(RegisteredListener::getListener)
                .findFirst();
        optional.ifPresent(HandlerList::unregisterAll);

        this.playerInventories.keySet().forEach(player -> {
            player.removeMetadata(KEY, getHolder());
            player.closeInventory();
        });
    }

    @Override
    public void allowPlayerInventoryInteractions(boolean state) {
        this.playerInventoryInteractionsAllowed = state;
    }

    public boolean isPlayerInventoryInteractionsAllowed() {
        return this.playerInventoryInteractionsAllowed;
    }

    protected static Optional<AbstractMenu> getMenu(Player player) {
        return player.getMetadata(KEY).stream()
                .filter(meta -> Objects.equals(meta.getOwningPlugin(), getHolder()))
                .map(MetadataValue::value)
                .filter(AbstractMenu.class::isInstance)
                .map(AbstractMenu.class::cast)
                .findFirst();
    }

    protected static void setMenu(Player player, AbstractMenu menu) {
        player.setMetadata(KEY, new FixedMetadataValue(getHolder(), menu));
        menu.playerInventories.put(player, menu.getInventory(player, true));
    }

    public static boolean hasAnyuMenu(Player player) {
        return getMenu(player).isPresent();
    }

    public static JavaPlugin getHolder() {
        return JavaPlugin.getProvidingPlugin(AbstractMenu.class);
    }
}
