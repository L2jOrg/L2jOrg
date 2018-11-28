package npc.model.residences.castle;

import org.l2j.commons.collections.MultiValueSet;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.entity.residence.Castle;
import org.l2j.gameserver.model.instances.NpcInstance;
import org.l2j.gameserver.model.pledge.Clan;
import org.l2j.gameserver.network.l2.components.NpcString;
import org.l2j.gameserver.templates.npc.NpcTemplate;
import org.l2j.gameserver.utils.Functions;

import java.util.StringTokenizer;

/**
 * @author Bonux
**/
public class RevolutionaryInstance extends NpcInstance
{
	public RevolutionaryInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public String getHtmlDir(String filename, Player player)
	{
		return "castle/revolutionary/";
	}

	@Override
	public String getHtmlFilename(int val, Player player)
	{
		if(val == 0)
		{
			Castle castle = getCastle(player);
			if(castle == null || castle.getOwnerId() == 0)
				return "no_clan.htm";
		}
		return super.getHtmlFilename(val, player);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		StringTokenizer st = new StringTokenizer(command);
		String cmd = st.nextToken();
		if(cmd.equals("take_bundle")) // TODO [Bonux]
		{
			showChatWindow(player, "castle/revolutionary/no_bundle.htm", false);
		}
		else
			super.onBypassFeedback(player, command);
	}

	@Override
	public void showChatWindow(Player player, int val, boolean firstTalk, Object... arg)
	{
		Castle castle = getCastle(player);
		Clan clan = (castle == null ? null : castle.getOwner());
		if(clan != null)
		{
			if(val == 0)
				Functions.npcSayToPlayer(this, player, NpcString.WE_WILL_EXECUTE_OUR_PLAN_ON_SUNDAY_NIGHT_IF_YOU_JOIN_US_I_WILL_GIVE_YOU_A_SUBSTANTIAL_REWARD);
		}

		super.showChatWindow(player, val, firstTalk, arg);
	}
}