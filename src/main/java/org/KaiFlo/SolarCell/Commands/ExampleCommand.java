package org.KaiFlo.SolarCell.Commands;

import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.KaiFlo.SolarCell.Helpers.BlockHelper;

import javax.annotation.Nonnull;
import java.util.Objects;

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
        World defaultWorld = Objects.requireNonNull(Universe.get().getDefaultWorld());
        defaultWorld.execute(() -> {

            Store<EntityStore> store = defaultWorld.getEntityStore().getStore();
            var playerRef = ctx.senderAsPlayerRef();
            var playerTransform = store.getComponent(Objects.requireNonNull(playerRef), TransformComponent.getComponentType());
            Vector3i playerPosition = Objects.requireNonNull(playerTransform).getPosition().toVector3i();
            var size = sizeArg.get(ctx);
            if (size == null) size = 5;
            BlockHelper.executeForCubeAround(playerPosition.x, playerPosition.y, playerPosition.z, size,true, (x, y, z) -> {
                defaultWorld.breakBlock(x, y, z, 0);
            });
        });
    }

}
