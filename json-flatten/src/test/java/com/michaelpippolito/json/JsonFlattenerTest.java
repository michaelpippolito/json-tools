package com.michaelpippolito.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import static com.michaelpippolito.json.JsonFlattener.EMPTY_ARRAY;
import static org.junit.jupiter.api.Assertions.*;

public class JsonFlattenerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testFlattenJsonString() throws IOException {
        String originalJson =
                new String(Objects.requireNonNull(JsonFlattenerTest.class.getClassLoader().getResourceAsStream("test.json")).readAllBytes());


        Map<String, String> flattenedJson = JsonFlattener.flattenJsonString(originalJson);

        assertTrue(flattenedJson.containsKey("string"));
        assertEquals("string", flattenedJson.get("string"));
        assertTrue(flattenedJson.containsKey("boolean"));
        assertEquals("true", flattenedJson.get("boolean"));
        assertTrue(flattenedJson.containsKey("number"));
        assertEquals("1", flattenedJson.get("number"));
        assertTrue(flattenedJson.containsKey("null"));
        assertNull(flattenedJson.get("null"));
        assertTrue(flattenedJson.containsKey("emptyArray"));
        assertEquals(EMPTY_ARRAY, flattenedJson.get("emptyArray"));
        assertTrue(flattenedJson.containsKey("primitiveArray_0"));
        assertEquals("string", flattenedJson.get("primitiveArray_0"));
        assertTrue(flattenedJson.containsKey("primitiveArray_1"));
        assertEquals("true", flattenedJson.get("primitiveArray_1"));
        assertTrue(flattenedJson.containsKey("primitiveArray_2"));
        assertEquals("1", flattenedJson.get("primitiveArray_2"));
        assertTrue(flattenedJson.containsKey("primitiveArray_3"));
        assertNull(flattenedJson.get("primitiveArray_3"));

        assertTrue(flattenedJson.containsKey("object_string"));
        assertEquals("string", flattenedJson.get("object_string"));
        assertTrue(flattenedJson.containsKey("object_boolean"));
        assertEquals("true", flattenedJson.get("object_boolean"));
        assertTrue(flattenedJson.containsKey("object_number"));
        assertEquals("1", flattenedJson.get("object_number"));
        assertTrue(flattenedJson.containsKey("object_null"));
        assertNull(flattenedJson.get("object_null"));
        assertTrue(flattenedJson.containsKey("object_emptyArray"));
        assertEquals(EMPTY_ARRAY, flattenedJson.get("object_emptyArray"));
        assertTrue(flattenedJson.containsKey("object_primitiveArray_0"));
        assertEquals("string", flattenedJson.get("object_primitiveArray_0"));
        assertTrue(flattenedJson.containsKey("object_primitiveArray_1"));
        assertEquals("true", flattenedJson.get("object_primitiveArray_1"));
        assertTrue(flattenedJson.containsKey("object_primitiveArray_2"));
        assertEquals("1", flattenedJson.get("object_primitiveArray_2"));
        assertTrue(flattenedJson.containsKey("object_primitiveArray_3"));
        assertNull(flattenedJson.get("object_primitiveArray_3"));

        assertTrue(flattenedJson.containsKey("object_object_string"));
        assertEquals("string", flattenedJson.get("object_object_string"));
        assertTrue(flattenedJson.containsKey("object_object_boolean"));
        assertEquals("true", flattenedJson.get("object_object_boolean"));
        assertTrue(flattenedJson.containsKey("object_object_number"));
        assertEquals("1", flattenedJson.get("object_object_number"));
        assertTrue(flattenedJson.containsKey("object_object_null"));
        assertNull(flattenedJson.get("object_object_null"));
        assertTrue(flattenedJson.containsKey("object_object_emptyArray"));
        assertEquals(EMPTY_ARRAY, flattenedJson.get("object_object_emptyArray"));
        assertTrue(flattenedJson.containsKey("object_object_primitiveArray_0"));
        assertEquals("string", flattenedJson.get("object_object_primitiveArray_0"));
        assertTrue(flattenedJson.containsKey("object_object_primitiveArray_1"));
        assertEquals("true", flattenedJson.get("object_object_primitiveArray_1"));
        assertTrue(flattenedJson.containsKey("object_object_primitiveArray_2"));
        assertEquals("1", flattenedJson.get("object_object_primitiveArray_2"));
        assertTrue(flattenedJson.containsKey("object_object_primitiveArray_3"));
        assertNull(flattenedJson.get("object_object_primitiveArray_3"));

        assertTrue(flattenedJson.containsKey("objectArray_0_string"));
        assertEquals("string", flattenedJson.get("objectArray_0_string"));
        assertTrue(flattenedJson.containsKey("objectArray_0_boolean"));
        assertEquals("true", flattenedJson.get("objectArray_0_boolean"));
        assertTrue(flattenedJson.containsKey("objectArray_0_number"));
        assertEquals("1", flattenedJson.get("objectArray_0_number"));
        assertTrue(flattenedJson.containsKey("objectArray_0_null"));
        assertNull(flattenedJson.get("objectArray_0_null"));
        assertTrue(flattenedJson.containsKey("objectArray_0_emptyArray"));
        assertEquals(EMPTY_ARRAY, flattenedJson.get("objectArray_0_emptyArray"));
        assertTrue(flattenedJson.containsKey("objectArray_0_primitiveArray_0"));
        assertEquals("string", flattenedJson.get("objectArray_0_primitiveArray_0"));
        assertTrue(flattenedJson.containsKey("objectArray_0_primitiveArray_1"));
        assertEquals("true", flattenedJson.get("objectArray_0_primitiveArray_1"));
        assertTrue(flattenedJson.containsKey("objectArray_0_primitiveArray_2"));
        assertEquals("1", flattenedJson.get("objectArray_0_primitiveArray_2"));
        assertTrue(flattenedJson.containsKey("objectArray_0_primitiveArray_3"));
        assertNull(flattenedJson.get("objectArray_0_primitiveArray_3"));
    }
}
