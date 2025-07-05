package com.brandon.globaleconomy.economy.impl.workers;

import com.brandon.globaleconomy.city.City;
import java.util.UUID;

public class Mayor extends Worker {
    public Mayor(City city, String name, UUID npcId) {
        super(city, name, WorkerRole.MAYOR, npcId);
    }

    @Override
    public void performWork(City city) {
        // Placeholder: assign jobs based on town resources
        System.out.println(name + " is governing " + city.getName());
        markCooldown();
    }
}
