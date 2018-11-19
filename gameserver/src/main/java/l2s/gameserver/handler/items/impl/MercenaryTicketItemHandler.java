package l2s.gameserver.handler.items.impl;

import l2s.commons.dao.JdbcEntityState;
import l2s.gameserver.dao.CastleHiredGuardDAO;
import l2s.gameserver.instancemanager.ReflectionManager;
import l2s.gameserver.model.Playable;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Zone;
import l2s.gameserver.model.entity.residence.Castle;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.model.pledge.Privilege;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ActionFailPacket;
import l2s.gameserver.templates.item.support.MerchantGuard;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.Log;
import l2s.gameserver.utils.PositionUtils;

/**
 * @author VISTALL
 */
public class MercenaryTicketItemHandler extends DefaultItemHandler
{
	@Override
	public boolean useItem(Playable playable, ItemInstance item, boolean ctrl)
	{
		// [VISTALL] old style on click - not used
		return false;
	}

	@Override
	public void dropItem(Player player, ItemInstance item, long count, Location loc)
	{
		if(!player.hasPrivilege(Privilege.CS_FS_MERCENARIES) || player.getClan().getCastle() == 0)
		{
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_THE_AUTHORITY_TO_POSITION_MERCENARIES, ActionFailPacket.STATIC);
			return;
		}

		Castle castle = player.getCastle();
		MerchantGuard guard = castle.getMerchantGuard(item.getItemId());
		if(guard == null || !castle.checkIfInZone(loc, ReflectionManager.MAIN) || player.isActionBlocked(Zone.BLOCKED_ACTION_DROP_MERCHANT_GUARD))
		{
			player.sendPacket(SystemMsg.YOU_CANNOT_POSITION_MERCENARIES_HERE, ActionFailPacket.STATIC);
			return;
		}

		if(castle.getSiegeEvent().isInProgress())
		{
			player.sendPacket(SystemMsg.A_MERCENARY_CAN_BE_ASSIGNED_TO_A_POSITION_FROM_THE_BEGINNING_OF_THE_SEAL_VALIDATION_PERIOD_UNTIL_THE_TIME_WHEN_A_SIEGE_STARTS, ActionFailPacket.STATIC);
			return;
		}

		int countOfGuard = 0;
		for(ItemInstance $item : castle.getSpawnMerchantTickets())
		{
			if(PositionUtils.getDistance($item.getLoc(), loc) < 200)
			{
				player.sendPacket(SystemMsg.POSITIONING_CANNOT_BE_DONE_HERE_BECAUSE_THE_DISTANCE_BETWEEN_MERCENARIES_IS_TOO_SHORT, ActionFailPacket.STATIC);
				return;
			}
			if($item.getItemId() == guard.getItemId())
				countOfGuard ++;
		}

		if(countOfGuard >= guard.getMax())
		{
			player.sendPacket(SystemMsg.THIS_MERCENARY_CANNOT_BE_POSITIONED_ANYMORE, ActionFailPacket.STATIC);
			return;
		}

		item = player.getInventory().removeItemByObjectId(item.getObjectId(), 1);
		if(item == null)
		{
			player.sendActionFailed();
			return;
		}

		Log.LogItem(player, Log.Drop, item);

		item.dropToTheGround(player, loc);
		player.disableDrop(1000);

		player.sendChanges();

		item.delete();
		item.setJdbcState(JdbcEntityState.STORED);

		castle.getSpawnMerchantTickets().add(item);
		CastleHiredGuardDAO.getInstance().insert(castle, item.getItemId(), item.getLoc());
	}

	@Override
	public boolean pickupItem(Playable playable, ItemInstance item)
	{
		if(!playable.isPlayer())
			return false;

		Player player = (Player)playable;
		if(!player.hasPrivilege(Privilege.CS_FS_MERCENARIES) || player.getClan().getCastle() == 0)
		{
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_THE_AUTHORITY_TO_CANCEL_MERCENARY_POSITIONING);
			return false;
		}

		Castle castle = player.getCastle();
		if(!castle.getSpawnMerchantTickets().contains(item))
		{
			player.sendPacket(SystemMsg.THIS_IS_NOT_A_MERCENARY_OF_A_CASTLE_THAT_YOU_OWN_AND_SO_YOU_CANNOT_CANCEL_ITS_POSITIONING);
			return false;
		}

		if(castle.getSiegeEvent().isInProgress())
		{
			player.sendPacket(SystemMsg.A_MERCENARY_CAN_BE_ASSIGNED_TO_A_POSITION_FROM_THE_BEGINNING_OF_THE_SEAL_VALIDATION_PERIOD_UNTIL_THE_TIME_WHEN_A_SIEGE_STARTS, ActionFailPacket.STATIC);
			return false;
		}
		castle.getSpawnMerchantTickets().remove(item);
		CastleHiredGuardDAO.getInstance().delete(castle, item);
		return true;
	}
}