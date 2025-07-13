package com.brandon.globaleconomy.commands;

import com.brandon.globaleconomy.npc.impl.WorkerTrait;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class WorkerDebugCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage("§7[WorkerDebug] Checking all Citizens NPCs...");

        int total = 0;
        int withTrait = 0;
        int validWorkers = 0;

        for (NPC npc : CitizensAPI.getNPCRegistry()) {
            total++;

            if (npc.hasTrait(WorkerTrait.class)) {
                withTrait++;
                WorkerTrait trait = npc.getTrait(WorkerTrait.class);

                if (trait.getWorker() != null) {
                    validWorkers++;
                    sender.sendMessage("§a[NPC " + npc.getId() + "] §7" + npc.getName() + " has a valid Worker: " +
                            trait.getWorker().getName() + " (" + trait.getWorker().getRole() + ")");
                } else {
                    sender.sendMessage("§e[NPC " + npc.getId() + "] §7" + npc.getName() + " has WorkerTrait but worker is §cNULL");
                }
            } else {
                sender.sendMessage("§c[NPC " + npc.getId() + "] §7" + npc.getName() + " has §cNO WorkerTrait");
            }
        }

        sender.sendMessage("§7---");
        sender.sendMessage("§7Total NPCs: §f" + total);
        sender.sendMessage("§7With WorkerTrait: §f" + withTrait);
        sender.sendMessage("§7With valid Worker: §f" + validWorkers);

        return true;
    }
}
