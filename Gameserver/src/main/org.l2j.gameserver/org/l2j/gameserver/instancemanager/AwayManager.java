package org.l2j.gameserver.instancemanager;

import org.l2j.commons.lang.reference.HardReference;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.ThreadPoolManager;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.components.CustomMessage;
import org.l2j.gameserver.network.l2.s2c.SetupGaugePacket;
import org.l2j.gameserver.network.l2.s2c.SocialActionPacket;
import io.github.joealisson.primitive.maps.IntObjectMap;
import io.github.joealisson.primitive.maps.impl.HashIntObjectMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AwayManager
{
	protected static final Logger _log = LoggerFactory.getLogger(AwayManager.class);

	private static AwayManager _instance;

	public static final AwayManager getInstance()
	{
		if(_instance == null)
			_instance = new AwayManager();
		return _instance;
	}

	private IntObjectMap<String> _awayTexts = new HashIntObjectMap<String>();

	private AwayManager()
	{
		_log.info("Away Manager: Initializing...");
	}

	public void setAway(Player player, String text)
	{
		player.broadcastPacket(new SocialActionPacket(player.getObjectId(), SocialActionPacket.WAITING));
		player.sendMessage(new CustomMessage("org.l2j.gameserver.instancemanager.AwayManager.setAway").addNumber(Config.AWAY_TIMER));
		player.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
		player.sendPacket(new SetupGaugePacket(player, SetupGaugePacket.Colors.BLUE, Config.AWAY_TIMER * 1000));
		player.block();

		ThreadPoolManager.getInstance().schedule(new PlayerAwayTask(player, text), Config.AWAY_TIMER * 1000);
	}

	public void setBack(Player player)
	{
		player.sendMessage(new CustomMessage("org.l2j.gameserver.instancemanager.AwayManager.setBack").addNumber(Config.BACK_TIMER));
		player.sendPacket(new SetupGaugePacket(player, SetupGaugePacket.Colors.BLUE, Config.BACK_TIMER * 1000));

		ThreadPoolManager.getInstance().schedule(new PlayerBackTask(player), Config.BACK_TIMER * 1000);
	}

	public String getAwayText(Player player)
	{
		return _awayTexts.get(player.getObjectId());
	}

	private class PlayerAwayTask implements Runnable
	{
		private final HardReference<Player> _playerRef;
		private final String _awayText;

		public PlayerAwayTask(Player player, String awayText)
		{
			_playerRef = player.getRef();
			_awayText = awayText;
		}

		@Override
		public void run()
		{
			Player player = _playerRef.get();
			if(player == null)
				return;

			if(player.isAttackingNow() || player.isCastingNow())
			{
				player.unblock();
				player.sendMessage(new CustomMessage("org.l2j.gameserver.instancemanager.AwayManager.PlayerAwayTask.InCombat"));
				return;
			}

			if(_awayText.length() <= 1)
				player.sendMessage(new CustomMessage("org.l2j.gameserver.instancemanager.AwayManager.PlayerAwayTask.NoText"));
			else
				player.sendMessage(new CustomMessage("org.l2j.gameserver.instancemanager.AwayManager.PlayerAwayTask").addString(_awayText));

			_awayTexts.put(player.getObjectId(), _awayText);

			player.setAwayingMode(true);
			player.abortAttack(true, false);
			player.abortCast(true, false);
			player.setTarget(null);
			player.sitDown(null);
			player.broadcastUserInfo(true);
		}
	}

	private class PlayerBackTask implements Runnable
	{
		private final HardReference<Player> _playerRef;

		public PlayerBackTask(Player player)
		{
			_playerRef = player.getRef();
		}

		@Override
		public void run()
		{
			Player player = _playerRef.get();
			if(player == null)
				return;

			player.sendMessage(new CustomMessage("org.l2j.gameserver.instancemanager.AwayManager.PlayerBackTask"));

			_awayTexts.remove(player.getObjectId());

			player.unblock();
			player.setAwayingMode(false);
			player.standUp();
			player.broadcastUserInfo(false);
		}
	}
}