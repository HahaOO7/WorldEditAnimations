package at.emielregis.worldeditanimations;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.Optional;

import static at.emielregis.worldeditanimations.Utils.parseVector;
import static java.util.Arrays.copyOfRange;

public class AnimationStepLinear implements Cloneable, IAnimationStep {
    private Vector start, end;
    private final World world;
    private AnimationFrame displayedFrame;
    private Clipboard clipboard;
    private int step = 0;
    private final int steps;

    public AnimationStepLinear(String[] args, ClipboardCache cache) {
        world = cache.getWorld();
        start = parseVector(copyOfRange(args, 0, 3));
        end = parseVector(copyOfRange(args, 3, 6));
        steps = Integer.parseInt(args[6]);
        clipboard = cache.get(args[7]);
    }

    public boolean display() {
        if(step > steps) step = 0;
        Vector pos = end.clone().subtract(start);
        double delta = 1d / steps * step;
        pos = pos.multiply(delta);
        pos = pos.add(start);
        displayedFrame = new AnimationFrame(pos.toLocation(world), clipboard);
        displayedFrame.paste();
        step++;
        return step >= steps;
    }

    public boolean destroy() {
        Optional.ofNullable(displayedFrame).ifPresent(AnimationFrame::delete);
        return step >= steps;
    }

    public AnimationStepLinear clone() {
        try {
            AnimationStepLinear clone = (AnimationStepLinear) super.clone();
            clone.start = start;
            clone.end = end;
            clone.clipboard = clipboard;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
