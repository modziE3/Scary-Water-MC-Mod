package net.modzyyy.scarywater;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public class StaminaManager {

    private static final String STAMINA_KEY = "ScaryWaterStamina";

    public static double getStamina(Player player) {
        CompoundTag data = player.getPersistentData();

        if (!data.contains(STAMINA_KEY)) {
            setStamina(player, Config.MAX_STAMINA.get());
        }

        return data.getDouble(STAMINA_KEY);
    }

    public static void setStamina(Player player, double stamina) {
        double maxStamina = Config.MAX_STAMINA.get();

        stamina = Math.max(0.0, Math.min(stamina, maxStamina));

        player.getPersistentData().putDouble(STAMINA_KEY, stamina);
    }

    public static void drain(Player player, double amount) {
        setStamina(player, getStamina(player) - amount);
    }

    public static void regenerate(Player player, double amount) {
        setStamina(player, getStamina(player) + amount);
    }

    public static boolean hasStamina(Player player) {
        return getStamina(player) > 0.0;
    }
}
