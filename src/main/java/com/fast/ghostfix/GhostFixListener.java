package com.fast.ghostfix;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class GhostFixListener implements Listener {
    
    private final FastNoGhost plugin;
    private final GhostFixManager fixManager;
    
    public GhostFixListener(FastNoGhost plugin, GhostFixManager fixManager) {
        this.plugin = plugin;
        this.fixManager = fixManager;
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        fixManager.registerBlockBreak(event.getPlayer(), event.getBlock());
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        fixManager.fixBlock(event.getPlayer(), event.getBlock());
    }
    
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        fixManager.clearPlayer(event.getPlayer());
    }
}
