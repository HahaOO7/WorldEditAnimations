package at.emielregis.worldeditanimations;

import at.haha007.edencommands.CommandRegistry;
import at.haha007.edencommands.tree.CommandContext;
import at.haha007.edencommands.tree.node.CommandNode;
import at.haha007.edencommands.tree.node.LiteralCommandNode;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static at.haha007.edencommands.tree.node.ArgumentCommandNode.argument;
import static at.haha007.edencommands.tree.node.LiteralCommandNode.literal;
import static at.haha007.edencommands.tree.node.argument.StringArgumentParser.stringParser;
import static net.md_5.bungee.api.ChatColor.*;

public final class WorldEditAnimations extends JavaPlugin {
    @Getter
    private static WorldEditAnimations instance;
    private final Map<String, Animation> animationList = new HashMap<>();
    private final Map<String, Animation> testAnimations = new HashMap<>();


    @Override
    public void onEnable() {
        instance = this;

        LiteralCommandNode command = literal("animation");
        CommandNode stopCommand = literal("stop").then(argument("key", stringParser()).tabCompletes(c -> testAnimations.keySet().stream().toList()).executes(this::stopCommand));
        CommandNode startCommand = literal("start").then(argument("key", stringParser()).tabCompletes(this::tabComplete).executes(this::startCommand));
        command.then(startCommand);
        command.then(stopCommand);

        CommandNode autostart = literal("autostart");
        autostart.executes(this::sendAutostartInfo);
        autostart.then(argument("key", stringParser()).tabCompletes(this::tabComplete).executes(this::toggleAutostart));
        command.then(autostart);
        command.then(literal("reload").executes(this::reload));
        CommandRegistry.register(command);

        Bukkit.getScheduler().runTask(this, this::postEnable);
    }

    private List<String> tabComplete(CommandContext context) {
        return getAnimationFolderNames().stream().toList();
    }

    private void postEnable() {
        //make sure all worlds are loaded
        startAnimations();
        cleanupConfig();
    }

    private void cleanupConfig() {
        reloadConfig();
        FileConfiguration cfg = getConfig();
        cfg.set("enabled_animations",
                cfg.getStringList("enabled_animations")
                        .stream()
                        .filter(this::exists)
                        .sorted()
                        .toList()
        );
    }

    private void startAnimations() {
        saveDefaultConfig();
        reloadConfig();
        List<String> animations = getConfig().getStringList("enabled_animations");
        for (String key : Objects.requireNonNull(animations)) {
            try {
                Animation animation = new Animation(new File(getDataFolder(), key));
                animationList.put(key, animation);
            } catch (Exception e) {
                getLogger().severe("Failed to load animation: " + key);
                if (getConfig().getBoolean("debug")) e.printStackTrace();
            }
        }

    }

    private void stopAnimations() {
        animationList.values().forEach(Animation::stop);
        testAnimations.values().forEach(Animation::stop);
        animationList.clear();
        testAnimations.clear();
    }


    private void reload(CommandContext context) {
        stopAnimations();
        startAnimations();
        context.getSender().sendMessage(GOLD + "Animations reloaded");
    }

    private void toggleAutostart(CommandContext context) {
        String key = context.getParameter("key", String.class);
        CommandSender sender = context.getSender();
        if (!exists(key)) {
            sender.sendMessage(GOLD + "Couldn't find this animation.");
        }
        reloadConfig();

        List<String> enabled = getConfig().getStringList("enabled_animations");
        if (enabled.contains(key)) {
            enabled.remove(key);
            sender.sendMessage(GOLD + "Disabled autostart of " + YELLOW + key);
        } else {
            enabled.add(key);
            sender.sendMessage(GOLD + "Enabled autostart of " + YELLOW + key);
        }

        sender.sendMessage(GOLD + "Use " + YELLOW + "/animation reload" + GOLD + " to show changes.");
        getConfig().set("enabled_animations", enabled);
        saveConfig();
    }

    private boolean exists(String key) {
        File folder = new File(getDataFolder(), key);
        if (!folder.isDirectory()) return false;
        File cfg = new File(folder, "animation.yml");
        return cfg.exists();
    }

    private void sendAutostartInfo(CommandContext context) {
        Set<String> files = getAnimationFolderNames();
        Set<String> started = animationList.keySet();

        List<String> all = new ArrayList<>();
        all.addAll(files);
        all.addAll(started);
        all.sort(String::compareTo);
        CommandSender sender = context.getSender();
        sender.sendMessage(GOLD + "Use " + YELLOW + "/animation autostart <key> " + GOLD + " to toggle ");
        sender.sendMessage(GREEN + "running " + GRAY + "not running " + RED + "file deleted");
        all.forEach(s -> sender.sendMessage(getColor(files.contains(s), started.contains(s)) + s));
    }

    private Set<String> getAnimationFolderNames() {
        return Arrays.stream(Objects.requireNonNull(getDataFolder().listFiles()))
                .map(File::getName)
                .filter(this::exists)
                .collect(Collectors.toSet());
    }

    private ChatColor getColor(boolean fileExists, boolean started) {
        if (fileExists && started) {
            //file exists and started
            return GREEN;
        } else if (fileExists) {
            //file exists but not started
            return GRAY;
        } else if (started) {
            //file deleted
            return RED;
        } else {
            throw new IllegalStateException();
        }
    }

    private void stopCommand(CommandContext context) {
        String key = context.getParameter("key", String.class);
        CommandSender sender = context.getSender();
        Animation animation = testAnimations.get(key);
        if (animation == null) {
            sender.sendMessage(GOLD + "This animation is not active, active animations:");
            sender.sendMessage(String.join(YELLOW + ", " + GOLD, testAnimations.keySet()));
            return;
        }
        testAnimations.remove(key);
        animation.stop();
        sender.sendMessage(GOLD + "Stopped animation: " + YELLOW + key);
    }

    private void startCommand(CommandContext context) {
        String key = context.getParameter("key", String.class);
        CommandSender sender = context.getSender();
        if (testAnimations.containsKey(key)) {
            sender.sendMessage(GOLD + "This animation is already active.");
            return;
        }
        if (!exists(key)) {
            sender.sendMessage(GOLD + "This animation doesn't exist.");
            return;
        }
        try {
            Animation animation = new Animation(new File(getDataFolder(), key));
            testAnimations.put(key, animation);
            sender.sendMessage(GOLD + "Animation started.");
        } catch (IllegalArgumentException e) {
            sender.sendMessage(GOLD + "Couldn't load the animation, check the console for more details.");
            e.printStackTrace();
        }
    }
}
