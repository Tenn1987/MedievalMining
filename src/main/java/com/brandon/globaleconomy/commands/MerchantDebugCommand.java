package com.brandon.globaleconomy.commands;

import com.brandon.globaleconomy.economy.impl.workers.Merchant;
import com.brandon.globaleconomy.economy.impl.workers.Worker;
import com.brandon.globaleconomy.economy.impl.workers.WorkerRole;
import com.brandon.globaleconomy.economy.market.MarketAPI;
import com.brandon.globaleconomy.economy.market.MarketItem;
import com.brandon.globaleconomy.npc.traits.WorkerTrait;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Map;

public class MerchantDebugCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage("§6[MerchantDebug] Starting diagnostic for all merchants...");

        for (NPC npc : CitizensAPI.getNPCRegistry()) {
            if (!npc.hasTrait(WorkerTrait.class)) continue;

            WorkerTrait trait = npc.getTrait(WorkerTrait.class);
            Worker worker = trait.getWorker();
            if (worker == null || worker.getRole() != WorkerRole.MERCHANT) continue;

            sender.sendMessage("§7---");
            sender.sendMessage("§eMerchant: §f" + worker.getName());
            sender.sendMessage("§7City: §f" + worker.getCity().getName());

            Map<String, Integer> resources = worker.getCity().getResources();
            if (resources.isEmpty()) {
                sender.sendMessage("§cInventory is empty.");
                continue;
            }

            sender.sendMessage("§7Resources:");
            resources.forEach((item, qty) -> sender.sendMessage("  §f" + item + ": " + qty));

            sender.sendMessage("§7Checking tradable items:");
            boolean foundTradable = false;

            for (Map.Entry<String, Integer> entry : resources.entrySet()) {
                String itemName = entry.getKey();
                int qty = entry.getValue();

                Material mat = Material.matchMaterial(itemName);
                MarketItem marketItem = mat != null ? MarketAPI.getInstance().getItem(mat) : null;

                if (mat == null) {
                    sender.sendMessage("§c- Invalid material: " + itemName);
                    continue;
                }

                if (marketItem == null) {
                    sender.sendMessage("§c- Not in market: " + itemName);
                    continue;
                }

                if (qty <= 2) {
                    sender.sendMessage("§e- §7" + itemName + " has too little stock (" + qty + "), won't sell.");
                    continue;
                }

                sender.sendMessage("§a- §7" + itemName + " is tradable at $" + marketItem.getCurrentPrice());
                foundTradable = true;
            }

            if (!foundTradable) {
                sender.sendMessage("§cNo items qualified for sale.");
            }
        }

        return true;
    }
}
