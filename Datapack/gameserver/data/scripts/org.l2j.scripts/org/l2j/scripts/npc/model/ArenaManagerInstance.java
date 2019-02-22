package org.l2j.scripts.npc.model;

import org.l2j.commons.collections.MultiValueSet;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.Zone.ZoneType;
import org.l2j.gameserver.model.instances.NpcInstance;
import org.l2j.gameserver.network.l2.components.SystemMsg;
import org.l2j.gameserver.network.l2.s2c.SystemMessagePacket;
import org.l2j.gameserver.templates.npc.NpcTemplate;

import java.util.StringTokenizer;

/**
 * @author Evil_dnk
 * reworked by Bonux
**/
public class ArenaManagerInstance extends NpcInstance
{
	public ArenaManagerInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		StringTokenizer st = new StringTokenizer(command, "_");
		String cmd = st.nextToken();
		if(cmd.equalsIgnoreCase("recovery"))
		{
			if(!st.hasMoreTokens())
				return;

			if(player.isInZone(ZoneType.battle_zone))
				return;

			final int neededmoney = 1000;
			final long currentmoney = player.getAdena();
			if(neededmoney > currentmoney)
			{
				player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
				return;
			}

			player.reduceAdena(neededmoney, true);

			String cmd2 = st.nextToken();
			if(cmd2.equalsIgnoreCase("cp"))
			{
				player.setCurrentCp(player.getMaxCp());
				player.sendPacket(new SystemMessagePacket(SystemMsg.S1_CP_HAS_BEEN_RESTORED).addName(player));
			}
			else if(cmd2.equalsIgnoreCase("hp"))
			{
				player.setCurrentHp(player.getMaxHp(), false);
				player.sendPacket(new SystemMessagePacket(SystemMsg.S1_HP_HAS_BEEN_RESTORED).addName(player));
			}
		}
		else
			super.onBypassFeedback(player, command);
	}
}
