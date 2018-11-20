package org.l2j.authserver.xml;

import org.l2j.commons.xml.XMLReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.util.HashMap;
import java.util.Map;

public class ServerNameReader extends XMLReader<ServersList> {

    private final Map<Integer, String> serverNames;

    public ServerNameReader() throws JAXBException {
        serverNames = new HashMap<>();
    }

    public Map<Integer, String> getServerNames() {
        return serverNames;
    }

    @Override
    protected void processEntity(ServersList entity) {
        entity.getServer().forEach(info -> serverNames.put(info.getId(), info.getName()));
    }

    @Override
    protected JAXBContext getJAXBContext() throws JAXBException {
        return JAXBContext.newInstance(ServersList.class);
    }

    @Override
    protected String getSchemaFilePath() {
        return "servername.xsd";
    }

    @Override
    protected String[] getXmlFileDirectories() {
        return new String[] { "."};
    }
}
