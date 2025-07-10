package com.brandon.globaleconomy.economy.impl.workers;

import com.brandon.globaleconomy.city.City;

import java.util.*;

public class WorkerManager {
    private static WorkerManager instance;
    private final Map<UUID, Worker> workers = new HashMap<>();
    private final List<Worker> allWorkers = new ArrayList<>();

    public static WorkerManager getInstance() {
        if (instance == null) instance = new WorkerManager();
        return instance;
    }

    public void registerWorker(Worker worker) { allWorkers.add(worker);
        workers.put(worker.getNpcId(), worker);
    }

    public List<Worker> getAllWorkers() {
        return allWorkers;
    }

    public void addWorker(Worker worker) {
        workers.put(worker.getNpcId(), worker);
    }


    public Worker createWorker(WorkerRole role, City city, String name, UUID npcId) {
        return switch (role) {
            case FARMER -> new Farmer(city, name, npcId);
            case MINER -> new Miner(city, name, npcId);
            case MERCHANT -> new Merchant(city, name, npcId);
            case GUARD -> new Guard(city, name, npcId);
            case FISHERMAN -> new Fisherman(city, name, npcId);
            case WOODSMAN -> new Woodsman(city, name, npcId);
            case BUILDER -> new Builder(city, name, npcId);
            default -> throw new IllegalArgumentException("Unhandled worker role: " + role);
        };
    }
}