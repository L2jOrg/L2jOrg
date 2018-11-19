package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.Config;
import l2s.gameserver.instancemanager.games.MiniGameScoreManager;
import l2s.gameserver.model.Player;

/**
 * @author VISTALL
 * @date  19:55:45/25.05.2010
 */
public class RequestBR_MiniGameInsertScore extends L2GameClientPacket
{
	private int _score;

	@Override
	protected void readImpl() throws Exception
	{
		_score = readD();
	}

	@Override
	protected void runImpl() throws Exception
	{
		Player player = getClient().getActiveChar();
		if(player == null || !Config.EX_JAPAN_MINIGAME)
			return;

		MiniGameScoreManager.getInstance().insertScore(player, _score);
	}
}