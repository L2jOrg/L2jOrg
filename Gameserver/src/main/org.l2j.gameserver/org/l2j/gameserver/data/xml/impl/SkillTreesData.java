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

import io.github.joealisson.primitive.*;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.SkillLearn;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.base.AcquireSkillType;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.base.SocialClass;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.holders.PlayerSkillHolder;
import org.l2j.gameserver.model.holders.SkillHolder;
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
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.commons.configuration.Configurator.getSettings;

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

    // ClassId, Map of Skill Hash Code, SkillLearn
    private static final Map<ClassId, LongMap<SkillLearn>> classSkillTrees = new HashMap<>();
    // Skill Hash Code, SkillLearn
    private static final LongMap<SkillLearn> fishingSkillTree = new HashLongMap<>();
    private static final LongMap<SkillLearn> pledgeSkillTree = new HashLongMap<>();
    private static final LongMap<SkillLearn> subPledgeSkillTree = new HashLongMap<>();
    private static final LongMap<SkillLearn> transformSkillTree = new HashLongMap<>();
    private static final LongMap<SkillLearn> commonSkillTree = new HashLongMap<>();
    // Other skill trees
    private static final LongMap<SkillLearn> nobleSkillTree = new HashLongMap<>();
    private static final LongMap<SkillLearn> heroSkillTree = new HashLongMap<>();
    private static final LongMap<SkillLearn> gameMasterSkillTree = new HashLongMap<>();
    private static final LongMap<SkillLearn> gameMasterAuraSkillTree = new HashLongMap<>();
    // Remove skill tree
    private static final Map<ClassId, IntSet> removeSkillCache = new HashMap<>();
    /**
     * Parent class Ids are read from XML and stored in this map, to allow easy customization.
     */
    private static final Map<ClassId, ClassId> parentClassMap = new HashMap<>();
    private final AtomicBoolean isLoading = new AtomicBoolean();
    // Checker, sorted arrays of hash codes
    private IntMap<long[]> _skillsByClassIdHashCodes; // Occupation skills
    private IntMap<long[]> _skillsByRaceHashCodes; // Race-specific Transformations
    private long[] _allSkillsHashCodes; // Fishing, Collection, Transformations, Common Skills.

    private SkillTreesData() {
    }

    @Override
    protected Path getSchemaFilePath() {
        return getSettings(ServerSettings.class).dataPackDirectory().resolve("data/xsd/skillTrees.xsd");
    }

    @Override
    public void load() {
        isLoading.set(true);

        classSkillTrees.clear();
        fishingSkillTree.clear();
        pledgeSkillTree.clear();
        subPledgeSkillTree.clear();
        transformSkillTree.clear();
        nobleSkillTree.clear();
        heroSkillTree.clear();
        gameMasterSkillTree.clear();
        gameMasterAuraSkillTree.clear();
        removeSkillCache.clear();

        parseDatapackDirectory("data/skillTrees/", true);

        // Generate check arrays.
        generateCheckArrays();

        // Logs a report with skill trees info.
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
        var cId = parseInteger(attributes, "classId", -1);
        var parentId = parseInteger(attributes, "parentClassId", -1);

        ClassId classId = ClassId.getClassId(cId);

        if(nonNull(classId) && parentId > -1 && !cId.equals(parentId)) {
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
        final SkillLearn skillLearn = new SkillLearn(new StatsSet(parseAttributes(skillNode)));

        // test if skill exists
        SkillEngine.getInstance().getSkill(skillLearn.getSkillId(), skillLearn.getSkillLevel());

        for (Node b = skillNode.getFirstChild(); b != null; b = b.getNextSibling()) {
            var attrs = b.getAttributes();

            switch (b.getNodeName()) {
                case "item" -> skillLearn.addRequiredItem(new ItemHolder(parseInteger(attrs, "id"), parseInteger(attrs, "count")));
                case "preRequisiteSkill" -> skillLearn.addPreReqSkill(new SkillHolder(parseInteger(attrs, "id"), parseInteger(attrs, "lvl")));
                case "race" -> skillLearn.addRace(Race.valueOf(b.getTextContent()));
                case "residenceId" -> skillLearn.addResidenceId(Integer.valueOf(b.getTextContent()));
                case "socialClass" -> skillLearn.setSocialClass(Enum.valueOf(SocialClass.class, b.getTextContent()));
                case "removeSkill" -> {
                    final int removeSkillId = parseInteger(attrs, "id");
                    skillLearn.addRemoveSkills(removeSkillId);
                    removeSkillCache.computeIfAbsent(classId, k -> new HashIntSet()).add(removeSkillId);
                }
            }
        }

        final long skillHashCode = SkillEngine.skillHashCode(skillLearn.getSkillId(), skillLearn.getSkillLevel());
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
            case "subPledgeSkillTree" -> subPledgeSkillTree.put(skillHashCode, skillLearn);
            case "transformSkillTree" -> transformSkillTree.put(skillHashCode, skillLearn);
            case "nobleSkillTree" -> nobleSkillTree.put(skillHashCode, skillLearn);
            case "heroSkillTree" -> heroSkillTree.put(skillHashCode, skillLearn);
            case "gameMasterSkillTree" -> gameMasterSkillTree.put(skillHashCode, skillLearn);
            case "gameMasterAuraSkillTree" -> gameMasterAuraSkillTree.put(skillHashCode, skillLearn);
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
        // Add all skills that belong to all classes.
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


    /**
     * Gets the noble skill tree.
     *
     * @return the complete Noble Skill Tree
     */
    public List<Skill> getNobleSkillTree() {
        return nobleSkillTree.values().stream().map(entry -> SkillEngine.getInstance().getSkill(entry.getSkillId(), entry.getSkillLevel())).collect(Collectors.toList());
    }

    /**
     * Gets the noble skill tree.
     *
     * @return the complete Noble Skill Tree
     */
    public List<Skill> getNobleSkillAutoGetTree() {
        return nobleSkillTree.values().stream().filter(SkillLearn::isAutoGet).map(entry -> SkillEngine.getInstance().getSkill(entry.getSkillId(), entry.getSkillLevel())).collect(Collectors.toList());
    }

    /**
     * Gets the hero skill tree.
     *
     * @return the complete Hero Skill Tree
     */
    public List<Skill> getHeroSkillTree() {
        return heroSkillTree.values().stream().map(entry -> SkillEngine.getInstance().getSkill(entry.getSkillId(), entry.getSkillLevel())).collect(Collectors.toList());
    }

    /**
     * Gets the Game Master skill tree.
     *
     * @return the complete Game Master Skill Tree
     */
    public List<Skill> getGMSkillTree() {
        return gameMasterSkillTree.values().stream().map(entry -> SkillEngine.getInstance().getSkill(entry.getSkillId(), entry.getSkillLevel())).collect(Collectors.toList());
    }

    /**
     * Gets the Game Master Aura skill tree.
     *
     * @return the complete Game Master Aura Skill Tree
     */
    public List<Skill> getGMAuraSkillTree() {
        return gameMasterAuraSkillTree.values().stream().map(entry -> SkillEngine.getInstance().getSkill(entry.getSkillId(), entry.getSkillLevel())).collect(Collectors.toList());
    }

    public boolean hasAvailableSkills(Player player, ClassId classId) {
        final LongMap<SkillLearn> skills = getCompleteClassSkillTree(classId);

        for (SkillLearn skill : skills.values()) {
            if ((skill.getSkillId() == CommonSkill.DIVINE_INSPIRATION.getId()) || skill.isAutoGet() || skill.isLearnedByFS() || (skill.getGetLevel() > player.getLevel())) {
                continue;
            }
            final Skill oldSkill = player.getKnownSkill(skill.getSkillId());
            if ((oldSkill != null) && (oldSkill.getLevel() == (skill.getSkillLevel() - 1))) {
                return true;
            } else if ((oldSkill == null) && (skill.getSkillLevel() == 1)) {
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
     * @param includeByFs    if {@code true} skills from Forgotten Scroll will be included
     * @param includeAutoGet if {@code true} Auto-Get skills will be included
     * @return all available skills for a given {@code player}, {@code classId}, {@code includeByFs} and {@code includeAutoGet}
     */
    public List<SkillLearn> getAvailableSkills(Player player, ClassId classId, boolean includeByFs, boolean includeAutoGet) {
        return getAvailableSkills(player, classId, includeByFs, includeAutoGet, player);
    }

    /**
     * Gets the available skills.
     *
     * @param player         the learning skill player
     * @param classId        the learning skill class Id
     * @param includeByFs    if {@code true} skills from Forgotten Scroll will be included
     * @param includeAutoGet if {@code true} Auto-Get skills will be included
     * @param holder         the skill holder
     * @return all available skills for a given {@code player}, {@code classId}, {@code includeByFs} and {@code includeAutoGet}
     */
    private List<SkillLearn> getAvailableSkills(Player player, ClassId classId, boolean includeByFs, boolean includeAutoGet, ISkillsHolder holder) {
        final List<SkillLearn> result = new LinkedList<>();
        final LongMap<SkillLearn> skills = getCompleteClassSkillTree(classId);

        if (skills.isEmpty()) {
            // The Skill Tree for this class is undefined.
            LOGGER.warn("Skilltree for class {} is not defined!", classId);
            return result;
        }

        for (var entry : skills.entrySet()) {
            final SkillLearn skill = entry.getValue();

            if(skill.isAutoGet() && !includeAutoGet || skill.isLearnedByFS() && !includeByFs || isRemoveSkill(classId, skill.getSkillId())) {
                continue;
            }

            if (player.getLevel() >= skill.getGetLevel()) {
                if (skill.getSkillLevel() > SkillEngine.getInstance().getMaxLevel(skill.getSkillId())) {
                    LOGGER.warn("SkillTreesData found learnable skill {} with level higher than max skill level!", skill.getSkillId());
                    continue;
                }

                final Skill oldSkill = holder.getKnownSkill(skill.getSkillId());
                checkSkillLevel(result, skill, oldSkill);
            }
        }
        return result;
    }

    public Collection<Skill> getAllAvailableSkills(Player player, ClassId classId, boolean includeByFs, boolean includeAutoGet) {
        // Get available skills
        final PlayerSkillHolder holder = new PlayerSkillHolder(player);
        final Set<Integer> removed = new HashSet<>();
        for (int i = 0; i < 1000; i++) // Infinite loop warning
        {
            final List<SkillLearn> learnable = getAvailableSkills(player, classId, includeByFs, includeAutoGet, holder);
            if (learnable.isEmpty()) {
                // No more skills to learn
                break;
            }

            if (learnable.stream().allMatch(skillLearn -> removed.contains(skillLearn.getSkillId()))) {
                // All remaining skills has been removed
                break;
            }

            for (SkillLearn skillLearn : learnable) {
                final Skill skill = SkillEngine.getInstance().getSkill(skillLearn.getSkillId(), skillLearn.getSkillLevel());
                // Cleanup skills that has to be removed
                for (int skillId : skillLearn.getRemoveSkills()) {
                    // Mark skill as removed, so it doesn't gets added
                    removed.add(skillId);

                    // Remove skill from player's skill list or prepared holder's skill list
                    final Skill playerSkillToRemove = player.getKnownSkill(skillId);
                    final Skill holderSkillToRemove = holder.getKnownSkill(skillId);

                    // If player has the skill remove it
                    if (playerSkillToRemove != null) {
                        player.removeSkill(playerSkillToRemove);
                    }

                    // If holder already contains the skill remove it
                    if (holderSkillToRemove != null) {
                        holder.removeSkill(holderSkillToRemove);
                    }
                }

                if (!removed.contains(skill.getId())) {
                    holder.addSkill(skill);
                }
            }
        }
        return holder.getSkills().values();
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
            if (!skill.getRaces().isEmpty() && !skill.getRaces().contains(race)) {
                continue;
            }

            if (skill.isAutoGet() && (player.getLevel() >= skill.getGetLevel())) {
                final Skill oldSkill = player.getKnownSkill(skill.getSkillId());
                if (oldSkill != null) {
                    if (oldSkill.getLevel() < skill.getSkillLevel()) {
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
            if (!skill.getRaces().isEmpty() && !skill.getRaces().contains(playerRace)) {
                continue;
            }

            if (skill.isLearnedByNpc() && (player.getLevel() >= skill.getGetLevel())) {
                final Skill oldSkill = player.getSkills().get(skill.getSkillId());
                checkSkillLevel(result, skill, oldSkill);
            }
        }
        return result;
    }

    private void checkSkillLevel(List<SkillLearn> result, SkillLearn skill, Skill oldSkill) {
        if (oldSkill != null) {
            if (oldSkill.getLevel() == (skill.getSkillLevel() - 1)) {
                result.add(skill);
            }
        } else if (skill.getSkillLevel() == 1) {
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
            if (!skill.isResidencialSkill() && (clan.getLevel() >= skill.getGetLevel())) {
                final Skill oldSkill = clan.getSkills().get(skill.getSkillId());
                if (oldSkill != null) {
                    if ((oldSkill.getLevel() + 1) == skill.getSkillLevel()) {
                        result.add(skill);
                    }
                } else if (skill.getSkillLevel() == 1) {
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
     * @param includeSquad if squad skill will be added too
     * @return all the available pledge skills for a given {@code clan}
     */
    public Map<Integer, SkillLearn> getMaxPledgeSkills(Clan clan, boolean includeSquad) {
        final Map<Integer, SkillLearn> result = new HashMap<>();
        for (SkillLearn skill : pledgeSkillTree.values()) {
            if (!skill.isResidencialSkill() && (clan.getLevel() >= skill.getGetLevel())) {
                checkClanSkillLevel(clan, result, skill);
            }
        }

        if (includeSquad) {
            for (SkillLearn skill : subPledgeSkillTree.values()) {
                if ((clan.getLevel() >= skill.getGetLevel())) {
                    checkClanSkillLevel(clan, result, skill);
                }
            }
        }
        return result;
    }

    private void checkClanSkillLevel(Clan clan, Map<Integer, SkillLearn> result, SkillLearn skill) {
        final Skill oldSkill = clan.getSkills().get(skill.getSkillId());
        if ((oldSkill == null) || (oldSkill.getLevel() < skill.getSkillLevel())) {
            result.put(skill.getSkillId(), skill);
        }
    }

    /**
     * Gets the available sub pledge skills.
     *
     * @param clan the sub-pledge skill learning clan
     * @return all the available Sub-Pledge skills for a given {@code clan}
     */
    public List<SkillLearn> getAvailableSubPledgeSkills(Clan clan) {
        final List<SkillLearn> result = new ArrayList<>();
        for (SkillLearn skill : subPledgeSkillTree.values()) {
            if ((clan.getLevel() >= skill.getGetLevel()) && clan.isLearnableSubSkill(skill.getSkillId(), skill.getSkillLevel())) {
                result.add(skill);
            }
        }
        return result;
    }

    /**
     * Gets the available residential skills.
     *
     * @param residenceId the id of the Castle, Fort, Territory
     * @return all the available Residential skills for a given {@code residenceId}
     */
    public List<SkillLearn> getAvailableResidentialSkills(int residenceId) {
        return pledgeSkillTree.values().stream().filter( s -> s.isResidencialSkill() && s.getResidenceIds().contains(residenceId)).collect(Collectors.toList());
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
        SkillLearn sl = null;
        switch (skillType) {
            case CLASS: {
                sl = getClassSkill(id, lvl, player.getClassId());
                break;
            }
            case TRANSFORM: {
                sl = getTransformSkill(id, lvl);
                break;
            }
            case FISHING: {
                sl = getFishingSkill(id, lvl);
                break;
            }
            case PLEDGE: {
                sl = getPledgeSkill(id, lvl);
                break;
            }
            case SUBPLEDGE: {
                sl = getSubPledgeSkill(id, lvl);
                break;
            }
        }
        return sl;
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
     * Gets the sub pledge skill.
     *
     * @param id  the sub-pledge skill Id
     * @param lvl the sub-pledge skill level
     * @return the sub-pledge skill from the Sub-Pledge Skill Tree for a given {@code id} and {@code lvl}
     */
    public SkillLearn getSubPledgeSkill(int id, int lvl) {
        return subPledgeSkillTree.get(SkillEngine.skillHashCode(id, lvl));
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
                if (player.getLevel() < s.getGetLevel()) {
                    if ((minLevel == 0) || (minLevel > s.getGetLevel())) {
                        minLevel = s.getGetLevel();
                    }
                }
            }
        }
        return minLevel;
    }

    public List<SkillLearn> getNextAvailableSkills(Player player, ClassId classId, boolean includeByFs, boolean includeAutoGet) {
        final LongMap<SkillLearn> completeClassSkillTree = getCompleteClassSkillTree(classId);
        final List<SkillLearn> result = new LinkedList<>();
        if (completeClassSkillTree.isEmpty()) {
            return result;
        }
        final int minLevelForNewSkill = getMinLevelForNewSkill(player, completeClassSkillTree);

        if (minLevelForNewSkill > 0) {
            for (SkillLearn skill : completeClassSkillTree.values()) {
                if ((!includeAutoGet && skill.isAutoGet()) || (!includeByFs && skill.isLearnedByFS())) {
                    continue;
                }
                if (minLevelForNewSkill <= skill.getGetLevel()) {
                    final Skill oldSkill = player.getKnownSkill(skill.getSkillId());
                    checkSkillLevel(result, skill, oldSkill);
                }
            }
        }
        return result;
    }

    public void cleanSkillUponAwakening(Player player) {
        for (Skill skill : player.getAllSkills()) {
            final int maxLvl = SkillEngine.getInstance().getMaxLevel(skill.getId());
            final long hashCode = SkillEngine.skillHashCode(skill.getId(), maxLvl);

            if (!isCurrentClassSkillNoParent(player.getClassId(), hashCode) && !isRemoveSkill(player.getClassId(), skill.getId())) {
                player.removeSkill(skill, true, true);
            }
        }
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
        return gameMasterSkillTree.containsKey(hashCode) || gameMasterAuraSkillTree.containsKey(hashCode);
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
        return pledgeSkillTree.containsKey(hashCode) || subPledgeSkillTree.containsKey(hashCode);
    }

    public boolean isRemoveSkill(ClassId classId, int skillId) {
        return removeSkillCache.getOrDefault(classId, Containers.emptyIntSet()).contains(skillId);
    }

    private boolean isCurrentClassSkillNoParent(ClassId classId, Long hashCode) {
        return classSkillTrees.getOrDefault(classId, Containers.emptyLongMap()).containsKey(hashCode);
    }

    /**
     * Adds the skills.
     *
     * @param gmchar     the player to add the Game Master skills
     * @param auraSkills if {@code true} it will add "GM Aura" skills, else will add the "GM regular" skills
     */
    public void addSkills(Player gmchar, boolean auraSkills) {
        final Collection<SkillLearn> skills = auraSkills ? gameMasterAuraSkillTree.values() : gameMasterSkillTree.values();
        final SkillEngine st = SkillEngine.getInstance();
        for (SkillLearn sl : skills) {
            gmchar.addSkill(st.getSkill(sl.getSkillId(), sl.getSkillLevel()), false); // Don't Save GM skills to database
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
        _skillsByClassIdHashCodes = new HashIntMap<>(keySet.size());
        for (ClassId cls : keySet) {
            tempMap = getCompleteClassSkillTree(cls);
            array = tempMap.keySet().toArray();
            tempMap.clear();
            Arrays.sort(array);
            _skillsByClassIdHashCodes.put(cls.getId(), array);
        }

        // Race specific skills from Fishing and Transformation skill trees.
        final List<Long> list = new ArrayList<>();
        _skillsByRaceHashCodes = new HashIntMap<>(Race.values().length);
        for (Race r : Race.values()) {
            for (SkillLearn s : fishingSkillTree.values()) {
                if (s.getRaces().contains(r)) {
                    list.add(SkillEngine.skillHashCode(s.getSkillId(), s.getSkillLevel()));
                }
            }

            for (SkillLearn s : transformSkillTree.values()) {
                if (s.getRaces().contains(r)) {
                    list.add(SkillEngine.skillHashCode(s.getSkillId(), s.getSkillLevel()));
                }
            }

            i = 0;
            array = new long[list.size()];
            for (long s : list) {
                array[i++] = s;
            }
            Arrays.sort(array);
            _skillsByRaceHashCodes.put(r.ordinal(), array);
            list.clear();
        }

        // Skills available for all classes and races
        for (SkillLearn s : commonSkillTree.values()) {
            if (s.getRaces().isEmpty()) {
                list.add(SkillEngine.skillHashCode(s.getSkillId(), s.getSkillLevel()));
            }
        }

        for (SkillLearn s : fishingSkillTree.values()) {
            if (s.getRaces().isEmpty()) {
                list.add(SkillEngine.skillHashCode(s.getSkillId(), s.getSkillLevel()));
            }
        }

        for (SkillLearn s : transformSkillTree.values()) {
            if (s.getRaces().isEmpty()) {
                list.add(SkillEngine.skillHashCode(s.getSkillId(), s.getSkillLevel()));
            }
        }

        _allSkillsHashCodes = new long[list.size()];
        int j = 0;
        for (long hashcode : list) {
            _allSkillsHashCodes[j++] = hashcode;
        }
        Arrays.sort(_allSkillsHashCodes);
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

        if (Arrays.binarySearch(_skillsByClassIdHashCodes.get(player.getClassId().getId()), hashCode) >= 0) {
            return true;
        }

        if (Arrays.binarySearch(_skillsByRaceHashCodes.get(player.getRace().ordinal()), hashCode) >= 0) {
            return true;
        }

        return Arrays.binarySearch(_allSkillsHashCodes, hashCode) >= 0;

    }

    private void report() {
        int classSkillTreeCount = classSkillTrees.values().stream().mapToInt(LongMap::size).sum();
        var dwarvenOnlyFishingSkillCount = fishingSkillTree.values().stream().filter(s -> s.getRaces().contains(Race.DWARF)).count();
        var resSkillCount = pledgeSkillTree.values().stream().filter(SkillLearn::isResidencialSkill).count();

        LOGGER.info("Loaded {} Class Skills for {} Class Skill Trees",  classSkillTreeCount, classSkillTrees.size());
        LOGGER.info("Loaded {} Fishing Skills, {} Dwarven only Fishing Skills",  fishingSkillTree.size(), dwarvenOnlyFishingSkillCount);
        LOGGER.info("Loaded {} Pledge Skills, {} for Pledge and {} Residential",  pledgeSkillTree.size(), pledgeSkillTree.size() - resSkillCount, resSkillCount);
        LOGGER.info("Loaded {} Sub-Pledge Skills.", subPledgeSkillTree.size());
        LOGGER.info("Loaded {} Transform Skills.", transformSkillTree.size());
        LOGGER.info("Loaded {} Noble Skills.", nobleSkillTree.size());
        LOGGER.info("Loaded {} Hero Skills.", heroSkillTree.size());
        LOGGER.info("Loaded {} Game Master Skills.", gameMasterSkillTree.size());
        LOGGER.info("Loaded {} Game Master Aura Skills.", gameMasterAuraSkillTree.size());
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
