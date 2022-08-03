package svinerus.buildtogether.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import svinerus.buildtogether.building.Building;

public class LayerFinishedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final Building building;

    public LayerFinishedEvent(Building building) {
        this.building = building;
    }


    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public Building getBuilding() {
        return building;
    }
}