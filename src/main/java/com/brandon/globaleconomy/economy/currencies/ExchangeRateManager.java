package com.brandon.globaleconomy.economy.currencies;

import com.brandon.globaleconomy.city.City;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

public class ExchangeRateManager {
    private static final ExchangeRateManager instance = new ExchangeRateManager();
    private final Map<String, Double> currencyValues = new HashMap<>(); // e.g., {"GBP": 1.0, "FRF": 1.25}
    private final Map<String, String> currencyBacking = new HashMap<>(); // e.g., "GBP" -> "GOLD_INGOT"


    public static ExchangeRateManager getInstance() {
        return instance;
    }

    public void registerCurrency(String code, double initialValue, String backingMaterial) {
        currencyValues.put(code.toUpperCase(), initialValue);
        currencyBacking.put(code.toUpperCase(), backingMaterial.toUpperCase());
    }

    public void recalculateCurrencyFromBacking(City city) {
        for (String currency : currencyValues.keySet()) {
            String backing = currencyBacking.get(currency);
            if (backing == null) continue;

            int backingAmount = city.getCityInventory().getOrDefault(Material.valueOf(backing), 0);
            double newValue = Math.max(0.1, Math.log1p(backingAmount)); // logarithmic scale
            currencyValues.put(currency, newValue);
        }
    }



    public boolean hasCurrency(String currencyName) {
        return currencyValues.containsKey(currencyName.toUpperCase());
    }

    public void updateValue(String code, double newValue) {
        currencyValues.put(code.toUpperCase(), newValue);
    }

    public double getValue(String code) {
        return currencyValues.getOrDefault(code.toUpperCase(), 1.0);
    }

    public double getExchangeRate(String from, String to) {
        double fromVal = getValue(from);
        double toVal = getValue(to);
        return fromVal / toVal;
    }

    public String formatExchange(String from, String to) {
        double rate = getExchangeRate(from, to);
        return String.format("1 %s = %.2f %s", from, rate, to);
    }

    public Map<String, Double> getAllRates(String base) {
        Map<String, Double> result = new HashMap<>();
        double baseVal = getValue(base);
        for (Map.Entry<String, Double> entry : currencyValues.entrySet()) {
            result.put(entry.getKey(), baseVal / entry.getValue());
        }
        return result;
    }
}
