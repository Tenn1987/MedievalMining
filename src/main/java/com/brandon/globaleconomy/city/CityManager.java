package com.brandon.globaleconomy.city;

import com.brandon.globaleconomy.city.claims.ClaimManager;
import com.brandon.globaleconomy.economy.impl.workers.*;
import com.brandon.globaleconomy.npc.impl.NPCSpawner;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.EntityType;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CityManager {
    private final Map<String, City> cities = new HashMap<>();
    private final Map<String, City> cityByName = new HashMap<>();
    private ClaimManager claimManager = new ClaimManager(2); // or whatever radius you want

    // Available colors to randomly assign (extend or randomize as needed)
    private static final String[] POSSIBLE_COLORS = {
            "#FF0000", "#00FF00", "#0000FF", "#FFFF00", "#FF00FF", "#00FFFF",
            "#FFA500", "#A52A2A", "#008000", "#800080", "#008080", "#000000"
    };

    public Map<String, City> getCities() {
        return cities;
    }

    public City getCity(String name) {
        return cities.get(name.toLowerCase());
    }

    public City getCityByName(String name) {
        return cityByName.get(name);
    }

    // Add city with or without mayor (backwards compatible)
    public void addCity(City city) {
        cities.put(city.getName().toLowerCase(), city);
        cityByName.put(city.getName(), city);
        city.scanForResources(48);  // Scan for resources on add

        // === Hook: Create initial city workers ===
        UUID mayorId = UUID.randomUUID();
        Worker mayor = new Mayor(city, "Mayor_" + city.getName(), mayorId); // ✅ Correct
        Worker farmer = new Farmer(city, "Farmer_" + city.getName(), UUID.randomUUID());
        Worker woodsman = new Woodsman(city, "Woodsman_" + city.getName(), UUID.randomUUID());
        Worker merchant = new Merchant(city, "Merchant_" + city.getName(), UUID.randomUUID());


        WorkerManager.getInstance().registerWorker(mayor);
        WorkerManager.getInstance().registerWorker(farmer);
        WorkerManager.getInstance().registerWorker(woodsman);
        WorkerManager.getInstance().registerWorker(merchant);

        // If you're using Citizens, spawn the NPCs visibly:
        Location baseSpawn = city.getLocation();
        Location spawnOffset = baseSpawn.clone().add(1, 1, 1);

        NPCSpawner.spawnWorkerNpc(mayor, spawnOffset);
        NPCSpawner.spawnWorkerNpc(farmer, spawnOffset.clone().add(1, 0, 0));
        NPCSpawner.spawnWorkerNpc(woodsman, spawnOffset.clone().add(0, 0, 1));
        NPCSpawner.spawnWorkerNpc(merchant, spawnOffset.clone().add(-1, 0, 0));

    }

    public boolean removeCity(String name) {
        cityByName.remove(name);
        return cities.remove(name.toLowerCase()) != null;
    }

    public void scanAllCities() {
        for (City city : cities.values()) {
            city.scanForResources(48);
        }
    }

    public Map<String, Integer> getLocalResources() {
        return new HashMap<>();
    }

    public String getRandomColor() {
        int idx = (int) (Math.random() * POSSIBLE_COLORS.length);
        return POSSIBLE_COLORS[idx];
    }

    // Lookup city by mayor UUID
    public City getCityByMayor(UUID mayorId) {
        for (City city : cities.values()) {
            if (city.getMayorId() != null && city.getMayorId().equals(mayorId)) {
                return city;
            }
        }
        return null;
    }

    // Optional: change the mayor of a city
    public boolean setMayor(String cityName, UUID newMayorId) {
        City city = getCity(cityName);
        if (city != null) {
            city.setMayorId(newMayorId);
            return true;
        }
        return false;
    }

    // Optionally: transfer mayorship with a command
    public boolean transferMayor(String cityName, UUID currentMayorId, UUID newMayorId) {
        City city = getCity(cityName);
        if (city != null && city.getMayorId() != null && city.getMayorId().equals(currentMayorId)) {
            city.setMayorId(newMayorId);
            return true;
        }
        return false;
    }

    // === New city creation overloads for hybrid mayor support (PLAYER/NPC) ===

    /**
     * Add a city with a player mayor.
     */
    public void addCityWithMayor(String name, String nation, Location location, int population, String color, String currencyName, UUID mayorId, String parentCityName) {
        City city = new City(name, nation, location, population, color, currencyName, mayorId);
        city.setParentCityName(parentCityName);
        city.setForceUseParentCurrency(true);
        addCity(city);
        buildBellPedestal(location);
    }

    /**
     * Add a city with an NPC mayor.
     */
    public void addCityWithNpcMayor(String name, String nation, Location location, int population, String color, String currencyName, String parentCityName) {
        // Create the NPC
        NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, name + "_Mayor");
        int mayorNpcId = npc.getId();

        // Create the city with NPC mayor
        City city = new City(name, nation, location, population, color, currencyName, mayorNpcId);

        // Optional: assign parent if needed
        city.setParentCityName(parentCityName);
        city.setForceUseParentCurrency(parentCityName != null);

        // Add city to map and build bell
        addCity(city);
        buildBellPedestal(location);
    }

    public void clear() {
        cities.clear();
        cityByName.clear();
        // ...clear any other maps/fields you use!
    }

    /**
     * Add a city with no mayor specified (legacy/empty).
     */
    public void addCityNoMayor(String name, String nation, Location location, int population, String color, String currencyName) {
        City city = new City(name, nation, location, population, color, currencyName);
        addCity(city);
        buildBellPedestal(location); // Build bell pedestal
    }

    public City getCityAt(Location location) {
        // Example: check each city, see if location is inside bounds/claim (customize as needed)
        for (City city : cities.values()) {
            if (city.isLocationInCity(location)) return city;
        }
        return null;
    }

    private void buildBellPedestal(Location center) {
        World world = center.getWorld();
        int x = center.getBlockX();
        int y = center.getBlockY();
        int z = center.getBlockZ();

        // Clear space (3x3 up to 3 blocks tall)
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                for (int dy = 0; dy <= 2; dy++) {
                    world.getBlockAt(x + dx, y + dy, z + dz).setType(Material.AIR);
                }
            }
        }

        // Set 3x3 cobblestone base
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                world.getBlockAt(x + dx, y, z + dz).setType(Material.COBBLESTONE);
            }
        }

        // Raise center and place bell
        world.getBlockAt(x, y + 1, z).setType(Material.COBBLESTONE);
        world.getBlockAt(x, y + 2, z).setType(Material.BELL);
    }
}