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
package org.l2j.gameserver.model.actor.templates;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.api.elemental.ElementalType;
import org.l2j.gameserver.data.xml.impl.NpcData;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.vip.VipEngine;
import org.l2j.gameserver.enums.*;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.holders.DropHolder;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.interfaces.IIdentifiable;
import org.l2j.gameserver.model.item.CommonItem;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.util.GameUtils;

import java.util.*;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * NPC template.
 *
 * @author NosBit
 */
public final class NpcTemplate extends CreatureTemplate implements IIdentifiable {

    private final List<DropHolder> _dropListDeath = new ArrayList<>();
    private final List<DropHolder> _dropListSpoil = new ArrayList<>();
    private int _id;
    private int _displayId;
    private byte _level;
    private String _type;
    private String _name;
    private boolean _usingServerSideName;
    private String _title;
    private boolean _usingServerSideTitle;
    private StatsSet _parameters;
    private Sex _sex;
    private int _chestId;
    private int _rhandId;
    private int _lhandId;
    private int _weaponEnchant;
    private double _exp;
    private double _sp;
    private double _raidPoints;
    private boolean _unique;
    private boolean _attackable;
    private boolean _targetable;
    private boolean _talkable;
    private boolean _isQuestMonster;
    private boolean _undying;
    private boolean _showName;
    private boolean _randomWalk;
    private boolean _randomAnimation;
    private boolean _flying;
    private boolean _canMove;
    private boolean _noSleepMode;
    private boolean _passableDoor;
    private boolean _hasSummoner;
    private boolean _canBeSown;
    private boolean _canBeCrt;
    private boolean _isDeathPenalty;
    private int _corpseTime;
    private AIType _aiType;
    private int _aggroRange;
    private int _clanHelpRange;
    private int _dodge;
    private boolean _isChaos;
    private boolean _isAggressive;
    private int _soulShot;
    private int _spiritShot;
    private int _soulShotChance;
    private int _spiritShotChance;
    private int _minSkillChance;
    private int _maxSkillChance;
    private double _hitTimeFactor;
    private double _hitTimeFactorSkill;
    private Map<Integer, Skill> _skills;
    private Map<AISkillScope, List<Skill>> _aiSkillLists;
    private Set<Integer> _clans;
    private Set<Integer> _ignoreClanNpcIds;
    private double _collisionRadiusGrown;
    private double _collisionHeightGrown;
    private int _mpRewardValue;
    private MpRewardType _mpRewardType;
    private int _mpRewardTicks;
    private MpRewardAffectType _mpRewardAffectType;

    private List<Integer> _extendDrop;
    private ElementalType elementalType;
    private long attributeExp;

    /**
     * Constructor of Creature.
     *
     * @param set The StatsSet object to transfer data to the method
     */
    public NpcTemplate(StatsSet set) {
        super(set);
    }

    public static boolean isAssignableTo(Class<?> sub, Class<?> clazz) {
        // If clazz represents an interface
        if (clazz.isInterface()) {
            // check if obj implements the clazz interface
            for (Class<?> interface1 : sub.getInterfaces()) {
                if (clazz.getName().equals(interface1.getName())) {
                    return true;
                }
            }
        } else {
            do {
                if (sub.getName().equals(clazz.getName())) {
                    return true;
                }

                sub = sub.getSuperclass();
            }
            while (sub != null);
        }
        return false;
    }

    /**
     * Checks if obj can be assigned to the Class represented by clazz.<br>
     * This is true if, and only if, obj is the same class represented by clazz, or a subclass of it or obj implements the interface represented by clazz.
     *
     * @param obj
     * @param clazz
     * @return {@code true} if the object can be assigned to the class, {@code false} otherwise
     */
    public static boolean isAssignableTo(Object obj, Class<?> clazz) {
        return isAssignableTo(obj.getClass(), clazz);
    }

