package com.enjin.minecraft_commons.spigot.command;

import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.assertj.core.api.Assertions.*;

@RunWith(PowerMockRunner.class)
public class SpigotCommandCallerTypeTest {

    private SpigotCommandCallerType type;

    @Before
    public void setUp() {
        this.type = new SpigotCommandCallerType(Player.class);
    }

    @Test
    public void testConstructor_NoErrors() {
        assertThat(new SpigotCommandCallerType(Player.class)).isNotNull();
    }

    @Test(expected = NullPointerException.class)
    public void testConstructor_NullRawType_NullPointerException() {
        new SpigotCommandCallerType(null);
    }

    @Test
    public void testGetRawType_ReturnsPlayerClass() {
        assertThat(this.type.getRawType()).isEqualTo(Player.class);
    }

    @Test
    public void testCompare_NullCaller_ReturnsFalse() {
        assertThat(this.type.compare(null)).isFalse();
    }

    @Test
    public void testCompare_PlayerCaller_ReturnsTrue() {
        Player player = PowerMockito.mock(Player.class);
        assertThat(this.type.compare(player)).isTrue();
        Mockito.verify(player).getClass();
    }

}
