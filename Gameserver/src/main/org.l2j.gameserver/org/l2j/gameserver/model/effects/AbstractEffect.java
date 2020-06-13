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
package org.l2j.gameserver.model.effects;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.item.instance.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Abstract effect implementation.<br>
 * Instant effects should not override {@link #onExit(Creature, Creature, Skill)}.<br>
 * Instant effects should not override {@link #canStart(Creature, Creature, Skill)}, all checks should be done {@link #onStart(Creature, Creature, Skill, Item)}.<br>
 *  * Do not call super class methods {@link #onStart(Creature, Creature, Skill, Item)} nor {@link #onExit(Creature, Creature, Skill)}.
 *
 * @author Zoey76
 */
public abstract class AbstractEffect  {
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
    public boolean calcSuccess(Creature effector, Creature effected, Skill skill) {
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
    public boolean canStart(Creature effector, Creature effected, Skill skill) {
        return true;
    }

    public void instant(Creature effector, Creature effected, Skill skill, Item item) {

    }

    public void continuousInstant(Creature effector, Creature effected, Skill skill, Item item) {

    }

    public void onStart(Creature effector, Creature effected, Skill skill, Item item){

    }

    public void onExit(Creature effector, Creature effected, Skill skill) {

    }

    /**
     * Called on each tick.<br>
     * If the abnormal time is lesser than zero it will last forever.
     *
     * @param effector
     * @param effected
     * @param skill
     * @param item
     * @return if {@code true} this effect will continue forever, if {@code false} it will stop after abnormal time has passed
     */
    public boolean onActionTime(Creature effector, Creature effected, Skill skill, Item item) {
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
    public boolean canPump(Creature effector, Creature effected, Skill skill) {
        return true;
    }

    /**
     * @param effected
     * @param skill
     */
    public void pump(Creature effected, Skill skill) {

    }

    /**
     * Get this effect's type.<br>
     * TODO: Remove.
     *
     * @return the effect type
     */
    public EffectType getEffectType() {
        return EffectType.NONE;
    }

    @Override
    public String toString() {
        return "Effect " + getClass().getSimpleName();
    }
}