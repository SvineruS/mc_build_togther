package svinerus.buildtogether.building;

import org.bukkit.Location;
import svinerus.buildtogether.schema.generator.Creator;
import svinerus.buildtogether.utils.Config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public record BuildingsManager(HashMap<String, Building> buildings) {
    public static BuildingsManager instance;

// todo save to cfg

    public void create(String buildingName, String schematicName, Location location) throws IOException, IllegalArgumentException {
        if (buildings.containsKey(buildingName))
            throw new IllegalArgumentException("Building with name " + buildingName + " already exists");

        var schema = Creator.Create(schematicName, location);
        buildings.put(buildingName, new Building(buildingName, schema));
    }

    public void remove(String name) {
        if (!buildings.containsKey(name))
            throw new IllegalArgumentException("Building with name " + name + " does not exist");

        buildings.get(name).shutdown();
        buildings.remove(name);
    }

    public void shutdown() throws IOException {
        buildings.values().forEach(Building::shutdown);
        Config.saveBuildings(BuildingsManager.instance.buildings);
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
