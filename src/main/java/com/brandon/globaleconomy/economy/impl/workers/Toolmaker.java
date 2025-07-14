package com.brandon.globaleconomy.economy.impl.workers;

import com.brandon.globaleconomy.city.City;
import com.brandon.globaleconomy.city.CityProductionManager;
import org.bukkit.Material;

import java.util.*;

public class Toolmaker extends Worker {
    public Toolmaker(City city, String name, UUID npcId) {
        super(city, name, WorkerRole.TOOLMAKER, npcId);
    }

    private static final List<String> TOOL_TYPES = List.of("AXE", "SWORD", "PICKAXE", "SHOVEL", "HOE");
    private static final Map<String, Integer> MATERIAL_COST = Map.of(
            "AXE", 3,
            "SWORD", 2,
            "PICKAXE", 3,
            "SHOVEL", 1,
            "HOE", 2
    );
    private static final int STICK_COST = 2;

    private static final List<String> MATERIAL_TIERS = List.of("IRON", "STONE", "WOOD");

    @Override
    public void performWork(City city) {
        CityProductionManager manager = city.getProductionManager();
        Map<String, Integer> resources = city.getResources();

        for (String toolType : TOOL_TYPES) {
            for (String tier : MATERIAL_TIERS) {
                String toolName = getToolName(tier, toolType);
                int materialNeeded = MATERIAL_COST.getOrDefault(toolType, 2);

                if (canCraft(resources, tier, materialNeeded, STICK_COST)
                        && manager.canProduce(this, toolName, 1)) {

                    // Consume resources
                    manager.consume(tier, materialNeeded);
                    manager.consume("STICK", STICK_COST);

                    // Add crafted tool
                    manager.recordProduction(toolName, 1);
                    city.addItem(Material.valueOf(toolName), 1);

                    rateLimitedLog(getName(), "Toolmaker " + getName() + " crafted 1 " + toolName + " for " + city.getName(), 5000);
                    markCooldown();
                    return; // Craft one tool per tick
                }
            }
        }
    }

    private boolean canCraft(Map<String, Integer> resources, String material, int materialCount, int stickCount) {
        return resources.getOrDefault("STICK", 0) >= stickCount
                && resources.getOrDefault(material, 0) >= materialCount;
    }

    private String getToolName(String material, String toolType) {
        return switch (material) {
            case "WOOD" -> "WOODEN_" + toolType;
            default -> material + "_" + toolType;
        };
    }
}
