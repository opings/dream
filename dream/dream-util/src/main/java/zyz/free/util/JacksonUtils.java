package zyz.free.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * jackson 工具类
 */
public class JacksonUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 八大基本数据类型
     */
    private static final List<Class> baseType = Arrays.asList(new Class[]{Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class, Character.class, Boolean.class});

    /**
     * 对象转 json，用于引用类型，包括 json 格式的字符串
     *
     * @param obj
     * @return
     * @throws JsonProcessingException
     */
    public static String toJson(Object obj){
        try{
            toJsonCheckType(obj);
            if (obj instanceof String) {
                obj = toJsonNode((String) obj);
                return obj.toString();
            }
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            // ignore
        }
        return null;
    }

    /**
     * 对象转 json，保留缩进，用于引用类型，包括 json 格式的字符串
     *
     * @param obj
     * @return
     * @throws JsonProcessingException
     */
    public static String toPrettyJson(Object obj) throws JsonProcessingException {
        toJsonCheckType(obj);
        if (obj instanceof String) {
            obj = toJsonNode((String) obj);
            return ((JsonNode) obj).toPrettyString();
        }
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
    }

    /**
     * toJson 检测，null 和 八大基本数据类型检测
     *
     * @param obj
     */
    private static void toJsonCheckType(Object obj) {
        if (obj == null) {
            throw new RuntimeException("obj -> null, 不支持转换为 json");
        }
        if (baseType.contains(obj.getClass())) {
            throw new RuntimeException(String.format("obj -> %s, %s 类型不支持转换为 json", obj, obj.getClass().getSimpleName()));
        }
    }

    /**
     * json 转对象
     *
     * @param json
     * @param clazz
     * @param <T>
     * @return
     * @throws JsonProcessingException
     */
    public static <T> T toObject(String json, Class<T> clazz) throws JsonProcessingException {
        return objectMapper.readValue(json, clazz);
    }

    /**
     * json 转集合
     *
     * @param json
     * @param clazz
     * @param <T>
     * @return
     * @throws JsonProcessingException
     */
    public static <T> List<T> toList(String json, Class<T> clazz) throws JsonProcessingException {
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(List.class, clazz);
        return objectMapper.readValue(json, javaType);
    }

    /**
     * json 字符串转 JsonNode，即 JsonObject
     *
     * @param json
     * @return
     * @throws JsonProcessingException
     */
    public static JsonNode toJsonNode(String json) throws JsonProcessingException {
        JsonNode jsonNode = objectMapper.readTree(json);
        if (!jsonNode.isObject() && !jsonNode.isArray()) {
            throw new RuntimeException("json -> " + json + ", 不是合法的 Json 或 JsonArray");
        }
        return jsonNode;
    }

    /**
     * json 字符串转 JsonNode 集合，即 JsonObject 集合
     *
     * @param json
     * @return
     * @throws JsonProcessingException
     */
    public static List<JsonNode> toJsonNodeList(String json) throws JsonProcessingException {
        List<JsonNode> list = new ArrayList<>();
        JsonNode jsonNode = toJsonNode(json);
        if (jsonNode.isArray()) {
            jsonNode.iterator().forEachRemaining(e -> list.add(e));
        } else {
            throw new RuntimeException("json -> " + json + ", 不是合法的 ArrayNode");
        }
        return list;
    }

    /**
     * json 字符串转 ObjectNode，即 JsonObject
     *
     * @param json
     * @return
     * @throws JsonProcessingException
     */
    public static ObjectNode toObjectNode(String json) throws JsonProcessingException {
        JsonNode jsonNode = toJsonNode(json);
        if (jsonNode.isObject()) {
            return (ObjectNode) jsonNode;
        }
        throw new RuntimeException("json -> " + json + ", 不是合法的 ObjectNode");
    }

    /**
     * json 字符串转 ArrayNode，即 JsonArray
     *
     * @param json
     * @return
     * @throws JsonProcessingException
     */
    public static ArrayNode toArrayNode(String json) throws JsonProcessingException {
        JsonNode jsonNode = toJsonNode(json);
        if (jsonNode.isArray()) {
            return (ArrayNode) jsonNode;
        }
        throw new RuntimeException("json -> " + json + ", 不是合法的 ArrayNode");
    }

}