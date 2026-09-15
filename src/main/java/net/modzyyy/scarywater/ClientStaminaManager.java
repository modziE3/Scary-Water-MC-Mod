package net.modzyyy.scarywater;

public class ClientStaminaManager {

    private static double stamina = Config.MAX_STAMINA.get();

    public static double getStamina() {
        return stamina;
    }

    public static void setStamina(double value) {
        double maxStamina = Config.MAX_STAMINA.get();

        stamina = Math.max(
                0.0,
                Math.min(value, maxStamina)
        );
    }

    public static boolean hasStamina() {
        return stamina > 0.0;
    }
}