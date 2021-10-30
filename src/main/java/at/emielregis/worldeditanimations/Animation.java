package at.emielregis.worldeditanimations;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;

import java.io.File;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Animation implements Listener {
    private static final Pattern pattern = Pattern.compile("(?<type>.+)\\[(?<args>.+)]");

    private int index = 0;
    private final int taskId;
    private IAnimationStep displayedStep;
    private final IAnimationStep[] steps;

    public Animation(File folder) {
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(new File(folder, "animation.yml"));
        int period = cfg.getInt("period");
        World world = Bukkit.getWorld(Objects.requireNonNull(cfg.getString("world")));
        ClipboardCache cache = new ClipboardCache(folder, world);
        steps = cfg.getStringList("animationSteps").stream().map(s -> parse(s, cache)).filter(Objects::nonNull).toList().toArray(new IAnimationStep[0]);
        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(WorldEditAnimations.getInstance(), this::displayFrame, 0, period);
    }

    private IAnimationStep parse(String s, ClipboardCache cache) {
        Matcher matcher = pattern.matcher(s);
        if (!matcher.matches()) throw new IllegalStateException("couldn't parse animation: " + s);
        String type = matcher.group("type");
        String[] args = matcher.group("args").split(",");
        return switch (type) {
            case "step" -> new AnimationStepFrame(args, cache);
            case "linear" -> new AnimationStepLinear(args, cache);
            default -> throw new IllegalStateException("couldn't parse animation: " + s);
        };
    }

    private void displayFrame() {
        //destroy old step if exists
        //if the old step is done increment index and set displayedStep to null
        if (displayedStep != null && displayedStep.destroy()) {
            displayedStep = null;
            index++;
        }
        //if the displayed step is null get the next step to display
        if (displayedStep == null) {
            displayedStep = steps[index % steps.length];
        }
        //display the current frame
        displayedStep.display();
    }

    public void stop() {
        if (displayedStep != null) displayedStep.destroy();
        Bukkit.getScheduler().cancelTask(taskId);
    }
}
