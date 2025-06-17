package com.brandon.globaleconomy.commands;

import com.brandon.globaleconomy.economy.currencies.CurrencyManager;
import com.brandon.globaleconomy.economy.currencies.NationalCurrency;
import com.brandon.globaleconomy.city.City;
import com.brandon.globaleconomy.city.CityManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CurrencyCommand implements CommandExecutor {

    private final CurrencyManager currencyManager;
    private final CityManager cityManager; // Make sure to import your CityManager

    public CurrencyCommand(CurrencyManager currencyManager, CityManager cityManager) {
        this.currencyManager = currencyManager;
        this.cityManager = cityManager;
    }

    // Helper: can the sender create a currency?
    private boolean canCreateCurrency(CommandSender sender) {
        if (!(sender instanceof Player)) {
            // Console/server always allowed
            return true;
        }
        Player player = (Player) sender;
        // Admin permission/op
        if (player.isOp() || player.hasPermission("economy.currency.create")) {
            return true;
        }
        // Mayor check
        City city = cityManager.getCityByMayor(player.getUniqueId());
        if (city != null && city.getMayorId().equals(player.getUniqueId())) {
            return true;
        }
        // Rebel NPC check
        if (isRebelNPC(player)) {
            return true;
        }
        return false;
    }

    // Stub for future rebel logic
    private boolean isRebelNPC(Player player) {
        // Example: if you tag rebels via metadata, scoreboard, etc.
        // return player.hasMetadata("rebel_leader");
        return false;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage("§cUsage: /currency <create|info|list> ...");
            return true;
        }

        if (args[0].equalsIgnoreCase("create")) {
            if (!canCreateCurrency(sender)) {
                sender.sendMessage("§cOnly mayors, admins, or rebel leaders can create currencies.");
                return true;
            }

            // Usage: /currency create <name> <metalBacked:true/false> [material] [ratio]
            if (args.length < 3) {
                sender.sendMessage("§cUsage: /currency create <name> <metalBacked:true/false> [material] [ratio]");
                return true;
            }

            String name = args[1];
            boolean metalBacked;
            try {
                metalBacked = Boolean.parseBoolean(args[2]);
            } catch (Exception e) {
                sender.sendMessage("§cSecond argument must be 'true' or 'false' (for metal-backed).");
                return true;
            }

            String material = null;
            double ratio = 0.0;
            if (metalBacked) {
                if (args.length < 5) {
                    sender.sendMessage("§cUsage for metal-backed: /currency create <name> true <material> <ratio>");
                    return true;
                }
                material = args[3];
                try {
                    ratio = Double.parseDouble(args[4]);
                } catch (Exception e) {
                    sender.sendMessage("§cRatio must be a number (e.g., 1.0).");
                    return true;
                }
            }

            boolean created = currencyManager.createCurrency(name, metalBacked, material, ratio);
            if (created) {
                sender.sendMessage("§aCurrency '" + name + "' created successfully!");
            } else {
                sender.sendMessage("§cCurrency '" + name + "' already exists.");
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("info")) {
            if (args.length < 2) {
                sender.sendMessage("§cUsage: /currency info <name>");
                return true;
            }
            String name = args[1];
            NationalCurrency currency = currencyManager.getCurrency(name);
            if (currency == null) {
                sender.sendMessage("§cNo such currency: " + name);
                return true;
            }
            sender.sendMessage("§eCurrency: " + currency.getName());
            sender.sendMessage("§eBacked by metal: " + currency.isMetalBacked());
            if (currency.isMetalBacked()) {
                sender.sendMessage("§eBacking Material: " + currency.getBackingMaterial());
                sender.sendMessage("§eBacking Ratio: " + currency.getBackingRatio());
            }
            sender.sendMessage("§eTotal Supply: " + currency.getSupply());
            return true;
        }

        if (args[0].equalsIgnoreCase("list")) {
            sender.sendMessage("§eAvailable currencies:");
            for (String code : currencyManager.getCurrencyCodes()) {
                sender.sendMessage(" - " + code);
            }
            return true;
        }

        sender.sendMessage("§cUnknown subcommand. Use /currency <create|info|list>");
        return true;
    }
}
