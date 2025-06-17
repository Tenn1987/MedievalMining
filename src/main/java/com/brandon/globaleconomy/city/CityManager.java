package com.brandon.globaleconomy.city;

import com.brandon.globaleconomy.city.claims.ClaimManager;
import org.bukkit.Location;

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
    public void addCityWithMayor(String name, String nation, Location location, int population, String color, String currencyName, UUID mayorId) {
        City city = new City(name, nation, location, population, color, currencyName, mayorId);
        addCity(city); // Will trigger resource scan
    }

    /**
     * Add a city with an NPC mayor.
     */
    public void addCityWithNpcMayor(String name, String nation, Location location, int population, String color, String currencyName, int mayorNpcId) {
        City city = new City(name, nation, location, population, color, currencyName, mayorNpcId);
        addCity(city); // Will trigger resource scan
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
        addCity(city); // Will trigger resource scan
    }

    public City getCityAt(Location location) {
        // Example: check each city, see if location is inside bounds/claim (customize as needed)
        for (City city : cities.values()) {
            if (city.isLocationInCity(location)) return city;
        }
        return null;
    }


    // If your City constructor changes, update accordingly above!
}