package com.brandon.globaleconomy.economy.impl.workers;

import com.brandon.globaleconomy.city.City;

import java.util.UUID;

public class WorkerFactory {

    // String-based (used by config/loaders)
    public static Worker createWorker(String role, City city, String name, UUID npcId) {
        return createWorker(WorkerRole.valueOf(role.toUpperCase()), city, name, npcId);
    }

    // Enum-based (used internally)
    public static Worker createWorker(WorkerRole role, City city, String name, UUID npcId) {
        switch (role) {
            case FARMER:     return new Farmer(city, name, npcId);
            case MINER:      return new Miner(city, name, npcId);
            case MERCHANT:   return new Merchant(city, name, npcId);
            case GUARD:      return new Guard(city, name, npcId);
            case FISHERMAN:  return new Fisherman(city, name, npcId);
            case WOODSMAN:   return new Woodsman(city, name, npcId);
            case BUILDER:    return new Builder(city, name, npcId);
            case MAYOR:      return new Mayor(city, name, npcId);
            default: throw new IllegalArgumentException("Unknown WorkerRole: " + role);
        }
    }
}

