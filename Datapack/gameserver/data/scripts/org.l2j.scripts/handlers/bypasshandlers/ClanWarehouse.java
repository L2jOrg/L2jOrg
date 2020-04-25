package handlers.bypasshandlers;

import org.l2j.gameserver.handler.IBypassHandler;
import org.l2j.gameserver.model.ClanPrivilege;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.instance.Warehouse;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.WareHouseWithdrawalList;
import org.l2j.gameserver.network.serverpackets.items.WarehouseDepositList;

import static org.l2j.gameserver.util.GameUtils.isNpc;

public class ClanWarehouse implements IBypassHandler
{
	private static final String[] COMMANDS =
	{
		"withdrawc",
		"depositc"
	};
	
	@Override
	public boolean useBypass(String command, Player player, Creature target) {
		
		if (!isNpc(target))
		{
			return false;
		}
		
		final Npc npc = (Npc) target;
		if (!(npc instanceof Warehouse) && (npc.getClan() != null))
		{
			return false;
		}
		
		if (player.hasItemRequest())
		{
			return false;
		}
		else if (player.getClan() == null)
		{
			player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_THE_RIGHT_TO_USE_THE_CLAN_WAREHOUSE);
			return false;
		}
		else if (player.getClan().getLevel() == 0)
		{
			player.sendPacket(SystemMessageId.ONLY_CLANS_OF_CLAN_LEVEL_1_OR_ABOVE_CAN_USE_A_CLAN_WAREHOUSE);
			return false;
		}
		else
		{
			try
			{
				if (command.toLowerCase().startsWith(COMMANDS[0])) // WithdrawC
				{
					player.sendPacket(ActionFailed.STATIC_PACKET);
					
					if (!player.hasClanPrivilege(ClanPrivilege.CL_VIEW_WAREHOUSE))
					{
						player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_THE_RIGHT_TO_USE_THE_CLAN_WAREHOUSE);
						return true;
					}
					
					player.setActiveWarehouse(player.getClan().getWarehouse());
					
					if (player.getActiveWarehouse().getSize() == 0)
					{
						player.sendPacket(SystemMessageId.YOU_HAVE_NOT_DEPOSITED_ANY_ITEMS_IN_YOUR_WAREHOUSE);
						return true;
					}
					
					for (Item i : player.getActiveWarehouse().getItems())
					{
						if (i.isTimeLimitedItem() && (i.getRemainingTime() <= 0))
						{
							player.getActiveWarehouse().destroyItem("Item", i, player, null);
						}
					}
					
					player.sendPacket(new WareHouseWithdrawalList(1, player, WareHouseWithdrawalList.CLAN));
					player.sendPacket(new WareHouseWithdrawalList(2, player, WareHouseWithdrawalList.CLAN));
					return true;
				}
				else if (command.toLowerCase().startsWith(COMMANDS[1])) // DepositC
				{
					player.sendPacket(ActionFailed.STATIC_PACKET);
					player.setActiveWarehouse(player.getClan().getWarehouse());
					player.setInventoryBlockingStatus(true);
					WarehouseDepositList.openOfClan(player);
					return true;
				}
				
				return false;
			}
			catch (Exception e)
			{
				LOGGER.warn("Exception in " + getClass().getSimpleName(), e);
			}
		}
		return false;
	}
	
	@Override
	public String[] getBypassList()
	{
		return COMMANDS;
	}
}
