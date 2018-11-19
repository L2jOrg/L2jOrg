package handler.items;

import l2s.gameserver.Config;
import l2s.gameserver.model.Playable;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Zone;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.model.entity.olympiad.Olympiad;
import l2s.gameserver.network.l2.components.CustomMessage;

/**
 * @author Bonux
**/
public class OfflineItem extends SimpleItemHandler
{
	@Override
	protected boolean useItemImpl(Player player, ItemInstance item, boolean ctrl)
	{
		return false;
	}

	@Override
	public boolean forceUseItem(Playable playable, ItemInstance item, boolean ctrl)
	{
		if(!Config.SERVICES_OFFLINE_TRADE_ALLOW)
			return true;

		if(!playable.isPlayer())
			return true;

		Player player = playable.getPlayer();

		if(player.isInArenaObserverMode() || player.getOlympiadGame() != null || Olympiad.isRegisteredInComp(player) || player.isPK())
		{
			player.sendActionFailed();
			return true;
		}

		if(player.getLevel() < Config.SERVICES_OFFLINE_TRADE_MIN_LEVEL)
		{
			player.sendMessage(new CustomMessage("itemshandlers.OfflineItem.LowLevel").addNumber(Config.SERVICES_OFFLINE_TRADE_MIN_LEVEL));
			return true;
		}

		if(!player.isInStoreMode())
		{
			player.sendMessage(new CustomMessage("itemshandlers.OfflineItem.IncorrectUse"));
			return true;
		}

		switch(Config.SERVICES_OFFLINE_TRADE_ALLOW_ZONE)
		{
			case 1:
				if(!player.isInPeaceZone())
				{
					player.sendMessage(new CustomMessage("trade.OfflineNoTradeZoneOnlyPeace"));
					return true;
				}
				break;
			case 2:
				if(!player.isInZone(Zone.ZoneType.offshore))
				{
					player.sendMessage(new CustomMessage("trade.OfflineNoTradeZoneOnlyOffshore"));
					return true;
				}
				break;
		}

		if(player.isActionBlocked(Zone.BLOCKED_ACTION_PRIVATE_STORE))
		{
			player.sendMessage(new CustomMessage("trade.OfflineNoTradeZone"));
			return true;
		}

		final int delay = item.getTemporalLifeTime();
		if(delay > 0)
			player.offline(delay);
		else
		{
			if(!reduceItem(player, item))
				return true;
			player.offline();
		}
		return true;
	}
}
