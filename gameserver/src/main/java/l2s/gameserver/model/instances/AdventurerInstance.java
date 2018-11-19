package l2s.gameserver.model.instances;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.instancemanager.RaidBossSpawnManager;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Spawner;
import l2s.gameserver.network.l2.components.CustomMessage;
import l2s.gameserver.network.l2.s2c.ExShowQuestInfoPacket;
import l2s.gameserver.network.l2.s2c.RadarControlPacket;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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