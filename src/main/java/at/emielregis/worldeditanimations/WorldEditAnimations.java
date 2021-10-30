package at.emielregis.worldeditanimations;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class WorldEditAnimations extends JavaPlugin implements Listener {
    private static WorldEditAnimations instance;
    public final List<Animation> animationList = new ArrayList<>();

    public static WorldEditAnimations getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        reloadConfig();
        getServer().getPluginManager().registerEvents(this, this);

        ConfigurationSection animations = getConfig().getConfigurationSection("animations");

        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () ->
        {
            if (animations != null) {
                for (String key : animations.getKeys(false)) {
                    ConfigurationSection animCfg = animations.getConfigurationSection(key);
                    int delay = Objects.requireNonNull(animCfg).getInt("delay");
                    List<String[]> list = Objects.requireNonNull(animCfg.getList("schems")).stream().map(v -> (List<String>) v).map(v -> v.toArray(new String[0])).collect(Collectors.toList());

                    Animation animation = new Animation(delay, list, Bukkit.getServer().getWorld("world"), key);
                    animationList.add(animation);
                }
                animationList.forEach(Animation::startAnimation);
            }
        }, 1);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
