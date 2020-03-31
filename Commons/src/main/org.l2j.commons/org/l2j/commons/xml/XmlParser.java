package org.l2j.commons.xml;

import io.github.joealisson.primitive.*;
import org.l2j.commons.util.StreamUtil;
import org.l2j.commons.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * @author JoeAlisson
 */
public class XmlParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(XmlParser.class);

    /**
     * @return {@code true} if the node is an element type, {@code false} otherwise
     */
    protected static boolean isNode(Node node) {
        return node.getNodeType() == Node.ELEMENT_NODE;
    }

    /**
     * @return {@code true} if the node is an element type, {@code false} otherwise
     */
    protected static boolean isText(Node node) {
        return node.getNodeType() == Node.TEXT_NODE;
    }

    /**
     * Parses a boolean value.
     * @param node the node to parse
     * @param defaultValue the default value
     * @return if the node is not null, the value of the parsed node, otherwise the default value
     */
    protected Boolean parseBoolean(Node node, Boolean defaultValue) {
        return node != null ? Boolean.valueOf(node.getNodeValue()) : defaultValue;
    }

    /**
     * Parses a boolean value.
     * @param node the node to parse
     * @return if the node is not null, the value of the parsed node, otherwise null
     */
    protected Boolean parseBoolean(Node node) {
        return parseBoolean(node, null);
    }

    /**
     * Parses a boolean value.
     * @param attrs the attributes
     * @param name the name of the attribute to parse
     * @return if the node is not null, the value of the parsed node, otherwise null
     */
    protected Boolean parseBoolean(NamedNodeMap attrs, String name)
    {
        return parseBoolean(attrs.getNamedItem(name));
    }

    /**
     * Parses a boolean value.
     * @param attrs the attributes
     * @param name the name of the attribute to parse
     * @param defaultValue the default value
     * @return if the node is not null, the value of the parsed node, otherwise the default value
     */
    protected Boolean parseBoolean(NamedNodeMap attrs, String name, Boolean defaultValue)
    {
        return parseBoolean(attrs.getNamedItem(name), defaultValue);
    }

    /**
     * Parses a byte value.
     * @param node the node to parse
     * @param defaultValue the default value
     * @return if the node is not null, the value of the parsed node, otherwise the default value
     */
    protected Byte parseByte(Node node, Byte defaultValue)
    {
        return node != null ? Byte.decode(node.getNodeValue()) : defaultValue;
    }

    /**
     * Parses a byte value.
     * @param node the node to parse
     * @return if the node is not null, the value of the parsed node, otherwise null
     */
    protected Byte parseByte(Node node)
    {
        return parseByte(node, null);
    }

    /**
     * Parses a byte value.
     * @param attrs the attributes
     * @param name the name of the attribute to parse
     * @return if the node is not null, the value of the parsed node, otherwise null
     */
    protected Byte parseByte(NamedNodeMap attrs, String name)
    {
        return parseByte(attrs.getNamedItem(name));
    }

    /**
     * Parses a byte value.
     * @param attrs the attributes
     * @param name the name of the attribute to parse
     * @param defaultValue the default value
     * @return if the node is not null, the value of the parsed node, otherwise the default value
     */
    protected Byte parseByte(NamedNodeMap attrs, String name, Byte defaultValue)
    {
        return parseByte(attrs.getNamedItem(name), defaultValue);
    }

    /**
     * Parses a short value.
     * @param node the node to parse
     * @param defaultValue the default value
     * @return if the node is not null, the value of the parsed node, otherwise the default value
     */
    protected Short parseShort(Node node, Short defaultValue)
    {
        return node != null ? Short.decode(node.getNodeValue()) : defaultValue;
    }

    /**
     * Parses a short value.
     * @param node the node to parse
     * @return if the node is not null, the value of the parsed node, otherwise null
     */
    protected Short parseShort(Node node)
    {
        return parseShort(node, null);
    }

    /**
     * Parses a short value.
     * @param attrs the attributes
     * @param name the name of the attribute to parse
     * @return if the node is not null, the value of the parsed node, otherwise null
     */
    protected Short parseShort(NamedNodeMap attrs, String name)
    {
        return parseShort(attrs.getNamedItem(name));
    }

    /**
     * Parses a short value.
     * @param attrs the attributes
     * @param name the name of the attribute to parse
     * @param defaultValue the default value
     * @return if the node is not null, the value of the parsed node, otherwise the default value
     */
    protected Short parseShort(NamedNodeMap attrs, String name, Short defaultValue)
    {
        return parseShort(attrs.getNamedItem(name), defaultValue);
    }

    /**
     * Parses an int value.
     * @param node the node to parse
     * @param defaultValue the default value
     * @return if the node is not null, the value of the parsed node, otherwise the default value
     */
    protected int parseInt(Node node, int defaultValue)
    {
        return node != null ? Integer.decode(node.getNodeValue()) : defaultValue;
    }

    /**
     * parse the node value as int.
     *
     * @return -1 if node is null or the node name not exists.
     */
    protected int parseInt(NamedNodeMap node, String name) {
        return parseInt(node, name, -1);
    }

    /**
     * parse the node value as int.
     *
     */
    protected int parseInt(NamedNodeMap node, String name, int defaultValue) {
        return nonNull(node) ? parseInt(node.getNamedItem(name), defaultValue) : defaultValue;
    }

    /**
     * Parses an int value.
     * @param node the node to parse
     * @return if the node is not null, the value of the parsed node, otherwise the default value
     */
    protected int parseInt(Node node)
    {
        return parseInt(node, -1);
    }

    /**
     * parse the node value as long.
     *
     * @return -1 if node is null or the node name not exists.
     */
    protected long parselong(NamedNodeMap node, String name) {
        return nonNull(node) ? parselong(node.getNamedItem(name), -1L) : -1L;
    }

    protected long parselong(Node node, long defaultValue) {
        return  nonNull(node ) ? Long.decode(node.getNodeValue())  : defaultValue;
    }

    /**
     * Parses an integer value.
     * @param node the node to parse
     * @param defaultValue the default value
     * @return if the node is not null, the value of the parsed node, otherwise the default value
     */
    protected Integer parseInteger(Node node, Integer defaultValue)
    {
        return node != null ? Integer.decode(node.getNodeValue()) : defaultValue;
    }

    /**
     * Parses an integer value.
     * @param node the node to parse
     * @return if the node is not null, the value of the parsed node, otherwise null
     */
    protected Integer parseInteger(Node node)
    {
        return parseInteger(node, null);
    }

    /**
     * Parses an integer value.
     * @param attrs the attributes
     * @param name the name of the attribute to parse
     * @return if the node is not null, the value of the parsed node, otherwise null
     */
    protected Integer parseInteger(NamedNodeMap attrs, String name)
    {
        return parseInteger(attrs.getNamedItem(name));
    }

    /**
     * Parses an integer value.
     * @param attrs the attributes
     * @param name the name of the attribute to parse
     * @param defaultValue the default value
     * @return if the node is not null, the value of the parsed node, otherwise the default value
     */
    protected Integer parseInteger(NamedNodeMap attrs, String name, Integer defaultValue)
    {
        return parseInteger(attrs.getNamedItem(name), defaultValue);
    }

    /**
     * Parses a long value.
     * @param node the node to parse
     * @param defaultValue the default value
     * @return if the node is not null, the value of the parsed node, otherwise the default value
     */
    protected Long parseLong(Node node, Long defaultValue)
    {
        return node != null ? Long.decode(node.getNodeValue()) : defaultValue;
    }

    /**
     * Parses a long value.
     * @param node the node to parse
     * @return if the node is not null, the value of the parsed node, otherwise null
     */
    protected Long parseLong(Node node)
    {
        return parseLong(node, null);
    }

    /**
     * Parses a long value.
     * @param attrs the attributes
     * @param name the name of the attribute to parse
     * @return if the node is not null, the value of the parsed node, otherwise null
     */
    protected Long parseLong(NamedNodeMap attrs, String name)
    {
        return parseLong(attrs.getNamedItem(name));
    }

    /**
     * Parses a long value.
     * @param attrs the attributes
     * @param name the name of the attribute to parse
     * @param defaultValue the default value
     * @return if the node is not null, the value of the parsed node, otherwise the default value
     */
    protected Long parseLong(NamedNodeMap attrs, String name, Long defaultValue)
    {
        return parseLong(attrs.getNamedItem(name), defaultValue);
    }

    /**
     * Parses a float value.
     * @param node the node to parse
     * @param defaultValue the default value
     * @return if the node is not null, the value of the parsed node, otherwise the default value
     */
    protected Float parseFloat(Node node, Float defaultValue)
    {
        return node != null ? Float.valueOf(node.getNodeValue()) : defaultValue;
    }

    /**
     * Parses a float value.
     * @param node the node to parse
     * @return if the node is not null, the value of the parsed node, otherwise null
     */
    protected Float parseFloat(Node node)
    {
        return parseFloat(node, null);
    }

    /**
     * Parses a float value.
     * @param attrs the attributes
     * @param name the name of the attribute to parse
     * @return if the node is not null, the value of the parsed node, otherwise null
     */
    protected Float parseFloat(NamedNodeMap attrs, String name)
    {
        return parseFloat(attrs.getNamedItem(name));
    }

    /**
     * Parses a float value.
     * @param attrs the attributes
     * @param name the name of the attribute to parse
     * @param defaultValue the default value
     * @return if the node is not null, the value of the parsed node, otherwise the default value
     */
    protected Float parseFloat(NamedNodeMap attrs, String name, Float defaultValue)
    {
        return parseFloat(attrs.getNamedItem(name), defaultValue);
    }

    /**
     * Parses a double value.
     * @param node the node to parse
     * @param defaultValue the default value
     * @return if the node is not null, the value of the parsed node, otherwise the default value
     */
    protected Double parseDouble(Node node, Double defaultValue)
    {
        return node != null ? Double.valueOf(node.getNodeValue()) : defaultValue;
    }

    /**
     * Parses a double value.
     * @param node the node to parse
     * @return if the node is not null, the value of the parsed node, otherwise null
     */
    protected Double parseDouble(Node node)
    {
        return parseDouble(node, null);
    }

    /**
     * Parses a double value.
     * @param attrs the attributes
     * @param name the name of the attribute to parse
     * @return if the node is not null, the value of the parsed node, otherwise null
     */
    protected Double parseDouble(NamedNodeMap attrs, String name)
    {
        return parseDouble(attrs.getNamedItem(name));
    }

    /**
     * Parses a double value.
     * @param attrs the attributes
     * @param name the name of the attribute to parse
     * @param defaultValue the default value
     * @return if the node is not null, the value of the parsed node, otherwise the default value
     */
    protected Double parseDouble(NamedNodeMap attrs, String name, Double defaultValue)
    {
        return parseDouble(attrs.getNamedItem(name), defaultValue);
    }

    /**
     * Parses a string value.
     * @param node the node to parse
     * @param defaultValue the default value
     * @return if the node is not null, the value of the parsed node, otherwise the default value
     */
    private String parseString(Node node, String defaultValue)
    {
        return node != null ? node.getNodeValue() : defaultValue;
    }

    /**
     * Parses a string value.
     * @param node the node to parse
     * @return if the node is not null, the value of the parsed node, otherwise null
     */
    protected String parseString(Node node) {
        return parseString(node, null);
    }

    /**
     * Parses a string value.
     * @param attrs the attributes
     * @param name the name of the attribute to parse
     * @return if the node is not null, the value of the parsed node, otherwise null
     */
    protected String parseString(NamedNodeMap attrs, String name) {
        return parseString(attrs.getNamedItem(name));
    }

    /**
     * Parses a string value.
     * @param attrs the attributes
     * @param name the name of the attribute to parse
     * @param defaultValue the default value
     * @return if the node is not null, the value of the parsed node, otherwise the default value
     */
    protected String parseString(NamedNodeMap attrs, String name, String defaultValue)
    {
        return parseString(attrs.getNamedItem(name), defaultValue);
    }

    /**
     * Parses an enumerated value.
     * @param <T> the enumerated type
     * @param node the node to parse
     * @param clazz the class of the enumerated
     * @param defaultValue the default value
     * @return if the node is not null and the node value is valid the parsed value, otherwise the default value
     */
    protected <T extends Enum<T>> T parseEnum(Node node, Class<T> clazz, T defaultValue)
    {
        if (node == null)
        {
            return defaultValue;
        }

        try
        {
            return Enum.valueOf(clazz, node.getNodeValue());
        }
        catch (IllegalArgumentException e)
        {
            LOGGER.warn("Invalid value specified for node: " + node.getNodeName() + " specified value: " + node.getNodeValue() + " should be enum value of \"" + clazz.getSimpleName() + "\" using default value: " + defaultValue);
            return defaultValue;
        }
    }

    /**
     * Parses an enumerated value.
     * @param <T> the enumerated type
     * @param node the node to parse
     * @param clazz the class of the enumerated
     * @return if the node is not null and the node value is valid the parsed value, otherwise null
     */
    protected <T extends Enum<T>> T parseEnum(Node node, Class<T> clazz)
    {
        return parseEnum(node, clazz, null);
    }

    /**
     * Parses an enumerated value.
     * @param <T> the enumerated type
     * @param attrs the attributes
     * @param clazz the class of the enumerated
     * @param name the name of the attribute to parse
     * @return if the node is not null and the node value is valid the parsed value, otherwise null
     */
    protected <T extends Enum<T>> T parseEnum(NamedNodeMap attrs, Class<T> clazz, String name)
    {
        return parseEnum(attrs.getNamedItem(name), clazz);
    }

    /**
     * Parses an enumerated value.
     * @param <T> the enumerated type
     * @param attrs the attributes
     * @param clazz the class of the enumerated
     * @param name the name of the attribute to parse
     * @param defaultValue the default value
     * @return if the node is not null and the node value is valid the parsed value, otherwise the default value
     */
    protected <T extends Enum<T>> T parseEnum(NamedNodeMap attrs, Class<T> clazz, String name, T defaultValue)
    {
        return parseEnum(attrs.getNamedItem(name), clazz, defaultValue);
    }

    protected <T extends Enum<T>> EnumSet<T> parseEnumSet(NamedNodeMap attrs, Class<T> enumClass, String name) {
        var items = attrs.getNamedItem(name);


        if(nonNull(items) && Util.isNotEmpty(items.getNodeValue())) {
            try {
                return StreamUtil.collectToEnumSet(enumClass, Arrays.stream(items.getNodeValue().split(" ")).map(e -> Enum.valueOf(enumClass, e)));
            } catch (Exception e) {
                LOGGER.warn(e.getMessage(), e);
            }
        }
        return EnumSet.noneOf(enumClass);

    }

    /**
     * @return parses all attributes to a Map
     */
    protected Map<String, Object> parseAttributes(Node node) {
        final NamedNodeMap attrs = node.getAttributes();
        final Map<String, Object> map = new LinkedHashMap<>();
        for (int i = 0; i < attrs.getLength(); i++) {
            final Node att = attrs.item(i);
            map.put(att.getNodeName(), att.getNodeValue());
        }
        return map;
    }

    protected IntSet parseIntSet(Node node) {
        if(nonNull(node)) {
            var value = nonNull(node.getNodeValue()) ? node.getNodeValue() : node.getTextContent();
            return StreamUtil.collectToSet(Arrays.stream(value.split("\\s")).filter(Util::isInteger).mapToInt(Integer::parseInt));
        }
        return Containers.emptyIntSet();
    }

    protected IntList parseIntList(Node node) {
        if(nonNull(node)) {
            var values = node.getNodeValue().split("[,;]");
            var list = new ArrayIntList(values.length);
            for (String val :  values) {
                if(Util.isInteger(val)) {
                    list.add(Integer.parseInt(val));
                }
            }
            return list;
        }
        return Containers.emptyList();
    }
}
