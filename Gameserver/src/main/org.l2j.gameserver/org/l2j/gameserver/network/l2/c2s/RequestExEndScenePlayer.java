package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;

import java.nio.ByteBuffer;

public class RequestExEndScenePlayer extends L2GameClientPacket
{
	private int _movieId;

	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		_movieId = buffer.getInt();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = client.getActiveChar();
		if(activeChar == null)
			return;

		if(!activeChar.isInMovie() || activeChar.getMovieId() != _movieId)
		{
			activeChar.sendActionFailed();
			return;
		}

		activeChar.endScenePlayer();
	}
}