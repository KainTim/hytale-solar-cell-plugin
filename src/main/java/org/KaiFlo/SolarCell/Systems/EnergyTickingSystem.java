package org.KaiFlo.SolarCell.Systems;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.protocol.Vector3i;
import com.hypixel.hytale.server.core.asset.type.blocktick.BlockTickStrategy;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockComponentChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection;
import com.hypixel.hytale.server.core.universe.world.chunk.section.ChunkSection;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import org.KaiFlo.SolarCell.Helpers.BlockHelper;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.KaiFlo.SolarCell.Helpers.BlockHelper.HyLogger;

public class EnergyTickingSystem extends EntityTickingSystem<ChunkStore> {

    private final Map<List<ComponentType<ChunkStore, ?>>, Map.Entry<ITickingSystem,Set<Ref<ChunkStore>>>> componentsToTickingSystem = new HashMap<>();
    private long lastTime = 0L;


    @Override
    public void tick(float v, int archetypeIndex, @NonNullDecl ArchetypeChunk<ChunkStore> archetypeChunk, @NonNullDecl Store<ChunkStore> store, @NonNullDecl CommandBuffer<ChunkStore> commandBuffer) {
        var currentTime = System.nanoTime();
//        HyLogger.atInfo().log("V:"+v);
        if ((currentTime/1_000_000_000.0)-0.1> lastTime/1_000_000_000.0){
//            HyLogger.atInfo().log("Reset, currentTime: "+currentTime/1_000_000_000.0+", lastTime: "+lastTime/1_000_000_000.0);
            lastTime = currentTime;
            componentsToTickingSystem.forEach((_, iTickingSystemSetEntry) -> iTickingSystemSetEntry.getValue().clear());
        }
        var blockSection = archetypeChunk.getComponent(archetypeIndex, BlockSection.getComponentType());
        if (blockSection == null) return;

        var chunkSection = archetypeChunk.getComponent(archetypeIndex, ChunkSection.getComponentType());
        if (chunkSection == null) return;

        var blockComponentChunk = commandBuffer.getComponent(chunkSection.getChunkColumnReference(), BlockComponentChunk.getComponentType());
        var worldChunk = commandBuffer.getComponent(chunkSection.getChunkColumnReference(), WorldChunk.getComponentType());
        if (blockComponentChunk == null || worldChunk == null) return;

        var entrySet = componentsToTickingSystem.entrySet();
        var foundComponentTypes = new ArrayList<ComponentType<ChunkStore, ?>>();

        blockSection.forEachTicking(null, null, chunkSection.getY(), (_, _, localX, localY, localZ, _) -> {
            var blockRef = blockComponentChunk.getEntityReference(ChunkUtil.indexBlockInColumn(localX, localY, localZ));
            if (blockRef == null) {

//                HyLogger.atInfo().log("Ignored block at "+localX+", "+localY+", "+localZ);
                return BlockTickStrategy.CONTINUE;
            }

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
                .forEach(entry -> {
                    hasAny.set(true);
                    entry.getKey().accept(blockRef,foundComponents, archetype, globalPosition, blockComponentChunk, commandBuffer, worldChunk.getWorld(),entry.getValue());
                });

//            HyLogger.atInfo().log("Continued block at "+localX+", "+localY+", "+localZ);
            return BlockTickStrategy.CONTINUE;
        });

    }

    @NullableDecl
    @Override
    public Query<ChunkStore> getQuery() {
        return Query.and(BlockSection.getComponentType(), ChunkSection.getComponentType());
    }

    public EnergyTickingSystem withTickingSystemForComponentTypes(List<ComponentType<ChunkStore, ?>> componentTypes, ITickingSystem tickingSystem) {
        componentsToTickingSystem.put(componentTypes, Map.entry(tickingSystem, new HashSet<>()));
        return this;
    }
}