    @Override
    public void set(StatsSet set) {
        super.set(set);
        _id = set.getInt("id");
        _displayId = set.getInt("displayId", _id);
        _level = set.getByte("level", (byte) 70);
        _type = set.getString("type", "Npc");
        _name = set.getString("name", "");
        _usingServerSideName = set.getBoolean("usingServerSideName", false);
        _title = set.getString("title", "");
        _usingServerSideTitle = set.getBoolean("usingServerSideTitle", false);
        setRace(set.getEnum("race", Race.class, Race.NONE));
        _sex = set.getEnum("sex", Sex.class, Sex.ETC);

        elementalType = set.getEnum("elementalType", ElementalType.class);

        _chestId = set.getInt("chestId", 0);
        _rhandId = set.getInt("rhandId", 0);
        _lhandId = set.getInt("lhandId", 0);
        _weaponEnchant = set.getInt("weaponEnchant", 0);

        _exp = set.getDouble("exp", 0);
        _sp = set.getDouble("sp", 0);
        _raidPoints = set.getDouble("raidPoints", 0);
        attributeExp = set.getLong("attribute_exp", 0);

        _unique = set.getBoolean("unique", !_type.equals("Monster") && !_type.equals("RaidBoss") && !_type.equals("GrandBoss"));
        _attackable = set.getBoolean("attackable", true);
        _targetable = set.getBoolean("targetable", true);
        _talkable = set.getBoolean("talkable", true);
        _isQuestMonster = _title.contains("Quest");
        _undying = set.getBoolean("undying", true);
        _showName = set.getBoolean("showName", true);
        _randomWalk = set.getBoolean("randomWalk", !_type.equals("Guard"));
        _randomAnimation = set.getBoolean("randomAnimation", true);
        _flying = set.getBoolean("flying", false);
        _canMove = set.getBoolean("canMove", true);
        _noSleepMode = set.getBoolean("noSleepMode", false);
        _passableDoor = set.getBoolean("passableDoor", false);
        _hasSummoner = set.getBoolean("hasSummoner", false);
        _canBeSown = set.getBoolean("canBeSown", false);
        _canBeCrt = set.getBoolean("exCrtEffect", true);
        _isDeathPenalty = set.getBoolean("isDeathPenalty", false);

        _corpseTime = set.getInt("corpseTime", Config.DEFAULT_CORPSE_TIME);

        _aiType = set.getEnum("aiType", AIType.class, AIType.FIGHTER);
        _aggroRange = set.getInt("aggroRange", 0);
        _clanHelpRange = set.getInt("clanHelpRange", 0);
        _dodge = set.getInt("dodge", 0);
        _isChaos = set.getBoolean("isChaos", false);
        _isAggressive = set.getBoolean("isAggressive", false);

        _soulShot = set.getInt("soulShot", 0);
        _spiritShot = set.getInt("spiritShot", 0);
        _soulShotChance = set.getInt("shotShotChance", 0);
        _spiritShotChance = set.getInt("spiritShotChance", 0);

        _minSkillChance = set.getInt("minSkillChance", 7);
        _maxSkillChance = set.getInt("maxSkillChance", 15);

        _hitTimeFactor = set.getInt("hitTime", 100) / 100d;
        _hitTimeFactorSkill = set.getInt("hitTimeSkill", 100) / 100d;

        _collisionRadiusGrown = set.getDouble("collisionRadiusGrown", 0);
        _collisionHeightGrown = set.getDouble("collisionHeightGrown", 0);

        _mpRewardValue = set.getInt("mpRewardValue", 0);
        _mpRewardType = set.getEnum("mpRewardType", MpRewardType.class, MpRewardType.DIFF);
        _mpRewardTicks = set.getInt("mpRewardTicks", 0);
        _mpRewardAffectType = set.getEnum("mpRewardAffectType", MpRewardAffectType.class, MpRewardAffectType.SOLO);

        _extendDrop = set.getList("extendDrop", Integer.class);

        if (Config.ENABLE_NPC_STAT_MULTIPIERS) // Custom NPC Stat Multipliers
        {
            switch (_type) {
                case "Monster": {
                    _baseValues.put(Stat.MAX_HP, getBaseHpMax() * Config.MONSTER_HP_MULTIPLIER);
                    _baseValues.put(Stat.MAX_MP, getBaseMpMax() * Config.MONSTER_MP_MULTIPLIER);
                    _baseValues.put(Stat.PHYSICAL_ATTACK, getBasePAtk() * Config.MONSTER_PATK_MULTIPLIER);
                    _baseValues.put(Stat.MAGIC_ATTACK, getBaseMAtk() * Config.MONSTER_MATK_MULTIPLIER);
                    _baseValues.put(Stat.PHYSICAL_DEFENCE, getBasePDef() * Config.MONSTER_PDEF_MULTIPLIER);
                    _baseValues.put(Stat.MAGICAL_DEFENCE, getBaseMDef() * Config.MONSTER_MDEF_MULTIPLIER);
                    _aggroRange *= Config.MONSTER_AGRRO_RANGE_MULTIPLIER;
                    _clanHelpRange *= Config.MONSTER_CLAN_HELP_RANGE_MULTIPLIER;
                    break;
                }
                case "RaidBoss":
                case "GrandBoss": {
                    _baseValues.put(Stat.MAX_HP, getBaseHpMax() * Config.RAIDBOSS_HP_MULTIPLIER);
                    _baseValues.put(Stat.MAX_MP, getBaseMpMax() * Config.RAIDBOSS_MP_MULTIPLIER);
                    _baseValues.put(Stat.PHYSICAL_ATTACK, getBasePAtk() * Config.RAIDBOSS_PATK_MULTIPLIER);
                    _baseValues.put(Stat.MAGIC_ATTACK, getBaseMAtk() * Config.RAIDBOSS_MATK_MULTIPLIER);
                    _baseValues.put(Stat.PHYSICAL_DEFENCE, getBasePDef() * Config.RAIDBOSS_PDEF_MULTIPLIER);
                    _baseValues.put(Stat.MAGICAL_DEFENCE, getBaseMDef() * Config.RAIDBOSS_MDEF_MULTIPLIER);
                    _aggroRange *= Config.RAIDBOSS_AGRRO_RANGE_MULTIPLIER;
                    _clanHelpRange *= Config.RAIDBOSS_CLAN_HELP_RANGE_MULTIPLIER;
                    break;
                }
                case "Guard": {
                    _baseValues.put(Stat.MAX_HP, getBaseHpMax() * Config.GUARD_HP_MULTIPLIER);
                    _baseValues.put(Stat.MAX_MP, getBaseMpMax() * Config.GUARD_MP_MULTIPLIER);
                    _baseValues.put(Stat.PHYSICAL_ATTACK, getBasePAtk() * Config.GUARD_PATK_MULTIPLIER);
                    _baseValues.put(Stat.MAGIC_ATTACK, getBaseMAtk() * Config.GUARD_MATK_MULTIPLIER);
                    _baseValues.put(Stat.PHYSICAL_DEFENCE, getBasePDef() * Config.GUARD_PDEF_MULTIPLIER);
                    _baseValues.put(Stat.MAGICAL_DEFENCE, getBaseMDef() * Config.GUARD_MDEF_MULTIPLIER);
                    _aggroRange *= Config.GUARD_AGRRO_RANGE_MULTIPLIER;
                    _clanHelpRange *= Config.GUARD_CLAN_HELP_RANGE_MULTIPLIER;
                    break;
                }
                case "Defender": {
                    _baseValues.put(Stat.MAX_HP, getBaseHpMax() * Config.DEFENDER_HP_MULTIPLIER);
                    _baseValues.put(Stat.MAX_MP, getBaseMpMax() * Config.DEFENDER_MP_MULTIPLIER);
                    _baseValues.put(Stat.PHYSICAL_ATTACK, getBasePAtk() * Config.DEFENDER_PATK_MULTIPLIER);
                    _baseValues.put(Stat.MAGIC_ATTACK, getBaseMAtk() * Config.DEFENDER_MATK_MULTIPLIER);
                    _baseValues.put(Stat.PHYSICAL_DEFENCE, getBasePDef() * Config.DEFENDER_PDEF_MULTIPLIER);
                    _baseValues.put(Stat.MAGICAL_DEFENCE, getBaseMDef() * Config.DEFENDER_MDEF_MULTIPLIER);
                    _aggroRange *= Config.DEFENDER_AGRRO_RANGE_MULTIPLIER;
                    _clanHelpRange *= Config.DEFENDER_CLAN_HELP_RANGE_MULTIPLIER;
                    break;
                }
            }
        }
    }

