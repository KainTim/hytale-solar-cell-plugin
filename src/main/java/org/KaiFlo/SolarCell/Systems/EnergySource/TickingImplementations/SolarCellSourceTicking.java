package org.KaiFlo.SolarCell.Systems.EnergySource.TickingImplementations;

import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.protocol.Vector3i;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockComponentChunk;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import org.KaiFlo.SolarCell.Components.EnergySource.Implementations.EnergySourceComponent;
import org.KaiFlo.SolarCell.Components.EnergyStorage.Implementations.EnergyStorageComponent;
import org.KaiFlo.SolarCell.Systems.ITickingSystem;

import java.util.List;
import java.util.Set;

import static org.KaiFlo.SolarCell.Helpers.BlockHelper.HyLogger;
import static org.KaiFlo.SolarCell.Helpers.ComponentHelper.getComponentOfType;

public class SolarCellSourceTicking implements ITickingSystem {

    @Override
    public void accept(Ref<ChunkStore> blockRef, List<Component<ChunkStore>> foundComponents, Archetype<ChunkStore> archetype, Vector3i globalPosition, BlockComponentChunk blockComponentChunk, CommandBuffer<ChunkStore> commandBuffer, World world, Set<Ref<ChunkStore>> blockRefs) {
        var energyStorage = getComponentOfType(foundComponents, EnergyStorageComponent.class).orElse(null);
        if (energyStorage == null) return;
        var energySource = getComponentOfType(foundComponents, EnergySourceComponent.class).orElse(null);
        if (energySource == null) return;
        var received = energyStorage.receiveEnergy(energySource.getGeneratesPerTick());
        if (received!= 0){
            HyLogger.atInfo().log("Block at " + globalPosition.x+", "+ globalPosition.y+", " +globalPosition.z+" received " + received + " Energy, now at "+energyStorage.getCurrentEnergyAmount());
        }
    }

}
