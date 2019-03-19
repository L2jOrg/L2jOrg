package org.l2j.gameserver.data.xml.impl;

import org.l2j.gameserver.model.SayuneEntry;
import org.l2j.gameserver.util.IGameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author UnAfraid
 */
public class SayuneData implements IGameXmlReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(SayuneData.class.getName());

    private final Map<Integer, SayuneEntry> _maps = new HashMap<>();

    private SayuneData() {
        load();
    }

    @Override
    public void load() {
        parseDatapackFile("data/SayuneData.xml");
        LOGGER.info("Loaded: {} maps.", _maps.size());
    }

    @Override
    public void parseDocument(Document doc, File f) {
        for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
            if ("list".equalsIgnoreCase(n.getNodeName())) {
                for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
                    if ("map".equalsIgnoreCase(d.getNodeName())) {
                        final int id = parseInteger(d.getAttributes(), "id");
                        final SayuneEntry map = new SayuneEntry(id);
                        parseEntries(map, d);
                        _maps.put(map.getId(), map);
                    }
                }
            }
        }
    }

    private void parseEntries(SayuneEntry lastEntry, Node n) {
        NamedNodeMap attrs;
        for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
            if ("selector".equals(d.getNodeName()) || "choice".equals(d.getNodeName()) || "loc".equals(d.getNodeName())) {
                attrs = d.getAttributes();
                final int id = parseInteger(attrs, "id");
                final int x = parseInteger(attrs, "x");
                final int y = parseInteger(attrs, "y");
                final int z = parseInteger(attrs, "z");

                parseEntries(lastEntry.addInnerEntry(new SayuneEntry("selector".equals(d.getNodeName()), id, x, y, z)), d);
            }
        }
    }

    public SayuneEntry getMap(int id) {
        return _maps.get(id);
    }

    public Collection<SayuneEntry> getMaps() {
        return _maps.values();
    }

    public static SayuneData getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final SayuneData INSTANCE = new SayuneData();
    }
}
