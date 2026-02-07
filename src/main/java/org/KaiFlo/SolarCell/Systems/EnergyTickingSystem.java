package org.KaiFlo.SolarCell.Systems;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.protocol.Vector3i;
import com.hypixel.hytale.server.core.asset.type.blocktick.BlockTickStrategy;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockComponentChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection;
import com.hypixel.hytale.server.core.universe.world.chunk.section.ChunkSection;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import org.KaiFlo.SolarCell.Components.EnergyConsumer.Implementations.EnergyConsumerComponent;
import org.KaiFlo.SolarCell.Components.EnergySource.Implementations.EnergySourceComponent;
import org.KaiFlo.SolarCell.Components.EnergyStorage.Implementations.EnergyStorageComponent;
import org.KaiFlo.SolarCell.Systems.EnergySource.IEnergySourceTicking;
import org.KaiFlo.SolarCell.Systems.EnergySource.SolarCellSourceTicking;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.List;

public class EnergyTickingSystem extends EntityTickingSystem<ChunkStore> {
    private final List<IEnergySourceTicking> energySourceTicking = List.of(new SolarCellSourceTicking());

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

                    int globalX = localX + (worldChunk.getX() * 32);
                    int globalZ = localZ + (worldChunk.getZ() * 32);
                    var globalPosition = new Vector3i(globalX, localY, globalZ);

                    var energySourceComponent = commandBuffer.getComponent(blockRef, EnergySourceComponent.getComponentType());
                    var energyConsumerComponent = commandBuffer.getComponent(blockRef, EnergyConsumerComponent.getComponentType());
                    var energyStorageComponent = commandBuffer.getComponent(blockRef, EnergyStorageComponent.getComponentType());

                    if (energySourceComponent != null && energyStorageComponent != null){
                        energySourceTicking.forEach(energySourceTicking -> energySourceTicking.accept(energySourceComponent, energyStorageComponent,globalPosition,blockCompChunk,commandBuffer));
                        return BlockTickStrategy.CONTINUE;
                    }


                    return BlockTickStrategy.IGNORED;
                });

    }

    @NullableDecl
    @Override
    public Query<ChunkStore> getQuery() {
        return Query.and(BlockSection.getComponentType(), ChunkSection.getComponentType());
    }
}
