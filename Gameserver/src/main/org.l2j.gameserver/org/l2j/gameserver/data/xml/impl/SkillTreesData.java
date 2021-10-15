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
import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.engine.skill.api.SkillLearn;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.base.AcquireSkillType;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.base.SocialStatus;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.holders.PlayerSkillHolder;
import org.l2j.gameserver.model.interfaces.ISkillsHolder;
import org.l2j.gameserver.model.skills.CommonSkill;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.GameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.File;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * This class loads and manage the characters and pledges skills trees.<br>
 * Here can be found the following skill trees:<br>
 * <ul>
 * <li>Class skill trees: player skill trees for each class.</li>
 * <li>Fishing skill tree: player skill tree for fishing related skills.</li>
 * <li>Transform skill tree: player skill tree for transformation related skills.</li>
 * <li>Noble skill tree: player skill tree for noblesse related skills.</li>
 * <li>Hero skill tree: player skill tree for heroes related skills.</li>
 * <li>GM skill tree: player skill tree for Game Master related skills.</li>
 * <li>Common skill tree: custom skill tree for players, skills in this skill tree will be available for all players.</li>
 * <li>Pledge skill tree: clan skill tree for main clan.</li>
 * <li>Sub-Pledge skill tree: clan skill tree for sub-clans.</li>
 * </ul>
 * For easy customization of player class skill trees, the parent Id of each class is taken from the XML data, this means you can use a different class parent Id than in the normal game play, for example all 3rd class dagger users will have Treasure Hunter skills as 1st and 2nd class skills.<br>
 * For XML schema please refer to skillTrees.xsd in datapack in xsd folder and for parameters documentation refer to documentation.txt in skillTrees folder.<br>
 *
 * @author Zoey76
 * @author JoeAlisson
 */
