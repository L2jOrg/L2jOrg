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
package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.engine.mission.MissionDataHolder;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.item.ItemTemplate;

/**
 * The Class Condition.
 *
 * @author mkizub
 */
public abstract class Condition  {
    private String _msg;
    private int _msgId;
    private boolean _addName = false;

    /**
     * Gets the message.
     *
     * @return the message
     */
    public final String getMessage() {
        return _msg;
    }

    /**
     * Sets the message.
     *
     * @param msg the new message
     */
    public final void setMessage(String msg) {
        _msg = msg;
    }

    /**
     * Gets the message id.
     *
     * @return the message id
     */
    public final int getMessageId() {
        return _msgId;
    }

    /**
     * Sets the message id.
     *
     * @param msgId the new message id
     */
    public final void setMessageId(int msgId) {
        _msgId = msgId;
    }

    /**
     * Adds the name.
     */
    public final void addName() {
        _addName = true;
    }

    /**
     * Checks if is adds the name.
     *
     * @return true, if is adds the name
     */
    public final boolean isAddName() {
        return _addName;
    }


    public final boolean test(Creature caster, Creature target, Skill skill) {
        return test(caster, target, skill, null);
    }

    public final boolean test(Creature caster, Creature target, ItemTemplate item) {
        return test(caster, target, null, null);
    }

    public final boolean test(Creature caster, MissionDataHolder onewayreward) {
        return test(caster, null, null, null);
    }

    public final boolean test(Creature caster, Creature target, Skill skill, ItemTemplate item) {
        return testImpl(caster, target, skill, item);
    }

    /**
     * Test the condition.
     *
     * @param effector the effector
     * @param effected the effected
     * @param skill    the skill
     * @param item     the item
     * @return {@code true} if successful, {@code false} otherwise
     */
    public abstract boolean testImpl(Creature effector, Creature effected, Skill skill, ItemTemplate item);
}
