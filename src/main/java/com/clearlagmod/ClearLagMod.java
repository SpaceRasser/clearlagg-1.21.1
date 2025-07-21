package com.clearlagmod;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;

@Mod("clearlagmod")
public class ClearLagMod {
    private static final int CLEAR_INTERVAL_SECONDS = 1800; // 30 минут
    private static final int TICKS_PER_SECOND = 20;
    // В секундах: за сколько до начала очистки шлём уведомления
    private static final int[] WARNING_SECONDS = {300, 60, 10}; // 5 минут, 1 минута, 10 секунд

    private int tickCounter = 0;
    // Флаги, чтобы не слать одно и то же уведомление несколько раз за цикл
    private final boolean[] warningsSent = new boolean[WARNING_SECONDS.length];

    public ClearLagMod() {
        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onServerTick(ServerTickEvent.Post event) {
        tickCounter++;

        int totalTicks = CLEAR_INTERVAL_SECONDS * TICKS_PER_SECOND;
        int ticksLeft = totalTicks - tickCounter;
        int secondsLeft = ticksLeft / TICKS_PER_SECOND;

        // Проверяем каждое заданное время предупреждения
        for (int i = 0; i < WARNING_SECONDS.length; i++) {
            if (!warningsSent[i] && secondsLeft == WARNING_SECONDS[i]) {
                broadcastWarning(event.getServer().getAllLevels().iterator().next(), WARNING_SECONDS[i]);
                warningsSent[i] = true;
            }
        }

        // Если пора чистить — делаем работу и сбрасываем счётчики
        if (tickCounter >= totalTicks) {
            tickCounter = 0;
            clearAllLevels(event);
            // Сбрасываем флаги предупреждений для нового цикла
            for (int i = 0; i < warningsSent.length; i++) {
                warningsSent[i] = false;
            }
        }
    }

    private void broadcastWarning(ServerLevel anyLevel, int seconds) {
        String humanTime;
        if (seconds >= 60) {
            humanTime = (seconds / 60) + " мин.";
        } else {
            humanTime = seconds + " сек.";
        }
        anyLevel.getServer().getPlayerList().broadcastSystemMessage(
                Component.literal("§e[ClearLag] Внимание! Очистка через " + humanTime + "!"),
                false
        );
    }

    private void clearAllLevels(ServerTickEvent.Post event) {
        for (ServerLevel level : event.getServer().getAllLevels()) {
            clearDroppedItems(level);
        }
    }

    private void clearDroppedItems(ServerLevel level) {
        int removedItems = 0;
        AABB worldBounds = new AABB(-30000000, -64, -30000000, 30000000, 320, 30000000);

        for (ItemEntity item : level.getEntitiesOfClass(ItemEntity.class, worldBounds)) {
            item.discard();
            removedItems++;
        }

        if (removedItems > 0) {
            level.getServer().getPlayerList().broadcastSystemMessage(
                    Component.literal("§a[ClearLag] Удалено предметов в "
                            + level.dimension().location() + ": " + removedItems),
                    false
            );
        }
    }
}
