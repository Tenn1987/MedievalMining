package com.brandon.globaleconomy.diplomacy;

import java.util.*;

public class DiplomaticManager {

    public enum DiplomaticStatus {
        ALLIED,
        FRIENDLY,
        NEUTRAL,
        EMBARGO,
        AT_WAR
    }

    private static DiplomaticManager instance;
    private final Map<String, Map<String, DiplomaticStatus>> diplomacyMap = new HashMap<>();

    public static DiplomaticManager getInstance() {
        if (instance == null) instance = new DiplomaticManager();
        return instance;
    }

    public DiplomaticStatus getStatus(String nationA, String nationB) {
        return diplomacyMap.getOrDefault(nationA, new HashMap<>())
                .getOrDefault(nationB, DiplomaticStatus.NEUTRAL);
    }

    public void setStatus(String nationA, String nationB, DiplomaticStatus status) {
        diplomacyMap.computeIfAbsent(nationA, k -> new HashMap<>()).put(nationB, status);
        diplomacyMap.computeIfAbsent(nationB, k -> new HashMap<>()).put(nationA, status);
    }

    public void recordTrade(String nationA, String nationB) {
        DiplomaticStatus current = getStatus(nationA, nationB);
        if (current == DiplomaticStatus.AT_WAR) return; // no improvement at war
        if (current == DiplomaticStatus.EMBARGO) {
            setStatus(nationA, nationB, DiplomaticStatus.NEUTRAL);
        } else if (current == DiplomaticStatus.NEUTRAL) {
            setStatus(nationA, nationB, DiplomaticStatus.FRIENDLY);
        }
    }

    public void recordAttack(String nationA, String nationB) {
        setStatus(nationA, nationB, DiplomaticStatus.AT_WAR);
    }

    public void recordEmbargo(String nationA, String nationB) {
        if (getStatus(nationA, nationB) != DiplomaticStatus.AT_WAR) {
            setStatus(nationA, nationB, DiplomaticStatus.EMBARGO);
        }
    }

    public void resolvePeace(String nationA, String nationB) {
        setStatus(nationA, nationB, DiplomaticStatus.NEUTRAL);
    }

    public List<String> getAllies(String nation) {
        Map<String, DiplomaticStatus> relations = diplomacyMap.getOrDefault(nation, new HashMap<>());
        List<String> allies = new ArrayList<>();
        for (Map.Entry<String, DiplomaticStatus> entry : relations.entrySet()) {
            if (entry.getValue() == DiplomaticStatus.FRIENDLY) {
                allies.add(entry.getKey());
            }
        }
        return allies;
    }

    public boolean formAlliance(String name1, String name2) {
        if (name1.equalsIgnoreCase(name2)) return false;

        DiplomaticStatus current = getStatus(name1, name2);
        if (current == DiplomaticStatus.AT_WAR) return false; // Can't ally with an enemy

        setStatus(name1, name2, DiplomaticStatus.ALLIED);
        return true;
    }

}