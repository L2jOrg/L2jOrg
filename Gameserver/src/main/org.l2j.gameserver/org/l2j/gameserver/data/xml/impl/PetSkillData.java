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

import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.holders.SkillHolder;
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

import static org.l2j.commons.configuration.Configurator.getSettings;

/**
 * @author Mobius
 */
public class PetSkillData extends GameXmlReader {
    private static Logger LOGGER = LoggerFactory.getLogger(PetSkillData.class);
    private final Map<Integer, Map<Long, SkillHolder>> _skillTrees = new HashMap<>();

    private PetSkillData() {
    }

    @Override
    protected Path getSchemaFilePath() {
        return getSettings(ServerSettings.class).dataPackDirectory().resolve("data/xsd/PetSkillData.xsd");
    }

    @Override
    public void load() {
        _skillTrees.clear();
        parseDatapackFile("data/PetSkillData.xml");
        LOGGER.info("Loaded {} skills.", _skillTrees.size());
        releaseResources();
    }

    @Override
    public void parseDocument(Document doc, File f) {
        for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
            if ("list".equalsIgnoreCase(n.getNodeName())) {
                for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
                    if ("skill".equalsIgnoreCase(d.getNodeName())) {
                        final NamedNodeMap attrs = d.getAttributes();

                        final int npcId = parseInteger(attrs, "npcId");
                        final int skillId = parseInteger(attrs, "skillId");
                        final int skillLvl = parseInteger(attrs, "skillLvl");

                        Map<Long, SkillHolder> skillTree = _skillTrees.computeIfAbsent(npcId, k -> new HashMap<>());

                        if (SkillEngine.getInstance().getSkill(skillId, skillLvl == 0 ? 1 : skillLvl) != null) {
                            skillTree.put(SkillEngine.skillHashCode(skillId, skillLvl + 1), new SkillHolder(skillId, skillLvl));
                        } else {
                            LOGGER.info("Could not find skill with id {}, level {} for NPC  {}", skillId, skillLvl, npcId);
                        }
                    }
                }
            }
        }
    }

    public int getAvailableLevel(Summon pet, int skillId) {
        int lvl = 0;
        if (!_skillTrees.containsKey(pet.getId())) {
            LOGGER.warn("Pet id {} does not have any skills assigned.", pet.getId());
            return lvl;
        }

        for (SkillHolder skillHolder : _skillTrees.get(pet.getId()).values()) {
            if (skillHolder.getSkillId() != skillId) {
                continue;
            }
            if (skillHolder.getLevel() == 0) {
                if (pet.getLevel() < 70) {
                    lvl = pet.getLevel() / 10;
                    if (lvl <= 0) {
                        lvl = 1;
                    }
                } else {
                    lvl = 7 + ((pet.getLevel() - 70) / 5);
                }

                // formula usable for skill that have 10 or more skill levels
                final int maxLvl = SkillEngine.getInstance().getMaxLevel(skillHolder.getSkillId());
                if (lvl > maxLvl) {
                    lvl = maxLvl;
                }
                break;
            } else if (1 <= pet.getLevel()) {
                if (skillHolder.getLevel() > lvl) {
                    lvl = skillHolder.getLevel();
                }
            }
        }

        return lvl;
    }

    public static void init()  {
        getInstance().load();
    }

    public static PetSkillData getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final PetSkillData INSTANCE = new PetSkillData();
    }
}
