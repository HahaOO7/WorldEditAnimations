package at.emielregis.worldeditanimations;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.Listener;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Animation implements Listener {
    private final int delay;
    private int index = 0;
    public ArrayList<AnimationFrame> animationFrames = new ArrayList<>();

    public Animation(int delay, List<String[]> entries, org.bukkit.World world, String name) {
        this.delay = delay;
        File directory = new File(WorldEditAnimations.getInstance().getDataFolder(), name);
        Map<String, Clipboard> clipboardMap = new HashMap<>();

        for (String[] strings : entries) {
            String[] positionString = strings[0].split(" ");

            File file = new File(directory, strings[1]);

            ClipboardFormat format = ClipboardFormats.findByFile(file);
            if (format == null) continue;

            Clipboard clipboard = clipboardMap.computeIfAbsent(file.getName(), key -> {
                try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
                    return reader.read();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            });

            if (clipboard == null) continue;

            Location loc = new Location(world, Integer.parseInt(positionString[0]), Integer.parseInt(positionString[1]), Integer.parseInt(positionString[2]));
            AnimationFrame entry = new AnimationFrame(loc, clipboard);
            this.animationFrames.add(entry);
        }

    }

    public void startAnimation() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(WorldEditAnimations.getInstance(), this::displayFrame, 0, delay);
    }

    private void displayFrame() {
        if (index > 0) {
            animationFrames.get(index % animationFrames.size()).delete();
        }
        animationFrames.get(++index % animationFrames.size()).paste();
    }
}
