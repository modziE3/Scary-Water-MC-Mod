package net.modzyyy.scarywater.mixin;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.effect.MobEffects;
import net.modzyyy.scarywater.ClientStaminaManager;
import net.modzyyy.scarywater.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {

    private boolean scarywater$isExhausted(LocalPlayer player) {

        return Config.STAMINA_ENABLED.getAsBoolean()
                && !player.hasEffect(MobEffects.CONDUIT_POWER)
                && ClientStaminaManager.getStamina() <= 0.0;
    }

    @Inject(
            method = "aiStep",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/player/Input;tick(ZF)V",
                    shift = At.Shift.AFTER
            )
    )
    private void scarywater$blockJumpWhenExhausted(
            CallbackInfo ci
    ) {

        LocalPlayer player =
                (LocalPlayer) (Object) this;

        if (player.isCreative()) {
            return;
        }

        if (!player.isInWater()) {
            return;
        }

        if (player.horizontalCollision) {
            return;
        }

        if (scarywater$isExhausted(player)) {
            player.input.jumping = false;
            player.setSwimming(false);
            player.setSprinting(false);
        }
    }

    @Redirect(
            method = "aiStep",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/KeyMapping;isDown()Z"
            )
    )
    private boolean scarywater$blockSprintWhenExhausted(
            KeyMapping keyMapping
    ) {

        LocalPlayer player =
                (LocalPlayer) (Object) this;

        if (player.isCreative()) {
            return keyMapping.isDown();
        }

        if (keyMapping == Minecraft.getInstance().options.keySprint
                && player.isInWater()
                && scarywater$isExhausted(player)) {

            return false;
        }

        return keyMapping.isDown();
    }
}