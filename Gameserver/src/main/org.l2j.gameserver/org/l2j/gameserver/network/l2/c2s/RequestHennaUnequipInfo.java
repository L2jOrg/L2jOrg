package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.data.xml.holder.HennaHolder;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.s2c.HennaUnequipInfoPacket;
import org.l2j.gameserver.templates.HennaTemplate;

public class RequestHennaUnequipInfo extends L2GameClientPacket
{
	private int _symbolId;

	/**
	 * format: d
	 */
	@Override
	protected void readImpl()
	{
		_symbolId = readInt();
	}

	@Override
	protected void runImpl()
	{
		Player player = getClient().getActiveChar();
		if(player == null)
			return;

		HennaTemplate henna = HennaHolder.getInstance().getHenna(_symbolId);
		if(henna != null)
			player.sendPacket(new HennaUnequipInfoPacket(henna, player));
	}
}