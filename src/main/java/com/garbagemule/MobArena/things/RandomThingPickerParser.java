package com.garbagemule.MobArena.things;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class RandomThingPickerParser implements ThingPickerParser {

    private final ThingPickerParser parser;
    private final Random random;

    public RandomThingPickerParser(
        ThingPickerParser parser,
        Random random
    ) {
        this.parser = parser;
        this.random = random;
    }

    @Override
    public ThingPicker parse(String s) {
        if (!(s.startsWith("random(") && s.endsWith(")"))) {
            return null;
        }

        String inner = ParserUtil.extractBetween(s, '(', ')');
        Map<ThingPicker, Integer> relativeWeights = new HashMap<>();
        Map<ThingPicker, Integer> absoluteWeights = new HashMap<>();
        List<ThingPicker> unweightedPickers = ParserUtil.split(inner)
            .stream()
            .map(String::trim)
            .map((thingString) -> {
                Pattern weightedThingPattern = Pattern.compile("(?<weight>[0-9]*)(?<absolute>%?):(?<thing>.*)");
                Matcher weightedThingMatcher = weightedThingPattern.matcher(thingString);

                if (!weightedThingMatcher.matches()) {
                    ThingPicker defaultWeightedThing = parser.parse(thingString);
                    relativeWeights.put(defaultWeightedThing, 1);
                    return defaultWeightedThing;
                }
                ThingPicker weightedThing = parser.parse(weightedThingMatcher.group("thing"));

                int weight = Integer.parseInt(weightedThingMatcher.group("weight"));
                if (weightedThingMatcher.group("absolute").isEmpty()) {
                    relativeWeights.put(weightedThing, weight);
                } else {
                    absoluteWeights.put(weightedThing, weight);
                }

                return weightedThing;
            })
            .collect(Collectors.toList());

        double absoluteSum = absoluteWeights.values().stream().mapToInt(Integer::intValue).sum();
        if (absoluteSum > 100) {
            throw new IllegalArgumentException("Sum of absolute weights can't be more then 100%.");
        }
        double remainingProbability = 1.0 - (absoluteSum / 100.0);
        int relativeSum = relativeWeights.values().stream().mapToInt(Integer::intValue).sum();
        if (relativeSum == 0) {
            remainingProbability = 0;
        }

        if (unweightedPickers.isEmpty()) {
            throw new IllegalArgumentException("Nothing to pick from: " + s);
        }
        if (unweightedPickers.size() == 1) {
            return unweightedPickers.get(0);
        }

        List<ThingPicker> weightedPickers = new ArrayList<>();
        for (Map.Entry<ThingPicker, Integer> entry : absoluteWeights.entrySet()) {
            int weight = entry.getValue();
            int count = Math.toIntExact(Math.round(weight * 100.0));
            for (int i = 0; i < count; i++) {
                weightedPickers.add(entry.getKey());
            }
        }

        for (Map.Entry<ThingPicker, Integer> entry : relativeWeights.entrySet()) {
            ThingPicker picker = entry.getKey();
            int weight = entry.getValue();
            double probability = (double) weight / relativeSum * remainingProbability;
            int count = (int) Math.round(probability * 10000);
            for (int i = 0; i < count; i++) {
                weightedPickers.add(picker);
            }
        }

        Collections.shuffle(weightedPickers);

        return new RandomThingPicker(weightedPickers, random);
    }

}
