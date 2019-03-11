/*
 * This file is part of the L2J Mobius project.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.script;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class ScriptDocument {
    private static final Logger LOGGER = Logger.getLogger(ScriptDocument.class.getName());
    private final String _name;
    private Document _document;

    public ScriptDocument(String name, InputStream input) {
        _name = name;

        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            final DocumentBuilder builder = factory.newDocumentBuilder();
            _document = builder.parse(input);

        } catch (SAXException sxe) {
            // Error generated during parsing)
            Exception x = sxe;
            if (sxe.getException() != null) {
                x = sxe.getException();
            }
            LOGGER.warning(getClass().getSimpleName() + ": " + x.getMessage());
        } catch (ParserConfigurationException pce) {
            // Parser with specified options can't be built
            LOGGER.log(Level.WARNING, "", pce);
        } catch (IOException ioe) {
            // I/O error
            LOGGER.log(Level.WARNING, "", ioe);
        }
    }

    public Document getDocument() {
        return _document;
    }

    /**
     * @return Returns the _name.
     */
    public String getName() {
        return _name;
    }

    @Override
    public String toString() {
        return _name;
    }
}
