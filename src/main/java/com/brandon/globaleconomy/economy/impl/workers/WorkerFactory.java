package com.brandon.globaleconomy.economy.impl.workers;

import com.brandon.globaleconomy.city.City;

public class WorkerFactory {
    public static Worker createWorker(String role, City city, String name) {
        switch (role.toLowerCase()) {
            case "farmer": return new Farmer(city, name);
            case "miner": return new Miner(city, name);
            case "merchant": return new Merchant(city, name);
            case "guard": return new Guard(city, name);
            case "fisherman": return new Fisherman(city, name);
            default: throw new IllegalArgumentException("Unknown worker role: " + role);
        }
    }
}
