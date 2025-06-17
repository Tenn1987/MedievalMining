package com.brandon.globaleconomy.economy;

import java.util.*;

public class WalletManager {
    // playerUUID -> (currencyName -> balance)
    private final Map<UUID, Map<String, Double>> wallets = new HashMap<>();

    public double getBalance(UUID player, String currency) {
        return wallets.getOrDefault(player, Collections.emptyMap()).getOrDefault(currency, 0.0);
    }

    public void deposit(UUID player, String currency, double amount) {
        wallets.computeIfAbsent(player, k -> new HashMap<>())
                .merge(currency, amount, Double::sum);
    }

    public boolean withdraw(UUID player, String currency, double amount) {
        Map<String, Double> bal = wallets.getOrDefault(player, null);
        if (bal == null || bal.getOrDefault(currency, 0.0) < amount) return false;
        bal.put(currency, bal.get(currency) - amount);
        return true;
    }

    public Set<String> getCurrencies(UUID player) {
        return wallets.getOrDefault(player, Collections.emptyMap()).keySet();
    }
}