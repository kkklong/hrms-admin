package com.hrms.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@SuppressWarnings("unused")
public class JsonUtils {
    private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);

    private static class ObjectMapperHolder {
        private static final ObjectMapper TRANSFER_OBJECT_MAPPER = new ObjectMapper()
                .registerModule(new ParameterNamesModule())
                .registerModule(new Jdk8Module())
                .registerModule(buildJavaTimeModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true)
                .configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false)
                .setTimeZone(TimeZone.getDefault())
                .setDateFormat(getDateTimeFormat());

        private static final ObjectMapper SERIALIZATION_OBJECT_MAPPER = new ObjectMapper()
                .registerModule(new ParameterNamesModule())
                .registerModule(new Jdk8Module())
                .registerModule(buildJavaTimeModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false)
                .setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY)
                .activateDefaultTyping(BasicPolymorphicTypeValidator.builder()
                        .allowIfSubType(Object.class)
                        .build(), ObjectMapper.DefaultTyping.NON_FINAL);


        private static SimpleDateFormat getDateTimeFormat() {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                    Locale.getDefault());
            simpleDateFormat.setLenient(false);
            return simpleDateFormat;
        }
    }


    private JsonUtils() {
    }

    public static JavaTimeModule buildJavaTimeModule() {

        JavaTimeModule module = new JavaTimeModule();
        DateTimeFormatter dtFormatter = DateTimeFormatter
                .ofPattern("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        DateTimeFormatter dFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd",
                Locale.getDefault());
        DateTimeFormatter tFormatter = DateTimeFormatter.ofPattern("HH:mm:ss",
                Locale.getDefault());
        module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(dtFormatter));
        module.addSerializer(LocalDate.class, new LocalDateSerializer(dFormatter));
        module.addSerializer(LocalTime.class, new LocalTimeSerializer(tFormatter));
        module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(dtFormatter));
        module.addDeserializer(LocalDate.class, new LocalDateDeserializer(dFormatter));
        module.addDeserializer(LocalTime.class, new LocalTimeDeserializer(tFormatter));
        return module;
    }

    public static ObjectNode createObjectNode() {
        return transferObjectMapper().createObjectNode();
    }

    public static ArrayNode createArrayNode() {
        return transferObjectMapper().createArrayNode();
    }

    public static ObjectMapper transferObjectMapper() {
        return ObjectMapperHolder.TRANSFER_OBJECT_MAPPER;
    }

    public static ObjectMapper serializationObjectMapper() {
        return ObjectMapperHolder.SERIALIZATION_OBJECT_MAPPER;
    }

    public static String writeValueAsString(ObjectMapper om, Object object)
            throws JsonProcessingException {
        if (om != null && object != null) {
            return om.writeValueAsString(object);
        }
        return null;
    }

    public static String writeValueAsString(Object object) throws JsonProcessingException {
        return writeValueAsString(transferObjectMapper(), object);
    }

    public static String toJSON(ObjectMapper om, Object object) {
        try {
            return writeValueAsString(om, object);
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public static String toJSON(Object object) {
        return toJSON(transferObjectMapper(), object);
    }

    public static Optional<String> optional2JSON(ObjectMapper om, Object object) {
        return Optional.ofNullable(toJSON(om, object));
    }

    public static Optional<String> optional2JSON(Object object) {
        return optional2JSON(transferObjectMapper(), object);
    }

    public static String toJSON(ObjectMapper om, Object object, String defaultValue) {
        return optional2JSON(om, object).orElse(defaultValue);
    }

    public static String toJSON(Object object, String defaultValue) {
        return toJSON(transferObjectMapper(), object, defaultValue);
    }

    public static JsonNode readTree(ObjectMapper om, String json) throws IOException {
        if (om != null && json != null && !json.trim()
                .isEmpty()) {
            return om.readTree(json);
        }
        return null;
    }

    public static JsonNode readTree(String json) throws IOException {
        return readTree(transferObjectMapper(), json);
    }

    public static JsonNode toJsonNode(ObjectMapper om, String json) {
        try {
            return readTree(om, json);
        } catch (IOException e) {
            logger.error("error json:\n{}", json, e);
            return null;
        }
    }

    public static JsonNode toJsonNode(String json) {
        return toJsonNode(transferObjectMapper(), json);
    }

    public static Optional<JsonNode> optional2JsonNode(ObjectMapper om, String json) {
        return Optional.ofNullable(toJsonNode(om, json));
    }

    public static Optional<JsonNode> optional2JsonNode(String json) {
        return optional2JsonNode(transferObjectMapper(), json);
    }

    public static JsonNode toJsonNode(ObjectMapper om, String json, JsonNode defaultValue) {
        return optional2JsonNode(om, json).orElse(defaultValue);
    }

    public static JsonNode toJsonNode(String json, JsonNode defaultValue) {
        return toJsonNode(transferObjectMapper(), json, defaultValue);
    }

    public static <T> T readValue(ObjectMapper om, String json, Class<T> clazz) throws IOException {
        if (om != null && json != null && !json.trim()
                .isEmpty() && clazz != null) {
            return om.readerFor(clazz)
                    .readValue(json);
        }
        return null;
    }

    public static <T> T readValue(ObjectMapper om, JsonNode json, Class<T> clazz) throws IOException {
        if (om != null && json != null && !json.isEmpty() && clazz != null) {
            return om.readerFor(clazz)
                    .readValue(json);
        }
        return null;
    }

    public static <T> T readValue(String json, Class<T> clazz) throws IOException {
        return readValue(transferObjectMapper(), json, clazz);
    }

    public static <T> T toObject(ObjectMapper om, String json, Class<T> clazz) {
        try {
            return readValue(om, json, clazz);
        } catch (IOException e) {
            logger.error("error json:\n{}", json, e);
            return null;
        }
    }

    public static <T> T toObject(ObjectMapper om, JsonNode json, Class<T> clazz) {
        try {
            return readValue(om, json, clazz);
        } catch (IOException e) {
            logger.error("error json:\n{}", json, e);
            return null;
        }
    }

    public static <T> T toObject(String json, Class<T> clazz) {
        return toObject(transferObjectMapper(), json, clazz);
    }

    public static <T> T toObject(JsonNode json, Class<T> clazz) {
        return toObject(transferObjectMapper(), json, clazz);
    }

    public static <T> Optional<T> optional2Object(ObjectMapper om, String json, Class<T> clazz) {
        return Optional.ofNullable(toObject(om, json, clazz));
    }

    public static <T> Optional<T> optional2Object(ObjectMapper om, JsonNode json, Class<T> clazz) {
        return Optional.ofNullable(toObject(om, json, clazz));
    }

    public static <T> Optional<T> optional2Object(String json, Class<T> clazz) {
        return optional2Object(transferObjectMapper(), json, clazz);
    }

    public static <T> Optional<T> optional2Object(JsonNode json, Class<T> clazz) {
        return optional2Object(transferObjectMapper(), json, clazz);
    }

    public static <T> T toObject(ObjectMapper om, String json, Class<T> clazz, T defaultValue) {
        return optional2Object(om, json, clazz).orElse(defaultValue);
    }

    public static <T> T toObject(ObjectMapper om, JsonNode json, Class<T> clazz, T defaultValue) {
        return optional2Object(om, json, clazz).orElse(defaultValue);
    }

    public static <T> T toObject(String json, Class<T> clazz, T defaultValue) {
        return toObject(transferObjectMapper(), json, clazz, defaultValue);
    }

    public static <T> T toObject(JsonNode json, Class<T> clazz, T defaultValue) {
        return toObject(transferObjectMapper(), json, clazz, defaultValue);
    }

    public static <T> T readValue(ObjectMapper om, String json, TypeReference<T> typeReference)
            throws IOException {
        if (om != null && json != null && !json.trim()
                .isEmpty() && typeReference != null) {
            return om.readerFor(typeReference)
                    .readValue(json);
        }
        return null;
    }

    public static <T> T readValue(ObjectMapper om, JsonNode json, TypeReference<T> typeReference)
            throws IOException {
        if (om != null && json != null && typeReference != null) {
            return om.readerFor(typeReference)
                    .readValue(json);
        }
        return null;
    }

    public static <T> T readValue(String json, TypeReference<T> typeReference) throws IOException {
        return readValue(transferObjectMapper(), json, typeReference);
    }

    public static <T> T readValue(JsonNode json, TypeReference<T> typeReference) throws IOException {
        return readValue(transferObjectMapper(), json, typeReference);
    }

    public static <T> T toObject(ObjectMapper om, String json, TypeReference<T> typeReference) {
        try {
            return readValue(om, json, typeReference);
        } catch (IOException e) {
            logger.error("error json:\n{}", json, e);
            return null;
        }
    }

    public static <T> T toObject(ObjectMapper om, JsonNode json, TypeReference<T> typeReference) {
        try {
            return readValue(om, json, typeReference);
        } catch (IOException e) {
            logger.error("error json:\n{}", json, e);
            return null;
        }
    }

    public static <T> T toObject(String json, TypeReference<T> typeReference) {
        return toObject(transferObjectMapper(), json, typeReference);
    }

    public static <T> T toObject(JsonNode json, TypeReference<T> typeReference) {
        return toObject(transferObjectMapper(), json, typeReference);
    }

    public static <T> Optional<T> optional2Object(ObjectMapper om, String json,
                                                  TypeReference<T> typeReference) {
        return Optional.ofNullable(toObject(om, json, typeReference));
    }

    public static <T> Optional<T> optional2Object(ObjectMapper om, JsonNode json,
                                                  TypeReference<T> typeReference) {
        return Optional.ofNullable(toObject(om, json, typeReference));
    }

    public static <T> Optional<T> optional2Object(String json, TypeReference<T> typeReference) {
        return optional2Object(transferObjectMapper(), json, typeReference);
    }

    public static <T> Optional<T> optional2Object(JsonNode json, TypeReference<T> typeReference) {
        return optional2Object(transferObjectMapper(), json, typeReference);
    }

    public static <T> T toObject(ObjectMapper om, String json, TypeReference<T> typeReference,
                                 T defaultValue) {
        return optional2Object(om, json, typeReference).orElse(defaultValue);
    }

    public static <T> T toObject(ObjectMapper om, JsonNode json, TypeReference<T> typeReference,
                                 T defaultValue) {
        return optional2Object(om, json, typeReference).orElse(defaultValue);
    }

    public static <T> T toObject(String json, TypeReference<T> typeReference, T defaultValue) {
        return toObject(transferObjectMapper(), json, typeReference, defaultValue);
    }

    public static <T> T toObject(JsonNode json, TypeReference<T> typeReference, T defaultValue) {
        return toObject(transferObjectMapper(), json, typeReference, defaultValue);
    }

    public static <K, V> Map<K, V> readMap(ObjectMapper om, String json, Class<K> keyType,
                                           Class<V> valueType) throws IOException {
        return readValue(om, json, new TypeReference<>() {
        });
    }

    public static <K, V> Map<K, V> readMap(String json, Class<K> keyType, Class<V> valueType)
            throws IOException {
        return readMap(transferObjectMapper(), json, keyType, valueType);
    }

    public static <K, V> Map<K, V> toMap(ObjectMapper om, String json, Class<K> keyType,
                                         Class<V> valueType) {
        try {
            return readMap(om, json, keyType, valueType);
        } catch (IOException e) {
            logger.error("error json:\n{}", json, e);
            return null;
        }
    }

    public static <K, V> Map<K, V> toMap(String json, Class<K> keyType, Class<V> valueType) {
        return toMap(transferObjectMapper(), json, keyType, valueType);
    }

    public static <K, V> Optional<Map<K, V>> optional2Map(ObjectMapper om, String json,
                                                          Class<K> keyType, Class<V> valueType) {
        return Optional.ofNullable(toMap(om, json, keyType, valueType));
    }

    public static <K, V> Optional<Map<K, V>> optional2Map(String json, Class<K> keyType,
                                                          Class<V> valueType) {
        return optional2Map(transferObjectMapper(), json, keyType, valueType);
    }

    public static <K, V> Map<K, V> toMap(ObjectMapper om, String json, Class<K> keyType,
                                         Class<V> valueType, Map<K, V> defaultValue) {
        return optional2Map(om, json, keyType, valueType).orElse(defaultValue);
    }

    public static <K, V> Map<K, V> toMap(String json, Class<K> keyType, Class<V> valueType,
                                         Map<K, V> defaultValue) {
        return toMap(transferObjectMapper(), json, keyType, valueType, defaultValue);
    }

    public static <V> Map<String, V> readMap(ObjectMapper om, String json, Class<V> valueType)
            throws IOException {
        return readMap(om, json, String.class, valueType);
    }

    public static <V> Map<String, V> readMap(String json, Class<V> valueType) throws IOException {
        return readMap(transferObjectMapper(), json, valueType);
    }

    public static <V> Map<String, V> toMap(ObjectMapper om, String json, Class<V> valueType) {
        try {
            return readMap(om, json, valueType);
        } catch (IOException e) {
            logger.error("error json:\n{}", json, e);
            return null;
        }
    }

    public static <V> Map<String, V> toMap(String json, Class<V> valueType) {
        return toMap(transferObjectMapper(), json, valueType);
    }

    public static <V> Optional<Map<String, V>> optional2Map(ObjectMapper om, String json,
                                                            Class<V> valueType) {
        return Optional.ofNullable(toMap(om, json, valueType));
    }

    public static <V> Optional<Map<String, V>> optional2Map(String json, Class<V> valueType) {
        return optional2Map(transferObjectMapper(), json, valueType);
    }

    public static <V> Map<String, V> toMap(ObjectMapper om, String json, Class<V> valueType,
                                           Map<String, V> defaultValue) {
        return optional2Map(om, json, valueType).orElse(defaultValue);
    }

    public static <V> Map<String, V> toMap(String json, Class<V> valueType,
                                           Map<String, V> defaultValue) {
        return toMap(transferObjectMapper(), json, valueType, defaultValue);
    }

    public static Map<String, Object> readMap(ObjectMapper om, String json) throws IOException {
        return readMap(om, json, Object.class);
    }

    public static Map<String, Object> readMap(String json) throws IOException {
        return readMap(transferObjectMapper(), json);
    }

    public static Map<String, Object> toMap(ObjectMapper om, String json) {
        try {
            return readMap(om, json);
        } catch (IOException e) {
            logger.error("error json:\n{}", json, e);
            return null;
        }
    }

    public static Map<String, Object> toMap(String json) {
        return toMap(transferObjectMapper(), json);
    }

    public static Optional<Map<String, Object>> optional2Map(ObjectMapper om, String json) {
        return Optional.ofNullable(toMap(om, json));
    }

    public static Optional<Map<String, Object>> optional2Map(String json) {
        return optional2Map(transferObjectMapper(), json);
    }

    public static Map<String, Object> toMap(ObjectMapper om, String json,
                                            Map<String, Object> defaultValue) {
        return optional2Map(om, json).orElse(defaultValue);
    }

    public static Map<String, Object> toMap(String json, Map<String, Object> defaultValue) {
        return toMap(transferObjectMapper(), json, defaultValue);
    }

    public static Map<String, String> toStringMap(ObjectMapper om, String json) {
        return toMap(om, json, String.class);
    }

    public static Map<String, String> toStringMap(String json) {
        return toStringMap(transferObjectMapper(), json);
    }

    public static Optional<Map<String, String>> optional2StringMap(ObjectMapper om, String json) {
        return optional2Map(om, json, String.class);
    }

    public static Optional<Map<String, String>> optional2StringMap(String json) {
        return optional2StringMap(transferObjectMapper(), json);
    }

    public static Map<String, String> toStringMap(ObjectMapper om, String json,
                                                  Map<String, String> defaultValue) {
        return toMap(om, json, String.class, defaultValue);
    }

    public static Map<String, String> toStringMap(String json, Map<String, String> defaultValue) {
        return toStringMap(transferObjectMapper(), json, defaultValue);
    }

    public static <T> List<T> readList(ObjectMapper om, String json, Class<T> clazz)
            throws IOException {
        if (om != null && json != null && !json.trim()
                .isEmpty() && clazz != null) {
            return om.readerFor(om.getTypeFactory()
                            .constructCollectionType(List.class, clazz))
                    .readValue(json);
        }
        return null;
    }

    public static <T> List<T> readList(String json, Class<T> clazz) throws IOException {
        return readList(transferObjectMapper(), json, clazz);
    }

    public static <T> List<T> toList(ObjectMapper om, String json, Class<T> clazz) {
        try {
            return readList(om, json, clazz);
        } catch (IOException e) {
            logger.error("error json:\n{}", json, e);
            return null;
        }
    }

    public static <T> List<T> toList(String json, Class<T> clazz) {
        return toList(transferObjectMapper(), json, clazz);
    }

    public static <T> Optional<List<T>> optional2List(ObjectMapper om, String json, Class<T> clazz) {
        return Optional.ofNullable(toList(om, json, clazz));
    }

    public static <T> Optional<List<T>> optional2List(String json, Class<T> clazz) {
        return optional2List(transferObjectMapper(), json, clazz);
    }

    public static <T> List<T> toList(ObjectMapper om, String json, Class<T> clazz,
                                     List<T> defaultValue) {
        return optional2List(om, json, clazz).orElse(defaultValue);
    }

    public static <T> List<T> toList(String json, Class<T> clazz, List<T> defaultValue) {
        return toList(transferObjectMapper(), json, clazz, defaultValue);
    }

    public static <T> Set<T> readSet(ObjectMapper om, String json, Class<T> clazz)
            throws IOException {
        if (om != null && json != null && !json.trim()
                .isEmpty() && clazz != null) {
            return om.readerFor(om.getTypeFactory()
                            .constructCollectionType(Set.class, clazz))
                    .readValue(json);
        }
        return null;
    }

    public static <T> Set<T> readSet(String json, Class<T> clazz) throws IOException {
        return readSet(transferObjectMapper(), json, clazz);
    }

    public static <T> Set<T> toSet(ObjectMapper om, String json, Class<T> clazz) {
        try {
            return readSet(om, json, clazz);
        } catch (IOException e) {
            logger.error("error json:\n{}", json, e);
            return null;
        }
    }

    public static <T> Set<T> toSet(String json, Class<T> clazz) {
        return toSet(transferObjectMapper(), json, clazz);
    }

    public static <T> Optional<Set<T>> optional2Set(ObjectMapper om, String json, Class<T> clazz) {
        return Optional.ofNullable(toSet(om, json, clazz));
    }

    public static <T> Optional<Set<T>> optional2Set(String json, Class<T> clazz) {
        return optional2Set(transferObjectMapper(), json, clazz);
    }

    public static <T> Set<T> toSet(ObjectMapper om, String json, Class<T> clazz,
                                   Set<T> defaultValue) {
        return optional2Set(om, json, clazz).orElse(defaultValue);
    }

    public static <T> Set<T> toSet(String json, Class<T> clazz, Set<T> defaultValue) {
        return toSet(transferObjectMapper(), json, clazz, defaultValue);
    }


    public static <T> T[] readArray(ObjectMapper om, String json, Class<T> clazz) throws IOException {
        if (om != null && json != null && !json.trim()
                .isEmpty() && clazz != null) {
            return om.readerFor(om.getTypeFactory()
                            .constructArrayType(clazz))
                    .readValue(json);
        }
        return null;
    }

    public static <T> T[] readArray(String json, Class<T> clazz) throws IOException {
        return readArray(transferObjectMapper(), json, clazz);
    }

    public static <T> T[] toArray(ObjectMapper om, String json, Class<T> clazz) {
        try {
            return readArray(om, json, clazz);
        } catch (IOException e) {
            logger.error("error json:\n{}", json, e);
            return null;
        }
    }

    public static <T> T[] toArray(String json, Class<T> clazz) {
        return toArray(transferObjectMapper(), json, clazz);
    }

    public static <T> Optional<T[]> optional2Array(ObjectMapper om, String json, Class<T> clazz) {
        return Optional.ofNullable(toArray(om, json, clazz));
    }

    public static <T> Optional<T[]> optional2Array(String json, Class<T> clazz) {
        return optional2Array(transferObjectMapper(), json, clazz);
    }

    public static <T> T[] toArray(ObjectMapper om, String json, Class<T> clazz, T[] defaultValue) {
        return optional2Array(om, json, clazz).orElse(defaultValue);
    }

    public static <T> T[] toArray(String json, Class<T> clazz, T[] defaultValue) {
        return toArray(transferObjectMapper(), json, clazz, defaultValue);
    }

    public static JsonNode getJsonNode(JsonNode jsonNode, String fieldName, JsonNode defaultValue) {
        if (jsonNode != null && jsonNode.has(fieldName)) {
            return jsonNode.get(fieldName);
        }
        return defaultValue;
    }

    /**
     * 取得{@code jsonNode}下的{@code fieldName}節點，{@code jsonNode}或{@code fieldName}不存在時回傳null
     *
     * @param jsonNode  當前節點
     * @param fieldName 要取得的節點名稱
     */
    public static JsonNode getJsonNode(JsonNode jsonNode, String fieldName) {
        return getJsonNode(jsonNode, fieldName, null);
    }

    public static Optional<JsonNode> optionalGetJsonNode(JsonNode jsonNode, String fieldName) {
        return Optional.ofNullable(getJsonNode(jsonNode, fieldName));
    }

    /**
     * <ul>
     * <li>使用{@link JsonNode#textValue()}取得String值</li>
     * <li>不會自動轉換非字串欄位，需要自動轉換請用{@link JsonUtils#getAsText(JsonNode, String, String)}</li>
     * <li>{@link JsonUtils#getJsonNode(JsonNode, String)}為null或非字串欄位回傳{@code defaultValue}</li>
     * </ul>
     *
     * @param jsonNode     要取值的節點
     * @param fieldName    要取值的欄位名稱
     * @param defaultValue null時要回傳的值
     */
    public static String getText(JsonNode jsonNode, String fieldName, String defaultValue) {
        return optionalGetJsonNode(jsonNode, fieldName)
                .filter(JsonNode::isTextual)
                .map(JsonNode::textValue)
                .orElse(defaultValue);
    }

    /**
     * <ul>
     * <li>使用{@link JsonNode#textValue()}取得String值</li>
     * <li>不會自動轉換非字串欄位，需要自動轉換請用{@link JsonUtils#getAsText(JsonNode, String)}</li>
     * <li>會將null轉成空字串，需要null值請用{@link JsonUtils#getTextWithNull(JsonNode, String)}</li>
     * </ul>
     *
     * @param jsonNode  要取值的節點
     * @param fieldName 要取值的欄位名稱
     */
    public static String getText(JsonNode jsonNode, String fieldName) {
        return getText(jsonNode, fieldName, "");
    }

    /**
     * <ul>
     * <li>使用{@link JsonNode#textValue()}取得String值</li>
     * <li>不會自動轉換非字串欄位，需要自動轉換請用{@link JsonUtils#getAsText(JsonNode, String)}</li>
     * <li>需要將null值轉成空字串請用{@link JsonUtils#getText(JsonNode, String)}</li>
     * </ul>
     *
     * @param jsonNode  要取值的節點
     * @param fieldName 要取值的欄位名稱
     */
    public static String getTextWithNull(JsonNode jsonNode, String fieldName) {
        return getText(jsonNode, fieldName, null);
    }

    /**
     * <ul>
     * <li>使用{@link JsonNode#asText(String)}取得String值</li>
     * <li>會自動轉換非字串欄位，不需要自動轉換請用{@link JsonUtils#getText(JsonNode, String)}</li>
     * <li>會將null轉成{@code defaultValue}</li>
     * </ul>
     *
     * @param jsonNode     要取值的節點
     * @param fieldName    要取值的欄位名稱
     * @param defaultValue 當欄位回傳null時要回傳的值
     */
    public static String getAsText(JsonNode jsonNode, String fieldName, String defaultValue) {
        return optionalGetJsonNode(jsonNode, fieldName)
                .map(node -> node.asText(defaultValue))
                .orElse(defaultValue);
    }

    /**
     * <ul>
     * <li>使用{@link JsonNode#asText()}取得String值</li>
     * <li>會自動轉換非字串欄位，不需要自動轉換請用{@link JsonUtils#getText(JsonNode, String)}</li>
     * <li>會將null轉成空字串，需要null值請用{@link JsonUtils#getAsTextWithNull(JsonNode, String)}</li>
     * </ul>
     *
     * @param jsonNode  要取值的節點
     * @param fieldName 要取值的欄位名稱
     */
    public static String getAsText(JsonNode jsonNode, String fieldName) {
        return getAsText(jsonNode, fieldName, "");
    }

    /**
     * <ul>
     * <li>使用{@link JsonNode#asText()}取得String值</li>
     * <li>會自動轉換非字串欄位，不需要自動轉換請用{@link JsonUtils#getText(JsonNode, String)}</li>
     * <li>會將null轉成空字串，需要null值請用{@link JsonUtils#getAsText(JsonNode, String)}</li>
     * </ul>
     *
     * @param jsonNode  要取值的節點
     * @param fieldName 要取值的欄位名稱
     */
    public static String getAsTextWithNull(JsonNode jsonNode, String fieldName) {
        return getAsText(jsonNode, fieldName, null);
    }

    /**
     * <ul>
     * <li>使用{@link JsonNode#booleanValue()}取得boolean值</li>
     * <li>不會自動轉換非布林欄位，需要自動轉換請用{@link JsonUtils#getAsBoolean(JsonNode, String, boolean)}</li>
     * <li>{@link JsonUtils#getJsonNode(JsonNode, String)}為null或非布林欄位回傳{@code defaultValue}</li>
     * </ul>
     *
     * @param jsonNode     要取值的節點
     * @param fieldName    要取值的欄位名稱
     * @param defaultValue {@link JsonUtils#getJsonNode(JsonNode, String)}為null或非布林欄位的回傳值
     */
    public static boolean getBoolean(JsonNode jsonNode, String fieldName, boolean defaultValue) {
        return optionalGetJsonNode(jsonNode, fieldName)
                .filter(JsonNode::isBoolean)
                .map(JsonNode::booleanValue)
                .orElse(defaultValue);
    }

    /**
     * <ul>
     * <li>使用{@link JsonNode#booleanValue()}取得boolean值</li>
     * <li>不會自動轉換非布林欄位，需要自動轉換請用{@link JsonUtils#getAsBoolean(JsonNode, String)}</li>
     * <li>{@link JsonUtils#getJsonNode(JsonNode, String)}為null或非布林欄位回傳false</li>
     * </ul>
     *
     * @param jsonNode  要取值的節點
     * @param fieldName 要取值的欄位名稱
     */
    public static boolean getBoolean(JsonNode jsonNode, String fieldName) {
        return getBoolean(jsonNode, fieldName, false);
    }

    /**
     * <ul>
     * <li>使用{@link JsonNode#asBoolean(boolean)}取得boolean值</li>
     * <li>會自動轉換非布林欄位，不需要自動轉換請用{@link JsonUtils#getBoolean(JsonNode, String)}</li>
     * <li>當無法成功轉成boolean時回傳{@code defaultValue}</li>
     * </ul>
     *
     * @param jsonNode     要取值的節點
     * @param fieldName    要取值的欄位名稱
     * @param defaultValue 當欄位無法成功轉成boolean時傳的值
     */
    public static boolean getAsBoolean(JsonNode jsonNode, String fieldName, boolean defaultValue) {
        return optionalGetJsonNode(jsonNode, fieldName)
                .map(node -> node.asBoolean(defaultValue))
                .orElse(defaultValue);
    }

    /**
     * <ul>
     * <li>使用{@link JsonNode#asBoolean(boolean)}取得boolean值</li>
     * <li>會自動轉換非布林欄位，不需要自動轉換請用{@link JsonUtils#getBoolean(JsonNode, String)}</li>
     * <li>當無法成功轉成boolean時回傳false</li>
     * </ul>
     *
     * @param jsonNode  要取值的節點
     * @param fieldName 要取值的欄位名稱
     */
    public static boolean getAsBoolean(JsonNode jsonNode, String fieldName) {
        return getAsBoolean(jsonNode, fieldName, false);
    }

    /**
     * <ul>
     * <li>使用{@link JsonNode#intValue()}取得int值</li>
     * <li>不會自動轉換非數值欄位，需要自動轉換請用{@link JsonUtils#getAsInt(JsonNode, String, int)}</li>
     * <li>{@link JsonUtils#getJsonNode(JsonNode, String)}為null或非數值欄位回傳{@code defaultValue}</li>
     * </ul>
     *
     * @param jsonNode     要取值的節點
     * @param fieldName    要取值的欄位名稱
     * @param defaultValue {@link JsonUtils#getJsonNode(JsonNode, String)}為null或非數值欄位的回傳值
     */
    public static int getInt(JsonNode jsonNode, String fieldName, int defaultValue) {
        return optionalGetJsonNode(jsonNode, fieldName)
                .filter(JsonNode::isNumber)
                .map(JsonNode::intValue)
                .orElse(defaultValue);
    }

    /**
     * <ul>
     * <li>使用{@link JsonNode#intValue()}取得int值</li>
     * <li>不會自動轉換非數值欄位，需要自動轉換請用{@link JsonUtils#getAsInt(JsonNode, String)}</li>
     * <li>{@link JsonUtils#getJsonNode(JsonNode, String)}為null或非數值欄位回傳0</li>
     * </ul>
     *
     * @param jsonNode  要取值的節點
     * @param fieldName 要取值的欄位名稱
     */
    public static int getInt(JsonNode jsonNode, String fieldName) {
        return getInt(jsonNode, fieldName, 0);
    }

    /**
     * <ul>
     * <li>使用{@link JsonNode#asInt(int)}取得int值</li>
     * <li>會自動轉換非數值欄位，不需要自動轉換請用{@link JsonUtils#getInt(JsonNode, String)}</li>
     * <li>當無法成功轉成int時回傳{@code defaultValue}</li>
     * </ul>
     *
     * @param jsonNode     要取值的節點
     * @param fieldName    要取值的欄位名稱
     * @param defaultValue 當欄位無法成功轉成int時傳的值
     */
    public static int getAsInt(JsonNode jsonNode, String fieldName, int defaultValue) {
        return optionalGetJsonNode(jsonNode, fieldName)
                .map(node -> node.asInt(defaultValue))
                .orElse(defaultValue);
    }

    /**
     * <ul>
     * <li>使用{@link JsonNode#asInt(int)}取得int值</li>
     * <li>會自動轉換非數值欄位，不需要自動轉換請用{@link JsonUtils#getInt(JsonNode, String)}</li>
     * <li>當無法成功轉成int時回傳0</li>
     * </ul>
     *
     * @param jsonNode  要取值的節點
     * @param fieldName 要取值的欄位名稱
     */
    public static int getAsInt(JsonNode jsonNode, String fieldName) {
        return getAsInt(jsonNode, fieldName, 0);
    }

    /**
     * <ul>
     * <li>使用{@link JsonNode#longValue()}取得long值</li>
     * <li>不會自動轉換非數值欄位，需要自動轉換請用{@link JsonUtils#getAsLong(JsonNode, String, long)}</li>
     * <li>{@link JsonUtils#getJsonNode(JsonNode, String)}為null或非數值欄位回傳{@code defaultValue}</li>
     * </ul>
     *
     * @param jsonNode     要取值的節點
     * @param fieldName    要取值的欄位名稱
     * @param defaultValue {@link JsonUtils#getJsonNode(JsonNode, String)}為null或非數值欄位的回傳值
     */
    public static long getLong(JsonNode jsonNode, String fieldName, long defaultValue) {
        return optionalGetJsonNode(jsonNode, fieldName)
                .filter(JsonNode::isNumber)
                .map(JsonNode::longValue)
                .orElse(defaultValue);
    }

    /**
     * <ul>
     * <li>使用{@link JsonNode#longValue()}取得long值</li>
     * <li>不會自動轉換非數值欄位，需要自動轉換請用{@link JsonUtils#getAsLong(JsonNode, String)}</li>
     * <li>{@link JsonUtils#getJsonNode(JsonNode, String)}為null或非數值欄位回傳0</li>
     * </ul>
     *
     * @param jsonNode  要取值的節點
     * @param fieldName 要取值的欄位名稱
     */
    public static long getLong(JsonNode jsonNode, String fieldName) {
        return getLong(jsonNode, fieldName, 0);
    }

    /**
     * <ul>
     * <li>使用{@link JsonNode#asLong(long)}取得long值</li>
     * <li>會自動轉換非數值欄位，不需要自動轉換請用{@link JsonUtils#getLong(JsonNode, String)}</li>
     * <li>當無法成功轉成long時回傳{@code defaultValue}</li>
     * </ul>
     *
     * @param jsonNode     要取值的節點
     * @param fieldName    要取值的欄位名稱
     * @param defaultValue 當欄位無法成功轉成long時傳的值
     */
    public static long getAsLong(JsonNode jsonNode, String fieldName, long defaultValue) {
        return optionalGetJsonNode(jsonNode, fieldName)
                .map(node -> node.asLong(defaultValue))
                .orElse(defaultValue);
    }

    /**
     * <ul>
     * <li>使用{@link JsonNode#asLong(long)}取得long值</li>
     * <li>會自動轉換非數值欄位，不需要自動轉換請用{@link JsonUtils#getLong(JsonNode, String)}</li>
     * <li>當無法成功轉成long時回傳0</li>
     * </ul>
     *
     * @param jsonNode  要取值的節點
     * @param fieldName 要取值的欄位名稱
     */
    public static long getAsLong(JsonNode jsonNode, String fieldName) {
        return getAsLong(jsonNode, fieldName, 0);
    }

    /**
     * <ul>
     * <li>使用{@link JsonNode#doubleValue()}取得double值</li>
     * <li>不會自動轉換非數值欄位，需要自動轉換請用{@link JsonUtils#getAsDouble(JsonNode, String, double)}</li>
     * <li>{@link JsonUtils#getJsonNode(JsonNode, String)}為null或非數值欄位回傳{@code defaultValue}</li>
     * </ul>
     *
     * @param jsonNode     要取值的節點
     * @param fieldName    要取值的欄位名稱
     * @param defaultValue {@link JsonUtils#getJsonNode(JsonNode, String)}為null或非數值欄位的回傳值
     */
    public static double getDouble(JsonNode jsonNode, String fieldName, double defaultValue) {
        return optionalGetJsonNode(jsonNode, fieldName)
                .filter(JsonNode::isNumber)
                .map(JsonNode::doubleValue)
                .orElse(defaultValue);
    }

    /**
     * <ul>
     * <li>使用{@link JsonNode#doubleValue()}取得double值</li>
     * <li>不會自動轉換非數值欄位，需要自動轉換請用{@link JsonUtils#getAsDouble(JsonNode, String)}</li>
     * <li>{@link JsonUtils#getJsonNode(JsonNode, String)}為null或非數值欄位回傳0</li>
     * </ul>
     *
     * @param jsonNode  要取值的節點
     * @param fieldName 要取值的欄位名稱
     */
    public static double getDouble(JsonNode jsonNode, String fieldName) {
        return getDouble(jsonNode, fieldName, 0);
    }

    /**
     * <ul>
     * <li>使用{@link JsonNode#asDouble(double)}取得double值</li>
     * <li>會自動轉換非數值欄位，不需要自動轉換請用{@link JsonUtils#getDouble(JsonNode, String)}</li>
     * <li>當無法成功轉成double時回傳{@code defaultValue}</li>
     * </ul>
     *
     * @param jsonNode     要取值的節點
     * @param fieldName    要取值的欄位名稱
     * @param defaultValue 當欄位無法成功轉成double時傳的值
     */
    public static double getAsDouble(JsonNode jsonNode, String fieldName, double defaultValue) {
        return optionalGetJsonNode(jsonNode, fieldName)
                .map(node -> node.asDouble(defaultValue))
                .orElse(defaultValue);
    }

    /**
     * <ul>
     * <li>使用{@link JsonNode#asDouble(double)}取得double值</li>
     * <li>會自動轉換非數值欄位，不需要自動轉換請用{@link JsonUtils#getDouble(JsonNode, String)}</li>
     * <li>當無法成功轉成double時回傳0</li>
     * </ul>
     *
     * @param jsonNode  要取值的節點
     * @param fieldName 要取值的欄位名稱
     */
    public static double getAsDouble(JsonNode jsonNode, String fieldName) {
        return getAsDouble(jsonNode, fieldName, 0);
    }

    /**
     * <ul>
     * <li>使用{@link JsonNode#decimalValue()}取得decimal值</li>
     * <li>不會自動轉換非數值欄位</li>
     * <li>{@link JsonUtils#getJsonNode(JsonNode, String)}為null或非數值欄位回傳{@code defaultValue}</li>
     * </ul>
     *
     * @param jsonNode     要取值的節點
     * @param fieldName    要取值的欄位名稱
     * @param defaultValue {@link JsonUtils#getJsonNode(JsonNode, String)}為null或非數值欄位的回傳值
     */
    public static BigDecimal getDecimal(JsonNode jsonNode, String fieldName, BigDecimal defaultValue) {
        return optionalGetJsonNode(jsonNode, fieldName).filter(JsonNode::isNumber)
                .map(JsonNode::decimalValue)
                .orElse(defaultValue);
    }

    /**
     * <ul>
     * <li>使用{@link JsonNode#decimalValue()}取得decimal值</li>
     * <li>不會自動轉換非數值欄位，需要自動轉換請用{@link JsonUtils#getAsDecimal(JsonNode, String)}</li>
     * <li>{@link JsonUtils#getJsonNode(JsonNode, String)}為null或非數值欄位回傳{@link BigDecimal#ZERO}</li>
     * </ul>
     *
     * @param jsonNode  要取值的節點
     * @param fieldName 要取值的欄位名稱
     */
    public static BigDecimal getDecimal(JsonNode jsonNode, String fieldName) {
        return getDecimal(jsonNode, fieldName, BigDecimal.ZERO);
    }

    /**
     * <ul>
     * <li>使用{@link JsonNode#decimalValue()}取得decimal值</li>
     * <li>不會自動轉換非數值欄位，需要自動轉換請用{@link JsonUtils#getAsDecimal(JsonNode, String)}</li>
     * <li>{@link JsonUtils#getJsonNode(JsonNode, String)}為null或非數值欄位回傳null</li>
     * </ul>
     *
     * @param jsonNode  要取值的節點
     * @param fieldName 要取值的欄位名稱
     */
    public static BigDecimal getDecimalWithNull(JsonNode jsonNode, String fieldName) {
        return getDecimal(jsonNode, fieldName, null);
    }

    /**
     * <ul>
     * <li>數值欄位使用{@link JsonNode#decimalValue()}取得decimal值，非數值欄位使用{@link JsonUtils#canConvertDouble(JsonNode)}為true時回傳{@code BigDecimal.valueOf(jsonNode.get(fieldName)}，其餘情況回傳defaultValue
     * <li>會自動轉換非數值欄位，不需要自動轉換請用{@link JsonUtils#getDecimal(JsonNode, String)}</li>
     * <li>當無法成功轉成decimal時回傳{@code defaultValue}</li>
     * </ul>
     *
     * @param jsonNode     要取值的節點
     * @param fieldName    要取值的欄位名稱
     * @param defaultValue 當欄位無法成功轉成decimal時傳的值
     */
    public static BigDecimal getAsDecimal(JsonNode jsonNode, String fieldName, BigDecimal defaultValue) {
        return optionalGetJsonNode(jsonNode, fieldName)
                .map(node -> {
                    if (node.isNumber()) {
                        return node.decimalValue();
                    }
                    if (canConvertDouble(node)) {
                        return BigDecimal.valueOf(node.asDouble());
                    }
                    return defaultValue;
                })
                .orElse(defaultValue);
    }

    public static boolean canConvertDouble(JsonNode node) {
        return !(node.asDouble(0) == 0) || !(node.asDouble(1) == 1);
    }

    /**
     * <ul>
     * <li>使用{@link JsonUtils#getAsDecimal(JsonNode, String, BigDecimal)}取得{@link BigDecimal}</li>
     * <li>會自動轉換非數值欄位，不需要自動轉換請用{@link JsonUtils#getDecimal(JsonNode, String)}</li>
     * <li>當無法成功轉成decimal時回傳{@link BigDecimal#ZERO}</li>
     * </ul>
     *
     * @param jsonNode  要取值的節點
     * @param fieldName 要取值的欄位名稱
     */
    public static BigDecimal getAsDecimal(JsonNode jsonNode, String fieldName) {
        return getAsDecimal(jsonNode, fieldName, BigDecimal.ZERO);
    }

    /**
     * <ul>
     * <li>使用{@link JsonUtils#getAsDecimal(JsonNode, String, BigDecimal)}取得{@link BigDecimal}</li>
     * <li>會自動轉換非數值欄位，不需要自動轉換請用{@link JsonUtils#getDecimal(JsonNode, String)}</li>
     * <li>當無法成功轉成decimal時回傳{@link BigDecimal#ZERO}</li>
     * </ul>
     *
     * @param jsonNode  要取值的節點
     * @param fieldName 要取值的欄位名稱
     */
    public static BigDecimal getAsDecimalWithNull(JsonNode jsonNode, String fieldName) {
        return getAsDecimal(jsonNode, fieldName, null);
    }

    public static JsonNode copyJsonNode(JsonNode jsonNode) {
        return toJsonNode(jsonNode.toString());
    }
}
