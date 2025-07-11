package com.brandon.globaleconomy.economy.impl.workers;

import com.brandon.globaleconomy.city.City;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.CitizensAPI;
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
    protected long cooldownMillis = 10000; // Default to 10 sec

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
        return System.currentTimeMillis() - lastWorkTime > cooldownMillis;
    }

    public void markCooldown() {
        lastWorkTime = System.currentTimeMillis();
    }

    public void addToInventory(ItemStack item) {
        inventory.put(item, inventory.getOrDefault(item, 0) + item.getAmount());
    }

    protected void consume(String item, int amount) {
        city.getProductionManager().consume(item, amount);
    }

    protected void produce(String item, int amount) {
        city.getProductionManager().produce(item, amount);
    }

    protected void logWork(String message) {
        org.bukkit.Bukkit.getLogger().info(getName() + ": " + message);
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

    public NPC getNpc() {
        return CitizensAPI.getNPCRegistry().getByUniqueIdGlobal(npcId);
    }
}