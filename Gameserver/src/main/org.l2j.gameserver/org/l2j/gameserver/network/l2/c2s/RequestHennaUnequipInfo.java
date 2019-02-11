package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.data.xml.holder.HennaHolder;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.s2c.HennaUnequipInfoPacket;
import org.l2j.gameserver.templates.HennaTemplate;

import java.nio.ByteBuffer;

public class RequestHennaUnequipInfo extends L2GameClientPacket
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

		HennaTemplate henna = HennaHolder.getInstance().getHenna(_symbolId);
		if(henna != null)
			player.sendPacket(new HennaUnequipInfoPacket(henna, player));
	}
}