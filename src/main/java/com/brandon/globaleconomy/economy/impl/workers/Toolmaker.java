package com.brandon.globaleconomy.economy.impl.workers;

import com.brandon.globaleconomy.city.City;
import com.brandon.globaleconomy.city.CityProductionManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;

public class Toolmaker extends Worker {
    public Toolmaker(City city, String name, UUID npcId) {
        super(city, name, WorkerRole.TOOLMAKER, npcId);
    }

    @Override
    public void performWork(City city) {
        CityProductionManager productionManager = city.getProductionManager();
        Map<String, Integer> resources = city.getResources();

        tryCraftTool(city, productionManager, resources, "IRON", 2, 3, "IRON_AXE");
        tryCraftTool(city, productionManager, resources, "IRON", 2, 2, "IRON_SWORD");
        tryCraftTool(city, productionManager, resources, "IRON", 2, 3, "IRON_PICKAXE");
        tryCraftTool(city, productionManager, resources, "IRON", 2, 1, "IRON_SHOVEL");
        tryCraftTool(city, productionManager, resources, "IRON", 2, 2, "IRON_HOE");

        tryCraftTool(city, productionManager, resources, "STONE", 2, 3, "STONE_AXE");
        tryCraftTool(city, productionManager, resources, "STONE", 2, 2, "STONE_SWORD");
        tryCraftTool(city, productionManager, resources, "STONE", 2, 3, "STONE_PICKAXE");
        tryCraftTool(city, productionManager, resources, "STONE", 2, 1, "STONE_SHOVEL");
        tryCraftTool(city, productionManager, resources, "STONE", 2, 2, "STONE_HOE");

        tryCraftTool(city, productionManager, resources, "WOOD", 2, 3, "WOODEN_AXE");
        tryCraftTool(city, productionManager, resources, "WOOD", 2, 2, "WOODEN_SWORD");
        tryCraftTool(city, productionManager, resources, "WOOD", 2, 3, "WOODEN_PICKAXE");
        tryCraftTool(city, productionManager, resources, "WOOD", 2, 1, "WOODEN_SHOVEL");
        tryCraftTool(city, productionManager, resources, "WOOD", 2, 2, "WOODEN_HOE");
    }

    private void tryCraftTool(City city, CityProductionManager manager, Map<String, Integer> resources,
                              String material, int sticksRequired, int materialRequired, String toolName) {

        String sticks = "STICK";
        if (resources.getOrDefault(sticks, 0) < sticksRequired) return;
        if (resources.getOrDefault(material, 0) < materialRequired) return;

        if (!manager.canProduce(this, toolName, 1)) return;

        // Consume resources
        manager.consume(sticks, sticksRequired);
        manager.consume(material, materialRequired);

        // Record and add tool
        manager.recordProduction(toolName, 1);
        city.addItem(Material.valueOf(toolName), 1);
    }
}
