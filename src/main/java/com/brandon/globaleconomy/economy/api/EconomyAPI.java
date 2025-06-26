package com.brandon.globaleconomy.economy.api;

import com.brandon.globaleconomy.city.City;
import com.brandon.globaleconomy.city.CityManager;
import com.brandon.globaleconomy.economy.currencies.*;
import com.brandon.globaleconomy.economy.currencies.ExchangeEngine;
import com.brandon.globaleconomy.economy.WalletManager;

import java.util.Map;
import java.util.UUID;

public class EconomyAPI {
    private final WalletManager walletManager;
    private final CurrencyManager currencyManager;
    private final ExchangeEngine exchangeEngine;
    private final CityManager cityManager;

    public EconomyAPI(WalletManager walletManager, CurrencyManager currencyManager,
                      ExchangeEngine exchangeEngine, CityManager cityManager) {
        this.walletManager = walletManager;
        this.currencyManager = currencyManager;
        this.exchangeEngine = exchangeEngine;
        this.cityManager = cityManager;
    }

    // Player economy
    public void depositToPlayer(UUID playerId, String currency, double amount) {
        walletManager.deposit(playerId, currency, amount);
    }

    public boolean withdrawFromPlayer(UUID playerId, String currency, double amount) {
        return walletManager.withdraw(playerId, currency, amount);
    }

    public double getPlayerBalance(UUID playerId, String currency) {
        return walletManager.getBalance(playerId, currency);
    }

    public Map<String, Double> getPlayerWallet(UUID playerId) {
        return walletManager.getAllBalances(playerId);
    }

    // City economy
    public boolean depositToCity(String cityName, String currency, double amount) {
        City city = cityManager.getCityByName(cityName);
        if (city == null) return false;
        city.depositToTreasury(currency, amount);
        return true;
    }

    public boolean withdrawFromCity(String cityName, String currency, double amount) {
        City city = cityManager.getCityByName(cityName);
        if (city == null) return false;
        return city.withdrawFromTreasury(currency, amount);
    }

    public double getCityBalance(String cityName, String currency) {
        City city = cityManager.getCityByName(cityName);
        if (city == null) return 0.0;
        return city.getCityBalance(currency);
    }

    public Map<String, Double> getCityTreasury(String cityName) {
        City city = cityManager.getCityByName(cityName);
        if (city == null) return Map.of();
        return city.getAllCityBalances();
    }

    // Currency creation & exchange
    public boolean createCityCurrency(String cityName, String currencyName) {
        if (currencyManager.hasCurrency(currencyName)) return false;
        currencyManager.addCurrency(currencyName);
        exchangeEngine.addCurrency(currencyName);

        City city = cityManager.getCityByName(cityName);
        if (city != null) {
            city.setPrimaryCurrency(currencyName);
        }
        return true;
    }

    public boolean adoptCurrency(String cityName, String existingCurrency) {
        if (!currencyManager.hasCurrency(existingCurrency)) return false;
        City city = cityManager.getCityByName(cityName);
        if (city == null) return false;
        city.setPrimaryCurrency(existingCurrency);
        return true;
    }

    public double exchange(String from, String to, double amount) {
        return exchangeEngine.convert(from, to, amount);
    }

    public double getExchangeRate(String from, String to) {
        return exchangeEngine.getRate(from, to);
    }
}
