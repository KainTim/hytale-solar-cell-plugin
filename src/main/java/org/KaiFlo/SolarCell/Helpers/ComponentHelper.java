package org.KaiFlo.SolarCell.Helpers;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;

import java.util.List;
import java.util.Optional;

public class ComponentHelper {
    public static<T> Optional<T> getComponentOfType(List<Component<ChunkStore>> components, Class<T> type){
        return components.stream()
                .filter(x -> x.getClass() == type)
                .map(type::cast)
                .findFirst();
    }
}
