/*
 * Copyright © 2019-2021 L2JOrg
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
package org.l2j.gameserver.engine.elemental;

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

/**
 * @author JoeAlisson
 */
public final class ElementalSpiritEngine extends GameXmlReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(ElementalSpiritEngine.class);

    public static final long EXTRACT_FEE = 1000000;
    public static final float FRAGMENT_XP_CONSUME = 50000.0f;
    public static final int TALENT_INIT_FEE = 50000;
    public static final int MAX_STAGE = 5;

    private final Map<Byte, Map<Byte, ElementalSpiritTemplate>> spiritData = new HashMap<>(4);

    private ElementalSpiritEngine() {
    }

    public ElementalSpiritTemplate getSpirit(byte type, byte stage) {
        if(spiritData.containsKey(type)) {
           return spiritData.get(type).get(stage);
        }
        return null;
    }

    @Override
    protected Path getSchemaFilePath() {
        return ServerSettings.dataPackDirectory().resolve("data/elemental/elemental-spirits.xsd");
    }

    @Override
    public void load() {
        parseDatapackFile("data/elemental/elemental-spirits.xml");
        LOGGER.info("Loaded {} Elemental Spirits Templates.", spiritData.size());
        releaseResources();
    }

    @Override
    protected void parseDocument(Document doc, File f) {
        forEach(doc, "list",  list -> forEach(list, "spirit", this::parseSpirit));
    }

    private void parseSpirit(Node spiritNode) {
        var attributes = spiritNode.getAttributes();
        var type = parseByte(attributes, "type");
        var stage = parseByte(attributes, "stage");
        var npcId = parseInt(attributes, "npc");
        var extractItem = parseInt(attributes, "extract-item");
        var maxCharacteristics = parseInt(attributes, "max-characteristics");
        ElementalSpiritTemplate template = new ElementalSpiritTemplate(type, stage, npcId, extractItem, maxCharacteristics);
        spiritData.computeIfAbsent(type, HashMap::new).put(stage, template);

        forEach(spiritNode, "level", levelNode -> {
            var levelInfo = levelNode.getAttributes();
            var level = parseInt(levelInfo, "id");
            var attack = parseInt(levelInfo, "atk");
            var defense = parseInt(levelInfo, "def");
            var criticalRate = parseInt(levelInfo, "crit-rate");
            var criticalDamage = parseInt(levelInfo, "crit-dam");
            var maxExperience = parseLong(levelInfo, "max-exp");
            template.addLevelInfo(level, attack, defense, criticalRate, criticalDamage, maxExperience);
        });

        forEach(spiritNode, "evolve-item", itemNode -> {
            var itemInfo = itemNode.getAttributes();
            var itemId = parseInt(itemInfo, "id");
            var count = parseInt(itemInfo, "count");
            template.addItemToEvolve(itemId, count);
        });

        forEach(spiritNode, "absorb-item", absorbItemNode -> {
            var absorbInfo = absorbItemNode.getAttributes();
            var itemId = parseInt(absorbInfo, "id");
            var experience = parseInt(absorbInfo, "experience");
            template.addAbsorbItem(itemId, experience);
        });

    }

    public static void init() {
        getInstance().load();
    }

    public static ElementalSpiritEngine getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final ElementalSpiritEngine INSTANCE = new ElementalSpiritEngine();
    }
}
