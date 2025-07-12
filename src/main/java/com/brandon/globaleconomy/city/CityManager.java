package com.brandon.globaleconomy.city;

import com.brandon.globaleconomy.city.claims.ClaimManager;
import com.brandon.globaleconomy.economy.currencies.CurrencyManager;
import com.brandon.globaleconomy.economy.impl.workers.*;
import com.brandon.globaleconomy.npc.impl.NPCSpawner;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.*;

public class CityManager {
    private final Map<String, City> cities = new HashMap<>();
    private final Map<String, City> cityByName = new HashMap<>();
    private ClaimManager claimManager = new ClaimManager(2);

    private static final String[] POSSIBLE_COLORS = {
            "#FF0000", "#00FF00", "#0000FF", "#FFFF00", "#FF00FF", "#00FFFF",
            "#FFA500", "#A52A2A", "#008000", "#800080", "#008080", "#000000"
    };

    private static final List<String> FOOD_RESOURCES = List.of(
            "BREAD", "COOKED_BEEF", "COOKED_PORKCHOP", "COOKED_CHICKEN",
            "SWEET_BERRIES", "GLOW_BERRIES", "CARROT", "POTATO", "MELON_SLICE"
    );

    public Map<String, City> getCities() {
        return cities;
    }

    public City getCity(String name) {
        return cities.get(name.toLowerCase());
    }

    public City getCityByChunk(Chunk chunk) {
        Location loc = chunk.getBlock(0, 0, 0).getLocation();
        return getCityAt(loc);
    }

    public City getCityByName(String name) {
        return cityByName.get(name);
    }

