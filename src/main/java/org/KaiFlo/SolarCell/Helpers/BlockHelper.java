package org.KaiFlo.SolarCell.Helpers;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.server.core.modules.block.BlockModule;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockComponentChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.*;

public class BlockHelper {
    public static final HytaleLogger HyLogger = HytaleLogger.getLogger();

    public static void executeForCubeAround(int x, int y, int z, int size, boolean own, Callback callback) {
        for (int xOffset = 0; xOffset < size; xOffset++) {
            for (int yOffset = 0; yOffset < size; yOffset++) {
                for (int zOffset = 0; zOffset < size; zOffset++) {
                    var xPos = (xOffset - size / 2) + x;
                    var yPos = (yOffset - size / 2) + y;
                    var zPos = (zOffset - size / 2) + z;
                    if (!(xPos == x && yPos == y && zPos == z)) {
                        callback.accept(xPos, yPos, zPos);
                    } else if (own) {
                        callback.accept(xPos, yPos, zPos);
                    }
                }
            }
        }
    }

    public static void executeForCubeAroundChunkSafe(
        int centerX, int centerY, int centerZ,
        int size,
        boolean includeCenter,
        World world,
        CommandBuffer<ChunkStore> commandBuffer,
        ChunkSafeCallback chunkSafeCallback
        ) {
        int halfSize = size / 2;
        Map<Long, List<BlockPos>> positionsByChunk = new HashMap<>();

        for (int xOffset = -halfSize; xOffset <= halfSize; xOffset++) {
            for (int yOffset = -halfSize; yOffset <= halfSize; yOffset++) {
                for (int zOffset = -halfSize; zOffset <= halfSize; zOffset++) {

                    int xPos = centerX + xOffset;
                    int yPos = centerY + yOffset;
                    int zPos = centerZ + zOffset;

                    if (!includeCenter && xPos == centerX && yPos == centerY && zPos == centerZ) {
                        continue;
                    }

                    int chunkX = Math.floorDiv(xPos, 32);
                    int chunkZ = Math.floorDiv(zPos, 32);
                    long chunkIndex = ChunkUtil.indexChunk(chunkX, chunkZ);

                    int localX = Math.floorMod(xPos, 32);
                    int localZ = Math.floorMod(zPos, 32);

                    positionsByChunk
                        .computeIfAbsent(chunkIndex, _ -> new ArrayList<>())
                        .add(new BlockPos(xPos, yPos, zPos, localX, localZ));
                }
            }
        }
        world.execute(() -> {
            for (var entry : positionsByChunk.entrySet()) {
                long chunkIndex = entry.getKey();
                List<BlockPos> blockPositions = entry.getValue();

                WorldChunk chunk = world.getChunkIfLoaded(chunkIndex);
                if (chunk == null) continue;

                var blockComponentChunk = commandBuffer.getComponent(
                    chunk.getReference(),
                    BlockComponentChunk.getComponentType()
                );
                if (blockComponentChunk == null) continue;

                for (BlockPos pos : blockPositions) {
                    int index = ChunkUtil.indexBlockInColumn(
                        pos.localX(), pos.y(), pos.localZ()
                    );

                    var targetRef = blockComponentChunk.getEntityReference(index);
                    if (targetRef == null) continue;

                    chunkSafeCallback.accept(
                        pos.x(), pos.y(), pos.z(),
                        targetRef,
                        blockComponentChunk,
                        chunk
                    );
                }
            }
        });


    }
    record BlockPos(int x, int y, int z, int localX, int localZ) {}
    public interface Callback {
        void accept(int x, int y, int z);
    }
    public interface ChunkSafeCallback {

        void accept(int x, int y, int z, Ref<ChunkStore> targetRef, BlockComponentChunk blockComponentChunk, WorldChunk targetChunk);
    }

    public static void setBlockRefTicking(Ref<ChunkStore> blockRef, @NonNullDecl CommandBuffer<ChunkStore> commandBuffer) {
        BlockModule.BlockStateInfo blockInfo = commandBuffer.getComponent(blockRef, BlockModule.BlockStateInfo.getComponentType());
        if (blockInfo == null) return;

        WorldChunk worldChunk = commandBuffer.getComponent(blockInfo.getChunkRef(), WorldChunk.getComponentType());
        if (worldChunk == null) return;

        int x = ChunkUtil.xFromBlockInColumn(blockInfo.getIndex());
        int y = ChunkUtil.yFromBlockInColumn(blockInfo.getIndex());
        int z = ChunkUtil.zFromBlockInColumn(blockInfo.getIndex());

        worldChunk.setTicking(x, y, z, true);
    }
}


