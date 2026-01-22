package com.garbagemule.MobArena.things;

import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ThingGroupPicker implements ThingPicker {

    private final List<ThingPicker> pickers;

    public ThingGroupPicker(List<ThingPicker> pickers) {
        this.pickers = pickers;
    }

    @Override
    public Thing pick() {
        return this.pick(null);
    }

    @Override
    public Thing pick(Player player) {
        List<Thing> things = pickers.stream()
                .map(thingPicker -> thingPicker.pick(player))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (things.isEmpty()) {
            return null;
        }

        if (things.size() == 1) {
            return things.get(0);
        }

        return new ThingGroup(things);
    }

    @Override
    public String toString() {
        String list = pickers.stream()
            .map(ThingPicker::toString)
            .collect(Collectors.joining(" and "));
        return "(" + list + ")";
    }

}
