package org.l2j.gameserver.model.skills;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.xml.impl.EnchantSkillGroupsData;
import org.l2j.gameserver.data.xml.impl.SkillData;
import org.l2j.gameserver.data.xml.impl.SkillTreesData;
import org.l2j.gameserver.enums.*;
import org.l2j.gameserver.handler.AffectScopeHandler;
import org.l2j.gameserver.handler.IAffectScopeHandler;
import org.l2j.gameserver.handler.ITargetTypeHandler;
import org.l2j.gameserver.handler.TargetHandler;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.PcCondOverride;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.cubic.CubicInstance;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.holders.AttachSkillHolder;
import org.l2j.gameserver.model.interfaces.IIdentifiable;
import org.l2j.gameserver.model.items.instance.Item;
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

public final class Skill implements IIdentifiable {
    private static final Logger LOGGER = LoggerFactory.getLogger(Skill.class);

    /**
     * Skill ID.
     */
    private final int _id;
    /**
     * Skill level.
     */
    private final int _level;
    /**
     * Skill sub level.
     */
    private final int _subLevel;
    /**
     * Custom skill ID displayed by the client.
     */
    private final int _displayId;
    /**
     * Custom skill level displayed by the client.
     */
    private final int _displayLevel;
    /**
     * Skill client's name.
     */
    private final String _name;
    /**
     * Operative type: passive, active, toggle.
     */
    private final SkillOperateType _operateType;
    private final int _magic;
    private final TraitType _traitType;
    private final boolean _staticReuse;
    /**
     * MP consumption.
     */
    private final int _mpConsume;
    /**
     * Initial MP consumption.
     */
    private final int _mpInitialConsume;
    /**
     * MP consumption per channeling.
     */
    private final int _mpPerChanneling;
    /**
     * HP consumption.
     */
    private final int _hpConsume;
    /**
     * Amount of items consumed by this skill from caster.
     */
    private final int _itemConsumeCount;
    /**
     * Id of item consumed by this skill from caster.
     */
    private final int _itemConsumeId;
    /**
     * Fame points consumed by this skill from caster
     */
    private final int _famePointConsume;
    /**
     * Clan points consumed by this skill from caster's clan
     */
    private final int _clanRepConsume;
    /**
     * Cast range: how far can be the target.
     */
    private final int _castRange;
    /**
     * Effect range: how far the skill affect the target.
     */
    private final int _effectRange;
    /**
     * Abnormal instant, used for herbs mostly.
     */
    private final boolean _isAbnormalInstant;
    /**
     * Abnormal level, global effect level.
     */
    private final int _abnormalLvl;
    /**
     * Abnormal type: global effect "group".
     */
    private final AbnormalType _abnormalType;
    /**
     * Abnormal type: local effect "group".
     */
    private final AbnormalType _subordinationAbnormalType;
    /**
     * Abnormal time: global effect duration time.
     */
    private final int _abnormalTime;
    /**
     * If {@code true} this skill's effect should stay after death.
     */
    private final boolean _stayAfterDeath;
    /**
     * If {@code true} this skill's effect recovery HP/MP or CP from herb.
     */
    private final boolean _isRecoveryHerb;
    private final int _refId;
    // all times in milliseconds
    private final int _hitTime;
    private final double _hitCancelTime;
    private final int _coolTime;
    private final long _reuseHashCode;
    private final int _reuseDelay;
    private final int _reuseDelayGroup;
    private final int _magicLevel;
    private final int _lvlBonusRate;
    private final int _activateRate;
    private final int _minChance;
    private final int _maxChance;
    // Effecting area of the skill, in radius.
    // The radius center varies according to the _targetType:
    // "caster" if targetType = AURA/PARTY/CLAN or "target" if targetType = AREA
    private final TargetType _targetType;
    private final AffectScope _affectScope;
    private final AffectObject _affectObject;
    private final int _affectRange;
    private final int[] _fanRange = new int[4]; // unk;startDegree;fanAffectRange;fanAffectAngle
    public final int[] _affectLimit = new int[3]; // TODO: Third value is unknown... find it out!
    @Deprecated
    private final int[] _affectHeight = new int[2];
    private final NextActionType _nextAction;
    private final boolean _removedOnAnyActionExceptMove;
    private final boolean _removedOnDamage;
    private final boolean _blockedInOlympiad;
    private final AttributeType _attributeType;
    private final int _attributeValue;
    private final BasicProperty _basicProperty;
    private final int _minPledgeClass;
    private final int _soulMaxConsume;
    private final int _chargeConsume;
    private final boolean _isTriggeredSkill; // If true the skill will take activation buff slot instead of a normal buff slot
    private final int _effectPoint;
    public final Map<SkillConditionScope, List<ISkillCondition>> _conditionLists = new EnumMap<>(SkillConditionScope.class);
    public final Map<EffectScope, List<AbstractEffect>> _effectLists = new EnumMap<>(EffectScope.class);
    private final boolean _isDebuff;
    private final boolean _isSuicideAttack;
    private final boolean _canBeDispelled;
    private final boolean _excludedFromCheck;
    private final boolean _withoutAction;
    private final String _icon;
    // Channeling data
    private final int _channelingSkillId;
    private final long _channelingStart;
    private final long _channelingTickInterval;
    // Mentoring
    private final boolean _isMentoring;
    // Stance skill IDs
    private final int _doubleCastSkill;
    private final boolean _canDoubleCast;
    private final boolean _canCastWhileDisabled;
    private final boolean _isSharedWithSummon;
    private final boolean _isNecessaryToggle;
    private final boolean _deleteAbnormalOnLeave;
    private final boolean _irreplacableBuff; // Stays after death, on subclass change, cant be canceled.
    private final boolean _blockActionUseSkill; // Blocks the use skill client action and is not showed on skill list.
    private final int _toggleGroupId;
    private final int _attachToggleGroupId;
    private final List<AttachSkillHolder> _attachSkills;
    private final Set<AbnormalType> _abnormalResists;
    private final double _magicCriticalRate;
    private final SkillBuffType _buffType;
    private final boolean _displayInList;
    /**
     * Abnormal visual effect: the visual effect displayed ingame.
     */
    private Set<AbnormalVisualEffect> _abnormalVisualEffects;
    private volatile Byte[] _effectTypes;

