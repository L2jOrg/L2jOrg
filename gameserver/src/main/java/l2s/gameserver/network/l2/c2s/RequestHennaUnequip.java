package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.instances.player.Henna;
import l2s.gameserver.network.l2.components.SystemMsg;

public class RequestHennaUnequip extends L2GameClientPacket
{
	private int _symbolId;

	/**
	 * format: d
	 */
	@Override
	protected void readImpl()
	{
		_symbolId = readD();
	}

	@Override
	protected void runImpl()
	{
		Player player = getClient().getActiveChar();
		if(player == null)
			return;

		Henna henna = player.getHennaList().get(_symbolId);
		if(henna == null)
			return;

		long removePrice = henna.getTemplate().getRemovePrice();
		if(removePrice > 0 && !player.reduceAdena(removePrice))
		{
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			return;
		}

		if(player.getHennaList().remove(henna))
			player.sendPacket(SystemMsg.THE_SYMBOL_HAS_BEEN_DELETED);
	}
}