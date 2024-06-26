package com.michaelpippolito.json;

import com.michaelpippolito.json.JsonCompareResult.BasicJsonMatch;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JsonCompareTest {
    @Test
    public void testJsonEquals() throws IOException {
        String originalJson = new String(Objects.requireNonNull(JsonCompareTest.class.getClassLoader().getResourceAsStream("test.json")).readAllBytes());
        JsonCompareResult result = JsonCompare.compareJsonStrings(originalJson, originalJson);
        assertEquals(result.getExpectedCount(), result.getActualCount());
        assertEquals(0, result.getMismatchedFields().size());
        assertEquals(0, result.getMissingFields().size());
        assertEquals(0, result.getExtraFields().size());
    }

    @Test
    public void testJsonNotEquals() throws IOException {
        String expected = new String(Objects.requireNonNull(JsonCompareTest.class.getClassLoader().getResourceAsStream("test.json")).readAllBytes());
        String actual = new String(Objects.requireNonNull(JsonCompareTest.class.getClassLoader().getResourceAsStream("testNotEquals.json")).readAllBytes());

        JsonCompareResult result = JsonCompare.compareJsonStrings(expected, actual, Collections.singleton("ignore"));
        assertEquals(result.getExpectedCount(), result.getActualCount());

        assertTrue(result.getMatchedFields().contains(new BasicJsonMatch("null", null)));
        assertTrue(result.getMatchedFields().contains(new BasicJsonMatch("emptyArray", "EMPTY_ARRAY")));
        assertTrue(result.getMatchedFields().contains(new BasicJsonMatch("primitiveArray[0]", "string")));
        assertTrue(result.getMatchedFields().contains(new BasicJsonMatch("primitiveArray[1]", "true")));
        assertTrue(result.getMatchedFields().contains(new BasicJsonMatch("primitiveArray[3]", null)));
        assertTrue(result.getMatchedFields().contains(new BasicJsonMatch("object_boolean", "true")));
        assertTrue(result.getMatchedFields().contains(new BasicJsonMatch("object_null", null)));
        assertTrue(result.getMatchedFields().contains(new BasicJsonMatch("object_emptyArray", "EMPTY_ARRAY")));
        assertTrue(result.getMatchedFields().contains(new BasicJsonMatch("object_primitiveArray[0]", "string")));
        assertTrue(result.getMatchedFields().contains(new BasicJsonMatch("object_primitiveArray[1]", "true")));
        assertTrue(result.getMatchedFields().contains(new BasicJsonMatch("object_primitiveArray[2]", "1")));
        assertTrue(result.getMatchedFields().contains(new BasicJsonMatch("object_primitiveArray[3]", null)));

        assertTrue(result.getMismatchedFields().containsKey("string"));
        assertEquals("string", result.getMismatchedFields().get("string").expectedValue());
        assertEquals("x", result.getMismatchedFields().get("string").actualValue());
        assertTrue(result.getMismatchedFields().containsKey("boolean"));
        assertEquals("true", result.getMismatchedFields().get("boolean").expectedValue());
        assertEquals("false", result.getMismatchedFields().get("boolean").actualValue());
        assertTrue(result.getMismatchedFields().containsKey("primitiveArray[2]"));
        assertEquals("1", result.getMismatchedFields().get("primitiveArray[2]").expectedValue());
        assertEquals("2", result.getMismatchedFields().get("primitiveArray[2]").actualValue());
        assertTrue(result.getMismatchedFields().containsKey("object_number"));
        assertEquals("1", result.getMismatchedFields().get("object_number").expectedValue());
        assertEquals("2", result.getMismatchedFields().get("object_number").actualValue());

        assertTrue(result.getMissingFields().containsKey("number"));
        assertTrue(result.getMissingFields().containsKey("object_string"));

        assertTrue(result.getExtraFields().containsKey("extra"));
        assertTrue(result.getExtraFields().containsKey("object_extra"));
    }

    @Test
    public void testIgnoreArrayOrder() throws IOException {
        String expected =  new String(Objects.requireNonNull(JsonCompareTest.class.getClassLoader().getResourceAsStream("testIgnoreArrayOrder1.json")).readAllBytes());
        String actual =  new String(Objects.requireNonNull(JsonCompareTest.class.getClassLoader().getResourceAsStream("testIgnoreArrayOrder2.json")).readAllBytes());
        JsonCompareResult result = JsonCompare.compareJsonStrings(expected, actual, true);
        assertEquals(22, result.getMatchedFields().size());
        assertEquals(0, result.getMismatchedFields().size());
        assertEquals(0, result.getMissingFields().size());
        assertEquals(0, result.getExtraFields().size());
    }
}
