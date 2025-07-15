package com.brandon.globaleconomy.economy.impl.workers;

import com.brandon.globaleconomy.city.City;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;
import java.util.UUID;

import static com.brandon.globaleconomy.core.PluginCore.getInstance;

public class Resident extends Worker {
    private long lastWanderTime = 0;
    private static final long WANDER_COOLDOWN_MS = 12000;
    private static final Random RANDOM = new Random();

    public Resident(City city, String name, UUID uuid) {
        super(city, name, WorkerRole.RESIDENT, uuid);
    }

    @Override
    public void performWork(City city) {
        if (System.currentTimeMillis() - lastWanderTime < WANDER_COOLDOWN_MS) return;
        lastWanderTime = System.currentTimeMillis();

        NPC npc = CitizensAPI.getNPCRegistry().getByUniqueId(getNpcId());
        if (npc == null || !npc.isSpawned()) return;

        Location base = city.getLocation();
        World world = base.getWorld();
        if (world == null) return;

        int dx = RANDOM.nextInt(11) - 5; // -5 to +5
        int dz = RANDOM.nextInt(11) - 5;
        Location wanderTarget = base.clone().add(dx, 0, dz);
        wanderTarget.setY(world.getHighestBlockYAt(wanderTarget));

        npc.getNavigator().setTarget(wanderTarget);

        // Optional: Return to center after wandering
        new BukkitRunnable() {
            @Override
            public void run() {
                if (npc.isSpawned()) {
                    npc.getNavigator().setTarget(city.getLocation());
                }
            }
        }.runTaskLater(getInstance(), 60L); // return after ~3 seconds
    }
}
