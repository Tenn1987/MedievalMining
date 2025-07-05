package com.brandon.globaleconomy.economy.impl.workers;

import com.brandon.globaleconomy.city.City;
import java.util.UUID;

public class Guard extends Worker {
    public Guard(City city, String name, UUID npcId) {
        super(city, name, WorkerRole.GUARD, npcId);
    }

    @Override
    public void performWork(City city) {
        // Guarding logic here
        markCooldown();
    }
}