    @Override
    public int getId() {
        return _id;
    }

    public int getDisplayId() {
        return _displayId;
    }

    public byte getLevel() {
        return _level;
    }

    public String getType() {
        return _type;
    }

    public boolean isType(String type) {
        return _type.equalsIgnoreCase(type);
    }

    public String getName() {
        return _name;
    }

    public boolean isUsingServerSideName() {
        return _usingServerSideName;
    }

    public String getTitle() {
        return _title;
    }

    public boolean isUsingServerSideTitle() {
        return _usingServerSideTitle;
    }

    public StatsSet getParameters() {
        return _parameters;
    }

    public void setParameters(StatsSet set) {
        _parameters = set;
    }

    public Sex getSex() {
        return _sex;
    }

    public int getChestId() {
        return _chestId;
    }

    public int getRHandId() {
        return _rhandId;
    }

    public int getLHandId() {
        return _lhandId;
    }

    public int getWeaponEnchant() {
        return _weaponEnchant;
    }

    public double getExp() {
        return _exp;
    }

    public double getSP() {
        return _sp;
    }

    public long getAttributeExp() {
        return attributeExp;
    }

    public ElementalType getElementalType() {
        return elementalType;
    }


    public double getRaidPoints() {
        return _raidPoints;
    }

    public boolean isUnique() {
        return _unique;
    }

