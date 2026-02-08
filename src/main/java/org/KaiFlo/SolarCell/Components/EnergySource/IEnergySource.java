package org.KaiFlo.SolarCell.Components.EnergySource;

public interface IEnergySource {

    /**
     * @return True if energy source is endless, False otherwise
     */
    boolean isEndless();

    /**
     * @return If the energy source is not endless, returns the Capacity of the energy source, otherwise -1
     */
    long getEnergyCapacity();

    /**
     * @return The amount of energy the source produces per Tick
     */
    long getGeneratesPerTick();

    void setGeneratesPerTick(long generatesPerTick);

    void setEnergyCapacity(long energyCapacity);
}
