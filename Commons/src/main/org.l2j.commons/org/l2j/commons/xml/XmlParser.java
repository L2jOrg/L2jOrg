/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.commons.xml;

import io.github.joealisson.primitive.ArrayIntList;
import io.github.joealisson.primitive.Containers;
import io.github.joealisson.primitive.IntList;
import io.github.joealisson.primitive.IntSet;
import org.l2j.commons.util.StreamUtil;
import org.l2j.commons.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.Objects.nonNull;
import static org.l2j.commons.util.Util.isNotEmpty;

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
    protected float parseFloat(Node node, float defaultValue) {
        return nonNull(node) ? Float.parseFloat(node.getNodeValue()) : defaultValue;
    }

    /**
     * Parses a float value.
     * @param node the node to parse
     * @return if the node is not null, the value of the parsed node, otherwise 0
     */
    protected float parseFloat(Node node) {
        return parseFloat(node, 0);
    }

    /**
     * Parses a float value.
     * @param attrs the attributes
     * @param name the name of the attribute to parse
     * @return if the node is not null, the value of the parsed node, otherwise 0
     */
    protected float parseFloat(NamedNodeMap attrs, String name) {
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
    protected double parseDouble(Node node, double defaultValue) {
        return nonNull(node) ? Double.parseDouble(node.getNodeValue()) : defaultValue;
    }

    /**
     * Parses a double value.
     * @param node the node to parse
     * @return if the node is not null, the value of the parsed node, otherwise null
     */
    protected double parseDouble(Node node) {
        return parseDouble(node, 0d);
    }

    /**
     * Parses a double value.
     * @param attrs the attributes
     * @param name the name of the attribute to parse
     * @return if the node is not null, the value of the parsed node, otherwise null
     */
    protected double parseDouble(NamedNodeMap attrs, String name) {
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
        return parseEnumSet(attrs.getNamedItem(name), enumClass);
    }

    protected <T extends Enum<T>> EnumSet<T> parseEnumSet(Node node, Class<T> enumClass) {
        if(nonNull(node)) {
            var value = isNotEmpty(node.getNodeValue()) ? node.getNodeValue() : node.getTextContent();
            return StreamUtil.collectToEnumSet(enumClass, Arrays.stream(value.split("\\s")).map(e -> Enum.valueOf(enumClass, e)));
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

    protected IntSet parseIntSet(NamedNodeMap attrs, String name) {
        return parseIntSet(attrs.getNamedItem(name));
    }

    protected IntSet parseIntSet(Node node) {
        if(nonNull(node)) {
            var value = isNotEmpty(node.getNodeValue()) ? node.getNodeValue() : node.getTextContent();
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
