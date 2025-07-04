package com.brandon.globaleconomy.economy.impl.workers;

import com.brandon.globaleconomy.city.City;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.UUID;

public abstract class Worker {
    protected final City city;
    protected final UUID npcId;
    protected final String name;
    protected final WorkerRole role;
    protected final Map<ItemStack, Integer> inventory = new HashMap<>();
    protected long lastWorkTime = 0;

    public Worker(City city, String name, WorkerRole role, UUID npcId) {
        this.city = city;
        this.name = name;
        this.role = role;
        this.npcId = npcId;
    }

    public City getCity() { return city; }

    public abstract void performWork(City city);

    public UUID getNpcId() {
        return npcId;
    }

    public WorkerRole getRole() {
        return role;
    }

    public String getName() {
        return name;
    }

    public boolean isReadyToWork() {
        return System.currentTimeMillis() - lastWorkTime > 10000; // 10 sec cooldown
    }

    public void markCooldown() {
        lastWorkTime = System.currentTimeMillis();
    }

    public void addToInventory(ItemStack item) {
        inventory.put(item, inventory.getOrDefault(item, 0) + item.getAmount());
    }

    public boolean removeFromInventory(ItemStack item, int amount) {
        Integer current = inventory.get(item);
        if (current == null || current < amount) return false;
        if (current == amount) inventory.remove(item);
        else inventory.put(item, current - amount);
        return true;
    }

    public boolean hasInInventory(ItemStack item, int amount) {
        return inventory.getOrDefault(item, 0) >= amount;
    }
}