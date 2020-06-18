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
package org.l2j.gameserver.model.actor.instance;

import org.l2j.gameserver.ai.CreatureAI;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.ai.FriendlyNpcAI;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.character.npc.OnAttackableAttack;
import org.l2j.gameserver.model.events.impl.character.npc.OnAttackableKill;
import org.l2j.gameserver.model.events.impl.character.npc.OnNpcFirstTalk;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.util.GameUtils;

/**
 * @author GKR, Sdw
 */
public class FriendlyNpc extends Attackable {
    private boolean _isAutoAttackable = true;
    private int _baseHateAmount = 1;

    public FriendlyNpc(NpcTemplate template) {
        super(template);
        setInstanceType(InstanceType.FriendlyNpcInstance);
    }

    public int getHateBaseAmount() {
        return _baseHateAmount;
    }

    @Override
    public boolean isAutoAttackable(Creature attacker) {
        return _isAutoAttackable && !GameUtils.isPlayable(attacker) && !(attacker instanceof FriendlyNpc);
    }

    @Override
    public void setAutoAttackable(boolean state) {
        _isAutoAttackable = state;
    }

    @Override
    public void addDamage(Creature attacker, int damage, Skill skill) {
        if (!GameUtils.isPlayable(attacker) && !(attacker instanceof FriendlyNpc)) {
            super.addDamage(attacker, damage, skill);
        }

        if (GameUtils.isAttackable(attacker)) {
            EventDispatcher.getInstance().notifyEventAsync(new OnAttackableAttack(null, this, damage, skill, false), this);
        }
    }

    @Override
    public void addDamageHate(Creature attacker, int damage, int aggro) {
        if (!GameUtils.isPlayable(attacker) && !(attacker instanceof FriendlyNpc)) {
            super.addDamageHate(attacker, damage, aggro);
        }
    }

    @Override
    public boolean doDie(Creature killer) {
        // Kill the Folk (the corpse disappeared after 7 seconds)
        if (!super.doDie(killer)) {
            return false;
        }

        if (GameUtils.isAttackable(killer)) {
            // Delayed notification
            EventDispatcher.getInstance().notifyEventAsync(new OnAttackableKill(null, this, false), this);
        }
        return true;
    }

    @Override
    public void onAction(Player player, boolean interact) {
        if (!canTarget(player)) {
            return;
        }

        // Check if the Player already target the Guard
        if (getObjectId() != player.getTargetId()) {
            // Set the target of the Player player
            player.setTarget(this);
        } else if (interact) {
            // Calculate the distance between the Player and the Folk
            if (!canInteract(player)) {
                // Set the Player Intention to AI_INTENTION_INTERACT
                player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, this);
            } else {
                player.setLastFolkNPC(this);

                // Open a chat window on client with the text of the Guard
                if (hasListener(EventType.ON_NPC_QUEST_START)) {
                    player.setLastQuestNpcObject(getObjectId());
                }

                if (hasListener(EventType.ON_NPC_FIRST_TALK)) {
                    EventDispatcher.getInstance().notifyEventAsync(new OnNpcFirstTalk(this, player), this);
                } else {
                    showChatWindow(player, 0);
                }
            }
        }
        // Send a Server->Client ActionFailed to the Player in order to avoid that the client wait another packet
        player.sendPacket(ActionFailed.STATIC_PACKET);
    }

    @Override
    public String getHtmlPath(int npcId, int val) {
        String pom = "";
        if (val == 0) {
            pom = Integer.toString(npcId);
        } else {
            pom = npcId + "-" + val;
        }
        return "data/html/default/" + pom + ".htm";
    }

    @Override
    protected CreatureAI initAI() {
        return new FriendlyNpcAI(this);
    }
}
