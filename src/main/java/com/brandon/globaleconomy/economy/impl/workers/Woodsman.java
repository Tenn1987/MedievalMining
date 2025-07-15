package com.brandon.globaleconomy.economy.impl.workers;

import com.brandon.globaleconomy.city.City;
import com.brandon.globaleconomy.city.CityInventory;
import com.brandon.globaleconomy.city.CityResourceScanner;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Random;

import static com.brandon.globaleconomy.core.PluginCore.getInstance;

public class Woodsman extends Worker {

    private Location forestLocation;
    private boolean headingToForest = true;
    private long lastWorked = 0;
    private static final Random RANDOM = new Random();

    private static final List<Material> LOG_TYPES = List.of(
            Material.OAK_LOG, Material.BIRCH_LOG, Material.SPRUCE_LOG,
            Material.JUNGLE_LOG, Material.ACACIA_LOG, Material.DARK_OAK_LOG,
            Material.CHERRY_LOG, Material.MANGROVE_LOG
    );

    public Woodsman(City city, String name, java.util.UUID uuid) {
        super(city, name, WorkerRole.WOODSMAN, uuid);
    }

    @Override
    public void performWork(City city) {
        if (System.currentTimeMillis() - lastWorked < 10000) return;
        lastWorked = System.currentTimeMillis();

        if (forestLocation == null) {
            forestLocation = CityResourceScanner.getNearestResource(city, CityResourceScanner.CityResourceType.FOREST);
            if (forestLocation == null) {
                city.log("§c[Woodsman] No forest found near " + city.getName());
                return;
            }
        }

        NPC npc = getNPC();
        if (npc == null || !npc.isSpawned()) {
            city.log("§c[Woodsman] NPC " + getName() + " is not spawned.");
            return;
        }

        if (headingToForest) {
            npc.getNavigator().setTarget(forestLocation);
            headingToForest = false;
            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                Material logType = LOG_TYPES.get(RANDOM.nextInt(LOG_TYPES.size()));
                int amount = 2 + RANDOM.nextInt(3);

                CityInventory inventory = city.getInventory();
                inventory.addItem(logType, amount);

                city.log("§7[Woodsman] " + getName() + " returned with " + amount + " " + logType.name());

                npc.getNavigator().setTarget(city.getLocation());
                headingToForest = true;
            }
        }.runTaskLater(getInstance(), 60L);
    }

    private NPC getNPC() {
        return CitizensAPI.getNPCRegistry().getByUniqueId(getNpcId());
    }
}
