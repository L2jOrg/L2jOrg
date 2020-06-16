package ai.areas.TowerOfInsolence.HeavenlyRift;

import org.l2j.gameserver.instancemanager.GlobalVariablesManager;
import org.l2j.gameserver.model.actor.instance.Folk;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;

import java.util.StringTokenizer;

/**
 * @reworked by Thoss
 */
public class TowerInstance extends Folk
{
	private static int[] ITEM_REWARD = { 49764, 49765 };
	
	public TowerInstance(NpcTemplate template) {
		super(template);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		StringTokenizer st = new StringTokenizer(command, "_");
		String cmd = st.nextToken();
		if(cmd.equals("getreward"))
		{
			if(GlobalVariablesManager.getInstance().getInt("heavenly_rift_reward", 0) == 1)
			{
				GlobalVariablesManager.getInstance().set("heavenly_rift_reward", 0);
				if(player.isGM())
				{
					for(int r : ITEM_REWARD)
						player.addItem("TowerInstance", r, 1, this, true);
				}
				else
				{
					for(Player partyMember : player.getParty().getMembers())
					{
						for(int r : ITEM_REWARD)
							partyMember.addItem("TowerInstance", r, 1, this, true);
					}
				}	
				showChatWindow(player, "default/" + getId() + "-3.htm");
			}	
		}	
		else
			super.onBypassFeedback(player, command);
	}

	@Override
	public String getHtmlPath(int npcId, int val) {
	{
		String filename = "";

		if(!getActingPlayer().isGM() && (!getActingPlayer().isInParty() || !getActingPlayer().getParty().isLeader(getActingPlayer())))
			filename = getId() + "-4.htm";
		else if(!isDead() && GlobalVariablesManager.getInstance().getInt("heavenly_rift_complete", 0) == 2)
		{
			if(GlobalVariablesManager.getInstance().getInt("heavenly_rift_reward", 0) == 1)
				filename = getId() + ".htm";
			else
				filename = getId() + "-2.htm";
		}
		else
			filename = getId() + "-1.htm";

		return filename;
	}

	// TODO: think about that ...
	/*
	@Override
	public boolean isDebuffImmune()
	{
		return false;
	}*/
}