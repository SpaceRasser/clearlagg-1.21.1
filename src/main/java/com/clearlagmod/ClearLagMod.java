package com.clearlagmod;

import com.clearlagmod.config.ClearLagConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.Arrays;
import java.util.List;

@Mod("clearlagmod")
public class ClearLagMod {
    private static final int TICKS_PER_SECOND = 20;

    private int tickCounter;
    private boolean[] warningsSent;

    public ClearLagMod(ModContainer container, IEventBus modBus) {

        container.registerConfig(ModConfig.Type.COMMON, ClearLagConfig.SPEC);

        modBus.register(new ConfigListener(this));

        NeoForge.EVENT_BUS.register(this);
    }

    private void initializeConfig() {
        this.tickCounter = 0;
        List<? extends Integer> warns = ClearLagConfig.CONFIG.warningSeconds.get();
        this.warningsSent = new boolean[warns.size()];
    }

    @SubscribeEvent
    public void onServerTick(ServerTickEvent.Post event) {
        tickCounter++;

        int interval = ClearLagConfig.CONFIG.clearIntervalSeconds.get();
        int totalTicks = interval * TICKS_PER_SECOND;
        int secondsLeft = (totalTicks - tickCounter) / TICKS_PER_SECOND;

        List<? extends Integer> warns = ClearLagConfig.CONFIG.warningSeconds.get();
        for (int i = 0; i < warns.size(); i++) {
            int warnSec = warns.get(i);
            if (!warningsSent[i] && secondsLeft == warnSec) {
                broadcastWarning(event.getServer().getAllLevels().iterator().next(), warnSec);
                warningsSent[i] = true;
            }
        }

        if (tickCounter >= totalTicks) {
            tickCounter = 0;
            clearAllLevels(event);
            Arrays.fill(warningsSent, false);
        }
    }

    private void broadcastWarning(ServerLevel level, int seconds) {
        if (seconds >= 60) {
            level.getServer().getPlayerList().broadcastSystemMessage(
                    Component.translatable("clearlagmod.warning.minutes", seconds / 60),
                    false
            );
        } else {
            level.getServer().getPlayerList().broadcastSystemMessage(
                    Component.translatable("clearlagmod.warning.seconds", seconds),
                    false
            );
        }
    }

    private void clearAllLevels(ServerTickEvent.Post event) {
        for (ServerLevel level : event.getServer().getAllLevels()) {
            clearDroppedItems(level);
        }
    }

    private void clearDroppedItems(ServerLevel level) {
        int removedItems = 0;
        AABB worldBounds = new AABB(-3e7, -64, -3e7, 3e7, 320, 3e7);

        for (ItemEntity item : level.getEntitiesOfClass(ItemEntity.class, worldBounds)) {
            item.discard();
            removedItems++;
        }

        if (removedItems > 0) {
            String dimName = level.dimension().location().toString();
            level.getServer().getPlayerList().broadcastSystemMessage(
                    Component.translatable("clearlagmod.cleared", removedItems, dimName),
                    false
            );
        }
    }

    public static class ConfigListener {
        private final ClearLagMod mod;

        public ConfigListener(ClearLagMod mod) {
            this.mod = mod;
        }

        @SubscribeEvent
        public void onConfigLoading(ModConfigEvent.Loading event) {
            if (event.getConfig().getSpec() == ClearLagConfig.SPEC) {
                mod.initializeConfig();
            }
        }

        @SubscribeEvent
        public void onConfigReloading(ModConfigEvent.Reloading event) {
            if (event.getConfig().getSpec() == ClearLagConfig.SPEC) {
                mod.initializeConfig();
            }
        }
    }
}
