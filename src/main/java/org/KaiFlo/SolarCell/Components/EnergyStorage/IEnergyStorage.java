package org.KaiFlo.SolarCell.Components.EnergyStorage;

public interface IEnergyStorage {

    long getMaxCapacity();

    void setMaxCapacity(long maxCapacity);

    long getExtractEnergyPerTick();

    void setExtractEnergyPerTick(long extractEnergyPerTick);

    long getCurrentEnergyAmount();

    void setCurrentEnergyAmount(long currentEnergyAmount);

    long getReceiveEnergyPerTick();

    void setReceiveEnergyPerTick(long receiveEnergyPerTick);

    long extractEnergy(long requiredEnergy);

    long receiveEnergy(long inputEnergy);

    double getCurrentEnergyToCapacityRatio();
}
