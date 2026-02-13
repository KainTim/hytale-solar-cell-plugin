package org.KaiFlo.SolarCell.Systems.EnergyStorage.TickingImplementations;

import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.Vector3i;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockComponentChunk;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.util.NotificationUtil;
import org.KaiFlo.SolarCell.Components.EnergySource.Implementations.EnergySourceComponent;
import org.KaiFlo.SolarCell.Components.EnergyStorage.Implementations.EnergyStorageComponent;
import org.KaiFlo.SolarCell.Enums.BlockRotation;
import org.KaiFlo.SolarCell.Systems.ITickingSystem;

import java.util.List;

import static org.KaiFlo.SolarCell.Helpers.BlockHelper.*;
import static org.KaiFlo.SolarCell.Helpers.ComponentHelper.getComponentOfType;

public class BatteryStorageTicking implements ITickingSystem {

    @Override
    public void accept(Ref<ChunkStore> blockRef, List<Component<ChunkStore>> foundComponents, Archetype<ChunkStore> archetype, Vector3i globalPosition, BlockComponentChunk blockComponentChunk, CommandBuffer<ChunkStore> commandBuffer, World world) {
        var energyStorage = getComponentOfType(foundComponents, EnergyStorageComponent.class).orElse(null);
        if (energyStorage == null) return;
        var energySourceComponent = getComponentOfType(foundComponents, EnergySourceComponent.class).orElse(null);
        if (energySourceComponent != null) return;

        int blockRotationIndex = world.getBlockRotationIndex(globalPosition.x, globalPosition.y, globalPosition.z);
//        HyLogger.atInfo().log("Block at " + globalPosition.x+", "+ globalPosition.y+", " +globalPosition.z+": "+blockRotationIndex);

        var rotation = BlockRotation.getEnum(blockRotationIndex);

        executeForDirection(globalPosition.x, globalPosition.y, globalPosition.z, world, commandBuffer, rotation,
            (x, y, z, targetRef, _, _) -> {
                if (energyStorage.getCurrentEnergyAmount() >= energyStorage.getMaxCapacity()) return;
                var targetEnergyStorage = commandBuffer.getComponent(targetRef, EnergyStorageComponent.getComponentType());
                if (targetEnergyStorage == null) return;
                if (targetEnergyStorage.getCurrentEnergyAmount() < energyStorage.getCurrentEnergyAmount() && commandBuffer.getComponent(targetRef, EnergySourceComponent.getComponentType()) == null) return;

                var extractEnergy = Math.min(energyStorage.getReceiveEnergyPerTick(), targetEnergyStorage.getCurrentEnergyAmount());

                transmitEnergy(energyStorage,targetEnergyStorage,extractEnergy,globalPosition, x, y, z);

                //Input bei Lüftungsgitter
                //INPUT index 0 --> North
                //INPUT index 1 --> West
                //INPUT index 2 --> South
                //INPUT index 3 --> East
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
