package npc.model.heavenlyrift;

import java.util.StringTokenizer;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.instancemanager.ServerVariables;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.DefenderInstance;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.ItemFunctions;

/**
 * @reworked by Bonux
 */
public class TowerInstance extends DefenderInstance
{
	private static final long serialVersionUID = 1L;

	private static int[] ITEM_REWARD = { 49764, 49765 };
	
	public TowerInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		StringTokenizer st = new StringTokenizer(command, "_");
		String cmd = st.nextToken();
		if(cmd.equals("getreward"))
		{
			if(ServerVariables.getInt("heavenly_rift_reward", 0) == 1)
			{
				ServerVariables.set("heavenly_rift_reward", 0);
				if(player.isGM())
				{
					for(int r : ITEM_REWARD)
						ItemFunctions.addItem(player, r, 1);					
				}
				else
				{
					for(Player partyMember : player.getParty().getPartyMembers())
					{
						for(int r : ITEM_REWARD)
							ItemFunctions.addItem(partyMember, r, 1);
					}
				}	
				showChatWindow(player, "default/" + getNpcId() + "-3.htm", false);
			}	
		}	
		else
			super.onBypassFeedback(player, command);
	}

	@Override
	public String getHtmlFilename(int val, Player player)
	{
		String filename = "";

		if(!player.isGM() && (!player.isInParty() || !player.getParty().isLeader(player)))
			filename = getNpcId() + "-4.htm";
		else if(!isDead() && ServerVariables.getInt("heavenly_rift_complete", 0) == 2)
		{
			if(ServerVariables.getInt("heavenly_rift_reward", 0) == 1)
				filename = getNpcId() + ".htm";
			else
				filename = getNpcId() + "-2.htm";
		}
		else
			filename = getNpcId() + "-1.htm";

		return filename;
	}

	@Override
	public boolean isDebuffImmune()
	{
		return false;
	}
}