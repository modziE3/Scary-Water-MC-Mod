package net.modzyyy.scarywater.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.modzyyy.scarywater.Config;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.common.extensions.ILivingEntityExtension;
import net.neoforged.neoforge.fluids.FluidType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ILivingEntityExtension.class)
public interface LivingEntityMixin {

    @Inject(
            method = "jumpInFluid",
            at = @At("HEAD"),
            cancellable = true
    )
    private void scarywater$limitWaterHeight(
            FluidType type,
            CallbackInfo ci
    ) {
        ILivingEntityExtension extension =
                (ILivingEntityExtension) (Object) this;

        LivingEntity entity = extension.self();

        if (!(entity instanceof Player player)) {
            return;
        }

        if (type != NeoForgeMod.WATER_TYPE.value()) {
            return;
        }

        if (!Config.ENABLED.getAsBoolean()) {
            return;
        }

        double waterSurface = scarywater$getWaterSurface(player);

        if (Double.isNaN(waterSurface)) {
            return;
        }

        double eyeY = player.getEyeY();

        if (player.horizontalCollision) {
            return;
        }

        double allowedEyeHeight = Config.ALLOWED_EYE_HEIGHT.get();

        if (eyeY > waterSurface + allowedEyeHeight) {
            ci.cancel();
        }
    }

    private static double scarywater$getWaterSurface(Player player) {

        AABB box = player.getBoundingBox();

        int minX = (int) Math.floor(box.minX);
        int maxX = (int) Math.floor(box.maxX);

        int minZ = (int) Math.floor(box.minZ);
        int maxZ = (int) Math.floor(box.maxZ);

        int minY = (int) Math.floor(box.minY) - 1;
        int maxY = (int) Math.ceil(box.maxY) + 1;

        double highestSurface = Double.NaN;

        BlockPos.MutableBlockPos pos =
                new BlockPos.MutableBlockPos();

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                for (int y = minY; y <= maxY; y++) {

                    pos.set(x, y, z);

                    FluidState fluid =
                            player.level().getFluidState(pos);

                    if (fluid.is(FluidTags.WATER)) {

                        double surface =
                                y + fluid.getHeight(
                                        player.level(),
                                        pos
                                );

                        if (Double.isNaN(highestSurface)
                                || surface > highestSurface) {
                            highestSurface = surface;
                        }
                    }
                }
            }
        }

        return highestSurface;
    }
}