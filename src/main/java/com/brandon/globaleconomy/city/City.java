package com.brandon.globaleconomy.city;

import com.brandon.globaleconomy.city.CityProductionManager;
import com.brandon.globaleconomy.economy.impl.workers.Worker;
import com.brandon.globaleconomy.economy.impl.workers.WorkerRole;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;

import java.io.Serializable;
import java.util.*;

public class City implements Serializable {
    private static final long serialVersionUID = 1L;

    // Core attributes
    private final String name;
    private final String nation;
    private final Location location;
    private final int population;
    private String color;
    private String primaryCurrency;
    private boolean independent = true;
    private String parentCityName = null;
    private boolean forceUseParentCurrency = false;
    private boolean currencyEnforced;

    // Inventory and treasury
    private final Map<Material, Integer> inventory = new HashMap<>();
    private final Map<String, Integer> resources = new HashMap<>();
    private final Map<String, Double> treasury = new HashMap<>();

    // Workers and mayor
    private List<Worker> workers = new ArrayList<>();
    private UUID mayorId = null;
    private Integer mayorNpcId = null;

    // Other locations
    private Location chestLocation;
    private List<Location> bedLocations = new ArrayList<>();

    // Managers
    private final CityInventory cityInventory = new CityInventory();
    private final CityProductionManager productionManager = new CityProductionManager(this);

    // Constructors
    public City(String name, String nation, Location location, int population, String color, String currencyName, UUID mayorId) {
        this.name = name;
        this.nation = nation;
        this.location = location;
        this.population = population;
        this.color = color;
        this.primaryCurrency = currencyName;
        this.mayorId = mayorId;
        this.currencyEnforced = (parentCityName != null);
    }

    public City(String name, String nation, Location location, int population, String color, String currencyName, int mayorNpcId) {
        this.name = name;
        this.nation = nation;
        this.location = location;
        this.population = population;
        this.color = color;
        this.primaryCurrency = currencyName;
        this.mayorNpcId = mayorNpcId;
    }

    public City(String name, String nation, Location location, int population, String color, String currencyName) {
        this.name = name;
        this.nation = nation;
        this.location = location;
        this.population = population;
        this.color = color;
        this.primaryCurrency = currencyName;
    }

    public Map<Material, Integer> getAllCityInventory() {
        return new HashMap<>(inventory); // ✅ This one has Material keys
    }

    public double getUnemploymentRate() {
        long total = workers.size();
        long unemployed = workers.stream().filter(w -> w.getRole() == WorkerRole.RESIDENT).count();
        return total > 0 ? (double) unemployed / total * 100 : 0.0;
    }



    // Getters
    public String getName() { return name; }
    public String getNation() { return nation; }
    public Location getLocation() { return location; }
    public int getPopulation() { return population; }
    public String getColor() { return color; }
    public String getPrimaryCurrency() { return primaryCurrency; }
    public List<Worker> getWorkers() { return workers; }
    public Location getChestLocation() { return chestLocation; }
    public CityInventory getInventory() { return cityInventory; }
    public Map<String, Integer> getResources() { return resources; }
    public CityProductionManager getProductionManager() { return productionManager; }

    public boolean hasPlayerMayor() { return mayorId != null; }
    public boolean hasNpcMayor() { return mayorNpcId != null; }
    public UUID getMayorId() { return mayorId; }
    public Integer getMayorNpcId() { return mayorNpcId; }
    public boolean isCurrencyEnforced() { return currencyEnforced; }
    public boolean isIndependent() { return independent; }
    public String getParentCityName() { return parentCityName; }
    public boolean isForceUseParentCurrency() { return forceUseParentCurrency; }

    // Setters
    public void setPrimaryCurrency(String currency) { this.primaryCurrency = currency; }
    public void setChestLocation(Location chestLocation) { this.chestLocation = chestLocation; }
    public void setWorkers(List<Worker> workers) { this.workers = workers; }
    public void addWorker(Worker worker) { this.workers.add(worker); }
    public void setMayorId(UUID id) { this.mayorId = id; this.mayorNpcId = null; }
    public void setMayorNpcId(Integer id) { this.mayorNpcId = id; this.mayorId = null; }
    public void setIndependent(boolean independent) { this.independent = independent; }
    public void setParentCityName(String parentCityName) { this.parentCityName = parentCityName; }
    public void setForceUseParentCurrency(boolean force) { this.forceUseParentCurrency = force; }
    public void setColor(String color) { this.color = color; }
    public void removeItem(Material material, int amount) {
        cityInventory.removeItem(material, amount);
    }


    public String getEffectiveCurrency(CityManager cityManager) {
        if (forceUseParentCurrency && parentCityName != null) {
            City parent = cityManager.getCity(parentCityName);
            return parent != null ? parent.getPrimaryCurrency() : primaryCurrency;
        }
        return primaryCurrency;
    }

    // Treasury methods
    public void depositToTreasury(String currency, double amount) {
        treasury.merge(currency, amount, Double::sum);
    }

