package com.fast.ghostfix;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.*;

public class GhostFixManager {
    
    private final FastNoGhost plugin;
    
    private final Map<UUID, Map<Location, Long>> recentBreaks = new HashMap<>();
    
    private int radius;
    private boolean miningOnly;
    private long maxBreakTime;
    
    public GhostFixManager(FastNoGhost plugin) {
        this.plugin = plugin;
        reload();
    }
    
    public void reload() {
        radius = Math.min(plugin.getConfig().getInt("fix.radius", 5), 5);
        miningOnly = plugin.getConfig().getBoolean("fix.mining-only", true);
        maxBreakTime = plugin.getConfig().getLong("detection.max-break-time", 5000);
    }
    
    public void fixAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            try {
                fixForPlayer(player);
            } catch (Exception e) {
                plugin.getLogger().warning("Error fixing for " + player.getName() + ": " + e.getMessage());
            }
        }
    }
    
    public void fixForPlayer(Player player) {
        Map<Location, Long> breaks = recentBreaks.get(player.getUniqueId());
        
        if (miningOnly) {
            if (breaks == null || breaks.isEmpty()) return;
            
            long now = System.currentTimeMillis();
            breaks.entrySet().removeIf(e -> now - e.getValue() > maxBreakTime);
            
            if (breaks.isEmpty()) return;
        }
        
        Location loc = player.getLocation();
        int fixed = 0;
        
        Set<Location> blocksToUpdate = new HashSet<>();
        
        if (miningOnly && breaks != null) {
            blocksToUpdate.addAll(breaks.keySet());
        } else {
            int px = loc.getBlockX();
            int py = loc.getBlockY();
            int pz = loc.getBlockZ();
            
            for (int x = -radius; x <= radius; x += 2) {
                for (int y = -radius; y <= radius; y += 2) {
                    for (int z = -radius; z <= radius; z += 2) {
                        blocksToUpdate.add(new Location(loc.getWorld(), px + x, py + y, pz + z));
                    }
                }
            }
        }
        
        for (Location blockLoc : blocksToUpdate) {
            if (blockLoc.getWorld() == null) continue;
            
            Block block = blockLoc.getBlock();
            
            if (block.getType().isAir()) continue;
            
            player.sendBlockChange(blockLoc, block.getBlockData());
            fixed++;
        }
    }
    
    public void registerBlockBreak(Player player, Block block) {
        Location loc = block.getLocation().clone();
        recentBreaks.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>())
            .put(loc, System.currentTimeMillis());
    }
    
    public void clearPlayer(Player player) {
        recentBreaks.remove(player.getUniqueId());
    }
    
    public void fixBlock(Player player, Block block) {
        if (block.getType().isAir()) return;
        player.sendBlockChange(block.getLocation(), block.getBlockData());
    }
}
