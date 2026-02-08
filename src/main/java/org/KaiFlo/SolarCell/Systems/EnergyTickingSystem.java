package org.KaiFlo.SolarCell.Systems;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.protocol.Vector3i;
import com.hypixel.hytale.server.core.asset.type.blocktick.BlockTickStrategy;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockComponentChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection;
import com.hypixel.hytale.server.core.universe.world.chunk.section.ChunkSection;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class EnergyTickingSystem extends EntityTickingSystem<ChunkStore> {

    private final Map<List<ComponentType<ChunkStore, ?>>, ITickingSystem> componentsToTickingSystem = new HashMap<>();


    @Override
    public void tick(float v, int archetypeIndex, @NonNullDecl ArchetypeChunk<ChunkStore> archetypeChunk, @NonNullDecl Store<ChunkStore> store, @NonNullDecl CommandBuffer<ChunkStore> commandBuffer) {

        var blockSection = archetypeChunk.getComponent(archetypeIndex, BlockSection.getComponentType());
        if (blockSection == null || blockSection.getTickingBlocksCount() != 0) return;

        var chunkSection = archetypeChunk.getComponent(archetypeIndex, ChunkSection.getComponentType());
        if (chunkSection == null) return;

        var blockComponentChunk = commandBuffer.getComponent(chunkSection.getChunkColumnReference(), BlockComponentChunk.getComponentType());
        var worldChunk = commandBuffer.getComponent(chunkSection.getChunkColumnReference(), WorldChunk.getComponentType());
        if (blockComponentChunk == null || worldChunk == null) return;

        var entrySet = componentsToTickingSystem.entrySet();
        var foundComponentTypes = new ArrayList<ComponentType<ChunkStore, ?>>();

        blockSection.forEachTicking(blockComponentChunk, commandBuffer, chunkSection.getY(), (blockCompChunk, _, localX, localY, localZ, _) -> {
            var blockRef = blockCompChunk.getEntityReference(ChunkUtil.indexBlockInColumn(localX, localY, localZ));
            if (blockRef == null) return BlockTickStrategy.IGNORED;

            int globalX = localX + (worldChunk.getX() * 32);
            int globalZ = localZ + (worldChunk.getZ() * 32);
            var globalPosition = new Vector3i(globalX, localY, globalZ);


            var archetype = commandBuffer.getArchetype(blockRef);

            foundComponentTypes.clear();
            var foundComponents = new ArrayList<Component<ChunkStore>>();
            for (int i = 0; i < archetype.length(); i++) {
                var type = archetype.get(i);
                if (type == null) continue;
                foundComponentTypes.add(type);
                foundComponents.add(commandBuffer.getComponent(blockRef, type));
            }

            AtomicBoolean hasAny = new AtomicBoolean(false);
            entrySet.stream()
                .filter(entry -> foundComponentTypes.containsAll(entry.getKey())).map(Map.Entry::getValue)
                .forEach(tickingSystem -> {
                    hasAny.set(true);
                    tickingSystem.accept(foundComponents, archetype, globalPosition, blockCompChunk, commandBuffer, worldChunk.getWorld());
                });
            return hasAny.get() ? BlockTickStrategy.CONTINUE : BlockTickStrategy.IGNORED;
        });

    }

    @NullableDecl
    @Override
    public Query<ChunkStore> getQuery() {
        return Query.and(BlockSection.getComponentType(), ChunkSection.getComponentType());
    }

    public EnergyTickingSystem withTickingSystemForComponentTypes(List<ComponentType<ChunkStore, ?>> componentTypes, ITickingSystem tickingSystem) {
        componentsToTickingSystem.put(componentTypes, tickingSystem);
        return this;
    }
}
