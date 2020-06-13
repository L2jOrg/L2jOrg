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
package org.l2j.gameserver.engine.skill.api;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.xml.impl.SkillTreesData;
import org.l2j.gameserver.engine.skill.SkillAutoUseType;
import org.l2j.gameserver.enums.AttributeType;
import org.l2j.gameserver.enums.BasicProperty;
import org.l2j.gameserver.enums.NextActionType;
import org.l2j.gameserver.handler.AffectScopeHandler;
import org.l2j.gameserver.handler.TargetHandler;
import org.l2j.gameserver.model.PcCondOverride;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.cubic.CubicInstance;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.interfaces.IIdentifiable;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.skills.*;
import org.l2j.gameserver.model.skills.targets.AffectObject;
import org.l2j.gameserver.model.skills.targets.AffectScope;
import org.l2j.gameserver.model.skills.targets.TargetType;
import org.l2j.gameserver.model.stats.BasicPropertyResist;
import org.l2j.gameserver.model.stats.Formulas;
import org.l2j.gameserver.model.stats.TraitType;
import org.l2j.gameserver.network.SystemMessageId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.commons.util.Util.contains;
import static org.l2j.commons.util.Util.falseIfNullOrElse;
import static org.l2j.gameserver.network.serverpackets.SystemMessage.getSystemMessage;
import static org.l2j.gameserver.util.GameUtils.*;

/**
 * @author JoeAlisson
 */
