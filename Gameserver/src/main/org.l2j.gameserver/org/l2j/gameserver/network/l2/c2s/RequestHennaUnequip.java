package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.actor.instances.player.Henna;
import org.l2j.gameserver.network.l2.components.SystemMsg;

import java.nio.ByteBuffer;

public class RequestHennaUnequip extends L2GameClientPacket
{
	private int _symbolId;

	/**
	 * format: d
     * @param buffer
     */
	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		_symbolId = buffer.getInt();
	}

	@Override
	protected void runImpl()
	{
		Player player = client.getActiveChar();
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