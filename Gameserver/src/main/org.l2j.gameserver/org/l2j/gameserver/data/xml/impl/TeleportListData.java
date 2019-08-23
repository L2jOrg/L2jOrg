package org.l2j.gameserver.data.xml.impl;

import io.github.joealisson.primitive.HashIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.data.xml.model.TeleportData;
import org.l2j.gameserver.model.Location;
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

    private static Location INCORRECT_LOCATION = new Location(0, 0, 0);

    private IntMap<TeleportData> infos = new HashIntMap<>();

    private TeleportListData() {
        // singleton
    }

    public Optional<TeleportData> getInfo(int id) {
        var info = infos.get(id);
        if (nonNull(info)) {
            return Optional.of(info);
        }

        LOGGER.warn("Can't find teleport list for id: " + id);
        return Optional.empty();
    }

    @Override
    protected Path getSchemaFilePath() {
        return getSettings(ServerSettings.class).dataPackDirectory().resolve("data/xsd/teleports.xsd");
    }

    @Override
    public void load() {
        parseDatapackFile("data/teleports.xml");
    }

    @Override
    protected void parseDocument(Document doc, File f) {
        forEach(doc, "list",  list -> forEach(list, "teleport", this::parseTeleport));
    }

    private void parseTeleport(Node teleportNode) {
        var attributes = teleportNode.getAttributes();
        var id = parseInteger(attributes, "id");
        var price = parseInteger(attributes, "price");

        Location location;
        var locationNode = teleportNode.getFirstChild();
        if(nonNull(locationNode)) {
            location = parseLocation(locationNode);
        }
        else {
            LOGGER.warn("Can't find location node in TeleportListInfo");
            location = INCORRECT_LOCATION;
        }

        var info = new TeleportData(price, location);
        infos.put(id, info);
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