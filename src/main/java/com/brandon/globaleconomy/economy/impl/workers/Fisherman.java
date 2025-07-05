package com.brandon.globaleconomy.economy.impl.workers;

import com.brandon.globaleconomy.city.City;
import java.util.UUID;

public class Fisherman extends Worker {
    public Fisherman(City city, String name, UUID npcId) {
        super(city, name, WorkerRole.FISHERMAN, npcId);
    }

    @Override
    public void performWork(City city) {
        // Fishing logic here
        markCooldown();
    }
}