public final class SkillTreesData extends GameXmlReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(SkillTreesData.class);

    private static final Map<ClassId, LongMap<SkillLearn>> classSkillTrees = new EnumMap<>(ClassId.class);
    private static final LongMap<SkillLearn> fishingSkillTree = new HashLongMap<>();
    private static final LongMap<SkillLearn> pledgeSkillTree = new HashLongMap<>();
    private static final LongMap<SkillLearn> transformSkillTree = new HashLongMap<>();
    private static final LongMap<SkillLearn> commonSkillTree = new HashLongMap<>();

    private static final LongMap<SkillLearn> heroSkillTree = new HashLongMap<>();
    private static final LongMap<SkillLearn> gameMasterSkillTree = new HashLongMap<>();

    /**
     * Parent class Ids are read from XML and stored in this map, to allow easy customization.
     */
    private static final Map<ClassId, ClassId> parentClassMap = new HashMap<>();
    private final AtomicBoolean isLoading = new AtomicBoolean();
    // Checker, sorted arrays of hash codes
    private IntMap<long[]> skillsByClassIdHashCodes; // Occupation skills
    private IntMap<long[]> skillsByRaceHashCodes; // Race-specific Transformations
    private long[] allSkillsHashCodes; // Fishing, Collection, Transformations, Common Skills.

    private SkillTreesData() {
    }

    @Override
    protected Path getSchemaFilePath() {
        return ServerSettings.dataPackDirectory().resolve("data/xsd/skillTrees.xsd");
    }

    @Override
    public void load() {
        isLoading.set(true);

        classSkillTrees.clear();
        fishingSkillTree.clear();
        pledgeSkillTree.clear();
        transformSkillTree.clear();
        heroSkillTree.clear();
        gameMasterSkillTree.clear();

        parseDatapackDirectory("data/skillTrees/", true);
        generateCheckArrays();
        report();
        isLoading.set(false);
        releaseResources();
    }

    @Override
    public void parseDocument(Document doc, File f) {
        forEach(doc, "list", listNode -> forEach(listNode, "skillTree", this::parseSkillTree));
    }

    private void parseSkillTree(Node skillTreeNode) {
        var attributes = skillTreeNode.getAttributes();

        var type = parseString(attributes,"type");
        var cId = parseInt(attributes, "classId", -1);
        var parentId = parseInt(attributes, "parentClassId", -1);

        ClassId classId = ClassId.getClassId(cId);

        if(nonNull(classId) && parentId > -1 && cId != parentId) {
            parentClassMap.putIfAbsent(classId, ClassId.getClassId(parentId));
        }

        final LongMap<SkillLearn> classSkillTree = new HashLongMap<>();

        forEach(skillTreeNode, "skill", skillNode -> parseSkill(skillNode, classSkillTree, classId, type));

        if (type.equals("classSkillTree") && (cId > -1)) {
            final var classSkillTrees = SkillTreesData.classSkillTrees.get(classId);
            if (isNull(classSkillTrees)) {
                SkillTreesData.classSkillTrees.put(classId, classSkillTree);
            } else {
                classSkillTrees.putAll(classSkillTree);
            }
        }
    }

    private void parseSkill(Node skillNode, LongMap<SkillLearn> classSkillTree, ClassId classId, String type) {
        List<Skill> replaceSkills = null;
        List<ItemHolder> items = null;

        for (var node = skillNode.getFirstChild(); node != null; node = node.getNextSibling()) {
            if("replace-skill".equals(node.getNodeName())) {
                if(replaceSkills == null) {
                    replaceSkills = new ArrayList<>();
                }
                replaceSkills.add(parseSkillInfo(node));
            } else if("item".equals(node.getNodeName())) {
                if(items == null) {
                    items = new ArrayList<>();
                }
                items.add(parseItemHolder(node));
            }
        }
        createNewSkillLearn(skillNode, classSkillTree, classId, type, replaceSkills, items);
    }

    private void createNewSkillLearn(Node skillNode, LongMap<SkillLearn> classSkillTree, ClassId classId, String type, List<Skill> replaceSkills, List<ItemHolder> items) {
        var attrs = skillNode.getAttributes();
        var id = parseInt(attrs,"id");
        var level = parseInt(attrs, "level");
        var requiredLevel = parseInt(attrs, "required-level");
        var autoLearn = parseBoolean(attrs, "auto-learn");
        var sp = parseLong(attrs, "sp");
        var learnedByNpc = parseBoolean(attrs, "learned-by-npc");
        var socialStatus = parseEnum(attrs, SocialStatus.class, "social-status");
        Set<Race> races = Collections.unmodifiableSet(parseEnumSet(attrs, Race.class, "races"));
        var residences = parseIntSet(attrs, "residences");

        items = items == null ? Collections.emptyList() : Collections.unmodifiableList(items);
        replaceSkills = replaceSkills == null ? Collections.emptyList() : Collections.unmodifiableList(replaceSkills);

        var skillLearn = new SkillLearn(id, level, requiredLevel, autoLearn, sp, learnedByNpc, socialStatus, races, items, replaceSkills, residences);

        addToSkillTree(classSkillTree, classId, type, skillLearn);
    }

    private void addToSkillTree(LongMap<SkillLearn> classSkillTree, ClassId classId, String type, SkillLearn skillLearn) {
        final long skillHashCode = SkillEngine.skillHashCode(skillLearn.id(), skillLearn.level());
        switch (type) {
            case "classSkillTree" -> {
                if (nonNull(classId)) {
                    classSkillTree.put(skillHashCode, skillLearn);
                } else {
                    commonSkillTree.put(skillHashCode, skillLearn);
                }
            }
            case "fishingSkillTree" -> fishingSkillTree.put(skillHashCode, skillLearn);
            case "pledgeSkillTree" -> pledgeSkillTree.put(skillHashCode, skillLearn);
            case "transformSkillTree" -> transformSkillTree.put(skillHashCode, skillLearn);
            case "heroSkillTree" -> heroSkillTree.put(skillHashCode, skillLearn);
            case "gameMasterSkillTree" -> gameMasterSkillTree.put(skillHashCode, skillLearn);
            default -> LOGGER.warn("Unknown Skill Tree type: {}", type);
        }
    }

    /**
     * Method to get the complete skill tree for a given class id.<br>
     * Include all skills common to all classes.<br>
     * Includes all parent skill trees.
     *
     * @param classId the class skill tree Id
     * @return the complete Class Skill Tree including skill trees from parent class for a given {@code classId}
     */
    public LongMap<SkillLearn> getCompleteClassSkillTree(ClassId classId) {
        final LongMap<SkillLearn> skillTree = new HashLongMap<>(commonSkillTree);
        while ((classId != null) && (classSkillTrees.get(classId) != null)) {
            skillTree.putAll(classSkillTrees.get(classId));
            classId = parentClassMap.get(classId);
        }
        return skillTree;
    }

    /**
     * Gets the fishing skill tree.
     *
     * @return the complete Fishing Skill Tree
     */
    public LongMap<SkillLearn> getFishingSkillTree() {
        return fishingSkillTree;
    }

    public void forEachHeroSkill(Consumer<Skill> action) {
        for (var learn : heroSkillTree.values()) {
            action.accept(learn.getSkill());
        }
    }

    public boolean hasAvailableSkills(Player player, ClassId classId) {
        final LongMap<SkillLearn> skills = getCompleteClassSkillTree(classId);

        for (SkillLearn skill : skills.values()) {
            if ((skill.id() == CommonSkill.DIVINE_INSPIRATION.getId()) || skill.autoLearn() || (skill.requiredLevel() > player.getLevel())) {
                continue;
            }
            final Skill oldSkill = player.getKnownSkill(skill.id());
            if ((oldSkill != null && oldSkill.getLevel() == skill.level() - 1) || (oldSkill == null && skill.level() == 1)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the available skills.
     *
     * @param player         the learning skill player
     * @param classId        the learning skill class Id
     * @param includeAutoGet if {@code true} Auto-Get skills will be included
     * @return all available skills for a given {@code player}, {@code classId}, {@code includeByFs} and {@code includeAutoGet}
     */
    public List<SkillLearn> getAvailableSkills(Player player, ClassId classId, boolean includeAutoGet) {
        return getAvailableSkills(player, classId, includeAutoGet, player);
    }

    /**
     * Gets the available skills.
     *
     * @param player         the learning skill player
     * @param classId        the learning skill class Id
     * @param includeAutoGet if {@code true} Auto-Get skills will be included
     * @param holder         the skill holder
     * @return all available skills for a given {@code player}, {@code classId}, {@code includeByFs} and {@code includeAutoGet}
     */
    private List<SkillLearn> getAvailableSkills(Player player, ClassId classId, boolean includeAutoGet, ISkillsHolder holder) {
        final List<SkillLearn> result = new LinkedList<>();
        final LongMap<SkillLearn> skills = getCompleteClassSkillTree(classId);

        if (skills.isEmpty()) {
            // The Skill Tree for this class is undefined.
            LOGGER.warn("Skilltree for class {} is not defined!", classId);
            return result;
        }

        for (var entry : skills.entrySet()) {
            final SkillLearn skill = entry.getValue();

            if(skill.autoLearn() && !includeAutoGet) {
                continue;
            }

            if (player.getLevel() >= skill.requiredLevel()) {
                if (skill.level() > SkillEngine.getInstance().getMaxLevel(skill.id())) {
                    LOGGER.warn("SkillTreesData found learnable skill {} with level higher than max skill level!", skill.id());
                    continue;
                }

                final Skill oldSkill = holder.getKnownSkill(skill.id());
                checkSkillLevel(result, skill, oldSkill);
            }
        }
        return result;
    }

    public Collection<Skill> getAllAvailableSkills(Player player, ClassId classId, boolean includeAutoGet) {
        final PlayerSkillHolder holder = new PlayerSkillHolder(player);
        List<SkillLearn> learnable;
        do {
            learnable = getAvailableSkills(player, classId, includeAutoGet, holder);
            for (var skillLearn : learnable) {
                var skill = skillLearn.getSkill();
                if (!checkReplacement(learnable, skillLearn, skill)) {
                    holder.addSkill(skill);
                }
            }
        } while (!learnable.isEmpty());
        return holder.getSkills().values();
    }

    private boolean checkReplacement(List<SkillLearn> learnable, SkillLearn skillLearn, Skill skill) {
        var replaced = false;
        var it = learnable.iterator();
        while (it.hasNext()) {
            var next = it.next();
            if(next.replaceSkills().contains(skill)) {
                replaced = true;
            }
            if(skillLearn.replaceSkills().contains(next.getSkill())) {
                it.remove();
            }
        }
        return replaced;
    }

    /**
     * Gets the available auto get skills.
     *
     * @param player the player requesting the Auto-Get skills
     * @return all the available Auto-Get skills for a given {@code player}
     */
    public List<SkillLearn> getAvailableAutoGetSkills(Player player) {
        final List<SkillLearn> result = new ArrayList<>();
        final LongMap<SkillLearn> skills = getCompleteClassSkillTree(player.getClassId());
        if (skills.isEmpty()) {
            // The Skill Tree for this class is undefined, so we return an empty list.
            LOGGER.warn("Skill Tree for this class Id({}) is not defined!", player.getClassId());
            return result;
        }

        final Race race = player.getRace();

        for (SkillLearn skill : skills.values()) {
            if (!skill.races().isEmpty() && !skill.races().contains(race)) {
                continue;
            }

            if (skill.autoLearn() && (player.getLevel() >= skill.requiredLevel())) {
                final Skill oldSkill = player.getKnownSkill(skill.id());
                if (oldSkill != null) {
                    if (oldSkill.getLevel() < skill.level()) {
                        result.add(skill);
                    }
                } else {
                    result.add(skill);
                }
            }
        }
        return result;
    }

    /**
     * Dwarvens will get additional dwarven only fishing skills.
     *
     * @param player the player
     * @return all the available Fishing skills for a given {@code player}
     */
    public List<SkillLearn> getAvailableFishingSkills(Player player) {
        final List<SkillLearn> result = new ArrayList<>();
        final Race playerRace = player.getRace();
        for (SkillLearn skill : fishingSkillTree.values()) {
            // If skill is Race specific and the player's race isn't allowed, skip it.
            if (!skill.races().isEmpty() && !skill.races().contains(playerRace)) {
                continue;
            }

            if (skill.learnedByNpc() && (player.getLevel() >= skill.requiredLevel())) {
                final Skill oldSkill = player.getSkills().get(skill.id());
                checkSkillLevel(result, skill, oldSkill);
            }
        }
        return result;
    }

    private void checkSkillLevel(List<SkillLearn> result, SkillLearn skill, Skill oldSkill) {
        if (oldSkill != null) {
            if (oldSkill.getLevel() == (skill.level() - 1)) {
                result.add(skill);
            }
        } else if (skill.level() == 1) {
            result.add(skill);
        }
    }

    /**
     * Gets the available pledge skills.
     *
     * @param clan the pledge skill learning clan
     * @return all the available Pledge skills for a given {@code clan}
     */
    public List<SkillLearn> getAvailablePledgeSkills(Clan clan) {
        final List<SkillLearn> result = new ArrayList<>();

        for (SkillLearn skill : pledgeSkillTree.values()) {
            if (!skill.isResidential() && clan.getLevel() >= skill.requiredLevel()) {
                final Skill oldSkill = clan.getSkills().get(skill.id());
                if (oldSkill != null) {
                    if ((oldSkill.getLevel() + 1) == skill.level()) {
                        result.add(skill);
                    }
                } else if (skill.level() == 1) {
                    result.add(skill);
                }
            }
        }
        return result;
    }

    /**
     * Gets the available pledge skills.
     *
     * @param clan         the pledge skill learning clan
     * @return all the available pledge skills for a given {@code clan}
     */
    public IntMap<SkillLearn> getMaxPledgeSkills(Clan clan) {
        final IntMap<SkillLearn> result = new HashIntMap<>();
        for (SkillLearn skill : pledgeSkillTree.values()) {
            if (!skill.isResidential() && clan.getLevel() >= skill.requiredLevel()) {
                checkClanSkillLevel(clan, result, skill);
            }
        }
        return result;
    }

    private void checkClanSkillLevel(Clan clan, IntMap<SkillLearn> result, SkillLearn skill) {
        final Skill oldSkill = clan.getSkills().get(skill.id());
        if ((oldSkill == null) || (oldSkill.getLevel() < skill.level())) {
            result.put(skill.id(), skill);
        }
    }

    /**
     * Gets the available residential skills.
     *
     * @param residenceId the id of the Castle, Fort, Territory
     * @return all the available Residential skills for a given {@code residenceId}
     */
    public List<SkillLearn> getAvailableResidentialSkills(int residenceId) {
        List<SkillLearn> list = new ArrayList<>();
        for (var s : pledgeSkillTree.values()) {
            if (s.residences().contains(residenceId)) {
                list.add(s);
            }
        }
        return list;
    }

    /**
     * Just a wrapper for all skill trees.
     *
     * @param skillType the skill type
     * @param id        the skill Id
     * @param lvl       the skill level
     * @param player    the player learning the skill
     * @return the skill learn for the specified parameters
     */
    public SkillLearn getSkillLearn(AcquireSkillType skillType, int id, int lvl, Player player) {
        return switch (skillType) {
            case CLASS -> getClassSkill(id, lvl, player.getClassId());
            case TRANSFORM -> getTransformSkill(id, lvl);
            case FISHING ->  getFishingSkill(id, lvl);
            case PLEDGE -> getPledgeSkill(id, lvl);
            default -> null;
        };
    }

    /**
     * Gets the transform skill.
     *
     * @param id  the transformation skill Id
     * @param lvl the transformation skill level
     * @return the transform skill from the Transform Skill Tree for a given {@code id} and {@code lvl}
     */
    private SkillLearn getTransformSkill(int id, int lvl) {
        return transformSkillTree.get(SkillEngine.skillHashCode(id, lvl));
    }

    /**
     * Gets the class skill.
     *
     * @param id      the class skill Id
     * @param lvl     the class skill level.
     * @param classId the class skill tree Id
     * @return the class skill from the Class Skill Trees for a given {@code classId}, {@code id} and {@code lvl}
     */
    public SkillLearn getClassSkill(int id, int lvl, ClassId classId) {
        return getCompleteClassSkillTree(classId).get(SkillEngine.skillHashCode(id, lvl));
    }

    /**
     * Gets the fishing skill.
     *
     * @param id  the fishing skill Id
     * @param lvl the fishing skill level
     * @return Fishing skill from the Fishing Skill Tree for a given {@code id} and {@code lvl}
     */
    private SkillLearn getFishingSkill(int id, int lvl) {
        return fishingSkillTree.get(SkillEngine.skillHashCode(id, lvl));
    }

    /**
     * Gets the pledge skill.
     *
     * @param id  the pledge skill Id
     * @param lvl the pledge skill level
     * @return the pledge skill from the Pledge Skill Tree for a given {@code id} and {@code lvl}
     */
    public SkillLearn getPledgeSkill(int id, int lvl) {
        return pledgeSkillTree.get(SkillEngine.skillHashCode(id, lvl));
    }

    /**
     * Gets the minimum level for new skill.
     *
     * @param player    the player that requires the minimum level
     * @param skillTree the skill tree to search the minimum get level
     * @return the minimum level for a new skill for a given {@code player} and {@code skillTree}
     */
    public int getMinLevelForNewSkill(Player player, LongMap<SkillLearn> skillTree) {
        int minLevel = 0;
        if (skillTree.isEmpty()) {
            LOGGER.warn(": SkillTree is not defined for getMinLevelForNewSkill!");
        } else {
            for (SkillLearn s : skillTree.values()) {
                if (player.getLevel() < s.requiredLevel() && (minLevel == 0 || minLevel > s.requiredLevel())) {
                    minLevel = s.requiredLevel();
                }
            }
        }
        return minLevel;
    }

    public List<SkillLearn> getNextAvailableSkills(Player player, ClassId classId, boolean includeAutoGet) {
        final LongMap<SkillLearn> completeClassSkillTree = getCompleteClassSkillTree(classId);
        final List<SkillLearn> result = new LinkedList<>();
        if (completeClassSkillTree.isEmpty()) {
            return result;
        }
        final int minLevelForNewSkill = getMinLevelForNewSkill(player, completeClassSkillTree);

        if (minLevelForNewSkill > 0) {
            for (SkillLearn skill : completeClassSkillTree.values()) {
                if (!includeAutoGet && skill.autoLearn()) {
                    continue;
                }
                if (minLevelForNewSkill <= skill.requiredLevel()) {
                    final Skill oldSkill = player.getKnownSkill(skill.id());
                    checkSkillLevel(result, skill, oldSkill);
                }
            }
        }
        return result;
    }


    /**
     * Checks if is hero skill.
     *
     * @param skillId    the Id of the skill to check
     * @param skillLevel the level of the skill to check, if it's -1 only Id will be checked
     * @return {@code true} if the skill is present in the Hero Skill Tree, {@code false} otherwise
     */
    public boolean isHeroSkill(int skillId, int skillLevel) {
        return heroSkillTree.containsKey(SkillEngine.skillHashCode(skillId, skillLevel));
    }

    /**
     * Checks if is GM skill.
     *
     * @param skillId    the Id of the skill to check
     * @param skillLevel the level of the skill to check, if it's -1 only Id will be checked
     * @return {@code true} if the skill is present in the Game Master Skill Trees, {@code false} otherwise
     */
    public boolean isGMSkill(int skillId, int skillLevel) {
        final long hashCode = SkillEngine.skillHashCode(skillId, skillLevel);
        return gameMasterSkillTree.containsKey(hashCode);
    }

    /**
     * Checks if a skill is a Clan skill.
     *
     * @param skillId    the Id of the skill to check
     * @param skillLevel the level of the skill to check
     * @return {@code true} if the skill is present in the Pledge or Subpledge Skill Trees, {@code false} otherwise
     */
    public boolean isClanSkill(int skillId, int skillLevel) {
        final long hashCode = SkillEngine.skillHashCode(skillId, skillLevel);
        return pledgeSkillTree.containsKey(hashCode);
    }

    public void addGMSkills(Player gm) {
        for (SkillLearn learn : gameMasterSkillTree.values()) {
            gm.addSkill(learn.getSkill(), false);
        }
    }

    /**
     * Create and store hash values for skills for easy and fast checks.
     */
    private void generateCheckArrays() {
        int i;
        long[] array;

        // Class specific skills:
        LongMap<SkillLearn> tempMap;
        final Set<ClassId> keySet = classSkillTrees.keySet();
        skillsByClassIdHashCodes = new HashIntMap<>(keySet.size());
        for (ClassId cls : keySet) {
            tempMap = getCompleteClassSkillTree(cls);
            array = tempMap.keySet().toArray();
            tempMap.clear();
            Arrays.sort(array);
            skillsByClassIdHashCodes.put(cls.getId(), array);
        }

        // Race specific skills from Fishing and Transformation skill trees.
        final List<Long> list = new ArrayList<>();
        skillsByRaceHashCodes = new HashIntMap<>(Race.values().length);
        for (Race r : Race.values()) {
            for (SkillLearn s : fishingSkillTree.values()) {
                if (s.races().contains(r)) {
                    list.add(SkillEngine.skillHashCode(s.id(), s.level()));
                }
            }

            for (SkillLearn s : transformSkillTree.values()) {
                if (s.races().contains(r)) {
                    list.add(SkillEngine.skillHashCode(s.id(), s.level()));
                }
            }

            i = 0;
            array = new long[list.size()];
            for (long s : list) {
                array[i++] = s;
            }
            Arrays.sort(array);
            skillsByRaceHashCodes.put(r.ordinal(), array);
            list.clear();
        }

        // Skills available for all classes and races
        for (SkillLearn s : commonSkillTree.values()) {
            if (s.races().isEmpty()) {
                list.add(SkillEngine.skillHashCode(s.id(), s.level()));
            }
        }

        for (SkillLearn s : fishingSkillTree.values()) {
            if (s.races().isEmpty()) {
                list.add(SkillEngine.skillHashCode(s.id(), s.level()));
            }
        }

        for (SkillLearn s : transformSkillTree.values()) {
            if (s.races().isEmpty()) {
                list.add(SkillEngine.skillHashCode(s.id(), s.level()));
            }
        }

        allSkillsHashCodes = new long[list.size()];
        int j = 0;
        for (long hashcode : list) {
            allSkillsHashCodes[j++] = hashcode;
        }
        Arrays.sort(allSkillsHashCodes);
    }

    /**
     * Verify if the give skill is valid for the given player.<br>
     * GM's skills are excluded for GM players
     *
     * @param player the player to verify the skill
     * @param skill  the skill to be verified
     * @return {@code true} if the skill is allowed to the given player
     */
    public boolean isSkillAllowed(Player player, Skill skill) {
        if (skill.isExcludedFromCheck()) {
            return true;
        }

        if (player.isGM() && skill.isGMSkill()) {
            return true;
        }

        // Prevent accidental skill remove during reload
        if (isLoading.get()) {
            return true;
        }

        final int maxLvl = SkillEngine.getInstance().getMaxLevel(skill.getId());
        final long hashCode = SkillEngine.skillHashCode(skill.getId(), Math.min(skill.getLevel(), maxLvl));

        if (Arrays.binarySearch(skillsByClassIdHashCodes.get(player.getClassId().getId()), hashCode) >= 0) {
            return true;
        }

        if (Arrays.binarySearch(skillsByRaceHashCodes.get(player.getRace().ordinal()), hashCode) >= 0) {
            return true;
        }

        return Arrays.binarySearch(allSkillsHashCodes, hashCode) >= 0;

    }

    private void report() {
        int classSkillTreeCount = classSkillTrees.values().stream().mapToInt(LongMap::size).sum();
        var dwarvenOnlyFishingSkillCount = fishingSkillTree.values().stream().filter(s -> s.races().contains(Race.DWARF)).count();

        LOGGER.info("Loaded {} Class Skills for {} Class Skill Trees",  classSkillTreeCount, classSkillTrees.size());
        LOGGER.info("Loaded {} Fishing Skills, {} Dwarven only Fishing Skills",  fishingSkillTree.size(), dwarvenOnlyFishingSkillCount);
        LOGGER.info("Loaded {} Pledge Skills, {} for Pledge",  pledgeSkillTree.size(), pledgeSkillTree.size());
        LOGGER.info("Loaded {} Transform Skills.", transformSkillTree.size());
        LOGGER.info("Loaded {} Hero Skills.", heroSkillTree.size());
        LOGGER.info("Loaded {} Game Master Skills.", gameMasterSkillTree.size());
        LOGGER.info("Loaded {} Common Skills to all classes.", commonSkillTree.size());
    }

    public static void init() {
        getInstance().load();
    }

    public static SkillTreesData getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        protected static final SkillTreesData INSTANCE = new SkillTreesData();
    }
}
