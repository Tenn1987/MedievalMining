package com.brandon.globaleconomy.nations;

import com.brandon.globaleconomy.city.City;
import com.brandon.globaleconomy.diplomacy.DiplomaticStatus;

import java.util.*;

public class DiplomacyManager {
    private static DiplomacyManager instance;

    private final Map<String, Map<String, DiplomaticStatus>> relations = new HashMap<>();

    // 🔹 Singleton accessor
    public static DiplomacyManager getInstance() {
        if (instance == null) {
            instance = new DiplomacyManager();
        }
        return instance;
    }

    // 🔹 Private constructor
    private DiplomacyManager() {}

    public DiplomaticStatus getRelation(String nationA, String nationB) {
        return relations
                .getOrDefault(nationA, new HashMap<>())
                .getOrDefault(nationB, DiplomaticStatus.NEUTRAL);
    }

    public void setRelation(String nationA, String nationB, DiplomaticStatus status) {
        relations.computeIfAbsent(nationA, k -> new HashMap<>()).put(nationB, status);
        relations.computeIfAbsent(nationB, k -> new HashMap<>()).put(nationA, status);
    }

    public List<City> getFriendlyCities(String nation, List<City> allCities) {
        List<City> result = new ArrayList<>();
        for (City city : allCities) {
            String otherNation = city.getNation();
            if (!nation.equals(otherNation)
                    && getRelation(nation, otherNation) == DiplomaticStatus.FRIENDLY) {
                result.add(city);
            }
        }
        return result;
    }
}
