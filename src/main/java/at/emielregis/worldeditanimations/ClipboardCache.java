package at.emielregis.worldeditanimations;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import lombok.Getter;
import org.bukkit.World;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

public class ClipboardCache extends HashMap<String, Clipboard> {
    private final File folder;
    @Getter
    private final World world;

    public ClipboardCache(File folder, World world) {
        super();
        this.folder = folder;
        this.world = world;
    }

    public Clipboard get(String schematicName) {
        return computeIfAbsent(schematicName, this::load);
    }

    private Clipboard load(String schematicName) {
        File file = new File(folder, schematicName);
        if (!file.exists()) return null;
        ClipboardFormat format = ClipboardFormats.findByFile(file);
        if (format == null) return null;
        try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
            return reader.read();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
