package org.l2j.gameserver.data.xml.impl;

import org.l2j.gameserver.model.actor.L2Summon;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.util.IGameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Mobius
 */
public class PetSkillData implements IGameXmlReader {
    private static Logger LOGGER = LoggerFactory.getLogger(PetSkillData.class);
    private final Map<Integer, Map<Long, SkillHolder>> _skillTrees = new HashMap<>();

    private PetSkillData() {
        load();
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

                        Map<Long, SkillHolder> skillTree = _skillTrees.computeIfAbsent(npcId, k -> new HashMap<>());

                        if (SkillData.getInstance().getSkill(skillId, skillLvl == 0 ? 1 : skillLvl) != null) {
                            skillTree.put(SkillData.getSkillHashCode(skillId, skillLvl + 1), new SkillHolder(skillId, skillLvl));
                        } else {
                            LOGGER.info("Could not find skill with id {}, level {} for NPC  {}", skillId, skillLvl, npcId);
                        }
                    }
                }
            }
        }
    }

    public int getAvailableLevel(L2Summon pet, int skillId) {
        int lvl = 0;
        if (!_skillTrees.containsKey(pet.getId())) {
            LOGGER.warn("Pet id {} does not have any skills assigned.", pet.getId());
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

    public static PetSkillData getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final PetSkillData INSTANCE = new PetSkillData();
    }
}
