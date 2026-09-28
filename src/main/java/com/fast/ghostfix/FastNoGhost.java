package com.fast.ghostfix;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class FastNoGhost extends JavaPlugin {
    
    private GhostFixManager fixManager;
    private BukkitTask fixTask;
    
    @Override
    public void onEnable() {
        saveDefaultConfig();
        
        fixManager = new GhostFixManager(this);
        
        getServer().getPluginManager().registerEvents(
            new GhostFixListener(this, fixManager), this);
        
        startFixTask();
        
        getCommand("noghost").setExecutor((sender, cmd, label, args) -> {
            sender.sendMessage(color("&6&m═══════════════════════════"));
            sender.sendMessage(color("&6FastNoGhost &7v1.0.0"));
            sender.sendMessage(color("&7Anti Ghost Block System"));
            sender.sendMessage(color("&7Author: &fFast"));
            sender.sendMessage(color("&6&m═══════════════════════════"));
            return true;
        });
        
        getCommand("noghostreload").setExecutor((sender, cmd, label, args) -> {
            reloadConfig();
            fixManager.reload();
            stopFixTask();
            startFixTask();
            sender.sendMessage(color(getConfig().getString("messages.prefix") + 
                getConfig().getString("messages.reloaded")));
            return true;
        });
        
        getLogger().info("FastNoGhost enabled!");
    }
    
    @Override
    public void onDisable() {
        stopFixTask();
        getLogger().info("FastNoGhost disabled");
    }
    
    private void startFixTask() {
        if (!getConfig().getBoolean("fix.auto-fix", true)) return;
        int interval = getConfig().getInt("fix.interval", 40);
        fixTask = getServer().getScheduler().runTaskTimer(this, () -> {
            fixManager.fixAll();
        }, interval, interval);
    }
    
    private void stopFixTask() {
        if (fixTask != null) {
            fixTask.cancel();
            fixTask = null;
        }
    }
    
    public GhostFixManager getFixManager() {
        return fixManager;
    }
    
    private String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
