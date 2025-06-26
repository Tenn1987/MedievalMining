
package com.brandon.globaleconomy.economy;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WalletManager {
    private final Map<UUID, Map<String, Double>> wallets = new HashMap<>();

    public void deposit(UUID playerId, String currency, double amount) {
        wallets.computeIfAbsent(playerId, k -> new HashMap<>());
        wallets.get(playerId).merge(currency, amount, Double::sum);
    }

    public boolean withdraw(UUID playerId, String currency, double amount) {
        Map<String, Double> wallet = wallets.get(playerId);
        if (wallet == null) return false;
        double current = wallet.getOrDefault(currency, 0.0);
        if (current >= amount) {
            wallet.put(currency, current - amount);
            return true;
        }
        return false;
    }

    public double getBalance(UUID playerId, String currency) {
        Map<String, Double> wallet = wallets.get(playerId);
        return wallet != null ? wallet.getOrDefault(currency, 0.0) : 0.0;
    }

    public Map<String, Double> getAllBalances(UUID playerId) {
        return new HashMap<>(wallets.getOrDefault(playerId, new HashMap<>()));
    }
}
