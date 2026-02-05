package org.KaiFlo.SolarCell;

import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
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

    public ExampleCommand(String pluginName, String pluginVersion) {
        super("test", "Prints a test message from the " + pluginName + " plugin.");
        this.setPermissionGroup(GameMode.Adventure); // Allows the command to be used by anyone, not just OP
        this.pluginName = pluginName;
        this.pluginVersion = pluginVersion;
    }

    @Override
    protected void executeSync(@Nonnull CommandContext ctx) {
        ctx.sendMessage(Message.raw("Hello from the " + pluginName + " v" + pluginVersion + " plugin!"));
        var chunkStore = Objects.requireNonNull(Universe.get().getDefaultWorld()).getChunkStore();
        LongSet chunkIndexes = chunkStore.getChunkIndexes();
        chunkIndexes.forEach(chunkIndex -> {
            var ref = chunkStore.getChunkReference(chunkIndex);
            if (ref == null) return;
            Universe.get().getDefaultWorld().execute(() -> {
                LOGGER.atInfo().log("Chunk Ref: "+ref);
                Archetype<ChunkStore> archetype = Universe.get().getDefaultWorld().getChunkStore().getStore().getArchetype(ref);
                for (int i = 0; i < archetype.length(); i++) {
                    var a = archetype.get(i);
                    if (a == null) continue;

                    LOGGER.atInfo().log(a.getTypeClass().getName());
                }
            });

        });
        LOGGER.atInfo().log(chunkIndexes.longStream().mapToObj(Long::toString).collect(Collectors.joining(", ")));
    }
}