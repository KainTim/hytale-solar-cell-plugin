package org.KaiFlo.SolarCell.Components.EnergySource.Implementations;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import org.KaiFlo.SolarCell.Components.EnergySource.IEnergySource;
import org.KaiFlo.SolarCell.SolarCellPlugin;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import static org.KaiFlo.SolarCell.Helpers.BlockHelper.HyLogger;

public class EnergySourceComponent implements Component<ChunkStore>, IEnergySource {
    public static final BuilderCodec<EnergySourceComponent> CODEC = BuilderCodec.builder(EnergySourceComponent.class, EnergySourceComponent::new)
            .append(new KeyedCodec<>("GeneratesPerTick", Codec.LONG),
                    (component, value) -> component.generatesPerTick = value,
                    (component) -> component.generatesPerTick
            )
            .addValidator(Validators.greaterThanOrEqual(0L))
            .documentation("GeneratesPerTick defines the Sources GeneratesPerTick")
            .add()
            .append(new KeyedCodec<>("EnergyCapacity", Codec.LONG),
                    (component, value) -> component.energyCapacity = value,
                    (component) -> component.energyCapacity
            )
            .addValidator(Validators.greaterThanOrEqual(-1L))
            .documentation("EnergyCapacity defines how long energy can be produced (Set to -1 if endless energy production)")
            .add()
            .build();

    private long generatesPerTick = 5;
    private long energyCapacity = -1;

    public static ComponentType<ChunkStore, EnergySourceComponent> getComponentType() {
        return SolarCellPlugin.get().getEnergySourceComponentType();
    }

    private EnergySourceComponent copyFrom(EnergySourceComponent other) {
        this.generatesPerTick = other.generatesPerTick;
        return this;
    }

    @NullableDecl
    @Override
    public Component<ChunkStore> clone() {
        try {
            super.clone();
        } catch (CloneNotSupportedException e) {
            HyLogger.atWarning().log("Cloning of " + this.getClass().getName() + " failed.");
        }
        return new EnergySourceComponent().copyFrom(this);
    }

    @Override
    public boolean isEndless() {
        return energyCapacity == -1;
    }

    @Override
    public long getEnergyCapacity() {
        return energyCapacity;
    }

    @Override
    public void setEnergyCapacity(long energyCapacity) {
        this.energyCapacity = energyCapacity;
    }

    @Override
    public long getGeneratesPerTick() {
        return generatesPerTick;
    }

    @Override
    public void setGeneratesPerTick(long generatesPerTick) {
        this.generatesPerTick = generatesPerTick;
    }
}
