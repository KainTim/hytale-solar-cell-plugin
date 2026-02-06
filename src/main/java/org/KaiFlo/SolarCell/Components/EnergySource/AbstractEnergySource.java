package org.KaiFlo.SolarCell.Components.EnergySource;

public class AbstractEnergySource implements  IEnergySource{
    @Override
    public boolean isEndless() {
        return true;
    }

    @Override
    public long getEnergyCapacity() {
        return -1;
    }

    @Override
    public long getEnergyRatePerTick() {
        return 1;
    }
}
