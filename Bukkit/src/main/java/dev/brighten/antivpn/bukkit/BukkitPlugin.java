package dev.brighten.antivpn.bukkit;

import dev.brighten.antivpn.AntiVPN;
import dev.brighten.antivpn.command.Command;
import lombok.val;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class BukkitPlugin extends JavaPlugin {

    public static BukkitPlugin pluginInstance;
    private SimpleCommandMap commandMap;
    private List<org.bukkit.command.Command> registeredCommands = new ArrayList<>();

    public void onEnable() {
        pluginInstance = this;

        //Loading config
        System.out.println("Loading config...");
        saveDefaultConfig();

        System.out.println("Starting AntiVPN services...");
        AntiVPN.start(new BukkitConfig(), new BukkitListener(), new BukkitPlayerExecutor());

        System.out.println("Setting up and registering commands...");
        if (pluginInstance.getServer().getPluginManager() instanceof SimplePluginManager) {
            SimplePluginManager manager = (SimplePluginManager) pluginInstance.getServer().getPluginManager();
            try {
                Field field = SimplePluginManager.class.getDeclaredField("commandMap");
                field.setAccessible(true);
                commandMap = (SimpleCommandMap) field.get(manager);
            } catch (IllegalArgumentException | SecurityException | NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        for (Command command : AntiVPN.getInstance().getCommands()) {
            val newCommand = new org.bukkit.command.Command(command.name(),
                    command.description(), command.usage(), Arrays.asList(command.aliases())) {
                @Override
                public boolean execute(CommandSender sender, String s, String[] args) {
                    if(!sender.hasPermission("antivpn.command.*")
                            && !sender.hasPermission(command.permission())) {
                        sender.sendMessage(ChatColor.RED + "No permission.");
                        return true;
                    }

                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            command.execute(new BukkitCommandExecutor(sender), args)));

                    return true;
                }
            };

            registeredCommands.add(newCommand);
            commandMap.register("antivpn", newCommand);
        }
    }

    @Override
    public void onDisable() {
        System.out.println("Stopping plugin services...");
        AntiVPN.getInstance().stop();

        System.out.println("Unregistering commands...");
        try {
            Map<String, org.bukkit.command.Command> knownCommands = (Map<String, org.bukkit.command.Command>)
                    SimpleCommandMap.class.getField("knownCommands").get(commandMap);

            knownCommands.values().removeAll(registeredCommands);
            registeredCommands.clear();
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }

        System.out.println("Unregistering listeners...");
        HandlerList.unregisterAll(this);

        System.out.println("Cancelling any running tasks...");
        Bukkit.getScheduler().cancelTasks(this);
    }
}
