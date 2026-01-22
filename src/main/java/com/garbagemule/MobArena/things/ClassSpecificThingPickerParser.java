package com.garbagemule.MobArena.things;

import com.garbagemule.MobArena.MobArena;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ClassSpecificThingPickerParser implements ThingPickerParser {

    private final ThingPickerParser parser;

    private final MobArena plugin;

    public ClassSpecificThingPickerParser(ThingPickerParser parser, MobArena plugin) {
        this.parser = parser;
        this.plugin = plugin;
    }

    @Override
    // TODO better error messages
    public ThingPicker parse(String s) {
        // class([<class>, <thing>], ...)
        if (!(s.startsWith("class(") && s.endsWith(")"))) {
            return null;
        }

        String inner = ParserUtil.extractBetween(s, '(', ')');
        List<String> classThingPairs = ParserUtil.split(inner)
            .stream()
            .map(String::trim)
            .collect(Collectors.toList());

        Map<String, ThingPicker> classSpecificPickers = new HashMap<>();

        if (classThingPairs.isEmpty()) {
            throw new IllegalArgumentException("Nothing to group: " + s);
        }
        for (String classThingPair : classThingPairs) {
            if (!(classThingPair.startsWith("[") && classThingPair.endsWith("]"))) {
                throw new IllegalArgumentException("classThingPair must be surrounded by brackets []: " + classThingPair);
            }
            inner = ParserUtil.extractBetween(classThingPair, '[', ']');
            List<String> parts = ParserUtil.split(inner)
                    .stream()
                    .map(String::trim)
                    .collect(Collectors.toList());

            if (parts.isEmpty()) {
                throw new IllegalArgumentException("syntax error class(<class>, <thing>): " + s);
            }
            if (parts.size() != 2) {
                throw new IllegalArgumentException("syntax error class(<class>, <thing>): " + s);
            }
            String className = parts.get(0);
            Set<String> classes = plugin.getArenaMaster().getClasses().keySet();
            boolean classExists = false;
            for (String classKey : classes) {
                if (className.equalsIgnoreCase(classKey)) {
                    classExists = true;
                    break;
                }
            }
            if (!classExists) {
                throw new IllegalArgumentException("Invalid class: " + className + "' try (" + classes + ")");
            }
            ThingPicker picker = parser.parse(parts.get(1));
            if (picker == null) {
                throw new IllegalArgumentException("Invalid thing: " + parts.get(1));
            }

            classSpecificPickers.put(className, picker);
        }

        return new ClassSpecificThingPicker(classSpecificPickers, plugin);
    }

}
