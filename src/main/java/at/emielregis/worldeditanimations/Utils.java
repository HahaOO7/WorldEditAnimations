package at.emielregis.worldeditanimations;

import org.bukkit.util.Vector;

public enum Utils {
    ;

    public static Vector parseVector(String[] args) {
        return new Vector(Double.parseDouble(args[0]), Double.parseDouble(args[1]), Double.parseDouble(args[2]));
    }

}
