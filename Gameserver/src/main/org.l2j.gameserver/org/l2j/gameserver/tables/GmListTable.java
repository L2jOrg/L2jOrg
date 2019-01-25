package org.l2j.gameserver.tables;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.components.SystemMsg;
import org.l2j.gameserver.network.l2.s2c.L2GameServerPacket;
import org.l2j.gameserver.network.l2.s2c.SystemMessagePacket;
import org.l2j.gameserver.settings.ServerSettings;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.l2j.commons.configuration.Configurator.getSettings;
import static org.l2j.gameserver.model.GameObjectsStorage.getPlayers;

public class GmListTable
{
	public static List<Player> getAllGMs()
	{
		List<Player> gmList = new ArrayList<Player>();
		for(Player player : getPlayers())
			if(player.isGM())
				gmList.add(player);

		return gmList;
	}

	public static List<Player> getAllVisibleGMs() {
		List<Player> gmList = new ArrayList<>();
		if(!getSettings(ServerSettings.class).isHideGMStatus()) {
			gmList.addAll(getPlayers().stream().filter(p -> p.isGM() && !p.isGMInvisible()).collect(Collectors.toList()));
		}

		return gmList;
	}

	public static void sendListToPlayer(Player player)
	{
		List<Player> gmList = getAllVisibleGMs();
		if(gmList.isEmpty())
		{
			player.sendPacket(SystemMsg.THERE_ARE_NO_GMS_CURRENTLY_VISIBLE_IN_THE_PUBLIC_LIST_AS_THEY_MAY_BE_PERFORMING_OTHER_FUNCTIONS_AT_THE_MOMENT);
			return;
		}

		player.sendPacket(SystemMsg.GM_LIST);
		for(Player gm : gmList)
			player.sendPacket(new SystemMessagePacket(SystemMsg.GM__C1).addName(gm));
	}

	public static void broadcastToGMs(L2GameServerPacket packet)
	{
		for(Player gm : getAllGMs())
			gm.sendPacket(packet);
	}

	public static void broadcastMessageToGMs(String message)
	{
		for(Player gm : getAllGMs())
			gm.sendMessage(message);
	}
}