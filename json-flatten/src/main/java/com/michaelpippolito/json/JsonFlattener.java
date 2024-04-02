package com.michaelpippolito.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.RequiredArgsConstructor;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public class JsonFlattener {
    private static final String DEFAULT_SEPARATOR = "_";
    private static final String DEFAULT_BASE_PATH = "";
    public static final String EMPTY_ARRAY = "EMPTY_ARRAY";

    public static Map<String, String> flattenJsonString(String json) throws JsonProcessingException {
        return flattenJsonString(json, DEFAULT_SEPARATOR, DEFAULT_BASE_PATH);
    }

    public static Map<String, String> flattenJsonString(String json, String separator, String basePath) throws JsonProcessingException {
        Map<String, String> flattenedJson = new LinkedHashMap<>();
        JsonNode jsonNode = new ObjectMapper().readTree(json);
        jsonNode.fieldNames().forEachRemaining(new JsonFieldConsumer(jsonNode, flattenedJson, basePath, separator));
        return flattenedJson;
    }


    @RequiredArgsConstructor
    public static class JsonFieldConsumer implements Consumer<String> {
        private final JsonNode jsonNode;
        private final Map<String, String> flattenedJson;
        private final String basePath;
        private final String separator;

        @Override
        public void accept(String fieldName) {
            JsonNode jsonField = jsonNode.get(fieldName);
            String flattenedKey = DEFAULT_BASE_PATH.equals(basePath) ? fieldName : basePath + separator + fieldName;
            acceptField(flattenedKey, jsonField);
        }

        private void acceptField(String key, JsonNode field) {
            switch (field.getNodeType()) {
                case ARRAY -> acceptArrayField(key, (ArrayNode) field);
                case OBJECT, POJO -> field.fieldNames().forEachRemaining(new JsonFieldConsumer(field, flattenedJson, key, separator));
                case MISSING, NULL -> flattenedJson.put(key, null);
                case BINARY, STRING -> flattenedJson.put(key, field.textValue());
                case BOOLEAN -> flattenedJson.put(key, String.valueOf(field.booleanValue()));
                case NUMBER -> flattenedJson.put(key, field.numberValue().toString());
            }
        }

        private void acceptArrayField(String flattenedKey, ArrayNode arrayField) {
            if (arrayField.size() == 0) {
                flattenedJson.put(flattenedKey, EMPTY_ARRAY);
            } else {
                for (int index = 0; index < arrayField.size(); index++) {
                    JsonNode arrayEntry = arrayField.get(index);
                    String arrayEntryKey = String.format("%s[%d]", flattenedKey, index);
                    acceptField(arrayEntryKey, arrayEntry);
                }
            }
        }
    }
}
