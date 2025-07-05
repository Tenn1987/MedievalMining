package com.brandon.globaleconomy.economy.impl.workers;

import com.brandon.globaleconomy.city.City;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.Location;
import java.util.UUID;

public class Builder extends Worker {
    public Builder(City city, String name, UUID npcId) {
        super(city, name, WorkerRole.BUILDER, npcId);
    }


    @Override
    public void performWork(City city) {
        if (!isReadyToWork()) return;

        // Placeholder: build a simple stone foundation
        Location center = city.getLocation().clone().add(3, 0, 3); // offset from city center
        for (int x = 0; x < 3; x++) {
            for (int z = 0; z < 3; z++) {
                Block block = center.clone().add(x, 0, z).getBlock();
                block.setType(Material.COBBLESTONE);
            }
        }

        System.out.println(name + " is building a 3x3 foundation near " + city.getName());
        markCooldown();
    }

    public static Builder builder(City city, String name, UUID npcId) {
        return new Builder(city, name, npcId);
    }
}
