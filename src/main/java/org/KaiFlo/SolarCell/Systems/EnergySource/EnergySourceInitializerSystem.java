package org.KaiFlo.SolarCell.Systems.EnergySource;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.RefSystem;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.server.core.modules.block.BlockModule;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import org.KaiFlo.SolarCell.Components.EnergySource.Implementations.EnergySourceComponent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class EnergySourceInitializerSystem extends RefSystem<ChunkStore> {
    @Override
    public void onEntityAdded(@NonNullDecl Ref<ChunkStore> ref, @NonNullDecl AddReason addReason, @NonNullDecl Store<ChunkStore> store, @NonNullDecl CommandBuffer<ChunkStore> commandBuffer) {
        BlockModule.BlockStateInfo blockInfo = commandBuffer.getComponent(ref, BlockModule.BlockStateInfo.getComponentType());
        if(blockInfo == null) return;

        WorldChunk worldChunk = commandBuffer.getComponent(blockInfo.getChunkRef(), WorldChunk.getComponentType());
        if(worldChunk == null) return;

        int x = ChunkUtil.xFromBlockInColumn(blockInfo.getIndex());
        int y = ChunkUtil.yFromBlockInColumn(blockInfo.getIndex());
        int z = ChunkUtil.zFromBlockInColumn(blockInfo.getIndex());

        worldChunk.setTicking(x, y, z, true);
        HytaleLogger.getLogger().atInfo().log(String.valueOf(worldChunk.isTicking(x, y, z)));
    }


    @Override
    public void onEntityRemove(@NonNullDecl Ref ref, @NonNullDecl RemoveReason removeReason, @NonNullDecl Store store, @NonNullDecl CommandBuffer commandBuffer) {
        //Nothing to do yet
    }

    @NullableDecl
    @Override
    public Query<ChunkStore> getQuery() {
        return Query.and(EnergySourceComponent.getComponentType(), BlockModule.BlockStateInfo.getComponentType());
    }
}
