package com.brandon.globaleconomy.economy.impl.workers;

import com.brandon.globaleconomy.city.City;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Random;
import java.util.UUID;

public class Resident extends Worker {
    private static final long IDLE_MOVE_INTERVAL = 12000;
    private long lastMoveTime = 0;
    private static final Random RANDOM = new Random();

    public Resident(City city, String name, UUID uuid) {
        super(city, name, WorkerRole.RESIDENT, uuid);
    }

    @Override
    public void performWork(City city) {
        System.out.println("[Resident] performWork called");
        if (System.currentTimeMillis() - lastMoveTime < IDLE_MOVE_INTERVAL) return;
        lastMoveTime = System.currentTimeMillis();

        NPC npc = CitizensAPI.getNPCRegistry().getByUniqueId(npcId);
        if (npc == null || !npc.isSpawned()) return;

        Location base = city.getLocation();
        Location target = base.clone().add(-5 + RANDOM.nextInt(11), 0, -5 + RANDOM.nextInt(11));

        World world = target.getWorld();
        int y = world.getHighestBlockYAt(target.getBlockX(), target.getBlockZ());
        target.setY(y);

        npc.getNavigator().setTarget(target);
        Bukkit.getLogger().info("[Resident] " + getName() + " moving to " + target);

    }
}
