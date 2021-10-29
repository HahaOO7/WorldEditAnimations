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
import java.util.List;

public class Animation implements Listener {
    private int delay;
    private File directory;
    private int index = 0;
    public ArrayList<AnimationFrame> animationFrames = new ArrayList<>();

    public Animation(int delay, List<String[]> entries, org.bukkit.World world, String name) {
        this.delay = delay;
        directory = new File(WorldEditAnimations.getInstance().getDataFolder(), name);

        for (int i = 0; i < entries.size(); i++) {
            String[] locs = entries.get(i)[0].split(" ");

            File file = new File(directory, entries.get(i)[1]);
            Clipboard clipboard;
            ClipboardFormat format = ClipboardFormats.findByFile(file);
            try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
                clipboard = reader.read();
                AnimationFrame entry = new AnimationFrame(new Location(world, Integer.parseInt(locs[0]), Integer.parseInt(locs[1]), Integer.parseInt(locs[2])), clipboard);
                this.animationFrames.add(entry);
            } catch (IOException e) {
                e.printStackTrace();
            }
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
