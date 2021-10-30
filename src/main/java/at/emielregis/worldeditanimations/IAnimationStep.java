package at.emielregis.worldeditanimations;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface IAnimationStep {
    //return true if it is the last step in the AnimationStep
    boolean display();

    boolean destroy();


}
