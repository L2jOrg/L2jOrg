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
package org.l2j.gameserver.engine.skill.api;

import io.github.joealisson.primitive.*;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.GameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.File;
import java.nio.file.Path;

/**
 * @author Mobius
 * @author JoeAlisson
 */
public class PetSkillEngine extends GameXmlReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(PetSkillEngine.class);
    private final IntMap<LongMap<SkillHolder>> skillTrees = new HashIntMap<>();

    private PetSkillEngine() {
    }

    @Override
    protected Path getSchemaFilePath() {
        return ServerSettings.dataPackDirectory().resolve("data/xsd/pet-skills.xsd");
    }

    @Override
    public void load() {
        skillTrees.clear();
        parseDatapackFile("data/pet-skills.xml");
        LOGGER.info("Loaded {} skills.", skillTrees.size());
        releaseResources();
    }

    @Override
    public void parseDocument(Document doc, File f) {
        for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
            if ("list".equalsIgnoreCase(n.getNodeName())) {
                for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
                    parsePetSkill(d);
                }
            }
        }
    }

    private void parsePetSkill(Node skillNode) {
        final var attrs = skillNode.getAttributes();
        final var npcId = parseInt(attrs, "npc");

        var skillInfo = new SkillHolder(parseInt(attrs, "id"), parseInt(attrs, "level"));
        var skillTree = skillTrees.computeIfAbsent(npcId, k -> new HashLongMap<>());
        skillTree.put(skillInfo.getSkillId(), skillInfo);
    }

    public Skill getAvailableSkill(Summon servitor, int skillId) {
        var skillInfo = skillTrees.getOrDefault(servitor.getId(), Containers.emptyLongMap()).get(skillId);
        if(skillInfo == null) {
            return null;
        }
        return skillInfo.getLevel() > 0 ?
                skillInfo.getSkill() :
                SkillEngine.getInstance().getSkill(skillInfo.getSkillId(), calculateLevel(servitor, skillId));
    }

    private int calculateLevel(Summon pet, int skillId) {
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
        final int maxLvl = SkillEngine.getInstance().getMaxLevel(skillId);
        if (lvl > maxLvl) {
            lvl = maxLvl;
        }
        return lvl;
    }

    public static void init()  {
        getInstance().load();
    }

    public static PetSkillEngine getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final PetSkillEngine INSTANCE = new PetSkillEngine();
    }
}
