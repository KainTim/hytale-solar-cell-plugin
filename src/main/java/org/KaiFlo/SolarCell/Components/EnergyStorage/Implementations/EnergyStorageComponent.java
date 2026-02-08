package org.KaiFlo.SolarCell.Components.EnergyStorage.Implementations;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import org.KaiFlo.SolarCell.Components.EnergyStorage.IEnergyStorage;
import org.KaiFlo.SolarCell.SolarCellPlugin;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import static org.KaiFlo.SolarCell.Helpers.BlockHelper.HyLogger;

public class EnergyStorageComponent implements Component<ChunkStore>, IEnergyStorage {
    public static final BuilderCodec<EnergyStorageComponent> CODEC = BuilderCodec.builder(EnergyStorageComponent.class, EnergyStorageComponent::new)
            .append(new KeyedCodec<>("MaxCapacity", Codec.LONG),
                    (component, value) -> component.maxCapacity = value,
                    (component) -> component.maxCapacity
            )
            .addValidator(Validators.greaterThanOrEqual(0L))
            .documentation("MaxCapacity defines the Storage MaxCapacity")
            .add()
            .append(new KeyedCodec<>("ExtractEnergyPerTick", Codec.LONG),
                    (component, value) -> component.extractEnergyPerTick = value,
                    (component) -> component.extractEnergyPerTick
            )
            .addValidator(Validators.greaterThanOrEqual(0L))
            .documentation("ExtractEnergyPerTick defines the Storage ExtractEnergyPerTick")
            .add()
            .append(new KeyedCodec<>("ReceiveEnergyPerTick", Codec.LONG),
                    (component, value) -> component.receiveEnergyPerTick = value,
                    (component) -> component.receiveEnergyPerTick
            )
            .addValidator(Validators.greaterThanOrEqual(0L))
            .documentation("ReceiveEnergyPerTick defines the Storage ReceiveEnergyPerTick")
            .add()
            .append(new KeyedCodec<>("CurrentEnergyAmount", Codec.LONG),
                    (component, value) -> component.currentEnergyAmount = value,
                    (component) -> component.currentEnergyAmount
            )
            .addValidator(Validators.greaterThanOrEqual(0L))
            .documentation("CurrentEnergyAmount defines the Storage CurrentEnergyAmount")
            .add()
            .build();


    private long maxCapacity;
    private long extractEnergyPerTick;
    private long currentEnergyAmount;
    private long receiveEnergyPerTick;

    @Override
    public long getMaxCapacity() {
        return maxCapacity;
    }

    @Override
    public void setMaxCapacity(long maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    @Override
    public long getExtractEnergyPerTick() {
        return extractEnergyPerTick;
    }

    @Override
    public void setExtractEnergyPerTick(long extractEnergyPerTick) {
        this.extractEnergyPerTick = extractEnergyPerTick;
    }

    @Override
    public long getCurrentEnergyAmount() {
        return currentEnergyAmount;
    }

    @Override
    public void setCurrentEnergyAmount(long currentEnergyAmount) {
        this.currentEnergyAmount = currentEnergyAmount;
    }

    @Override
    public long getReceiveEnergyPerTick() {
        return receiveEnergyPerTick;
    }

    @Override
    public void setReceiveEnergyPerTick(long receiveEnergyPerTick) {
        this.receiveEnergyPerTick = receiveEnergyPerTick;
    }

    @Override
    public double getCurrentEnergyToCapacityRatio(){
        return (double) currentEnergyAmount / maxCapacity;
    }

    @Override
    public long extractEnergy(long requiredEnergy) {
        var extractedEnergy = Math.min(currentEnergyAmount, Math.min(requiredEnergy, extractEnergyPerTick));
        currentEnergyAmount -= extractedEnergy;
        return extractedEnergy;
    }

    @Override
    public long receiveEnergy(long inputEnergy) {
        var receivedEnergy = Math.min(maxCapacity - currentEnergyAmount, Math.min(inputEnergy, receiveEnergyPerTick));
        currentEnergyAmount += receivedEnergy;
        return receivedEnergy;
    }

    private EnergyStorageComponent copyFrom(EnergyStorageComponent other) {
        this.maxCapacity = other.maxCapacity;
        this.currentEnergyAmount = other.currentEnergyAmount;
        this.receiveEnergyPerTick = other.receiveEnergyPerTick;
        this.extractEnergyPerTick = other.extractEnergyPerTick;
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
        return new EnergyStorageComponent().copyFrom(this);
    }

    public static ComponentType<ChunkStore, EnergyStorageComponent> getComponentType() {
        return SolarCellPlugin.get().getEnergyStorageComponentType();
    }

}
