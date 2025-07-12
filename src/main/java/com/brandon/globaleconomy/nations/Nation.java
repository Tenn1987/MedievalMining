package com.brandon.globaleconomy.nations;

import com.brandon.globaleconomy.nations.politics.PoliticalNPC;
import com.brandon.globaleconomy.nations.politics.PoliticalRole;

import java.io.Serializable;
import java.util.*;

public class Nation implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String name;
    private final String currencyName;
    private final List<String> cityNames = new ArrayList<>();
    private final List<PoliticalNPC> politicalNPCs = new ArrayList<>();
    private final Map<String, Double> treasury = new HashMap<>(); // currencyName -> balance

    private boolean enforceNationalCurrency = true;

    public Nation(String name, String currencyName) {
        this.name = name;
        this.currencyName = currencyName;
    }

    // City Management
    public void addCity(String cityName) {
        if (!cityNames.contains(cityName)) cityNames.add(cityName);
    }

    public List<String> getCityNames() {
        return new ArrayList<>(cityNames);
    }

    // Political NPCs
    public void addPoliticalNPC(PoliticalNPC npc) {
        politicalNPCs.add(npc);
    }

    public List<PoliticalNPC> getPoliticalNPCs() {
        return new ArrayList<>(politicalNPCs);
    }

    public PoliticalNPC getLeader() {
        return politicalNPCs.stream()
                .filter(n -> n.getRole() == PoliticalRole.KING || n.getRole() == PoliticalRole.QUEEN)
                .findFirst().orElse(null);
    }

    // Treasury and Economy
    public void deposit(String currency, double amount) {
        treasury.merge(currency, amount, Double::sum);
    }

    public boolean withdraw(String currency, double amount) {
        double current = treasury.getOrDefault(currency, 0.0);
        if (current >= amount) {
            treasury.put(currency, current - amount);
            return true;
        }
        return false;
    }

    public double getBalance(String currency) {
        return treasury.getOrDefault(currency, 0.0);
    }

    public Map<String, Double> getAllBalances() {
        return new HashMap<>(treasury);
    }

    // Currency Enforcement
    public boolean isCurrencyEnforced() {
        return enforceNationalCurrency;
    }

    public void setCurrencyEnforced(boolean enforce) {
        this.enforceNationalCurrency = enforce;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public String getName() {
        return name;
    }
}
