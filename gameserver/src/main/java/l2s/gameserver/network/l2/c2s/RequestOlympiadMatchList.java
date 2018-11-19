package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.ObservableArena;
import l2s.gameserver.model.Player;
import l2s.gameserver.utils.NpcUtils;

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