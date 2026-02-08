package org.KaiFlo.SolarCell.Systems.EnergyStorage.TickingImplementations;

import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.protocol.Vector3i;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockComponentChunk;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import org.KaiFlo.SolarCell.Components.EnergySource.Implementations.EnergySourceComponent;
import org.KaiFlo.SolarCell.Components.EnergyStorage.Implementations.EnergyStorageComponent;
import org.KaiFlo.SolarCell.Systems.ITickingSystem;

import java.util.List;

import static org.KaiFlo.SolarCell.Helpers.BlockHelper.HyLogger;
import static org.KaiFlo.SolarCell.Helpers.BlockHelper.executeForCubeAroundChunkSafe;
import static org.KaiFlo.SolarCell.Helpers.ComponentHelper.getComponentOfType;

public class BatteryStorageTicking implements ITickingSystem {

    @Override
    public void accept(List<Component<ChunkStore>> foundComponents, Archetype<ChunkStore> archetype, Vector3i globalPosition, BlockComponentChunk blockComponentChunk, CommandBuffer<ChunkStore> commandBuffer, World world) {
        var energyStorage = getComponentOfType(foundComponents, EnergyStorageComponent.class).orElse(null);
        if (energyStorage == null) return;
        var energySourceComponent = getComponentOfType(foundComponents, EnergySourceComponent.class).orElse(null);
        if (energySourceComponent != null) return;
        if (energyStorage.getCurrentEnergyAmount() >= energyStorage.getMaxCapacity()) {
            return;
        }
        executeForCubeAroundChunkSafe(globalPosition.x, globalPosition.y, globalPosition.z, 5, false, world, commandBuffer,
            (x, y, z, targetRef, blockCompChunk, targetChunk) -> {

                var targetEnergyStorage = commandBuffer.getComponent(targetRef, EnergyStorageComponent.getComponentType());
                if (targetEnergyStorage == null) return;
                if (targetEnergyStorage.getCurrentEnergyAmount() < energyStorage.getCurrentEnergyAmount()) return;

                long energy = targetEnergyStorage.extractEnergy(Math.min(energyStorage.getMaxCapacity()-energyStorage.getCurrentEnergyAmount(),
                    Math.min(
                    targetEnergyStorage.getExtractEnergyPerTick(),
                    energyStorage.getReceiveEnergyPerTick()
                )));
                long inserted = energyStorage.receiveEnergy(energy);

                if (inserted != 0 && energyStorage.getCurrentEnergyAmount() != energyStorage.getMaxCapacity()) {
                    HyLogger.atInfo().log("Inserted " + inserted + "/" + energy +
                        " |" + targetEnergyStorage.getCurrentEnergyAmount() + "| into storage" +
                        " at Block " + globalPosition.x + ", " + globalPosition.y + ", " + globalPosition.z + ", " +
                        energyStorage.getCurrentEnergyAmount() + "/" + energyStorage.getMaxCapacity());
                }
            }
        );
    }
}
