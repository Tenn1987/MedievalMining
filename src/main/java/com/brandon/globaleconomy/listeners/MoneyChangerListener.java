package com.brandon.globaleconomy.listeners;

import com.brandon.globaleconomy.gui.MoneyChangerGUI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class MoneyChangerListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getView().getTitle().equalsIgnoreCase("Moneychanger")) {
            MoneyChangerGUI.handleClick(e);
        }
    }
}
