package at.emielregis.worldeditanimations;

public class AnimationStepNoop implements IAnimationStep {
    private final int count;
    private int index = 0;

    public AnimationStepNoop(String[] args, ClipboardCache cache) {
        count = Integer.parseInt(args[0]);
    }

    public boolean display() {
        if (index >= count) index = 0;
        index++;
        return index >= count;
    }

    public boolean destroy() {
        return index >= count;
    }
}
