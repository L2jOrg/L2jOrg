package org.l2j.gameserver.data.xml.impl;

import io.github.joealisson.primitive.Containers;
import io.github.joealisson.primitive.maps.IntObjectMap;
import io.github.joealisson.primitive.maps.LongObjectMap;
import io.github.joealisson.primitive.maps.impl.HashIntObjectMap;
import io.github.joealisson.primitive.maps.impl.HashLongObjectMap;
import io.github.joealisson.primitive.pair.LongObjectPair;
import io.github.joealisson.primitive.sets.IntSet;
import io.github.joealisson.primitive.sets.impl.HashIntSet;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.enums.CategoryType;
import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.model.L2Clan;
import org.l2j.gameserver.model.L2SkillLearn;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.base.AcquireSkillType;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.base.SocialClass;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.holders.PlayerSkillHolder;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.model.interfaces.ISkillsHolder;
import org.l2j.gameserver.model.skills.CommonSkill;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.GameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.File;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
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
 */
public final class SkillTreesData extends GameXmlReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(SkillTreesData.class.getName());

    // ClassId, Map of Skill Hash Code, L2SkillLearn
    private static final Map<ClassId, LongObjectMap<L2SkillLearn>> classSkillTrees = new HashMap<>();
    // Skill Hash Code, L2SkillLearn
    private static final LongObjectMap<L2SkillLearn> fishingSkillTree = new HashLongObjectMap<>();
    private static final LongObjectMap<L2SkillLearn> pledgeSkillTree = new HashLongObjectMap<>();
    private static final LongObjectMap<L2SkillLearn> subPledgeSkillTree = new HashLongObjectMap<>();
    private static final LongObjectMap<L2SkillLearn> transformSkillTree = new HashLongObjectMap<>();
    private static final LongObjectMap<L2SkillLearn> commonSkillTree = new HashLongObjectMap<>();
    // Other skill trees
    private static final LongObjectMap<L2SkillLearn> nobleSkillTree = new HashLongObjectMap<>();
    private static final LongObjectMap<L2SkillLearn> heroSkillTree = new HashLongObjectMap<>();
    private static final LongObjectMap<L2SkillLearn> gameMasterSkillTree = new HashLongObjectMap<>();
    private static final LongObjectMap<L2SkillLearn> gameMasterAuraSkillTree = new HashLongObjectMap<>();
    // Remove skill tree
    private static final Map<ClassId, IntSet> removeSkillCache = new HashMap<>();
    /**
     * Parent class Ids are read from XML and stored in this map, to allow easy customization.
     */
    private static final Map<ClassId, ClassId> _parentClassMap = new HashMap<>();
    private final AtomicBoolean isLoading = new AtomicBoolean();
    // Checker, sorted arrays of hash codes
    private IntObjectMap<long[]> _skillsByClassIdHashCodes; // Occupation skills
    private IntObjectMap<long[]> _skillsByRaceHashCodes; // Race-specific Transformations
    private long[] _allSkillsHashCodes; // Fishing, Collection, Transformations, Common Skills.

    private SkillTreesData() {
        load();
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
    }

    @Override
    public void parseDocument(Document doc, File f) {
        NamedNodeMap attrs;
        Node attr;
        String type;
        int cId;
        int parentClassId;
        ClassId classId = null;
        for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
            if ("list".equalsIgnoreCase(n.getNodeName())) {
                for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
                    if ("skillTree".equalsIgnoreCase(d.getNodeName())) {

                        final LongObjectMap<L2SkillLearn> classSkillTree = new HashLongObjectMap<>();

                        type = d.getAttributes().getNamedItem("type").getNodeValue();
                        attr = d.getAttributes().getNamedItem("classId");
                        if (attr != null) {
                            cId = Integer.parseInt(attr.getNodeValue());
                            classId = ClassId.values()[cId];
                        } else {
                            cId = -1;
                        }


                        attr = d.getAttributes().getNamedItem("parentClassId");
                        if (attr != null) {
                            parentClassId = Integer.parseInt(attr.getNodeValue());
                            if ((cId > -1) && (cId != parentClassId) && (parentClassId > -1) && !_parentClassMap.containsKey(classId)) {
                                _parentClassMap.put(classId, ClassId.values()[parentClassId]);
                            }
                        }

                        for (Node c = d.getFirstChild(); c != null; c = c.getNextSibling()) {
                            if ("skill".equalsIgnoreCase(c.getNodeName())) {
                                final StatsSet learnSkillSet = new StatsSet();
                                attrs = c.getAttributes();
                                for (int i = 0; i < attrs.getLength(); i++) {
                                    attr = attrs.item(i);
                                    learnSkillSet.set(attr.getNodeName(), attr.getNodeValue());
                                }

                                final L2SkillLearn skillLearn = new L2SkillLearn(learnSkillSet);

                                // test if skill exists
                                SkillData.getInstance().getSkill(skillLearn.getSkillId(), skillLearn.getSkillLevel());

                                for (Node b = c.getFirstChild(); b != null; b = b.getNextSibling()) {
                                    attrs = b.getAttributes();
                                    switch (b.getNodeName()) {
                                        case "item": {
                                            skillLearn.addRequiredItem(new ItemHolder(parseInteger(attrs, "id"), parseInteger(attrs, "count")));
                                            break;
                                        }
                                        case "preRequisiteSkill": {
                                            skillLearn.addPreReqSkill(new SkillHolder(parseInteger(attrs, "id"), parseInteger(attrs, "lvl")));
                                            break;
                                        }
                                        case "race": {
                                            skillLearn.addRace(Race.valueOf(b.getTextContent()));
                                            break;
                                        }
                                        case "residenceId": {
                                            skillLearn.addResidenceId(Integer.valueOf(b.getTextContent()));
                                            break;
                                        }
                                        case "socialClass": {
                                            skillLearn.setSocialClass(Enum.valueOf(SocialClass.class, b.getTextContent()));
                                            break;
                                        }
                                        case "removeSkill": {
                                            final int removeSkillId = parseInteger(attrs, "id");
                                            skillLearn.addRemoveSkills(removeSkillId);
                                            removeSkillCache.computeIfAbsent(classId, k -> new HashIntSet()).add(removeSkillId);
                                            break;
                                        }
                                    }
                                }

                                final long skillHashCode = SkillData.getSkillHashCode(skillLearn.getSkillId(), skillLearn.getSkillLevel());
                                switch (type) {
                                    case "classSkillTree": {
                                        if (cId != -1) {
                                            classSkillTree.put(skillHashCode, skillLearn);
                                        } else {
                                            commonSkillTree.put(skillHashCode, skillLearn);
                                        }
                                        break;
                                    }
                                    case "fishingSkillTree": {
                                        fishingSkillTree.put(skillHashCode, skillLearn);
                                        break;
                                    }
                                    case "pledgeSkillTree": {
                                        pledgeSkillTree.put(skillHashCode, skillLearn);
                                        break;
                                    }
                                    case "subPledgeSkillTree": {
                                        subPledgeSkillTree.put(skillHashCode, skillLearn);
                                        break;
                                    }
                                    case "transformSkillTree": {
                                        transformSkillTree.put(skillHashCode, skillLearn);
                                        break;
                                    }
                                    case "nobleSkillTree": {
                                        nobleSkillTree.put(skillHashCode, skillLearn);
                                        break;
                                    }
                                    case "heroSkillTree": {
                                        heroSkillTree.put(skillHashCode, skillLearn);
                                        break;
                                    }
                                    case "gameMasterSkillTree": {
                                        gameMasterSkillTree.put(skillHashCode, skillLearn);
                                        break;
                                    }
                                    case "gameMasterAuraSkillTree": {
                                        gameMasterAuraSkillTree.put(skillHashCode, skillLearn);
                                        break;
                                    }
                                    default: {
                                        LOGGER.warn("Unknown Skill Tree type: {}", type);
                                    }
                                }
                            }
                        }

                        if (type.equals("classSkillTree") && (cId > -1)) {
                            final var classSkillTrees = SkillTreesData.classSkillTrees.get(classId);
                            if (isNull(classSkillTrees)) {
                                SkillTreesData.classSkillTrees.put(classId, classSkillTree);
                            } else {
                                classSkillTrees.putAll(classSkillTree);
                            }
                        }
                    }
                }
            }
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
    public LongObjectMap<L2SkillLearn> getCompleteClassSkillTree(ClassId classId) {
        // Add all skills that belong to all classes.
        final LongObjectMap<L2SkillLearn> skillTree = new HashLongObjectMap<>(commonSkillTree);
        while ((classId != null) && (classSkillTrees.get(classId) != null)) {
            skillTree.putAll(classSkillTrees.get(classId));
            classId = _parentClassMap.get(classId);
        }
        return skillTree;
    }

    /**
     * Gets the fishing skill tree.
     *
     * @return the complete Fishing Skill Tree
     */
    public LongObjectMap<L2SkillLearn> getFishingSkillTree() {
        return fishingSkillTree;
    }


    /**
     * Gets the noble skill tree.
     *
     * @return the complete Noble Skill Tree
     */
    public List<Skill> getNobleSkillTree() {
        return nobleSkillTree.values().stream().map(entry -> SkillData.getInstance().getSkill(entry.getSkillId(), entry.getSkillLevel())).collect(Collectors.toList());
    }

    /**
     * Gets the noble skill tree.
     *
     * @return the complete Noble Skill Tree
     */
    public List<Skill> getNobleSkillAutoGetTree() {
        return nobleSkillTree.values().stream().filter(L2SkillLearn::isAutoGet).map(entry -> SkillData.getInstance().getSkill(entry.getSkillId(), entry.getSkillLevel())).collect(Collectors.toList());
    }

    /**
     * Gets the hero skill tree.
     *
     * @return the complete Hero Skill Tree
     */
    public List<Skill> getHeroSkillTree() {
        return heroSkillTree.values().stream().map(entry -> SkillData.getInstance().getSkill(entry.getSkillId(), entry.getSkillLevel())).collect(Collectors.toList());
    }

    /**
     * Gets the Game Master skill tree.
     *
     * @return the complete Game Master Skill Tree
     */
    public List<Skill> getGMSkillTree() {
        return gameMasterSkillTree.values().stream().map(entry -> SkillData.getInstance().getSkill(entry.getSkillId(), entry.getSkillLevel())).collect(Collectors.toList());
    }

    /**
     * Gets the Game Master Aura skill tree.
     *
     * @return the complete Game Master Aura Skill Tree
     */
    public List<Skill> getGMAuraSkillTree() {
        return gameMasterAuraSkillTree.values().stream().map(entry -> SkillData.getInstance().getSkill(entry.getSkillId(), entry.getSkillLevel())).collect(Collectors.toList());
    }

    public boolean hasAvailableSkills(L2PcInstance player, ClassId classId) {
        final LongObjectMap<L2SkillLearn> skills = getCompleteClassSkillTree(classId);

        for (L2SkillLearn skill : skills.values()) {
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
    public List<L2SkillLearn> getAvailableSkills(L2PcInstance player, ClassId classId, boolean includeByFs, boolean includeAutoGet) {
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
    private List<L2SkillLearn> getAvailableSkills(L2PcInstance player, ClassId classId, boolean includeByFs, boolean includeAutoGet, ISkillsHolder holder) {
        final List<L2SkillLearn> result = new LinkedList<>();
        final LongObjectMap<L2SkillLearn> skills = getCompleteClassSkillTree(classId);

        if (skills.isEmpty()) {
            // The Skill Tree for this class is undefined.
            LOGGER.warn(": Skilltree for class " + classId + " is not defined!");
            return result;
        }

        final boolean isAwaken = player.isInCategory(CategoryType.SIXTH_CLASS_GROUP) && (player.isDualClassActive());


        for (LongObjectPair<L2SkillLearn> entry : skills.entrySet()) {
            final L2SkillLearn skill = entry.getValue();

            if (((skill.getSkillId() == CommonSkill.DIVINE_INSPIRATION.getId()) && (!Config.AUTO_LEARN_DIVINE_INSPIRATION && includeAutoGet) && !player.isGM()) || (!includeAutoGet && skill.isAutoGet()) || (!includeByFs && skill.isLearnedByFS()) || isRemoveSkill(classId, skill.getSkillId())) {
                continue;
            }

            if (isAwaken && !isCurrentClassSkillNoParent(classId, entry.getKey())) {
                continue;
            }

            if (player.getLevel() >= skill.getGetLevel()) {
                if (skill.getSkillLevel() > SkillData.getInstance().getMaxLevel(skill.getSkillId())) {
                    LOGGER.error(": SkillTreesData found learnable skill " + skill.getSkillId() + " with level higher than max skill level!");
                    continue;
                }

                final Skill oldSkill = holder.getKnownSkill(skill.getSkillId());
                checkSkillLevel(result, skill, oldSkill);
            }
        }
        return result;
    }

    public Collection<Skill> getAllAvailableSkills(L2PcInstance player, ClassId classId, boolean includeByFs, boolean includeAutoGet) {
        // Get available skills
        final PlayerSkillHolder holder = new PlayerSkillHolder(player);
        final Set<Integer> removed = new HashSet<>();
        for (int i = 0; i < 1000; i++) // Infinite loop warning
        {
            final List<L2SkillLearn> learnable = getAvailableSkills(player, classId, includeByFs, includeAutoGet, holder);
            if (learnable.isEmpty()) {
                // No more skills to learn
                break;
            }

            if (learnable.stream().allMatch(skillLearn -> removed.contains(skillLearn.getSkillId()))) {
                // All remaining skills has been removed
                break;
            }

            for (L2SkillLearn skillLearn : learnable) {
                final Skill skill = SkillData.getInstance().getSkill(skillLearn.getSkillId(), skillLearn.getSkillLevel());
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
    public List<L2SkillLearn> getAvailableAutoGetSkills(L2PcInstance player) {
        final List<L2SkillLearn> result = new ArrayList<>();
        final LongObjectMap<L2SkillLearn> skills = getCompleteClassSkillTree(player.getClassId());
        if (skills.isEmpty()) {
            // The Skill Tree for this class is undefined, so we return an empty list.
            LOGGER.warn(": Skill Tree for this class Id(" + player.getClassId() + ") is not defined!");
            return result;
        }

        final Race race = player.getRace();
        final boolean isAwaken = player.isInCategory(CategoryType.SIXTH_CLASS_GROUP);

        for (L2SkillLearn skill : skills.values()) {
            if (!skill.getRaces().isEmpty() && !skill.getRaces().contains(race)) {
                continue;
            }

            final int maxLvl = SkillData.getInstance().getMaxLevel(skill.getSkillId());
            final long hashCode = SkillData.getSkillHashCode(skill.getSkillId(), maxLvl);

            if (skill.isAutoGet() && (player.getLevel() >= skill.getGetLevel())) {
                final Skill oldSkill = player.getKnownSkill(skill.getSkillId());
                if (oldSkill != null) {
                    if (oldSkill.getLevel() < skill.getSkillLevel()) {
                        result.add(skill);
                    }
                } else if (!isAwaken || isCurrentClassSkillNoParent(player.getClassId(), hashCode)) {
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
    public List<L2SkillLearn> getAvailableFishingSkills(L2PcInstance player) {
        final List<L2SkillLearn> result = new ArrayList<>();
        final Race playerRace = player.getRace();
        for (L2SkillLearn skill : fishingSkillTree.values()) {
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

    private void checkSkillLevel(List<L2SkillLearn> result, L2SkillLearn skill, Skill oldSkill) {
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
    public List<L2SkillLearn> getAvailablePledgeSkills(L2Clan clan) {
        final List<L2SkillLearn> result = new ArrayList<>();

        for (L2SkillLearn skill : pledgeSkillTree.values()) {
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
    public Map<Integer, L2SkillLearn> getMaxPledgeSkills(L2Clan clan, boolean includeSquad) {
        final Map<Integer, L2SkillLearn> result = new HashMap<>();
        for (L2SkillLearn skill : pledgeSkillTree.values()) {
            if (!skill.isResidencialSkill() && (clan.getLevel() >= skill.getGetLevel())) {
                checkClanSkillLevel(clan, result, skill);
            }
        }

        if (includeSquad) {
            for (L2SkillLearn skill : subPledgeSkillTree.values()) {
                if ((clan.getLevel() >= skill.getGetLevel())) {
                    checkClanSkillLevel(clan, result, skill);
                }
            }
        }
        return result;
    }

    private void checkClanSkillLevel(L2Clan clan, Map<Integer, L2SkillLearn> result, L2SkillLearn skill) {
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
    public List<L2SkillLearn> getAvailableSubPledgeSkills(L2Clan clan) {
        final List<L2SkillLearn> result = new ArrayList<>();
        for (L2SkillLearn skill : subPledgeSkillTree.values()) {
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
    public List<L2SkillLearn> getAvailableResidentialSkills(int residenceId) {
        final List<L2SkillLearn> result = new ArrayList<>();
        for (L2SkillLearn skill : pledgeSkillTree.values()) {
            if (skill.isResidencialSkill() && skill.getResidenceIds().contains(residenceId)) {
                result.add(skill);
            }
        }
        return result;
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
    public L2SkillLearn getSkillLearn(AcquireSkillType skillType, int id, int lvl, L2PcInstance player) {
        L2SkillLearn sl = null;
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
    private L2SkillLearn getTransformSkill(int id, int lvl) {
        return transformSkillTree.get(SkillData.getSkillHashCode(id, lvl));
    }

    /**
     * Gets the class skill.
     *
     * @param id      the class skill Id
     * @param lvl     the class skill level.
     * @param classId the class skill tree Id
     * @return the class skill from the Class Skill Trees for a given {@code classId}, {@code id} and {@code lvl}
     */
    public L2SkillLearn getClassSkill(int id, int lvl, ClassId classId) {
        return getCompleteClassSkillTree(classId).get(SkillData.getSkillHashCode(id, lvl));
    }

    /**
     * Gets the fishing skill.
     *
     * @param id  the fishing skill Id
     * @param lvl the fishing skill level
     * @return Fishing skill from the Fishing Skill Tree for a given {@code id} and {@code lvl}
     */
    private L2SkillLearn getFishingSkill(int id, int lvl) {
        return fishingSkillTree.get(SkillData.getSkillHashCode(id, lvl));
    }

    /**
     * Gets the pledge skill.
     *
     * @param id  the pledge skill Id
     * @param lvl the pledge skill level
     * @return the pledge skill from the Pledge Skill Tree for a given {@code id} and {@code lvl}
     */
    public L2SkillLearn getPledgeSkill(int id, int lvl) {
        return pledgeSkillTree.get(SkillData.getSkillHashCode(id, lvl));
    }

    /**
     * Gets the sub pledge skill.
     *
     * @param id  the sub-pledge skill Id
     * @param lvl the sub-pledge skill level
     * @return the sub-pledge skill from the Sub-Pledge Skill Tree for a given {@code id} and {@code lvl}
     */
    public L2SkillLearn getSubPledgeSkill(int id, int lvl) {
        return subPledgeSkillTree.get(SkillData.getSkillHashCode(id, lvl));
    }

    /**
     * Gets the minimum level for new skill.
     *
     * @param player    the player that requires the minimum level
     * @param skillTree the skill tree to search the minimum get level
     * @return the minimum level for a new skill for a given {@code player} and {@code skillTree}
     */
    public int getMinLevelForNewSkill(L2PcInstance player, LongObjectMap<L2SkillLearn> skillTree) {
        int minLevel = 0;
        if (skillTree.isEmpty()) {
            LOGGER.warn(": SkillTree is not defined for getMinLevelForNewSkill!");
        } else {
            for (L2SkillLearn s : skillTree.values()) {
                if (player.getLevel() < s.getGetLevel()) {
                    if ((minLevel == 0) || (minLevel > s.getGetLevel())) {
                        minLevel = s.getGetLevel();
                    }
                }
            }
        }
        return minLevel;
    }

    public List<L2SkillLearn> getNextAvailableSkills(L2PcInstance player, ClassId classId, boolean includeByFs, boolean includeAutoGet) {
        final LongObjectMap<L2SkillLearn> completeClassSkillTree = getCompleteClassSkillTree(classId);
        final List<L2SkillLearn> result = new LinkedList<>();
        if (completeClassSkillTree.isEmpty()) {
            return result;
        }
        final int minLevelForNewSkill = getMinLevelForNewSkill(player, completeClassSkillTree);

        if (minLevelForNewSkill > 0) {
            for (L2SkillLearn skill : completeClassSkillTree.values()) {
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

    public void cleanSkillUponAwakening(L2PcInstance player) {
        for (Skill skill : player.getAllSkills()) {
            final int maxLvl = SkillData.getInstance().getMaxLevel(skill.getId());
            final long hashCode = SkillData.getSkillHashCode(skill.getId(), maxLvl);

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
        return heroSkillTree.containsKey(SkillData.getSkillHashCode(skillId, skillLevel));
    }

    /**
     * Checks if is GM skill.
     *
     * @param skillId    the Id of the skill to check
     * @param skillLevel the level of the skill to check, if it's -1 only Id will be checked
     * @return {@code true} if the skill is present in the Game Master Skill Trees, {@code false} otherwise
     */
    public boolean isGMSkill(int skillId, int skillLevel) {
        final long hashCode = SkillData.getSkillHashCode(skillId, skillLevel);
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
        final long hashCode = SkillData.getSkillHashCode(skillId, skillLevel);
        return pledgeSkillTree.containsKey(hashCode) || subPledgeSkillTree.containsKey(hashCode);
    }

    public boolean isRemoveSkill(ClassId classId, int skillId) {
        return removeSkillCache.getOrDefault(classId, Containers.EMPTY_INT_SET).contains(skillId);
    }

    private boolean isCurrentClassSkillNoParent(ClassId classId, Long hashCode) {
        return classSkillTrees.getOrDefault(classId, Containers.emptyLongObjectMap()).containsKey(hashCode);
    }

    /**
     * Adds the skills.
     *
     * @param gmchar     the player to add the Game Master skills
     * @param auraSkills if {@code true} it will add "GM Aura" skills, else will add the "GM regular" skills
     */
    public void addSkills(L2PcInstance gmchar, boolean auraSkills) {
        final Collection<L2SkillLearn> skills = auraSkills ? gameMasterAuraSkillTree.values() : gameMasterSkillTree.values();
        final SkillData st = SkillData.getInstance();
        for (L2SkillLearn sl : skills) {
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
        LongObjectMap<L2SkillLearn> tempMap;
        final Set<ClassId> keySet = classSkillTrees.keySet();
        _skillsByClassIdHashCodes = new HashIntObjectMap<>(keySet.size());
        for (ClassId cls : keySet) {
            tempMap = getCompleteClassSkillTree(cls);
            array = tempMap.keySet().toArray();
            tempMap.clear();
            Arrays.sort(array);
            _skillsByClassIdHashCodes.put(cls.ordinal(), array);
        }

        // Race specific skills from Fishing and Transformation skill trees.
        final List<Long> list = new ArrayList<>();
        _skillsByRaceHashCodes = new HashIntObjectMap<>(Race.values().length);
        for (Race r : Race.values()) {
            for (L2SkillLearn s : fishingSkillTree.values()) {
                if (s.getRaces().contains(r)) {
                    list.add(SkillData.getSkillHashCode(s.getSkillId(), s.getSkillLevel()));
                }
            }

            for (L2SkillLearn s : transformSkillTree.values()) {
                if (s.getRaces().contains(r)) {
                    list.add(SkillData.getSkillHashCode(s.getSkillId(), s.getSkillLevel()));
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
        for (L2SkillLearn s : commonSkillTree.values()) {
            if (s.getRaces().isEmpty()) {
                list.add(SkillData.getSkillHashCode(s.getSkillId(), s.getSkillLevel()));
            }
        }

        for (L2SkillLearn s : fishingSkillTree.values()) {
            if (s.getRaces().isEmpty()) {
                list.add(SkillData.getSkillHashCode(s.getSkillId(), s.getSkillLevel()));
            }
        }

        for (L2SkillLearn s : transformSkillTree.values()) {
            if (s.getRaces().isEmpty()) {
                list.add(SkillData.getSkillHashCode(s.getSkillId(), s.getSkillLevel()));
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
    public boolean isSkillAllowed(L2PcInstance player, Skill skill) {
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

        final int maxLvl = SkillData.getInstance().getMaxLevel(skill.getId());
        final long hashCode = SkillData.getSkillHashCode(skill.getId(), Math.min(skill.getLevel(), maxLvl));

        if (Arrays.binarySearch(_skillsByClassIdHashCodes.get(player.getClassId().ordinal()), hashCode) >= 0) {
            return true;
        }

        if (Arrays.binarySearch(_skillsByRaceHashCodes.get(player.getRace().ordinal()), hashCode) >= 0) {
            return true;
        }

        return Arrays.binarySearch(_allSkillsHashCodes, hashCode) >= 0;

    }

    private void report() {
        int classSkillTreeCount = classSkillTrees.values().stream().mapToInt(LongObjectMap::size).sum();
        var dwarvenOnlyFishingSkillCount = fishingSkillTree.values().stream().filter(s -> s.getRaces().contains(Race.DWARF)).count();
        var resSkillCount = pledgeSkillTree.values().stream().filter(L2SkillLearn::isResidencialSkill).count();

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

    public static SkillTreesData getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        protected static final SkillTreesData INSTANCE = new SkillTreesData();
    }
}
