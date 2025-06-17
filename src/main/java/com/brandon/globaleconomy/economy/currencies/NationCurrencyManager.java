package com.brandon.globaleconomy.economy.currencies;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class NationCurrencyManager extends CurrencyManager {
    // For per-player currency balances
    private final Map<UUID, Map<String, Double>> balances = new HashMap<>();

    // Create new currency (same as CurrencyManager, can extend/override)
    @Override
    public boolean createCurrency(String name, boolean isMetalBacked, String backingMaterial, double backingRatio) {
        return super.createCurrency(name, isMetalBacked, backingMaterial, backingRatio);
    }

    public double getBalance(UUID playerId, String currency) {
        return balances.getOrDefault(playerId, new HashMap<>()).getOrDefault(currency, 0.0);
    }

    public void setBalance(UUID playerId, String currency, double amount) {
        balances.computeIfAbsent(playerId, k -> new HashMap<>()).put(currency, amount);
    }

    public boolean deposit(UUID playerId, String currency, double amount) {
        double current = getBalance(playerId, currency);
        setBalance(playerId, currency, current + amount);
        return true;
    }

    public boolean withdraw(UUID playerId, String currency, double amount) {
        double current = getBalance(playerId, currency);
        if (current < amount) return false;
        setBalance(playerId, currency, current - amount);
        return true;
    }


}
