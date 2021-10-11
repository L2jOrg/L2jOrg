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

import io.github.joealisson.primitive.ArrayIntList;
import io.github.joealisson.primitive.HashIntMap;
import io.github.joealisson.primitive.IntList;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.api.elemental.ElementalType;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.enums.AISkillScope;
import org.l2j.gameserver.enums.DropType;
import org.l2j.gameserver.enums.MpRewardAffectType;
import org.l2j.gameserver.enums.MpRewardType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.holders.DropHolder;
import org.l2j.gameserver.settings.GeneralSettings;
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
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static org.l2j.commons.util.Util.computeIfNonNull;
import static org.l2j.commons.util.Util.contains;

/**
 * NPC data parser.
 *
 * @author NosBit
 * @author JoeAlisson
 */
public class NpcData extends GameXmlReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(NpcData.class);

    private final IntList masterIDs = new ArrayIntList();
    private final IntMap<NpcTemplate> npcs = new HashIntMap<>();
    private final Map<String, Integer> clans = new HashMap<>();

    private NpcData() {
    }

    @Override
    protected Path getSchemaFilePath() {
        return ServerSettings.dataPackDirectory().resolve("data/stats/npcs/npcs.xsd");
    }

    public boolean isMaster(int id) {
        return masterIDs.contains(id);
    }

    @Override
    public synchronized void load() {
        masterIDs.clear();

        parseDatapackDirectory("data/stats/npcs", false);
        LOGGER.info("Loaded {} NPCs.", npcs.size());

        if (GeneralSettings.loadCustomNPC()) {
            final int npcCount = npcs.size();
            parseDatapackDirectory("data/stats/npcs/custom", true);
            LOGGER.info("Loaded {} Custom NPCs", npcs.size() - npcCount);
        }
        releaseResources();
    }

    @Override
    public void parseDocument(Document doc, File f) {
        for (Node node = doc.getFirstChild(); node != null; node = node.getNextSibling()) {
            if ("list".equalsIgnoreCase(node.getNodeName())) {
                for (Node listNode = node.getFirstChild(); listNode != null; listNode = listNode.getNextSibling()) {
                    if ("npc".equalsIgnoreCase(listNode.getNodeName())) {
                        NamedNodeMap attrs = listNode.getAttributes();
                        final StatsSet set = new StatsSet(new HashMap<>());
                        final int npcId = parseInt(attrs, "id");
                        Map<String, Object> parameters = null;
                        Map<Integer, Skill> skills = null;
                        Set<Integer> clans = null;
                        Set<Integer> ignoreClanNpcIds = null;
                        List<DropHolder> dropLists = null;
                        set.set("id", npcId);
                        set.set("displayId", parseInt(attrs, "displayId", npcId));
                        set.set("level", parseByte(attrs, "level", (byte) 70));
                        set.set("type", parseString(attrs, "type", "Npc"));
                        set.set("name", parseString(attrs, "name"));
                        set.set("usingServerSideName", parseBoolean(attrs, "usingServerSideName"));
                        set.set("title", parseString(attrs, "title"));
                        set.set("usingServerSideTitle", parseBoolean(attrs, "usingServerSideTitle"));
                        set.set("elementalType", parseEnum(attrs, ElementalType.class, "element"));

                        for (Node npcNode = listNode.getFirstChild(); npcNode != null; npcNode = npcNode.getNextSibling()) {
                            attrs = npcNode.getAttributes();
                            switch (npcNode.getNodeName().toLowerCase()) {
                                case "parameters": {
                                    if (parameters == null) {
                                        parameters = new HashMap<>();
                                    }
                                    parameters.putAll(parseParameters(npcNode));
                                    break;
                                }
                                case "race":
                                case "sex": {
                                    set.set(npcNode.getNodeName(), npcNode.getTextContent().toUpperCase());
                                    break;
                                }
                                case "equipment": {
                                    set.set("chestId", parseInt(attrs, "chest"));
                                    set.set("rhandId", parseInt(attrs, "rhand"));
                                    set.set("lhandId", parseInt(attrs, "lhand"));
                                    set.set("weaponEnchant", parseInt(attrs, "weaponEnchant"));
                                    break;
                                }
                                case "acquire": {
                                    set.set("exp", parseDouble(attrs, "exp"));
                                    set.set("attribute_exp", parseLong(attrs, "attribute_exp"));
                                    set.set("sp", parseDouble(attrs, "sp"));
                                    set.set("raidPoints", parseDouble(attrs, "raidPoints"));
                                    break;
                                }
                                case "mpreward": {
                                    set.set("mpRewardValue", parseInt(attrs, "value"));
                                    set.set("mpRewardType", parseEnum(attrs, MpRewardType.class, "type", MpRewardType.DIFF));
                                    set.set("mpRewardTicks", parseInt(attrs, "ticks"));
                                    set.set("mpRewardAffectType", parseEnum(attrs, MpRewardAffectType.class, "affects", MpRewardAffectType.SOLO));
                                    break;
                                }
                                case "stats": {
                                    set.set("baseSTR", parseInt(attrs, "str"));
                                    set.set("baseINT", parseInt(attrs, "int"));
                                    set.set("baseDEX", parseInt(attrs, "dex"));
                                    set.set("baseWIT", parseInt(attrs, "wit"));
                                    set.set("baseCON", parseInt(attrs, "con"));
                                    set.set("baseMEN", parseInt(attrs, "men"));
                                    for (Node statsNode = npcNode.getFirstChild(); statsNode != null; statsNode = statsNode.getNextSibling()) {
                                        attrs = statsNode.getAttributes();
                                        switch (statsNode.getNodeName().toLowerCase()) {
                                            case "vitals": {
                                                set.set("baseHpMax", parseDouble(attrs, "hp"));
                                                set.set("baseHpReg", parseDouble(attrs, "hpRegen"));
                                                set.set("baseMpMax", parseDouble(attrs, "mp"));
                                                set.set("baseMpReg", parseDouble(attrs, "mpRegen"));
                                                break;
                                            }
                                            case "attack": {
                                                set.set("basePAtk", parseDouble(attrs, "physical"));
                                                set.set("baseMAtk", parseDouble(attrs, "magical"));
                                                set.set("baseRndDam", parseInt(attrs, "random"));
                                                set.set("baseCritRate", parseDouble(attrs, "critical"));
                                                set.set("accuracy", parseFloat(attrs, "accuracy")); // TODO: Implement me
                                                set.set("basePAtkSpd", parseFloat(attrs, "attackSpeed", 300F));
                                                set.set("reuseDelay", parseInt(attrs, "reuseDelay")); // TODO: Implement me
                                                set.set("baseAtkType", parseString(attrs, "type"));
                                                set.set("baseAtkRange", parseInt(attrs, "range"));
                                                set.set("distance", parseInt(attrs, "distance")); // TODO: Implement me
                                                set.set("width", parseInt(attrs, "width")); // TODO: Implement me
                                                break;
                                            }
                                            case "defence": {
                                                set.set("basePDef", parseDouble(attrs, "physical"));
                                                set.set("baseMDef", parseDouble(attrs, "magical"));
                                                set.set("evasion", parseInt(attrs, "evasion")); // TODO: Implement me
                                                set.set("baseShldDef", parseInt(attrs, "shield"));
                                                set.set("baseShldRate", parseInt(attrs, "shieldRate"));
                                                break;
                                            }
                                            case "abnormalresist": {
                                                set.set("physicalAbnormalResist", parseDouble(attrs, "physical", 10));
                                                set.set("magicAbnormalResist", parseDouble(attrs, "magic", 10));
                                                break;
                                            }
                                            case "attribute": {
                                                for (Node attribute_node = statsNode.getFirstChild(); attribute_node != null; attribute_node = attribute_node.getNextSibling()) {
                                                    attrs = attribute_node.getAttributes();
                                                    switch (attribute_node.getNodeName().toLowerCase()) {
                                                        case "attack": {
                                                            final String attackAttributeType = parseString(attrs, "type");
                                                            switch (attackAttributeType.toUpperCase()) {
                                                                case "FIRE": {
                                                                    set.set("baseFire", parseInt(attrs, "value"));
                                                                    break;
                                                                }
                                                                case "WATER": {
                                                                    set.set("baseWater", parseInt(attrs, "value"));
                                                                    break;
                                                                }
                                                                case "WIND": {
                                                                    set.set("baseWind", parseInt(attrs, "value"));
                                                                    break;
                                                                }
                                                                case "EARTH": {
                                                                    set.set("baseEarth", parseInt(attrs, "value"));
                                                                    break;
                                                                }
                                                                case "DARK": {
                                                                    set.set("baseDark", parseInt(attrs, "value"));
                                                                    break;
                                                                }
                                                                case "HOLY": {
                                                                    set.set("baseHoly", parseInt(attrs, "value"));
                                                                    break;
                                                                }
                                                            }
                                                            break;
                                                        }
                                                        case "defence": {
                                                            set.set("baseFireRes", parseInt(attrs, "fire"));
                                                            set.set("baseWaterRes", parseInt(attrs, "water"));
                                                            set.set("baseWindRes", parseInt(attrs, "wind"));
                                                            set.set("baseEarthRes", parseInt(attrs, "earth"));
                                                            set.set("baseHolyRes", parseInt(attrs, "holy"));
                                                            set.set("baseDarkRes", parseInt(attrs, "dark"));
                                                            set.set("baseElementRes", parseInt(attrs, "default"));
                                                            break;
                                                        }
                                                    }
                                                }
                                                break;
                                            }
                                            case "speed": {
                                                for (Node speedNode = statsNode.getFirstChild(); speedNode != null; speedNode = speedNode.getNextSibling()) {
                                                    attrs = speedNode.getAttributes();
                                                    switch (speedNode.getNodeName().toLowerCase()) {
                                                        case "walk": {
                                                            final var ground = parseDouble(attrs, "ground", 50);
                                                            set.set("baseWalkSpd", ground);
                                                            set.set("baseSwimWalkSpd", parseDouble(attrs, "swim", ground));
                                                            set.set("baseFlyWalkSpd", parseDouble(attrs, "fly", ground));
                                                            break;
                                                        }
                                                        case "run": {
                                                            final var ground = parseDouble(attrs, "ground", 120);
                                                            set.set("baseRunSpd", ground);
                                                            set.set("baseSwimRunSpd", parseDouble(attrs, "swim", ground));
                                                            set.set("baseFlyRunSpd", parseDouble(attrs, "fly", ground));
                                                            break;
                                                        }
                                                    }
                                                }
                                                break;
                                            }
                                            case "hittime": {
                                                set.set("hitTime", parseInt(npcNode, 100));
                                                break;
                                            }
                                        }
                                    }
                                    break;
                                }
                                case "status": {
                                    set.set("unique", (Boolean) computeIfNonNull(attrs.getNamedItem("unique"), this::parseBoolean));
                                    set.set("attackable", parseBoolean(attrs, "attackable", true));
                                    set.set("targetable", parseBoolean(attrs, "targetable", true));
                                    set.set("talkable", parseBoolean(attrs, "talkable", true));
                                    set.set("undying", parseBoolean(attrs, "undying", true));
                                    set.set("showName", parseBoolean(attrs, "showName", true));

                                    set.set("randomWalk", (Boolean) computeIfNonNull(attrs.getNamedItem("randomWalk"), this::parseBoolean));
                                    set.set("randomAnimation", parseBoolean(attrs, "randomAnimation", true));
                                    set.set("flying", parseBoolean(attrs, "flying"));
                                    set.set("canMove", parseBoolean(attrs, "canMove", true));
                                    set.set("noSleepMode", parseBoolean(attrs, "noSleepMode"));
                                    set.set("passableDoor", parseBoolean(attrs, "passableDoor"));
                                    set.set("hasSummoner", parseBoolean(attrs, "hasSummoner"));
                                    set.set("canBeSown", parseBoolean(attrs, "canBeSown"));
                                    set.set("isDeathPenalty", parseBoolean(attrs, "isDeathPenalty"));
                                    break;
                                }
                                case "skilllist": {
                                    skills = new HashMap<>();
                                    for (Node skillListNode = npcNode.getFirstChild(); skillListNode != null; skillListNode = skillListNode.getNextSibling()) {
                                        if ("skill".equalsIgnoreCase(skillListNode.getNodeName())) {
                                            attrs = skillListNode.getAttributes();
                                            final int skillId = parseInt(attrs, "id");
                                            final int skillLevel = parseInt(attrs, "level");
                                            final Skill skill = SkillEngine.getInstance().getSkill(skillId, skillLevel);
                                            if (skill != null) {
                                                skills.put(skill.getId(), skill);
                                            } else {
                                                LOGGER.warn("[" + f.getName() + "] skill not found. NPC ID: " + npcId + " Skill ID: " + skillId + " Skill Level: " + skillLevel);
                                            }
                                        }
                                    }
                                    break;
                                }
                                case "shots": {
                                    set.set("soulShot", parseInt(attrs, "soul"));
                                    set.set("spiritShot", parseInt(attrs, "spirit"));
                                    set.set("shotShotChance", parseInt(attrs, "shotChance"));
                                    set.set("spiritShotChance", parseInt(attrs, "spiritChance"));
                                    break;
                                }
                                case "corpsetime": {
                                    set.set("corpseTime", parseInt(npcNode, Config.DEFAULT_CORPSE_TIME));
                                    break;
                                }
                                case "excrteffect": {
                                    set.set("exCrtEffect", parseBoolean(npcNode, true));
                                    break;
                                }
                                case "snpcprophprate": {
                                    set.set("sNpcPropHpRate", npcNode.getTextContent()); // TODO: Implement me default 1 type double
                                    break;
                                }
                                case "ai": {
                                    set.set("aiType", parseString(attrs, "type", null));
                                    set.set("aggroRange", parseInt(attrs, "aggroRange"));
                                    set.set("clanHelpRange", parseInt(attrs, "clanHelpRange"));
                                    set.set("dodge", parseInt(attrs, "dodge"));
                                    set.set("isChaos", parseBoolean(attrs, "isChaos"));
                                    set.set("isAggressive", parseBoolean(attrs, "isAggressive"));
                                    for (Node aiNode = npcNode.getFirstChild(); aiNode != null; aiNode = aiNode.getNextSibling()) {
                                        attrs = aiNode.getAttributes();
                                        switch (aiNode.getNodeName().toLowerCase()) {
                                            case "skill": {
                                                set.set("minSkillChance", parseInt(attrs, "minChance", 7));
                                                set.set("maxSkillChance", parseInt(attrs, "maxChance", 15));
                                                set.set("primarySkillId", parseInt(attrs, "primaryId"));
                                                set.set("shortRangeSkillId", parseInt(attrs, "shortRangeId"));
                                                set.set("shortRangeSkillChance", parseInt(attrs, "shortRangeChance"));
                                                set.set("longRangeSkillId", parseInt(attrs, "longRangeId"));
                                                set.set("longRangeSkillChance", parseInt(attrs, "longRangeChance"));
                                                break;
                                            }
                                            case "clanlist": {
                                                for (Node clanListNode = aiNode.getFirstChild(); clanListNode != null; clanListNode = clanListNode.getNextSibling()) {
                                                    switch (clanListNode.getNodeName().toLowerCase()) {
                                                        case "clan": {
                                                            if (clans == null) {
                                                                clans = new HashSet<>(1);
                                                            }
                                                            clans.add(getOrCreateClanId(clanListNode.getTextContent()));
                                                            break;
                                                        }
                                                        case "ignorenpcid": {
                                                            if (ignoreClanNpcIds == null) {
                                                                ignoreClanNpcIds = new HashSet<>(1);
                                                            }
                                                            ignoreClanNpcIds.add(Integer.parseInt(clanListNode.getTextContent()));
                                                            break;
                                                        }
                                                    }
                                                }
                                                break;
                                            }
                                        }
                                    }
                                    break;
                                }
                                case "droplists": {
                                    for (Node drop_lists_node = npcNode.getFirstChild(); drop_lists_node != null; drop_lists_node = drop_lists_node.getNextSibling()) {
                                        DropType dropType = null;

                                        try {
                                            dropType = Enum.valueOf(DropType.class, drop_lists_node.getNodeName().toUpperCase());
                                        } catch (Exception e) {
                                        }

                                        if (dropType != null) {
                                            if (dropLists == null) {
                                                dropLists = new ArrayList<>();
                                            }

                                            for (Node drop_node = drop_lists_node.getFirstChild(); drop_node != null; drop_node = drop_node.getNextSibling()) {
                                                final NamedNodeMap drop_attrs = drop_node.getAttributes();
                                                if ("item".equalsIgnoreCase(drop_node.getNodeName())) {
                                                    final double chance = parseDouble(drop_attrs, "chance");
                                                    final DropHolder dropItem = new DropHolder(dropType, parseInt(drop_attrs, "id"), parseLong(drop_attrs, "min"), parseLong(drop_attrs, "max"), dropType == DropType.LUCKY ? chance / 100 : chance);
                                                    if (ItemEngine.getInstance().getTemplate(parseInt(drop_attrs, "id")) == null) {
                                                        LOGGER.warn("DropListItem: Could not find item with id " + parseInt(drop_attrs, "id") + ".");
                                                    } else {
                                                        dropLists.add(dropItem);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    break;
                                }
                                case "extenddrop": {
                                    final IntList extendDrop = new ArrayIntList();
                                    forEach(npcNode, "id", idNode ->
                                    {
                                        extendDrop.add(Integer.parseInt(idNode.getTextContent()));
                                    });
                                    set.set("extendDrop", extendDrop);
                                    break;
                                }
                                case "collision": {
                                    for (Node collisionNode = npcNode.getFirstChild(); collisionNode != null; collisionNode = collisionNode.getNextSibling()) {
                                        attrs = collisionNode.getAttributes();
                                        switch (collisionNode.getNodeName().toLowerCase()) {
                                            case "radius": {
                                                set.set("collision_radius", parseDouble(attrs, "normal"));
                                                set.set("collisionRadiusGrown", parseDouble(attrs, "grown"));
                                                break;
                                            }
                                            case "height": {
                                                set.set("collision_height", parseDouble(attrs, "normal"));
                                                set.set("collisionHeightGrown", parseDouble(attrs, "grown"));
                                                break;
                                            }
                                        }
                                    }
                                    break;
                                }
                            }
                        }

                        NpcTemplate template = npcs.get(npcId);
                        if (template == null) {
                            template = new NpcTemplate(set);
                            npcs.put(template.getId(), template);
                        } else {
                            template.set(set);
                        }

                        if (parameters != null) {
                            // Using unmodifiable map parameters of template are not meant to be changed at runtime.
                            template.setParameters(new StatsSet(Collections.unmodifiableMap(parameters)));
                        } else {
                            template.setParameters(StatsSet.EMPTY_STATSET);
                        }

                        if (skills != null) {
                            Map<AISkillScope, List<Skill>> aiSkillLists = null;
                            for (Skill skill : skills.values()) {
                                if (!skill.isPassive()) {
                                    if (aiSkillLists == null) {
                                        aiSkillLists = new EnumMap<>(AISkillScope.class);
                                    }

                                    final List<AISkillScope> aiSkillScopes = new ArrayList<>();
                                    final AISkillScope shortOrLongRangeScope = skill.getCastRange() <= 150 ? AISkillScope.SHORT_RANGE : AISkillScope.LONG_RANGE;
                                    if (skill.isSuicideAttack()) {
                                        aiSkillScopes.add(AISkillScope.SUICIDE);
                                    } else {
                                        aiSkillScopes.add(AISkillScope.GENERAL);

                                        if (skill.isContinuous()) {
                                            if (!skill.isDebuff()) {
                                                aiSkillScopes.add(AISkillScope.BUFF);
                                            } else {
                                                aiSkillScopes.add(AISkillScope.DEBUFF);
                                                aiSkillScopes.add(AISkillScope.COT);
                                                aiSkillScopes.add(shortOrLongRangeScope);
                                            }
                                        } else if (skill.hasAnyEffectType(EffectType.DISPEL, EffectType.DISPEL_BY_SLOT)) {
                                            aiSkillScopes.add(AISkillScope.NEGATIVE);
                                            aiSkillScopes.add(shortOrLongRangeScope);
                                        } else if (skill.hasAnyEffectType(EffectType.HEAL)) {
                                            aiSkillScopes.add(AISkillScope.HEAL);
                                        } else if (skill.hasAnyEffectType(EffectType.PHYSICAL_ATTACK, EffectType.PHYSICAL_ATTACK_HP_LINK, EffectType.MAGICAL_ATTACK, EffectType.DEATH_LINK, EffectType.HP_DRAIN)) {
                                            aiSkillScopes.add(AISkillScope.ATTACK);
                                            aiSkillScopes.add(AISkillScope.UNIVERSAL);
                                            aiSkillScopes.add(shortOrLongRangeScope);
                                        } else if (skill.hasAnyEffectType(EffectType.SLEEP)) {
                                            aiSkillScopes.add(AISkillScope.IMMOBILIZE);
                                        } else if (skill.hasAnyEffectType(EffectType.BLOCK_ACTIONS, EffectType.ROOT)) {
                                            aiSkillScopes.add(AISkillScope.IMMOBILIZE);
                                            aiSkillScopes.add(shortOrLongRangeScope);
                                        } else if (skill.hasAnyEffectType(EffectType.MUTE, EffectType.BLOCK_CONTROL)) {
                                            aiSkillScopes.add(AISkillScope.COT);
                                            aiSkillScopes.add(shortOrLongRangeScope);
                                        } else if (skill.hasAnyEffectType(EffectType.DMG_OVER_TIME, EffectType.DMG_OVER_TIME_PERCENT)) {
                                            aiSkillScopes.add(shortOrLongRangeScope);
                                        } else if (skill.hasAnyEffectType(EffectType.RESURRECTION)) {
                                            aiSkillScopes.add(AISkillScope.RES);
                                        } else {
                                            aiSkillScopes.add(AISkillScope.UNIVERSAL);
                                        }
                                    }

                                    for (AISkillScope aiSkillScope : aiSkillScopes) {
                                        aiSkillLists.computeIfAbsent(aiSkillScope, k -> new ArrayList<>()).add(skill);
                                    }
                                }
                            }

                            template.setSkills(skills);
                            template.setAISkillLists(aiSkillLists);
                        } else {
                            template.setSkills(null);
                            template.setAISkillLists(null);
                        }

                        template.setClans(clans);
                        template.setIgnoreClanNpcIds(ignoreClanNpcIds);

                        if (dropLists != null) {
                            for (DropHolder dropHolder : dropLists) {
                                if (!Config.DROP_RANDOM_CRAFT_MATERIALS && (dropHolder.getItemId() >= 92908) && (dropHolder.getItemId() <= 92919))
                                {
                                    continue;
                                }

                                switch (dropHolder.getDropType()) {
                                    case DROP:
                                    case LUCKY: // TODO: Luck is added to death drops.
                                    {
                                        template.addDrop(dropHolder);
                                        break;
                                    }
                                    case SPOIL: {
                                        template.addSpoil(dropHolder);
                                        break;
                                    }
                                }
                            }
                        }

                        if (!template.getParameters().getMinionList("Privates").isEmpty()) {
                            if (template.getParameters().getSet().get("SummonPrivateRate") == null) {
                                masterIDs.add(template.getId());
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Gets or creates a clan id if it doesnt exists.
     *
     * @param clanName the clan name to get or create its id
     * @return the clan id for the given clan name
     */
    private int getOrCreateClanId(String clanName) {
        Integer id = clans.get(clanName);
        if (id == null) {
            id = clans.size();
            clans.put(clanName, id);
        }
        return id;
    }

    /**
     * Gets the clan id
     *
     * @param clanName the clan name to get its id
     * @return the clan id for the given clan name if it exists, -1 otherwise
     */
    public int getClanId(String clanName) {
        final Integer id = clans.get(clanName);
        return id != null ? id : -1;
    }

    public Set<String> getClansByIds(Set<Integer> clanIds) {
        final Set<String> result = new HashSet<>();
        if (clanIds == null) {
            return result;
        }
        for (Entry<String, Integer> record : clans.entrySet()) {
            for (int id : clanIds) {
                if (record.getValue() == id) {
                    result.add(record.getKey());
                }
            }
        }
        return result;
    }

    /**
     * Gets the template.
     *
     * @param id the template Id to get.
     * @return the template for the given id.
     */
    public NpcTemplate getTemplate(int id) {
        return npcs.get(id);
    }

    /**
     * Gets the template by name.
     *
     * @param name of the template to get.
     * @return the template for the given name.
     */
    public NpcTemplate getTemplateByName(String name) {
        for (NpcTemplate npcTemplate : npcs.values()) {
            if (npcTemplate.getName().equalsIgnoreCase(name)) {
                return npcTemplate;
            }
        }
        return null;
    }

    /**
     * Gets all templates matching the filter.
     *
     * @param filter
     * @return the template list for the given filter
     */
    public List<NpcTemplate> getTemplates(Predicate<NpcTemplate> filter) {
        //@formatter:off
        return npcs.values().stream()
                .filter(filter)
                .collect(Collectors.toList());
        //@formatter:on
    }

    /**
     * Gets the all of level.
     *
     * @param lvls of all the templates to get.
     * @return the template list for the given level.
     */
    public List<NpcTemplate> getAllOfLevel(int... lvls) {
        return getTemplates(template -> contains(lvls, template.getLevel()));
    }

    /**
     * Gets the all monsters of level.
     *
     * @param lvls of all the monster templates to get.
     * @return the template list for the given level.
     */
    public List<NpcTemplate> getAllMonstersOfLevel(int... lvls) {
        return getTemplates(template -> contains(lvls, template.getLevel()) && template.isType("Monster"));
    }

    /**
     * Gets the all npc starting with.
     *
     * @param text of all the NPC templates which its name start with.
     * @return the template list for the given letter.
     */
    public List<NpcTemplate> getAllNpcStartingWith(String text) {
        return getTemplates(template -> template.isType("Npc") && template.getName().startsWith(text));
    }

    public boolean existsNpc(int npcId) {
        return nonNull(npcs.get(npcId));
    }

    public static void init() {
        getInstance().load();
    }

    public static NpcData getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final NpcData INSTANCE = new NpcData();
    }
}
