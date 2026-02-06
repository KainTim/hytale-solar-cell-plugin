package org.KaiFlo.SolarCell;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import org.KaiFlo.SolarCell.Commands.ExampleCommand;
import org.KaiFlo.SolarCell.Components.EnergySource.Implementations.SolarCellComponent;
import org.KaiFlo.SolarCell.Systems.EnergySource.SolarCellInitializer;
import org.KaiFlo.SolarCell.Systems.EnergySource.SolarCellTickingSystem;

import javax.annotation.Nonnull;

public class SolarCellPlugin extends JavaPlugin {

    protected static SolarCellPlugin instance;

    public static SolarCellPlugin get() {
        return instance;
    }

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    private ComponentType<ChunkStore, SolarCellComponent> solarCellComponentType;

    public SolarCellPlugin(@Nonnull JavaPluginInit init) {
        super(init);
        LOGGER.atInfo().log("Hello from " + this.getName() + " version " + this.getManifest().getVersion().toString());
    }

    @Override
    protected void setup() {
        instance = this;
        LOGGER.atInfo().log("Setting up plugin " + this.getName());

        solarCellComponentType = this.getChunkStoreRegistry().registerComponent(SolarCellComponent.class, "SolarCell", SolarCellComponent.CODEC);

        this.getCommandRegistry().registerCommand(new ExampleCommand(this.getName(), this.getManifest().getVersion().toString()));

        this.getChunkStoreRegistry().registerSystem(new SolarCellInitializer());
        this.getChunkStoreRegistry().registerSystem(new SolarCellTickingSystem());

    }

    public ComponentType<ChunkStore, SolarCellComponent> getSolarCellComponentType() {
        return solarCellComponentType;
    }
} 