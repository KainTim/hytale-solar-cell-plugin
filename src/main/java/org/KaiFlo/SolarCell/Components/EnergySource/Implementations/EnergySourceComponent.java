package org.KaiFlo.SolarCell.Components.EnergySource.Implementations;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import org.KaiFlo.SolarCell.Components.EnergySource.AbstractEnergySource;
import org.KaiFlo.SolarCell.SolarCellPlugin;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class EnergySourceComponent extends AbstractEnergySource implements Component<ChunkStore> {
    public static final BuilderCodec<EnergySourceComponent> CODEC = BuilderCodec.builder(EnergySourceComponent.class, EnergySourceComponent::new).build();

    private final HytaleLogger Logger = HytaleLogger.getLogger();
    private long energyRatePerTick = 5;

    public static ComponentType<ChunkStore, EnergySourceComponent> getComponentType() {
        return SolarCellPlugin.get().getSolarCellComponentType();
    }

    private EnergySourceComponent copyFrom(EnergySourceComponent other) {
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
        return new EnergySourceComponent().copyFrom(this);
    }

    @Override
    public long getEnergyRatePerTick() {
        return energyRatePerTick;
    }

    public void setEnergyRatePerTick(long energyRatePerTick) {
        this.energyRatePerTick = energyRatePerTick;
    }
}
