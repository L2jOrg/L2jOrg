/*
 * This file is part of the L2J Mobius project.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.mobius.gameserver.data.xml.impl;

import org.l2j.commons.util.IGameXmlReader;
import org.l2j.gameserver.mobius.gameserver.model.actor.L2Summon;
import org.l2j.gameserver.mobius.gameserver.model.holders.SkillHolder;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author Mobius
 */
public class PetSkillData implements IGameXmlReader {
    private static Logger LOGGER = Logger.getLogger(PetSkillData.class.getName());
    private final Map<Integer, Map<Long, SkillHolder>> _skillTrees = new HashMap<>();

    protected PetSkillData() {
        load();
    }

    public static PetSkillData getInstance() {
        return SingletonHolder._instance;
    }

    @Override
    public void load() {
        _skillTrees.clear();
        parseDatapackFile("data/PetSkillData.xml");
        LOGGER.info(getClass().getSimpleName() + ": Loaded " + _skillTrees.size() + " skills.");
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

                        Map<Long, SkillHolder> skillTree = _skillTrees.get(npcId);
                        if (skillTree == null) {
                            skillTree = new HashMap<>();
                            _skillTrees.put(npcId, skillTree);
                        }

                        if (SkillData.getInstance().getSkill(skillId, skillLvl == 0 ? 1 : skillLvl) != null) {
                            skillTree.put(SkillData.getSkillHashCode(skillId, skillLvl + 1), new SkillHolder(skillId, skillLvl));
                        } else {
                            LOGGER.info(getClass().getSimpleName() + ": Could not find skill with id " + skillId + ", level " + skillLvl + " for NPC " + npcId + ".");
                        }
                    }
                }
            }
        }
    }

    public int getAvailableLevel(L2Summon pet, int skillId) {
        int lvl = 0;
        if (!_skillTrees.containsKey(pet.getId())) {
            LOGGER.warning(getClass().getSimpleName() + ": Pet id " + pet.getId() + " does not have any skills assigned.");
            return lvl;
        }

        for (SkillHolder skillHolder : _skillTrees.get(pet.getId()).values()) {
            if (skillHolder.getSkillId() != skillId) {
                continue;
            }
            if (skillHolder.getSkillLevel() == 0) {
                if (pet.getLevel() < 70) {
                    lvl = pet.getLevel() / 10;
                    if (lvl <= 0) {
                        lvl = 1;
                    }
                } else {
                    lvl = 7 + ((pet.getLevel() - 70) / 5);
                }

                // formula usable for skill that have 10 or more skill levels
                final int maxLvl = SkillData.getInstance().getMaxLevel(skillHolder.getSkillId());
                if (lvl > maxLvl) {
                    lvl = maxLvl;
                }
                break;
            } else if (1 <= pet.getLevel()) {
                if (skillHolder.getSkillLevel() > lvl) {
                    lvl = skillHolder.getSkillLevel();
                }
            }
        }

        return lvl;
    }

    public List<Integer> getAvailableSkills(L2Summon pet) {
        final List<Integer> skillIds = new ArrayList<>();
        if (!_skillTrees.containsKey(pet.getId())) {
            LOGGER.warning(getClass().getSimpleName() + ": Pet id " + pet.getId() + " does not have any skills assigned.");
            return skillIds;
        }

        for (SkillHolder skillHolder : _skillTrees.get(pet.getId()).values()) {
            if (skillIds.contains(skillHolder.getSkillId())) {
                continue;
            }
            skillIds.add(skillHolder.getSkillId());
        }

        return skillIds;
    }

    private static class SingletonHolder {
        protected static final PetSkillData _instance = new PetSkillData();
    }
}
