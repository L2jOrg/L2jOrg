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
package org.l2j.authserver.data.xml;

import io.github.joealisson.primitive.HashIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.commons.xml.XmlReader;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.File;
import java.nio.file.Path;

public class ServerNameReader extends XmlReader {

    private IntMap<String> serverNames;

    public ServerNameReader() {
        serverNames = new HashIntMap<>();
        load();
    }

    public IntMap<String> getServerNames() {
        return serverNames;
    }

    @Override
    protected Path getSchemaFilePath() {
        return Path.of("servername.xsd");
    }

    @Override
    public void load() {
        parseFile(new File("servername.xml"));
        releaseResources();
    }

    @Override
    protected void parseDocument(Document doc, File f) {
        forEach(doc, "servers_list", list -> forEach(list, "server", this::parseServerName));
    }

    private void parseServerName(Node serverNameNode) {
        var attrs = serverNameNode.getAttributes();
        serverNames.put(parseInteger(attrs, "id"), parseString(attrs, "name"));
    }

    public void cleanUp() {
        serverNames = null;
    }
}
