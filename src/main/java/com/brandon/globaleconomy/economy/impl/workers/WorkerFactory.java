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
        return switch (role) {
            case FARMER -> new Farmer(city, name, npcId);
            case MINER -> new Miner(city, name, npcId);
            case MERCHANT -> new Merchant(city, name, npcId);
            case GUARD -> new Guard(city, name, npcId);
            case FISHERMAN -> new Fisherman(city, name, npcId);
            case WOODSMAN -> new Woodsman(city, name, npcId);
            case BUILDER -> new Builder(city, name, npcId);
            case DIGGER -> new Digger(city, name, npcId);
            case FORAGER -> new Forager(city, name, npcId);
            case POLETURNER -> new Poleturner(city, name, npcId);
            case TOOLMAKER -> new Toolmaker(city, name, npcId);
            case MAYOR -> new Mayor(city, name, npcId);
            case RESIDENT -> new Resident(city, name, npcId); // ✅ ADD THIS
            default -> throw new IllegalArgumentException("Unhandled worker role: " + role);
        };
    }

}

