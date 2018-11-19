package l2s.gameserver.utils;

import l2s.gameserver.Config;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.items.Warehouse;
import l2s.gameserver.model.items.Warehouse.WarehouseType;
import l2s.gameserver.model.pledge.Clan;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.WareHouseDepositListPacket;
import l2s.gameserver.network.l2.s2c.WareHouseWithdrawListPacket;

public final class WarehouseFunctions
{
	private WarehouseFunctions()
	{}

	public static void showFreightWindow(Player player)
	{
		if(!WarehouseFunctions.canShowWarehouseWithdrawList(player, WarehouseType.FREIGHT))
		{
			player.sendActionFailed();
			return;
		}

		player.setUsingWarehouseType(WarehouseType.FREIGHT);
		player.sendPacket(new WareHouseWithdrawListPacket(player, WarehouseType.FREIGHT));
	}

	public static void showRetrieveWindow(Player player)
	{
		if(!WarehouseFunctions.canShowWarehouseWithdrawList(player, WarehouseType.PRIVATE))
		{
			player.sendActionFailed();
			return;
		}

		player.setUsingWarehouseType(WarehouseType.PRIVATE);
		player.sendPacket(new WareHouseWithdrawListPacket(player, WarehouseType.PRIVATE));
	}

	public static void showDepositWindow(Player player)
	{
		if(!WarehouseFunctions.canShowWarehouseDepositList(player, WarehouseType.PRIVATE))
		{
			player.sendActionFailed();
			return;
		}

		player.setUsingWarehouseType(WarehouseType.PRIVATE);
		player.sendPacket(new WareHouseDepositListPacket(player, WarehouseType.PRIVATE));
	}

	public static void showDepositWindowClan(Player player)
	{
		if(!WarehouseFunctions.canShowWarehouseDepositList(player, WarehouseType.CLAN))
		{
			player.sendActionFailed();
			return;
		}

		if(!(player.isClanLeader() || (Config.ALT_ALLOW_OTHERS_WITHDRAW_FROM_CLAN_WAREHOUSE || player.getVarBoolean("canWhWithdraw")) && (player.getClanPrivileges() & Clan.CP_CL_WAREHOUSE_SEARCH) == Clan.CP_CL_WAREHOUSE_SEARCH))
			player.sendPacket(SystemMsg.ITEMS_LEFT_AT_THE_CLAN_HALL_WAREHOUSE_CAN_ONLY_BE_RETRIEVED_BY_THE_CLAN_LEADER);

		player.setUsingWarehouseType(WarehouseType.CLAN);
		player.sendPacket(new WareHouseDepositListPacket(player, WarehouseType.CLAN));
	}

	public static void showWithdrawWindowClan(Player player)
	{
		if(!WarehouseFunctions.canShowWarehouseWithdrawList(player, WarehouseType.CLAN))
		{
			player.sendActionFailed();
			return;
		}

		player.setUsingWarehouseType(WarehouseType.CLAN);
		player.sendPacket(new WareHouseWithdrawListPacket(player, WarehouseType.CLAN));
	}

	public static boolean canShowWarehouseWithdrawList(Player player, WarehouseType type)
	{
		if(!player.getPlayerAccess().UseWarehouse)
			return false;

		Warehouse warehouse = null;
		switch(type)
		{
			case PRIVATE:
				warehouse = player.getWarehouse();
				break;
			case FREIGHT:
				warehouse = player.getFreight();
				break;
			case CLAN:
			case CASTLE:

				if(player.getClan() == null || player.getClan().getLevel() == 0)
				{
					player.sendPacket(SystemMsg.ONLY_CLANS_OF_CLAN_LEVEL_1_OR_HIGHER_CAN_USE_A_CLAN_WAREHOUSE);
					return false;
				}

				boolean canWithdrawCWH = false;
				if(player.getClan() != null)
					if((player.getClanPrivileges() & Clan.CP_CL_WAREHOUSE_SEARCH) == Clan.CP_CL_WAREHOUSE_SEARCH)
						canWithdrawCWH = true;
				if(!canWithdrawCWH)
				{
					player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_THE_RIGHT_TO_USE_THE_CLAN_WAREHOUSE);
					return false;
				}
				warehouse = player.getClan().getWarehouse();
				break;
			default:
				return false;
		}

		if(warehouse.getSize() == 0)
		{
			player.sendPacket(type == WarehouseType.FREIGHT ? SystemMsg.NO_PACKAGES_HAVE_ARRIVED : SystemMsg.YOU_HAVE_NOT_DEPOSITED_ANY_ITEMS_IN_YOUR_WAREHOUSE);
			return false;
		}

		return true;
	}

	public static boolean canShowWarehouseDepositList(Player player, WarehouseType type)
	{
		if(!player.getPlayerAccess().UseWarehouse)
			return false;

		switch(type)
		{
			case PRIVATE:
				return true;
			case CLAN:
			case CASTLE:

				if(player.getClan() == null || player.getClan().getLevel() == 0)
				{
					player.sendPacket(SystemMsg.ONLY_CLANS_OF_CLAN_LEVEL_1_OR_HIGHER_CAN_USE_A_CLAN_WAREHOUSE);
					return false;
				}

				boolean canWithdrawCWH = false;
				if(player.getClan() != null)
					if((player.getClanPrivileges() & Clan.CP_CL_WAREHOUSE_SEARCH) == Clan.CP_CL_WAREHOUSE_SEARCH)
						canWithdrawCWH = true;
				if(!canWithdrawCWH)
				{
					player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_THE_RIGHT_TO_USE_THE_CLAN_WAREHOUSE);
					return false;
				}
				return true;
			default:
				return false;
		}
	}
}