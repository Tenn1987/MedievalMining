package com.brandon.globaleconomy.npc;

import com.brandon.globaleconomy.city.City;
import com.brandon.globaleconomy.city.CityManager;
import com.brandon.globaleconomy.economy.impl.workers.Worker;
import net.citizensnpcs.api.npc.NPC;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NPCNationManager {

    private static NPCNationManager instance;

    private final Map<UUID, Worker> npcToWorker = new HashMap<>();
    private final Map<UUID, City> npcToCity = new HashMap<>();

    private CityManager cityManager;

    public static NPCNationManager getInstance() {
        if (instance == null) {
            instance = new NPCNationManager();
        }
        return instance;
    }

    private NPCNationManager() {
    }

    public void initialize(CityManager cityManager) {
        this.cityManager = cityManager;
    }

    public void registerWorker(NPC npc, Worker worker) {
        npcToWorker.put(npc.getUniqueId(), worker);
    }

    public void registerCityNPC(NPC npc, City city) {
        npcToCity.put(npc.getUniqueId(), city);
    }

    public Worker getWorkerByNPC(NPC npc) {
        return npcToWorker.get(npc.getUniqueId());
    }

    public City getCityByNPC(NPC npc) {
        return npcToCity.get(npc.getUniqueId());
    }

    public void unregister(NPC npc) {
        npcToWorker.remove(npc.getUniqueId());
        npcToCity.remove(npc.getUniqueId());
    }

    public void clearAll() {
        npcToWorker.clear();
        npcToCity.clear();
    }

    public CityManager getCityManager() {
        return cityManager;
    }
}
