package com.garbagemule.MobArena.things;

import org.bukkit.entity.Player;

class NothingPicker implements ThingPicker {

    private static final NothingPicker instance = new NothingPicker();

    @Override
    public Thing pick() {
        return null;
    }

    @Override
    public Thing pick(Player player) {
        return null;
    }

    @Override
    public String toString() {
        return "nothing";
    }

    public static NothingPicker getInstance() {
        return instance;
    }

}
