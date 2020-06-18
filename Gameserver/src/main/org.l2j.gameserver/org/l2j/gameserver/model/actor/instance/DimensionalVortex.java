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

import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.instancemanager.GlobalVariablesManager;
import org.l2j.gameserver.model.HeavenlyRift;
import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.util.GameUtils;

import java.util.StringTokenizer;

/**
 * @reworked by Thoss
 */
public class DimensionalVortex extends Folk {
	private static final int ITEM_ID = 49759;

	public DimensionalVortex(NpcTemplate template)
	{
		super(template);
	}

	@Override
	public void onBypassFeedback(Player player, String command) {
		StringTokenizer st = new StringTokenizer(command, "_");
		String cmd = st.nextToken();
		if(cmd.equals("tryenter")) {
			if(player.getInventory().getInventoryItemCount(ITEM_ID, -1) >= 1) {
				if(isBusy()) {
					Arushinai.showBusyWindow(player, this);
					return;
				}

				if(player.isGM()) {
					setBusy(true);

					player.destroyItemByItemId("Rift", ITEM_ID, 1, this, true);

					GlobalVariablesManager.getInstance().set("heavenly_rift_complete", 0);
					GlobalVariablesManager.getInstance().set("heavenly_rift_level", 0);

					player.teleToLocation(112685, 13362, 10966);

					ThreadPool.schedule(new HeavenlyRift.ClearZoneTask(this), 180000);
					return;
				}
				
				if(!player.isInParty()) {
					player.sendPacket(SystemMessageId.YOU_ARE_NOT_CURRENTLY_IN_A_PARTY_SO_YOU_CANNOT_ENTER);
					return;
				}

				Party party = player.getParty();
				if(!party.isLeader(player)) {
					player.sendPacket(SystemMessageId.ONLY_A_PARTY_LEADER_CAN_MAKE_THE_REQUEST_TO_ENTER);
					return;
				}
				
				for(Player partyMember : party.getMembers()) {
					if(!GameUtils.checkIfInRange(1000, player, partyMember, false)) {
						SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_IN_A_LOCATION_WHICH_CANNOT_BE_ENTERED_THEREFORE_IT_CANNOT_BE_PROCESSED);
						sm.addPcName(partyMember);
						player.sendPacket(sm);
						party.broadcastToPartyMembers(player, sm);
						return;
					}
				}

				setBusy(true);

				player.destroyItemByItemId("Rift", ITEM_ID, 1, this, true);

				GlobalVariablesManager.getInstance().set("heavenly_rift_complete", 0);
				GlobalVariablesManager.getInstance().set("heavenly_rift_level", 0);

				for(Player partyMember : party.getMembers())
					partyMember.teleToLocation(112685, 13362, 10966);

				ThreadPool.schedule(new HeavenlyRift.ClearZoneTask(this), 20 * 60 * 1000);
			}
			else {
				showChatWindow(player, "data/html/default/" + getId() + "-3.htm");
			}
		}	
		else if(cmd.equals("exchange")) {
			long count_have = player.getInventory().getInventoryItemCount(49767, -1);
			if(count_have < 10) { //exchange ratio 10:1
				showChatWindow(player, "data/html/default/" + getId() + "-2.htm");
				return;	
			}

			if(count_have % 10 != 0) //odd
				count_have -= count_have % 10;

			long to_give = count_have / 10;

			player.destroyItemByItemId("Rift", 49767, count_have, this, true);
			player.addItem("Rift", 49759, to_give, this, true);
		}
		else
			super.onBypassFeedback(player, command);
	}
}
