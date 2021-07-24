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

import io.github.joealisson.primitive.HashIntMap;
import io.github.joealisson.primitive.HashLongMap;
import io.github.joealisson.primitive.IntMap;
import io.github.joealisson.primitive.LongMap;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.GameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.File;
import java.nio.file.Path;

/**
 * @author Mobius
 * @author JoeAlisson
 */
public class PetSkillData extends GameXmlReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(PetSkillData.class);
    private final IntMap<LongMap<Skill>> skillTrees = new HashIntMap<>();

    private PetSkillData() {
    }

    @Override
    protected Path getSchemaFilePath() {
        return ServerSettings.dataPackDirectory().resolve("data/xsd/PetSkillData.xsd");
    }

    @Override
    public void load() {
        skillTrees.clear();
        parseDatapackFile("data/PetSkillData.xml");
        LOGGER.info("Loaded {} skills.", skillTrees.size());
        releaseResources();
    }

    @Override
    public void parseDocument(Document doc, File f) {
        for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
            if ("list".equalsIgnoreCase(n.getNodeName())) {
                for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
                    if ("skill".equalsIgnoreCase(d.getNodeName())) {
                        final NamedNodeMap attrs = d.getAttributes();

                        final int npcId = parseInt(attrs, "npcId");
                        var skill = parseSkillInfo(d, "skillId", "skillLvl");

                        LongMap<Skill> skillTree = skillTrees.computeIfAbsent(npcId, k -> new HashLongMap<>());

                        if (skill != null) {
                            skillTree.put(SkillEngine.skillHashCode(skill.getId(), skill.getLevel()), skill);
                        } else {
                            LOGGER.info("Could not find skill with id {}, level {} for NPC  {}", parseInt(attrs, "skillId"), parseInt(attrs, "skillLvl"), npcId);
                        }
                    }
                }
            }
        }
    }

    public int getAvailableLevel(Summon pet, int skillId) {
        int lvl = 0;
        if (!skillTrees.containsKey(pet.getId())) {
            LOGGER.warn("Pet id {} does not have any skills assigned.", pet.getId());
            return lvl;
        }

        for (var skill : skillTrees.get(pet.getId()).values()) {
            if (skill.getId() == skillId) {
                if (skill.getLevel() == 0) {
                    lvl = calculateLevel(pet, skill);
                    break;
                } else if (1 <= pet.getLevel() && skill.getLevel() > lvl) {
                    lvl = skill.getLevel();
                }
            }
        }
        return lvl;
    }

    private int calculateLevel(Summon pet, Skill skill) {
        int lvl;
        if (pet.getLevel() < 70) {
            lvl = pet.getLevel() / 10;
            if (lvl <= 0) {
                lvl = 1;
            }
        } else {
            lvl = 7 + ((pet.getLevel() - 70) / 5);
        }

        // formula usable for skill that have 10 or more skill levels
        final int maxLvl = SkillEngine.getInstance().getMaxLevel(skill.getId());
        if (lvl > maxLvl) {
            lvl = maxLvl;
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