    public Skill(StatsSet set) {
        _id = set.getInt(".id");
        _level = set.getInt(".level");
        _subLevel = set.getInt(".subLevel", 0);
        _refId = set.getInt(".referenceId", 0);
        _displayId = set.getInt(".displayId", _id);
        _displayLevel = set.getInt(".displayLevel", _level);
        _name = set.getString(".name", "");
        _operateType = set.getEnum("operateType", SkillOperateType.class);
        _magic = set.getInt("isMagic", 0);
        _traitType = set.getEnum("trait", TraitType.class, TraitType.NONE);
        _staticReuse = set.getBoolean("staticReuse", false);
        _mpConsume = set.getInt("mpConsume", 0);
        _mpInitialConsume = set.getInt("mpInitialConsume", 0);
        _mpPerChanneling = set.getInt("mpPerChanneling", _mpConsume);
        _hpConsume = set.getInt("hpConsume", 0);
        _itemConsumeCount = set.getInt("itemConsumeCount", 0);
        _itemConsumeId = set.getInt("itemConsumeId", 0);
        _famePointConsume = set.getInt("famePointConsume", 0);
        _clanRepConsume = set.getInt("clanRepConsume", 0);

        _castRange = set.getInt("castRange", -1);
        _effectRange = set.getInt("effectRange", -1);
        _abnormalLvl = set.getInt("abnormalLvl", 0);
        _abnormalType = set.getEnum("abnormalType", AbnormalType.class, AbnormalType.NONE);
        _subordinationAbnormalType = set.getEnum("subordinationAbnormalType", AbnormalType.class, AbnormalType.NONE);

        int abnormalTime = set.getInt("abnormalTime", 0);
        if (Config.ENABLE_MODIFY_SKILL_DURATION && Config.SKILL_DURATION_LIST.containsKey(_id)) {
            if ((_level < 100) || (_level > 140)) {
                abnormalTime = Config.SKILL_DURATION_LIST.get(_id);
            } else if (_level < 140) {
                abnormalTime += Config.SKILL_DURATION_LIST.get(_id);
            }
        }

        _abnormalTime = abnormalTime;
        _isAbnormalInstant = set.getBoolean("abnormalInstant", false);
        parseAbnormalVisualEffect(set.getString("abnormalVisualEffect", null));

        _stayAfterDeath = set.getBoolean("stayAfterDeath", false);

        _hitTime = set.getInt("hitTime", 0);
        _hitCancelTime = set.getDouble("hitCancelTime", 0);
        _coolTime = set.getInt("coolTime", 0);
        _isDebuff = set.getBoolean("isDebuff", false);
        _isRecoveryHerb = set.getBoolean("isRecoveryHerb", false);

        if (Config.ENABLE_MODIFY_SKILL_REUSE && Config.SKILL_REUSE_LIST.containsKey(_id)) {
            _reuseDelay = Config.SKILL_REUSE_LIST.get(_id);
        } else {
            _reuseDelay = set.getInt("reuseDelay", 0);
        }

        _reuseDelayGroup = set.getInt("reuseDelayGroup", -1);
        _reuseHashCode = SkillData.getSkillHashCode(_reuseDelayGroup > 0 ? _reuseDelayGroup : _id, _level, _subLevel);

        _targetType = set.getEnum("targetType", TargetType.class, TargetType.SELF);
        _affectScope = set.getEnum("affectScope", AffectScope.class, AffectScope.SINGLE);
        _affectObject = set.getEnum("affectObject", AffectObject.class, AffectObject.ALL);
        _affectRange = set.getInt("affectRange", 0);

        final String fanRange = set.getString("fanRange", null);
        if (fanRange != null) {
            try {
                final String[] valuesSplit = fanRange.split(";");
                _fanRange[0] = Integer.parseInt(valuesSplit[0]);
                _fanRange[1] = Integer.parseInt(valuesSplit[1]);
                _fanRange[2] = Integer.parseInt(valuesSplit[2]);
                _fanRange[3] = Integer.parseInt(valuesSplit[3]);
            } catch (Exception e) {
                throw new IllegalArgumentException("SkillId: " + _id + " invalid fanRange value: " + fanRange + ", \"unk;startDegree;fanAffectRange;fanAffectAngle\" required");
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
                throw new IllegalArgumentException("SkillId: " + _id + " invalid affectLimit value: " + affectLimit + ", \"minAffected-additionalRandom\" required");
            }
        }

        final String affectHeight = set.getString("affectHeight", null);
        if (affectHeight != null) {
            try {
                final String[] valuesSplit = affectHeight.split(";");
                _affectHeight[0] = Integer.parseInt(valuesSplit[0]);
                _affectHeight[1] = Integer.parseInt(valuesSplit[1]);
            } catch (Exception e) {
                throw new IllegalArgumentException("SkillId: " + _id + " invalid affectHeight value: " + affectHeight + ", \"minHeight-maxHeight\" required");
            }

            if (_affectHeight[0] > _affectHeight[1]) {
                throw new IllegalArgumentException("SkillId: " + _id + " invalid affectHeight value: " + affectHeight + ", \"minHeight-maxHeight\" required, minHeight is higher than maxHeight!");
            }
        }

        _magicLevel = set.getInt("magicLvl", 0);
        _lvlBonusRate = set.getInt("lvlBonusRate", 0);
        _activateRate = set.getInt("activateRate", -1);
        _minChance = set.getInt("minChance", Config.MIN_ABNORMAL_STATE_SUCCESS_RATE);
        _maxChance = set.getInt("maxChance", Config.MAX_ABNORMAL_STATE_SUCCESS_RATE);

        _nextAction = set.getEnum("nextAction", NextActionType.class, NextActionType.NONE);

        _removedOnAnyActionExceptMove = set.getBoolean("removedOnAnyActionExceptMove", false);
        _removedOnDamage = set.getBoolean("removedOnDamage", false);

        _blockedInOlympiad = set.getBoolean("blockedInOlympiad", false);

        _attributeType = set.getEnum("attributeType", AttributeType.class, AttributeType.NONE);
        _attributeValue = set.getInt("attributeValue", 0);

        _basicProperty = set.getEnum("basicProperty", BasicProperty.class, BasicProperty.NONE);

        _isSuicideAttack = set.getBoolean("isSuicideAttack", false);

        _minPledgeClass = set.getInt("minPledgeClass", 0);

        _soulMaxConsume = set.getInt("soulMaxConsumeCount", 0);
        _chargeConsume = set.getInt("chargeConsume", 0);

        _isTriggeredSkill = set.getBoolean("isTriggeredSkill", false);
        _effectPoint = set.getInt("effectPoint", 0);

        _canBeDispelled = set.getBoolean("canBeDispelled", true);

        _excludedFromCheck = set.getBoolean("excludedFromCheck", false);
        _withoutAction = set.getBoolean("withoutAction", false);

        _icon = set.getString("icon", "icon.skill0000");

        _channelingSkillId = set.getInt("channelingSkillId", 0);
        _channelingTickInterval = (long) set.getFloat("channelingTickInterval", 2000f) * 1000;
        _channelingStart = (long) (set.getFloat("channelingStart", 0f) * 1000);

        _isMentoring = set.getBoolean("isMentoring", false);

        _doubleCastSkill = set.getInt("doubleCastSkill", 0);

        _canDoubleCast = set.getBoolean("canDoubleCast", false);
        _canCastWhileDisabled = set.getBoolean("canCastWhileDisabled", false);
        _isSharedWithSummon = set.getBoolean("isSharedWithSummon", true);

        _isNecessaryToggle = set.getBoolean("isNecessaryToggle", false);
        _deleteAbnormalOnLeave = set.getBoolean("deleteAbnormalOnLeave", false);
        _irreplacableBuff = set.getBoolean("irreplacableBuff", false);
        _blockActionUseSkill = set.getBoolean("blockActionUseSkill", false);

        _toggleGroupId = set.getInt("toggleGroupId", -1);
        _attachToggleGroupId = set.getInt("attachToggleGroupId", -1);
        _attachSkills = set.getList("attachSkillList", StatsSet.class, Collections.emptyList()).stream().map(AttachSkillHolder::fromStatsSet).collect(Collectors.toList());

        final String abnormalResist = set.getString("abnormalResists", null);
        if (abnormalResist != null) {
            final String[] abnormalResistStrings = abnormalResist.split(";");
            if (abnormalResistStrings.length > 0) {
                _abnormalResists = new HashSet<>(abnormalResistStrings.length);
                for (String s : abnormalResistStrings) {
                    try {
                        _abnormalResists.add(AbnormalType.valueOf(s));
                    } catch (Exception e) {
                        LOGGER.warn("Skill ID[" + _id + "] Expected AbnormalType for abnormalResists but found " + s, e);
                    }
                }
            } else {
                _abnormalResists = Collections.emptySet();
            }
        } else {
            _abnormalResists = Collections.emptySet();
        }

        _magicCriticalRate = set.getDouble("magicCriticalRate", 0);
        _buffType = _isTriggeredSkill ? SkillBuffType.TRIGGER : isToggle() ? SkillBuffType.TOGGLE : isDance() ? SkillBuffType.DANCE : _isDebuff ? SkillBuffType.DEBUFF : !isHealingPotionSkill() ? SkillBuffType.BUFF : SkillBuffType.NONE;
        _displayInList = set.getBoolean("displayInList", true);
    }

