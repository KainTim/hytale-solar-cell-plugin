package org.KaiFlo.SolarCell;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import org.KaiFlo.SolarCell.Commands.ExampleCommand;
import org.KaiFlo.SolarCell.Components.EnergyConsumer.Implementations.EnergyConsumerComponent;
import org.KaiFlo.SolarCell.Components.EnergySource.Implementations.EnergySourceComponent;
import org.KaiFlo.SolarCell.Components.EnergyStorage.Implementations.EnergyStorageComponent;
import org.KaiFlo.SolarCell.Systems.EnergySource.EnergySourceInitializerSystem;
import org.KaiFlo.SolarCell.Systems.EnergySource.EnergyProducerTickingSystem;

import javax.annotation.Nonnull;

public class SolarCellPlugin extends JavaPlugin {

    protected static SolarCellPlugin instance;

    public static SolarCellPlugin get() {
        return instance;
    }

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    private ComponentType<ChunkStore, EnergySourceComponent> energySourceComponentType;
    private ComponentType<ChunkStore, EnergyConsumerComponent> energyConsumerComponentType;
    private ComponentType<ChunkStore, EnergyStorageComponent> energyStorageComponentType;

    public SolarCellPlugin(@Nonnull JavaPluginInit init) {
        super(init);
        LOGGER.atInfo().log("Hello from " + this.getName() + " version " + this.getManifest().getVersion().toString());
    }

    @Override
    protected void setup() {
        instance = this;
        LOGGER.atInfo().log("Setting up plugin " + this.getName());

        energySourceComponentType = this.getChunkStoreRegistry().registerComponent(EnergySourceComponent.class, "EnergySource", EnergySourceComponent.CODEC);
        energyConsumerComponentType = this.getChunkStoreRegistry().registerComponent(EnergyConsumerComponent.class, "EnergyConsumer", EnergyConsumerComponent.CODEC);
        energyStorageComponentType = this.getChunkStoreRegistry().registerComponent(EnergyStorageComponent.class, "EnergyStorage", EnergyStorageComponent.CODEC);

        this.getCommandRegistry().registerCommand(new ExampleCommand(this.getName(), this.getManifest().getVersion().toString()));

        this.getChunkStoreRegistry().registerSystem(new EnergySourceInitializerSystem());
        this.getChunkStoreRegistry().registerSystem(new EnergyProducerTickingSystem());

    }

    public ComponentType<ChunkStore, EnergySourceComponent> getEnergySourceComponentType() {
        return energySourceComponentType;
    }

    public ComponentType<ChunkStore, EnergyConsumerComponent> getEnergyConsumerComponentType() {
        return energyConsumerComponentType;
    }

    public ComponentType<ChunkStore, EnergyStorageComponent> getEnergyStorageComponentType() {
        return energyStorageComponentType;
    }
} 