package org.KaiFlo.SolarCell;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.protocol.InteractionType;

import com.hypixel.hytale.protocol.SimpleBlockInteraction;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;

import com.hypixel.hytale.server.core.modules.entity.component.DisplayNameComponent;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInteraction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.client.UseBlockInteraction;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockComponentChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.NotificationUtil;

import com.hypixel.hytale.protocol.BlockPosition;
import org.KaiFlo.SolarCell.Components.EnergyStorage.Implementations.EnergyStorageComponent;

import javax.annotation.Nonnull;

public class StorageIntercationListener extends SimpleInstantInteraction {
    public static final BuilderCodec<StorageIntercationListener> CODEC = BuilderCodec.builder(
            StorageIntercationListener.class, StorageIntercationListener::new, SimpleInstantInteraction.CODEC
    ).build();

    @Override
    protected void firstRun(@Nonnull InteractionType interactionType, @Nonnull InteractionContext interactionContext, @Nonnull CooldownHandler cooldownHandler) {
        CommandBuffer<EntityStore> commandBuffer = interactionContext.getCommandBuffer();
        Ref<EntityStore> ref = interactionContext.getEntity();
        BlockPosition pos = interactionContext.getTargetBlock();
        Player player = commandBuffer.getComponent(ref, Player.getComponentType());
        World world = commandBuffer.getExternalData().getWorld();

        if (interactionType.getValue() == InteractionType.Use.getValue()) {

            int chunkX = Math.floorDiv(pos.x, 32);
            int chunkZ = Math.floorDiv(pos.z, 32);
            int localX = Math.floorMod(pos.x, 32);
            int localZ = Math.floorMod(pos.z, 32);

            world.execute(() -> {
                var chunkStore = world.getChunkStore().getStore();

                var targetChunk = world.getChunk(ChunkUtil.indexChunk(chunkX, chunkZ));
                if (targetChunk == null) return;

                var blockComponentChunk = chunkStore.getComponent(
                        targetChunk.getReference(),
                        BlockComponentChunk.getComponentType()
                );
                if (blockComponentChunk == null) return;

                int index = ChunkUtil.indexBlockInColumn(localX, pos.y, localZ);
                var targetRef = blockComponentChunk.getEntityReference(index);
                if (targetRef == null) return;

                var energyStorage = chunkStore.getComponent(targetRef, EnergyStorageComponent.getComponentType());
                if (energyStorage == null) return;
                NotificationUtil.sendNotificationToUniverse(String.format("%d/%d (%.2f%%)",
                        energyStorage.getCurrentEnergyAmount(),
                        energyStorage.getMaxCapacity(),
                        energyStorage.getCurrentEnergyToCapacityRatio() * 100
                ));
            });
        }
    }
}
