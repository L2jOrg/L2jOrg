package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.instancemanager.games.MiniGameScoreManager;
import org.l2j.gameserver.model.Player;

import java.nio.ByteBuffer;

/**
 * @author VISTALL
 * @date  19:55:45/25.05.2010
 */
public class RequestBR_MiniGameInsertScore extends L2GameClientPacket
{
	private int _score;

	@Override
	protected void readImpl(ByteBuffer buffer) throws Exception
	{
		_score = buffer.getInt();
	}

	@Override
	protected void runImpl() throws Exception
	{
		Player player = client.getActiveChar();
		if(player == null || !Config.EX_JAPAN_MINIGAME)
			return;

		MiniGameScoreManager.getInstance().insertScore(player, _score);
	}
}