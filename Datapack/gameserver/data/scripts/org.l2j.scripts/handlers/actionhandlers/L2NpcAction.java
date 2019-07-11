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
package handlers.actionhandlers;

import org.l2j.gameserver.Config;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.geoengine.GeoEngine;
import org.l2j.gameserver.handler.IActionHandler;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.entity.L2Event;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.character.npc.OnNpcFirstTalk;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.MoveToPawn;

public class L2NpcAction implements IActionHandler
{
	/**
	 * Manage actions when a player click on the Npc.<BR>
	 * <BR>
	 * <B><U> Actions on first click on the Npc (Select it)</U> :</B><BR>
	 * <BR>
	 * <li>Set the Npc as target of the Player player (if necessary)</li>
	 * <li>Send a Server->Client packet MyTargetSelected to the Player player (display the select window)</li>
	 * <li>If Npc is autoAttackable, send a Server->Client packet StatusUpdate to the Player in order to update Npc HP bar</li>
	 * <li>Send a Server->Client packet ValidateLocation to correct the Npc position and heading on the client</li><BR>
	 * <BR>
	 * <B><U> Actions on second click on the Npc (Attack it/Intercat with it)</U> :</B><BR>
	 * <BR>
	 * <li>Send a Server->Client packet MyTargetSelected to the Player player (display the select window)</li>
	 * <li>If Npc is autoAttackable, notify the Player AI with AI_INTENTION_ATTACK (after a height verification)</li>
	 * <li>If Npc is NOT autoAttackable, notify the Player AI with AI_INTENTION_INTERACT (after a distance verification) and show message</li><BR>
	 * <BR>
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : Each group of Server->Client packet must be terminated by a ActionFailed packet in order to avoid that client wait an other packet</B></FONT><BR>
	 * <BR>
	 * <B><U> Example of use </U> :</B><BR>
	 * <BR>
	 * <li>Client packet : Action, AttackRequest</li><BR>
	 * <BR>
	 * @param activeChar The Player that start an action on the Npc
	 */
	@Override
	public boolean action(Player activeChar, WorldObject target, boolean interact)
	{
		if (!((Npc) target).canTarget(activeChar))
		{
			return false;
		}
		activeChar.setLastFolkNPC((Npc) target);
		// Check if the Player already target the Npc
		if (target != activeChar.getTarget())
		{
			// Set the target of the Player activeChar
			activeChar.setTarget(target);
			// Check if the activeChar is attackable (without a forced attack)
			if (target.isAutoAttackable(activeChar))
			{
				((Npc) target).getAI(); // wake up ai
			}
		}
		else if (interact)
		{
			// Check if the activeChar is attackable (without a forced attack) and isn't dead
			if (target.isAutoAttackable(activeChar) && !((Creature) target).isAlikeDead())
			{
				// Check if target is in LoS
				if (GeoEngine.getInstance().canSeeTarget(activeChar, target))
				{
					// Set the Player Intention to AI_INTENTION_ATTACK
					activeChar.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
				}
				else
				{
					// Send a Server->Client ActionFailed to the Player in order to avoid that the client wait another packet
					activeChar.sendPacket(ActionFailed.STATIC_PACKET);
				}
			}
			else if (!target.isAutoAttackable(activeChar))
			{
				// Calculate the distance between the Player and the Npc
				if (!((Npc) target).canInteract(activeChar))
				{
					// Notify the Player AI with AI_INTENTION_INTERACT
					activeChar.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, target);
				}
				else
				{
					final Npc npc = (Npc) target;
					if (!activeChar.isSitting()) // Needed for Mystic Tavern Globe
					{
						// Turn NPC to the player.
						activeChar.sendPacket(new MoveToPawn(activeChar, npc, 100));
						if (npc.hasRandomAnimation())
						{
							npc.onRandomAnimation(Rnd.get(8));
						}
					}
					// Open a chat window on client with the text of the Npc
					if (npc.getVariables().getBoolean("eventmob", false))
					{
						L2Event.showEventHtml(activeChar, String.valueOf(target.getObjectId()));
					}
					else
					{
						if (npc.hasListener(EventType.ON_NPC_QUEST_START))
						{
							activeChar.setLastQuestNpcObject(target.getObjectId());
						}
						if (npc.hasListener(EventType.ON_NPC_FIRST_TALK))
						{
							EventDispatcher.getInstance().notifyEventAsync(new OnNpcFirstTalk(npc, activeChar), npc);
						}
						else
						{
							npc.showChatWindow(activeChar);
						}
					}
					if (Config.PLAYER_MOVEMENT_BLOCK_TIME > 0)
					{
						activeChar.updateNotMoveUntil();
					}
				}
			}
		}
		return true;
	}
	
	@Override
	public InstanceType getInstanceType()
	{
		return InstanceType.L2Npc;
	}
}