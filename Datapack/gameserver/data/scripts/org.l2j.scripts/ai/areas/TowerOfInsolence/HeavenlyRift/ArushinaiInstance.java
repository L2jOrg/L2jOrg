package ai.areas.TowerOfInsolence.HeavenlyRift;

import java.util.StringTokenizer;
import java.util.concurrent.atomic.AtomicInteger;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.instancemanager.GlobalVariablesManager;
import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.model.actor.instance.Folk;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.world.World;

/**
 * @reworked by Bonux
 */
public class ArushinaiInstance extends Folk
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
			if(GlobalVariablesManager.getInstance().getInt("heavenly_rift_complete", 0) == 0)
			{
				int riftLevel = Rnd.get(1, 3);
				GlobalVariablesManager.getInstance().set("heavenly_rift_level", riftLevel);
				GlobalVariablesManager.getInstance().set("heavenly_rift_complete", 4);
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
				// TODO: busy window
				//showBusyWindow(player);
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
						if(!GameUtils.checkIfInRange(1000, player, partyMember, false))
						{
							final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_IN_A_LOCATION_WHICH_CANNOT_BE_ENTERED_THEREFORE_IT_CANNOT_BE_PROCESSED);
							sm.addPcName(partyMember);
							player.sendPacket(sm);
							party.broadcastToPartyMembers(player, sm);
							return;
						}
					}

					GlobalVariablesManager.getInstance().set("heavenly_rift_reward", 0);
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
					GlobalVariablesManager.getInstance().set("heavenly_rift_complete", 0);
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

	@Override
	public String getHtmlPath(int npcId, int val) {
		String filename = "";
		if(val == 1)
			filename = getId() + "-1.htm";
		else if(GlobalVariablesManager.getInstance().getInt("heavenly_rift_complete", 0) > 0)
			filename = getId() + "-2.htm";
		else
			filename = getId() + ".htm";
		return filename;
	}
}