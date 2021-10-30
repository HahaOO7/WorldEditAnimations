package at.emielregis.worldeditanimations;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.Arrays;

public class AnimationStepFrame implements IAnimationStep {
    private final AnimationFrame frame;

    public AnimationStepFrame(String[] args, ClipboardCache cache) {
        Vector pos = Utils.parseVector(Arrays.copyOfRange(args,0,3));
        Location loc = pos.toLocation(cache.getWorld());
        this.frame = new AnimationFrame(loc, cache.get(args[3]));
    }

    public boolean display() {
        frame.paste();
        return true;
    }

    public boolean destroy() {
        frame.delete();
        return true;
    }
}
