package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.entity.Hero;

import java.nio.ByteBuffer;

/**
 * Format chS
 * c (id) 0xD0
 * h (subid) 0x0C
 * S the hero's words :)
 *
 */
public class RequestWriteHeroWords extends L2GameClientPacket
{
	private String _heroWords;

	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		_heroWords = readString(buffer);
	}

	@Override
	protected void runImpl()
	{
		final Player player = client.getActiveChar();
		if(player == null || !player.isHero())
			return;

		if(_heroWords == null || _heroWords.length() > 300)
			return;

		Hero.getInstance().setHeroMessage(player.getObjectId(), _heroWords);
	}
}