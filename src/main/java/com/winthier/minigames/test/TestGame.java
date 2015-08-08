package com.winthier.minigames.test;

import com.winthier.minigames.MinigamesPlugin;
import com.winthier.minigames.game.Game;
import com.winthier.minigames.util.BukkitFuture;
import com.winthier.minigames.util.Msg;
import com.winthier.minigames.util.Players;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class TestGame extends Game implements Listener {
    World world;
    BukkitRunnable task;
    int ticks;

    public TestGame() {}

    @Override
    public void onEnable() {
        // Load the test world, with onWorldsLoaded() as callback
        MinigamesPlugin.getWorldManager().loadWorld(
            this,
            "/home/creative/minecraft/worlds/Test/",
            new BukkitFuture<World>() {
                @Override public void run() {
                    onWorldsLoaded(this);
                }
            });
    }

    void onWorldsLoaded(BukkitFuture<World> future)
    {
        world = future.get();
        // Register all EventHandlers
        MinigamesPlugin.getEventManager().registerEvents(this, this);
        // Call onTick() once per tick.
        task = new BukkitRunnable() {
            @Override public void run() {
                onTick();
            }
        };
        task.runTaskTimer(MinigamesPlugin.getInstance(), 1, 1);
        // Switch to PLAY state, inviting registered players
        ready();
    }

    @Override
    public void onDisable()
    {
        task.cancel();
    }

    void onTick()
    {
        int ticks = this.ticks++;
        switch (ticks) {
        case 20*10:
            announce("&aHalf time.");
            break;
        case 20*60:
            announce("&cGame over.");
            break;
        case 20*70:
            cancel();
        }
    }

    @Override
    public Location getSpawnLocation(Player player)
    {
        return world.getSpawnLocation();
    }

    @Override
    public void onPlayerReady(Player player) {
        // Reset as if they joined for the first time
        Players.reset(player);
        Msg.send(player, "&aWelcome to %s!", getName());
    }

    @Override
    public boolean onCommand(Player player, String command, String[] args)
    {
        if ("test".equals(command)) {
            Msg.send(player, "&ahi");
        } else {
            return false;
        }
        return true;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event)
    {
        announce("&a%s", event.getBlock().getType());
    }
}
