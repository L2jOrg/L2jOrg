package org.l2j.gameserver.engine.skill.api;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.xml.impl.EnchantSkillGroupsData;
import org.l2j.gameserver.data.xml.impl.SkillData;
import org.l2j.gameserver.data.xml.impl.SkillTreesData;
import org.l2j.gameserver.engine.skill.SkillType;
import org.l2j.gameserver.enums.AttributeType;
import org.l2j.gameserver.enums.BasicProperty;
import org.l2j.gameserver.enums.NextActionType;
import org.l2j.gameserver.enums.ShotType;
import org.l2j.gameserver.handler.AffectScopeHandler;
import org.l2j.gameserver.handler.IAffectScopeHandler;
import org.l2j.gameserver.handler.ITargetTypeHandler;
import org.l2j.gameserver.handler.TargetHandler;
import org.l2j.gameserver.model.PcCondOverride;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.cubic.CubicInstance;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.holders.AttachSkillHolder;
import org.l2j.gameserver.model.interfaces.IIdentifiable;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.skills.*;
import org.l2j.gameserver.model.skills.targets.AffectObject;
import org.l2j.gameserver.model.skills.targets.AffectScope;
import org.l2j.gameserver.model.skills.targets.TargetType;
import org.l2j.gameserver.model.stats.BasicPropertyResist;
import org.l2j.gameserver.model.stats.Formulas;
import org.l2j.gameserver.model.stats.TraitType;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.l2j.gameserver.util.GameUtils.*;

/**
 * @author JoeAlisson
 */
