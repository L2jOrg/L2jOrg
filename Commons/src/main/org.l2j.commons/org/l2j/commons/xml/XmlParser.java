/*
 * Copyright © 2019-2021 L2JOrg
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

import java.util.*;

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
     * Parses a boolean value.
     * @param node the node to parse
     * @param defaultValue the default value
     * @return if the node is not null, the value of the parsed node, otherwise the default value
     */
    public boolean parseBoolean(Node node, boolean defaultValue) {
        return node != null ? Boolean.parseBoolean(node.getNodeValue()) : defaultValue;
    }

    /**
     * Parses a boolean value.
     * @param node the node to parse
     * @return if the node is not null, the value of the parsed node, otherwise false
     */
    public boolean parseBoolean(Node node) {
        return parseBoolean(node, false);
    }

    /**
     * Parses a boolean value.
     * @param attrs the attributes
     * @param name the name of the attribute to parse
     * @return if the node is not null, the value of the parsed node, otherwise null
     */
    public boolean parseBoolean(NamedNodeMap attrs, String name)
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
    public boolean parseBoolean(NamedNodeMap attrs, String name, boolean defaultValue)
    {
        return parseBoolean(attrs.getNamedItem(name), defaultValue);
    }

    /**
     * Parses a byte value.
     * @param node the node to parse
     * @param defaultValue the default value
     * @return if the node is not null, the value of the parsed node, otherwise the default value
     */
    public byte parseByte(Node node, byte defaultValue)
    {
        return node != null ? Byte.parseByte(node.getNodeValue()) : defaultValue;
    }

    /**
     * Parses a byte value.
     * @param node the node to parse
     * @return if the node is not null, the value of the parsed node, otherwise null
     */
    public byte parseByte(Node node)
    {
        return parseByte(node, (byte) 0);
    }

    /**
     * Parses a byte value.
     * @param attrs the attributes
     * @param name the name of the attribute to parse
     * @return if the node is not null, the value of the parsed node, otherwise null
     */
    public byte parseByte(NamedNodeMap attrs, String name)
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
    public byte parseByte(NamedNodeMap attrs, String name, byte defaultValue)
    {
        return parseByte(attrs.getNamedItem(name), defaultValue);
    }

    /**
     * Parses a short value.
     * @param node the node to parse
     * @param defaultValue the default value
     * @return if the node is not null, the value of the parsed node, otherwise the default value
     */
    public short parseShort(Node node, short defaultValue)
    {
        return node != null ? Short.decode(node.getNodeValue()) : defaultValue;
    }

    /**
     * Parses a short value.
     * @param node the node to parse
     * @return if the node is not null, the value of the parsed node, otherwise null
     */
    public short parseShort(Node node)
    {
        return parseShort(node, (short) 0);
    }

    /**
     * Parses a short value.
     * @param attrs the attributes
     * @param name the name of the attribute to parse
     * @return if the node is not null, the value of the parsed node, otherwise null
     */
    public short parseShort(NamedNodeMap attrs, String name)
    {
        return parseShort(attrs.getNamedItem(name));
    }

    /**
     * Parses an int value.
     * @param node the node to parse
     * @param defaultValue the default value
     * @return if the node is not null, the value of the parsed node, otherwise the default value
     */
    public int parseInt(Node node, int defaultValue)
    {
        return nonNull(node) ? Integer.decode(Objects.requireNonNullElseGet(node.getNodeValue(), node::getTextContent)) : defaultValue;
    }

    /**
     * parse the node value as int.
     *
     * @return 0 if node is null or the node name not exists.
     */
    public int parseInt(NamedNodeMap node, String name) {
        return parseInt(node, name, 0);
    }

    /**
     * parse the node value as int.
     *
     */
    public int parseInt(NamedNodeMap node, String name, int defaultValue) {
        return nonNull(node) ? parseInt(node.getNamedItem(name), defaultValue) : defaultValue;
    }

    /**
     * Parses an int value.
     * @param node the node to parse
     * @return if the node is not null, the value of the parsed node, otherwise the default value
     */
    public int parseInt(Node node)
    {
        return parseInt(node, -1);
    }

    /**
     * parse the node value as long.
     *
     * @return 0 if node is null or the node name not exists.
     */
    public long parseLong(NamedNodeMap node, String name) {
        return nonNull(node) ? parseLong(node.getNamedItem(name), 0) : 0;
    }

    /**
     * parse the node value as long.
     *
     * @return defaultValue  if node is null or the node name not exists.
     */
    public long parseLong(NamedNodeMap node, String name, long defaultValue) {
        return nonNull(node) ? parseLong(node.getNamedItem(name), defaultValue) : defaultValue;
    }

    public long parseLong(Node node, long defaultValue) {
        return  nonNull(node ) ? Long.decode(node.getNodeValue())  : defaultValue;
    }

    /**
     * Parses a float value.
     * @param node the node to parse
     * @param defaultValue the default value
     * @return if the node is not null, the value of the parsed node, otherwise the default value
     */
    public float parseFloat(Node node, float defaultValue) {
        return nonNull(node) ? Float.parseFloat(node.getNodeValue()) : defaultValue;
    }

    /**
     * Parses a float value.
     * @param node the node to parse
     * @return if the node is not null, the value of the parsed node, otherwise 0
     */
    public float parseFloat(Node node) {
        return parseFloat(node, 0);
    }

    /**
     * Parses a float value.
     * @param attrs the attributes
     * @param name the name of the attribute to parse
     * @return if the node is not null, the value of the parsed node, otherwise 0
     */
    public float parseFloat(NamedNodeMap attrs, String name) {
        return parseFloat(attrs.getNamedItem(name));
    }

    /**
     * Parses a float value.
     * @param attrs the attributes
     * @param name the name of the attribute to parse
     * @param defaultValue the default value
     * @return if the node is not null, the value of the parsed node, otherwise the default value
     */
    public float parseFloat(NamedNodeMap attrs, String name, Float defaultValue)
    {
        return parseFloat(attrs.getNamedItem(name), defaultValue);
    }

    /**
     * Parses a double value.
     * @param node the node to parse
     * @param defaultValue the default value
     * @return if the node is not null, the value of the parsed node, otherwise the default value
     */
    public double parseDouble(Node node, double defaultValue) {
        return nonNull(node) ? Double.parseDouble(node.getNodeValue()) : defaultValue;
    }

    /**
     * Parses a double value.
     * @param node the node to parse
     * @return if the node is not null, the value of the parsed node, otherwise null
     */
    public double parseDouble(Node node) {
        return parseDouble(node, 0d);
    }

    /**
     * Parses a double value.
     * @param attrs the attributes
     * @param name the name of the attribute to parse
     * @return if the node is not null, the value of the parsed node, otherwise null
     */
    public double parseDouble(NamedNodeMap attrs, String name) {
        return parseDouble(attrs.getNamedItem(name));
    }

    /**
     * Parses a double value.
     * @param attrs the attributes
     * @param name the name of the attribute to parse
     * @param defaultValue the default value
     * @return if the node is not null, the value of the parsed node, otherwise the default value
     */
    public double parseDouble(NamedNodeMap attrs, String name, double defaultValue)
    {
        return parseDouble(attrs.getNamedItem(name), defaultValue);
    }

    /**
     * Parses a string value.
     * @param node the node to parse
     * @param defaultValue the default value
     * @return if the node is not null, the value of the parsed node, otherwise the default value
     */
    public String parseString(Node node, String defaultValue)
    {
        return node != null ? node.getNodeValue() : defaultValue;
    }

    /**
     * Parses a string value.
     * @param node the node to parse
     * @return if the node is not null, the value of the parsed node, otherwise null
     */
    public String parseString(Node node) {
        return parseString(node, null);
    }

    /**
     * Parses a string value.
     * @param attrs the attributes
     * @param name the name of the attribute to parse
     * @return if the node is not null, the value of the parsed node, otherwise null
     */
    public String parseString(NamedNodeMap attrs, String name) {
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
    public  <T extends Enum<T>> T parseEnum(Node node, Class<T> clazz, T defaultValue) {
        if (node == null) {
            return defaultValue;
        }
        var value = isNotEmpty(node.getNodeValue()) ? node.getNodeValue() : node.getTextContent();
        try {
            return Enum.valueOf(clazz, value);
        }
        catch (IllegalArgumentException e) {
            LOGGER.warn("Invalid value specified for node: {} specified value: {} should be enum value of '{}' using default value: {}",node.getNodeName(), value, clazz.getSimpleName(), defaultValue);
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
    public  <T extends Enum<T>> T parseEnum(Node node, Class<T> clazz)
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
    public  <T extends Enum<T>> T parseEnum(NamedNodeMap attrs, Class<T> clazz, String name)
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
    public  <T extends Enum<T>> T parseEnum(NamedNodeMap attrs, Class<T> clazz, String name, T defaultValue)
    {
        return parseEnum(attrs.getNamedItem(name), clazz, defaultValue);
    }

    public  <T extends Enum<T>> EnumSet<T> parseEnumSet(NamedNodeMap attrs, Class<T> enumClass, String name) {
        return parseEnumSet(attrs.getNamedItem(name), enumClass);
    }

    public  <T extends Enum<T>> EnumSet<T> parseEnumSet(Node node, Class<T> enumClass) {
        if(nonNull(node)) {
            var value = isNotEmpty(node.getNodeValue()) ? node.getNodeValue() : node.getTextContent();
            return StreamUtil.collectToEnumSet(enumClass, Arrays.stream(value.split("\\s")).map(e -> Enum.valueOf(enumClass, e)));
        }
        return EnumSet.noneOf(enumClass);
    }

    /**
     * @return parses all attributes to a Map
     */
    public Map<String, Object> parseAttributes(Node node) {
        final NamedNodeMap attrs = node.getAttributes();
        final Map<String, Object> map = new LinkedHashMap<>();
        for (int i = 0; i < attrs.getLength(); i++) {
            final Node att = attrs.item(i);
            map.put(att.getNodeName(), att.getNodeValue());
        }
        return map;
    }

    public IntSet parseIntSet(NamedNodeMap attrs, String name) {
        return parseIntSet(attrs.getNamedItem(name));
    }

    public IntSet parseIntSet(Node node) {
        if(nonNull(node)) {
            var value = isNotEmpty(node.getNodeValue()) ? node.getNodeValue() : node.getTextContent();
            return StreamUtil.collectToSet(Arrays.stream(value.split("\\s")).filter(Util::isInteger).mapToInt(Integer::parseInt));
        }
        return Containers.emptyIntSet();
    }

    public IntList parseIntList(Node node) {
        if(nonNull(node)) {
            var value = isNotEmpty(node.getNodeValue()) ? node.getNodeValue() : node.getTextContent();
            var values = value.split("\\s");
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

    public int[] parseIntArray(NamedNodeMap attrs, String name) {
        return parseIntArray(attrs.getNamedItem(name));
    }

    public int[] parseIntArray(Node node ) {
        if(nonNull(node)) {
            var nodeValue = isNotEmpty(node.getNodeValue()) ? node.getNodeValue() : node.getTextContent();
            var values = nodeValue.split("\\s");
            int[] array = new int[values.length];
            for (int i = 0; i < array.length; i++) {
                if(Util.isInteger(values[i])) {
                    array[i] = Integer.parseInt(values[i]);
                }
            }
            return array;
        }
        return Util.INT_ARRAY_EMPTY;
    }
}
