package org.l2j.gameserver.data.xml.impl;

import io.github.joealisson.primitive.HashIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.data.xml.model.TeleportData;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.GameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

import static java.util.Objects.nonNull;
import static org.l2j.commons.configuration.Configurator.getSettings;

public final class TeleportListData extends GameXmlReader {

    private static Logger LOGGER = LoggerFactory.getLogger(TeleportListData.class);

    private IntMap<TeleportData> infos = new HashIntMap<>();

    private TeleportListData() {
        // singleton
    }

    public Optional<TeleportData> getInfo(int id) {
        var data = Optional.ofNullable(infos.get(id));
        if(data.isEmpty()) {
            LOGGER.warn("Can't find teleport list for id: {}", id);
        }
        return data;
    }

    @Override
    protected Path getSchemaFilePath() {
        return getSettings(ServerSettings.class).dataPackDirectory().resolve("data/xsd/teleports.xsd");
    }

    @Override
    public void load() {
        parseDatapackFile("data/teleports.xml");
        LOGGER.info("Loaded {} Teleports", infos.size());
        releaseResources();
    }

    @Override
    protected void parseDocument(Document doc, File f) {
        forEach(doc, "list",  list -> forEach(list, "teleport", this::parseTeleport));
    }

    private void parseTeleport(Node teleportNode) {
        var attributes = teleportNode.getAttributes();
        var id = parseInteger(attributes, "id");
        var price = parseInteger(attributes, "price");
        var castle = parseByte(attributes, "castle");

        var locationNode = teleportNode.getFirstChild();
        if(nonNull(locationNode)) {
            infos.put(id, new TeleportData(price, parseLocation(locationNode), castle));
        }
        else {
            LOGGER.warn("Can't find location node in teleports.xml id {}", id);
        }

    }

    public static void init() {
        getInstance().load();
    }

    public static TeleportListData getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final TeleportListData INSTANCE = new TeleportListData();
    }
}