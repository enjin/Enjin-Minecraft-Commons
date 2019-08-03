package com.enjin.minecraft_commons.spigot.ui.menu.component;

import com.enjin.minecraft_commons.spigot.ui.Dimension;
import com.enjin.minecraft_commons.spigot.ui.Position;
import com.enjin.minecraft_commons.spigot.ui.menu.component.pagination.PagedComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.IntStream;

public class SimpleMenuComponent extends MenuComponent {

    private ItemStack[][] contents;

    public SimpleMenuComponent(Dimension dimension) {
        super(dimension);
        this.contents = new ItemStack[dimension.getHeight()][dimension.getWidth()];
    }

    public void setItem(Position position, ItemStack stack) {
        setItem(position.getX(), position.getY(), stack);
    }

    public void setItem(Position position, ItemStack stack, Consumer<Player> action) {
        this.setItem(position, stack);
        this.addAction(stack, action, ClickType.LEFT);
    }

    public void setItem(int x, int y, ItemStack stack) {
        this.contents[x][y] = stack;
    }

    public void removeItem(Position position) {
        removeItem(position.getX(), position.getY());
    }

    public void removeItem(int x, int y) {
        this.contents[x][y] = null;
    }

    public void setToggle(Position position, boolean onState, ItemStack on, ItemStack off, BiConsumer<Player, Boolean> toggle) {
        this.addAction(on, player -> {
            toggle.accept(player, false);
            setItem(position, off);
            getParent().updateAll();
        }, ClickType.LEFT);
        this.addAction(off, player -> {
            toggle.accept(player, true);
            setItem(position, on);
            getParent().updateAll();
        }, ClickType.LEFT);
        this.setItem(position, onState ? on : off);
    }

    public void setNextPage(Position position, PagedComponent component, BiFunction<Integer, Integer, ItemStack> pageStack, Function<Integer, ItemStack> last) {
        this.setItem(position, pageStack.apply(0, 0), player -> component.nextPage(player));
        component.onPage((player, page) -> {
            if (page < component.getPageCount(player) - 1) {
                this.setItem(player, position, pageStack.apply(component.getPageNumber(player) + 1, component.getPageCount(player)));
            } else {
                this.setItem(player, position, last.apply(component.getPageCount(player)));
            }
            return true;
        });
    }

    public void setPreviousPage(Position position, PagedComponent component, BiFunction<Integer, Integer, ItemStack> pageStack, Function<Integer, ItemStack> first) {
        setItem(position, pageStack.apply(0, 0), player -> component.previousPage(player));
        component.onPage((player, page) -> {
            if (0 < page) {
                this.setItem(player, position, pageStack.apply(component.getPageNumber(player) + 1, component.getPageCount(player)));
            } else {
                this.setItem(player, position, first.apply(component.getPageCount(player)));
            }
            return true;
        });
    }

    @Override
    public void draw(Player player) {
        IntStream.range(0, getDimension().getHeight()).forEach(y -> {
            IntStream.range(0, getDimension().getWidth()).forEach(x -> {
                Position pos = Position.of(x, y);
                this.setItem(player, pos, this.contents[y][x]);
            });
        });
    }
}
