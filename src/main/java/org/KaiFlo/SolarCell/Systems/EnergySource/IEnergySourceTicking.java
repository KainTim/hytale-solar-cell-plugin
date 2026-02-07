package org.KaiFlo.SolarCell.Systems.EnergySource;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.protocol.Vector3i;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockComponentChunk;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import org.KaiFlo.SolarCell.Components.EnergySource.Implementations.EnergySourceComponent;
import org.KaiFlo.SolarCell.Components.EnergyStorage.Implementations.EnergyStorageComponent;

public interface IEnergySourceTicking{
    void accept (EnergySourceComponent thisEnergySource, EnergyStorageComponent thisEnergyStorage, Vector3i globalPosition, BlockComponentChunk blockComponentChunk, CommandBuffer<ChunkStore> commandBuffer);
}
