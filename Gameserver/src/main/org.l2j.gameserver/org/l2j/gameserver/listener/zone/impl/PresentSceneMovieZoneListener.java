package org.l2j.gameserver.listener.zone.impl;

import org.l2j.commons.lang.reference.HardReference;
import org.l2j.commons.threading.RunnableImpl;
import org.l2j.gameserver.ThreadPoolManager;
import org.l2j.gameserver.listener.zone.OnZoneEnterLeaveListener;
import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.Zone;
import org.l2j.gameserver.network.l2.components.SceneMovie;

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