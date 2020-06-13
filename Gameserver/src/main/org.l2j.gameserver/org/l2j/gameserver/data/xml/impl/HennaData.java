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
package org.l2j.gameserver.data.xml.impl;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.item.Henna;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.GameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.l2j.commons.configuration.Configurator.getSettings;


/**
 * This class holds the henna related information.<br>
 * Cost and required amount to add the henna to the player.<br>
 * Cost and retrieved amount for removing the henna from the player.<br>
 * Allowed classes to wear each henna.
 *
 * @author Zoey76, Mobius
 */
public final class HennaData extends GameXmlReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(HennaData.class);

    private final Map<Integer, Henna> _hennaList = new HashMap<>();

    private HennaData() {
        load();
    }

    @Override
    protected Path getSchemaFilePath() {
        return getSettings(ServerSettings.class).dataPackDirectory().resolve("data/xsd/hennaList.xsd");
    }

    @Override
    public void load() {
        _hennaList.clear();
        parseDatapackFile("data/stats/hennaList.xml");
        LOGGER.info("Loaded {} Henna data.", _hennaList.size());
        releaseResources();
    }

    @Override
    public void parseDocument(Document doc, File f) {
        for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
            if ("list".equals(n.getNodeName())) {
                for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
                    if ("henna".equals(d.getNodeName())) {
                        parseHenna(d);
                    }
                }
            }
        }
    }

    /**
     * Parses the henna.
     *
     * @param d the d
     */
    private void parseHenna(Node d) {
        final StatsSet set = new StatsSet();
        final List<ClassId> wearClassIds = new ArrayList<>();
        final List<Skill> skills = new ArrayList<>();
        NamedNodeMap attrs = d.getAttributes();
        Node attr;
        for (int i = 0; i < attrs.getLength(); i++) {
            attr = attrs.item(i);
            set.set(attr.getNodeName(), attr.getNodeValue());
        }

        for (Node c = d.getFirstChild(); c != null; c = c.getNextSibling()) {
            final String name = c.getNodeName();
            attrs = c.getAttributes();
            switch (name) {
                case "stats": {
                    for (int i = 0; i < attrs.getLength(); i++) {
                        attr = attrs.item(i);
                        set.set(attr.getNodeName(), attr.getNodeValue());
                    }
                    break;
                }
                case "wear": {
                    attr = attrs.getNamedItem("count");
                    set.set("wear_count", attr.getNodeValue());
                    attr = attrs.getNamedItem("fee");
                    set.set("wear_fee", attr.getNodeValue());
                    break;
                }
                case "cancel": {
                    attr = attrs.getNamedItem("count");
                    set.set("cancel_count", attr.getNodeValue());
                    attr = attrs.getNamedItem("fee");
                    set.set("cancel_fee", attr.getNodeValue());
                    break;
                }
                case "duration": {
                    attr = attrs.getNamedItem("time"); // in minutes
                    set.set("duration", attr.getNodeValue());
                    break;
                }
                case "skill": {
                    skills.add(SkillEngine.getInstance().getSkill(parseInteger(attrs, "id"), parseInteger(attrs, "level")));
                    break;
                }
                case "classId": {
                    wearClassIds.add(ClassId.getClassId(Integer.parseInt(c.getTextContent())));
                    break;
                }
            }
        }
        final Henna henna = new Henna(set);
        henna.setSkills(skills);
        henna.setWearClassIds(wearClassIds);
        _hennaList.put(henna.getDyeId(), henna);
    }

    /**
     * Gets the henna.
     *
     * @param id of the dye.
     * @return the dye with that id.
     */
    public Henna getHenna(int id) {
        return _hennaList.get(id);
    }

    /**
     * Gets the henna list.
     *
     * @param classId the player's class Id.
     * @return the list with all the allowed dyes.
     */
    public List<Henna> getHennaList(ClassId classId) {
        final List<Henna> list = new ArrayList<>();
        for (Henna henna : _hennaList.values()) {
            if (henna.isAllowedClass(classId)) {
                list.add(henna);
            }
        }
        return list;
    }

    public static HennaData getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final HennaData INSTANCE = new HennaData();
    }
}