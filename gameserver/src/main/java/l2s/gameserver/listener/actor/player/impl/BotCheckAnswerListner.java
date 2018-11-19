package l2s.gameserver.listener.actor.player.impl;

import l2s.commons.lang.reference.HardReference;

import l2s.gameserver.instancemanager.BotCheckManager;
import l2s.gameserver.listener.actor.player.OnAnswerListener;
import l2s.gameserver.model.Player;

/**
 * @author Iqman
 * @date 11:35/21.0.2013
 */
public class BotCheckAnswerListner implements OnAnswerListener
{
	private HardReference<Player> _playerRef;
	private int _qId;

	public BotCheckAnswerListner(Player player, int qId)
	{
		_playerRef = player.getRef();
		_qId = qId;
	}

	@Override
	public void sayYes()
	{
		Player player = _playerRef.get();
		if(player == null)
			return;
		boolean rightAnswer = BotCheckManager.checkAnswer(_qId, true);	
		if(rightAnswer)
		{
			player.increaseBotRating();
			sendFeedBack(player, true, player.isLangRus());
		}	
		else
		{
			sendFeedBack(player, false, player.isLangRus());
			player.decreaseBotRating();
		}	
	}

	@Override
	public void sayNo()
	{
		Player player = _playerRef.get();
		if(player == null)
			return;
		boolean rightAnswer = BotCheckManager.checkAnswer(_qId, false);
		if(rightAnswer)
		{
			player.increaseBotRating();
			sendFeedBack(player, true, player.isLangRus());
		}	
		else
		{
			player.decreaseBotRating();
			sendFeedBack(player, false, player.isLangRus());
		}	
	}
	
	private void sendFeedBack(Player player, boolean rightAnswer, boolean isLangRus)
	{
		if(rightAnswer)
		{
			if(isLangRus)
				player.sendMessage("Вы ответили правильно!");
			else
				player.sendMessage("Your answer is correct!");
		}
		else
		{
			if(isLangRus)
				player.sendMessage("Вы ответили не правильно! В случае и вы ответите не верно несколько раз подряд, то вы будете помещены в тюрьму.");
			else
				player.sendMessage("Your answer is incorrect! In case you will answer several time incorectly, you will be placed in jail for botting");
		}
	}
}