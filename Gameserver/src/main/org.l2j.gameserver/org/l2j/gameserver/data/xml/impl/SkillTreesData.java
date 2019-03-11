package org.l2j.gameserver.data.xml.impl;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.enums.CategoryType;
import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.enums.SubclassType;
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
import org.l2j.gameserver.util.IGameXmlReader;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.File;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * This class loads and manage the characters and pledges skills trees.<br>
 * Here can be found the following skill trees:<br>
 * <ul>
 * <li>Class skill trees: player skill trees for each class.</li>
 * <li>Transfer skill trees: player skill trees for each healer class.</lI>
 * <li>Collect skill tree: player skill tree for Gracia related skills.</li>
 * <li>Fishing skill tree: player skill tree for fishing related skills.</li>
 * <li>Transform skill tree: player skill tree for transformation related skills.</li>
 * <li>Sub-Class skill tree: player skill tree for sub-class related skills.</li>
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
public final class SkillTreesData implements IGameXmlReader {
    private static final Logger LOGGER = Logger.getLogger(SkillTreesData.class.getName());

    // ClassId, Map of Skill Hash Code, L2SkillLearn
    private static final Map<ClassId, Map<Long, L2SkillLearn>> _classSkillTrees = new HashMap<>();
    private static final Map<ClassId, Map<Long, L2SkillLearn>> _transferSkillTrees = new HashMap<>();
    private static final Map<Race, Map<Long, L2SkillLearn>> _raceSkillTree = new HashMap<>();
    private static final Map<SubclassType, Map<Long, L2SkillLearn>> _revelationSkillTree = new HashMap<>();
    private static final Map<ClassId, Set<Integer>> _awakeningSaveSkillTree = new HashMap<>();
    // Skill Hash Code, L2SkillLearn
    private static final Map<Long, L2SkillLearn> _collectSkillTree = new HashMap<>();
    private static final Map<Long, L2SkillLearn> _fishingSkillTree = new HashMap<>();
    private static final Map<Long, L2SkillLearn> _pledgeSkillTree = new HashMap<>();
    private static final Map<Long, L2SkillLearn> _subClassSkillTree = new HashMap<>();
    private static final Map<Long, L2SkillLearn> _subPledgeSkillTree = new HashMap<>();
    private static final Map<Long, L2SkillLearn> _transformSkillTree = new HashMap<>();
    private static final Map<Long, L2SkillLearn> _commonSkillTree = new HashMap<>();
    private static final Map<Long, L2SkillLearn> _abilitySkillTree = new HashMap<>();
    private static final Map<Long, L2SkillLearn> _alchemySkillTree = new HashMap<>();
    private static final Map<Long, L2SkillLearn> _dualClassSkillTree = new HashMap<>();
    // Other skill trees
    private static final Map<Long, L2SkillLearn> _nobleSkillTree = new HashMap<>();
    private static final Map<Long, L2SkillLearn> _heroSkillTree = new HashMap<>();
    private static final Map<Long, L2SkillLearn> _gameMasterSkillTree = new HashMap<>();
    private static final Map<Long, L2SkillLearn> _gameMasterAuraSkillTree = new HashMap<>();
    // Remove skill tree
    private static final Map<ClassId, Set<Integer>> _removeSkillCache = new HashMap<>();
    /**
     * Parent class Ids are read from XML and stored in this map, to allow easy customization.
     */
    private static final Map<ClassId, ClassId> _parentClassMap = new HashMap<>();
    private final AtomicBoolean _isLoading = new AtomicBoolean();
    // Checker, sorted arrays of hash codes
    private Map<Integer, long[]> _skillsByClassIdHashCodes; // Occupation skills
    private Map<Integer, long[]> _skillsByRaceHashCodes; // Race-specific Transformations
    private long[] _allSkillsHashCodes; // Fishing, Collection, Transformations, Common Skills.

    /**
     * Instantiates a new skill trees data.
     */
    protected SkillTreesData() {
        load();
    }

    /**
     * Gets the single instance of SkillTreesData.
     *
     * @return the only instance of this class
     */
    public static SkillTreesData getInstance() {
        return SingletonHolder._instance;
    }

    @Override
    public void load() {
        _isLoading.set(true);

        _classSkillTrees.clear();
        _collectSkillTree.clear();
        _fishingSkillTree.clear();
        _pledgeSkillTree.clear();
        _subClassSkillTree.clear();
        _subPledgeSkillTree.clear();
        _transferSkillTrees.clear();
        _transformSkillTree.clear();
        _nobleSkillTree.clear();
        _abilitySkillTree.clear();
        _alchemySkillTree.clear();
        _heroSkillTree.clear();
        _gameMasterSkillTree.clear();
        _gameMasterAuraSkillTree.clear();
        _raceSkillTree.clear();
        _revelationSkillTree.clear();
        _dualClassSkillTree.clear();
        _removeSkillCache.clear();
        _awakeningSaveSkillTree.clear();

        // Load files.
        parseDatapackDirectory("data/skillTrees/", true);

        // Generate check arrays.
        generateCheckArrays();

        // Logs a report with skill trees info.
        report();

        _isLoading.set(false);
    }

