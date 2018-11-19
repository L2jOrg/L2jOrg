package handler.voicecommands;

import l2s.gameserver.Config;
import l2s.gameserver.data.string.ItemNameHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Zone;
import l2s.gameserver.model.entity.olympiad.Olympiad;
import l2s.gameserver.network.l2.components.CustomMessage;
import l2s.gameserver.utils.ItemFunctions;

public class Offline extends ScriptVoiceCommandHandler
{
	private String[] COMMANDS = new String[] { "offline" };

	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String args)
	{
		if(activeChar.isInBuffStore())
		{
			if(!Config.BUFF_STORE_ENABLED)
			{
				activeChar.sendMessage(new CustomMessage("voicedcommandhandlers.Offline.IncorrectUse"));
				return false;
			}
			activeChar.offline();
			return true;
		}

		if(!Config.SERVICES_OFFLINE_TRADE_ALLOW)
		{
			activeChar.sendMessage(new CustomMessage("voicedcommandhandlers.Offline.Disabled"));
			return false;
		}

		if(activeChar.isInArenaObserverMode() || activeChar.getOlympiadGame() != null || Olympiad.isRegisteredInComp(activeChar) || activeChar.isPK())
		{
			activeChar.sendActionFailed();
			return false;
		}

		if(activeChar.getLevel() < Config.SERVICES_OFFLINE_TRADE_MIN_LEVEL)
		{
			activeChar.sendMessage(new CustomMessage("voicedcommandhandlers.Offline.LowLevel").addNumber(Config.SERVICES_OFFLINE_TRADE_MIN_LEVEL));
			return false;
		}

		if(!activeChar.isInStoreMode())
		{
			activeChar.sendMessage(new CustomMessage("voicedcommandhandlers.Offline.IncorrectUse"));
			return false;
		}

		if(activeChar.getNoChannelRemained() > 0)
		{
			activeChar.sendMessage(new CustomMessage("voicedcommandhandlers.Offline.BanChat"));
			return false;
		}

		switch(Config.SERVICES_OFFLINE_TRADE_ALLOW_ZONE)
		{
			case 1:
				if(!activeChar.isInPeaceZone())
				{
					activeChar.sendMessage(new CustomMessage("trade.OfflineNoTradeZoneOnlyPeace"));
					return false;
				}
				break;
			case 2:
				if(!activeChar.isInZone(Zone.ZoneType.offshore))
				{
					activeChar.sendMessage(new CustomMessage("trade.OfflineNoTradeZoneOnlyOffshore"));
					return false;
				}
				break;
		}

		if(activeChar.isActionBlocked(Zone.BLOCKED_ACTION_PRIVATE_STORE))
		{
			activeChar.sendMessage(new CustomMessage("trade.OfflineNoTradeZone"));
			return false;
		}

		if(Config.SERVICES_OFFLINE_TRADE_PRICE > 0 && Config.SERVICES_OFFLINE_TRADE_PRICE_ITEM > 0)
		{
			if(!ItemFunctions.deleteItem(activeChar, Config.SERVICES_OFFLINE_TRADE_PRICE_ITEM, Config.SERVICES_OFFLINE_TRADE_PRICE))
			{
				activeChar.sendMessage(new CustomMessage("voicedcommandhandlers.Offline.NotEnough").addString(ItemNameHolder.getInstance().getItemName(activeChar, Config.SERVICES_OFFLINE_TRADE_PRICE_ITEM)).addNumber(Config.SERVICES_OFFLINE_TRADE_PRICE));
				return false;
			}
		}

		activeChar.offline();
		return true;
	}

	@Override
	public String[] getVoicedCommandList()
	{
		return COMMANDS;
	}
}