package com.brandon.globaleconomy.listeners;

import com.brandon.globaleconomy.city.City;
import com.brandon.globaleconomy.gui.MerchantTradeGUI;
import com.brandon.globaleconomy.city.CityManager;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.entity.Player;

public class NPCClickListener implements Listener {

    private final CityManager cityManager;

    public NPCClickListener(CityManager cityManager) {
        this.cityManager = cityManager;
    }

    @EventHandler
    public void onNpcRightClick(NPCRightClickEvent event) {
        NPC npc = event.getNPC();
        Player player = event.getClicker();

        if (npc.getName().contains("Merchant")) {
            City city = cityManager.getCityAt(player.getLocation());
            if (city != null) {
                MerchantTradeGUI.open(player, city);
            }
        }
    }
}
