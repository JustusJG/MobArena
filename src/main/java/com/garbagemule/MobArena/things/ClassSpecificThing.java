package com.garbagemule.MobArena.things;

import org.bukkit.entity.Player;

import java.util.Map;

public class ClassSpecificThing implements Thing {

    private final Map<String, Thing> classSpecificThings;
    private final String className;

    public ClassSpecificThing(Map<String, Thing> classSpecificThings, String className) {
        this.classSpecificThings = classSpecificThings;
        this.className = className;
    }

    @Override
    public boolean giveTo(Player player) {
        return classSpecificThings.get(className).giveTo(player);
    }

    @Override
    public boolean takeFrom(Player player) {
        return false;
    }

    @Override
    public boolean heldBy(Player player) {
        return false;
    }

    @Override
    public String toString() {
        return classSpecificThings.get(className).toString();
    }
}
