package org.l2j.authserver.data.xml;

import io.github.joealisson.primitive.maps.IntObjectMap;
import io.github.joealisson.primitive.maps.impl.HashIntObjectMap;
import org.l2j.commons.xml.XMLReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

public class ServerNameReader extends XMLReader<ServersList> {

    private final IntObjectMap<String> serverNames;

    public ServerNameReader() throws JAXBException {
        serverNames = new HashIntObjectMap<>();
    }

    public IntObjectMap<String> getServerNames() {
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
        return new String[] { "." };
    }
}
