package com.brandon.globaleconomy.economy.impl.workers;

import com.brandon.globaleconomy.city.City;
import java.util.*;

public class WorkerManager {

    // For now, all worker creation is manual
    public Worker createWorker(String role, City city, String name) {
        switch (role.toLowerCase()) {
            case "farmer":
                return new Farmer(city, name);
            case "miner":
                return new Miner(city, name);
            case "merchant":
                return new Merchant(city, name);
            case "guard":
                return new Guard(city, name);
            default:
                return null;
        }
    }

    // You can expand this manager later to keep track of all workers globally if you wish
}