    public TraitType getTraitType() {
        return _traitType;
    }

    public AttributeType getAttributeType() {
        return _attributeType;
    }

    public int getAttributeValue() {
        return _attributeValue;
    }

    public boolean isAOE() {
        switch (_affectScope) {
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
        return _isSuicideAttack;
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
        return _isAbnormalInstant;
    }

    /**
     * Gets the skill abnormal type.
     *
     * @return the abnormal type
     */
    public AbnormalType getAbnormalType() {
        return _abnormalType;
    }

    /**
     * Gets the skill subordination abnormal type.
     *
     * @return the abnormal type
     */
    public AbnormalType getSubordinationAbnormalType() {
        return _subordinationAbnormalType;
    }

    /**
     * Gets the skill abnormal level.
     *
     * @return the skill abnormal level
     */
    public int getAbnormalLvl() {
        return _abnormalLvl;
    }

    /**
     * Gets the skill abnormal time.<br>
     * Is the base to calculate the duration of the continuous effects of this skill.
     *
     * @return the abnormal time
     */
    public int getAbnormalTime() {
        return _abnormalTime;
    }

    /**
     * Gets the skill abnormal visual effect.
     *
     * @return the abnormal visual effect
     */
    public Set<AbnormalVisualEffect> getAbnormalVisualEffects() {
        return (_abnormalVisualEffects != null) ? _abnormalVisualEffects : Collections.emptySet();
    }

    /**
     * Verify if the skill has abnormal visual effects.
     *
     * @return {@code true} if the skill has abnormal visual effects, {@code false} otherwise
     */
    public boolean hasAbnormalVisualEffects() {
        return (_abnormalVisualEffects != null) && !_abnormalVisualEffects.isEmpty();
    }

    /**
     * Gets the skill magic level.
     *
     * @return the skill magic level
     */
    public int getMagicLevel() {
        return _magicLevel;
    }

    public int getLvlBonusRate() {
        return _lvlBonusRate;
    }

    public int getActivateRate() {
        return _activateRate;
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
        return _removedOnAnyActionExceptMove;
    }

    /**
     * @return {@code true} if skill effects should be removed on damage
     */
    public boolean isRemovedOnDamage() {
        return _removedOnDamage;
    }

    /**
     * @return {@code true} if skill can not be used in olympiad.
     */
    public boolean isBlockedInOlympiad() {
        return _blockedInOlympiad;
    }

    /**
     * Return the additional effect Id.
     *
     * @return
     */
    public int getChannelingSkillId() {
        return _channelingSkillId;
    }

    /**
     * Return character action after cast
     *
     * @return
     */
    public NextActionType getNextAction() {
        return _nextAction;
    }

    /**
     * @return Returns the castRange.
     */
    public int getCastRange() {
        return _castRange;
    }

    /**
     * @return Returns the effectRange.
     */
    public int getEffectRange() {
        return _effectRange;
    }

    /**
     * @return Returns the hpConsume.
     */
    public int getHpConsume() {
        return _hpConsume;
    }

    /**
     * Gets the skill ID.
     *
     * @return the skill ID
     */
    @Override
    public int getId() {
        return _id;
    }

    /**
     * Verify if this skill is a debuff.
     *
     * @return {@code true} if this skill is a debuff, {@code false} otherwise
     */
    public boolean isDebuff() {
        return _isDebuff;
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
        return _displayId;
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
        return _basicProperty;
    }

    /**
     * @return Returns the how much item will be consumed.
     */
    public int getItemConsumeCount() {
        return _itemConsumeCount;
    }

    /**
     * @return Returns the ID of item for consume.
     */
    public int getItemConsumeId() {
        return _itemConsumeId;
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
        return _level;
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
        return _staticReuse;
    }

    /**
     * @return Returns the mpConsume.
     */
    public int getMpConsume() {
        return _mpConsume;
    }

    /**
     * @return Returns the mpInitialConsume.
     */
    public int getMpInitialConsume() {
        return _mpInitialConsume;
    }

    /**
     * @return Mana consumption per channeling tick.
     */
    public int getMpPerChanneling() {
        return _mpPerChanneling;
    }

    /**
     * @return the skill name
     */
    public String getName() {
        return _name;
    }

    /**
     * @return the reuse delay
     */
    public int getReuseDelay() {
        return _reuseDelay;
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
        return _hitTime;
    }

    public double getHitCancelTime() {
        return _hitCancelTime;
    }

    /**
     * @return the cool time
     */
    public int getCoolTime() {
        return _coolTime;
    }

    /**
     * @return the target type of the skill : SELF, TARGET, SUMMON, GROUND...
     */
    public TargetType getTargetType() {
        return _targetType;
    }

    /**
     * @return the affect scope of the skill : SINGLE, FAN, SQUARE, PARTY, PLEDGE...
     */
    public AffectScope getAffectScope() {
        return _affectScope;
    }

    /**
     * @return the affect object of the skill : All, Clan, Friend, NotFriend, Invisible...
     */
    public AffectObject getAffectObject() {
        return _affectObject;
    }

    /**
     * @return the AOE range of the skill.
     */
    public int getAffectRange() {
        return _affectRange;
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
        return _operateType.isActive();
    }

    public boolean isPassive() {
        return _operateType.isPassive();
    }

    public boolean isToggle() {
        return _operateType.isToggle();
    }

    public boolean isAura() {
        return _operateType.isAura();
    }

    public boolean isHidingMessages() {
        return _operateType.isHidingMessages();
    }

    public boolean isNotBroadcastable() {
        return _operateType.isNotBroadcastable();
    }

    public boolean isContinuous() {
        return _operateType.isContinuous() || isSelfContinuous();
    }

    public boolean isFlyType() {
        return _operateType.isFlyType();
    }

    public boolean isSelfContinuous() {
        return _operateType.isSelfContinuous();
    }

    public boolean isChanneling() {
        return _operateType.isChanneling();
    }

    public boolean isTriggeredSkill() {
        return _isTriggeredSkill;
    }

    public boolean isSynergySkill() {
        return _operateType.isSynergy();
    }

    public SkillOperateType getOperateType() {
        return _operateType;
    }

    /**
     * Verify if the skill is a transformation skill.
     *
     * @return {@code true} if the skill is a transformation, {@code false} otherwise
     */
    public boolean isTransformation() {
        return _abnormalType == AbnormalType.TRANSFORM;
    }

    public int getEffectPoint() {
        return _effectPoint;
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
        return SkillTreesData.getInstance().isHeroSkill(_id, _level);
    }

    public boolean isGMSkill() {
        return SkillTreesData.getInstance().isGMSkill(_id, _level);
    }

    public boolean is7Signs() {
        return (_id > 4360) && (_id < 4367);
    }

    /**
     * Verify if this is a healing potion skill.
     *
     * @return {@code true} if this is a healing potion skill, {@code false} otherwise
     */
    public boolean isHealingPotionSkill() {
        return _abnormalType == AbnormalType.HP_RECOVER;
    }

    public int getMaxSoulConsumeCount() {
        return _soulMaxConsume;
    }

    public int getChargeConsumeCount() {
        return _chargeConsume;
    }

    public boolean isStayAfterDeath() {
        return _stayAfterDeath || _irreplacableBuff || _isNecessaryToggle;
    }

    public boolean isBad() {
        return _effectPoint < 0;
    }

    public boolean checkCondition(Creature activeChar, WorldObject object) {
        if ((activeChar.canOverrideCond(PcCondOverride.SKILL_CONDITIONS) && !Config.GM_SKILL_RESTRICTION)) {
            return true;
        }

        if (isPlayer(activeChar) && activeChar.getActingPlayer().isMounted() && isBad() && !MountEnabledSkillList.contains(_id)) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS);
            sm.addSkillName(_id);
            activeChar.sendPacket(sm);
            return false;
        }

