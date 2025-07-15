package com.brandon.globaleconomy.city;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Biome;

import java.util.EnumSet;
import java.util.logging.Logger;

public class CityResourceScanner {

    public enum CityResourceType {
        FOREST,
        FERTILE,
        HILLS,
        WATER,
        MINERAL,
        DESERT,
        SWAMP
    }

    private static final int SCAN_RADIUS = 64;
    private static final int STEP = 4;

    public static Location getNearestResource(City city, CityResourceType type) {
        Location origin = city.getLocation();
        World world = origin.getWorld();
        Logger log = Bukkit.getLogger();

        if (world == null) {
            log.warning("[CityResourceScanner] World is null for city " + city.getName());
            return null;
        }

        double closestDistance = Double.MAX_VALUE;
        Location bestLocation = null;

        for (int dx = -SCAN_RADIUS; dx <= SCAN_RADIUS; dx += STEP) {
            for (int dz = -SCAN_RADIUS; dz <= SCAN_RADIUS; dz += STEP) {
                Location testLoc = origin.clone().add(dx, 0, dz);
                testLoc.setY(world.getHighestBlockYAt(testLoc));
                Block block = testLoc.getBlock();

                if (matchesResourceType(block, type)) {
                    double distance = origin.distanceSquared(testLoc);
                    if (distance < closestDistance) {
                        closestDistance = distance;
                        bestLocation = testLoc;
                    }
                }
            }
        }

        if (bestLocation == null) {
            log.info("[CityResourceScanner] No " + type.name() + " resource found near " + city.getName());
        }

        return bestLocation;
    }

    private static boolean matchesResourceType(Block block, CityResourceType type) {
        Material material = block.getType();
        Biome biome = block.getBiome();

        return switch (type) {
            case FOREST -> biome.name().contains("FOREST") || biome.name().contains("TAIGA") || biome.name().contains("JUNGLE");
            case FERTILE -> EnumSet.of(
                    Material.DIRT,
                    Material.GRASS_BLOCK,
                    Material.COARSE_DIRT,
                    Material.FARMLAND,
                    Material.MUD
            ).contains(material);
            case HILLS -> biome.name().contains("HILL") || biome.name().contains("MOUNTAIN") || biome.name().contains("PEAK");
            case WATER -> material == Material.WATER;
            case MINERAL -> biome.name().contains("BADLANDS") || biome.name().contains("MOUNTAIN") || biome.name().contains("HILL");
            case DESERT -> biome.name().contains("DESERT") || biome.name().contains("BADLANDS");
            case SWAMP -> biome.name().contains("SWAMP") || biome.name().contains("MANGROVE");
        };
    }
}
