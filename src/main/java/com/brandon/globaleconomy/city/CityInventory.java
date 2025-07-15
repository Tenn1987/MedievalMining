package com.brandon.globaleconomy.city;

import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

public class CityInventory {
    private final Map<Material, Integer> inventory = new HashMap<>();

    public void deposit(Material material, int amount) {
        inventory.put(material, inventory.getOrDefault(material, 0) + amount);
    }

    public boolean withdraw(Material material, int amount) {
        int currentAmount = inventory.getOrDefault(material, 0);
        if (currentAmount < amount) {
            return false;
        }
        inventory.put(material, currentAmount - amount);
        return true;
    }

    public int getQuantity(Material material) {
        return inventory.getOrDefault(material, 0);
    }

    public Map<Material, Integer> getAllItems() {
        return new HashMap<>(inventory);
    }

    public boolean has(Material material, int amount) {
        return getQuantity(material) >= amount;
    }

    public void addItem(Material material, int amount) {
        int current = inventory.getOrDefault(material, 0);
        inventory.put(material, Math.max(current - amount, 0));
    }

    public void removeItem(Material material, int amount) {
        int current = inventory.getOrDefault(material, 0);
        inventory.put(material, Math.max(current - amount, 0));
    }
}
