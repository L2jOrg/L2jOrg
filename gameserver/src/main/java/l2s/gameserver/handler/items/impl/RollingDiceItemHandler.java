package l2s.gameserver.handler.items.impl;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.Playable;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.DicePacket;
import l2s.gameserver.network.l2.s2c.SystemMessage;

public class RollingDiceItemHandler extends DefaultItemHandler
{
	@Override
	public boolean useItem(Playable playable, ItemInstance item, boolean ctrl)
	{
		if(playable == null || !playable.isPlayer())
			return false;
		Player player = (Player) playable;

		int itemId = item.getItemId();

		if(player.isInOlympiadMode())
		{
			player.sendPacket(SystemMsg.YOU_CANNOT_USE_THAT_ITEM_IN_A_GRAND_OLYMPIAD_MATCH);
			return false;
		}

		if(player.isSitting())
		{
			player.sendPacket(SystemMsg.YOU_CANNOT_MOVE_WHILE_SITTING);
			return false;
		}

		int number = Rnd.get(1, 6);
		if(number == 0)
		{
			player.sendPacket(SystemMsg.YOU_MAY_NOT_THROW_THE_DICE_AT_THIS_TIME);
			return false;
		}

		player.broadcastPacket(new DicePacket(player.getObjectId(), itemId, number, player.getX() - 30, player.getY() - 30, player.getZ()));
		player.broadcastPacket(new SystemMessage(SystemMessage.S1_HAS_ROLLED_S2).addString(player.getName()).addNumber(number));
		return true;
	}
}