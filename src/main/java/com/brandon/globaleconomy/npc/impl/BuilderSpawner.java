package com.brandon.globaleconomy.npc.impl;

import com.brandon.globaleconomy.city.City;
import com.brandon.globaleconomy.economy.impl.workers.Builder;
import com.brandon.globaleconomy.npc.traits.BuilderTrait;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;

import org.bukkit.entity.EntityType;

import java.util.UUID;

public class BuilderSpawner {

    public static void spawn(City city) {
        String name = city.getName() + " Builder";
        UUID uuid = UUID.randomUUID();

        Builder builder = new Builder(city, name, uuid);

        NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, name);
        npc.spawn(city.getLocation());
        npc.data().setPersistent("role", "BUILDER");
        BuilderTrait trait = new BuilderTrait();
        trait.assign(builder, city);
        npc.addTrait(trait); // ✅ Add the empty trait, then assign


        city.getWorkers().add(builder);
        System.out.println("[DEBUG] Builder NPC spawned for " + city.getName());
    }

}
