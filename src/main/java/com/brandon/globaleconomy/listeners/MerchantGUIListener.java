package com.brandon.globaleconomy.listeners;

import com.brandon.globaleconomy.city.City;
import com.brandon.globaleconomy.gui.MerchantTradeGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class MerchantGUIListener implements Listener {

    private final com.brandon.globaleconomy.city.CityManager cityManager;

    public MerchantGUIListener(com.brandon.globaleconomy.city.CityManager cityManager) {
        this.cityManager = cityManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;

        if (!e.getView().getTitle().startsWith("Merchant: ")) return;

        City city = cityManager.getCityAt(player.getLocation());
        if (city != null) {
            MerchantTradeGUI.handleClick(e, city);
        }
    }
}
