package org.l2j.authserver.data.xml;

import io.github.joealisson.primitive.maps.IntObjectMap;
import io.github.joealisson.primitive.maps.impl.HashIntObjectMap;
import org.l2j.commons.xml.XmlReader;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.File;
import java.nio.file.Path;

public class ServerNameReader extends XmlReader {

    private IntObjectMap<String> serverNames;

    public ServerNameReader() {
        serverNames = new HashIntObjectMap<>();
        load();
    }

    public IntObjectMap<String> getServerNames() {
        return serverNames;
    }

    @Override
    protected Path getSchemaFilePath() {
        return Path.of("servername.xsd");
    }

    @Override
    public void load() {
        parseFile(new File("servername.xml"));
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
