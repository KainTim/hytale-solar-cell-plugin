package org.KaiFlo.SolarCell.Systems.EnergySource;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.server.core.asset.type.blocktick.BlockTickStrategy;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockComponentChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection;
import com.hypixel.hytale.server.core.universe.world.chunk.section.ChunkSection;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import dev.zkiller.energystorage.components.EnergyStorageBlockComponent;
import org.KaiFlo.SolarCell.Components.EnergySource.Implementations.EnergySourceComponent;
import org.KaiFlo.SolarCell.Helpers.BlockHelper;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class EnergyProducerTickingSystem extends EntityTickingSystem<ChunkStore> {
    private final HytaleLogger LOGGER = HytaleLogger.getLogger();

    @Override
    public void tick(float v, int i, @NonNullDecl ArchetypeChunk<ChunkStore> archetypeChunk, @NonNullDecl Store<ChunkStore> store, @NonNullDecl CommandBuffer<ChunkStore> commandBuffer) {
        var blockSection = archetypeChunk.getComponent(i, BlockSection.getComponentType());
        if (blockSection == null || blockSection.getTickingBlocksCount() != 0) return;

        var chunkSection = archetypeChunk.getComponent(i, ChunkSection.getComponentType());
        if (chunkSection == null) return;

        var blockComponentChunk = commandBuffer.getComponent(chunkSection.getChunkColumnReference(), BlockComponentChunk.getComponentType());
        var worldChunk = commandBuffer.getComponent(chunkSection.getChunkColumnReference(), WorldChunk.getComponentType());
        if (blockComponentChunk == null || worldChunk == null) return;

        blockSection.forEachTicking(blockComponentChunk, commandBuffer, chunkSection.getY(),
                (blockCompChunk, _, localX, localY, localZ, _) -> {
                    var blockRef = blockCompChunk.getEntityReference(ChunkUtil.indexBlockInColumn(localX, localY, localZ));
                    if (blockRef == null) return BlockTickStrategy.IGNORED;
                    var thisEnergySourceComponent = commandBuffer.getComponent(blockRef, EnergySourceComponent.getComponentType());
                    var thisEnergyStorageComponent = commandBuffer.getComponent(blockRef, EnergyStorageBlockComponent.getComponentType());
                    if (thisEnergySourceComponent == null || thisEnergyStorageComponent == null)
                        return BlockTickStrategy.IGNORED;

                    int globalX = localX + (worldChunk.getX() * 32);
                    int globalZ = localZ + (worldChunk.getZ() * 32);

                    BlockHelper.executeForCubeAround(globalX, localY, globalZ, 5, false, (x, y, z) -> {
                        var index = ChunkUtil.indexBlockInColumn(x, y, z);
                        var targetRef = blockCompChunk.getEntityReference(index);
                        if (targetRef == null) return;
                        var targetEnergySource = commandBuffer.getComponent(targetRef, EnergySourceComponent.getComponentType());
                        var targetEnergyStorage = commandBuffer.getComponent(targetRef, EnergyStorageBlockComponent.getComponentType());
                        if (targetEnergySource == null || targetEnergyStorage == null) return;

                        var energy = targetEnergyStorage.extractEnergy(targetEnergySource.getEnergyRatePerTick(), false);
                        var inserted = thisEnergyStorageComponent.receiveEnergy(energy, false);
                        LOGGER.atInfo().log("Inserted " + inserted + "/" + energy + " |" + targetEnergyStorage.getEnergyStored() + "| into storage" +
                                " at Block " + globalX + ", " + localY + ", " + globalZ + ", " +
                                thisEnergyStorageComponent.getEnergyStored() + "/" + thisEnergyStorageComponent.getMaxEnergyStored());
                    });

                    return BlockTickStrategy.CONTINUE;
                });

    }

    @NullableDecl
    @Override
    public Query<ChunkStore> getQuery() {
        return Query.and(BlockSection.getComponentType(), ChunkSection.getComponentType());
    }
}
