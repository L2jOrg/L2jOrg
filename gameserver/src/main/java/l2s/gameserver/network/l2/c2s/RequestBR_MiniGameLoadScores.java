package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.Config;
import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.s2c.ExBR_MiniGameLoadScores;

/**
 * @author VISTALL
 * @date  19:57:41/25.05.2010
 */
public class RequestBR_MiniGameLoadScores extends L2GameClientPacket
{
	@Override
	protected void readImpl() throws Exception
	{
		//
	}

	@Override
	protected void runImpl() throws Exception
	{
		Player player = getClient().getActiveChar();
		if(player == null || !Config.EX_JAPAN_MINIGAME)
			return;

		player.sendPacket(new ExBR_MiniGameLoadScores(player));
	}
}