public final class Skill implements IIdentifiable, Cloneable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Skill.class);

    private final int id;
    private final String name;
    private final SkillOperateType operateType;
    private final SkillType type;
    private final boolean debuff;
    private final int maxLevel;

    public Map<SkillConditionScope, List<SkillCondition>> conditions = new EnumMap<>(SkillConditionScope.class);
    public Map<EffectScope, List<AbstractEffect>> effects = new EnumMap<>(EffectScope.class);
    private TraitType traitType = TraitType.NONE;

    private AbnormalType abnormalType = AbnormalType.NONE;
    private AbnormalType subordinationAbnormalType = AbnormalType.NONE;
    private EnumSet<AbnormalVisualEffect>  abnormalVisualEffect;
    private boolean isAbnormalInstant;
    private int abnormalLvl;
    private int abnormalTime;
    private int activateRate;

    private AttributeType attributeType = AttributeType.NONE;
    private int attributeValue;

    private String icon;
    private int level;
    private int displayId;

    private int castRange;
    private int effectRange;

    private int coolTime;
    private int hitTime;
    private double hitCancelTime;

    private int reuseDelayGroup = -1;
    private boolean staticReuse;
    private long reuseHashCode;
    private int reuseDelay;

    private int magicLevel;
    private int effectPoint;
    private int levelBonusRate;
    private double magicCriticalRate;

    private int minChance;
    private int maxChance;

    private TargetType targetType;
    public AffectScope affectScope;
    private AffectObject affectObject;
    private int affectRange;
    public int affectMin;
    public int affectRandom;

    private int manaConsume;
    private int manaInitialConsume;
    private int hpConsume;
    private int soulMaxConsume;
    private int chargeConsume;
    private int itemConsumeId;
    private int itemConsumeCount;

    private boolean removedOnAnyActionExceptMove;
    private boolean removedOnDamage;

    private boolean blockedInOlympiad;
    private boolean stayAfterDeath;
    private boolean isTriggeredSkill;
    private boolean isSuicideAttack;
    private boolean canBeDispelled;
    private boolean excludedFromCheck;
    private boolean withoutAction;

    private int channelingSkillId;
    private long channelingStart;
    private long channelingTickInterval;
    private int mpPerChanneling;

    private boolean canCastWhileDisabled;
    private boolean isSharedWithSummon;
    private boolean deleteAbnormalOnLeave;
    private boolean irreplacableBuff;
    private boolean blockActionUseSkill;

    private Set<AbnormalType> abnormalResists;
    private SkillBuffType buffType;
    private BasicProperty basicProperty;
    private NextActionType nextAction = NextActionType.NONE;
    private SkillAutoUseType skillAutoUseType;

    private int fanStartAngle;
    private int fanRadius;
    private int fanAngle;

    private volatile long effectsMask = -1;
    private boolean useCustomTime;
    private boolean useCustomDelay;

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

        if(isNull(abnormalResists)) {
            abnormalResists = Collections.emptySet();
        }

        if (Config.ENABLE_MODIFY_SKILL_REUSE && Config.SKILL_REUSE_LIST.containsKey(id)) {
            useCustomDelay = true;
            reuseDelay = Config.SKILL_REUSE_LIST.get(id);
        }

        if (Config.ENABLE_MODIFY_SKILL_DURATION && Config.SKILL_DURATION_LIST.containsKey(id)) {
            useCustomTime = true;
            abnormalTime = Config.SKILL_DURATION_LIST.get(id);
        }

        reuseHashCode = SkillEngine.skillHashCode(reuseDelayGroup > 0 ? reuseDelayGroup : id, level);

        minChance = Config.MIN_ABNORMAL_STATE_SUCCESS_RATE;
        maxChance = Config.MAX_ABNORMAL_STATE_SUCCESS_RATE;
    }

    public boolean checkCondition(Creature activeChar, WorldObject object) {
        if (activeChar.canOverrideCond(PcCondOverride.SKILL_CONDITIONS) && !Config.GM_SKILL_RESTRICTION) {
            return true;
        }

        if (isPlayer(activeChar) && activeChar.getActingPlayer().isMounted() && isBad() && !MountEnabledSkillList.contains(id)) {
            activeChar.sendPacket(getSystemMessage(SystemMessageId.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(id));
            return false;
        }

        if (!checkConditions(SkillConditionScope.GENERAL, activeChar, object) || !checkConditions(SkillConditionScope.TARGET, activeChar, object)) {
            // Self targeted bad skills should not send a message.
            if (!(activeChar == object && isBad()) ) {
                activeChar.sendPacket(getSystemMessage(SystemMessageId.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(id));
            }
            return false;
        }
        return true;
    }

    public boolean checkConditions(SkillConditionScope skillConditionScope, Creature caster, WorldObject target) {
        return conditions.getOrDefault(skillConditionScope, Collections.emptyList()).stream().allMatch(c -> c.canUse(caster, this, target));
    }

    public WorldObject getTarget(Creature activeChar, boolean forceUse, boolean dontMove, boolean sendMessage) {
        return getTarget(activeChar, activeChar.getTarget(), forceUse, dontMove, sendMessage);
    }

    public WorldObject getTarget(Creature activeChar, WorldObject seletedTarget, boolean forceUse, boolean dontMove, boolean sendMessage) {
        var handler = TargetHandler.getInstance().getHandler(targetType);
        if (nonNull(handler)) {
            try {
                return handler.getTarget(activeChar, seletedTarget, this, forceUse, dontMove, sendMessage);
            } catch (Exception e) {
                LOGGER.error("Could not execute target handler {} on skill {}", handler, this, e);
            }
        }
        if(activeChar.isGM()) {
            activeChar.sendMessage(format("Target type %s of skill %s is not currently handled.", targetType, this));
        }
        return null;
    }

    /**
     * @param activeChar the character that needs to gather targets.
     * @param target     the initial target activeChar is focusing upon.
     * @return list containing objects gathered in a specific geometric way that are valid to be affected by this skill.
     */
    public List<WorldObject> getTargetsAffected(Creature activeChar, WorldObject target) {
        if (isNull(target)) {
            return Collections.emptyList();
        }
        var handler = AffectScopeHandler.getInstance().getHandler(affectScope);
        if (nonNull(handler)) {
            try {
                final List<WorldObject> result = new LinkedList<>();
                handler.forEachAffected(activeChar, target, this, result::add);
                return result;
            } catch (Exception e) {
                LOGGER.error("Could not execute affect scope handler {} of Skill {}", handler, this, e);
            }
        }
        if(activeChar.isGM()) {
            activeChar.sendMessage(format("Target affect scope %s of skill %s is not currently handled.", affectScope, this));
        }
        return Collections.emptyList();
    }

    public void forEachTargetAffected(Creature activeChar, WorldObject target, Consumer<? super WorldObject> action) {
        if (isNull(target)) {
            return;
        }

        var handler = AffectScopeHandler.getInstance().getHandler(affectScope);
        if (nonNull(handler)) {
            try {
                handler.forEachAffected(activeChar, target, this, action);
            } catch (Exception e) {
                LOGGER.warn("Could not execute affect scope handler {} of skill {}", handler, this, e);
            }
        } else if(activeChar.isGM()) {
            activeChar.sendMessage(String.format("Target affect scope %s of skill %s is not currently handled.", affectScope, this));
        }
    }

    public void applyEffectScope(EffectScope effectScope, BuffInfo info, boolean applyInstantEffects, boolean addContinuousEffects) {
        if (nonNull(effectScope) && hasEffects(effectScope)) {
            for (var effect : getEffects(effectScope)) {
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

    public void applyEffects(Creature effector, Creature effected) {
        applyEffects(effector, effected, false, false, true, 0, null);
    }

    private void applyEffects(Creature effector, Creature effected, Item item) {
        applyEffects(effector, effected, false, false, true, 0, item);
    }

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
     */
    public void applyEffects(Creature effector, Creature effected, boolean self, boolean passive, boolean instant, int abnormalTime, Item item) {
        if (isNull(effected)) {
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

    public void applyChannelingEffects(Creature effector, Creature effected) {
        if (isNull(effected)) {
            return;
        }

        final BuffInfo info = new BuffInfo(effector, effected, this, false, null, null);
        applyEffectScope(EffectScope.CHANNELING, info, true, true);
    }

    public void activateSkill(Creature caster, WorldObject... targets) {
        activateSkill(caster, null, targets);
    }

    public void activateSkill(Creature caster, Item item, WorldObject... targets) {
        activateSkill(caster, null, item, targets);
    }

    public void activateSkill(CubicInstance cubic, WorldObject... targets) {
        activateSkill(cubic.getOwner(), cubic, null, targets);
    }

    private void activateSkill(Creature caster, CubicInstance cubic, Item item, WorldObject... targets) {
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

        if (hasEffects(EffectScope.SELF)) {
            if (caster.isAffectedBySkill(id)) {
                caster.stopSkillEffects(true, id);
            }
            applyEffects(caster, caster, true, false, true, 0, item);
        }
        if (isSuicideAttack) {
            caster.doDie(caster);
        }
    }

    public boolean hasAnyEffectType(EffectType... effectTypes) {
        if(isNull(effectTypes)) {
            return false;
        }

        if (this.effectsMask == -1) {
            synchronized (this) {
                if (this.effectsMask == -1) {
                    effectsMask = effects.values().stream().flatMap(Collection::stream).mapToLong(e -> e.getEffectType().mask()).reduce(0, (a, b) -> a | b);
                }
            }
        }

        for (EffectType type : effectTypes) {
            if ((effectsMask & type.mask()) != 0) {
                return true;
            }
        }
        return false;
    }

    public boolean hasAnyEffectType(EffectScope effectScope, EffectType... effectTypes) {
        if (hasEffects(effectScope) || isNull(effectTypes)) {
            return false;
        }

        return effects.get(effectScope).stream().anyMatch(e -> contains(effectTypes, e.getEffectType()));
    }

    public void addEffect(EffectScope effectScope, AbstractEffect effect) {
        effects.computeIfAbsent(effectScope, k -> new ArrayList<>()).add(effect);
    }

    @Override
    public int getId() {
        return id;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    void setAttributeType(AttributeType type) {
        attributeType = type;
    }
    public AttributeType getAttributeType() {
        return attributeType;
    }

    void setAttributeValue(int value) {
        attributeValue = value;
    }

    public int getAttributeValue() {
        return attributeValue;
    }

    public boolean allowOnTransform() {
        return isPassive();
    }

    void setAbnormalInstant(boolean instant) {
        this.isAbnormalInstant = instant;
    }

    public boolean isAbnormalInstant() {
        return isAbnormalInstant;
    }

    void setAbnormalType(AbnormalType type) {
        abnormalType = type;
    }

    public AbnormalType getAbnormalType() {
        return abnormalType;
    }

    void setAbnormalSubordination(AbnormalType subordination) {
        subordinationAbnormalType = subordination;
    }

    public AbnormalType getSubordinationAbnormalType() {
        return subordinationAbnormalType;
    }

    void setAbnormalLevel(int level) {
        abnormalLvl = level;
    }

    public int getAbnormalLvl() {
        return abnormalLvl;
    }

    void setAbnormalTime(int time) {
        if(!useCustomTime) {
            abnormalTime = time;
        }
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

    void setAbnormalVisualEffect(EnumSet<AbnormalVisualEffect> visual) {
        visual.remove(AbnormalVisualEffect.NONE);
        abnormalVisualEffect = visual;
    }

    public EnumSet<AbnormalVisualEffect> getAbnormalVisualEffect() {
        return abnormalVisualEffect;
    }

    public boolean hasAbnormalVisualEffect() {
        return falseIfNullOrElse(abnormalVisualEffect, Predicate.not(AbstractCollection::isEmpty));
    }

    public int getMagicLevel() {
        return magicLevel;
    }

    void setMagicLevel(int level) {
        magicLevel = level;
    }

    public int getActivateRate() {
        return activateRate;
    }

    void setActivateRate(int rate) {
        activateRate = rate;
    }

    public int getMinChance() {
        return minChance;
    }

    public int getMaxChance() {
        return maxChance;
    }

    void setNextAction(NextActionType action) {
        nextAction = action;
    }

    public NextActionType getNextAction() {
        return nextAction;
    }

    void setCastRange(int range) {
        castRange = range;
    }

    public int getCastRange() {
        return castRange;
    }

    void setEffectRange(int effectRange) {
        this.effectRange = effectRange;
    }

    public int getEffectRange() {
        return effectRange;
    }

    void setHpConsume(int consume) {
        hpConsume = consume;
    }

    public int getHpConsume() {
        return hpConsume;
    }

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

    public BasicProperty getBasicProperty() {
        return basicProperty;
    }

    public int getItemConsumeCount() {
        return itemConsumeCount;
    }

    void setItemConsumeCount(int count) {
        itemConsumeCount = count;
    }

    public int getItemConsumeId() {
        return itemConsumeId;
    }

    public int getLevel() {
        return level;
    }

    void setLevel(int level) {
        this.level = level;
    }

    public int getSubLevel() {
        return 0;
    }

    public SkillType getSkillType() {
        return type;
    }

    public boolean isPhysical() {
        return type == SkillType.PHYSIC;
    }

    public boolean isMagic() {
        return type == SkillType.MAGIC;
    }

    public boolean isStatic() {
        return type == SkillType.STATIC;
    }

    public boolean isDance() {
        return type == SkillType.DANCE;
    }

    public boolean isStaticReuse() {
        return staticReuse;
    }

    void setStaticReuse(boolean staticReuse) {
        this.staticReuse = staticReuse;
    }

    public int getMpConsume() {
        return manaConsume;
    }

    public int getMpInitialConsume() {
        return manaInitialConsume;
    }

    public int getMpPerChanneling() {
        return mpPerChanneling;
    }

    public String getName() {
        return name;
    }

    public int getReuseDelay() {
        return reuseDelay;
    }

    public int getReuseDelayGroup() {
        return reuseDelayGroup;
    }

    public long getReuseHashCode() {
        return reuseHashCode;
    }

    public int getHitTime() {
        return hitTime;
    }

    void setHitTime(int time) {
        hitTime = time;
    }

    public double getHitCancelTime() {
        return hitCancelTime;
    }

    void setHitCancelTime(double time) {
        hitCancelTime = time;
    }

    public int getCoolTime() {
        return coolTime;
    }

    void setCoolTime(int time) {
        coolTime = time;
    }

    public TargetType getTargetType() {
        return targetType;
    }

    void setTargetType(TargetType type) {
        targetType = type;
    }

    void setAffectScope(AffectScope scope) {
        affectScope = scope;
    }

    public AffectObject getAffectObject() {
        return affectObject;
    }

    void setAffectObject(AffectObject object) {
        affectObject = object;
    }

    public int getAffectRange() {
        return affectRange;
    }

    void setAffectRange(int range) {
        affectRange = range;
    }

    /**
     * @return the maximum amount of targets the skill can affect or 0 if unlimited.
     */
    public int getAffectLimit() {
        return (affectMin > 0 || affectRandom > 0) ? affectMin + Rnd.get(affectRandom) : 0;
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

    public boolean isTransformation() {
        return abnormalType == AbnormalType.TRANSFORM;
    }

    public int getEffectPoint() {
        return effectPoint;
    }

    void setEffectPoint(int effectPoints) {
        effectPoint = effectPoints;
    }

    public boolean useSoulShot() {
        return hasAnyEffectType(EffectType.PHYSICAL_ATTACK, EffectType.PHYSICAL_ATTACK_HP_LINK);
    }

    public boolean useSpiritShot() {
        return type == SkillType.MAGIC;
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
        return stayAfterDeath || irreplacableBuff;
    }

    void setStayAfterDeath(boolean stayAfterDeath) {
        this.stayAfterDeath = stayAfterDeath;
    }

    public boolean isBad() {
        return effectPoint < 0;
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
        return falseIfNullOrElse(effects.get(effectScope), Predicate.not(List::isEmpty));
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

    public boolean canBeDispelled() {
        return canBeDispelled;
    }

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

    void setWithoutAction(boolean withoutAction) {
        this.withoutAction = withoutAction;
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

    public boolean canCastWhileDisabled() {
        return canCastWhileDisabled;
    }

    public boolean isSharedWithSummon() {
        return isSharedWithSummon;
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

    /**
     * @return if skill could not be requested for use by players.
     */
    public boolean isBlockActionUseSkill() {
        return blockActionUseSkill;
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

    public TraitType getTrait() {
        return traitType;
    }

    void setProperty(BasicProperty property) {
        basicProperty = property;
    }

    void setLevelBonusRate(int rate) {
        levelBonusRate = rate;
    }

    public int getLevelBonusRate() {
        return levelBonusRate;
    }

    void setRemoveOnAction(boolean removeOnAction) {
        removedOnAnyActionExceptMove = removeOnAction;
    }

    public boolean isRemovedOnAnyActionExceptMove() {
        return removedOnAnyActionExceptMove;
    }

    void setRemoveOnDamage(boolean removeOnDamage) {
        removedOnDamage = removeOnDamage;
    }

    public boolean isRemovedOnDamage() {
        return removedOnDamage;
    }

    void setBlockedOnOlympiad(boolean blockedOnOlympiad) {
        this.blockedInOlympiad = blockedOnOlympiad;
    }

    public boolean isBlockedInOlympiad() {
        return blockedInOlympiad;
    }

    void setSuicide(boolean suicide) {
        this.isSuicideAttack = suicide;
    }

    public boolean isSuicideAttack() {
        return isSuicideAttack;
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

    void setSkillAutoUseType(SkillAutoUseType skillAutoUseType) {
        this.skillAutoUseType = skillAutoUseType;
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

    void setResistAbnormals(Set<AbnormalType> abnormals) {
        this.abnormalResists = abnormals;
    }

    void setChannelingSkill(int skill) {
        this.channelingSkillId = skill;
    }

    public int getChannelingSkillId() {
        return channelingSkillId;
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
        if(!useCustomDelay) {
            reuseDelay = reuse;
        }
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

    void setAbnormalChance(int chance) {
        activateRate = chance;
    }

    Skill clone(boolean keepEffects, boolean keepConditions) throws CloneNotSupportedException {
        var clone = clone();
        if (!keepEffects) {
            clone.effects = new EnumMap<>(EffectScope.class);
        }
        if(!keepConditions) {
            clone.conditions = new EnumMap<>(SkillConditionScope.class);
        }
        return clone;
    }

    @Override
    protected Skill clone() throws CloneNotSupportedException {
        return (Skill) super.clone();
    }

    @Override
    public String toString() {
        return format("Skill %s (%d, %d)", name, id, level);
    }

    public boolean isAutoUse() {
        return falseIfNullOrElse(skillAutoUseType, t -> t != SkillAutoUseType.NONE);
    }

    public boolean isAutoTransformation() {
        return skillAutoUseType == SkillAutoUseType.TRANSFORM;
    }

    public boolean isAutoBuff() {
        return skillAutoUseType == SkillAutoUseType.BUFF;
    }
}
