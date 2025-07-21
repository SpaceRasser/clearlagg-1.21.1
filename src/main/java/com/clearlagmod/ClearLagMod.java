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
    private static final int CLEAR_INTERVAL_SECONDS = 1800; // 5 минут
    private int tickCounter = 0;

    public ClearLagMod() {
        NeoForge.EVENT_BUS.register(this); // Используем NeoForge вместо MinecraftForge
    }

    @SubscribeEvent
    public void onServerTick(ServerTickEvent.Post event) {
        tickCounter++;
        if (tickCounter >= CLEAR_INTERVAL_SECONDS * 20) {
            tickCounter = 0;

            // Проходимся по всем измерениям сервера
            for (ServerLevel level : event.getServer().getAllLevels()) {
                clearDroppedItems(level);
            }
        }
    }

    private void clearDroppedItems(ServerLevel level) {
        int removedItems = 0;

        // AABB, охватывающий весь мир (±30 миллионов блоков — лимит мира Minecraft)
        AABB worldBounds = new AABB(-30000000, -64, -30000000, 30000000, 320, 30000000);

        for (ItemEntity item : level.getEntitiesOfClass(ItemEntity.class, worldBounds)) {
            System.out.println("[DEBUG] Найден предмет в " + level.dimension().location() + ": " + item.getItem());
            item.discard();
            removedItems++;
        }

        if (removedItems > 0) {
            level.getServer().getPlayerList().broadcastSystemMessage(
                    Component.literal("§a[ClearLag] Удалено предметов в " + level.dimension().location() + ": " + removedItems),
                    false
            );
        }
    }

}