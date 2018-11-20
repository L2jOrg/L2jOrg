package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.ObservableArena;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.utils.NpcUtils;

/**
 * @author VISTALL
 * @date 0:20/09.04.2011
 */
public class RequestOlympiadMatchList extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
		// trigger
	}

	@Override
	protected void runImpl()
	{
		Player player = getClient().getActiveChar();
		if(player == null)
			return;

		if(NpcUtils.canPassPacket(player, this) != null)
			return;

		ObservableArena arena = player.getObservableArena();
		if(arena == null)
			return;

		arena.showObservableArenasList(player);
	}
}