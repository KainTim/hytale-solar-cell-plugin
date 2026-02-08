package org.KaiFlo.SolarCell.Systems.EnergyStorage;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.RefSystem;
import com.hypixel.hytale.server.core.modules.block.BlockModule;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import org.KaiFlo.SolarCell.Components.EnergyStorage.Implementations.EnergyStorageComponent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import static org.KaiFlo.SolarCell.Helpers.BlockHelper.HyLogger;
import static org.KaiFlo.SolarCell.Helpers.BlockHelper.setBlockRefTicking;

public class EnergyStorageInitializerSystem extends RefSystem<ChunkStore> {
    @Override
    public void onEntityAdded(@NonNullDecl Ref<ChunkStore> ref, @NonNullDecl AddReason addReason, @NonNullDecl Store<ChunkStore> store, @NonNullDecl CommandBuffer<ChunkStore> commandBuffer) {
        HyLogger.atInfo().log("onEntityAdded");
        setBlockRefTicking(ref, commandBuffer);
    }


    @Override
    public void onEntityRemove(@NonNullDecl Ref ref, @NonNullDecl RemoveReason removeReason, @NonNullDecl Store store, @NonNullDecl CommandBuffer commandBuffer) {
        //Nothing to do yet
    }

    @NullableDecl
    @Override
    public Query<ChunkStore> getQuery() {
        return Query.and(EnergyStorageComponent.getComponentType(), BlockModule.BlockStateInfo.getComponentType());
    }
}
