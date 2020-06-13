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

import org.l2j.commons.xml.XmlReader;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.templates.CubicTemplate;
import org.l2j.gameserver.model.cubic.CubicSkill;
import org.l2j.gameserver.model.cubic.ICubicConditionHolder;
import org.l2j.gameserver.model.cubic.conditions.HealthCondition;
import org.l2j.gameserver.model.cubic.conditions.HpCondition;
import org.l2j.gameserver.model.cubic.conditions.HpCondition.HpConditionType;
import org.l2j.gameserver.model.cubic.conditions.RangeCondition;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.GameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.File;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.l2j.commons.configuration.Configurator.getSettings;

/**
 * @author UnAfraid
 */
public class CubicData extends GameXmlReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(CubicData.class);

    private final Map<Integer, Map<Integer, CubicTemplate>> _cubics = new HashMap<>();

    private CubicData() {
        load();
    }

    @Override
    protected Path getSchemaFilePath() {
        return getSettings(ServerSettings.class).dataPackDirectory().resolve("data/xsd/cubics.xsd");
    }

    @Override
    public void load() {
        _cubics.clear();
        parseDatapackDirectory("data/stats/cubics", true);
        LOGGER.info("Loaded {} cubics.", _cubics.size());
        releaseResources();
    }

    @Override
    public void parseDocument(Document doc, File f) {
        forEach(doc, "list", listNode -> forEach(listNode, "cubic", cubicNode ->
        {
            parseTemplate(cubicNode, new CubicTemplate(new StatsSet(parseAttributes(cubicNode))));
        }));
    }

    /**
     * @param cubicNode
     * @param template
     */
    private void parseTemplate(Node cubicNode, CubicTemplate template) {
        forEach(cubicNode, XmlReader::isNode, innerNode ->
        {
            switch (innerNode.getNodeName()) {
                case "conditions": {
                    parseConditions(innerNode, template, template);
                    break;
                }
                case "skills": {
                    parseSkills(innerNode, template);
                    break;
                }
            }
        });
        _cubics.computeIfAbsent(template.getId(), key -> new HashMap<>()).put(template.getLevel(), template);
    }

    /**
     * @param cubicNode
     * @param template
     * @param holder
     */
    private void parseConditions(Node cubicNode, CubicTemplate template, ICubicConditionHolder holder) {
        forEach(cubicNode, XmlReader::isNode, conditionNode ->
        {
            switch (conditionNode.getNodeName()) {
                case "hp": {
                    final HpConditionType type = parseEnum(conditionNode.getAttributes(), HpConditionType.class, "type");
                    final int hpPer = parseInteger(conditionNode.getAttributes(), "percent");
                    holder.addCondition(new HpCondition(type, hpPer));
                    break;
                }
                case "range": {
                    final int range = parseInteger(conditionNode.getAttributes(), "value");
                    holder.addCondition(new RangeCondition(range));
                    break;
                }
                case "healthPercent": {
                    final int min = parseInteger(conditionNode.getAttributes(), "min");
                    final int max = parseInteger(conditionNode.getAttributes(), "max");
                    holder.addCondition(new HealthCondition(min, max));
                    break;
                }
                default: {
                    LOGGER.warn("Attempting to use not implemented condition: " + conditionNode.getNodeName() + " for cubic id: " + template.getId() + " level: " + template.getLevel());
                    break;
                }
            }
        });
    }

    /**
     * @param cubicNode
     * @param template
     */
    private void parseSkills(Node cubicNode, CubicTemplate template) {
        forEach(cubicNode, "skill", skillNode ->
        {
            final CubicSkill skill = new CubicSkill(new StatsSet(parseAttributes(skillNode)));
            forEach(cubicNode, "conditions", conditionNode -> parseConditions(cubicNode, template, skill));
            template.getSkills().add(skill);
        });
    }

    /**
     * @param id
     * @param level
     * @return the CubicTemplate for specified id and level
     */
    public CubicTemplate getCubicTemplate(int id, int level) {
        return _cubics.getOrDefault(id, Collections.emptyMap()).get(level);
    }

    public static CubicData getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {

        private static final CubicData INSTANCE = new CubicData();
    }
}
