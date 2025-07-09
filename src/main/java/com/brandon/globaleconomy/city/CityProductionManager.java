package com.brandon.globaleconomy.city;

import com.brandon.globaleconomy.economy.impl.workers.Worker;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;

public class CityProductionManager {

    private final City city;
    private final Map<String, Integer> dailyLimits = new HashMap<>();
    private final Map<String, Integer> producedToday = new HashMap<>();

    public CityProductionManager(City city) {
        this.city = city;
    }

    public boolean canProduce(Worker worker, String resource, int amount) {
        int current = producedToday.getOrDefault(resource, 0);
        int limit = dailyLimits.getOrDefault(resource, Integer.MAX_VALUE);

        boolean enoughStock = city.getResources().getOrDefault(resource, 0) >= amount;
        boolean underLimit = current + amount <= limit;

        if (!enoughStock || !underLimit) {
            Bukkit.getLogger().info("[CityProduction] " + city.getName() +
                    ": " + worker.getName() + " denied production of " + amount + "x " + resource +
                    " (Stock OK? " + enoughStock + ", Limit OK? " + underLimit + ")");
        }

        return enoughStock && underLimit;
    }

    public boolean produce(Worker worker, String resource, int amount) {
        if (!canProduce(worker, resource, amount)) {
            return false;
        }
        recordProduction(resource, amount);
        consume(resource, amount); // Optional: only needed if resource is input-dependent
        return true;
    }

    public void recordProduction(String resource, int amount) {
        producedToday.put(resource, producedToday.getOrDefault(resource, 0) + amount);
    }

    public void setDailyLimit(String resource, int limit) {
        dailyLimits.put(resource, limit);
    }

    public void resetDailyProduction() {
        producedToday.clear();
    }

    public Map<String, Integer> getProductionStats() {
        return new HashMap<>(producedToday);
    }

    public void consume(String item, int amount) {
        Map<String, Integer> res = city.getResources();
        res.put(item, Math.max(0, res.getOrDefault(item, 0) - amount));
    }

    // Optional: if you still want a raw add-to-inventory method
    public void produce(String item, int amount) {
        Map<String, Integer> res = city.getResources();
        res.put(item, res.getOrDefault(item, 0) + amount);
    }
}
