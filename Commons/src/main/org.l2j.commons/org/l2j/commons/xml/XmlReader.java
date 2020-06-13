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

import org.l2j.commons.util.FilterUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
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
import java.util.function.Consumer;
import java.util.function.Predicate;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * @author Zoey76
 * @author JoeAlisson
 */
public abstract class XmlReader extends XmlParser {

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

    protected void releaseResources(){
        documentBuilder = null;
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

    /**
     * Parses a single XML file.<br>
     * If the file was successfully parsed, call {@link #parseDocument(Document, File)} for the parsed document.<br>
     * <b>Validation is enforced.</b>
     * @param file the XML file to parse.
     */
    protected void parseFile(File file) {
        if (!FilterUtil.xmlFile(file)) {
            LOGGER.warn("Could not parse {} is not a file or it doesn't exist!", file);
            return;
        }

        try {
            if(isNull(documentBuilder)){
                createDocumentBuilder();
            }
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
    protected boolean parseDirectory(File dir, boolean recursive) {
        if (!dir.exists()) {
            LOGGER.warn("Folder {} doesn't exist!", dir.getAbsolutePath());
            return false;
        }

        final File[] listOfFiles = dir.listFiles();
        if(nonNull(listOfFiles)) {
            for (File f : listOfFiles) {
                if (recursive && f.isDirectory()) {
                    parseDirectory(f, recursive);
                } else if (FilterUtil.xmlFile(f.toPath())) {
                    parseFile(f);
                }
            }
        }
        return true;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * Executes action for each child of node
     */
    protected void forEach(Node node, Consumer<Node> action)
    {
        forEach(node, a -> true, action);
    }

    /**
     * Executes action for each child that matches nodeName
     */
    protected void forEach(Node node, String nodeName, Consumer<Node> action)
    {
        forEach(node, innerNode -> nodeName.equalsIgnoreCase(innerNode.getNodeName()), action);
    }

    /**
     * Executes action for each child of node if matches the filter specified
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


    protected abstract Path getSchemaFilePath();

    /**
     * This method can be used to load/reload the data.<br>
     * It's highly recommended to clear the data storage, either the list or map.
     */
    public abstract void load();

    /**
     * Abstract method that when implemented will parse the current document.<br>
     * Is expected to be call from {@link #parseFile(File)}.
     * @param doc the current document to parse
     * @param f the current file
     */
    protected abstract void parseDocument(Document doc, File f);

    /**
     * Simple XML error handler.
     * @author Zoey76
     */
    static class XMLErrorHandler implements ErrorHandler {
        @Override
        public void warning(SAXParseException e) throws SAXParseException {
            throw e;
        }

        @Override
        public void error(SAXParseException e) throws SAXParseException {
            throw e;
        }

        @Override
        public void fatalError(SAXParseException e) throws SAXParseException {
            throw e;
        }
    }
}
