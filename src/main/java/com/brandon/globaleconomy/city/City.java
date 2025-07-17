package com.brandon.globaleconomy.city;

import com.brandon.globaleconomy.city.CityProductionManager;
import com.brandon.globaleconomy.economy.impl.workers.Worker;
import com.brandon.globaleconomy.economy.impl.workers.WorkerRole;
import com.brandon.globaleconomy.npc.traits.WorkerTrait;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.Location;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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
    private final Set<Location> builtPlots = new HashSet<>();

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
        return new HashMap<>(inventory);
    }

    public double getUnemploymentRate() {
        long total = workers.size();
        long unemployed = workers.stream().filter(w -> w.getRole() == WorkerRole.RESIDENT).count();
        return total > 0 ? (double) unemployed / total * 100 : 0.0;
    }

    public List<NPC> getUnemployedNPCs() {
        return StreamSupport.stream(CitizensAPI.getNPCRegistry().spliterator(), false)
                .filter(npc -> npc.hasTrait(WorkerTrait.class))
                .filter(npc -> {
                    WorkerTrait trait = npc.getTrait(WorkerTrait.class);
                    Worker worker = trait.getWorker();
                    return worker != null
                            && getName().equalsIgnoreCase(worker.getCity().getName())
                            && worker.getRole() == WorkerRole.RESIDENT;
                })
                .collect(Collectors.toList());
    }

    public void addResource(String name, int amount) {
        resources.put(name, resources.getOrDefault(name, 0) + amount);
    }

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
    public void removeItem(Material material, int amount) { cityInventory.removeItem(material, amount); }

    public String getEffectiveCurrency(CityManager cityManager) {
        if (forceUseParentCurrency && parentCityName != null) {
            City parent = cityManager.getCity(parentCityName);
            return parent != null ? parent.getPrimaryCurrency() : primaryCurrency;
        }
        return primaryCurrency;
    }

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
                NPC npc = CitizensAPI.getNPCRegistry().getById(mayorNpcId);
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

    public Set<Location> getBuiltPlots() {
        return builtPlots;
    }

    public void addBuiltPlot(Location loc) {
        builtPlots.add(loc);
    }

    public boolean isPlotUsed(Location loc) {
        return builtPlots.stream().anyMatch(l ->
                l.getWorld().equals(loc.getWorld()) &&
                        l.getBlockX() == loc.getBlockX() &&
                        l.getBlockZ() == loc.getBlockZ()
        );
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

    public List<Location> getFertilePlots(Material cropType) {
        List<Location> fertile = new ArrayList<>();
        World world = getLocation().getWorld();
        Location center = getLocation();

        for (int x = -10; x <= 10; x++) {
            for (int z = -10; z <= 10; z++) {
                Location loc = center.clone().add(x, 0, z);
                loc.setY(world.getHighestBlockYAt(loc));
                Material ground = loc.clone().subtract(0, 1, 0).getBlock().getType();
                if (ground == Material.FARMLAND && loc.getBlock().getType() == Material.AIR) {
                    fertile.add(loc.clone());
                }
            }
        }
        return fertile;
    }

    public Map<Material, Integer> getCityInventory() {
        return inventory;
    }

    public void log(String message) {
        Bukkit.getLogger().info("[City: " + getName() + "] " + message);
    }

    // Scan nearby terrain to detect resource types (uses CityResourceScanner)
    public void scanForResources(int radius) {
        CityResourceScanner scanner = new CityResourceScanner();
        for (CityResourceScanner.CityResourceType type : CityResourceScanner.CityResourceType.values()) {
            Location loc = CityResourceScanner.getNearestResource(this, type);
            if (loc != null) {
                this.addResource(type.name(), 1); // Treat presence as 1 unit
            }
        }
    }

    // Check if a given location falls within this city's boundaries
    public boolean isLocationInCity(Location loc) {
        World cityWorld = location.getWorld();
        if (!cityWorld.equals(loc.getWorld())) return false;

        int dx = Math.abs(location.getBlockX() - loc.getBlockX());
        int dz = Math.abs(location.getBlockZ() - loc.getBlockZ());

        return dx <= 32 && dz <= 32; // Example: 64x64 claim radius
    }

}
