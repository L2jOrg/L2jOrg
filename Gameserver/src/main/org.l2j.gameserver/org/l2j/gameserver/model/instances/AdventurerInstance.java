package org.l2j.gameserver.model.instances;

import org.l2j.commons.collections.MultiValueSet;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.s2c.ExShowQuestInfoPacket;
import org.l2j.gameserver.templates.npc.NpcTemplate;

import java.util.StringTokenizer;

public class AdventurerInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	public AdventurerInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		StringTokenizer st = new StringTokenizer(command, "_");
		String cmd = st.nextToken();
		if(cmd.equals("questlist"))
			player.sendPacket(ExShowQuestInfoPacket.STATIC);
		else
			super.onBypassFeedback(player, command);
	}

	@Override
	public String getHtmlDir(String filename, Player player)
	{
		return "adventurer_guildsman/";
	}
}