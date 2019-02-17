package org.l2j.gameserver.tables;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.components.SystemMsg;
import org.l2j.gameserver.network.l2.s2c.L2GameServerPacket;
import org.l2j.gameserver.network.l2.s2c.SystemMessagePacket;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public class GmListTable {

	private static final Queue<Player> GMS = new ConcurrentLinkedQueue<>();

	public static List<Player> getAllVisibleGMs() {
		return GMS.stream().filter(p -> !p.isGMInvisible()).collect(Collectors.toList());
	}

	public static void sendListToPlayer(Player player) {
		if(GMS.isEmpty()) {
			player.sendPacket(SystemMsg.THERE_ARE_NO_GMS_CURRENTLY_VISIBLE_IN_THE_PUBLIC_LIST_AS_THEY_MAY_BE_PERFORMING_OTHER_FUNCTIONS_AT_THE_MOMENT);
		} else {
			player.sendPacket(SystemMsg.GM_LIST);
			GMS.forEach(gm -> player.sendPacket(new SystemMessagePacket(SystemMsg.GM__C1).addName(gm)));
		}
	}

	public static void broadcastToGMs(L2GameServerPacket packet) {
		GMS.forEach(gm -> gm.sendPacket(packet));
	}

	public static void broadcastMessageToGMs(String message) {
		GMS.forEach(gm -> gm.sendMessage(message));
	}

	public static void add(Player activeChar) {
		GMS.add(activeChar);
	}

	public static void remove(Player player) {
		GMS.remove(player);
	}
}