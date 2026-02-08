package org.KaiFlo.SolarCell.Components.EnergyConsumer.Implementations;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import org.KaiFlo.SolarCell.Components.EnergyConsumer.IEnergyConsumer;
import org.KaiFlo.SolarCell.SolarCellPlugin;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import static org.KaiFlo.SolarCell.Helpers.BlockHelper.HyLogger;

public class EnergyConsumerComponent implements Component<ChunkStore>, IEnergyConsumer {
    public static final BuilderCodec<EnergyConsumerComponent> CODEC = BuilderCodec.builder(EnergyConsumerComponent.class, EnergyConsumerComponent::new)
            .append(new KeyedCodec<>("ConsumptionPerTick", Codec.LONG),
                    (component, value) -> component.consumptionPerTick = value,
                    (component) -> component.consumptionPerTick
            )
            .addValidator(Validators.greaterThanOrEqual(0L))
            .documentation("ConsumptionPerTick defines the Consumers ConsumptionPerTick")
            .add()
            .build();

    private long consumptionPerTick;
    private float workingCapabilityRatio;

    @Override
    public float getWorkingCapabilityRatio() {
        return workingCapabilityRatio;
    }

    @Override
    public void setWorkingCapabilityRatio(float workingCapabilityRatio) {
        this.workingCapabilityRatio = workingCapabilityRatio;
    }

    @Override
    public long getConsumptionPerTick() {
        return consumptionPerTick;
    }

    @Override
    public void setConsumptionPerTick(long consumptionPerTick) {
        this.consumptionPerTick = consumptionPerTick;
    }

    private EnergyConsumerComponent copyFrom(EnergyConsumerComponent other) {
        this.consumptionPerTick = other.consumptionPerTick;
        this.workingCapabilityRatio = other.workingCapabilityRatio;
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
        return new EnergyConsumerComponent().copyFrom(this);
    }

    public static ComponentType<ChunkStore, EnergyConsumerComponent> getComponentType() {
        return SolarCellPlugin.get().getEnergyConsumerComponentType();
    }
}
