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

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.instancemanager.GlobalVariablesManager;
import org.l2j.gameserver.model.HeavenlyRift;
import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.util.GameUtils;

import java.util.StringTokenizer;

/**
 * @reworked by Thoss
 */
public class Arushinai extends Folk {
	public Arushinai(NpcTemplate template)
	{
		super(template);
	}

	@Override
	public void onBypassFeedback(Player player, String command) {
		StringTokenizer st = new StringTokenizer(command, "_");
		String cmd = st.nextToken();
		if(cmd.equals("proceed")) {
			if(!player.isGM()) {
				Party party = player.getParty();
				if(party == null) {
					player.sendPacket(SystemMessageId.YOU_ARE_NOT_CURRENTLY_IN_A_PARTY_SO_YOU_CANNOT_ENTER);
					player.teleToLocation(114264, 13352, -5104); // Back to Dimensional Vortex
					return;
				}
				if(!party.isLeader(player)) {
					player.sendPacket(SystemMessageId.ONLY_A_PARTY_LEADER_CAN_MAKE_THE_REQUEST_TO_ENTER);
					return;
				}
			}
			if(GlobalVariablesManager.getInstance().getInt("heavenly_rift_complete", 0) == 0) {
				int riftLevel = Rnd.get(1, 3);
				GlobalVariablesManager.getInstance().set("heavenly_rift_level", riftLevel);
				GlobalVariablesManager.getInstance().set("heavenly_rift_complete", 4);
				switch(riftLevel) {
					case 1:
						HeavenlyRift.startEvent20Bomb(player);
						break;
					case 2:
						HeavenlyRift.startEventTower(player);
						break;
					case 3:
						HeavenlyRift.startEvent40Angels(player);
						break;
					default:
						break;
				}
			}	
			else {
				showBusyWindow(player, this);
			}
		} else if(cmd.equals("finish")) {
			if(player.isInParty())
			{
				Party party = player.getParty();
				if(party.isLeader(player)) {
					for(Player partyMember : party.getMembers()) {
						if(!GameUtils.checkIfInRange(1000, player, partyMember, false)) {
							final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_IN_A_LOCATION_WHICH_CANNOT_BE_ENTERED_THEREFORE_IT_CANNOT_BE_PROCESSED);
							sm.addPcName(partyMember);
							player.sendPacket(sm);
							party.broadcastToPartyMembers(player, sm);
							return;
						}
					}

					GlobalVariablesManager.getInstance().set("heavenly_rift_reward", 0);
					// GlobalVariablesManager.getInstance().set("heavenly_rift_complete", 0);
					for(Player partyMember : party.getMembers())
						partyMember.teleToLocation(114264, 13352, -5104);
				}
				else {
					player.sendPacket(SystemMessageId.YOU_CANNOT_PROCEED_AS_YOU_ARE_NOT_A_PARTY_LEADER);
				}
			}
			else {
				if(player.isGM()) {
					GlobalVariablesManager.getInstance().set("heavenly_rift_complete", 0);
					player.teleToLocation(114264, 13352, -5104);
				}
				else {
					player.sendPacket(SystemMessageId.YOU_ARE_NOT_IN_A_PARTY);
					return;
				}
			}
		}		
		else
			super.onBypassFeedback(player, command);
	}

	public static void showBusyWindow(Player player, Npc npc) {
		player.sendPacket(new NpcHtmlMessage("<html><body>" + npc.getName() + ":<br>Rift are already in progress! Come back later.</body></html>"));
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}

	@Override
	public String getHtmlPath(int npcId, int val) {
		String filename = "data/html/default/";
		if(val == 1)
			filename += npcId + "-1.htm";
		else if(GlobalVariablesManager.getInstance().getInt("heavenly_rift_complete", 0) > 0)
			filename += npcId + "-2.htm";
		else
			filename += npcId + ".htm";
		return filename;
	}
}