public final class Skill implements IIdentifiable, Cloneable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Skill.class);

    private final int id;
    private final String name;
    private final SkillOperateType operateType;
    private SkillType type;

    private int level;
    private  int castRange;

    private int _subLevel;
    private int displayId;
    private int _displayLevel;
    private int _magic;
    private TraitType traitType;
    private boolean staticReuse;
    private int manaConsume;
    private int manaInitialConsume;
    private  int mpPerChanneling;
    private  int hpConsume;
    private  int itemConsumeCount;
    private  int itemConsumeId;
    /**
     * Fame points consumed by this skill from caster
     */
    private  int _famePointConsume;
    /**
     * Clan points consumed by this skill from caster's clan
     */
    private  int _clanRepConsume;


    /**
     * Effect range: how far the skill affect the target.
     */
    private  int effectRange;
    /**
     * Abnormal instant, used for herbs mostly.
     */
    private  boolean isAbnormalInstant;
    /**
     * Abnormal level, global effect level.
     */
    private  int abnormalLvl;
    /**
     * Abnormal type: global effect "group".
     */
    private  AbnormalType abnormalType;
    /**
     * Abnormal type: local effect "group".
     */
    private  AbnormalType subordinationAbnormalType;
    /**
     * Abnormal time: global effect duration time.
     */
    private  int abnormalTime;
    /**
     * If {@code true} this skill's effect should stay after death.
     */
    private  boolean stayAfterDeath;
    /**
     * If {@code true} this skill's effect recovery HP/MP or CP from herb.
     */
    private  boolean _isRecoveryHerb;
    private  int _refId;
    // all times in milliseconds
    private  int hitTime;
    private  double hitCancelTime;
    private  int coolTime;
    private  long _reuseHashCode;
    private  int reuseDelay;
    private  int _reuseDelayGroup;
    private  int magicLevel;
    private  int levelBonusRate;
    private  int activateRate;
    private  int _minChance;
    private  int _maxChance;
    // Effecting area of the skill, in radius.
    // The radius center varies according to the _targetType:
    // "caster" if targetType = AURA/PARTY/CLAN or "target" if targetType = AREA
    private  TargetType targetType;
    private  AffectScope affectScope;
    private  AffectObject affectObject;
    private  int affectRange;
    @Deprecated // use fanRange properties instead
    private final int[] _fanRange = new int[4]; // unk;startDegree;fanAffectRange;fanAffectAngle

    @Deprecated // Change to affect min and random
    public final int[] _affectLimit = new int[3]; // TODO: Third value is unknown... find it out!
    @Deprecated
    private final int[] _affectHeight = new int[2];
    private  NextActionType nextAction;
    private  boolean removedOnAnyActionExceptMove;
    private  boolean removedOnDamage;
    private  boolean blockedInOlympiad;
    private  AttributeType attributeType;
    private  int attributeValue;
    private  BasicProperty basicProperty;
    private  int _minPledgeClass;
    private  int soulMaxConsume;
    private  int chargeConsume;
    private  boolean isTriggeredSkill; // If true the skill will take activation buff slot instead of a normal buff slot
    private  int effectPoint;
    public final Map<SkillConditionScope, List<ISkillCondition>> _conditionLists = new EnumMap<>(SkillConditionScope.class);
    public final Map<EffectScope, List<AbstractEffect>> _effectLists = new EnumMap<>(EffectScope.class);
    private final boolean debuff;
    private  boolean isSuicideAttack;
    private  boolean canBeDispelled;
    private  boolean excludedFromCheck;
    private  boolean withoutAction;
    private  String icon;
    // Channeling data
    private  int channelingSkillId;
    private  long channelingStart;
    private  long channelingTickInterval;
    // Mentoring
    private  boolean _isMentoring;
    // Stance skill IDs
    private  int _doubleCastSkill;
    private  boolean _canDoubleCast;
    private  boolean canCastWhileDisabled;
    private  boolean isSharedWithSummon;
    private  boolean _isNecessaryToggle;
    private  boolean deleteAbnormalOnLeave;
    private  boolean irreplacableBuff; // Stays after death, on subclass change, cant be canceled.
    private  boolean blockActionUseSkill; // Blocks the use skill client action and is not showed on skill list.
    private  int _toggleGroupId;
    private  int _attachToggleGroupId;
    private  List<AttachSkillHolder> _attachSkills;
    private  Set<AbnormalType> abnormalResists;
    private  double magicCriticalRate;
    private  SkillBuffType _buffType;
    private  boolean _displayInList;
    private boolean autoUse;

    @Deprecated // Chance to instance
    private Set<AbnormalVisualEffect> abnormalVisualEffects;
    private volatile Byte[] _effectTypes;
    private int affectMin;
    private int affectRandom;
    private int fanRangeStartAngle;
    private int fanRangeRadius;
    private int fanRangeAngle;

    public Skill(StatsSet set) {
        id = set.getInt(".id");
        level = set.getInt(".level");
        _subLevel = set.getInt(".subLevel", 0);
        _refId = set.getInt(".referenceId", 0);
        displayId = set.getInt(".displayId", id);
        _displayLevel = set.getInt(".displayLevel", level);
        name = set.getString(".name", "");
        operateType = set.getEnum("operateType", SkillOperateType.class);
        _magic = set.getInt("isMagic", 0);
        traitType = set.getEnum("trait", TraitType.class, TraitType.NONE);
        staticReuse = set.getBoolean("staticReuse", false);
        manaConsume = set.getInt("mpConsume", 0);
        manaInitialConsume = set.getInt("mpInitialConsume", 0);
        mpPerChanneling = set.getInt("mpPerChanneling", manaConsume);
        hpConsume = set.getInt("hpConsume", 0);
        itemConsumeCount = set.getInt("itemConsumeCount", 0);
        itemConsumeId = set.getInt("itemConsumeId", 0);
        _famePointConsume = set.getInt("famePointConsume", 0);
        _clanRepConsume = set.getInt("clanRepConsume", 0);

        castRange = set.getInt("castRange", -1);
        effectRange = set.getInt("effectRange", -1);
        abnormalLvl = set.getInt("abnormalLvl", 0);
        abnormalType = set.getEnum("abnormalType", AbnormalType.class, AbnormalType.NONE);
        subordinationAbnormalType = set.getEnum("subordinationAbnormalType", AbnormalType.class, AbnormalType.NONE);

        int abnormalTime = set.getInt("abnormalTime", 0);
        if (Config.ENABLE_MODIFY_SKILL_DURATION && Config.SKILL_DURATION_LIST.containsKey(id)) {
            if ((level < 100) || (level > 140)) {
                abnormalTime = Config.SKILL_DURATION_LIST.get(id);
            } else if (level < 140) {
                abnormalTime += Config.SKILL_DURATION_LIST.get(id);
            }
        }

        this.abnormalTime = abnormalTime;
        isAbnormalInstant = set.getBoolean("abnormalInstant", false);
        parseAbnormalVisualEffect(set.getString("abnormalVisualEffect", null));

        stayAfterDeath = set.getBoolean("stayAfterDeath", false);

        hitTime = set.getInt("hitTime", 0);
        hitCancelTime = set.getDouble("hitCancelTime", 0);
        coolTime = set.getInt("coolTime", 0);
        debuff = set.getBoolean("isDebuff", false);
        _isRecoveryHerb = set.getBoolean("isRecoveryHerb", false);

        if (Config.ENABLE_MODIFY_SKILL_REUSE && Config.SKILL_REUSE_LIST.containsKey(id)) {
            reuseDelay = Config.SKILL_REUSE_LIST.get(id);
        } else {
            reuseDelay = set.getInt("reuseDelay", 0);
        }

        _reuseDelayGroup = set.getInt("reuseDelayGroup", -1);
        _reuseHashCode = SkillData.getSkillHashCode(_reuseDelayGroup > 0 ? _reuseDelayGroup : id, level, _subLevel);

        targetType = set.getEnum("targetType", TargetType.class, TargetType.SELF);
        affectScope = set.getEnum("affectScope", AffectScope.class, AffectScope.SINGLE);
        affectObject = set.getEnum("affectObject", AffectObject.class, AffectObject.ALL);
        affectRange = set.getInt("affectRange", 0);

        final String fanRange = set.getString("fanRange", null);
        if (fanRange != null) {
            try {
                final String[] valuesSplit = fanRange.split(";");
                _fanRange[0] = Integer.parseInt(valuesSplit[0]);
                _fanRange[1] = Integer.parseInt(valuesSplit[1]);
                _fanRange[2] = Integer.parseInt(valuesSplit[2]);
                _fanRange[3] = Integer.parseInt(valuesSplit[3]);
            } catch (Exception e) {
                throw new IllegalArgumentException("SkillId: " + id + " invalid fanRange value: " + fanRange + ", \"unk;startDegree;fanAffectRange;fanAffectAngle\" required");
            }
        }

        final String affectLimit = set.getString("affectLimit", null);
        if (affectLimit != null) {
            try {
                final String[] valuesSplit = affectLimit.split("-");
                _affectLimit[0] = Integer.parseInt(valuesSplit[0]);
                _affectLimit[1] = Integer.parseInt(valuesSplit[1]);
                if (valuesSplit.length > 2) {
                    _affectLimit[2] = Integer.parseInt(valuesSplit[2]);
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("SkillId: " + id + " invalid affectLimit value: " + affectLimit + ", \"minAffected-additionalRandom\" required");
            }
        }

        final String affectHeight = set.getString("affectHeight", null);
        if (affectHeight != null) {
            try {
                final String[] valuesSplit = affectHeight.split(";");
                _affectHeight[0] = Integer.parseInt(valuesSplit[0]);
                _affectHeight[1] = Integer.parseInt(valuesSplit[1]);
            } catch (Exception e) {
                throw new IllegalArgumentException("SkillId: " + id + " invalid affectHeight value: " + affectHeight + ", \"minHeight-maxHeight\" required");
            }

            if (_affectHeight[0] > _affectHeight[1]) {
                throw new IllegalArgumentException("SkillId: " + id + " invalid affectHeight value: " + affectHeight + ", \"minHeight-maxHeight\" required, minHeight is higher than maxHeight!");
            }
        }

        magicLevel = set.getInt("magicLvl", 0);
        levelBonusRate = set.getInt("lvlBonusRate", 0);
        activateRate = set.getInt("activateRate", -1);
        _minChance = set.getInt("minChance", Config.MIN_ABNORMAL_STATE_SUCCESS_RATE);
        _maxChance = set.getInt("maxChance", Config.MAX_ABNORMAL_STATE_SUCCESS_RATE);

        nextAction = set.getEnum("nextAction", NextActionType.class, NextActionType.NONE);

        removedOnAnyActionExceptMove = set.getBoolean("removedOnAnyActionExceptMove", false);
        removedOnDamage = set.getBoolean("removedOnDamage", false);

        blockedInOlympiad = set.getBoolean("blockedInOlympiad", false);

        attributeType = set.getEnum("attributeType", AttributeType.class, AttributeType.NONE);
        attributeValue = set.getInt("attributeValue", 0);

        basicProperty = set.getEnum("basicProperty", BasicProperty.class, BasicProperty.NONE);

        isSuicideAttack = set.getBoolean("isSuicideAttack", false);

        _minPledgeClass = set.getInt("minPledgeClass", 0);

        soulMaxConsume = set.getInt("soulMaxConsumeCount", 0);
        chargeConsume = set.getInt("chargeConsume", 0);

        isTriggeredSkill = set.getBoolean("isTriggeredSkill", false);
        effectPoint = set.getInt("effectPoint", 0);

        canBeDispelled = set.getBoolean("canBeDispelled", true);

        excludedFromCheck = set.getBoolean("excludedFromCheck", false);
        withoutAction = set.getBoolean("withoutAction", false);

        icon = set.getString("icon", "icon.skill0000");

        channelingSkillId = set.getInt("channelingSkillId", 0);
        channelingTickInterval = (long) set.getFloat("channelingTickInterval", 2000f) * 1000;
        channelingStart = (long) (set.getFloat("channelingStart", 0f) * 1000);

        _isMentoring = set.getBoolean("isMentoring", false);

        _doubleCastSkill = set.getInt("doubleCastSkill", 0);

        _canDoubleCast = set.getBoolean("canDoubleCast", false);
        canCastWhileDisabled = set.getBoolean("canCastWhileDisabled", false);
        isSharedWithSummon = set.getBoolean("isSharedWithSummon", true);

        _isNecessaryToggle = set.getBoolean("isNecessaryToggle", false);
        deleteAbnormalOnLeave = set.getBoolean("deleteAbnormalOnLeave", false);
        irreplacableBuff = set.getBoolean("irreplacableBuff", false);
        blockActionUseSkill = set.getBoolean("blockActionUseSkill", false);

        _toggleGroupId = set.getInt("toggleGroupId", -1);
        _attachToggleGroupId = set.getInt("attachToggleGroupId", -1);
        _attachSkills = set.getList("attachSkillList", StatsSet.class, Collections.emptyList()).stream().map(AttachSkillHolder::fromStatsSet).collect(Collectors.toList());

        final String abnormalResist = set.getString("abnormalResists", null);
        if (abnormalResist != null) {
            final String[] abnormalResistStrings = abnormalResist.split(";");
            if (abnormalResistStrings.length > 0) {
                abnormalResists = new HashSet<>(abnormalResistStrings.length);
                for (String s : abnormalResistStrings) {
                    try {
                        abnormalResists.add(AbnormalType.valueOf(s));
                    } catch (Exception e) {
                        LOGGER.warn("Skill ID[" + id + "] Expected AbnormalType for abnormalResists but found " + s, e);
                    }
                }
            } else {
                abnormalResists = Collections.emptySet();
            }
        } else {
            abnormalResists = Collections.emptySet();
        }

        magicCriticalRate = set.getDouble("magicCriticalRate", 0);
        _buffType = isTriggeredSkill ? SkillBuffType.TRIGGER : isToggle() ? SkillBuffType.TOGGLE : isDance() ? SkillBuffType.DANCE : debuff ? SkillBuffType.DEBUFF : !isHealingPotionSkill() ? SkillBuffType.BUFF : SkillBuffType.NONE;
        _displayInList = set.getBoolean("displayInList", true);
    }

    public Skill(int id, String name, boolean debuff, SkillOperateType action, SkillType type) {
        this.id = id;
        this.level = 1;
        this.name = name;
        this.debuff = debuff;
        operateType = action;
        this.type = type;
    }

    public TraitType getTraitType() {
        return traitType;
    }

    public AttributeType getAttributeType() {
        return attributeType;
    }

    public int getAttributeValue() {
        return attributeValue;
    }

    public boolean isAOE() {
        switch (affectScope) {
            case FAN:
            case FAN_PB:
            case POINT_BLANK:
            case RANGE:
            case RING_RANGE:
            case SQUARE:
            case SQUARE_PB: {
                return true;
            }
        }
        return false;
    }

    public boolean isSuicideAttack() {
        return isSuicideAttack;
    }

    public boolean allowOnTransform() {
        return isPassive();
    }

    /**
     * Verify if this skill is abnormal instant.<br>
     * Herb buff skills yield {@code true} for this check.
     *
     * @return {@code true} if the skill is abnormal instant, {@code false} otherwise
     */
    public boolean isAbnormalInstant() {
        return isAbnormalInstant;
    }

    /**
     * Gets the skill abnormal type.
     *
     * @return the abnormal type
     */
    public AbnormalType getAbnormalType() {
        return abnormalType;
    }

    /**
     * Gets the skill subordination abnormal type.
     *
     * @return the abnormal type
     */
    public AbnormalType getSubordinationAbnormalType() {
        return subordinationAbnormalType;
    }

    /**
     * Gets the skill abnormal level.
     *
     * @return the skill abnormal level
     */
    public int getAbnormalLvl() {
        return abnormalLvl;
    }

    /**
     * Gets the skill abnormal time.<br>
     * Is the base to calculate the duration of the continuous effects of this skill.
     *
     * @return the abnormal time
     */
    public int getAbnormalTime() {
        return abnormalTime;
    }

    /**
     * Gets the skill abnormal visual effect.
     *
     * @return the abnormal visual effect
     */
    public Set<AbnormalVisualEffect> getAbnormalVisualEffects() {
        return (abnormalVisualEffects != null) ? abnormalVisualEffects : Collections.emptySet();
    }

    /**
     * Verify if the skill has abnormal visual effects.
     *
     * @return {@code true} if the skill has abnormal visual effects, {@code false} otherwise
     */
    public boolean hasAbnormalVisualEffects() {
        return (abnormalVisualEffects != null) && !abnormalVisualEffects.isEmpty();
    }

    /**
     * Gets the skill magic level.
     *
     * @return the skill magic level
     */
    public int getMagicLevel() {
        return magicLevel;
    }

    public int getLvlBonusRate() {
        return levelBonusRate;
    }

    public int getActivateRate() {
        return activateRate;
    }

    /**
     * Return custom minimum skill/effect chance.
     *
     * @return
     */
    public int getMinChance() {
        return _minChance;
    }

    /**
     * Return custom maximum skill/effect chance.
     *
     * @return
     */
    public int getMaxChance() {
        return _maxChance;
    }

    /**
     * Return true if skill effects should be removed on any action except movement
     *
     * @return
     */
    public boolean isRemovedOnAnyActionExceptMove() {
        return removedOnAnyActionExceptMove;
    }

    /**
     * @return {@code true} if skill effects should be removed on damage
     */
    public boolean isRemovedOnDamage() {
        return removedOnDamage;
    }

    /**
     * @return {@code true} if skill can not be used in olympiad.
     */
    public boolean isBlockedInOlympiad() {
        return blockedInOlympiad;
    }

    /**
     * Return the additional effect Id.
     *
     * @return
     */
    public int getChannelingSkillId() {
        return channelingSkillId;
    }

    /**
     * Return character action after cast
     *
     * @return
     */
    public NextActionType getNextAction() {
        return nextAction;
    }

    /**
     * @return Returns the castRange.
     */
    public int getCastRange() {
        return castRange;
    }

    /**
     * @return Returns the effectRange.
     */
    public int getEffectRange() {
        return effectRange;
    }

    /**
     * @return Returns the hpConsume.
     */
    public int getHpConsume() {
        return hpConsume;
    }

    /**
     * Gets the skill ID.
     *
     * @return the skill ID
     */
    @Override
    public int getId() {
        return id;
    }

    /**
     * Verify if this skill is a debuff.
     *
     * @return {@code true} if this skill is a debuff, {@code false} otherwise
     */
    public boolean isDebuff() {
        return debuff;
    }

    /**
     * Verify if this skill is coming from Recovery Herb.
     *
     * @return {@code true} if this skill is a recover herb, {@code false} otherwise
     */
    public boolean isRecoveryHerb() {
        return _isRecoveryHerb;
    }

    public int getDisplayId() {
        return displayId;
    }

    public int getDisplayLevel() {
        return _displayLevel;
    }

    /**
     * Return skill basic property type.
     *
     * @return
     */
    public BasicProperty getBasicProperty() {
        return basicProperty;
    }

    /**
     * @return Returns the how much item will be consumed.
     */
    public int getItemConsumeCount() {
        return itemConsumeCount;
    }

    /**
     * @return Returns the ID of item for consume.
     */
    public int getItemConsumeId() {
        return itemConsumeId;
    }

    /**
     * @return Fame points consumed by this skill from caster
     */
    public int getFamePointConsume() {
        return _famePointConsume;
    }

    /**
     * @return Clan points consumed by this skill from caster's clan
     */
    public int getClanRepConsume() {
        return _clanRepConsume;
    }

    /**
     * @return Returns the level.
     */
    public int getLevel() {
        return level;
    }

    /**
     * @return Returns the sub level.
     */
    public int getSubLevel() {
        return _subLevel;
    }

    /**
     * @return isMagic integer value from the XML.
     */
    public int getMagicType() {
        return _magic;
    }

    /**
     * @return Returns true to set physical skills.
     */
    public boolean isPhysical() {
        return _magic == 0;
    }

    /**
     * @return Returns true to set magic skills.
     */
    public boolean isMagic() {
        return _magic == 1;
    }

    /**
     * @return Returns true to set static skills.
     */
    public boolean isStatic() {
        return _magic == 2;
    }

    /**
     * @return Returns true to set dance skills.
     */
    public boolean isDance() {
        return _magic == 3;
    }

    /**
     * @return Returns true to set static reuse.
     */
    public boolean isStaticReuse() {
        return staticReuse;
    }

    /**
     * @return Returns the mpConsume.
     */
    public int getMpConsume() {
        return manaConsume;
    }

    /**
     * @return Returns the mpInitialConsume.
     */
    public int getMpInitialConsume() {
        return manaInitialConsume;
    }

    /**
     * @return Mana consumption per channeling tick.
     */
    public int getMpPerChanneling() {
        return mpPerChanneling;
    }

    /**
     * @return the skill name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the reuse delay
     */
    public int getReuseDelay() {
        return reuseDelay;
    }

    /**
     * @return the skill ID from which the reuse delay should be taken.
     */
    public int getReuseDelayGroup() {
        return _reuseDelayGroup;
    }

    public long getReuseHashCode() {
        return _reuseHashCode;
    }

    public int getHitTime() {
        return hitTime;
    }

    public double getHitCancelTime() {
        return hitCancelTime;
    }

    /**
     * @return the cool time
     */
    public int getCoolTime() {
        return coolTime;
    }

    /**
     * @return the target type of the skill : SELF, TARGET, SUMMON, GROUND...
     */
    public TargetType getTargetType() {
        return targetType;
    }

    /**
     * @return the affect scope of the skill : SINGLE, FAN, SQUARE, PARTY, PLEDGE...
     */
    public AffectScope getAffectScope() {
        return affectScope;
    }

    /**
     * @return the affect object of the skill : All, Clan, Friend, NotFriend, Invisible...
     */
    public AffectObject getAffectObject() {
        return affectObject;
    }

    /**
     * @return the AOE range of the skill.
     */
    public int getAffectRange() {
        return affectRange;
    }

    /**
     * @return the AOE fan range of the skill.
     */
    public int[] getFanRange() {
        return _fanRange;
    }

    /**
     * @return the maximum amount of targets the skill can affect or 0 if unlimited.
     */
    public int getAffectLimit() {
        if ((_affectLimit[0] > 0) || (_affectLimit[1] > 0)) {
            return (_affectLimit[0] + Rnd.get(_affectLimit[1]));
        }

        return 0;
    }

    public boolean isActive() {
        return operateType.isActive();
    }

    public boolean isPassive() {
        return operateType.isPassive();
    }

    public boolean isToggle() {
        return operateType.isToggle();
    }

    public boolean isAura() {
        return operateType.isAura();
    }

    public boolean isHidingMessages() {
        return operateType.isHidingMessages();
    }

    public boolean isNotBroadcastable() {
        return operateType.isNotBroadcastable();
    }

    public boolean isContinuous() {
        return operateType.isContinuous() || isSelfContinuous();
    }

    public boolean isFlyType() {
        return operateType.isFlyType();
    }

    public boolean isSelfContinuous() {
        return operateType.isSelfContinuous();
    }

    public boolean isChanneling() {
        return operateType.isChanneling();
    }

    public boolean isTriggeredSkill() {
        return isTriggeredSkill;
    }

    public boolean isSynergySkill() {
        return operateType.isSynergy();
    }

    public SkillOperateType getOperateType() {
        return operateType;
    }

    /**
     * Verify if the skill is a transformation skill.
     *
     * @return {@code true} if the skill is a transformation, {@code false} otherwise
     */
    public boolean isTransformation() {
        return abnormalType == AbnormalType.TRANSFORM;
    }

    public int getEffectPoint() {
        return effectPoint;
    }

    public boolean useSoulShot() {
        return hasEffectType(EffectType.PHYSICAL_ATTACK, EffectType.PHYSICAL_ATTACK_HP_LINK);
    }

    public boolean useSpiritShot() {
        return _magic == 1;
    }

    public boolean useFishShot() {
        return hasEffectType(EffectType.FISHING);
    }

    public int getMinPledgeClass() {
        return _minPledgeClass;
    }

    public boolean isHeroSkill() {
        return SkillTreesData.getInstance().isHeroSkill(id, level);
    }

    public boolean isGMSkill() {
        return SkillTreesData.getInstance().isGMSkill(id, level);
    }

    public boolean is7Signs() {
        return (id > 4360) && (id < 4367);
    }

    /**
     * Verify if this is a healing potion skill.
     *
     * @return {@code true} if this is a healing potion skill, {@code false} otherwise
     */
    public boolean isHealingPotionSkill() {
        return abnormalType == AbnormalType.HP_RECOVER;
    }

    public int getMaxSoulConsumeCount() {
        return soulMaxConsume;
    }

    public int getChargeConsumeCount() {
        return chargeConsume;
    }

    public boolean isStayAfterDeath() {
        return stayAfterDeath || irreplacableBuff || _isNecessaryToggle;
    }

    public boolean isBad() {
        return effectPoint < 0;
    }

    public boolean checkCondition(Creature activeChar, WorldObject object) {
        if ((activeChar.canOverrideCond(PcCondOverride.SKILL_CONDITIONS) && !Config.GM_SKILL_RESTRICTION)) {
            return true;
        }

        if (isPlayer(activeChar) && activeChar.getActingPlayer().isMounted() && isBad() && !MountEnabledSkillList.contains(id)) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS);
            sm.addSkillName(id);
            activeChar.sendPacket(sm);
            return false;
        }

        if (!checkConditions(SkillConditionScope.GENERAL, activeChar, object) || !checkConditions(SkillConditionScope.TARGET, activeChar, object)) {
            if (!((activeChar == object) && isBad())) // Self targeted bad skills should not send a message.
            {
                final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS);
                sm.addSkillName(id);
                activeChar.sendPacket(sm);
            }
            return false;
        }

        return true;
    }

    /**
     * @param activeChar  the character that requests getting the skill target.
     * @param forceUse    if character pressed ctrl (force pick target)
     * @param dontMove    if character pressed shift (dont move and pick target only if in range)
     * @param sendMessage send SystemMessageId packet if target is incorrect.
     * @return {@code WorldObject} this skill can be used on, or {@code null} if there is no such.
     */
    public WorldObject getTarget(Creature activeChar, boolean forceUse, boolean dontMove, boolean sendMessage) {
        return getTarget(activeChar, activeChar.getTarget(), forceUse, dontMove, sendMessage);
    }

    /**
     * @param activeChar    the character that requests getting the skill target.
     * @param seletedTarget the target that has been selected by this character to be checked.
     * @param forceUse      if character pressed ctrl (force pick target)
     * @param dontMove      if character pressed shift (dont move and pick target only if in range)
     * @param sendMessage   send SystemMessageId packet if target is incorrect.
     * @return the selected {@code WorldObject} this skill can be used on, or {@code null} if there is no such.
     */
    public WorldObject getTarget(Creature activeChar, WorldObject seletedTarget, boolean forceUse, boolean dontMove, boolean sendMessage) {
        final ITargetTypeHandler handler = TargetHandler.getInstance().getHandler(getTargetType());
        if (handler != null) {
            try {
                return handler.getTarget(activeChar, seletedTarget, this, forceUse, dontMove, sendMessage);
            } catch (Exception e) {
                LOGGER.warn("Exception in Skill.getTarget(): " + e.getMessage(), e);
            }
        }
        activeChar.sendMessage("Target type of skill " + this + " is not currently handled.");
        return null;
    }

    /**
     * @param activeChar the character that needs to gather targets.
     * @param target     the initial target activeChar is focusing upon.
     * @return list containing objects gathered in a specific geometric way that are valid to be affected by this skill.
     */
    public List<WorldObject> getTargetsAffected(Creature activeChar, WorldObject target) {
        if (target == null) {
            return null;
        }
        final IAffectScopeHandler handler = AffectScopeHandler.getInstance().getHandler(getAffectScope());
        if (handler != null) {
            try {
                final List<WorldObject> result = new LinkedList<>();
                handler.forEachAffected(activeChar, target, this, result::add);
                return result;
            } catch (Exception e) {
                LOGGER.warn("Exception in Skill.getTargetsAffected(): " + e.getMessage(), e);
            }
        }
        activeChar.sendMessage("Target affect scope of skill " + this + " is not currently handled.");
        return null;
    }

    /**
     * @param activeChar the character that needs to gather targets.
     * @param target     the initial target activeChar is focusing upon.
     * @param action     for each affected target.
     */
    public void forEachTargetAffected(Creature activeChar, WorldObject target, Consumer<? super WorldObject> action) {
        if (target == null) {
            return;
        }

        final IAffectScopeHandler handler = AffectScopeHandler.getInstance().getHandler(getAffectScope());
        if (handler != null) {
            try {
                handler.forEachAffected(activeChar, target, this, action);
            } catch (Exception e) {
                LOGGER.warn("Exception in Skill.forEachTargetAffected(): " + e.getMessage(), e);
            }
        } else {
            activeChar.sendMessage("Target affect scope of skill " + this + " is not currently handled.");
        }
    }

    /**
     * Adds an effect to the effect list for the given effect scope.
     *
     * @param effectScope the effect scope
     * @param effect      the effect
     */
    public void addEffect(EffectScope effectScope, AbstractEffect effect) {
        _effectLists.computeIfAbsent(effectScope, k -> new ArrayList<>()).add(effect);
    }

    /**
     * Gets the skill effects.
     *
     * @param effectScope the effect scope
     * @return the list of effects for the give scope
     */
    public List<AbstractEffect> getEffects(EffectScope effectScope) {
        return _effectLists.get(effectScope);
    }

    /**
     * Verify if this skill has effects for the given scope.
     *
     * @param effectScope the effect scope
     * @return {@code true} if this skill has effects for the given scope, {@code false} otherwise
     */
    public boolean hasEffects(EffectScope effectScope) {
        final List<AbstractEffect> effects = _effectLists.get(effectScope);
        return (effects != null) && !effects.isEmpty();
    }

    /**
     * Applies the effects from this skill to the target for the given effect scope.
     *
     * @param effectScope          the effect scope
     * @param info                 the buff info
     * @param applyInstantEffects  if {@code true} instant effects will be applied to the effected
     * @param addContinuousEffects if {@code true} continuous effects will be applied to the effected
     */
    public void applyEffectScope(EffectScope effectScope, BuffInfo info, boolean applyInstantEffects, boolean addContinuousEffects) {
        if ((effectScope != null) && hasEffects(effectScope)) {
            for (AbstractEffect effect : getEffects(effectScope)) {
                if (effect.isInstant()) {
                    if (applyInstantEffects && effect.calcSuccess(info.getEffector(), info.getEffected(), info.getSkill())) {
                        effect.instant(info.getEffector(), info.getEffected(), info.getSkill(), info.getItem());
                    }
                } else if (addContinuousEffects) {
                    if (applyInstantEffects) {
                        effect.continuousInstant(info.getEffector(), info.getEffected(), info.getSkill(), info.getItem());
                    }

                    if (effect.canStart(info.getEffector(), info.getEffected(), info.getSkill())) {
                        info.addEffect(effect);
                    }

                    // tempfix for hp/mp regeneration
                    // TODO: Find where regen stops and make a proper fix
                    if (isPlayer(info.getEffected()) && !info.getSkill().isBad()) {
                        info.getEffected().getActingPlayer().getStatus().startHpMpRegeneration();
                    }
                }
            }
        }
    }

    /**
     * Method overload for {@link Skill#applyEffects(Creature, Creature, boolean, boolean, boolean, int, Item)}.<br>
     * Simplify the calls.
     *
     * @param effector the caster of the skill
     * @param effected the target of the effect
     */
    public void applyEffects(Creature effector, Creature effected) {
        applyEffects(effector, effected, false, false, true, 0, null);
    }

    /**
     * Method overload for {@link Skill#applyEffects(Creature, Creature, boolean, boolean, boolean, int, Item)}.<br>
     * Simplify the calls.
     *
     * @param effector the caster of the skill
     * @param effected the target of the effect
     * @param item
     */
    public void applyEffects(Creature effector, Creature effected, Item item) {
        applyEffects(effector, effected, false, false, true, 0, item);
    }

    /**
     * Method overload for {@link Skill#applyEffects(Creature, Creature, boolean, boolean, boolean, int, Item)}.<br>
     * Simplify the calls, allowing abnormal time time customization.
     *
     * @param effector     the caster of the skill
     * @param effected     the target of the effect
     * @param instant      if {@code true} instant effects will be applied to the effected
     * @param abnormalTime custom abnormal time, if equal or lesser than zero will be ignored
     */
    public void applyEffects(Creature effector, Creature effected, boolean instant, int abnormalTime) {
        applyEffects(effector, effected, false, false, instant, abnormalTime, null);
    }

    /**
     * Applies the effects from this skill to the target.
     *
     * @param effector     the caster of the skill
     * @param effected     the target of the effect
     * @param self         if {@code true} self-effects will be casted on the caster
     * @param passive      if {@code true} passive effects will be applied to the effector
     * @param instant      if {@code true} instant effects will be applied to the effected
     * @param abnormalTime custom abnormal time, if equal or lesser than zero will be ignored
     * @param item
     */
    public void applyEffects(Creature effector, Creature effected, boolean self, boolean passive, boolean instant, int abnormalTime, Item item) {
        // null targets cannot receive any effects.
        if (effected == null) {
            return;
        }

        if (effected.isIgnoringSkillEffects(id, level)) {
            return;
        }

        boolean addContinuousEffects = !passive && (operateType.isToggle() || (operateType.isContinuous() && Formulas.calcEffectSuccess(effector, effected, this)));
        if (!self && !passive) {
            final BuffInfo info = new BuffInfo(effector, effected, this, !instant, item, null);
            if (addContinuousEffects && (abnormalTime > 0)) {
                info.setAbnormalTime(abnormalTime);
            }

            applyEffectScope(EffectScope.GENERAL, info, instant, addContinuousEffects);

            final EffectScope pvpOrPveEffectScope = isPlayable(effector) && isAttackable(effected) ? EffectScope.PVE : isPlayable(effector) && isPlayable(effected) ? EffectScope.PVP : null;
            applyEffectScope(pvpOrPveEffectScope, info, instant, addContinuousEffects);

            if (addContinuousEffects) {
                // Aura skills reset the abnormal time.
                final BuffInfo existingInfo = operateType.isAura() ? effected.getEffectList().getBuffInfoBySkillId(id) : null;
                if (existingInfo != null) {
                    existingInfo.resetAbnormalTime(info.getAbnormalTime());
                } else {
                    effected.getEffectList().add(info);
                }

                // Check for mesmerizing debuffs and increase resist level.
                if (debuff && (basicProperty != BasicProperty.NONE) && effected.hasBasicPropertyResist()) {
                    final BasicPropertyResist resist = effected.getBasicPropertyResist(basicProperty);
                    resist.increaseResistLevel();
                }
            }

            // Support for buff sharing feature including healing herbs.
            if (isSharedWithSummon && isPlayer(effected) && effected.hasServitors() && !isTransformation()) {
                if ((addContinuousEffects && isContinuous() && !debuff) || _isRecoveryHerb) {
                    effected.getServitors().values().forEach(s -> applyEffects(effector, s, _isRecoveryHerb, 0));
                }
            }
        }

        if (self) {
            addContinuousEffects = !passive && (operateType.isToggle() || (operateType.isSelfContinuous() && Formulas.calcEffectSuccess(effector, effector, this)));

            final BuffInfo info = new BuffInfo(effector, effector, this, !instant, item, null);
            if (addContinuousEffects && (abnormalTime > 0)) {
                info.setAbnormalTime(abnormalTime);
            }

            applyEffectScope(EffectScope.SELF, info, instant, addContinuousEffects);

            if (addContinuousEffects) {
                // Aura skills reset the abnormal time.
                final BuffInfo existingInfo = operateType.isAura() ? effector.getEffectList().getBuffInfoBySkillId(id) : null;
                if (existingInfo != null) {
                    existingInfo.resetAbnormalTime(info.getAbnormalTime());
                } else {
                    info.getEffector().getEffectList().add(info);
                }
            }

            // Support for buff sharing feature.
            // Avoiding Servitor Share since it's implementation already "shares" the effect.
            if (addContinuousEffects && isSharedWithSummon && isPlayer(info.getEffected()) && isContinuous() && !debuff && info.getEffected().hasServitors()) {
                info.getEffected().getServitors().values().forEach(s -> applyEffects(effector, s, false, 0));
            }
        }

        if (passive && checkConditions(SkillConditionScope.PASSIVE, effector, effector)) {
            final BuffInfo info = new BuffInfo(effector, effector, this, true, item, null);
            applyEffectScope(EffectScope.GENERAL, info, false, true);
            effector.getEffectList().add(info);
        }
    }

    /**
     * Applies the channeling effects from this skill to the target.
     *
     * @param effector the caster of the skill
     * @param effected the target of the effect
     */
    public void applyChannelingEffects(Creature effector, Creature effected) {
        // null targets cannot receive any effects.
        if (effected == null) {
            return;
        }

        final BuffInfo info = new BuffInfo(effector, effected, this, false, null, null);
        applyEffectScope(EffectScope.CHANNELING, info, true, true);
    }

    /**
     * Activates a skill for the given creature and targets.
     *
     * @param caster  the caster
     * @param targets the targets
     */
    public void activateSkill(Creature caster, WorldObject... targets) {
        activateSkill(caster, null, targets);
    }

    /**
     * Activates a skill for the given creature and targets.
     *
     * @param caster  the caster
     * @param item
     * @param targets the targets
     */
    public void activateSkill(Creature caster, Item item, WorldObject... targets) {
        activateSkill(caster, null, item, targets);
    }

    /**
     * Activates a skill for the given cubic and targets.
     *
     * @param cubic   the cubic
     * @param targets the targets
     */
    public void activateSkill(CubicInstance cubic, WorldObject... targets) {
        activateSkill(cubic.getOwner(), cubic, null, targets);
    }

    /**
     * Activates the skill to the targets.
     *
     * @param caster  the caster
     * @param cubic   the cubic that cast the skill, can be {@code null}
     * @param item
     * @param targets the targets
     */
    public final void activateSkill(Creature caster, CubicInstance cubic, Item item, WorldObject... targets) {
        for (WorldObject targetObject : targets) {
            if (!isCreature(targetObject)) {
                continue;
            }

            final Creature target = (Creature) targetObject;
            if (Formulas.calcBuffDebuffReflection(target, this)) {
                // if skill is reflected instant effects should be casted on target
                // and continuous effects on caster
                applyEffects(target, caster, false, 0);

                final BuffInfo info = new BuffInfo(caster, target, this, false, item, null);
                applyEffectScope(EffectScope.GENERAL, info, true, false);

                final EffectScope pvpOrPveEffectScope = isPlayable(caster) && isAttackable(target) ? EffectScope.PVE : isPlayable(caster)  && isPlayable(target) ? EffectScope.PVP : null;
                applyEffectScope(pvpOrPveEffectScope, info, true, false);
            } else {
                applyEffects(caster, target, item);
            }
        }

        // Self Effect
        if (hasEffects(EffectScope.SELF)) {
            if (caster.isAffectedBySkill(id)) {
                caster.stopSkillEffects(true, id);
            }
            applyEffects(caster, caster, true, false, true, 0, item);
        }

        if (cubic == null) {
            if (useSpiritShot()) {
                caster.unchargeShot(caster.isChargedShot(ShotType.BLESSED_SPIRITSHOTS) ? ShotType.BLESSED_SPIRITSHOTS : ShotType.SPIRITSHOTS);
            } else if (useSoulShot()) {
                caster.unchargeShot(caster.isChargedShot(ShotType.BLESSED_SOULSHOTS) ? ShotType.BLESSED_SOULSHOTS : ShotType.SOULSHOTS);
            }
        }

        if (isSuicideAttack) {
            caster.doDie(caster);
        }
    }

    /**
     * Adds a condition to the condition list for the given condition scope.
     *
     * @param skillConditionScope the condition scope
     * @param skillCondition      the condition
     */
    public void addCondition(SkillConditionScope skillConditionScope, ISkillCondition skillCondition) {
        _conditionLists.computeIfAbsent(skillConditionScope, k -> new ArrayList<>()).add(skillCondition);
    }

    /**
     * Checks the conditions of this skills for the given condition scope.
     *
     * @param skillConditionScope the condition scope
     * @param caster              the caster
     * @param target              the target
     * @return {@code false} if at least one condition returns false, {@code true} otherwise
     */
    public boolean checkConditions(SkillConditionScope skillConditionScope, Creature caster, WorldObject target) {
        return _conditionLists.getOrDefault(skillConditionScope, Collections.emptyList()).stream().allMatch(c -> c.canUse(caster, this, target));
    }

    @Override
    public String toString() {
        return "Skill " + name + "(" + id + "," + level + "," + _subLevel + ")";
    }

    /**
     * used for tracking item id in case that item consume cannot be used
     *
     * @return reference item id
     */
    public int getReferenceItemId() {
        return _refId;
    }

    public boolean canBeDispelled() {
        return canBeDispelled;
    }

    /**
     * Verify if the skill can be stolen.
     *
     * @return {@code true} if skill can be stolen, {@code false} otherwise
     */
    public boolean canBeStolen() {
        return !isPassive() && !isToggle() && !debuff && !irreplacableBuff && !isHeroSkill() && !isGMSkill() && !(isStatic() && (getId() != CommonSkill.CARAVANS_SECRET_MEDICINE.getId())) && canBeDispelled;
    }

    public boolean isClanSkill() {
        return SkillTreesData.getInstance().isClanSkill(id, level);
    }

    public boolean isExcludedFromCheck() {
        return excludedFromCheck;
    }

    public boolean isWithoutAction() {
        return withoutAction;
    }

    /**
     * Parses all the abnormal visual effects.
     *
     * @param abnormalVisualEffects the abnormal visual effects list
     */
    private void parseAbnormalVisualEffect(String abnormalVisualEffects) {
        if (abnormalVisualEffects != null) {
            final String[] data = abnormalVisualEffects.split(";");
            final Set<AbnormalVisualEffect> aves = new HashSet<>(1);
            for (String aveString : data) {
                final AbnormalVisualEffect ave = AbnormalVisualEffect.findByName(aveString);
                if (ave != null) {
                    aves.add(ave);
                } else {
                    LOGGER.warn("Invalid AbnormalVisualEffect(" + this + ") found for Skill(" + aveString + ")");
                }
            }

            if (!aves.isEmpty()) {
                this.abnormalVisualEffects = aves;
            }
        }
    }

    /**
     * @param effectType  Effect type to check if its present on this skill effects.
     * @param effectTypes Effect types to check if are present on this skill effects.
     * @return {@code true} if at least one of specified {@link EffectType} types is present on this skill effects, {@code false} otherwise.
     */
    public boolean hasEffectType(EffectType effectType, EffectType... effectTypes) {
        if (_effectTypes == null) {
            synchronized (this) {
                if (_effectTypes == null) {
                    final Set<Byte> effectTypesSet = new HashSet<>();
                    for (List<AbstractEffect> effectList : _effectLists.values()) {
                        if (effectList != null) {
                            for (AbstractEffect effect : effectList) {
                                effectTypesSet.add((byte) effect.getEffectType().ordinal());
                            }
                        }
                    }

                    final Byte[] effectTypesArray = effectTypesSet.toArray(new Byte[effectTypesSet.size()]);
                    Arrays.sort(effectTypesArray);
                    _effectTypes = effectTypesArray;
                }
            }
        }

        if (Arrays.binarySearch(_effectTypes, (byte) effectType.ordinal()) >= 0) {
            return true;
        }

        for (EffectType type : effectTypes) {
            if (Arrays.binarySearch(_effectTypes, (byte) type.ordinal()) >= 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param effectScope Effect Scope to look inside for the specific effect type.
     * @param effectType  Effect type to check if its present on this skill effects.
     * @param effectTypes Effect types to check if are present on this skill effects.
     * @return {@code true} if at least one of specified {@link EffectType} types is present on this skill effects, {@code false} otherwise.
     */
    public boolean hasEffectType(EffectScope effectScope, EffectType effectType, EffectType... effectTypes) {
        if (hasEffects(effectScope)) {
            return false;
        }

        for (AbstractEffect effect : _effectLists.get(effectScope)) {
            if (effectType == effect.getEffectType()) {
                return true;
            }

            for (EffectType type : effectTypes) {
                if (type == effect.getEffectType()) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * @return icon of the current skill.
     */
    public String getIcon() {
        return icon;
    }

    public long getChannelingTickInterval() {
        return channelingTickInterval;
    }

    public long getChannelingTickInitialDelay() {
        return channelingStart;
    }

    public boolean isMentoring() {
        return _isMentoring;
    }

    /**
     * @param activeChar
     * @return alternative skill that has been attached due to the effect of toggle skills on the player (e.g Fire Stance, Water Stance).
     */
    public Skill getAttachedSkill(Creature activeChar) {
        // If character is double casting, return double cast skill.
        if ((_doubleCastSkill > 0) && activeChar.isAffected(EffectFlag.DOUBLE_CAST)) {
            return SkillData.getInstance().getSkill(getDoubleCastSkill(), getLevel(), getSubLevel());
        }

        // Default toggle group ID, assume nothing attached.
        if ((_attachToggleGroupId <= 0) || (_attachSkills == null)) {
            return null;
        }

        //@formatter:off
        final int toggleSkillId = activeChar.getEffectList().getEffects().stream()
                .filter(info -> info.getSkill().getToggleGroupId() == _attachToggleGroupId)
                .mapToInt(info -> info.getSkill().getId())
                .findAny().orElse(0);
        //@formatter:on

        // No active toggles with this toggle group ID found.
        if (toggleSkillId == 0) {
            return null;
        }

        final AttachSkillHolder attachedSkill = _attachSkills.stream().filter(ash -> ash.getRequiredSkillId() == toggleSkillId).findAny().orElse(null);

        // No attached skills for this toggle found.
        if (attachedSkill == null) {
            return null;
        }

        return SkillData.getInstance().getSkill(attachedSkill.getSkillId(), getLevel(), getSubLevel());
    }

    public boolean canDoubleCast() {
        return _canDoubleCast;
    }

    public int getDoubleCastSkill() {
        return _doubleCastSkill;
    }

    public boolean canCastWhileDisabled() {
        return canCastWhileDisabled;
    }

    public boolean isSharedWithSummon() {
        return isSharedWithSummon;
    }

    public boolean isNecessaryToggle() {
        return _isNecessaryToggle;
    }

    public boolean isDeleteAbnormalOnLeave() {
        return deleteAbnormalOnLeave;
    }

    /**
     * @return {@code true} if the buff cannot be replaced, canceled, removed on death, etc.<br>
     * It can be only overriden by higher stack, but buff still remains ticking and activates once the higher stack buff has passed away.
     */
    public boolean isIrreplacableBuff() {
        return irreplacableBuff;
    }

    public boolean isDisplayInList() {
        return _displayInList;
    }

    /**
     * @return if skill could not be requested for use by players.
     */
    public boolean isBlockActionUseSkill() {
        return blockActionUseSkill;
    }

    public int getToggleGroupId() {
        return _toggleGroupId;
    }

    public int getAttachToggleGroupId() {
        return _attachToggleGroupId;
    }

    public List<AttachSkillHolder> getAttachSkills() {
        return _attachSkills;
    }

    public Set<AbnormalType> getAbnormalResists() {
        return abnormalResists;
    }

    public double getMagicCriticalRate() {
        return magicCriticalRate;
    }

    public SkillBuffType getBuffType() {
        return _buffType;
    }

    public boolean isEnchantable() {
        return EnchantSkillGroupsData.getInstance().isEnchantable(this);
    }

    protected void setIcon(String icon) {
        this.icon = icon;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setTrait(TraitType trait) {
        this.traitType = trait;
    }

    public void setNextAction(NextActionType action) {
        nextAction = action;
    }

    public void setProperty(BasicProperty property) {
        basicProperty = property;
    }

    public void setStaticReuse(boolean staticReuse) {
        this.staticReuse = staticReuse;
    }

    public void setMagicCriticalRate(double rate) {
        this.magicCriticalRate = rate;
    }

    public void setStayAfterDeath(boolean stayAfterDeath) {
        this.stayAfterDeath = stayAfterDeath;
    }

    public void setDisplayId(int id) {
        displayId = id;
    }

    public void setHitCancelTime(double time) {
        hitCancelTime = time;
    }

    public void setLevelBonusRate(int rate) {
        levelBonusRate = rate;
    }

    @Override
    protected Skill clone() throws CloneNotSupportedException {
        return (Skill) super.clone();
    }

    public void setRemoveOnAction(boolean removeOnAction) {
        removedOnAnyActionExceptMove = removeOnAction;
    }

    public void setRemoveOnDamage(boolean removeOnDamage) {
        removedOnDamage = removeOnDamage;
    }

    public void setBlockedOnOlympiad(boolean blockedOnOlympiad) {
        this.blockedInOlympiad = blockedOnOlympiad;
    }

    public void setSuicide(boolean suicide) {
        this.isSuicideAttack = suicide;
    }

    public void setTriggered(boolean triggered) {
        this.isTriggeredSkill = triggered;
    }

    public void setDispellable(boolean dispellable) {
        this.canBeDispelled = dispellable;
    }

    public void setCheck(boolean check) {
        excludedFromCheck = !check;
    }

    public void setWithoutAction(boolean withoutAction) {
        this.withoutAction = withoutAction;
    }

    public void setCanCastDisabled(boolean castDisabled) {
        this.canCastWhileDisabled = castDisabled;
    }

    public void setSummonShared(boolean summonShared) {
        this.isSharedWithSummon = summonShared;
    }

    public void setRemoveAbnormalOnLeave(boolean remove) {
        deleteAbnormalOnLeave = remove;
    }

    public void setIrreplacable(boolean irreplacable) {
        irreplacableBuff = irreplacable;
    }

    public void setBlockActionSkill(boolean block) {
        this.blockActionUseSkill = block;
    }

    public void setAutoUse(boolean autoUse) {
        this.autoUse = autoUse;
    }

    public void setSoulConsume(int souls) {
        soulMaxConsume = souls;
    }

    public void setChargeConsume(int charges) {
        chargeConsume = charges;
    }

    public void setTargetType(TargetType type) {
        targetType = type;
    }

    public void setAffectScope(AffectScope scope) {
        affectScope = scope;
    }

    public void setAffectObject(AffectObject object) {
        affectObject = object;
    }

    public void setAffectRange(int range) {
        affectRange = range;
    }

    public void setAffectMin(int affectMin) {
        this.affectMin = affectMin;
    }

    public void setAffectRandom(int affectRandom) {
        this.affectRandom = affectRandom;
    }

    public void setAbnormalType(AbnormalType type) {
        abnormalType = type;
    }

    public void setAbnormalVisual(AbnormalVisualEffect visual) {
        abnormalVisualEffects = Set.of(visual);
    }

    public void setAbnormalSubordination(AbnormalType subordination) {
        subordinationAbnormalType = subordination;
    }

    public void setAbnormalInstant(boolean instant) {
        this.isAbnormalInstant = instant;
    }

    public void setResistAbnormals(Set<AbnormalType> abnormals) {
        this.abnormalResists = abnormals;
    }

    public void setChannelingSkill(int skill) {
        this.channelingSkillId = skill;
    }

    public void setChannelingMpConsume(int mpConsume) {
        mpPerChanneling = mpConsume;
    }

    public void setChannelingInitialDelay(long delay) {
        channelingStart = delay;
    }

    public void setChannelingInterval(long interval) {
        channelingTickInterval = interval;
    }

    public void setMagicLevel(int level) {
        magicLevel = level;
    }

    public void setCastRange(int range) {
        castRange = range;
    }

    public void setReuse(int reuse) {
        reuseDelay = reuse;
    }

    public void setCoolTime(int time) {
        coolTime = time;
    }

    public void setEffectPoint(int effectPoints) {
        effectPoint = effectPoints;
    }

    public void setEffectRange(int effectRange) {
        this.effectRange = effectRange;
    }

    public void setHitTime(int time) {
        hitTime = time;
    }

    public void setActivateRate(int rate) {
        activateRate = rate;
    }

    public void setAttributeType(AttributeType type) {
        attributeType = type;
    }

    public void setAttributeValue(int value) {
        attributeValue = value;
    }

    public void setManaInitConsume(int consume) {
        manaInitialConsume = consume;
    }

    public void setManaConsume(int consume) {
        manaConsume = consume;
    }

    public void setHpConsume(int consume) {
        hpConsume = consume;
    }

    public void setItemConsume(int item) {
        itemConsumeId = item;
    }

    public void setItemConsumeCount(int count) {
        itemConsumeCount = count;
    }

    public void setFanRangeStartAngle(int angle) {
        this.fanRangeStartAngle = angle;
    }

    public void setFanRangeRadius(int radius) {
        this.fanRangeRadius = radius;
    }

    public void setFanRangeAngle(int angle) {
        this.fanRangeAngle = angle;
    }

    public void setAbnormalLevel(int level) {
        abnormalLvl = level;
    }

    public void setAbnormalTime(int time) {
        abnormalTime = time;
    }

    public void setAbnormalChance(int chance) {
        activateRate = chance;
    }
}
