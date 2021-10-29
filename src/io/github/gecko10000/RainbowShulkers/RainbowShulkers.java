package io.github.gecko10000.RainbowShulkers;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Shulker;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.stream.Stream;

public class RainbowShulkers extends JavaPlugin {

    public final NamespacedKey rainbowKey = NamespacedKey.fromString("rainbowshulkers:rainbow");
    public final Random random = new Random();

    public final Map<Shulker, LinkedHashSet<DyeColor>> rainbowShulkers = new HashMap<>();
    private BukkitTask rainbowTask;

    public void onEnable() {
        reload();
        new Listeners(this);
        new CommandHandler(this);
        Bukkit.getWorlds().stream()
                .flatMap(w -> Stream.of(w.getLoadedChunks()))
                .forEach(chunk -> {
                    Arrays.stream(chunk.getEntities())
                            .filter(Shulker.class::isInstance)
                            .map(Shulker.class::cast)
                            .filter(s -> s.getPersistentDataContainer().has(rainbowKey, PersistentDataType.BYTE))
                            .forEach(shulker -> rainbowShulkers.put(shulker, randomizeDyes()));
                });
    }

    public void onDisable() {
        rainbowTask.cancel();
    }

    public void reload() {
        saveDefaultConfig();
        reloadConfig();
        if (rainbowTask != null) {
            rainbowTask.cancel();
        }
        rainbowTask = rainbowTask();
    }

    public BukkitTask rainbowTask() {
        long interval = getConfig().getLong("tickInterval");
        return Bukkit.getScheduler().runTaskTimer(this, () -> {
            rainbowShulkers.forEach((shulker, colors) -> {
                DyeColor next = colors.iterator().next();
                shulker.setColor(next);
                colors.remove(next);
                colors.add(next);
            });
        }, interval, interval);
    }

    public LinkedHashSet<DyeColor> randomizeDyes() {
        LinkedHashSet<DyeColor> dyes = new LinkedHashSet<>();
        List<DyeColor> colors = new ArrayList<>(List.of(DyeColor.values()));
        Collections.shuffle(colors);
        dyes.addAll(colors);
        return dyes;
    }

}
