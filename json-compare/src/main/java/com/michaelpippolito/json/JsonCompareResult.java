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
    private final Set<JsonMatch> matchedFields;
    private final Map<String, JsonMismatch> mismatchedFields;
    private final Map<String, String> missingFields;
    private final Map<String, String> extraFields;


    public record JsonMismatch(String expectedValue, String actualValue) {
    }

    public interface JsonMatch {
    }

    public record BasicJsonMatch(String key, String value) implements JsonMatch {
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof BasicJsonMatch toCompare) {
                return this.key == null ? toCompare.key() == null : this.key.equals(toCompare.key())
                        && this.value == null ? toCompare.value() == null : this.value.equals(toCompare.value());
            }
            return false;
        }

        @Override
        public int hashCode() {
            int result = 17; // Initial value
            result = 31 * result + this.key.hashCode();
            if (this.value != null) {
                result = 31 * result + this.value.hashCode();
            }
            return result;
        }
    }

    public record ArrayJsonMatch(String expectedKey, String actualKey, String value) implements JsonMatch {
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof ArrayJsonMatch toCompare) {
                return this.expectedKey == null ? toCompare.expectedKey() == null : this.expectedKey.equals(toCompare.expectedKey())
                        && this.actualKey == null ? toCompare.actualKey() == null : this.actualKey.equals(toCompare.actualKey())
                        && this.value == null ? toCompare.value() == null : this.value.equals(toCompare.value());
            }
            return false;
        }

        @Override
        public int hashCode() {
            int result = 19; // Initial value
            result = 31 * result + this.expectedKey.hashCode();
            result = 31 * result + this.actualKey.hashCode();
            if (this.value != null) {
                result = 31 * result + this.value.hashCode();
            }
            return result;
        }
    }
}
