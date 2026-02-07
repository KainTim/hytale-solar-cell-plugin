package org.KaiFlo.SolarCell.Components.EnergyConsumer;

public interface IEnergyConsumer {
    float getWorkingCapabilityRatio();

    void setWorkingCapabilityRatio(float workingCapabilityRatio);

    long getConsumptionPerTick();

    void setConsumptionPerTick(long consumptionPerTick);
}
