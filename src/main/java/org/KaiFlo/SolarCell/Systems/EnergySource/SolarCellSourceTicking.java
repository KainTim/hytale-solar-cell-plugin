package org.KaiFlo.SolarCell.Systems.EnergySource;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.protocol.Vector3i;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockComponentChunk;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import org.KaiFlo.SolarCell.Components.EnergySource.Implementations.EnergySourceComponent;
import org.KaiFlo.SolarCell.Components.EnergyStorage.Implementations.EnergyStorageComponent;
import org.KaiFlo.SolarCell.Helpers.BlockHelper;

import static org.KaiFlo.SolarCell.Helpers.BlockHelper.LOGGER;

public class SolarCellSourceTicking implements IEnergySourceTicking{
    @Override
    public void accept(EnergySourceComponent thisEnergySource, EnergyStorageComponent thisEnergyStorage, Vector3i globalPosition, BlockComponentChunk blockComponentChunk, CommandBuffer<ChunkStore> commandBuffer){

        BlockHelper.executeForCubeAround(globalPosition.x, globalPosition.y, globalPosition.z, 5, false, (x, y, z) -> {
            var index = ChunkUtil.indexBlockInColumn(x, y, z);
            var targetRef = blockComponentChunk.getEntityReference(index);
            if (targetRef == null) return;
            var targetEnergySource = commandBuffer.getComponent(targetRef, EnergySourceComponent.getComponentType());
            var targetEnergyStorage = commandBuffer.getComponent(targetRef, EnergyStorageComponent.getComponentType());
            if (targetEnergySource == null || targetEnergyStorage == null) return;

            var energy = targetEnergyStorage.extractEnergy(targetEnergySource.getGeneratesPerTick());
            var inserted = thisEnergyStorage.receiveEnergy(energy);
            LOGGER.atInfo().log("Inserted " + inserted + "/" + energy + " |" + targetEnergyStorage.getCurrentEnergyAmount() + "| into storage" +
                    " at Block " + globalPosition.x + ", " + globalPosition.y + ", " + globalPosition.z + ", " +
                    thisEnergyStorage.getCurrentEnergyAmount() + "/" + thisEnergyStorage.getMaxCapacity());
        });
    }
}
