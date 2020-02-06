package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.engine.mission.MissionDataHolder;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.items.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;

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