    public boolean isAttackable() {
        return _attackable;
    }

    public boolean isTargetable() {
        return _targetable;
    }

    public boolean isTalkable() {
        return _talkable;
    }

    public boolean isQuestMonster() {
        return _isQuestMonster;
    }

    public boolean isUndying() {
        return _undying;
    }

    public boolean isShowName() {
        return _showName;
    }

    public boolean isRandomWalkEnabled() {
        return _randomWalk;
    }

    public boolean isRandomAnimationEnabled() {
        return _randomAnimation;
    }

    public boolean isFlying() {
        return _flying;
    }

    public boolean canMove() {
        return _canMove;
    }

    public boolean isNoSleepMode() {
        return _noSleepMode;
    }

    public boolean isPassableDoor() {
        return _passableDoor;
    }

    public boolean hasSummoner() {
        return _hasSummoner;
    }

    public boolean canBeSown() {
        return _canBeSown;
    }

    public boolean canBeCrt() {
        return _canBeCrt;
    }

    public boolean isDeathPenalty() {
        return _isDeathPenalty;
    }

    public int getCorpseTime() {
        return _corpseTime;
    }

    public AIType getAIType() {
        return _aiType;
    }

    public int getAggroRange() {
        return _aggroRange;
    }

    public int getClanHelpRange() {
        return _clanHelpRange;
    }

    public int getDodge() {
        return _dodge;
    }

    public boolean isChaos() {
        return _isChaos;
    }

    public boolean isAggressive() {
        return _isAggressive;
    }

    public int getSoulShot() {
        return _soulShot;
    }

    public int getSpiritShot() {
        return _spiritShot;
    }

    public int getSoulShotChance() {
        return _soulShotChance;
    }

    public int getSpiritShotChance() {
        return _spiritShotChance;
    }

    public int getMinSkillChance() {
        return _minSkillChance;
    }

    public int getMaxSkillChance() {
        return _maxSkillChance;
    }

    public double getHitTimeFactor() {
        return _hitTimeFactor;
    }

    public double getHitTimeFactorSkill() {
        return _hitTimeFactorSkill;
    }

    @Override
    public Map<Integer, Skill> getSkills() {
        return _skills;
    }

