package svinerus.buildtogether.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import svinerus.buildtogether.building.Building;

public class BuildingFinishedEvent extends BuildTogetherEvent {
    private final Building building;

    public BuildingFinishedEvent(Building building) {
        this.building = building;
    }

    public Building getBuilding() {
        return building;
    }
}
