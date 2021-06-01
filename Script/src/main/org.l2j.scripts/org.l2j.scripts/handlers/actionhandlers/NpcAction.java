/*
 * Copyright © 2019-2021 L2JOrg
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
package org.l2j.scripts.handlers.actionhandlers;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.engine.geo.GeoEngine;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.handler.IActionHandler;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.entity.Event;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.character.npc.OnNpcFirstTalk;
import org.l2j.gameserver.network.serverpackets.MoveToPawn;
import org.l2j.gameserver.settings.CharacterSettings;

/**
 * @author JoeAlisson
 */
public class NpcAction implements IActionHandler
{
	/**
	 * Manage actions when a player click on the Npc.<BR>
	 * @param player The Player that start an action on the Npc
	 */
	@Override
	public boolean action(Player player, WorldObject target, boolean interact) {
		if(!(target instanceof Npc npc) || !npc.canBeTarget(player)) {
			return false;
		}

		if (npc != player.getTarget()) {
			setPlayerTarget(player, npc);
		}
		else if (interact) {
			interact(player, npc);
		}
		return true;
	}

	private void interact(Player player, Npc npc) {
		if (npc.isAutoAttackable(player) && !npc.isAlikeDead()) {
			attack(player, npc);
		} else if (!npc.isAutoAttackable(player)) {
			if (!npc.canInteract(player)) {
				player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, npc);
			} else {
				moveNextTo(player, npc);
				talk(player, npc);
				if (CharacterSettings.npcTalkBlockingTime() > 0) {
					player.updateNotMoveUntil();
				}
			}
		}
	}

	private void moveNextTo(Player player, Npc npc) {
		if (!player.isSitting()) // Needed for Mystic Tavern Globe
		{
			player.sendPacket(new MoveToPawn(player, npc, 100));
			if (npc.hasRandomAnimation()) {
				npc.onRandomAnimation(Rnd.get(8));
			}
		}
	}

	private void talk(Player player, Npc npc) {
		player.setLastFolkNPC(npc);
		if (npc.hasVariables() && npc.getVariables().getBoolean("eventmob", false)) {
			Event.showEventHtml(player, String.valueOf(npc.getObjectId()));
		} else {
			if (npc.hasListener(EventType.ON_NPC_QUEST_START)) {
				player.setLastQuestNpcObject(npc.getObjectId());
			}
			if (npc.hasListener(EventType.ON_NPC_FIRST_TALK)) {
				EventDispatcher.getInstance().notifyEventAsync(new OnNpcFirstTalk(npc, player), npc);
			} else {
				npc.showChatWindow(player);
			}
		}
	}

	private void attack(Player player, Npc npc) {
		if (GeoEngine.getInstance().canSeeTarget(player, npc)) {
			player.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, npc);
		}
	}

	private void setPlayerTarget(Player player, Npc npc) {
		player.setTarget(npc);
		if (npc.isAutoAttackable(player)) {
			npc.getAI(); // wake up ai
		}
	}

	@Override
	public InstanceType getInstanceType()
	{
		return InstanceType.L2Npc;
	}
}