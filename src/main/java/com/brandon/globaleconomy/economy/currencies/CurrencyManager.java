package com.brandon.globaleconomy.economy.currencies;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CurrencyManager {
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

}
