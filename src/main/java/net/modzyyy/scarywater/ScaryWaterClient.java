package net.modzyyy.scarywater;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(
        modid = ScaryWater.MODID,
        value = Dist.CLIENT
)
public class ScaryWaterClient {

    private static boolean lastMoving = false;
    private static boolean lastJumping = false;

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {

        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null) {
            return;
        }

        boolean moving =
                minecraft.options.keyUp.isDown()
                        || minecraft.options.keyDown.isDown()
                        || minecraft.options.keyLeft.isDown()
                        || minecraft.options.keyRight.isDown()
                        || minecraft.options.keyJump.isDown();

        boolean jumping =
                minecraft.options.keyJump.isDown();

        if (moving != lastMoving || jumping != lastJumping) {

            lastMoving = moving;
            lastJumping = jumping;

            PacketDistributor.sendToServer(
                    new PlayerInputPayload(moving, jumping)
            );
        }
    }
}