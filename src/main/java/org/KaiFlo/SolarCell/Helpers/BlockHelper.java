package org.KaiFlo.SolarCell.Helpers;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.server.core.modules.block.BlockModule;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class BlockHelper {

    public static void executeForCubeAround(int x, int y, int z, int size, boolean own, Callback callback) {
        for (int xOffset = 0; xOffset < size; xOffset++) {
            for (int yOffset = 0; yOffset <size; yOffset++) {
                for (int zOffset = 0; zOffset < size; zOffset++) {
                    var xPos = (xOffset -size/2)+x;
                    var yPos = (yOffset -size/2)+y;
                    var zPos = (zOffset -size/2)+z;
                    if (!(xPos == x && yPos == y && zPos == z)) {
                        callback.accept(xPos, yPos, zPos);
                    }
                    else if (own) {
                        callback.accept(xPos, yPos, zPos);
                    }
                }
            }
        }
    }
    public interface Callback {
        void accept(int x, int y, int z);
    }

    public static void setBlockRefTicking(Ref<ChunkStore> blockRef, @NonNullDecl CommandBuffer<ChunkStore> commandBuffer) {
        BlockModule.BlockStateInfo blockInfo = commandBuffer.getComponent(blockRef, BlockModule.BlockStateInfo.getComponentType());
        if(blockInfo == null) return;

        WorldChunk worldChunk = commandBuffer.getComponent(blockInfo.getChunkRef(), WorldChunk.getComponentType());
        if(worldChunk == null) return;

        int x = ChunkUtil.xFromBlockInColumn(blockInfo.getIndex());
        int y = ChunkUtil.yFromBlockInColumn(blockInfo.getIndex());
        int z = ChunkUtil.zFromBlockInColumn(blockInfo.getIndex());

        worldChunk.setTicking(x, y, z, true);
        HytaleLogger.getLogger().atInfo().log(String.valueOf(worldChunk.isTicking(x, y, z)));
    }
}


