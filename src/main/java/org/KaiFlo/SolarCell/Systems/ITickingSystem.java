package org.KaiFlo.SolarCell.Systems;

import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.protocol.Vector3i;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockComponentChunk;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;

import java.util.List;
import java.util.Set;

public interface ITickingSystem {

    void accept(Ref<ChunkStore> blockRef, List<Component<ChunkStore>> foundComponents, Archetype<ChunkStore> archetype, Vector3i globalPosition, BlockComponentChunk blockComponentChunk, CommandBuffer<ChunkStore> commandBuffer, World world, Set<Ref<ChunkStore>> blockRefs);
}
