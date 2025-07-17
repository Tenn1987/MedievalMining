package com.brandon.globaleconomy.city;

import com.brandon.globaleconomy.economy.impl.workers.Worker;
import com.brandon.globaleconomy.economy.impl.workers.WorkerFactory;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class CityYamlLoader {
    private final File dataFolder;

    public CityYamlLoader(File dataFolder) {
        this.dataFolder = dataFolder;
    }

    public void saveCities(Map<String, City> cities) {
        Yaml yaml = new Yaml();
        Map<String, Object> saveMap = new LinkedHashMap<>();

        for (City city : cities.values()) {
            Map<String, Object> cityData = new LinkedHashMap<>();
            cityData.put("name", city.getName());
            cityData.put("nation", city.getNation());
            cityData.put("world", city.getLocation().getWorld().getName());
            cityData.put("x", city.getLocation().getBlockX());
            cityData.put("y", city.getLocation().getBlockY());
            cityData.put("z", city.getLocation().getBlockZ());
            cityData.put("population", city.getPopulation());
            cityData.put("color", city.getColor());
            cityData.put("currencyName", city.getPrimaryCurrency());
            cityData.put("mayorId", city.getMayorId() != null ? city.getMayorId().toString() : "");

            // Save chest location if exists
            if (city.getChestLocation() != null) {
                Location chestLoc = city.getChestLocation();
                cityData.put("chestWorld", chestLoc.getWorld().getName());
                cityData.put("chestX", chestLoc.getBlockX());
                cityData.put("chestY", chestLoc.getBlockY());
                cityData.put("chestZ", chestLoc.getBlockZ());
            }

            List<Map<String, String>> workers = new ArrayList<>();
            if (city.getWorkers() != null) {
                for (Worker worker : city.getWorkers()) {
                    Map<String, String> workerMap = new HashMap<>();
                    workerMap.put("role", worker.getRole().name());
                    workerMap.put("name", worker.getName());
                    workerMap.put("uuid", worker.getNpcId().toString());
                    workers.add(workerMap);
                }
            }
            cityData.put("workers", workers);

            // Save builtPlots
            List<String> builtPlotStrings = city.getBuiltPlots().stream()
                    .map(loc -> loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ())
                    .collect(Collectors.toList());
            cityData.put("builtPlots", builtPlotStrings);

            saveMap.put(city.getName(), cityData);
        }

        File outFile = new File(dataFolder, "cities.yml");
        try (Writer writer = new FileWriter(outFile)) {
            yaml.dump(saveMap, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Map<String, City> loadCities() {
        Map<String, City> cityMap = new LinkedHashMap<>();
        File file = new File(dataFolder, "cities.yml");
        if (!file.exists()) return cityMap;

        Yaml yaml = new Yaml();
        try (InputStream is = new FileInputStream(file)) {
            Map<String, Object> obj = yaml.load(is);
            if (obj != null) {
                for (Map.Entry<String, Object> entry : obj.entrySet()) {
                    Map<String, Object> cityData = (Map<String, Object>) entry.getValue();

                    String name = (String) cityData.get("name");
                    String nation = (String) cityData.get("nation");
                    String world = (String) cityData.get("world");
                    int x = (int) cityData.get("x");
                    int y = (int) cityData.get("y");
                    int z = (int) cityData.get("z");
                    int population = (int) cityData.get("population");
                    String color = (String) cityData.get("color");
                    String currencyName = (String) cityData.get("currencyName");
                    UUID mayorId = null;
                    if (cityData.get("mayorId") != null && !((String) cityData.get("mayorId")).isEmpty()) {
                        mayorId = UUID.fromString((String) cityData.get("mayorId"));
                    }

                    Location loc = new Location(Bukkit.getWorld(world), x, y, z);
                    City city = new City(name, nation, loc, population, color, currencyName, mayorId);

                    // Load optional chest location
                    if (cityData.containsKey("chestX")) {
                        String chestWorld = (String) cityData.get("chestWorld");
                        int cx = (int) cityData.get("chestX");
                        int cy = (int) cityData.get("chestY");
                        int cz = (int) cityData.get("chestZ");
                        Location chestLoc = new Location(Bukkit.getWorld(chestWorld), cx, cy, cz);
                        city.setChestLocation(chestLoc);
                    }

                    // Load builtPlots
                    List<String> builtPlotStrings = (List<String>) cityData.get("builtPlots");
                    if (builtPlotStrings != null) {
                        for (String s : builtPlotStrings) {
                            String[] parts = s.split(",");
                            if (parts.length == 4) {
                                World w = Bukkit.getWorld(parts[0]);
                                int bx = Integer.parseInt(parts[1]);
                                int by = Integer.parseInt(parts[2]);
                                int bz = Integer.parseInt(parts[3]);
                                if (w != null) {
                                    city.addBuiltPlot(new Location(w, bx, by, bz));
                                }
                            }
                        }
                    }

                    List<Map<String, String>> workers = (List<Map<String, String>>) cityData.get("workers");
                    if (workers != null) {
                        for (Map<String, String> w : workers) {
                            String role = w.get("role");
                            String workerName = w.get("name");
                            String uuidStr = w.get("uuid");
                            UUID npcId = uuidStr != null ? UUID.fromString(uuidStr) : UUID.randomUUID();
                            Worker worker = WorkerFactory.createWorker(role, city, workerName, npcId);
                            city.addWorker(worker);
                        }
                    }
                    cityMap.put(name, city);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cityMap;
    }
}