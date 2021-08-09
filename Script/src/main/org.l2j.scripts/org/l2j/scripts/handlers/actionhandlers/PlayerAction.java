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

import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.engine.geo.GeoEngine;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.handler.IActionHandler;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ActionFailed;

public class PlayerAction implements IActionHandler {
	/**
	 * Manage actions when a player click on this Player.<BR>
	 * <BR>
	 * <B><U> Actions on first click on the Player (Select it)</U> :</B><BR>
	 * <BR>
	 * <li>Set the target of the player</li>
	 * <li>Send a Server->Client packet MyTargetSelected to the player (display the select window)</li><BR>
	 * <BR>
	 * <B><U> Actions on second click on the Player (Follow it/Attack it/Intercat with it)</U> :</B><BR>
	 * <BR>
	 * <li>Send a Server->Client packet MyTargetSelected to the player (display the select window)</li>
	 * <li>If target Player has a Private Store, notify the player AI with AI_INTENTION_INTERACT</li>
	 * <li>If target Player is autoAttackable, notify the player AI with AI_INTENTION_ATTACK</li> <BR>
	 * <BR>
	 * <li>If target Player is NOT autoAttackable, notify the player AI with AI_INTENTION_FOLLOW</li><BR>
	 * <BR>
	 * <B><U> Example of use </U> :</B><BR>
	 * <BR>
	 * <li>Client packet : Action, AttackRequest</li><BR>
	 * <BR>
	 * @param player The player that start an action on target Player
	 */
	@Override
	public boolean action(Player player, WorldObject target, boolean interact) {
		if (player.isControlBlocked()) {
			return false;
		}
		
		if (player.isLockedTarget() && (player.getLockedTarget() != target)) {
			player.sendPacket(SystemMessageId.FAILED_TO_CHANGE_ENMITY);
			return false;
		}

		if(!GeoEngine.getInstance().canSeeTarget(player, target)) {
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return false;
		}
		
		if (player.getTarget() != target) {
			player.setTarget(target);
		} else if (interact) {
			if (((Player) target).getPrivateStoreType() != PrivateStoreType.NONE) {
				player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, target);
			} else if (target.isAutoAttackable(player)) {
				player.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
			} else {
				player.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, target);
			}
		}
		player.onActionRequest();
		return true;
	}
	
	@Override
	public InstanceType getInstanceType()
	{
		return InstanceType.L2PcInstance;
	}
}
