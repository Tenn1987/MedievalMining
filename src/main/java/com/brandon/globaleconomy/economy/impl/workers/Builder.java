package com.brandon.globaleconomy.economy.impl.workers;

import com.brandon.globaleconomy.city.City;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;

import java.util.*;

public class Builder extends Worker {

    private static final int PLOT_SIZE = 7;
    private static final int GAP = 2;
    private static final Random random = new Random();

    public Builder(City city, String name, UUID npcId) {
        super(city, name, WorkerRole.BUILDER, npcId);
    }

    @Override
    public void performWork(City city) {
        if (!isReadyToWork()) return;

        Location center = city.getLocation();
        World world = center.getWorld();
        if (world == null) return;

        Biome biome = center.getBlock().getBiome();
        List<BuildingTemplate> templates = TemplateManager.getTemplatesForBiome(biome);
        if (templates.isEmpty()) return;

        // Find a place to build
        Location buildLoc = findBuildLocation(city);
        if (buildLoc == null) return;

        // Flatten area
        flattenTerrain(buildLoc, PLOT_SIZE, world);

        // Pick and build structure
        BuildingTemplate template = templates.get(random.nextInt(templates.size()));
        buildStructure(buildLoc, template, world);

        // Mark this plot as used
        city.addBuiltPlot(buildLoc);

        // Mark cooldown
        markCooldown();
    }

    private Location findBuildLocation(City city) {
        Location center = city.getLocation();
        World world = center.getWorld();
        int radius = 25;

        for (int dx = -radius; dx <= radius; dx += PLOT_SIZE + GAP) {
            for (int dz = -radius; dz <= radius; dz += PLOT_SIZE + GAP) {
                Location check = center.clone().add(dx, 0, dz);
                if (city.isPlotUsed(check)) continue;
                if (isFlatEnough(check, PLOT_SIZE, world)) {
                    return check;
                }
            }
        }
        return null;
    }

    private boolean isFlatEnough(Location origin, int size, World world) {
        int baseY = origin.getBlockY();
        for (int x = 0; x < size; x++) {
            for (int z = 0; z < size; z++) {
                int y = origin.clone().add(x, 0, z).getBlockY();
                if (Math.abs(y - baseY) > 1) return false;
            }
        }
        return true;
    }

    private void flattenTerrain(Location origin, int size, World world) {
        int baseY = origin.getBlockY();
        for (int x = 0; x < size; x++) {
            for (int z = 0; z < size; z++) {
                Location blockLoc = origin.clone().add(x, 0, z);
                Block block = blockLoc.getBlock();
                block.setType(Material.GRASS_BLOCK);
                blockLoc.clone().add(0, 1, 0).getBlock().setType(Material.AIR);
                blockLoc.clone().add(0, 2, 0).getBlock().setType(Material.AIR);
            }
        }
    }

    private void buildStructure(Location origin, BuildingTemplate template, World world) {
        int y = origin.getBlockY();
        List<String> layout = template.layout();
        for (int z = 0; z < layout.size(); z++) {
            String row = layout.get(z);
            for (int x = 0; x < row.length(); x++) {
                char c = row.charAt(x);
                Material mat = switch (c) {
                    case 'W' -> template.wall();
                    case 'F' -> template.floor();
                    case 'R' -> template.roof();
                    case 'D' -> Material.OAK_DOOR;
                    case 'B' -> Material.BARREL;
                    case 'C' -> Material.CRAFTING_TABLE;
                    case ' ' -> Material.AIR;
                    default -> Material.COBBLESTONE;
                };
                Location blockLoc = origin.clone().add(x, 0, z);
                blockLoc.getBlock().setType(mat);
            }
        }
    }

    // --- Template Inner Class ---
    public record BuildingTemplate(String name, List<String> layout, Material floor, Material wall, Material roof) {}

    public static class TemplateManager {
        private static final Map<Biome, List<BuildingTemplate>> biomeTemplates = new HashMap<>();

        static {
            registerDefaults();
        }

        public static List<BuildingTemplate> getTemplatesForBiome(Biome biome) {
            return biomeTemplates.getOrDefault(biome, List.of());
        }

        private static void registerDefaults() {
            biomeTemplates.put(Biome.PLAINS, List.of(
                    new BuildingTemplate("FarmerHut", List.of(
                            "WWW",
                            "WFW",
                            "WDW"
                    ), Material.OAK_PLANKS, Material.OAK_LOG, Material.OAK_SLAB),
                    new BuildingTemplate("WoodsmanCabin", List.of(
                            "WWW",
                            "WFW",
                            "WDW"
                    ), Material.SPRUCE_PLANKS, Material.SPRUCE_LOG, Material.SPRUCE_SLAB),
                    new BuildingTemplate("MerchantShop", List.of(
                            "WWW",
                            "WFW",
                            "WCD"
                    ), Material.STONE, Material.OAK_PLANKS, Material.BRICK_SLAB)
            ));
            biomeTemplates.put(Biome.TAIGA, List.of(
                    new BuildingTemplate("LogCabin", List.of(
                            "WWW",
                            "WFW",
                            "W W"
                    ), Material.SPRUCE_PLANKS, Material.SPRUCE_LOG, Material.SPRUCE_SLAB)
            ));
        }
    }
}
