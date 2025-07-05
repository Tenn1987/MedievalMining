package com.brandon.globaleconomy.economy.impl.workers;

import com.brandon.globaleconomy.city.City;
import java.util.UUID;

public class Merchant extends Worker {
    public Merchant(City city, String name, UUID npcId) {
        super(city, name, WorkerRole.MERCHANT, npcId);
    }

    @Override
    public void performWork(City city) {
        // Trading logic here
        markCooldown();
    }
}
