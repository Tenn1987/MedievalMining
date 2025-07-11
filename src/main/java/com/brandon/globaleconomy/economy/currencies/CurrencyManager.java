package com.brandon.globaleconomy.economy.currencies;

import com.brandon.globaleconomy.economy.currencies.CurrencyManager;

import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CurrencyManager {
    private static CurrencyManager instance;
    public static CurrencyManager getInstance() {
        if (instance == null) {
            instance = new CurrencyManager();
        }
        return instance;
    }
    
    private final Map<String, NationalCurrency> currencies = new HashMap<>();
    private final MetalPool metalPool = new MetalPool();

    public boolean createCurrency(String name, boolean metalBacked, String backingMaterial, double backingRatio) {
        if (currencies.containsKey(name)) return false;
        currencies.put(name, new NationalCurrency(name, metalBacked, backingMaterial, backingRatio));
        return true;
    }

    public NationalCurrency getCurrency(String name) {
        return currencies.get(name);
    }

    public Set<String> getCurrencyCodes() {
        return currencies.keySet();
    }

    public boolean hasCurrency(String name) {
        return currencies.containsKey(name);
    }

    public void addCurrency(String name) {
        createCurrency(name, false, null, 0.0); // Default values for non-metal-backed currency
    }

    public MetalPool getMetalPool() {
        return metalPool;
    }

    public Map<String, NationalCurrency> getCurrencies() {
        return currencies;
    }

    public double getValue(String code) {
        NationalCurrency currency = currencies.get(code);
        if (currency == null) return 0.0;
        return currency.getValue(metalPool);
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
        for (Map.Entry<String, NationalCurrency> entry : currencies.entrySet()) {
            result.put(entry.getKey(), baseVal / entry.getValue().getValue(metalPool));
        }
        return result;
    }

    public void refreshCurrencyValues(Map<String, Integer> globalBackings) {
        for (NationalCurrency currency : currencies.values()) {
            if (currency.isMetalBacked()) {
                int backingAmount = globalBackings.getOrDefault(currency.getBackingMaterial(), 0);
                currency.adjustValueFromBacking(backingAmount);
            }
        }
    }
    public Material getCurrencyMaterial(String currencyCode) {
        NationalCurrency currency = currencies.get(currencyCode);
        if (currency == null) return Material.IRON_INGOT;

        String backing = currency.getBackingMaterial();
        if (backing == null) return Material.IRON_INGOT;

        try {
            return Material.valueOf(backing.toUpperCase()); // e.g., "gold" → GOLD
        } catch (IllegalArgumentException e) {
            return Material.IRON_INGOT;
        }
    }

}
