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

import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.character.npc.OnNpcFirstTalk;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.world.World;

/**
 * This class manages all Guards in the world. It inherits all methods from Attackable and adds some more such as tracking PK and aggressive Monster.
 */
public class Guard extends Attackable {
    /**
     * Constructor of Guard (use Creature and Folk constructor).<br>
     * <B><U> Actions</U> :</B>
     * <ul>
     * <li>Call the Creature constructor to set the _template of the Guard (copy skills from template to object and link _calculators to NPC_STD_CALCULATOR)</li>
     * <li>Set the name of the Guard</li>
     * <li>Create a RandomAnimation Task that will be launched after the calculated delay if the server allow it</li>
     * </ul>
     *
     * @param template to apply to the NPC
     */
    public Guard(NpcTemplate template) {
        super(template);
        setInstanceType(InstanceType.L2GuardInstance);
    }

    @Override
    public boolean isAutoAttackable(Creature attacker) {
        if (GameUtils.isMonster(attacker)) {
            return true;
        }
        return super.isAutoAttackable(attacker);
    }

    @Override
    public void addDamage(Creature attacker, int damage, Skill skill) {
        super.addDamage(attacker, damage, skill);
        getAI().startFollow(attacker);
        addDamageHate(attacker, 0, 10);
        World.getInstance().forEachVisibleObjectInRange(this, Guard.class, 500, guard ->
        {
            guard.getAI().startFollow(attacker);
            guard.addDamageHate(attacker, 0, 10);
        });
    }

    /**
     * Set the home location of its Guard.
     */
    @Override
    public void onSpawn() {
        super.onSpawn();
        setRandomWalking(getTemplate().isRandomWalkEnabled());
        if(getWorldRegion().isActive()) {
            getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
        }
    }

    /**
     * Return the pathfile of the selected HTML file in function of the Guard Identifier and of the page number.<br>
     * <B><U> Format of the pathfile </U> :</B>
     * <ul>
     * <li>if page number = 0 : <B>data/html/guard/12006.htm</B> (npcId-page number)</li>
     * <li>if page number > 0 : <B>data/html/guard/12006-1.htm</B> (npcId-page number)</li>
     * </ul>
     *
     * @param npcId The Identifier of the Folk whose text must be display
     * @param val   The number of the page to display
     */
    @Override
    public String getHtmlPath(int npcId, int val) {
        String pom = "";
        if (val == 0) {
            pom = Integer.toString(npcId);
        } else {
            pom = npcId + "-" + val;
        }
        return "data/html/guard/" + pom + ".htm";
    }

    /**
     * Manage actions when a player click on the Guard.<br>
     * <B><U> Actions on first click on the Guard (Select it)</U> :</B>
     * <ul>
     * <li>Set the Guard as target of the Player player (if necessary)</li>
     * <li>Send a Server->Client packet MyTargetSelected to the Player player (display the select window)</li>
     * <li>Set the Player Intention to AI_INTENTION_IDLE</li>
     * <li>Send a Server->Client packet ValidateLocation to correct the Guard position and heading on the client</li>
     * </ul>
     * <B><U> Actions on second click on the Guard (Attack it/Interact with it)</U> :</B>
     * <ul>
     * <li>If Player is in the _aggroList of the Guard, set the Player Intention to AI_INTENTION_ATTACK</li>
     * <li>If Player is NOT in the _aggroList of the Guard, set the Player Intention to AI_INTENTION_INTERACT (after a distance verification) and show message</li>
     * </ul>
     * <B><U> Example of use </U> :</B>
     * <ul>
     * <li>Client packet : Action, AttackRequest</li>
     * </ul>
     *
     * @param player The Player that start an action on the Guard
     */
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
            // Check if the Player is in the _aggroList of the Guard
            if (containsTarget(player)) {
                // Set the Player Intention to AI_INTENTION_ATTACK
                player.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, this);
            } else {
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
        }
        // Send a Server->Client ActionFailed to the Player in order to avoid that the client wait another packet
        player.sendPacket(ActionFailed.STATIC_PACKET);
    }
}
