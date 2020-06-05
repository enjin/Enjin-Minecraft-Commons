package com.enjin.minecraft_commons.spigot.map;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.awt.*;

public class ImageRenderer extends MapRenderer {

    private final Image image;

    private ImageRenderer() {
        throw new IllegalStateException();
    }

    public ImageRenderer(Image image) {
        this.image = image;
    }

    @Override
    public void render(MapView map, MapCanvas canvas, Player player) {
        canvas.drawImage(0, 0, MapPalette.resizeImage(image));
    }

    public static void apply(MapView map, Image image) {
        if (map == null)
            throw new NullPointerException();

        map.getRenderers().clear();
        map.addRenderer(new ImageRenderer(image));
    }

}
