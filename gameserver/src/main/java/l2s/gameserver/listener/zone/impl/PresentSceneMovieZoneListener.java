package l2s.gameserver.listener.zone.impl;

import l2s.commons.lang.reference.HardReference;
import l2s.commons.threading.RunnableImpl;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Zone;
import l2s.gameserver.network.l2.components.SceneMovie;

/**
 * @author Bonux
 */
public class PresentSceneMovieZoneListener implements OnZoneEnterLeaveListener
{
	private static class ShowMovie extends RunnableImpl
	{
		private final SceneMovie _sceneMovie;
		private final HardReference<Player> _playerRef;

		public ShowMovie(SceneMovie sceneMovie, Player player)
		{
			_sceneMovie = sceneMovie;
			_playerRef = player.getRef();
		}

		@Override
		public void runImpl()
		{
			Player player = _playerRef.get();
			if(player == null)
				return;

			player.startScenePlayer(_sceneMovie);
		}
	}

	private final SceneMovie _sceneMovie;

	public PresentSceneMovieZoneListener(SceneMovie sceneMovie)
	{
		_sceneMovie = sceneMovie;
	}

	@Override
	public void onZoneEnter(Zone zone, Creature actor)
	{
		if(!actor.isPlayer())
			return;

		Player player = actor.getPlayer();
		if(player == null)
			return;

		String var = "@" + _sceneMovie.toString().toLowerCase();
		if(!player.getVarBoolean(var))
		{
			scheduleShowMovie(_sceneMovie, player);
			player.setVar(var, "true", -1);
		}
	}

	@Override
	public void onZoneLeave(Zone zone, Creature cha)
	{}

	public static void scheduleShowMovie(SceneMovie sceneMovie, Player player)
	{
		ThreadPoolManager.getInstance().schedule(new ShowMovie(sceneMovie, player), 1000L);
	}
}