package com.brandon.globaleconomy.city;

import org.bukkit.Location;
import java.io.Serializable;
import java.util.*;

import com.brandon.globaleconomy.economy.impl.workers.Worker;

public class City implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Map<String, Integer> resources = new HashMap<>();
    public Map<String, Integer> getResources() { return resources; }

    // --- City Core ---
    private final String name;
    private final String nation;
    private final Location location;
    private final int population;
    private String color;           // HEX color (e.g., "#ff00ff")
    private String primaryCurrency; // Replaces 'currencyName'
    private Map<String, Double> treasury = new HashMap<>();
    private List<Worker> workers = new ArrayList<>();

    // --- Mayor (hybrid) ---
    private UUID mayorId;        // For player mayors
    private Integer mayorNpcId;  // For Citizens NPC mayors

    // --- New Currency Control Fields ---
    private boolean independent = true;
    private String parentCityName = null;
    private boolean forceUseParentCurrency = false;
    private boolean currencyEnforced;


    // --- Constructors ---

    // Player mayor constructor
    public City(String name, String nation, Location location, int population, String color, String currencyName, UUID mayorId) {
        this.name = name;
        this.nation = nation;
        this.location = location;
        this.population = population;
        this.color = color;
        this.primaryCurrency = currencyName;
        this.mayorId = mayorId;
        this.mayorNpcId = null;

        this.parentCityName = parentCityName;
        this.currencyEnforced = (parentCityName != null);
    }

    // NPC mayor constructor
    public City(String name, String nation, Location location, int population, String color, String currencyName, int mayorNpcId) {
        this.name = name;
        this.nation = nation;
        this.location = location;
        this.population = population;
        this.color = color;
        this.primaryCurrency = currencyName;
        this.mayorId = null;
        this.mayorNpcId = mayorNpcId;
    }

    // Minimal constructor
    public City(String name, String nation, Location location, int population, String color, String currencyName) {
        this.name = name;
        this.nation = nation;
        this.location = location;
        this.population = population;
        this.color = color;
        this.primaryCurrency = currencyName;
        this.mayorId = null;
        this.mayorNpcId = null;

        this.parentCityName = null;
        this.currencyEnforced = false;
    }



    // --- Getters ---

    public String getName() { return name; }
    public String getNation() { return nation; }
    public Location getLocation() { return location; }
    public int getPopulation() { return population; }
    public String getColor() { return color; }
    public String getPrimaryCurrency() { return primaryCurrency; }
    public List<Worker> getWorkers() { return workers; }

    public void setPrimaryCurrency(String currency) {
        this.primaryCurrency = currency;
    }
    public boolean isCurrencyEnforced() { return currencyEnforced; }

    // --- Mayor Getters/Setters ---
    public boolean hasPlayerMayor() { return mayorId != null; }
    public boolean hasNpcMayor() { return mayorNpcId != null; }

    public UUID getMayorId() { return mayorId; }
    public void setMayorId(UUID id) { this.mayorId = id; this.mayorNpcId = null; }

    public Integer getMayorNpcId() { return mayorNpcId; }
    public void setMayorNpcId(Integer npcId) { this.mayorNpcId = npcId; this.mayorId = null; }

    // --- Workers ---
    public void setWorkers(List<Worker> workers) { this.workers = workers; }
    public void addWorker(Worker worker) { this.workers.add(worker); }

    // --- Treasury Methods ---
    public void depositToTreasury(String currency, double amount) {
        treasury.merge(currency, amount, Double::sum);
    }

    public boolean withdrawFromTreasury(String currency, double amount) {
        double balance = treasury.getOrDefault(currency, 0.0);
        if (balance >= amount) {
            treasury.put(currency, balance - amount);
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

    public boolean removeWorkerByName(String name) {
        Iterator<Worker> iterator = workers.iterator();
        while (iterator.hasNext()) {
            Worker worker = iterator.next();
            if (worker.getName().equalsIgnoreCase(name)) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }


    // --- Display Mayor Name ---
    public String getMayorDisplayName() {
        if (hasPlayerMayor()) {
            org.bukkit.OfflinePlayer player = org.bukkit.Bukkit.getOfflinePlayer(mayorId);
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

    // --- Resources ---
    public void scanForResources(int radius) {
        if (location == null || location.getWorld() == null) return;
        resources.clear();

        int cx = location.getBlockX();
        int cz = location.getBlockZ();
        int yMin = Math.max(0, location.getBlockY() - 32);
        int yMax = Math.min(255, location.getBlockY() + 32);

        String[] blockTypes = {
                "COAL_ORE", "IRON_ORE", "GOLD_ORE", "COPPER_ORE", "DIAMOND_ORE", "EMERALD_ORE",
                "REDSTONE_ORE", "LAPIS_ORE", "DEEPSLATE_COAL_ORE", "DEEPSLATE_IRON_ORE",
                "DEEPSLATE_GOLD_ORE", "DEEPSLATE_COPPER_ORE", "DEEPSLATE_DIAMOND_ORE",
                "DEEPSLATE_EMERALD_ORE", "DEEPSLATE_REDSTONE_ORE", "DEEPSLATE_LAPIS_ORE",
                "OAK_LOG", "BIRCH_LOG", "SPRUCE_LOG", "JUNGLE_LOG", "ACACIA_LOG", "DARK_OAK_LOG",
                "CHERRY_LOG", "MANGROVE_LOG", "ACACIA_PLANKS", "CHERRY_PLANKS", "WHEAT", "CARROTS",
                "POTATOES", "BEETROOTS", "PUMPKIN", "MELON", "SUGAR_CANE", "CACTUS", "BAMBOO",
                "COCOA", "SWEET_BERRIES", "GLOW_BERRIES", "KELP", "SEAGRASS", "MANGROVE_ROOTS",
                "MOSS_BLOCK", "AZALEA", "FLOWERING_AZALEA", "CLAY", "SAND", "GRAVEL", "ICE",
                "SNOW_BLOCK", "DRIPSTONE_BLOCK", "MUD"
        };

        for (String type : blockTypes) {
            resources.put(type, 0);
        }

        for (int x = cx - radius; x <= cx + radius; x++) {
            for (int z = cz - radius; z <= cz + radius; z++) {
                for (int y = yMin; y <= yMax; y++) {
                    org.bukkit.block.Block block = location.getWorld().getBlockAt(x, y, z);
                    String blockName = block.getType().name();
                    if (resources.containsKey(blockName)) {
                        resources.put(blockName, resources.get(blockName) + 1);
                    }
                }
            }
        }
    }

    public boolean isLocationInCity(org.bukkit.Location loc) {
        if (loc == null || this.location == null) return false;
        if (!loc.getWorld().equals(this.location.getWorld())) return false;
        double dx = loc.getBlockX() - this.location.getBlockX();
        double dz = loc.getBlockZ() - this.location.getBlockZ();
        double distance = Math.sqrt(dx * dx + dz * dz);
        return distance <= 48;
    }

    public String getColorCode() { return color; }
    public void setColor(String color) { this.color = color; }

    // --- New Currency Logic ---
    public boolean isIndependent() { return independent; }
    public void setIndependent(boolean independent) { this.independent = independent; }

    public String getParentCityName() { return parentCityName; }
    public void setParentCityName(String parentCityName) { this.parentCityName = parentCityName; }


    public boolean isForceUseParentCurrency() { return forceUseParentCurrency; }
    public void setForceUseParentCurrency(boolean forceUseParentCurrency) { this.forceUseParentCurrency = forceUseParentCurrency; }

    public String getEffectiveCurrency(CityManager cityManager) {
        if (forceUseParentCurrency && parentCityName != null) {
            City parent = cityManager.getCity(parentCityName);
            return parent != null ? parent.getPrimaryCurrency() : primaryCurrency;
        }
        return primaryCurrency;
    }
}
