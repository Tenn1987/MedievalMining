package com.brandon.globaleconomy.economy.currencies;

import com.brandon.globaleconomy.core.PluginCore;

public class ExchangeEngine {

    private final ExchangeRateManager exchangeRateManager = ExchangeRateManager.getInstance();

    public ExchangeRateManager getExchangeRateManager() {
        return exchangeRateManager;
    }

    public double getRate(String from, String to) {
        return exchangeRateManager.getExchangeRate(from, to);
    }

    public double convert(String from, String to, double amount) {
        return amount * getRate(from, to);
    }

    public void addCurrency(String currency) {
        NationalCurrency nationalCurrency = PluginCore.getInstance().getCurrencyManager().getCurrency(currency);
        if (nationalCurrency == null) return;

        String code = nationalCurrency.getName();
        double value = nationalCurrency.isMetalBacked()
                ? nationalCurrency.getBackingRatio() // or a calculated value
                : 1.0;
        String backingMaterial = nationalCurrency.getBackingMaterial() != null
                ? nationalCurrency.getBackingMaterial()
                : "FIAT";

        if (!ExchangeRateManager.getInstance().getAllRates(code).containsKey(code)) {
            ExchangeRateManager.getInstance().registerCurrency(code, value, backingMaterial);
        }
    }

}