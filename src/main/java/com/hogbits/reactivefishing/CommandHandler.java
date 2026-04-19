package com.hogbits.reactivefishing;

import java.util.Collections;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public final class CommandHandler implements CommandExecutor, TabCompleter {
    private final ConfigManager configManager;

    public CommandHandler(ConfigManager configManager) {
        this.configManager = configManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1 || !args[0].equalsIgnoreCase("reload")) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " reload");
            return true;
        }

        if (!sender.hasPermission("reactivefishing.reload")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        configManager.reload();
        sender.sendMessage(ChatColor.GREEN + "ReactiveFishing config reloaded.");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String input = args[0].toLowerCase();
            if ("reload".startsWith(input)) {
                return Collections.singletonList("reload");
            }
        }
        return Collections.emptyList();
    }
}
