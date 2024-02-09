package com.michaelpippolito.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.michaelpippolito.json.JsonCompareResult.JsonMismatch;

import java.util.*;

public class JsonCompare {
    private static final Set<String> DEFAULT_IGNORE_FIELDS = Collections.emptySet();

    public static JsonCompareResult compareJsonStrings(String expectedJson, String actualJson) throws JsonProcessingException {
        return compareJsonStrings(expectedJson, actualJson, DEFAULT_IGNORE_FIELDS);
    }

    public static JsonCompareResult compareJsonStrings(String expectedJson, String actualJson, Set<String> ignoreFields) throws JsonProcessingException {
        Map<String, String> flattenedExpectedJson = JsonFlattener.flattenJsonString(expectedJson);
        Map<String, String> flattenedActualJson = JsonFlattener.flattenJsonString(actualJson);

        Set<String> matchedFields = new LinkedHashSet<>();
        Map<String, JsonMismatch> mismatchedFields = new LinkedHashMap<>();
        Map<String, String> missingFields = new LinkedHashMap<>();
        Map<String, String> extraFields = new LinkedHashMap<>();

        flattenedExpectedJson.forEach((expectedKey, expectedValue) -> {
            if (!ignoreFields.contains(expectedKey)) {
                if (flattenedActualJson.containsKey(expectedKey)) {
                    String actualValue = flattenedActualJson.get(expectedKey);

                    if (expectedValue == null) {
                        if (actualValue == null) {
                            matchedFields.add(expectedKey);
                        } else {
                            mismatchedFields.put(expectedKey, new JsonMismatch(null, actualValue));
                        }
                    } else if (expectedValue.equals(actualValue)) {
                        matchedFields.add(expectedKey);
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
                if (!matchedFields.contains(actualKey) && !mismatchedFields.containsKey(actualKey)) {
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
}