        if (!checkConditions(SkillConditionScope.GENERAL, activeChar, object) || !checkConditions(SkillConditionScope.TARGET, activeChar, object)) {
            if (!((activeChar == object) && isBad())) // Self targeted bad skills should not send a message.
            {
                final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS);
                sm.addSkillName(_id);
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

        if (effected.isIgnoringSkillEffects(_id, _level)) {
            return;
        }

        boolean addContinuousEffects = !passive && (_operateType.isToggle() || (_operateType.isContinuous() && Formulas.calcEffectSuccess(effector, effected, this)));
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
                final BuffInfo existingInfo = _operateType.isAura() ? effected.getEffectList().getBuffInfoBySkillId(_id) : null;
                if (existingInfo != null) {
                    existingInfo.resetAbnormalTime(info.getAbnormalTime());
                } else {
                    effected.getEffectList().add(info);
                }

                // Check for mesmerizing debuffs and increase resist level.
                if (_isDebuff && (_basicProperty != BasicProperty.NONE) && effected.hasBasicPropertyResist()) {
                    final BasicPropertyResist resist = effected.getBasicPropertyResist(_basicProperty);
                    resist.increaseResistLevel();
                }
            }

            // Support for buff sharing feature including healing herbs.
            if (_isSharedWithSummon && isPlayer(effected) && effected.hasServitors() && !isTransformation()) {
                if ((addContinuousEffects && isContinuous() && !_isDebuff) || _isRecoveryHerb) {
                    effected.getServitors().values().forEach(s -> applyEffects(effector, s, _isRecoveryHerb, 0));
                }
            }
        }

        if (self) {
            addContinuousEffects = !passive && (_operateType.isToggle() || (_operateType.isSelfContinuous() && Formulas.calcEffectSuccess(effector, effector, this)));

            final BuffInfo info = new BuffInfo(effector, effector, this, !instant, item, null);
            if (addContinuousEffects && (abnormalTime > 0)) {
                info.setAbnormalTime(abnormalTime);
            }

            applyEffectScope(EffectScope.SELF, info, instant, addContinuousEffects);

            if (addContinuousEffects) {
                // Aura skills reset the abnormal time.
                final BuffInfo existingInfo = _operateType.isAura() ? effector.getEffectList().getBuffInfoBySkillId(_id) : null;
                if (existingInfo != null) {
                    existingInfo.resetAbnormalTime(info.getAbnormalTime());
                } else {
                    info.getEffector().getEffectList().add(info);
                }
            }

            // Support for buff sharing feature.
            // Avoiding Servitor Share since it's implementation already "shares" the effect.
            if (addContinuousEffects && _isSharedWithSummon && isPlayer(info.getEffected()) && isContinuous() && !_isDebuff && info.getEffected().hasServitors()) {
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
            if (caster.isAffectedBySkill(_id)) {
                caster.stopSkillEffects(true, _id);
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

        if (_isSuicideAttack) {
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
        return "Skill " + _name + "(" + _id + "," + _level + "," + _subLevel + ")";
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
        return _canBeDispelled;
    }

    /**
     * Verify if the skill can be stolen.
     *
     * @return {@code true} if skill can be stolen, {@code false} otherwise
     */
    public boolean canBeStolen() {
        return !isPassive() && !isToggle() && !_isDebuff && !_irreplacableBuff && !isHeroSkill() && !isGMSkill() && !(isStatic() && (getId() != CommonSkill.CARAVANS_SECRET_MEDICINE.getId())) && _canBeDispelled;
    }

    public boolean isClanSkill() {
        return SkillTreesData.getInstance().isClanSkill(_id, _level);
    }

    public boolean isExcludedFromCheck() {
        return _excludedFromCheck;
    }

    public boolean isWithoutAction() {
        return _withoutAction;
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
                _abnormalVisualEffects = aves;
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
        return _icon;
    }

    public long getChannelingTickInterval() {
        return _channelingTickInterval;
    }

    public long getChannelingTickInitialDelay() {
        return _channelingStart;
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
        return _canCastWhileDisabled;
    }

    public boolean isSharedWithSummon() {
        return _isSharedWithSummon;
    }

    public boolean isNecessaryToggle() {
        return _isNecessaryToggle;
    }

    public boolean isDeleteAbnormalOnLeave() {
        return _deleteAbnormalOnLeave;
    }

    /**
     * @return {@code true} if the buff cannot be replaced, canceled, removed on death, etc.<br>
     * It can be only overriden by higher stack, but buff still remains ticking and activates once the higher stack buff has passed away.
     */
    public boolean isIrreplacableBuff() {
        return _irreplacableBuff;
    }

    public boolean isDisplayInList() {
        return _displayInList;
    }

    /**
     * @return if skill could not be requested for use by players.
     */
    public boolean isBlockActionUseSkill() {
        return _blockActionUseSkill;
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
        return _abnormalResists;
    }

    public double getMagicCriticalRate() {
        return _magicCriticalRate;
    }

    public SkillBuffType getBuffType() {
        return _buffType;
    }

    public boolean isEnchantable() {
        return EnchantSkillGroupsData.getInstance().isEnchantable(this);
    }
}
