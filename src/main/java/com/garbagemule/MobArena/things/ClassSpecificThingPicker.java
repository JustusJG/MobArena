package com.garbagemule.MobArena.things;

import com.garbagemule.MobArena.MobArena;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class ClassSpecificThingPicker implements ThingPicker {

    private final Map<String, ThingPicker> classSpecificPickers;

    private final MobArena plugin;

    public ClassSpecificThingPicker(Map<String, ThingPicker> classSpecificPickers, MobArena plugin) {
        this.classSpecificPickers = classSpecificPickers;

        this.plugin = plugin;
    }

    @Override
    public Thing pick() {
        return null;
    }

    @Override
    public ClassSpecificThing pick(Player player) {
        if (player == null) {
            return null;
        }
        String playerClassName = plugin.getArenaMaster()
                .getArenaWithPlayer(player)
                .getArenaPlayer(player)
                .getArenaClass().getSlug();
        Map<String, Thing> classSpecificThings = new HashMap<>();
        for (Map.Entry<String, ThingPicker> entry : classSpecificPickers.entrySet()) {
            classSpecificThings.put(entry.getKey(), entry.getValue().pick(player));
        }
        return new ClassSpecificThing(classSpecificThings, playerClassName);
    }

    @Override
    public String toString() {
        StringBuilder inner = new StringBuilder();
        int entryNum = 1;
        for (Map.Entry<String, ThingPicker> entry : classSpecificPickers.entrySet()) {
            inner.append("[")
                    .append(entry.getKey())
                    .append(", ")
                    .append(entry.getValue())
                    .append("]");
            if (entryNum++ < classSpecificPickers.size()) {
                inner.append(", ");
            }
        }
        return "class(" + inner + ")"; // TODO player specific?
    }

}
