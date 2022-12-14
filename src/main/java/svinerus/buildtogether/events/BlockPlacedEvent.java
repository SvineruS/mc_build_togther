package svinerus.buildtogether.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.jetbrains.annotations.NotNull;
import svinerus.buildtogether.building.BlockPlacement;
import svinerus.buildtogether.building.Building;

public class BlockPlacedEvent extends BuildTogetherEvent {
    private final Building building;
    private final BlockEvent blockEvent;
    private final BlockPlacement blockPlacement; // place or break

    public BlockPlacedEvent(Building building, BlockEvent blockEvent, BlockPlacement blockPlacement) {
        this.building = building;
        this.blockEvent = blockEvent;
        this.blockPlacement = blockPlacement;
        getPlayer(); // implicit check for `blockEvent` type
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

    public Player getPlayer() {
        if (blockEvent instanceof BlockPlaceEvent) return ((BlockPlaceEvent) blockEvent).getPlayer();
        if (blockEvent instanceof BlockBreakEvent) return ((BlockBreakEvent) blockEvent).getPlayer();
        throw new IllegalStateException("Unexpected value: " + blockEvent);
    }
}
