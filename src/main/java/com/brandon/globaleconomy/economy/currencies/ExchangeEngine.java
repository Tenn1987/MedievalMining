package com.brandon.globaleconomy.economy.currencies;

import java.util.HashMap;
import java.util.Map;

public class ExchangeEngine {
    private final Map<String, Map<String, Double>> rates = new HashMap<>();

    public void addCurrency(String currency) {
        rates.putIfAbsent(currency, new HashMap<>());
        for (String other : rates.keySet()) {
            if (!other.equals(currency)) {
                double rate = Math.random() * 0.5 + 0.75; // 0.75 - 1.25
                rates.get(currency).put(other, rate);
                rates.get(other).put(currency, 1.0 / rate);
            }
        }
    }

    public double getRate(String from, String to) {
        return rates.getOrDefault(from, Map.of()).getOrDefault(to, 1.0);
    }

    public double convert(String from, String to, double amount) {
        return amount * getRate(from, to);
    }
}
