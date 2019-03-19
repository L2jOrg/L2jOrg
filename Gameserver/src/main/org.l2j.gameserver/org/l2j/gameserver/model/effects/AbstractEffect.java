package org.l2j.gameserver.model.effects;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.L2Character;
import org.l2j.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.model.skills.Skill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Abstract effect implementation.<br>
 * Instant effects should not override {@link #onExit(L2Character, L2Character, Skill)}.<br>
 * Instant effects should not override {@link #canStart(L2Character, L2Character, Skill)}, all checks should be done {@link #onStart(L2Character, L2Character, Skill)}.<br>
 * Do not call super class methods {@link #onStart(L2Character, L2Character, Skill)} nor {@link #onExit(L2Character, L2Character, Skill)}.
 *
 * @author Zoey76
 */
public abstract class AbstractEffect {
    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractEffect.class);

    private int _ticks;

    /**
     * Gets the effect ticks
     *
     * @return the ticks
     */
    public int getTicks() {
        return _ticks;
    }

    /**
     * Sets the effect ticks
     *
     * @param ticks the ticks
     */
    protected void setTicks(int ticks) {
        _ticks = ticks;
    }

    public double getTicksMultiplier() {
        return (getTicks() * Config.EFFECT_TICK_RATIO) / 1000f;
    }

    /**
     * Calculates whether this effects land or not.<br>
     * If it lands will be scheduled and added to the character effect list.<br>
     * Override in effect implementation to change behavior. <br>
     * <b>Warning:</b> Must be used only for instant effects continuous effects will not call this they have their success handled by activate_rate.
     *
     * @param effector
     * @param effected
     * @param skill
     * @return {@code true} if this effect land, {@code false} otherwise
     */
    public boolean calcSuccess(L2Character effector, L2Character effected, Skill skill) {
        return true;
    }

    /**
     * Verify if the buff can start.<br>
     * Used for continuous effects.
     *
     * @param effector
     * @param effected
     * @param skill
     * @return {@code true} if all the start conditions are meet, {@code false} otherwise
     */
    public boolean canStart(L2Character effector, L2Character effected, Skill skill) {
        return true;
    }

    public void instant(L2Character effector, L2Character effected, Skill skill, L2ItemInstance item) {

    }

    public void continuousInstant(L2Character effector, L2Character effected, Skill skill, L2ItemInstance item) {

    }

    public void onStart(L2Character effector, L2Character effected, Skill skill) {

    }

    public void onExit(L2Character effector, L2Character effected, Skill skill) {

    }

    /**
     * Called on each tick.<br>
     * If the abnormal time is lesser than zero it will last forever.
     *
     * @param effector
     * @param effected
     * @param skill
     * @return if {@code true} this effect will continue forever, if {@code false} it will stop after abnormal time has passed
     */
    public boolean onActionTime(L2Character effector, L2Character effected, Skill skill) {
        return false;
    }

    /**
     * Get the effect flags.
     *
     * @return bit flag for current effect
     */
    public long getEffectFlags() {
        return EffectFlag.NONE.getMask();
    }

    public boolean checkCondition(Object obj) {
        return true;
    }

    /**
     * Verify if this effect is an instant effect.
     *
     * @return {@code true} if this effect is instant, {@code false} otherwise
     */
    public boolean isInstant() {
        return false;
    }

    /**
     * @param effector
     * @param effected
     * @param skill
     * @return {@code true} if pump can be invoked, {@code false} otherwise
     */
    public boolean canPump(L2Character effector, L2Character effected, Skill skill) {
        return true;
    }

    /**
     * @param effected
     * @param skill
     */
    public void pump(L2Character effected, Skill skill) {

    }

    /**
     * Get this effect's type.<br>
     * TODO: Remove.
     *
     * @return the effect type
     */
    public L2EffectType getEffectType() {
        return L2EffectType.NONE;
    }

    @Override
    public String toString() {
        return "Effect " + getClass().getSimpleName();
    }
}