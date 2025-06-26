package com.brandon.globaleconomy.city;

import com.brandon.globaleconomy.economy.impl.workers.Worker;
import com.brandon.globaleconomy.economy.impl.workers.WorkerFactory;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

// CityYamlLoader.java

public class CityYamlLoader {
    private final File dataFolder;

    public CityYamlLoader(File dataFolder) {
        this.dataFolder = dataFolder;
    }

    // SAVE ALL CITIES TO YAML
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
            // Optional: Save workers as simple role:name list
            List<Map<String, String>> workers = new ArrayList<>();
            if (city.getWorkers() != null) {
                for (Worker worker : city.getWorkers()) {
                    Map<String, String> workerMap = new HashMap<>();
                    workerMap.put("role", worker.getRole());
                    workerMap.put("name", worker.getName());
                    workers.add(workerMap);
                }
            }
            cityData.put("workers", workers);

            saveMap.put(city.getName(), cityData);
        }

        File outFile = new File(dataFolder, "cities.yml");
        try (Writer writer = new FileWriter(outFile)) {
            yaml.dump(saveMap, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // LOAD ALL CITIES FROM YAML
    public Map<String, City> loadCities() {
        Map<String, City> cityMap = new LinkedHashMap<>();
        File file = new File(dataFolder, "cities.yml");
        if (!file.exists()) {
            return cityMap;
        }

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
                    if (cityData.get("mayorId") != null && !((String)cityData.get("mayorId")).isEmpty()) {
                        mayorId = UUID.fromString((String) cityData.get("mayorId"));
                    }
                    org.bukkit.Location loc = new org.bukkit.Location(
                            org.bukkit.Bukkit.getWorld(world), x, y, z
                    );
                    City city = new City(name, nation, loc, population, color, currencyName, mayorId);

                    // Rebuild workers
                    List<Map<String, String>> workers = (List<Map<String, String>>) cityData.get("workers");
                    if (workers != null) {
                        for (Map<String, String> w : workers) {
                            String role = w.get("role");
                            String workerName = w.get("name");
                            Worker worker = WorkerFactory.createWorker(role, city, workerName);
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

