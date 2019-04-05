package org.l2j.commons.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static java.util.Objects.nonNull;

/**
 * @author Zoey76
 *
 */
public abstract class XmlReader
{
	private static Logger LOGGER = LoggerFactory.getLogger(XmlReader.class);

	private DocumentBuilder documentBuilder;

	protected XmlReader() {
		createDocumentBuilder();
	}

	private void createDocumentBuilder() {
		try {
			Schema schema = loadSchema();

			var factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			factory.setIgnoringComments(true);
			factory.setIgnoringElementContentWhitespace(true);
			if(nonNull(schema)) {
				factory.setSchema(schema);
			} else {
				factory.setValidating(true);
			}

			documentBuilder = factory.newDocumentBuilder();
			documentBuilder.setErrorHandler(new XMLErrorHandler());
		} catch (ParserConfigurationException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
		}
	}

	private Schema loadSchema()  {
		try {
			var schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			var path = getSchemaFilePath();
			if(nonNull(path) && Files.isRegularFile(path)) {
				return schemaFactory.newSchema(path.toFile());
			} else {
				LOGGER.warn("Schema Validation disabled, the path {} is not a file", path);
			}
		} catch (SAXException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
		}
		return null;
	}


	protected abstract Path getSchemaFilePath();

	/**
	 * This method can be used to load/reload the data.<br>
	 * It's highly recommended to clear the data storage, either the list or map.
	 */
	public abstract void load();

	/**
	 * Parses a single XML file.<br>
	 * If the file was successfully parsed, call {@link #parseDocument(Document, File)} for the parsed document.<br>
	 * <b>Validation is enforced.</b>
	 * @param file the XML file to parse.
	 */
	protected void parseFile(File file) {
		if (!FilterUtil.xmlFilter(file)) {
			LOGGER.warn("Could not parse {} is not a file or it doesn't exist!", file);
			return;
		}

		try {
			parseDocument(documentBuilder.parse(file), file);
		}
		catch (SAXParseException e) {
			LOGGER.warn("Could not parse file: " + file.getName()+ " at line: " + e.getLineNumber() + ", column: " + e.getColumnNumber() + " :", e);
		}
		catch (Exception e)
		{
			LOGGER.warn("Could not parse file: " + file.getName(), e);
		}
	}


	boolean parseDirectory(File file) {
		return parseDirectory(file, false);
	}

	/**
	 * Loads all XML files from {@code path} and calls {@link #parseFile(File)} for each one of them.
	 * @param dir the directory object to scan.
	 * @param recursive parses all sub folders if there is.
	 * @return {@code false} if it fails to find the directory, {@code true} otherwise.
	 */
	protected boolean parseDirectory(File dir, boolean recursive)
	{
		if (!dir.exists())
		{
			LOGGER.warn("Folder " + dir.getAbsolutePath() + " doesn't exist!");
			return false;
		}

		final File[] listOfFiles = dir.listFiles();
		for (File f : listOfFiles)
		{
			if (recursive && f.isDirectory())
			{
				parseDirectory(f, recursive);
			}
			else if (FilterUtil.xmlFilter(f.toPath()))
			{
				parseFile(f);
			}
		}
		return true;
	}

	/**
	 * Abstract method that when implemented will parse the current document.<br>
	 * Is expected to be call from {@link #parseFile(File)}.
	 * @param doc the current document to parse
	 * @param f the current file
	 */
	public abstract void parseDocument(Document doc, File f);

	/**
	 * Parses a boolean value.
	 * @param node the node to parse
	 * @param defaultValue the default value
	 * @return if the node is not null, the value of the parsed node, otherwise the default value
	 */
	protected Boolean parseBoolean(Node node, Boolean defaultValue)
	{
		return node != null ? Boolean.valueOf(node.getNodeValue()) : defaultValue;
	}

	/**
	 * Parses a boolean value.
	 * @param node the node to parse
	 * @return if the node is not null, the value of the parsed node, otherwise null
	 */
	protected Boolean parseBoolean(Node node)
	{
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
	protected int parseInt(Node node, Integer defaultValue)
	{
		return node != null ? Integer.decode(node.getNodeValue()) : defaultValue;
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
	protected String parseString(Node node, String defaultValue)
	{
		return node != null ? node.getNodeValue() : defaultValue;
	}

	/**
	 * Parses a string value.
	 * @param node the node to parse
	 * @return if the node is not null, the value of the parsed node, otherwise null
	 */
	protected String parseString(Node node)
	{
		return parseString(node, null);
	}

	/**
	 * Parses a string value.
	 * @param attrs the attributes
	 * @param name the name of the attribute to parse
	 * @return if the node is not null, the value of the parsed node, otherwise null
	 */
	protected String parseString(NamedNodeMap attrs, String name)
	{
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

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
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

	/**
	 * @param node
	 * @return parses all attributes to a Map
	 */
	protected Map<String, Object> parseAttributes(Node node)
	{
		final NamedNodeMap attrs = node.getAttributes();
		final Map<String, Object> map = new LinkedHashMap<>();
		for (int i = 0; i < attrs.getLength(); i++)
		{
			final Node att = attrs.item(i);
			map.put(att.getNodeName(), att.getNodeValue());
		}
		return map;
	}

	/**
	 * Executes action for each child of node
	 * @param node
	 * @param action
	 */
	protected void forEach(Node node, Consumer<Node> action)
	{
		forEach(node, a -> true, action);
	}

	/**
	 * Executes action for each child that matches nodeName
	 * @param node
	 * @param nodeName
	 * @param action
	 */
	protected void forEach(Node node, String nodeName, Consumer<Node> action)
	{
		forEach(node, innerNode -> nodeName.equalsIgnoreCase(innerNode.getNodeName()), action);
	}

	/**
	 * Executes action for each child of node if matches the filter specified
	 * @param node
	 * @param filter
	 * @param action
	 */
	protected void forEach(Node node, Predicate<Node> filter, Consumer<Node> action)
	{
		final NodeList list = node.getChildNodes();
		for (int i = 0; i < list.getLength(); i++)
		{
			final Node targetNode = list.item(i);
			if (filter.test(targetNode))
			{
				action.accept(targetNode);
			}
		}
	}

	/**
	 * @param node
	 * @return {@code true} if the node is an element type, {@code false} otherwise
	 */
	protected static boolean isNode(Node node)
	{
		return node.getNodeType() == Node.ELEMENT_NODE;
	}

	/**
	 * @param node
	 * @return {@code true} if the node is an element type, {@code false} otherwise
	 */
	protected static boolean isText(Node node)
	{
		return node.getNodeType() == Node.TEXT_NODE;
	}

	/**
	 * Simple XML error handler.
	 * @author Zoey76
	 */
	class XMLErrorHandler implements ErrorHandler
	{
		@Override
		public void warning(SAXParseException e) throws SAXParseException
		{
			throw e;
		}

		@Override
		public void error(SAXParseException e) throws SAXParseException
		{
			throw e;
		}

		@Override
		public void fatalError(SAXParseException e) throws SAXParseException
		{
			throw e;
		}
	}
}
