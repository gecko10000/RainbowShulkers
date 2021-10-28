package io.github.gecko10000.RainbowShulkers;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandHandler implements CommandExecutor {

    private final RainbowShulkers plugin;

    public CommandHandler(RainbowShulkers plugin) {
        this.plugin = plugin;
        plugin.getCommand("rainbowshulkers").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0 || !args[0].equalsIgnoreCase("reload")) {
            return response(sender, "&cUsage: /rainbowshulkers reload");
        }
        plugin.reload();
        return response(sender, "&aConfig reloaded!");
    }

    private boolean response(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        return true;
    }
}
