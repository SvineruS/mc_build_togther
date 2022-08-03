package svinerus.buildtogether.building;

import org.bukkit.Location;
import svinerus.buildtogether.generator.Creator;
import svinerus.buildtogether.utils.storage.Storage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public record BuildingsManager(HashMap<String, Building> buildings) {
    public static BuildingsManager instance;

    public BuildingsManager(HashMap<String, Building> buildings) {
        this.buildings = buildings;
        buildings.values().forEach(Building::onEnable);
    }

    public Building create(String buildingName, String schematicName, Location location) throws IOException, IllegalArgumentException {
        if (buildings.containsKey(buildingName))
            throw new IllegalArgumentException("Building with name " + buildingName + " already exists");

        var schema = Creator.Create(schematicName, location);
        var building = new Building(buildingName, schema);
        buildings.put(buildingName, building);
        building.onEnable();
        return building;
    }

    public void remove(String name) {
        if (!buildings.containsKey(name))
            throw new IllegalArgumentException("Building with name " + name + " does not exist");

        buildings.get(name).shutdown();
        buildings.remove(name);
    }

    public void shutdown() throws IOException {
        buildings.values().forEach(Building::shutdown);
        Storage.Buildings.saveBuildings(BuildingsManager.instance.buildings);
    }

    public Building getBuilding(Location location) {
        return buildings.values().stream().filter(building -> building.isInside(location)).findFirst().orElse(null);
    }

    public Building getBuilding(String buildingName) {
        return buildings.get(buildingName);
    }

    public List<String> getNames() {
        return new ArrayList<>(buildings.keySet());
    }


}
