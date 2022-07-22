package svinerus.buildtogether.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockEvent;
import org.jetbrains.annotations.NotNull;
import svinerus.buildtogether.building.BlockPlacement;
import svinerus.buildtogether.building.Building;

public class BlockPlaced extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final Building building;
    private final BlockEvent blockEvent;
    private final BlockPlacement blockPlacement; // place or break

    public BlockPlaced(Building building, BlockEvent blockEvent, BlockPlacement blockPlacement) {
        this.building = building;
        this.blockEvent = blockEvent;
        this.blockPlacement = blockPlacement;
    }


    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public BlockEvent getBlockEvent() {
        return blockEvent;
    }

    public BlockPlacement getBlockPlacement() {
        return blockPlacement;
    }

    public Building getBuilding() {
        return building;
    }
}
