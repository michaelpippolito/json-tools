package com.michaelpippolito.json;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;
import java.util.Set;

@Builder
@Getter
public class JsonCompareResult {
    private final int expectedCount;
    private final int actualCount;
    private final Set<String> matchedFields;
    private final Map<String, JsonMismatch> mismatchedFields;
    private final Map<String, String> missingFields;
    private final Map<String, String> extraFields;


    public record JsonMismatch(String expectedValue, String actualValue) {
    }
}
