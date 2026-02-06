package org.KaiFlo.SolarCell.Components.EnergySource.Implementations;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import org.KaiFlo.SolarCell.Components.EnergySource.AbstractEnergySource;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class SolarCellComponent extends AbstractEnergySource implements Component<ChunkStore> {
    public static final BuilderCodec<SolarCellComponent> CODEC = BuilderCodec.builder(SolarCellComponent.class, SolarCellComponent::new).build();

    private final HytaleLogger Logger = HytaleLogger.getLogger();
    private long energyRatePerTick = 5;

    private SolarCellComponent copyFrom(SolarCellComponent other) {
        this.energyRatePerTick = other.energyRatePerTick;
        return this;
    }

    @NullableDecl
    @Override
    public Component<ChunkStore> clone() {
        try {
            super.clone();
        } catch (CloneNotSupportedException e) {
            Logger.atWarning().log("Cloning of " + this.getClass().getName() + " failed.");
        }
        return new SolarCellComponent().copyFrom(this);
    }

    @Override
    public long getEnergyRatePerTick() {
        return energyRatePerTick;
    }

    public void setEnergyRatePerTick(long energyRatePerTick) {
        this.energyRatePerTick = energyRatePerTick;
    }
}
