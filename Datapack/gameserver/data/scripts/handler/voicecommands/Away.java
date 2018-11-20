package handler.voicecommands;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.instancemanager.AwayManager;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.Zone.ZoneType;
import org.l2j.gameserver.model.entity.events.impl.SiegeEvent;
import org.l2j.gameserver.network.l2.components.CustomMessage;
import org.apache.commons.lang3.StringUtils;

/**
 * @author: Kolobrodik
 * @date: 17:38/25.05.2012
 * @description: Данный класс предоставляет собой обработку команд для сервиса "Away"
 */
public class Away extends ScriptVoiceCommandHandler
{
	private String[] COMMANDS = new String[]{"away", "back"};

	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String text)
	{
		if(command.startsWith("away"))
		{
			if(Config.AWAY_ONLY_FOR_PREMIUM && !activeChar.hasPremiumAccount())
			{
				activeChar.sendMessage(new CustomMessage("PremiumOnly"));
				return false;
			}
			return away(activeChar, text);
		}
		else if(command.startsWith("back"))
			return back(activeChar);
		return false;
	}

	private boolean away(Player activeChar, String text)
	{
		//check char is all ready in away mode
		if(activeChar.isInAwayingMode())
		{
			activeChar.sendMessage(new CustomMessage("org.l2j.gameserver.handler.voicecommands.impl.Away.Already"));
			return false;
		}

		if(!activeChar.isInZone(ZoneType.peace_zone) && Config.AWAY_PEACE_ZONE)
		{
			activeChar.sendMessage(new CustomMessage("org.l2j.gameserver.handler.voicecommands.impl.Away.PieceOnly"));
			return false;
		}

		//check player is death/fake death and movement disable
		if(activeChar.isMovementDisabled() || activeChar.isAlikeDead())
			return false;

		// Check if player is in Siege
		SiegeEvent<?, ?> siege = activeChar.getEvent(SiegeEvent.class);
		if(siege != null)
		{
			activeChar.sendMessage(new CustomMessage("org.l2j.gameserver.handler.voicecommands.impl.Away.Siege"));
			return false;
		}

		// Check if player is in Duel
		if(activeChar.isInDuel())
		{
			activeChar.sendMessage(new CustomMessage("org.l2j.gameserver.handler.voicecommands.impl.Away.Duel"));
			return false;
		}

		//check player is in Olympiade
		if(activeChar.isInOlympiadMode() || activeChar.getOlympiadGame() != null)
		{
			activeChar.sendMessage(new CustomMessage("org.l2j.gameserver.handler.voicecommands.impl.Away.Olympiad"));
			return false;
		}

		// Check player is in observer mode
		if(activeChar.isInObserverMode())
		{
			activeChar.sendMessage(new CustomMessage("org.l2j.gameserver.handler.voicecommands.impl.Away.Observer"));
			return false;
		}

		//check player have karma/pk/pvp status
		if(activeChar.getKarma() > 0 || activeChar.getPvpFlag() > 0)
		{
			activeChar.sendMessage(new CustomMessage("org.l2j.gameserver.handler.voicecommands.impl.Away.Pvp"));
			return false;
		}

		if(text == null)
			text = StringUtils.EMPTY;

		//check away text have not more then 10 letter
		if(text.length() > 10)
		{
			activeChar.sendMessage(new CustomMessage("org.l2j.gameserver.handler.voicecommands.impl.Away.Text"));
			return false;
		}

		// check if player have no one in target
		if(activeChar.getTarget() == null)
			//set this Player status away in AwayManager
			AwayManager.getInstance().setAway(activeChar, text);
		else
		{
			activeChar.sendMessage(new CustomMessage("org.l2j.gameserver.handler.voicecommands.impl.Away.Target"));
			return false;
		}

		return true;
	}

	private boolean back(Player activeChar)
	{
		if(!activeChar.isInAwayingMode())
		{
			activeChar.sendMessage(new CustomMessage("org.l2j.gameserver.handler.voicecommands.impl.Away.Not"));
			return false;
		}
		AwayManager.getInstance().setBack(activeChar);
		return true;
	}

	@Override
	public String[] getVoicedCommandList()
	{
		return COMMANDS;
	}
}
