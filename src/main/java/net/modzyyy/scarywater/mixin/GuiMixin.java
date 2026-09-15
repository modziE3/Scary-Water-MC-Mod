package net.modzyyy.scarywater.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.modzyyy.scarywater.ClientStaminaManager;
import net.modzyyy.scarywater.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {

    private static final ResourceLocation EXPERIENCE_BAR_BACKGROUND_SPRITE =
            ResourceLocation.withDefaultNamespace(
                    "hud/experience_bar_background"
            );

    private static final ResourceLocation EXPERIENCE_BAR_PROGRESS_SPRITE =
            ResourceLocation.withDefaultNamespace(
                    "hud/experience_bar_progress"
            );


    @Inject(
            method = "renderExperienceLevel",
            at = @At("HEAD"),
            cancellable = true
    )
    private void scarywater$cancelExpLevelForStaminaBar(
            GuiGraphics guiGraphics,
            DeltaTracker deltaTracker,
            CallbackInfo ci
    ) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;

        if (player == null) return;
        if (!Config.STAMINA_ENABLED.getAsBoolean()) return;
        if (player.isCreative()) return;
        if (player.hasEffect(MobEffects.CONDUIT_POWER)) return;

        double stamina = ClientStaminaManager.getStamina();
        double maxStamina = Config.MAX_STAMINA.get();

        boolean showStamina =
                player.isInWater()
                        || stamina < maxStamina;

        if (!showStamina) {
            return;
        }

        ci.cancel();
    }

    @Inject(
            method = "renderExperienceBar",
            at = @At("HEAD"),
            cancellable = true
    )
    private void scarywater$renderStaminaBar(
            GuiGraphics guiGraphics,
            int x,
            CallbackInfo ci
    ) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;

        if (player == null) {
            return;
        }

        if (!Config.STAMINA_ENABLED.getAsBoolean()) {
            return;
        }

        if (player.isCreative()) {
            return;
        }

        if (player.hasEffect(MobEffects.CONDUIT_POWER)) {
            return;
        }

        double stamina = ClientStaminaManager.getStamina();
        double maxStamina = Config.MAX_STAMINA.get();

        boolean showStamina =
                player.isInWater()
                        || stamina < maxStamina;

        if (!showStamina) {
            return;
        }

        int y = guiGraphics.guiHeight() - 32 + 3;

        renderStaminaBar(
                guiGraphics,
                x,
                y,
                stamina,
                maxStamina
        );

        ci.cancel();
    }

    private void renderStaminaBar(
            GuiGraphics guiGraphics,
            int x,
            int y,
            double stamina,
            double maxStamina
    ) {
        int filledWidth = (int)(
                (stamina / maxStamina) * 183.0F
        );

        filledWidth = Math.max(
                0,
                Math.min(
                        filledWidth,
                        182
                )
        );

        RenderSystem.enableBlend();

        guiGraphics.blitSprite(
                EXPERIENCE_BAR_BACKGROUND_SPRITE,
                x,
                y,
                182,
                5
        );

        if (filledWidth > 0) {

            int color = parseStaminaColor();

            float red =
                    ((color >> 16) & 0xFF) / 255.0F;

            float green =
                    ((color >> 8) & 0xFF) / 255.0F;

            float blue =
                    (color & 0xFF) / 255.0F;

            guiGraphics.setColor(
                    red,
                    green,
                    blue,
                    1.0F
            );

            guiGraphics.blitSprite(
                    EXPERIENCE_BAR_PROGRESS_SPRITE,
                    182,
                    5,
                    0,
                    0,
                    x,
                    y,
                    filledWidth,
                    5
            );

            guiGraphics.setColor(
                    1.0F,
                    1.0F,
                    1.0F,
                    1.0F
            );
        }

        RenderSystem.disableBlend();
    }

    private int parseStaminaColor() {
        String value = Config.STAMINA_COLOR.get();

        try {
            if (value.startsWith("#")) {
                value = value.substring(1);
            }

            return Integer.parseInt(value, 16);

        } catch (Exception ignored) {
            return 0x55FF55;
        }
    }
}