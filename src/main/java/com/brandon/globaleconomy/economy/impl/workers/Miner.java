package com.brandon.globaleconomy.economy.impl.workers;

import com.brandon.globaleconomy.city.City;
import java.util.UUID;

public class Miner extends Worker {
    public Miner(City city, String name, UUID npcId) {
        super(city, name, WorkerRole.MINER, npcId);
    }

    @Override
    public void performWork(City city) {
        // Mining logic here
        markCooldown();
    }
}