    public boolean withdrawFromTreasury(String currency, double amount) {
        double current = treasury.getOrDefault(currency, 0.0);
        if (current >= amount) {
            treasury.put(currency, current - amount);
            return true;
        }
        return false;
    }

    public double getCityBalance(String currency) {
        return treasury.getOrDefault(currency, 0.0);
    }

    public Map<String, Double> getAllCityBalances() {
        return new HashMap<>(treasury);
    }

    // Bed management
    public void addBedLocation(Location bedLoc) {
        if (!bedLocations.contains(bedLoc)) bedLocations.add(bedLoc);
    }

    public boolean hasAvailableBed() {
        return !bedLocations.isEmpty();
    }

    public boolean claimBed(Location bedLoc) {
        return bedLocations.remove(bedLoc);
    }

    public String getMayorDisplayName() {
        if (hasPlayerMayor()) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(mayorId);
            return player != null ? player.getName() : "Unknown";
        } else if (hasNpcMayor()) {
            try {
                net.citizensnpcs.api.npc.NPC npc = net.citizensnpcs.api.CitizensAPI.getNPCRegistry().getById(mayorNpcId);
                return npc != null ? "[NPC] " + npc.getFullName() : "Unknown NPC";
            } catch (Exception e) {
                return "NPC:" + mayorNpcId;
            }
        }
        return "None";
    }

    public boolean removeWorkerByName(String name) {
        Iterator<Worker> iterator = workers.iterator();
        while (iterator.hasNext()) {
            Worker w = iterator.next();
            if (w.getName().equalsIgnoreCase(name)) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }

    // Resource scanning
    public void scanForResources(int radius) {
        if (location == null || location.getWorld() == null) return;
        resources.clear();

        int cx = location.getBlockX();
        int cz = location.getBlockZ();
        int yMin = Math.max(0, location.getBlockY() - 32);
        int yMax = Math.min(255, location.getBlockY() + 32);

        String[] types = {
                "COAL_ORE", "IRON_ORE", "GOLD_ORE", "COPPER_ORE", "DIAMOND_ORE", "EMERALD_ORE",
                "REDSTONE_ORE", "LAPIS_ORE", "DEEPSLATE_COAL_ORE", "DEEPSLATE_IRON_ORE",
                "DEEPSLATE_GOLD_ORE", "DEEPSLATE_COPPER_ORE", "DEEPSLATE_DIAMOND_ORE",
                "DEEPSLATE_EMERALD_ORE", "DEEPSLATE_REDSTONE_ORE", "DEEPSLATE_LAPIS_ORE",
                "STONE", "DIORITE", "GRANITE", "SANDSTONE", "DEEPSLATE", "OAK_LOG", "BIRCH_LOG",
                "SPRUCE_LOG", "JUNGLE_LOG", "ACACIA_LOG", "DARK_OAK_LOG", "CHERRY_LOG", "MANGROVE_LOG",
                "WHEAT", "CARROTS", "POTATOES", "BEETROOTS", "PUMPKIN", "MELON",
                "SUGAR_CANE", "CACTUS", "BAMBOO", "COCOA", "SWEET_BERRIES", "GLOW_BERRIES", "KELP",
                "SEAGRASS", "MANGROVE_ROOTS", "MOSS_BLOCK", "AZALEA", "FLOWERING_AZALEA",
                "CLAY", "SAND", "GRAVEL", "ICE", "SNOW_BLOCK", "DRIPSTONE_BLOCK", "MUD"
        };

        for (String t : types) resources.put(t, 0);

        for (int x = cx - radius; x <= cx + radius; x++) {
            for (int z = cz - radius; z <= cz + radius; z++) {
                for (int y = yMin; y <= yMax; y++) {
                    Block block = location.getWorld().getBlockAt(x, y, z);
                    String name = block.getType().name();
                    if (resources.containsKey(name)) {
                        resources.put(name, resources.get(name) + 1);
                    }
                }
            }
        }
    }

    public boolean isLocationInCity(Location loc) {
        if (loc == null || location == null || !loc.getWorld().equals(location.getWorld())) return false;
        double dx = loc.getBlockX() - location.getBlockX();
        double dz = loc.getBlockZ() - location.getBlockZ();
        return Math.sqrt(dx * dx + dz * dz) <= 48;
    }

    public String getColorCode() {
        return color;
    }

    // Material Inventory API
    public boolean useMaterial(Material material) {
        String key = material.name();
        int count = resources.getOrDefault(key, 0);
        if (count > 0) {
            resources.put(key, count - 1);
            return true;
        }
        return false;
    }

    public boolean hasItem(Material material) {
        return inventory.getOrDefault(material, 0) > 0;
    }

    public boolean takeItem(Material material, int amount) {
        int current = inventory.getOrDefault(material, 0);
        if (current >= amount) {
            inventory.put(material, current - amount);
            return true;
        }
        return false;
    }

    public void addItem(Material material, int amount) {
        inventory.put(material, inventory.getOrDefault(material, 0) + amount);
    }

    public Map<Material, Integer> getCityInventory() {
        return inventory;
    }
}
