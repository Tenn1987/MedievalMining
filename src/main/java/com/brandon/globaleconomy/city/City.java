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
    private String currencyName;    // Currency (by name)
    private List<Worker> workers = new ArrayList<>();

    // --- Mayor (hybrid) ---
    private UUID mayorId;        // For player mayors
    private Integer mayorNpcId;  // For Citizens NPC mayors

        // --- Constructors ---

    // Player mayor constructor
    public City(String name, String nation, Location location, int population, String color, String currencyName, UUID mayorId) {
        this.name = name;
        this.nation = nation;
        this.location = location;
        this.population = population;
        this.color = color;
        this.currencyName = currencyName;
        this.mayorId = mayorId;
        this.mayorNpcId = null;
    }

    // NPC mayor constructor
    public City(String name, String nation, Location location, int population, String color, String currencyName, int mayorNpcId) {
        this.name = name;
        this.nation = nation;
        this.location = location;
        this.population = population;
        this.color = color;
        this.currencyName = currencyName;
        this.mayorId = null;
        this.mayorNpcId = mayorNpcId;
    }

    // Minimal/legacy constructor (no mayor set yet)
    public City(String name, String nation, Location location, int population, String color, String currencyName) {
        this.name = name;
        this.nation = nation;
        this.location = location;
        this.population = population;
        this.color = color;
        this.currencyName = currencyName;
        this.mayorId = null;
        this.mayorNpcId = null;
    }

    // --- Getters ---

    public String getName() { return name; }
    public String getNation() { return nation; }
    public Location getLocation() { return location; }
    public int getPopulation() { return population; }
    public String getColor() { return color; }
    public String getCurrencyName() { return currencyName; }
    public List<Worker> getWorkers() { return workers; }

    // --- Mayor Getters/Setters ---
    public boolean hasPlayerMayor() { return mayorId != null; }
    public boolean hasNpcMayor() { return mayorNpcId != null; }

    public UUID getMayorId() { return mayorId; }
    public void setMayorId(UUID id) { this.mayorId = id; this.mayorNpcId = null; }

    public Integer getMayorNpcId() { return mayorNpcId; }
    public void setMayorNpcId(Integer npcId) { this.mayorNpcId = npcId; this.mayorId = null; }

    // --- Workers ---
    public void setWorkers(List<Worker> workers) { this.workers = workers; }
    public void addWorker(Worker worker) {
        this.workers.add(worker);
    }

    // --- Display Mayor Name (Player or NPC) ---
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

    //---- Resources ---

    public void scanForResources(int radius) {
        if (location == null || location.getWorld() == null) return;
        resources.clear();

        int cx = location.getBlockX();
        int cz = location.getBlockZ();
        int yMin = Math.max(0, location.getBlockY() - 32);
        int yMax = Math.min(255, location.getBlockY() + 32);

        // --- Define blocks to count ---
        String[] blockTypes = {
                // Ores
                "COAL_ORE", "IRON_ORE", "GOLD_ORE", "COPPER_ORE", "DIAMOND_ORE", "EMERALD_ORE", "REDSTONE_ORE", "LAPIS_ORE",
                "DEEPSLATE_COAL_ORE", "DEEPSLATE_IRON_ORE", "DEEPSLATE_GOLD_ORE", "DEEPSLATE_COPPER_ORE", "DEEPSLATE_DIAMOND_ORE",
                "DEEPSLATE_EMERALD_ORE", "DEEPSLATE_REDSTONE_ORE", "DEEPSLATE_LAPIS_ORE",
                // Logs (including signature regional trees)
                "OAK_LOG", "BIRCH_LOG", "SPRUCE_LOG", "JUNGLE_LOG", "ACACIA_LOG", "DARK_OAK_LOG", "CHERRY_LOG", "MANGROVE_LOG",
                // Planks as secondary check (if chopped)
                "ACACIA_PLANKS", "CHERRY_PLANKS",
                // Crops/foodstuffs
                "WHEAT", "CARROTS", "POTATOES", "BEETROOTS", "PUMPKIN", "MELON", "SUGAR_CANE", "CACTUS", "BAMBOO",
                "COCOA", "SWEET_BERRIES", "GLOW_BERRIES",
                // Other region-defining plants
                "KELP", "SEAGRASS", "MANGROVE_ROOTS", "MOSS_BLOCK", "AZALEA", "FLOWERING_AZALEA",
                // Specialty blocks for trade flavor (expand as needed)
                "CLAY", "SAND", "GRAVEL", "ICE", "SNOW_BLOCK", "DRIPSTONE_BLOCK", "MUD"
        };

        for (String type : blockTypes) {
            resources.put(type, 0);
        }

        // --- Area scan (may be heavy for large radius; optimize as needed) ---
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
        return distance <= 48; // or whatever your desired city radius is
    }


    public String getColorCode() { return color;
    }

    public void setColor(String color) {
        this.color = color;
    }


    // --- Other city logic goes here as needed (economy, resources, etc.) ---
}
