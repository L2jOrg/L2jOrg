package org.l2j.gameserver.engine.skill.api;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.xml.impl.SkillTreesData;
import org.l2j.gameserver.engine.skill.SkillAutoUseType;
import org.l2j.gameserver.enums.AttributeType;
import org.l2j.gameserver.enums.BasicProperty;
import org.l2j.gameserver.enums.NextActionType;
import org.l2j.gameserver.enums.ShotType;
import org.l2j.gameserver.handler.AffectScopeHandler;
import org.l2j.gameserver.handler.IAffectScopeHandler;
import org.l2j.gameserver.handler.ITargetTypeHandler;
import org.l2j.gameserver.handler.TargetHandler;
import org.l2j.gameserver.model.PcCondOverride;
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

import static org.l2j.gameserver.util.GameUtils.*;

/**
 * @author JoeAlisson
 */
public final class Skill implements IIdentifiable, Cloneable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Skill.class);

    private final Map<SkillConditionScope, List<SkillCondition>> conditions = new EnumMap<>(SkillConditionScope.class);
    private final int id;
    private final String name;
    private final SkillOperateType operateType;
    private final SkillType type;
    private final boolean debuff;
    private final int maxLevel;

    public Map<EffectScope, List<AbstractEffect>> effects = new EnumMap<>(EffectScope.class);
    private TraitType traitType = TraitType.NONE;
    private AbnormalType abnormalType = AbnormalType.NONE;
    private AbnormalType subordinationAbnormalType = AbnormalType.NONE;
    private int level;
    private int castRange;
    private int displayId;
    private boolean staticReuse;
    private int manaConsume;
    private int manaInitialConsume;
    private int mpPerChanneling;
    private int hpConsume;
    private int itemConsumeCount;
    private int itemConsumeId;
    private int effectRange;
    private boolean isAbnormalInstant;
    private int abnormalLvl;
    private int abnormalTime;
    private boolean stayAfterDeath;
    private int hitTime;
    private double hitCancelTime;
    private int coolTime;
    private long reuseHashCode;
    private int reuseDelay;
    private int reuseDelayGroup = -1;
    private int magicLevel;
    private int levelBonusRate;
    private int activateRate;
    private int minChance;
    private int maxChance;
    // Effecting area of the skill, in radius.
    // The radius center varies according to the _targetType:
    // "caster" if targetType = AURA/PARTY/CLAN or "target" if targetType = AREA
    private TargetType targetType;
    private AffectScope affectScope;
    private AffectObject affectObject;
    private int affectRange;
    private NextActionType nextAction = NextActionType.NONE;
    private boolean removedOnAnyActionExceptMove;
    private boolean removedOnDamage;
    private boolean blockedInOlympiad;
    private AttributeType attributeType = AttributeType.NONE;
    private int attributeValue;
    private BasicProperty basicProperty;
    private int _minPledgeClass;
    private int soulMaxConsume;
    private int chargeConsume;
    private boolean isTriggeredSkill; // If true the skill will take activation buff slot instead of a normal buff slot
    private int effectPoint;
    private boolean isSuicideAttack;
    private boolean canBeDispelled;
    private boolean excludedFromCheck;
    private boolean withoutAction;
    private String icon;
    // Channeling data
    private int channelingSkillId;
    private long channelingStart;
    private long channelingTickInterval;
    // Mentoring
    private boolean _isMentoring;
    // Stance skill IDs
    private int _doubleCastSkill;
    private boolean _canDoubleCast;
    private boolean canCastWhileDisabled;
    private boolean isSharedWithSummon;
    private boolean _isNecessaryToggle;
    private boolean deleteAbnormalOnLeave;
    private boolean irreplacableBuff; // Stays after death, on subclass change, cant be canceled.
    private boolean blockActionUseSkill; // Blocks the use skill client action and is not showed on skill list.
    private int _toggleGroupId;
    private int _attachToggleGroupId;
    private List<AttachSkillHolder> _attachSkills = Collections.emptyList();
    private Set<AbnormalType> abnormalResists;
    private double magicCriticalRate;
    private SkillBuffType buffType;
    private boolean _displayInList;
    private SkillAutoUseType autoUse;

    @Deprecated // Chance to instance
    private Set<AbnormalVisualEffect> abnormalVisualEffects;
    private volatile Byte[] _effectTypes;
    private int affectMin;
    private int affectRandom;
    private int fanStartAngle;
    private int fanRadius;
    private int fanAngle;

    Skill(int id, String name, int maxLevel, boolean debuff, SkillOperateType action, SkillType type) {
        this.id = id;
        this.level = 1;
        this.name = name;
        this.maxLevel = maxLevel;
        this.debuff = debuff;
        operateType = action;
        this.type = type;
    }

    void computeSkillAttributes() {
        buffType = isTriggeredSkill ? SkillBuffType.TRIGGER : isToggle() ? SkillBuffType.TOGGLE : isDance() ? SkillBuffType.DANCE : debuff ? SkillBuffType.DEBUFF : !isHealingPotionSkill() ? SkillBuffType.BUFF : SkillBuffType.NONE;

        if (Config.ENABLE_MODIFY_SKILL_REUSE && Config.SKILL_REUSE_LIST.containsKey(id)) {
            reuseDelay = Config.SKILL_REUSE_LIST.get(id);
        }

        if (Config.ENABLE_MODIFY_SKILL_DURATION && Config.SKILL_DURATION_LIST.containsKey(id)) {
            if ((level < 100) || (level > 140)) {
                abnormalTime = Config.SKILL_DURATION_LIST.get(id);
            } else if (level < 140) {
                abnormalTime += Config.SKILL_DURATION_LIST.get(id);
            }
        }

        reuseHashCode = SkillEngine.skillHashCode(reuseDelayGroup > 0 ? reuseDelayGroup : id, level);

        minChance = Config.MIN_ABNORMAL_STATE_SUCCESS_RATE;
        maxChance = Config.MAX_ABNORMAL_STATE_SUCCESS_RATE;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public TraitType getTraitType() {
        return traitType;
    }

    public AttributeType getAttributeType() {
        return attributeType;
    }

    public void setAttributeType(AttributeType type) {
        attributeType = type;
    }

    public int getAttributeValue() {
        return attributeValue;
    }

    public void setAttributeValue(int value) {
        attributeValue = value;
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

    public void setAbnormalInstant(boolean instant) {
        this.isAbnormalInstant = instant;
    }

    /**
     * Gets the skill abnormal type.
     *
     * @return the abnormal type
     */
    public AbnormalType getAbnormalType() {
        return abnormalType;
    }

    public void setAbnormalType(AbnormalType type) {
        abnormalType = type;
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

    public void setAbnormalTime(int time) {
        abnormalTime = time;
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

    public void setMagicLevel(int level) {
        magicLevel = level;
    }

    public int getLvlBonusRate() {
        return levelBonusRate;
    }

    public int getActivateRate() {
        return activateRate;
    }

    public void setActivateRate(int rate) {
        activateRate = rate;
    }

    /**
     * Return custom minimum skill/effect chance.
     *
     * @return
     */
    public int getMinChance() {
        return minChance;
    }

    /**
     * Return custom maximum skill/effect chance.
     *
     * @return
     */
    public int getMaxChance() {
        return maxChance;
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

    void setNextAction(NextActionType action) {
        nextAction = action;
    }

    /**
     * @return Returns the castRange.
     */
    public int getCastRange() {
        return castRange;
    }

    public void setCastRange(int range) {
        castRange = range;
    }

    /**
     * @return Returns the effectRange.
     */
    public int getEffectRange() {
        return effectRange;
    }

    public void setEffectRange(int effectRange) {
        this.effectRange = effectRange;
    }

    /**
     * @return Returns the hpConsume.
     */
    public int getHpConsume() {
        return hpConsume;
    }

    public void setHpConsume(int consume) {
        hpConsume = consume;
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

    public int getDisplayId() {
        return displayId;
    }

    void setDisplayId(int id) {
        displayId = id;
    }

    public int getDisplayLevel() {
        return level;
    }

    /**
     * Return skill basic property type.
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

    public void setItemConsumeCount(int count) {
        itemConsumeCount = count;
    }

    /**
     * @return Returns the ID of item for consume.
     */
    public int getItemConsumeId() {
        return itemConsumeId;
    }

    /**
     * @return Returns the level.
     */
    public int getLevel() {
        return level;
    }

    void setLevel(int level) {
        this.level = level;
    }

    /**
     * @return Returns the sub level.
     */
    public int getSubLevel() {
        return 0;
    }

    public SkillType getSkillType() {
        return type;
    }

    /**
     * @return Returns true to set physical skills.
     */
    public boolean isPhysical() {
        return type == SkillType.PHYSIC;
    }

    /**
     * @return Returns true to set magic skills.
     */
    public boolean isMagic() {
        return type == SkillType.MAGIC;
    }

    /**
     * @return Returns true to set static skills.
     */
    public boolean isStatic() {
        return type == SkillType.STATIC;
    }

    /**
     * @return Returns true to set dance skills.
     */
    public boolean isDance() {
        return type == SkillType.DANCE;
    }

    /**
     * @return Returns true to set static reuse.
     */
    public boolean isStaticReuse() {
        return staticReuse;
    }

    void setStaticReuse(boolean staticReuse) {
        this.staticReuse = staticReuse;
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
        return reuseDelayGroup;
    }

    public long getReuseHashCode() {
        return reuseHashCode;
    }

    public int getHitTime() {
        return hitTime;
    }

    public void setHitTime(int time) {
        hitTime = time;
    }

    public double getHitCancelTime() {
        return hitCancelTime;
    }

    void setHitCancelTime(double time) {
        hitCancelTime = time;
    }

    /**
     * @return the cool time
     */
    public int getCoolTime() {
        return coolTime;
    }

    public void setCoolTime(int time) {
        coolTime = time;
    }

    /**
     * @return the target type of the skill : SELF, TARGET, SUMMON, GROUND...
     */
    public TargetType getTargetType() {
        return targetType;
    }

    public void setTargetType(TargetType type) {
        targetType = type;
    }

    /**
     * @return the affect scope of the skill : SINGLE, FAN, SQUARE, PARTY, PLEDGE...
     */
    public AffectScope getAffectScope() {
        return affectScope;
    }

    public void setAffectScope(AffectScope scope) {
        affectScope = scope;
    }

    /**
     * @return the affect object of the skill : All, Clan, Friend, NotFriend, Invisible...
     */
    public AffectObject getAffectObject() {
        return affectObject;
    }

    public void setAffectObject(AffectObject object) {
        affectObject = object;
    }

    /**
     * @return the AOE range of the skill.
     */
    public int getAffectRange() {
        return affectRange;
    }

    public void setAffectRange(int range) {
        affectRange = range;
    }

    /**
     * @return the maximum amount of targets the skill can affect or 0 if unlimited.
     */
    public int getAffectLimit() {
        return affectMin + Rnd.get(affectRandom);
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

    public void setEffectPoint(int effectPoints) {
        effectPoint = effectPoints;
    }

    public boolean useSoulShot() {
        return hasEffectType(EffectType.PHYSICAL_ATTACK, EffectType.PHYSICAL_ATTACK_HP_LINK);
    }

    public boolean useSpiritShot() {
        return type == SkillType.MAGIC;
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

    void setStayAfterDeath(boolean stayAfterDeath) {
        this.stayAfterDeath = stayAfterDeath;
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
        effects.computeIfAbsent(effectScope, k -> new ArrayList<>()).add(effect);
    }

    /**
     * Gets the skill effects.
     *
     * @param effectScope the effect scope
     * @return the list of effects for the give scope
     */
    public List<AbstractEffect> getEffects(EffectScope effectScope) {
        return effects.get(effectScope);
    }

    /**
     * Verify if this skill has effects for the given scope.
     *
     * @param effectScope the effect scope
     * @return {@code true} if this skill has effects for the given scope, {@code false} otherwise
     */
    public boolean hasEffects(EffectScope effectScope) {
        final List<AbstractEffect> effects = this.effects.get(effectScope);
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
                if ((addContinuousEffects && isContinuous() && !debuff)) {
                    effected.getServitors().values().forEach(s -> applyEffects(effector, s, instant, 0));
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

                final EffectScope pvpOrPveEffectScope = isPlayable(caster) && isAttackable(target) ? EffectScope.PVE : isPlayable(caster) && isPlayable(target) ? EffectScope.PVP : null;
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
    public void addCondition(SkillConditionScope skillConditionScope, SkillCondition skillCondition) {
        conditions.computeIfAbsent(skillConditionScope, k -> new ArrayList<>()).add(skillCondition);
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
        return conditions.getOrDefault(skillConditionScope, Collections.emptyList()).stream().allMatch(c -> c.canUse(caster, this, target));
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

    public void setWithoutAction(boolean withoutAction) {
        this.withoutAction = withoutAction;
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
                    for (List<AbstractEffect> effectList : effects.values()) {
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

        for (AbstractEffect effect : effects.get(effectScope)) {
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
     * @param activeChar
     * @return alternative skill that has been attached due to the effect of toggle skills on the player (e.g Fire Stance, Water Stance).
     */
    public Skill getAttachedSkill(Creature activeChar) {
        // If character is double casting, return double cast skill.
        if ((_doubleCastSkill > 0) && activeChar.isAffected(EffectFlag.DOUBLE_CAST)) {
            return SkillEngine.getInstance().getSkill(getDoubleCastSkill(), getLevel());
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

        return SkillEngine.getInstance().getSkill(attachedSkill.getSkillId(), getLevel());
    }

    public String getIcon() {
        return icon;
    }

    void setIcon(String icon) {
        this.icon = icon;
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

    void setMagicCriticalRate(double rate) {
        this.magicCriticalRate = rate;
    }

    public SkillBuffType getBuffType() {
        return buffType;
    }

    public boolean isEnchantable() {
        return false;
    }

    void setTrait(TraitType trait) {
        this.traitType = trait;
    }

    void setProperty(BasicProperty property) {
        basicProperty = property;
    }

    void setLevelBonusRate(int rate) {
        levelBonusRate = rate;
    }

    void setRemoveOnAction(boolean removeOnAction) {
        removedOnAnyActionExceptMove = removeOnAction;
    }

    void setRemoveOnDamage(boolean removeOnDamage) {
        removedOnDamage = removeOnDamage;
    }

    void setBlockedOnOlympiad(boolean blockedOnOlympiad) {
        this.blockedInOlympiad = blockedOnOlympiad;
    }

    void setSuicide(boolean suicide) {
        this.isSuicideAttack = suicide;
    }

    void setTriggered(boolean triggered) {
        this.isTriggeredSkill = triggered;
    }

    void setDispellable(boolean dispellable) {
        this.canBeDispelled = dispellable;
    }

    void setCheck(boolean check) {
        excludedFromCheck = !check;
    }

    void setCanCastDisabled(boolean castDisabled) {
        this.canCastWhileDisabled = castDisabled;
    }

    void setSummonShared(boolean summonShared) {
        this.isSharedWithSummon = summonShared;
    }

    void setRemoveAbnormalOnLeave(boolean remove) {
        deleteAbnormalOnLeave = remove;
    }

    void setIrreplacable(boolean irreplacable) {
        irreplacableBuff = irreplacable;
    }

    void setBlockActionSkill(boolean block) {
        this.blockActionUseSkill = block;
    }

    void setAutoUse(SkillAutoUseType autoUse) {
        this.autoUse = autoUse;
    }

    void setSoulConsume(int souls) {
        soulMaxConsume = souls;
    }

    void setChargeConsume(int charges) {
        chargeConsume = charges;
    }

    void setAffectMin(int affectMin) {
        this.affectMin = affectMin;
    }

    void setAffectRandom(int affectRandom) {
        this.affectRandom = affectRandom;
    }

    void setAbnormalVisual(AbnormalVisualEffect visual) {
        abnormalVisualEffects = Set.of(visual);
    }

    void setAbnormalSubordination(AbnormalType subordination) {
        subordinationAbnormalType = subordination;
    }

    void setResistAbnormals(Set<AbnormalType> abnormals) {
        this.abnormalResists = abnormals;
    }

    void setChannelingSkill(int skill) {
        this.channelingSkillId = skill;
    }

    void setChannelingMpConsume(int mpConsume) {
        mpPerChanneling = mpConsume;
    }

    void setChannelingInitialDelay(long delay) {
        channelingStart = delay;
    }

    void setChannelingInterval(long interval) {
        channelingTickInterval = interval;
    }

    void setReuse(int reuse) {
        reuseDelay = reuse;
    }

    void setManaInitConsume(int consume) {
        manaInitialConsume = consume;
    }

    void setManaConsume(int consume) {
        manaConsume = consume;
    }

    void setItemConsume(int item) {
        itemConsumeId = item;
    }

    void setFanStartAngle(int angle) {
        this.fanStartAngle = angle;
    }

    public int getFanStartAngle() {
        return fanStartAngle;
    }

    void setFanRadius(int radius) {
        this.fanRadius = radius;
    }

    public int getFanRadius() {
        return fanRadius;
    }

    void setFanAngle(int angle) {
        this.fanAngle = angle;
    }

    public int getFanAngle() {
        return fanAngle;
    }

    void setAbnormalLevel(int level) {
        abnormalLvl = level;
    }

    void setAbnormalChance(int chance) {
        activateRate = chance;
    }

    Skill clone(boolean mantainAttributes) throws CloneNotSupportedException {
        var clone = clone();
        if (!mantainAttributes) {
            clone.effects = new EnumMap<>(EffectScope.class);
        }
        return clone;
    }

    @Override
    protected Skill clone() throws CloneNotSupportedException {
        return (Skill) super.clone();
    }

    @Override
    public String toString() {
        return String.format("Skill %s (%d, %d)", name, id, level);
    }
}
