package com.michaelpippolito.json;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

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

        JsonCompareResult result = JsonCompare.compareJsonStrings(expected, actual);
        assertEquals(result.getExpectedCount(), result.getActualCount());

        assertTrue(result.getMatchedFields().contains("null"));
        assertTrue(result.getMatchedFields().contains("emptyArray"));
        assertTrue(result.getMatchedFields().contains("primitiveArray_0"));
        assertTrue(result.getMatchedFields().contains("primitiveArray_1"));
        assertTrue(result.getMatchedFields().contains("primitiveArray_3"));
        assertTrue(result.getMatchedFields().contains("object_boolean"));
        assertTrue(result.getMatchedFields().contains("object_null"));
        assertTrue(result.getMatchedFields().contains("object_emptyArray"));
        assertTrue(result.getMatchedFields().contains("object_primitiveArray_0"));
        assertTrue(result.getMatchedFields().contains("object_primitiveArray_1"));
        assertTrue(result.getMatchedFields().contains("object_primitiveArray_2"));
        assertTrue(result.getMatchedFields().contains("object_primitiveArray_3"));

        assertTrue(result.getMismatchedFields().containsKey("string"));
        assertEquals("string", result.getMismatchedFields().get("string").expectedValue());
        assertEquals("x", result.getMismatchedFields().get("string").actualValue());
        assertTrue(result.getMismatchedFields().containsKey("boolean"));
        assertEquals("true", result.getMismatchedFields().get("boolean").expectedValue());
        assertEquals("false", result.getMismatchedFields().get("boolean").actualValue());
        assertTrue(result.getMismatchedFields().containsKey("primitiveArray_2"));
        assertEquals("1", result.getMismatchedFields().get("primitiveArray_2").expectedValue());
        assertEquals("2", result.getMismatchedFields().get("primitiveArray_2").actualValue());
        assertTrue(result.getMismatchedFields().containsKey("object_number"));
        assertEquals("1", result.getMismatchedFields().get("object_number").expectedValue());
        assertEquals("2", result.getMismatchedFields().get("object_number").actualValue());

        assertTrue(result.getMissingFields().contains("number"));
        assertTrue(result.getMissingFields().contains("object_string"));

        assertTrue(result.getExtraFields().contains("extra"));
        assertTrue(result.getExtraFields().contains("object_extra"));
    }
}
