package org.KaiFlo.SolarCell.Systems.EnergySource;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.server.core.asset.type.blocktick.BlockTickStrategy;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockComponentChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection;
import com.hypixel.hytale.server.core.universe.world.chunk.section.ChunkSection;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import org.KaiFlo.SolarCell.Components.EnergySource.Implementations.SolarCellComponent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class SolarCellTickingSystem extends EntityTickingSystem<ChunkStore> {

    @Override
    public void tick(float v, int i, @NonNullDecl ArchetypeChunk<ChunkStore> archetypeChunk, @NonNullDecl Store<ChunkStore> store, @NonNullDecl CommandBuffer<ChunkStore> commandBuffer) {
        var blockSection = archetypeChunk.getComponent(i, BlockSection.getComponentType());
        if (blockSection == null || blockSection.getTickingBlocksCount() != 0) return;

        var chunkSection = archetypeChunk.getComponent(i, ChunkSection.getComponentType());
        if (chunkSection == null) return;

        var blockComponentChunk = commandBuffer.getComponent(chunkSection.getChunkColumnReference(), BlockComponentChunk.getComponentType());
        var worldChunk = commandBuffer.getComponent(chunkSection.getChunkColumnReference(), WorldChunk.getComponentType());
        if (blockComponentChunk == null || worldChunk == null) return;

        var world = worldChunk.getWorld();

        blockSection.forEachTicking(blockComponentChunk, commandBuffer, chunkSection.getY(),
                (_, _, localX, localY, localZ, _) -> {
                    var blockRef = blockComponentChunk.getEntityReference(ChunkUtil.indexBlockInColumn(localX, localY, localZ));
                    if (blockRef == null) return BlockTickStrategy.IGNORED;
                    var solarCellComponent = commandBuffer.getComponent(blockRef, SolarCellComponent.getComponentType());
                    if (solarCellComponent == null) return BlockTickStrategy.IGNORED;

                    int globalX = localX + (worldChunk.getX() * 32);
                    int globalZ = localZ + (worldChunk.getZ() * 32);

                    world.execute(() -> {
                        world.setBlock(globalX + 1, localY, globalZ, "Rock_Ice");
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
