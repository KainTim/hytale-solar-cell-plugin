package org.KaiFlo.SolarCell.Systems.EnergySource;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.RefSystem;
import com.hypixel.hytale.server.core.modules.block.BlockModule;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import org.KaiFlo.SolarCell.Components.EnergySource.Implementations.EnergySourceComponent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import static org.KaiFlo.SolarCell.Helpers.BlockHelper.setBlockRefTicking;

public class EnergySourceInitializerSystem extends RefSystem<ChunkStore> {
    @Override
    public void onEntityAdded(@NonNullDecl Ref<ChunkStore> ref, @NonNullDecl AddReason addReason, @NonNullDecl Store<ChunkStore> store, @NonNullDecl CommandBuffer<ChunkStore> commandBuffer) {
        setBlockRefTicking(ref, commandBuffer);
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
