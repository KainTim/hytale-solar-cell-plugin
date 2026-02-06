package org.KaiFlo.SolarCell;

import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.BlockPlacementSettings;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.ArgWrapper;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.WrappedArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.accessor.ChunkAccessor;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.longs.LongSet;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * This is an example command that will simply print the name of the plugin in chat when used.
 */
public class ExampleCommand extends CommandBase {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private final String pluginName;
    private final String pluginVersion;
    private final OptionalArg<Integer> sizeArg;

    public ExampleCommand(String pluginName, String pluginVersion) {
        super("test", "Prints a test message from the " + pluginName + " plugin.");
        this.setPermissionGroup(GameMode.Adventure); // Allows the command to be used by anyone, not just OP
        this.pluginName = pluginName;
        this.pluginVersion = pluginVersion;
        this.sizeArg = this.withOptionalArg("", "", ArgTypes.INTEGER);
    }

    @Override
    protected void executeSync(@Nonnull CommandContext ctx) {
        ctx.sendMessage(Message.raw("Hello from the " + pluginName + " v" + pluginVersion + " plugin!"));
        World defaultWorld = Universe.get().getDefaultWorld();
        var chunkStore = Objects.requireNonNull(defaultWorld).getChunkStore();
        LongSet chunkIndexes = chunkStore.getChunkIndexes();
        defaultWorld.execute(() -> {

            Store<EntityStore> store = defaultWorld.getEntityStore().getStore();
            Vector3i position = Objects.requireNonNull(store.getComponent(Objects.requireNonNull(ctx.senderAsPlayerRef()), TransformComponent.getComponentType()))
                    .getPosition()
                    .toVector3i();
            var size = sizeArg.get(ctx);
            if (size == null) size = 5;
            executeForCubeAround(position.x, position.y, position.z, size, (x, y, z) -> {
//                BlockType blockType = defaultWorld.getBlockType(x, y, z);
//                if (blockType != null) {
//                    LOGGER.atInfo().log(blockType.getId() + " at " + x + "," + y + "," + z);
//                }
                defaultWorld.breakBlock(x, y, z, 0);
            });
//            chunkIndexes.forEach(chunkIndex -> {
//                if (blockType == null) {
//                    LOGGER.atInfo().log("No blocktype found for chunk index: " + chunkIndex);
//                    return;
//                }
//                String id = blockType.getId();
//                LOGGER.atInfo().log("Block ID: " + id);
//            });
        });
    }

    void executeForCubeAround(int x, int y, int z, int size, Callback callback) {
        for (int x1 = x - size / 2; x1 < x + size / 2; x1++) {
            for (int y1 = y - size / 2; y1 < y + size / 2; y1++) {
                for (int z1 = z - size / 2; z1 < z + size / 2; z1++) {
                    callback.onBlockPosition(x1, y1, z1);
                }
            }
        }
    }

    interface Callback {
        void onBlockPosition(int x, int y, int z);
    }
}