    /**
     * Parse a skill tree file and store it into the correct skill tree.
     */
    @Override
    public void parseDocument(Document doc, File f) {
        NamedNodeMap attrs;
        Node attr;
        String type = null;
        Race race = null;
        SubclassType subType = null;
        int cId = -1;
        int parentClassId = -1;
        ClassId classId = null;
        for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
            if ("list".equalsIgnoreCase(n.getNodeName())) {
                for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
                    if ("skillTree".equalsIgnoreCase(d.getNodeName())) {
                        final Map<Long, L2SkillLearn> classSkillTree = new HashMap<>();
                        final Map<Long, L2SkillLearn> transferSkillTree = new HashMap<>();
                        final Map<Long, L2SkillLearn> raceSkillTree = new HashMap<>();
                        final Map<Long, L2SkillLearn> revelationSkillTree = new HashMap<>();

                        type = d.getAttributes().getNamedItem("type").getNodeValue();
                        attr = d.getAttributes().getNamedItem("classId");
                        if (attr != null) {
                            cId = Integer.parseInt(attr.getNodeValue());
                            classId = ClassId.values()[cId];
                        } else {
                            cId = -1;
                        }

                        attr = d.getAttributes().getNamedItem("race");
                        if (attr != null) {
                            race = parseEnum(attr, Race.class);
                        }

                        attr = d.getAttributes().getNamedItem("subType");
                        if (attr != null) {
                            subType = parseEnum(attr, SubclassType.class);
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
                                            _removeSkillCache.computeIfAbsent(classId, k -> new HashSet<>()).add(removeSkillId);
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
                                            _commonSkillTree.put(skillHashCode, skillLearn);
                                        }
                                        break;
                                    }
                                    case "transferSkillTree": {
                                        transferSkillTree.put(skillHashCode, skillLearn);
                                        break;
                                    }
                                    case "collectSkillTree": {
                                        _collectSkillTree.put(skillHashCode, skillLearn);
                                        break;
                                    }
                                    case "raceSkillTree": {
                                        raceSkillTree.put(skillHashCode, skillLearn);
                                        break;
                                    }
                                    case "revelationSkillTree": {
                                        revelationSkillTree.put(skillHashCode, skillLearn);
                                        break;
                                    }
                                    case "fishingSkillTree": {
                                        _fishingSkillTree.put(skillHashCode, skillLearn);
                                        break;
                                    }
                                    case "pledgeSkillTree": {
                                        _pledgeSkillTree.put(skillHashCode, skillLearn);
                                        break;
                                    }
                                    case "subClassSkillTree": {
                                        _subClassSkillTree.put(skillHashCode, skillLearn);
                                        break;
                                    }
                                    case "subPledgeSkillTree": {
                                        _subPledgeSkillTree.put(skillHashCode, skillLearn);
                                        break;
                                    }
                                    case "transformSkillTree": {
                                        _transformSkillTree.put(skillHashCode, skillLearn);
                                        break;
                                    }
                                    case "nobleSkillTree": {
                                        _nobleSkillTree.put(skillHashCode, skillLearn);
                                        break;
                                    }
                                    case "abilitySkillTree": {
                                        _abilitySkillTree.put(skillHashCode, skillLearn);
                                        break;
                                    }
                                    case "alchemySkillTree": {
                                        _alchemySkillTree.put(skillHashCode, skillLearn);
                                        break;
                                    }
                                    case "heroSkillTree": {
                                        _heroSkillTree.put(skillHashCode, skillLearn);
                                        break;
                                    }
                                    case "gameMasterSkillTree": {
                                        _gameMasterSkillTree.put(skillHashCode, skillLearn);
                                        break;
                                    }
                                    case "gameMasterAuraSkillTree": {
                                        _gameMasterAuraSkillTree.put(skillHashCode, skillLearn);
                                        break;
                                    }
                                    case "dualClassSkillTree": {
                                        _dualClassSkillTree.put(skillHashCode, skillLearn);
                                        break;
                                    }
                                    case "awakeningSaveSkillTree": {
                                        _awakeningSaveSkillTree.computeIfAbsent(classId, k -> new HashSet<>()).add(skillLearn.getSkillId());
                                        break;
                                    }
                                    default: {
                                        LOGGER.warning(getClass().getSimpleName() + ": Unknown Skill Tree type: " + type + "!");
                                    }
                                }
                            }
                        }

                        if (type.equals("transferSkillTree")) {
                            _transferSkillTrees.put(classId, transferSkillTree);
                        } else if (type.equals("classSkillTree") && (cId > -1)) {
                            final Map<Long, L2SkillLearn> classSkillTrees = _classSkillTrees.get(classId);
                            if (classSkillTrees == null) {
                                _classSkillTrees.put(classId, classSkillTree);
                            } else {
                                classSkillTrees.putAll(classSkillTree);
                            }
                        } else if (type.equals("raceSkillTree") && (race != null)) {
                            final Map<Long, L2SkillLearn> raceSkillTrees = _raceSkillTree.get(race);
                            if (raceSkillTrees == null) {
                                _raceSkillTree.put(race, raceSkillTree);
                            } else {
                                raceSkillTrees.putAll(raceSkillTree);
                            }
                        } else if (type.equals("revelationSkillTree") && (subType != null)) {
                            final Map<Long, L2SkillLearn> revelationSkillTrees = _revelationSkillTree.get(subType);
                            if (revelationSkillTrees == null) {
                                _revelationSkillTree.put(subType, revelationSkillTree);
                            } else {
                                revelationSkillTrees.putAll(revelationSkillTree);
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
    public Map<Long, L2SkillLearn> getCompleteClassSkillTree(ClassId classId) {
        final Map<Long, L2SkillLearn> skillTree = new HashMap<>();
        // Add all skills that belong to all classes.
        skillTree.putAll(_commonSkillTree);
        while ((classId != null) && (_classSkillTrees.get(classId) != null)) {
            skillTree.putAll(_classSkillTrees.get(classId));
            classId = _parentClassMap.get(classId);
        }
        return skillTree;
    }

    /**
     * Gets the transfer skill tree.<br>
     * If new classes are implemented over 3rd class, we use a recursive call.
     *
     * @param classId the transfer skill tree Id
     * @return the complete Transfer Skill Tree for a given {@code classId}
     */
    public Map<Long, L2SkillLearn> getTransferSkillTree(ClassId classId) {
        return _transferSkillTrees.get(classId);
    }

    /**
     * Gets the race skill tree.<br>
     *
     * @param race the race skill tree Id
     * @return the complete race Skill Tree for a given {@code Race}
     */
    public Collection<L2SkillLearn> getRaceSkillTree(Race race) {
        return _raceSkillTree.containsKey(race) ? _raceSkillTree.get(race).values() : Collections.emptyList();
    }

    /**
     * Gets the common skill tree.
     *
     * @return the complete Common Skill Tree
     */
    public Map<Long, L2SkillLearn> getCommonSkillTree() {
        return _commonSkillTree;
    }

    /**
     * Gets the collect skill tree.
     *
     * @return the complete Collect Skill Tree
     */
    public Map<Long, L2SkillLearn> getCollectSkillTree() {
        return _collectSkillTree;
    }

    /**
     * Gets the fishing skill tree.
     *
     * @return the complete Fishing Skill Tree
     */
    public Map<Long, L2SkillLearn> getFishingSkillTree() {
        return _fishingSkillTree;
    }

    /**
     * Gets the pledge skill tree.
     *
     * @return the complete Pledge Skill Tree
     */
    public Map<Long, L2SkillLearn> getPledgeSkillTree() {
        return _pledgeSkillTree;
    }

    /**
     * Gets the sub class skill tree.
     *
     * @return the complete Sub-Class Skill Tree
     */
    public Map<Long, L2SkillLearn> getSubClassSkillTree() {
        return _subClassSkillTree;
    }

    /**
     * Gets the sub pledge skill tree.
     *
     * @return the complete Sub-Pledge Skill Tree
     */
    public Map<Long, L2SkillLearn> getSubPledgeSkillTree() {
        return _subPledgeSkillTree;
    }

    /**
     * Gets the transform skill tree.
     *
     * @return the complete Transform Skill Tree
     */
    public Map<Long, L2SkillLearn> getTransformSkillTree() {
        return _transformSkillTree;
    }

    /**
     * Gets the ability skill tree.
     *
     * @return the complete Ability Skill Tree
     */
    public Map<Long, L2SkillLearn> getAbilitySkillTree() {
        return _abilitySkillTree;
    }

    /**
     * Gets the ability skill tree.
     *
     * @return the complete Ability Skill Tree
     */
    public Map<Long, L2SkillLearn> getAlchemySkillTree() {
        return _alchemySkillTree;
    }

    /**
     * Gets the noble skill tree.
     *
     * @return the complete Noble Skill Tree
     */
    public List<Skill> getNobleSkillTree() {
        return _nobleSkillTree.values().stream().map(entry -> SkillData.getInstance().getSkill(entry.getSkillId(), entry.getSkillLevel())).collect(Collectors.toList());
    }

    /**
     * Gets the noble skill tree.
     *
     * @return the complete Noble Skill Tree
     */
    public List<Skill> getNobleSkillAutoGetTree() {
        return _nobleSkillTree.values().stream().filter(entry -> entry.isAutoGet()).map(entry -> SkillData.getInstance().getSkill(entry.getSkillId(), entry.getSkillLevel())).collect(Collectors.toList());
    }

    /**
     * Gets the hero skill tree.
     *
     * @return the complete Hero Skill Tree
     */
    public List<Skill> getHeroSkillTree() {
        return _heroSkillTree.values().stream().map(entry -> SkillData.getInstance().getSkill(entry.getSkillId(), entry.getSkillLevel())).collect(Collectors.toList());
    }

    /**
     * Gets the Game Master skill tree.
     *
     * @return the complete Game Master Skill Tree
     */
    public List<Skill> getGMSkillTree() {
        return _gameMasterSkillTree.values().stream().map(entry -> SkillData.getInstance().getSkill(entry.getSkillId(), entry.getSkillLevel())).collect(Collectors.toList());
    }

    /**
     * Gets the Game Master Aura skill tree.
     *
     * @return the complete Game Master Aura Skill Tree
     */
    public List<Skill> getGMAuraSkillTree() {
        return _gameMasterAuraSkillTree.values().stream().map(entry -> SkillData.getInstance().getSkill(entry.getSkillId(), entry.getSkillLevel())).collect(Collectors.toList());
    }

    /**
     * @param player
     * @param classId
     * @return {@code true} if player is able to learn new skills on his current level, {@code false} otherwise.
     */
    public boolean hasAvailableSkills(L2PcInstance player, ClassId classId) {
        final Map<Long, L2SkillLearn> skills = getCompleteClassSkillTree(classId);
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
     * @param holder
     * @return all available skills for a given {@code player}, {@code classId}, {@code includeByFs} and {@code includeAutoGet}
     */
    private List<L2SkillLearn> getAvailableSkills(L2PcInstance player, ClassId classId, boolean includeByFs, boolean includeAutoGet, ISkillsHolder holder) {
        final List<L2SkillLearn> result = new LinkedList<>();
        final Map<Long, L2SkillLearn> skills = getCompleteClassSkillTree(classId);

        if (skills.isEmpty()) {
            // The Skill Tree for this class is undefined.
            LOGGER.warning(getClass().getSimpleName() + ": Skilltree for class " + classId + " is not defined!");
            return result;
        }

        final boolean isAwaken = player.isInCategory(CategoryType.SIXTH_CLASS_GROUP) && ((player.getRace() != Race.ERTHEIA) || player.isDualClassActive());

        for (Entry<Long, L2SkillLearn> entry : skills.entrySet()) {
            final L2SkillLearn skill = entry.getValue();

            if (((skill.getSkillId() == CommonSkill.DIVINE_INSPIRATION.getId()) && (!Config.AUTO_LEARN_DIVINE_INSPIRATION && includeAutoGet) && !player.isGM()) || (!includeAutoGet && skill.isAutoGet()) || (!includeByFs && skill.isLearnedByFS()) || isRemoveSkill(classId, skill.getSkillId())) {
                continue;
            }

            if (isAwaken && !isCurrentClassSkillNoParent(classId, entry.getKey()) && !isAwakenSaveSkill(player.getClassId(), skill.getSkillId())) {
                continue;
            }

            if (player.getLevel() >= skill.getGetLevel()) {
                if (skill.getSkillLevel() > SkillData.getInstance().getMaxLevel(skill.getSkillId())) {
                    LOGGER.severe(getClass().getSimpleName() + ": SkillTreesData found learnable skill " + skill.getSkillId() + " with level higher than max skill level!");
                    continue;
                }

                final Skill oldSkill = holder.getKnownSkill(skill.getSkillId());
                if (oldSkill != null) {
                    if (oldSkill.getLevel() == (skill.getSkillLevel() - 1)) {
                        result.add(skill);
                    }
                } else if (skill.getSkillLevel() == 1) {
                    result.add(skill);
                }
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
        final Map<Long, L2SkillLearn> skills = getCompleteClassSkillTree(player.getClassId());
        if (skills.isEmpty()) {
            // The Skill Tree for this class is undefined, so we return an empty list.
            LOGGER.warning(getClass().getSimpleName() + ": Skill Tree for this class Id(" + player.getClassId() + ") is not defined!");
            return result;
        }

        final Race race = player.getRace();
        final boolean isAwaken = player.isInCategory(CategoryType.SIXTH_CLASS_GROUP);

        // Race skills
        if (isAwaken) {
            for (L2SkillLearn skill : getRaceSkillTree(race)) {
                if (player.getKnownSkill(skill.getSkillId()) == null) {
                    result.add(skill);
                }
            }
        }

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
                } else if (!isAwaken || isCurrentClassSkillNoParent(player.getClassId(), hashCode) || isAwakenSaveSkill(player.getClassId(), skill.getSkillId())) {
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
        for (L2SkillLearn skill : _fishingSkillTree.values()) {
            // If skill is Race specific and the player's race isn't allowed, skip it.
            if (!skill.getRaces().isEmpty() && !skill.getRaces().contains(playerRace)) {
                continue;
            }

            if (skill.isLearnedByNpc() && (player.getLevel() >= skill.getGetLevel())) {
                final Skill oldSkill = player.getSkills().get(skill.getSkillId());
                if (oldSkill != null) {
                    if (oldSkill.getLevel() == (skill.getSkillLevel() - 1)) {
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
     * Gets the available revelation skills
     *
     * @param player the player requesting the revelation skills
     * @param type   the player current subclass type
     * @return all the available revelation skills for a given {@code player}
     */
    public List<L2SkillLearn> getAvailableRevelationSkills(L2PcInstance player, SubclassType type) {
        final List<L2SkillLearn> result = new ArrayList<>();
        final Map<Long, L2SkillLearn> revelationSkills = _revelationSkillTree.get(type);

        for (L2SkillLearn skill : revelationSkills.values()) {
            final Skill oldSkill = player.getSkills().get(skill.getSkillId());

            if (oldSkill == null) {
                result.add(skill);
            }
        }
        return result;
    }

    /**
     * Gets the available alchemy skills, restricted to Ertheia
     *
     * @param player the player requesting the alchemy skills
     * @return all the available alchemy skills for a given {@code player}
     */
    public List<L2SkillLearn> getAvailableAlchemySkills(L2PcInstance player) {
        final List<L2SkillLearn> result = new ArrayList<>();

        for (L2SkillLearn skill : _alchemySkillTree.values()) {
            if (skill.isLearnedByNpc() && (player.getLevel() >= skill.getGetLevel())) {
                final Skill oldSkill = player.getSkills().get(skill.getSkillId());

                if (oldSkill != null) {
                    if (oldSkill.getLevel() == (skill.getSkillLevel() - 1)) {
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
     * Used in Gracia continent.
     *
     * @param player the collecting skill learning player
     * @return all the available Collecting skills for a given {@code player}
     */
    public List<L2SkillLearn> getAvailableCollectSkills(L2PcInstance player) {
        final List<L2SkillLearn> result = new ArrayList<>();
        for (L2SkillLearn skill : _collectSkillTree.values()) {
            final Skill oldSkill = player.getSkills().get(skill.getSkillId());
            if (oldSkill != null) {
                if (oldSkill.getLevel() == (skill.getSkillLevel() - 1)) {
                    result.add(skill);
                }
            } else if (skill.getSkillLevel() == 1) {
                result.add(skill);
            }
        }
        return result;
    }

    /**
     * Gets the available transfer skills.
     *
     * @param player the transfer skill learning player
     * @return all the available Transfer skills for a given {@code player}
     */
    public List<L2SkillLearn> getAvailableTransferSkills(L2PcInstance player) {
        final List<L2SkillLearn> result = new ArrayList<>();
        final ClassId classId = player.getClassId();

        if (!_transferSkillTrees.containsKey(classId)) {
            return result;
        }

        for (L2SkillLearn skill : _transferSkillTrees.get(classId).values()) {
            // If player doesn't know this transfer skill:
            if (player.getKnownSkill(skill.getSkillId()) == null) {
                result.add(skill);
            }
        }
        return result;
    }

    /**
     * Some transformations are not available for some races.
     *
     * @param player the transformation skill learning player
     * @return all the available Transformation skills for a given {@code player}
     */
    public List<L2SkillLearn> getAvailableTransformSkills(L2PcInstance player) {
        final List<L2SkillLearn> result = new ArrayList<>();
        final Race race = player.getRace();
        for (L2SkillLearn skill : _transformSkillTree.values()) {
            if ((player.getLevel() >= skill.getGetLevel()) && (skill.getRaces().isEmpty() || skill.getRaces().contains(race))) {
                final Skill oldSkill = player.getSkills().get(skill.getSkillId());
                if (oldSkill != null) {
                    if (oldSkill.getLevel() == (skill.getSkillLevel() - 1)) {
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
     * @param clan the pledge skill learning clan
     * @return all the available Pledge skills for a given {@code clan}
     */
    public List<L2SkillLearn> getAvailablePledgeSkills(L2Clan clan) {
        final List<L2SkillLearn> result = new ArrayList<>();

        for (L2SkillLearn skill : _pledgeSkillTree.values()) {
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
        for (L2SkillLearn skill : _pledgeSkillTree.values()) {
            if (!skill.isResidencialSkill() && (clan.getLevel() >= skill.getGetLevel())) {
                final Skill oldSkill = clan.getSkills().get(skill.getSkillId());
                if ((oldSkill == null) || (oldSkill.getLevel() < skill.getSkillLevel())) {
                    result.put(skill.getSkillId(), skill);
                }
            }
        }

        if (includeSquad) {
            for (L2SkillLearn skill : _subPledgeSkillTree.values()) {
                if ((clan.getLevel() >= skill.getGetLevel())) {
                    final Skill oldSkill = clan.getSkills().get(skill.getSkillId());
                    if ((oldSkill == null) || (oldSkill.getLevel() < skill.getSkillLevel())) {
                        result.put(skill.getSkillId(), skill);
                    }
                }
            }
        }
        return result;
    }

    /**
     * Gets the available sub pledge skills.
     *
     * @param clan the sub-pledge skill learning clan
     * @return all the available Sub-Pledge skills for a given {@code clan}
     */
    public List<L2SkillLearn> getAvailableSubPledgeSkills(L2Clan clan) {
        final List<L2SkillLearn> result = new ArrayList<>();
        for (L2SkillLearn skill : _subPledgeSkillTree.values()) {
            if ((clan.getLevel() >= skill.getGetLevel()) && clan.isLearnableSubSkill(skill.getSkillId(), skill.getSkillLevel())) {
                result.add(skill);
            }
        }
        return result;
    }

    /**
     * Gets the available sub class skills.
     *
     * @param player the sub-class skill learning player
     * @return all the available Sub-Class skills for a given {@code player}
     */
    public List<L2SkillLearn> getAvailableSubClassSkills(L2PcInstance player) {
        final List<L2SkillLearn> result = new ArrayList<>();
        for (L2SkillLearn skill : _subClassSkillTree.values()) {
            final Skill oldSkill = player.getSkills().get(skill.getSkillId());
            if (((oldSkill == null) && (skill.getSkillLevel() == 1)) || ((oldSkill != null) && (oldSkill.getLevel() == (skill.getSkillLevel() - 1)))) {
                result.add(skill);
            }
        }
        return result;
    }

    /**
     * Gets the available dual class skills.
     *
     * @param player the dual-class skill learning player
     * @return all the available Dual-Class skills for a given {@code player} sorted by skill ID
     */
    public List<L2SkillLearn> getAvailableDualClassSkills(L2PcInstance player) {
        final List<L2SkillLearn> result = new ArrayList<>();
        for (L2SkillLearn skill : _dualClassSkillTree.values()) {
            final Skill oldSkill = player.getSkills().get(skill.getSkillId());
            if (((oldSkill == null) && (skill.getSkillLevel() == 1)) || ((oldSkill != null) && (oldSkill.getLevel() == (skill.getSkillLevel() - 1)))) {
                result.add(skill);
            }
        }
        result.sort(Comparator.comparing(L2SkillLearn::getSkillId));
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
        for (L2SkillLearn skill : _pledgeSkillTree.values()) {
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
            case TRANSFER: {
                sl = getTransferSkill(id, lvl, player.getClassId());
                break;
            }
            case SUBCLASS: {
                sl = getSubClassSkill(id, lvl);
                break;
            }
            case COLLECT: {
                sl = getCollectSkill(id, lvl);
                break;
            }
            case REVELATION: {
                sl = getRevelationSkill(SubclassType.BASECLASS, id, lvl);
                break;
            }
            case REVELATION_DUALCLASS: {
                sl = getRevelationSkill(SubclassType.DUALCLASS, id, lvl);
                break;
            }
            case ALCHEMY: {
                sl = getAlchemySkill(id, lvl);
                break;
            }
            case DUALCLASS: {
                sl = getDualClassSkill(id, lvl);
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
        return _transformSkillTree.get(SkillData.getSkillHashCode(id, lvl));
    }

    /**
     * Gets the ability skill.
     *
     * @param id  the ability skill Id
     * @param lvl the ability skill level
     * @return the ability skill from the Ability Skill Tree for a given {@code id} and {@code lvl}
     */
    public L2SkillLearn getAbilitySkill(int id, int lvl) {
        return _abilitySkillTree.get(SkillData.getSkillHashCode(id, lvl));
    }

    /**
     * Gets the alchemy skill.
     *
     * @param id  the alchemy skill Id
     * @param lvl the alchemy skill level
     * @return the alchemy skill from the Alchemy Skill Tree for a given {@code id} and {@code lvl}
     */
    private L2SkillLearn getAlchemySkill(int id, int lvl) {
        return _alchemySkillTree.get(SkillData.getSkillHashCode(id, lvl));
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
        return _fishingSkillTree.get(SkillData.getSkillHashCode(id, lvl));
    }

    /**
     * Gets the pledge skill.
     *
     * @param id  the pledge skill Id
     * @param lvl the pledge skill level
     * @return the pledge skill from the Pledge Skill Tree for a given {@code id} and {@code lvl}
     */
    public L2SkillLearn getPledgeSkill(int id, int lvl) {
        return _pledgeSkillTree.get(SkillData.getSkillHashCode(id, lvl));
    }

    /**
     * Gets the sub pledge skill.
     *
     * @param id  the sub-pledge skill Id
     * @param lvl the sub-pledge skill level
     * @return the sub-pledge skill from the Sub-Pledge Skill Tree for a given {@code id} and {@code lvl}
     */
    public L2SkillLearn getSubPledgeSkill(int id, int lvl) {
        return _subPledgeSkillTree.get(SkillData.getSkillHashCode(id, lvl));
    }

    /**
     * Gets the transfer skill.
     *
     * @param id      the transfer skill Id
     * @param lvl     the transfer skill level.
     * @param classId the transfer skill tree Id
     * @return the transfer skill from the Transfer Skill Trees for a given {@code classId}, {@code id} and {@code lvl}
     */
    private L2SkillLearn getTransferSkill(int id, int lvl, ClassId classId) {
        if (_transferSkillTrees.get(classId) != null) {
            return _transferSkillTrees.get(classId).get(SkillData.getSkillHashCode(id, lvl));
        }
        return null;
    }

    /**
     * Gets the race skill.
     *
     * @param id   the race skill Id
     * @param lvl  the race skill level.
     * @param race the race skill tree Id
     * @return the transfer skill from the Race Skill Trees for a given {@code race}, {@code id} and {@code lvl}
     */
    private L2SkillLearn getRaceSkill(int id, int lvl, Race race) {
        for (L2SkillLearn skill : getRaceSkillTree(race)) {
            if ((skill.getSkillId() == id) && (skill.getSkillLevel() == lvl)) {
                return skill;
            }
        }
        return null;
    }

    /**
     * Gets the sub class skill.
     *
     * @param id  the sub-class skill Id
     * @param lvl the sub-class skill level
     * @return the sub-class skill from the Sub-Class Skill Tree for a given {@code id} and {@code lvl}
     */
    private L2SkillLearn getSubClassSkill(int id, int lvl) {
        return _subClassSkillTree.get(SkillData.getSkillHashCode(id, lvl));
    }

    /**
     * Gets the dual class skill.
     *
     * @param id  the dual-class skill Id
     * @param lvl the dual-class skill level
     * @return the dual-class skill from the Dual-Class Skill Tree for a given {@code id} and {@code lvl}
     */
    public L2SkillLearn getDualClassSkill(int id, int lvl) {
        return _dualClassSkillTree.get(SkillData.getSkillHashCode(id, lvl));
    }

    /**
     * Gets the common skill.
     *
     * @param id  the common skill Id.
     * @param lvl the common skill level
     * @return the common skill from the Common Skill Tree for a given {@code id} and {@code lvl}
     */
    public L2SkillLearn getCommonSkill(int id, int lvl) {
        return _commonSkillTree.get(SkillData.getSkillHashCode(id, lvl));
    }

    /**
     * Gets the collect skill.
     *
     * @param id  the collect skill Id
     * @param lvl the collect skill level
     * @return the collect skill from the Collect Skill Tree for a given {@code id} and {@code lvl}
     */
    public L2SkillLearn getCollectSkill(int id, int lvl) {
        return _collectSkillTree.get(SkillData.getSkillHashCode(id, lvl));
    }

    /**
     * Gets the revelation skill.
     *
     * @param type the subclass type
     * @param id   the revelation skill Id
     * @param lvl  the revelation skill level
     * @return the revelation skill from the Revelation Skill Tree for a given {@code id} and {@code lvl}
     */
    public L2SkillLearn getRevelationSkill(SubclassType type, int id, int lvl) {
        return _revelationSkillTree.get(type).get(SkillData.getSkillHashCode(id, lvl));
    }

    /**
     * Gets the minimum level for new skill.
     *
     * @param player    the player that requires the minimum level
     * @param skillTree the skill tree to search the minimum get level
     * @return the minimum level for a new skill for a given {@code player} and {@code skillTree}
     */
    public int getMinLevelForNewSkill(L2PcInstance player, Map<Long, L2SkillLearn> skillTree) {
        int minLevel = 0;
        if (skillTree.isEmpty()) {
            LOGGER.warning(getClass().getSimpleName() + ": SkillTree is not defined for getMinLevelForNewSkill!");
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
        final Map<Long, L2SkillLearn> completeClassSkillTree = getCompleteClassSkillTree(classId);
        final List<L2SkillLearn> result = new LinkedList<>();
        if (completeClassSkillTree.isEmpty()) {
            return result;
        }
        final int minLevelForNewSkill = getMinLevelForNewSkill(player, completeClassSkillTree);

        if (minLevelForNewSkill > 0) {
            for (L2SkillLearn skill : completeClassSkillTree.values()) {
                if (skill.getGetLevel() > Config.PLAYER_MAXIMUM_LEVEL) {
                    continue;
                }
                if ((!includeAutoGet && skill.isAutoGet()) || (!includeByFs && skill.isLearnedByFS())) {
                    continue;
                }
                if (minLevelForNewSkill <= skill.getGetLevel()) {
                    final Skill oldSkill = player.getKnownSkill(skill.getSkillId());
                    if (oldSkill != null) {
                        if (oldSkill.getLevel() == (skill.getSkillLevel() - 1)) {
                            result.add(skill);
                        }
                    } else if (skill.getSkillLevel() == 1) {
                        result.add(skill);
                    }
                }
            }
        }
        return result;
    }

    public void cleanSkillUponAwakening(L2PcInstance player) {
        for (Skill skill : player.getAllSkills()) {
            final int maxLvl = SkillData.getInstance().getMaxLevel(skill.getId());
            final long hashCode = SkillData.getSkillHashCode(skill.getId(), maxLvl);

            if (!isCurrentClassSkillNoParent(player.getClassId(), hashCode) && !isRemoveSkill(player.getClassId(), skill.getId()) && !isAwakenSaveSkill(player.getClassId(), skill.getId())) {
                player.removeSkill(skill, true, true);
            }
        }
    }

    public boolean isAlchemySkill(int skillId, int skillLevel) {
        return _alchemySkillTree.containsKey(SkillData.getSkillHashCode(skillId, skillLevel));
    }

    /**
     * Checks if is hero skill.
     *
     * @param skillId    the Id of the skill to check
     * @param skillLevel the level of the skill to check, if it's -1 only Id will be checked
     * @return {@code true} if the skill is present in the Hero Skill Tree, {@code false} otherwise
     */
    public boolean isHeroSkill(int skillId, int skillLevel) {
        return _heroSkillTree.containsKey(SkillData.getSkillHashCode(skillId, skillLevel));
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
        return _gameMasterSkillTree.containsKey(hashCode) || _gameMasterAuraSkillTree.containsKey(hashCode);
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
        return _pledgeSkillTree.containsKey(hashCode) || _subPledgeSkillTree.containsKey(hashCode);
    }

    public boolean isRemoveSkill(ClassId classId, int skillId) {
        return _removeSkillCache.getOrDefault(classId, Collections.emptySet()).contains(skillId);
    }

    public boolean isCurrentClassSkillNoParent(ClassId classId, Long hashCode) {
        return _classSkillTrees.getOrDefault(classId, Collections.emptyMap()).containsKey(hashCode);
    }

    public boolean isAwakenSaveSkill(ClassId classId, int skillId) {
        return _awakeningSaveSkillTree.getOrDefault(classId, Collections.emptySet()).contains(skillId);
    }

    /**
     * Adds the skills.
     *
     * @param gmchar     the player to add the Game Master skills
     * @param auraSkills if {@code true} it will add "GM Aura" skills, else will add the "GM regular" skills
     */
    public void addSkills(L2PcInstance gmchar, boolean auraSkills) {
        final Collection<L2SkillLearn> skills = auraSkills ? _gameMasterAuraSkillTree.values() : _gameMasterSkillTree.values();
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
        Map<Long, L2SkillLearn> tempMap;
        final Set<ClassId> keySet = _classSkillTrees.keySet();
        _skillsByClassIdHashCodes = new HashMap<>(keySet.size());
        for (ClassId cls : keySet) {
            i = 0;
            tempMap = getCompleteClassSkillTree(cls);
            array = new long[tempMap.size()];
            for (long h : tempMap.keySet()) {
                array[i++] = h;
            }
            tempMap.clear();
            Arrays.sort(array);
            _skillsByClassIdHashCodes.put(cls.ordinal(), array);
        }

        // Race specific skills from Fishing and Transformation skill trees.
        final List<Long> list = new ArrayList<>();
        _skillsByRaceHashCodes = new HashMap<>(Race.values().length);
        for (Race r : Race.values()) {
            for (L2SkillLearn s : _fishingSkillTree.values()) {
                if (s.getRaces().contains(r)) {
                    list.add(SkillData.getSkillHashCode(s.getSkillId(), s.getSkillLevel()));
                }
            }

            for (L2SkillLearn s : _transformSkillTree.values()) {
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
        for (L2SkillLearn s : _commonSkillTree.values()) {
            if (s.getRaces().isEmpty()) {
                list.add(SkillData.getSkillHashCode(s.getSkillId(), s.getSkillLevel()));
            }
        }

        for (L2SkillLearn s : _fishingSkillTree.values()) {
            if (s.getRaces().isEmpty()) {
                list.add(SkillData.getSkillHashCode(s.getSkillId(), s.getSkillLevel()));
            }
        }

        for (L2SkillLearn s : _transformSkillTree.values()) {
            if (s.getRaces().isEmpty()) {
                list.add(SkillData.getSkillHashCode(s.getSkillId(), s.getSkillLevel()));
            }
        }

        for (L2SkillLearn s : _collectSkillTree.values()) {
            list.add(SkillData.getSkillHashCode(s.getSkillId(), s.getSkillLevel()));
        }

        for (L2SkillLearn s : _abilitySkillTree.values()) {
            list.add(SkillData.getSkillHashCode(s.getSkillId(), s.getSkillLevel()));
        }

        for (L2SkillLearn s : _alchemySkillTree.values()) {
            list.add(SkillData.getSkillHashCode(s.getSkillId(), s.getSkillLevel()));
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
        if (_isLoading.get()) {
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

        if (Arrays.binarySearch(_allSkillsHashCodes, hashCode) >= 0) {
            return true;
        }

        // Exclude Transfer Skills from this check.
        if (getTransferSkill(skill.getId(), Math.min(skill.getLevel(), maxLvl), player.getClassId()) != null) {
            return true;
        }

        // Exclude Race skills from this check.
        if (getRaceSkill(skill.getId(), Math.min(skill.getLevel(), maxLvl), player.getRace()) != null) {
            return true;
        }

        return false;
    }

    /**
     * Logs current Skill Trees skills count.
     */
    private void report() {
        int classSkillTreeCount = 0;
        for (Map<Long, L2SkillLearn> classSkillTree : _classSkillTrees.values()) {
            classSkillTreeCount += classSkillTree.size();
        }

        int transferSkillTreeCount = 0;
        for (Map<Long, L2SkillLearn> trasferSkillTree : _transferSkillTrees.values()) {
            transferSkillTreeCount += trasferSkillTree.size();
        }

        int raceSkillTreeCount = 0;
        for (Map<Long, L2SkillLearn> raceSkillTree : _raceSkillTree.values()) {
            raceSkillTreeCount += raceSkillTree.size();
        }

        int revelationSkillTreeCount = 0;
        for (Map<Long, L2SkillLearn> revelationSkillTree : _revelationSkillTree.values()) {
            revelationSkillTreeCount += revelationSkillTree.size();
        }

        int dwarvenOnlyFishingSkillCount = 0;
        for (L2SkillLearn fishSkill : _fishingSkillTree.values()) {
            if (fishSkill.getRaces().contains(Race.DWARF)) {
                dwarvenOnlyFishingSkillCount++;
            }
        }

        int resSkillCount = 0;
        for (L2SkillLearn pledgeSkill : _pledgeSkillTree.values()) {
            if (pledgeSkill.isResidencialSkill()) {
                resSkillCount++;
            }
        }

        final String className = getClass().getSimpleName();
        LOGGER.info(className + ": Loaded " + classSkillTreeCount + " Class Skills for " + _classSkillTrees.size() + " Class Skill Trees.");
        LOGGER.info(className + ": Loaded " + _subClassSkillTree.size() + " Sub-Class Skills.");
        LOGGER.info(className + ": Loaded " + _dualClassSkillTree.size() + " Dual-Class Skills.");
        LOGGER.info(className + ": Loaded " + transferSkillTreeCount + " Transfer Skills for " + _transferSkillTrees.size() + " Transfer Skill Trees.");
        LOGGER.info(className + ": Loaded " + raceSkillTreeCount + " Race skills for " + _raceSkillTree.size() + " Race Skill Trees.");
        LOGGER.info(className + ": Loaded " + _fishingSkillTree.size() + " Fishing Skills, " + dwarvenOnlyFishingSkillCount + " Dwarven only Fishing Skills.");
        LOGGER.info(className + ": Loaded " + _collectSkillTree.size() + " Collect Skills.");
        LOGGER.info(className + ": Loaded " + _pledgeSkillTree.size() + " Pledge Skills, " + (_pledgeSkillTree.size() - resSkillCount) + " for Pledge and " + resSkillCount + " Residential.");
        LOGGER.info(className + ": Loaded " + _subPledgeSkillTree.size() + " Sub-Pledge Skills.");
        LOGGER.info(className + ": Loaded " + _transformSkillTree.size() + " Transform Skills.");
        LOGGER.info(className + ": Loaded " + _nobleSkillTree.size() + " Noble Skills.");
        LOGGER.info(className + ": Loaded " + _heroSkillTree.size() + " Hero Skills.");
        LOGGER.info(className + ": Loaded " + _gameMasterSkillTree.size() + " Game Master Skills.");
        LOGGER.info(className + ": Loaded " + _gameMasterAuraSkillTree.size() + " Game Master Aura Skills.");
        LOGGER.info(className + ": Loaded " + _abilitySkillTree.size() + " Ability Skills.");
        LOGGER.info(className + ": Loaded " + _alchemySkillTree.size() + " Alchemy Skills.");
        LOGGER.info(className + ": Loaded " + _awakeningSaveSkillTree.size() + " Class Awaken Save Skills.");
        LOGGER.info(className + ": Loaded " + revelationSkillTreeCount + " Revelation Skills.");

        final int commonSkills = _commonSkillTree.size();
        if (commonSkills > 0) {
            LOGGER.info(className + ": Loaded " + commonSkills + " Common Skills to all classes.");
        }
    }

    /**
     * Singleton holder for the SkillTreesData class.
     */
    private static class SingletonHolder {
        protected static final SkillTreesData _instance = new SkillTreesData();
    }
}
