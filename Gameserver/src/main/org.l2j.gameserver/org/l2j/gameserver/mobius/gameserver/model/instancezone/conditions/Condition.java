/*
 * This file is part of the L2J Mobius project.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.mobius.gameserver.model.instancezone.conditions;

import org.l2j.gameserver.mobius.gameserver.model.StatsSet;
import org.l2j.gameserver.mobius.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.instancezone.InstanceTemplate;
import org.l2j.gameserver.mobius.gameserver.network.SystemMessageId;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.SystemMessage;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * Abstract instance condition
 *
 * @author malyelfik
 */
public abstract class Condition {
    private final InstanceTemplate _template;
    private final StatsSet _parameters;
    private final boolean _leaderOnly;
    private final boolean _showMessageAndHtml;
    private SystemMessageId _systemMsg = null;
    private BiConsumer<SystemMessage, L2PcInstance> _systemMsgParams = null;

    /**
     * Create new condition
     *
     * @param template           template of instance where condition will be registered.
     * @param parameters         parameters of current condition
     * @param onlyLeader         flag which means if only leader should be affected (leader means player who wants to enter not group leader!)
     * @param showMessageAndHtml if {@code true} and HTML message is defined then both are send, otherwise only HTML or message is send
     */
    public Condition(InstanceTemplate template, StatsSet parameters, boolean onlyLeader, boolean showMessageAndHtml) {
        _template = template;
        _parameters = parameters;
        _leaderOnly = onlyLeader;
        _showMessageAndHtml = showMessageAndHtml;
    }

    /**
     * Gets parameters of condition.
     *
     * @return set of parameters
     */
    protected final StatsSet getParameters() {
        return _parameters;
    }

    /**
     * Template of instance where condition is registered.
     *
     * @return instance template
     */
    public InstanceTemplate getInstanceTemplate() {
        return _template;
    }

    /**
     * Check if condition is valid for enter group {@code group}.
     *
     * @param npc          instance of NPC which was used to enter into instance
     * @param group        group which contain players which wants to enter
     * @param htmlCallback HTML callback function used to display fail HTML to player
     * @return {@code true} when all conditions met, otherwise {@code false}
     */
    public boolean validate(L2Npc npc, List<L2PcInstance> group, BiConsumer<L2PcInstance, String> htmlCallback) {
        for (L2PcInstance member : group) {
            if (!test(member, npc, group)) {
                sendMessage(group, member, htmlCallback);
                return false;
            }

            if (_leaderOnly) {
                break;
            }
        }
        return true;
    }

    /**
     * Send fail message to enter player group.
     *
     * @param group        group which contain players from enter group
     * @param member       player which doesn't meet condition
     * @param htmlCallback HTML callback function used to display fail HTML to player
     */
    private void sendMessage(List<L2PcInstance> group, L2PcInstance member, BiConsumer<L2PcInstance, String> htmlCallback) {
        // Send HTML message if condition has any
        final String html = _parameters.getString("html", null);
        if ((html != null) && (htmlCallback != null)) {
            // Send HTML only to player who make request to enter
            htmlCallback.accept(group.get(0), html);
            // Stop execution if only one message is allowed
            if (!_showMessageAndHtml) {
                return;
            }
        }

        // Send text message if condition has any
        final String message = _parameters.getString("message", null);
        if (message != null) {
            if (_leaderOnly) {
                member.sendMessage(message);
            } else {
                group.forEach(p -> p.sendMessage(message));
            }
            return;
        }

        // Send system message if condition has any
        if (_systemMsg != null) {
            final SystemMessage msg = SystemMessage.getSystemMessage(_systemMsg);
            if (_systemMsgParams != null) {
                _systemMsgParams.accept(msg, member);
            }

            if (_leaderOnly) {
                member.sendPacket(msg);
            } else {
                group.forEach(p -> p.sendPacket(msg));
            }
        }
    }

    /**
     * Apply condition effect to enter player group.<br>
     * This method is called when all instance conditions are met.
     *
     * @param group group of players which wants to enter into instance
     */
    public void applyEffect(List<L2PcInstance> group) {
        for (L2PcInstance member : group) {
            onSuccess(member);
            if (_leaderOnly) {
                break;
            }
        }
    }

    /**
     * Set system message which should be send to player when validation fails.
     *
     * @param msg identification code of system message
     */
    protected void setSystemMessage(SystemMessageId msg) {
        _systemMsg = msg;
    }

    /**
     * Set system message which should be send to player when validation fails.<br>
     * This method also allows set system message parameters like <i>player name, item name, ...</i>.
     *
     * @param msg    identification code of system message
     * @param params function which set parameters to system message
     */
    protected void setSystemMessage(SystemMessageId msg, BiConsumer<SystemMessage, L2PcInstance> params) {
        setSystemMessage(msg);
        _systemMsgParams = params;
    }

    /**
     * Test condition for player.<br>
     * <i>Calls {@link Condition#test(L2PcInstance, L2Npc)} by default.</i>
     *
     * @param player instance of player which should meet condition
     * @param npc    instance of NPC used to enter into instance
     * @param group  group of players which wants to enter
     * @return {@code true} on success, {@code false} on fail
     */
    protected boolean test(L2PcInstance player, L2Npc npc, List<L2PcInstance> group) {
        return test(player, npc);
    }

    /**
     * Test condition for player.
     *
     * @param player instance of player which should meet condition
     * @param npc    instance of NPC used to enter into instance
     * @return {@code true} on success, {@code false} on fail
     */
    protected boolean test(L2PcInstance player, L2Npc npc) {
        return true;
    }

    /**
     * Apply condition effects to player.<br>
     * This method is called when all instance conditions are met.
     *
     * @param player player which should be affected
     */
    protected void onSuccess(L2PcInstance player) {

    }
}