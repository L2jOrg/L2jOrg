package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.components.SceneMovie;
import l2s.gameserver.network.l2.s2c.ExStopScenePlayerPacket;

/**
 * @author Bonux
**/
public final class RequestExEscapeScene extends L2GameClientPacket
{
	protected void readImpl()
	{
		//
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		if(!activeChar.isInMovie())
		{
			activeChar.sendActionFailed();
			return;
		}

		SceneMovie movie = SceneMovie.getMovie(activeChar.getMovieId());
		if(movie == null || !movie.isCancellable())
		{
			activeChar.sendActionFailed();
			return;
		}

		activeChar.endScenePlayer();
		activeChar.sendPacket(new ExStopScenePlayerPacket(movie.getId()));
	}
}