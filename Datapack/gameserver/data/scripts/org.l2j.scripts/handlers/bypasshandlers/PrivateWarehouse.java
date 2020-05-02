package handlers.bypasshandlers;

import org.l2j.gameserver.handler.IBypassHandler;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.WareHouseWithdrawalList;
import org.l2j.gameserver.network.serverpackets.item.WarehouseDepositList;

import static org.l2j.gameserver.util.GameUtils.isNpc;

public class PrivateWarehouse implements IBypassHandler {

	private static final String[] COMMANDS = {
		"withdrawp",
		"depositp"
	};
	
	@Override
	public boolean useBypass(String command, Player player, Creature target) {
		if (!isNpc(target)) {
			return false;
		}
		
		if (player.hasItemRequest()) {
			return false;
		}

		if (command.toLowerCase().startsWith(COMMANDS[0])) { // WithdrawP
			showWithdrawWindow(player);
			return true;
		}
		else if (command.toLowerCase().startsWith(COMMANDS[1])) { // DepositP
			player.setActiveWarehouse(player.getWarehouse());
			player.setInventoryBlockingStatus(true);
			WarehouseDepositList.openOfPlayer(player);
			return true;
		}
		return false;
	}
	
	private static void showWithdrawWindow(Player player) {
		player.setActiveWarehouse(player.getWarehouse());
		
		if (player.getActiveWarehouse().getSize() == 0) {
			player.sendPacket(SystemMessageId.YOU_HAVE_NOT_DEPOSITED_ANY_ITEMS_IN_YOUR_WAREHOUSE);
			return;
		}
		
		player.sendPacket(new WareHouseWithdrawalList(1, player, WareHouseWithdrawalList.PRIVATE));
		player.sendPacket(new WareHouseWithdrawalList(2, player, WareHouseWithdrawalList.PRIVATE));
	}
	
	@Override
	public String[] getBypassList() {
		return COMMANDS;
	}
}
