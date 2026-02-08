package org.KaiFlo.SolarCell.Systems.EnergyStorage.TickingImplementations;

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

import static org.KaiFlo.SolarCell.Helpers.BlockHelper.HyLogger;
import static org.KaiFlo.SolarCell.Helpers.BlockHelper.executeForCubeAroundChunkSafe;
import static org.KaiFlo.SolarCell.Helpers.ComponentHelper.getComponentOfType;

public class BatteryStorageTicking implements ITickingSystem {

    @Override
    public void accept(Ref<ChunkStore> blockRef, List<Component<ChunkStore>> foundComponents, Archetype<ChunkStore> archetype, Vector3i globalPosition, BlockComponentChunk blockComponentChunk, CommandBuffer<ChunkStore> commandBuffer, World world) {
        var energyStorage = getComponentOfType(foundComponents, EnergyStorageComponent.class).orElse(null);
        if (energyStorage == null) return;
        var energySourceComponent = getComponentOfType(foundComponents, EnergySourceComponent.class).orElse(null);
        if (energySourceComponent != null) return;
        executeForCubeAroundChunkSafe(globalPosition.x, globalPosition.y, globalPosition.z, 5, false, world, commandBuffer,
            (x, y, z, targetRef, _, _) -> {
                if (energyStorage.getCurrentEnergyAmount() >= energyStorage.getMaxCapacity()) {
                    return;
                }

                var targetEnergyStorage = commandBuffer.getComponent(targetRef, EnergyStorageComponent.getComponentType());
                if (targetEnergyStorage == null) return;
                if (targetEnergyStorage.getCurrentEnergyAmount() < energyStorage.getCurrentEnergyAmount()) return;

                long diff = targetEnergyStorage.getCurrentEnergyAmount()
                    - energyStorage.getCurrentEnergyAmount();
                var diffWasNegative = diff < 0;
                diff = Math.abs(diff);

                long extractTarget = Math.min(
                    Math.ceilDiv(diff,2),
                    Math.min(
                        targetEnergyStorage.getExtractEnergyPerTick(),
                        energyStorage.getReceiveEnergyPerTick()
                    )
                );
                if (extractTarget<=0){
                    return;
                }
                if (diffWasNegative) {
                    transmitEnergy(targetEnergyStorage,energyStorage,extractTarget,new Vector3i(x,y,z), globalPosition.x, globalPosition.y, globalPosition.z);
                }else {
                    transmitEnergy(energyStorage,targetEnergyStorage,extractTarget,globalPosition, x, y, z);
                }

            }
        );
    }

    private static void transmitEnergy( EnergyStorageComponent energyStorage, EnergyStorageComponent targetEnergyStorage,long extractTarget, Vector3i globalPosition, int x, int y, int z) {
        long energy = 1;
        if (extractTarget > 1 && !(energyStorage.getCurrentEnergyAmount()+1>=energyStorage.getMaxCapacity())) {
            energy = targetEnergyStorage.extractEnergy(extractTarget);
            HyLogger.atInfo().log("Extracted " + energy + "/" + extractTarget +
                " |" + targetEnergyStorage.getCurrentEnergyAmount() + "| from storage" +
                " at Block " + x + ", " + y + ", " + z + ", now at " +
                targetEnergyStorage.getCurrentEnergyAmount() + "/" + targetEnergyStorage.getMaxCapacity());
        }

        long inserted = energyStorage.receiveEnergy(energy);
        if (inserted < energy) {
            var received = targetEnergyStorage.receiveEnergy(energy - inserted);
            HyLogger.atInfo().log("TO MUCH:Inserted " + received + "/" + (energy - inserted) +
                " |" + targetEnergyStorage.getCurrentEnergyAmount() + "| into storage" +
                " at Block " + x + ", " + y + ", " + z + ", now at " +
                targetEnergyStorage.getCurrentEnergyAmount() + "/" + targetEnergyStorage.getMaxCapacity());
        }

        if (inserted != 0) {
            HyLogger.atInfo().log("Inserted " + inserted + "/" + energy +
                " |" + energyStorage.getCurrentEnergyAmount() + "| into storage" +
                " at Block " + globalPosition.x + ", " + globalPosition.y + ", " + globalPosition.z + ", now at " +
                energyStorage.getCurrentEnergyAmount() + "/" + energyStorage.getMaxCapacity());
        }
    }
}
