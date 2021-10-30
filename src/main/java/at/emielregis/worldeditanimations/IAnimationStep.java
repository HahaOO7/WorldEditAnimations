package at.emielregis.worldeditanimations;

public interface IAnimationStep {
    //return true if it is the last step in the AnimationStep
    boolean display();

    boolean destroy();
}