    public void addCity(City city) {
        cities.put(city.getName().toLowerCase(), city);
        cityByName.put(city.getName(), city);
        city.scanForResources(48);

        Location center = city.getLocation();
        World world = center.getWorld();
        int radius = 48;

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = center.getBlockY() - 10; y <= center.getBlockY() + 10; y++) {
                    Material type = world.getBlockAt(center.clone().add(x, y, z)).getType();
                    if (type == Material.DEAD_BUSH) {
                        city.addResource("STICK", 1);
                    }
                }
            }
        }

        if (!city.getWorkers().isEmpty()) {
            Bukkit.getLogger().info("City " + city.getName() + " already has workers, skipping default spawn.");
            return;
        }

        UUID mayorId = UUID.randomUUID();
        Worker mayor = new Mayor(city, "Mayor_" + city.getName(), mayorId);
        Worker farmer = new Farmer(city, "Farmer_" + city.getName(), UUID.randomUUID());
        Worker woodsman = new Woodsman(city, "Woodsman_" + city.getName(), UUID.randomUUID());
        Worker merchant = new Merchant(city, "Merchant_" + city.getName(), UUID.randomUUID());

        WorkerManager.getInstance().registerWorker(mayor);
        WorkerManager.getInstance().registerWorker(farmer);
        WorkerManager.getInstance().registerWorker(woodsman);
        WorkerManager.getInstance().registerWorker(merchant);

        for (int i = 1; i <= 4; i++) {
            Worker resident = new Resident(city, "Resident_" + i + "_" + city.getName(), UUID.randomUUID());
            WorkerManager.getInstance().registerWorker(resident);
            city.addWorker(resident);
            NPCSpawner.spawnWorkerNpc(resident, city.getLocation().clone().add(i - 2, 1, 2));
        }

        city.addWorker(mayor);
        city.addWorker(farmer);
        city.addWorker(woodsman);
        city.addWorker(merchant);

        Location baseSpawn = city.getLocation();
        Location[] spawnOffsets = new Location[] {
                baseSpawn.clone().add(2, 1, 0),
                baseSpawn.clone().add(-2, 1, 0),
                baseSpawn.clone().add(0, 1, 2),
                baseSpawn.clone().add(0, 1, -2)
        };

        NPCSpawner.spawnWorkerNpc(mayor, spawnOffsets[0]);
        NPCSpawner.spawnWorkerNpc(farmer, spawnOffsets[1]);
        NPCSpawner.spawnWorkerNpc(woodsman, spawnOffsets[2]);
        NPCSpawner.spawnWorkerNpc(merchant, spawnOffsets[3]);

        Bukkit.getLogger().info(city.getName() + " initialized with unemployment rate: " + city.getUnemploymentRate() + "%");
    }

    public int countBedsNear(Location center) {
        int radius = 50;
        int bedCount = 0;
        World world = center.getWorld();

        for (int x = -radius; x <= radius; x++) {
            for (int y = -5; y <= 5; y++) {
                for (int z = -radius; z <= radius; z++) {
                    Material blockType = world.getBlockAt(center.clone().add(x, y, z)).getType();
                    if (blockType.name().endsWith("_BED")) {
                        bedCount++;
                    }
                }
            }
        }
        return bedCount;
    }

    public void checkAndSpawnResidents(City city) {
        Map<String, Integer> stockpile = city.getResources();
        int foodCount = 0;

        for (String food : FOOD_RESOURCES) {
            foodCount += stockpile.getOrDefault(food, 0);
        }

        int bedCount = countBedsNear(city.getLocation());
        long residentCount = city.getWorkers().stream().filter(w -> w instanceof Resident).count();

        if (foodCount >= 40 && bedCount > residentCount) {
            stockpile.put("BREAD", stockpile.getOrDefault("BREAD", 0) - 6);
            spawnResident(city);
        }
    }

    private void spawnResident(City city) {
        UUID id = UUID.randomUUID();
        Worker resident = new Resident(city, "Resident_" + id.toString().substring(0, 6), id);

        Location spawnLoc = city.getLocation().clone().add(2 - new Random().nextInt(5), 1, 2 - new Random().nextInt(5));
        NPCSpawner.spawnWorkerNpc(resident, spawnLoc);
        WorkerManager.getInstance().registerWorker(resident);

        Bukkit.getLogger().info("A new Resident has joined the city of " + city.getName());
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

    public City getCityByMayor(UUID mayorId) {
        for (City city : cities.values()) {
            if (city.getMayorId() != null && city.getMayorId().equals(mayorId)) {
                return city;
            }
        }
        return null;
    }

    public boolean setMayor(String cityName, UUID newMayorId) {
        City city = getCity(cityName);
        if (city != null) {
            city.setMayorId(newMayorId);
            return true;
        }
        return false;
    }

    public boolean transferMayor(String cityName, UUID currentMayorId, UUID newMayorId) {
        City city = getCity(cityName);
        if (city != null && city.getMayorId() != null && city.getMayorId().equals(currentMayorId)) {
            city.setMayorId(newMayorId);
            return true;
        }
        return false;
    }

    public void addCityWithMayor(String name, String nation, Location location, int population, String color, String currencyName, UUID mayorId, String parentCityName) {
        City city = new City(name, nation, location, population, color, currencyName, mayorId);
        city.setParentCityName(parentCityName);
        city.setForceUseParentCurrency(true);
        addCity(city);
        buildBellPedestal(location);
    }

    public void addCityWithNpcMayor(String name, String nation, Location location, int population, String color,
                                    String currencyName, String parentCityName,
                                    boolean metalBacked, String backingMaterial, double backingRatio) {
        CurrencyManager currencyManager = CurrencyManager.getInstance();
        if (!currencyManager.hasCurrency(currencyName)) {
            currencyManager.createCurrency(currencyName, metalBacked, backingMaterial, backingRatio);
        }

        NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, name + "_Mayor");
        int mayorNpcId = npc.getId();

        City city = new City(name, nation, location, population, color, currencyName, mayorNpcId);
        city.setParentCityName(parentCityName);
        city.setForceUseParentCurrency(parentCityName != null);

        addCity(city);
        buildBellPedestal(location);
    }

    public void clear() {
        cities.clear();
        cityByName.clear();
    }

    public void addCityNoMayor(String name, String nation, Location location, int population, String color, String currencyName) {
        City city = new City(name, nation, location, population, color, currencyName);
        addCity(city);
        buildBellPedestal(location);
    }

    public City getCityAt(Location location) {
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

        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                for (int dy = 0; dy <= 2; dy++) {
                    world.getBlockAt(x + dx, y + dy, z + dz).setType(Material.AIR);
                }
            }
        }

        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                world.getBlockAt(x + dx, y, z + dz).setType(Material.COBBLESTONE);
            }
        }

        world.getBlockAt(x, y + 1, z).setType(Material.COBBLESTONE);
        world.getBlockAt(x, y + 2, z).setType(Material.BELL);
    }
}