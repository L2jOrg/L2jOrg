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
package org.l2j.gameserver.data.xml.impl;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.transform.Transform;
import org.l2j.gameserver.model.actor.transform.TransformLevelData;
import org.l2j.gameserver.model.actor.transform.TransformTemplate;
import org.l2j.gameserver.model.holders.AdditionalItemHolder;
import org.l2j.gameserver.model.holders.AdditionalSkillHolder;
import org.l2j.gameserver.network.serverpackets.ExBasicActionList;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.GameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Transformation data.
 *
 * @author UnAfraid
 */
public final class TransformData extends GameXmlReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransformData.class);
    public static final String ACTIONS = "actions";

    private final Map<Integer, Transform> _transformData = new HashMap<>();

    private TransformData() {
        load();
    }

    @Override
    protected Path getSchemaFilePath() {
        return ServerSettings.dataPackDirectory().resolve("data/xsd/transformations.xsd");
    }

    @Override
    public synchronized void load() {
        _transformData.clear();
        parseDatapackDirectory("data/stats/transformations", false);
        LOGGER.info("Loaded: {} transform templates.", _transformData.size());
        releaseResources();
    }

    @Override
    public void parseDocument(Document doc, File f) {
        for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
            if ("list".equalsIgnoreCase(n.getNodeName())) {
                for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
                    if ("transform".equalsIgnoreCase(d.getNodeName())) {
                        parseTransformNode(d);
                    }
                }
            }
        }
    }

    private void parseTransformNode(Node d) {
        NamedNodeMap attrs = d.getAttributes();
        final StatsSet set = new StatsSet();
        for (int i = 0; i < attrs.getLength(); i++) {
            final Node att = attrs.item(i);
            set.set(att.getNodeName(), att.getNodeValue());
        }
        final Transform transform = new Transform(set);
        for (Node cd = d.getFirstChild(); cd != null; cd = cd.getNextSibling()) {
            final boolean isMale = "Male".equalsIgnoreCase(cd.getNodeName());
            if ("Male".equalsIgnoreCase(cd.getNodeName()) || "Female".equalsIgnoreCase(cd.getNodeName())) {
                parseTransform(set, transform, cd, isMale);
            }
        }
        _transformData.put(transform.getId(), transform);
    }

    private void parseTransform(StatsSet set, Transform transform, Node cd, boolean isMale) {
        TransformTemplate templateData = null;
        for (Node z = cd.getFirstChild(); z != null; z = z.getNextSibling()) {
            templateData = switch (z.getNodeName()) {
                case "common" ->   parseCommonTransform(set, transform, isMale, z);
                case "skills" ->   parseTransformSkills(set, transform, isMale, templateData, z);
                case ACTIONS ->  parseTransformActions(set, transform, isMale, templateData, z);
                case "items" ->    parseItems(set, transform, isMale, templateData, z);
                case "levels" ->   parseLevels(set, transform, isMale, templateData, z);
                case "additionalSkills" ->  parseAdditionalTransformSkills(set, transform, isMale, templateData, z);
                default -> templateData;
            };
        }
    }

    private TransformTemplate parseLevels(StatsSet set, Transform transform, boolean isMale, TransformTemplate templateData, Node z) {
        NamedNodeMap attrs;
        if (templateData == null) {
            templateData = new TransformTemplate(set);
            transform.setTemplate(isMale, templateData);
        }

        final StatsSet levelsSet = new StatsSet();
        for (Node s = z.getFirstChild(); s != null; s = s.getNextSibling()) {
            if ("level".equals(s.getNodeName())) {
                attrs = s.getAttributes();
                for (int i = 0; i < attrs.getLength(); i++) {
                    final Node att = attrs.item(i);
                    levelsSet.set(att.getNodeName(), att.getNodeValue());
                }
            }
        }
        templateData.addLevelData(new TransformLevelData(levelsSet));
        return templateData;
    }

    private TransformTemplate parseItems(StatsSet set, Transform transform, boolean isMale, TransformTemplate templateData, Node z) {
        NamedNodeMap attrs;
        if (templateData == null) {
            templateData = new TransformTemplate(set);
            transform.setTemplate(isMale, templateData);
        }
        for (Node s = z.getFirstChild(); s != null; s = s.getNextSibling()) {
            if ("item".equals(s.getNodeName())) {
                attrs = s.getAttributes();
                final int itemId = parseInt(attrs, "id");
                final boolean allowed = parseBoolean(attrs, "allowed");
                templateData.addAdditionalItem(new AdditionalItemHolder(itemId, allowed));
            }
        }
        return templateData;
    }

    private TransformTemplate parseAdditionalTransformSkills(StatsSet set, Transform transform, boolean isMale, TransformTemplate templateData, Node z) {
        NamedNodeMap attrs;
        if (templateData == null) {
            templateData = new TransformTemplate(set);
            transform.setTemplate(isMale, templateData);
        }
        for (Node s = z.getFirstChild(); s != null; s = s.getNextSibling()) {
            if ("skill".equals(s.getNodeName())) {
                attrs = s.getAttributes();
                var skill = parseSkillInfo(s);
                final int minLevel = parseInt(attrs, "minLevel");
                templateData.addAdditionalSkill(new AdditionalSkillHolder(skill, minLevel));
            }
        }
        return templateData;
    }

    private TransformTemplate parseTransformActions(StatsSet set, Transform transform, boolean isMale, TransformTemplate templateData, Node z) {
        if (templateData == null) {
            templateData = new TransformTemplate(set);
            transform.setTemplate(isMale, templateData);
        }
        set.set(ACTIONS, z.getTextContent());
        final int[] actions = set.getIntArray(ACTIONS, " ");
        templateData.setBasicActionList(new ExBasicActionList(actions));
        return templateData;
    }

    private TransformTemplate parseTransformSkills(StatsSet set, Transform transform, boolean isMale, TransformTemplate templateData, Node z) {
        if (templateData == null) {
            templateData = new TransformTemplate(set);
            transform.setTemplate(isMale, templateData);
        }
        for (Node s = z.getFirstChild(); s != null; s = s.getNextSibling()) {
            if ("skill".equals(s.getNodeName())) {
                templateData.addSkill(parseSkillInfo(s));
            }
        }
        return templateData;
    }

    private TransformTemplate parseCommonTransform(StatsSet set, Transform transform, boolean isMale, Node z) {
        NamedNodeMap attrs;
        TransformTemplate templateData;
        for (Node s = z.getFirstChild(); s != null; s = s.getNextSibling()) {
            switch (s.getNodeName()) {
                case "base", "stats", "defense", "magicDefense", "collision", "moving" -> {
                    attrs = s.getAttributes();
                    for (int i = 0; i < attrs.getLength(); i++) {
                        final Node att = attrs.item(i);
                        set.set(att.getNodeName(), att.getNodeValue());
                    }
                }
                default -> LOGGER.warn("Unknown common transformation node {}", s.getNodeName());
            }
        }
        templateData = new TransformTemplate(set);
        transform.setTemplate(isMale, templateData);
        return templateData;
    }

    public Transform getTransform(int id) {
        return _transformData.get(id);
    }

    public static TransformData getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final TransformData INSTANCE = new TransformData();
    }
}
