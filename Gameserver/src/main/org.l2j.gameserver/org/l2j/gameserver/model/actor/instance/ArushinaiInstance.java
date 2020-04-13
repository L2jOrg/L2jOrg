/*
package org.l2j.gameserver.model.actor.instance;

import java.util.StringTokenizer;

*/
/*import l2s.commons.collections.MultiValueSet;
import l2s.commons.util.Rnd;
import l2s.gameserver.instancemanager.ServerVariables;
import l2s.gameserver.model.Party;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.templates.npc.NpcTemplate;

import manager.HeavenlyRift;*//*

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.instancemanager.HeavenlyRift;
import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.util.GameUtils;

*/
/**
 * @reworked by Bonux
 *//*

public class ArushinaiInstance extends Npc
{
	private static final long serialVersionUID = 1L;

	public ArushinaiInstance(NpcTemplate template)
	{
		super(template);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		StringTokenizer st = new StringTokenizer(command, "_");
		String cmd = st.nextToken();
		if(cmd.equals("proceed"))
		{
			if(!player.isGM())
			{
				Party party = player.getParty();
				if(party == null)
				{
					// TODO: Add message.
					player.teleToLocation(114264, 13352, -5104);
					return;
				}
				if(!party.isLeader(player))
				{
					// TODO: Add message.
					return;
				}
			}
			if(0 == 0) //if(ServerVariables.getInt("heavenly_rift_complete", 0) == 0)
			{
				int riftLevel = Rnd.get(1, 3);
				//ServerVariables.set("heavenly_rift_level", riftLevel);
				//ServerVariables.set("heavenly_rift_complete", 4);
				switch(riftLevel) 
				{
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
				// showBusyWindow(player); // Try use public void showChatWindow(Player player, String filename) from NPC class
			}
		}
		else if(cmd.equals("finish"))
		{
			if(player.isInParty())
			{
				Party party = player.getParty();
				if(party.isLeader(player))
				{
					for(Player partyMember : party.getMembers())
					{
						if(!GameUtils.checkIfInRange(1000, player, partyMember,true))
						{
							*/
/*ServerPacket sm = new SystemMessage(SystemMessageId.C1_IS_IN_A_LOCATION_WHICH_CANNOT_BE_ENTERED_THEREFORE_IT_CANNOT_BE_PROCESSED);

							sm.addName(partyMember);
							party.broadcastToPartyMembers(player, sm);*//*

							return;
						}
					}

					//ServerVariables.set("heavenly_rift_reward", 0);
					//ServerVariables.set("heavenly_rift_complete", 0);
					for(Player partyMember : party.getMembers())
						partyMember.teleToLocation(114264, 13352, -5104);
				}
				else
				{
					// TODO: Add message.
				}
			}
			else
			{
				if(player.isGM())
				{
					//ServerVariables.set("heavenly_rift_complete", 0);
					player.teleToLocation(114264, 13352, -5104);
				}
				else
				{
					// TODO: Add message.
				}
			}
		}		
		else
			super.onBypassFeedback(player, command);
	}


	public String getHtmlPath(int val, Player player)
	{
		String filename = "";
		if(val == 1)
			filename = getId() + "-1.htm";
		*/
/*else if(ServerVariables.getInt("heavenly_rift_complete", 0) > 0)
			filename = getId() + "-2.htm";*//*

		else
			filename = getId() + ".htm";
		return filename;
	}
}*/
