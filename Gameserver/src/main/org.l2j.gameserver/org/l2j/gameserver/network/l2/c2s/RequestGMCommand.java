package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.World;
import org.l2j.gameserver.model.items.ItemInstance;
import org.l2j.gameserver.network.l2.s2c.ExGMViewQuestItemListPacket;
import org.l2j.gameserver.network.l2.s2c.GMHennaInfoPacket;
import org.l2j.gameserver.network.l2.s2c.GMViewCharacterInfoPacket;
import org.l2j.gameserver.network.l2.s2c.GMViewItemListPacket;
import org.l2j.gameserver.network.l2.s2c.GMViewPledgeInfoPacket;
import org.l2j.gameserver.network.l2.s2c.GMViewQuestInfoPacket;
import org.l2j.gameserver.network.l2.s2c.GMViewSkillInfoPacket;
import org.l2j.gameserver.network.l2.s2c.GMViewWarehouseWithdrawListPacket;

import java.nio.ByteBuffer;

public class RequestGMCommand extends L2GameClientPacket
{
	private String _targetName;
	private int _command;

	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		_targetName = readString(buffer);
		_command = buffer.getInt();
		// buffer.getInt();
	}

	@Override
	protected void runImpl()
	{
		Player player = client.getActiveChar();
		Player target = World.getPlayer(_targetName);
		if(player == null || target == null)
			return;
		if(!player.getPlayerAccess().CanViewChar)
			return;

		switch(_command)
		{
			case 1:
				player.sendPacket(new GMViewCharacterInfoPacket(target));
				player.sendPacket(new GMHennaInfoPacket(target));
				break;
			case 2:
				if(target.getClan() != null)
					player.sendPacket(new GMViewPledgeInfoPacket(target));
				break;
			case 3:
				player.sendPacket(new GMViewSkillInfoPacket(target));
				break;
			case 4:
				player.sendPacket(new GMViewQuestInfoPacket(target));
				break;
			case 5:
				ItemInstance[] items = target.getInventory().getItems();
				int questSize = 0;
				for(ItemInstance item : items)
					if(item.getTemplate().isQuest())
						questSize++;
				player.sendPacket(new GMViewItemListPacket(1, target, items, items.length - questSize));
				player.sendPacket(new GMViewItemListPacket(2, target, items, items.length - questSize));
				player.sendPacket(new ExGMViewQuestItemListPacket(target, items, questSize));

				player.sendPacket(new GMHennaInfoPacket(target));
				break;
			case 6:
				player.sendPacket(new GMViewWarehouseWithdrawListPacket(target));
				break;
		}
	}
}