    public void setSkills(Map<Integer, Skill> skills) {
        _skills = skills != null ? Collections.unmodifiableMap(skills) : Collections.emptyMap();
    }

    public List<Skill> getAISkills(AISkillScope aiSkillScope) {
        return _aiSkillLists.getOrDefault(aiSkillScope, Collections.emptyList());
    }

    public void setAISkillLists(Map<AISkillScope, List<Skill>> aiSkillLists) {
        _aiSkillLists = aiSkillLists != null ? Collections.unmodifiableMap(aiSkillLists) : Collections.emptyMap();
    }

    public Set<Integer> getClans() {
        return _clans;
    }

    /**
     * @param clans A sorted array of clan ids
     */
    public void setClans(Set<Integer> clans) {
        _clans = clans != null ? Collections.unmodifiableSet(clans) : null;
    }

    public int getMpRewardValue() {
        return _mpRewardValue;
    }

    public MpRewardType getMpRewardType() {
        return _mpRewardType;
    }

    public int getMpRewardTicks() {
        return _mpRewardTicks;
    }

    public MpRewardAffectType getMpRewardAffectType() {
        return _mpRewardAffectType;
    }

    /**
     * @param clanName  clan name to check if it belongs to this NPC template clans.
     * @param clanNames clan names to check if they belong to this NPC template clans.
     * @return {@code true} if at least one of the clan names belong to this NPC template clans, {@code false} otherwise.
     */
    public boolean isClan(String clanName, String... clanNames) {
        // Using local variable for the sake of reloading since it can be turned to null.
        final Set<Integer> clans = _clans;

        if (clans == null) {
            return false;
        }

        int clanId = NpcData.getInstance().getClanId("ALL");
        if (clans.contains(clanId)) {
            return true;
        }

        clanId = NpcData.getInstance().getClanId(clanName);
        if (clans.contains(clanId)) {
            return true;
        }

        for (String name : clanNames) {
            clanId = NpcData.getInstance().getClanId(name);
            if (clans.contains(clanId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param clans A set of clan names to check if they belong to this NPC template clans.
     * @return {@code true} if at least one of the clan names belong to this NPC template clans, {@code false} otherwise.
     */
    public boolean isClan(Set<Integer> clans) {
        // Using local variable for the sake of reloading since it can be turned to null.
        final Set<Integer> clanSet = _clans;

        if ((clanSet == null) || (clans == null)) {
            return false;
        }

        final int clanId = NpcData.getInstance().getClanId("ALL");
        if (clanSet.contains(clanId)) {
            return true;
        }

        for (Integer id : clans) {
            if (clanSet.contains(id)) {
                return true;
            }
        }
        return false;
    }

    public Set<Integer> getIgnoreClanNpcIds() {
        return _ignoreClanNpcIds;
    }

    /**
     * @param ignoreClanNpcIds the ignore clan npc ids
     */
    public void setIgnoreClanNpcIds(Set<Integer> ignoreClanNpcIds) {
        _ignoreClanNpcIds = ignoreClanNpcIds != null ? Collections.unmodifiableSet(ignoreClanNpcIds) : null;
    }

    public void addDrop(DropHolder dropHolder) {
        _dropListDeath.add(dropHolder);
    }

    public void addSpoil(DropHolder dropHolder) {
        _dropListSpoil.add(dropHolder);
    }

    public List<DropHolder> getDropList(DropType dropType) {
        return switch (dropType) {
            case DROP, LUCKY -> _dropListDeath;
            case SPOIL -> _dropListSpoil;
        };
    }

    private double calculateLevelGapChanceToDrop(DropHolder dropItem, int levelDifference) {
        final double levelGapChanceToDrop;
        if (dropItem.getItemId() == CommonItem.ADENA) {
            levelGapChanceToDrop = GameUtils.map(levelDifference, -Config.DROP_ADENA_MAX_LEVEL_DIFFERENCE, -Config.DROP_ADENA_MIN_LEVEL_DIFFERENCE, Config.DROP_ADENA_MIN_LEVEL_GAP_CHANCE, 100.0);
        } else {
            levelGapChanceToDrop = GameUtils.map(levelDifference, -Config.DROP_ITEM_MAX_LEVEL_DIFFERENCE, -Config.DROP_ITEM_MIN_LEVEL_DIFFERENCE, Config.DROP_ITEM_MIN_LEVEL_GAP_CHANCE, 100.0);
        }

        return levelGapChanceToDrop;
    }

    private void processDropList(DropType dropType, Collection<ItemHolder> itemsToDrop, Creature victim, Creature killer) {
        var list = getDropList(dropType);
        if (isNull(list)) {
            return;
        }

        // randomize drop order
        Collections.shuffle(list);

        int dropOccurrenceCounter = victim.isRaid() ? Config.DROP_MAX_OCCURRENCES_RAIDBOSS : Config.DROP_MAX_OCCURRENCES_NORMAL;
        for (DropHolder dropItem : list) {

            if (dropOccurrenceCounter == 0 && dropItem.getChance() < 100) {
                continue;
            }

            final ItemHolder drop = calculateDropWithLevelGap(dropItem, victim, killer);
            if(nonNull(drop)) {
                if (dropItem.getChance() < 100) {
                    dropOccurrenceCounter--;
                }

                itemsToDrop.add(drop);
            }
        }

        if (victim.isChampion()) {
            if ((victim.getLevel() < killer.getLevel()) && (Rnd.get(100) < Config.CHAMPION_REWARD_LOWER_LVL_ITEM_CHANCE)) {
                return;
            }
            if ((victim.getLevel() > killer.getLevel()) && (Rnd.get(100) < Config.CHAMPION_REWARD_HIGHER_LVL_ITEM_CHANCE)) {
                return;
            }

            itemsToDrop.add(new ItemHolder(Config.CHAMPION_REWARD_ID, Config.CHAMPION_REWARD_QTY));
        }
    }

    private void proccessEtcDrop(Collection<ItemHolder> items, Creature victim, Creature killer) {
        final List<DropHolder> dropList = new ArrayList<>();
        if (nonNull(killer.getActingPlayer())) {
            float silverCoinChance = VipEngine.getInstance().getSilverCoinDropChance(killer.getActingPlayer());
            float rustyCoinChance = VipEngine.getInstance().getRustyCoinDropChance(killer.getActingPlayer());
            float l2CoinDropChance = (float) killer.getStats().getValue(Stat.BONUS_L2COIN_DROP_RATE) + Config.L2_COIN_DROP_RATE;

            if (silverCoinChance > 0) {
                dropList.add(new DropHolder(DropType.DROP, CommonItem.SILVER_COIN, 2, 5, silverCoinChance));
            }

            if (rustyCoinChance > 0) {
                dropList.add(new DropHolder(DropType.DROP, CommonItem.RUSTY_COIN, 2, 5, rustyCoinChance));
            }

            if (l2CoinDropChance > 0) {
                dropList.add(new DropHolder(DropType.DROP, CommonItem.L2_COIN, 1, 1, l2CoinDropChance));
            }
        }

        for (DropHolder dropItem : dropList) {
            final ItemHolder drop = calculateDropWithLevelGap(dropItem, victim, killer);
            if (drop == null) {
                continue;
            }

            items.add(drop);
        }
    }

    private ItemHolder calculateDropWithLevelGap(DropHolder dropItem, Creature victim, Creature killer) {
        final int levelDifference = victim.getLevel() - killer.getLevel();
        double levelGapChanceToDrop = calculateLevelGapChanceToDrop(dropItem, levelDifference);
        if (!Rnd.chance(levelGapChanceToDrop)) {
            return null;
        }

        return calculateDrop(dropItem, victim, killer);
    }

    public Collection<ItemHolder> calculateDrops(DropType dropType, Creature victim, Creature killer) {
        Collection<ItemHolder> calculatedDrops = new ArrayList<>();
        if (dropType == DropType.DROP) {
            proccessEtcDrop(calculatedDrops, victim, killer);
        }

        processDropList(dropType, calculatedDrops, victim, killer);

        return calculatedDrops;
    }

    /**
     * All item drop chance calculations are done by this method.
     *
     * @param dropItem
     * @param victim
     * @param killer
     * @return ItemHolder
     */
    private ItemHolder calculateDrop(DropHolder dropItem, Creature victim, Creature killer) {
        switch (dropItem.getDropType()) {
            case DROP:
            case LUCKY: {
                final int itemId = dropItem.getItemId();
                final ItemTemplate item = ItemEngine.getInstance().getTemplate(itemId);
                final boolean champion = victim.isChampion();

                double rateChance = 1;
                if (Config.RATE_DROP_CHANCE_BY_ID.get(itemId) != null) {
                    rateChance *= Config.RATE_DROP_CHANCE_BY_ID.get(dropItem.getItemId());
                    if (champion && (itemId == CommonItem.ADENA)) {
                        rateChance *= Config.CHAMPION_ADENAS_REWARDS_CHANCE;
                    }
                } else if (item.hasExImmediateEffect()) {
                    rateChance *= Config.RATE_HERB_DROP_CHANCE_MULTIPLIER;
                } else if (victim.isRaid()) {
                    rateChance *= Config.RATE_RAID_DROP_CHANCE_MULTIPLIER;
                } else {
                    rateChance *= Config.RATE_DEATH_DROP_CHANCE_MULTIPLIER * (champion ? Config.CHAMPION_REWARDS_CHANCE : 1);
                }

                // bonus drop rate effect
                rateChance *= killer.getStats().getValue(Stat.BONUS_DROP_RATE, 1);

                // calculate if item will drop
                if (Rnd.chance(dropItem.getChance() * rateChance)) {
                    // amount is calculated after chance returned success
                    double rateAmount = 1;
                    if (Config.RATE_DROP_AMOUNT_BY_ID.get(itemId) != null) {
                        rateAmount *= Config.RATE_DROP_AMOUNT_BY_ID.get(itemId);
                        if (champion && (itemId == CommonItem.ADENA)) {
                            rateAmount *= Config.CHAMPION_ADENAS_REWARDS_AMOUNT;
                        }
                    } else if (item.hasExImmediateEffect()) {
                        rateAmount *= Config.RATE_HERB_DROP_AMOUNT_MULTIPLIER;
                    } else if (victim.isRaid()) {
                        rateAmount *= Config.RATE_RAID_DROP_AMOUNT_MULTIPLIER;
                    } else {
                        rateAmount *= Config.RATE_DEATH_DROP_AMOUNT_MULTIPLIER * (champion ? Config.CHAMPION_REWARDS_AMOUNT : 1);
                    }

                    // bonus drop amount effect
                    rateAmount *= killer.getStats().getValue(Stat.BONUS_DROP_AMOUNT, 1);

                    // finally
                    return new ItemHolder(itemId, (long) (Rnd.get(dropItem.getMin(), dropItem.getMax()) * rateAmount));
                }
                break;
            }
            case SPOIL: {
                // chance
                double rateChance = Config.RATE_SPOIL_DROP_CHANCE_MULTIPLIER;

                // bonus drop rate effect
                rateChance *= killer.getStats().getValue(Stat.BONUS_SPOIL_RATE, 1);

                // calculate if item will be rewarded
                if (Rnd.chance(dropItem.getChance() * rateChance)) {
                    // amount is calculated after chance returned success
                    double rateAmount = Config.RATE_SPOIL_DROP_AMOUNT_MULTIPLIER;

                    // finally
                    return new ItemHolder(dropItem.getItemId(), (long) (Rnd.get(dropItem.getMin(), dropItem.getMax()) * rateAmount));
                }
                break;
            }
        }
        return null;
    }

    public double getCollisionRadiusGrown() {
        return _collisionRadiusGrown;
    }

    public double getCollisionHeightGrown() {
        return _collisionHeightGrown;
    }

    public List<Integer> getExtendDrop() {
        return _extendDrop == null ? Collections.emptyList() : _extendDrop;
    }
}
