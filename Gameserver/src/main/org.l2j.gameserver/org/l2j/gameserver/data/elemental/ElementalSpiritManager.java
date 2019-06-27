package org.l2j.gameserver.data.elemental;

import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.GameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.l2j.commons.configuration.Configurator.getSettings;

public class ElementalSpiritManager extends GameXmlReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(ElementalSpiritManager.class);
    public static final float FRAGMENT_XP_CONSUME = 50000.0f;

    private final Map<Byte, Map<Byte, ElementalSpiritTemplate>> spiritData;

    private ElementalSpiritManager() {
        spiritData = new HashMap<>(4);
    }

    public static void init() {
        getInstance().load();
    }

    ElementalSpiritTemplate getSpirit(byte type, byte stage) {
        if(spiritData.containsKey(type)) {
           return spiritData.get(type).get(stage);
        }
        return null;
    }

    @Override
    protected Path getSchemaFilePath() {
        return getSettings(ServerSettings.class).dataPackDirectory().resolve("data/elemental/elementalSpirits.xsd");
    }

    @Override
    public void load() {
        parseDatapackFile("data/elemental/elementalSpirits.xml");
        LOGGER.info("Loaded {} Elemental Spirits Templates.", spiritData.size());
    }

    @Override
    protected void parseDocument(Document doc, File f) {
        forEach(doc, "list",  list -> forEach(list, "spirit", this::parseSpirit));
    }

    private void parseSpirit(Node spiritNode) {
        var attributes = spiritNode.getAttributes();
        var type = parseByte(attributes, "type");
        var stage = parseByte(attributes, "stage");
        var npcId = parseInteger(attributes, "npcId");
        var extractItem = parseInteger(attributes, "extractItem");
        var maxCharacteristics = parseInteger(attributes, "maxCharacteristics");
        ElementalSpiritTemplate template = new ElementalSpiritTemplate(type, stage, npcId, extractItem, maxCharacteristics);
        spiritData.computeIfAbsent(type, HashMap::new).put(stage, template);

        forEach(spiritNode, "level", levelNode -> {
            var levelInfo = levelNode.getAttributes();
            var level = parseInteger(levelInfo, "id");
            var attack = parseInteger(levelInfo, "atk");
            var defense = parseInteger(levelInfo, "def");
            var criticalRate = parseInteger(levelInfo, "critRate");
            var criticalDamage = parseInteger(levelInfo, "critDam");
            var maxExperience = parseLong(levelInfo, "maxExp");
            template.addLevelInfo(level, attack, defense, criticalRate, criticalDamage, maxExperience);
        });

        forEach(spiritNode, "itemToEvolve", itemNode -> {
            var itemInfo = itemNode.getAttributes();
            var itemId = parseInteger(itemInfo, "id");
            var count = parseInteger(itemInfo, "count");
            template.addItemToEvolve(itemId, count);
        });

        forEach(spiritNode, "absorbItem", absorbItemNode -> {
            var absorbInfo = absorbItemNode.getAttributes();
            var itemId = parseInteger(absorbInfo, "id");
            var experience = parseInteger(absorbInfo, "experience");
            template.addAbsorbItem(itemId, experience);
        });

    }

    public static ElementalSpiritManager getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final ElementalSpiritManager INSTANCE = new ElementalSpiritManager();
    }
}
