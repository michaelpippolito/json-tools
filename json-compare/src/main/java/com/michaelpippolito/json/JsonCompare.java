package com.michaelpippolito.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.michaelpippolito.json.JsonCompareResult.ArrayJsonMatch;
import com.michaelpippolito.json.JsonCompareResult.BasicJsonMatch;
import com.michaelpippolito.json.JsonCompareResult.JsonMatch;
import com.michaelpippolito.json.JsonCompareResult.JsonMismatch;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class JsonCompare {
    private static final Set<String> DEFAULT_IGNORE_FIELDS = Collections.emptySet();
    private static final boolean DEFAULT_IGNORE_ARRAY_ORDER = false;
    private static final String ARRAY_FIELD_REGEX = "\\[\\d+]";

    public static JsonCompareResult compareJsonStrings(String expectedJson, String actualJson) throws JsonProcessingException {
        return compareJsonStrings(expectedJson, actualJson, DEFAULT_IGNORE_FIELDS, DEFAULT_IGNORE_ARRAY_ORDER);
    }

    public static JsonCompareResult compareJsonStrings(String expectedJson, String actualJson, boolean ignoreArrayOrder) throws JsonProcessingException {
        return compareJsonStrings(expectedJson, actualJson, DEFAULT_IGNORE_FIELDS, ignoreArrayOrder);
    }

    public static JsonCompareResult compareJsonStrings(String expectedJson, String actualJson, Set<String> ignoreFields) throws JsonProcessingException {
        return compareJsonStrings(expectedJson, actualJson, ignoreFields, DEFAULT_IGNORE_ARRAY_ORDER);
    }

    public static JsonCompareResult compareJsonStrings(String expectedJson, String actualJson, Set<String> ignoreFields, boolean ignoreArrayOrder) throws JsonProcessingException {
        Map<String, String> flattenedExpectedJson = JsonFlattener.flattenJsonString(expectedJson);
        Map<String, String> flattenedActualJson = JsonFlattener.flattenJsonString(actualJson);

        Set<JsonMatch> matchedFields = new LinkedHashSet<>();
        Map<String, JsonMismatch> mismatchedFields = new LinkedHashMap<>();
        Map<String, String> missingFields = new LinkedHashMap<>();
        Map<String, String> extraFields = new LinkedHashMap<>();

        flattenedExpectedJson.forEach((expectedKey, expectedValue) -> {
            if (ignoreArrayOrder && isArrayField(expectedKey)) {
                compareArrayFieldIgnoreOrder(expectedKey, flattenedExpectedJson, flattenedActualJson, matchedFields, missingFields);
            } else if (!ignoreFields.contains(expectedKey)) {
                if (flattenedActualJson.containsKey(expectedKey)) {
                    String actualValue = flattenedActualJson.get(expectedKey);

                    if (expectedValue == null) {
                        if (actualValue == null) {
                            matchedFields.add(new BasicJsonMatch(expectedKey, null));
                        } else {
                            mismatchedFields.put(expectedKey, new JsonMismatch(null, actualValue));
                        }
                    } else if (expectedValue.equals(actualValue)) {
                        matchedFields.add(new BasicJsonMatch(expectedKey, expectedValue));
                    } else {
                        mismatchedFields.put(expectedKey, new JsonMismatch(expectedValue, actualValue));
                    }
                } else {
                    missingFields.put(expectedKey, expectedValue);
                }
            }
        });

        flattenedActualJson.forEach((actualKey, actualValue) -> {
            if (!ignoreFields.contains(actualKey)) {
                if (!isArrayField(actualKey) && ignoreArrayOrder && !matchedFields.contains(new BasicJsonMatch(actualKey, actualValue)) && !mismatchedFields.containsKey(actualKey)) {
                    extraFields.put(actualKey, actualValue);
                }
            }
        });

        return JsonCompareResult.builder()
                .expectedCount(flattenedExpectedJson.size())
                .actualCount(flattenedActualJson.size())
                .matchedFields(matchedFields)
                .mismatchedFields(mismatchedFields)
                .missingFields(missingFields)
                .extraFields(extraFields)
                .build();
    }

    private static boolean isArrayField(String expectedKey) {
        return Pattern.compile(ARRAY_FIELD_REGEX).matcher(expectedKey).find();
    }

    private static void compareArrayFieldIgnoreOrder(String arrayKey, Map<String, String> flattenedExpectedJson, Map<String, String> flattenedActualJson, Set<JsonMatch> matchedFields, Map<String, String> missingFields) {
        if (matchedFields.stream().noneMatch(match -> match instanceof ArrayJsonMatch toCompare && toCompare.expectedKey().equals(arrayKey)) && !missingFields.containsKey(arrayKey)) {
            StringBuilder expectedPrefixBuilder = new StringBuilder();
            StringBuilder actualRegexBuilder = new StringBuilder();
            AtomicInteger lastIndex = new AtomicInteger();
            Pattern.compile(ARRAY_FIELD_REGEX).matcher(arrayKey).results().forEach(match -> {
                expectedPrefixBuilder.append(arrayKey, lastIndex.get(), match.end());
                actualRegexBuilder.append(arrayKey, lastIndex.get(), match.start());
                actualRegexBuilder.append(ARRAY_FIELD_REGEX);
                lastIndex.set(match.end());
            });

            Map<String, KVWrapper> expectedArrayFields = flattenedExpectedJson.entrySet().stream()
                    .filter(entry -> entry.getKey().startsWith(expectedPrefixBuilder.toString()))
                    .collect(Collectors.toMap(entry -> entry.getKey().replaceAll(ARRAY_FIELD_REGEX, ""), entry -> new KVWrapper(entry.getKey(), entry.getValue())));
            Map<String, String> actualArrayFields = flattenedActualJson.entrySet().stream()
                    .filter(entry -> Pattern.compile(actualRegexBuilder.toString()).matcher(entry.getKey()).find())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));


            Set<String> checkedKeys = new HashSet<>();
            boolean matched = false;
            for (String actualKey : actualArrayFields.keySet()) {
                if (!checkedKeys.contains(actualKey) && matchedFields.stream().noneMatch(match -> match instanceof ArrayJsonMatch toCompare && toCompare.actualKey().equals(actualKey))) {
                    Map<String, KVWrapper> actualArrayFieldGroup = actualArrayFields.entrySet().stream()
                            .filter(entry -> entry.getKey().startsWith(actualKey.substring(0, actualKey.lastIndexOf("]"))))
                            .collect(Collectors.toMap(entry -> entry.getKey().replaceAll(ARRAY_FIELD_REGEX, ""), entry -> new KVWrapper(entry.getKey(), entry.getValue())));
                    checkedKeys.addAll(actualArrayFieldGroup.values().stream().map(KVWrapper::key).collect(Collectors.toSet()));

                    if (actualArrayFieldGroup.size() == expectedArrayFields.size()) {
                        boolean fullyMatched = true;
                        for (String expectedKey : expectedArrayFields.keySet()) {
                            if (!actualArrayFieldGroup.containsKey(expectedKey) || !expectedArrayFields.get(expectedKey).value().equals(actualArrayFieldGroup.get(expectedKey).value())) {
                                fullyMatched = false;
                                break;
                            }
                        }
                        if (fullyMatched) {
                            for (String key : expectedArrayFields.keySet()) {
                                matchedFields.add(new ArrayJsonMatch(
                                        expectedArrayFields.get(key).key(),
                                        actualArrayFieldGroup.get(key).key(),
                                        expectedArrayFields.get(key).value()
                                ));
                            }
                            matched = true;
                            break;
                        }
                    }
                }
            }

            if (!matched) {
                for (String key : expectedArrayFields.keySet()) {
                    missingFields.put(expectedArrayFields.get(key).key(), expectedArrayFields.get(key).value());
                }
            }
        }
    }

    private record KVWrapper(String key, String value) {
    }
}
