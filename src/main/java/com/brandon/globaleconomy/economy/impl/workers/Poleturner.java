package com.brandon.globaleconomy.economy.impl.workers;

import com.brandon.globaleconomy.city.City;
import com.brandon.globaleconomy.city.CityProductionManager;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class Poleturner extends Worker {
    private static final List<Material> LOG_TYPES = Arrays.asList(
            Material.OAK_LOG, Material.BIRCH_LOG, Material.SPRUCE_LOG,
            Material.JUNGLE_LOG, Material.ACACIA_LOG, Material.DARK_OAK_LOG,
            Material.MANGROVE_LOG, Material.CHERRY_LOG, Material.DEAD_BUSH // Desert fallback
    );

    public Poleturner(City city, String name, UUID npcId) {
        super(city, name, WorkerRole.POLETURNER, npcId);
    }

    @Override
    public WorkerRole getRole() {
        return WorkerRole.POLETURNER;
    }

    @Override
    public void performWork(City city) {
        if (!isReadyToWork()) return;

        CityProductionManager manager = city.getProductionManager();

        Material inputLog = LOG_TYPES.stream()
                .filter(mat -> city.getResources().getOrDefault(mat.name(), 0) >= 1)
                .findFirst()
                .orElse(null);

        if (inputLog == null) return;

        // Determine stick output: DEAD_BUSH gives 2, logs give 8
        int outputSticks = inputLog == Material.DEAD_BUSH ? 2 : 8;

        if (!manager.canProduce(this, "STICK", outputSticks)) {
            city.log("§e[POLETURNER] " + getName() + " was blocked from making sticks due to production limits.");
            return;
        }

        manager.consume(inputLog.name(), 1);
        manager.recordProduction("STICK", outputSticks);
        city.addItem(Material.STICK, outputSticks);

        rateLimitedLog(getName(), "Poleturner " + getName() + " crafted " + outputSticks + " sticks for " + city.getName(), 5000);
        markCooldown();
    }
}
