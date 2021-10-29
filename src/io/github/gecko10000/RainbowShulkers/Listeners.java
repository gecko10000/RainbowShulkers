package io.github.gecko10000.RainbowShulkers;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Shulker;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkPopulateEvent;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.event.world.EntitiesUnloadEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class Listeners implements Listener {

    private final RainbowShulkers plugin;

    public Listeners(RainbowShulkers plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onGenerate(ChunkLoadEvent evt) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            if (evt.isNewChunk()) {
                Arrays.stream(evt.getChunk().getEntities())
                        .filter(Shulker.class::isInstance)
                        .map(Shulker.class::cast)
                        .filter(s -> plugin.random.nextDouble() < plugin.getConfig().getDouble("chance.natural"))
                        .forEach(shulker -> {
                            shulker.getPersistentDataContainer().set(plugin.rainbowKey, PersistentDataType.BYTE, (byte) 1);
                            plugin.rainbowShulkers.put(shulker, plugin.randomizeDyes());
                        });
                return;
            }
        });
    }

    @EventHandler
    public void onLoad(EntitiesLoadEvent evt) {
        Arrays.stream(evt.getChunk().getEntities())
                .filter(Shulker.class::isInstance)
                .map(Shulker.class::cast)
                .filter(s -> s.getPersistentDataContainer().has(plugin.rainbowKey, PersistentDataType.BYTE))
                .forEach(s -> plugin.rainbowShulkers.put(s, plugin.randomizeDyes()));
    }

    @EventHandler
    public void onUnload(EntitiesUnloadEvent evt) {
        Arrays.stream(evt.getChunk().getEntities())
                .filter(Shulker.class::isInstance)
                .map(Shulker.class::cast)
                .forEach(plugin.rainbowShulkers::remove);
    }

    @EventHandler (ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onSpawn(CreatureSpawnEvent evt) {
        if (evt.getEntityType() != EntityType.SHULKER) {
            return;
        }
        if (plugin.random.nextDouble() >= plugin.getConfig().getDouble("chance.other")) {
            return;
        }
        Entity entity = evt.getEntity();
        entity.getPersistentDataContainer().set(plugin.rainbowKey, PersistentDataType.BYTE, (byte) 1);
        plugin.rainbowShulkers.put((Shulker) entity, plugin.randomizeDyes());
    }

}
