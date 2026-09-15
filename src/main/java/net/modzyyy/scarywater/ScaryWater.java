package net.modzyyy.scarywater;

import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.slf4j.Logger;

@Mod(ScaryWater.MODID)
public class ScaryWater {

    public static final String MODID = "scarywater";

    public static final Logger LOGGER = LogUtils.getLogger();

    public ScaryWater(
            IEventBus modEventBus,
            ModContainer modContainer
    ) {

        NeoForge.EVENT_BUS.register(this);

        modEventBus.addListener(this::registerPayloads);

        modContainer.registerConfig(
                ModConfig.Type.COMMON,
                Config.SPEC
        );

        modContainer.registerExtensionPoint(
                IConfigScreenFactory.class,
                ConfigurationScreen::new
        );
    }

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent.Post event) {

        Player player = event.getEntity();

        if (player.level().isClientSide()) {
            return;
        }

        if (!Config.STAMINA_ENABLED.getAsBoolean()) {
            return;
        }

        if (player.hasEffect(MobEffects.CONDUIT_POWER)) {
            return;
        }

        if (!player.isInWater()) {

            double regenPerTick =
                    Config.STAMINA_REGEN.get() / 20.0;

            StaminaManager.regenerate(
                    player,
                    regenPerTick
            );

        } else {

            boolean moving =
                    player.getPersistentData()
                            .getBoolean("ScaryWaterMoving");

            if (moving) {

                if (player.isSwimming()) {

                    double drainPerTick =
                            Config.SWIMMING_DRAIN.get() / 20.0;

                    StaminaManager.drain(
                            player,
                            drainPerTick
                    );

                } else {

                    double drainPerTick =
                            Config.BOBBING_DRAIN.get() / 20.0;

                    StaminaManager.drain(
                            player,
                            drainPerTick
                    );
                }
            }
        }

        if (player instanceof ServerPlayer serverPlayer) {

            PacketDistributor.sendToPlayer(
                    serverPlayer,
                    new StaminaPayload(
                            StaminaManager.getStamina(player)
                    )
            );
        }
    }
    @SubscribeEvent
    public void onExhaustedPlayerTick(PlayerTickEvent.Post event) {

        Player player = event.getEntity();

        if (!Config.STAMINA_ENABLED.getAsBoolean()) {
            return;
        }

        if (!player.isInWater()) {
            return;
        }

        if (player.hasEffect(MobEffects.CONDUIT_POWER)) {
            return;
        }

        if (StaminaManager.getStamina(player) > 0.1) {
            return;
        }
    }

    private void registerPayloads(RegisterPayloadHandlersEvent event) {

        var registrar = event.registrar("1");

        registrar.playToServer(
                PlayerInputPayload.TYPE,
                PlayerInputPayload.STREAM_CODEC,
                ScaryWater::handlePlayerInput
        );

        registrar.playToClient(
                StaminaPayload.TYPE,
                StaminaPayload.STREAM_CODEC,
                ScaryWater::handleStamina
        );
    }

    private static void handlePlayerInput(
            PlayerInputPayload payload,
            IPayloadContext context
    ) {
        context.enqueueWork(() -> {

            if (context.player() instanceof Player player) {
                player.getPersistentData().putBoolean(
                        "ScaryWaterMoving",
                        payload.moving()
                );

                player.getPersistentData().putBoolean(
                        "ScaryWaterJumping",
                        payload.jumping()
                );
            }

        });
    }

    private static void handleStamina(
            StaminaPayload payload,
            IPayloadContext context
    ) {
        context.enqueueWork(() -> {
            ClientStaminaManager.setStamina(payload.stamina());
        });
    }
}
